# Design

## Context

M5-A2（commit `7592ce9`）的 `FrameworkListPage` 用常量 `PAGE_SIZE = 20` 拉第一页。实测 `db/data.sql` 有 **34 条**框架种子 —— `34 / 20 = 2` 页。页面渲染了「共 34 条 · 第 1/2 页」但没有任何翻页控件，剩余 14 条不可达。

后端 `FrameworksController.list` 的 `totalPage` 计算已正确（`ceil(total / pageSize)`，见 M5-A1 的实现），所以**缺的只是前端状态与控件**。

## Goals / Non-Goals

**Goals:**

- `curPage` 从常量提升为 `useState<number>`，列入 `useEffect` 依赖；
- 加「上一页 / 下一页」按钮 + 边界禁用；
- 翻页期间进入 loading，不呈现旧页数据；
- 单测 3 → 5 用例。

**Non-Goals:**

- 不做「每页 N 条」下拉（`pageSize` 保持常量 20）。
- 不改 `ProjectListPage`（M2-2 落地，`PAGE_SIZE = 10`，同样有隐藏数据问题 —— 但那是另一页，且 10 条种子当前够用；见 Open Questions）。
- 不做 URL 同步（`?page=2` 可分享链接）—— 本变更只做内存态翻页。
- 不动后端 / 契约 / 生成物 / 路由表。

## Decisions

### D1：`curPage` 用 `useState`，不引入路由参数或 URL query

**决定**：`const [curPage, setCurPage] = useState(1)`，翻页只改内存态。

**理由**：
- 与 M5-A2 design D5 的 `useEffect([api])` 形态一致，最小改动；
- URL 同步需要 `useSearchParams` + 与后端契约的页码参数约定，且 M4 后是 hash 路由（`HashRouter`），query 参数会变成 `/#/frameworks?page=2` —— 语义更复杂。当前数据量（34 条 / 2 页）不值得引入。

**备选方案**：(a) URL query —— 可分享链接但复杂度上升；(b) `useState` —— 本变更采用。

### D2：`useEffect` 依赖 `[api, curPage]`，切页时重跑

**决定**：`useEffect(() => {...}, [api, curPage])`。

**理由**：
- `api` 由 `useMemo` 稳定，`curPage` 变化即触发重跑 —— 这是 React 数据拉取的常规形态；
- 已有 `cancelled` 标志防竞态（M5-A2 已实现），依赖扩充不需要额外处理。

**备选方案**：(a) 手动在按钮 onClick 里调 API（绕过 effect）—— 会让 loading 态与请求逻辑散落两处；(b) 依赖扩充 —— 本变更采用。

### D3：翻页控件放在表格下方，`data-testid="framework-list-paging"` 复用为整块

**决定**：表格下方渲染一个 `<nav>`，内含「上一页」按钮 + `data-testid="framework-list-paging"` 的页码文本 + 「下一页」按钮。

**理由**：
- 表格上方已有标题 + 分页信息（M5-A2 的 `<h1>` 内含 `<small data-testid="framework-list-paging">`）；
- 若把控件也放上方会让标题区过重。放在下方是列表页的常见布局（先看数据，再操作）。

**备选方案**：(a) 全部放标题区 —— 标题区过重；(b) 表格下方 —— 本变更采用。

### D4：单页时（`totalPage <= 1`）仍渲染翻页控件但两个按钮都禁用

**决定**：`totalPage === 1` 时「上一页」与「下一页」都禁用，控件仍可见。

**理由**：
- 隐藏会让布局在"翻到第 2 页时按钮突然出现"时跳动（视觉不稳定）；
- 禁用态明确告知用户"数据就这些"。

**备选方案**：(a) `totalPage <= 1` 时隐藏整个控件 —— 布局跳动；(b) 禁用 —— 本变更采用。

## Risks / Trade-offs

**[翻页请求竞态]** → 已有 `cancelled` 标志（effect cleanup 时置 `true`），快速连点不会出现旧响应覆盖新页。M5-A2 已实现，本变更只扩充依赖。

**[`useBuildLog` 的 `entries` 式累加在此不适用]** → 框架列表是"整页替换"语义，不是"追加"，所以不需要累加器；`setState({ kind: 'ready', data })` 直接替换。**loading 期间旧数据不保留** —— 符合 spec Scenario 3。

**[后端 `totalPage` 在 `totalRow === 0` 时算 0]** → `0 / 20 = 0`，`totalPage = 0`，此时 `curPage >= totalPage` 为 `1 >= 0` → 「下一页」禁用（正确）；「上一页」也因 `curPage <= 1` 禁用（正确）。但空态下 `curPage=1` 显示「第 1/0 页」语义略怪 —— 见 Open Questions。

**[M5-A2 已归档 spec 的 Scenario 1 写 `api.list4({ curPage: 1, pageSize: N })`]** → 本变更后 `curPage` 不再恒为 1；Scenario 1 描述的是"mount 时"的形态，仍准确（mount 时 `curPage` 初值 1）。无需修订。

## Migration Plan

单批交付（一个 feature commit）：

1. 修改 `aicode-web/src/pages/FrameworkListPage/index.tsx`：加 `useState<number>(1)`、`useEffect` 依赖改 `[api, curPage]`、表格下方加翻页控件；
2. 修改 `aicode-web/src/pages/FrameworkListPage/index.test.tsx`：3 → 5 用例；
3. `npm run typecheck` + `npm test` + `npm run build`；
4. 同步 delta spec 到 `aicode-web-page` 并 archive。

**回滚**：单 commit revert。

## Open Questions

- **URL 同步（`?page=2` 可分享）** —— 本变更不做。当前数据量不值得；若框架种子增长到 10+ 页或需要分享特定页，再评估（hash 路由下 query 会变成 `/#/frameworks?page=2`，语义需与后端约定）。
- **空态下「第 1/0 页」的显示** —— `totalRow === 0` 时 `totalPage = 0`。当前实现会让分页文本显示「第 1/0 页」。可考虑在空态时把分页文本改为「共 0 条」而不显示页码；属展示细节，未在本变更范围。
- **`ProjectListPage` 同样有隐藏数据问题** —— `PAGE_SIZE = 10`，若项目数 > 10 则同样不可翻页。本变更只改框架页；`ProjectListPage` 是否跟进见后续决策。
- **`pageSize` 下拉（10 / 20 / 50）** —— 本变更明确不做。
