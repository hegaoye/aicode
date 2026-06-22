# sql-reverse-parse Specification

## Purpose
TBD - created by archiving change add-core-business-tests. Update Purpose after archive.
## Requirements
### Requirement: ProjectServiceImpl.execute 建库阶段幂等
The system SHALL treat `createDatabase` as idempotent: if the H2 schema named `project.englishName` already exists, the build MUST skip the DDL execution and MUST continue to the `parse` phase (no silent success, no spurious Server_Error).

#### Scenario: 库已存在时直接进入 parse 阶段
- **WHEN** `execute(code)` is called and the schema `englishName` already exists in H2
- **THEN** `createDatabase` returns without throwing, and `parse(code)` runs

#### Scenario: 库不存在时建库并跑 SQL
- **WHEN** the schema does NOT exist
- **THEN** `createDatabase` creates the schema and runs each `state=Enable` `project_sql.tsql`

#### Scenario: 缺 projectSql 时抛 Empty_Param
- **WHEN** `projectCode` has no `state=Enable` `project_sql` row
- **THEN** `execute` throws `Empty_Param`

### Requirement: ProjectServiceImpl.parse 清场使用 MapFieldColumn::getMapClassTableCode
The system SHALL, before re-parsing, delete all `map_field_column` and `map_relationship` rows whose `mapClassTableCode` matches the project's existing `project_map` entries (NOT by the wrong field `getCode`).

#### Scenario: 二次 init 字段数与当前表列数一致
- **WHEN** `parse(code)` is called twice on the same project, the second call MUST remove all stale `map_field_column` rows for the project first
- **THEN** after the second parse, the count of `map_field_column` rows for the project equals the count of columns in the table

#### Scenario: 二次 init 关联数与新表结构一致
- **WHEN** `parse(code)` is called twice, the second call MUST remove all stale `map_relationship` rows for the project
- **THEN** the `map_relationship` rows after the second parse reflect only the current schema (no orphans)

### Requirement: ProjectServiceImpl.delete 路径不含 classpath 前缀
The system SHALL, when deleting a project, remove the workspace directory at `<Setting.Workspace>/<englishName>` and the ZIP at `<Setting.Repository_Path>/<englishName>.zip` — without prepending the JVM classpath (which would never match the configured path).

#### Scenario: 工作空间目录被删除
- **WHEN** `delete(code)` is called and the workspace directory exists
- **THEN** the directory at `<Setting.Workspace>/<englishName>` is removed (NOT `<classpath>/<Setting.Workspace>/<englishName>`)

#### Scenario: ZIP 文件被删除
- **WHEN** `delete(code)` is called and the ZIP file exists
- **THEN** the file at `<Setting.Repository_Path>/<englishName>.zip` is removed

### Requirement: ProjectServiceImpl.execute 完成后项目标记为已解析
The system SHALL set `project.isParseTable = Y` and `project.isParseClass = Y` and refresh `project.updateTime` after a successful `parse` call.

#### Scenario: parse 成功更新项目状态
- **WHEN** `parse(code)` completes successfully
- **THEN** `project.isParseTable = Y`, `project.isParseClass = Y`, and `project.updateTime` is the current time

