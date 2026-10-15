# Proposal

## Why

`aicode/src/main/resources/static/` 下打包着一套 Angular 7.0.4 + ng-zorro + jQuery 的前端产物，它与后端 API 之间存在大量**从未被任何测试守护的隐式契约**。实测挖掘前端 bundle 后确认了三条关键事实：

1. **全局请求过滤器无条件给每个请求 URL 拼 `.shtml` 后缀**（`t.url += ".shtml"`，除显式声明了 `shtml` 属性的请求外），WebSocket 端点同样是 `ws://host/websocket.shtml`。这意味着后端必须提供后缀路由兼容能力，否则前端 100% 不可用。
2. **ID 为 19 位雪花值**（如 `1882812765213425664`），远超 JS 安全整数上限 `2^53-1 = 9007199254740992`。`WebDataConvertConfig` 专门注册了 `Long → ToStringSerializer`，把 ID 序列化成 JSON 字符串以规避精度丢失——但这条契约没有任何测试保护。
3. **统一响应体 `R` 的结构、日期格式 `yyyy-MM-dd HH:mm:ss` + GMT+8、null 字段不被省略**同样无测试保护。

即将启动的 Java 25 / Spring Boot 3.3.9 → 4.1 升级会同时触碰以上全部三条（Spring Framework 7 删除后缀匹配 API；Spring Boot 4 默认切换到 Jackson 3）。**在没有契约测试的情况下，这类回归全部是静默的**——编译通过、启动成功、接口返回 200，但前端拿到的 ID 已经被 JavaScript 截断成 `1882812765213425600`。

本变更先把契约钉成可执行测试，在升级前的 Spring Boot 3.3.9 基线上跑绿，作为后续 5 个升级变更的「金标准」。

## What Changes

- 新增能力 `api-contract`，以可执行测试的形式定义前后端之间的接口契约
- 新增集成测试类 `ApiContractIT`，覆盖五组断言：
  1. ID 以 JSON **字符串**形式返回（对抗 Jackson 3 精度回归）
  2. `.shtml` 与无后缀路径**双路径等价可达**（对抗 Spring Framework 7 移除后缀匹配）
  3. 统一响应 `R` 的结构（`code` / `success` / `data`）不变
  4. 日期字段格式与时区不变（`yyyy-MM-dd HH:mm:ss` + GMT+8）
  5. null 字段不被 JSON 序列化省略
- 从前端 bundle 中提取的 45 个端点路径作为测试数据固化到测试代码中
- 修正 `LoginCtrlShtmlPathTest` 中过时的技术描述（`@MockBean` 语义说明）

本变更**不修改任何生产代码**——所有断言 SHALL 在现有 Spring Boot 3.3.9 基线上直接通过，其价值在于「冻结现状」。

## Capabilities

### New Capabilities

- `api-contract`: 定义 aicode 后端 REST 接口与仓库内静态前端之间的可观测契约，包括 ID 序列化形态、路径后缀兼容、统一响应结构、日期格式与时区、null 字段保留等要求。

### Modified Capabilities

无。

## Impact

- **新增文件**：
  - `openspec/changes/2026-10-14-add-api-contract-tests/specs/api-contract/spec.md`
  - `aicode/src/test/java/com/aicode/contract/ApiContractIT.java`
  - `aicode/src/test/java/com/aicode/contract/FrontendEndpointCatalog.java`（前端端点清单）
- **修改文件**：`aicode/src/test/java/com/aicode/session/ctrl/LoginCtrlShtmlPathTest.java`（仅注释）
- **不影响**：生产代码、Gradle 依赖、运行时行为、前端静态资源
- **测试基础设施**：需要一个可在 H2 内存库上启动的 `test` profile（当前 `LoginCtrlShtmlPathTest` 等测试已使用 `@ActiveProfiles("test")`，本变更复用其约定）
- **验收命令**：`./aicode/gradlew :aicode:test --tests "com.aicode.contract.*"`
