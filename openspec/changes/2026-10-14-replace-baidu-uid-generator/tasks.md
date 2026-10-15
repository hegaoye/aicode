# Tasks

## 1. 主键策略切换（关键步骤）

- [ ] 1.1 在 `MybatisPlusConfigTidb` 中显式设置全局主键策略：`globalConfig.setDbConfig(new GlobalConfig.DbConfig().setIdType(IdType.AUTO))`，并补 `com.baomidou.mybatisplus.annotation.IdType` import
- [ ] 1.2 全仓 grep 确认无任何 `@TableId` 注解（若存在则同步改为 `type = IdType.AUTO`）
- [ ] 1.3 删除 `ModuleServiceImpl.save()` 中的 `entity.setId(uidGenerator.getUID())` 及 `UidGenerator` 字段与 import
- [ ] 1.4 删除 `ProjectServiceImpl` 中的 `project.setId(uidGenerator.getUID())` 及 `UidGenerator` 字段与 import
- [ ] 1.5 删除 `ProjectFramworkServiceImpl.save()` 中的 `entity.setId(uidGenerator.getUID())` 及 `UidGenerator` 字段与 import

## 2. 业务编码改为内存雪花

- [ ] 2.1 `ProjectServiceImpl`：删除 `project.setCode(String.valueOf(project.getId()))`，改为 `project.setCode(IdUtil.getSnowflakeNextIdStr())`，并加注释说明「业务编码，非主键派生」
- [ ] 2.2 `ProjectServiceImpl:337`：`new MapClassTable(String.valueOf(uidGenerator.getUID()), ...)` 的首参改为 `IdUtil.getSnowflakeNextIdStr()`
- [ ] 2.3 `ProjectServiceImpl` 中 `ProjectMap.builder().id(uidGenerator.getUID())` 改为依赖主键自增（删除该 `.id(...)` 调用）
- [ ] 2.4 `ProjectRepositoryAccountServiceImpl`：`setCode(String.valueOf(uidGenerator.getUID()))` 改为 `IdUtil.getSnowflakeNextIdStr()`
- [ ] 2.5 `GeneratorSVImpl`：`frameworksTemplate.setCode(String.valueOf(uidGenerator.getUID()))` 改为 `IdUtil.getSnowflakeNextIdStr()`
- [ ] 2.6 确认 `cn.hutool.core.util.IdUtil` 在 `aicode` 模块可用（`hutool-all` 已是现有依赖，无需新增）
- [ ] 2.7 全仓 grep 确认 `getUID()` 零残留

## 3. 移除外部组件与第二数据源

- [ ] 3.1 删除 `aicode/src/main/java/com/aicode/config/BaiduUidConfig.java`
- [ ] 3.2 删除 `aicode/src/main/java/com/aicode/config/MybatisPlusConfigUid.java`
- [ ] 3.3 `DataSourceConfig`：删除 `uidDataSourceProperties` 与 `uidDataSource` 两个 Bean 及其 `@Primary` 标记，清理不再需要的 import
- [ ] 3.4 `application.yml`：删除整个 `spring.datasource.uid` 配置块
- [ ] 3.5 删除 `aicode/libs/uid-generator-2025.02.23-SNAPSHOT.jar` 与 `aicode/libs/worker_node.sql`
- [ ] 3.6 `aicode/build.gradle`：确认 `fileTree(dir: 'libs', include: ['*.jar'])` 删除后不产生空依赖问题；若无其他 jar，一并移除该 fileTree
- [ ] 3.7 `db/schema.sql`：若含 `worker_node` 建表语句则移除；同时确认 `data.sql` 无对该表的初始化数据
- [ ] 3.8 全仓 grep 确认 `com.baidu.fsg.uid` 零残留
- [ ] 3.9 全仓 grep 确认 `uidDataSource` 零残留

## 4. 验证

- [ ] 4.1 执行 `./aicode/gradlew :aicode:bootRun` 冷启动，确认仅初始化一个 Druid 连接池（日志中只有一个 `{dataSource-1}`，SHALL NOT 出现 `{dataSource-2}`）
- [ ] 4.2 通过 UI 新建一个项目，检查其 `id` 与 `code`：`id` SHALL 为小整数，`code` SHALL 为 19 位纯数字，且 `code != String.valueOf(id)`
- [ ] 4.3 连续新建两个项目，确认 `id` 严格递增、`code` 互不相同
- [ ] 4.4 执行 `./aicode/gradlew :aicode:test --tests "com.aicode.contract.*"`，契约测试 SHALL 全绿（确认 `id` 仍序列化为字符串）
- [ ] 4.5 执行生成主链路端到端（新建项目 → 导入 SQL → init → 构建），确认产物目录结构与文件内容与变更前一致

## 5. 文档

- [ ] 5.1 更新 `AGENTS.md` §4：删除「存在两个 DataSource Bean」及 `tidb` / `uid` 双数据源的描述，改为单数据源说明
- [ ] 5.2 更新 `AGENTS.md` §5.6 关键架构决策表：删除「双数据源」行
- [ ] 5.3 更新 `AGENTS.md` §10：删除与第二数据源 / `worker_node` 相关的坑位说明
- [ ] 5.4 在 `docs/standards/architecture.md` 的「后续演进」中记录：Hutool 雪花默认从 MAC/PID 派生 workerId，多副本部署前需改为显式 `IdUtil.getSnowflake(workerId, datacenterId)`
- [ ] 5.5 在代码生成处加注释标明「`code` 是业务编码，不是主键」，避免后续误用

## 6. 验收

- [ ] 6.1 执行 `./aicode/gradlew test`，全部测试 SHALL 通过
- [ ] 6.2 执行 `./aicode/gradlew :aicode:bootBuildImage`，确认镜像构建成功
- [ ] 6.3 确认 `openspec validate 2026-10-14-replace-baidu-uid-generator --strict` 通过
- [ ] 6.4 记录回滚前提：若需回滚本变更，必须同时恢复 jar、第二数据源配置与 `worker_node` 建表语句
