# 功能规格：SQL 导入与反向解析（spec-sql-parse）

> 通用约定见 [spec-overview.md §4](spec-overview.md)。本域是「SQL 反向驱动」的核心入口，对应主流程第 3、4 步。

---

## 1. 功能目标

- 为项目保存/修改建表 SQL 脚本（`tsql`）。
- 执行 `init`：在内嵌 H2 中按项目 `englishName` 建库、执行用户 SQL 建表，再反向解析库表元数据，生成「类↔表（`map_class_table`）」与「字段↔列（`map_field_column`）」映射。

---

## 2. 数据模型

### 表 `project_sql`

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | bigint PK | 主键 |
| `projectCode` | varchar(64) PK | 项目编码 |
| `code` | varchar(64) | tsql 编码 |
| `tsql` | text | SQL 脚本原文 |
| `state` | varchar(16) | Enable/Disenable/Delete |

枚举 `ProjectSqlState`：`Enable("启用")`、`Disenable("停用")`、`Delete("删除")`。

### 解析输出表（由本域写入，详见 [spec-mapping-display.md](spec-mapping-display.md)）

- `project_map`：项目↔类表映射关联（`projectCode`, `mapClassTableCode`）。
- `map_class_table`：`code`, `tableName`, `className`, `notes`。
- `map_field_column`：`code`, `mapClassTableCode`, `column`, `field`, `sqlType`, `fieldType`, `notes`, `defaultValue`, `isPrimaryKey`, `isDate`, `isState`。

### 数据库自省实体（facade/database/entity，映射 information_schema）

- `Table`：`tableName`、`remarks`。
- `Column`：`columnName`、`columnDefault`、`isNullable`、`typeName`、`remarks`。
- `Database`：`catalogName`、`schemaName`、`defaultCahracterSetName`、`defaultCollationName`。

---

## 3. API 端点

### 3.1 保存 SQL —— `POST /project/sql/build` 【核心】

- 控制器：`ProjectSqlController`（`@RequestMapping("/project/sql")`）。
- 入参：`ProjectSql`（隐藏绑定）—— 至少 `projectCode`、`tsql`。
- 规则：`projectSqlService.save()`；设置 `code`、`state = Enable`。
- 返回：`R.success()`。

请求示例：`POST /project/sql/build?projectCode=<code>&tsql=<CREATE TABLE...>`

### 3.2 查询 SQL —— `GET /project/sql/load`

- 入参：`code`、`projectCode`。
- 规则：`getOne` 返回脚本详情。
- 返回：`R`，`data` 为 `ProjectSql`。

### 3.3 修改 SQL —— `POST /project/sql/modify`

- 入参：`ProjectSqlVO`。
- 规则：先 `remove`（按 projectCode）再 `save`（覆盖式更新）。
- 返回：`R`。

### 3.4 已废弃端点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/project/sql/load/projectCode/{projectCode}` | 按项目编码查（@Deprecated） |
| GET | `/project/sql/list` | 列表（@Deprecated） |
| DELETE | `/project/sql/delete` | 删除（@Deprecated） |

### 3.5 执行初始化（建库+解析）—— `POST /project/init` 【核心】

定义于 `ProjectController`，调用 `projectService.execute(code)`。**这是本域最关键的流程**，详见 §4。

---

## 4. 核心流程：`execute(code)` = 建库 + 解析

`ProjectServiceImpl.execute(code)` 顺序执行两步，任一失败抛 `Server_Error`：

### 4.1 第一步 `createDatabase(code)`

1. 校验 `code` 非空、项目存在（否则 `Empty_Param` / `Result_Not_Exist`）。
2. 取项目全部 `project_sql`；`database = project.englishName`；若无 SQL 或 database 空 → `Empty_Param`。
3. 若该 database 在 H2 中尚不存在（`databaseDAO.count(database) <= 0`）：
   - 读 `Setting(DefaultDatabase)`。
   - 对每条 `state=Enable` 的 SQL：`databaseDAO.createDatabase(database, tsql, defaultDatabaseSetting)` —— 在 H2 中创建以 `englishName` 命名的 schema 并执行用户建表 SQL。
   - 返回 true。

> SQL 兼容：H2 运行于 `MODE=MYSQL`。`SqlConvertEnum`（common 枚举）提供一组正则用于清洗 MySQL 方言（如 `DROP TABLE IF EXISTS`、`ENGINE=`、`DEFAULT CHARSET=`、`AUTO_INCREMENT`、注释等），使脚本可在 H2 执行。用户须保证 SQL 合法（参考 README 关于非法 `"order"` 双引号的说明）。

### 4.2 第二步 `parse(code)` —— 反向解析生成映射

1. 校验项目存在。
2. **清理旧映射**：遍历项目现有 `project_map`，删除对应 `map_class_table`、`map_field_column`、`map_relationship`，再删 `project_map`。
3. 自省表：`tableDAO.list(englishName)` 查 information_schema 得表列表；为空 → `Result_Not_Exist`。
4. 对每张表：
   - `columnDAO.list(englishName, tableName)` 取列；为空 → `Result_Not_Exist`。
   - 建 `MapClassTable(code=UID, tableName, notes=remarks)` 并调 `mapClassTable.toJava()`：**表名 → 类名**（下划线转大驼峰，如 `user_order` → `UserOrder`）。
   - 写 `project_map`（项目↔该类表）。
   - 写 `map_class_table`。
   - 对每列建 `MapFieldColumn`：
     - `column = columnName`、`sqlType = typeName`、`notes = remarks`、`defaultValue = columnDefault`。
     - `isPrimaryKey = DatabaseDataTypesUtils.isPrimaryKey(isNullable) ? Y : N`。
     - `isDate = DatabaseDataTypesUtils.isDate(typeName) ? Y : N`。
     - `isState`：默认 N；若列 `remarks` 经 `StringTools.getStateOrType(remarks)` 解析出状态/枚举映射（非空）则置 Y。
     - 调 `mapFieldColumn.toJava()`：**列名 → 属性名**（驼峰化）、**sqlType → fieldType**（Java 类型，经 `DatabaseDataTypesUtils`/`JavaPrimitiveTypeMapping`，如 `bigint→Long`、`varchar→String`、`datetime→Date`）。
   - 批量写 `map_field_column`（`batchInsert`）。
5. 置 `project.isParseTable=Y`、`isParseClass=Y`、刷新 `updateTime`，更新项目。
6. 返回 true。

### 4.3 类型映射关键工具（common/core/tools/core/typemapping）

- `DatabaseDataTypesUtils`：`isPrimaryKey(...)`、`isDate(typeName)`、SQL→Java 类型判定。
- `JavaPrimitiveTypeMapping` / `JdbcType` / `DatabaseTypeUtils`：JDBC 类型 ↔ Java 类型映射表。
- 重建时须覆盖至少：bigint/int/tinyint→Long/Integer、varchar/text/char→String、datetime/timestamp/date→Date、decimal/numeric→BigDecimal、bit/boolean→Boolean、double/float→Double。

---

## 5. 解析结果查询

解析完成后，前端用 `GET /project/relationship/listMapClassTable?projectCode=<code>` 查看生成的类表映射列表（见 [spec-mapping-display.md](spec-mapping-display.md)）。

---

## 6. 验收标准

- [ ] 保存 SQL 后 `init` 能在 H2 建出以 `englishName` 命名的库并建表。
- [ ] 每张表生成一条 `map_class_table`（类名为驼峰）与一条 `project_map`。
- [ ] 每列生成一条 `map_field_column`，主键/日期/状态标志与 Java 类型正确。
- [ ] 重复 `init` 会先清理旧映射再重建（幂等）。
- [ ] 解析后项目 `isParseTable=Y`、`isParseClass=Y`。

---

## 7. 关联规格

- 映射的人工调整、关联关系、显示属性 → [spec-mapping-display.md](spec-mapping-display.md)
- 用映射生成代码 → [spec-codegen.md](spec-codegen.md)
