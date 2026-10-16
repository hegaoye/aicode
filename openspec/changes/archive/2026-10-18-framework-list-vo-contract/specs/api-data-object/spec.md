# Spec Delta

## ADDED Requirements

### Requirement: 凭据字段 SHALL NOT 出现在任何接口响应中

凡持久化实体（Entity / PO）中的**凭据类字段**——口令散列、Git 仓库密码、访问令牌——SHALL NOT 被序列化进任何接口响应；出参视图对象（VO）SHALL NOT 声明此类字段。本约束**跨领域适用**，不限账户域：任何领域的实体若含凭据列，其 VO 投影 SHALL 一并剔除。

> 该约束由 M3-D 在 `AccountVO.password` 上的单点防护上升为通用约束，M5-A1 在 `Frameworks.password`（Git 仓库密码）上第二次落地。

#### Scenario: Git 仓库密码不随框架列表/详情接口泄漏

- **WHEN** 客户端请求 `GET /framework/list` 或 `GET /framework/load?code=`（框架技术池，含模板仓库 Git 凭据）
- **THEN** 响应的 `data` 节点（列表时为 `data.records[*]`，详情时为 `data` 本身）SHALL NOT 含 `password` 键
- **AND** 契约 schema 中对应的 VO SHALL NOT 声明 `password` 属性

#### Scenario: 凭据字段不出现在前端生成类型中

- **WHEN** orval 从入库契约生成前端类型
- **THEN** 生成物 SHALL NOT 含任何凭据字段名（`password` / `secret` / `token` 等）

#### Scenario: 反向验证：以实体直出接口 SHALL 立即暴露凭据并使断言失败

- **WHEN** 临时把某接口的出参类型从 VO 改回持久化实体（破坏本约束）
- **THEN** 对应的契约守护断言 SHALL 失败，错误信息 SHALL 明确指出 `password` 是多余字段

### Requirement: 继承 BaseVO 的 VO SHALL NOT 把查询辅助字段带入响应

视图对象（VO）SHALL NOT 继承携带**查询辅助字段**的基类（如 `com.aicode.core.BaseVO` 的 `createTimeBegin` / `createTimeEnd` / `updateTimeBegin` / `updateTimeEnd`）。这些字段的语义是"入参：按时间范围过滤"，被 Jackson 序列化进响应即构成派生字段泄漏。

本约束**不禁止**分页 VO 继承 `BaseVO`——只要响应侧不序列化这些字段（如用 `@JsonIgnore`）；但**默认形态**应为「VO 独立声明持久化字段」。

#### Scenario: 框架列表的 VO 字段集合不含 BaseVO 查询辅助字段

- **WHEN** 评审 `GET /framework/list` 响应的 `data.records[*]` 字段集合
- **THEN** 该集合 SHALL NOT 含 `createTimeBegin` / `createTimeEnd` / `updateTimeBegin` / `updateTimeEnd`
- **AND** 对应 VO 类 SHALL NOT 声明 `extends BaseVO`

#### Scenario: 已存在继承 BaseVO 的 VO SHALL 在后续改造中被替换而非沿用

- **WHEN** 某个继承 `BaseVO` 的 VO 所在端点因其它理由被改造（如 M5-A1 的密码泄漏）
- **THEN** 该 VO SHALL 被替换为不继承 `BaseVO` 的等价 VO，SHALL NOT 沿用原类（避免"反正已经继承了，改造时忘了这 4 个字段"）
