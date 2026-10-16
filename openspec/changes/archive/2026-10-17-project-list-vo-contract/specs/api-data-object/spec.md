# Spec Delta

## ADDED Requirements

### Requirement: 分页列表接口 SHALL 使用 PageVO<XxxVO> 而非 Page<Object>

涉及分页返回的列表接口 SHALL 以 `PageVO<T>` 作为出参的元素容器（`data` 字段即是该 `PageVO<T>`），其元素 `T` SHALL 为某一 VO 类型，SHALL NOT 为 `Object` / `unknown`。该约束与本能力已有的"列表接口出参为 VO"Scenario 互补，专门收紧分页形态，避免 `voList: unknown` 的契约漏出。

#### Scenario: 分页列表接口的 data 元素带真实类型

- **WHEN** 某分页列表接口（如 `GET /project/list`）返回
- **THEN** 契约 schema 中 `data.records` 的元素类型 SHALL 为该接口对应的 VO 类型（如 `ProjectVO`），SHALL NOT 为 `{}` 或 `object` 之类的无约束类型

#### Scenario: 端点字段被改名前端编译失败依然成立

- **WHEN** 后端修改了某分页列表接口的响应字段（并按 `api-contract-artifact` 流程同步刷新了入库契约与 orval 生成物），而前端消费该端点的代码仍按旧字段名读取
- **THEN** `npm run typecheck` SHALL 失败，错误 SHALL 指向被改名的字段，SHALL NOT 让"由 `unknown` 兜底而跳过类型检查"成为绕过路径

#### Scenario: 接口响应不包含 Page<object> 这种不确定结果集

- **WHEN** 评审或测试任何分页列表接口
- **THEN** 导出契约中 SHALL NOT 出现 `Page` 类型本身以未指定 `voList` 元素类型的形式被引用；任何 Page 实例 SHALL 通过 `PageVO<XxxVO>` 出现
