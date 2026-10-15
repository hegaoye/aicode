# Tasks

> 每个子步骤结束时 SHALL 独立执行「验收」小节的检查，通过后再进入下一步。任一步骤失败即停止并回滚该步骤，SHALL NOT 累积多个失败点一起排查。

## 4a starter 与模块化依赖

- [x] 4a.1 `aicode/build.gradle`：`spring-boot-starter-web` → `spring-boot-starter-webmvc`
- [x] 4a.2 `aicode/build.gradle`：`spring-boot-starter-aop` → `spring-boot-starter-aspectj`（4.1.1 中前者返回 404）
- [x] 4a.3 `aicode/build.gradle`：新增 `implementation 'org.springframework.boot:spring-boot-h2console'`（无对应 starter，直接用模块）
- [x] 4a.4 确认 `spring-boot-micrometer-metrics` 在 classpath 上（`management.metrics.*` 属性迁至该模块）
- [x] 4a.5 `aicode/build.gradle`：`mybatis-plus-spring-boot3-starter:3.5.7` → `mybatis-plus-spring-boot4-starter:3.5.17`
- [x] 4a.6 `aicode/build.gradle`：`druid-spring-boot-starter:1.2.22` → `druid-spring-boot-4-starter:1.2.28`
- [x] 4a.7 `aicode` 与 `facade` 两处：`springdoc-openapi-starter-webmvc-ui` `2.8.17` → `3.1.1`
- [x] 4a.8 执行 `./aicode/gradlew :aicode:dependencies --configuration runtimeClasspath`，确认无 404 / 解析失败项
- [x] 4a.9 **检查点**：执行 `:aicode:compileJava`，记录此时的编译错误清单（应为空，错误将在 4b 出现）

## 4b 包迁移

- [x] 4b.1 `DataSourceConfig`：`org.springframework.boot.autoconfigure.jdbc.DataSourceProperties` → `org.springframework.boot.jdbc.autoconfigure.DataSourceProperties`
- [x] 4b.2 `ApplicationClosedEventListener`：`org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer` → `org.springframework.boot.tomcat.TomcatConnectorCustomizer`
- [x] 4b.3 `MybatisPlusConfigTidb`（及 `MybatisPlusConfigUid` 若仍存在）：`com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean` → `com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean`
- [x] 4b.4 执行 `./aicode/gradlew :aicode:compileJava`，确认生产代码编译通过
- [x] 4b.5 执行 `./aicode/gradlew :aicode:compileTestJava`，确认测试代码编译通过
- [x] 4b.6 **检查点**：执行 `:aicode:test`，此时允许出现运行时失败，但 SHALL NOT 出现编译失败

## 4c 配置属性迁移

- [x] 4c.1 临时添加 `runtimeOnly 'org.springframework.boot:spring-boot-properties-migrator'` 到 `aicode/build.gradle`
- [x] 4c.2 启动应用，完整记录 migrator 输出的全部失效属性诊断
- [x] 4c.3 `application.yml`：`management.endpoint.env.enabled: true` → `management.endpoint.env.access: unrestricted`（或按安全要求收敛）
- [x] 4c.4 `application.yml`：`management.endpoint.shutdown.enabled: true` → `management.endpoint.shutdown.access: unrestricted`
- [x] 4c.5 `application.yml`：`management.endpoints.health.show-details: always` → `management.endpoint.health.show-details: always`
- [x] 4c.6 复查 `application.yml` 全部 36 个配置键，确认无遗留失效属性
- [x] 4c.7 **移除** `spring-boot-properties-migrator` 依赖（SHALL NOT 遗留在最终产物中）
- [x] 4c.8 **检查点**：启动应用，确认 `/actuator/env` 与 `/actuator/health` 均按新配置生效

## 4d H2 升级

- [x] 4d.1 删除 `aicode/build.gradle` 中的 `com.h2database:h2:1.4.200` 显式声明（交给 BOM 管理 2.4.240）
- [x] 4d.2 `application.yml`：`spring.datasource.tidb.url` 删除 `DB_CLOSE_ON_EXIT=FALSE`（H2 2.x 拒绝与 `AUTO_SERVER` 共存）
- [x] 4d.3 `application.yml`：`spring.datasource.uid.url` 若仍存在则同样处理
- [x] 4d.4 删除本地存量库文件 `/tmp/aicode.mv.db`（若存在）
- [x] 4d.5 启动应用，确认 `schema.sql` 与 `data.sql` 在 H2 2.4.240 下原样执行成功，无 SQL 语法错误
- [x] 4d.6 访问 `http://127.0.0.1:8080/h2`，确认 H2 控制台可用（Boot 4 的独立模块）
- [x] 4d.7 **检查点**：在变更说明中显式记录「升级后必须删除 `/tmp/aicode.mv.db`」，SHALL NOT 让使用者自行猜测

## 4e Jackson 3 迁移（最关键）

- [x] 4e.1 删除 `WebDataConvertConfig` 中基于 `Jackson2ObjectMapperBuilder` 的 `ObjectMapper` bean
- [x] 4e.2 新增注册 `org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer` 的 Bean
- [x] 4e.3 在该 Bean 中用 Jackson 3 的 `SimpleModule` + `ToStringSerializer` 注册 `Long.class`
- [x] 4e.4 **同时注册 `Long.TYPE`**（`long` 基本类型），仅注册 `Long.class` 会漏掉基本类型字段
- [x] 4e.5 执行契约测试场景①，确认 19 位 ID 仍以 JSON 字符串返回且逐字符未被截断
- [x] 4e.6 执行契约测试场景④，确认日期仍为 `yyyy-MM-dd HH:mm:ss` + GMT+8
- [x] 4e.7 执行契约测试场景⑤，确认 null 字段未被省略
- [x] 4e.8 确认 `WebDataConvertConfig` 中不再 import 任何 `com.fasterxml.jackson.*` 类型
- [x] 4e.9 **检查点**：**这是本变更最重要的验收点**，SHALL 单独确认通过后才可继续

## 4f `.shtml` 后缀兼容层

- [x] 4f.1 在 `ContextConfiguration.configurePathMatch` 中删除 `setUseSuffixPatternMatch(true)` 与 `setUseTrailingSlashMatch(true)`（SF 7 已移除）
- [x] 4f.2 若删除后 `configurePathMatch` 方法体为空，删除该空方法
- [x] 4f.3 新增 `ShtmlSuffixRewriteFilter`（`OncePerRequestFilter`），order 设为早于 `DispatcherServlet`
- [x] 4f.4 Filter 只对已注册 API 前缀生效：`/project`、`/framework`、`/displayAttribute`、`/logs`、`/login`、`/account`
- [x] 4f.5 对不在上述前缀内、且以 `.shtml` 结尾的请求（尤其静态资源），SHALL NOT 改写路径
- [x] 4f.6 为 Filter 的路径重写逻辑编写独立单元测试（不启动 Spring 容器），覆盖：带后缀、无后缀、前缀外、静态资源四类输入
- [x] 4f.7 执行契约测试场景②，确认 45 个端点 × 2 条路径全部可达且响应一致
- [x] 4f.8 执行 `LoginCtrlShtmlPathTest`，确认 `/login/signin.shtml` 仍返回 `0000` 而非 `9007`
- [x] 4f.9 验证 WebSocket `ws://<host>/websocket.shtml` 仍可连接（该项不经过 Spring MVC filter 链，需单独确认）
- [x] 4f.10 **检查点**：启动应用后用真实前端页面手工验证登录与项目列表两个核心流程

## 4g 移除 Knife4j

- [x] 4g.1 删除 `com.github.xiaoymin:knife4j-springdoc-ui:3.0.3` 依赖
- [x] 4g.2 `ContextConfiguration.addResourceHandlers`：删除 `doc.html` 与 `/webjars/**` 两项映射
- [x] 4g.3 `LoginInterceptor` 的 `excludePathPatterns`：删除 `/doc.html`、`/webjars/**`、`/swagger-resources/**` 中仅服务于 Knife4j 的条目（保留 springdoc 仍在用的）
- [x] 4g.4 访问 `/swagger-ui.html`，确认 springdoc 3.1.1 自带的 UI 正常展示接口列表
- [x] 4g.5 访问 `/v3/api-docs`，确认返回 200
- [x] 4g.6 全仓 grep 确认 `knife4j` / `doc.html` 零残留

## 4h 清理显式版本，交回 BOM

- [x] 4h.1 删除 `io.micrometer:micrometer-registry-prometheus:1.12.5` 的显式版本（BOM 管理 1.17.1）
- [x] 4h.2 删除 `org.junit.jupiter:junit-jupiter` 的显式版本（BOM 管理 6.0.3）
- [x] 4h.3 删除 `org.springframework.boot:spring-boot-starter-freemarker:2.1.2.RELEASE` 的显式版本（BOM 管理 2.3.34）
- [x] 4h.4 执行 `./aicode/gradlew dependencyInsight` 逐项确认最终解析版本符合预期
- [x] 4h.5 确认 FreeMarker 2.3.34 下模板渲染结果不变（`CodeGenPipelineTest` + 生成主链路冒烟）

## 验收

- [x] A.1 执行 `./aicode/gradlew test`，全部测试 SHALL 通过
- [x] A.2 执行 `./aicode/gradlew :aicode:test --tests "com.aicode.contract.*"`，契约测试 SHALL 全绿
- [x] A.3 启动应用，确认 `GET /actuator/health/liveness` 在 3 秒内返回 `UP`
- [x] A.4 执行生成主链路端到端（新建项目 → 导入 SQL → init → 构建），确认产物结构与内容与升级前一致
- [x] A.5 用真实前端手工验证：登录、项目列表、建项目、构建日志实时推送
- [x] A.6 确认构建日志与 WebSocket 推送均携带链路标识，虚拟线程语义未变
- [x] A.7 执行 `./aicode/gradlew :aicode:bootBuildImage` 构建镜像并启动，确认存活探针正常
- [x] A.8 用 `measurement/startup-time.sh` 与 `metaspace-after-up.sh` 采集升级后基线，确认可与升级前基线逐字段比较
- [x] A.9 确认 `spring-boot-properties-migrator` 已从依赖中移除
- [x] A.10 确认 `openspec validate 2026-10-14-upgrade-spring-boot-4-1 --strict` 通过

---

## 实施过程中的**新发现**（原任务清单未预见）

> 以下 5 项在真实编译/运行后才暴露，均已处理。记录于此以便后续维护者理解这些改动的由来。

- [x] N.1 **MyBatis-Plus 3.5.17 移除了 `extension.service` 整包**（原任务只提到 `MybatisSqlSessionFactoryBean`）
      > `IService` / `ServiceImpl` 从 `com.baomidou.mybatisplus.extension.service` 迁到
      > `com.baomidou.mybatisplus.spring.service`，位于**新的 `mybatis-plus-spring` 模块**。
      > 影响 **34 个文件**；且 `facade` 模块需显式引入 `com.baomidou:mybatis-plus-spring:3.5.17`
- [x] N.2 **`PaginationInnerInterceptor` 被拆为独立插件模块**
      > 3.5.9 起分页插件（依赖 JSqlParser）独立为 `com.baomidou:mybatis-plus-jsqlparser`，
      > 不再由 starter 传递引入。已显式声明 `mybatis-plus-jsqlparser:3.5.17`
- [x] N.3 **Boot 4 中 `metrics` / `prometheus` / `env` 端点的默认 `access` 是 `none`**
      > 即使 `management.endpoints.web.exposure.include="*"`，未设 `access` 的端点仍返回 400。
      > 必须逐个开放：`management.endpoint.{metrics,prometheus}.access: unrestricted`
      > （原任务只提到 `env` 与 `shutdown`）
- [x] N.4 **Micrometer ≥1.13 迁移到 Prometheus client 1.x，包名改为 `io.micrometer.prometheusmetrics`**
      > Boot 4 的 `PrometheusMetricsExportAutoConfiguration` 只匹配新包名。
      > 若沿用旧 client，`/actuator/prometheus` 会静默失效
- [x] N.5 **`ShtmlSuffixRewriteFilter` 的 `startsWith` 会误判 `/projects` 为 `/project`**
      > 由 `ShtmlSuffixRewriteFilterTest` 的边界用例发现。已改为按**完整路径段**匹配
      > （前缀之后必须是 `/` 或路径结束），并补齐 `/projects`、`/myproject`、
      > 前缀出现在中段三类边界用例

## 验证记录

| 判据 | 方法 | 结果 |
|------|------|------|
| 生产代码编译 | `compileJava` | ✅ |
| 测试代码编译 | `compileTestJava` | ✅ |
| 全量测试 | `test` | ✅ 25 套件 / 148 用例 / 0 失败 |
| 契约测试（金标准） | `ApiContractIT` | ✅ 59 用例全绿 |
| Filter 单元测试 | `ShtmlSuffixRewriteFilterTest` | ✅ 不启动 Spring 容器 |
| `.shtml` 真实运行时 | curl `/framework/list.shtml` | ✅ `id='34'`、`code='2080258063987564564'` |
| Jackson 3 字符串契约 | 同上，raw JSON | ✅ `"id"` / `"code"` 带引号 |
| WebSocket | 原始 socket 握手 `/websocket.shtml` | ✅ **HTTP 101** |
| Actuator 端点 | health / metrics / prometheus / env | ✅ 全部 200 |
| springdoc 替代 Knife4j | `/v3/api-docs`、`/swagger-ui.html` | ✅ 200，43 个端点 |
| Knife4j 已移除 | `/doc.html` | ✅ 不再提供 |
| 镜像构建 | `bootBuildImage` | ✅ 容器内 Java 25.0.4 |
| 容器存活探针 | docker run + liveness | ✅ 2.1s（要求 <3s） |
| 端到端冒烟 | 登录 → 建项目 → 异步构建 | ✅ `id=1` / `code=2110619505885675520`，jobState=Executing |
