# codegen

## Purpose

执行异步代码生成：把项目的映射数据 × 所选框架模板渲染成可运行项目代码，检出/克隆目标 Git 仓库、自动 commit & push、打包 ZIP。构建过程通过 WebSocket 实时回显进度并落盘可回看。这是系统的核心价值，编排于 `GeneratorSVImpl.aiCode()` 七步。

## Requirements

### Requirement: 触发异步构建
The system SHALL accept `GET /project/job/execute?code=<projectCode>` and return immediately, with the actual generation work dispatched to a Java 21 virtual thread (`@Async`).

#### Scenario: submit and return
- **WHEN** `GET /project/job/execute?code=<projectCode>` is called
- **THEN** the system creates a `ProjectJob` (`state = Executing`, `code = uidGenerator.getUID()`, `number = current`), dispatches the generator asynchronously, and returns `R` whose `data` contains the new `ProjectJob`

### Requirement: 七步编排（aiCode）
The system SHALL run, on the virtual thread, the seven-step pipeline: build workspace → load model → prepare templates → generate sources → emit SQL → git commit & push → zip deliver.

#### Scenario: step 1 — build workspace
- **WHEN** step 1 starts
- **THEN** the system reads `Setting(Workspace)`, computes `projectPath = Workspace/<englishName>`, deletes the path if it exists, optionally `cloneGit` from the project's `project_repository_account.home` (only when `type = GIT` and `home` ends with `.git`), and creates one subdirectory per selected framework when more than one is selected; `project.buildNumber++`

#### Scenario: step 2 — load model
- **WHEN** step 2 runs
- **THEN** the system loads `project_map` for the project, then for each entry loads its `MapClassTable` with `MapFieldColumn` and `MapRelationship` populated

#### Scenario: step 3 — prepare templates
- **WHEN** step 3 runs
- **THEN** the system reads `Setting(Template_Path)`, `cloneGit` each selected framework's `gitHome` to the template path (with `account/password` only when `isPublic = N`), deletes directories that don't match the selected `frameworks.name`s, walks the template tree (skipping `.git` and `README.md`), and registers each file into `frameworks_template` (code = UID, frameworkCode, path); it then resolves the engine from the repo's `aicode.json` via `adapterTemplateEngine`

#### Scenario: step 4 — generate sources
- **WHEN** step 4 runs
- **THEN** the system iterates `framework × template file × MapClassTable`:
- split columns into `pkColumns` / `notPkColumns` / `tableColumns` (excluding `updateTime`, `summary`, `marker`, `vn` and similar bookkeeping fields)
- build `oneToOneList` and `oneToManyList` from `MapRelationship` per the `isOneToOne` / `isOneToMany` flags
- group classes by `model` and assemble `modelClasses` / `modelDatas` (ModelData)
- construct a `TemplateData` and substitute target path placeholders (Freemarker `${...}` and Beetl `$...$` styles, including `${basepackage}/${basePackage}/${package}/${Package}` → `basePackage` with `.` → `/`, `${className}`/`${classNameLower}`/`${dashedCaseName}`, `${module}` = `englishName`, `${model}` = `TemplateData.model`, `$classNameState$` for status enums; strip `.ftl`/`.btl`; prepend `projectPath/<frameworks.name>/` for multi-framework projects, otherwise `projectPath/`)
- honor `project.isIncrement = N` (full generation) — when full mode, render every template; when not full, skip existing outputs
- dispatch rendering: `.jar` / `gradlew` etc. via `copyFileToDirectory`; templates containing `$classNameState$` get expanded per status field; ordinary templates go to `freemarkerHelper.generate(...)` or `beetlHelper.generate(...)` based on the engine resolved in step 3

#### Scenario: step 5 — emit SQL
- **WHEN** step 5 runs
- **THEN** the system writes `projectPath/<englishName>.sql` containing the project SQL plus the `WORKER_NODE` DDL

#### Scenario: step 6 — git commit and push
- **WHEN** the project has an active Git `project_repository_account`
- **THEN** the system calls `GitTools.commitAndPush(projectPath, account, password, "AI-Code 为您构建代码…")`
- **AND WHEN** the type is `SVN` the system skips this step (SVN is not implemented; do not silently fake success)
- **AND WHEN** no repository account is configured, the system skips this step entirely

#### Scenario: step 7 — zip and deliver
- **WHEN** step 7 runs
- **THEN** the system reads `Setting(Repository_Path)`, calls `ZipTools.zip` to write `Repository_Path/<englishName>.zip`, sets `project.downloadUrl = /project/download/<englishName>`, and finalizes the `ProjectJob`

### Requirement: 异常内部消化
The system SHALL wrap the seven-step pipeline in a top-level `try/catch` inside the `@Async` method, set `ProjectJob.state = Error` on any thrown exception, and SHALL NOT let the exception escape to the global `ExceptionHandle` (the request thread is long gone and the global handler is request-scoped).

#### Scenario: failure
- **WHEN** any step throws
- **THEN** the system logs the exception, sets `ProjectJob.state = Error`, pushes `Finished: ERROR` to the WebSocket, and returns normally from the virtual thread

### Requirement: 实时与落盘日志双写
The system SHALL, for every step, push progress via `WSClientManager.sendMessage(msg)` AND persist via `logsSV.saveLogs(path)` (the two calls are paired).

#### Scenario: live log
- **WHEN** a WebSocket client is connected to `/websocket.shtml`
- **THEN** the system pushes per-step progress messages to that client

#### Scenario: historical log
- **WHEN** `GET /logs/load?projectCode=<code>&datetime=<datetime>` is called
- **THEN** the system returns the on-disk log content for that build

### Requirement: 构建结束清理
The system SHALL, on completion or failure, clear the `frameworks_template` table and delete the cloned template directory.

#### Scenario: cleanup
- **WHEN** `aiCode()` reaches its terminal state
- **THEN** the system empties `frameworks_template` and deletes `Template_Path/<framework-name>` clones (so the next build re-clones fresh templates)

## Notes

- **入口控制器**：`ProjectJobController`（`@RequestMapping("/project/job")`），端点 `GET /execute`。
- **日志控制器**：`LogsCtrl`（`@RequestMapping("/logs")`），端点 `GET /load`。
- **WebSocket 端点**：`/websocket.shtml`（Spring `WebSocketConfig` + `WebsocketSpringCofigurator`），由 `WSClientManager`（`ConcurrentHashMap<String, Session>` 静态持有）管理。
- **数据表**：
  - `project_job`：`id`, `projectCode`, `code`(PK), `number`, `state`, `createTime`。枚举 `ProjectJobState`：`Create`, `Executing`, `Completed`, `Error`, `Waring`。
  - `project_job_logs`：`id`, `code`, `log`（单行日志）。
  - `project_code_catalog`（deprecated 控制器）：记录生成文件清单。
- **运行期模型**：
  - `TemplateData`（aicode/config/template）—— 模板渲染唯一数据源，变量清单见 `frameworks-template` Notes。
  - `ModelData`（`model:String` + `classes:List<MapClassTable>`）—— 按模块分组的类集合。
  - `MapStatus`（`statusName`, `name`, `value`, `targetFilePath`, `notes`, `mapStatusList`）—— 状态枚举类生成。
- **关键组件**：
  - `GeneratorSVImpl`（aicode/project/service）—— 七步编排。
  - `ProjectJobServiceImpl` —— 建任务 + `@Async` 触发。
  - `TaskExecutorConfig` —— 虚拟线程 `@Async` 执行器。
  - `TemplateHelper` + `FreemarkerHelper` / `BeetlHelper` —— 渲染策略。
  - `WSClientManager` / `WebSocketServer` —— 实时日志。
  - `LogsSVImpl` —— 日志落盘与回看。
  - `GitTools`（common）—— jgit 封装（`cloneGit`, `commitAndPush`）。
  - `ZipTools`（common）—— zip4j 封装。
- **Deprecated 端点**：`/project/job/load`、`/project/job/build`、`/project/job/list`、`/project/job/modify`、`/project/job/delete`、`/projectCodeCatalog/*`。
- **TODO**（已存在但未实现）：业务模块信息获取（`GeneratorSVImpl` 第 5 步标 `TODO`）、SVN 推送（`buildProject` 中留空）。
- **依赖**：输入数据全部来自 `mapping-display`（映射 + 显示属性）、`frameworks-template`（框架 + 模板 + 引擎）、`settings-repository`（`Setting` 路径 + 仓库账户）。`auth-account` 仅为调用时的 token 约定，本能力不直接校验。
- **安全**：不要在日志或 WebSocket 推送中泄露 Git/SVN 凭据；构建期间不要在事务中执行 git clone/文件 IO。
