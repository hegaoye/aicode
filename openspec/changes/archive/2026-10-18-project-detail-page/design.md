# Design

## Context

M2-2 之后路由表 `/project/:code` 被 `<RequireAuth>` 包裹 + 接 `<ProjectPlaceholderPage>` 占位；M2-2 的 ProjectListPage 行点击通过 `<Link to={`/project/${encodeURIComponent(row.code ?? '')}`}>` 已连到这个路由。M3-A-1 把 `loadByCode` 改返 `R<ProjectVO>` 并修了 2 处既有 NPE，让前端能干净地 `unwrapSuccess(loadByCode(code))`。

本变更只需要把占位换成真接入的详情页。范围小，沿用 M1 / M2-2 已落地的所有样板：
- `unwrapSuccess` from `src/lib/contract-response`
- `useAuth` / `tokenStore.get()` from `src/lib/auth/`（拦截器已注入 Authorization 头，本页无需显式穿 token）
- MSW `getLoadByCodeMockHandler` from `src/generated/api.msw`（已在 `test-setup.ts` 全局注册，M0 阶段）

## Goals / Non-Goals

**Goals:**

- 替换 `aicode-web/src/pages/ProjectDetailPage/index.tsx`（覆盖现有占位），让 `/project/:code` 路由第一次拉真实数据；
- 引入 3 状态视觉模型（`loading` / `ready` / `error`），与 `ProjectListPage` 的渲染保持一致；
- 用 `getLoadByCodeMockHandler`（已存在）的 override 路径覆盖 3 种测试场景：成功 / 找不到 / R 失败；
- 保留占位文件结构（同名 index.tsx，单一文件），不引入新目录。

**Non-Goals:**

- 不接"编辑 / 删除 / 触发构建" 等其他 action（M3-B/C 议程）；
- 不做字段裁剪 —— `ProjectVO` 22 字段全部展示一次过（与 `api-data-object` Rule "VO SHALL NOT 等于实体的全部持久化字段" 略冲突，但裁剪是 M3-C 范围）；
- 不接操作相关的"返回列表"按钮 + 面包屑 —— 导航 back 由 react-router `useNavigate` 与浏览器返回按钮天然支持；
- 不引入 react-router `useNavigate` 显式跳列表 —— 浏览器 `←` 已够；
- 不为本页面单独 mock 设计（沿用 `getLoadByCodeMockHandler` 默认 faker mock 数据）。
- 不重写 `ProjectPlaceholderPage` —— 该页面被本变更整体替换；其测试职责纳入 `ProjectDetailPage.test.tsx`。

## Decisions

### D1：直接重写占位文件，不保留目录别名

**决定**：将 `aicode-web/src/pages/ProjectDetailPage/index.tsx` **整文件替换**为 `ProjectDetailPage` 真实实现；不保留 `ProjectPlaceholderPage` 目录。

**理由**：

- 占位代码本身仅 24 行且只服务占位意图；保留两份等价组件只会让 reviewer 困惑"为什么有两份"；
- `App.tsx` import 路径 `from './pages/ProjectDetailPage'` 不变 —— 占位是文件内容，文件名同名替换不影响路由；
- 与 M2-2 在 `ProjectPlaceholderPage` 内留下"占位文字以明示该路由待 M3 接入"的设计 D7 一致 —— M3-A 第 2 阶段正是消化这条"显式待接入"约定。

**备选方案**：(a) 新建 `pages/ProjectDetailPage/` 替代 `pages/ProjectPlaceholderPage/`，用 `mv` + import 路径全部跟改 —— 多此一举；(b) 同名替换但分两步——本变更采用。

### D2：22 个字段全部展示，按持久化形态直出

**决定**：detail 表格展示 `ProjectVO` 全部 22 个持久化字段；不裁剪（裁剪属于 M3-C）。

**理由**：

- 详情页语义是"完整视图"——前端消费者（M2-2 列表页跳详情）期望看到全部业务字段；裁剪到"只显示前端消费的字段"会让详情页变成"半视图"，违反用户对 `/project/:code` 的语义期望；
- `api-data-object` Rule 要求 VO 字段不多于前端实际消费集 —— 详情页的"前端实际消费集 = 全部 22 个"是合理的局部例外；
- 列表页 M2-1 已经裁剪过（仅 5 列），详情页不该再裁；
- 反例：让 `englishName` 在 ListPage 出现但在 DetailPage 不出现会让两层语义不一致。

**备选方案**：(a) 仅展示 ListPage 同 5 列 —— 与"详情"语义背离；(b) M3-C 同步裁剪 —— 范围扩张。

### D3：`useState + useEffect`，不上 SWR / React Query

**决定**：延续 M1 / M2-2 的 `useState` + `useEffect` 单包拉取范式。

**理由**：

- 与 `DisplayAttributesPage`、`ProjectListPage` 完全一致；
- 本仓库当前 devDependencies **没有** SWR / React Query（M2-2 README 章节记）；引入会让 M3 也加入新依赖；
- 一次拉取只需展示 + 一份单测的三种状态，新引入数据请求库的价值低。

**备选方案**：(a) SWR —— 增加 `swr` 依赖 + 范式打破一致；(b) React Query —— 同上；(c) 沿用 useState —— 本变更采用。

### D4：错误状态分两类：`data: null` 显示友好"项目不存在"，非空错误抛 ContractError 显示气泡

**决定**：

- `data === null && code === "0000"` → 渲染 `<p>项目不存在</p>`（业务空态）；
- `code !== "0000"` 或 HTTP 异常 → 渲染 `<p role="alert" data-testid="project-detail-error">`（含 `ContractError.info`）。

**理由**：

- M3-A-1 把"找不到 project"从 NPE 改返 `R.success(null)`，这条契约层信号前端需要感知 —— 否则用户看到空白表格就以为"还在加载"；
- `api-contract` Spec "统一响应结构" 已规定 `success/code/info/data` 四键；前端用 `data === null` 区分"业务空"与 `code !== "0000"` 区分"协议失败"是与契约层约定一致的最小代价表达。

**备选方案**：(a) 两者都用 `data-testid="project-detail-error"` —— 失去区分失去语义；(b) 加一个 `data-testid="project-detail-empty"` —— 增加测试 fixture 复杂；(c) 双状态 —— 本变更采用。

### D5：测试使用 MSW 覆盖的固定数据而非默认 faker mock

**决定**：`ProjectDetailPage.test.tsx` 三用例都用 `server.use(getLoadByCodeMockHandler({...}))` 覆盖固定数据；不依赖默认 faker 随机数据。

**理由**：

- 与 M2-2 `ProjectListPage.test.tsx` 模式一致 —— 已经被 M2-2 反向验证有效（"（暂无数据）"等断言用确定性数据才稳定）；
- 默认 faker mock 会因字段值随机使断言脆弱（如 `name` 可能是 `undefined`，断言 `expect(getByText(name))` 失败）；
- `getLoadByCodeMockHandler` 的 `overrideResponse` 参数本身就是为确定性测试设计。

**备选方案**：(a) 默认 faker + 多次运行统计 —— 测试运行时间 + 调试难；(b) 固定 override —— 本变更采用。

## Risks / Trade-offs

**[MSW default mock 形态变化]** —— `getLoadByCodeMockHandler` 在 M3-A-1 重生成后默认 mock 体形态变为 `RProjectVO`（含 `info/code/success/data`）；如果直接调用不 override，**默认 faker mock 仍会返 `RProjectVO` 形态**。这是 M3-A-1 重生成带来的事实变化，但前端页面仍能 work。

**[`null data` 在前端 TypeScript 推断]** —— `unwrapSuccess(resp)` 返 `NonNullable<E['data']>`，对 `RProjectVO.data` 而言是 `ProjectVO`（不可为 null）。但实际上后端可能在 data 为 null 时也返 `R.success()` —— 前端拿到 `null` 时 TS 上是 `ProjectVO`（CompileTime），runtime 是 `null`。我必须 `data && <DetailView data={data}/>` 防御。

**[`ProjectPlaceholderPage` 整文件替换影响 git blame 历史]** —— 占位代码本就是 M2-2 才新加的（M2-2 commit `9b3e2b3`），blame 历史没损失；本变更一次性消化占位 vs 真实实现的语义收益超过 blame 历史保留。

## Migration Plan

单批交付（一个 feature commit）：

1. **替换 `aicode-web/src/pages/ProjectDetailPage/index.tsx`**：用 ProjectDetailPage 真实实现覆盖占位；
2. **新增 `aicode-web/src/pages/ProjectDetailPage/index.test.tsx`**：3 个用例（成功 / 找不到 / 服务器错误）；
3. **执行 `npm run typecheck` 与 `npm test`**：验证 46 用例通过（M2-2 基线 43 + 本变更 3）；
4. **执行 `npm run build`**：验证产物大小变化 < 1 KB；
5. **同步 delta spec 到 `openspec/specs/aicode-web-page` 并 archive**。

**回滚**：单 commit revert 即恢复占位；路由表不变（旧 import 重新可用）。

## Open Questions

- M3-C（字段裁剪）将在本页与 ListPage 同时重新设计 `ProjectVO`，届时本页会再裁一次。本变更暂不动此议题
