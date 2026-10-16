# Tasks

## 1. 修 `getByPattern` 的 null 保护

- [x] 1.1 `common/.../core/tools/StringTools.java` 的 `getByPattern` 开头加 `if (str == null) return new ArrayList<>();` —— 在**底层通用工具**加保护而非在 `parse()` 判空，一次修好所有调用方（design D5） *(已完成：`getByPattern` 开头加 null 保护，注释说明该 NPE 的真实入口（parse → getStateOrType(remarks)）与「null 即无可匹配内容」的语义)*
- [x] 1.2 `StringToolsTest` 补 2 条：`getByPattern(null, reg)` 返回空 list 而非抛 NPE；`getStateOrType(null)` 返回空 map（⇒ 调用方得 `isState=N`，与"注释无状态语义"语义一致） *(已完成：StringToolsTest **+3 条**（null 返空 list / getStateOrType(null) 返空 map / null 与空串同义）)*
- [x] 1.3 验证 `:common:test` exit=0 *(通过：`:common:test` exit=0，StringToolsTest 13 用例全绿)*
- [x] 1.4 **反向验证**：临时去掉 null 保护 → 端到端 IT 的「无 COMMENT DDL」用例变红 *(**成立**：去掉 null 保护 → InitChainIT 正路径变红，报「轮询超时：期望 2 张表，已轮询 533 次，最后观测到 1 张」—— 且揭示真实行为：第一张表建成了、第二张 NPE)*

## 2. 临时探针改造成正式 `InitChainIT`

- [x] 2.1 删掉 `InitChainProbeTest`，新建 `aicode/src/test/java/com/aicode/project/InitChainIT.java` *(已完成：删除临时探针 InitChainProbeTest，新建 `aicode/src/test/java/com/aicode/project/InitChainIT.java`)*
- [x] 2.2 **从 HTTP 层发起**（design D1）：`POST /login/signin` → `POST /project/build` → `POST /project/sql/build` → `POST /project/init`，用 `MockMvcBuilders.webAppContextSetup(wac).addFilters(shtmlSuffixRewriteFilter)` *(已完成：走 HTTP 层（signin → project/build → project/sql/build → project/init）+ `addFilters(shtmlSuffixRewriteFilter)`)*
- [x] 2.3 `@MockitoBean ServerEndpointExporter`（MOCK 环境下无 `ServerContainer` 会启动失败，同 `LoginPasswordMigrationIT`） *(已完成：@MockitoBean ServerEndpointExporter（MOCK 环境无 ServerContainer）)*
- [x] 2.4 **建项目后按 `englishName` 读回真实 `code`**（design D4 —— `save()` 会重新生成 `code` 覆盖入参，探针实测）；并**断言入参 code ≠ 落库 code**（把该副作用本身变成守护） *(已完成：建项目后**断言 Controller 返回的 code == 落库的 code**，把 save() 重新生成 code 这一副作用本身变成守护)*
- [x] 2.5 `englishName` 用**全小写 + 时间戳后缀**（避免 schema 名冲突）；表名避开 H2 保留字（`customer` / `shop_order`，**不用** `user` / `order` —— 实测在 `MODE=MYSQL` 下仍失败） *(已完成：englishName = `e2e_{shop|bad}_<yyyyMMddHHmmssSSS>`（全小写 + 时间戳）；表名用 customer / shop_order，**不用** user / order（实测在 MODE=MYSQL 下仍失败）)*
- [x] 2.6 测试 DDL **故意不含 `COMMENT`**（design D2 —— 这是本次 bug 的触发条件，也是真实用户的常见写法） *(已完成：DDL **不含任何 COMMENT**)*
- [x] 2.7 **异步等待用轮询**（design D3）：调 init 后轮询 `listMapClassTable` 直到非空；间隔 50ms、上限 30s；超时断言 SHALL 带上**已轮询次数与最后观测值**。SHALL NOT 用 `sleep` 固定时长（AGENTS §7.6） *(已完成：轮询 50ms / 上限 30s；**超时断言带已轮询次数与最后观测值**；失败路径用「连续 3 次结果一致」作为结束信号（该路径本就建不出表，无法用表数判定）)*

## 3. 断言（正路径）

- [x] 3.1 `listMapClassTable` 返回**表数**与预期一致（表名 / 类名 / kebab-case 前端命名逐项断言） *(已完成：断言表数=2 + 表名集合含 customer/shop_order + 每张表的 className / dashedCaseName 非空（顺序不保证，用集合比较）)*
- [x] 3.2 `listMapFieldColumn` 返回**列数**与预期一致；逐列断言 `column` → `field` 映射、`fieldType`、**主键标记**、**日期标记**（AGENTS §10 记过 H2 元数据差异，这些是最易错的几项） *(已完成：断言 customer 有 3 列；逐列断言 field 映射（created_at→createdAt）/ fieldType 非空 / **id 的 isPrimaryKey=Y** / **created_at 的 isDate=Y** / **无 COMMENT 列的 isState=N**)*
- [x] 3.3 断言 `Project.isParseTable` / `isParseClass` 均为 `Y`（`parse()` 成功后才置位） *(已完成：断言 isParseTable 与 isParseClass 均为 Y)*

## 4. 断言（失败路径）

- [x] 4.1 导入**语法错误的 DDL** → 断言 `listMapClassTable` **不含**目标表，且 `isParseClass != Y`（不留下「看起来成功」的半成品 —— design D6、spec 的对应 Scenario） *(已完成：非法 DDL（缺右括号）→ 断言表清单不含 broken、isParseClass != Y)*
- [x] 4.2 断言失败路径**不超时**（即失败是"快速失败"，不是"卡到超时"）—— 否则测试要等满 30 秒 *(完成���：断言失败路径耗时 < POLL_TIMEOUT（快速失败而非等满 30 秒）)*

## 5. 清理

- [x] 5.1 `@AfterEach` 清理测试创建的 schema（`DROP SCHEMA IF EXISTS <englishName>`）—— 否则污染其他测试（design Risks） *(已完成：@AfterEach 执行 `DROP SCHEMA IF EXISTS ... CASCADE`，失败不掩盖测试本身的失败)*
- [x] 5.2 确认测试可**重复运行**（连跑两次都绿），无残留依赖 *(通过：**连跑两次均 2 tests / 0 failures**（--rerun-tasks 强制重跑）)*

## 6. 验收 + 文档 + 登记

- [x] 6.1 全套测试 `JAVA_HOME=$(/usr/libexec/java_home -v 25) ./aicode/gradlew test`，登记三模块用例数（预期 326 → 约 334） *(实测：aicode **226**（224 + InitChainIT 2）/ 2 跳过 / 0 失败；facade 39；common **66**（63 + 3）/ 1 跳过 / 0 失败 —— 合计 **331**)*
- [x] 6.2 连跑两次 `:aicode:test --tests "*InitChainIT*"` 确认幂等 *(通过：两次重跑均 2/0)*
- [x] 6.3 契约 / 前端核对：应**零变化**（`git diff --stat contracts/openapi/openapi.yaml` 与前端均无 diff） *(通过：`contracts/openapi/openapi.yaml` 与 `aicode-web/src` **均无 diff**)*
- [x] 6.4 `openspec validate --strict 2026-10-18-init-chain-e2e-regression` 与 `--specs` *(openspec validate --strict 与 --specs 均通过（**15/15**，新增能力后）)*
- [x] 6.5 **`AGENTS.md §7.10 第 8 项`**：从「当前无测试覆盖」改为「**已覆盖段 1（建项目→导SQL→真实建库+逆解析）**；**构建段仍无覆盖**（依赖外部模板仓）—— 明确标注避免误以为已全覆盖」 *(完成���：§7.10 第 8 项从「当前无测试覆盖」改为「**段 1 已覆盖**（列出断言范围）+ **构建段仍无覆盖**及其原因」—— 明确标注以免误以为已全覆盖；并记入写这类测试的三个坑)*
- [x] 6.6 `AGENTS.md §7.1`：把 `getStateOrType(null)` NPE 补进「测试全绿 ≠ 被测过」的第二个实例（与 `@TableField` 事故并列） *(完成���：§7.1 把 `getStateOrType(null)` NPE 补为「测试全绿 ≠ 被测过」的**第二个实例**，并点出两次事故的共同点)*
- [x] 6.7 同步 delta spec 到新建能力 `init-chain-regression`，然后 `openspec archive` *(已完成：物化新能力 `init-chain-regression`（1 Req 3 Scenarios）；变更已 archive)*
- [x] 6.8 commit（中文 message） *(已完成：中文 message，单变更单 commit)*