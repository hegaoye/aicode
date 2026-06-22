## Context

代码生成主链路 `GeneratorSVImpl.aiCode()` 7 步编排（建工作空间 → 装模型 → 下载模板 → 渲染源码 → 发射 SQL → 清理 → 提交/打包）已在 P0/P1/P2/P3 中重构 4 次（抽 `ProjectJobExecutor` / 抽 `TemplateEngineAdapter` + `SqlEmitter` + `ZipPackager` / 修 `parse` 字段 / 改 `createDatabase` 幂等）。但 `./gradlew test` 仍是空操作——`docs/standards/testing.md §1` 自承"零测试"。

当前已有少量工具类测试（`Md5Test` / `JwtTokenTest` / `StringHelperTest` / `StringToolsTest` / `YNEnumTest` / `TemplateEngineEnumTest` / `PasswordCryptoTest` / `TemplateEngineAdapterTest`，共 45 用例），但**全部是纯函数 / 单元测试**，无 Spring 上下文、无 HTTP、无 DB。

本次 change 把"业务核心"提到测试金字塔的中间层（MockMvc 切片 + 必要时的 SpringBootTest），建立对已修复的 15 个风险（R1-R14 + R17）的回归防线。

## Goals / Non-Goals

**Goals:**

- 4 个能力（`code-gen-pipeline` / `sql-reverse-parse` / `map-relationship-crud` / `project-init-endpoint`）每个有 ≥3 个核心场景测试
- 测试覆盖本次优化已修复的关键契约：`@Async` 自调 / `parse` 字段 / `delete` 路径 / `createDatabase` 幂等 / `OneToMany` 反向 / 占位符替换 / 增量分支 / DB-state 锁 / 凭据加密端到端
- 测试在 CI 友好（无需外部 Git 仓库、无需 MySQL，H2 内存模式即可）
- 测试不修改任何生产代码

**Non-Goals:**

- 不为前端 / WebSocket / `@Async` 端到端行为写测试（仍属集成测试空白，本 change 不开新坑）
- 不写性能/负载测试
- 不引入新测试依赖（复用 `spring-boot-starter-test` 4.0.0-M5 + JUnit 5.10.2）
- 不重构 `GeneratorSVImpl` / `ProjectServiceImpl` 本身（避免改动与测试同时进行）
- 不为已删除的 4 套 controller（`ModuleFile` / `ProjectModel` / `ProjectModelClass` / `ProjectCodeCatalog`）补任何测试

## Decisions

### Decision 1: 单元测试 vs MockMvc 切片 vs SpringBootTest 分层

按 `docs/standards/testing.md §3`：

- **纯函数 / 工具类**（`TemplateEngineAdapter` 等已有）：单测，无 Spring
- **单 Service 行为**（`MapRelationshipController.build` 双关联）：`@WebMvcTest` 切片 + `@MockBean` 替 DAO
- **跨层（DB + Service + 触发器）**（`ProjectServiceImpl.execute` / `ProjectJobServiceImpl.execute`）：`@SpringBootTest(webEnvironment = NONE)` + H2 in-memory
- **WebSocket / 真 Git / 真 ZIP**（`aiCode` 七步）：**不写端到端**，只测**已抽出的纯 collaborator**（`TemplateEngineAdapter` / `SqlEmitter` / `ZipPackager` / `ProjectJobExecutor`）的契约

**理由**：`aiCode` 七步涉及 jgit clone 真实仓库 + 文件 IO + WebSocket。端到端测试在 CI 环境下不稳定（网络、临时文件清理），与"零新依赖"目标冲突。**通过测纯 collaborator 来覆盖关键契约**（P4-3 已抽出）即可。

### Decision 2: H2 测试库用 in-memory，独立 URL

`aicode/src/test/resources/application-test.yml`：

```yaml
spring:
  datasource:
    tidb:
      url: jdbc:h2:mem:aicode_test;MODE=MYSQL;DB_CLOSE_DELAY=-1
      username: sa
      password: sa
    uid: # 同上，MyBatis-Plus 配置复制
```

**理由**：避免污染 `/tmp/aicode` 默认库（与 `testing.md §8` 一致）。in-memory 模式测试结束自动清理。

### Decision 3: Mock 的策略

- **Mock**：`ProjectMapper` / `MapFieldColumnMapper` / `SettingMapper` 等 MyBatis Mapper（用 `@MockBean`）
- **不 Mock**：`R` / `BaseException` / `TemplateEngineEnum` / `YNEnum` / `StringHelper` / `MapClassTable` 实体（用真实对象）
- **Mock 静态方法**：`WSClientManager.sendMessage` 用 `Mockito.mockStatic`（Java 17+ 支持）

### Decision 4: 测试方法命名 + AAA 风格

`@DisplayName` 中文 + 方法名英文 `action_condition_expected`，如 `init_givenActiveSql_createsAndParsesMapping`。

每方法只验证一个行为点。三段式 Arrange-Act-Assert。

## Risks / Trade-offs

- **静态方法 Mock 复杂度**（WSClientManager）：`mockStatic` 需 `try-with-resources`，与 `@SpringBootTest` 集成需小心作用域。→ **Mitigation**：用 `@BeforeEach` 初始化 static mock，测试方法内 `try (MockedStatic<WSClientManager> ms = Mockito.mockStatic(WSClientManager.class)) { ms.verify(...) }`
- **H2 与 MySQL 方言差异**（`MODE=MYSQL` 不支持某些 H2-only 语法）：`schema.sql` 用 `IF NOT EXISTS` 模式可能与 H2 不兼容。→ **Mitigation**：测试只覆盖 `db/data.sql` 的种子 + 业务路径，不依赖 schema 完整性；用 `@Sql` 注解夹具数据
- **测试执行时间**：`@SpringBootTest` 单次启动 5-15s，30+ 用例会超 60s。→ **Mitigation**：默认跑 `@WebMvcTest` 切片（~1s/类），只在 1-2 个核心 case 用 `@SpringBootTest`
- **现存代码可测性差**（如 `GeneratorSVImpl` 仍是 715 行单体）：部分契约藏在私有方法中。→ **Mitigation**：测已抽出的 collaborator 而非原方法；不重构生产代码
- **Druid + H2 兼容性**：`application.yml` 用 Druid，但测试时 in-memory H2 + Druid 已知有连接池 bug。→ **Mitigation**：测试用 `spring.datasource.tidb.type=com.alibaba.druid.pool.DruidDataSource` 显式声明；如有问题 fallback `com.zaxxer.hikari.HikariDataSource`

## Migration Plan

无 migration。本次仅添加测试文件，**不修改生产代码**。回归：

1. 提交 + push
2. 本地 `./gradlew test` 跑通
3. 后续 PR 必须 `./gradlew test` 全过才合并

## Open Questions

- Q1: 是否要在 CI 加 GitHub Actions（`.github/workflows/test.yml`）？当前项目无 CI 配置文件。**建议**：本 change 范围内不引入，留作独立 PR。
- Q2: 端到端 WebSocket 推送测试是否在后续 iteration 单独做？**建议**：是，本 change 不开此坑。
