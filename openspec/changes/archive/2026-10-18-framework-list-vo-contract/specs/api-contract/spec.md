# Spec Delta

## ADDED Requirements

### Requirement: 分页接口 SHALL 统一用 PageVO<T> 而非 Page<T>

凡返回分页数据的接口，其出参容器 SHALL 是 `com.aicode.core.PageVO<T>`（列表字段名为 `records`，并携带 `totalRow` / `totalPage` / `curPage` / `pageSize`），SHALL NOT 使用 `com.aicode.core.Page<E>`（列表字段名为 `voList`，且携带 `params` / `sortColumns` / `optObject` 等内部字段）。

两种容器并存会让契约层出现**两种分页字段名**（`records` vs `voList`），前端消费时需按端点分别记忆，且 `Page` 的内部字段（`params` / `optObject`）也会进入响应。

> 该约束由 M2-1 在 `/project/list` 上落地（收敛到 `R<PageVO<ProjectSummaryVO>>`），M5-A1 在 `/framework/list` 上完成剩余收敛。

#### Scenario: 框架列表接口的 records 取代 voList

- **WHEN** 客户端请求 `GET /framework/list`，响应的 `data` 节点 SHALL 含 `records` 数组（元素为框架 VO）以及 `totalRow` / `curPage` / `pageSize` / `totalPage`
- **AND** 契约 schema 中响应容器 SHALL ref `PageVO<FrameworkSummaryVO>` 展开形态，SHALL NOT ref 旧的 `Page<FrameworksPageVO>`

#### Scenario: 无 Page<E> 容器残留于对外契约

- **WHEN** 遍历 `contracts/openapi/openapi.yaml` 的 `components.schemas`
- **THEN** SHALL NOT 出现由 `com.aicode.core.Page` 展开的 schema（其特征是含 `voList` / `params` / `sortColumns` / `optObject` 字段）
- **AND** 仓库内已无 Controller 以 `R<Page<...>>` 作为出参返回类型

#### Scenario: 反向验证：改回 Page<E> 容器 SHALL 使契约断言失败

- **WHEN** 临时把某分页接口的出参容器从 `PageVO<T>` 改回 `Page<T>`
- **THEN** 契约守护断言 SHALL 失败，错误信息 SHALL 明确指出 `voList` 不在期望字段集合内（或 `records` 缺失）
