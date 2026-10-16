# Tasks

## 1. ws-client + useBuildLog + BuildLogPanel（aicode-web-realtime 能力落地）

- [x] 1.1 新增 `aicode-web/src/lib/realtime/ws-client.ts`：导出 `connect(url, onMessage)` 返 `{ close(), send() }`；URL 含 `?token=<token>` 注入（`getToken()`）；首连接失败 500ms 后一次重连，再失败转交 hook 处理；类型定 `WSClient` 与 `WSMessageHandler`；验证：`npm run typecheck` exit=0 *(已完成：`tsc --noEmit` exit=0；模块作用域 `lastActiveClient` 防泄漏；close 后 `removeEventListener` 防止迟到消息)*
- [x] 1.2 新增 `aicode-web/src/lib/realtime/ws-client.test.ts`：3 用例 ① token 注入 URL ② 一次重连（用 vitest fake timers + 拦截 `window.WebSocket`）③ `close()` 后再收消息被忽略；验证：`npm test -- ws-client` 全绿 *(实测 7 用例通过：扩到 7 项覆盖 token 注入/无 token/前缀剥离/重连/close 不重连/不泄漏/send；用 `vi.stubGlobal('WebSocket', ...)` 替代 `window.WebSocket = ...` 因为 jsdom 把 globalThis.WebSocket 标 read-only)*
- [x] 1.3 新增 `aicode-web/src/lib/realtime/use-build-log.tsx`：导出 `useBuildLog(projectCode)` hook；4 状态 idle/running/error；`startStream()` 调 `api.execute + unwrapSuccess` + ws 客户端；ws 消息剥离 `"# "` 前缀后追加 entries；cleanup `close()` + `setStatus('idle')`；验证：`npm run typecheck` 0 错 *(已完成：`tsc --noEmit` exit=0；axios 拦截器自动注入 `Authorization: Bearer <token>`，hook 不再显式传 token)*
- [x] 1.4 新增 `aicode-web/src/lib/realtime/use-build-log.test.tsx`：4 用例 ① startStream → execute + open ws ② ws 推送 → entries 追加 ③ execute 抛错 → status='error' + 错误条目 ④ unmount 时 ws close + status 重置 idle；用 `vi.mock('./ws-client')` 隔离；验证：`npm test -- use-build-log` 全绿 *(实测 5 用例通过：4 项 + 1 项新增 clear() 行为——确保构建面板"关闭"按钮覆盖)`
- [x] 1.5 新增 `aicode-web/src/lib/realtime/build-log-panel.tsx`：默认导出组件 props `{ projectCode: string }`；`idle` 时 `return null`；`running/error` 渲染：标题 + 已运行时长（按 `startedAt` 计算）+ entries 列表（倒序）+ 关闭按钮（`clear()`）；`error` 多渲染一个 ❌ 徽章；验证：`npm run typecheck` 0 错 *(已完成：`tsc --noEmit` exit=0；倒序列表 + ❌ 徽章 + 关闭按钮 + `data-testid` 钉死结构)*
- [x] 1.6 新增 `aicode-web/src/lib/realtime/build-log-panel.test.tsx`：3 用例 ① running 状态渲染 ② idle 不渲染 ③ error 状态渲染 ❌ 徽章；用 `vi.mock('./use-build-log')` 隔离；验证：`npm test -- build-log-panel` 全绿 *(实测 3 用例通过；"关闭按钮 → clear()" 已转 `use-build-log.test.tsx` 用例 5，由 hook 层覆盖——避免测试双重职责)*

## 2. ProjectListPage 接入"构建"列 + `<BuildLogPanel>`

- [x] 2.1 修改 `aicode-web/src/pages/ProjectListPage/index.tsx`：表格新增「构建」列（每行一个按钮，state !== 'Enable' 时禁用）；点击触发 `useBuildLog(row.code).startStream()`；行下方折叠区显示 `<BuildLogPanel projectCode={row.code} />`；验证：`npm run typecheck` 0 错 *(已完成：`tsc --noEmit` exit=0；`state` 改 `buildingCodes: Set<string>`，按钮点击即加 Set 也展开；BuildLogPanel mount 时自动 startStream；`state !== Enable` 按钮 disabled；行下方 row `<td colSpan={6}>` 包 `<BuildLogPanel>`)*
- [x] 2.2 `ProjectListPage.test.tsx` 加 1 用例 触发"构建"按钮 → 期望 `<BuildLogPanel>` 元素出现在行内；用 `getExecuteMockHandler` 覆盖 succeed；验证：`npm test -- ProjectListPage` 全绿（含既有 3 + 新增 1） *(实测：ProjectListPage 共 5 用例通过——新增 2（点击展开 BuildLogPanel、state !== Enable 按钮 disabled）；既有用例仍绿；`vi.mock` 隔离 ws-client 用于面板不真连接)*

## 3. 文档与登记

- [x] 3.1 修改 `aicode-web/README.md`：在"How to wire next endpoint"章节新增"实时推送（WebSocket）"小节，含 ws-client / useBuildLog / BuildLogPanel 三件套入口说明 *(已完成：新增"实时推送（WebSocket）"章节含设计决策"无 completed 状态机"+接口接入四步)*
- [x] 3.2 修改 `AGENTS.md §11.3`：登记 M3-B 已合并（项目列表"构建"按钮 + 实时日志面板） *(已完成：登记段已就地说明本变更三件套 + 后端零改动事实)*
- [x] 3.3 执行 `openspec validate --strict 2026-10-18-project-execute-and-ws-log`，验证无校验错误 *(已完成：`Change is valid`，0 错)*
- [x] 3.4 同步 delta spec 到 `openspec/specs/aicode-web-realtime` 并 `openspec archive`；验证新能力物化；`openspec list --specs` 含 `aicode-web-realtime`（3 Requirements / 10 Scenarios） *(已完成：主 spec 物化 3 Requirements 10 Scenarios —— `useBuildLog` 状态机从 4 改为 3 状态（去 completed）并加 blockquote 设计解释；`openspec validate --specs` 12/12 通过；变更 mv 至 archive；`openspec list` No active changes)*
