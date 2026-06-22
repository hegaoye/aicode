# 功能规格：全局设置与版本控制账户（spec-settings-repository）

> 通用约定见 [spec-overview.md §4](spec-overview.md)。本域提供系统级路径配置（Setting）与项目级 Git/SVN 产物仓库凭据（RepositoryAccount）。这些配置被 [spec-sql-parse.md](spec-sql-parse.md) 与 [spec-codegen.md](spec-codegen.md) 大量读取。

---

## 1. 功能目标

- 维护系统全局键值设置（工作空间、模板路径、ZIP 仓库路径、默认数据库等）。
- 为项目维护产物仓库账户（Git/SVN 地址与凭据），用于检出与推送生成代码。

---

## 2. 全局设置（Setting）

### 表 `setting`

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | bigint PK | 主键 |
| `k` | varchar(64) | 键 |
| `v` | varchar(64) | 值 |
| `description` | varchar(256) | 说明 |

### 枚举 `SettingKey`（系统约定键，重建须在 `db/data.sql` 预置对应行）

| 键 | 含义 | 典型用途 |
|----|------|----------|
| `DefaultDatabase` | 默认数据库 | 建库时的默认库设置（[spec-sql-parse §4.1](spec-sql-parse.md)） |
| `Gradle_Directory_Structure` | gradle 目录结构 | 产物结构约定 |
| `Workspace` | 工作目录 | 生成产物的工作空间根（[spec-codegen §4.1](spec-codegen.md)） |
| `Package_entity` | 实体目录命名 | 实体包路径 |
| `Template_Path` | 模板默认路径 | 模板克隆/读取根（[spec-codegen §4.3](spec-codegen.md)） |
| `Repository_Path` | zip 仓库路径 | ZIP 产物输出目录（[spec-codegen §4.8](spec-codegen.md)） |
| `GitHome_Default` | 默认系统仓库路径 | 默认模板仓库 |
| `SandBox_Path` | 沙箱环境目录 | 隔离构建（规划） |

枚举 `SettingState`：空枚举（占位）。

### 实体 `Setting`：`id:Long`、`k:String`、`v:String`、`description:String`。

> ⚠️ 无独立 Setting 控制器对外 CRUD；设置主要由 `db/data.sql` 初始化、`SettingMapper` 读取。`settingService.load()` 被项目下载等流程调用。重建时**必须**在初始化数据中写入上表关键键值（尤其 `Workspace`、`Template_Path`、`Repository_Path`、`DefaultDatabase`），否则生成主链路无法定位路径。

---

## 3. 版本控制账户（RepositoryAccount）

### 表 `project_repository_account`

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | bigint PK | 主键 |
| `code` | varchar(64) PK | 版本管理编码 |
| `projectCode` | varchar(64) | 项目编码 |
| `account` | varchar(64) | 账户名 |
| `password` | varchar(32) | 密码（**当前明文**） |
| `home` | varchar(256) | 仓库地址 |
| `description` | varchar(256) | 仓库说明 |
| `state` | varchar(16) | Enable/Disenable/Delete |
| `type` | varchar(16) | 仓库类型：Git, Svn |

枚举：`ProjectRepositoryAccountState`（Enable/Disenable/Delete）、`ProjectRepositoryTypeEnum`（`GIT("GIT")`、`SVN("SVN")`，`getEnum(String)`）。

### API 端点（`ProjectRepositoryAccountController`，`@RequestMapping("/project/repository")`）

| 方法 | 路径 | 入参 | 返回 | 说明 |
|------|------|------|------|------|
| POST | `/project/repository/build` | `ProjectRepositoryAccount`(隐藏) | `R` | 创建项目仓库账户【核心】 |
| POST | `/project/repository/modify` | `ProjectRepositoryAccount`(隐藏) | `R` | 修改（getOne 后 updateById）【核心】 |
| GET | `/project/repository/load` | `code` | `R` | 查询详情 |
| GET | `/project/repository/load/code/{code}` | `@PathVariable code` | `ProjectRepositoryAccountVO` | 按编码查（**@Deprecated**） |
| GET | `/project/repository/list` | `ProjectRepositoryAccountPageVO`(隐藏) | `R` | 列表（**@Deprecated**） |

### 业务规则

- 一个项目通常对应一条仓库账户记录（`projectCode` 关联）。
- 生成时（[spec-codegen.md](spec-codegen.md)）：
  - `type=GIT` 且 `home` 以 `.git` 结尾 → 构建前 `cloneGit` 检出、构建后 `commitAndPush` 推送。
  - `type=SVN` → **TODO，未实现**（`buildProject` 中留空）。
  - 未配置仓库账户 → 跳过 Git 步骤，仅生成到工作空间并打 ZIP。

---

## 4. 其它项目从属配置（模块，多为 @Deprecated）

这些控制器存在但前端基本未用，重建时可保留接口空壳以兼容：

### 模块池 `module` / `module_file`

- `Module`：`id, code, name, description`。`ModuleFile`：`id, moudleCode, path`。
- `ProjectModuleController`（`/project/mouldles`，@Deprecated）：`load`/`add`/`list`。
- 关联表 `project_module`：`projectCode, moduleCode`。
- 枚举 `ModuleEnum`（生成分层语义）：`po, vo, dao, ctrl, facade, service`（各带中文描述）。

### UI 模型树 `project_model` / `project_model_class`（@Deprecated）

- `ProjectModel`：`id, code, preCode, name, route, css, isMenu(Y/N), ico`（菜单/模型树）。
- `ProjectModelClass`：`id, mapClassTableCode, projectModelCode`。
- 控制器 `ProjectModelController`(`/projectModel`)、`ProjectModelClassController`(`/projectModelClass`) 均 @Deprecated。

### 产物目录 `project_code_catalog`（@Deprecated）

- `ProjectCodeCatalogController`(`/projectCodeCatalog`)：build/list/modify/delete，记录生成文件清单。

> `GeneratorSVImpl` 中「获取模块信息」标注 `TODO`，业务模块编排为规划能力，见 [architecture.md §7.2](../standards/architecture.md)。

---

## 5. 验收标准

- [ ] `db/data.sql` 预置 `Workspace`/`Template_Path`/`Repository_Path`/`DefaultDatabase` 等设置，生成链路可正确定位路径。
- [ ] 为项目配置 Git 仓库后，生成时能检出并推送产物。
- [ ] 未配置仓库时生成不报错，仅产出工作空间代码与 ZIP。
- [ ] SVN 类型当前不支持（明确返回/跳过，不静默假成功）。

---

## 6. 安全注意

- 仓库密码、框架私库密码当前**明文入库**（`project_repository_account.password`、`frameworks.password`）。生产重建建议加密存储，详见 [code-style.md §13](../standards/code-style.md)。
