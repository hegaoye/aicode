# Design

## Context

账户口令是**无盐 MD5**，且已因 `/account/list` 响应泄漏而落盘多处（前端生成物、代理日志、
浏览器历史）。AGENTS §11.1 早把它列为高优先级安全项，至今未做。

实施前核实又发现两个更严重的问题（都在本次范围内）：

**① `reg` 端点把明文口令直接落库**

```java
accountService.save(account);   // 前端传什么就存什么，连 MD5 都没有
```

**② 口令散列被放进 SQL 的 WHERE 子句**

```java
.eq(Account::getPassword, Md5.md5(password))
```

② 在算法层面**挡住了 BCrypt** —— 带盐散列每次结果不同，SQL 等值匹配必然失效。
故口令校验**必须**改为「先按 account 查出、再用编码器比对」。这不是可选项，是技术前提。

## Goals / Non-Goals

**Goals:**

- `Account.password` 从无盐 MD5 迁到 BCrypt；
- 登录走「先查账户、再校验口令」，散列不进 SQL；
- **存量 MD5 口令在用户下次成功登录时透明升级**（用户已拍板）；
- 修 `reg` 的明文落库。

**Non-Goals:**

- **不动 `Frameworks.password` / `ProjectRepositoryAccount.password`** —— 它们走
  `PasswordCrypto`（对称加密），因为这两个凭据**需要解密后取回**（推送到 Git 仓库、
  用于 clone/push）。单向散列会让它们不可用。
  **单向散列只适用于「只需校验、无需取回」的口令。** 这是本变更最关键的边界。
- 不改登录的响应契约（`R<String>`，token 或 null）。
- 不做「忘记密码 / 邮件重置」。
- 不改 JWT 密钥管理（AGENTS §11.1 的另一项独立议题）。
- 不强制任何用户改密码（透明升级，无感）。

## Decisions

### D1：`PasswordHasher` 放 `common`，封装三种操作

```java
public static String hash(String raw)              // BCrypt 散列
public static boolean matches(String raw, String stored)   // 双格式校验
public static boolean needsUpgrade(String stored)  // 识别 MD5 / 低 cost
```

`matches` 内部按**前缀**分派：BCrypt 散列以 `$2` 开头，其余按 MD5 十六进制串处理。
这个判据是稳定的 —— BCrypt 的模块crypt规范固定 `$2a$` / `$2b$` / `$2y$` 前缀。

**为何不引 Argon2**：BCrypt 在 Spring 生态里更成熟（`spring-security-crypto` 是官方组件，
仅依赖 spring-core）；Argon2 需要 `bouncycastle`。用户已拍板 BCrypt。

**为何放 `common`**：与既有的 `Md5` / `PasswordCrypto` 同域（它们都在
`common/src/main/java/com/aicode/core/tools/`），便于后续统一收敛散列策略。

### D2：`needsUpgrade` 用「非 BCrypt 前缀」而非「BCrypt cost 偏低」

**决定**：`needsUpgrade(stored)` = `!stored.startsWith("$2")`。

**理由**：MD5 → BCrypt 的迁移是本次唯一要处理的。cost 调优是**独立议题** ——
若顺手把 cost 阈值也写进来，将来调参会让"登录时是否改写"的行为随配置漂移，
难以推理。当前 BCrypt 用 spring-security-crypto 的默认 cost（10）。

**代价**：未来提高 cost 时，存量散列不会被自动重算。届时另开变更。

### D3：登录改造为「先查账户 → 再校验口令」

```java
Account accountObj = accountService.getOne(
        new LambdaQueryWrapper<Account>().eq(Account::getAccount, account));
if (accountObj == null) {
    return R.success();                        // 与「口令错误」同样返回空，不区分
}
if (!PasswordHasher.matches(password, accountObj.getPassword())) {
    return R.success();
}
// 透明升级：存量 MD5 通过后顺手改写
if (PasswordHasher.needsUpgrade(accountObj.getPassword())) {
    accountService.update(new Account[]{...}, wrapper);   // 或 updateById
}
```

**保留原有的「不区分账户不存在与口令错误」语义** —— 那是刻意的（防账户枚举），
沿用既有 Requirement「口令错误被拒绝」的 `data` 为空契约。

**⚠️ 改写散列的写入路径**：用 `update` 而非 `updateById`（`Account` 有 `code` 与 `id`，
需确认主键语义；实施时核实）。

### D4：透明升级只在**校验通过后**发生

**决定**：`needsUpgrade` 为真**且** `matches` 为真，才改写。

**理由**：口令错误时绝不能改写 —— 否则攻击者可以用任意错误口令触发改写，
把合法用户的口令替换成他不知道的值（账户锁死）。

顺序上必须是「先校验、后改写」，不是「看到 MD5 就先改写」。

### D5：不动 `PasswordCrypto` 的两个使用方

| 凭据 | 现状 | 处置 |
|---|---|---|
| `Account.password` | `Md5.md5()` | **本变更**：→ BCrypt |
| `Frameworks.password` | `PasswordCrypto.encrypt()`（对称） | **不动** —— 需解密后推送到 Git |
| `ProjectRepositoryAccount.password` | `PasswordCrypto.encrypt()`（对称） | **不动** —— 同上 |

**判据是「是否需要取回明文」**，不是「是不是密码」。这是本变更最重要的边界，
已在 spec 与代码注释中写明。

## Risks / Trade-offs

**[改错会锁死所有人]** → 缓解三重：① 透明升级保证旧口令继续可用，
`data.sql` 种子**无需修改** ② 守护测试必须覆盖「MD5 存量 → 首次登录成功且库中已变 BCrypt」
③ 本变更不碰 `reg` 之外的任何写入路径。

**[`PasswordCrypto` 是什么实现、有多强]** → 未在本变更评估。它是对称加密，
密钥硬编码在代码里（AGENTS §11.1 的密钥管理议题）。**本变更不动**，但值得后续评估。

**[BCrypt cost=10 会让登录变慢]** → 典型 50–100ms（单次），对登录场景可接受。
比无盐 MD5 的"1 次彩虹表查询"慢，但那正是我们要的成本。

**[存量的无盐 MD5 已泄漏]** → 本变更**不能修复已泄漏的散列**（彩虹表随时可用）。
真正的缓解是**轮换口令** —— 已登记在 AGENTS §11.1 的运维建议里。
**本变更让「轮换后的新口令」不再用弱算法**，但旧口令在轮换前依然是风险敞口。

## Migration Plan

单批交付（一个 commit）：

1. `aicode/build.gradle` 加 `spring-security-crypto`；
2. `common` 新增 `PasswordHasher` + 单测；
3. `LoginCtrl.signin` 改造（先查后校验 + 透明升级）；
4. `LoginCtrl.reg` 加散列；
5. 集成测试：`MD5 存量 → 登录成功 → 库中变 BCrypt`；
6. 契约核对（响应形状不变，零漂移）+ AGENTS 登记 + validate + archive。

**回滚**：单 commit revert。**注意**：一旦有用户登录过，散列已被改写为 BCrypt，
回滚代码会导致那些账户无法用旧算法登录 —— 但 `data.sql` 种子会在重启时被
`spring.sql.init.mode=always` 重置（AGENTS §4），开发环境无碍；生产环境回滚需配套。

## Open Questions

- **`PasswordCrypto` 的强度** —— 对称加密 + 硬编码密钥。是否应改为 KMS / Vault，
  属 AGENTS §11.1 的独立议题。
- **JWT 密钥硬编码** —— 同上，独立议题。
- **`needsUpgrade` 的 cost 策略** —— 当前只看前缀。未来提 cost 时如何让存量自动重算。
- **口令轮换的运维动作** —— 本变更不代做，已在 AGENTS §11.1 登记为运维建议。