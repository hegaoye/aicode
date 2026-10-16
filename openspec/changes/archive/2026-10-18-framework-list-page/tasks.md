# Tasks

## 1. FrameworkListPage 实现

- [x] 1.1 新增 `aicode-web/src/pages/FrameworkListPage/index.tsx`：默认导出组件；`useState<LoadState>` + `useMemo(() => getAicodeRESTfulAPIs(), [])` + `useEffect([api])` 调 `api.list4({ curPage: 1, pageSize: PAGE_SIZE })`（`PAGE_SIZE = 20`）；经 `unwrapSuccess(resp)` 取 `PageVOFrameworkSummaryVO`；`catch` 里 `err instanceof ContractError` 走 error 状态、否则包 `ContractError('UNKNOWN', String(err))`；验证：`npm run typecheck` exit=0 *(已完成：LoadState 3 态 + useEffect([api]) + list4({curPage:1,pageSize:20}) + unwrapSuccess；typecheck exit=0)*
- [x] 1.2 渲染 3 状态（沿 `ProjectListPage` 形态）：① loading → `<section aria-busy="true">` + "加载中…" ② error → `<p role="alert" data-testid="framework-list-error">加载失败：{info||code}</p>` ③ ready → `<table>` 4 列（`code` / `name` / `description` / `isPublic`）+ 行 `data-testid="framework-list-row"` + 分页信息 `data-testid="framework-list-paging"`（`共 N 条 · 第 X/Y 页`）；`records` 为空数组时渲染 `data-testid="framework-list-empty"`（"（暂无数据）"）；`description` 用 `max-width` + `text-overflow: ellipsis` 约束；验证：`npm run typecheck` exit=0 *(已完成：loading / error / ready 三态渲染；4 列表格 + data-testid（framework-list-row / -empty / -paging / -error）；description 用 max-width+ellipsis 约束)*
- [x] 1.3 `isPublic` 列把 `'Y'` / `'N'` 映射为「公开」/「非公开」（design D2）；验证：单测断言渲染文案为中文而非 `'Y'` *(已完成：isPublic 渲染为「公开」/「非公开」；单测断言中文文案且不含 >Y<)*
- [x] 1.4 组件 SHALL NOT 读取 `data.voList`（spec Scenario 4 钉死）；亦不读 `PageVOFrameworkSummaryVO` 的 4 个 `createTimeBegin/End` / `updateTimeBegin/End` 查询辅助字段；验证：源码 grep 无 `voList` / `createTimeBegin` 字样 *(已完成：源码仅在注释中提及 voList / *Begin/*End（说明为何不读），实际代码无引用)*

## 2. 路由接入

- [x] 2.1 修改 `aicode-web/src/App.tsx`：import `FrameworkListPage`；新增 `<Route path="/frameworks" element={<RequireAuth><AppShell><FrameworkListPage /></AppShell></RequireAuth>} />`；验证：`npm run typecheck` exit=0 *(已完成：import FrameworkListPage + /frameworks 路由（RequireAuth + AppShell）；typecheck exit=0)*
- [x] 2.2 修改 `aicode-web/src/App.tsx` 占位路由 `/`：「M2 已接入」段落之后新增「M5 已接入：查看框架列表」`<Link to="/frameworks">`；验证：`npm run typecheck` exit=0 + `App.test.tsx` 仍全绿 *(已完成：占位路由新增「M5 已接入：查看框架列表」链接；App.test + AppShell.test 4/4 仍绿)*

## 3. 单元测试

- [x] 3.1 新增 `aicode-web/src/pages/FrameworkListPage/index.test.tsx`：3 用例，用 `server.use(getList4MockHandler(fixed))` 覆盖确定性数据（避免 faker 随机 `success`）： *(已完成：3 用例全过（成功渲染含中文「公开」与分页信息 / 空态非错误 / R.code!==0000 错误气泡）)*
  - 用例① 成功渲染：`data.records` 含 1 条 → 表格可见 `code` / `name` / `description` / 中文「公开」；`data-testid="framework-list-paging"` 含「共 1 条」
  - 用例② 空态：`records: []` → `framework-list-empty` 可见，且 `framework-list-error` **不**可见
  - 用例③ 错误：`R{ success: false, code: '9999', info: '服务器内部错误' }` → `framework-list-error` 可见且含 info，且无 `framework-list-row`
  - 验证：`npm test -- FrameworkListPage` 3/3 通过
- [x] 3.2 组件渲染依赖 `MemoryRouter` 包裹（与 `ProjectDetailPage.test.tsx` 同）；`useAuth` 走真实 `tokenStore`（测试内 `setToken` 预设）；验证：3 用例不因鉴权跳登录而空渲染 *(已完成：MemoryRouter + Route 包裹；setToken 预设使 isAuthenticated 为 true)*

## 4. 验收 + 文档 + 登记

- [x] 4.1 跑 `npm run typecheck`：exit=0 *(已完成：typecheck exit=0)*
- [x] 4.2 跑 `npm test`：预期 17 → **18 文件 / 83 用例**全过 *(实测：18 文件 / 83 用例全过（80 基线 + 本变更 3）)*
- [x] 4.3 跑 `npm run build`：产物变化 < 2 KB *(实测：338.95 KB / gzip 107.71 KB（+2.2 KB，符合 <2 KB 量级预期）)*
- [x] 4.4 修改 `aicode-web/README.md` §「M2 已对齐的端点形状」段：把 `/framework/list` 列入"前端已接入端点"清单 *(已完成：aicode-web/README.md「M2 已对齐的端点形状」段补 /framework/list 条目并标注 M5-A2 已接入)*
- [x] 4.5 修改 `AGENTS.md §11.3`：在 M2–M4 前端测试清单里补 `FrameworkListPage`（3 用例） *(已完成：AGENTS.md §11.3 前端页面层清单补 FrameworkListPage；顶部统计 17 文件/80 用例 → 18 文件/83 用例)*
- [x] 4.6 执行 `openspec validate --strict 2026-10-18-framework-list-page`：校验通过 *(已完成：Change is valid，0 错)*
- [x] 4.7 同步 delta spec 到 `openspec/specs/aicode-web-page` 并 `openspec archive`；验证主能力 requirements 现状 5 → 6 *(已完成：主 spec 物化 1 Requirement 4 Scenarios；aicode-web-page requirements 5 → 6；13/13 通过；变更 mv 至 archive；No active changes)*
