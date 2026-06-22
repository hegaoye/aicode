# AI-Code 架构设计文档

> 一个以模板引擎为核心、由「SQL 反向驱动」的全栈代码生成框架。
> 拿到 SQL 建表脚本，像 Jenkins 打包一样「点击生成」一整套 `dao → service → ctrl → frontend` 可运行代码，并自动提交 Git / 打包下载。

- 文档版本：v1.0
- 生成日期：2026-06-22
- 适用代码分支：`dev`
- 运行环境：Java 21、Spring Boot 3.3.9

> 📑 配套规范（从本文第 5、7、9 章拆分扩充）：
> - [code-style.md](code-style.md) —— 编码风格与约定（命名、分层、响应、模板、安全红线）
> - [testing.md](testing.md) —— 测试策略与编写约定

---

## 目录

1. [需求总结](#1-需求总结)
2. [总体架构分析](#2-总体架构分析)
3. [核心业务流程](#3-核心业务流程代码生成主链路)
4. [领域与数据模型分析](#4-领域与数据模型分析)
5. [技术规范分析](#5-技术规范分析)
6. [技术选型分析](#6-技术选型分析)
7. [模块管理分析](#7-模块管理分析)
8. [部署架构](#8-部署架构)
9. [现状评估与改进建议](#9-现状评估与改进建议)

---

## 1. 需求总结

### 1.1 项目定位

AI-Code 是一款 **代码生成脚手架（Scaffolding / Code Generator）**，解决的核心痛点是：

> 同样的技术框架下，新业务总是在重复编写 CRUD（增、删、查、改、分页、条件查询、关联查询、关联分页）等「无聊但必须」的样板代码。

它与「完整开源项目」（如 RBAC 权限系统）的思路不同——不提供现成业务，而是提供**抽象与整合的能力**：把可复用的技术框架做成「模板」，业务模型一旦以 SQL 表达，即可一键组合各类技术栈生成全套可运行代码。理念是「让今天的一行代码在未来体现更大价值」，所谓 **「无招胜有招」**。

### 1.2 功能性需求

| 编号 | 功能 | 说明 |
|------|------|------|
| F1 | SQL 反向解析 | 导入 `CREATE TABLE` 脚本，自动建库建表并反射出表/列元数据 |
| F2 | 模型映射管理 | Web-UI 维护「类↔表」「字段↔列」映射、类间关联关系（1:1 / 1:N） |
| F3 | 显示属性配置 | 配置字段在列表页/详情页的显隐、控件类型、校验规则，驱动前端代码生成 |
| F4 | 模板仓库管理 | 从 Git 模板库（公有/私有）按需拉取技术框架模板 |
| F5 | 多技术栈组合 | 一个项目可同时选择多个框架（如 springboot + angular），各自生成到独立目录 |
| F6 | 一键构建 | 异步生成 `dao→service→ctrl→frontend` 全套代码，接口自动对接 |
| F7 | 实时构建日志 | 通过 WebSocket 像 Jenkins 一样实时推送构建日志，并落盘可回看 |
| F8 | 产物交付 | 自动提交到 Git/SVN 仓库 + 打包 ZIP 供下载 |
| F9 | 增量生成 | 支持增量/全量两种生成模式 |
| F10 | 对话式生成 | `.opencode` Skill 支持通过 TUI/聊天工具对话式完成全流程 |

### 1.3 非功能性需求

- **开箱即用**：默认 H2 文件数据库（MySQL 兼容模式），5 分钟可启动，支持 Docker / K8s。
- **可扩展**：模板与引擎可插拔（Freemarker / Beetl），技术栈通过外部 Git 模板仓库扩展，无需改代码。
- **高吞吐构建**：基于 Java 21 虚拟线程执行异步构建任务。
- **可观测**：集成 Actuator + Prometheus（Micrometer），管理端口 8088。

---

## 2. 总体架构分析

### 2.1 架构风格

- **单体分层 + 模块化（Modular Monolith）**：Gradle 多模块单体应用，逻辑上严格分层。
- **门面隔离（Facade Pattern）**：`facade` 模块只放契约（实体/DTO/VO/Service 接口/异常），实现下沉到 `aicode`，实现「接口与实现分离」。
- **模板方法 + 数据驱动**：生成逻辑固定，输出由「模板 + 模型数据（TemplateData）」决定，是典型的 **数据驱动代码生成**。

### 2.2 三大 Gradle 模块

```
aicode (根, settings.gradle)
├── common   —— 与业务无关的通用基础库（工具、注解、枚举、统一响应）
├── facade   —— 业务契约层（entity / dto / vo / service 接口 / exceptions）
└── aicode   —— Spring Boot 主应用（ctrl / service 实现 / dao / config / 模板引擎 / websocket）
```

依赖方向（单向，无环）：

```
        ┌─────────┐
        │ aicode  │  (Spring Boot 应用, 可运行)
        └────┬────┘
       depends on
        ┌────┴────┐
        │ facade  │  (契约: entity/dto/vo/service 接口)
        └────┬────┘
       depends on
        ┌────┴────┐
        │ common  │  (基础工具, 无 Spring Web 依赖)
        └─────────┘
```

> `common` 不依赖 Spring Boot Web，可被任意模块复用；`facade` 依赖 `common` 与 MyBatis-Plus 注解；`aicode` 同时依赖二者并提供全部实现与 Web 入口。

### 2.3 分层架构（aicode 主应用内部）

按业务域（package-by-feature）划分，每个域内再分层：

```
HTTP / WebSocket
      │
┌─────▼─────────────────────────────────────────────┐
│ ctrl   控制器层      *Controller   (@RestController) │
├────────────────────────────────────────────────────┤
│ service 业务实现层   *ServiceImpl  (实现 facade 接口) │
├────────────────────────────────────────────────────┤
│ dao    数据访问层    *DAO + mapper/*Mapper (MyBatis-Plus)│
├────────────────────────────────────────────────────┤
│ DB     H2 / MySQL 兼容  (Druid 连接池, 双数据源)      │
└────────────────────────────────────────────────────┘
横切关注点: filter(拦截器) · config(配置) · config/template(模板引擎) · config/websocket
```

业务域（功能包）：`account`（账户）、`database`（库表列元数据）、`map`（映射关系）、`display`（显示属性）、`frameworks`（框架/模板）、`module`（模块池）、`project`（项目，最核心）、`setting`（全局设置）、`session`（登录/首页）。

### 2.4 关键组件协作图

```
            ┌──────────────────────────────────────────────────────┐
            │                   Web-UI (Angular, 独立仓库)            │
            └───────────────┬──────────────────────────────────────┘
            REST(JWT token)  │   ▲ WebSocket(/websocket.shtml 实时日志)
                             ▼   │
  ┌──────────────────────────────────────────────────────────────────┐
  │  ProjectController / ProjectJobController / ProjectSqlController ...│
  └───────────────┬──────────────────────────────────────────────────┘
                  │ execute(projectCode)
                  ▼
  ┌───────────────────────────┐   @Async(虚拟线程)
  │ ProjectJobServiceImpl      │──────────────┐
  └───────────────────────────┘              ▼
                                ┌──────────────────────────────┐
                                │ GeneratorSVImpl.aiCode(...)   │ 代码生成核心编排
                                └───┬───────────┬───────────┬───┘
                  ┌─────────────────┘           │           └─────────────────┐
                  ▼                             ▼                             ▼
         GitTools(jgit)              TemplateHelper(策略)            ZipTools(zip4j)
      clone 模板/commit&push     Freemarker / Beetl 渲染          打包 ZIP 产物
                  │                             │
                  ▼                             ▼
        Git 模板仓库(gitee)            TemplateData(模型数据) + .ftl/.btl 模板
```

---

## 3. 核心业务流程（代码生成主链路）

入口：`GET /project/job/execute?code={projectCode}` → 创建 `ProjectJob`（状态 `Executing`）→ `@Async` 虚拟线程执行 → `GeneratorSVImpl.aiCode()`。

`GeneratorSVImpl.aiCode()` 的七步编排：

1. **创建工作空间**（`buildProject`）：清理旧目录；若配置了 Git 仓库则 `clone` 检出；多技术栈时为每个框架建子目录；`buildNumber++`。
2. **转换模型**：按 `projectCode` 加载 `ProjectMap → MapClassTable`，并装配其 `MapFieldColumn`（字段列）与 `MapRelationship`（关联）。
3. **下载模板**（`prepareframeworksTemplateList`）：从 Git 模板仓库 `clone` 所选框架模板，删除无关目录，把模板文件登记进 `frameworks_template` 表，并通过 `aicode.json` 探测模板引擎（Freemarker/Beetl）。
4. **生成源码**（`generator`，三重循环：框架 × 模板文件 × 映射类）：
   - 拆分主键/非主键/表格字段，装配 1:1、1:N 关联与显示属性，构造 **`TemplateData`**（模板唯一数据源）。
   - 解析目标路径：替换 `${basepackage}`/`${className}`/`${module}`/`${model}` 等占位符（同时兼容 Beetl 的 `$xxx$` 风格）。
   - 调用 `TemplateHelper.generate(templateData, targetFilePath, templatePath)` 渲染落盘；支持状态枚举类（`$classNameState$`）的衍生生成。
5. **生成附加 SQL**（`generateTsql`）：把项目 SQL 追加分布式 ID 注册表 `worker_node` 写入产物目录。
6. **版本控制**：若配置仓库账号，`GitTools.commitAndPush` 自动提交并推送。
7. **打包交付**（`zipProject`）：ZIP 打包到仓库目录，回填 `downloadUrl`；更新 `ProjectJob` 状态为 `Completed`/`Error`。

> 全流程每一步都通过 `WSClientManager.sendMessage()` 实时推送 WebSocket 日志，并 `logsSV.saveLogs()` 落盘，实现「Jenkins 式」可回看构建日志。

---

## 4. 领域与数据模型分析

### 4.1 核心实体与关系

```
Project (项目, 聚合根)
 ├─ ProjectSql                         项目 SQL 脚本
 ├─ ProjectFramwork ─→ Frameworks ─→ FrameworksTemplate   选用的技术栈与模板
 ├─ ProjectMap ─→ MapClassTable        类↔表 映射
 │                 ├─ MapFieldColumn ─→ DisplayAttribute   字段↔列 映射 + 前端显示属性
 │                 └─ MapRelationship  类间关联 (1:1 / 1:N)
 ├─ ProjectModule ─→ Module            模块池
 ├─ ProjectModel / ProjectModelClass   UI 菜单/模型树
 ├─ ProjectRepositoryAccount           Git/SVN 凭据
 └─ ProjectJob / ProjectJobLogs        构建任务与日志

Database / Table / Column   —— 映射 information_schema, 用于 SQL 解析后的元数据自省
Setting                     —— 全局 K-V 配置 (Workspace / Template_Path / Repository_Path ...)
Account                     —— 用户账户
```

### 4.2 「三层映射」是本框架的灵魂

代码生成的本质是把「数据库世界」翻译为「代码世界」，通过三层映射声明式描述：

| 映射 | 实体 | 作用 |
|------|------|------|
| 类 ↔ 表 | `MapClassTable` | `tableName → className`，决定生成哪些类 |
| 字段 ↔ 列 | `MapFieldColumn` | `column → field`，SQL 类型 → Java 类型转换，标记主键/日期/状态 |
| 关联关系 | `MapRelationship` | 通过 `mainField/joinField` 定义 1:1、1:N，生成关联查询/导航 |

### 4.3 显示属性驱动前端

`DisplayAttribute` 是每个字段的「UI 契约」：是否列表页/详情页展示、控件类型（Input/Select/DatePicker…）、是否必填、校验模式、查询匹配方式、是否可插入/更新。生成器据此渲染表单、列表、详情页与查询接口——这是前端代码能「一条龙」生成的关键。

### 4.4 数据库总览

Schema 共约 **22 张表**（`db/schema.sql`），默认 H2 文件库（MySQL 兼容模式）。核心表：`project`、`project_*`（sql/framwork/module/map/job/job_logs/model/model_class/repository_account/code_catalog）、`map_class_table`、`map_field_column`、`map_relationship`、`display_attribute`、`frameworks`、`frameworks_template`、`module`、`setting`、`account`，以及分布式 ID 的 `worker_node`。

---

## 5. 技术规范分析

> 📤 本章内容（包与命名、分层职责、数据对象分型、统一响应 `R`、异常处理、持久层、横切关注点、Lombok、日志、模板、构建与安全红线等编码规范）已拆分至独立文档：**[code-style.md](code-style.md)**。
>
> 此处不再重复，请直接参阅该文档。

---

## 6. 技术选型分析

### 6.1 技术栈总览

| 层次 | 选型 | 版本 | 选型理由 |
|------|------|------|----------|
| 语言/运行时 | Java | 21 | 使用虚拟线程（Project Loom）支撑高并发异步构建 |
| 应用框架 | Spring Boot | 3.3.9 | 主流、生态完善；Web + WebSocket + Actuator + Validation + AOP |
| 持久层 | MyBatis-Plus | 3.5.7 | CRUD 零样板、Lambda 条件构造器、内置分页 |
| 连接池 | Druid | 1.2.22 | 监控统计（stat filter）、SQL 防注入能力强 |
| 数据库 | H2 (MySQL 模式) | 1.4.200 | 内嵌零部署、开箱即用；生产可切 MySQL/TiDB |
| 模板引擎 | Freemarker / Beetl | 2.1.2 / 3.15.12 | **可插拔双引擎**，模板自带 `aicode.json` 声明引擎 |
| ID 生成 | 百度 UidGenerator | - | Snowflake 改良，分布式唯一 ID（独立 uid 数据源管理 worker_node）|
| 版本控制 | Eclipse JGit | 4.9.2 | 纯 Java 操作 Git，clone 模板 / commit&push 产物 |
| 实时通信 | Spring WebSocket | - | 构建日志实时推送（Jenkins 式体验）|
| API 文档 | SpringDoc + Knife4j | 2.3.0 / 3.0.3 | OpenAPI 3 + 增强中文 UI |
| 监控 | Actuator + Micrometer Prometheus | 1.12.5 | 健康检查、指标暴露 |
| 缓存 | Caffeine | 3.1.8 | 本地高性能缓存 |
| 工具 | Hutool / FastJSON2 / PageHelper / commons-* / zip4j / pinyin4j | - | 通用工具集 |
| 前端 | Angular（独立仓库）| - | 生成的前端代码及管理 UI |

### 6.2 关键选型决策点

- **双数据源（tidb + uid）**：
  - `tidbDataSource`（`@Primary`，业务数据）+ `uidDataSource`（百度 UID 的 `worker_node` 注册表）。二者均连同一 H2 库但 MyBatis-Plus 配置隔离（`MybatisPlusConfigTidb` 扫描 `com.aicode.*.dao.mapper`，`MybatisPlusConfigUid` 扫描 `com.baidu.fsg.uid.worker.dao`），为切换 TiDB 等分布式库预留了扩展位。
- **可插拔模板引擎（策略模式）**：`TemplateHelper` 接口 + `FreemarkerHelper`/`BeetlHelper` 两实现；运行期由模板内 `aicode.json` 的 `engine` 字段动态选择，新增引擎只需实现接口。
- **外部 Git 模板仓库**：技术栈即「模板仓库」，与代码解耦。新增/升级框架（如 SpringBoot 2.x→3.x、新增 RocketMQ）只改模板、不改生成器，可社区共建（模板库：gitee.com/helixin/aicode_template）。
- **虚拟线程异步**：`TaskExecutorConfig` 用 `Thread.startVirtualThread` 承载 `@Async` 构建任务，构建是 IO 密集（git clone / 文件读写），虚拟线程显著降低线程成本。

---

## 7. 模块管理分析

### 7.1 Gradle 模块组织

`settings.gradle` 聚合三模块：`common`、`facade`、`aicode`。根 `build.gradle` 为空，各模块独立声明插件与依赖。

| 模块 | 性质 | 关键依赖 | 职责 |
|------|------|----------|------|
| `common` | 普通 Java 库 | jgit、java-jwt、hutool、fastjson2、zip4j、pinyin4j | 与业务无关的工具、注解、枚举、统一响应/分页/异常基类 |
| `facade` | 普通 Java 库 | mybatis-plus-extension、springdoc、依赖 `common` | 业务契约：entity、dto、vo、service 接口、域异常 |
| `aicode` | Spring Boot 应用 | spring-boot-starter-*、mybatis-plus、druid、freemarker、beetl、依赖 `common`+`facade` | Web 入口、service 实现、dao、config、模板引擎、websocket |

### 7.2 「模块」的两重含义（需区分）

1. **工程模块（Gradle module）**：上文 common/facade/aicode，是物理代码组织。
2. **业务模块（Module / ProjectModule / `ModuleEnum`）**：领域概念，指生成项目时的逻辑分层单元（po/vo/dao/ctrl/facade/service）与「模块池」。代码中 `module` 域、`TemplateData.module/model` 仅用于**项目管理与目录分离**，不参与 Java 包定义。`GeneratorSVImpl` 第 5 步「获取模块信息」目前标注 `TODO`，属规划中能力。

### 7.3 可扩展点（插件化思路）

| 扩展维度 | 机制 | 是否改代码 |
|----------|------|-----------|
| 新增技术框架/模板 | 提交模板到 Git 模板仓库 | 否 |
| 新增模板引擎 | 实现 `TemplateHelper` 接口 | 是（一处） |
| 新增数据库类型 | 扩展数据源/类型映射 `DatabaseDataTypesUtils` | 是 |
| 模板可用变量 | 在 `TemplateData` 增字段 | 是（一处） |
| 对话式生成 | `.opencode/skills/aicode/SKILL.md` 定义流程 | 否（声明式） |

---

## 8. 部署架构

- **本地**：`./gradlew bootRun`，访问 `http://127.0.0.1:8080/index.html`（admin/888888），H2 控制台 `/h2`。
- **Docker（推荐）**：`devops/docker/`（Dockerfile + docker-compose + 初始化 SQL），镜像 `hegaoye/aicode`，与 DB `link`。
- **Kubernetes**：`devops/k8s/deployment.yml`，NodePort 暴露。
- **Nginx**：README 提供反向代理配置（含 WebSocket `Upgrade` 透传、12h 读超时以适配长构建）。
- **配置外部化**：数据源 `url/username/password` 支持 `${database}/${username}/${password}` 占位符注入。
- **优雅停机**：`server.shutdown: graceful` + 30s 超时；`ApplicationReadyEventListener`/`ApplicationClosedEventListener` 处理生命周期。

---

## 9. 现状评估与改进建议

### 9.1 优点

- 抽象到位：三层映射 + 模板驱动，把「重复编码」转化为「配置 + 模板」，复用性强。
- 解耦彻底：模板与生成器分离、引擎可插拔、技术栈外置 Git 仓库，社区可共建。
- 体验现代：虚拟线程异步构建、WebSocket 实时日志、Docker/K8s 一键部署、对话式（opencode）生成。
- 分层清晰：facade 契约隔离、统一响应/异常、package-by-feature。

### 9.2 风险与待办

| 项 | 现状 | 建议 |
|----|------|------|
| 安全 | `LoginInterceptor` 未挂载、token 走 URL 参数、默认弱口令、H2 控制台 `web-allow-others: true` | 启用登录拦截器、token 改 Header、生产关闭 H2 控制台、强制改密 |
| 凭据存储 | Git/SVN 账号密码明文入库 | 加密存储（如 Jasypt）|
| 模块能力 | `GeneratorSVImpl` 模块信息、SVN 工具为 `TODO` | 补全 SVN 支持与业务模块编排 |
| 数据库版本 | H2 锁定 1.4.200（旧版）| 评估升级，关注 H2 历史安全公告 |
| 统一包装 | `GlobalResponseBodyAdvice` 注释关闭 | 评估启用以减少各 Controller 重复 `R.*` |

> 📤 关于「测试缺失」的现状、测试策略与优先补齐清单，已拆分至独立文档：**[testing.md](testing.md)**。

---

## 附：关键代码索引

| 关注点 | 文件 |
|--------|------|
| 应用入口 | `aicode/src/main/java/com/aicode/Application.java` |
| 生成核心编排 | `aicode/.../project/service/GeneratorSVImpl.java` |
| 模板数据模型 | `aicode/.../config/template/TemplateData.java` |
| 模板引擎策略 | `aicode/.../config/template/TemplateHelper.java` + `FreemarkerHelper`/`BeetlHelper` |
| 异步线程池 | `aicode/.../config/TaskExecutorConfig.java` |
| WebSocket 日志 | `aicode/.../config/websocket/WSClientManager.java`、`WebSocketServer.java` |
| 双数据源 | `aicode/.../config/DataSourceConfig.java`、`MybatisPlusConfigTidb/Uid.java` |
| 统一响应 | `common/src/main/java/com/aicode/core/R.java` |
| Git 工具 | `common/.../core/tools/GitTools.java` |
| 数据库 Schema | `aicode/src/main/resources/db/schema.sql` |
| 对话式生成流程 | `.opencode/skills/aicode/SKILL.md` |
