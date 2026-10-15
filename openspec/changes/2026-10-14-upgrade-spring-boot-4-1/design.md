# Design

## Context

本变更跨越的断裂面已在前序调研中逐条实证。本设计只记录**有多个合理选项、且需要写明「为什么不用更简单的做法」**的决策；纯机械的版本替换不进入本文件（见 tasks.md）。

实证基线（全部来自对 Spring Boot 4.1.1 / Spring Framework 7.0.9 / MyBatis-Plus 3.5.17 制品的实际检查，而非文档推断）：

**编译期必炸（4 项）**

| 项 | 证据 |
|----|------|
| `PathMatchConfigurer` 无 `setUseSuffixPatternMatch` | `javap` SF 7.0.9 输出中该方法已消失 |
| `org.springframework.boot.autoconfigure.jdbc.DataSourceProperties` 不存在 | Boot 4.1.1 jar 内为 `org.springframework.boot.jdbc.autoconfigure.DataSourceProperties` |
| `org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer` 不存在 | Boot 4.1.1 jar 内为 `org.springframework.boot.tomcat.TomcatConnectorCustomizer` |
| `com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean` 不存在 | MyBatis-Plus 3.5.17 的 extension jar 中该类已移除，迁至 `mybatis-plus-spring` 模块 |

**依赖期必炸（2 项）**

| 项 | 证据 |
|----|------|
| `spring-boot-starter-aop:4.1.1` → HTTP 404 | Maven Central 实测；已重命名为 `spring-boot-starter-aspectj` |
| H2 2.x 拒绝 `AUTO_SERVER=TRUE && DB_CLOSE_ON_EXIT=FALSE` | 实跑报错 `JdbcSQLFeatureNotSupportedException: Feature not supported` |

**静默回归（2 项，本变更的主要风险）**

| 项 | 机制 |
|----|------|
| Jackson 3 取代 Jackson 2 成为默认 | `spring-boot-starter-webmvc` → `spring-boot-starter-jackson` → Jackson 3.1.5（`tools.jackson`）。自建的 Jackson 2 `ObjectMapper` bean 不再被 HTTP 层使用，`Long → ToStringSerializer` 静默失效 |
| `.shtml` 后缀匹配消失 | 前端 bundle 的全局 `ajaxPrefilter` 无条件执行 `t.url += ".shtml"`；删除配置后前端全部接口 404，报错不提示原因 |

## Goals / Non-Goals

**Goals:**

- 应用运行在 Spring Boot 4.1.1 / Jakarta EE 11 / Spring Framework 7 上
- `api-contract` 能力的全部场景保持通过
- 每个子步骤（4a–4h）可独立提交与独立验证，出问题时能精确回滚
- 移除已停止维护的第三方组件（Knife4j）

**Non-Goals:**

- 不做原生镜像（GraalVM native-image）构建——与本次升级正交
- 不引入 Spring Security——认证仍是自定义拦截器
- 不升级外部模板仓库 `aicode_template` 的 Spring Boot 版本——生成产物与本平台解耦
- 不做数据库迁移工具引入（H2 是嵌入式开发库，直接重建即可）

## Decisions

### 决策 1：`.shtml` 用 Filter 重写，而非在 45 个端点上显式声明双路径

前端 bundle 中 `c2t9` 模块的 `URL` 配置对象 + 全局请求过滤器确认了 45 个 API 端点，全部被无条件追加 `.shtml`。

| 方案 | 做法 | 评估 |
|------|------|------|
| **A（采用）Filter 重写** | `ShtmlSuffixRewriteFilter` 在 DispatcherServlet 之前把 `/x.shtml` 改写为 `/x` | ✅ 零侵入，45 个端点一行不改；✅ 纯函数式逻辑可脱离容器单测；✅ 前端完全无感 |
| B 显式双路径映射 | `@GetMapping({"/signin", "/signin.shtml"})` | ❌ 45 个端点各写两遍，共 90 处映射；后续新增端点极易遗漏，遗漏即线上 404 |
| C 覆写 `RequestMappingHandlerMapping` | 重写后缀匹配语义 | ❌ 与 Boot 4 默认的 PathPatternParser 冲突；重、脆、难测 |
| D 改前端去掉 `.shtml` | 改 Angular 代码 | ❌ 前端是打包产物，改动需重新构建整个 Angular 工程；且超出「不动前端」的约束 |

选 A。核心优势是**把一次性成本从 90 处降到 1 处**，且新增端点时无遗漏风险。

**Filter 的四个必须处理的细节**（写入 tasks）：

1. **只对已注册前缀生效**。静态资源 `*.css` / `*.js` / `*.svg` / `*.woff2` 中大量文件以点号结尾，若无差别重写会破坏 `addResourceHandler("/**")` 的解析。已注册 API 前缀：`/project`、`/framework`、`/displayAttribute`、`/logs`、`/login`、`/account`。
2. **重写发生在拦截器匹配之前**，因此 `LoginInterceptor` 的 `excludePathPatterns("/login/**")` 依然命中——已由 `LoginCtrlShtmlPathTest` 覆盖。
3. **WebSocket `/websocket.shtml` 走 Tomcat 的 JSR-356 端点映射，不经过 Spring MVC filter 链**，需单独验证可达性。
4. Filter 的 `order` SHALL 早于 `DispatcherServlet`，且 SHALL NOT 影响 `spring.mvc.static-path-pattern: /**` 的静态资源解析。

### 决策 2：Jackson 3 一次做完，不走 `use-jackson2-defaults` 过渡

Boot 4 提供了两条过渡路径：
- `spring.jackson.use-jackson2-defaults=true` —— 让 Jackson 3 尽量对齐 Jackson 2 的默认值
- `spring-boot-jackson2` 模块 —— 保留一套完整的 Jackson 2 自动配置

**均不采用**，理由是它们都不能解决核心问题：

`Long → ToStringSerializer` 是**本项目自定义的模块注册**，不是库的默认行为。走 Jackson 2 兼容模块意味着这个自定义规则要写两遍（Jackson 2 一份、Jackson 3 一份），而 Boot 官方明确说明 `spring-boot-jackson2` 是「过渡期模块，未来版本会移除」。结果是：两份自定义规则 + 一个即将被删的依赖 + 一条尚未覆盖的迁移路径。

更关键的是，过渡态下**「Long 到底走哪个 mapper」本身就难以验证**——Jackson 2 的 `ObjectMapper` bean 与 Jackson 3 的 `JsonMapper` bean 并存时，HTTP 转换器用哪个取决于注册顺序与优先级。把这个不确定性留到下一轮，等于把 C1 这个静默 bug 埋进生产。

**采用的方案**：

```java
// 删除 WebDataConvertConfig 中的 Jackson2 ObjectMapper bean
@Bean
public JsonMapperBuilderCustomizer longToStringCustomizer() {
    return builder -> {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        builder.addModules(module);
    };
}
```

`JsonMapperBuilderCustomizer` 是 Boot 4 中 `Jackson2ObjectMapperBuilderCustomizer` 的继任者（迁移指引中明确点名的重命名），它作用于**默认序列化链路本身**，因此不可能出现「注册成功但没生效」。

`Long.TYPE`（基本类型）必须单独注册——只注册 `Long.class` 会漏掉 `long` 字段。

### 决策 3：H2 升级与 Boot 4 绑定，不能延后

Boot 4 把 H2 控制台拆成独立模块 `spring-boot-h2console`，其对 H2 2.x 的依赖是显式的。当前锁死的 1.4.200（2017 年版本）在 Boot 4 下无法工作。

AGENTS.md §4 写「H2 版本**锁死 1.4.200**（新版会破坏 Spring Boot 3 的 H2 console 路由）」——这条约束的适用对象是 Boot 3，在 Boot 4 下自动失效。因此该红线 SHALL 在本变更中正式废除。

**已实证的好消息**：`schema.sql`（346 行）与 `data.sql`（153 行）在 H2 2.4.240 + `MODE=MYSQL;DATABASE_TO_UPPER=false` 下**原样执行成功**。原本担心的 `bigint(20)` / `int(11)` 显示宽度、反引号、`COMMENT` 子句在 H2 2.x 的 MySQL 兼容模式下均被接受，`SELECT 1 FROM DUAL` 也仍可用。零 SQL 改动。

**唯一的硬约束**：`AUTO_SERVER=TRUE && DB_CLOSE_ON_EXIT=FALSE` 被 H2 2.x 明确拒绝，两个数据源的 URL 都要删掉 `DB_CLOSE_ON_EXIT=FALSE`。

**必须写进文档**：存量 `/tmp/aicode.mv.db` 是 1.4.200 格式，2.x 打不开。不写清楚的话，使用者启动失败会误判成升级引入的 bug。

### 决策 4：移除 Knife4j，保留 springdoc 自带 UI

现状是 `knife4j-springdoc-ui:3.0.3`（**2021 年停更**，面向 Spring Boot 2）与 `springdoc-openapi:2.3.0` 并存，本身就是历史遗留。

| 方案 | 评估 |
|------|------|
| **A（采用）移除** | ✅ springdoc 3.1.1 自带完整 Swagger UI；✅ 去掉一个停更两年的依赖与它的 javax 时代静态资源 |
| B 降级 springdoc 保 Knife4j | ❌ 等于放弃 Boot 4 |
| C 换 Scalar / RapiDoc | ❌ 引入新的前端资源依赖，需单独评估；本轮不增加变更面 |

移除后 SHALL 同步清理 `ContextConfiguration` 中的 `doc.html` 与 `/webjars/**` 资源映射，以及 `LoginInterceptor` 排除列表中的对应条目——否则会留下指向不存在资源的无效路由。

### 决策 5：配置属性迁移用官方诊断工具，不人工比对

Boot 提供 `spring-boot-properties-migrator`：加入依赖后启动时会打印所有已失效属性的诊断信息。

36 个配置键已逐条比对 Boot 4.1.1 的配置元数据，结论是 3 个失效（`management.endpoint.env.enabled`、`management.endpoint.shutdown.enabled`、`management.endpoints.health.show-details`）。但**人工比对依赖我下载的 jar 集合是否完整**（`server.*` 在 `spring-boot-web-server` 模块、`spring.sql.init.*` 在 `spring-boot-sql` 模块，最初都被误判为「不存在」）。

因此采用官方工具作为权威来源：临时挂 `runtimeOnly 'org.springframework.boot:spring-boot-properties-migrator'`，启动一次吃诊断，改完属性后**移除该依赖**。

### 决策 6：子步骤切分原则是「可独立验证」

8 个子步骤按「改完能立刻跑测试验证」切分，而非按「文件归属」切分。例如 Jackson 3 迁移（4e）与 `.shtml` Filter（4f）分属不同文件，但都必须在契约测试下验证；starter 改名（4a）必须在包迁移（4b）之前，因为后者依赖前者带来的类路径。

### Risks / Trade-offs

| 风险 | 影响 | 缓解 |
|------|------|------|
| Jackson 3 的日期序列化默认值与 Jackson 2 不同 | `createTime` 变成时间戳或 ISO 格式 | 契约测试场景④专门验证；`spring.jackson.date-format` 经实测在 Boot 4 元数据中仍为有效属性 |
| Jackson 3 自动注册 classpath 上的所有模块 | 行为变化（Boot 4 新增 `spring.jackson.find-and-add-modules`） | 若出现意外模块干扰，设为 `false`；契约测试会暴露 |
| `spring-boot-properties-migrator` 忘记移除 | 额外的启动开销与告警 | tasks 中把「移除该依赖」列为独立验收项 |
| H2 存量数据文件导致启动失败被误判为升级 bug | 排查时间浪费 | 变更文档显式写「必须删除 `/tmp/aicode.mv.db`」；本地环境用独立 `database` 环境变量隔离 |
| MyBatis-Plus `spring-boot4-starter:3.5.17` 是针对 Boot 4.0.1 构建的 | 在 Boot 4.1.1 上可能有兼容问题 | 若出现问题，向上一个可用版本或升级到更新版本；tasks 中要求先跑全量测试再进入下一子步骤 |
| WebSocket 端点受 Filter 或 Servlet 映射变更影响 | 构建日志推送失效 | 契约测试场景⑥单独验证；生成主链路端到端冒烟也会覆盖 |
| DevTools 实时重载默认关闭 | 开发体验变化 | 若团队依赖，在 `application.yml` 显式开启 `spring.devtools.livereload.enabled` |
