## Why

`feature/20260622/delta` 已经在 `application.yml` 开启 `spring.threads.virtual.enabled=true` 并通过自定义 `Thread.startVirtualThread` 包装 `@Async` 任务，但 `MdcTaskDecorator` 类**已实现却未装配**到任何 Executor——导致 `@Async` 跨虚拟线程执行时 traceId 上下文丢失，异步日志链路不可追踪。同时当前用 `Thread.startVirtualThread` 手动开 VT 是冗余方案（Spring 6.1 的 `SimpleAsyncTaskExecutor` 已内置 `setVirtualThreads(boolean)`）。本 change 做低风险渐进调整，让已有 VT 基础设施真正发挥作用。

## What Changes

- **`TaskExecutorConfig` 重写**：用 Spring 6.1 标准 `SimpleAsyncTaskExecutor.setVirtualThreads(true)` 替代 `Thread.startVirtualThread` 匿名类；通过 `setTaskDecorator(mdcTaskDecorator)` 装配现有 `MdcTaskDecorator` Bean，让 `@Async` 跨虚拟线程保留 traceId。
- **`WebSocketConfig` 加注释**：明确阶段 1 保留 Jakarta WebSocket 默认同步回调语义，阶段 2 计划异步化 `WSClientManager.sendMessage`。
- **`application.yml` 加 `management.metrics.enable.threads: true`**：暴露 `jvm.threads.*` 指标，启动后可区分平台线程与虚拟线程数（可观测性）。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `code-gen-pipeline`: `@Async` 任务的虚拟线程执行器 MUST 装配 `MdcTaskDecorator`，保证 traceId 跨虚拟线程边界传递；`SimpleAsyncTaskExecutor` MUST 使用 Spring 6.1+ `setVirtualThreads(true)` 标准 API 而非手动 `Thread.startVirtualThread` 包装；actuator MUST 暴露 `jvm.threads.*` 指标。

## Impact

- **生产代码改动**：
  - `aicode/src/main/java/com/aicode/config/TaskExecutorConfig.java`（重写，~30 行）
  - `aicode/src/main/java/com/aicode/config/websocket/WebSocketConfig.java`（加注释，~10 行）
  - `aicode/src/main/resources/application.yml`（加 1 行）
- **测试代码改动**：无（80 个测试用例零回归；新增 actuator 指标不影响测试）
- **dev 分支影响**：零。改动文件均为当前分支独有变更；dev 不动。
- **外部 API 变化**：无。所有改动是内部执行器配置 + 指标暴露。
- **行为变化**：仅异步日志的 traceId 现在能跨虚拟线程传递（修复当前 `traceId: null` 的 bug）。
- **新增依赖**：无（复用 Spring 6.1 + 现有 `MdcTaskDecorator`）。
