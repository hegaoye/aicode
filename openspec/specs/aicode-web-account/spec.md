# aicode-web-account Specification

## Purpose

定义前端账户接入的可执行形状：M3-D 落地 `GET /account/load` 后端端点（返 `R<AccountPublicVO>` 不含 `password` 字段），前端 `useCurrentAccount` hook 调该端点拿当前账户详情（前端不传 code，后端从 token claim 抽 `accountCode`，避免前端 jwtutil 依赖），`<AccountMenu>` 组件作上导航用户徽章，`<AccountPage>` 详情页消费 3 字段。本能力的形态是 M2-2 `aicode-web-auth` 在账户维度的镜像，复用 M2-2 的 `useAuth` token 生命周期与 axios 拦截器鉴权。

## Requirements

### Requirement: useCurrentAccount SHALL 是 token 持有下的账户详情轻包装

`useCurrentAccount()` hook SHALL 调 `api.loadByCode1()`（**不**传 `code` —— 后端从 token claim 抽 `accountCode` 自己查账户，避免前端 jwtutil 依赖）拿当前账户的 `R<AccountPublicVO>`（**不含** `password` 字段）；返回 `{ account, code, id, isLoading, error, refresh }`；`isLoading` 在请求未完成时为 true；`error` 在请求失败时返 `ContractError`；`refresh` 显式重拉。

#### Scenario: token 存在时调 /account/load 拿 account 信息

- **WHEN** 用户登录后 `useAuth.isAuthenticated === true` 且 `useCurrentAccount` mount
- **THEN** hook SHALL 调 `api.loadByCode1()`（不传 code，依赖后端从 token claim 抽 accountCode） → 经 `unwrapSuccess` 拆 `R.data` → 设 `{ account, code, id }` 状态
- **AND** `account` 字段 SHALL 是账户显示名（如 `'admin'` / `'aicode'` 等）

#### Scenario: 服务端失败时 error 字段有值

- **WHEN** `/account/load` 返回 4xx / 5xx 错误响应
- **THEN** `useCurrentAccount` SHALL 设 `error` 字段为 `ContractError` 实例（含 code / info）
- **AND** `account` / `code` / `id` 字段 SHALL 保持 null
- **AND** SHALL NOT 抛错到上层（让 `<AccountMenu>` / `<AccountPage>` 渲染降级 UI）

#### Scenario: refresh 显式重拉

- **WHEN** 调用方调 `useCurrentAccount().refresh()`
- **THEN** hook SHALL 重新调用 `api.loadByCode1()`，与 mount 时的行为一致
- **AND** 刷新期间 `isLoading` 为 true

### Requirement: AccountMenu SHALL 是上导航用户区，不参与业务逻辑

`<AccountMenu>` 组件 SHALL 在 `<AppShell>` 顶部 spacer 之前渲染；`isAuthenticated === false` 时不渲染任何 DOM（保持未登录页无 UI 噪声）；`isAuthenticated === true` 时渲染一个徽章（带账户显示名）+ 下拉列表，下拉项含"账户详情"链接（跳 `/account/me`）与"退出"按钮（调 `useAuth().signOut()` + `useNavigate('/login')`）。

#### Scenario: isAuthenticated === true 时渲染用户徽章与下拉

- **WHEN** `<AppShell>` mount 且 `useAuth.isAuthenticated === true`
- **THEN** `<AccountMenu>` SHALL 渲染：① `<span data-testid="account-menu">` 包裹徽章 ② 徽章显示账户显示名（来自 `useCurrentAccount().account`）③ 点击徽章展开下拉 ④ 下拉内"账户详情"链接 + "退出"按钮
- **AND** "退出"按钮点击 SHALL 调 `useAuth().signOut()` 清 token + `useNavigate('/login')` 跳登录

#### Scenario: isAuthenticated === false 时不渲染

- **WHEN** `<AppShell>` mount 且 `useAuth.isAuthenticated === false`
- **THEN** `<AccountMenu>` SHALL 返回 null，DOM 中无 `data-testid="account-menu"`
- **AND** 已登录态下挂载的 `<AppShell>` 不应"泄露"未登录 UI（确保跳转回登录页后 UI 干净）

### Requirement: AccountPage SHALL 是 useCurrentAccount 的浅渲染，3 字段受 password 防护

`<AccountPage>` 组件 SHALL 调 `useCurrentAccount()` 渲染账户详情；mount 时 SHALL 自动调用 hook 的 load 触发器；3 状态机 `loading | ready | error` 同 M2-2 样板（`ProjectDetailPage` 等）；**关键**：响应 `data` SHALL NOT 包含 `password` 字段（即便后端 `AccountPublicVO` 已被前端消费层消费，反向验证 ApiContractIT 钉死 4 字段不超集）。

#### Scenario: ready 状态渲染 3 字段（id / code / account），不含 password

- **WHEN** 组件 mount 且 `useCurrentAccount` 已成功拿到账户信息
- **THEN** 组件 SHALL 渲染：① `<h1>账户详情</h1>` ② 表格三行（`id` / `code` / `account`） ③ `<Link to="/">返回列表</Link>`
- **AND** DOM 中 SHALL NOT 出现 `data-testid="account-field-password"` 之类 password 相关元素

#### Scenario: 加载与错误状态

- **WHEN** `useCurrentAccount` `isLoading === true`
- **THEN** 组件 SHALL 渲染"加载中…"
- **WHEN** `useCurrentAccount` `error` 有值
- **THEN** 组件 SHALL 渲染 `<p role="alert" data-testid="account-error">` 含 `ContractError.info` 文案
- **AND** SHALL NOT 显示任何 password 相关字段（即便 error 时也不应暴露）
