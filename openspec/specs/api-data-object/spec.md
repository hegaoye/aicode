# api-data-object Capability

## Purpose

定义 aicode 接口出参的数据对象契约：Controller SHALL 返回视图对象（VO）而非持久化实体，VO SHALL NOT 序列化不参与持久化的派生字段与嵌套关联对象。元驱动表单所需的展示属性 SHALL 完整出现在 VO 中，使接口响应恰好等于前端渲染所需的最小数据集。

本能力的存在是因为接口响应一旦被导出为契约文件（见 `api-contract`），实体中的一切都会成为契约的一部分 —— 包括本不该外泄的派生字段与对象树。因此「返回 VO 而非实体」不是风格偏好，而是**契约质量的决定性因素**。

## Requirements

### Requirement: Controller SHALL 返回 VO 而非持久化实体

系统 SHALL 以视图对象作为接口出参的类型，SHALL NOT 直接序列化持久化实体（Entity / PO）。视图对象 SHALL 按前端消费场景裁剪字段，SHALL NOT 是实体的字段全集拷贝。

#### Scenario: 列表接口出参为 VO

- **WHEN** 某列表接口需要返回数据库中的多行记录
- **THEN** 其出参类型 SHALL 为视图对象列表，SHALL NOT 为实体列表

#### Scenario: VO 字段是前端实际消费集的子集

- **WHEN** 某视图对象完成定义
- **THEN** 其字段集合 SHALL 不多于前端渲染所需的字段，SHALL NOT 等于实体的全部持久化字段

### Requirement: VO SHALL NOT 序列化派生字段与嵌套关联对象

视图对象 SHALL NOT 包含以下两类成员：不参与持久化的**计算型 / 派生字段**（由其他字段推导得出、或仅供服务端内部使用的标记位），以及**其他实体的嵌套引用**（可能进一步引发递归与过度取数）。

若前端确有需要某类字段，SHALL 以专用的、语义明确的投影形式提供，SHALL NOT 直接内嵌实体。

#### Scenario: 派生字段不出现在响应中

- **WHEN** 实体上存在由其他字段推导得到的标记位（如「是否为日期类型」「是否主键」的布尔标记）
- **THEN** 该标记位 SHALL NOT 出现在出参中，SHALL NOT 令前端误以为它是需要处理的数据

#### Scenario: 无嵌套实体对象树

- **WHEN** 出参中需要表达关联关系
- **THEN** 该关系 SHALL 以标量字段或专用投影对象表达，SHALL NOT 内嵌另一个实体，SHALL NOT 形成对象树

#### Scenario: 避免递归与过度取数

- **WHEN** 某实体引用了其他实体，且后者又引用了更多实体
- **THEN** 该引用链 SHALL NOT 出现在响应中，SHALL NOT 造成序列化深度不受控或单次查询返回远超前端所需的数据量

### Requirement: 元驱动表单所需的展示属性 SHALL 完整出现在 VO 中

当接口服务于**元数据驱动的动态表单**时，出参 SHALL 包含渲染控件所需的全部属性：控件类型、是否必填、查询匹配方式、校验提示、显示顺序、以及在列表页与详情页的可见性。出参 SHALL NOT 要求前端回查第二个端点才能完成渲染。

#### Scenario: 单次请求即可完成表单渲染

- **WHEN** 前端需要按后端配置渲染一个动态表单
- **THEN** 单次接口响应 SHALL 包含渲染所需的全部属性，SHALL NOT 需要额外请求补全

#### Scenario: 控件类型可直接决定渲染组件

- **WHEN** 出参中包含控件类型属性
- **THEN** 前端 SHALL 能据此选择渲染组件，SHALL NOT 需要硬编码字段名到控件的映射

#### Scenario: 展示属性的嵌套表达保持扁平可读

- **WHEN** 展示属性以嵌套对象形式出现在出参中
- **THEN** 其字段名与语义 SHALL 与前端配置界面的表述一致，SHALL NOT 因实体内部命名而歧义
