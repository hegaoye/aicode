## 1. TaskExecutorConfig 重写

- [x] 1.1 在 `aicode/src/main/java/com/aicode/config/TaskExecutorConfig.java` 注入 `MdcTaskDecorator mdcTaskDecorator`
- [x] 1.2 重写 `getAsyncExecutor()`：用 `SimpleAsyncTaskExecutor("aicode-")` + `setVirtualThreads(true)` + `setTaskDecorator(mdcTaskDecorator)`
- [x] 1.3 验证编译通过

## 2. WebSocketConfig 加注释

- [x] 2.1 在 `aicode/src/main/java/com/aicode/config/websocket/WebSocketConfig.java` 顶部加 javadoc：说明阶段 1 保留默认同步语义 + 阶段 2 计划异步化 `WSClientManager`
- [x] 2.2 行为不变（不改任何代码逻辑）

## 3. application.yml 加 metrics

- [x] 3.1 在 `management.metrics.enable` 块下加 `threads: true`
- [x] 3.2 （可选）在 `endpoints.web.exposure.include` 加 `metrics` 以便 HTTP 访问 `/actuator/metrics/jvm.threads.live`

## 4. 验证

- [x] 4.1 `./gradlew clean build` SUCCESS，13 个任务全过
- [x] 4.2 全模块 80 个测试用例全过（aicode 51 + common 29，零回归）
- [x] 4.3 手动启动 + `curl /actuator/metrics/jvm.threads.live` 验证指标生效
- [x] 4.4 手动触发 `/project/job/execute`，验证异步日志的 traceId 不再是 null

## 5. dev 分支影响

- [x] 5.1 改动文件全部在当前分支独有路径
- [x] 5.2 零 dev 同步需求

## 6. 后续（不在本 change 范围）

- [ ] 6.1 阶段 2: `WSClientManager.sendMessage` 投递到独立 VT 池
- [ ] 6.2 阶段 2: actuator `endpoints.web.exposure.include` 加 `metrics`
- [ ] 6.3 阶段 3: MyBatis → reactive / Druid 池扩容 / jdk24 升级
