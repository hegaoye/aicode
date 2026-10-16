# Proposal

## Why

**所有分页端点的 `data` 层都带 4 个永远是 `null` 的噪音字段。**

```
契约里的 PageVOProjectSummaryVO = 9 字段
  ├─ createTimeBegin / createTimeEnd / updateTimeBegin / updateTimeEnd   ← 噪音，恒 null
  └─ curPage / pageSize / totalRow / totalPage / records                 ← 真实字段
```

根因：`PageVO<T> extends BaseVO`，而 `BaseVO` 带 4 个**时间范围查询辅助字段**（语义是
"入参：按时间范围过滤"）。容器继承了一个入参基类，查询辅助字段就跟着进了每个响应。

前端**零消费**这 4 个字段 —— 唯一的 grep 命中是 `FrameworkListPage` 里的一条注释
「也不读 createTimeBegin/End、updateTimeBegin/End」。

## 这件事 spec 早就预告了

`api-data-object` 的既有 Requirement「继承 BaseVO 的 VO SHALL NOT 把查询辅助字段带入响应」
**逐字写着**：

> **分页容器**（`PageVO<T>`）当前也继承 `BaseVO`，导致所有分页端点的 `data` 层携带这 4 个
> 字段 —— 该层属全仓共性问题，**彻底修法应另开变更**。

本变更就是那个"另开变更"。

## 为什么列表元素层已经没有这个问题了

M3-C 的 VO 裁切（`2026-10-17-project-vo-trim`）把前端在用的列表元素 VO 全部改成不继承
`BaseVO`：

| VO | 继承 |
|---|---|
| `ProjectSummaryVO` / `ProjectDetailVO` | 不继承 ✓ |
| `AccountPublicVO` / `FrameworkSummaryVO` | 不继承 ✓ |
| `MapClassTableVO` / `MapFieldColumnVO` | 不继承 ✓ |

**所以只剩容器一层**。而且 20 个 `*PageVO` 里 **18 个已零引用**（M3-C 淘汰的），
仅 2 个还在用、且都作为**入参**（`@Parameter(hidden = true)` 的形参），删继承对它们无影响。

## What Changes

`PageVO<T>` **去掉 `extends BaseVO`**（一行代码）。

- `PageVO` 对 `BaseVO` 的 4 个字段**零引用** → 编译零影响；
- 契约里所有 `PageVO*` schema 从 9 字段降到 **5 字段**；
- 前端生成物 `PageVOProjectSummaryVO` 等同步减少 4 个可选键（`?`，不影响现有代码）。

## BREAKING

契约 schema 变化：所有分页端点的 `data` 少 4 个恒 null 的可选字段。
前端**零消费**这些字段（已核实），故无实际破坏。

## Capabilities

### Modified Capabilities

- **`api-data-object`** —— MODIFIED Req「继承 BaseVO 的 VO SHALL NOT 把查询辅助字段带入响应」：
  该 Req 原本只覆盖**列表元素层**并把容器列为"另开变更"，本次把**分页容器层**也纳入，
  并明确「查询辅助字段基类 SHALL NOT 被出参容器继承」+「入参 DTO 仍可继承该基类」
  （区分出参与出参两种职责）。

### New Capabilities

（无。）

## Impact

- **后端**：`common/.../core/PageVO.java` 一行（去掉 `extends BaseVO`）；
  `contracts/openapi/openapi.yaml`（所有 `PageVO*` schema）。
- **测试**：契约层断言 `data` 不含那 4 个字段（对 `/project/list` 实际调用）+ schema 层
  断言所有 `PageVO*` 均为 5 字段。
- **前端**：生成物变化（4 个可选键消失），**零代码改动**。
- **风险**：**低**。改动是一行；`PageVO` 对被删字段零引用；前端零消费。
  主要成本是契约产物刷新。