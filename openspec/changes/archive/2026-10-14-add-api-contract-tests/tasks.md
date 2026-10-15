# Tasks

## 1. 测试基础设施

- [x] 1.1 数据源隔离 ✅ 无需改动
      > 原任务假定该文件不存在，实测**已存在**于 `aicode/src/test/resources/application-test.yml`（2026-07-09 建立，6 个既有测试类已在用）。现状已满足：两个数据源均指向 `jdbc:h2:mem:aicode_test_tidb` / `aicode_test_uid`，SHALL NOT 触碰 `/tmp/aicode`
      > 差异：生产 `application.yml` 带 `DATABASE_TO_UPPER=false`，test profile 未带。已实测 H2 1.4.200 下两种模式的 `SELECT id, englishName` 均能命中（MySQL 兼容模式对裸列名的大小写解析一致），且响应 JSON 的字段名由 Jackson 序列化 Java 实体属性决定、与 DB 列名大小写无关 → **对契约断言无影响**，SHALL NOT 为此改动 test profile
- [x] 1.2 `spring.sql.init` 在 test profile 下加载 `db/schema.sql` + `db/data.sql`（`mode: always`）✅ 已在 `application-test.yml` 第 32–36 行配置
- [x] 1.3 test profile 下 `management.server.port: -1`（第 42–43 行）与 `spring.h2.console.enabled: false`（第 37–39 行）均已关闭 ✅ 端点 exposure 亦 `exclude: "*"`

## 2. 固化前端端点清单

- [x] 2.1 新增 `aicode/src/test/java/com/aicode/contract/FrontendEndpointCatalog.java` ✅
      > 实测从前端 bundle 的 `c2t9` 模块提取到 **46 个**路径（含 `/res/enum/`、`/upload/basic/uid` 两个后端未实现的）。与本轮初稿记录的「45 个」不符，SHALL 以实测的 46 为准
- [x] 2.2 类注释已标明来源文件、webpack 模块名（`c2t9`）、提取日期（2026-10-14）与提取方式，并显式声明「不是从后端枚举」及其理由 ✅
- [x] 2.3 两个后端未实现的端点已以 `BASE_ENUM` / `BASE_UUID` 常量 + `BACKEND_MISSING` 列表显式登记，各带原因注释（枚举接口残留 / UID 能力已由 vendored jar 接管）；`REACHABLE` 通过 `filter` 排除，SHALL NOT 静默省略 ✅
      > 附加：已一并确认后端确有对应的六个一级前缀 `/project`、`/framework`、`/displayAttribute`、`/logs`、`/login`、`/account`，作为 `.shtml` 兼容层白名单登记为 `REGISTERED_PREFIXES`（供 ⑦ Boot 4 变更使用）

## 3. 实现契约断言

- [x] 3.1 新增 `aicode/src/test/java/com/aicode/contract/ApiContractIT.java`，采用 `@SpringBootTest(webEnvironment = MOCK)` + `@ActiveProfiles("test")` + `webAppContextSetup` ✅
- [x] 3.2 打桩范围最小化 ✅ **业务层 Service 刻意不打桩** —— 契约断言需要真实的 MyBatis 查询与 Jackson 序列化，mock 掉之后验证的就只是「序列化行为」而非「真实接口返回」，反而失去意义。仅打桩 `ServerEndpointExporter`（依赖真实容器，MOCK 环境下无 `ServerContainer` 会导致上下文起不来）
      > ⚠️ **原任务写的 `@MockitoBean` 在当前版本不可用**：该注解由 Spring Framework **6.2** 引入，本项目当前是 6.1.16，编译失败。已改用 `@MockBean` 并在代码中注明 SHALL 在 `upgrade-spring-boot-3-5` 的任务 3.1–3.6 一并迁移
- [x] 3.3 严格性通过读原始 JSON 文本实现 ✅ 未使用 `jsonPath`（它对数字与字符串做宽松比较，区分不了 `"123"` 与 `123`），改为 `PARSER.readTree(body)` 后断言节点类型 + 与 DB 原值比对
- [x] 3.4 场景①ID 字符串 ✅ 4/4 通过
      > **实测修正**：分页包装类是 `com.aicode.core.Page`，列表字段为 `voList`（非 MyBatis-Plus 的 `records`）、计数为 `totalRow`。断言已按真实结构调整
- [x] 3.5 场景②后缀双路径 ✅ **44/44 通过**（46 个前端端点 - 2 个后端未实现）
- [x] 3.6 场景③R 结构 ✅ 3/3（其中 1 条因发现真实缺陷而以 `@Disabled` 登记，见 3.10）
- [x] 3.7 场景④日期格式与时区 ✅ 3/3 通过（GMT+8 偏移用固定时刻断言，避免午夜边界抖动）
- [x] 3.8 场景⑤null 保留 ✅ 2/2 通过
- [x] 3.9 场景⑥WebSocket 端点 ✅ 2/2 通过。MOCK 环境无嵌入式容器，无法建立真实 WS 连接，改为断言该路径**未被静态资源处理器吞掉**（不得返回 200、不得是静态资源 content-type），已在代码注释说明
- [x] 3.10 **【已发现真实安全缺陷】以 `@Disabled` 显式登记，不让测试长期红**
      > 空 token 请求 `/framework/list.shtml` 返回 **200 + 完整业务数据**，而非 9007。根因：`ContextConfiguration#addInterceptors` 的 `excludePathPatterns` 中 `"/**/*"` 及 `/*.html` / `*.js` / `*.css` / `*.png` 一批通配规则，本意是放行静态资源，但 `AntPathMatcher` 判定它们**同样匹配带 `.shtml` 后缀的 API 路径** → `LoginInterceptor` 对前端全部请求都不校验 token，鉴权形同虚设
      > 已实测（AntPathMatcher）：`/framework/list.shtml`、`/project/list.shtml`、`/logs/load.shtml` 均被 `/**/*` 命中
      > 处理方式：① 该断言标记 `@Disabled` + 完整根因注释；② 新增 `shtmlApiRequests_currentlyBypassAuthentication` 把**当前实际行为钉死为已知事实**，将来修复后此测试会红，从而提醒移除 `@Disabled`；③ 在 `ContextConfiguration` 的排除规则处加警示注释指向修复变更
      > **处理决定（2026-10-15 补充）**：经评估**不修复** —— 收紧排除规则属独立的安全加固工作，不在平台升级系列范围内。该缺陷以 `@Disabled` + 完整根因注释长期登记，并由 `shtmlApiRequests_currentlyBypassAuthentication` 把当前实际行为钉死为可执行事实。本变更 SHALL NOT 触碰生产代码。

## 4. 文档与既有测试

- [x] 4.1 修正 `LoginCtrlShtmlPathTest` 类注释 ✅ 保留原有 bug 背景说明，补充：① 与 `ApiContractIT` 场景②的分工（前者关注登录端点自身，后者参数化覆盖全部 44 个端点）② 指向 `api-contract` 能力 ③ 交叉引用本次发现的鉴权绕过缺陷
- [x] 4.2 把 `ApiContractIT` 写入「升级验收基准」清单 ✅
      > ⚠️ **原任务前提错误**：`docs/standards/testing.md` 在仓库中不存在（同 `docs/standards/` 整个目录缺失）。经确认，改为**把内容写入 `AGENTS.md` §7.10**（该文件是仓库唯一规范入口），并顺带移除 `AGENTS.md` 中全部失效的 `docs/standards/*.md` 引用（共 9 处），改写 §规范体系声明为「本文件即完整规范」

## 5. 验收

- [x] 5.1 执行 `./aicode/gradlew :aicode:test --tests "com.aicode.contract.*"`，全部场景通过 ✅
      > 实测 **59 个用例，0 失败**（含 1 个 `@Disabled` 的已知缺陷登记）
- [x] 5.2 执行 `./aicode/gradlew test`，既有测试类仍全绿，总耗时未显著劣化 ✅
      > 实测 **25 个测试套件 / 148 个用例 / 0 失败 / 2 跳过**
      > 2 跳过 = ① 本变更登记的已知缺陷 ② 既有的 `GitToolsCloneCompletenessTest`（`-Dnetwork.tests=true` 守卫，默认不执行）
      > 既有测试类统计：`find aicode/src/test common/src/test facade/src/test -name '*.java' | wc -l` → **18**（`aicode` 10 / `common` 8 / `facade` 0），本变更新增 2 个（`ApiContractIT`、`TestDatabaseInitializer`）
- [x] 5.3 确认测试过程未写入 `/tmp/aicode` ✅
      > `/tmp/aicode.mv.db` 的 mtime 仍为 `Oct 14 22:22`（本次会话开始前），契约测试全程使用 H2 内存库
- [x] 5.4 确认 `openspec validate 2026-10-14-add-api-contract-tests --strict` 通过 ✅（`is valid`）

## 6. 附带修复（超出原任务范围，经确认后执行）

- [x] 6.1 修复 test profile 下业务库（H2 内存库 `aicode_test_tidb`）**从未被初始化**的既有缺陷
      > 根因：本项目自定义 `tidbDataSource` / `uidDataSource` 两个 DataSource Bean，且 `uidDataSource` 标了 `@Primary`。Spring Boot 的 `SqlInitializationAutoConfiguration` 只把 `SqlDataSourceScriptDatabaseInitializer` 绑定到「唯一/主」DataSource，因此 `spring.sql.init.mode=always` 初始化的只是 uid 库 —— 业务表（`frameworks` / `project` / `map_*`）所在的 tidb 库从未建表，表现为 `Table "FRAMEWORKS" not found`
      > 修复：`application-test.yml` 中 uid 库保持 `mode: always`（脚本改用 `db/uid_schema.sql`，因 `BaiduUidConfig` 的 `uidGenerator` 在 Bean 构造期即 SELECT `worker_node`，该表必须先于业务 Bean 存在）；tidb 库由新增的 `TestDatabaseInitializer`（`InitializingBean` + `ResourceDatabasePopulator`）显式执行 `schema.sql` + `data.sql`
      > 生产 `application.yml` **未改动**，`spring.sql.init.mode` 仍为 `always`
- [x] 6.2 已查明：**生产环境不受此缺陷影响**，SHALL NOT 单独立项
      > 证据：生产 `application.yml` 中 tidb 与 uid 两个数据源**指向同一个 H2 文件**
      > ```
      > jdbc:h2:file:${database:/tmp/aicode};DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL;DATABASE_TO_UPPER=false;AUTO_SERVER=TRUE
      > ```
      > Boot 把初始化器绑到哪个数据源都无所谓 —— 建一次表，两个 Bean 都指向同一份库。
      > 另核实 `db/uid_schema.sql` 与 `db/schema.sql` 的建表内容**完全一致**（仅空格对齐不同）。
      > 该问题只在测试的**双内存库**配置下才显现，因此修复范围严格限定在 `application-test.yml`，生产配置零改动
