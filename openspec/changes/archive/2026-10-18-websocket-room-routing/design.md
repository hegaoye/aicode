# Design

## Context

`WSClientManager` 用**硬编码的单一 key** 管理会话：

```java
private static String token = "aicode";
public static void put(Session session) {
    Session ss = get();
    if (ss == null) {          // ← 只在该 key 不存在时才保存
        sessionMap.put(token, session);
```

**只保留第一个连接**，后续连接被丢弃。而前端两个 hook 的 URL **已经在传 `?buildId=<projectCode>`**
—— 后端 `@OnOpen` 完全忽略了它。

用户已拍板：**ThreadLocal 上下文** + **无房间时广播兜底**。

## Goals / Non-Goals

**Goals:**

- ws 连接按**房间**（项目编码）分别持有，多标签页共存；
- 消息只送到订阅了该任务房间的连接；
- **43 处 `sendMessage` 零改动**；
- `@OnClose` 能正确清理对应房间的连接（否则泄漏）。

**Non-Goals:**

- 不做鉴权（ws 的 token 校验是另一议题）。
- 不改 ws URL 形态（`buildId` 已在传）。
- 不做「同一标签页的多个连接」的场景区分（用户主动开才会）。

## Decisions

### D1：房间 key = URL 的 `buildId`，回退为默认房间

`WebSocketServer.onOpen` 读 `session.getRequestParameterMap().get("buildId")`；
取不到时用一个常量（如 `"__default__"）兜底，保证「URL 没带参数」的旧客户端仍能工作。

**为何不回退到固定 `"aicode"`**：那会让「无参数连接」和「显式 aicode 房间」混在一起。
独立常量更清晰，日志里也能分辨。

### D2：`remove(Session)` —— `@OnClose` 必须能拿到 session

现状是 `public void onClose()`（**无参**）+ `WSClientManager.remove()`（**按硬编码 key 删**）。
多房间下无法知道关的是哪个房间的连接，**必然泄漏**。

改为 `@OnClose public void onClose(Session session)` → `WSClientManager.remove(session)`，
内部按 session 反查房间（维护 `Map<Session, String>` 的反向索引，或直接遍历
`sessionMap` 找 value == session）。

**选择**：用 `ConcurrentHashMap<Session, String> sessionRooms` 做反向索引 —— O(1)，
且在 `put` 时一并写入。多一条映射的内存成本可忽略（连接数是用户标签页数量级）。

### D3：ThreadLocal 存「当前任务房间」，`sendMessage` 读取

```java
private static final ThreadLocal<String> CURRENT_ROOM = new ThreadLocal<>();

public static void withRoom(String room, Runnable task) {
    CURRENT_ROOM.set(room);
    try { task.run(); } finally { CURRENT_ROOM.remove(); }
}
```

`sendMessage(msg)`：取 `CURRENT_ROOM.get()`，非空则推该房间；**为空则广播给所有连接**。

**为何 try/finally 必须**：异步执行在虚拟线程上，若不清理，线程复用（VTH 由载体线程调度，
但 ThreadLocal 存在 VTH 自己的上下文里）时可能把房间带给**下一个不相关的任务** ——
那会导致消息推错房间，比不清理更糟。

**虚拟线程上的 ThreadLocal**：每个 VTH 有独立的 ThreadLocal 副本，不与载体线程共享，
故不会跨任务污染；`finally` 清理是为同一 VTH 被复用（Spring 的 VTH 不复用，但显式清理
是正确习惯且成本为零）。

### D4：不改 43 处调用点

43 处里有 17 处在 `GeneratorSVImpl` 的深层方法内。若逐个加 `room` 参数，
projectCode 要沿 `aiCode → xxx → yyy` 的方法签名下传 —— **动生成主链路**（AGENTS §5.5
称其为核心资产）。ThreadLocal 让入口建立上下文后，深层调用零感知。

**入口在哪**：
- `ProjectJobExecutor.execute(projectCode, projectJob)` → `withRoom(projectCode, () -> ...)`
- `ProjectInitExecutor.execute(code)` → `withRoom(code, () -> ...)`

**注意**：executor 的 try/catch 必须在 `withRoom` **内部**，否则 catch 里推的 error 消息
会因 ThreadLocal 已清理而广播出去。

### D5：无房间时广播（兜底，不静默退化）

`sendShellMsg` 之类无任务上下文的调用会广播给所有连接。

**理由**：最坏情况是「消息送达范围比预期宽」，而**丢弃**会让现有功能静默失效 ——
后者更难发现。

## Risks / Trade-offs

**[VTH 上的 ThreadLocal 与 Spring `@Async` 边界]** → `@Async` 方法体内 VTH 上下文是干净的；
`withRoom` 在方法体内 set，`sendMessage` 在同一 VTH 上读 —— 一致。已由集成测试守护。

**[`GeneratorSVImpl` 是否有不经 executor 的调用路径]** → 测试里可能有直接调用 `aiCode`。
那种情况下没有 `withRoom`，消息会广播 —— **与当前行为一致**，不算退化。

**[广播兜底可能把 A 项目的消息送到 B 项目]** → 只发生在「无房间上下文」的调用上
（`sendShellMsg`）。构建与解析的主路径都有房间。若未来发现误投，应给那类调用显式指定房间。

## Migration Plan

单批（一个 commit）：

1. `WSClientManager`：重写会话管理（`put(session, room)` / `remove(session)` /
   `sendMessage` 按房间 / `withRoom` / 反向索引）；
2. `WebSocketServer`：`@OnOpen` 读 URL 参数传 room；`@OnClose` 接收 session；
3. `ProjectJobExecutor` / `ProjectInitExecutor`：`withRoom` 包裹（try/catch 在内层）；
4. 测试：`WSClientManager` 单测（≥6 条）+ 两个 executor 的「推到正确房间」集成测试；
5. 前端零改动 + 契约核对 + AGENTS 登记 + validate + archive。

**回滚**：单 commit revert。

## Open Questions

- **ws 的鉴权** —— `@OnOpen` 不校验 token，任何人都能连并订阅任意房间（可偷看别人的构建日志）。
  当前是单用户内网工具，风险有限；但若将来支持多用户，**房间鉴权是必需的**。
  见 AGENTS §11.1「多用户」议题。
- **同房间多连接**（同一项目开两个标签页）→ 消息推给该房间**所有**连接（广播到房间内）。
  符合预期 —— 两个标签页都在看同一个项目。
- **历史房间清理** → `@OnClose` 正常清理即可。若客户端异常断开（网络），
  session 由容器回收但 `@OnClose` 是否触发取决于实现；残留 session 会在
  `sendMessage` 时因 `isOpen()` 为 false 被移除（现有逻辑已处理）。