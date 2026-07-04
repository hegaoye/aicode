# AGENTS.md

> 本文件是面向后续 OpenCode 协作会话的项目说明文档。所有内容均以中文沟通（代码、命令、路径、URL、包名等技术标识符保留英文原文）。
> 适用范围：项目内一切协作、提问、变更建议、提交信息、文档撰写。

## 规范体系（必读）

本文件 §5（架构规范）、§6（编码规范）、§7（测试规范）整合自 `docs/standards/` 下的三份基线文档：

| 本文件章节 | 基线文档（细则原文） | 角色 |
|-----------|-------------------|------|
| §5 架构规范 | `docs/standards/architecture.md` | 架构风格、依赖方向、核心实体、生成主链路、关键决策 |
| §6 编码规范 | `docs/standards/code-style.md` | 包命名、分层、数据对象、统一响应、异常、持久层、Lombok、日志、模板、安全红线 |
| §7 测试规范 | `docs/standards/testing.md` | 测试金字塔、JUnit 5 / AssertJ / Mockito、目录命名、Generator 主链路测试策略、优先补齐清单 |

> **AGENTS.md 是「全局要求」要点摘要，完整细则、上下文、示例以 `docs/standards/*.md` 为准**。新增条目或原则冲突时，先看原文，再决定是否同步回 AGENTS.md。
>
> CDS（Class Data Sharing）运维操作步骤详见 `docs/standards/cds.md`，本文 §9 仅作指针引用。

---

## 0. 沟通规范

- **语言**：项目相关沟通（对话、文档、提交信息、变更说明、PR 描述）一律使用**简体中文**。
- **保留项**：代码标识符（包名、类名、变量名、URL、命令、配置键、错误码）保持英文原文不动。
- **代码注释**：源代码中的中文注释为项目原有约定，**保留不动**；新增注释同样使用中文。
- **术语**：通用技术术语（如 Spring Boot、Gradle、JWT、CI）保留英文；业务术语用中文（如"代码生成"、"模板仓库"、"构建项目"）。
- **回应语言**：默认使用中文回复；用户切换为英文时跟随切换。

---

## 1. 项目概述

`aicode` 是一个基于 **Spring Boot 3.3.9 / Java 21** 的**代码生成平台**：

- **架构风格**：单体分层 + 模块化（Modular Monolith）+ 门面隔离（Facade Pattern）+ 模板方法 / 数据驱动。
- **能力**：接收 SQL 脚本与 freemarker/beetl 模板，逆向生成完整的 CRUD 工程（DAO → Service → Ctrl → 前端），并可推送到 Git / SVN 仓库。
- **可对话入口**：本仓库同时承载一个对话式 OpenCode 技能（`.opencode/skills/aicode/`，触发关键词 `aicode` / "创建Java项目" / "代码生成"）。**两者职责不同**：本仓库是服务端本身；该技能是面向用户对话的"使用入口"。

---

## 2. 模块结构（Gradle 多模块）

根 `settings.gradle` 声明三个子项目；其他目录（`sql/`、`devops/`、`measurement/`、`docs/`、`openspec/`、`screenshots/`）均为松散内容，**不是** Gradle 模块。

| 模块 | 角色 | 备注 |
|------|------|------|
| `:aicode`  | Spring Boot 应用 — 控制器、服务、Mapper、静态前端 | **唯一可运行且有测试的模块**。入口 `com.aicode.Application`。 |
| `:facade`  | 与下游生成工程共享的 DTO / facade 类型 | 依赖 `:common`。无 main，无测试。 |
| `:common` | 共享工具：JGit、hutool、JWT、pinyin4j、zip4j | 被 `:facade` 与 `:aicode` 同时依赖。**禁**引入 spring-web（详见 §6.2）。 |

依赖方向**单向、无环**（详见 §6.2、architecture.md:73-98）：

```
aicode (Spring Boot 应用)  ──→  facade (契约)  ──→  common (基础工具)
```

Gradle Wrapper 位于 `./aicode/gradlew`（仓库根目录**没有** wrapper），所有 Gradle 命令必须走这个嵌套 wrapper。

---

## 3. 构建 / 开发命令

要求 **Java 21**（Dockerfile 锁定 amazoncorretto:21.0.11-alpine3.23）。

```
./aicode/gradlew :aicode:bootRun        # 启动应用，:8080（业务）/ :8088（actuator）
./aicode/gradlew test                    # 运行所有模块的 JUnit 5 测试
./aicode/gradlew :aicode:bootBuildImage  # Paketo CNB 构建镜像（默认 publish=false）
./aicode/gradlew build                   # 完整构建
```

镜像默认仅产出到本地。推送到 Harbor：设置 `HARBOR_USER` / `HARBOR_PASS` / `HARBOR_URL` 环境变量，并启用 `aicode/build.gradle` 中 `bootBuildImage { ... }` 内被注释的 `publish = true` 块。

---

## 4. 运行时事实

- 应用端口：`http://127.0.0.1:8080`；actuator：`http://127.0.0.1:8088`；H2 控制台：`http://127.0.0.1:8080/h2`（sa/sa，生产应关闭，见 §6.13）。
- 默认账户：`admin / 888888`。**单用户模式**，token 通过 URL 参数 `?token=xxx` 传递（**无 Header 鉴权**，改进路径见 §6.13）。
- K8s 与 aicode 技能共用的存活探针：`GET /actuator/health/liveness`，技能要求 **3 秒内**返回 `UP`。
- 数据库为 **H2（MySQL 兼容模式）**：`jdbc:h2:file:/tmp/aicode;...MODE=MYSQL;AUTO_SERVER=TRUE`。`spring.sql.init.mode=always` 会在每次启动重跑 `classpath:db/schema.sql` 与 `data.sql` —— 状态会被重置，除非用 `database` 环境变量指向其他文件路径。
- 存在**两个 DataSource Bean**（`tidb-data` 与 `uid-data`），默认指向同一 H2 文件。**不要**"顺手"去重，**需先理解下游生成工程的依赖**（详见 §6.8）。
- Spring 虚拟线程已启用（`spring.threads.virtual.enabled: true`），测试与阻塞 JDBC 调用需按 VTH 假设编写。
- H2 版本**锁死 1.4.200**（新版会破坏 Spring Boot 3 的 H2 console 路由）。
- `aicode/libs/*.jar` 是 vendored jar，由 `fileTree(dir: 'libs')` 引入；**不要**用 Maven 重新解析这些依赖。

---

## 5. 架构规范

> 摘自 `docs/standards/architecture.md`。完整架构、关键代码索引见 architecture.md:316-329。

### 5.1 架构风格

- **单体分层 + 模块化（Modular Monolith）**：Gradle 多模块单体，逻辑上严格分层（详见 architecture.md:67-71）。
- **门面隔离（Facade Pattern）**：`facade` 模块只放契约（实体/DTO/VO/Service 接口/异常），实现下沉到 `aicode`，实现「接口与实现分离」。
- **模板方法 + 数据驱动**：生成逻辑固定，输出由「模板 + 模型数据（`TemplateData`）」决定，是典型的**数据驱动代码生成**。

### 5.2 模块依赖（单向无环，禁反向）

参见 §2 的依赖图，详见 architecture.md:73-98 与 code-style.md:42-59。**红线**：

- `common` 不得引入 `spring-boot-starter-web`。
- 业务接口 `*Service` 声明在 `facade`，实现 `*ServiceImpl` 放在 `aicode`。
- 不得制造循环依赖。

### 5.3 分层架构（aicode 主应用内部）

按业务域（**package-by-feature**）分包，域内再分层（详见 architecture.md:100-119）：

```
HTTP / WebSocket
      │
┌─────▼─────────────────────────────────────────────┐
│ ctrl   控制器层      *Controller (@RestController) │
├────────────────────────────────────────────────────┤
│ service 业务实现层   *ServiceImpl (实现 facade 接口) │
├────────────────────────────────────────────────────┤
│ dao   数据访问层    *DAO + mapper/*Mapper (MyBatis-Plus)│
├────────────────────────────────────────────────────┤
│ DB   H2 / MySQL 兼容 (Druid 连接池, 双数据源)          │
└────────────────────────────────────────────────────┘
横切关注点: filter(拦截器) · config(配置) · config/template(模板引擎) · config/websocket
```

业务域：`account`（账户）、`database`（库表列元数据）、`map`（映射关系）、`display`（显示属性）、`frameworks`（框架/模板）、`module`（模块池）、`project`（项目，最核心）、`setting`（全局设置）、`session`（登录/首页）。**新增功能优先归入已有域**，确需新域时按 §6.3 完整建子包。

### 5.4 核心实体与「三层映射」（本框架的灵魂）

Project 是聚合根：包含 `ProjectSql`、`ProjectFramwork`、`ProjectMap`、`ProjectModule`、`ProjectModel`、`ProjectRepositoryAccount`、`ProjectJob` 等（详见 architecture.md:172-191）。

**三层映射**声明式描述「DB 世界 → Code 世界」的翻译：

| 映射 | 实体 | 作用 |
|------|------|------|
| 类 ↔ 表 | `MapClassTable` | `tableName → className`，决定生成哪些类 |
| 字段 ↔ 列 | `MapFieldColumn` | `column → field`，SQL 类型 → Java 类型转换，标记主键/日期/状态 |
| 关联关系 | `MapRelationship` | 通过 `mainField/joinField` 定义 1:1、1:N，生成关联查询/导航 |

`DisplayAttribute` 是每个字段的「UI 契约」（控件类型、校验、列表/详情显隐、查询匹配方式、可插入/更新），驱动前端代码生成 —— 这是前端代码能「一条龙」生成的关键。

### 5.5 代码生成主链路（七步编排）

入口：`GET /project/job/execute?code={projectCode}` → 建 `ProjectJob`（状态 `Executing`）→ `@Async` 虚拟线程执行 → `GeneratorSVImpl.aiCode()`（详见 architecture.md:151-167）：

1. **创建工作空间**（`buildProject`）：清理旧目录；若配置 Git 则 `clone` 检出；多技术栈时为每个框架建子目录；`buildNumber++`。
2. **转换模型**：按 `projectCode` 加载 `ProjectMap → MapClassTable`，装配 `MapFieldColumn` 与 `MapRelationship`。
3. **下载模板**（`prepareframeworksTemplateList`）：从 Git 模板仓库 `clone` 所选框架模板，删除无关目录，把模板文件登记进 `frameworks_template` 表；通过 `aicode.json` 探测引擎（Freemarker/Beetl）。
4. **生成源码**（`generator`，三重循环：框架 × 模板文件 × 映射类）：
   - 拆分主键/非主键/表格字段，装配 1:1、1:N 与显示属性，构造 `TemplateData`（模板唯一数据源）。
   - 解析目标路径：`${basepackage}`/`${className}`/`${module}`/`${model}` 等占位符（同时兼容 Beetl 的 `$xxx$` 风格）。
   - 调用 `TemplateHelper.generate(templateData, targetFilePath, templatePath)` 渲染落盘；支持状态枚举类（`$classNameState$`）的衍生生成。
5. **生成附加 SQL**（`generateTsql`）：把项目 SQL + 分布式 ID 注册表 `worker_node` 写入产物目录。
6. **版本控制**：若配置仓库账号，`GitTools.commitAndPush` 自动提交并推送。
7. **打包交付**（`zipProject`）：ZIP 打包到仓库目录，回填 `downloadUrl`；更新 `ProjectJob` 状态为 `Completed` / `Error`。

> 每步通过 `WSClientManager.sendMessage()` 实时推送 WebSocket 日志，并 `logsSV.saveLogs()` 落盘 ——「Jenkins 式」可回看构建日志。

### 5.6 关键架构决策（不可破坏）

| 决策 | 内容 | 详见 |
|------|------|------|
| 双数据源 | `tidbDataSource`（业务，`@Primary`）+ `uidDataSource`（`worker_node`）。MyBatis-Plus 配置隔离：`MybatisPlusConfigTidb` 扫 `com.aicode.*.dao.mapper`，`MybatisPlusConfigUid` 扫 `com.baidu.fsg.uid.worker.dao` | architecture.md:243-246 |
| 可插拔模板引擎（策略模式） | `TemplateHelper` 接口 + `FreemarkerHelper` / `BeetlHelper` 两实现；运行期由模板内 `aicode.json` 的 `engine` 字段动态选择 | architecture.md:246、code-style.md:211-219 |
| 外部 Git 模板仓库 | 技术栈即「模板仓库」，与代码解耦。新增/升级框架（如 SpringBoot 2.x→3.x、新增 RocketMQ）只改模板、不改生成器（`gitee.com/helixin/aicode_template`） | architecture.md:247 |
| 虚拟线程异步 | `TaskExecutorConfig` 用 `Thread.startVirtualThread` 承载 `@Async` 构建任务，构建是 IO 密集（git clone / 文件读写） | architecture.md:248 |
| 「模块」两重含义（勿混） | 工程模块：common/facade/aicode（见 §2）；业务模块：`Module` / `ProjectModule` / `ModuleEnum`，用于**项目管理与目录分离**，不参与 Java 包定义；`GeneratorSVImpl` 第 5 步「获取模块信息」标 `TODO` | architecture.md:265-267 |

### 5.7 部署架构

- 本地：`./gradlew bootRun`；Docker（推荐）：CNB/Paketo；K8s：`devops/k8s/deployment.yml`；Nginx 反代（含 `Upgrade` 透传 + 12h 读超时以适配长构建）。
- 配置外部化：数据源 `${database}/${username}/${password}` 占位符注入。
- 优雅停机：`server.shutdown: graceful` + 30s 超时；`ApplicationReadyEventListener` / `ApplicationClosedEventListener` 处理生命周期。
- CDS（Class Data Sharing）启用步骤与回滚路径见 `docs/standards/cds.md`（CNB/Paketo `BP_JVM_CDS_ENABLED=true`）。

---

## 6. 编码规范

> 摘自 `docs/standards/code-style.md` 的**全局要求**摘要。完整细则见 code-style.md。

### 6.1 通用约定

- **语言/编译**：Java 21，UTF-8（各模块 `build.gradle` 已配 `options.encoding = 'UTF-8'`）。
- **构建**：Gradle 多模块；新增第三方依赖统一加在对应模块的 `build.gradle`，显式写版本号，不依赖隐式传递。
- **缩进**：4 空格，**禁 Tab**。
- **行尾/文件**：LF，文件以空行结尾。
- **注释语言**：业务注释中文，与现有代码一致；类头可保留 `Created by xxx on date` 风格或补 Javadoc。
- **禁提交**：`build/`、`.gradle/`、`bin/`、`*.iml`、`.idea/`、`*.log`（已在 `.gitignore`）。

### 6.2 模块依赖（红线圈）

三模块单向无环：`aicode → facade → common`（详见 §5.2、code-style.md:42-59）。红线：

- `common` **禁**引入 `spring-boot-starter-web`。
- 业务接口（`*Service`）声明在 `facade`，实现（`*ServiceImpl`）放在 `aicode`，**接口与实现物理分离**。
- 不得制造循环依赖。

### 6.3 包与命名

- **package-by-feature（按业务域分包）**：根包 `com.aicode`，按业务域分一级包，域内再按层分子包：

  ```
  com.aicode.<domain>.controller          控制器
  com.aicode.<domain>.service       Service 实现 (aicode 模块)
  com.aicode.<domain>.dao           DAO
  com.aicode.<domain>.dao.mapper    MyBatis-Plus Mapper
  com.aicode.<domain>.entity        实体 (facade 模块)
  com.aicode.<domain>.dto / .vo     入参 / 出参 (facade 模块)
  com.aicode.<domain>.service       Service 接口 (facade 模块)
  ```

- **类后缀强制**：

  | 后缀 | 含义 | 模块 | 示例 |
  |------|------|------|------|
  | `*Controller` | REST 控制器 | aicode | `ProjectController` |
  | `*Service` | 业务接口 | facade | `ProjectService` |
  | `*ServiceImpl` | 业务实现 | aicode | `ProjectServiceImpl` |
  | `*DAO` | 数据访问封装 | aicode | `ProjectDAO` |
  | `*Mapper` | MyBatis-Plus Mapper | aicode | `ProjectMapper` |
  | `*Exception` | 域异常 | facade | `ProjectException` |
  | `*Status` | 状态枚举 | facade | `ProjectJobStatus` |

- **命名风格**：类名 `UpperCamelCase`；方法/变量 `lowerCamelCase`；常量 `UPPER_SNAKE_CASE`。
- **DB → 命名转换**：DB 表/列 `snake_case`；→ Java 类名 `UpperCamelCase`、变量 `lowerCamelCase`、前端 `dashedCaseName`（kebab-case）。
- **枚举值首字母大写**：与 `ProjectJobState.Executing/Completed/Error`、`TemplateEngineEnum.Freemarker/Beetl` 一致。

### 6.4 分层职责

| 层 | 职责 | 禁做 |
|----|------|-----|
| `controller` 控制器 | 参数校验、调用 service、包装 `R` 返回 | 写业务逻辑、直接访问 Mapper |
| `service` 实现 | 业务编排、事务边界、组装 VO | 处理 HTTP 细节（request/response） |
| `dao` / `mapper` | 数据访问、条件构造 | 含业务规则 |

- Controller 入参校验：`spring-boot-starter-validation`（`@Valid` + DTO 上的约束注解）或显式断言；空值/非法参数返标准错误码（见 §6.6）。
- 跨域调用通过 `facade` 的 `*Service` 接口，**禁** `new` 实现类或跨域引用 `*ServiceImpl`。
- 三分层依赖方向**单向、无环**，新增代码必须遵守：`controller → service → dao`。

### 6.5 数据对象（Entity / DTO / VO）

严格区分入参与出参，**禁**用一个对象贯穿全层：

| 类型 | 用途 | 位置 |
|------|------|------|
| `entity`（PO） | 对应数据库表，MyBatis-Plus 映射 | `facade/<domain>/entity` |
| `*DTO` | 入参（请求），细分 `SaveDTO` / `ModifyDTO` / `PageDTO` | `facade/<domain>/dto` |
| `*VO` | 出参（响应），细分 `VO` / `SaveVO` / `ModifyVO` / `PageVO` | `facade/<domain>/vo` |

- 新增用 `*SaveDTO`，修改用 `*ModifyDTO`，分页用 `*PageDTO`，避免单一大对象。
- VO 继承 `BaseVO`；分页响应统一用 `PageVO`（`records / totalRow / totalPage / curPage / pageSize`）。
- Entity ↔ DTO/VO 转换：`BeanUtils.copyProperties` 或 `StringTools.convertObject/convertList`（fastjson2 round-trip）；转换逻辑放 service 层。
- **实体不要直接暴露给前端**；Controller 返回 VO。

### 6.6 接口与统一响应

- 所有 REST 接口返 `common` 的 `R`（详见 `common/src/main/java/com/aicode/core/R.java`）：

  ```json
  { "code": "0000", "info": "success", "data": {}, "success": true }
  ```

- **错误码约定**：

  | code | 含义 |
  |------|------|
  | `0000` | 成功 |
  | `9003` / `9004` | 参数为空 / 参数非法 |
  | `9007` | 未授权 / Token 无效 |
  | `9999` | 服务器错误 |

- 成功 `R.success()` / `R.success(data)`；失败 `R.failed(...)`；**禁**裸返实体或字符串。
- `GlobalResponseBodyAdvice` 当前**注释关闭**，各 Controller 须**显式**包装 `R`；若后续启用统一包装，须全局移除手动包装，避免双重包裹。
- **REST 约定**：路径以业务域为前缀（`/project/...`、`/framework/...`、`/project/relationship/...`）；用 SpringDoc OpenAPI 3 注解（`@Tag`/`@Operation`/`@Schema`）描述接口，配合 Knife4j UI。

### 6.7 异常处理

- 业务异常抛 `facade/exceptions` 下的域异常（继承 `BaseException`），按 `BaseExceptionEnum` 携带标准错误码与消息。
- 全局由 `ExceptionHandle`（`@ControllerAdvice`）统一捕获，转为标准 `R`，HTTP 400。**不要**在 Controller 里 `try/catch` 后吞掉异常或自行拼装错误结构。
- **唯一例外**：异步构建主链路 `GeneratorSVImpl.aiCode()` 运行在 `@Async` 虚拟线程、脱离请求上下文，须在方法内 `try/catch` 并把失败写入 `ProjectJob.state=Error` + WebSocket / 日志，**不能让异常逃逸到线程顶层**。
- **禁** `e.printStackTrace()` 作为唯一处理（现存代码有此遗留，新代码改用 `log.error(msg, e)`）。

### 6.8 持久层（MyBatis-Plus）

- 简单 CRUD 用 MyBatis-Plus 内置方法 + **Lambda 条件构造器** `LambdaQueryWrapper`（类型安全，避免裸字符串列名）。
- 复杂 SQL 写 `resources/mapper/<domain>/*.xml`，与 Mapper 接口对应。
- 分页用 MyBatis-Plus 分页插件（`MybatisPlusInterceptor`），**禁**手写 limit 拼接。
- 创建/更新时间由 `MybatisPlusMetaObjectHandler` 自动填充，**禁**业务手动 set。
- **双数据源**：业务 Mapper 归 `tidbDataSource`（`MybatisPlusConfigTidb` 扫 `com.aicode.*.dao.mapper`）；分布式 ID 的 `worker_node` 归 `uidDataSource`。**新增业务 Mapper 默认进 tidb**，禁误放到 uid 数据源扫描路径。
- 主键/分布式 ID 用 `UidGenerator.getUID()`（百度 UID），禁自造序列。

### 6.9 横切关注点

- **链路追踪**：所有请求经 `ContextInterceptor` 注入 traceId 到 MDC；异步任务须用 `MdcTaskDecorator` 传递 MDC，保证日志链路连续。
- **事务**：由 `TransactionalAopConfig` 的 AOP 切面统一管理，按约定切入 service 方法；避免在一个事务里做 git clone、文件 IO 等长耗时操作。
- **异步**：耗时构建用 `@Async`，由 `TaskExecutorConfig`（Java 21 虚拟线程）承载；`@Async` 方法**须 public** 且**经 Spring 代理调用**（禁类内自调）。
- **实时日志**：构建过程进度统一经 `WSClientManager.sendMessage()` 推送，并 `logsSV.saveLogs()` 落盘，**二者成对出现**。

### 6.10 Lombok 与样板代码

- 实体/DTO/VO 用 `@Data`；需要构造器链式构建用 `@Builder`（现有 `Project.builder()...build()` 风格）。
- 日志用 `@Slf4j`，禁手写 `LoggerFactory.getLogger`。
- Lombok 仅作 `compileOnly + annotationProcessor`（均为 1.18.32），不传递到运行期。
- 禁为 Lombok 已生成的 getter / setter 再手写重复代码。

### 6.11 日志

- 框架：Logback，配置 `logback-debug.xml` / `logback-info.xml`；`com.aicode` 包默认 debug。
- 占位符而非字符串拼接：`log.info("project: {}", project)`。
- 级别：调试细节 `debug`；关键流程节点 `info`；可恢复异常 `warn`；错误 `error` 并带异常对象。
- 切换日志级别时同步修改 `logging.config` 指向的文件名（见 application.yml 注释约定）。
- **禁**打印敏感信息（Git/SVN 密码、token）到日志。

### 6.12 代码生成模板规范

生成器的「输出代码风格」由**外部模板**决定，模板编写须遵守：

- **模板数据唯一来源**是 `TemplateData`；新增可用变量须先在 `TemplateData` 加字段（一处），再在模板引用。可用变量见 `TemplateData` 类头 Javadoc（如 `${basePackage}`、`${className}`、`${model}`、`${columns}`、`${displayAttributes}` 等）。
- **占位符双风格**：Freemarker 用 `${xxx}`，Beetl 用 `$xxx$`；路径占位符（`${basepackage}` / `${className}` / `${module}` / `${model}` / `$classNameState$`）由 `GeneratorSVImpl.generator()` 统一替换，模板目录/文件名须使用这些约定名。
- **引擎声明**：模板仓库根放 `aicode.json`，用 `engine` 字段声明 `Freemarker` / `Beetl`；未声明默认 Freemarker。
- 模板文件后缀 `.ftl`（Freemarker）/ `.btl`（Beetl），生成时自动去除后缀。
- 新增模板引擎只需实现 `TemplateHelper` 接口（`generate(templateData, targetFilePath, templatePath)`）并注册为 `@Service`，不改生成主链路。

### 6.13 安全编码红线

新代码与改造须遵守：

- **凭据**：Git / SVN / 账户密码**禁明文**入库或入日志；逐步迁移到加密存储（如 Jasypt）。
- **认证**：新接口的 token **优先走请求头**（`Authorization`），**禁新增**「token 走 URL 参数」的接口；推动启用 `LoginInterceptor`。
- **默认口令**：生产环境**禁**保留默认 `admin / 888888`，须强制改密。
- **H2 控制台**：生产环境关闭（`spring.h2.console.enabled=false`，禁用 `web-allow-others`）。
- **SQL**：用户提供的建表 SQL 在受控库执行前须校验合法性（参考 README 关于非法 `"order"` 的说明），避免注入与执行失败。

---

## 7. 测试规范

> 摘自 `docs/standards/testing.md` 的**全局要求**摘要。完整细则见 testing.md。

### 7.1 现状

诚实说明：**当前仓库三模块均无 `src/test` 实现**；`aicode` 模块仅声明了 `testImplementation 'org.springframework.boot:spring-boot-starter-test'`，`common` / `facade` 未引入测试依赖。

本节为 **to-be 规范**：约定后续测试应如何组织与编写，并在 §7.10 给出优先补齐清单。**新增功能应同步补测试**，逐步建立安全网。

### 7.2 技术栈

| 用途 | 选型 | 说明 |
|------|------|------|
| 测试框架 | JUnit 5（Jupiter） | 随 `spring-boot-starter-test` 引入 |
| 断言 | AssertJ / JUnit 5 Assertions | **优先 AssertJ** 链式断言，可读性好 |
| Mock | Mockito | 随 starter 引入，隔离依赖 |
| Spring 测试 | `@SpringBootTest` / `@MockBean` | 集成测试上下文 |
| Web 测试 | `MockMvc`（`@AutoConfigureMockMvc`）/ `WebTestClient` | Controller 层 |
| 数据库 | H2（MySQL 兼容模式） | 与运行时一致，天然适合做测试库 |

`common` / `facade` 写单元测试，须各自在 `build.gradle` 补 `testImplementation 'org.junit.jupiter:junit-jupiter'`（或对应 BOM）。

### 7.3 分层与目标

遵循测试金字塔，重「快而多」的单元测试，集成测试覆盖关键链路：

| 层级 | 范围 | 启动 Spring | 典型对象 |
|------|------|------------|---------|
| 单元 | 单类/单方法，依赖全部 mock | 否 | `common` 工具类、枚举、`TemplateData` 装配逻辑、`StringTools` / `StringHelper` |
| 切片 | 单层 + 部分上下文 | 部分 | Controller（`MockMvc`）、Mapper（`@MybatisPlusTest` 风格 + H2） |
| 集成 | 跨层、近真实 | 是（`@SpringBootTest`） | 登录 → 建项目 → 导 SQL → init → 构建 全链路 |

各模块侧重：

- **common**：纯函数工具最易测，应做到高覆盖（类型映射、字符串/拼音、SQL 转义、zip、JWT）。
- **facade**：POJO 为主，测枚举转换（`TemplateEngineEnum.getTemplate`、各 `*State`、`ModuleEnum.getEnum`）。
- **aicode**：测 service 编排与**生成主链路**（见 §7.7）。

### 7.4 目录与命名

- 测试源码放各模块 `src/test/java`，包路径与被测类**镜像一致**（`com.aicode.<domain>...`）。
- 测试类命名：被测类名 + 后缀。
  - 单元测试：`XxxTest`（如 `StringToolsTest`、`TemplateDataTest`）。
  - 集成测试：`XxxIT` 或 `XxxIntegrationTest`（如 `GeneratorIT`）。
- 测试方法名用「行为_条件_期望」或中文 `@DisplayName` 描述场景，例如：
  - `getTemplate_givenLowerCaseName_returnsFreemarker()`
  - `@DisplayName("非法列名 order 应被纠正为合法变量名")`
- 测试资源放 `src/test/resources`（独立 schema / data、样例 SQL、样例模板）。

### 7.5 单元测试规范

- **隔离**：被测类的外部依赖（Mapper、`GitTools`、`TemplateHelper`、文件系统）一律 `@Mock` / `@MockBean`，不触网、不落盘。
- **三段式**：Arrange–Act–Assert（准备—执行—断言），一个测试只验证一个行为点。
- **断言具体**：断言到具体值/结构，不要只断言「非空」。
- **不依赖顺序**：测试间相互独立，**禁**共享可变静态状态。
- **覆盖边界**：空集合、null、非法输入、大小写、特殊字符（尤其字符串 / SQL 处理类）。
- 纯函数优先无 Spring 上下文，保证毫秒级执行。

### 7.6 集成测试规范

- 用 `@SpringBootTest` 启动真实上下文，数据源指向**独立 H2 测试库**（**禁污染** `/tmp/aicode` 默认库，见 §7.8）。
- Controller 用 `MockMvc` 验证：HTTP 状态、统一响应体 `R` 结构（`code/success/data`）、错误码（`0000/9003/9004/9007/9999`）。
- 涉及 `@Async` 的构建：测试中应能等待任务完成（**轮询** `ProjectJob.state` 至 `Completed/Error`，设超时），**禁** `sleep` 固定时长。
- 涉及 WebSocket 日志：可对 `WSClientManager.sendMessage` 做验证替身，断言关键节点消息被推送。
- 集成测试默认**不连真实远程 Git**；`GitTools` 的 clone / push 用 mock 或本地裸仓库（`file://`）替代，避免外网依赖与不稳定。

### 7.7 代码生成主链路测试策略（重点）

生成主链路 `GeneratorSVImpl.aiCode()`（详见 §5.5）是核心资产，也是当前最大测试空白。建议分层覆盖：

#### §7.7.1 纯逻辑单元测试（最高优先级，无 IO）

可在不启动 Spring、不碰 Git/文件的前提下测：

- **`TemplateData` 装配**：给定 `MapClassTable` + `MapFieldColumn` 列表，验证主键/非主键/表格字段拆分、`model` 推导（表名含 `_` 取前缀）、`classNameLower` / `dashedCaseName` 生成、1:1 / 1:N 关系装配、显示属性 → `Field` 映射。
- **路径占位符替换**：抽取/验证 `${basepackage}` / `${className}` / `${module}` / `${model}`、Beetl `$xxx$`、`$classNameState$` 的替换结果（建议把 `generator()` 中路径拼装逻辑重构为可独立测试的纯方法）。
- **引擎适配**：`adapterTemplateEngine` 给定不同 `aicode.json`（含错误文件名兼容 `ai-code.json` 等）返回正确 `TemplateEngineEnum`，缺失时回退 Freemarker。
- **类型映射**：`DatabaseDataTypesUtils` / `JavaPrimitiveTypeMapping` 的 SQL → Java 类型转换。

#### §7.7.2 模板渲染测试（`TemplateHelper`）

- 准备最小样例模板（`.ftl` / `.btl`）+ 构造好的 `TemplateData`，调用 `FreemarkerHelper.generate` / `BeetlHelper.generate`，断言生成文件内容符合预期。
- 输出目录用 JUnit 5 `@TempDir`，测试后自动清理。

#### §7.7.3 端到端集成测试（mock 外部依赖）

- 用 `@SpringBootTest` 走 `execute → @Async → aiCode` 全流程，但：
  - `GitTools` 用本地裸仓库或 mock，模板「下载」改为指向 `src/test/resources` 内置模板目录；
  - 断言：`ProjectJob` 最终状态 `Completed`、产物目录结构正确、关键文件生成、ZIP 生成、`buildNumber` 自增。
- 失败路径：构造异常（如模板缺失），断言 `ProjectJob.state=Error` 且不抛到线程顶层。

> 提升可测性：逐步把 `GeneratorSVImpl` 中的「文件系统 / Git / 设置读取」抽象为可注入接口，降低对 `Setting` 表与真实路径的硬依赖。

### 7.8 测试数据与环境

- **独立测试库**：测试 `application-test.yml` 覆盖数据源 URL，使用**内存或临时文件 H2**（如 `jdbc:h2:mem:aicode_test;MODE=MYSQL;DB_CLOSE_DELAY=-1`），避免写入默认 `/tmp/aicode`。
- **Schema / Data**：复用 `db/schema.sql`；测试专用初始化数据放 `src/test/resources`；每个测试类 / 方法保证数据独立（`@Transactional` 回滚 或 显式清理）。
- **临时目录**：所有产物 / 克隆目录用 `@TempDir`，**禁**写死绝对路径。
- **样例 SQL**：准备覆盖单表、多表、关联、含状态枚举字段、含非法命名（如 `order`）的建表脚本作为测试夹具。

### 7.9 运行与覆盖率

- 全部测试：`./gradlew test`；单模块：`./gradlew :aicode:test`。
- 集成测试（`*IT`）建议绑定到 `integrationTest` 任务或用标签 `@Tag("it")` 区分，CI 中分阶段执行。
- 覆盖率：引入 **JaCoCo**（`jacoco` 插件），关注**核心包**（`com.aicode.project.service`、`com.aicode.config.template`、`com.aicode.core.tools`）的行/分支覆盖，而非整体百分比。
- 约束：新增 / 修改生成主链路、工具类的 PR **必须**附带测试；CI 测试失败禁合并。

### 7.10 优先补齐清单（按投入产出比）

1. `common` 工具纯函数单测：`StringTools`、`StringHelper`、`DatabaseDataTypesUtils`、`ZipTools`、`JwtToken`。
2. `facade` 枚举转换单测：`TemplateEngineEnum`、`ModuleEnum`、各 `*State`。
3. `TemplateData` 装配单测（§7.7.1）—— 保护生成逻辑的核心数据模型。
4. `TemplateHelper` 渲染单测（§7.7.2，Freemarker + Beetl 各一）。
5. Controller 切片测试（`MockMvc`）：登录、建项目、导 SQL、init 的响应体与错误码。
6. 生成主链路端到端集成测试（§7.7.3，mock Git / 模板）。

---

## 8. OpenSpec 工作流

变更管理走**规范驱动**模式，**非平凡工作必须使用 opsx 技能**，禁止直接动代码。

- 当前规范：`openspec/specs/<能力>/spec.md`
- 提案中的变更：`openspec/changes/<变更id>/{proposal.md, tasks.md, specs/...}`
- 已完成变更：`openspec/changes/archive/<变更id>/`
- 审计日志：`openspec/analysis/audit-*.md`

opsx 入口（功能等价，运行时暴露哪个就用哪个）：

- OpenCode：`/opsx-propose`、`/opsx-apply`、`/opsx-sync`、`/opsx-archive`（定义在 `.opencode/commands/`）
- Claude / skills 镜像：`.claude/skills/openspec-propose`、`openspec-apply-change`、`openspec-sync-specs`、`openspec-archive-change`

仓库**已遵循** OpenSpec 流程：新增功能前先看 `openspec/specs/` 与 `changes/archive/`；若已有相关能力，请在原能力下创建 delta spec，**不要**直接改代码。

废弃 API 治理流程（见 `openspec/specs/api-deprecation-policy/spec.md`）：**标记 → 30 天观察 → 内部 grep 二次核查 → 删除**；删除时同步更新 `aicode/README.md §四`。

---

## 9. 容器 / 部署

- Dockerfile：`aicode/Dockerfile`，基础镜像 `amazoncorretto:21.0.11-alpine3.23`，端口 8080，`JAVA_OPTS="-Xms128m -Xmx512m"`。
- K8s 清单与 nginx upstream 示例：`devops/k8s/`、`README.md` §本地部署 nginx。
- 性能基线脚本：`measurement/startup-time.sh`（冷启动到 `/actuator/health/liveness` 200 的 wall-clock）、`measurement/metaspace-after-up.sh`（Metaspace 用量）—— 输出 JSON，便于 CI 比对。
- **CDS 启用**：见 `docs/standards/cds.md`（CNB/Paketo `BP_JVM_CDS_ENABLED=true`，启动期 metaspace 下降 78%、wall-clock 加速约 17%）。
- dockerhub 镜像（**遗留**，仅供外部教程使用）：`hegaoye/aicode:20210613`。**生产构建请使用 `bootBuildImage`**，不要用此 tag。

---

## 10. 容易踩坑的点

- 编辑**根** `build.gradle` **无效** —— 文件为空。所有 Gradle 配置都在各子项目的 `build.gradle` 里。
- `LOG_HOME_IS_UNDEFINED/` 目录会出现在 `aicode/` 与仓库根下 —— 这是 logback 在 `LOG_HOME` 环境变量未设置时的日志输出，属预期行为且被 `.gitignore` 忽略。
- `commons-lang3` 在 `aicode/build.gradle` 中被声明了**两次**（3.4 与 3.18.0），**不要**顺手清理 —— 旧版 API 表面可能被代码依赖；若要清理请走专门的 OpenSpec 变更。
- 技能关键字 `aicode` 与项目名 `aicode` **同名**，回复前先确认用户问的是哪个。
- 模板仓库与 `aicode_template` 仓的兼容性由模板侧负责，不要把模板代码提交到本仓库。
- JWT 默认 60 分钟过期，密钥硬编码在 `JwtToken` 中；当前没有刷新 token 机制，**安全审计要清楚这一点**。

---

## 11. 后续演进思考（未来方向 / 技术债 / 路线）

> 本节是面向后续会话的"前瞻性"提示，不是承诺，仅用于在新会话中保持方向一致。

### 11.1 安全加固（高优先级）

- **密码存储**：当前为 MD5（见 `openspec/specs/auth-account/spec.md`）。应升级为 **BCrypt** 或 **Argon2**，并提供平滑迁移方案。
- **鉴权方式**：JWT 走 URL query 参数会被记录在代理日志、浏览器历史中。应迁移到 **`Authorization: Bearer`** Header。
- **多用户**：当前单用户模式不能满足企业场景，需引入用户-角色-权限（RBAC）模型。
- **H2 控制台暴露**：`/h2` 默认对外可访问，应通过 profile 或配置项在生产环境关闭（详见 §6.13）。
- **密钥管理**：`JwtToken` 的密钥硬编码，应迁移到外部密钥管理（Vault / KMS / 环境变量）。

### 11.2 架构演进

- **前后端分离**：当前前端以 Angular 构建产物形式放在 `aicode/src/main/resources/static/`。后续可考虑独立前端仓库 + 独立 CI，前端走 nginx 静态托管。
- **服务拆分**：单体应用可拆为「元数据服务 / 模板服务 / 代码生成服务 / 任务调度服务」四个微服务，对应 K8s 部署。
- **持久化升级**：H2 适合单机开发与演示，生产场景应切到 MySQL / PostgreSQL；schema 兼容 MySQL 模式但需要严格回归。
- **缓存层**：Druid 已配置，可考虑引入 Redis 缓存「模板元数据 / 项目配置 / 解析结果」。

### 11.3 测试演进

- 当前只有 `SmokeTest` 占位。**已建立** OpenSpec 业务测试变更模板（`2026-06-22-add-core-business-tests`），后续按包逐步补齐（详见 §7.10）：
  - Controller 层：`MockMvc` 覆盖主要接口
  - Service 层：Mockito 单测，验证业务分支
  - Mapper 层：`@SpringBootTest` + H2 真实 SQL 验证
  - 模板渲染层：freemarker / beetl 渲染结果快照测试
- 性能基线：把 `measurement/startup-time.sh` 的输出接入 CI，**冷启动回归超过阈值**则 fail。

### 11.4 CI / CD

- 当前**没有 CI**。建议接入 GitHub Actions / GitLab CI，至少包含：
  - `./gradlew clean build` 全量构建
  - `./gradlew test` 全量测试
  - `measurement/startup-time.sh` 性能基线
  - `bootBuildImage` 镜像构建 + 漏洞扫描（Trivy）
- 镜像发布：默认 `publish=false`，发布到 Harbor 走 `HARBOR_*` 环境变量。

### 11.5 可观测性

- 已有 actuator + Prometheus 指标（`micrometer-registry-prometheus`）。
- 后续可接入 ELK / Loki 做日志聚合，引入 OpenTelemetry 做链路追踪。
- WebSocket 构建日志（README 提及）目前**未结构化**，应统一接入 traceId。

### 11.6 模板管理

- 模板仓库在 `https://gitee.com/helixin/aicode_template`，由社区维护。
- 后续可支持：**模板版本锁定**（`ref` / `tag`）、**灰度发布**（先在测试项目验证）、**私有仓库认证**（token 加密存储）。
- 模板与生成工程之间应建立**契约测试**，避免模板升级直接破坏生成代码（见 §7.7）。

### 11.7 API 治理

- `api-deprecation-policy` 流程已建立，所有 `@Deprecated` 端点必须走 30 天观察期。
- 新增 API 需在 `openspec/specs/<能力>/spec.md` 中先有 Scenario，**先规范后实现**。
- 对外契约：Swagger / Knife4j UI（`springdoc-openapi-starter-webmvc-ui`）已暴露，**保持接口文档与代码同步**。

### 11.8 国际化

- 前端当前中文硬编码，**长期**需要支持中 / 英 i18n。
- 错误码（`R.success` / `R.failed` 的 `code` + `msg`）当前是中文，应抽离到 i18n 资源文件。
- 提示文案、操作日志应与 i18n 体系打通。

### 11.9 数据库迁移策略

- `sql.init.mode=always` 适用于开发与 demo，**生产必须切到 `embedded` 或 `never`** + Flyway / Liquibase 显式管理 schema 版本。
- 已有 `sql/ai_code_init.sql`（全量初始化）与 `sql/frameworks.sql`（模板元数据），未来可作为 Flyway baseline。

### 11.10 升级路径

- Spring Boot 3.3.9 → 后续 minor 升级注意 Jakarta EE 10 兼容性。
- Java 21 LTS → 关注 25 LTS 升级窗口。
- H2 1.4.200 锁死 → 需找 Spring Boot 3 兼容窗口再升（console 路由是核心阻塞点）。
- MyBatis-Plus 3.5.7 → 3.5.x 新版；评估是否升级到 4.x。

---

## 12. 变更与协作流程

1. **新会话开场**：先读本文件 + `docs/standards/*.md` 三份基线 + 走 OpenSpec 流程；非平凡变更**必须**用 `openspec-propose` 技能。
2. **代码改动前**：搜索 `openspec/specs/` 看是否已有相关能力；若有则建 delta spec；若无则**先**建立新能力 spec。
3. **本地验证**：先跑 `measurement/startup-time.sh` 与 `GET /actuator/health/liveness`，再走 `bootRun`。
4. **提交流程**：变更归档（`openspec-archive-change`）后再考虑合并；**没有归档的变更不算完成**。
5. **沟通渠道**：本仓库内一切对话、回复、PR 描述、commit message（推荐中文或中英混排）**均使用中文**。
6. **规范同步**：本文件 §5 / §6 / §7 是 `docs/standards/*.md` 的摘要，新增条目或原则冲突时**先**改原文，再决定是否同步回 AGENTS.md。

---

## 13. 一句话总结

> 本仓库是 Spring Boot 21 单体代码生成平台，三模块（`common` / `facade` / `aicode`），走 OpenSpec 规范驱动变更；三层映射 + 数据驱动模板生成主链路，H2 + VTH 跑默认 runtime，Paketo CNB 打镜像，K8s 部署；架构 / 编码 / 测试三套基线规范以 `docs/standards/*.md` 为准，本 AGENTS.md 是其**全局要求要点摘要**；安全、CI、可观测性、模板治理均有明确的演进方向；**沟通一律中文**。
