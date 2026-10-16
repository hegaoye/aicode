# Spec Delta

## ADDED Requirements

### Requirement: 受保护 REST 接口 SHALL 走 R<T> 包装而非裸实体出参

凡受保护（受 `LoginInterceptor` 拦截）的 REST 接口，其 Controller 方法的出参 SHALL 是 `R<T>` 形式（`T` 为 VO 或 PageVO<T>），SHALL NOT 直接序列化持久化实体或自定义类。本 Requirement 是对现有 Requirement 「统一响应包装的 data SHALL 携带真实结构」的强约束延伸 — 该 Requirement 要求 `data` 携带真实结构，本 Requirement 钉死"任何受保护接口 SHALL NOT 跨过 `R` 包络直接出参"。

历史遗留端点（如 `GET /project/load/code/{code}` 在 2026-10-18 之前返裸 `ProjectVO`）应在本能力落地后逐步收回；本变更起，任何新引入或变更的受保护接口 SHALL 满足本 Requirement。

#### Scenario: 任何受保护端点的契约 schema SHALL NOT 直接 ref 实体类型

- **WHEN** 评审或回归测试任何受保护 REST 接口的契约
- **THEN** 契约 schema 中响应的顶层 schema SHALL 为 R-包络（如 `R<VO>` 渲染后的 `RProjectVO`、`R<PageVO<T>>` 渲染后的 `RPageVOProjectVO` 等），SHALL NOT 直接 ref 到实体类型（如 `Project`、`MapFieldColumn`、`Page`）
- **AND** 响应字段的 `data` 节点 SHALL 是该 VO / PageVO 的 schema，且 SHALL NOT 是 `{}` 或 `object` 这种无约束形态

#### Scenario: 历史遗漏端点（如 loadByCode）改返 R<T> 后契约形状收敛

- **WHEN** `GET /project/load/code/{code}` 由原本直返 `ProjectVO` 改造为返 `R<ProjectVO>`
- **THEN** 契约 schema 中响应的 `data` 节点 SHALL ref `ProjectVO` 而不是裸 ref，SHALL NOT 出现 "ProjectVO 作为响应 schema 整体" 这种无包络形态
- **AND** 任何"过去写过 R<T>"的客户端代码 SHALL 在无修改前提下仍能成功解构；存量"直接吃实体"的旧代码 SHALL 让 `resp.data.code` 取到 `undefined` 而非业务值，触发类型守护

#### Scenario: 反向验证：以裸实体出参接入受保护接口 SHALL 立即失真

- **WHEN** 临时把 Controller 装配改回直返持久化实体（破坏本 Requirement）
- **THEN** `api-contract` 系列契约断言（如 `ApiContractIT` 期待 `data.code === 种子编码` 的断言） SHALL 失败，错误 SHALL 指向裸实体出参而非 `code` 字段缺省
- **AND** 该反向验证保证本 Requirement 不会被后续回归悄悄破坏
