# Proposal

## Why

账户口令目前是 **无盐 MD5**，且已因 `/account/list` 的响应泄漏而落盘多处
（前端生成物、Nginx 访问日志、浏览器历史 —— 见 `2026-10-18-account-list-security-and-pagination`）。

无盐 MD5 可用彩虹表 + GPU 秒级爆破常见口令。AGENTS §11.1 早就把
「密码存储：当前为 MD5，应升级为 BCrypt 或 Argon2」列为**高优先级安全加固项**，
至今未做。

### 顺带发现两个更严重的问题

**① `reg` 端点把明文口令直接落库**

```java
@PostMapping("/reg")
public R<Void> reg(@Parameter(hidden = true) Account account) {
    accountService.save(account);   // ← 前端传什么就存什么，连 MD5 都没有
    return R.success();
}
```

这比 MD5 更严重 —— **明文**进库。本变更一并修复。

**② 口令散列被放进 SQL 的 WHERE 子句**

```java
.eq(Account::getAccount, account)
.eq(Account::getPassword, Md5.md5(password))   // ← 散列进 SQL
```

这有两个后果：① 散列值会出现在 SQL 日志里（若开启）；② **它挡住了 BCrypt** ——
BCrypt 每次加随机盐，相同口令的散列**互不相同**，SQL 等值匹配必然失效。

故口令校验**必须**改为「先按 account 查出账户、再用编码器校验口令」。这不是可选项，
是 BCrypt 化的技术前提。

## What Changes

### 1. 口令散列：MD5 → BCrypt

新增 `common` 的 `PasswordHasher`（BCrypt 封装，基于 `spring-security-crypto`）：

- `hash(明文)` → `$2a$...`（含随机盐与 cost）
- `matches(明文, 散列)` → 校验
- `needsUpgrade(散列)` → 判断是否需要重算（MD5 或 BCrypt cost 偏低）

### 2. 登录：双格式并存 + 透明升级

用户已拍板**登录时透明升级**：

```
存储值以 $2 开头 → BCrypt 校验
否则（存量 MD5）  → MD5 校验；通过则**顺手改写为 BCrypt**
```

用户无感（旧口令继续能登录），每次登录自动把存量账户收敛到新格式。

### 3. `reg` 端点：散列后再存

明文入口直接 `save` 的缺陷一并修掉。

### 4. 依赖

`spring-security-crypto`（Spring 官方，仅依赖 spring-core，无侵入）。

**不动** `Frameworks.password` / `ProjectRepositoryAccount.password` —— 它们走
`PasswordCrypto`（对称加密），因为这两个凭据**需要解密后使用**（推送到 Git 仓库）。
用单向散列会拿不回明文。**单向散列只适用于「只需校验、无需取回」的口令。**

## BREAKING

`data.sql` 种子里 `admin` / `aicode` 的口令是 MD5 散列值 —— 透明升级机制保证它们
**首次登录时自动转为 BCrypt**，无需改种子数据。

**但**任何**外部**以 MD5 散列写入的存量账户（若有）也走同一路径：首次登录成功后自动升级。

## Capabilities

### Modified Capabilities

- **`auth-account`** —— MODIFIED Req「单用户账户存储」：
  该 Req 现有 Scenario 明写「返回值 SHALL 是 **MD5 十六进制串**」—— 本变更改为
  「SHALL 是带盐的自适应散列（BCrypt），SHALL NOT 是无盐 MD5」，并新增
  「登录 SHALL NOT 把口令散列放进 SQL 的 WHERE 子句」+「存量弱散列 SHALL 在登录成功时透明升级」
  两个 Scenario。

## Impact

- **后端**：`common` 新增 `PasswordHasher`；`aicode/build.gradle` 加依赖；
  `LoginCtrl.signin` 与 `reg` 改造；`contracts/openapi/openapi.yaml`（零变化 —— 不影响响应形状）。
- **测试**：新增 `PasswordHasher` 单测（≥6 条：同口令两次散列不同 / matches 正确 /
  错口令不匹配 / needsUpgrade 识别 MD5 / 明文不是合法散列）；
  `LoginCtrl` 透明升级的集成测试。
- **前端**：零改动。
- **风险**：**中**。改的是**登录主链路** —— 改错会锁死所有人。
  缓解：透明升级保证旧口令继续可用；`data.sql` 种子无需改；
  守护测试必须覆盖「MD5 存量 → 首次登录成功且库中已变 BCrypt」。