# api-data-object Capability

## Purpose

定义 aicode 接口出参的数据对象契约：Controller SHALL 返回视图对象（VO）而非持久化实体，VO SHALL NOT 序列化不参与持久化的派生字段与嵌套关联对象。元驱动表单所需的展示属性 SHALL 完整出现在 VO 中，使接口响应恰好等于前端渲染所需的最小数据集。

本能力的存在是因为接口响应一旦被导出为契约文件（见 `api-contract`），实体中的一切都会成为契约的一部分 —— 包括本不该外泄的派生字段与对象树。因此「返回 VO 而非实体」不是风格偏好，而是**契约质量的决定性因素**。

## Requirements

### Requirement: Controller SHALL 返回 VO 而非持久化实体

Controller 层 SHALL 返回 VO 而非持久化实体。实体中的派生字段、嵌套关联对象、以及与该端点消费场景无关的内部字段 SHALL NOT 出现在响应中。

**写侧端点（创建 / 修改 / 触发动作）同样适用**：写侧返回体用于「让前端确认这次写入了什么」，其字段集合 SHALL 是**该写操作实际写入的字段**，而非实体的全集。SHALL NOT 因为"实体只有 N 个字段、返回整个实体更省事"而让写侧端点直出实体。

**映射实体（`MapFieldColumn` / `MapRelationship` / `MapClassTable`）同样适用**：这三个实体是代码生成「三层映射」的核心数据结构（AGENTS §5.4），其嵌套关联对象（`displayAttribute` / `mapRelationship` / `associateClass` / `mapFieldColumnList` / `mapRelationshipList`）在列表场景下全是 `null`，进契约只是噪音。

**「出参 VO」与「入参载体」是两种职责，SHALL NOT 混用同一个类**：某个类若同时承担"接收用户提交的含敏感字段的请求"与"作为响应返回"，则敏感字段会随响应泄漏。本仓的实例：`AccountVO` 含 `password`，作为 `PUT /account/modify` 与 `DELETE /account/delete` 的**入参**是恰当的（修改/定位账户确需携带），但作为 `GET /account/list` 的**出参元素**就是凭据泄漏。出参 SHALL 用不含敏感字段的读投影。

具体清单：

- **`ProjectCreateVO`**（`POST /project/build`）：8 字段 —— `code` / `name` / `englishName` / `author` / `basePackage` / `databaseType` / `state` / `createTime`
- **`ProjectSqlVO`**（`POST /project/sql/build` 与 `/modify`）：4 字段 —— `code` / `projectCode` / `tsql` / `state`
- **`MapFieldColumnReadVO`**（`GET /project/relationship/listMapFieldColumn`）：19 → **8 字段** —— `code` / `mapClassTableCode` / `column` / `field` / `fieldType` / `isPrimaryKey` / `isDate` / `isState`
- **`MapRelationshipReadVO`**（`GET /project/relationship/list` 与 `listMapFieldColumn`）：11 → **7 字段** —— `code` / `mapClassTableCode` / `associateCode` / `isOneToOne` / `isOneToMany` / `mainField` / `joinField`
- **`MapClassTableVO`**（`GET /project/relationship/listMapClassTable`）：9 → **6 字段** —— `code` / `tableName` / `className` / `classModel` / `dashedCaseName` / `notes`
- **`RelationshipFieldsVO`**（`GET /project/relationship/listMapFieldColumn`）：3 字段 —— `mainFields: List<MapFieldColumnReadVO>` / `associateFields: List<MapFieldColumnReadVO>` / `mapRelationship: MapRelationshipReadVO | null`
- **`AccountPublicVO`**（`GET /account/list` 与 `GET /account/load`）：**3 字段** —— `id` / `code` / `account`，SHALL NOT 含 `password`

> **`MapFieldColumn` 刻意裁掉什么**：嵌套 `displayAttribute`（有专门的 `DisplayAttributesPage` 消费，且 `DisplayAttribute` 本身是 UI 契约、属另一条线）、嵌套 `mapRelationship`、`notes` / `defaultValue` / `sqlType` / `upper`、4 个派生 boolean（`checkDate` / `checkState` / `checkPk` / `oneToOne`…）。保留的 8 个正是「配关联关系 + 展示字段」所需：字段名、字段类型、主键/日期/状态标记。
>
> **`MapRelationship` 刻意裁掉什么**：自增主键 `id`、嵌套 `associateClass`（整个 `MapClassTable` 实体，列表场景必为 `null`）、派生 boolean `oneToOne` / `oneToMany`（与 `isOneToOne` / `isOneToMany` 重复 —— 前者是 boolean 字段，后者是 `YNEnum` 字符串化的结果，语义同源）。
>
> **`AccountPublicVO` 为何替代 `AccountVO`**：`password` 按 `Md5.md5()` 无盐存储，可离线爆破。列表场景只需"哪一行、什么账户、什么编码"，携带密码既无用途又是凭据泄漏。

#### Scenario: 列表接口出参为 VO

- **WHEN** 某列表接口需要返回数据库中的多行记录
- **THEN** 其出参类型 SHALL 为视图对象列表，SHALL NOT 为实体列表

#### Scenario: VO 字段是前端实际消费集的子集

- **WHEN** 某视图对象完成定义
- **THEN** 其字段集合 SHALL 不多于前端渲染所需的字段，SHALL NOT 等于实体的全部持久化字段

#### Scenario: 写侧端点 SHALL NOT 直出实体全集

- **WHEN** 写侧端点被导出到契约，生成的 schema 被检查
- **THEN** `POST /project/build` 的 `data` schema SHALL 是 `ProjectCreateVO`（8 字段），SHALL NOT 是 `Project` 实体（29 字段）
- **AND** `POST /project/sql/build` 与 `POST /project/sql/modify` 的 `data` schema SHALL 是 `ProjectSqlVO`（4 字段），SHALL NOT 是 `ProjectSql` 实体（5 字段）

#### Scenario: 写侧 VO SHALL NOT 包含实体内部的实现细节字段

- **WHEN** 写侧 VO 的字段集合与实体字段集合做集合差
- **THEN** `ProjectCreateVO` SHALL NOT 包含 `sqlFile` `downloadUrl` `buildNumber` `isRepository` `isParseTable` `isParseClass` `isIncrement` `relationshipAndDisplay`，SHALL NOT 包含任何关联 list
- **AND** `ProjectSqlVO` SHALL NOT 包含实体的 `id`（数据库自增主键 SHALL NOT 出现在任何接口响应中）

#### Scenario: 映射实体列表 SHALL 用投影 VO 而非实体

- **WHEN** `GET /project/relationship/listMapClassTable` 与 `GET /project/relationship/list` 的响应被检查
- **THEN** 前者的 `data` 元素 SHALL 是 `MapClassTableVO`（6 字段），SHALL NOT 含 `mapFieldColumnList` / `mapRelationshipList` / `id`
- **AND** 后者的 `data` 元素 SHALL 是 `MapRelationshipReadVO`（7 字段），SHALL NOT 含 `associateClass` / `id` / 派生 boolean `oneToOne` / `oneToMany`

#### Scenario: MapFieldColumn 读投影 SHALL 只含配关联与展示所需字段

- **WHEN** `MapFieldColumnReadVO` 的字段集合与 `MapFieldColumn` 实体（19 字段）做集合差
- **THEN** SHALL 恰好保留 `code` `mapClassTableCode` `column` `field` `fieldType` `isPrimaryKey` `isDate` `isState` 共 8 个
- **AND** SHALL NOT 含 `id` / `displayAttribute` / `mapRelationship` / `notes` / `defaultValue` / `sqlType` / `upper` / `checkDate` / `checkState` / `checkPk`
- **AND** 13 字段的 `MapFieldColumnVO` SHALL 保持不变（`/displayAttribute/list` 依赖它）

#### Scenario: JSONObject 型响应 SHALL NOT 以 R<Object> 出参

- **WHEN** 某端点组装的是一个结构化的组合对象（而非单一实体或列表）
- **THEN** SHALL 新建一个具名 VO 承载该结构，SHALL NOT 以 `R<Object>` / `R<JSONObject>` 出参 —— 后者的契约 `data` 是无约束的 `{}`，前端无法得知内部字段
- **AND** `GET /project/relationship/listMapFieldColumn` 的 `data` schema SHALL 是 `RelationshipFieldsVO`（3 字段），SHALL NOT 是 `object`

#### Scenario: 含凭据字段的入参载体 SHALL NOT 兼作出参

- **WHEN** 某 VO 含 `password`（或 token、密钥等凭据字段）并被用作请求入参
- **THEN** 该 VO SHALL NOT 同时出现在任何接口的响应中 —— 否则凭据会随响应流向浏览器、代理日志与前端生成物
- **AND** 对应的出参 SHALL 改用不含凭据的读投影（本仓为 `AccountPublicVO`）
- **AND** 该读投影 SHALL 被同一个 Controller 的所有出参端点共用，避免"改了一处漏了另一处"

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

### Requirement: Project 详情页与列表页 SHALL 用不同 VO 投影

`Project` 实体的 22 个持久化字段对应到前端两类页面（列表 + 详情）时 SHALL 拆为两种 VO，其字段集合 SHALL 分别反映两类页面的实际消费场景，不应是实体的全集拷贝。同一份 VO 不应同时给两种页面消费。

具体清单（与 M3-C 实施对齐）：

- **`ProjectSummaryVO`**（列表页消费集合；详见 list 页 `ProjectListPage`）：`code` / `name` / `englishName` / `state` / `createTime`，共 5 字段
- **`ProjectDetailVO`**（详情页消费集合；详见 detail 页 `ProjectDetailPage`）：`code` / `name` / `englishName` / `description` / `copyright` / `author` / `phone` / `basePackage` / `buildNumber` / `createTime` / `updateTime` / `accountCode`，共 12 字段

列表 VO 字段数量 SHALL NOT 大于 5（详情 VO 不大 12）；两类 VO 字段集合 SHALL NOT 完全相等（语义不同）。

> **后续变更原则**：每加入一个对应列表 / 详情消费场景的新端点，须重新审视该 VO 是否仍满足本 Rule。新增字段须先有前端消费需求作为依据。

#### Scenario: Project 列表接口的 records 元素 SHALL 是 ProjectSummaryVO 而非 Project 实体全字段

- **WHEN** `/project/list` 端点的 `data.records[]` 元素被导出契约 schema 检查
- **THEN** 元素 schema SHALL 是 `ProjectSummaryVO`（5 字段），SHALL NOT 包含实体的全 22 字段
- **AND** 类型为 `ProjectSummaryVO` 的字段 SHALL NOT 等于项目详情页消费的 `ProjectDetailVO` 的字段集合

#### Scenario: Project 单条详情接口的元素 SHALL 是 ProjectDetailVO 而非 Project 实体全字段

- **WHEN** `/project/load/code/{code}` 端点的 `data` 元素被导出契约 schema 检查
- **THEN** 元素 schema SHALL 是 `ProjectDetailVO`（12 字段），SHALL NOT 包含实体的全 22 字段
- **AND** 该 schema SHALL NOT 与 `ProjectSummaryVO` schema 完全一致（语义不同：列表 vs 详情）

### Requirement: VO 字段裁切反向验证 —— 列表与详情 VO SHALL NOT 退化成全字段

当新加入端点或修改字段时 SHALL NOT 退化为"返 Project 实体全集"。反向验证 SHALL 钉死这一约束。

#### Scenario: 列表 VO 字段集合 SHALL NOT 等于 Project 实体 22 字段全集

- **WHEN** 评审或回归测试 `ProjectSummaryVO` 字段集合
- **THEN** 字段集合 SHALL 与 `Project` 实体的 22 字段全集合做集合差——`ProjectSummaryVO` SHALL 不包含 `description` `databaseType` `language` `copyright` `author` `phone` `basePackage` `sqlFile` `downloadUrl` `buildNumber` `isRepository` `isParseTable` `isParseClass` `isIncrement` `updateTime` `accountCode`（除已有的 `code` `name` `englishName` `state` `createTime` 之外）
- **AND** 该断言 SHALL 在前端生成物 `aicode-web/src/generated/model/projectSummaryVO.ts` 自动从 orval 取中验证

#### Scenario: 详情 VO 字段集合 SHALL NOT 等于 Project 实体 22 字段全集

- **WHEN** 评审或回归测试 `ProjectDetailVO` 字段集合
- **THEN** 字段集合 SHALL 与 `Project` 实体的 22 字段全集合做集合差——`ProjectDetailVO` SHALL 不包含 `databaseType` `language` `state` `sqlFile` `downloadUrl` `isRepository` `isParseTable` `isParseClass` `isIncrement`（除详情页消费的 12 字段外）
- **AND** 该断言 SHALL 在 orval 生成物中验证

### Requirement: 分页列表接口 SHALL 使用 PageVO<XxxVO> 而非 Page<Object>

涉及分页返回的列表接口 SHALL 以 `PageVO<T>` 作为出参的元素容器（`data` 字段即是该 `PageVO<T>`），其元素 `T` SHALL 为某一 VO 类型，SHALL NOT 为 `Object` / `unknown`。该约束与本能力已有的"列表接口出参为 VO"Scenario 互补，专门收紧分页形态，避免 `voList: unknown` 的契约漏出。

#### Scenario: 分页列表接口的 data 元素带真实类型

- **WHEN** 某分页列表接口（如 `GET /project/list`）返回
- **THEN** 契约 schema 中 `data.records` 的元素类型 SHALL 为该接口对应的 VO 类型（如 `ProjectVO`），SHALL NOT 为 `{}` 或 `object` 之类的无约束类型

#### Scenario: 端点字段被改名前端编译失败依然成立

- **WHEN** 后端修改了某分页列表接口的响应字段（并按 `api-contract-artifact` 流程同步刷新了入库契约与 orval 生成物），而前端消费该端点的代码仍按旧字段名读取
- **THEN** `npm run typecheck` SHALL 失败，错误 SHALL 指向被改名的字段，SHALL NOT 让"由 `unknown` 兜底而跳过类型检查"成为绕过路径

#### Scenario: 接口响应不包含 Page<object> 这种不确定结果集

- **WHEN** 评审或测试任何分页列表接口
- **THEN** 导出契约中 SHALL NOT 出现 `Page` 类型本身以未指定 `voList` 元素类型的形式被引用；任何 Page 实例 SHALL 通过 `PageVO<XxxVO>` 出现

### Requirement: 凭据字段 SHALL NOT 出现在任何接口响应中

任何接口的响应 SHALL NOT 包含凭据字段（如密码、Git 仓库口令、密钥、token）。

具体到 `Frameworks` 实体：`password`（模板仓库的 Git 口令）SHALL NOT 出现在任何响应中。
`/framework/list` 已由 `2026-10-18-framework-list-vo-contract`（M5-A1）引入
`FrameworkSummaryVO`（7 字段： `id` / `code` / `name` / `description` / `gitHome` /
`account` / `isPublic`）堵住；`/framework/modify` 与 `/framework/updateIsPublic`
由 `2026-10-18-framework-write-endpoints` 一并收敛。

**同一 Controller 的所有出参端点 SHALL 共用同一个不含凭据的读投影** —— 若某个端点
单独保留实体出参，它就是契约里的定时炸弹：前端无消费者时不泄漏，但任何人接管理面、
用契约生成物提交，就立刻把凭据写进响应。

**入参侧 SHALL NOT 因该约束而裁剪**：修改 / 切换状态时携带 `password` 之类的凭据字段
作为**请求参数**是正当用途（哪怕本不需要，那是另一个问题）。判据 SHALL 是
「出现在响应的 `data` 上」还是「作为请求参数被绑定」。

#### Scenario: Git 仓库密码不随框架列表/详情接口泄漏

- **WHEN** 客户端请求 `GET /framework/list` 或 `GET /framework/load?code=`（框架技术池，含模板仓库 Git 凭据）
- **THEN** 响应的 `data` 节点（列表时为 `data.records[*]`，详情时为 `data` 本身）SHALL NOT 含 `password` 键
- **AND** 契约 schema 中对应的 VO SHALL NOT 声明 `password` 属性

#### Scenario: 凭据字段不出现在前端生成类型中

- **WHEN** orval 从入库契约生成前端类型
- **THEN** 生成物 SHALL NOT 含任何凭据字段名（`password` / `secret` / `token` 等）

#### Scenario: 反向验证：以实体直出接口 SHALL 立即暴露凭据并使断言失败

- **WHEN** 临时把某接口的出参类型从 VO 改回持久化实体（破坏本约束）
- **THEN** 对应的契约守护断言 SHALL 失败，错误信息 SHALL 明确指出 `password` 是多余字段

#### Scenario: 框架管理的写侧端点 SHALL NOT 直出实体

- **WHEN** 评审 `/framework/modify` 与 `/framework/updateIsPublic` 的响应 schema
- **THEN** 两者 SHALL ref `FrameworkSummaryVO`，SHALL NOT ref `Frameworks` 实体
- **AND** 对应 schema SHALL NOT 含 `password`
- **AND** 该约束 SHALL 与 `/framework/list` 指向**同一个** `FrameworkSummaryVO`
  （同一 Controller 的出参端点共用一个读投影）

#### Scenario: 写侧端点的契约 SHALL 导出参数

- **WHEN** 前端要用契约生成物调用 `/framework/modify` 或 `/framework/updateIsPublic`
- **THEN** 契约里这两个端点 SHALL 导出参数（`updateIsPublic` 至少含 `code` 与 `isPublic`）
- **AND** SHALL NOT 因形参被 `@Parameter(hidden = true)` 隐藏而完全无参数 ——
  那会让前端无法构造请求

#### Scenario: 入参携带凭据 SHALL NOT 被误判为泄漏

- **WHEN** 某端点的**请求参数**中包含凭据字段（如修改时携带新的 Git 口令）
- **THEN** 该字段 SHALL 保留在请求 schema 中 —— 那是它的正当用途
- **AND** 判据 SHALL 是「出现在响应的 `data` 上」而非「出现在请求里」

### Requirement: 继承 BaseVO 的 VO SHALL NOT 把查询辅助字段带入响应

视图对象（VO）SHALL NOT 继承携带**查询辅助字段**的基类（如 `com.aicode.core.BaseVO` 的
`createTimeBegin` / `createTimeEnd` / `updateTimeBegin` / `updateTimeEnd`）。这些字段的语义是
"入参：按时间范围过滤"，被 Jackson 序列化进响应即构成派生字段泄漏。

**分页容器层同样 SHALL NOT 继承该基类**：`PageVO<T>` 是所有分页端点的 `data` 类型，
它一旦继承 `BaseVO`，每个分页响应都会携带这 4 个恒为 `null` 的字段。

**出参与入参是两种职责，SHALL 分别对待**：

| 位置 | 能否继承带查询辅助字段的基类 |
|---|---|
| **出参** VO / 分页容器 | **SHALL NOT** —— 查询条件在响应里恒为 null，纯噪音 |
| **入参** DTO / 查询对象 | **MAY** —— 时间范围过滤正是其用途 |

判据 SHALL 是「该类型出现在响应的 `data` 上，还是作为请求参数被绑定」，SHALL NOT 是
「它叫 VO 还是叫 DTO」—— 命名不构成职责区分。

#### Scenario: 框架列表的 VO 字段集合不含 BaseVO 查询辅助字段

- **WHEN** 评审 `GET /framework/list` 响应的 `data.records[*]` 字段集合
- **THEN** 该集合 SHALL NOT 含 `createTimeBegin` / `createTimeEnd` / `updateTimeBegin` / `updateTimeEnd`
- **AND** 对应 VO 类 SHALL NOT 声明 `extends BaseVO`

#### Scenario: 分页容器的 data SHALL NOT 含查询辅助字段

- **WHEN** 评审任何分页端点响应的 `data` 节点字段集合（如 `RPageVOProjectSummaryVO.data`）
- **THEN** 该集合 SHALL NOT 含 `createTimeBegin` / `createTimeEnd` / `updateTimeBegin` / `updateTimeEnd`
- **AND** 分页容器类型 SHALL NOT 声明 `extends BaseVO`
- **AND** 该约束 SHALL 覆盖**所有**分页端点（`/project/list` / `/account/list` /
  `/framework/list` …），SHALL NOT 只修其中一个端点

#### Scenario: 入参 DTO SHALL 仍可继承查询辅助字段基类

- **WHEN** 某类型作为**请求参数**被绑定（`@RequestBody` 或按字段绑定的实体形参），
  且其字段承载时间范围过滤
- **THEN** 该类型 **MAY** 继承携带查询辅助字段的基类 —— 那是这些字段的正当用途
- **AND** 判据 SHALL 是「出现在响应的 `data` 上」还是「作为请求参数被绑定」，
  SHALL NOT 是「叫 VO 还是叫 DTO」

#### Scenario: 已存在继承 BaseVO 的 VO SHALL 在后续改造中被替换而非沿用

- **WHEN** 某个继承 `BaseVO` 的 VO 所在端点因其它理由被改造（如 M5-A1 的密码泄漏）
