# Proposal

## Why

`2026-10-18-project-list-real-pagination`（commit `0960cc0`）已把 `ProjectController.list` 修成真分页 —— 响应现在真实回填 `curPage` / `pageSize` / `totalPage`，SQL 只取当前页。

但前端 `ProjectListPage` 仍把页码**硬编码**为 1：

```tsx
// aicode-web/src/pages/ProjectListPage/index.tsx:69
.list2({ token, curPage: 1, pageSize: PAGE_SIZE })
```

且无任何翻页控件 —— 用户创建超过 `PAGE_SIZE = 10` 个项目后，**剩余项目永久不可见**（当前 `db/data.sql` 只有 1 条种子，缺陷尚未暴露）。

后端前置已就绪，本变更补上前端这一半，与已落地的框架页（`2026-10-18-framework-list-pagination`）形态完全一致。

## What Changes

- **`ProjectListPage` 的 `curPage` 从常量提升为 `useState<number>`**（初值 1）；`useEffect` 依赖从 `[api]` 改为 `[api, curPage]`；请求参数 `list2({ token, curPage, pageSize: PAGE_SIZE })`。
- **新增翻页控件**（表格下方 `<nav data-testid="project-list-pager">`）：
  - 「上一页」`data-testid="project-list-prev"` —— `disabled={curPage <= 1}`；
  - 「下一页」`data-testid="project-list-next"` —— `disabled={curPage >= totalPage}`；
  - 页码文本 `data-testid="project-list-paging"`（「第 X/Y 页」）。
- **分页信息拆位**：`<h1>` 内保留「共 N 条」（`data-testid="project-list-total"`），页码移到翻页控件 —— 与框架页一致，避免 testid 复用歧义。
- **切页期间回 loading**：effect 起始处显式 `setState({ kind: 'loading' })`，避免"旧页行 + 新页码"瞬时错配。
- **`buildingCodes` 状态跨页保留**：构建中的项目若翻到别页，`buildingCodes` 仍持有其 code —— 翻回该页时按钮仍显示"构建中…"，符合预期。
- **单测 5 → 7 用例**：新增 2 条 —— ① 点击「下一页」重新拉取并替换表格；② 末页「下一页」禁用且点击不发请求。

**BREAKING**：无。

**契约 / 后端 / 生成物**：零改动。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

（无 —— 框架页变更 `2026-10-18-framework-list-pagination` 已把 `aicode-web-page` 的「列表页翻页 SHALL 重新拉取并更新表格，且边界 SHALL 禁用按钮」Requirement 物化到主能力（6 → 7 Requirements）。本变更的 `ProjectListPage` 是**该 Requirement 的第二个落地实现**，无需新增或修改 spec。）

## Impact

- **代码**：
  - 修改 `aicode-web/src/pages/ProjectListPage/index.tsx`（`curPage` state + 翻页控件 + effect 依赖 + 标题分页信息拆位）；
  - 修改 `aicode-web/src/pages/ProjectListPage/index.test.tsx`（5 → **7** 用例）。
- **测试**：前端 85 → **87** 用例。
- **不影响**：后端零改动；契约零改动；生成物零改动；路由表零改动；`buildingCodes` 构建日志逻辑零改动。
- **风险**：低。纯前端状态管理，形态与框架页一致（已验证）。
