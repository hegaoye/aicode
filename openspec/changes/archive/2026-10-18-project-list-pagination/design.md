# Design

## Context

`ProjectListPage`（M2-2 落地）的页码硬编码：`list2({ token, curPage: 1, pageSize: PAGE_SIZE })`，无翻页控件。

后端真分页已由 `2026-10-18-project-list-real-pagination`（commit `0960cc0`）就绪 —— `curPage` / `pageSize` / `totalPage` 现在真实回填。

**本页与 `FrameworkListPage` 的形态差异**：后者只有表格；本页每行有「详情」与「构建」两个操作，且 `buildingCodes`（构建中的 code 集合）是**跨行状态**。这是本页独有的语义 —— 翻页时它必须保留。

## Goals / Non-Goals

**Goals:**

- `curPage` 提升为 `useState<number>`，`useEffect` 依赖 `[api, curPage]`；
- 翻页控件 + 边界禁用；
- 切页回 loading；
- `buildingCodes` 跨翻页保留；
- 单测 5 → 7。

**Non-Goals:**

- 不改 URL 同步（`?page=2`）—— 与框架页一致，本变更不做。
- 不做 `pageSize` 下拉。
- 不改 `buildingCodes` 的语义与 `startBuildFor` 实现。
- 不动后端 / 契约 / 生成物。

## Decisions

### D1：完全复用 `FrameworkListPage` 的翻页形态（D1–D5 逐条沿用）

**决定**：`useState` 而非 URL query（D1）；`useEffect` 依赖扩充而非手调 API（D2）；控件放表格下方（D3）；单页时控件仍渲染但两按钮禁用（D4）；依赖只放 `[api, curPage]`（D5）。

**理由**：两个列表页的翻页语义完全一致（`PageVO<T>` + `curPage` / `pageSize`）。**任何形态分叉都会让后续第三个列表页不知道该抄哪个** —— 统一比"各自最优"更重要。

### D2：`buildingCodes` 不随翻页重置

**决定**：`buildingCodes` 是独立的 `useState`，不放在 effect 的 cleanup 里；翻页只改 `curPage`，不动 `buildingCodes`。

**理由**：
- 构建是**后台任务**（`ProjectJobExecutor.execute` 走 `@Async` VTH 池），用户在构建期间完全可能翻到别页看别的项目；
- 若翻页清空 `buildingCodes`，翻回来时"构建中…"标记消失 —— 用户会以为构建停了，从而重复点击触发并发构建（后端 `ProjectJobServiceImpl` 有 Executing 检查会拒绝，但用户体验是"点了没反应"）；
- 现状（本变更前）没有翻页，`buildingCodes` 天然跨整个页面生命周期 —— 翻页不应改变这个既有语义。

**备选方案**：(a) 翻页清空 —— 会造成上述误解；(b) 保留 —— 本变更采用。

### D3：构建日志面板不随翻页卸载

**决定**：`<BuildLogPanel>` 内联在每行的 `<tr>` 内（现状）。翻页会卸载当前页的所有 `<tr>`，因此**当前页之外的构建日志面板随翻页消失**；但 `buildingCodes` 保留，翻回该页时面板重新挂载并重新 `startStream`（M3-B `BuildLogPanel` 的 mount 语义）。

**理由**：这是现状的固有行为（翻页 = 换 DOM），不由本变更新增。`BuildLogPanel` mount 时重新 `startStream` 会重新调 `execute` —— 需确认这是否会触发并发构建。

**⚠️ 实施时须验证**：`BuildLogPanel` mount → `useBuildLog().startStream()` → `api.execute({ code })`。若翻回该页触发第二次 `execute`，后端 `ProjectJobServiceImpl` 的 Executing 检查会抛错（`Server_Error`）→ 用户看到构建失败。

**处置（实施期已确认，2026-10-16）**：

**实测确认缺陷真实存在** —— 用 MSW 数 `execute` 端点调用次数：首次构建 1 次，翻走再翻回后变 **2** 次。即翻回必然重复触发构建。

用户批准**加 `autoStart` prop** 方案（`autoStart?: boolean`，默认 `true` 向后兼容）：

- `BuildLogPanel` 新增 `autoStart?: boolean`（默认 `true`）与 `onMounted?: () => void` 两个**可选** prop；
- `ProjectListPage` 用 `mountedPanelsRef`（`useRef<Set<string>>`，跨翻页保留）记录"面板曾挂载过的 code"，传 `autoStart={!mountedPanelsRef.current.has(rowCode)}` 与 `onMounted={() => mountedPanelsRef.current.add(rowCode)}`。

**为何不能用 `startedBuilds` useState**：点击构建时 `startedBuilds` 立即含该 code，而 `BuildLogPanel` 首次挂载发生在同一次渲染批次（`isBuilding` 刚变 true），此时 `autoStart` 已算成 `false` —— **首次就不会启动构建**。ref 不触发重渲染，`autoStart` 的取值在 render 期已定型，故能区分"首次挂载"与"翻回重挂"。

**反向验证**：临时把 `if (projectCode && autoStart)` 短路为 `if (projectCode && true)` → 用例红（`expected 2 to be 1`），恢复后 9/9 绿。

**副作用（已知且接受）**：翻回时面板不再 `autoStart`，故 `useBuildLog` 内部 `startStream` 里的 `appendEntry('开始构建...')` 首条不会重现 —— 翻回后只拿得到 ws 推送的续流日志。后台构建与日志流均不受影响（构建早已启动）。若未来要求翻回也能看到首条，需把 entries 状态提升到页面层，属独立优化。

**原备选方案的否掉理由**："面板消失"的方案会让翻回后构建日志不可见，用户无法确认构建是否在进行 —— 反而更容易误判。

## Risks / Trade-offs

**[翻回构建中的页会重复 `execute`]** → D3 已识别。实施 task 2.1 时先验证；若确实触发，取"有 `buildingCodes` 时不再挂面板"的最小处置。这是**实施期可能需要的设计修订点**，已在 design 显式标注。

**[`totalPage = 0` 的空态]** → `curPage >= totalPage` 为 `1 >= 0` → 两按钮均禁用（正确）；但页码文本显示「第 1/0 页」语义略怪。与框架页同一问题，本变更不重复修（见框架页 design 的 Open Questions）。

**[测试需按 `curPage` 返回不同数据]** → 复用框架页用例④ 的 `getList2MockHandler` 的 `overrideResponse` **函数形式**，按 `info.request.url` 判别 `curPage`。`list2` 的 query 参数名是 `curPage`（见 `list2Params.ts`）。

## Migration Plan

单批交付（一个 feature commit）：

1. 修改 `ProjectListPage/index.tsx`：`curPage` state + effect 依赖 + 翻页控件 + 标题分页信息拆位；
2. 修改 `ProjectListPage/index.test.tsx`：5 → 7 用例；
3. `npm run typecheck` + `npm test` + `npm run build`；
4. 同步 delta spec（"列表页翻页 SHALL 在每个列表页面独立成立"，含新增的第 4 个 Scenario「跨翻页保留其它交互状态」）到 `aicode-web-page` 并 archive。

**回滚**：单 commit revert。

## Open Questions

- ~~**`BuildLogPanel` 翻回重复 `execute`**~~ —— **已解决**，见 D3 处置段（加 `autoStart` / `onMounted`）。
- **`/account/list` 前端无页面** —— 该端点虽有同类分页字段缺陷（见 `2026-10-18-project-list-real-pagination` 的 Open Questions），但前端从未消费它；接账户管理页时一并处理。
