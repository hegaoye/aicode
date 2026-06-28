## Context

`feature/20260622/delta` 当前虚拟线程现状：
- ✅ `application.yml:9` 已开 `spring.threads.virtual.enabled: true`（Spring Boot 3.2+ 内置）
- ✅ `TaskExecutorConfig:25` 用 `Thread.startVirtualThread` 自定义 @Async Executor
- ❌ `MdcTaskDecorator` 类**已实现**但**未装配**到任何 Executor → @Async 跨虚拟线程丢 traceId
- ❌ 当前手动 `Thread.startVirtualThread` 包装是 Spring 6.1 标准的冗余实现
- ⚠️ 56 个 controller 端点 + 1 个 @Async 方法（`ProjectJobExecutor.execute`）已运行在虚拟线程上但日志链路断

## Goals / Non-Goals

**Goals:**
- @Async 跨虚拟线程保留 traceId 上下文（修复异步日志链断 bug）
- 用 Spring 6.1+ 标准 API（`setVirtualThreads` / `setTaskDecorator`）替代手动包装
- 暴露 `jvm.threads.*` 指标，便于验证 VT 是否生效
- 改动只落在当前分支独有的文件，dev 分支零影响
- 零行为变化（除 traceId 链路修复 + 指标暴露外）

**Non-Goals:**
- 不深入 WebSocket sendMessage 异步化（推迟阶段 2）
- 不改 MyBatis 同步 → reactive（推迟阶段 3）
- 不扩容 Druid 连接池（推迟阶段 3）
- 不升级 jdk24（推迟阶段 3）
- 不重写 WSClientManager 静态锁（推迟阶段 2）

## Decisions

### Decision 1: 用 Spring 6.1+ `setVirtualThreads` API 替代 `Thread.startVirtualThread`

**理由**：`SimpleAsyncTaskExecutor.setVirtualThreads(boolean)`（Spring 6.1+）是标准 API，等价于当前匿名类 `Thread.startVirtualThread(task)`，但语义更明确，线程名前缀由 `SimpleAsyncTaskExecutor("aicode-")` 统一管理。

### Decision 2: 装配 `MdcTaskDecorator` 到当前 Executor

**理由**：`MdcTaskDecorator` 类已实现 `TaskDecorator` 接口（`config/MdcTaskDecorator.java:10`），但当前 `TaskExecutorConfig` 未注入该 Bean，导致 `@Async` 任务（`ProjectJobExecutor.execute`）跨虚拟线程执行时丢失 traceId 上下文，日志链路断。

**修复**：在 `TaskExecutorConfig` 注入 `MdcTaskDecorator`，调用 `exec.setTaskDecorator(mdcTaskDecorator)`。

**注意点**：`MdcTaskDecorator.decorate` 已正确实现 MDC 跨线程传递（line 21-34：copyOfContextMap → setContextMap → clear），无需改该类。

### Decision 3: actuator 暴露 `jvm.threads.*` 指标

**理由**：当前 `application.yml:99` 的 `management.metrics.enable.*` 未列出 `threads`——`/actuator/metrics/jvm.threads.live` 端点不可用。Spring Boot Actuator 默认就有 `jvm.threads.*` 指标，只是没显式启用。

**改动**：
```yaml
management:
  metrics:
    enable:
      threads: true    # 新增
```

**副作用**：`endpoints.web.exposure.exclude: "*"` 已禁所有 actuator 端点，新指标默认不暴露。若需 `/actuator/metrics/jvm.threads.live` 可用，须在 `include` 列表加 `metrics`。

### Decision 4: WebSocketConfig 行为不变，仅加注释

**理由**：`@OnMessage` / `@OnOpen` 同步语义是 Jakarta WebSocket 默认行为，改成异步会触及生命周期和 `Session` 线程局部性。阶段 1 明确推迟到阶段 2。

**改动**：仅加 javadoc 解释阶段 1 不深入 + 阶段 2 计划（拆 `WSClientManager.sendMessage` 到独立线程池投递）。

## Risks / Trade-offs

- [MdcTaskDecorator 跨 VT 传递 MDC 的开销] → 每次任务一次 MDC copy（~微秒级），可忽略；**收益**：异步日志可追踪
- [setVirtualThreads(true) 与 application.yml `spring.threads.virtual.enabled` 是否重复] → **不重复**：
  - `spring.threads.virtual.enabled` 控 Tomcat connector 处理 HTTP 请求
  - `SimpleAsyncTaskExecutor.setVirtualThreads` 控 `@Async` 任务
  - 两个独立机制覆盖不同入口
- [dev 分支影响] → 0
- [现有测试] → 80 个用例全过（aicode 51 + common 29）；MdcTaskDecorator 不被任何测试直接调用
- [actuator metrics 端点暴露] → 需在 `endpoints.web.exposure.include` 加 `metrics` 才能 HTTP 访问；当前 `exclude: "*"` 完全禁所有

## Migration Plan

无数据库迁移。纯代码改动 + 配置改动。

回归：
1. 提交 + push
2. CI 跑 `./gradlew clean build` 验证 80 用例全过
3. 手动启动 + `curl /actuator/metrics/jvm.threads.live` 验证指标

## Open Questions

- Q1: 阶段 1 三个改动是否**一个 PR** 还是**三个 PR**？
- Q2: actuator metrics 端点（`/actuator/metrics/*`）是否要在 production 暴露？当前完全禁。
- Q3: `MdcTaskDecorator` 在新线程无父 MDC 时生成新 UUID（line 27）——这与 `ContextInterceptor` 在请求无 `traceId` header 时用 `uidGenerator.getUID()` 的逻辑对齐。确认一致 OK。
