# 功能规格：代码生成主链路（spec-codegen）

> 通用约定见 [spec-overview.md §4](spec-overview.md)。本域是系统的核心价值——把映射 + 模板渲染成可运行项目代码，对应主流程第 9、10 步。架构叙述见 [architecture.md §3](../standards/architecture.md)。

---

## 1. 功能目标

- 触发一次构建任务（异步），把项目的映射数据 × 所选框架模板生成全套代码。
- 自动检出/克隆模板与目标 Git 仓库、提交推送产物、打包 ZIP。
- 构建过程通过 WebSocket 实时回显日志，并落盘可回看。

---

## 2. 数据模型

### 表 `project_job`（构建任务）

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | bigint PK | 主键 |
| `projectCode` | varchar(64) | 项目编码 |
| `code` | varchar(64) PK | 任务编码 |
| `number` | varchar(64) | 第几次执行 |
| `state` | varchar(16) | Create/Executing/Completed/Error/Waring |
| `createTime` | datetime | 执行时间 |

枚举 `ProjectJobState`：`Create("创建")`、`Executing("执行中")`、`Completed("完成")`、`Error("失败")`、`Waring("警告")`。

### 表 `project_job_logs`（构建日志）

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | bigint PK | 主键 |
| `code` | varchar(64) | 任务/项目编码 |
| `log` | varchar(1024) | 单行日志 |

### 表 `project_code_catalog`（产物文件目录，@Deprecated 控制器）

`id, code, projectCode, basePackage, fileName, fileSuffix, relativePath, absolutePath` —— 记录生成文件清单。

### 表 `worker_node`（UID 注册表，生成产物 SQL 也会追加此表 DDL）

见 [spec-overview.md §4.5/4.7](spec-overview.md)。

### 运行期内存模型

- `TemplateData`：模板渲染唯一数据源（变量见 [spec-frameworks-template.md §4.3](spec-frameworks-template.md)）。
- `ModelData`：`model:String` + `classes:List<MapClassTable>`，按模块分组类。
- `MapStatus`：状态枚举生成用（`statusName`、`name`、`value`、`targetFilePath`、`notes`、`mapStatusList`）。

---

## 3. API 端点

### 3.1 执行生成 —— `GET /project/job/execute` 【核心入口】

- 控制器：`ProjectJobController`（`@RequestMapping("/project/job")`）。
- 入参：`@RequestParam("code") String projectCode`（即 `?code=<projectCode>`）。
- 规则：`projectJobService.execute(projectCode)` —— 创建 `ProjectJob`（state=Executing，code=UID，number=本次序号），随后**异步**触发生成；立即返回任务对象（非阻塞）。
- 返回：`R`，`data` 含 `ProjectJob`（含任务 `code`）。

### 3.2 查看构建日志 —— `GET /logs/load`

- 控制器：`LogsCtrl`（`@RequestMapping("/logs")`）。
- 入参：`projectCode`、`datetime`。
- 规则：`logsSV.scanPath()` 读取该次构建落盘的日志文件内容。
- 返回：`R`，`data` 为日志文本/行集合。

### 3.3 实时日志（WebSocket）

- 端点 `/websocket.shtml`；构建过程经 `WSClientManager.sendMessage(msg)` 推送，前端实时显示（Jenkins 式）。

### 3.4 已废弃端点（`/project/job/*`）

`load`、`build`、`list`、`modify`、`delete` 均 @Deprecated。

---

## 4. 核心流程：`GeneratorSVImpl.aiCode(projectCode, projectJob)`

运行在 `@Async` 虚拟线程（`TaskExecutorConfig` 用 `Thread.startVirtualThread`）。全程 `try/catch`，失败置 `ProjectJob.state=Error`，成功置 `Completed`；异常不逃逸线程。每步双写：`WSClientManager.sendMessage` + `logsSV.saveLogs(path)`。

先 `logsSV.createLogFiles(projectCode, createTime)` 建日志文件。然后七步：

### 4.1 创建工作空间（`buildProject`）
1. 读 `Setting(Workspace)` 得工作目录，`projectPath = Workspace/<englishName>`。
2. 若目录已存在 → 删除重建。
3. 若项目配置了 Git 仓库账户（`project_repository_account`，type=GIT，home 以 `.git` 结尾）→ `GitTools.cloneGit(home, projectPath, account, password)` 检出。
4. 若选了多个框架 → 为每个 `frameworks.name` 在 projectPath 下建子目录。
5. `project.buildNumber++`。

### 4.2 转换模型
- 按 `projectCode` 查 `project_map` → 每个 `MapClassTable`，装配其 `map_field_column`（字段）与 `map_relationship`（关联），得 `mapClassTableList`。

### 4.3 下载/登记模板（`prepareframeworksTemplateList`）
1. 读 `Setting(Template_Path)` 得模板根目录。
2. 对项目每个框架：按 `isPublic` 选择 `GitTools.cloneGit(gitHome,...)`（公有）或带账号密码（私有）克隆到模板目录。
3. 删除模板仓库中与所选框架无关的目录（按 `frameworks.name` 关键字过滤）。
4. 遍历模板文件（跳过 `.git`、`README.md`），逐个登记到 `frameworks_template`（code=UID, frameworkCode, path）。
5. 探测模板引擎：遇 `aicode.json` 时 `adapterTemplateEngine` 解析 `engine`；最终返回 `TemplateEngineEnum`（缺省 Freemarker）。

### 4.4 生成源码（`generator`，三重循环：框架 × 模板文件 × 映射类）
对每个 (框架, 模板文件, mapClassTable)：
1. 拆分字段：主键 `pkColumns` / 非主键 `notPkColumns` / 表格字段 `tableColumns`（排除 updateTime/summary/marker/vn 等）。
2. 装配关联：遍历 `map_relationship`，按 `isOneToOne`/`isOneToMany` 生成 `oneToOneList`/`oneToManyList`（各为 `TemplateData`）。
3. 按模块分组：构造 `models`、`modelDatas`(ModelData)、`modelClasses`。
4. 构造 `TemplateData`（含上述 + `displayAttributes`）。
5. **解析目标路径**：把模板相对路径中的占位符替换为实际值：
   - `${basepackage}/${basePackage}/${package}/${Package}` → `basePackage`（`.`换`/`）
   - `${className}`→类名、`${classNameLower}`→`StringHelper.toJavaVariableName`、`${dashedCaseName}`→`StringTools.humpToLine`
   - `${module}`→`englishName`、`${model}`→`templateData.model`
   - Beetl 同义 `$xxx$`；去 `.ftl`/`.btl` 后缀
   - 多框架时目标前缀 `projectPath/<frameworks.name>/`，单框架时 `projectPath/`
6. **增量判断**：`project.isIncrement == N`（全量）时才生成；模板文件存在才处理。
7. 渲染：
   - `.jar`/`gradlew` 等二进制 → 直接 `copyFileToDirectory`。
   - 含 `$classNameState$` 的模板 → 对每个状态字段（`genStatus`）派生多份（枚举类生成）。
   - 普通模板 → 按引擎调 `freemarkerHelper.generate(...)` 或 `beetlHelper.generate(...)`。

### 4.5 生成附加 SQL（`generateTsql`）
- 把项目 SQL + 追加 `worker_node` 建表 DDL 写到 `projectPath/<englishName>.sql`。

### 4.6 清理临时模板
- `cleanTemplates`：清空 `frameworks_template` 表，删除克隆下来的模板目录。

### 4.7 版本控制提交
- 若配置仓库账户：`GitTools.commitAndPush(projectPath, account, password, "AI-Code 为您构建代码…")` 提交并推送。

### 4.8 打包交付（`zipProject`）
- 读 `Setting(Repository_Path)`，`ZipTools.zip` 打包到 `Repository_Path/<englishName>.zip`；回填 `project.downloadUrl = /project/download/<englishName>`。

### 4.9 收尾
- 成功：`ProjectJob.state=Completed`，推送 `Finished: SUCCESS`；失败：`Error` + `Finished: ERROR`。

---

## 5. 关键实现组件（重建必需）

| 组件 | 位置 | 作用 |
|------|------|------|
| `GeneratorSVImpl` | aicode/project/service | 生成主编排（实现 `GenerateSV`） |
| `ProjectJobServiceImpl` | aicode/project/service | 建任务 + `@Async` 触发 |
| `TaskExecutorConfig` | aicode/config | 虚拟线程 `@Async` 执行器 |
| `TemplateData` | aicode/config/template | 模板数据模型 |
| `TemplateHelper`+Freemarker/Beetl | aicode/config/template | 渲染策略 |
| `WSClientManager`/`WebSocketServer` | aicode/config/websocket | 实时日志推送 |
| `LogsSVImpl` | aicode/project/service | 日志落盘/读取 |
| `GitTools` | common/core/tools | clone/commit/push（jgit） |
| `ZipTools` | common/core/tools | 打包 ZIP（zip4j） |

---

## 6. 验收标准

- [ ] `execute` 立即返回任务（state=Executing），不阻塞 HTTP。
- [ ] 构建过程中 WebSocket 收到逐步日志，且 `logs/load` 能回看。
- [ ] 产物目录生成全套代码 + `<englishName>.sql`（含 worker_node）。
- [ ] 多框架时各框架生成到独立子目录。
- [ ] 配置 Git 仓库时产物被 commit & push；ZIP 生成且 `downloadUrl` 回填。
- [ ] 构建结束 `frameworks_template` 被清空、临时模板目录被删除。
- [ ] 失败时 `ProjectJob.state=Error`，异常不导致线程崩溃。

---

## 7. 关联规格

- 输入数据来源：映射 [spec-mapping-display.md](spec-mapping-display.md)、框架/模板 [spec-frameworks-template.md](spec-frameworks-template.md)
- 路径/凭据来源：[spec-settings-repository.md](spec-settings-repository.md)
