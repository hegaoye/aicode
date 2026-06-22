# sql-parse

## Purpose

把用户提供的 `CREATE TABLE` 脚本保存为项目资产，并在初始化阶段在 H2 中按 `englishName` 建库建表，再用 `information_schema` 反向解析出「类↔表」与「字段↔列」映射。是「SQL 反向驱动」的核心入口。

## Requirements

### Requirement: 保存 SQL 脚本
The system SHALL persist the user-provided DDL against a project via `POST /project/sql/build`.

#### Scenario: save tsql
- **WHEN** `POST /project/sql/build?projectCode=<code>&tsql=<sql>` is called
- **THEN** the system assigns a `code`, sets `state = Enable`, and returns `R.success()`

### Requirement: 修改 SQL 脚本
The system SHALL support replacing the stored SQL via `POST /project/sql/modify`.

#### Scenario: modify tsql
- **WHEN** `POST /project/sql/modify` is called with a `ProjectSqlVO`
- **THEN** the system removes the existing `project_sql` row by `projectCode` and saves the new payload (replace, not merge)

### Requirement: 查询 SQL 脚本
The system SHALL return the stored SQL via `GET /project/sql/load`.

#### Scenario: load tsql
- **WHEN** `GET /project/sql/load?code=<code>&projectCode=<projectCode>` is called
- **THEN** the system returns the `ProjectSql` in `data`

### Requirement: 项目初始化（建库 + 解析）
The system SHALL, on `POST /project/init?code=<projectCode>`, run the two-phase `execute(code)` flow.

#### Scenario: phase 1 — create database
- **WHEN** the project exists, has `englishName`, and has at least one `state = Enable` `project_sql` row
- **THEN** the system creates a H2 schema named `englishName` (only if `databaseDAO.count(englishName) <= 0`), runs each `project_sql.tsql` through `databaseDAO.createDatabase(englishName, tsql, setting.DefaultDatabase)`, and reads `Setting(DefaultDatabase)` for default-db hints

#### Scenario: phase 1 — no sql
- **WHEN** the project has no `state = Enable` `project_sql`, or `englishName` is empty
- **THEN** the system returns `R.failed(Empty_Param)`

#### Scenario: phase 2 — reverse parse
- **WHEN** phase 1 succeeds
- **THEN** the system (a) clears the project's existing `project_map` and the `map_class_table` / `map_field_column` / `map_relationship` rows referenced by them, (b) lists tables from `information_schema` for `englishName` (empty → `Result_Not_Exist`), (c) for each table, lists its columns and writes one `MapClassTable` + one `project_map` + one `MapFieldColumn` per column, (d) sets `project.isParseTable = Y`, `project.isParseClass = Y`, refreshes `updateTime`

#### Scenario: re-init is idempotent
- **WHEN** `init` is called twice on the same project
- **THEN** the system produces a fresh mapping set without duplicates (old mapping rows are removed first)

#### Scenario: derive class and field names
- **WHEN** a `MapClassTable` is built from a table
- **THEN** `className` is the table name converted to UpperCamelCase (e.g. `user_order` → `UserOrder`)

- **AND** for each column, `field` is the column name converted to lowerCamelCase, and `fieldType` is derived from `sqlType` via `DatabaseDataTypesUtils` / `JavaPrimitiveTypeMapping` (e.g. `bigint → Long`, `varchar → String`, `datetime → Date`, `decimal → BigDecimal`, `bit/boolean → Boolean`)

- **AND** `isPrimaryKey`, `isDate`, `isState` flags are set by `DatabaseDataTypesUtils` + `StringTools.getStateOrType(remarks)` (state Y if remarks parses to a state/enum mapping)

#### Scenario: any failure
- **WHEN** either phase throws or yields no tables
- **THEN** the system surfaces `Server_Error` / `Result_Not_Exist` via the global `R` failure path

## Notes

- **`project_sql` 表**：`id`, `projectCode`(PK), `code`, `tsql(text)`, `state`。枚举 `ProjectSqlState`: `Enable/Disenable/Delete`。
- **写出的下游表**（schema 定义在 `db/schema.sql`）：`project_map`, `map_class_table`, `map_field_column`。
- **`information_schema` 自省实体**（facade/database/entity，**不**写入业务表）：`Table`(`tableName`, `remarks`)、`Column`(`columnName`, `columnDefault`, `isNullable`, `typeName`, `remarks`)、`Database`(`catalogName`, `schemaName`, ...)。
- **H2 MySQL 兼容**：`MODE=MYSQL` 模式下运行 DDL。`common` 的 `SqlConvertEnum` 用一组正则清洗 MySQL 方言（`DROP TABLE IF EXISTS`、`ENGINE=`、`DEFAULT CHARSET=`、`AUTO_INCREMENT`、注释等）以便在 H2 执行。
- **SQL 合法性**：`README.md` 提到双引号包裹的关键字（如 `"order"`）会让执行失败；用户须保证 SQL 合法。
- **类型映射必备覆盖**（重建底线）：`bigint/int/tinyint → Long/Integer`，`varchar/text/char → String`，`datetime/timestamp/date → Date`，`decimal/numeric → BigDecimal`，`bit/boolean → Boolean`，`double/float → Double`。
- **Deprecated 端点**：`/project/sql/load/projectCode/{projectCode}`、`/project/sql/list`、`/project/sql/delete`。
- **下游**：`mapping-display` 消费本能力写出的 `map_class_table` / `map_field_column`；`codegen` 消费完整映射生成代码。
