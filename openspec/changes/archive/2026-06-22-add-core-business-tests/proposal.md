## Why

代码生成主链路（七步编排、SQL 反向解析、模板渲染、ZIP 打包、Git 推送）当前**完全没有自动化测试覆盖**（`./gradlew test` 通过但 0 业务断言；`docs/standards/testing.md §1` 自承）。任何一次重构（如已完成的 `GeneratorSVImpl` 拆分）都可能无声破坏 `MapClassTable` 装配、占位符替换、引擎适配等核心契约。本 change 为核心业务流程补齐测试，建立"测试即契约"的安全网，让 P0/P1/P2/P3/P4 累计 24 项优化成果不再因后续改动而回归。

## What Changes

- 新增 `code-gen-pipeline` 能力：覆盖 `GeneratorSVImpl` 主链路关键契约（占位符替换、路径拼装、增量/全量分支、状态枚举类衍生）
- 新增 `sql-reverse-parse` 能力：覆盖 `ProjectServiceImpl.execute` 两阶段（建库 + 反向解析）的字段映射、类型推断、幂等重建
- 新增 `map-relationship-crud` 能力：覆盖 `MapRelationshipController.build` 的双向关联建立、OneToMany 反向 OneToOne 的方向保持（P1-3）
- 新增 `project-init-endpoint` 能力：覆盖 `POST /project/init` 触发 + 状态机推进（含 P2-4 DB-state 锁）
- 引入 MockMvc + H2 测试库 + JUnit 5（aicode 模块已有 testImplementation）
- 修复依赖：`@WebMvcTest` 切片 + `@SpringBootTest` 集成分层（按 `docs/standards/testing.md §6`）
- **不引入**新依赖：复用现有 `spring-boot-starter-test`（已声明）

## Capabilities

### New Capabilities

- `code-gen-pipeline`: GeneratorSVImpl 主链路七步编排的关键契约测试（占位符替换、路径拼装、模板清理、模板引擎适配、ZIP 命名、并发锁）
- `sql-reverse-parse`: ProjectServiceImpl.execute 两阶段（createDatabase + parse）的字段/类型映射、幂等重建、englishName 唯一约束
- `map-relationship-crud`: MapRelationshipController.build 双向关联 + OneToMany 反向方向保持
- `project-init-endpoint`: POST /project/init 端到端流程 + ProjectJob DB 状态锁

### Modified Capabilities

无。本次仅新增测试，不修改任何能力的需求。

## Impact

- **新增文件**：`aicode/src/test/java/com/aicode/**`（按能力分包，预计 4-6 个测试类、30+ 用例）
- **新增配置**：`aicode/src/test/resources/application-test.yml`（独立 H2 测试库 URL，避免污染 `/tmp/aicode`）
- **修改配置**：`aicode/build.gradle` 加 JUnit 5 + Spring Boot Test 配置（已有 `spring-boot-starter-test`，需补 `useJUnitPlatform`）
- **运行时无影响**：测试仅在 `gradle test` 任务触发，不进生产 jar
- **CI**：当前无 CI workflow；本地 `./gradlew test` 已可验证
- **依赖**：无新依赖（Maven Central 现有）
