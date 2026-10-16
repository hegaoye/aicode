# Spec Delta

## ADDED Requirements

### Requirement: 会接收客户端输入的形参 SHALL NOT 被隐藏而从契约消失

方法的形参若承载客户端输入（`@RequestBody`，或无 `@RequestBody` 时按字段名绑定的
实体形参），SHALL NOT 标注 `@Parameter(hidden = true)`。

该注解会让 springdoc **忽略整个形参** —— 连方法级 `@Parameters` 块里对该参数的声明
也会被盖掉，结果是契约里该端点**完全没有参数**，前端拿不到任何可调用的函数签名
（orval 生成的函数只有 axios 配置项，没有业务参数）。

**判据 SHALL 是「该形参是否承载客户端输入」**，SHALL NOT 是「参数是否复杂」或
「是否为实体」—— 后者恰恰是写侧端点的常态（实体按字段名绑定 query/form 参数）。

#### Scenario: 形参带 hidden 时契约 SHALL 完全无参数

- **WHEN** 某端点的方法形参承载客户端输入（如实体形参、按字段绑定的字符串），
  且标了 `@Parameter(hidden = true)`
- **THEN** 契约里该端点 SHALL NOT 出现 `parameters` 段
- **AND** orval 生成的函数签名 SHALL 只有 axios 配置项、没有业务参数
- **AND** 前端 SHALL 无法据此构造请求

#### Scenario: 写侧端点的契约 SHALL 导出其业务参数

- **WHEN** 评审任一写侧端点（创建 / 修改 / 删除 / 状态切换）的契约
- **THEN** 该端点 SHALL 导出其业务参数（如 `code` / `isPublic` / `projectCode`）
- **AND** SHALL NOT 因形参被隐藏而退化为「无参数端点」

#### Scenario: 响应体形参（@RequestBody）不受本 Requirement 约束

- **WHEN** 某形参标注 `@RequestBody`
- **THEN** 它 SHALL 照常出现在契约的 `requestBody` 段 —— 该场景本就不依赖 `@Parameters`
- **AND** 本 Requirement 针对的是**被 `@Parameter(hidden = true)` 抹掉的普通形参**