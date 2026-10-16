# Design

## Context

后端 `ProjectJobController.execute` (line 40) `/project/job/execute` GET 端点已经存在：同步落 `project_job` 行（state = Executing）+ 触发 `@Async ProjectJobExecutor.execute`（VTH 池，不阻塞 HTTP 线程）+ `GenerateSVImpl.aiCode` 七步构建。`GeneratorSVImpl` 在第 117/129/138/155/163-165/169/180 行已多次 `WSClientManager.sendMessage(...)` 推送 free-form 日志。ws 路径 `/websocket.shtml` 由 `WebSocketServer` 维护（`aicode/src/main/java/com/aicode/config/websocket/WebSocketServer.java`），前缀 `"# "`（详见 `WSClientManager.sendMessage` 把 message 与前缀 join 后用 `session.getBasicRemote().sendText` 输出）。

前端 M2-2 已具备 `useAuth` / `<RequireAuth>` / `tokenStore` 三件套；`api.execute(params)` 已存在（orval M0 生成）；`getExecuteMockHandler` 已在 `test-setup.ts` 全局注册（M0 提供）。仅缺**前端 ws 客户端 + 实时日志面板**。

## Goals / Non-Goals

**Goals:**

- 新增 `aicode-web-realtime` capability：前端独立 ws 客户端 + `useBuildLog` hook + `<BuildLogPanel>` UI 三件套
- `ProjectListPage` 每行加"构建"按钮，点击展开 `<BuildLogPanel>` 接收实时日志
- 复用现有拦截器：`execute()` 走 HTTP `Authorization: Bearer <token>`；ws 用 `?token=${tokenStore.get()}` 兼容 fallback（详见 `auth-account` spec R5）
- `execute()` 抛 `ContractError` 走 `status = 'error'` 路径
- 卸载 / `clear()` 时释放 ws 连接

**Non-Goals:**

- 不接后端 ws 推送加固（`GeneratorSVImpl` 已推 ws）
- 不接 `ProjectDetailPage` 的"日志查询面板"（未来 M3-E 议程）
- 不接"在 ws 上订阅 ProjectJob.state 变更"实时面板（`ProjectJobVO.state` 需 HTTP 拉取；不是 ws 推送主题）
- 不接通知 toast / 多订阅路由 —— 本期只支持"项目列表 + 一键构建 + 实时日志"
- 不引入 STOMP / Socket.IO —— 用原生 `WebSocket` 即足够

## Decisions

### D1：`useBuildLog` 状态机去掉 `completed` 维度

**决定**：`status ∈ { 'idle' | 'running' | 'error' }`，无 `completed`。

**理由**：后端 `GeneratorSVImpl` 推送的是 free-form 日志（如 "git 努力下载中....."、"转化数据库结构与类模型成功！"），**没有 `[DONE]` sentinel**（实测 line 117/129/138/155/163-165/169/180）。要前端做 `completed` 视觉必须自造 sentinel（"以 `done.` 开头字符串"等），副作用是：
- 与后端字符串耦合破坏契约层无关性
- 设计脆弱（后端日后改字符串前端失配）
- 实际"完成"语义应由 `ProjectJobVO.state` 变更承担，那需要 `/project/job/load` 拉取，**不是 ws 主题**

`running → idle` 走用户 `clear()` 或组件 unmount（cleanup）。`error → idle` 同上。

**备选方案**：(a) 加 `[DONE]` sentinel —— 与后端字符串耦合；(b) 给 `ProjectJobService.execute` 后端加 `state` 字段 ws 推送 —— 改变协议 + 后端双线；(c) 走 D1 简化 —— 本变更采用。

### D2：ws URL 用原生 `WebSocket` 不封装 Promise

**决定**：`ws-client.ts` 不返回 Promise / observable，直接用 `new WebSocket(url)` + callback。理由：
- `onMessage` 是单条条目的 handler
- `close()` 同步调用 socket.close
- jsdom **不实现** WebSocket（junit 不支持）—— Vitest 测试里必须用 `vi.stubGlobal('WebSocket', MockWS)`，**或**用 `vi.mock` 拦截 `ws-client.ts`
- 简单 callback 模型在新能力 `aicode-web-realtime` 内更可读

**备选方案**：(a) Promise / async iterator —— 抽象过深，单条文本流用不上；(b) RxJS Subject —— 增加依赖；(c) 原生 + callback —— 本变更采用。

### D3：ws-client 自动重连一次而非永久

**决定**：掉线后间隔 500ms 自动重连一次，再次掉线停。

**理由**：
- 单次掉线几乎总是后端 VTH 池换页 / GC pause 暂时不可达；500ms 后通常就回来
- 永久重连在前端构建面板是噪音（用户已经走到别的页面去了）
- 该 trade-off 与 ws-client spec 第 1 个 Requirement 一致

**备选方案**：(a) 永久重连 —— 持 ws 资源过多；(b) 不重连 —— 用户体感差；(c) 一次重连 —— 本变更采用。

### D4：`<BuildLogPanel>` 在 `status === 'idle'` 不渲染任何 DOM

**决定**：组件在 idle 时 `return null`。

**理由**：
- ProjectListPage 行内已经有"构建"按钮；按钮本身已是 idle 视觉提示
- 不渲染空 div 不占页面空间，DOM 也更干净
- 测试断言稳定（`queryByTestId('build-log-panel')` 必为 null 表示干净)

### D5：M2-2 的 `useAuth` 体系，本变更不引入新 token 通道

**决定**：ws URL 用 `tokenStore.get()`；HTTP 用 axios 拦截器注入 `Authorization: Bearer <token>`。两路并行，符合 `auth-account` spec R5"URL 参数兼容"。

**理由**：
- 仓库现状：`LoginInterceptor`（`auth-account` spec 第 3 个 Requirement）已同时支持 `Authorization` 与 URL `?token=`
- 前端：M2-2 已实现的 `tokenStore` / 拦截器;ws 客户端只是另一处复用 `getToken()`
- 不重新设计 token 注入

### D6：测试用 `vi.mock` mock 整个 `ws-client.ts` 而非全局 WebSocket

**决定**：在 `use-build-log.test.tsx` 与 `BuildLogPanel.test.tsx` 中用 `vi.mock('./ws-client', ...)` 替换 `connect` 函数；测试简单可靠。

**理由**：
- jsdom 不实现 WebSocket；vi.stubGlobal('WebSocket') 需要 stub 一个完整类
- `vi.mock` 只 mock 一个 `connect` 函数：测试只关心 `onMessage` 回调被调、与 close/release 时序
- 与 M2-2 `auth-interceptor.test.ts` 中拦截 axios handler 的方式同思路

## Risks / Trade-offs

**[无 `[DONE]` sentinel 导致前端"完成"不可见]** → 用户必须自己点关闭/离开页面判断。本风险被设计为产品语义（构建是 fire-and-forget 后台任务，前端不需要"完成"通知，仅实时日志）。详情状态由 `ProjectJobVO.state` 拉取。

**[jsdom 测 ws 完整性]** → `vi.mock` 隔离了 ws-client 内部；hook 单测只校验生命周期（`startStream → execute + connect → ws msg → entries 追加 → close`）。真实 ws 端到端不在单测范围；手动 dev server + `npm run dev` 时验证。

**[WebSocket 协议不与 axios 同源]** → vite dev proxy 已配 `'/websocket': { target: 'ws://127.0.0.1:8080', ws: true }`，生产端则由 nginx `/websocket` location upstream ws:// backend 处理。本变更不涉及 nginx 改动，由仓库 M0 README 与 K8s 清单保持原状。

**[后端 ws 推送 free-form 是哪条字符串]** → `WSClientManager.sendMessage(msg)` 把 `msg` 加 `"# "` 前缀输出。我们 strip `"# "` 后保留原文本给用户看。如果后端有时推送空字符串（实测没有），`entries += { at, text: '' }` —— 类型上 `text: string` 接受。单测覆盖不到这一点；可以归 M3-C 议程（ProjectJobLogs 数据结构）。

## Migration Plan

单批交付（一个 feature commit）：

1. **新增 `aicode-web/src/lib/realtime/`**：
   - `ws-client.ts`：`connect` 函数 + 类型；
   - `use-build-log.tsx`：React hook；
   - `build-log-panel.tsx`：UI 组件；
   - 配套单测 `ws-client.test.ts`, `use-build-log.test.tsx`, `BuildLogPanel.test.tsx`。
2. **修改 `ProjectListPage`**：每行加"构建"按钮 + `useBuildLog` + `<BuildLogPanel>`。
3. **修改 `aicode-web/README.md`**：新增"实时推送（WebSocket）"章节。
4. **修改 `AGENTS.md §11.3`**：登记 M3-B 已合并事实段。
5. **执行 `npm run typecheck && npm test`**：预期 56 用例（M2-2 47 + 本变更 9）。
6. **同步 delta spec 到 `openspec/specs/aicode-web-realtime` 并 archive**。

**回滚**：单 commit revert 即恢复 ProjectListPage 无构建按钮 + 删除 `lib/realtime/`，后端无任何变更无须回滚。

## Open Questions

- 是否在 `BuildLogPanel` 里嵌一个"复制日志"按钮便于调试复盘——属于便利功能，本变更不做（M3+ 议程）
- 是否要把 `entries` 持久化（如 `sessionStorage`）以便用户切 tab 回看——本变更不做，每次 `startStream` 重置
