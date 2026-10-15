# auth-account Capability

## Purpose

定义 aicode 平台的账户、登录与会话能力：单用户模式下的账户存储、口令散列方式、JWT 签发与校验、以及登录拦截器的放行/拒绝规则。本基线是对既有行为的记录（补建于 2026-10-14），用于后续安全加固变更的对照基准。

## Requirements

### Requirement: 单用户账户存储

系统 SHALL 以单用户模式运行，仅内置一个账户用于登录；账户记录持久化在关系库中，口令以**不可逆散列**形式存储，数据库中 SHALL NOT 出现明文口令。

#### Scenario: 默认账户可登录

- **WHEN** 使用内置账户 `admin` 与其正确口令调用登录接口
- **THEN** 系统返回 `code=0000`，且 `data` 为一个 JWT 字符串

#### Scenario: 口令以散列形式落库

- **WHEN** 查询账户表的口令列
- **THEN** 返回值 SHALL 是 MD5 十六进制串，SHALL NOT 是明文口令

#### Scenario: 口令错误被拒绝

- **WHEN** 使用正确账户名与错误口令调用登录接口
- **THEN** 系统返回 `code=0000` 且 `data` 为空（不区分「账户不存在」与「口令错误」），SHALL NOT 签发 token

### Requirement: JWT 会话令牌

系统 SHALL 使用 JWT 签发会话令牌，默认有效期为 60 分钟；令牌 SHALL NOT 提供刷新机制，过期后 SHALL 要求重新登录。

#### Scenario: 令牌默认有效期

- **WHEN** 系统签发一个会话令牌
- **THEN** 该令牌的过期时间为签发时刻起 60 分钟

#### Scenario: 令牌过期后被拒绝

- **WHEN** 客户端携带一个已过期的令牌访问受保护接口
- **THEN** 系统返回 `code=9007`（未授权 / Token 无效）

### Requirement: 登录拦截器的令牌来源优先级

系统 SHALL 按以下顺序解析令牌：① 请求头 `Authorization: Bearer <token>`；② URL 查询参数 `?token=<token>`。任一来源命中且签名校验通过 SHALL 放行；两个来源均未命中或校验失败 SHALL 拒绝。

#### Scenario: 请求头令牌优先放行

- **WHEN** 请求同时携带有效的 `Authorization: Bearer <token>` 与无效的 `?token=<bad>`
- **THEN** 系统放行该请求

#### Scenario: URL 参数令牌兼容放行

- **WHEN** 请求仅携带有效的 `?token=<token>`，且声明 `x-requested-with: XMLHttpRequest`
- **THEN** 系统放行该请求

#### Scenario: 令牌缺失时 AJAX 请求被拒绝

- **WHEN** 携带 `x-requested-with: XMLHttpRequest` 但未提供任何有效令牌
- **THEN** 系统返回 `code=9007`，响应体 SHALL NOT 是 HTML 登录页

#### Scenario: 登录与文档路径豁免

- **WHEN** 请求路径落在登录接口、API 文档、静态资源、H2 控制台或 Actuator 豁免区间内
- **THEN** 拦截器 SHALL NOT 拦截该请求，即使未携带令牌

### Requirement: 凭据传输方式的演进约束

系统 SHALL NOT 新增任何「令牌仅走 URL 查询参数」的接口；新建接口 SHALL 优先使用 `Authorization` 请求头传递令牌。

#### Scenario: 现有 URL 参数兼容性保留

- **WHEN** 存量客户端继续以 `?token=<token>` 调用既有接口
- **THEN** 系统 SHALL 继续接受，保证不破坏现网前端

#### Scenario: 新接口拒绝 URL 参数传递

- **WHEN** 某接口在本次变更后被标记为新接口
- **THEN** 该接口 SHALL NOT 依赖 URL 查询参数接收令牌
