# api-contract Specification

## Purpose
定义 aicode 后端 REST 接口与仓库内静态前端（`src/main/resources/static/`）之间的可观测契约。前端为 Angular 7.0.4 + ng-zorro + jQuery 的打包产物，无法在 Java 侧做类型检查，因此所有跨语言约定（ID 序列化形态、路径后缀、响应结构、日期格式）必须由后端以可执行测试的方式显式守护。本能力是 Java 25 / Spring Boot 4.1 升级系列变更的验收基准。

## Requirements

### Requirement: 标识符以 JSON 字符串形式序列化

系统 SHALL 将所有 `Long` / `long` 类型的字段（尤其是主键 `id` 与业务编码 `code`）序列化为 JSON **字符串**，SHALL NOT 输出为 JSON 数字。原因：业务编码为 19 位雪花值，且历史数据中的主键同样为 19 位，二者在过渡期内共存；统一字符串化可保证任何取值都不会被客户端的 `JSON.parse` 截断。

序列化层的实现 SHALL 使用当前运行时的默认 JSON 库提供的扩展点来注册转换规则，SHALL NOT 通过构造一个与默认序列化链路无关的独立映射实例来实现——后者会导致注册静默失效。

#### Scenario: 列表接口的 id 为字符串

- **WHEN** 客户端请求任意列表接口并读取 `data.records[]` 中每一项的 `id`
- **THEN** 每个 `id` 的 JSON 类型 SHALL 为字符串，SHALL NOT 为数字

#### Scenario: 标识符精度不被截断

- **WHEN** 系统返回一个 19 位数字的标识符
- **THEN** 返回的字符串 SHALL 与数据库中的原值逐字符相同，SHALL NOT 出现末位被改写为 `0` 的情况

#### Scenario: 标识符形态为纯数字

- **WHEN** 系统生成业务编码 `code`
- **THEN** 该值 SHALL 匹配正则 `^\d{1,19}$`

#### Scenario: 装箱与基本类型均被覆盖

- **WHEN** 某字段声明为 `Long`（装箱类型）或 `long`（基本类型）
- **THEN** 两种声明 SHALL 均被序列化为字符串

#### Scenario: 新生成的自增主键同样是字符串

- **WHEN** 系统新增一条记录（主键为自增小整数）后返回该记录
- **THEN** 其主键的 JSON 类型 SHALL 仍为字符串

#### Scenario: 转换规则确实作用于默认序列化链路

- **WHEN** 运行时切换到新的默认 JSON 库
- **THEN** 已注册的字符串化规则 SHALL 仍然生效，SHALL NOT 出现「注册成功但响应中仍为数字」的静默失效

### Requirement: 路径后缀兼容能力

系统 SHALL 支持前端使用的 `.shtml` 路径后缀。带后缀路径与无后缀路径 SHALL 指向同一处理逻辑。该能力 SHALL 由平台自身提供的兼容层实现，SHALL NOT 依赖框架的默认路由匹配行为——后者在框架版本间已发生变更。

兼容层 SHALL 只作用于平台已注册的 API 路径前缀，SHALL NOT 改写静态资源的解析路径。

#### Scenario: 带后缀路径可达

- **WHEN** 客户端以 `/project/list.shtml` 形式请求任意已注册端点
- **THEN** 系统 SHALL 返回与无后缀请求 `/project/list` 完全一致的状态码与响应体

#### Scenario: 无后缀路径仍可达

- **WHEN** 客户端以 `/project/list` 形式请求
- **THEN** 系统 SHALL 正常处理该请求

#### Scenario: 后缀兼容不覆盖静态资源

- **WHEN** 客户端请求静态资源（如 `*.css`、`*.js`、`*.svg`、`*.woff2`）
- **THEN** 系统 SHALL 按静态资源处理，SHALL NOT 因后缀兼容逻辑而改变其解析路径

#### Scenario: 后缀兼容不改变拦截器匹配

- **WHEN** 客户端以 `/login/signin.shtml` 请求登录端点
- **THEN** 登录拦截器的豁免规则 SHALL 仍然命中，SHALL NOT 返回 `9007`

#### Scenario: WebSocket 端点带后缀可达

- **WHEN** 客户端以 `ws://<host>/websocket.shtml` 建立 WebSocket 连接
- **THEN** 连接 SHALL 成功建立

#### Scenario: 兼容层不引入前缀外副作用

- **WHEN** 客户端请求一个既不在已注册 API 前缀内、也不是静态资源的路径
- **THEN** 该请求 SHALL 保持原有处理方式，SHALL NOT 被后缀兼容逻辑改写

### Requirement: 统一响应结构

系统 SHALL 始终使用统一响应包装结构返回结果，包含 `code`（业务错误码）、`info`（提示信息）、`data`（业务数据）、`success`（布尔标记）四个键。`data` 为空时 SHALL 以 `null` 呈现，SHALL NOT 省略该键。

#### Scenario: 成功响应结构

- **WHEN** 任意接口处理成功
- **THEN** 响应体 SHALL 满足 `code == "0000"`、`success == true`，且包含 `data` 键

#### Scenario: 失败响应使用标准错误码

- **WHEN** 任意接口因参数或权限原因失败
- **THEN** 响应体 SHALL 满足 `success == false`，且 `code` ∈ {`9003`（参数为空）, `9004`（参数非法）, `9007`（未授权/Token 无效）, `9999`（服务器错误）}

#### Scenario: AJAX 请求失败时返回 JSON 而非 HTML

- **WHEN** 未携带有效令牌的 AJAX 请求（携带 `x-requested-with: XMLHttpRequest`）被拦截
- **THEN** 响应体 SHALL 为 JSON 格式的失败响应，SHALL NOT 是 HTML 登录页

### Requirement: 日期格式与时区

系统 SHALL 以 `yyyy-MM-dd HH:mm:ss` 格式序列化日期时间字段，时区固定为 GMT+8。该语义 SHALL 在默认 JSON 库发生换代后仍被显式保证，SHALL NOT 依赖库的默认行为。

#### Scenario: 日期字段格式

- **WHEN** 客户端读取返回对象中的日期时间字段（如 `createTime`、`modifyTime`）
- **THEN** 该值 SHALL 匹配正则 `^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$`

#### Scenario: 日期不以时间戳形式输出

- **WHEN** 系统序列化日期时间字段
- **THEN** 输出 SHALL NOT 是纪元毫秒数字

#### Scenario: 时区固定为 GMT+8

- **WHEN** 系统格式化日期时间
- **THEN** 格式化 SHALL 使用 GMT+8 时区，SHALL NOT 受服务器本地时区影响

### Requirement: null 字段保留

系统 SHALL 在序列化响应对象时保留值为 `null` 的字段，SHALL NOT 因全局序列化配置而省略这些键。

#### Scenario: 空值字段仍出现在 JSON 中

- **WHEN** 某对象的某个字段值为 `null`
- **THEN** 序列化后的 JSON SHALL 包含该键且值为 `null`，SHALL NOT 直接省略该键

#### Scenario: 响应顶层 data 为 null 时保留

- **WHEN** 接口成功但业务数据为空
- **THEN** 响应体 SHALL 包含 `"data": null`

#### Scenario: 默认序列化库换代后语义不变

- **WHEN** 运行时切换到新的默认 JSON 库
- **THEN** null 字段的保留行为 SHALL 与切换前一致

### Requirement: 契约测试在升级前先行落地

系统的接口契约 SHALL 由可执行测试守护，且这些测试 SHALL 在任何升级变更开始之前就在当前基线上通过，作为后续升级的验收基准。

#### Scenario: 契约测试在升级前全绿

- **WHEN** 在未做任何升级的基线版本上执行契约测试
- **THEN** 全部契约场景 SHALL 通过

#### Scenario: 升级引入契约回归时测试失败

- **WHEN** 某次依赖或运行时升级破坏了任一契约场景
- **THEN** 对应的契约测试 SHALL 失败并明确指出被破坏的要求，SHALL NOT 静默通过

#### Scenario: 前端端点清单被显式登记

- **WHEN** 静态前端调用了某个后端端点
- **THEN** 该端点路径 SHALL 出现在契约测试的端点清单中，接受 `.shtml` 与无后缀双路径可达性验证
