# Tasks

## 1. 依赖 + `PasswordHasher`

- [x] 1.1 `aicode/build.gradle` 加 `spring-security-crypto`（版本由 Spring Boot BOM 管理，**不显式写版本** —— AGENTS §6.1；实施时确认 Boot 4.1.1 的 BOM 确实管理它，若不管理再显式写版本） *(已完成：`common/build.gradle` 加 `spring-security-crypto`（BOM 管理，解析到 7.1.1，不显式写版本）。**依赖最初加错了模块**（放 aicode，代码在 common）→ 移正。另**必须显式补 `spring-core`**（见 1.1 的实施备注）)*
- [x] 1.2 `common/src/main/java/com/aicode/core/tools/PasswordHasher.java`： *(已完成：`PasswordHasher`（hash / matches / needsUpgrade / isBcrypt）；Javadoc 写明「单向散列只适用于只需校验、无需取回的凭据；Git 口令 SHALL NOT 用本类」)*
  - `hash(String raw)` → BCrypt 散列（`$2a$` 开头）
  - `matches(String raw, String stored)` → 按前缀分派：`$2` 走 BCrypt，其余走 `Md5.md5(raw).equals(stored)`
  - `needsUpgrade(String stored)` → `!stored.startsWith("$2")`
  - 类头 Javadoc SHALL 说明「单向散列只适用于只需校验、无需取回的凭据；需解密后使用的凭据（Git 口令）SHALL NOT 用本类」
  - 验证 `./aicode/gradlew :common:compileJava` exit=0

## 2. `PasswordHasher` 单测

- [x] 2.1 新建 `common/src/test/java/com/aicode/core/tools/PasswordHasherTest.java`，≥6 条： *(已完成：**7 条**（`common` 无 AssertJ，按其既有约定用 JUnit 原生断言）—— hash 产出 $2 格式 / **同口令两次散列不同**（盐）/ matches BCrypt 正误 / **matches 兼容存量 MD5** / needsUpgrade 识别旧格式 / **脏数据返回 false 不抛异常**（含形似 BCrypt 但非法）/ hash(null) 抛 IAE)*
  ① 同一口令散列两次**结果不同**（随机盐）
  ② `matches(明文, BCrypt散列)` 为 true
  ③ `matches(错口令, BCrypt散列)` 为 false
  ④ `matches(明文, Md5散列)` 为 true（**兼容存量**）
  ⑤ `matches(错口令, Md5散列)` 为 false
  ⑥ `needsUpgrade(Md5散列)` 为 true、`needsUpgrade(BCrypt散列)` 为 false
  ⑦ **明文不是合法散列时 `matches` 返回 false 而非抛异常**（防脏数据导致 500）
- [x] 2.2 **反向验证**：临时让 `matches` 对 MD5 恒返回 true → 用例 ⑤ 变红 *(**成立**：MD5 分支恒返回 true → 用例变红)*

## 3. `LoginCtrl.signin` 改造

- [x] 3.1 改为「先按 account 查账户，SHALL NOT 把口令散列放进 SQL」： *(已完成：改为只按 account 查 + 应用内比对；改写用 `updateById`（`Account` 主键 id）)*
  核实散列改写的写入方式（`update` vs `updateById`，`Account` 的主键语义）并用对
- [x] 3.2 **保留「不区分账户不存在与口令错误」语义**：两种情况都返回 `R.success()`（data 为空），不签发 token *(通过：账户不存在与口令错误都返回 `R.success()`（data 为空），不签发 token)*
- [x] 3.3 **透明升级**：`matches` 通过**且** `needsUpgrade` 为真 → 把口令列改写为 `hash(raw)`。 *(已完成：**先校验、后改写**（design D4）—— 口令错误时绝不改写，否则攻击者可用错误口令把合法用户锁死)*
  **顺序必须是「先校验、后改写」**（design D4 —— 口令错误时改写会把合法用户锁死）
- [x] 3.4 验证 `:aicode:compileJava` exit=0；`Md5` 的 import 若已无用则清理 *(通过：compileJava exit=0；`Md5` 的 import 随替换自动移除)*

## 4. `LoginCtrl.reg` 加散列

- [x] 4.1 `reg` 在 `save` **之前**对 `account.getPassword()` 散列（修明文落库缺陷） *(已完成：`reg` 在 save 前 `setPassword(PasswordHasher.hash(...))` —— 修明文落库缺陷（此前直接 `save(account)`）)*
- [x] 4.2 验证 `reg` 现有行为未被破坏（账号/口令非空校验仍在） *(通过：账号/口令非空校验仍在)*

## 5. 集成测试（守护透明升级）

- [x] 5.1 新建或扩展 `LoginCtrl` 的集成测试： *(已完成：`LoginPasswordMigrationIT` **4 条**（真实数据库 H2 test profile）—— MD5 存量登录成功且已变 BCrypt / **口令错误不改写** / BCrypt 存量保持原值不反复重算 / BCrypt 存量错误口令不通过)*
  ① **MD5 存量 → 登录成功 + 库中口令已变 BCrypt**（透明升级的核心守护）
  ② **口令错误时 SHALL NOT 改写**（防账户锁死）
  ③ BCrypt 存量 → 登录成功且口令列**保持原值**（不反复重算）
- [x] 5.2 核对 `db/data.sql` 的种子口令（MD5）**无需修改** —— 透明升级保证首次登录即可 *(**无需修改种子** —— 透明升级保证首次登录即可。**但我在测试里猜错了种子明文**（写成 `admin`，实际是 `888888`）→ 用例当场失败，反推 MD5 才确认。已修正)*
- [x] 5.3 **反向验证**：临时把透明升级逻辑去掉 → 用例 ① 变红 *(**成立**：去掉透明升级逻辑 → 用例 ① 变红（`库中口令 SHALL 已被改写 expected: not equal but was: 21218cca77804d2ba...`）)*

## 6. 验收 + 文档 + 登记

- [x] 6.1 全套测试：`JAVA_HOME=$(/usr/libexec/java_home -v 25) ./aicode/gradlew test`，登记三模块用例数 *(实测：aicode **214**（210 + 4）/ 2 跳过 / 0 失败；facade 39；common **63**（56 + 7）/ 1 跳过 / 0 失败 —— 合计 **316**)*
- [x] 6.2 契约核对：登录响应形状未变（`R<String>`），`git diff --stat contracts/openapi/openapi.yaml` 应为空或仅 description 变化 *(**契约零变化**（`git diff --stat` 为空）—— 登录响应形状未变)*
- [x] 6.3 `openspec validate --strict 2026-10-18-password-bcrypt-migration` 与 `--specs` *(openspec validate --strict 与 --specs 均通过（14/14）)*
- [x] 6.4 `AGENTS.md §11.1`：把「密码存储：当前为 MD5」改为「**已升级 BCrypt**（`PasswordHasher`，含存量透明升级）」；并保留「已泄漏的旧 MD5 仍需轮换口令」的运维建议 *(已完成：§11.1「密码存储：当前为 MD5」改为「已升级为 BCrypt」+ 透明升级机制 + **保留「已泄漏的旧 MD5 仍需轮换」**的运维建议)*
- [x] 6.5 `AGENTS.md §10`：登记「**口令散列不得放进 SQL 的 WHERE 子句**」—— 它既泄漏散列值，也在算法层面排除带盐方案 *(已完成：§10 新增两条实测坑 —— ①「口令散列 SHALL NOT 放进 SQL 的 WHERE 子句」（含"它在算法层面排除带盐散列"的解释）②「spring-security-crypto 在 Boot BOM 下把 spring-core 标为 optional，必须显式补」（含 `common` 测试依赖只有 junit-jupiter 的提醒）)*
- [x] 6.6 同步 delta spec 到 `auth-account`，然后 `openspec archive` *(已完成：主 spec 物化 —— auth-account 该 Req 的 Scenario 3 → **7**（新增写入散列 / 校验不进 SQL / 透明升级 / 不反复重算）；14/14 通过；变更已 archive)*
- [x] 6.7 commit（中文 message） *(已完成：中文 message，单变更单 commit)*