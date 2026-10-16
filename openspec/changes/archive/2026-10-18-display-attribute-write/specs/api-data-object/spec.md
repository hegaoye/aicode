# Spec Delta

## MODIFIED Requirements

### Requirement: 元驱动表单所需的展示属性 SHALL 完整出现在 VO 中

`DisplayAttribute` 的各字段（控件类型、校验规则、列表/详情显隐、查询匹配方式、可插入/更新等）
SHALL 完整出现在对应 VO 中 —— 它们是**元驱动表单的 UI 契约**，字段缺失会让生成的前端表单退化为
默认渲染。

其中**受约束字段（枚举型）的值域 SHALL 在契约中以 `enum` 表达**，SHALL NOT 只把值域写在
`description` 的自然语言里。理由：前端要据此生成下拉选项与做类型校验；值域藏在描述里，
前端既无法生成控件也无法校验，只能硬编码一份自己的副本（必然漂移）。

具体受约束字段：

- **`displayType`** —— 取值自 `DisplayAttributeDisplayType`（`Input` / `InputNumber` /
  `DatePicker` / `Select` / `Cascader` / `Upload` / … 共 24 个）
- **`matchType`** —— 查询匹配方式（`=` / `!=` / `>=` / `<=` / `>` / `<` / `like` /
  左 like / 右 like 等）
- **`fieldValidationMode`** —— 字段校验方式（`Email` / `Address` / `Telephone` 等）

#### Scenario: 单次请求即可完成表单渲染

- **WHEN** 前端需要按后端配置渲染一个动态表单
- **THEN** 单次接口响应 SHALL 包含渲染所需的全部属性，SHALL NOT 需要额外请求补全

#### Scenario: 控件类型可直接决定渲染组件

- **WHEN** 出参中包含控件类型属性
- **THEN** 前端 SHALL 能据此选择渲染组件，SHALL NOT 需要硬编码字段名到控件的映射

#### Scenario: 展示属性的嵌套表达保持扁平可读

- **WHEN** 展示属性以嵌套对象形式出现在出参中
- **THEN** 其字段名与语义 SHALL 与前端配置界面的表述一致，SHALL NOT 因实体内部命名而歧义

#### Scenario: 受约束字段在契约中 SHALL 表达值域

- **WHEN** 导出契约并检查 `DisplayAttributeVO` 的 `displayType` / `matchType` / `fieldValidationMode`
- **THEN** 三者 SHALL 渲染为 `enum`（带 `enum:` 列表），SHALL NOT 是无约束的 `type: string`
- **AND** `enum` 的取值 SHALL 与后端枚举类的取值集合一致（不多不少）

#### Scenario: 前端 SHALL 从契约 enum 读值域而非硬编码副本

- **WHEN** 前端需要为 `displayType` 生成下拉选项
- **THEN** SHALL 从契约生成物（orval 产出的枚举类型 / 联合类型）取值
- **AND** SHALL NOT 在前端源码里另写一份字符串数组副本 —— 那份副本会在后端新增枚举时静默过期
