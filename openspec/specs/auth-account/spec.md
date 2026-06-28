# auth-account

## Purpose

提供单用户模式的账户认证与账户维护能力：登录签发 JWT token，注册写入新账户，账户 CRUD 接口（已标 `@Deprecated`，保留以兼容）。系统默认账户 `admin / 888888`，所有 token 走 URL query 参数（`?token=...`），密码在库内以 MD5 形式存储。
## Requirements
### Requirement: 登录
The system SHALL sign in a user via `GET /login/signin` and return a JWT token in the `R.data` field on success.

#### Scenario: correct credentials
- **WHEN** `GET /login/signin?account=admin&password=888888` is called
- **THEN** the system looks up `account = :account AND password = Md5.md5(:password)`, signs a JWT via `JwtToken.createToken(Constants.AccountCode, account.code)`, and returns `R.success(token)`

#### Scenario: wrong credentials
- **WHEN** the account exists but the password MD5 does not match
- **THEN** the system returns `R.success()` with empty `data` (success code, no payload — the client decides failure from the empty data)

#### Scenario: missing parameters
- **WHEN** `account` or `password` is empty
- **THEN** the system returns `R.failed(Illegal_Param)`

### Requirement: 注册
The system SHALL allow new account registration via `POST /login/reg`.

#### Scenario: register then sign in
- **WHEN** `POST /login/reg` is called with `account` and `password`
- **THEN** the system persists the account (the stored `password` MUST be MD5-equal to the plaintext so that `/login/signin` MD5 comparison succeeds on next sign-in)

### Requirement: 账户 CRUD
The system SHALL expose account CRUD endpoints under `/account` for backward compatibility (currently marked `@Deprecated`).

#### Scenario: create account
- **WHEN** `POST /account/build` receives `AccountSaveVO`
- **THEN** the system creates and returns the account

#### Scenario: list accounts
- **WHEN** `GET /account/list?curPage=N&pageSize=M` is called
- **THEN** the system returns a `PageVO<AccountVO>` (count + page)

#### Scenario: modify account
- **WHEN** `PUT /account/modify` receives `AccountVO`
- **THEN** the system updates and returns boolean true

#### Scenario: delete account
- **WHEN** `DELETE /account/delete` receives `AccountVO`
- **THEN** the system deletes and returns `R`

### Requirement: token-based identification
The system SHALL identify the calling account by a JWT claim keyed by `Constants.AccountCode` (value `accountCode`), where the claim value is the account's `code` (UID-derived string), and SHALL use HMAC256 with the default secret built into `JwtToken`, defaulting to 60-minute expiry.

#### Scenario: token verification
- **WHEN** a caller invokes a protected endpoint with `?token=<jwt>` in the URL
- **THEN** `JwtToken.verifier(token)` returns a `DecodedJWT` whose `accountCode` claim is the caller's account code

### Requirement: 登录入口兼容 .shtml 后缀
The system SHALL treat any URL under `/login/**` as a public login entry, including paths with suffixes such as `.shtml` (e.g. `/login/signin.shtml`), even when the request carries no token. The LoginInterceptor MUST NOT return false for these requests.

#### Scenario: 前端以 .shtml 后缀调用登录入口
- **WHEN** a GET request hits `GET /login/signin.shtml?account=admin&password=888888&token=`
- **THEN** the LoginInterceptor MUST return true (skip the interceptor chain)
- **AND** the LoginController.signin method is invoked
- **AND** the response is `R` with `code=0000` on success

### Requirement: 静态资源白名单覆盖多段路径
The system SHALL treat any URL under `/assets/**` as a public static resource path, including multi-segment asset paths used by the Angular frontend (e.g. `/assets/monaco/vs/editor/editor.main.js`). The LoginInterceptor MUST NOT return false for these requests.

#### Scenario: Angular 子目录静态资源加载
- **WHEN** a GET request hits `GET /assets/img/logo.png` or `GET /assets/monaco/vs/editor/editor.main.js`
- **THEN** the LoginInterceptor MUST return true
- **AND** the ResourceHttpRequestHandler serves the file with correct Content-Type and Content-Length matching the actual file size

#### Scenario: 多段后缀静态资源
- **WHEN** a GET request hits any URL matching `/**/*.png`, `/**/*.svg`, `/**/*.woff`, `/**/*.css`, etc.
- **THEN** the LoginInterceptor MUST return true and the static resource is served

## Notes

- **JWT helpers** (`common/core/tools/JwtToken`): `createToken(key, value)`, `verifier(token)`, `getTokenValue(token, key)`.
- **MD5 helper** (`common/core/tools/Md5`): `md5(String) -> hex`.
- **Constants** (`common/core/enums/Constants`): `AccountCode`, `sessionid`, `SS_TIMEOUT`, `Cookie_Maxage`.
- **`LoginInterceptor` 现状**：实现了从 `request.getParameter("token")` 取 token 并校验的逻辑，但**未挂载**到 `ContextConfiguration` 的拦截链（被注释掉）。目前没有任何端点被它保护 —— 不要假设它能拦截请求。
- **Auth on protected endpoints**：`project` / `frameworks` / `template` / `displayAttribute` 等端点要求调用方在 URL 里带 `?token=...`，由调用方（前端 / `.opencode` skill）持有。
- **Default account**：种子账户 `admin / 888888` 存于 `db/data.sql`（password 列是 MD5），每次启动都会被 `spring.sql.init.mode: always` 重建。
- **Deprecated endpoints**（`/account/load/code/{code}`、`/account/modify/password`）保留以兼容外部脚本。
- **Security red lines** (see `docs/standards/code-style.md` §13): token 走 URL 是已知缺口；生产应改为 `Authorization` 头、挂载 `LoginInterceptor`、强制改默认口令、关闭 H2 控制台 `web-allow-others`。
- **依赖**：`session` 域的 `LoginCtrl` 是登录端点宿主；所有下游 capability 在受保护调用时引用本能力的 token 约定。
