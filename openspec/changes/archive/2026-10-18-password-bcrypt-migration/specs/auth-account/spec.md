# Spec Delta

## MODIFIED Requirements

### Requirement: 单用户账户存储

系统 SHALL 以单用户模式运行，仅内置一个账户用于登录；账户记录持久化在关系库中，口令以**不可逆散列**形式存储，数据库中 SHALL NOT 出现明文口令。

口令散列 SHALL 是**带随机盐的自适应散列**（BCrypt 一类的方案），SHALL NOT 是**无盐**的
MD5 —— 无盐散列可用彩虹表 + GPU 秒级爆破常见口令；而账户口令散列曾随
`GET /account/list` 的响应泄漏（见 `api-data-object` 的凭据 Requirement），
落盘在前端生成物与代理日志中，这类泄漏**不可追溯清理**。

**写入路径 SHALL 散列后再落库**：任何接收明文口令的入口（注册、改密）SHALL NOT
把未经散列的值直接持久化。

**校验 SHALL 不依赖数据库的等值匹配**：由于带盐散列每次结果不同（同一口令两次散列不同），
登录 SHALL 先按账户名查出记录、再用散列算法比对，SHALL NOT 把口令散列放进 SQL 的
`WHERE` 子句 —— 那既会让散列值出现在 SQL 日志里，也在算法层面排除了带盐方案。

**存量弱散列 SHALL 在用户下次成功登录时透明升级**：系统 SHALL 识别旧格式散列，
在其校验通过后把库中值改写为新格式。用户无感（旧口令继续可用），每次登录逐步收敛。

#### Scenario: 默认账户可登录

- **WHEN** 使用内置账户 `admin` 与其正确口令调用登录接口
- **THEN** 系统返回 `code=0000`，且 `data` 为一个 JWT 字符串

#### Scenario: 口令以散列形式落库

- **WHEN** 查询账户表的口令列
- **THEN** 返回值 SHALL 是 BCrypt 散列（以 `$2` 开头），SHALL NOT 是无盐 MD5 十六进制串，
  也 SHALL NOT 是明文口令
- **AND** 同一口令散列两次 SHALL 不同（含随机盐）

#### Scenario: 口令错误被拒绝

- **WHEN** 使用正确账户名与错误口令调用登录接口
- **THEN** 系统返回 `code=0000` 且 `data` 为空（不区分「账户不存在」与「口令错误」），SHALL NOT 签发 token

#### Scenario: 写入路径 SHALL 散列后再落库

- **WHEN** 任何接收明文口令的入口被调用（注册 / 改密）
- **THEN** 持久化的 SHALL 是散列值，SHALL NOT 是调用方传入的原文
- **AND** 数据库中 SHALL NOT 出现明文口令

#### Scenario: 校验 SHALL NOT 依赖数据库等值匹配

- **WHEN** 登录接口按账户名查询账户记录
- **THEN** 查询条件 SHALL 只含账户名，SHALL NOT 含口令散列
- **AND** 口令比对 SHALL 在应用内用散列算法完成
- **AND** 该约束 SHALL 在带盐方案下成立（同口令两次散列不同，SQL 等值匹配必然失效）

#### Scenario: 存量弱散列 SHALL 在成功登录时透明升级

- **WHEN** 某账户的口令列存的是旧格式（无盐 MD5），用户用正确口令登录
- **THEN** 登录 SHALL 成功（`code=0000` 且签发 token）
- **AND** 该账户的口令列 SHALL 在本次登录过程中被改写为新格式（BCrypt）
- **AND** 用户 SHALL 无需任何操作或感知
- **AND** 若口令错误 SHALL NOT 触发改写

#### Scenario: 新格式账户 SHALL 正常登录且不被反复改写

- **WHEN** 某账户的口令列已是 BCrypt 散列，用户用正确口令登录
- **THEN** 登录 SHALL 成功
- **AND** 该账户的口令列 SHALL 保持原值（不每次登录都重新散列）

