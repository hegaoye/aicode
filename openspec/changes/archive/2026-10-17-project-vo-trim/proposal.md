# Proposal

## Why

`ProjectVO`（22 字段全拷贝 Project 实体）违反 `api-data-object` Scenario "VO 字段是前端实际消费集的子集"。M3-A-2 详情页直接展示 22 字段是个"全貌"的偷懒——后端"返了实体全集"前端就拿全集，违背 VO 投影的设计意图。

实际前端消费：

- **列表页**（M2-2）：5 列展示，`code / name / englishName / createTime`，外加详情链接 + 构建按钮（state 仅用于判断按钮 disable）。**7 字段**：`code` `name` `englishName` `createTime` `state` `updateTime` `accountCode`
- **详情页**（M3-A-2）：按 5 组共 22 字段全展示——这是详细页"全貌"。但**仍有裁剪空间**：`databaseType` / `language` / `state` / `copyright` / `author` / `phone` / `basePackage` / `isRepository` / `isParseTable` / `isParseClass` 这些"配置类"字段详情页**不展示**——详情页用户关心"项目名/描述/作者/路径/构建次数"，DB 类型在配置类字段在 metadata 视图里。

本变更落地裁切：列表与详情页**用不同 VO**——让前端消费集合与 VO 字段集合一一对应。

## What Changes

- **新增 `ProjectSummaryVO`**（`facade/src/main/java/com/aicode/project/vo/`）：列表页消费集合。7 字段：`code` `name` `englishName` `state` `createTime` `updateTime` `accountCode`
- **新增 `ProjectDetailVO`**：详情页消费集合。12 字段：`code` `name` `englishName` `description` `copyright` `author` `phone` `basePackage` `buildNumber` `createTime` `updateTime` `accountCode`
- **`ProjectVO` 删除**：22 字段全拷贝形态**仅由 detail/list 两个新 VO 取代**；前者 已无消费方，无需保留。
- **`ProjectController.list`**：`R<PageVO<ProjectSummaryVO>>`（替代 `R<PageVO<ProjectVO>>`，M2-1 已落）
- **`ProjectController.loadByCode`**：`R<ProjectDetailVO>`（替代 `R<ProjectVO>`，M3-A-1 已落）
- **`ProjectController.toProjectVO(...)`**：拆为两个新方法 `toSummary(Project)` / `toDetail(Project)`
- **`api-data-object`** 主 spec 新增 Requirement "Project 详情页与列表页 SHALL 用不同 VO 投影"含 2 个 Scenario：把"清单→VO 字段集合"这两层对应关系钉死
- **契约产物**：`OpenApiExportIT` 重导 `contracts/openapi/openapi.yaml`——`/project/list` schema 中 `data.records.items` 改 ref `ProjectSummaryVO`（7 字段），`/project/load/code/{code}` 改 ref `ProjectDetailVO`（12 字段）。orval 重生 `aicode-web/src/generated/` 出两类新 VO + 旧 `projectVO.ts` 被删除。
- **前端 types 调用更新**：
  - `ProjectListPage`：`ProjectVO[]` → `ProjectSummaryVO[]`
  - `ProjectDetailPage`：`ProjectVO` → `ProjectDetailVO`
- **测试更新**：
  - `ProjectListPage.test.tsx`：用 `ProjectSummaryVO` 字段断言；
  - `ProjectDetailPage` test（如有）：用 `ProjectDetailVO` 字段断言；
  - `ApiContractIT` `LoadByCodeReturnsWrappedVO` Scenario⑧ 改用 `ProjectDetailVO` 字段断言（`description` 是关键区分字段——`ProjectDetailVO` 含，`ProjectSummaryVO` 不含）。

**BREAKING**：仓库业务层（Java）零破坏——`ProjectVO` 是 facade 内一类，全文 grep（待实施复核）估计只被 `ProjectController` 与 `ApiContractIT` 引用，前者是改造对象，后者同步改。

**前端代码 BREAKING**：
- M2-2 ProjectListPage 单元测试 + M3-A-2 ProjectDetailPage 测试需要调整断言字段
- 库生产代码 `M0 / M1 / M2-2 / M3-A / M3-B` 未在任何页面使用 `ProjectVO` 字段集（这些页面消费的都是 `ProjectSummaryVO`/`ProjectDetailVO` 字段子集），实际上 break surface 是测试，不是生产 UI

## Capabilities

### New Capabilities

（无。本变更走 MODIFIED `api-data-object`，加固其"VO 字段是前端实际消费集的子集"Rule。）

### Modified Capabilities

- `api-data-object`：在 Requirement 1 下新增 Scenario "前端消费场景决定 VO 字段集合：列表与详情用不同 VO"——钉死 "列表页（5 列 + 状态/构建次数等）= 7 字段" 与 "详情页（11 字段）" 两条对应。M3-A-2 已落地"详情页 22 字段全展示"违反本 Rule，本变更将其收紧。

## Impact

- **代码**：
  - 新增 `facade/src/main/java/com/aicode/project/vo/ProjectSummaryVO.java`（7 字段）
  - 新增 `facade/src/main/java/com/aicode/project/vo/ProjectDetailVO.java`（12 字段）
  - 修改 `aicode/src/main/java/com/aicode/project/ctrl/ProjectController.java`：`list` 装配改 `toSummary`，`loadByCode` 装配改 `toDetail`
  - 删除 `facade/src/main/java/com/aicode/project/vo/ProjectVO.java`（被两新 VO 取代）
  - 修改 `aicode/src/test/java/com/aicode/contract/ApiContractIT.java` 场景⑧ 中 `loadByCode_returns_R_wrapped_ProjectVO` 改用 `ProjectDetailVO` 字段断言；`ProjectListPageVoContract` 场景⑦ 改用 `ProjectSummaryVO` 字段断言
  - 修改 `aicode-web/src/pages/ProjectListPage/index.tsx`：`ProjectVO` → `ProjectSummaryVO`
  - 修改 `aicode-web/src/pages/ProjectDetailPage/index.tsx`：`ProjectVO` → `ProjectDetailVO`，按 12 字段分 5 组
  - 修改 `aicode-web/src/pages/ProjectListPage/index.test.tsx` + `aicode-web/src/pages/ProjectDetailPage/index.test.tsx`（如有）：用对应 VO 字段断言
- **契约**：`contracts/openapi/openapi.yaml` 由 OpenApiExportIT 自动刷新；`/project/list` 的 `data.records.items` ref `ProjectSummaryVO`，`/project/load/code/{code}` ref `ProjectDetailVO`
- **不影响**：M3-A-1（loadByCode R 包络）+ M3-A-2（详情页）+ M3-B（一键构建 + ws 日志）+ M2-1/M2-2 已落地内容
- **风险**：低-中。突破面是测试与 orval 生成物；业务 Java 零业务层影响（facade 仅 controller 引用）。如果 grep 后发现业务层某处用 `ProjectVO`，需开额外变更同步——实施 task 1.5 用 grep 钉死。

