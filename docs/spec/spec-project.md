# 功能规格：项目管理（spec-project）

> 通用约定见 [spec-overview.md §4](spec-overview.md)。项目是整个系统的聚合根，其它功能域均挂在项目下。

---

## 1. 功能目标

- 创建一个代码生成「项目」，承载 SQL、映射、框架、模块、仓库、构建任务等全部配置。
- 查询项目列表/详情（详情为跨域聚合）。
- 修改、删除项目（删除需级联清理所有关联数据与产物文件）。
- 扫描项目产物文件树、下载产物。

---

## 2. 数据模型

### 表 `project`

| 列 | 类型 | 默认 | 说明 |
|----|------|------|------|
| `id` | bigint | PK auto_inc | 主键（UID） |
| `code` | varchar(64) | PK | 项目编码（= id 字符串） |
| `name` | varchar(256) | | 项目名 |
| `description` | varchar(512) | | 项目描述 |
| `englishName` | varchar(256) | | 项目英文名（**同时作为数据库 schema 名**，唯一） |
| `databaseType` | varchar(16) | | 数据库类型：Mysql,Oracle |
| `language` | varchar(32) | | 语言：Java,Python,Js |
| `state` | varchar(16) | | 项目状态：Enable/Disenable/Delete |
| `copyright` | varchar(512) | | 版权文字 |
| `author` | varchar(32) | | 作者 |
| `phone` | varchar(16) | | 联系方式 |
| `basePackage` | varchar(256) | | 基础包名 |
| `sqlFile` | varchar(256) | | 脚本文件地址 |
| `downloadUrl` | varchar(256) | | 下载地址 |
| `buildNumber` | int | | 生成次数 |
| `isRepository` | varchar(1) | 'N' | 是否仓库管理 |
| `isParseTable` | varchar(1) | 'N' | 是否已解析表 |
| `isParseClass` | varchar(1) | 'N' | 是否已解析类 |
| `createTime` | datetime | | 创建时间 |
| `updateTime` | datetime | | 更新时间 |
| `accountCode` | varchar(64) | | 所属账户编码 |
| `isIncrement` | varchar(16) | | 是否增量生成（Y/N） |

主键 `(id, code)`。

### 实体 `Project` 非表字段（`@TableField(exist=false)`）

`projectFramworkList`、`projectMapList`、`projectJobList`、`projectModuleList`、`projectRepositoryAccountList`、`projectSqlList`、`relationshipAndDisplay`（详情聚合用）。

### 枚举 `ProjectState`：`Enable("启用")`、`Disenable("停用")`、`Delete("删除")`。

---

## 3. API 端点（控制器 `ProjectController`，`@RequestMapping("/project")`）

### 3.1 创建项目 —— `POST /project/build` 【核心】

- 入参：`Project`（表单绑定 `@Parameter(hidden=true)`）+ `token`。
- 业务规则（`ProjectServiceImpl.save`）：
  1. 必填校验：`name`、`englishName`、`phone`、`author`、`copyright`、`databaseType`、`description`、`language`、`basePackage` 任一为空 → 抛 `Empty_Param`。
  2. 唯一校验：`englishName` 已存在 → 抛 `Exists`。
  3. 默认值：去除 `basePackage` 末尾 `.`；`id = uidGenerator.getUID()`；`code = String(id)`；`state = Enable`；`isIncrement = N`；`downloadUrl = "DownloadUrl"`；`createTime/updateTime = now`。
  4. 保存。
- 返回：`R`，`data` 含项目（含 `code`，供后续步骤使用）。

### 3.2 初始化（建库+解析）—— `POST /project/init` 【核心】

- 入参：`code`（项目编码）。
- 调用 `projectService.execute(code)`：见 [spec-sql-parse.md](spec-sql-parse.md)（建库 + 解析生成映射）。
- 返回：`R.success()`。

### 3.3 查询详情（聚合）—— `GET /project/load`

- 入参：`code`。
- 规则：`getOne` 项目 + 聚合 `projectFramworkService.list`、映射、SQL、仓库账户等关联，组装到 `Project` 的非表字段返回。
- 返回：`R`，`data` 为聚合后的项目详情。

### 3.4 项目列表 —— `GET /project/list`

- 入参：`token`、`curPage:Integer`、`pageSize:Integer`。
- 规则：`projectService.lambdaQuery()` 分页（按 state != Delete 等条件）。
- 返回：`R`，`data` 为分页结果（`PageVO` 语义）。

### 3.5 修改项目 —— `RequestMapping /project/modify`

- 入参：`Project`（隐藏绑定）。
- 规则：`getOne` 后 `saveOrUpdate`；刷新 `updateTime`。
- 返回：`R`。

### 3.6 删除项目 —— `POST /project/delete` 【级联】

- 入参：`ProjectVO`（隐藏绑定，至少含 `code`）。
- 规则（`ProjectServiceImpl.delete`）：
  1. `code` 空 → `Empty_Param`。
  2. 删除 `project` 行（按 code）。
  3. 级联删除：`project_framwork`、`project_sql`、`project_repository_account`、`project_module`、`project_job`、`project_job_logs`（按 project code/code）。
  4. 遍历 `project_map`：删除对应 `map_field_column`、`map_relationship`、`project_map`（按 mapClassTableCode）。
  5. 删除产物文件：工作空间目录 `Workspace/<englishName>` 与 ZIP `Repository_Path/<englishName>.zip`（路径用 `Setting` 解析）。
- 返回：`R`。

### 3.7 扫描文件路径 —— `GET /project/scan/path`

- 入参：`code`、`filePath`。
- 规则：`getOne` 项目后用 `FileUtil` 扫描指定路径，返回文件/目录列表（在线浏览产物）。
- 返回：`R`，`data` 为文件树/内容。

### 3.8 下载产物 —— `GET /project/download/{projectName}`（**@Deprecated**）

- 入参：`@PathVariable projectName`、`HttpServletResponse`。
- 规则：定位 `Repository_Path/<projectName>.zip`，以流写出。
- 返回：文件流（`void`）。

### 3.9 其它已废弃端点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/project/load/code/{code}` | 按编码查项目（@Deprecated） |

---

## 4. 验收标准

- [ ] 创建项目缺任一必填字段返回 `Empty_Param`；`englishName` 重复返回 `Exists`。
- [ ] 创建成功后 `code = id` 且 `state = Enable`。
- [ ] 删除项目后，所有关联表无残留、产物目录与 ZIP 被清除。
- [ ] `/project/load` 返回的详情包含框架、映射、SQL 等聚合数据。

---

## 5. 关联规格

- SQL 导入与 `init` 解析细节 → [spec-sql-parse.md](spec-sql-parse.md)
- 框架选择 → [spec-frameworks-template.md](spec-frameworks-template.md)
- Git 仓库配置 → [spec-settings-repository.md](spec-settings-repository.md)
- 执行生成 → [spec-codegen.md](spec-codegen.md)
