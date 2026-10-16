# Design

## Context

`/framework/modify` 与 `/framework/updateIsPublic`：

- **出参** `R<Frameworks>` —— 裸实体，含 `password`（Git 仓库口令）
- **入参** 形参 `@Parameter(hidden = true)` —— 契约里完全没有参数

**当前无活跃泄漏**（`FrameworkListPage` 只读，没人调），但这是**契约里的定时炸弹**：
任何人接框架管理面、用 orval 生成的函数提交，立刻把 Git 密码写进响应。

用户已拍板：**只修 A 类 2 个端点**，出参用 M5-A1 已建的 `FrameworkSummaryVO`（复用）。

## Goals / Non-Goals

**Goals:**

- 两个端点出参改 `FrameworkSummaryVO`（无 password）；
- 两个端点在契约里导出参数；
- 契约层断言防回归。

**Non-Goals:**

- **不修其余 12 处 `@Parameter(hidden = true)`** —— 它们无前端消费者、
  无安全含义（出参是 `R<Void>` / `R<String>` / 无前端触达的端点），
  修了也无从验证。留待真正要接对应页面时。
- 不接框架管理面的前端页面。
- 不动 `/framework/build` 与 `/framework/delete`（出参分别是 `R<Void>` / `R<String>`，
  无泄漏；前者无参数但前端无消费者）。

## Decisions

### D1：出参复用 `FrameworkSummaryVO`（7 字段）

**决定**：`R<Frameworks>` → `R<FrameworkSummaryVO>`。

**理由**：`/framework/list` 已在用它（M5-A1 建的）。**同一 Controller 的所有出参端点
共用一个读投影**是本仓新确立的约束（已写进 `api-data-object` 的凭据 Requirement）——
若某个端点单独保留实体出参，它就是定时炸弹。

**代价**：`modify` 若要回显 `Frameworks` 的完整信息，前端需再调 `/framework/load`。
可接受 —— 当前无前端消费。

### D2：`modify` 保留实体形参，`updateIsPublic` 保持 `@Parameters` 块

**决定**：
- `modify(@Parameter(hidden = true) Frameworks frameworks)` → `modify(Frameworks frameworks)`
  （去掉 `hidden`，保留实体形参 —— Spring 按字段名绑定 query/form 参数）；
- `updateIsPublic(@Parameter(hidden = true) Frameworks frameworks)` → `updateIsPublic(Frameworks frameworks)`
  （其 `@Parameters` 块已声明 `code` / `isPublic`，去掉 `hidden` 后契约导出这两个）。

**为何不给 `updateIsPublic` 换成显式 `@RequestParam String code, String isPublic`**：
那样契约更干净（只有 2 个参数而非整个实体）。但会**改方法签名**，
而 `frameworksService.update(frameworks, ...)` 需要整个实体来构造更新条件。
除非在方法体内手工构造 `Frameworks`，否则得动业务逻辑。

**取舍**：保持现状（去 hidden 即止）。契约会导出实体的全部字段作为**入参** ——
`password` 作为入参出现在 requestBody/query 里**不构成泄漏**（无真实值），
只是契约臃肿。这与 AGENTS §11.1 遗留项的判断一致。

**实施时需核实**：去掉 `hidden` 后 `modify` 的契约是否真的导出了参数
（若 springdoc 对实体形参的导出方式与预期不同，需回退到显式 `@RequestParam`）。

### D3：契约断言两条

- **凭据**：两个端点的响应 schema **不含 `password`** 且 ref `FrameworkSummaryVO`；
- **参数**：两个端点在契约里**有 `parameters` 段**（防 hidden 回归）。

## Risks / Trade-offs

**[去掉 `hidden` 后 `modify` 的契约会导出 `Frameworks` 实体的全部字段]** → 包括
`password` 作为**入参**。这不构成泄漏（无真实值），但契约臃肿。
**实施时核实实际导出结果**；若不可接受，另开变更做签名收窄。

**[`updateIsPublic` 有两个 `@RequestMapping` method（PUT + POST）]** → orval 生成了
`updateIsPublic` 与 `updateIsPublic1` 两个函数（对应 POST / PUT）。
两者都会随本次改动获得参数。**注意函数名是 orval 的自动去重**，不保证语义清晰。

**[`@Parameters` 块可能仍不生效]** → 前次实测（`/account/delete`）：形参带 `hidden` 时
`@Parameters` 块被盖掉。去掉 `hidden` 后是否生效需实测。若仍不生效，
改为显式 `@RequestParam` 形参。

## Migration Plan

单批（一个 commit）：

1. `FrameworksController` 两个方法：去 `hidden` + 改出参；
2. 契约层断言（凭据 + 参数）；
3. 契约产物 + orval 重新生成；
4. 前端 `typecheck` + `test`（预期零改动全绿）；
5. AGENTS.md 登记 + validate + archive。

**回滚**：单 commit revert。

## Open Questions

- **其余 12 处 `@Parameter(hidden = true)`** —— 需逐个核实形参语义，无统一套路。
  其中 `ProjectFramworkController.add` 收的是 `String projectStr`、
  `AccountController.delete` 收 `AccountVO`、`LoginCtrl.reg` 收 `Account` 实体。
  建议**在真正要接对应页面的那个变更里顺带修**，而非独立批量改。
- **`com.aicode.core.Page` 是否也继承 `BaseVO`** —— 同前，与本变更无关。
- **框架管理面的前端页面** —— `modify` / `delete` / 新增模板都还没有 UI。属功能补全。