# 功能规格：模型映射与显示属性（spec-mapping-display）

> 通用约定见 [spec-overview.md §4](spec-overview.md)。本域是「三层映射」的人工调优层，决定生成代码的结构与前端表现。映射的初始数据由 [spec-sql-parse.md](spec-sql-parse.md) 的 `init` 生成。

---

## 1. 功能目标

- 查看/调整 SQL 解析出的「类↔表」「字段↔列」映射。
- 维护类与类之间的关联关系（一对一 / 一对多），用于生成关联查询/导航代码。
- 维护每个字段的「显示属性」（DisplayAttribute），驱动前端表单、列表、详情页、查询条件的生成。

---

## 2. 数据模型

### 表 `map_class_table`（类↔表）

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | bigint PK | 主键 |
| `code` | varchar(64) PK | 映射编码 |
| `tableName` | varchar(64) | 表名 |
| `className` | varchar(64) | 类名（由表名驼峰化） |
| `notes` | varchar(512) | 注释 |

实体 `MapClassTable` 非表字段：`classModel`（类所在模块）、`dashedCaseName`（kebab 命名）、`mapFieldColumnList`、`mapRelationshipList`。方法 `toJava()`：表名→类名。

### 表 `map_field_column`（字段↔列）

| 列 | 类型 | 默认 | 说明 |
|----|------|------|------|
| `id` | bigint PK | | 主键 |
| `mapClassTableCode` | varchar(64) | | 所属类表编码 |
| `code` | varchar(64) | | 字段映射编码 |
| `column` | varchar(64) | | 列名 |
| `field` | varchar(64) | | 属性名 |
| `sqlType` | varchar(32) | | 列类型 |
| `fieldType` | varchar(64) | | Java 属性类型 |
| `notes` | varchar(512) | | 注释 |
| `defaultValue` | varchar(128) | | 默认值 |
| `isPrimaryKey` | varchar(1) | 'N' | 是否主键 |
| `isDate` | varchar(1) | 'N' | 是否时间类型 |
| `isState` | varchar(1) | 'N' | 是否状态字段 |

实体 `MapFieldColumn` 非表字段：`displayAttribute`、`mapRelationship`、`checkDate/checkState/checkPk/checkDigit:boolean`、`upper:String`。方法 `toJava()`：列名→属性名、sqlType→fieldType。

### 表 `map_relationship`（类间关联）

| 列 | 类型 | 默认 | 说明 |
|----|------|------|------|
| `id` | bigint PK | | 主键 |
| `code` | varchar(64) PK | | 关系编码 |
| `mapClassTableCode` | varchar(64) | '' | 主类表编码 |
| `associateCode` | varchar(64) | | 被关联类表编码 |
| `isOneToOne` | varchar(1) | 'N' | 是否一对一 |
| `isOneToMany` | varchar(1) | 'N' | 是否一对多 |
| `mainField` | varchar(32) | | 主表关联属性 |
| `joinField` | varchar(32) | | 从表关联属性 |

实体 `MapRelationship` 非表字段：`associateClass:MapClassTable`、`oneToOne/oneToMany:boolean`。

### 表 `display_attribute`（字段前端显示属性）

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | bigint PK | 主键 |
| `mapClassTableCode` | varchar(64) | 类模型编码 |
| `mapFieldColumnCode` | varchar(64) PK | 字段编码 |
| `isRequired` | varchar(2) | 是否必填 Y/N |
| `isInsert` | varchar(2) | 是否插入 |
| `isDeleteCondition` | varchar(2) | 是否删除条件 |
| `isAllowUpdate` | varchar(2) | 是否允许修改 |
| `isListPageDisplay` | varchar(2) | 是否列表页显示 |
| `isDetailPageDisplay` | varchar(2) | 是否详情页显示 |
| `isQueryRequired` | varchar(2) | 是否查询条件 |
| `isLineNew` | varchar(2) | 是否换行 |
| `matchType` | varchar(16) | 匹配方式：=,!=,>=,<=,>,<,like,左like,右like,between,in |
| `displayType` | varchar(32) | 控件类型（见枚举） |
| `displayName` | varchar(64) | 显示列名称 |
| `displayNo` | int | 显示顺序 |
| `fieldValidationMode` | varchar(32) | 验证模式（见枚举） |
| `validateText` | varchar(64) | 验证提示语 |
| `displayCss` | varchar(32) | CSS 样式 |

### 枚举

**`DisplayAttributeDisplayType`（控件类型）**：Autocomplete, Cascader, DatePicker, TimePicker, Input, Textarea, InputNumber, Mobile, Phone, MobileOrPhone, Password, Email, Website, IdCard, Mention, Select, MultiSelect, Radio, Checkbox, Rate, Silder, Switch, TreeSelect, Upload（各带中文描述）。

**`DisplayAttributeFieldValidationMode`（验证模式）**：Email, Address, Telephone, Password, Date(实现中为各项), Number, Integer, Positive_Integer, Text, IdCard, Website。

**`YNEnum`**：`Y(true)`、`N(false)`。

---

## 3. API 端点

### 3.1 模型关系控制器（`MapRelationshipController`，`@RequestMapping("/project/relationship")`）

#### 查看类表映射列表 —— `GET /project/relationship/listMapClassTable` 【核心】
- 入参：`projectCode`。
- 规则：`projectMapService.list`（按项目）+ `mapClassTableService.list` 组装，返回该项目所有 `MapClassTable`（含 tableName/className/注释）。
- 返回：`R`，`data` 为列表。

#### 查询某类的字段（用于设关联）—— `GET /project/relationship/listMapFieldColumn`
- 入参：`mapClassTableCode`、`associateCode`。
- 规则：列出字段供选择 `mainField`/`joinField`；附带已有关系。
- 返回：`R`。

#### 查询关联关系列表 —— `GET /project/relationship/list`
- 入参：`classTableCode`。
- 返回：`R`，该类的关联关系列表。

#### 创建/更新关联 —— `POST /project/relationship/build` 【核心】
- 入参：`mapClassTableCode`、`associateCode`、`oneToOne:YNEnum`、`oneToMany:YNEnum`、`mainField`、`joinField`。
- 规则：`mapRelationshipService.saveOrUpdate()`，生成 `code`。
- 返回：`R`。

#### 修改关联 —— `PUT /project/relationship/modify`
- 入参：`@RequestBody MapRelationshipVO`。返回 `boolean`。

#### 删除关联 —— `DELETE /project/relationship/delete`
- 入参：`codes`（支持逗号分隔多个编码）。返回 `R`。

#### 已废弃：`GET /project/relationship/load/code/{code}`、`GET /project/relationship/listByClassTableCode`。

### 3.2 显示属性控制器（`DisplayAttributeController`，`@RequestMapping("/displayAttribute")`）

#### 查询字段显示属性 —— `GET /displayAttribute/list` 【核心】
- 入参：`mapClassTableCode`。
- 规则：`mapFieldColumnService.list`（该类所有字段）+ 每字段 `displayAttributeService.getOne`，组装字段及其显示属性返回。
- 返回：`R`，`data` 为字段+显示属性列表。

#### 保存显示属性 —— `POST /displayAttribute/save` 【核心】
- 入参：`@RequestBody List<DisplayAttribute>`。
- 规则：逐条 `save`（无则插入）或 `updateById`（有则更新）。
- 返回：`R`。

#### 已废弃：`PUT /displayAttribute/modify`、`DELETE /displayAttribute/delete`。

---

## 4. 业务规则要点

- 一个字段的显示属性以 `mapFieldColumnCode` 为关键，决定它在生成的前端代码中是否出现在列表页/详情页、用什么控件、是否必填、查询匹配方式等。
- 关联关系 `mainField`（主表属性）+ `joinField`（从表属性）定义 JOIN 条件；`isOneToOne`/`isOneToMany` 决定生成单对象还是集合属性。
- 关联与显示属性最终在生成时被装配进 `TemplateData`（`oneToOneList`、`oneToManyList`、`displayAttributes`、`tableFields`），见 [spec-codegen.md](spec-codegen.md)。

---

## 5. 验收标准

- [ ] `listMapClassTable` 返回项目解析出的全部类表。
- [ ] 设置一对多关联后，关系表正确记录 `mainField`/`joinField` 与 `isOneToMany=Y`。
- [ ] 保存显示属性后，再次 `list` 能读回设置值。
- [ ] 删除关联支持一次传多个编码。
