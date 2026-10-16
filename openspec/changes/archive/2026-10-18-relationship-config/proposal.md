# Proposal

## Why

`2026-10-18-project-create-and-sql-init` 打通流程前 3 步后，代码生成配置流程只剩**最后一个断点**：

```
① 建项目 ✅  ② 导 SQL ✅  ③ 解析 ✅  ④ 配显示属性 ✅  ⑤ 配关联关系 ❌  ⑥ 构建 ✅
```

第 ⑤ 步是**三层映射的最后一块**（AGENTS §5.4）：`MapRelationship` 通过 `mainField`/`joinField` 定义 1:1、1:N，生成器据此产出关联查询与导航代码。**不配它，生成的代码就是一堆孤立 CRUD**。

### 但第 ⑤ 步目前**在契约层面就走不通**

核实 4 个端点后发现 4 处契约缺陷，其中一处是**致命**的：

| 端点 | 契约里的响应 | 问题 |
|---|---|---|
| `GET /listMapFieldColumn` | `RObject` | **`data` 是空对象 `{}`** —— 前端拿不到 `mainFields` / `associateFields` / `mapRelationship`。直接违反 AGENTS §6.6「出参形状 SHALL 由泛型返回类型承载，禁写裸 `R<Object>`」 |
| `GET /listMapClassTable` | `RListMapClassTable` | 裸实体 9 字段（含 2 个恒 `null` 的关联 list）—— **上个变更的 `ProjectSqlPanel` 已在用** |
| `GET /list` | `RListMapRelationship` | 裸实体 11 字段（含嵌套 `associateClass` 实体 + 2 个派生 boolean）|
| `PUT /modify` | **裸 `boolean`** | 不在 `R` 包络内，违反「禁裸返实体或字符串」|

`listMapFieldColumn` 是**配关联时用来选字段**的端点（返回 `JSONObject`：`{mainFields, associateFields, mapRelationship}`）。契约里 `data` 是 `{}`，前端**无法知道有哪些字段可选** —— orval 生成的 TS 类型只告诉你 `data?: unknown` 之类。

## What Changes

### 后端（4 处 VO 收敛 + 1 处返回类型修正）

**实施前核实（Task 0）的发现改变了实施方式**：这三个 VO 并非不存在，而是 crud 生成器的
既有产物，且**已被其它端点占用**：

| VO | 既有字段 | 已被谁占用 |
|---|---|---|
| `MapFieldColumnVO` | 13（含 `id` / `displayAttribute` / `sqlType` / `notes` / `defaultValue`）| **`/displayAttribute/list` 的现行出参** —— `DisplayAttributesPage`（M1）依赖 |
| `MapRelationshipVO` | 8（含 `id`）| `PUT /modify` 的 `@RequestBody` **入参**，且 `modify` 用 `getId()` 查询 |
| `MapClassTableVO` | 5（缺 `classModel` / `dashedCaseName`）| 无生产消费者 |

因此**不能裁改前两个**（会破坏已上线页面 / 既有端点的入参绑定）。用户拍板的处置：

| 用途 | 处置 |
|---|---|
| `MapFieldColumn` 出参（`listMapFieldColumn`）| **新建 `MapFieldColumnReadVO`（8 字段）** —— 原 13 字段的 `MapFieldColumnVO` 留给 `/displayAttribute/list` 不动 |
| `MapRelationship` 出参（`list` / `listMapFieldColumn`）| **新建 `MapRelationshipReadVO`（7 字段）** —— 原有 8 字段的 `MapRelationshipVO` 继续承担 `modify` 入参职责 |
| `MapClassTable` 出参（`listMapClassTable`）| **改造既有 `MapClassTableVO`：5 → 6 字段**（去 `id`，加 `classModel` / `dashedCaseName`）—— 该 VO 无生产消费者，改动安全；上个变更的 `ProjectSqlPanel` 直接改用它 |
| `listMapFieldColumn` 的组合结构 | **新建 `RelationshipFieldsVO`（3 字段）** |

**共 3 个新 VO + 1 处补齐**，而非 4 个全新 VO。

外加 `PUT /modify` 从裸 `boolean` 改为 `R<Boolean>`。

**`MapFieldColumn` 裁掉什么**：嵌套 `displayAttribute`（有专门的 `DisplayAttributesPage`，且 `DisplayAttribute` 本身是 UI 契约，另一条线）、嵌套 `mapRelationship`、`notes` / `defaultValue` / `sqlType` / `upper`、4 个派生 boolean（`checkDate` / `checkState` / `checkPk` / `oneToOne`…）。保留的 8 个正是配关联与展示所需：字段名、字段类型、主键/日期/状态标记。

### 前端（详情页新增「关联关系」区块）

- **表与字段**：复用 `ProjectSqlPanel` 已有的类↔表列表（改用新 `MapClassTableVO`），点任一表展开字段列表；
- **配置表单**：主表下拉 → 从表下拉 → 主字段下拉（拉 `listMapFieldColumn` 的 `mainFields`）→ 从字段下拉（`associateFields`）→ 1:1 / 1:N 单选 → 保存；
- **已配关系列表**：`GET /list` 展示 `mainField` / `joinField` / 基数。

**契约 / 生成物**：orval 重新生成（`RObject` → `RRelationshipFieldsVO` 等）。

## BREAKING

4 个端点的响应类型变更。`listMapClassTable` 的前端消费者是上个变更刚落地的 `ProjectSqlPanel` —— 同分支内同步改，无实际破坏。

## Capabilities

### Modified Capabilities

- **`api-data-object`** —— MODIFIED Req 1「Controller SHALL 返回 VO 而非持久化实体」：补
  `MapFieldColumn` / `MapRelationship` / `MapClassTable` 三个**映射实体**的字段裁切清单
  （延续 M3-C 起的 VO 裁切线），并增补「`JSONObject` 型响应 SHALL NOT 以
  `R<Object>` 出参」段落。
- **`api-contract`** —— MODIFIED Req「受保护 REST 接口 SHALL 走 `R<T>` 包装而非裸实体出参」：
  补 2 个 Scenario。**注意这是该 Requirement 的第二次被违反** —— 其既有 Scenario 已明写
  「`data` SHALL NOT 是 `{}` 或 `object` 这种无约束形态」，而
  `GET /project/relationship/listMapFieldColumn` 恰恰返 `R<Object>`（`data` 就是 `{}`）。
  第一次违反是 `loadByCode` 直返裸 `ProjectVO`（M3-A-1 已修）。本变更把第二次也修掉，
  并补一条「端点 SHALL NOT 裸返 `boolean`」的 Scenario（`PUT /modify` 的现状）。
- **`aicode-web-page`** —— ADDED 1 Requirement「多级联动的写侧表单 SHALL 按依赖顺序加载候选
  并可重置下游选择」。这是该能力**第二个覆盖写侧**的场景（第一个是
  `2026-10-18-project-create-and-sql-init` 的建项目表单，本变更是其**联动**加强形态）。

### New Capabilities

（无。）

## Impact

- **后端**：`facade` 4 个新 VO + `MapRelationshipController` 5 个方法签名；`contracts/openapi/openapi.yaml`。
- **前端**：`ProjectDetailPage` 扩展 `RelationshipPanel`（新组件 + 测试）、`ProjectSqlPanel` 换用 `MapClassTableVO`；预计前端 108 → 约 118 用例。
- **测试**：后端 `ApiContractIT` 新增场景⑫ 2 用例 + 反向验证。
- **风险**：**中**。① `MapFieldColumn` 是 AGENTS §6.10 记载过 `@TableField` 丢失事故的实体，动它需谨慎（本变更只动**出参 VO**，不动实体与 Mapper，风险可控）② 三级联动下拉（表 → 字段）需要处理"切换主表时清空下游选择"这类交互状态。