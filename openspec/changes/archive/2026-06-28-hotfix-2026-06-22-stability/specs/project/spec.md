## ADDED Requirements

### Requirement: 代码生成主链路 classModel 字段自愈
The system SHALL derive `MapClassTable.classModel` from `tableName` lazily when the field is null, so that historical database rows with `classModel IS NULL` do not cause NPE during code generation.

#### Scenario: DB 脏数据：classModel 为 null
- **WHEN** a `map_class_table` row exists with `class_model = NULL` (e.g. inserted before the field was populated)
- **AND** the code generation pipeline reads this row via `selectOne`
- **THEN** `getClassModel()` MUST return the first underscore-separated segment of `tableName` (or the whole `tableName` if no underscore)
- **AND** no NPE is thrown during `generator()` model-grouping (line 544)

#### Scenario: 边界：tableName 为 null
- **WHEN** `getClassModel()` is called and both `classModel` and `tableName` are null
- **THEN** `getClassModel()` MUST return null without throwing NPE

### Requirement: 防止 classModel 列表分组比较抛 NPE
The system SHALL use null-safe equality when comparing `MapClassTable.classModel` for model-grouping, so that a null `classModel` field does not cause NullPointerException.

#### Scenario: GeneratorSVImpl:544 NPE 防御
- **WHEN** `generator()` compares `mapClassTable.getClassModel()` with `mapClassTableObj.getClassModel()` for grouping
- **THEN** the comparison MUST use `Objects.equals(...)` instead of `.equals(...)` so that two null values are treated as equal without NPE

### Requirement: 项目源码下载端点保留
The system SHALL provide `GET /project/download/{projectName}` endpoint that streams the generated ZIP file from the configured `Repository_Path` directory. The endpoint MUST be reachable through the LoginInterceptor whitelist (no token required).

#### Scenario: 前端触发项目下载
- **WHEN** a GET request hits `GET /project/download/{projectName}` (no token, with or without `.shtml` suffix)
- **THEN** the LoginInterceptor MUST return true
- **AND** the `ProjectController.downloadFile` method is invoked
- **AND** if the ZIP file exists at `Repository_Path/{projectName}.zip`, the file is streamed with `Content-Type: application/force-download` and `Content-Disposition: attachment;fileName=...`

### Requirement: 按 code 加载项目端点保留
The system SHALL provide `GET /project/load/code/{code}` endpoint that returns the project entity for a given code. The endpoint MUST be reachable through the LoginInterceptor whitelist.

#### Scenario: 前端按 code 查询项目详情
- **WHEN** a GET request hits `GET /project/load/code/{code}`
- **THEN** the LoginInterceptor MUST return true
- **AND** the `ProjectController.loadByCode` method is invoked
- **AND** the response is `R` with the matching `ProjectVO` (or empty data if not found)
