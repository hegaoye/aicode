## MODIFIED Requirements

### Requirement: 标识符以 JSON 字符串形式序列化

系统 SHALL 将所有 `Long` / `long` 类型的字段（尤其是主键 `id` 与业务编码 `code`）序列化为 JSON **字符串**，SHALL NOT 输出为 JSON 数字。原因：即使主键已改为数据库自增，业务编码仍为长数字串，且历史数据中的主键仍为 19 位雪花值，两种形态在过渡期内共存，统一字符串化可保证任何取值都不会因 JSON 数字精度问题被客户端截断。

#### Scenario: 列表接口的 id 为字符串

- **WHEN** 客户端请求任意列表接口并读取 `data.records[]` 中每一项的 `id`
- **THEN** 每个 `id` 的 JSON 类型 SHALL 为字符串，SHALL NOT 为数字

#### Scenario: 新生成的自增主键同样是字符串

- **WHEN** 系统新增一条记录（主键为自增小整数）后返回该记录
- **THEN** 其主键的 JSON 类型 SHALL 仍为字符串

#### Scenario: 标识符精度不被截断

- **WHEN** 系统返回一个 19 位数字的标识符（存量数据）
- **THEN** 返回的字符串 SHALL 与数据库中的原值逐字符相同，SHALL NOT 出现末位被改写为 `0` 的情况

#### Scenario: 标识符形态为纯数字

- **WHEN** 系统生成业务编码 `code`
- **THEN** 该值 SHALL 匹配正则 `^\d{1,19}$`，与历史数据同构

#### Scenario: 装箱与基本类型均被覆盖

- **WHEN** 某字段声明为 `Long`（装箱类型）或 `long`（基本类型）
- **THEN** 两种声明 SHALL 均被序列化为字符串
