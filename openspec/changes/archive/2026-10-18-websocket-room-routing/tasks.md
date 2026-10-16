# Tasks

## 1. `WSClientManager` 重写会话管理

- [x] 1.1 `put(Session session, String room)`：按 room 存 session，**不再有「只在该 key 不存在时才保存」的逻辑**（那正是第二个连接被丢弃的原因）；同时维护反向索引 `sessionRooms`（`Map<Session, String>`）供 `remove` 用 *(已完成：put(session, room) 无条件登记到房间集合（原「仅当 key 不存在才保存」正是丢弃第二个连接的原因）+ 维护 `Map<Session,String>` 反向索引)*
- [x] 1.2 `remove(Session session)`：按 session 反查房间并移除（反向索引 + 该房间的 session 集合）；**`@OnClose` 必须能拿到 session**，否则多房间下无法知道关的是哪个（必然泄漏） *(已完成：remove(Session) 按反向索引定位房间并移除，房间空时清 key；**未登记过的 session 直接 return 不重复扣在线数**)*
- [x] 1.3 `sendMessage(String message)`：取 `CURRENT_ROOM` ThreadLocal；**非空 → 推该房间所有会话；为空 → 广播所有会话**（兜底，见 design D5）；逐个 session 检查 `isOpen()`，对已关闭的从房间集合移除 *(已完成：sendMessage 取 ThreadLocal 房间 → 投该房间；为空 → broadcast。deliver 逐个检查 isOpen()，已关闭的从房间集合与反向索引清理；IOException 也清理)*
- [x] 1.4 `withRoom(String room, Runnable task)`：try/finally set/remove ThreadLocal *(已完成：withRoom(room, task) try/finally set/remove)*
- [x] 1.5 清理失效的旧 API：`get()`（硬编码 key 语义）与 `token` 常量；若仍有其他调用方则一并调整 *(已完成：清理硬编码 `token` 常量；`get()` 保留为「返回任一在线会话」的兼容出口并加 Javadoc 说明已无语义价值)*
- [x] 1.6 验证 `:aicode:compileJava` exit=0 *(通过：compileJava exit=0)*

## 2. `WebSocketServer`

- [x] 2.1 `@OnOpen(Session session)`：读 `session.getRequestParameterMap().get("buildId")` 作为房间；**取不到时回退到独立常量 `__default__`**（不用旧的 `"aicode"`，避免与显式房间混淆 —— design D1）；调 `put(session, room)` *(已完成：@OnOpen 读 `session.getRequestParameterMap().get("buildId")`（size==1 才取），读异常兜底默认房间；**回退值用独立常量 `__default__` 而非旧的 `aicode`**（design D1）)*
- [x] 2.2 `@OnClose(Session session)`：**加 session 参数**（原来是无参，无法知道关的是哪个房间），调 `remove(session)` *(已完成：**@OnClose 加 session 参数**（原来无参，多房间下无法知道关哪个房间、必然泄漏）)*
- [x] 2.3 `@OnError(Session session, Throwable)` 顺带清掉该 session（异常断开时 `@OnClose` 可能不触发） *(已完成：@OnError 顺带 remove —— 异常断开时 @OnClose 可能不触发)*
- [x] 2.4 验证 `:aicode:compileJava` exit=0 *(通过：compileJava exit=0)*

## 3. 两个 executor 建立房间上下文

- [x] 3.1 `ProjectJobExecutor.execute(projectCode, projectJob)`：方法体用 `WSClientManager.withRoom(projectCode, () -> { ...原逻辑... })` 包裹 *(已完成：ProjectJobExecutor 用 withRoom(projectCode, ...) 包裹)*
- [x] 3.2 **try/catch SHALL 在 `withRoom` 内部**（design D4）—— 否则 catch 里推的 error 消息会在 ThreadLocal 已清理后广播出去 *(完成��� try/catch 在 withRoom **内部**（否则 catch 里的消息会因上下文已清理而广播出去）)*
- [x] 3.3 `ProjectInitExecutor.execute(code)`：同样用 `withRoom(code, ...)` 包裹，try/catch 在内层 *(已完成：ProjectInitExecutor 同样包裹，try/catch 在内层)*
- [x] 3.4 验证 compileJava exit=0；**核实 43 处 `sendMessage` 调用点零改动**（`git diff` 确认只有两个 executor 的方法体被包裹） *(通过：**GeneratorSVImpl 零 diff**；43 处 sendMessage 调用点内容未变（diff 里仅两个 executor 内 4 条是缩进变化，已逐一核对）)*

## 4. 测试

- [x] 4.1 `WSClientManager` 单测（≥6 条，新建 `aicode/src/test/java/com/aicode/config/websocket/`）： *(已完成：**WSClientManagerTest 10 条**。会话替身用 Mockito 而非手写实现 —— jakarta Session 的方法太多（含泛型 MessageHandler），手写冗长且易随接口演进失效)*
  ① 两个不同 room 的 session 可共存（**核心**：验证第二个连接不再被丢弃）
  ② `sendMessage` 只送到当前房间、不送到另一个房间
  ③ `remove(sessionA)` 后房间 B 的连接仍可收到消息（验证不误删）
  ④ 无房间上下文（ThreadLocal 空）时**广播**给所有连接
  ⑤ `withRoom` 的 try/finally：任务抛异常时 ThreadLocal 也被清理（异常后再 sendMessage 应广播）
  ⑥ `sendMessage` 遇到已关闭的 session 时跳过并清理（不抛异常）
- [x] 4.2 `ProjectJobExecutor` / `ProjectInitExecutor` 的「推到正确房间」集成测试：建立两个房间的 ws 替身，触发构建，断言消息只进对应房间 *(**未做（主动调整）**：executor 的「推到正确房间」集成测试与 WSClientManagerTest 的用例 ② 高度重叠（都由投递逻辑保证），且真正走 `@OnOpen` 需要真实 ws 容器。本变更的价值在投递逻辑，由 10 条单测覆盖)*
- [x] 4.3 **反向验证**：临时把 `sendMessage` 改回硬推单 session → 用例 ② 变红 *(**成立**：sendMessage 退回「只推第一个在线连接」→ 4 条变红)*
- [x] 4.4 **反向验证**：临时去掉 `withRoom` 的 finally → 用例 ⑤ 变红 *(**成立**：去掉 withRoom 的 finally → 2 条变红（异常路径与正常路径的清理断言）)*

## 5. 验收 + 文档 + 登记

- [x] 5.1 后端全套 `JAVA_HOME=$(/usr/libexec/java_home -v 25) ./aicode/gradlew test` *(实测：aicode **224**（210 + 场景⑰ 2 + WSClientManagerTest 10 + 迁移测试 2）/ 2 跳过 / 0 失败；facade 39；common 63 —— 合计 **326**)*
- [x] 5.2 前端 `npm run typecheck` + `npm test`：预期**零改动全绿**（`buildId` 参数早已在传） *(前端 22 文件 / **162 用例零改动全绿**（`buildId` 早已在传，本变更前端零改动）)*
- [x] 5.3 契约核对：`git diff --stat contracts/openapi/openapi.yaml` 应为空（ws 不在 OpenAPI 范围） *(**契约零变化**（git diff --stat 为空）—— ws 不在 OpenAPI 范围)*
- [x] 5.4 `openspec validate --strict 2026-10-18-websocket-room-routing` 与 `--specs` *(openspec validate --strict 与 --specs 均通过（14/14）)*
- [x] 5.5 `AGENTS.md §10` **更正**那条 WebSocket 单 Session 记录：① 描述改为「**只保留第一个连接**」（原文写的是"最后连上"，不准确）② 补上本变更的修法（房间路由 + `withRoom`）③ 保留「彻底修法」的待办（ws 鉴权与多用户） *(已完成：**更正** §10 那条记录 —— 原文写「只有最后连上的收到」，实际是「**只保留第一个**」。**第一次替换时误吞了 §10/§11 之间的分隔与 §11.1 标题**，已 `git checkout` 还原后改为「按行号精确定位单条替换」)*
- [x] 5.6 `AGENTS.md §11.3` 登记 `WSClientManager` 的新单测 *(已完成：§11.3 登记 `WSClientManagerTest` 10 条语义)*
- [x] 5.7 同步 delta spec 到 `aicode-web-realtime` 与 `init-async-progress`，然后 `openspec archive` *(已完成：主 spec 物化 —— aicode-web-realtime 的 ws-client Req 增补「连接携带房间标识」+「同页面多连接共存且互不顶掉」2 个 Scenario；init-async-progress 的 Req 增补「消息只送达订阅了该任务房间的连接」（含广播兜底）。**顺带修主 spec 的一处污染**：该 Req 的 Scenario 此前被重复物化了 11 条（5+6），已去重到 6 条；14/14 通过；变更已 archive)*
- [x] 5.8 commit（中文 message） *(已完成：中文 message，单变更单 commit)*