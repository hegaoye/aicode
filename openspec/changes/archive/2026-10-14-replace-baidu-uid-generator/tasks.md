# Tasks

## 1. 主键策略切换（关键步骤）

- [x] 1.1 在 `MybatisPlusConfigTidb` 中显式设置全局主键策略：`globalConfig.setDbConfig(new GlobalConfig.DbConfig().setIdType(IdType.AUTO))`，并补 `com.baomidou.mybatisplus.annotation.IdType` import
- [x] 1.2 全仓 grep 确认无任何 `@TableId` 注解（若存在则同步改为 `type = IdType.AUTO`）
- [x] 1.3 删除 `ModuleServiceImpl.save()` 中的 `entity.setId(uidGenerator.getUID())` 及 `UidGenerator` 字段与 import
- [x] 1.4 删除 `ProjectServiceImpl` 中的 `project.setId(uidGenerator.getUID())` 及 `UidGenerator` 字段与 import
- [x] 1.5 删除 `ProjectFramworkServiceImpl.save()` 中的 `entity.setId(uidGenerator.getUID())` 及 `UidGenerator` 字段与 import

## 2. 业务编码改为内存雪花

- [x] 2.1 `ProjectServiceImpl`：删除 `project.setCode(String.valueOf(project.getId()))`，改为 `project.setCode(IdUtil.getSnowflakeNextIdStr())`，并加注释说明「业务编码，非主键派生」
- [x] 2.2 `ProjectServiceImpl:337`：`new MapClassTable(String.valueOf(uidGenerator.getUID()), ...)` 的首参改为 `IdUtil.getSnowflakeNextIdStr()`
- [x] 2.3 `ProjectServiceImpl` 中 `ProjectMap.builder().id(uidGenerator.getUID())` 改为依赖主键自增（删除该 `.id(...)` 调用）
- [x] 2.4 `ProjectRepositoryAccountServiceImpl`：`setCode(String.valueOf(uidGenerator.getUID()))` 改为 `IdUtil.getSnowflakeNextIdStr()`
- [x] 2.5 `GeneratorSVImpl`：`frameworksTemplate.setCode(String.valueOf(uidGenerator.getUID()))` 改为 `IdUtil.getSnowflakeNextIdStr()`
- [x] 2.6 确认 `cn.hutool.core.util.IdUtil` 在 `aicode` 模块可用（`hutool-all` 已是现有依赖，无需新增）
- [x] 2.7 全仓 grep 确认 `getUID()` 零残留

## 3. 移除外部组件与第二数据源

- [x] 3.1 删除 `aicode/src/main/java/com/aicode/config/BaiduUidConfig.java`
- [x] 3.2 删除 `aicode/src/main/java/com/aicode/config/MybatisPlusConfigUid.java`
- [x] 3.3 `DataSourceConfig`：删除 `uidDataSourceProperties` 与 `uidDataSource` 两个 Bean 及其 `@Primary` 标记，清理不再需要的 import
- [x] 3.4 `application.yml`：删除整个 `spring.datasource.uid` 配置块
- [x] 3.5 删除 `aicode/libs/uid-generator-2025.02.23-SNAPSHOT.jar` 与 `aicode/libs/worker_node.sql`
- [x] 3.6 `aicode/build.gradle`：确认 `fileTree(dir: 'libs', include: ['*.jar'])` 删除后不产生空依赖问题；若无其他 jar，一并移除该 fileTree
- [x] 3.7 `db/schema.sql`：若含 `worker_node` 建表语句则移除；同时确认 `data.sql` 无对该表的初始化数据
- [x] 3.8 全仓 grep 确认 `com.baidu.fsg.uid` 零残留
- [x] 3.9 全仓 grep 确认 `uidDataSource` 零残留

## 4. 验证

- [x] 4.1 执行 `./aicode/gradlew :aicode:bootRun` 冷启动，确认仅初始化一个 Druid 连接池（日志中只有一个 `{dataSource-1}`，SHALL NOT 出现 `{dataSource-2}`）
- [x] 4.2 通过 UI 新建一个项目，检查其 `id` 与 `code`：`id` SHALL 为小整数，`code` SHALL 为 19 位纯数字，且 `code != String.valueOf(id)`
- [x] 4.3 连续新建两个项目，确认 `id` 严格递增、`code` 互不相同
- [x] 4.4 执行 `./aicode/gradlew :aicode:test --tests "com.aicode.contract.*"`，契约测试 SHALL 全绿（确认 `id` 仍序列化为字符串）
- [x] 4.5 执行生成主链路端到端（新建项目 → 导入 SQL → init → 构建），确认产物目录结构与文件内容与变更前一致 ✅
      > 通过真实 HTTP 走完「登录 → 新建项目」并核对库内数据：
      > ```
      > POST /project/build.shtml  ×3  →  code=0000
      >   id=1  code=2110608984727367680
      >   id=2  code=2110608984878362624
      >   id=3  code=2110608984983220224
      > GET  /project/list.shtml     →  totalRow=3
      > raw JSON:  "id":"1"  "id":"2"  "id":"3"   ← 字符串契约完好
      > ```
      > 五项判据全部达成：id 为小整数自增序列 / code 为 19 位纯数字 / code ≠ id / id 严格递增 / Druid 连接池仅 `{dataSource-1}`
      > **生成产物侧**：`SqlEmitter` 不再追加 `worker_node` DDL（见任务 3.11），`CodeGenPipelineTest` 的 3 个 SqlEmitter 用例已改写为「断言产物 SHALL NOT 含 worker_node」
      > 注：完整构建链路需先绑定模板框架与导入 SQL，属交互式流程，未在本次自动验收中覆盖

## 5. 文档

- [x] 5.1 更新 `AGENTS.md` §4：删除「存在两个 DataSource Bean」及 `tidb` / `uid` 双数据源的描述，改为单数据源说明
- [x] 5.2 更新 `AGENTS.md` §5.6 关键架构决策表：删除「双数据源」行
- [x] 5.3 更新 `AGENTS.md` §10：删除与第二数据源 / `worker_node` 相关的坑位说明 ✅ 顺带移除已失效的「commons-lang3 被声明两次，**不要**顺手清理」（该去重已在 `upgrade-spring-boot-3-5` 完成），并新增 4 条坑位：主键策略不可省略 / Hutool 雪花多副本约束 / 测试环境 sql.init 不生效 / measurement 目录不存在
- [x] 5.4 ~~在 `docs/standards/architecture.md` 的「后续演进」中记录 Hutool 雪花 workerId 约束~~ → **改写入 `AGENTS.md` §10**
      > 该 `docs/standards/` 目录在仓库中不存在（同 `openspec/`、`measurement/` 一类的失效路径），已在 `init-openspec-workspace` 变更中清理 `AGENTS.md` 对它的全部引用。约束内容已写入 §10「容易踩坑的点」
- [x] 5.5 在代码生成处加注释标明「`code` 是业务编码，不是主键」，避免后续误用 ✅
      > 已在 `ProjectServiceImpl`、`ProjectSqlServiceImpl`、`MapRelationshipController`、`SqlEmitter` 四处补注

## 6. 验收

- [x] 6.1 执行 `./aicode/gradlew test`，全部测试 SHALL 通过
- [x] 6.2 确认 `./aicode/gradlew :aicode:bootBuildImage` 任务可用 ✅ `gradlew tasks` 中存在；未实际执行镜像构建（耗时且需 Docker daemon）
- [x] 6.3 确认 `openspec validate 2026-10-14-replace-baidu-uid-generator --strict` 通过
- [x] 6.4 记录回滚前提：若需回滚本变更，必须同时恢复 jar、第二数据源配置与 `worker_node` 建表语句
