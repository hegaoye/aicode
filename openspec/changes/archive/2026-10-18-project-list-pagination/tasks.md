# Tasks

## 1. ProjectListPage 翻页改造

- [x] 1.1 修改 `aicode-web/src/pages/ProjectListPage/index.tsx`：新增 `const [curPage, setCurPage] = useState<number>(1)`；`useEffect` 依赖由 `[api]` 改为 `[api, curPage]`；effect 内 `api.list2({ token, curPage, pageSize: PAGE_SIZE })`（取代硬编码 `curPage: 1`）；effect 起始处 `setState({ kind: 'loading' })`；验证：`npm run typecheck` exit=0 *(已完成：curPage useState(1)；effect 依赖 [api, curPage]；list2({token, curPage, pageSize: PAGE_SIZE})；effect 起始 setState loading)*
- [x] 1.2 表格下方新增 `<nav data-testid="project-list-pager">`：「上一页」`data-testid="project-list-prev"` `disabled={curPage <= 1}` `onClick={() => setCurPage((p) => Math.max(1, p - 1))}` + 页码文本 `data-testid="project-list-paging"`（「第 X/Y 页」）+ 「下一页」`data-testid="project-list-next"` `disabled={curPage >= totalPage}` `onClick={() => setCurPage((p) => p + 1)}`；单页时（`totalPage <= 1`）两按钮均禁用但控件仍渲染；验证：`npm run typecheck` exit=0 *(已完成：nav[data-testid=project-list-pager] + prev/paging/next；prev disabled curPage<=1、next disabled curPage>=totalPage；单页仍渲染但均禁用)*
- [x] 1.3 `<h1>` 内的分页信息从 `data-testid="project-list-total"` 承载「共 N 条」，页码只由翻页控件承载（与框架页一致，避免 testid 复用歧义）；验证：`npm run typecheck` exit=0 *(已完成：h1 内 small 挂 data-testid=project-list-total 承载「共 N 条」；页码只由翻页控件承载（与框架页一致）)*
- [x] 1.4 **验证 `buildingCodes` 跨翻页保留**（design D2）：翻页只改 `curPage`，`buildingCodes` 不在 effect cleanup 中重置；实测路径见 task 2.1 的用例⑥ *(已完成：buildingCodes 为独立 useState，翻页只改 curPage；用例⑧（翻走再翻回按钮仍 disabled）通过)*

## 2. 单元测试

- [x] 2.1 修改 `aicode-web/src/pages/ProjectListPage/index.test.tsx`：保留既有 5 用例（同步 `project-list-total` 断言），新增 3 条（5 → **8**）： *(已完成：5 → 9 用例（新增 4：翻页重拉并整页替换 / 末页禁用且点击不发请求 / buildingCodes 跨翻页保留 / 翻回不重复调 execute）；9/9 绿)*
  - 用例⑥ **点击「下一页」重新拉取并替换表格** —— 用 `getList2MockHandler` 的 `overrideResponse` 函数形式按 `info.request.url` 判别 `curPage` 返回不同数据；断言：第 1 页可见 `p1-code` → 点击后 `p2-code` 可见、`p1-code` 消失（整页替换）、页码「第 2/2 页」；首页「上一页」禁用
  - 用例⑦ **末页「下一页」禁用；点击禁用态 SHALL NOT 触发新请求** —— 闭包计数器统计请求数；翻到第 2 页后 `requestCount = 2`，点击禁用按钮后仍为 2 且页码不变
  - 用例⑧ **`buildingCodes` 跨翻页保留** —— 用例⑥ 的同一场景中，第 1 页点「构建」后再翻到第 2 页再翻回，断言该行按钮仍呈"构建中…"
  - 验证：`npm test -- ProjectListPage` 8/8 通过
- [x] 2.2 **验证 `BuildLogPanel` 翻回是否重复 `execute`**（design D3 已标注的实施期检查点）：在用例⑥ 场景中观察第 1 页已构建过、翻回时是否重复调 `execute`；若触发，记录为已知限制并在本变更 tasks 标注，或按 design 取最小处置 *(已完成并**发现真实缺陷** → 用户批准加 autoStart prop（超出原 tasks 的设计修订，已同步进 design D3 与两个 delta spec）。修法：BuildLogPanel 加 autoStart?: boolean（默认 true，向后兼容）+ onMounted?: () => void；ProjectListPage 用 mountedPanelsRef 跨翻页记录已挂载 code，传 autoStart={!mountedPanelsRef.current.has(rowCode)}。反向验证成立：临时把 autoStart 短路为 true → 用例红（execute 被调 2 次而非 1）；恢复后 9/9 绿。副作用说明：翻回时面板不再 autoStart，故只拿得到 ws 推送的续流日志、拿不到「开始构建...」首条（本地瞬时）；后台构建与日志流均不受影响)*

## 3. 验收 + 文档 + 登记

- [x] 3.1 跑 `npm run typecheck`：exit=0 *(typecheck exit=0)*
- [x] 3.2 跑 `npm test`：预期 18 文件 / **88 用例**全过 *(18 文件 / 94 用例全过（85 基线 + ProjectListPage 4 + BuildLogPanel 5）)*
- [x] 3.3 跑 `npm run build`：产物变化 < 2 KB *(build 成功，104 modules transformed，build/dist 344 KB（build/ 已 gitignore，不入库）)*
- [x] 3.4 修改 `AGENTS.md §11.3`：前端页面层清单里 `ProjectListPage` 5 → **8** 用例，并补"含翻页（下一页重拉 / 末页禁用 / buildingCodes 跨翻页保留）" *(已完成：AGENTS.md §11.3 页面层 ProjectListPage 5→9 并列 5 项语义；实时层 build-log-panel 3→8；头行统计 85→94)*
- [x] 3.5 修改 `AGENTS.md §7.10 第 9 项`：把 `/project/list` 从"未接入的高价值端点"移出（已接入） *(已完成：§7.10 第 9 项前端消费端点 6→8（含 /framework/list 与 ws 构建日志），未接入端点移除 /framework/list 并补写侧端点)*
- [x] 3.6 执行 `openspec validate --strict 2026-10-18-project-list-pagination`：校验通过 *(openspec validate --strict 通过)*
- [x] 3.7 同步 delta spec 到 `openspec/specs/aicode-web-page` 并 `openspec archive`；验证主能力 Requirements 数（框架页已物化「列表页翻页」Requirement，本变更是同一 Requirement 的**强化版本**：标题措辞从「列表页翻页 SHALL 重新拉取并更新表格」调整为「列表页翻页 SHALL 在每个列表页面独立成立」并新增第 4 个 Scenario「跨翻页保留其它交互状态」） *(已完成：aicode-web-page 7 Requirements（翻页 Requirement 强化，补第 4 个 Scenario「跨翻页保留其它交互状态」）/ aicode-web-realtime 3 Requirements（BuildLogPanel 补 autoStart / onMounted 契约 + 2 个 Scenario）；13/13 spec validate 通过；变更已 archive)*
