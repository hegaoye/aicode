# mapping-display

## Purpose

维护三层映射（类↔表、字段↔列、类间关联）以及字段的「显示属性」（DisplayAttribute）。初始映射由 `sql-parse` 的 `init` 生成，本能力允许用户通过 Web 调整映射、补 1:1 / 1:N 关联、配置字段在前端表单/列表/详情页的呈现方式。

## Requirements

### Requirement: 类表映射列表
The system SHALL return all `MapClassTable` records for a project via `GET /project/relationship/listMapClassTable`.

#### Scenario: listMapClassTable
- **WHEN** `GET /project/relationship/listMapClassTable?projectCode=<code>` is called
- **THEN** the system returns the list of `MapClassTable` (each with `tableName`, `className`, `notes`, `code`) for the project

### Requirement: 字段列查询
The system SHALL return fields of a class-table for editing relationships and display attributes.

#### Scenario: list fields
- **WHEN** `GET /project/relationship/listMapFieldColumn?mapClassTableCode=<code>&associateCode=<code>` is called
- **THEN** the system returns the field list (used to choose `mainField` / `joinField` for a relationship) plus any existing relationships

### Requirement: 关联关系维护
The system SHALL allow creating, updating, querying, and deleting class-to-class relationships via `MapRelationshipController`.

#### Scenario: create or update relationship
- **WHEN** `POST /project/relationship/build` is called with `mapClassTableCode`, `associateCode`, `oneToOne:YNEnum`, `oneToMany:YNEnum`, `mainField`, `joinField`
- **THEN** the system persists the relationship (saveOrUpdate) and returns `R`

#### Scenario: list relationships
- **WHEN** `GET /project/relationship/list?classTableCode=<code>` is called
- **THEN** the system returns the relationships owned by the given class-table

#### Scenario: modify relationship
- **WHEN** `PUT /project/relationship/modify` receives a `MapRelationshipVO`
- **THEN** the system updates and returns boolean

#### Scenario: delete relationships
- **WHEN** `DELETE /project/relationship/delete` receives one or more comma-separated `codes`
- **THEN** the system deletes the matching relationship rows and returns `R`

### Requirement: 显示属性维护
The system SHALL allow saving the list of `DisplayAttribute` records for fields via `DisplayAttributeController`.

#### Scenario: list field + display attributes
- **WHEN** `GET /displayAttribute/list?mapClassTableCode=<code>` is called
- **THEN** the system returns the fields of the class-table, each merged with its `DisplayAttribute` (if any)

#### Scenario: save display attributes
- **WHEN** `POST /displayAttribute/save` receives a `List<DisplayAttribute>`
- **THEN** the system saves each record (insert if absent, update by id if present) and returns `R`

## Notes

- **`map_class_table` 字段**：`id`, `code`(PK), `tableName`, `className`, `notes`。实体 `MapClassTable` 非表字段：`classModel`, `dashedCaseName`, `mapFieldColumnList`, `mapRelationshipList`；方法 `toJava()` 做表名→类名。
- **`map_field_column` 字段**：`id`, `mapClassTableCode`, `code`, `column`, `field`, `sqlType`, `fieldType`, `notes`, `defaultValue`, `isPrimaryKey` (Y/N), `isDate` (Y/N), `isState` (Y/N)。实体 `MapFieldColumn` 非表字段：`displayAttribute`, `mapRelationship`, `checkDate/checkState/checkPk/checkDigit:boolean`, `upper:String`；方法 `toJava()` 做列名→属性名 + sqlType→fieldType。
- **`map_relationship` 字段**：`id`, `code`(PK), `mapClassTableCode`, `associateCode`, `isOneToOne` (Y/N), `isOneToMany` (Y/N), `mainField`, `joinField`。实体非表字段：`associateClass:MapClassTable`, `oneToOne/oneToMany:boolean`。
- **`display_attribute` 字段**：`mapClassTableCode`, `mapFieldColumnCode`(PK), `isRequired`, `isInsert`, `isDeleteCondition`, `isAllowUpdate`, `isListPageDisplay`, `isDetailPageDisplay`, `isQueryRequired`, `isLineNew`, `matchType`, `displayType`, `displayName`, `displayNo`, `fieldValidationMode`, `validateText`, `displayCss`。
- **枚举 `DisplayAttributeDisplayType`**（控件类型）：Autocomplete, Cascader, DatePicker, TimePicker, Input, Textarea, InputNumber, Mobile, Phone, MobileOrPhone, Password, Email, Website, IdCard, Mention, Select, MultiSelect, Radio, Checkbox, Rate, Silder, Switch, TreeSelect, Upload。
- **枚举 `DisplayAttributeFieldValidationMode`**（验证模式）：Email, Address, Telephone, Password, Date, Number, Integer, Positive_Integer, Text, IdCard, Website。
- **枚举 `YNEnum`**：`Y(true)`, `N(false)`。
- **生成时消费**：`codegen` 把 `oneToOneList` / `oneToManyList` / `displayAttributes` / `tableFields` 装配进 `TemplateData`（见 `frameworks-template` Notes 中变量清单）。
- **Deprecated 端点**：`/project/relationship/load/code/{code}`、`/project/relationship/listByClassTableCode`、`PUT /displayAttribute/modify`、`DELETE /displayAttribute/delete`。
- **下游**：`codegen` 直接消费本能力维护的映射与显示属性。
