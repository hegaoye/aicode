# code-gen-pipeline Delta — phase1-virtual-thread-tuning

## ADDED Requirements

### Requirement: @Async 任务的虚拟线程执行器装配 MdcTaskDecorator
The system SHALL configure `SimpleAsyncTaskExecutor` as the @Async executor, MUST enable virtual threads via `setVirtualThreads(true)`, and MUST attach `MdcTaskDecorator` so that `traceId` in MDC is propagated across the virtual thread boundary when @Async methods run on a separate thread.

#### Scenario: 异步任务跨虚拟线程保留 traceId
- **WHEN** HTTP request enters with `traceId: abc123` and triggers `@Async` method (e.g. `ProjectJobExecutor.execute`)
- **THEN** the @Async method runs on a virtual thread
- **AND** the virtual thread's MDC contains `traceId=abc123`
- **AND** log lines emitted inside the @Async method include `tid: abc123` (not `tid: null`)

#### Scenario: 异步任务无父 MDC 时生成新 traceId
- **WHEN** the @Async method is triggered without a parent MDC (e.g. scheduled task, manual call)
- **THEN** `MdcTaskDecorator` generates a random UUID for `traceId`
- **AND** the virtual thread's MDC contains the new UUID

### Requirement: 使用 Spring 6.1+ setVirtualThreads API 而非手动 Thread.startVirtualThread
The system SHALL use `SimpleAsyncTaskExecutor.setVirtualThreads(true)` (Spring 6.1+ standard API) instead of manually wrapping `Thread.startVirtualThread(task)` in an anonymous `execute` override.

#### Scenario: 替代手动包装
- **WHEN** an @Async method is submitted to the executor
- **THEN** Spring 6.1's `SimpleAsyncTaskExecutor` runs the task on a virtual thread (via its built-in `VirtualThreadDelegate`)
- **AND** the thread name has the configured prefix (e.g. `aicode-`)
- **AND** no anonymous `Thread.startVirtualThread(task)` override is present

### Requirement: 暴露 jvm.threads.* 指标
The system SHALL expose `jvm.threads.*` metrics via Spring Boot Actuator by setting `management.metrics.enable.threads: true`, so that operators can verify virtual threads are in use.

#### Scenario: actuator 暴露虚拟线程数
- **WHEN** the application starts and actuator metrics endpoint is exposed
- **THEN** `GET /actuator/metrics/jvm.threads.live` returns the current live thread count
- **AND** the value reflects both platform and virtual threads
