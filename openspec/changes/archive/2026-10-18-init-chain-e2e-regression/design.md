# Design

## Context

AGENTS §7.10 第 8 项：**代码生成主链路无端到端回归**，而 §7.1 记着它已经因此漏掉过一次
致命 bug（`@TableField` 丢失、215 个测试全绿、功能全废）。

本分支已累计 12 个变更改动这条链路。同时，实施前的技术探针又抓到第二个同类缺陷
（`getStateOrType(null)` NPE）。

## Goals / Non-Goals

**Goals:**

- 修 `getByPattern` 的 null 保护；
- 端到端回归「建项目 → 导 SQL → 异步 init（真实建库 + 真实逆解析）→ 验证解析产物」，
  真实 H2、零 mock；
- 覆盖本变更发现的 bug（无 COMMENT 的 DDL）。

**Non-Goals:**

- **不覆盖构建段**（`GeneratorSVImpl.aiCode`）。它 clone 外部模板仓（网络），mock 掉
  clone 也就 mock 掉了真正的风险点，且要造一套最小模板集。用户已确认只做段 1。
- 不给 `columnDAO` / `tableDAO` 补独立单测（端到端已覆盖其真实行为）。
- 不改 `parse()` 的业务逻辑（除 null 保护带来的间接影响）。

## Decisions

### D1：测试走 HTTP 层（`MockMvc`）而非直接调 Service

**决定**：从 `POST /login/signin` → `POST /project/build` → `POST /project/sql/build`
→ `POST /project/init` 依次发真实请求。

**理由**：链路上的参数绑定、鉴权拦截器、`.shtml` 过滤、**异步调度**都只在 HTTP 层才成立。
直接调 Service 会跳过 `ProjectInitExecutor` 的 `@Async`（那是本变更最该守的边界）。

**代价**：`init` 异步 → 需要轮询（见 D3）。

### D2：测试 DDL 用**不带 COMMENT** 的表（守住本次 bug）

**决定**：测试用的建表 SQL **故意不含 `COMMENT`**。

**理由**：这正是真实用户的常见写法（手写 DDL 时很少加注释），也是本次 bug 的触发条件。
用带 COMMENT 的 SQL 会让端到端测试**测不到该分支** —— 那就白做了。

**表名选择**：避开 H2 保留字。`user` / `order` 在 H2 `MODE=MYSQL` 下仍会失败
（实测：`CREATE TABLE user` → `expected "identifier"`）—— 这与 README 记录的
「非法 `order`」是同一类。改用 `customer` / `shop_order`。

**`englishName` 必须是全小写**：H2 配置为 `DATABASE_TO_UPPER=false`，
`INFORMATION_SCHEMA.TABLES.TABLE_SCHEMA` 存的就是**原文大小写**。虽然
「建 schema 传什么、查就传什么」自洽，但全小写能避免测试依赖大小写敏感的巧合。

### D3：异步等待用轮询 + 超时（AGENTS §7.6 明确要求）

**决定**：调 `POST /project/init` 后，**轮询** `GET /project/relationship/listMapClassTable`
直到非空或超时。

**轮询的依据**：`listMapClassTable` 非空 = 逆解析已产出结果（`parse()` 写完
`MapClassTable` 后才返回）。它是**可观测的完成信号**。

**为什么不轮询 ws**：测试环境无 ws 连接（`sendMessage` 无会话时静默丢弃），
收不到 `done` 事件。

**为什么不轮询 `isParseClass`**：它只在 `parse()` **全部成功**后才置 `Y`；
失败时 executor catch 掉异常，字段保持 `N` —— 于是「失败」与「还在跑」无法区分，
只能靠超时。而 `listMapClassTable` 在中途失败时**可能已有部分表**（无事务），
所以成功路径用它、失败路径断言它**不含**目标表即可区分。

**超时**：默认 30 秒，每次轮询间隔 50ms；超时抛断言错误并带上**已轮询次数与最后观测值**。

### D4：`save()` 会重新生成 `code` —— 测试必须读回真实值

**探针实测**：`projectService.save(p)` **忽略入参的 `code`、自己重新生成**
（探针设 `2111081919560912896`，落库是 `2111081919787405312`）。

**真实链路为何没这个问题**：`ProjectController.build` 是 `save()` **之后**才
`return R.success(project)`，前端拿到的就是新 `code`。

**故端到端测试必须**：建项目后**按 `englishName` 读回实体取真实 `code`**，后续步骤用它。
这本身也是一条值得断言的事实（`save` 的副作用）。

### D5：修 `getByPattern` 而非在 `parse()` 里判空

**决定**：`getByPattern` 开头加 `if (str == null) return new ArrayList<>();`。

**为何不在 `parse()` 判空**：`getByPattern` 是**通用工具**（`common`），被
`getStateOrType` 等多处调用。在**底层工具**加保护，一次修好所有调用方；
在上层判空则每个调用方都要各自处理 —— 迟早会漏。

**语义正确性**：`getStateOrType(null)` → 空 map → 调用方
`ynEnum = 空 ? Y : N` → `isState = N`，与「注释里没有状态语义」的既有语义一致
（`getStateOrType("1234567890")` 已返回空 map）。

### D6：失败路径用「非法 DDL」而非「空 SQL」

**决定**：失败用例导入语法错误的 DDL，断言 `listMapClassTable` **不含**目标表
且项目 `isParseClass != Y`。

**理由**：空 SQL 不触发任何异常（`projectSqls` 有值但 `sql` 为空 → 建表执行空语句），
测不到失败路径。非法 DDL 会让 `DatabaseDAO.createDatabase` 抛
`DatabaseException`，并顺带清理半成品 schema —— 这正是要守的行为。

## Risks / Trade-offs

**[测试会在 H2 test 库里创建真实 schema（`customer` 等）]** → 会污染其他测试。
**处置**：测试结束时清理（`DROP SCHEMA IF EXISTS`），或用带时间戳的 `englishName`
避免与固定名冲突（`seed` 断言里按 `englishName` 精确匹配，不受影响）。
**决定用时间戳 + 显式清理**，双保险。

**[真实建库/逆解析比 mock 慢]** → 一次端到端约 1–3 秒（H2 内存库 + 5 张表）。
可接受。**SHALL NOT** 为此牺牲断言粒度。

**[`@SpringBootTest` 会加载 ws 的 `ServerEndpointExporter`]** → MOCK 环境下
`jakarta.websocket.ServerContainer not available` 启动失败。需
`@MockitoBean ServerEndpointExporter`（同 `LoginPasswordMigrationIT`）。

**[端到端覆盖到 `parse()` 但没覆盖 `aiCode()`]** → 构建段仍无回归。
已在 proposal 的 Non-Goals 与 Open Questions 明确标注，避免"以为全覆盖了"。

## Migration Plan

单批（一个 commit）：

1. `common`：`getByPattern` 加 null 保护 + `StringToolsTest` 补 2 条（null / 空串）；
2. 把临时探针改造成 `InitChainIT`：删调试输出、走 HTTP 层、加轮询、DDL 不带 COMMENT；
3. 补失败路径用例；
4. 跑全量 + 文档 + validate + archive。

**回滚**：单 commit revert。⚠️ 但若已有测试数据依赖 `getByPattern(null)` 抛 NPE 的行为
（不可能 —— 那是 bug），无风险。

## Open Questions

- **构建段（`aiCode`）的端到端回归** —— 需要 mock `GitTools` + 造最小模板集。
  mock 掉 clone 也就 mock 掉了真正的风险点，收益有限。**待真实反馈再定**。
- **`columnDAO` / `tableDAO` 是否需要各自的单测** —— 端到端已覆盖其真实行为，
  独立的单测收益低于维护成本。
- **其他链路的同类空值分支** —— 本次只修了 `getStateOrType` 一处。
  `parse()` 里还有 `column.getRemarks()` → `mapFieldColumn.setRemarks(...)`（无风险）
  与 `StringTools.getStateOrType` 的调用（已修）。**是否还有别处的 null 未处理，
  应在更大范围的测试补齐后重查**。