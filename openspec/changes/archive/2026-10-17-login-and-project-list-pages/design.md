# Design

## Context

M1 已经沉淀 `unwrapSuccess`（在 `src/lib/contract-response/index.ts`）+ `DisplayControl`（在 `src/lib/display-controls/index.tsx`）+ MSW 全局 lifecycle（在 `src/test-setup.ts`）。前端 `aicode-web/src/generated/api.ts` 由 orval 从 `contracts/openapi/openapi.yaml` 导出，包含 41 个端点的请求函数。

后端两相关能力已具备：

- **`/login/signin`** — `auth-account` spec 已定义：账户存在且口令对 → `code:0000`, data 为 JWT 字符串；否则 `code:0000` + 空字符串（或失败码）。`LoginInterceptor` 按 `auth-account` spec 规定 Header Bearer 优先、URL `?token=` 兼容。
- **`/project/list`** — M2-1 改造后返 `R<PageVO<ProjectVO>>`，前端生成物 `projectVO.ts` + `pageVOProjectVO.ts` + `rPageVOProjectVO.ts` 已就绪。

`aicode-web/src/test-setup.ts` 已注册 `getList2MockHandler`；但未注册 `getSigninMockHandler` —— M2-2 落地时需追加。

## Goals / Non-Goals

**Goals:**

- 替换 M0 `LoginPage` 骨架，让其消费 `/login/signin`、存 token、跳 `/project/list`；
- 实现 `ProjectListPage` 消费 `/project/list`，行点击路由到 `/project/:code` 占位；
- 引入 `src/lib/auth/` 三件套（`token-store` / `auth-interceptor` / `use-auth`）+ `<RequireAuth>` 路由守卫；
- 落地后端 9007 触发自动登出与回登录页；
- 在 Vitest 单测里实证：拦截器 Header 注入、`ProjectListPage` 渲染、登录流走通；
- README 新增"受保护端点"分支，让 M3+ 接入新页面有样板可循。

**Non-Goals:**

- 不接 `/project/:code` 详情页（M3 范围），本变更留 `<ProjectPlaceholderPage>` 占位；
- 不接账户信息获取（`/account/load`），本变更登录后无"当前账户名"展示（未来 M3 顺便加上）；
- 不引入 React Query / SWR 等数据请求库（M1+M2 都是 `useState+useEffect`，本变更沿用）；
- 不修后端 `LoginController` 的 token 传递行为（Header Bearer 优先这条已固化于 `auth-account` spec）；
- 不引入状态管理库（M3 涉及多页共享时再评估 D6）；
- 不在 React 前端实现 refresh token（`auth-account` spec 明确规定不可实现）。

## Decisions

### D1：拦截器只动 Header，不动 params.token

**决定**：axios 拦截器只设 `Authorization: Bearer <token>` 头；或val 生成的 `list2({ token, curPage, pageSize })` 签名要求显式传 `params.token`，调用方仍按签名传值。

**理由**：

- 后端 `LoginInterceptor` 的优先级（`auth-account` spec）是 Header 优先 — 传入 `?token=` 即便"重复"，后端也只取 Header。这避免了"拦截器误污染 `signin` URL 把 token 写到 query"的风险。
- 不动 params 让前端代码显式承认 "我用的是 query 兼容双轨"，符合 AGENTS.md §6.13 "URL 参数作为遗留接口兼容、不为新接口默认"。
- 一旦 ORVAL 后续移除 `params.token`，前端代码随即报错，TypeScript 守护 shape drift。

**备选方案**：(a) 拦截器自动注入 `params.token` 同时设置 Header —— 会污染 `signin` URL（`?account=&password=&token=`），引入 XSS-with-Referer 风险面。(b) 在 `api.ts` 外包一层 thin client 重写所有受保护端点引入 token —— 范围扩大、维护面增加。

### D2：token-store 用 localStorage 的固定 key，不引入 secure storage

**决定**：用 `localStorage["aicode.token"]` 作为 token 持久化入口；写入 / 清除 / 读取封装在 `token-store.ts` 内。

**理由**：

- 与 M0 旧前端（Angular 7.0.4）使用的 cookie / localStorage 行为延续；
- `localStorage` 在所有现代浏览器都可用、同步 API、与 React 渲染周期自然衔接；
- 不引入新依赖（如 `js-cookie`）保持 `package.json` 不变。

**安全权衡**：localStorage 易受 XSS 取舍 — 与现有前端一致；AGENTS.md §11.1 已记 "token 落 cookie / HttpOnly"作为安全加固议程。本变更**不引入新攻击面**。

**备选方案**：(a) `js-cookie` —— 增加依赖、设计逻辑略复杂（本变更不必要的范围扩张）。(b) `sessionStorage` —— 浏览器关闭后丢失，不适合长期 JWT。

### D3：`useAuth` hook 返回 React 16 兼容的 stable 函数引用

**决定**：`useAuth` 用 `useCallback` 包装 `signIn` / `signOut` / `setToken`，依赖固定（仅依赖 `useState` 设定的 `token`）。

**理由**：

- 避免每次重新渲染生成新的函数引用引起下游 `<RequireAuth>` 与 `<LoginPage>` 的子组件不必要重渲染；
- 与现有 `DisplayAttributesPage` 不引入新模式（仍用 `useState` + `useEffect`）。

**备选方案**：(a) `useReducer` —— M1 已落地 `useState`，本变更不引入新模式。(b) Context 单例 —— M3 涉及多页共享时再评估。

### D4：`RequireAuth` 用 `Navigate` 而非 `Redirect`（React Router v6 API）

**决定**：`<RequireAuth>` 在 `token === null || token === ""` 时返 `<Navigate to="/login" state={{ from: location.pathname }} replace />`。

**理由**：

- React Router v6 重命名了 `Redirect` → `Navigate`，两者内部完全等价但 6.x 推荐 `Navigate`；
- `state.from` 让 LoginPage 登录成功后能 `navigate(-1)` 或读 `useLocation().state.from` 跳回原页面（M3 一并接，本变更不接）；
- `replace` 让用户不会因守卫跳转被加到 history stack 中。

**备选方案**：(a) HOC（`withAuth`）— 也可行，但 hooks 现代表达更整齐。(b) `<Redirect>` — 已 deprecated。

### D5：9007 自动登出仅清 token + 跳登录，不在拦截器里抛错

**决定**：axios 响应拦截器发现 `R.code === "9007"` 或 `axios` 默认 reject 的 `AxiosError.code === "ERR_BAD_REQUEST"` 401 时清 token 并 `window.history.replaceState` 跳 `/login`；**不抛错**给业务代码，让业务调用方仍以为请求成功，最后发现 UI 已被替换。

**理由**：

- 后端 9007 是后端把"鉴权失败"的语义编码进统一响应，业务代码不必再做"code==9007 判别"处理（重复检测）；
- 抛出错误让组件看到失败，会渲染错误气泡，然后导航跳登录 —— 用户在一帧内看到 `ContractError` 红字 + 跳转，体验割裂；
- "直接跳"是后端协议层语义，前端拦截器是协议守护者。

**备选方案**：(a) 拦截器抛 `ContractError` 让上层处理 — 上层每个组件都得判 9007，重复样板。(b) 拦截器吞掉错误，让 `Promise<void>` 返回 — 与 TS 类型契约不一致。

### D6：MSW test-setup.ts 中追加 `getSigninMockHandler` 注册

**决定**：在 `aicode-web/src/test-setup.ts` 内的 `setupServer(...)` 列表里追加 `getSigninMockHandler()`，与既有 7 个 mock handlers 并列。

**理由**：

- 与 M2-1 设计的"邻居端点提前注册"原则一致（即便本批次测试用例不直接访问 signin 端点，邻居注册避免 `ListPage` 单测缺拦截）；
- 不引入新的 Node 进程级拦截器配置，setupServer 仍是单一来源。

**备选方案**：(a) 让每个测试各自 `setupServer(getSigninMockHandler())` — 与现有全局模式不一致，复用率低。

### D7：`/project/:code` 占位页仅显示路由参数与提示文字

**决定**：`ProjectPlaceholderPage` 用 `useParams()` 拿到 `:code` 渲染于 `<p>` 中，并附"M3 详情页待接入"一句话文案。

**理由**：

- 让"行点击跳详情"链路在 M2-2 内完整跑通（演示路由 + 参数传递），不至于"链接点了 404"；
- 占位文字也防止有人把 `/project/:code` 当作已实现页面误用 —— 占位的明示是开发纪律；
- 不引入任何端点消费（避免触及 M2-1 之外的范围）。

**备选方案**：(a) `<Route path="/project/:code" element={<Navigate to="/project/list" replace />}>` — 体验差，"我点进去又被弹回"。(b) 真接 `loadByCode` 详情页 — M3 范围。

## Risks / Trade-offs

**[msw 拦截器对 4xx 响应不能返 `R.code="9007"` 形态]** → MSW 在 `http.get('*/login/signin', () => HttpResponse.json({ success: false, code: "9007", info: "..." }, { status: 200 }))` —— 这是 200 + body 内 R 失败。响应拦截器只需对 `resp.data.code === "9007"` 做处理；如果未来真有 `401 + 200 response` 反例，需要 second branch。

**[`getList2MockHandler` 已注册但测试可能因 `RPageVOProjectVO` schema 与设计预期不一致而失败]** → M2-1 已实测 `getList2MockHandler()` 通过 M2-1 ApiContractIT；本变更不会重新生成类型，schema 与约定一致。

**[路由守卫和 LoginPage 跳转之间的 race condition]** → `Navigate` 在 React Router 6 是同步换页（不像旧的 `<Redirect>` 异步 redirect）；用户提交登录后 token 写入 localStorage，下一帧 `<RequireAuth>` 就能读到，不会"刚登录又跳回登录"。

**[9007 拦截器自动跳登录会丢"原本要去的页面"信息]** → 通过 `state: { from: location.pathname }` 传入；`LoginPage` 登录成功后可读 `useLocation().state?.from` 跳回（M3 一并接）。本变更 LoginPage 登录成功后**总是**跳 `/project/list`（按用户期望）。

**[axios 拦截器注册与 React 渲染关系]** → `installAuthInterceptor()` 在 `main.tsx` 之前调用；同一应用只注册一次。Vitest 的 jsdom 环境会因 `window` 存在而正常工作；测试用例自己可手动调用 `server.resetHandlers` 重置拦截状态。

## Migration Plan

单批交付（一个 feature commit）：

1. **新增 `aicode-web/src/lib/auth/`**：
   - `token-store.ts`：三个函数 `get / set / clear`
   - `auth-interceptor.ts`：`installAuthInterceptor()` 一次注册 request + response interceptor
   - `use-auth.tsx`（或 `.ts`）：`useAuth()` hook
2. **新增 `aicode-web/src/components/RequireAuth.tsx`**
3. **新增 `aicode-web/src/pages/ProjectListPage/`**：
   - `index.tsx` 渲染 `PageVOProjectVO.records` 表格
   - `index.test.tsx` MSW 拦截 + 渲染 + 失败分支
4. **新增 `aicode-web/src/pages/ProjectPlaceholderPage/index.tsx`**
5. **替换 `aicode-web/src/pages/LoginPage.tsx`**：M0 骨架 → M2-2 真实调用
6. **修改 `aicode-web/src/App.tsx`**：注册 `/project/list` / `/project/:code`；接入 `<RequireAuth>`
7. **修改 `aicode-web/src/main.tsx`**：调用 `installAuthInterceptor()`
8. **修改 `aicode-web/src/test-setup.ts`**：注册 `getSigninMockHandler`
9. **修改 `aicode-web/README.md`**：新增"受保护端点"分支
10. **新增 `AGENTS.md §11.3` 登记**：M2 已合并事实段

**回滚**：单 commit revert 即恢复；token 是 localStorage 内容不会造成服务端脏数据。

## Open Questions

- `displayAttributes` 现有路由在 AppShell 内嵌 `<DisplayAttributesPage />`，**没有**包裹 `<RequireAuth>` —— M2-2 是否一并加上守卫？回答（默认）：本变更暂不动它，让"现有 M1 页面不强制登录"成为单独决定；M3 统一讨论守卫范围。
- `ProjectListPage` 跳 `/project/:code` 占位后，在占位页用 `<Link to="/project/list">` 还是 `navigate(-1)` 回？回答（默认）：M2-2 占位页只显示提示文字，不放返回 link（M2-2 后可由用户决定）。M3 详情页接入后再统一跳转体验。
