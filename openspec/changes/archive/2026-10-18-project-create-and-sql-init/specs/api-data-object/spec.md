# Spec Delta

## MODIFIED Requirements

### Requirement: Controller SHALL 返回 VO 而非持久化实体

Controller 层 SHALL 返回 VO 而非持久化实体。实体中的派生字段、嵌套关联对象、以及与该端点消费场景无关的内部字段 SHALL NOT 出现在响应中。

**写侧端点（创建 / 修改 / 触发动作）同样适用**：写侧返回体用于「让前端确认这次写入了什么」，其字段集合 SHALL 是**该写操作实际写入的字段**，而非实体的全集。SHALL NOT 因为"实体只有 N 个字段、返回整个实体更省事"而让写侧端点直出实体。

具体清单（与本变更实施对齐）：

- **`ProjectCreateVO`**（`POST /project/build` 返回）：`code` / `name` / `englishName` / `author` / `basePackage` / `databaseType` / `state` / `createTime`，共 8 字段
- **`ProjectSqlVO`**（`POST /project/sql/build` 与 `POST /project/sql/modify` 返回）：`code` / `projectCode` / `tsql` / `state`，共 4 字段

> **为何不返回实体全集**：`Project` 实体有 29 个字段（含 7 个关联 list 与 9 个纯内部字段如 `sqlFile` / `downloadUrl` / `isParseTable` / `isParseClass` / `isIncrement` / `relationshipAndDisplay`）。写侧返回它们会让前端被迫理解一堆用不到的字段，且把内部实现细节固化成跨语言契约 —— 一旦后端想给这些内部字段改名，契约就漂移。

> **与既有 Requirement 的关系**：本 Req 把「写侧」这一未覆盖的场景补齐。列表侧与详情侧的字段裁切另有 Requirement（「Project 详情页与列表页 SHALL 用不同 VO 投影」）。

#### Scenario: 列表接口出参为 VO

- **WHEN** 某列表接口需要返回数据库中的多行记录
- **THEN** 其出参类型 SHALL 为视图对象列表，SHALL NOT 为实体列表

#### Scenario: VO 字段是前端实际消费集的子集

- **WHEN** 某视图对象完成定义
- **THEN** 其字段集合 SHALL 不多于前端渲染所需的字段，SHALL NOT 等于实体的全部持久化字段

#### Scenario: 写侧端点 SHALL NOT 直出实体全集

- **WHEN** 写侧端点被导出到契约，生成的 schema 被检查
- **THEN** `POST /project/build` 的 `data` schema SHALL 是 `ProjectCreateVO`（8 字段），SHALL NOT 是 `Project` 实体（29 字段）
- **AND** `POST /project/sql/build` 与 `POST /project/sql/modify` 的 `data` schema SHALL 是 `ProjectSqlVO`（4 字段），SHALL NOT 是 `ProjectSql` 实体（5 字段）

#### Scenario: 写侧 VO SHALL NOT 包含实体内部的实现细节字段

- **WHEN** 写侧 VO 的字段集合与实体字段集合做集合差
- **THEN** `ProjectCreateVO` SHALL NOT 包含 `sqlFile` `downloadUrl` `buildNumber` `isRepository` `isParseTable` `isParseClass` `isIncrement` `relationshipAndDisplay`，SHALL NOT 包含任何关联 list
- **AND** `ProjectSqlVO` SHALL NOT 包含实体的 `id`（数据库自增主键 SHALL NOT 出现在任何接口响应中）