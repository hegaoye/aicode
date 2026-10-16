# Proposal

## Why

`openspec/changes/` 已累计 12 个变更改动**代码生成主链路**（`GeneratorSVImpl` 的终态消息、
`init` 异步化、关系配置、展示属性写入、口令散列、ws 路由），而
`AGENTS.md §7.10 第 8 项` 记录的事实是：**这条链路至今没有端到端回归**。

§7.1 记着硬教训：

> **实体列映射事故（2026-10-15）**：`MapFieldColumn` 的字段被抽到基类导致 12 个
> `@TableField` 丢失……**全部 215 个测试皆绿**。根因是**没有任何测试让该实体真正生成并执行
> SQL**（业务层纯内存装配、生成链路测试打桩 mapper）。

「测试全绿」不等于「被改动的属性被测过」。现在补正当其时 —— 改动的上下文还在。

## 探路已验证可行，且**抓到了一个真实缺陷**

实施前的技术探针（临时 `InitChainProbeTest`）在 H2 test profile 上真跑通了
「建项目 → 导 SQL → **真实建库 + 真实逆解析**」到 `parse` 阶段，途中撞上：

```
progress: createDatabase → parse(共 2 张表) → 正在解析第 1/2 张表：shop_order
execute 抛异常: NullPointerException —
  Cannot invoke "java.lang.CharSequence.length()" because "this.text" is null
```

根因（`ProjectServiceImpl.parse()` 对每一列无条件调用）：

```java
Map<String, Object> objectMap = StringTools.getStateOrType(column.getRemarks());
//                                                          ^^^^^^^^^^^^
// 用户没写 COMMENT 时为 null → getByPattern(null, reg) → Pattern.matcher(null) → NPE
```

**影响**：用户导入的建表 SQL 只要**有一列没写 `COMMENT`**，整个逆解析就失败。

**为什么至今未暴露**：`StringToolsTest` 的 3 条 `getStateOrType` 用例**全部传非 null 字符串**；
而所有 `parse` 的测试都打桩了 `columnDAO`，`remarks` 总有值。**这正是本变更要治的病。**

## What Changes

### 1. 修 `getByPattern` 的 null 保护（1 行）

```java
if (str == null) { return new ArrayList<>(); }
```

语义正确：`getStateOrType(null)` 应返回空 map → `isState=N`，与"注释里没有状态语义"
的既有语义一致（`getStateOrType("1234567890")` 已返回空 map）。

### 2. 端到端回归：`InitChainIT`

**真实**走：登录 → 建项目 → 导 SQL → **异步 init**（真实建库 + 真实逆解析）→
验证 `listMapClassTable` / `listMapFieldColumn` 的输出（表名 / 类名 / 列名 / 主键标记 /
日期标记），全部在真实 H2 上，**零 mock**。

- **异步等待**：AGENTS §7.6 要求「轮询至完成，设超时；**禁** `sleep` 固定时长」。
  轮询 `listMapClassTable` 直到非空。
- **失败路径**：导入非法 DDL → 断言**不产生半成品**（`isParseClass` 不为 `Y`，
  且 `listMapClassTable` 不含未解析完的表）。
- **边界用例**：DDL **不带 `COMMENT`**（本次 bug 的场景），断言能正常解析。

## BREAKING

无。测试新增 + 一行 null 保护（`getByPattern(null)` 从抛 NPE 改为返回空 list）。

## Capabilities

### Modified Capabilities

- **`auth-account`** —— 该能力有「口令以散列形式落库」的 Scenario；端到端测试的登录步骤
  依赖它（且 `2026-10-18-password-bcrypt-migration` 刚把 MD5 改为 BCrypt + 透明升级），
  本变更的 IT 会**首次真正走登录端点**，为其提供回归防线。

### New Capabilities

- **`init-chain-regression`** —— 新能力。定义「核心生成链路的端到端回归 SHALL 在真实数据库上
  覆盖」，并明确其**存在理由**：链路只被 mock 覆盖时，真实输入的空值 / 边界分支从未被触及
  （§7.1 的 `@TableField` 事故与本变更发现的 `getStateOrType(null)` NPE 都是该模式的产物）。

## Impact

- **后端**：`common` 的 `StringTools.getByPattern` 一行；新增 `InitChainIT`；
  删掉临时探针。
- **测试**：后端 326 → 约 **334** 用例（+8）。
- **契约 / 前端**：零变化。
- **风险**：**低**。唯一的行为改动是 `getByPattern(null)` 从抛 NPE 改为返回空 list ——
  该路径此前只会导致 NPE，没有依赖它的既有行为。