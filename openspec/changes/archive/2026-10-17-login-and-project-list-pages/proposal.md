# Proposal

## Why

M1 沉淀出了**消费契约产物的样板**（`unwrapSuccess` / `DisplayControl` / MSW），M2-1 让 `/project/list` 的契约形状真实化（`R<PageVO<ProjectVO>>` 含 22 字段）。但**还没有任何受保护页面真的接到了这条链上** —— `LoginPage` 是 M0 阶段写的骨架（不消费契约、不发请求），`/project/list` 这个 M2-1 已经具备的契约端点，至今没有前端消费者。

本变更把 M1 样板应用到两个真实业务页：

- **`/login`** —— 接入 `GET /login/signin`，成功后存 token 并跳到 `/project/list`。按 AGENTS.md §6.13，新接口**优先**走 `Authorization: Bearer` Header（M4 之前双轨兼容：URL `?token=` 仍可用，旧前端照常工作）；前端通过 axios 拦截器自动注入 Header，业务代码不必关心 token 在哪。
- **`/project/list`** —— 接入 `GET /project/list`，把分页结果按列表形式呈现，行点击跳 `/project/:code`（详情页 M3 接，本变更只留占位说明）。

这两条页面让 M1 沉淀的样板**第一次被多端点消费**，让"如何接下一个端点"五步样板的复用价值被实际验证；同时引入**前端登录态管理**这条新能力 —— token 持久化、路由守卫、HTTP 拦截器 —— 它们是为后续所有受保护页面服务的可复用单元。

## What Changes

- **新增 `LoginPage` 接入**：
  - 替换 M0 阶段占位骨架，调 `signin({ account, password })` —— 来自 `aicode-web/src/generated/api.ts`；
  - 经 `unwrapSuccess(resp)` 取 JWT 字面，`localStorage` 持久化，跳转 `/project/list`；
  - 失败以 `ContractError` 气泡呈现（沿用 M1 样板）。
- **新增 `ProjectListPage`**：
  - 调 `list2({ token, curPage, pageSize })` 取 `RPageVOProjectVO`（M2-1 已生成）；
  - 渲染表格列：`code` / `name` / `englishName` / `createTime`；
  - 行点击路由跳到 `/project/:code`（占位路由，M3 接入）。
- **新增 `src/lib/auth/` 单元**：
  - `token-store.ts`：读 / 写 / 清 `localStorage[aicode.token]`；
  - `auth-interceptor.ts`：注册 axios 拦截器，若 token 存在则注入 `Authorization: Bearer` Header；保留 `?token=` URL 参数的传输行为不动；
  - `useAuth.ts`：React 自定义 hook 暴露 `{ token, signIn, signOut, isAuthenticated }`。
- **新增 `src/pages/ProjectPlaceholderPage/`**：占位详情页；本期只显示 `code` 文案与"M3 详情页待接入"提示。
- **`App.tsx` 调整**：
  - `/login` 路由渲染 `LoginPage`；
  - 新增 `/project/list` 路由，渲染 `ProjectListPage`，被 `RequireAuth` 包裹（无 token 时跳 `/login`）；
  - 新增 `/project/:code` 占位路由，渲染 `ProjectPlaceholderPage`；
  - 占位路由 `/` 文案新增"查看项目列表"入口链接。
- **`main.tsx`**：在 `createRoot` 之前调用 `installAuthInterceptor()` 一次完成拦截器注册。
- **Vitest 单测**：
  - `token-store`：localStorage round-trip + 缺失时的 null 行为；
  - `auth-interceptor`：注入 Header / 保留 URL 参数 / 不存在 token 时不动请求；
  - `LoginPage`：MSW `getSigninMockHandler` 的成功 / 失败路径；
  - `ProjectListPage`：MSW `getList2MockHandler` 渲染 + 失败气泡。
- **`aicode-web/README.md` 的"How to wire next endpoint"** 新增"受保护端点"分支：登录后页面接入步骤。

**BREAKING**：无。本变更不修改后端契约、不修改 `auth-account` spec（前端拦截器就是 `auth-account` 行为在前端的对接，不改协议）；不修改运行时切换策略。`/project/:code` 是新路由，旧 13 MB Angular 7.0.4 产物不会被替换（M4 才切）。

## Capabilities

### New Capabilities

- `aicode-web-auth`：定义 aicode-web 的登录态管理与 HTTP 鉴权注入。token SHALL 持久化于 `localStorage`；axios 拦截器 SHALL 把 token 注入 `Authorization: Bearer` Header（不沿用 URL 参数形式作为新接口默认）；路由 SHALL 把无 token 受保护访问重定向到 `/login`；token SHALL 在 401 / 9007 时被清空且强制回登录页。本期只覆盖 `LoginPage` 与 `ProjectListPage` 两个使用方；该能力面向所有未来受保护页面。

### Modified Capabilities

（无。M0 / M1 / M2-1 已落地能力（`api-contract`、`api-contract-artifact`、`api-data-object`、`aicode-web-page`）继续生效。本变更只**新增**前端鉴权能力，不修改任何已有协议或行为。）

## Impact

- **代码**：
  - 新增 `aicode-web/src/lib/auth/{token-store.ts, auth-interceptor.ts, use-auth.ts}`（三件套）；
  - 替换 `aicode-web/src/pages/LoginPage.tsx`（当前是 M0 骨架）；
  - 新增 `aicode-web/src/pages/ProjectListPage/`（组件 + 单测）；
  - 新增 `aicode-web/src/pages/ProjectPlaceholderPage/`（占位）；
  - 新增 `aicode-web/src/components/RequireAuth.tsx`（路由守卫）；
  - 修改 `aicode-web/src/App.tsx`（路由 + 入口链接）；
  - 修改 `aicode-web/src/main.tsx`（拦截器注册）；
  - 修改 `aicode-web/src/test-setup.ts`（追加 `getSigninMockHandler` / `getList2MockHandler` 注册）。
- **契约产物**：`aicode-web/src/generated/` 在 M2-1 已包含 `signin` / `list2` 的请求函数与 MSW handlers，本变更**不重新生成**。
- **影响面**：
  - 与 M1 沉淀的样板完全正交 + 复用：`LoginPage` 与 `ProjectListPage` 都通过 `unwrapSuccess` 解构 `R`，通过 MSW 单测，通过 `<Link>` / react-router 路由跳转。
  - 旧 13 MB Angular 7.0.4 产物不受影响（运行时仍是其默认首页）。
  - **新增 axios 拦截器**只对新 React 前端代码生效；旧 Angular 产物走 `?token=` URL 参数，照常工作（双轨）。
- **风险**：
  - token 落 `localStorage` 是已知 XSS 取舍 —— M1 老前端也是这样做的（Angular 用 cookie）。本变更不引入新攻击面；安全加固属于 M3+ 议程（已记 AGENTS.md §11.1）。
  - 路由守卫 `RequireAuth` 解析 token 时仅看 localStorage 存在与否；过期由后端 `JwtToken`（默认 60 分钟）返回 9007 后再清理 —— 与 `auth-account` spec 的"SHOULD NOT provide refresh"约束一致。

