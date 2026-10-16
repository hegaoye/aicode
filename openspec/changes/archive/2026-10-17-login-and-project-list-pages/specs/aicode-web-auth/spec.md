# Spec Delta

## Purpose

定义 aicode-web 中"前端登录态管理与 HTTP 鉴权注入"的可执行形状：JWT 令牌 SHALL 由 `LoginPage` 通过 `GET /login/signin` 取得并存入 `localStorage`；axios 拦截器 SHALL 把有效令牌注入 `Authorization: Bearer <token>` 请求头（按 `auth-account` spec 的"请求头优先 + URL 参数兼容"优先级），URL 参数传递 SHALL NOT 是新接口默认；受保护页面 SHALL 被路由守卫包裹，无令牌时跳转 `/login`；后端 `code=9007` SHALL 自动清空本地令牌并回登录页。本能力面向所有未来受保护页面，是 M1 业务页面样板在鉴权维度上的延展。

## ADDED Requirements

### Requirement: JWT 令牌 SHALL 由 LoginPage 取得并存入 localStorage

`LoginPage` SHALL 调 `signin({ account, password })` 取得 JWT 字符串后 SHALL 调 `token-store.set(token)` 落 `localStorage`；后续请求 SHALL 通过 `token-store.get()` 读取同一处取值，不应在多个位置分散持久化。

#### Scenario: 登录成功后 token 写入 localStorage

- **WHEN** 用户在 `LoginPage` 提交正确的账号口令并后端返回 `code=0000` + JWT 字符串
- **THEN** `localStorage["aicode.token"]` SHALL 等于该字符串，且 `LoginPage` SHALL 触发路由跳转至 `/project/list`

#### Scenario: 登录失败时不写入 token

- **WHEN** 后端返回 `code != "0000"`（如 9003、9004）
- **THEN** `localStorage["aicode.token"]` SHALL NOT 被设置或被改写，UI SHALL 展示错误信息（来自 `ContractError.info`）

### Requirement: axios 拦截器 SHALL 把有效令牌注入 Authorization Bearer 请求头

应用启动时 SHALL 注册一次 axios 请求拦截器：当 `token-store.get()` 返非空字符串时，SHALL 把请求头 `Authorization` 设为 `Bearer <token>`；当 `token-store.get()` 返空时 SHALL NOT 设此头；URL 查询参数 `?token=` SHALL NOT 被本拦截器主动添加（新接口不依赖 URL 参数），仅当调用方自行传入时透传。

#### Scenario: 持有 token 时注入 Bearer

- **WHEN** 任意受保护业务页面发起请求且 `localStorage["aicode.token"]` 非空
- **THEN** 实际请求 SHALL 携带 `Authorization: Bearer <token>` 头，且 SHALL NOT 自动追加 `?token=` 到 URL

#### Scenario: 无 token 时不附加 Authorization

- **WHEN** 业务页面发起请求且 `localStorage["aicode.token"]` 不存在
- **THEN** 实际请求 SHALL NOT 带 `Authorization` 头，请求 SHALL NOT 在 URL 中追加 `?token=`

#### Scenario: 拦截器不重叠注入

- **WHEN** 同一应用内注册拦截器两次
- **THEN** 仅一次 `Authorization` 头被注入，SHALL NOT 出现重复或叠加

### Requirement: 受保护页面 SHALL 被路由守卫包裹，无令牌时跳转 /login

`App` 的路由 SHALL 在 `/project/list`、`/project/:code` 等受保护路由外层包 `<RequireAuth>` 组件；`RequireAuth` SHALL 在渲染前调 `token-store.get()`；空字符串 SHALL 用 `react-router-dom/Redirect`（或 `Navigate`）跳 `/login`。`/login` 与 `/login.shtml` SHALL NOT 被守卫。

#### Scenario: 无 token 访问 /project/list 跳转 /login

- **WHEN** 用户访问 `/project/list` 且 `localStorage["aicode.token"]` 不存在
- **THEN** 浏览器 SHALL 渲染 `/login` 而非 `/project/list`

#### Scenario: 有 token 访问 /project/list 正常渲染

- **WHEN** 用户访问 `/project/list` 且 `localStorage["aicode.token"]` 非空
- **THEN** `RequireAuth` SHALL 渲染其子组件（即 `ProjectListPage`）

### Requirement: 后端 9007 SHALL 清空本地令牌并强制回 /login

前端 SHALL 注册 axios 响应拦截器：当 `R.code === "9007"` 或 HTTP 401 时 SHALL `token-store.clear()`、并通过 `use-auth.signOut()` 与路由跳 `/login`（重复一次以保证 reload 行为）。该行为是后端 `auth-account` spec 中"令牌过期后被拒绝"的镜像。

#### Scenario: 收到 9007 清空 token 并跳 /login

- **WHEN** 受保护接口返回 `code: "9007"`
- **THEN** `localStorage["aicode.token"]` SHALL 在该响应处理过程中被清除，且浏览器 SHALL 在下一个事件循环跳到 `/login`

#### Scenario: 收到非 9007 不清空

- **WHEN** 受保护接口返回 `code: "9999"`（服务器错误）
- **THEN** 本地 token SHALL 不被清除，UI SHALL 仅展示错误（不让网络错误与授权错误共享同一处理路径）

### Requirement: token 生命周期 SHALL 仅在 token-store 一处管理

`localStorage["aicode.token"]` SHALL 是该前端唯一的 token 持久化入口；其他位置 SHALL NOT 单独缓存 token。读取也只通过 `token-store.get()`。任何修改 SHALL 经过 `set` / `clear` 方法，SHALL NOT 直接写 `localStorage`。

#### Scenario: 多处读到的 token 一致

- **WHEN** LoginPage 写入 token 后再被 RequireAuth 读出
- **THEN** 两次读到的 SHALL 是同一字符串；SHALL NOT 因为不同步刷新或 cache 导致一方空一方非空

#### Scenario: 清除入口唯一

- **WHEN** 任何代码片段想清除登录态
- **THEN** SHALL 仅通过 `token-store.clear()`；仓库内 SHALL NOT 出现直接 `localStorage.removeItem("aicode.token")` 的调用
