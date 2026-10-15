# Proposal

## Why

本变更把 aicode 平台从 Spring Boot 3.5.16 推进到 **4.1.1**。这是整个升级系列中唯一的「大爆炸点」，跨越了三大断裂面：

| 断裂面 | 具体变化 | 破坏形式 |
|--------|---------|---------|
| **Jakarta EE 11** | Servlet 6.1 基线、Tomcat 11.0.24 | 依赖层 |
| **模块化拆包** | `spring-boot-autoconfigure` 拆成数十个模块，starter 体系重命名 | 编译失败 |
| **Jackson 3** | 默认序列化库从 Jackson 2 切换到 `tools.jackson` | **静默行为回归** |

前两个断裂面会响亮地失败（编译不过），第三个不会。`WebDataConvertConfig` 自建的 Jackson 2 `ObjectMapper` bean 注册了 `Long → ToStringSerializer`，在 Boot 4 下**不再被 HTTP 层使用**，而编译与启动都正常——所有 19 位业务 ID 会被输出为 JSON 数字，JavaScript 静默截断成 `1882812765213425600`。

第四个断裂面同样静默：Spring Framework 7 移除了 `PathMatchConfigurer.setUseSuffixPatternMatch`，而前端 bundle 的全局请求过滤器**无条件给每个 URL 拼 `.shtml`**——后缀匹配一旦消失，前端 100% 不可用，且报错只是 404，不会提示「你删错了配置」。

本变更在 `api-contract` 能力的守护下推进：所有改造的验收标准不是「能启动」，而是**契约测试全绿**。

## What Changes

### 4a starter 与模块化

- `spring-boot-starter-web` → `spring-boot-starter-webmvc`（旧名已废弃）
- `spring-boot-starter-aop` → `spring-boot-starter-aspectj`（**`spring-boot-starter-aop:4.1.1` 返回 404**）
- 新增 `org.springframework.boot:spring-boot-h2console`（H2 控制台拆为独立模块，无对应 starter）
- 确认 `spring-boot-micrometer-metrics` 在 classpath 上（`management.metrics.*` 配置迁至该模块）
- MyBatis-Plus `mybatis-plus-spring-boot3-starter:3.5.7` → `mybatis-plus-spring-boot4-starter:3.5.17`
- Druid `druid-spring-boot-starter:1.2.22` → `druid-spring-boot-4-starter:1.2.28`
- springdoc-openapi `2.8.17` → `3.1.1`
- 临时引入 `spring-boot-properties-migrator` 用于属性迁移诊断，改造完成后移除

### 4b 包迁移（编译失败项）

- `org.springframework.boot.autoconfigure.jdbc.DataSourceProperties` → `org.springframework.boot.jdbc.autoconfigure.DataSourceProperties`
- `org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer` → `org.springframework.boot.tomcat.TomcatConnectorCustomizer`
- `com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean` → `com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean`（3.5.17 中已从 extension 模块移除）
- `Jackson2ObjectMapperBuilder` → Jackson 3 的 `JsonMapperBuilderCustomizer`（原类在 SF 7 中已 `@Deprecated`）

### 4c 配置属性迁移

- `management.endpoint.env.enabled` → `management.endpoint.env.access`
- `management.endpoint.shutdown.enabled` → `management.endpoint.shutdown.access`
- `management.endpoints.health.show-details` → `management.endpoint.health.show-details`

### 4d H2 升级

- H2 `1.4.200` → `2.4.240`（Boot BOM 管理；Boot 4 的 H2 控制台模块要求 2.x）
- **BREAKING**：JDBC URL 必须删除 `DB_CLOSE_ON_EXIT=FALSE`——H2 2.x 明确拒绝 `AUTO_SERVER=TRUE && DB_CLOSE_ON_EXIT=FALSE` 组合（实测报错 `Feature not supported`）
- **BREAKING**：存量 `/tmp/aicode.mv.db` 为 1.4.200 格式，无法被 2.x 打开，必须删除重建
- 已实证：`schema.sql`（346 行）与 `data.sql`（153 行）在 H2 2.4.240 + `MODE=MYSQL;DATABASE_TO_UPPER=false` 下**原样执行成功**，无需改动

### 4e Jackson 3 迁移（最关键）

- 删除 `WebDataConvertConfig` 中的 Jackson 2 `ObjectMapper` bean
- 改为注册 `org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer`，用 Jackson 3 的 `SimpleModule` 注册 `Long` / `Long.TYPE` → `ToStringSerializer`
- 验证 `spring.jackson.date-format`（`yyyy-MM-dd HH:mm:ss`）与 `time-zone`（GMT+8）在 Jackson 3 下仍按原语义生效

### 4f `.shtml` 后缀兼容层

- 在 `ContextConfiguration.configurePathMatch` 中删除 `setUseSuffixPatternMatch(true)` 与 `setUseTrailingSlashMatch(true)`（SF 7 已移除这两个 API）
- 新增 `ShtmlSuffixRewriteFilter`：对已注册的 API 前缀，把以 `.shtml` 结尾的请求路径重写为无后缀路径后再交给 DispatcherServlet
- 保留 WebSocket 端点 `/websocket.shtml` 的可达性

### 4g 移除 Knife4j

- 删除 `com.github.xiaoymin:knife4j-springdoc-ui:3.0.3`（2021 年停更，面向 Boot 2）
- 保留 springdoc 3.1.1 自带的 Swagger UI（`/swagger-ui.html`）
- 清理 `ContextConfiguration` 中的 `doc.html` 与 `/webjars/**` 资源映射，以及 `LoginInterceptor` 排除列表中的对应条目

### 4h 清理显式版本，交回 BOM

- 删除 `io.micrometer:micrometer-registry-prometheus:1.12.5` 的显式版本（BOM 管理 1.17.1）
- 删除 `org.junit.jupiter:junit-jupiter` 的显式版本（BOM 管理 6.0.3）
- 删除 `org.springframework.boot:spring-boot-starter-freemarker:2.1.2.RELEASE` 的显式版本（BOM 管理 2.3.34）

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `api-contract`: Jackson 3 迁移后标识符仍 SHALL 序列化为字符串；`.shtml` 后缀兼容 SHALL 由服务端兼容层提供而非依赖框架默认行为；日期格式与 null 字段保留语义 SHALL 在新序列化库下保持不变。
- `runtime-platform`: 运行基线 SHALL 升级为 Jakarta EE 11 / Spring Framework 7 / H2 2.4.240；Actuator 配置 SHALL 使用 Boot 4 的属性命名；API 文档 SHALL 由 springdoc 独立提供，SHALL NOT 依赖已停止维护的第三方 UI 组件。

> **归档顺序前置条件**：本变更修改的两个能力均由前序变更创建——`api-contract` 来自 `2026-10-14-add-api-contract-tests`，`runtime-platform` 来自 `2026-10-14-upgrade-gradle-9-jdk-25`。两者 SHALL 先归档，本变更才能归档。本变更的执行顺序为**整个系列的倒数第二步**，仅在 `2026-10-14-replace-baidu-uid-generator` 归档后进行（后者会改变 `id` 的取值形态，需先稳定）。

## Impact

- **修改文件**：`aicode/build.gradle`、`aicode/src/main/java/com/aicode/config/{WebDataConvertConfig,DataSourceConfig,MybatisPlusConfigTidb,ApplicationClosedEventListener,SpringDocConfig,ContextConfiguration(位于 filter 包)}.java`、`aicode/src/main/java/com/aicode/filter/ContextConfiguration.java`、新增 `ShtmlSuffixRewriteFilter`、`aicode/src/main/resources/application.yml`
- **依赖变更**：见 4a / 4h
- **数据影响**：H2 升级导致存量库文件不可用，须删除重建（`/tmp/aicode.mv.db`）
- **不影响**：代码生成产物的 Spring Boot 版本（由外部模板仓库 `aicode_template` 决定，与本平台解耦）
- **验收命令**：`./aicode/gradlew test` + `./aicode/gradlew :aicode:test --tests "com.aicode.contract.*"` + 生成主链路端到端冒烟
