# Proposal

## Why

`2026-10-15-dead-code-and-dependency-hygiene` 变更的任务组 4「`Field` 与 `MapFieldColumn` 去重」引入了一个**使代码生成主链路完全不可用的回归**：抽取共有基类 `AbstractField` 时，丢失了 `MapFieldColumn` 上**全部 12 个 `@TableField` 注解**（7 个显式列名 + 5 个 `exist = false`）。

后果是 MyBatis-Plus 回退到默认命名策略，把 camelCase 字段名转成 snake_case 列名（`mapClassTableCode` → `map_class_table_code`），同时把本应排除出持久化的 5 个派生字段当成真实列。生产环境执行 `GET /project/job/execute` 时立即抛出：

```
Column "map_class_table_code" not found; SQL statement:
SELECT id,map_class_table_code,code,column,field,sql_type,field_type,notes,
       default_value,is_primary_key,is_date,is_state,check_date,check_state,
       check_pk,check_digit,upper FROM map_field_column WHERE (map_class_table_code = ?)
```

该回归未被抓到，是因为**没有任何测试让 MyBatis-Plus 为 `MapFieldColumn` 生成 SQL 并真正打到数据库上** —— `ProjectServiceImpl` 的写入路径是纯内存的 `new MapFieldColumn()` + setter，而代码生成链路的测试用 `@MockitoBean` 打桩了 mapper。变更完成时 215 个测试全绿。

## What Changes

- **回退任务组 4 的基类抽取**：`MapFieldColumn` 与 `Field` 各自独立持有字段，恢复 `MapFieldColumn` 上的 12 个 `@TableField` 注解；删除 `AbstractField`。去重收益（消除 17 个重复字段声明与 5 个重复方法）明确放弃。
- **新增真实 SQL 集成测试**：以 `@SpringBootTest` + H2 真实建表，调用 `MapFieldColumnMapper.selectList` 等实际方法，使「实体字段与真实列的映射」成为被测试断言的属性。此类回归将在测试阶段变红，而不是等到用户手工触发构建。
- **记录并固化该教训**：在 `dependency-hygiene` 能力下新增要求 —— 实体与真实列之间的映射 SHALL 由真实 SQL 集成测试守护；SHALL NOT 依赖打桩 mapper 的测试来覆盖映射关系。
- **同步 ArchUnit 豁免集**与 `AGENTS.md` 中因基类抽取而产生的条目。

**BREAKING**：无对外接口变更。但 `AbstractField` 若被外部使用者引用将失效 —— 实测该类为本次变更新建，无任何外部调用方。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

- `dependency-hygiene`: 新增「实体与数据库列的映射 SHALL 由真实 SQL 测试守护」要求 —— 字段注解的增删改（含因继承结构调整而丢失）SHALL 被集成测试捕获；纯内存装配或打桩 mapper 的测试 SHALL NOT 被视为覆盖了列映射。

## Impact

- **代码**：删除 `common/.../core/AbstractField.java`；恢复 `facade/.../map/entity/MapFieldColumn.java` 与 `common/.../core/Field.java` 的独立字段声明与 12 个 `@TableField`。
- **测试**：新增 `MapFieldColumnMapperIT`（`@SpringBootTest` + H2），覆盖 selectList / insert / update 三条路径的实际列映射。
- **规范**：新增测试类与能力要求后需同步 `AGENTS.md` §6.3、§7.1、§7.10、§11.3。
- **风险**：回退后代码量回升约 40 行（重复的字段与方法声明）。这是为换取「代码生成主链路可用」而刻意付出的代价。
- **不变更**：MyBatis-Plus 版本、数据库 schema、序列化库、模板展开方式均不动。
