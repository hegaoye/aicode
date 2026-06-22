# project

## Purpose

以「项目」为聚合根承载一次代码生成的全部配置：基础元数据、SQL 脚本、所选框架、映射数据、模块、仓库账户、构建任务。提供创建、查询、修改、级联删除、文件扫描与下载能力。`englishName` 同时作为内嵌 H2 的 schema 名。

## Requirements

### Requirement: 创建项目
The system SHALL create a project via `POST /project/build`.

#### Scenario: required fields complete and unique
- **WHEN** `POST /project/build` is called with `name`, `englishName`, `phone`, `author`, `copyright`, `databaseType`, `description`, `language`, `basePackage` all present
- **AND** no existing project shares the same `englishName`
- **THEN** the system assigns `id = uidGenerator.getUID()`, sets `code = String.valueOf(id)`, defaults `state = Enable`, `isIncrement = N`, `downloadUrl = "DownloadUrl"`, `createTime/updateTime = now`, trims any trailing `.` from `basePackage`, persists the row, and returns `R` with `data` containing the new project (caller reads `data.code` for the next step)

#### Scenario: missing required field
- **WHEN** any of the required fields is empty
- **THEN** the system returns `R.failed(Empty_Param)`

#### Scenario: duplicate english name
- **WHEN** another project already exists with the same `englishName`
- **THEN** the system returns `R.failed(Exists)`

### Requirement: 项目初始化入口
The system SHALL expose `POST /project/init` as the entry point that triggers SQL-driven database creation and reverse parsing.

#### Scenario: init calls execute
- **WHEN** `POST /project/init?code=<projectCode>` is called
- **THEN** the system delegates to `projectService.execute(code)` (see `sql-parse`) and returns `R.success()`

### Requirement: 项目详情聚合
The system SHALL return a project detail that aggregates its frameworks, mappings, SQL, repository accounts, modules, and jobs via `GET /project/load`.

#### Scenario: load by code
- **WHEN** `GET /project/load?code=<projectCode>` is called
- **THEN** the system loads the project plus its related lists and returns them in `data` (`projectFramworkList`, `projectMapList`, `projectJobList`, `projectModuleList`, `projectRepositoryAccountList`, `projectSqlList`, `relationshipAndDisplay`)

### Requirement: 项目列表
The system SHALL return a paginated list of projects via `GET /project/list`.

#### Scenario: paged list
- **WHEN** `GET /project/list?curPage=N&pageSize=M` is called
- **THEN** the system filters by `state != Delete` and returns a paged result honoring `curPage` (1-based) and `pageSize` (default 25)

### Requirement: 修改项目
The system SHALL update a project via `PUT/POST /project/modify`.

#### Scenario: modify
- **WHEN** the endpoint receives a `Project` payload
- **THEN** the system loads the existing record, applies updates, refreshes `updateTime`, and returns `R`

### Requirement: 级联删除项目
The system SHALL delete a project and all of its dependent rows and artifact files via `POST /project/delete`.

#### Scenario: delete cascade
- **WHEN** `POST /project/delete` is called with a project `code`
- **AND** `code` is non-empty
- **THEN** the system deletes (by project `code` / class-table `code`):
- `project` row
- `project_framwork`, `project_sql`, `project_repository_account`, `project_module`, `project_job`, `project_job_logs`
- for each `project_map`: `map_field_column`, `map_relationship`, `project_map`
- artifact directory `Workspace/<englishName>` and ZIP `Repository_Path/<englishName>.zip` (paths resolved from `Setting`)
- **AND** returns `R`

#### Scenario: empty code
- **WHEN** `code` is empty
- **THEN** the system returns `R.failed(Empty_Param)`

### Requirement: 文件扫描与下载
The system SHALL support browsing generated artifacts and downloading the packaged ZIP.

#### Scenario: scan path
- **WHEN** `GET /project/scan/path?code=<projectCode>&filePath=<path>` is called
- **THEN** the system returns the file tree under `<path>` inside the project's workspace

#### Scenario: download zip
- **WHEN** `GET /project/download/{projectName}` is called
- **THEN** the system streams `Repository_Path/<projectName>.zip` to the response (this endpoint is `@Deprecated` and the path variable name is intentionally `proejctName` — leave as-is)

## Notes

- **`Project` 非表字段**（`@TableField(exist = false)`）：`projectFramworkList`, `projectMapList`, `projectJobList`, `projectModuleList`, `projectRepositoryAccountList`, `projectSqlList`, `relationshipAndDisplay`。
- **枚举 `ProjectState`**：`Enable("启用")` / `Disenable("停用")` / `Delete("删除")`。
- **`englishName` 是 schema 名**：`ProjectServiceImpl.execute` 据此在 H2 中 `CREATE SCHEMA` 并建表，因此必须全局唯一。
- **Deprecated 端点**：`/project/load/code/{code}`。
- **跨能力依赖**：本能力是聚合根，几乎被所有其它 capability 引用 —— SQL 入 `sql-parse`、框架入 `frameworks-template`、映射入 `mapping-display`、仓库入 `settings-repository`、构建入 `codegen`。
- **Cascade 顺序**：先删子表（按 `projectCode` 关联），再删主表行，最后清工作空间与 ZIP。文件删除失败不应阻塞数据删除（`R` 仍返回成功），但要落日志。
