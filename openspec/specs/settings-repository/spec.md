# settings-repository

## Purpose

提供系统级 K-V 配置（`Setting`，驱动 `codegen` 与 `sql-parse` 定位工作空间、模板路径、ZIP 路径等关键目录）和项目级 Git/SVN 产物仓库凭据（`project_repository_account`，驱动 `codegen` 的检出/推送）。还覆盖其它项目从属配置（`module` / `project_model` / `project_code_catalog`），它们目前是 `@Deprecated` 控制器，保留以兼容。

## Requirements

### Requirement: 必备系统设置键
The system SHALL have the following `Setting` rows seeded by `db/data.sql` so that `sql-parse` and `codegen` can locate their directories:

| 键 | 含义 | 读取方 |
|---|---|---|
| `DefaultDatabase` | 默认数据库 | `sql-parse` phase 1 |
| `Gradle_Directory_Structure` | gradle 目录结构 | `codegen` |
| `Workspace` | 工作目录根 | `codegen` step 1 |
| `Package_entity` | 实体目录命名 | `codegen` |
| `Template_Path` | 模板克隆/读取根 | `codegen` step 3 |
| `Repository_Path` | ZIP 产物输出目录 | `codegen` step 7 |
| `GitHome_Default` | 默认系统仓库路径 | 系统初始化 |
| `SandBox_Path` | 沙箱环境目录 | 规划中 |

#### Scenario: missing required key
- **WHEN** `Workspace` / `Template_Path` / `Repository_Path` / `DefaultDatabase` is missing from `db/data.sql`
- **THEN** `codegen` and `sql-parse` will fail to locate paths — these rows are mandatory at rebuild time

### Requirement: 项目仓库账户 CRUD
The system SHALL allow creating, reading, and updating a project's Git/SVN repository account via `ProjectRepositoryAccountController`.

#### Scenario: create repository account
- **WHEN** `POST /project/repository/build` receives a `ProjectRepositoryAccount`
- **THEN** the system persists it (with `code`, `projectCode`, `account`, `password`, `home`, `description`, `state`, `type`) and returns `R`

#### Scenario: modify repository account
- **WHEN** `POST /project/repository/modify` receives a `ProjectRepositoryAccount`
- **THEN** the system loads the existing record by code, applies updates via `updateById`, and returns `R`

#### Scenario: load repository account
- **WHEN** `GET /project/repository/load?code=<code>` is called
- **THEN** the system returns the matching record

#### Scenario: list repository accounts
- **WHEN** `GET /project/repository/list` is called (paged)
- **THEN** the system returns the paged result

### Requirement: 类型与构建路由
The system SHALL drive `codegen` Git logic from `project_repository_account.type` and `home`.

#### Scenario: GIT with .git home
- **WHEN** `type = GIT` and `home` ends with `.git`
- **THEN** `codegen` step 1 SHALL `cloneGit(home, projectPath, account, password)` and step 6 SHALL `commitAndPush(projectPath, account, password, ...)`

#### Scenario: SVN home
- **WHEN** `type = SVN`
- **THEN** the system SHALL skip the Git step (SVN is not implemented; do not silently fake success)

#### Scenario: no repository account
- **WHEN** the project has no active repository account
- **THEN** the system SHALL skip step 1's `cloneGit` and step 6 entirely, only producing the workspace + ZIP

## Notes

- **`setting` 表字段**：`id`(PK), `k`, `v`, `description`。枚举 `SettingKey`（**系统约定键**）与上表一致；枚举 `SettingState` 为空（占位）。**没有独立的 `Setting` 控制器**对外 CRUD —— 由 `db/data.sql` 预置、`SettingMapper` 读取；`settingService.load()` 供项目下载等流程使用。
- **`project_repository_account` 表字段**：`id`(PK), `code`(PK), `projectCode`, `account`, `password`（**当前明文**）, `home`, `description`, `state`, `type`。
- **枚举**：`ProjectRepositoryAccountState`（Enable/Disenable/Delete）、`ProjectRepositoryTypeEnum`（`GIT("GIT")`, `SVN("SVN")`，`getEnum(String)`）。
- **Deprecated 端点**：`/project/repository/load/code/{code}`、`/project/repository/list`。
- **其它项目从属配置（@Deprecated）**：
  - 模块池 `module` / `module_file` —— 实体 `Module`(`id, code, name, description`)、`ModuleFile`(`id, moudleCode, path`)；`ProjectModuleController`（`/project/mouldles`）的 `load` / `add` / `list`；关联表 `project_module`(`projectCode, moduleCode`)；枚举 `ModuleEnum`：`po, vo, dao, ctrl, facade, service`（生成分层语义）。
  - UI 模型树 `project_model` / `project_model_class` —— `ProjectModel`(`id, code, preCode, name, route, css, isMenu, ico`)、`ProjectModelClass`(`id, mapClassTableCode, projectModelCode`)；`/projectModel`、`/projectModelClass` 均 @Deprecated。
  - 产物目录 `project_code_catalog` —— `/projectCodeCatalog`（build/list/modify/delete），记录生成文件清单。
  - > `GeneratorSVImpl` 中「获取模块信息」标 `TODO`，业务模块编排属规划能力。
- **下游**：`sql-parse`（`DefaultDatabase`）、`codegen`（`Workspace` / `Template_Path` / `Repository_Path` / `Gradle_Directory_Structure` / `Package_entity` / `SandBox_Path`）和 `codegen`（`project_repository_account`）消费本能力。
- **安全红线**：`project_repository_account.password` 与 `frameworks.password` 当前**明文入库**；生产应迁移到加密存储（如 Jasypt），见 `code-style.md` §13。
