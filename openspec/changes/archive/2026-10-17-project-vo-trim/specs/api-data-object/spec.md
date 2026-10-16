# Spec Delta

## ADDED Requirements

### Requirement: Project 详情页与列表页 SHALL 用不同 VO 投影

`Project` 实体的 22 个持久化字段对应到前端两类页面（列表 + 详情）时 SHALL 拆为两种 VO，其字段集合 SHALL 分别反映两类页面的实际消费场景，不应是实体的全集拷贝。同一份 VO 不应同时给两种页面消费。

具体清单（与 M3-C 实施对齐）：

- **`ProjectSummaryVO`**（列表页消费集合；详见 list 页 `ProjectListPage`）：`code` / `name` / `englishName` / `state` / `createTime` / `updateTime` / `accountCode`，共 7 字段
- **`ProjectDetailVO`**（详情页消费集合；详见 detail 页 `ProjectDetailPage`）：`code` / `name` / `englishName` / `description` / `copyright` / `author` / `phone` / `basePackage` / `buildNumber` / `createTime` / `updateTime` / `accountCode`，共 12 字段

列表 VO 字段数量 SHALL NOT 大于 7（详情 VO 不大 12）；两类 VO 字段集合 SHALL NOT 完全相等（语义不同）。

> **后续变更原则**：每加入一个对应列表 / 详情消费场景的新端点，须重新审视该 VO 是否仍满足本 Rule。新增字段须先有前端消费需求作为依据。

#### Scenario: Project 列表接口的 records 元素 SHALL 是 ProjectSummaryVO 而非 Project 实体全字段

- **WHEN** `/project/list` 端点的 `data.records[]` 元素被导出契约 schema 检查
- **THEN** 元素 schema SHALL 是 `ProjectSummaryVO`（7 字段），SHALL NOT 包含实体的全 22 字段
- **AND** 类型为 `ProjectSummaryVO` 的字段 SHALL NOT 等于项目详情页消费的 `ProjectDetailVO` 的字段集合

#### Scenario: Project 单条详情接口的元素 SHALL 是 ProjectDetailVO 而非 Project 实体全字段

- **WHEN** `/project/load/code/{code}` 端点的 `data` 元素被导出契约 schema 检查
- **THEN** 元素 schema SHALL 是 `ProjectDetailVO`（12 字段），SHALL NOT 包含实体的全 22 字段
- **AND** 该 schema SHALL NOT 与 `ProjectSummaryVO` schema 完全一致（语义不同：列表 vs 详情）

### Requirement: VO 字段裁切反向验证 —— 列表与详情 VO SHALL NOT 退化成全字段

当新加入端点或修改字段时 SHALL NOT 退化为"返 Project 实体全集"。反向验证 SHALL 钉死这一约束。

#### Scenario: 列表 VO 字段集合 SHALL NOT 等于 Project 实体 22 字段全集

- **WHEN** 评审或回归测试 `ProjectSummaryVO` 字段集合
- **THEN** 字段集合 SHALL 与 `Project` 实体的 22 字段全集合做集合差——`ProjectSummaryVO` SHALL 不包含 `description` `databaseType` `language` `copyright` `author` `phone` `basePackage` `sqlFile` `downloadUrl` `buildNumber` `isRepository` `isParseTable` `isParseClass` `isIncrement` `updateTime`（除已有的 `code` `name` `englishName` `state` `createTime` `accountCode` 之外）
- **AND** 该断言 SHALL 在前端生成物 `aicode-web/src/generated/model/projectSummaryVO.ts` 自动从 orval 取中验证

#### Scenario: 详情 VO 字段集合 SHALL NOT 等于 Project 实体 22 字段全集

- **WHEN** 评审或回归测试 `ProjectDetailVO` 字段集合
- **THEN** 字段集合 SHALL 与 `Project` 实体的 22 字段全集合做集合差——`ProjectDetailVO` SHALL 不包含 `databaseType` `language` `state` `sqlFile` `downloadUrl` `isRepository` `isParseTable` `isParseClass` `isIncrement`（除详情页消费的 12 字段外）
- **AND** 该断言 SHALL 在 orval 生成物中验证
