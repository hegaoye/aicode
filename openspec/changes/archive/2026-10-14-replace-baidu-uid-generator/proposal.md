# Proposal

## Why

平台当前依赖一个 vendored 的闭源 jar（`aicode/libs/uid-generator-2025.02.23-SNAPSHOT.jar`，`com.baidu.fsg.uid`，源码在外部仓库）来生成标识符。它带来三重成本：

1. **第二数据源**：`MybatisPlusConfigUid` + `DataSourceConfig` 的 uid 分支 + 一个独立 Druid 连接池 + `worker_node` 表，全部只为存一个 workerId 分配记录。AGENTS.md §4 曾警告「不要顺手去重，需先理解下游生成工程的依赖」——但那个约束的**原始动机**（百度 UID 需要独立数据源存 `worker_node`）会随 jar 一起消失，因此这是有依据的删除而非拍脑袋。
2. **黑盒风险**：该 jar 不在任何官方支持矩阵内，Boot 4 升级中一旦出问题极难定位（源码不在本仓库、无法打补丁）。
3. **单实例下的过度设计**：当前部署为单实例（`devops/k8s/deployment.yml` replicas=1），分布式 ID 的 workerId 分配能力完全用不上。

同时，平台对 MyBatis-Plus 主键策略存在一个**隐蔽陷阱**：全局默认 `IdType` 是 `ASSIGN_ID`（雪花），而全部实体都没有 `@TableId` 注解。这意味着**仅仅删除 `setId()` 调用并不会得到数据库自增 ID**——MyBatis-Plus 会在 insert 时自动补一个雪花值，`schema.sql` 中的 `AUTO_INCREMENT` 根本不会被触发，雪花逻辑只是从百度 UID 搬进了 MyBatis-Plus 内部，且不会报任何错。

本变更把标识符生成策略显式化：**主键走数据库自增，业务编码走内存雪花**，并删除全部外部 UID 组件。

## What Changes

- **BREAKING**（依赖结构）：删除 `aicode/libs/uid-generator-*.jar` 与 `aicode/libs/worker_node.sql`
- **BREAKING**（数据源）：删除 `uidDataSource` 第二数据源、`MybatisPlusConfigUid`、`DataSourceConfig` 的 uid 分支、`BaiduUidConfig`，以及 `worker_node` 表
- **BREAKING**（主键生成语义）：主键 `id` 改为数据库自增。为此**必须**显式设置 MyBatis-Plus 全局 `DbConfig.idType = IdType.AUTO`，否则默认的 `ASSIGN_ID` 会继续在应用层生成雪花值
- `code` 业务编码改为由 Hutool `IdUtil.getSnowflakeNextIdStr()` 生成，产出仍是纯数字、与历史数据同构
- 解开 `Project.code = String.valueOf(Project.id)` 的耦合，二者独立生成
- `spring.datasource.uid.*` 配置从 `application.yml` 移除

## Capabilities

### New Capabilities

- `identifier-generation`: 定义平台标识符的生成策略——主键由数据库自增赋值、业务编码由内存雪花算法生成、二者相互独立，并规定生成值的可观测形态约束。

### Modified Capabilities

- `api-contract`: 标识符的**形态**不变（仍为字符串、仍为纯数字），但主键 `id` 的**取值范围**从 19 位雪花变为小整数自增序列。

> **归档顺序前置条件**：本变更对 `api-contract` 使用 `MODIFIED` 语义，而该能力由 `2026-10-14-add-api-contract-tests` 首次创建。SHALL 先归档后者，本变更才能归档（`openspec validate` 会提示 `target spec does not exist`）。本变更 SHALL NOT 在 `add-api-contract-tests` 归档前执行。

## Impact

- **删除文件**：`aicode/libs/uid-generator-2025.02.23-SNAPSHOT.jar`、`aicode/libs/worker_node.sql`
- **删除类**：`com.aicode.config.BaiduUidConfig`、`com.aicode.config.MybatisPlusConfigUid`
- **修改类**：`com.aicode.config.DataSourceConfig`（去 uid 分支）、`MybatisPlusConfigTidb`（加 `IdType.AUTO`）、`ModuleServiceImpl`、`ProjectServiceImpl`、`ProjectFramworkServiceImpl`、`ProjectRepositoryAccountServiceImpl`、`GeneratorSVImpl`
- **修改配置**：`aicode/src/main/resources/application.yml`（移除 `spring.datasource.uid`）
- **修改 schema**：`aicode/src/main/resources/db/schema.sql`（若含 `worker_node` 建表语句则移除）
- **兼容保证**：存量数据的 `id` / `code` 值**不做任何迁移**；`code` 仍是纯数字字符串，前端与模板无感知
- **已实证的前置条件**：H2 2.4.240 在 `MODE=MYSQL` 下支持「`bigint AUTO_INCREMENT` + `PRIMARY KEY(id, code)` 复合主键」，自增序列正常工作
- **已实证的安全前提**：`code` 不流入生成的 Java 标识符（`MapClassTable.toJava()` 中 `className` 由 `tableName` 推导），因此编码格式变化不影响生成产物
