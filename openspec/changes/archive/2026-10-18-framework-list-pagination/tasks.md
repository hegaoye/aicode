# Tasks

## 1. FrameworkListPage 翻页改造

- [x] 1.1 修改 `aicode-web/src/pages/FrameworkListPage/index.tsx`：新增 `const [curPage, setCurPage] = useState(1)`；`useEffect` 依赖数组由 `[api]` 改为 `[api, curPage]`；effect 内 `api.list4({ curPage, pageSize: PAGE_SIZE })`；验证：`npm run typecheck` exit=0 *(已完成：curPage useState(1)；effect 依赖 [api, curPage]；list4({curPage, pageSize: PAGE_SIZE})；typecheck exit=0)*
- [x] 1.2 表格下方新增 `<nav data-testid="framework-list-pager">`，内含：「上一页」`<button data-testid="framework-list-prev" disabled={curPage <= 1} onClick={() => setCurPage((p) => Math.max(1, p - 1))}>` + 现有 `data-testid="framework-list-paging"` 页码文本 + 「下一页」`<button data-testid="framework-list-next" disabled={curPage >= totalPage} onClick={() => setCurPage((p) => p + 1)}>`；单页时（`totalPage <= 1`）两按钮均禁用但控件仍渲染（design D4）；验证：`npm run typecheck` exit=0 *(已完成：表格下方 nav[framework-list-pager] + prev/next 按钮 + disabled 边界；单页时均禁用但控件渲染；标题区分页为 framework-list-total（避免 testid 重复）)*
- [x] 1.3 翻页期间 SHALL 进入 loading 态（不保留旧页数据）—— 现有 effect 在依赖变化时已经把 `state` 保持为上一次的 ready；SHALL 显式在 effect 起始处 `setState({ kind: 'loading' })` 以满足 spec Scenario 3；验证：单测覆盖"翻页后旧行消失" *(已完成：effect 起始处 setState({kind:loading})，翻页期间不回显旧页数据)*

## 2. 单元测试

- [x] 2.1 修改 `aicode-web/src/pages/FrameworkListPage/index.test.tsx`，保留既有 3 用例并新增 2 条： *(已完成：3→5 用例全过；用 getList4MockHandler 的 overrideResponse 函数形式按 curPage 返回不同数据)*
  - 用例④ **点击下一页 SHALL 重新拉取并替换表格** —— `server.use(getList4MockHandler(fixed))` 需能按 `curPage` 返回不同数据（用 `overrideResponse` 函数形式或两次 `server.use` + 两次点击）；断言：`framework-list-prev` 禁用态在第 2 页解除、表格行内容随第 2 页数据变化、`framework-list-paging` 含「第 2/2 页」
  - 用例⑤ **末页「下一页」禁用** —— 构造 `totalPage = 2, curPage = 2`（通过先点一次下一页到末页）；断言 `framework-list-next` 为 `disabled`，再点击不发出新请求
  - 验证：`npm test -- FrameworkListPage` 5/5 通过
- [x] 2.2 边界用例覆盖首页「上一页」禁用（可在用例④ 中一并断言） *(已完成：首页 prev 禁用在用例④ 一并断言)*

## 3. 验收 + 文档 + 登记

- [x] 3.1 跑 `npm run typecheck`：exit=0 *(已完成：typecheck exit=0)*
- [x] 3.2 跑 `npm test`：预期 18 文件 / **85 用例**全过 *(实测：18 文件 / 85 用例全过（83 基线 + 2 翻页）)*
- [x] 3.3 跑 `npm run build`：产物变化 < 2 KB *(实测：339.56 KB / gzip 107.86 KB（+0.6 KB）)*
- [x] 3.4 修改 `AGENTS.md §11.3`：前端页面层清单中 `FrameworkListPage` 用例数 3 → **5**，并补一句"含翻页（下一页重新拉取 / 末页禁用）" *(已完成：AGENTS.md §11.3 FrameworkListPage 3→5 用例并补翻页说明；顶部统计 83→85 用例)*
- [x] 3.5 执行 `openspec validate --strict 2026-10-18-framework-list-pagination`：校验通过 *(已完成：Change is valid，0 错)*
- [x] 3.6 同步 delta spec 到 `openspec/specs/aicode-web-page` 并 `openspec archive`；验证主能力新增 1 Requirement「列表页翻页 SHALL 重新拉取并更新表格，且边界 SHALL 禁用按钮」含 3 Scenarios；requirements 现状 6 → **7** *(已完成：主 spec 物化 1 Requirement 3 Scenarios；aicode-web-page requirements 6 → 7；13/13 通过；变更 mv 至 archive；No active changes)*
