# Proposal

## Why

`POST /project/init` 是**同步阻塞**的：`ProjectServiceImpl.execute()` 在 HTTP 请求线程里
建库、跑用户的建表 SQL、逆解析表结构与列。用户导入一段大 SQL 后点「解析」，界面会
**白屏等待数秒到数十秒**，且看不到任何进度。

```
execute(code)
  ├─ createDatabase(code)   // 建 schema + 跑用户的全部建表 SQL（慢，取决于 SQL 量）
  └─ parse(code)            // 查表 → 逐表 forEach 解析列 → 逐表落库（也慢）
```

`ProjectListPage` 的构建（`POST /project/job/execute`）**早已是异步 + ws 推日志**的
（`ProjectJobExecutor` 独立 Bean + `@Async`），唯独 `init` 还停在同步。

## 现状核实

| 事实 | 依据 |
|---|---|
| `execute()` 只有 1 个调用者 | `ProjectController:97` —— 改造无兼容负担 |
| `@Async` 载体已就绪 | `TaskExecutorConfig` 的 `SimpleAsyncTaskExecutor` + VTH |
| 已有异步范例 | `ProjectJobExecutor`（独立成 Bean，否则 `this.xxx()` 调 `@Async` 会绕过 AOP）|
| **天然的进度粒度** | `parse()` 里是 `tableList.forEach`，能报「第 i/N 张表」|

## 但现有 ws 协议撑不起这件事

`WSClientManager.sendMessage(String)` 只推 **free-form 文本**，`GeneratorSVImpl` 里
17 处调用全是日志行，**没有完成信号**。前端 `use-build-log.tsx` 的注释如实记录了这点：

> free-form 日志（实测 line 117/129/138/155 等），**无 `[DONE]` sentinel**

后果：构建完成后前端**无法知道任务已结束**，`running → idle` 只能靠用户手点关闭按钮
（`build-log-panel.tsx` 的 design D1 也是这么定的）。

`init` 若照搬这套，前端**不知道何时该刷新解析结果** —— 只能盲等。

## What Changes

### 1. `init` 改异步 + 结构化进度协议

`POST /project/init` 改为「**立即接受 + 结果经 ws 推送**」，与 `POST /project/job/execute`
同构。ws 上推**结构化 JSON**：

```json
{"type":"progress","phase":"createDatabase","current":0,"total":0,"message":"正在创建数据库并执行建表脚本…"}
{"type":"progress","phase":"parse","current":3,"total":12,"message":"正在解析第 3/12 张表：t_order"}
{"type":"done","message":"解析完成，共 12 张表"}
{"type":"error","message":"解析第 5 张表 t_item 时失败：..."}
```

JSON 以 `{` 开头，与既有 free-form 文本日志天然可区分，**不破坏现有构建日志链路**。

### 2. 后端实现

- 新建 `ProjectInitExecutor`（`@Component` + `@Async`，独立 Bean —— 否则 AOP 失效，
  这是 `ProjectJobExecutor` Javadoc 里写明的坑）；
- `ProjectServiceImpl.execute()` 签名加进度回调（`Consumer<InitProgress>`），
  在 `createDatabase` 与 `parse` 的逐表循环里上报；**业务逻辑一行不改**；
- `ProjectController.init` 调 executor 后**立即返回 `R<Void>`**（表示"已接受"）。

### 3. 前端

`ProjectSqlPanel` 的「解析」按钮：调 `init` → 订阅 ws → 显示进度（阶段 + `i/N` 进度条）
→ 收到 `done` 后重拉解析结果 → 收到 `error` 显示错误（**并如实告知已解析多少张**）。

## 不做的事

- **不引入事务**（用户已拍板）—— `parse()` 逐表写入但无事务，中途失败会留下半成品。
  本变更**如实上报**"第 i 张表失败、已写入 N 张"，但不回滚。加事务会改变 `parse()` 内部
  大量 delete/insert 的行为，是高风险变更，属独立议题。
- 不改构建日志链路（它仍是 free-form 文本、无完成信号）—— 另开变更。
- 不新建状态表或轮询端点。
- 不改 `WSClientManager` 的单 Session 限制。

## BREAKING

`POST /project/init` 的语义从「同步执行完毕返回结果」变为「**立即返回已接受，结果经 ws**」。
前端调用方需要相应改造（本次一并做）。后端逻辑无变化。

## Capabilities

### New Capabilities

- **`init-async-progress`** —— 新能力。定义「长耗时端点 SHALL 异步接受 + 经 ws 推结构化进度」：
  端点立即返回、进度消息三态（`progress` / `done` / `error`）、进度 SHALL 含阶段与
  `current/total`、失败 SHALL 如实上报已完成工作量、与既有 free-form 日志 SHALL 可共存于同一通道。

### Modified Capabilities

- **`aicode-web-realtime`** —— ADDED 1 Requirement「消费端 SHALL 按消息形态分流结构化事件与 free-form 文本」
  （ws 通道同时承载两类消息，既有 `ws-client` 与 `use-build-log` SHALL 不受新增事件影响）。

## Impact

- **后端**：`ProjectInitExecutor`（新）+ `ProjectService.execute` 签名变更 +
  `ProjectInitProgress`（新 POJO）+ `ProjectController.init`；
  `contracts/openapi/openapi.yaml` 的 `/project/init` 描述微调。
- **前端**：`useInitProgress`（新 hook）+ `ProjectSqlPanel` 的解析按钮改造 + 测试。
- **测试**：后端 `ApiContractIT` 1 用例（init 立即返回 + 异步触发）；
  前端 `useInitProgress` 约 6 用例 + `ProjectSqlPanel` 扩展。
- **风险**：**中**。
  ① `@Async` 必须经 Spring 代理调用（自调会静默变同步）——
  这是 `ProjectJobExecutor` Javadoc 里明确记过的坑，本变更须同样独立成 Bean；
  ② ws 是单 Session，init 与 build 并发时消息混流，靠 `type` 字段分流（当前单用户场景
  两者不会真并发，但需在 design 标注该局限）。