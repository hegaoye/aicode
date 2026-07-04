# AGENTS.md

> 本文件是面向后续 OpenCode 协作会话的项目说明文档。所有内容均以中文沟通（代码、命令、路径、URL、包名等技术标识符保留英文原文）。
> 适用范围：项目内一切协作、提问、变更建议、提交信息、文档撰写。

## 0. 沟通规范

- **语言**：项目相关沟通（对话、文档、提交信息、变更说明、PR 描述）一律使用**简体中文**。
- **保留项**：代码标识符（包名、类名、变量名、URL、命令、配置键、错误码）保持英文原文不动。
- **代码注释**：源代码中的中文注释为项目原有约定，**保留不动**；新增注释同样使用中文。
- **术语**：通用技术术语（如 Spring Boot、Gradle、JWT、CI）保留英文；业务术语用中文（如"代码生成"、"模板仓库"、"构建项目"）。
- **回应语言**：默认使用中文回复；用户切换为英文时跟随切换。

---

## 1. 项目概述

`aicode` 是一个基于 **Spring Boot 3.3.9 / Java 21** 的**代码生成平台**：接收 SQL 脚本与 freemarker/beetl 模板，逆向生成完整的 CRUD 工程（DAO → Service → Ctrl → 前端），并可推送到 Git / SVN 仓库。

同时，本仓库还承载一个**对话式 OpenCode 技能**（`.opencode/skills/aicode/`，触发关键词 `aicode` / "创建Java项目" / "代码生成"），该技能通过 HTTP 与本地运行的应用交互。**两者职责不同**：本仓库是服务端本身，而该技能是面向用户对话的"使用入口"。

## 2. 模块结构（Gradle 多模块）

根 `settings.gradle` 声明三个子项目；其他目录（`sql/`、`devops/`、`measurement/`、`docs/`、`openspec/`、`screenshots/`）均为松散内容，**不是** Gradle 模块。

| 模块 | 角色 | 备注 |
|------|------|------|
| `:aicode`  | Spring Boot 应用 — 控制器、服务、Mapper、静态前端 | **唯一可运行且有测试的模块**。入口 `com.aicode.Application`。 |
| `:facade`  | 与下游生成工程共享的 DTO / facade 类型 | 依赖 `:common`。无 main，无测试。 |
| `:common` | 共享工具：JGit、hutool、JWT、pinyin4j、zip4j | 被 `:facade` 与 `:aicode` 同时依赖。 |

Gradle Wrapper 位于 `./aicode/gradlew`（仓库根目录**没有** wrapper），所有 Gradle 命令必须走这个嵌套 wrapper。

## 3. 构建 / 开发命令

要求 **Java 21**（Dockerfile 锁定 amazoncorretto:21.0.11-alpine3.23）。

```
./aicode/gradlew :aicode:bootRun        # 启动应用，:8080（业务）/ :8088（actuator）
./aicode/gradlew test                    # 运行所有模块的 JUnit 5 测试
./aicode/gradlew :aicode:bootBuildImage  # Paketo CNB 构建镜像（默认 publish=false）
./aicode/gradlew build                   # 完整构建
```

镜像默认仅产出到本地。推送到 Harbor：设置 `HARBOR_USER` / `HARBOR_PASS` / `HARBOR_URL` 环境变量，并启用 `aicode/build.gradle` 中 `bootBuildImage { ... }` 内被注释的 `publish = true` 块。

## 4. 运行时事实

- 应用端口：`http://127.0.0.1:8080`；actuator：`http://127.0.0.1:8088`；H2 控制台：`http://127.0.0.1:8080/h2`（sa/sa）。
- 默认账户：`admin / 888888`。**单用户模式**，token 通过 URL 参数 `?token=xxx` 传递（**无 Header 鉴权**）。
- K8s 与 aicode 技能共用的存活探针：`GET /actuator/health/liveness`，技能要求 **3 秒内**返回 `UP`。
- 数据库为 **H2（MySQL 兼容模式）**：`jdbc:h2:file:/tmp/aicode;...MODE=MYSQL;AUTO_SERVER=TRUE`。`spring.sql.init.mode=always` 会在每次启动重跑 `classpath:db/schema.sql` 与 `data.sql` —— 状态会被重置，除非用 `database` 环境变量指向其他文件路径。
- 存在**两个 DataSource Bean**（`tidb-data` 与 `uid-data`），默认指向同一 H2 文件。**不要**"顺手"去重，**需先理解下游生成工程的依赖**。
- Spring 虚拟线程已启用（`spring.threads.virtual.enabled: true`），测试与阻塞 JDBC 调用需按 VTH 假设编写。
- H2 版本**锁死 1.4.200**（新版会破坏 Spring Boot 3 的 H2 console 路由）。
- `aicode/libs/*.jar` 是 vendored jar，由 `fileTree(dir: 'libs')` 引入；**不要**用 Maven 重新解析这些依赖。

## 5. 代码规范

- 主代码中**中文注释是常态**，保持原样，不要翻译或删减。
- 全量使用 Lombok（`compileOnly` + `annotationProcessor`，均为 `1.18.32`），生成代码保持 `@Data` / `@Builder` 风格。
- 持久层使用 **MyBatis-Plus 3.5.7 + Druid**，**不使用** Spring Data JPA。Mapper XML 位于 `aicode/src/main/resources/mapper/`。
- 模板引擎：freemarker 为主，beetl 为辅。模板**不在本仓库**，用户点击"构建项目"时从 `https://gitee.com/helixin/aicode_template` 拉取。
- 包结构约定：控制器 `com.aicode.<模块>.ctrl`，服务 `.service`，Mapper `.dao`。新增模块请遵循该嵌套（参考 `com.aicode.project`）。
- 登录入口兼容 `.shtml` 后缀（`GET /login/signin.shtml`），`LoginInterceptor` 必须放行（见 `openspec/specs/auth-account/spec.md`）。
- 构建产物与 `LOG_HOME_IS_UNDEFINED/` 目录是预期的、被 `.gitignore` 忽略的；**不要**尝试清理。

## 6. OpenSpec 工作流

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

## 7. 测试

当前唯一测试是 `aicode/src/test/java/com/aicode/SmokeTest.java`（JUnit 5 占位）。正式业务测试**必须**通过 OpenSpec 变更发起 —— 参考已归档的 `openspec/changes/archive/2026-06-22-add-core-business-tests/`，按包组织测试（如 `aicode/src/test/java/com/aicode/map/...`）。

aicode 技能侧的健康冒烟探针：`GET /actuator/health/liveness` 返回 `{"status":"UP"}` —— 提交基础设施类变更前**先**用它验证运行态。

## 8. 容器 / 部署

- Dockerfile：`aicode/Dockerfile`，基础镜像 `amazoncorretto:21.0.11-alpine3.23`，端口 8080，`JAVA_OPTS="-Xms128m -Xmx512m"`。
- K8s 清单与 nginx upstream 示例：`devops/k8s/`、`README.md` §本地部署 nginx。
- 性能基线脚本：`measurement/startup-time.sh`（冷启动到 `/actuator/health/liveness` 200 的 wall-clock）、`measurement/metaspace-after-up.sh`（Metaspace 用量）—— 输出 JSON，便于 CI 比对。
- dockerhub 镜像（**遗留**，仅供外部教程使用）：`hegaoye/aicode:20210613`。**生产构建请使用 `bootBuildImage`**，不要用此 tag。

## 9. 容易踩坑的点

- 编辑**根** `build.gradle` **无效** —— 文件为空。所有 Gradle 配置都在各子项目的 `build.gradle` 里。
- `LOG_HOME_IS_UNDEFINED/` 目录会出现在 `aicode/` 与仓库根下 —— 这是 logback 在 `LOG_HOME` 环境变量未设置时的日志输出，属预期行为且被 `.gitignore` 忽略。
- `commons-lang3` 在 `aicode/build.gradle` 中被声明了**两次**（3.4 与 3.18.0），**不要**顺手清理 —— 旧版 API 表面可能被代码依赖；若要清理请走专门的 OpenSpec 变更。
- 技能关键字 `aicode` 与项目名 `aicode` **同名**，回复前先确认用户问的是哪个。
- 模板仓库与 `aicode_template` 仓的兼容性由模板侧负责，不要把模板代码提交到本仓库。
- JWT 默认 60 分钟过期，密钥硬编码在 `JwtToken` 中；当前没有刷新 token 机制，**安全审计要清楚这一点**。

---

## 10. 后续演进思考（未来方向 / 技术债 / 路线）

> 本节是面向后续会话的"前瞻性"提示，不是承诺，仅用于在新会话中保持方向一致。

### 10.1 安全加固（高优先级）

- **密码存储**：当前为 MD5（见 `openspec/specs/auth-account/spec.md`）。应升级为 **BCrypt** 或 **Argon2**，并提供平滑迁移方案。
- **鉴权方式**：JWT 走 URL query 参数会被记录在代理日志、浏览器历史中。应迁移到 **`Authorization: Bearer`** Header。
- **多用户**：当前单用户模式不能满足企业场景，需引入用户-角色-权限（RBAC）模型。
- **H2 控制台暴露**：`/h2` 默认对外可访问，应通过 profile 或配置项在生产环境关闭。
- **密钥管理**：`JwtToken` 的密钥硬编码，应迁移到外部密钥管理（Vault / KMS / 环境变量）。

### 10.2 架构演进

- **前后端分离**：当前前端以 Angular 构建产物形式放在 `aicode/src/main/resources/static/`。后续可考虑独立前端仓库 + 独立 CI，前端走 nginx 静态托管。
- **服务拆分**：单体应用可拆为"元数据服务 / 模板服务 / 代码生成服务 / 任务调度服务"四个微服务，对应 K8s 部署。
- **持久化升级**：H2 适合单机开发与演示，生产场景应切到 MySQL / PostgreSQL；schema 兼容 MySQL 模式但需要严格回归。
- **缓存层**：Druid 已配置，可考虑引入 Redis 缓存"模板元数据 / 项目配置 / 解析结果"。

### 10.3 测试演进

- 当前只有 `SmokeTest` 占位。**已建立** OpenSpec 业务测试变更模板（`2026-06-22-add-core-business-tests`），后续按包逐步补齐：
  - Controller 层：`MockMvc` 覆盖主要接口
  - Service 层：Mockito 单测，验证业务分支
  - Mapper 层：`@SpringBootTest` + H2 真实 SQL 验证
  - 模板渲染层：freemarker / beetl 渲染结果快照测试
- 性能基线：把 `measurement/startup-time.sh` 的输出接入 CI，**冷启动回归超过阈值**则 fail。

### 10.4 CI / CD

- 当前**没有 CI**。建议接入 GitHub Actions / GitLab CI，至少包含：
  - `./gradlew clean build` 全量构建
  - `./gradlew test` 全量测试
  - `measurement/startup-time.sh` 性能基线
  - `bootBuildImage` 镜像构建 + 漏洞扫描（Trivy）
- 镜像发布：默认 `publish=false`，发布到 Harbor 走 `HARBOR_*` 环境变量。

### 10.5 可观测性

- 已有 actuator + Prometheus 指标（`micrometer-registry-prometheus`）。
- 后续可接入 ELK / Loki 做日志聚合，引入 OpenTelemetry 做链路追踪。
- WebSocket 构建日志（README 提及）目前**未结构化**，应统一接入 traceId。

### 10.6 模板管理

- 模板仓库在 `https://gitee.com/helixin/aicode_template`，由社区维护。
- 后续可支持：**模板版本锁定**（`ref` / `tag`）、**灰度发布**（先在测试项目验证）、**私有仓库认证**（token 加密存储）。
- 模板与生成工程之间应建立**契约测试**，避免模板升级直接破坏生成代码。

### 10.7 API 治理

- `api-deprecation-policy` 流程已建立，所有 `@Deprecated` 端点必须走 30 天观察期。
- 新增 API 需在 `openspec/specs/<能力>/spec.md` 中先有 Scenario，**先规范后实现**。
- 对外契约：Swagger / Knife4j UI（`springdoc-openapi-starter-webmvc-ui`）已暴露，**保持接口文档与代码同步**。

### 10.8 国际化

- 前端当前中文硬编码，**长期**需要支持中 / 英 i18n。
- 错误码（`R.success` / `R.failed` 的 `code` + `msg`）当前是中文，应抽离到 i18n 资源文件。
- 提示文案、操作日志应与 i18n 体系打通。

### 10.9 数据库迁移策略

- `sql.init.mode=always` 适用于开发与 demo，**生产必须切到 `embedded` 或 `never`** + Flyway / Liquibase 显式管理 schema 版本。
- 已有 `sql/ai_code_init.sql`（全量初始化）与 `sql/frameworks.sql`（模板元数据），未来可作为 Flyway baseline。

### 10.10 升级路径

- Spring Boot 3.3.9 → 后续 minor 升级注意 Jakarta EE 10 兼容性。
- Java 21 LTS → 关注 25 LTS 升级窗口。
- H2 1.4.200 锁死 → 需找 Spring Boot 3 兼容窗口再升（console 路由是核心阻塞点）。
- MyBatis-Plus 3.5.7 → 3.5.x 新版；评估是否升级到 4.x。

---

## 11. 变更与协作流程

1. **新会话开场**：先读本文件 + 走 OpenSpec 流程；非平凡变更**必须**用 `openspec-propose` 技能。
2. **代码改动前**：搜索 `openspec/specs/` 看是否已有相关能力；若有则建 delta spec；若无则**先**建立新能力 spec。
3. **本地验证**：先跑 `measurement/startup-time.sh` 与 `GET /actuator/health/liveness`，再走 `bootRun`。
4. **提交流程**：变更归档（`openspec-archive-change`）后再考虑合并；**没有归档的变更不算完成**。
5. **沟通渠道**：本仓库内一切对话、回复、PR 描述、commit message（推荐中文或中英混排）**均使用中文**。

---

## 12. 一句话总结

> 本仓库是 Spring Boot 21 单体代码生成平台，三模块（`common` / `facade` / `aicode`），走 OpenSpec 规范驱动变更，H2 + VTH 跑默认 runtime，Paketo CNB 打镜像，K8s 部署；安全、测试、CI、可观测性、模板治理均有明确的演进方向，**沟通一律中文**。
