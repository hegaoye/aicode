## ADDED Requirements

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
