# Proposal

## Why

`WSClientManager` 用一个**硬编码的单一 key** 管理会话：

```java
private static String token = "aicode";
public static void put(Session session) {
    Session ss = get();
    if (ss == null) {                      // ← 只在该 key 不存在时才保存
        sessionMap.put(token, session);
```

后果：**只保留第一个连接**。第二个标签页连上来时 `get() != null`，**被直接丢弃** ——
它的所有 ws 推送都收不到。

这在刚引入结构化进度协议后**变得更实际**：ws 通道上现在同时有 init 的进度事件与
build 的日志行，多个连接会混流；而 `type` 字段只能解决「能不能分辨」，
**解决不了「送到哪个标签页」**。

### 更正一处此前的错误描述

我在 `AGENTS.md §10` 记的是「只有**最后连上**的客户端能收到推送」。**不准确** ——
实际是**第一个**（后续连接因 `get() != null` 而不被保存）。

### 前端已经在传房间号，后端只是没用

两个 ws hook 的 URL 都带了 `?buildId=<projectCode>`：

```ts
`${proto}://${host}/websocket.shtml?buildId=${encodeURIComponent(projectCode)}`
```

而 `@OnOpen` 只是 `WSClientManager.put(session)` —— **完全忽略了 URL 参数**。

## What Changes

### 后端：按房间路由

- `WSClientManager.put(session, room)` —— 用 URL 的 `buildId`（回退为默认房间）作为 key；
- `WSClientManager.remove(session)` —— 按 session 反查房间再移除（**`@OnClose` 必须
  能拿到 session**，否则不知道关的是哪个房间）；
- `WSClientManager.sendMessage(msg)` —— 从 **ThreadLocal 上下文**取当前房间，推给该房间的
  所有会话；**无房间上下文时广播给所有会话**（兜底，不让现有功能静默退化）；
- 新增 `WSClientManager.withRoom(room, Runnable)` —— try/finally 设置/清理 ThreadLocal。

### 零改动点

**43 处 `sendMessage` 调用点一行不改** —— 房间由异步执行入口
（`ProjectJobExecutor` / `ProjectInitExecutor`）通过 `withRoom` 建立上下文。

**为何不用「逐个加 room 参数」**：那 43 处里有 17 处在 `GeneratorSVImpl` 的深层方法内，
要让它们知道 projectCode 就得把 projectCode 沿方法签名下传 —— **动生成主链路**，
风险显著高于 ThreadLocal。

### 前端：零改动

`buildId` 参数已在传。

## BREAKING

无。ws URL 与消息格式都不变，只是路由从「全局单连接」变为「按项目分房间」。
`sendShellMsg` 这类无任务上下文的调用走广播兜底。

## Capabilities

### Modified Capabilities

- **`init-async-progress`** —— MODIFIED 该 Req 的「结构化事件 SHALL 与既有自由文本日志共存
  于同一通道」：补上**同通道的连接隔离**要求 —— 同一通道上可能有多个连接（多标签页），
  消息 SHALL 只送达**与该任务相关**的那个连接，SHALL NOT 送达其他连接（否则用户在 A 页
  会看到 B 页的进度）。
- **`aicode-web-realtime`** —— MODIFIED Req「ws-client SHALL 是受鉴权 + 一次重连的轻
  WebSocket 包装」：明确 ws 连接携带**房间标识**，且同页面的多次连接 SHALL 可共存。

### New Capabilities

（无。）

## Impact

- **后端**：`WSClientManager`（重写会话管理）、`WebSocketServer`（`@OnOpen` 读 URL 参数、
  `@OnClose` 接收 session）、`ProjectJobExecutor` / `ProjectInitExecutor`（各自 `withRoom` 包裹）；
  `contracts/openapi/openapi.yaml`（零变化）。
- **测试**：新增 `WSClientManager` 单测（多连接分房 / 关闭只移除对应房 / 广播兜底 /
  `withRoom` 的 try-finally 清理）；`ProjectJobExecutor` 与 `ProjectInitExecutor` 的
  「消息推到正确房间」集成测试。
- **前端**：**零改动**。
- **风险**：**中**。改的是**消息投递的路由** —— 若房间解析有误会推错地方。
  缓解：广播兜底保证最坏情况退回当前行为；`@OnClose` 必须正确清理否则泄漏连接。