# Proposal

## Why

M2-2 阶段已经具备消费 `/project/load/code/{code}` 形态（`R<ProjectVO>`）的全部前置：

- **契约层**：M3-A-1 变更（`2026-10-18-project-load-by-code-vo-contract`）把 `loadByCode` 改造为 `R<ProjectVO>`，删 `@Deprecated` 与顺手修复两处既有 NPE。orval 重生成后 `LoadByCodeResult = AxiosResponse<RProjectVO>`。
- **样板层**：M1 沉淀了 `unwrapSuccess` + `ContractError`；M2-2 落地了 `useAuth` + `<RequireAuth>` + `tokenStore.get()`。
- **占位层**：`ProjectPlaceholderPage`（详见 `aicode-web/src/pages/ProjectPlaceholderPage/index.tsx`）当前只把 `:code` 渲染于提示文字中；路由 `/project/:code` 已包 `<RequireAuth>`；列表页 `ProjectListPage` 行点击 `<Link to={`/project/${encodeURIComponent(row.code ?? '')}`}>` 已通。

本变更**只缺最后一步**：把占位页换成真接入 `loadByCode` 的 `ProjectDetailPage`，让"列表 → 详情"路径首次具备可演示态，并验证 M3-A-1 整理后的契约形状在前端可用（即用 `unwrapSuccess` 直接解构无需任何额外适配）。

## What Changes

- **替换 `ProjectPlaceholderPage`** 为 `ProjectDetailPage`：
  - 同名文件 `aicode-web/src/pages/ProjectDetailPage/index.tsx`（覆盖现有目录内容，**文件路径不变**，路由 `/project/:code` 无须改）；
  - `ProjectPlaceholderPage` 角色在该变更完成后被 `ProjectDetailPage` 替换，**不**另起目录。
- **`ProjectDetailPage` 行为**：
  - `useParams<{ code: string }>()` 取 `:code`；
  - `useState` + `useEffect` 调 `api.loadByCode(code)` 经 `unwrapSuccess(resp)` 取 `ProjectVO`；
  - 渲染一个**信息表格**：`code` / `name` / `englishName` / `description` / `databaseType` / `language` / `state` / `copyright` / `author` / `phone` / `basePackage` / `buildNumber` / `isRepository` / `isParseTable` / `isParseClass` / `createTime` / `updateTime` / `accountCode` —— 按"标识 + 文本 + 时间"三组排列；
  - 不显示**派生字段**（`accountCode` 显示为常规字段，因它本身是持久化字段而非派生）；
  - 加载与错误状态分别渲染"加载中…" 与 `<p role="alert" data-testid="project-detail-error">`。
- **新增 Vitest 单测** `ProjectDetailPage/index.test.tsx`：
  - 用 MSW `getLoadByCodeMockHandler`（已在 `test-setup.ts` 全局注册）覆盖一组 `ProjectVO` → 期待表格含 `code` / `name` / `englishName` 等关键字段；
  - 覆盖 `R.code === "0000"` 但 `data` 为 `null`（找不到 code）路径 → 期待显示"项目不存在"友好错误；
  - 覆盖 `R.code === "9999"`（服务器错误） → 期待错误气泡。
- **测试基础设施**：`test-setup.ts` 已有 `getLoadByCodeMockHandler`（M0 阶段生成），无需变更。

**BREAKING**：无。

**前端生成物变化**：**无**（M3-A-1 已在该变更里把 `api.ts` 内 `loadByCode` 重生为 `AxiosResponse<RProjectVO>`，本变更直接消费该形态）。

## Capabilities

### New Capabilities

（无。详见下方 MODIFIED 部分。）

### Modified Capabilities

- `aicode-web-page`：在现有 Requirement "业务页面 SHALL 把统一响应解析与控件分发抽象为可复用单元" 下新增 1 个 Scenario，钉"详情类页面 SHALL 走 `useParams` 取 `:code` 入参并通过 `unwrapSuccess(loadByCode(code))` 消费 `R<ProjectVO>`"。此 Scenario 是 M1 / M2-2 样板层在"按 URL 参数驱动"的详情页场景的具体化，让样板覆盖到所有三类页面（列表 / 详情 / 占位升级为详情）。

## Impact

- **代码**：
  - `aicode-web/src/pages/ProjectDetailPage/index.tsx` 重写（替换原占位内容）；
  - `aicode-web/src/pages/ProjectDetailPage/index.test.tsx` 新增；
  - `aicode-web/src/App.tsx` 不变（路由 `/project/:code` 已在 M2-2 注册 + 加 `<RequireAuth>`）；
  - `aicode-web/src/test-setup.ts` 不变（handlers 已注册）。
- **契约产物**：`aicode-web/src/generated/**` 无需重新生成（M3-A-1 已刷）。
- **不影响**：后端零改动（已 M3-A-1 落地）；`api-contract` 主 spec 零改动（已在 M3-A-1 增补）；`aicode-web-auth` 零改动（详情页继承 `<RequireAuth>` 守卫）。
- **风险**：低。`loadByCode` 已 verified M3-A-1 的契约层断言；前端消费 `R<ProjectVO>` 经 M1 已有 `unwrapSuccess` 抽样。
