# code-gen-pipeline Specification

## Purpose
TBD - created by archiving change add-core-business-tests. Update Purpose after archive.
## Requirements
### Requirement: GeneratorSVImpl 路径占位符替换遵循 Freemarker 与 Beetl 双风格

The system SHALL replace path placeholders in template output paths using both Freemarker `${...}` and Beetl `$...$` styles, and SHALL strip `.ftl`/`.btl` suffixes.

#### Scenario: Freemarker 占位符全部被替换
- **WHEN** the template path contains `${basepackage}/${className}/${module}/${model}` and the project has `basePackage=com.demo` and `className=User` and `englishName=demo`
- **THEN** the target path uses `com/demo/User/demo/...` with the suffix removed

#### Scenario: Beetl 占位符 `$xxx$` 同样被替换
- **WHEN** the template path contains `$basepackage$/$className$/$module$` and the same project context
- **THEN** the target path uses `com/demo/User/demo/...` with `.btl` suffix removed

#### Scenario: 未匹配占位符保留原样
- **WHEN** the template path contains an unknown placeholder like `${unknownToken}`
- **THEN** the placeholder remains in the target path (forward-compat: templates that introduce new placeholders do not silently break)

### Requirement: GeneratorSVImpl 全量与增量生成模式互斥
The system SHALL honor `project.isIncrement`: full mode (N) regenerates all templates; incremental mode (Y) skips targets that already exist on disk.

#### Scenario: 增量模式下目标文件已存在 → 跳过
- **WHEN** `isIncrement=Y` and the computed target file path already exists in the workspace
- **THEN** the generator MUST NOT overwrite it and MUST NOT call the template engine

#### Scenario: 增量模式下目标文件不存在 → 仍渲染
- **WHEN** `isIncrement=Y` and the computed target file path does NOT exist in the workspace
- **THEN** the generator SHALL render the template into the target path

#### Scenario: 全量模式下无论目标是否存在都渲染
- **WHEN** `isIncrement=N`
- **THEN** the generator SHALL always render the template (existing files are overwritten)

### Requirement: GeneratorSVImpl 状态枚举类衍生（$classNameState$）
The system SHALL, when a template path contains `$classNameState$`, expand the template once per non-primary-key field whose `isState=Y`, generating one file per status field.

#### Scenario: 单状态字段衍生一个文件
- **WHEN** the template path contains `$classNameState$` and `MapClassTable` has exactly one field with `isState=Y`
- **THEN** the generator produces exactly one output file whose name is `<ClassName><StateFieldUpper>` (e.g. `UserStatus`)

#### Scenario: 无状态字段不衍生
- **WHEN** the template path contains `$classNameState$` and no field has `isState=Y`
- **THEN** the generator produces NO output file (skips the expansion)

### Requirement: TemplateEngineAdapter 引擎探测符合 4 种兼容文件名
The system SHALL detect the template engine by reading `aicode.json` (or compatible file names) at the template repo root, and SHALL fall back to Freemarker when the file is missing or its `engine` field is unrecognizable.

#### Scenario: aicode.json 声明 Freemarker
- **WHEN** the template repo root contains `aicode.json` with `{"engine":"Freemarker"}`
- **THEN** `TemplateEngineAdapter.detect` returns `TemplateEngineEnum.Freemarker`

#### Scenario: ai-code.json（兼容拼写）也被识别
- **WHEN** the template repo root contains `ai-code.json` with `{"engine":"Beetl"}`
- **THEN** `TemplateEngineAdapter.detect` returns `TemplateEngineEnum.Beetl`

#### Scenario: 元数据缺失返回 null（让调用方回退）
- **WHEN** the file does NOT exist
- **THEN** `TemplateEngineAdapter.detect` returns `null`

#### Scenario: 未知引擎字段值返回 null
- **WHEN** the file declares `{"engine":"Velocity"}` (unsupported)
- **THEN** `TemplateEngineAdapter.detect` returns `null`

### Requirement: SqlEmitter 合并 SQL 脚本含 worker_node DDL
The system SHALL emit a SQL file at `<projectPath>/<englishName>.sql` containing the user's original DDL plus the UidGenerator `worker_node` table DDL, and SHALL return the worker_node DDL snippet for logging.

#### Scenario: 完整 emit 返回 worker_node DDL
- **WHEN** `SqlEmitter.emit(projectPath, englishName, projectCode)` is called with a stored `ProjectSql.tsql`
- **THEN** the file contains the user DDL followed by the worker_node DDL, and the returned string equals the worker_node DDL snippet

#### Scenario: ProjectSql 缺失时只写 worker_node
- **WHEN** no `ProjectSql` row exists for the projectCode
- **THEN** the file contains only the header comment + worker_node DDL, and the method MUST NOT throw

### Requirement: ZipPackager 写出 ZIP 到 Repository_Path 并回填 downloadUrl
The system SHALL pack `<Setting.Workspace>/<englishName>` into `<Setting.Repository_Path>/<englishName>.zip` and set `project.downloadUrl = /project/download/<englishName>`.

#### Scenario: 完整 pack 路径正确
- **WHEN** `ZipPackager.pack(project)` is called with a project whose `englishName=demo`
- **THEN** the ZIP is written to `Repository_Path/demo.zip` and `project.downloadUrl` equals `/project/download/demo`

#### Scenario: Repository_Path 目录不存在时自动创建
- **WHEN** `Setting.Repository_Path` directory does NOT exist
- **THEN** `ZipPackager.pack` MUST create the directory before writing the ZIP

#### Scenario: 缺失必要 Setting 抛 IllegalStateException
- **WHEN** `Setting.Workspace` or `Setting.Repository_Path` row is missing
- **THEN** `ZipPackager.pack` MUST throw `IllegalStateException` (fail fast)

### Requirement: @Async 任务的虚拟线程执行器装配 MdcTaskDecorator
The system SHALL configure `SimpleAsyncTaskExecutor` as the @Async executor, MUST enable virtual threads via `setVirtualThreads(true)`, and MUST attach `MdcTaskDecorator` so that `traceId` in MDC is propagated across the virtual thread boundary when @Async methods run on a separate thread.

#### Scenario: 异步任务跨虚拟线程保留 traceId
- **WHEN** HTTP request enters with `traceId: abc123` and triggers `@Async` method (e.g. `ProjectJobExecutor.execute`)
- **THEN** the @Async method runs on a virtual thread
- **AND** the virtual thread's MDC contains `traceId=abc123`
- **AND** log lines emitted inside the @Async method include `tid: abc123` (not `tid: null`)

#### Scenario: 异步任务无父 MDC 时生成新 traceId
- **WHEN** the @Async method is triggered without a parent MDC (e.g. scheduled task, manual call)
- **THEN** `MdcTaskDecorator` generates a random UUID for `traceId`
- **AND** the virtual thread's MDC contains the new UUID

### Requirement: 使用 Spring 6.1+ setVirtualThreads API 而非手动 Thread.startVirtualThread
The system SHALL use `SimpleAsyncTaskExecutor.setVirtualThreads(true)` (Spring 6.1+ standard API) instead of manually wrapping `Thread.startVirtualThread(task)` in an anonymous `execute` override.

#### Scenario: 替代手动包装
- **WHEN** an @Async method is submitted to the executor
- **THEN** Spring 6.1's `SimpleAsyncTaskExecutor` runs the task on a virtual thread (via its built-in `VirtualThreadDelegate`)
- **AND** the thread name has the configured prefix (e.g. `aicode-`)
- **AND** no anonymous `Thread.startVirtualThread(task)` override is present

### Requirement: 暴露 jvm.threads.* 指标
The system SHALL expose `jvm.threads.*` metrics via Spring Boot Actuator by setting `management.metrics.enable.threads: true`, so that operators can verify virtual threads are in use.

#### Scenario: actuator 暴露虚拟线程数
- **WHEN** the application starts and actuator metrics endpoint is exposed
- **THEN** `GET /actuator/metrics/jvm.threads.live` returns the current live thread count
- **AND** the value reflects both platform and virtual threads

