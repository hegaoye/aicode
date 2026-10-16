# Design

## Context

代码生成配置流程的最后一块：`MapRelationship`（AGENTS §5.4「三层映射」的第三层）通过
`mainField` / `joinField` 定义 1:1 与 1:N，生成器据此产出关联查询与导航代码。
不配它，生成的代码就是一堆孤立 CRUD。

`2026-10-18-project-create-and-sql-init` 已打通 ① ② ③，④ 已由 M1 落地，只剩 ⑤。

## Goals / Non-Goals

**Goals:**

- 打通第 ⑤ 步：主表 → 主字段、从表 → 从字段 → 基数 → 保存；
- 修 4 处契约缺陷（`R<Object>` / 3 处裸实体 / 1 处裸 `boolean`），让前端能消费；
- 建立**级联表单**的形态（该能力此前只有单层表单）。

**Non-Goals:**

- 不做关系的可视化图形编辑器（拖拽连线）。表单足够，且关系数量级是「个位数到几十」。
- 不做关系的删除 / 编辑（后端有 `PUT /modify` 与 `DELETE /delete`，但 `build` 端点本身
  是 **upsert 语义** —— `load()` 先 `remove` 再 `new`，同 `(main, associate)` 重复提交即覆盖。
  所以"编辑"= 再提交一次，本变更不做专门的编辑 UI，复用同一表单即可）。
- 不改 `MapFieldColumn` **实体**与其 `@TableField` 映射（AGENTS §6.10 记载过该实体丢
  `@TableField` 的事故；本变更只动**出参 VO**，风险可控）。
- 不改后端业务逻辑（`saveOrUpdate` / 反向关系推导逻辑一行不动）。

## 实施前核实（Task 0）：三个 VO 早已存在且被占用

| 既有 VO | 字段 | 被谁占用 |
|---|---|---|
| `MapFieldColumnVO` | 13（含 `id` / `displayAttribute` / `sqlType` / `notes` / `defaultValue`）| **`/displayAttribute/list` 的现行出参** —— `DisplayAttributesPage`（M1 上线）依赖 |
| `MapRelationshipVO` | 8（含 `id`）| `PUT /modify` 的 `@RequestBody` 入参；`modify` 用 `getId()` 查库 |
| `MapClassTableVO` | 5（`id` / `code` / `tableName` / `className` / `notes`；缺 `classModel` / `dashedCaseName`）| 无生产消费者 |

**结论：裁改前两个会破坏既有功能。** 用户拍板的处置 —— 读投影另起 `*ReadVO`，
`MapClassTableVO` 则因无消费者而**改造**（5 → 6：去 `id`、加 `classModel` / `dashedCaseName`）。

**这是本变更最容易被漏掉的一层**：proposal 初稿写"新建 4 个 VO"，实施前核实才发现
其中 3 个概念已有对应物。**VO 命名空间在本仓已被 crud 生成器占了大半** —— 新增读投影
时 SHALL 先查 `facade/<domain>/vo/` 下有无同名概念。

## Decisions

### D1：`listMapFieldColumn` 的 `R<Object>` → `R<RelationshipFieldsVO>`

**决定**：新建 `RelationshipFieldsVO`（3 字段）承载 `JSONObject` 的三个 key：
`mainFields: List<MapFieldColumnReadVO>` / `associateFields: List<MapFieldColumnReadVO>` /
`mapRelationship: MapRelationshipReadVO`（可为 null）。

**理由**：契约里 `data` 是无约束的 `{}`，前端既拿不到字段名也无法做类型安全访问。
这不是"少了个包装"，而是**该端点在契约层面不可消费**。

**注意保持 JSON key 不变**：后端 `jsonObject.put("mainFields", ...)` 等三处 key
已进过任何已部署的前端，本变更 SHALL 保持一致（VO 字段名与之对齐）。

### D2：`MapFieldColumn` 19 → 8 字段的**新读投影** `MapFieldColumnReadVO`（用户已拍板）

**决定**：新建 `MapFieldColumnReadVO` 保留 `code` / `mapClassTableCode` / `column` /
`field` / `fieldType` / `isPrimaryKey` / `isDate` / `isState`。

**为何不裁改已有的 `MapFieldColumnVO`（13 字段）**：它是 `/displayAttribute/list` 的
现行出参，`DisplayAttributesPage`（M1 上线）正在消费 —— 裁字段是破坏性变更。
13 字段版本里对配关联场景无用但无害的字段是 `displayAttribute`（嵌套 VO）/ `sqlType` /
`notes` / `defaultValue`，新投影把它们全裁掉。

**理由**：这 8 个正是"配关联 + 展示字段"所需 —— 字段名（`field` / `column`）、
类型（`fieldType`）、标记（主键/日期/状态，供 UI 徽章）。

**裁掉的四类**：
- 自增主键 `id`（对外标识是 `code`）；
- 嵌套对象 `displayAttribute`（有专门的 `DisplayAttributesPage`，且 `DisplayAttribute`
  本身是"UI 契约"属另一条演进线，混进来会让这个 VO 职责不清）、
  `mapRelationship`（在 `MapRelationshipVO` 与 `RelationshipFieldsVO` 里已单独给）；
- `notes` / `defaultValue` / `sqlType` / `upper` —— 前端配关联与展示都不用；
- 4 个派生 boolean（`checkDate` / `checkState` / `checkPk` / `oneToOne`…）。

### D3：`MapRelationship` 11 → 7 字段的**新读投影** `MapRelationshipReadVO`

保留 `code` / `mapClassTableCode` / `associateCode` / `isOneToOne` / `isOneToMany` /
`mainField` / `joinField`。裁掉 `id`（主键）、`associateClass`（嵌套整个 `MapClassTable`
实体，列表场景必为 null）、派生 boolean `oneToOne` / `oneToMany`。

**为何不直接用已有的 8 字段 `MapRelationshipVO`**：它的 `id` 是 `PUT /modify` 查询必需的
**入参**（`update(..., eq(MapRelationship::getId, vo.getId()))`），裁掉会破坏该端点。
两个 VO 职责分离：`*ReadVO` 出参、`MapRelationshipVO` 入参。类头 Javadoc SHALL 写清区别。

> **`isOneToOne` 与 `oneToOne` 并存**：前者是 `YNEnum` 字符串化的结果（`"Y"` / `"N"`），
> 后者是 `boolean` 字段 —— 语义同源。留前者（字符串，可直接回填表单），后者是派生的。

### D4：两个互相独立的级联组

**决定**：表单是 **2 组独立级联**（主表→主字段、从表→从字段），共用中间的「基数」单选。

**理由**：后端 `build` 端点的入参就是平铺的 6 个
（`mapClassTableCode` / `associateCode` / `oneToOne` / `oneToMany` / `mainField` / `joinField`），
没有层级结构。前端分组只是为了交互清晰。

**关键**：改主表 SHALL 清空主字段，但 SHALL NOT 动从表/从字段（D 组独立）——
这是 delta spec 的 Scenario「两组级联 SHALL 互不干扰」。

### D5：`oneToOne` / `oneToMany` 用「基数」单选而非两个独立 checkbox

**决定**：UI 上是**一对多 / 一对一**的单选（两个 radio），提交时映射为：

| 选择 | `oneToOne` | `oneToMany` |
|---|---|---|
| 一对多 | `N` | `Y` |
| 一对一 | `Y` | `N` |

**理由**：后端签名虽有 4 个 `required` 参数，但语义上「基数」只有一个 —— 允许同时
`oneToOne=Y, oneToMany=Y` 是无效状态，两个 checkbox 会让用户勾出非法组合。radio 从源头挡住。

**⚠️ 与后端反向推导逻辑的一致性**：后端有反向关系推导
（`A->B oneToMany` 时反向 `B->A` 应是 oneToOne）。前端只提交正向，反向由后端负责 ——
SHALL NOT 在前端重复实现这套推导。

### D6：`modify` 裸 `boolean` → `R<Boolean>`

**决定**：`PUT /project/relationship/modify` 返回类型改为 `R<Boolean>`。

**理由**：裸 `boolean` 违反「端点 SHALL NOT 裸返非 `R` 包络类型」。前端对该端点暂无消费
（本变更不做编辑 UI），但契约里留着一个反例会持续误导 —— orval 生成的类型是
`Promise<boolean>`，看起来能用，实际绕过了 `unwrapSuccess`。

### D7：详情页再挂一个区块，而不是继续膨胀 `ProjectSqlPanel`

**决定**：新建独立组件 `RelationshipPanel.tsx`，挂在 `ProjectDetailPage` 里
`ProjectSqlPanel` 之后。

**理由**：`ProjectSqlPanel` 已经 190 行且职责单一（导 SQL + 解析）。
再塞关联配置会变成"配置页"而非"SQL 面板"。且关联配置的级联状态与 SQL 无关。

**代价**：详情页底部会有两个大区块。后续若要做真正的分步配置，应改为 tab / 向导 ——
留作后续演进（见 Open Questions）。

## Risks / Trade-offs

**[`MapFieldColumn` 嵌套对象被裁掉后，`DisplayAttributesPage` 是否受影响]** → **已核实：
不受影响**。`DisplayAttributesPage` 只调 `/displayAttribute/list`，消费该端点自己的
`MapFieldColumnVO`（13 字段，含 `displayAttribute`）。本变更的 `MapFieldColumnReadVO`
（8 字段）只服务于 `listMapFieldColumn`，两者互不干扰。这也是**不裁改已有 VO** 的原因。

**[`MapRelationshipVO` 裁掉 `associateClass` 后，关系列表显示不出"被关联的表名"** → 已知
局限：`MapRelationship` 只有 `associateCode`，要显示表名得再查一次 `listMapClassTable`
做 code→tableName 映射。前端 SHALL 从已有的 `MapClassTableVO` 列表在**本地**建映射表
（避免每个关系一次请求）。若映射不到（如关联表已被删除），显示 `associateCode` 原值。

**[`oneToOne` / `oneToMany` 的 `required` 参数与 radio 的组合是否会漏传]** → radio 保证
二选一，`handleSubmit` 里显式组装两个参数。反向断言由前端测试的"提交参数集合"用例守护。

**[`build` 端点是 upsert，重复提交会 `remove` + `new`** → 这是后端既有行为（`load()` 里
先查、查到就 `remove`）。**副作用**：每次保存都会生成**新的雪花 `code`** —— 旧 code 失效。
前端保存成功后 SHALL 重新拉关系列表（SHALL NOT 只本地追加），否则列表里显示的是已失效的 code。

## Migration Plan

单批交付（一个 feature commit）：

1. 后端：`facade` 4 个新 VO；
2. 后端：`MapRelationshipController` 5 个方法签名改（4 个 VO 化 + `modify` 包 `R`）；
3. 后端：`ApiContractIT` 场景⑫（2 用例）+ 反向验证；
4. `npm run generate`；
5. 前端：`RelationshipPanel`（新组件 + 测试）；
6. 前端：`ProjectSqlPanel` 换用 `MapClassTableVO`；
7. 契约产物 + AGENTS.md 登记 + validate + archive。

**回滚**：单 commit revert。注意 `ProjectSqlPanel` 依赖上一变更，若只回滚本变更会破坏它。

## Open Questions

- **详情页底部两个大区块是否该改成 tab / 分步配置** —— 待第 ⑤ 步落地后看实际观感再定。
  一次改动应由真实使用反馈驱动，不预判。
- **关系的删除 UI** —— 后端有 `DELETE /delete`（`codes` 逗号分隔）。本变更不做，
  等真有"配错了要删"的痛点再补。
- **`POST /project/relationship/build` 的 4 个 `required` 参数** —— 契约是好的（无
  `@Parameter(hidden = true)`），无需修。