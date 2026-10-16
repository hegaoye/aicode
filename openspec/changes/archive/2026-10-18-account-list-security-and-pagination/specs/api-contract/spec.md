# Spec Delta

## MODIFIED Requirements

### Requirement: 受保护 REST 接口 SHALL 走 R<T> 包装而非裸实体出参

凡受保护（受 `LoginInterceptor` 拦截）的 REST 接口，其 Controller 方法的出参 SHALL 是 `R<T>` 形式（`T` 为 VO 或 PageVO<T>），SHALL NOT 直接序列化持久化实体或自定义类。本 Requirement 是对现有 Requirement 「统一响应包装的 data SHALL 携带真实结构」的强约束延伸 — 该 Requirement 要求 `data` 携带真实结构，本 Requirement 钉死"任何受保护接口 SHALL NOT 跨过 `R` 包络直接出参"。

**`R` 包络 SHALL 包含一切出参，容器也不例外**：`PageVO<T>` / `List<T>` / 布尔值虽是"容器或标量"而非实体，但它们同样 SHALL 落在 `R` 内。SHALL NOT 因为"分页容器是本仓自定义类型、布尔值更轻量"而让它们直接成为方法的返回类型 —— 裸出参会绕过 `R` 的 `code` / `info` / `success` 三元组，前端的 `unwrapSuccess` 无从工作，该端点只能被特殊处理。

**入参侧 SHALL NOT 因形参被 `@Parameter(hidden = true)` 隐藏而从契约里消失**：形参被隐藏时 springdoc 会忽略整个形参，连方法级 `@Parameters` 块也会被盖掉，于是契约里该端点**没有任何参数**，前端无从构造请求。该注解 SHALL NOT 用于会接收客户端输入的形参。

历史遗留端点（如 `GET /project/load/code/{code}` 在 2026-10-18 之前返裸 `ProjectVO`、`GET /project/relationship/listMapFieldColumn` 返 `R<Object>`、`GET /account/list` 返裸 `PageVO<AccountVO>`）应在本能力落地后逐步收回；本变更起，任何新引入或变更的受保护接口 SHALL 满足本 Requirement。

> **本 Requirement 已连续三次被违反**：① `loadByCode` 直返裸 `ProjectVO`（M3-A-1 修）
> ② `listMapFieldColumn` 返 `R<Object>`（`2026-10-18-relationship-config` 修）
> ③ `/account/list` 返裸 `PageVO` + `/account/modify` 返裸 `boolean`（本变更修）。
> 反复违反说明**只写"SHALL 走 R<T>"不足以被守住**，故本次补充"容器也不例外"与
> "入参形参不得被 hidden 隐藏"两条可判定的细则。

#### Scenario: 任何受保护端点的契约 schema SHALL NOT 直接 ref 实体类型

- **WHEN** 评审或回归测试任何受保护 REST 接口的契约
- **THEN** 契约 schema 中响应的顶层 schema SHALL 为 R-包络（如 `R<VO>` 渲染后的 `RProjectVO`、`R<PageVO<T>>` 渲染后的 `RPageVOProjectSummaryVO` 等），SHALL NOT 直接 ref 到实体类型（如 `Project`、`MapFieldColumn`、`Page`）
- **AND** 响应字段的 `data` 节点 SHALL 是该 VO / PageVO 的 schema，且 SHALL NOT 是 `{}` 或 `object` 这种无约束形态

#### Scenario: 分页与标量出参 SHALL NOT 裸返

- **WHEN** 某受保护端点的返回类型是 `PageVO<T>` / `List<T>` 等容器
- **THEN** 契约里该端点的响应顶层 SHALL 是对应的 R-包络（`RPageVO<T>` / `RList<T>`）
- **AND** `GET /account/list`（曾返裸 `PageVO<AccountVO>`）SHALL 已包入 `R<PageVO<AccountPublicVO>>`

#### Scenario: 端点 SHALL NOT 裸返 boolean 等非 R 包络类型

- **WHEN** 某 Controller 方法的返回类型不是 `R<...>`
- **THEN** 契约里该端点的响应 SHALL NOT 出现顶层 `type: boolean` / `type: string` 这类无包络 schema
- **AND** `PUT /project/relationship/modify`（曾裸返 `boolean`）与 `PUT /account/modify` SHALL 返 `R<Boolean>`，使前端能用统一的 `unwrapSuccess` 消费

#### Scenario: 入参形参 SHALL NOT 被 hidden 隐藏而从契约消失

- **WHEN** 某端点的方法形参承载客户端输入（`@RequestBody` 或按字段绑定的实体形参）
- **THEN** 该形参 SHALL NOT 标注 `@Parameter(hidden = true)` —— 隐藏后契约里该端点 SHALL NOT 完全没有参数
- **AND** `DELETE /account/delete`（曾把承载删除条件的 `AccountVO` 形参标为 hidden，
  导致契约里无任何参数、前端无法发起删除）SHALL 已在契约中导出 `id` / `code`

#### Scenario: 历史遗漏端点（如 loadByCode）改返 R<T> 后契约形状收敛

- **WHEN** `GET /project/load/code/{code}` 由原本直返 `ProjectVO` 改造为返 `R<ProjectDetailVO>`
- **THEN** 契约 schema 中响应的 `data` 节点 SHALL ref `ProjectDetailVO` 而不是裸 ref，SHALL NOT 出现 "ProjectVO 作为响应 schema 整体" 这种无包络形态
- **AND** 任何"过去写过 R<T>"的客户端代码 SHALL 在无修改前提下仍能成功解构；存量"直接吃实体"的旧代码 SHALL 让 `resp.data.code` 取到 `undefined` 而非业务值，触发类型守护

#### Scenario: 反向验证：以裸实体出参接入受保护接口 SHALL 立即失真

- **WHEN** 临时把 Controller 装配改回直返持久化实体（破坏本 Requirement）
- **THEN** `api-contract` 系列契约断言（如 `ApiContractIT` 期待 `data.code === 种子编码` 的断言） SHALL 失败，错误 SHALL 指向裸实体出参而非 `code` 字段缺省
- **AND** 该反向验证保证本 Requirement 不会被后续回归悄悄破坏

#### Scenario: R<Object> 出参 SHALL 被识别为契约缺陷而非合法写法

- **WHEN** 某端点的返回类型泛型是 `Object` / `JSONObject` / `Map<String, Object>` 等无约束类型
- **THEN** 契约里该端点的 `data` 节点 SHALL NOT 渲染为无约束的 `object` —— 前端拿不到内部字段名，无法消费
- **AND** 该端点 SHALL 被归类为缺陷并改造为具名 VO
- **AND** 判定依据 SHALL 可自动化：`GET /project/relationship/listMapFieldColumn` 曾是本仓**唯一**的 `R<Object>` 端点，本 Scenario 为其回归防线