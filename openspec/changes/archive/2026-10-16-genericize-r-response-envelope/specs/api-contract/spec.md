# Spec Delta

## ADDED Requirements

### Requirement: 统一响应包装的 data SHALL 携带真实结构

统一响应包装的 `data` 字段 SHALL 携带接口出参的真实结构，SHALL NOT 是无类型占位（schema 为空对象）。接口 SHALL 通过泛型返回类型声明其出参形状，使序列化框架能够据此推导出 `data` 的具体 schema。

#### Scenario: 契约中的 data 有具体 schema

- **WHEN** 导出 OpenAPI 契约并查看统一响应包装的类型定义
- **THEN** 其 `data` 字段 SHALL 引用具体的出参 schema，SHALL NOT 为空对象

#### Scenario: 列表接口的 data 表达元素数组

- **WHEN** 某接口返回一组记录
- **THEN** 契约中该接口响应的 `data` SHALL 表达为对应元素类型的数组

#### Scenario: 无数据接口的 data 表达为空

- **WHEN** 某接口只返回成功与否、不携带数据
- **THEN** 其 `data` SHALL 表达为空，SHALL NOT 引用任意结构类型

### Requirement: 出参形状 SHALL 由声明类型承载而非运行期推断

接口出参的形状 SHALL 由方法签名上的泛型参数承载，SHALL NOT 依赖运行期反射推断或由调用方事后解释。同一方法在不同分支返回形态不一致的 `data` 时，SHALL 统一为可表达该分支的类型。

#### Scenario: 方法签名即出参契约

- **WHEN** 查看任一接口方法的声明
- **THEN** 其返回类型 SHALL 直接表明 `data` 的类型，SHALL NOT 是无类型包装

#### Scenario: 分支形态不一致时显式收窄

- **WHEN** 某方法在成功与失败分支返回形态不同的 `data`
- **THEN** 该方法的返回类型 SHALL 显式表达二者的共同类型，SHALL NOT 让契约出现二义

### Requirement: 泛型化 SHALL NOT 改变运行期 JSON 结构

将响应包装改为泛型 SHALL 仅影响编译期类型检查，SHALL NOT 改变任何接口在运行期返回的 JSON 字段名、字段类型与取值。

#### Scenario: 泛型化后响应结构不变

- **WHEN** 完成泛型化并执行既有的接口契约测试
- **THEN** 全部响应结构断言 SHALL 通过，SHALL NOT 因签名变化而出现差异