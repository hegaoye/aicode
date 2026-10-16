# Proposal

## Why

M5-A1（`2026-10-18-framework-list-vo-contract`）已把 `/framework/list` 收敛为 `R<PageVO<FrameworkSummaryVO>>`（7 字段、无 Git 口令、容器为 `records`），但**前端仍无消费者** —— 这条端点是"建项目"流程的第一步（选技术栈），业务价值最高却仍未接入。

M1–M4 已沉淀完整样板（`unwrapSuccess` 响应契约层 / `DisplayControl` 控件分发 / MSW 全局 lifecycle / `RequireAuth` 路由守卫 / `useAuth` 鉴权），本变更**纯消费**，不引入任何新抽象。

## What Changes

- **新增 `aicode-web/src/pages/FrameworkListPage/`**：
  - `index.tsx` —— 消费 `api.list4({ curPage, pageSize })` → `unwrapSuccess(resp)` → `PageVOFrameworkSummaryVO`；渲染表格 4 列（`code` / `name` / `description` / `isPublic`）+ 分页信息（`totalRow` / `curPage` / `totalPage`）；3 状态机（loading / ready / error）沿 M2-2 `ProjectListPage` 形态。
  - `index.test.tsx` —— 3 用例（成功渲染 / records 为空 / `R.code !== "0000"` 错误气泡），用 `getList4MockHandler` 覆盖（`test-setup.ts` 已全局注册）。
- **`App.tsx` 注册 `/frameworks` 路由**（受 `<RequireAuth>` 包裹 + `<AppShell>`）；占位路由 `/` 的「M2 已接入」段落新增「查看框架列表」链接。
- **MSW**：**无变更** —— `getList4MockHandler` 在 M2-2 已于 `test-setup.ts` 全局注册。

**契约产物**：无变更（M5-A1 已生成 `frameworkSummaryVO.ts` / `rFrameworkSummaryVO.ts` / `pageVOFrameworkSummaryVO.ts`）。

**BREAKING**：无。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

- `aicode-web-page`：在 Requirement「详情类业务页面 SHALL 通过 URL 参数驱动 unwrapSuccess 消费 R 包络」下新增 1 个 Scenario —— **列表类页面**（无 URL 参数、纯契约驱动）的对应形态：`useEffect` mount 即调契约请求函数、`unwrapSuccess` 拆出 `PageVO<T>`、`records` 逐项渲染。本变更把该 Requirement 的覆盖面从"详情类"扩到"列表类"，与 M2-2 `ProjectListPage` 的既有实践一致（该实践先前未被 spec 显式登记）。

## Impact

- **代码**：
  - 新增 `aicode-web/src/pages/FrameworkListPage/index.tsx`；
  - 新增 `aicode-web/src/pages/FrameworkListPage/index.test.tsx`（3 用例）；
  - 修改 `aicode-web/src/App.tsx`（注册 `/frameworks` 路由 + 占位路由入口链接）。
- **测试**：前端用例 80 → **83**。
- **不影响**：后端零改动；契约产物零改动；生成物零改动；`test-setup.ts` 零改动。
- **风险**：低。纯前端消费已就绪的契约产物，无 BREAKING 面。
