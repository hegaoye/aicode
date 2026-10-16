# Tasks

## 1. 鉴权三件套（token-store / auth-interceptor / use-auth）

- [x] 1.1 新增 `aicode-web/src/lib/auth/token-store.ts`：导出 `getToken() / setToken(token) / clearToken()`，内部读写 `localStorage["aicode.token"]`，缺省返 `null`；验证：`npm run typecheck` 0 错；并配套 `token-store.test.ts` 覆盖 ① round-trip ② 缺省值 ③ clear 后再 get 返 null *(已完成：7 用例通过；包含 trim、storage 抛错兜底)*
- [x] 1.2 新增 `aicode-web/src/lib/auth/auth-interceptor.ts`：导出 `installAuthInterceptor()`，注册 axios request 拦截器（token 非空时设 `Authorization: Bearer <token>`）+ response 拦截器（`resp.data.code === "9007"` 或 HTTP 401 时 `clearToken()` + `window.history.replaceState(null, "", "/login")`）；验证：`npm run typecheck` 通过；`auth-interceptor.test.ts` 用 vitest + msjsdom 验证 Header 注入 + 9007 行为 *(已完成：6 用例通过；包含重复安装幂等、AxiosHeaders.set 形式注入、9007 清 token)*
- [x] 1.3 新增 `aicode-web/src/lib/auth/use-auth.tsx`：导出 `useAuth()` hook，返 `{ token, signIn, signOut, isAuthenticated }`；`signIn(token)` 即 `setToken(token)`；`signOut()` 即 `clearToken()` 并触发 reload；验证：`use-auth.test.tsx` 覆盖 signIn / signOut 流程与 isAuthenticated 切换 *(已完成：7 用例通过；包含 storage 事件多 tab 同步、引用稳定、初始持久化感知)*
- [x] 1.4 修改 `aicode-web/src/main.tsx`：在 `createRoot` 之前 import + 调用 `installAuthInterceptor()` 一次；验证：`npm run typecheck` 0 错 *(已完成：main.tsx 已 import + 调；typecheck 0 错)*

## 2. 路由守卫

- [x] 2.1 新增 `aicode-web/src/components/RequireAuth.tsx`：导出默认组件，子节点为 ReactNode；内部 `useAuth()` 取 token；空时返 `<Navigate to="/login" state={{ from: location.pathname }} replace />`；非空时 `return children`；验证：`npm run typecheck` 通过 *(已完成：组件落地 + typecheck 0 错)*
- [x] 2.2 修改 `aicode-web/src/App.tsx`：在 `/project/list` 与 `/project/:code` 路由外层包 `<RequireAuth>`；占位路由 `/` 文案新增 `<Link to="/project/list">查看项目列表</Link>`；验证：`npm run typecheck` 0 错 *(已完成：App.tsx 三个受保护路由都用 `<RequireAuth>` 包裹，包含 `/displayAttributes`、`/project/list`、`/project/:code`；typecheck 0 错)*

## 3. LoginPage 接入

- [x] 3.1 替换 `aicode-web/src/pages/LoginPage.tsx`：保留视觉与表单（loading 状态、提交禁用、错误气泡样式）；`onSubmit` 触发 `signin({ account, password })` 后用 `unwrapSuccess(resp)` 取 JWT string；存 token + 跳 `/project/list`；失败以 `ContractError.info` 渲染气泡；验证：`npm run typecheck` 0 错；`LoginPage.test.tsx` 用 MSW `getSigninMockHandler` 覆盖成功 / 失败两种场景 *(已完成：3 用例通过；包含 success / wrong-password / R.code=!="0000" 三种场景)*
- [x] 3.2 反向验证：临时把 `tokenStore.set` 注释掉 → 登录成功但 `localStorage["aicode.token"]` 不写入 → 测试断言"导航到 /project/list"失败；恢复后通过 *(已完成：删除 `signIn(token)` 后 LoginPage.test 1 用例超时失败；恢复后 3/3 通过)*

## 4. ProjectListPage 接入

- [x] 4.1 新增 `aicode-web/src/pages/ProjectListPage/index.tsx`：默认导出组件；`useState` + `useEffect` 调 `list2({ token: tokenStore.get() ?? "", curPage: 1, pageSize: 10 })` 经 `unwrapSuccess(resp)` 取 `PageVOProjectVO`；渲染 `<table>` 含 5 列：`code` / `name` / `englishName` / `createTime` / 「详情」操作；每行"详情"`<Link to={`/project/${row.code}`}>` 跳占位详情；加载与错误状态分别渲染；验证：`npm run typecheck` 0 错 *(已完成：组件落地；`createTime` 显示用 `formatDate` 把 ISO 转 `yyyy-MM-dd HH:mm:ss`；typecheck 0 错)*
- [x] 4.2 新增 `aicode-web/src/pages/ProjectListPage/index.test.tsx`：MSW `getList2MockHandler` 覆盖一组 records → 期待表格含首行 `code` / `name` 文本；再覆盖返回 `code: "9007"` → 期待错误气泡出现；验证：`npm test -- ProjectListPage` 全绿 *(已完成：3 用例通过；包含 records / 空 records / R.code="9007" 三种场景)*

## 5. 占位详情页

- [x] 5.1 新增 `aicode-web/src/pages/ProjectPlaceholderPage/index.tsx`：默认导出组件；从 `useParams()` 取 `code`；渲染 `<AppShell><p>项目 {code} 详情（M3 待接入）</p></AppShell>`；验证：`npm run typecheck` 0 错 *(已完成：`useParams<{ code: string }>` 取参 + 占位提示文；typecheck 0 错)*

## 6. 拦截器测试

- [x] 6.1 注册 `getSigninMockHandler` 到 `aicode-web/src/test-setup.ts` 的 `setupServer(...)` 列表；验证：所有现有 Vitest 用例仍全绿（含现有 17 用例） *(已完成：`getSigninMockHandler` 注册；之前用例不受影响 —— 全部 43/43 通过)*
- [x] 6.2 反向验证：临时在 `auth-interceptor.ts` 中把 `Authorization` 头赋值注释 → `auth-interceptor.test.ts` 期望失败 `Authorization: Bearer xxx` 不在 headers；恢复后通过 *(已完成：删除 `if (token) { headers[...] = ... }` 后 2 用例失败；恢复后 6/6 通过)*

## 7. 文档与登记

- [x] 7.1 修改 `aicode-web/README.md`：在"How to wire next endpoint"章节新增「受保护端点」分支，覆盖 (a) 在受保护页面外层包 `<RequireAuth>` (b) 调受保护端点时用 `tokenStore.get()` 填充 token (c) MSW handler 注册到 setupFile (d) 失败用 `ContractError` 气泡 *(已完成：README 新增"受保护端点（鉴权接入）"章节，含五步要点)*
- [x] 7.2 修改 `AGENTS.md §11.3`：登记 M2 已合并（登录页接入 + 项目列表页接入 + 后端联调 Bearer 拦截器） *(已完成：AGENTS.md §11.3 登记 M2 三段新段 —— project-list-vo-contract、login-and-project-list-pages、aicode-web-auth 引入)*
- [x] 7.3 执行 `openspec validate --strict 2026-10-17-login-and-project-list-pages`，验证无校验错误 *(已完成：`Change is valid`，0 错)*
- [x] 7.4 同步 delta spec 到 `openspec/specs/aicode-web-auth` 并执行 `openspec archive`；验证新能力已物化、`openspec list --specs` 含 `aicode-web-auth` *(已完成：主 spec 物化 5 Requirements / 9 Scenarios；`openspec validate --specs` 11/11 通过；变更 mv 至 archive；`openspec list` No active changes)*
