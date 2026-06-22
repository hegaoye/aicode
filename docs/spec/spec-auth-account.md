# 功能规格：认证与账户（spec-auth-account）

> 通用约定（响应 `R`、错误码、JWT、分页）见 [spec-overview.md §4](spec-overview.md)。

---

## 1. 功能目标

- 提供账户登录并签发 JWT token。
- 提供账户注册。
- 提供账户管理（CRUD，当前 `AccountController` 已标 `@Deprecated`，前端未用，但需保留以兼容）。
- 系统为**单用户模式**，默认账户 `admin / 888888`。

---

## 2. 数据模型

### 表 `account`

| 列 | 类型 | 约束/默认 | 说明 |
|----|------|-----------|------|
| `id` | bigint | PK, auto_increment | 主键 |
| `code` | varchar(64) | PK, NOT NULL | 账户编码 |
| `account` | varchar(64) | NULL | 账户名 |
| `password` | varchar(64) | NULL | 密码（存 MD5） |

主键为 `(id, code)`。

### 实体 `Account`（facade/account/entity）

字段：`id:Long`、`code:String`、`account:String`、`password:String`。

### 相关枚举

- `Gender`：`Male("男")`、`Female("女")`、`Other("其他")`（账户扩展用，当前表未含）。

---

## 3. API 端点

### 3.1 登录 —— `GET /login/signin` 【核心】

- 控制器：`LoginCtrl`（`@RequestMapping("/login")`）。
- 入参（query）：`account:String`（必填）、`password:String`（必填）；隐式 `HttpServletResponse`。
- 业务规则：
  1. 断言 `account`、`password` 非空，否则返回 `Illegal_Param`。
  2. 查询：`account = :account AND password = Md5.md5(:password)`。
  3. 命中账户：`token = JwtToken.createToken("accountCode", account.code)`，返回 `R.success(token)`。
  4. 未命中：返回 `R.success()`（**注意：无 data，不报错**，前端据 `data` 是否为空判断登录成败）。
  5. 异常：返回 `R.failed(Illegal_Param)`。
- 返回：`R`，`data` 为 JWT 字符串或空。

请求示例：`GET /login/signin?account=admin&password=888888`
成功响应：`{"code":"0000","data":"eyJhbGciOiJIUzI1Ni...","success":true}`

### 3.2 注册 —— `POST /login/reg`

- 入参：`Account`（表单绑定，`@Parameter(hidden=true)`），至少 `account`、`password`。
- 规则：断言 `account`、`password` 非空 → `accountService.save(account)` → `R.success()`。
- ⚠️ 注意：现状 `reg` 直接保存（密码是否 MD5 取决于 `AccountServiceImpl.save` 实现）；重建时须与登录比对方式一致（登录用 `Md5.md5(password)` 比对，故注册须存 MD5）。

### 3.3 账户管理（`AccountController`，`@RequestMapping("/account")`，**@Deprecated**）

| 方法 | 路径 | 入参 | 返回 | 说明 |
|------|------|------|------|------|
| POST | `/account/build` | `@RequestBody AccountSaveVO` | `AccountSaveVO` | 创建账户 |
| GET | `/account/list` | `curPage:Integer`, `pageSize:Integer` | `PageVO<AccountVO>` | 分页列表（count + page） |
| PUT | `/account/modify` | `@RequestBody AccountVO` | `boolean` | 修改 |
| DELETE | `/account/delete` | `AccountVO`（隐藏） | `R` | 删除 |

---

## 4. 关键实现细节

- **JWT**（`common/core/tools/JwtToken`）：
  - `createToken(key, value)`：HMAC256，加入 claim（key→value）与过期时间（默认 60 分钟）；默认密钥内置于 `JwtToken`。
  - `verifier(token)`：校验签名与过期，返回 `DecodedJWT`。
  - `getTokenValue(token, key)`：取出 claim 值（账户 code）。
- **MD5**（`common/core/tools/Md5`）：`md5(String)` 返回十六进制摘要。
- **常量**（`common/core/enums/Constants`）：`AccountCode`（claim key = `accountCode`）、`sessionid`、`SS_TIMEOUT`、`Cookie_Maxage`。
- **拦截器现状**：`LoginInterceptor.preHandle` 从 `request.getParameter("token")` 取 token 校验，失败返回 `R.failed`（`Session_Out`/未授权）；但**未注册**到 `ContextConfiguration` 拦截链（被注释）。重建时如需鉴权，须在 `WebMvcConfigurer.addInterceptors` 注册并排除登录/静态资源路径。

---

## 5. 验收标准

- [ ] `admin/888888` 可登录并拿到非空 token。
- [ ] 错误密码登录返回 `R.success()` 但 `data` 为空。
- [ ] token 可被 `JwtToken.verifier` 校验通过，并能取出账户 code。
- [ ] 注册后的账户可用同口令登录（验证 MD5 一致性）。

---

## 6. 安全注意（重建改进项）

- token 走 URL 参数、默认弱口令、`LoginInterceptor` 未启用 —— 均为现状风险，详见 [code-style.md §13](../standards/code-style.md)。生产重建建议：token 走 `Authorization` 头、启用登录拦截、强制改密。
