# Proposal

## Why

`ProjectListPage`（M2-2）目前只能展示项目列表 + 详情跳转，**没有任何触发构建或查看日志的路径**。而后端这条链路其实已经具备：

- `GET /project/job/execute?code={projectCode}` 路径（由 `ProjectJobController.execute` 在 `aicode/src/main/java/com/aicode/project/ctrl/ProjectJobController.java:40` 处理）—— 同步落 `project_job` 表 + 触发 `@Async ProjectJobExecutor`（VTH 池）→ `GenerateSV.aiCode` 七步构建
- `GeneratorSVImpl` 在多步推送 `WSClientManager.sendMessage(...)` 到 `/websocket.shtml`（实测 line 117/129/138/155/163-165/169/180）
- `/websocket.shtml` 已被 M0 阶段 `WebSocketServer` 实现（详见 `aicode/src/main/java/com/aicode/config/websocket/WebSocketServer.java`）
- vite.config.ts 已配 `'/websocket': { target: 'ws://127.0.0.1:8080', ws: true }` 转发
- ApiContractIT 已有 `WebSocketEndpoint` Scenario 组维护 `/websocket.shtml` 端点可达性

**本变更填补前端这一段**：让 M2-2 已具备 `useAuth` / `<RequireAuth>` / `tokenStore` 的 React 端可以接入 `execute()` + 真实时日志。**没有后端 ws 推送改造**——后端 `GeneratorSVImpl` 早已推 ws（M0 README §五 "Jenkins 式"），前端只需在构建期间连 ws。

仅前端 UI 范围（按 brainstorming "仅前端 UI" 决策）：ProjectListPage 每行加"构建"按钮 + 触发后实时日志面板；不接 ProjectDetailPage、不接后端 ws 推送加固。

## What Changes

- **新增 `src/lib/realtime/`** （aicode-web-realtime capability 落地）：
  - `ws-client.ts`：薄 WebSocket 包装 —— `connect(url, onMessage)` 返 `{ close(), send() }`；接受 message handler；自动重连一次；token 通过 query `?token=` 注入（与 dev 下 devtools-cookie 一致）。
  - `use-build-log.ts`：React hook `useBuildLog()` 返 `{ entries, status, startStream(code), clear() }`；调用 `api.execute({ code })` 触发构建 + 启动 ws 监听 + 收到 `[DONE]` 或 `state === 'Error'` 后自动停止。
  - `<BuildLogPanel projectCode />`：UI 组件 —— 仅当 `status === 'running'` 渲染，含开始时间、实时时间、log 条目列表、关闭按钮；`status === 'idle'` 不渲染（隐藏）。
- **修改 `ProjectListPage`**：
  - 每行新增"构建"按钮（仅在 `state` 为 `Enable` 时启用；否则禁用并提示"项目未启用"）；
  - 点击触发 `useBuildLog().startStream(row.code)`：先调 `api.execute({ code })` 返回 `R<ProjectJob>`，调 `unwrapSuccess` 取 `ProjectJobVO`（含 `code` 任务编码 + `state`）；
  - 同时显示 `<BuildLogPanel projectCode={row.code} />` 在该行下方（展开折叠）。
- **MSW 单测**：用 `vi.mock('...ws-client')` 拦截 ws —— 单测下不让 ws 真连接。MSW 仍可拦截 `execute` 端点（getExecuteMockHandler M0 已注册）。
- **`<RequireAuth>` 与 token 鉴权**：项目路由已 M2-2 加 `<RequireAuth>`；新增"构建"动作在受保护页面内，axios 拦截器自动注入 `Authorization: Bearer <token>`。**额外**：`ws-client.ts` 中 ws URL 用 `?token=${tokenStore.get()}` 拼接（backend `LoginInterceptor` 也支持该形式作为 fallback）。
- **新增 Vitest 单测**：
  - `BuildLogPanel.test.tsx`：4 用例 ① 启动后立即渲染含初始 entry ② log 推送追加条目 ③ 收到 [DONE] 后变 'completed' + 调用 close ④ error 状态渲染；
  - `use-build-log.test.tsx`：3 用例 ① startStream 触发 execute + open ws ② ws 推送 → entries 追加 ③ API 抛错时 status === 'error' + 含错误条目。
- **`aicode-web/README.md`**："如何接下一个端点" 五步样板章节**不变**（仍是项目列表/详情模板），但在本变更内会加一个独立的"实时推送（WebSocket）"小节，说明 ws 客户端 + hook 的入口，便于 M3-E 等后续实时推送场景复用。

**BREAKING**：无。

**前端生成物变化**：无新生成（`execute` / `getExecuteMockHandler` M0 已生成）。

## Capabilities

### New Capabilities

- `aicode-web-realtime`：定义前端 WebSocket 客户端与实时事件 hook 的可执行形状：`ws-client` SHALL 是受 `tokenStore` 鉴权并支持自动重连的轻包装；`useBuildLog` SHALL 是与 `api.execute` 协同的 React hook，包含 3 状态（`idle`/`running`/`completed`/`error`）；`<BuildLogPanel>` SHALL 在 `running`/`completed`/`error` 期间渲染历史 + 实时条目，关闭按钮 SHALL 释放 ws 连接。本期只覆盖构建日志这一种实时事件流。

### Modified Capabilities

（无。前端其他能力都不涉及。）

## Impact

- **代码**：
  - 新增 `aicode-web/src/lib/realtime/{ws-client.ts, use-build-log.ts, build-log-panel.tsx, *.test.ts(x)}`；
  - 修改 `aicode-web/src/pages/ProjectListPage/index.tsx`：每行加"构建"按钮 + 内联 `<BuildLogPanel>`；
  - 修改 `aicode-web/README.md`：新增"实时推送（WebSocket）"章节。
- **不影响**：后端零改动；`api-contract` 零改动；`aicode-web-page` / `aicode-web-auth` 零改动。
- **风险**：
  - 中等：M3-B 引入了项目首个 ws 客户端代码路径，jsdom 不原生支持 WebSocket，需用 `vi.mock(...)` 在测试中拦截（与 `MSW` 处理 HTTP 同思路）。Runtime 走原生 `window.WebSocket`，vitest 不会替我 mock globalThis，需在每个测试里显式 stub；
  - `vi.mock` 复杂度高，故将"ws 客户端 + hook + 面板"三件套作为 `aicode-web-realtime` 独立单元，单测 7 个用例全部通过才视为本变更落地。
- **测试基础设施**：`test-setup.ts` 不变；`getExecuteMockHandler` 已在全局注册；ws 通过 `vi.mock` 拦截。
