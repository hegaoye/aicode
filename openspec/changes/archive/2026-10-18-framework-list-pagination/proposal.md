# Proposal

## Why

M5-A2（`2026-10-18-framework-list-page`）落地的 `FrameworkListPage` 用常量 `PAGE_SIZE = 20` 拉取**第一页**，渲染分页信息 `共 N 条 · 第 X/Y 页` —— 但**没有任何翻页控件**。

实测 `aicode/src/main/resources/db/data.sql` 有 **34 条**框架种子。`34 / 20 = 2` 页，即：

- 首屏渲染第 1 页的 20 条；
- 分页信息显示「共 34 条 · 第 1/2 页」；
- **剩余 14 条无法访问** —— 用户既看不到也拿不到。

这不是"锦上添花的交互增强"，而是**已落地的 UX 缺陷**：数据在库里、接口返回了、页面却把它藏起来了。

本变更把 `curPage` 从常量提升为 `useState`，并在 `useEffect` 依赖里引入它，使页面可翻页；加「上一页 / 下一页」按钮与边界禁用（首页禁用上一页、末页禁用下一页）。

## What Changes

- **`FrameworkListPage` 的 `curPage` 从常量提升为 `useState<number>`**（初值 1）；`useEffect` 依赖数组从 `[api]` 改为 `[api, curPage]`。
- **新增翻页控件**：
  - 「上一页」按钮 —— `disabled={curPage <= 1}`；
  - 「下一页」按钮 —— `disabled={curPage >= totalPage}`；
  - 「第 X/Y 页」文本沿用现有 `data-testid="framework-list-paging"`。
- **加载态不闪旧数据**：切页时进入 `loading` 状态，避免"第 1 页数据 + 第 2 页分页号"的错配瞬时呈现。
- **单测扩到 5 用例**：新增 2 条 —— ① 点击「下一页」后 `curPage` 变为 2 且请求参数随之改变、表格内容随之更新；② 末页时「下一页」禁用。

**契约产物**：无变更（`list4({ curPage, pageSize })` 两参数本就是契约的一部分）。
**后端**：无变更（`totalPage` 计算已正确：`ceil(total / pageSize)`）。

**BREAKING**：无。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

- `aicode-web-page`：在 Requirement「列表类业务页面 SHALL 通过 mount 即拉取并 unwrapSuccess 消费 PageVO<T> 包络」下新增 2 个 Scenario：
  1. **翻页 SHALL 重新拉取并更新表格** —— `curPage` 变化 SHALL 触发 effect 重跑、请求参数随之改变、表格内容随之替换；
  2. **翻页边界 SHALL 禁用按钮** —— 首页禁用「上一页」、末页禁用「下一页」，SHALL NOT 发出越界请求。

## Impact

- **代码**：
  - 修改 `aicode-web/src/pages/FrameworkListPage/index.tsx`（`curPage` state + 翻页控件 + `useEffect` 依赖）；
  - 修改 `aicode-web/src/pages/FrameworkListPage/index.test.tsx`（3 → **5** 用例）。
- **测试**：前端 83 → **85** 用例。
- **不影响**：后端零改动；契约零改动；生成物零改动；路由表零改动；其他页面零改动。
- **风险**：低。纯前端状态管理，`useEffect` 依赖扩充是 React 常规做法。
