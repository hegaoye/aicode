# Spec Delta

## MODIFIED Requirements

### Requirement: Controller SHALL 返回 VO 而非持久化实体

Controller 层 SHALL 返回 VO 而非持久化实体。实体中的派生字段、嵌套关联对象、以及与该端点消费场景无关的内部字段 SHALL NOT 出现在响应中。

**写侧端点（创建 / 修改 / 触发动作）同样适用**：写侧返回体用于「让前端确认这次写入了什么」，其字段集合 SHALL 是**该写操作实际写入的字段**，而非实体的全集。SHALL NOT 因为"实体只有 N 个字段、返回整个实体更省事"而让写侧端点直出实体。

**映射实体（`MapFieldColumn` / `MapRelationship` / `MapClassTable`）同样适用**：这三个实体是代码生成「三层映射」的核心数据结构（AGENTS §5.4），其嵌套关联对象（`displayAttribute` / `mapRelationship` / `associateClass` / `mapFieldColumnList` / `mapRelationshipList`）在列表场景下全是 `null`，进契约只是噪音。

具体清单：

- **`ProjectCreateVO`**（`POST /project/build`）：8 字段 —— `code` / `name` / `englishName` / `author` / `basePackage` / `databaseType` / `state` / `createTime`
- **`ProjectSqlVO`**（`POST /project/sql/build` 与 `/modify`）：4 字段 —— `code` / `projectCode` / `tsql` / `state`
- **`MapFieldColumnReadVO`**（`GET /project/relationship/listMapFieldColumn`）：19 → **8 字段** —— `code` / `mapClassTableCode` / `column` / `field` / `fieldType` / `isPrimaryKey` / `isDate` / `isState`
- **`MapRelationshipReadVO`**（`GET /project/relationship/list` 与 `listMapFieldColumn`）：11 → **7 字段** —— `code` / `mapClassTableCode` / `associateCode` / `isOneToOne` / `isOneToMany` / `mainField` / `joinField`
- **`MapClassTableVO`**（`GET /project/relationship/listMapClassTable`）：9 → **6 字段** —— `code` / `tableName` / `className` / `classModel` / `dashedCaseName` / `notes`
- **`RelationshipFieldsVO`**（`GET /project/relationship/listMapFieldColumn`）：3 字段 —— `mainFields: List<MapFieldColumnReadVO>` / `associateFields: List<MapFieldColumnReadVO>` / `mapRelationship: MapRelationshipReadVO | null`

> **为何新 VO 带 `Read` 后缀而非裁改已有的**：crud 生成器已产出同名的 `MapFieldColumnVO`（13 字段）
> 与 `MapRelationshipVO`（8 字段），但它们**已被占用** ——
> `MapFieldColumnVO` 是 `/displayAttribute/list` 的现行出参（`DisplayAttributesPage` 依赖），
> `MapRelationshipVO` 的 `id` 是 `PUT /modify` 查询必需的入参。裁改二者会破坏既有功能，
> 故读投影另起 `*ReadVO`，并在类头 Javadoc 写清职责区别。

> **`MapFieldColumn` 刻意裁掉什么**：嵌套 `displayAttribute`（有专门的 `DisplayAttributesPage` 消费，且 `DisplayAttribute` 本身是 UI 契约、属另一条线）、嵌套 `mapRelationship`、`notes` / `defaultValue` / `sqlType` / `upper`、4 个派生 boolean（`checkDate` / `checkState` / `checkPk` / `oneToOne`…）。保留的 8 个正是「配关联关系 + 展示字段」所需：字段名、字段类型、主键/日期/状态标记。
>
> **`MapRelationship` 刻意裁掉什么**：自增主键 `id`、嵌套 `associateClass`（整个实体）、派生 boolean `oneToOne` / `oneToMany`（与 `isOneToOne` / `isOneToMany` 重复 —— 前者是 boolean 字段，后者是 `YNEnum` 字符串化的结果，语义同源）。

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