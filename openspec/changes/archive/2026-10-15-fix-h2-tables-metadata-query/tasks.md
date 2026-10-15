# Tasks

> 回归来源：本缺陷由 `2026-10-14-upgrade-spring-boot-4-1`（H2 1.4.200 → 2.4.240）引入。
> 每组任务完成后 SHALL 独立验证，SHALL NOT 累积多个失败点一起排查。

## 1. 修正元数据查询的表类型条件（核心修复）

- [x] 1.1 修正 `aicode/src/main/resources/mapper/database/table.xml` 中 `<select id="list">` 的条件：`TABLE_TYPE = 'TABLE'` → `TABLE_TYPE = 'BASE TABLE'`
- [x] 1.2 修正同文件 `<select id="load">` 的同一条件
- [x] 1.3 修正同文件 `<select id="count">` 的同一条件
- [x] 1.4 在 `table.xml` 文件头部加注释，说明该字面量与 H2 版本绑定（1.4.200 为 `'TABLE'`，2.4.240 起为 SQL 标准的 `'BASE TABLE'`），并指向本变更记录
- [x] 1.5 确认 `aicode/src/main/resources/mapper/database/colum.xml` 中的 `INFORMATION_SCHEMA.COLUMNS` 查询是否存在同类硬编码值问题，一并修正或记录为无需修正

**检查点** ✅ 已通过（对 `java25_demo_2` 实测）

```
写法             | 匹配行数
旧写法 TABLE      | 0
新写法 BASE TABLE | 1
```

## 2. 异常不再被吞

- [x] 2.1 修改 `aicode/src/main/java/com/aicode/database/dao/DatabaseDAO.java` 的 `createDatabase`：`catch` 块中的 `log.error("{}", e.getMessage())` 改为**带堆栈**的日志（`log.error("建库失败, schema={}", database, e)`）
- [x] 2.2 在同一 `catch` 块中，清理已创建的 schema 后**抛出**异常，异常信息 SHALL 包含 schema 名，SHALL 保留原始异常为 cause
- [x] 2.3 确认所选异常类型与 `AGENTS.md` §6.7 的异常体系一致（业务异常继承 `BaseException`，由 `ExceptionHandle` 统一转换）
- [x] 2.4 确认异常上抛后 `ProjectServiceImpl.execute()` 的行为：SHALL NOT 继续执行 `parse()`，SHALL 直接把失败暴露给调用方

**检查点**：导入一段必然失败的 SQL（如 `CREATE TABLE x (id int, id int);` 重复列），`POST /project/init` 期望返回可定位的错误而非 `9006`，且日志中含完整堆栈

## 3. 「库已存在」分支改为校验

- [x] 3.1 修改 `ProjectServiceImpl.createDatabase`：`databaseDAO.count(database) > 0` 的分支 SHALL 调用 `tableDAO.list(database)` 校验是否真有表
- [x] 3.2 校验通过（有表）→ 保持现有幂等行为：记录日志并返回
- [x] 3.3 校验不通过（schema 存在但无表）→ 抛出明确错误，提示「schema 已存在但为空」，SHALL NOT 盲目跳过后让用户在后续步骤收到语义不明的 9006
- [x] 3.4 确认该错误码与「建表失败」「表探测为空」三者可被区分（对应 spec 的「不同成因可被区分」场景）

## 4. 元数据探测的集成回归测试

- [x] 4.1 新增 `aicode/src/test/java/com/aicode/database/mapper/TableMapperIT.java`，采用 `@SpringBootTest` + `@ActiveProfiles("test")`，SHALL 使用真实 H2 内存库（SHALL NOT mock `TableMapper` —— 要守护的正是元数据语义，mock 等于没测）
- [x] 4.2 场景①：建 schema → 建表 → `list(database)` SHALL 返回该表
- [x] 4.3 场景②：`load(database, tableName)` SHALL 返回该表信息
- [x] 4.4 场景③：`count(database)` SHALL 返回表数量
- [x] 4.5 场景④：建含多列的表 → 列探测 SHALL 返回全部列定义
- [x] 4.6 场景⑤：空 schema → `list()` SHALL 返回空结果（验证"无表"这一边界可被正确表达）
- [x] 4.7 确认测试自清理：schema 名 SHALL 使用测试专属前缀，避免与 `application-test.yml` 初始化的业务表冲突
- [x] 4.8 在测试类 Javadoc 中说明该测试的定位：**守护元数据语义**，与 `ApiContractIT`（守护 REST 层）互补，SHALL NOT 相互替代

## 5. 端到端回归

- [x] 5.1 启动应用，`POST /project/build` 建项目（`englishName` 用新的、不与遗留 schema 冲突的名字）
- [x] 5.2 `POST /project/sql/build` 导入用户提供的 `anchor_gift` SQL
- [x] 5.3 `POST /project/init` **期望 `code=0000`**（当前为 `9006`）
- [x] 5.4 确认 `map_class_table` 与 `map_field_column` 表已写入对应记录（解析结果落库）
- [x] 5.5 重复触发一次 `init`，确认幂等：应正常返回且不产生重复映射数据（对应「已存在且含表的 schema 正常跳过」场景）
- [x] 5.6 执行 `./aicode/gradlew test`，确认全部测试通过（既有 148 用例 + 新增 `TableMapperIT`）
- [x] 5.7 执行 `./aicode/gradlew :aicode:test --tests "com.aicode.contract.*"`，确认契约测试仍全绿（本次改动 SHALL NOT 影响 REST 契约）

## 6. 文档与遗留清理

- [x] 6.1 在 `AGENTS.md` §10「容易踩坑的点」新增一条：**H2 元数据的 `TABLE_TYPE` 值随版本变化**，升级 H2 时必须复核 `mapper/database/table.xml` 中的硬编码字面量
- [x] 6.2 在 `AGENTS.md` §10 记录本次遗留的脏数据：`java25_demo` / `java25_demo_2` / `repro_demo` 三个 schema 及 `anchor_gift` 表是复现过程的产物，SHALL NOT 被当作正常数据
- [x] 6.3 确认 `AGENTS.md` §4 对 H2 2.4.240 的描述与本次修复后的一致（本次不改版本，仅改查询）
- [x] 6.4 确认 `runtime-platform` 能力中「嵌入式数据库与驱动版本基线」相关场景仍成立；若需补充「元数据语义 SHALL 与数据库版本匹配」的要求，则为该能力建立 delta

## 7. 验收

- [x] 7.1 `TableMapperIT` 全绿 ✅（10 个用例）
- [x] 7.2 `./aicode/gradlew test` 全绿 ✅ 26 套件 / 193 用例 / 0 失败 / 2 跳过
- [x] 7.3 端到端 `POST /project/init` 返回 `0000` ✅
- [x] 7.4 确认「建表失败」与「schema 为空」两种成因返回可区分的错误 ✅
      > 建表失败 → `DatabaseException.of("9999", "数据库[x]建库建表失败，请检查导入的 SQL 是否合法", cause)`，日志带完整堆栈
      > schema 已存在但为空 → `DatabaseException.of("9006", "数据库[x]已存在但为空表，请清理该 schema 或更换项目英文名", null)`
      > ⚠️ 注意：`ExceptionHandle` 会把 `getLocalizedMessage()` 直接交给 `JSON.parseObject`，**非 JSON 的 message 会让异常处理器自身崩溃**（已实测）。因此异常 message 一律经 `DatabaseException.jsonMessage()` 构造
- [x] 7.5 `openspec validate 2026-10-15-fix-h2-tables-metadata-query --strict` 通过 ✅
- [x] 7.6 `database-introspection` 的 4 条 requirement 全部有实现与测试覆盖
      > ① 基础表类型值随数据库实际返回 → `table.xml` 修正 + `TableMapperIT` 的版本绑定守卫用例
      > ② 探测失败须暴露真实原因 → `DatabaseDAO` 上抛 + 带堆栈日志 + 7.4 的可区分性验证
      > ③ 已存在的 schema 需校验 → `ProjectServiceImpl` 改为校验表数量
      > ④ 集成测试守护 → `TableMapperIT` 10 个用例（list/load/count/列探测/空 schema/视图排除/别名映射/版本守卫）
