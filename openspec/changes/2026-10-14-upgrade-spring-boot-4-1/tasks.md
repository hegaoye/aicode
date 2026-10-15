# Tasks

> 每个子步骤结束时 SHALL 独立执行「验收」小节的检查，通过后再进入下一步。任一步骤失败即停止并回滚该步骤，SHALL NOT 累积多个失败点一起排查。

## 4a starter 与模块化依赖

- [ ] 4a.1 `aicode/build.gradle`：`spring-boot-starter-web` → `spring-boot-starter-webmvc`
- [ ] 4a.2 `aicode/build.gradle`：`spring-boot-starter-aop` → `spring-boot-starter-aspectj`（4.1.1 中前者返回 404）
- [ ] 4a.3 `aicode/build.gradle`：新增 `implementation 'org.springframework.boot:spring-boot-h2console'`（无对应 starter，直接用模块）
- [ ] 4a.4 确认 `spring-boot-micrometer-metrics` 在 classpath 上（`management.metrics.*` 属性迁至该模块）
- [ ] 4a.5 `aicode/build.gradle`：`mybatis-plus-spring-boot3-starter:3.5.7` → `mybatis-plus-spring-boot4-starter:3.5.17`
- [ ] 4a.6 `aicode/build.gradle`：`druid-spring-boot-starter:1.2.22` → `druid-spring-boot-4-starter:1.2.28`
- [ ] 4a.7 `aicode` 与 `facade` 两处：`springdoc-openapi-starter-webmvc-ui` `2.8.17` → `3.1.1`
- [ ] 4a.8 执行 `./aicode/gradlew :aicode:dependencies --configuration runtimeClasspath`，确认无 404 / 解析失败项
- [ ] 4a.9 **检查点**：执行 `:aicode:compileJava`，记录此时的编译错误清单（应为空，错误将在 4b 出现）

## 4b 包迁移

- [ ] 4b.1 `DataSourceConfig`：`org.springframework.boot.autoconfigure.jdbc.DataSourceProperties` → `org.springframework.boot.jdbc.autoconfigure.DataSourceProperties`
- [ ] 4b.2 `ApplicationClosedEventListener`：`org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer` → `org.springframework.boot.tomcat.TomcatConnectorCustomizer`
- [ ] 4b.3 `MybatisPlusConfigTidb`（及 `MybatisPlusConfigUid` 若仍存在）：`com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean` → `com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean`
- [ ] 4b.4 执行 `./aicode/gradlew :aicode:compileJava`，确认生产代码编译通过
- [ ] 4b.5 执行 `./aicode/gradlew :aicode:compileTestJava`，确认测试代码编译通过
- [ ] 4b.6 **检查点**：执行 `:aicode:test`，此时允许出现运行时失败，但 SHALL NOT 出现编译失败

## 4c 配置属性迁移

- [ ] 4c.1 临时添加 `runtimeOnly 'org.springframework.boot:spring-boot-properties-migrator'` 到 `aicode/build.gradle`
- [ ] 4c.2 启动应用，完整记录 migrator 输出的全部失效属性诊断
- [ ] 4c.3 `application.yml`：`management.endpoint.env.enabled: true` → `management.endpoint.env.access: unrestricted`（或按安全要求收敛）
- [ ] 4c.4 `application.yml`：`management.endpoint.shutdown.enabled: true` → `management.endpoint.shutdown.access: unrestricted`
- [ ] 4c.5 `application.yml`：`management.endpoints.health.show-details: always` → `management.endpoint.health.show-details: always`
- [ ] 4c.6 复查 `application.yml` 全部 36 个配置键，确认无遗留失效属性
- [ ] 4c.7 **移除** `spring-boot-properties-migrator` 依赖（SHALL NOT 遗留在最终产物中）
- [ ] 4c.8 **检查点**：启动应用，确认 `/actuator/env` 与 `/actuator/health` 均按新配置生效

## 4d H2 升级

- [ ] 4d.1 删除 `aicode/build.gradle` 中的 `com.h2database:h2:1.4.200` 显式声明（交给 BOM 管理 2.4.240）
- [ ] 4d.2 `application.yml`：`spring.datasource.tidb.url` 删除 `DB_CLOSE_ON_EXIT=FALSE`（H2 2.x 拒绝与 `AUTO_SERVER` 共存）
- [ ] 4d.3 `application.yml`：`spring.datasource.uid.url` 若仍存在则同样处理
- [ ] 4d.4 删除本地存量库文件 `/tmp/aicode.mv.db`（若存在）
- [ ] 4d.5 启动应用，确认 `schema.sql` 与 `data.sql` 在 H2 2.4.240 下原样执行成功，无 SQL 语法错误
- [ ] 4d.6 访问 `http://127.0.0.1:8080/h2`，确认 H2 控制台可用（Boot 4 的独立模块）
- [ ] 4d.7 **检查点**：在变更说明中显式记录「升级后必须删除 `/tmp/aicode.mv.db`」，SHALL NOT 让使用者自行猜测

## 4e Jackson 3 迁移（最关键）

- [ ] 4e.1 删除 `WebDataConvertConfig` 中基于 `Jackson2ObjectMapperBuilder` 的 `ObjectMapper` bean
- [ ] 4e.2 新增注册 `org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer` 的 Bean
- [ ] 4e.3 在该 Bean 中用 Jackson 3 的 `SimpleModule` + `ToStringSerializer` 注册 `Long.class`
- [ ] 4e.4 **同时注册 `Long.TYPE`**（`long` 基本类型），仅注册 `Long.class` 会漏掉基本类型字段
- [ ] 4e.5 执行契约测试场景①，确认 19 位 ID 仍以 JSON 字符串返回且逐字符未被截断
- [ ] 4e.6 执行契约测试场景④，确认日期仍为 `yyyy-MM-dd HH:mm:ss` + GMT+8
- [ ] 4e.7 执行契约测试场景⑤，确认 null 字段未被省略
- [ ] 4e.8 确认 `WebDataConvertConfig` 中不再 import 任何 `com.fasterxml.jackson.*` 类型
- [ ] 4e.9 **检查点**：**这是本变更最重要的验收点**，SHALL 单独确认通过后才可继续

## 4f `.shtml` 后缀兼容层

- [ ] 4f.1 在 `ContextConfiguration.configurePathMatch` 中删除 `setUseSuffixPatternMatch(true)` 与 `setUseTrailingSlashMatch(true)`（SF 7 已移除）
- [ ] 4f.2 若删除后 `configurePathMatch` 方法体为空，删除该空方法
- [ ] 4f.3 新增 `ShtmlSuffixRewriteFilter`（`OncePerRequestFilter`），order 设为早于 `DispatcherServlet`
- [ ] 4f.4 Filter 只对已注册 API 前缀生效：`/project`、`/framework`、`/displayAttribute`、`/logs`、`/login`、`/account`
- [ ] 4f.5 对不在上述前缀内、且以 `.shtml` 结尾的请求（尤其静态资源），SHALL NOT 改写路径
- [ ] 4f.6 为 Filter 的路径重写逻辑编写独立单元测试（不启动 Spring 容器），覆盖：带后缀、无后缀、前缀外、静态资源四类输入
- [ ] 4f.7 执行契约测试场景②，确认 45 个端点 × 2 条路径全部可达且响应一致
- [ ] 4f.8 执行 `LoginCtrlShtmlPathTest`，确认 `/login/signin.shtml` 仍返回 `0000` 而非 `9007`
- [ ] 4f.9 验证 WebSocket `ws://<host>/websocket.shtml` 仍可连接（该项不经过 Spring MVC filter 链，需单独确认）
- [ ] 4f.10 **检查点**：启动应用后用真实前端页面手工验证登录与项目列表两个核心流程

## 4g 移除 Knife4j

- [ ] 4g.1 删除 `com.github.xiaoymin:knife4j-springdoc-ui:3.0.3` 依赖
- [ ] 4g.2 `ContextConfiguration.addResourceHandlers`：删除 `doc.html` 与 `/webjars/**` 两项映射
- [ ] 4g.3 `LoginInterceptor` 的 `excludePathPatterns`：删除 `/doc.html`、`/webjars/**`、`/swagger-resources/**` 中仅服务于 Knife4j 的条目（保留 springdoc 仍在用的）
- [ ] 4g.4 访问 `/swagger-ui.html`，确认 springdoc 3.1.1 自带的 UI 正常展示接口列表
- [ ] 4g.5 访问 `/v3/api-docs`，确认返回 200
- [ ] 4g.6 全仓 grep 确认 `knife4j` / `doc.html` 零残留

## 4h 清理显式版本，交回 BOM

- [ ] 4h.1 删除 `io.micrometer:micrometer-registry-prometheus:1.12.5` 的显式版本（BOM 管理 1.17.1）
- [ ] 4h.2 删除 `org.junit.jupiter:junit-jupiter` 的显式版本（BOM 管理 6.0.3）
- [ ] 4h.3 删除 `org.springframework.boot:spring-boot-starter-freemarker:2.1.2.RELEASE` 的显式版本（BOM 管理 2.3.34）
- [ ] 4h.4 执行 `./aicode/gradlew dependencyInsight` 逐项确认最终解析版本符合预期
- [ ] 4h.5 确认 FreeMarker 2.3.34 下模板渲染结果不变（`CodeGenPipelineTest` + 生成主链路冒烟）

## 验收

- [ ] A.1 执行 `./aicode/gradlew test`，全部测试 SHALL 通过
- [ ] A.2 执行 `./aicode/gradlew :aicode:test --tests "com.aicode.contract.*"`，契约测试 SHALL 全绿
- [ ] A.3 启动应用，确认 `GET /actuator/health/liveness` 在 3 秒内返回 `UP`
- [ ] A.4 执行生成主链路端到端（新建项目 → 导入 SQL → init → 构建），确认产物结构与内容与升级前一致
- [ ] A.5 用真实前端手工验证：登录、项目列表、建项目、构建日志实时推送
- [ ] A.6 确认构建日志与 WebSocket 推送均携带链路标识，虚拟线程语义未变
- [ ] A.7 执行 `./aicode/gradlew :aicode:bootBuildImage` 构建镜像并启动，确认存活探针正常
- [ ] A.8 用 `measurement/startup-time.sh` 与 `metaspace-after-up.sh` 采集升级后基线，确认可与升级前基线逐字段比较
- [ ] A.9 确认 `spring-boot-properties-migrator` 已从依赖中移除
- [ ] A.10 确认 `openspec validate 2026-10-14-upgrade-spring-boot-4-1 --strict` 通过
