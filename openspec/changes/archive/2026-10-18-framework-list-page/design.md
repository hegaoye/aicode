# Design

## Context

M5-A1（commit `e6184d3`）把 `/framework/list` 收敛为 `R<PageVO<FrameworkSummaryVO>>`：

| 层 | 产物 |
|---|---|
| 请求函数 | `api.list4({ curPage, pageSize })` → `Promise<AxiosResponse<RPageVOFrameworkSummaryVO>>` |
| 容器类型 | `PageVOFrameworkSummaryVO`（`records` / `totalRow` / `totalPage` / `curPage` / `pageSize` + 4 个 `BaseVO` 查询辅助字段） |
| 元素类型 | `FrameworkSummaryVO`（7 字段：`id` / `code` / `name` / `description` / `gitHome` / `account` / `isPublic`） |
| MSW handler | `getList4MockHandler` —— 已在 M2-2 于 `src/test-setup.ts` 全局注册 |

M1–M4 已沉淀全部所需抽象，本变更**纯消费，不引入新抽象**。参照实现是 M2-2 的 `ProjectListPage`（同样 mount 即拉取 + 3 状态机 + `<table>` 渲染）。

## Goals / Non-Goals

**Goals:**

- `/frameworks` 路由（受 `<RequireAuth>` + `<AppShell>`）渲染框架列表；
- 消费 `list4` + `unwrapSuccess`，渲染 `records` 与 3 个分页信息；
- 3 状态机（loading / ready / error）沿 `ProjectListPage` 形态；
- 3 用例 Vitest（成功 / 空态 / 错误）。

**Non-Goals:**

- 不加分页交互控件（上一页 / 下一页按钮）—— 本变更只**展示**分页信息；交互式分页属后续增强（M5-C）。
- 不接 `/framework/load` 详情页 —— 列表页直接展示 `description`，点行进详情属后续。
- 不接 `/framework/build` / `modify` / `delete` 管理面 —— 见 M5-A1 的 Open Questions（它们仍返实体直出，需先收敛）。
- 不改任何后端 / 契约 / 生成物 / `test-setup.ts`。

## Decisions

### D1：表格 4 列（`code` / `name` / `description` / `isPublic`），不展示 `gitHome` / `account`

**决定**：渲染 4 列；`gitHome` 与 `account` 不进表格。

**理由**：
- 列表页的核心决策信息是"哪个技术栈"（`code` / `name` / `description`）+ "是否公开"（`isPublic`，决定该账户能否用）；
- `gitHome`（仓库 URL）与 `account`（仓库账号）是**信任度判据**，属详情页语义。当前无详情页（Non-Goal），故先不进表格 —— 避免表格 6 列过宽（视觉规则 3「空白克制 / 靠信息密度传达工具属性」）。

**备选方案**：(a) 6 列全展示 —— 表格过宽，且 `account` 是邮箱形态占宽；(b) 4 列 —— 本变更采用。

### D2：`isPublic` 列渲染中文而非 `'Y'` / `'N'`

**决定**：把 `'Y'` / `'N'` 映射为「公开」/「非公开」渲染。

**理由**：`Frameworks.isPublic` 的数据库语义是 `'Y'`/`'N'`（`aicode/src/main/resources/db/data.sql` 种子数据为 `'Y'`）。前端直接展示 `'Y'` 是把存储细节泄漏给用户。M3-C 的 `ProjectSummaryVO.state` 已在列表页渲染为原始值（`Enable`/`Disenable`）—— 该处未做映射是既有不足，本变更在框架页做对。

**备选方案**：(a) 原样渲染 `'Y'`/`'N'` —— 存储细节泄漏；(b) 中文映射 —— 本变更采用。

### D3：`PAGE_SIZE = 20`（覆盖 db/data.sql 的 10 条框架种子）

**决定**：常量 `PAGE_SIZE = 20`。

**理由**：`db/data.sql` 有 10 条框架种子（`id` 1–10）。`pageSize = 20` 让首屏一次拉全（`totalPage = 1`），测试与人工验证都不需要翻页。

**备选方案**：(a) `pageSize = 10` —— 恰好 1 页但若种子增长到 11 条就需要翻页；(b) `20` —— 有余量，本变更采用。

### D4：不引入 `DisplayControl` 控件分发器

**决定**：表格列直接渲染文本，不用 `<DisplayControl>`。

**理由**：`DisplayControl` 的分发依据是 `DisplayAttributeVO.displayType`（元驱动动态表单场景，M1 的 `/displayAttributes` 页面）。框架列表是**固定列的静态表格**，无 `displayAttribute`，故不适用。沿用 `ProjectListPage` 的直接渲染风格。

**备选方案**：(a) 复用 `DisplayControl` —— 需要伪造 `DisplayAttributeVO`，反而增加耦合；(b) 直接渲染 —— 本变更采用。

### D5：`useEffect` 依赖数组只放 `api`

**决定**：`useEffect(() => {...}, [api])`，`api` 由 `useMemo(() => getAicodeRESTfulAPIs(), [])` 稳定引用。

**理由**：沿 `ProjectListPage` 与 `ProjectDetailPage` 的既有形态 —— `api` 引用稳定故 effect 只跑一次；`curPage` 固定为 1（本变更无分页交互），故不放进依赖。

## Risks / Trade-offs

**[`PageVOFrameworkSummaryVO` 含 4 个 `BaseVO` 查询辅助字段]** → 前端不读它们（spec Scenario 4 钉死"读 `records` 不读 `voList`"）。这些 `null` 字段增加响应体积（每条约 80 字节），属全仓共性问题（M5-A1 已记 Open Questions），本变更不处理。

**[`description` 字段可能很长]** → 表格内用 `max-width` + `text-overflow: ellipsis` 约束，避免单行撑破布局；完整内容由详情页承载（Non-Goal）。

**[`getList4MockHandler` 默认 mock 用 faker，`success` 随机]** → 与 M3-B 的 `use-build-log.test.tsx` 同样处理：测试内 `server.use(getList4MockHandler(fixed))` 覆盖为确定性数据，避免偶发 `success=false`。

## Migration Plan

单批交付（一个 feature commit）：

1. 新增 `aicode-web/src/pages/FrameworkListPage/index.tsx`；
2. 新增 `aicode-web/src/pages/FrameworkListPage/index.test.tsx`（3 用例）；
3. 修改 `aicode-web/src/App.tsx`（注册 `/frameworks` 路由 + 占位路由入口链接）；
4. `npm run typecheck` + `npm test` + `npm run build`；
5. 同步 delta spec 到 `aicode-web-page` 并 archive。

**回滚**：单 commit revert。纯前端新增，无 BREAKING。

## Open Questions

- **交互式分页（上一页 / 下一页）何时接？** 本变更只展示分页信息。若框架种子增长到 > 20 条或用户需要浏览历史技术栈，需接交互分页（届时 `curPage` 进 `useState` 与 `useEffect` 依赖）。建议 M5-C。
- **`/framework/modify` / `updateIsPublic` 仍返实体直出（泄漏 password）** —— 属 M5-A1 的 Open Questions。接管理面前须先收敛。
- **列表页的行点击行为？** 本变更不做（Non-Goal）。若后续接 `/framework/:code` 详情页，可加 `<Link>` 包裹 `name` 列。
