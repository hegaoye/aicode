## Why

`aicode/README.md §四` 列出了 ~30 个标记为 `@Deprecated` 且"前端未调用"的端点（覆盖 `Project` / `ProjectJob` / `Frameworks` / `DisplayAttribute` / `MapRelationship` / `ProjectRepositoryAccount` / `ProjectSql` / `Logs` / `Index` 9 个 controller），但 `audit-2026-06-22.md §4.3` 指出这些端点"未实际删除（只加注解），路由膨胀、误用风险持续"。本 change 在不破坏现有契约的前提下清理这些无引用端点 + 同步陈旧 facade 接口/服务方法，让代码库与已建立的 spec 契约严格对齐。

## What Changes

- **删除**：`/project/load/code/{code}`、`/project/job/{load,build,list,modify,delete}`、`/project/repository/{load/code/{code},list}`、`/project/sql/{load/projectCode/{projectCode},list,delete}`、`/project/framwork/{build,modify,delete}`、`/project/relationship/{load/code/{code},listByClassTableCode}`、`/displayAttribute/{load/mapFieldColumnCode/{mapFieldColumnCode},modify,delete}`、`/framework/load/code/{code}`、`/account/{load/code/{code},modify/password}`、`/logs/{createLogFiles,saveLogs,loadFilePath}`、`/index` 等约 30 个 `@Deprecated` 端点
- **删除**：`@Deprecated` 端点对应的 `*Service` 私有/无引用方法（无调用方）
- **更新**：`aicode/README.md §四` "未使用的后端API" 列表——删除已删除端点；保留说明"已彻底删除"
- **不修改**：业务核心端点（`/login/signin`、`/project/init`、`/project/job/execute`、`/project/sql/build`、`/project/sql/modify`、`/project/relationship/build` 等 5 个 spec 全部已覆盖的能力）
- **不修改**：所有 `@Deprecated` 注解（被删的方法直接消失，无需注解）

## Capabilities

### New Capabilities

- `api-deprecation-policy`: 定义 aicode 项目对 `@Deprecated` 端点的处理流程：标记 → 30 天观望 → 二次确认引用 → 删除；本 change 作为该流程的首次执行

### Modified Capabilities

无。所有 11 个现有 spec 涵盖的端点（`auth-account` / `project` / `sql-parse` / `mapping-display` / `frameworks-template` / `codegen` / `settings-repository` / `code-gen-pipeline` / `sql-reverse-parse` / `map-relationship-crud` / `project-init-endpoint`）均不涉及被删端点——被删端点从未被 spec 收录。

## Impact

- **代码层**：
  - 11 个 controller 文件：删除方法，**不删 controller 类本身**（仍保留业务方法）
  - facade 接口（如 `MapRelationshipService` / `FrameworksService` 等）：删除无引用的 `loadByCode` / `loadByProjectCode` 等 `@Deprecated` 方法
  - 测试代码：删除任何引用了已删端点 / 方法的测试（`MapRelationshipControllerTest` / `ProjectInitEndpointTest` 等需重新审视）
- **API 层**：
  - **BREAKING** 变更：约 30 个 HTTP 端点消失；任何外部脚本/旧客户端调用会 404
  - 但 aicode/README.md §四 早已声明"前端未调用"，且前端 SPA 在另一仓库
- **运行时**：H2 schema / data.sql 不变（schema 中无专用表）；`pom`/`build.gradle` 不变
- **测试**：
  - `MapRelationshipControllerTest`：4.1 的 `@MockBean MapRelationshipService` 等需 mock 的方法若被删，测试需调整
  - `ProjectInitEndpointTest` / `ProjectJobServiceImplConcurrencyTest`：不涉及被删端点，不影响
- **依赖**：
  - 删 facade 接口方法会触发 `MapRelationshipServiceImpl` 等 impl 类的方法删除（其 `implements` 需更新）
  - 不影响其他模块
