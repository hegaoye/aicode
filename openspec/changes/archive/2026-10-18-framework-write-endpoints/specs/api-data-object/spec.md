# Spec Delta

## MODIFIED Requirements

### Requirement: 凭据字段 SHALL NOT 出现在任何接口响应中

任何接口的响应 SHALL NOT 包含凭据字段（如密码、Git 仓库口令、密钥、token）。

具体到 `Frameworks` 实体：`password`（模板仓库的 Git 口令）SHALL NOT 出现在任何响应中。
`/framework/list` 已由 `2026-10-18-framework-list-vo-contract`（M5-A1）引入
`FrameworkSummaryVO`（7 字段： `id` / `code` / `name` / `description` / `gitHome` /
`account` / `isPublic`）堵住；`/framework/modify` 与 `/framework/updateIsPublic`
由 `2026-10-18-framework-write-endpoints` 一并收敛。

**同一 Controller 的所有出参端点 SHALL 共用同一个不含凭据的读投影** —— 若某个端点
单独保留实体出参，它就是契约里的定时炸弹：前端无消费者时不泄漏，但任何人接管理面、
用契约生成物提交，就立刻把凭据写进响应。

**入参侧 SHALL NOT 因该约束而裁剪**：修改 / 切换状态时携带 `password` 之类的凭据字段
作为**请求参数**是正当用途（哪怕本不需要，那是另一个问题）。判据 SHALL 是
「出现在响应的 `data` 上」还是「作为请求参数被绑定」。

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

#### Scenario: 框架管理的写侧端点 SHALL NOT 直出实体

- **WHEN** 评审 `/framework/modify` 与 `/framework/updateIsPublic` 的响应 schema
- **THEN** 两者 SHALL ref `FrameworkSummaryVO`，SHALL NOT ref `Frameworks` 实体
- **AND** 对应 schema SHALL NOT 含 `password`
- **AND** 该约束 SHALL 与 `/framework/list` 指向**同一个** `FrameworkSummaryVO`
  （同一 Controller 的出参端点共用一个读投影）

#### Scenario: 写侧端点的契约 SHALL 导出参数

- **WHEN** 前端要用契约生成物调用 `/framework/modify` 或 `/framework/updateIsPublic`
- **THEN** 契约里这两个端点 SHALL 导出参数（`updateIsPublic` 至少含 `code` 与 `isPublic`）
- **AND** SHALL NOT 因形参被 `@Parameter(hidden = true)` 隐藏而完全无参数 ——
  那会让前端无法构造请求

#### Scenario: 入参携带凭据 SHALL NOT 被误判为泄漏

- **WHEN** 某端点的**请求参数**中包含凭据字段（如修改时携带新的 Git 口令）
- **THEN** 该字段 SHALL 保留在请求 schema 中 —— 那是它的正当用途
- **AND** 判据 SHALL 是「出现在响应的 `data` 上」而非「出现在请求里」