# frameworks-template

## Purpose

维护「技术框架池」（指向外部 Git 模板仓库的技术栈定义）、为项目选择框架，并定义可插拔的模板引擎抽象（Freemarker / Beetl），运行期由模板内 `aicode.json` 声明所选引擎。技术栈以模板仓库形式与生成器解耦，新增/升级技术栈只改模板、不改生成器。

## Requirements

### Requirement: 框架池分页查询
The system SHALL return a paginated list of frameworks via `GET /framework/list`.

#### Scenario: list frameworks
- **WHEN** `GET /framework/list?curPage=N&pageSize=M` is called
- **THEN** the system returns a paged result whose elements contain `code`, `name`, `description`, `gitHome`, `account`, `password`, `isPublic`

### Requirement: 框架池 CRUD
The system SHALL expose create / read / update / delete / toggle-public endpoints for the framework pool.

#### Scenario: create framework
- **WHEN** `POST /framework/build` receives a `Frameworks` payload
- **THEN** the system generates a `code` and persists `gitHome`, `account`, `password`, `isPublic` etc.

#### Scenario: load framework
- **WHEN** `GET /framework/load?code=<code>` is called
- **THEN** the system returns the framework details

#### Scenario: modify framework
- **WHEN** `PUT/POST /framework/modify` receives a `Frameworks` payload
- **THEN** the system updates and returns `R`

#### Scenario: update isPublic
- **WHEN** `PUT/POST /framework/updateIsPublic` receives a `Frameworks` payload
- **THEN** the system toggles the public flag and returns `R`

#### Scenario: delete framework
- **WHEN** `POST /framework/delete` receives a `FrameworksVO`
- **THEN** the system removes the row and returns `R`

### Requirement: 项目选用框架
The system SHALL allow setting one or more frameworks for a project via `POST /project/framwork/add`.

#### Scenario: set project frameworks
- **WHEN** `POST /project/framwork/add` is called with `projectStr` (the agreed JSON/serialized list of `{projectCode, frameworkCode}` pairs)
- **THEN** the system removes existing `project_framwork` rows for the project and batch-inserts the new ones, returning `R`

#### Scenario: load project framework
- **WHEN** `GET /project/framwork/load?projectCode=<p>&frameworkCode=<f>` is called
- **THEN** the system returns the project-framework record

#### Scenario: list project frameworks
- **WHEN** `GET /project/framwork/list?curPage=N&pageSize=M` is called
- **THEN** the system returns the project's framework bindings, paged

### Requirement: 模板引擎策略接口
The system SHALL expose a pluggable template engine interface `TemplateHelper` with `generate(TemplateData data, String targetFilePath, String templatePath)`, and SHALL register `FreemarkerHelper` and `BeetlHelper` as `@Service` implementations.

#### Scenario: route by aicode.json
- **WHEN** the generator (`codegen`) processes a template repo
- **THEN** it SHALL read `aicode.json` at the repo root (filename compatibility: `aicode.json`, `aicode`, `ai-code.json`, `ai-code`) and pick the engine whose `engine` field matches `TemplateEngineEnum.getTemplate(name)` (case-insensitive)

#### Scenario: default engine
- **WHEN** `aicode.json` is missing or its `engine` value cannot be recognized
- **THEN** the generator SHALL fall back to `Freemarker`

#### Scenario: add a new engine
- **WHEN** a new `TemplateHelper` implementation is registered as `@Service`
- **THEN** the generator SHALL be able to route to it without changes to the orchestration code

## Notes

- **`frameworks` 表字段**：`id`, `code`(PK), `name`, `description`, `gitHome`, `account`, `password`, `isPublic`。
- **`frameworks_template`（运行期临时表）**：`id`, `code`, `frameworkCode`, `path`。`codegen` 在每次构建里把克隆到的模板文件登记进来，构建结束后清空。
- **`project_framwork`**：`id`, `frameworkCode`, `projectCode`。
- **枚举 `TemplateEngineEnum`**：`Freemarker`, `Beetl`；`getTemplate(String)` 不区分大小写。
- **仓库凭据语义**：`frameworks.account/password` 是**模板仓库**的私有访问凭据；项目的产物仓库凭据在 `settings-repository`（`project_repository_account`）里定义，两者不要混用。
- **模板仓库目录约定**：按 `frameworks.name` 分目录；`codegen` 会按所选 `name` 裁剪无关目录。`README.md` 与 `.git` 在登记时跳过。
- **占位符**（由 `codegen` 的 `generator()` 替换，模板需按此命名）：`${basepackage}` / `${basePackage}` / `${package}` / `${Package}`、`${className}`、`${classNameLower}`（`StringHelper.toJavaVariableName`）、`${dashedCaseName}`（`StringTools.humpToLine`）、`${module}`（`englishName`）、`${model}`（`TemplateData.model`），Beetl 同义 `$xxx$`；状态枚举 `$classNameState$`。
- **后缀**：`.ftl`（Freemarker）/ `.btl`（Beetl），生成时自动去除。
- **`TemplateData` 可用变量**（模板编写契约，**新增变量须先在 `TemplateData` 加字段**）：`projectName`, `basePackage`, `module`, `model`, `table`, `clazz`, `tableName`, `className`, `classNameLower`, `dashedCaseName`, `notes`, `copyright`, `author`, `classes`, `columns`, `pkColumns`, `notPkColumns`, `fields`, `pkFields`, `notPkFields`, `tableFields`, `modelClasses`, `modelDatas`, `oneToOneList`, `oneToManyList`, `displayAttributes`, `states`, `classNameState`, `isRelation`, `mainField`, `joinField`。
- **Deprecated 端点**：`/framework/load/code/{code}`、`/project/framwork/build`、`PUT /project/framwork/modify`、`DELETE /project/framwork/delete`。
- **下游**：`codegen` 在第三步按本能力的 `frameworks_template` 登记结果执行渲染。
- **安全**：私有仓库的 `account/password` 当前明文入库（`code-style.md` §13）。
