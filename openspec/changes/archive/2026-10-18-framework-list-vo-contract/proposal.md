# Proposal

## Why

M5-A 计划接 React「框架选择页」（消费 `/framework/list`）时发现**两个安全问题**：

**问题 1 —— Git 仓库密码明文泄漏到前端（高危）**

- `GET /framework/load?code=` 返 `R<Frameworks>` —— **持久化实体直出**，含 `password` 字段（模板仓库的 Git 账号密码）。
- `GET /framework/list?curPage=&pageSize=` 返 `R<Page<FrameworksPageVO>>` —— `FrameworksPageVO` 含 `password` 字段。

两者都把 Git 凭证明文写进 HTTP 响应，落到浏览器开发者工具 / 前端 bundle / 任何代理日志里。这与 M3-D 刚堵掉的 `AccountVO.password` 是**同类漏洞**（M3-D 的 `ApiContractIT` 场景⑨ 已钉死 `/account/load` 的 `data` 不含 `password`）。

**问题 2 —— `BaseVO` 派生字段泄漏进响应（中危）**

`FrameworksPageVO extends BaseVO`，`BaseVO` 携带 4 个**查询辅助字段**（`createTimeBegin` / `createTimeEnd` / `updateTimeBegin` / `updateTimeEnd`）。这些字段的语义是"入参：按时间范围过滤"，却被 Jackson 序列化成响应的一部分——正是 `api-data-object` Requirement「VO SHALL NOT 序列化派生字段」禁止的形态。

**问题 3 —— 分页容器退化（中危）**

`/framework/list` 用 `com.aicode.core.Page<FrameworksPageVO>`（字段名 `voList`）而非 `PageVO<T>`（字段名 `records`）。这与 M2-1 已把 `/project/list` 收敛到 `R<PageVO<ProjectSummaryVO>>` 的方向相反 —— 契约层出现两种分页字段名并存，前端消费时需要按端点分别记忆。

本变更**先堵漏再接页面**（按 brainstorming 决策）：M5-A1 修后端契约，M5-A2 接 React 页面。

## What Changes

- **新增 `FrameworkSummaryVO`**（`facade/.../frameworks/vo/`）：7 字段 `id` / `code` / `name` / `description` / `gitHome` / `account` / `isPublic`，**不含** `password`、**不继承** `BaseVO`。
- **`FrameworksController.load`**：返 `R<FrameworkSummaryVO>`（替换 `R<Frameworks>` 实体直出）；装配手工逐字段（沿 M3-C `toSummary` 风格）；`code == null` 早返 `R.success()`（避开 `R.success(null)` 的 `BaseExceptionEnum` 重载 NPE，见 M3-A-1 记录）。
- **`FrameworksController.list`**：返 `R<PageVO<FrameworkSummaryVO>>`（替换 `R<Page<FrameworksPageVO>>`）；`records` 取代 `voList`；`totalRow` / `totalPage` 由 `PageVO` 承载。
- **删除 `FrameworksPageVO`**（`extends BaseVO`，含 password）—— 被 `FrameworkSummaryVO` 取代。
- **契约产物**：`OpenApiExportIT` 重导 `contracts/openapi/openapi.yaml`：
  - `/framework/list` schema 由 `RPageFrameworksPageVO` → `RPageVOFrameworkSummaryVO`；
  - `/framework/load` schema 由 `RFrameworks`（实体） → `RFrameworkSummaryVO`；
  - `components.schemas` 中 `FrameworksPageVO` 消失、`FrameworkSummaryVO` 出现。
- **orval 生成物**：`frameworkSummaryVO.ts` / `rFrameworkSummaryVO.ts` / `pageVOFrameworkSummaryVO.ts` 生成；`frameworksPageVO.ts` / `rFrameworks.ts`（若只被这两个端点用）消失。
- **契约守护测试**：新增 `ApiContractIT` 场景⑩ `FrameworkListNeverLeaksGitPassword`，断言 `/framework/list` 与 `/framework/load` 的响应中：
  - `data.records[*]` 与 `data` 的字段集合 ⊆ `FrameworkSummaryVO` 的 7 字段；
  - **显式断言不含 `password`**；
  - **显式断言不含 4 个 `BaseVO` 派生字段**；
  - **反向验证**：临时把 Controller 改回 `R<Frameworks>` / `R<Page<FrameworksPageVO>>` → 场景⑩ 立刻变红。

**BREAKING**：

- `/framework/load` 与 `/framework/list` 的响应结构变化（`data` 从实体 / `voList` 变成 VO / `records`）。仓库内 grep 确认无 Java 业务层调用（前端 React 尚未接，M5-A2 才接）。
- Angular 7 旧产物（已随 M4 删除）曾消费这两个端点；**运行时切换已完成**，故无实际破坏面。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

- `api-data-object`：在 Requirement「Controller SHALL 返回 VO 而非持久化实体」下新增 1 个 Scenario **"凭据字段 SHALL NOT 出现在任何接口响应中"** —— 把 M3-D 在 `AccountVO` 上的单点防护上升为**跨领域的通用约束**，覆盖 Git 密码这类"实体里的凭据列"。
- `api-data-object`：在 Requirement「VO SHALL NOT 序列化派生字段」下新增 1 个 Scenario **"继承 BaseVO 的分页 VO 不得把查询辅助字段带入响应"** —— 覆盖 `FrameworksPageVO extends BaseVO` 这类历史形态。
- `api-contract`：在 Requirement「统一响应包装的 data SHALL 携带真实结构」下新增 1 个 Scenario **"分页接口 SHALL 统一用 PageVO<T> 而非 Page<T>"** —— 消除 `voList` / `records` 两种字段名并存。

## Impact

- **代码**：
  - 新增 `facade/src/main/java/com/aicode/frameworks/vo/FrameworkSummaryVO.java`（7 字段，无 password，不继承 BaseVO）；
  - 修改 `aicode/src/main/java/com/aicode/frameworks/ctrl/FrameworksController.java`：新增 `toSummary(Frameworks)` 私有方法；`load` 与 `list` 两个方法改签名与装配；
  - 删除 `facade/src/main/java/com/aicode/frameworks/vo/FrameworksPageVO.java`；
  - 修改 `aicode/src/test/java/com/aicode/contract/ApiContractIT.java`：新增 `@Nested class FrameworkListNeverLeaksGitPassword`（3 用例含反向验证）。
- **契约产物**：`contracts/openapi/openapi.yaml` 由 `OpenApiExportIT` 自动刷新；`aicode-web/src/generated/` 由 `npm run generate` 重生。
- **不影响**：后端其他端点；前端 React 页面（M5-A2 才接）；M0–M4 已落地的一切。
- **风险**：
  - **低** —— 仓库内无 Java 业务层消费这两个端点；Angular 产物已随 M4 删除；前端 React 尚未接。BREAKING 面为空。
  - `FrameworksPageVO` 删除后若有其他引用会编译失败 —— task 1.5 用 grep 钉这一前提。
