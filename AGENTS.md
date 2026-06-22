# aicode

Spring Boot 3.3.9 应用：把 SQL `CREATE TABLE` 脚本经外部 Git 模板仓库（Freemarker/Beetl）
渲染为完整的 `dao → service → ctrl → frontend` 可运行项目，自动提交 Git 和/或打包 ZIP。
技术栈：Java 21、MyBatis-Plus、Druid、百度 UidGenerator；默认存储为 MySQL 兼容模式的文件型 H2。

## 构建与运行

```bash
cd aicode && ./gradlew bootRun      # 开发服务器 :8080
cd aicode && ./gradlew build        # 打包
```

需 Java 21。主类 `com.aicode.Application`。Gradle wrapper 仅在 `aicode/gradle/`，根
`build.gradle` 故意留空。

## 模块（单向依赖，无环）

- `common/`  — 工具类、`R`/`Page`/`BaseException`、JWT、JGit、拼音、zip4j、hutool、fastjson2。不依赖 Spring Web。
- `facade/`  — entity / DTO / VO / `*Service` 接口 / 域异常，mybatis-plus-extension。依赖 `common`。
- `aicode/`  — Spring Boot 应用：`*Controller` / `*ServiceImpl` / `*DAO` / `*Mapper` / `config/*` / `config/template/*` / `config/websocket/*`。依赖 `facade` + `common`。

业务域（package-by-feature）：`account` / `database` / `map` / `display` / `frameworks` / `module` / `project` / `setting` / `session`。

## 运行时

- 应用端口 `8080`；Actuator 端口 `8088`（`endpoints.web.exposure.include: "*"`，含 Prometheus）；H2 控制台 `/h2`（仅开发用）。
- 登录：`GET /login/signin?account=admin&password=888888`（**是 GET**）。返回 JWT 后以 URL query `?token=...` 传递。`LoginInterceptor` 存在但**未挂载**到拦截链，**不要假设它能拦截任何请求**。`account.password` 是 MD5。
- 构建进度通过 WebSocket `/websocket.shtml` 推送（`WSClientManager.sendMessage()`），同时落盘到 `project_job_logs`。

## 数据库（默认）

- H2 文件：`jdbc:h2:file:${database:/tmp/aicode};...MODE=MYSQL;AUTO_SERVER=TRUE`，`sa/sa`。
- `spring.sql.init.mode: always` 每次启动都会跑 `db/schema.sql`（22 张业务表 + `WORKER_NODE`）和 `db/data.sql`（种子 `admin/888888` + 34 条 `frameworks`），**DROP 后重建**。
- 两个 `DataSource`（`tidbDataSource` 业务、`uidDataSource` 分布式 ID）指向同一 H2 文件，但用不同 MyBatis-Plus 配置。新的业务 Mapper 放在 `com.aicode.<domain>.dao.mapper` 自动归到 `tidb`；`MybatisPlusConfigUid` 只扫 `com.baidu.fsg.uid.worker.dao`。
- H2 锁定在 `1.4.200`（老版本，为兼容锁定的 MyBatis-Plus），**不要随手升级**。
- `aicode/libs/uid-generator-2025.02.23-SNAPSHOT.jar` 通过 `implementation fileTree(dir: 'libs', include: ['*.jar'])` 加载，**不要删除**。

## 异步与构建主链路

- `@Async` 跑在 Java 21 虚拟线程上（`TaskExecutorConfig` → `Thread.startVirtualThread`）。
- 入口 `GET /project/job/execute?code={projectCode}`，代码路径：`ProjectJobServiceImpl` → `GeneratorSVImpl.aiCode()` 七步编排（建工作空间 → 装配模型 → clone 模板 → 渲染 → 写 SQL → Git 推送 → 打 ZIP）。
- 因为脱离请求线程，`aiCode()` 必须**内部** `try/catch` 并写 `ProjectJob.state = Error`，异常**不能**逃逸到全局 `ExceptionHandle`。

## 模板引擎

- 两种引擎，由模板仓库根的 `aicode.json` 声明（`"engine": "Freemarker" | "Beetl"`），默认 Freemarker。
- 文件后缀 `.ftl`（Freemarker）/ `.btl`（Beetl）。路径占位符 `${basepackage}` / `${className}` / `${module}` / `${model}` / `$classNameState$` 由 `GeneratorSVImpl.generator()` 在引擎渲染前后统一替换。
- 新增引擎：实现 `TemplateHelper.generate(templateData, targetFilePath, templatePath)` 并注册为 `@Service`。

## `.opencode` Skill（对话入口）

`.opencode/skills/aicode/SKILL.md` 是用户对话式生成的流程：键入 `aicode` → 健康检查
`8088/actuator/health/liveness` → `GET /login/signin` → `POST /project/build` →
`POST /project/sql/build` → `POST /project/init` → 选框架 → `POST /project/repository/build` →
`GET /project/job/execute`。**改动上述端点时必须同步更新该 skill**。

## 测试

**当前没有测试**：三个模块都没有 `src/test`；`aicode` 仅声明了 `spring-boot-starter-test` 依赖。`./gradlew test` 是空操作。目标规范在 `docs/standards/testing.md`；新代码应在被测类同包下补测试。

## 规范与深度文档

- `docs/standards/architecture.md` — 分层、模块依赖、双数据源、构建主链路。
- `docs/standards/code-style.md` — package-by-feature、`R` 统一响应、错误码（`0000/9003/9004/9007/9999`）、Lombok、安全红线。
- `docs/spec/spec-overview.md` 及 `spec-*.md` — 各域重建规格（auth / project / sql-parse / mapping-display / frameworks-template / codegen / settings-repository）。
- `aicode/README.md` — 完整 HTTP API 目录（Controller → 路径）。

## 易踩坑（不要顺手改）

- `db/data.sql` 的 `frameworks` 行里 `gitHome` 有拼写错误（`aicode-tamplate.git`、`aicode-template.git`），被模板和存量数据引用，**不要在普通改动里"修正"**。
- `devops/docker/Dockerfile` 和 `docker-compose.yml` 用的是过期的 `1.0-beta` 镜像标签，跟 README 的 `20210613` 不一致；当前能用的 Java 21 镜像是仓库根的 `aicode/Dockerfile`。
- `GlobalResponseBodyAdvice` **故意关闭**了，每个 Controller 都得手动包 `R.*` —— 新 Controller 不要返回裸对象。
- `logback-info.xml` 引用 `${LOG_HOME}`，但该 property 在文件里被注释掉；要用文件日志先 `export LOG_HOME=...`。
- token 走 URL 参数是已知安全缺口（见 `code-style.md` §13），不是普通改动里要"修"的 bug。

## 常用命令

```bash
# 启动后查看日志中的 traceId
tail -f aicode/build/libs/*.log 2>/dev/null   # 文件日志需先 export LOG_HOME

# 通过 skill 驱动
opencode   # 键入 `aicode` 进入对话式生成

# 数据库直连（开发）
# 浏览器 http://127.0.0.1:8080/h2  JDBC URL: jdbc:h2:file:/tmp/aicode  用户 sa / sa
```
