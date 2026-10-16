# Proposal

## Why

`/framework/modify` 与 `/framework/updateIsPublic` 是一对**契约里的定时炸弹**：

| | 现状 |
|---|---|
| **出参** | `R<Frameworks>` —— **裸实体，含 `password`（Git 仓库口令）** |
| **入参** | `@Parameter(hidden = true)` —— 契约里**完全没有参数** |

出参含密码这一点正是 AGENTS §11.1 记录的**凭据泄漏第二次出现**（第一次是 `/framework/list`，
M5-A1 已用 `FrameworkSummaryVO` 修掉）。当时记的遗留项是：

> `/framework/modify` 与 `/framework/updateIsPublic` 仍返 `R<Frameworks>` 实体直出，
> 同样泄漏 `password`（**无前端消费者**，接管理面时须另开变更收敛）

**当前无活跃泄漏** —— `FrameworkListPage` 是只读的（它的 `<button>` 是翻页控件），
没人调这两个端点。

**但契约里那两个签名（无参数 + 返 `RFrameworks`）是定时炸弹**：任何人接框架管理面、
用 orval 生成的函数提交，立刻把 Git 密码写进响应。修掉它，成本是两个方法的签名。

## What Changes

| 端点 | 改前 | 改后 |
|---|---|---|
| `PUT/POST /framework/modify` | `R<Frameworks>`（含 password）+ 契约无参数 | `R<FrameworkSummaryVO>`（7 字段，无 password）+ 契约导出真实参数 |
| `PUT/POST /framework/updateIsPublic` | 同上 | 同上 |

`FrameworkSummaryVO` 是 M5-A1 已建的 7 字段读投影（`id` / `code` / `name` / `description` /
`gitHome` / `account` / `isPublic`，**无 `password`**），`/framework/list` 已在用 —— **复用**。

**入参**：`updateIsPublic` 的 `@Parameters` 块已声明 `code` / `isPublic`，只需去掉形参上的
`hidden = true`；`modify` 需要整个 `Frameworks`（形参是实体，Spring 按字段名绑定 query/form
参数），去掉 `hidden` 后契约会导出实体字段。

## BREAKING

`modify` / `updateIsPublic` 的响应类型从 `RFrameworks` 变为 `RFrameworkSummaryVO`。
**前端零消费**（已核实：`FrameworkListPage` 只读），故无实际破坏。

orval 会重新生成这两个函数（此前无参数，现在带参数）。

## Capabilities

### Modified Capabilities

- **`api-data-object`** —— MODIFIED Req「凭据字段 SHALL NOT 出现在任何接口响应中」：
  把 `/framework/modify` 与 `/framework/updateIsPublic` 这两个**仍返实体**的端点纳入，
  并明确「同一 Controller 的所有出参端点 SHALL 共用同一个不含凭据的读投影」。
- **`api-contract`** —— ADDED 1 Requirement「会接收客户端输入的形参 SHALL NOT 被
  `@Parameter(hidden = true)` 隐藏而从契约消失」，含 Scenario。

### New Capabilities

（无。）

## Impact

- **后端**：`FrameworksController` 两个方法签名（出参 VO + 去 hidden）；
  `contracts/openapi/openapi.yaml`。
- **测试**：`ApiContractIT` 新增场景⑰ —— ① `modify` / `updateIsPublic` 的响应 schema
  **不含 `password`** ② 两者在契约里**导出了参数**（防 hidden 回归）。
- **前端**：orval 重新生成（两个函数从无参数变为带参数）；**零代码改动**（无消费者）。
- **风险**：**低**。两个方法的签名改动；前端零消费。
  ⚠️ 需确认去掉 `hidden` 后 `modify` 的契约不会导出 20+ 个实体字段（含 password 作为**入参**
  —— 那不构成泄漏，但契约会很臃肿）。实施时核实。