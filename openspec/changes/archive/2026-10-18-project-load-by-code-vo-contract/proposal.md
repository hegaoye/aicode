# Proposal

## Why

`ProjectController.loadByCode(code)` 当前直接返 `ProjectVO`（不经过 `R<T>` 包络）：

- 已用 `@Deprecated` 标记（`aicode/src/main/java/.../ProjectController.java:299`）；
- 签名 `(String code)` —— **没有 `token` 参数**，不依赖 `LoginInterceptor`；
- 返回类型 `ProjectVO`（裸实体），不返 `R<ProjectVO>`；
- 与"`api-contract` Requirement 统一响应包装的 data SHALL 携带真实结构"（详见 `openspec/specs/api-contract/spec.md` line 156）冲突 —— 该 Requirement 要求所有 Controller 出参走 `R<T>` 包络，`loadByCode` 是已知唯一例外。

本变更修正这一缺口：

1. **返 `R<ProjectVO>`** —— 让前端用 `unwrapSuccess` 样板上 `loadByCode`，无需各自处理裸实体；
2. **强制 `Authorization: Bearer` 鉴权** —— 当前 `loadByCode(code)` 不读 token，靠 `LoginInterceptor` 自然拦截；改为显式接收 token（仍由前端 axios 拦截器注入），让"受保护接口为何被保护"在签名层就清晰；
3. **删除 `@Deprecated`** —— 改造后该端点不再是历史遗漏，而是与 `auth-account` / `api-contract` 规范一致的现代端点，供给 M3-A 的 `ProjectDetailPage` 使用。

后端本变更只动一个 Controller 方法；前端不直接对应改动（前端在 M3-A 变更中接入"按 `R<ProjectVO>` 消费"的新形态）。

## What Changes

- **`ProjectController.loadByCode` 签名**：
  - 入参：`@PathVariable String code`；
  - 出参：`R<ProjectVO>`（替换裸 `ProjectVO`）；
  - 校验：`code == null` 仍返 `null`（保留原有早返回语义，向后兼容 R.success(null)）；
  - 删除 `@Deprecated` 注释；
  - 不再让 `loadByCode` 走"看起来可以匿名"的反模式。
- **依赖 `LoginInterceptor` 路径不变**：仍是 Header 优先 + URL `?token=` 兼容（`auth-account` R5）。
- **契约自动刷新**：`OpenApiExportIT` 重导出 `contracts/openapi/openapi.yaml`，让 `/project/load/code/{code}` 路径从 `$ref: "#/components/schemas/ProjectVO"` 改为 `$ref: "#/components/schemas/RProjectVO"`。
- **orval 生成物变化**：`aicode-web/src/generated/` 下 `api.ts` 中 `loadByCode` 的返回类型从 `Promise<AxiosResponse<ProjectVO>>` 变为 `Promise<AxiosResponse<RProjectVO>>`；`api.msw.ts` 的 `getLoadByCodeMockHandler` 也跟随调整（MSW 默认返 ProjectVO，新结构应是 `{info, code, success, data}` 包络）。
- **`api-contract` 主 spec 加固**：现有 Requirement "统一响应包装的 data SHALL 携带真实结构"（line 156）下新增一个 Scenario 钉死"凡受保护接口 SHALL NOT 直接序列化实体出参"，覆盖 `loadByCode` 这类历史遗漏的反例。

**BREAKING**：

- 任何存量调用方（Angular 旧前端或 curl 脚本）若依赖 `loadByCode` 直返 `ProjectVO` 形状 —— 调用代码需要从 `result.name` 改为 `result.data.name`（R 包络标准）。
- 仓库内 grep 搜索 `ProjectController.loadByCode` 调用：当前只有 Angular 7.0.4 打包产物（M4 才会切运行时），未在业务代码或前端 React 代码里出现。
- 实施时 SHALL 先 grep 全仓确认无业务层消费。

## Capabilities

### New Capabilities

（无。本变更走 MODIFIED。）

### Modified Capabilities

- `api-contract`：在现有 Requirement "统一响应包装的 data SHALL 携带真实结构" 下新增 1 个 Scenario，钉死"凡受保护 REST 接口的出参 SHALL 走 R<T> 包络，SHALL NOT 直返实体"。该 Scenario 将作为 M0/M1/M2 已落地契约层规则的**反向防线**，明示"`loadByCode` 这类历史遗漏不再被允许"。
- `auth-account`：现有 Requirement "凭据传输方式的演进约束" 已被本变更的事实行为满足（受保护接口必须显式 Authorization 传递），无需新增 Scenario；只在本变更设计文档中记入"loadByCode 强制走拦截器"这条实现事实。

## Impact

- **后端**：
  - `aicode/src/main/java/com/aicode/project/ctrl/ProjectController.java`：一处方法签名 + 装配 + 删 `@Deprecated`；
  - 现有的 `BeanUtils.copyProperties(project, projectVO)` 装配方式保留（与 `loadByCode` 当前实现一致，未变更）。
- **测试**：
  - 现有 `ProjectController` 没有针对性单测（实测 grep 无对应测试类）；
  - `ApiContractIT` 不必新增 —— 本变更不影响 `/project/list` 或 `/displayAttribute/list` 等既有契约路径的断言；
  - 但**新增一个 `ApiContractIT.@Nested` Scenario 组** "loadByCode 路径走 R<T>"，断言 `/project/load/code/contract-demo` 响应的 `data.code` 等于种子编码、`success === true`，**反向验证**：把 Controller 装配改回 `return projectVO`（裸实体）→ 该断言必须变红，因为 `data.code` 拿不到实体 `.code` 而是 `undefined`。
- **契约**：`contracts/openapi/openapi.yaml` —— `/project/load/code/{code}` 响应的 `data` 节点 schema 改为真实 `ProjectVO` 与 R 包络的组合（即 `RProjectVO` schema 或类似）。`PageVOProjectVO` / `RListMapRelationship` 等既有 schema 不受影响。
- **前端生成物**：`aicode-web/src/generated/api.ts` 内 `loadByCode` 的返回类型从 `Promise<AxiosResponse<ProjectVO>>` 变成 `Promise<AxiosResponse<RProjectVO>>`；调用方后续 M3-A 变更中可使用 `unwrapSuccess(resp)` 样板。`api.msw.ts` 内 `getLoadByCodeMockHandler` 形态跟随调整。
- **不影响**：M0 / M1 / M2-1 / M2-2 已落地的契约层、测试、运行时不变。
- **风险**：
  - 低：现有调用方仅 Angular 7.0.4 老前端（M4 才切），与 React + 新契约共同推进阶段不冲突；
  - 但仍需 grep 全仓确认无 Java 业务层独立调用 `loadByCode` —— 实施时 1.x 反向验证步骤会用 grep 钉这一前提。

