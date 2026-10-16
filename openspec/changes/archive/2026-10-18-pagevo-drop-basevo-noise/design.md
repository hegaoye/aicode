# Design

## Context

`PageVO<T> extends BaseVO`，`BaseVO` 带 4 个时间范围**查询辅助字段**。容器继承入参基类 →
每个分页响应的 `data` 都带 4 个恒 `null` 的字段。前端零消费。

`api-data-object` 的既有 Req 逐字写着「彻底修法应另开变更」—— 本变更就是它。

## Goals / Non-Goals

**Goals:**

- `PageVO<T>` 不再继承 `BaseVO`；
- 契约里所有 `PageVO*` schema 从 9 字段降到 5 字段；
- 契约层断言该约束覆盖**所有**分页端点（防"只修一个"）。

**Non-Goals:**

- **不清理 18 个零引用的 `*PageVO` 类** —— 它们是 `facade`（契约模块）里的公开类，
  删除属独立的死代码治理变更，与本变更的"容器职责"关注点不同。
- 不改 `BaseVO` 本身（入参 DTO 仍在用它）。
- 不改 `com.aicode.core.Page`（另有的一组分页容器，见 Risks）。

## Decisions

### D1：删 `extends BaseVO`，不改 `BaseVO`

**决定**：`public final class PageVO<VO> extends BaseVO implements Serializable`
→ `public final class PageVO<VO> implements Serializable`。

**理由**：
- `PageVO` 对 `BaseVO` 的 4 个字段**零引用**（已 grep 确认）→ 编译零影响；
- 改 `BaseVO`（如加 `@JsonIgnore`）会影响 **9 个 `*PageDTO` 入参** ——
  那些 DTO 靠这 4 个字段做时间范围过滤，加 `@JsonIgnore` 是否影响 `@RequestBody` 绑定
  取决于绑定方式，风险面更大且语义不对（那些字段在入参场景是**有用途**的）。

**否掉的备选**：`BaseVO` 加 `@JsonIgnore`。否掉理由如上 —— 它把"出参不该有"的问题
用"全局隐藏"来解决，会连带入参的正当用途一起改掉。

### D2：入参 DTO 保留继承 —— 明确写进 spec

本 Req 原文只说「VO SHALL NOT 继承」，容易让人**连 `*PageDTO` 一起删**。
本次补一条 Scenario 明确：**判据是「出现在响应的 `data` 上」还是「作为请求参数被绑定」**，
而非「叫 VO 还是叫 DTO」。

这很重要 —— 仓库里 `ProjectModulePageVO` / `ProjectFramworkPageVO` 名字带 `VO`，
但实际用作**入参**（`@Parameter(hidden = true)` 的形参）。若按名字删，会破坏它们。

### D3：契约断言覆盖所有分页端点

**决定**：除对 `/project/list` 的**数据层**断言外，另加一条**扫描型断言** ——
遍历导出契约里所有 `PageVO*` schema，逐个确认不含那 4 个字段。

**理由**：逐个端点写断言容易漏（`/account/list`、`/framework/list`、…）。
扫描型断言一次覆盖全部，且将来新增分页端点自动纳入。

## Risks / Trade-offs

**[仓库里还有另一组分页容器 `com.aicode.core.Page`]** → `ProjectModuleController.list`
与 `ProjectFramworkController.list` 返回的是 `Page<...>` 而非 `PageVO<...>`。
本次**不动** `Page`（它是否继承 `BaseVO` 需另核实；且这两个端点都带
`@Parameter(hidden = true)`，前端根本调不通）。**本变更只收敛 `PageVO`** ——
它是前端实际消费的容器（`/project/list`、`/framework/list`、`/account/list` 全用它）。

**契约产物刷新会带来 springdoc 的字段顺序抖动** → 按 AGENTS §10 已登记的方法
（两版各自 `sort` 后 `comm` 比对）判定真变化与抖动，只提交语义变化。

**前端生成物变化** → `PageVO*` 的 TS 类型少 4 个可选键。因是**可选**（`?`）且零消费，
现有代码零改动；但 `npm run typecheck` 仍须验证（防有代码用
`as unknown as` 绕过类型）。

## Migration Plan

单批（一个 commit）：

1. `PageVO.java` 删 `extends BaseVO`；
2. 契约层断言（数据层 + 扫描型）；
3. 契约产物 + orval 重新生成；
4. 前端 `typecheck` + `test`（预期零改动全绿）；
5. AGENTS.md 登记 + validate + archive。

**回滚**：单 commit revert。

## Open Questions

- **`com.aicode.core.Page` 是否也继承 `BaseVO`** —— 需另核实。若是，则存在第二组分页容器
  带同样噪音；但它服务的端点都带 `@Parameter(hidden = true)`（前端调不通），
  优先级低，可与「15 处 `hidden`」一并收敛。
- **18 个零引用的 `*PageVO` 何时清理** —— 死代码治理，独立变更。
- **`BaseVO` 是否该改名** —— 名字里的 `Base` 易被误当作通用出参基类。
  改名（如 `QueryConditionBase`）能预防未来的误继承，但波及 29 个类，属独立变更。