# Proposal

## Why

Spring Boot 4.1 升级把 H2 从 **1.4.200 升到 2.4.240**，其中一项未被察觉的行为变更直接使「解析数据库」功能全面失效：

```
H2 1.4.200:  INFORMATION_SCHEMA.TABLES.TABLE_TYPE = 'TABLE'
H2 2.4.240:  INFORMATION_SCHEMA.TABLES.TABLE_TYPE = 'BASE TABLE'      ← SQL 标准值
```

`aicode/src/main/resources/mapper/database/table.xml` 把旧值 `'TABLE'` **硬编码进了 SQL 条件**，因此升级后所有元数据查询恒定匹配 0 行。

```
ProjectServiceImpl.parse()
  └─ tableDAO.list(englishName)  →  0 行
  └─ isEmpty() → throw 9006
```

实测证据（同一实例内，只差一个词）：

| 条件 | 匹配行数 |
|------|---------|
| `TABLE_TYPE = 'TABLE'`（现写法） | **0** |
| `TABLE_TYPE = 'BASE TABLE'`（H2 2.x 实际值） | **1** |

**危害在于它是静默的**：建表照常成功（`repro_demo.anchor_gift` 确实建出来了），只有后续的元数据读取失效。而代码生成主链路的第一步「解析数据库信息」依赖它，等于**整个代码生成功能不可用**，且不产生任何显式错误线索。

## What Changes

- **修正 `table.xml` 中三处硬编码的 `TABLE_TYPE = 'TABLE'`**（`list` / `load` / `count`）为 H2 2.x 的实际取值，并在文件中注释说明该值与 H2 版本绑定
- **修正 `colum.xml` 中已被 H2 2.x 移除的 `TYPE_NAME` 列**为 `DATA_TYPE`，并用 `AS typeName` 显式声明别名
  > 此项为实施阶段由新增的 `TableMapperIT` 捕获的**第二处同类破坏性变更**，起草时的人工审查未发现。`Column` 实体的属性名是 `typeName`，与 `DATA_TYPE` 非驼峰对应，不加别名时 `typeName` 静默为 `null`，进而在 `DatabaseDataTypesUtils.isDate()` 抛 NPE —— 把 SQL 层的错误伪装成空指针，比第一处更难定位
- **让 `DatabaseDAO.createDatabase` 不再吞异常**：建库/建表失败时上抛，附带可定位的上下文（schema 名 + 原始异常），取代当前「只打一行 `e.getMessage()` 然后删库」的静默失败
- **修正「库已存在则跳过建库」的分支**：当前 `count > 0` 直接返回，不校验该 schema 内是否真的有表；空 schema 会让后续 `parse` 报出与「建表失败」完全相同的 9006，无法区分
- **补充 `TableMapper` 的集成回归测试**：建 schema → 建表 → 调 `list/load/count` → 断言能查到。目标是让任何未来的 H2 升级在测试阶段就变红，而不是等到真人点「解析」才发现

## Capabilities

### New Capabilities

- `database-introspection`: 平台从目标数据库的元数据中探测表与列的契约，包含三点 —— 探测 SHALL 能识别当前数据库版本实际返回的基础表类型值；探测失败时 SHALL 暴露可定位的真实原因而非统一的兜底错误码；探测行为 SHALL 有集成测试守护，使数据库升级造成的元数据语义变化在测试阶段即被发现。

### Modified Capabilities

- `runtime-platform`: 「嵌入式数据库与驱动版本基线」SHALL 增加两条要求 —— 升级数据库版本时 SHALL 复核所有 `INFORMATION_SCHEMA` 查询所用的列名与取值形式；元数据列名与实体属性名非驼峰对应时 SHALL 显式声明列别名，SHALL NOT 依赖自动映射产生「查询成功但属性为 null」的静默失败。

> 本次新增的是此前无人覆盖的能力面：现有 `api-contract` 定义的是 REST 响应格式，`runtime-platform` 定义的是版本基线，而「如何可靠地从数据库元数据读到表与列」两者都不涵盖。

## Impact

- **修改文件**：
  - `aicode/src/main/resources/mapper/database/table.xml`（三处 `TABLE_TYPE` 条件）
  - `aicode/src/main/resources/mapper/database/colum.xml`（`TYPE_NAME` → `DATA_TYPE` + 列别名）
  - `facade/src/main/java/com/aicode/exceptions/DatabaseException.java`（新增）
  - `aicode/src/main/java/com/aicode/database/dao/DatabaseDAO.java`（异常不再吞）
  - `aicode/src/main/java/com/aicode/project/service/ProjectServiceImpl.java`（`createDatabase` 的「库已存在」分支不再盲跳）
- **新增文件**：`aicode/src/test/java/com/aicode/database/mapper/TableMapperIT.java`
- **回归来源**：本缺陷由 `2026-10-14-upgrade-spring-boot-4-1`（H2 1.4.200 → 2.4.240）引入，属该变更的漏网项
- **不影响**：REST 接口契约（`api-contract`）、标识符策略、生成产物内容
- **验收命令**：
  - `./aicode/gradlew :aicode:test --tests "com.aicode.database.*"`
  - 端到端：建项目 → 导入 SQL → `POST /project/init` 期望 `code=0000`
