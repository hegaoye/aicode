# Tasks

## 1. 回退基类抽取，恢复映射注解

- [x] 1.1 删除 `common/src/main/java/com/aicode/core/tools/core/AbstractField.java`；验证文件不存在
- [x] 1.2 恢复 `facade/src/main/java/com/aicode/map/entity/MapFieldColumn.java` 为独立实体：恢复全部 `@TableField` 注解（实测 14 个 = 7 显式列名 + 7 `exist = false`）（`mapClassTableCode` / `sqlType` / `fieldType` / `defaultValue` / `isPrimaryKey` / `isDate` / `isState` 七个显式列名，`checkDate` / `checkState` / `checkPk` / `checkDigit` / `upper` 五个 `exist = false`），并恢复 `toJava()` 与 7 参构造器；验证编译通过
- [x] 1.3 恢复 `common/src/main/java/com/aicode/core/tools/core/Field.java` 为独立模板 DTO：恢复 17 个共有字段与 5 个派生方法（含 `toJava()` 的 `id` 特判分支）；验证编译通过
- [x] 1.4 更新 `VendoredDeadCodeIsolationTest` 的豁免集，移除 `AbstractField`；验证架构断言通过
- [x] 1.5 核对制品/实体：`git diff` 中确认 `MapFieldColumn` 的 `@TableField` 数量为 12；验证无遗漏

## 2. 真实 SQL 集成测试（守护列映射）

- [x] 2.1 新增 `MapFieldColumnMapperIT`，用 `@SpringBootTest` + 真实 `db/schema.sql` 建表，连真实 `MapFieldColumnMapper`；验证测试上下文可启动
- [x] 2.2 断言 `selectList` 能按 `mapClassTableCode` 条件查出数据（即显式列名映射生效）；验证测试通过
- [x] 2.3 断言插入 + 回查后，所有 12 个真实列的值均能正确读回；验证测试通过
- [x] 2.4 断言由该实体生成的 SELECT **不含** `check_date` / `check_state` / `check_pk` / `check_digit` / `upper` 五列（`exist = false` 生效）；验证测试通过
- [x] 2.5 **反向验证已执行**：删除 `@TableField("mapClassTableCode")` → 复现与生产**逐字一致**的 `Column "map_class_table_code" not found`，3/3 变红；删除 `@TableField(exist=false)`（checkDigit）→ 复现 `Column "check_digit" not found`，3/3 变红；两次恢复后均重新变绿

## 3. 规范同步

- [x] 3.1 同步 `AGENTS.md §6.3`：移除因基类抽取而产生的 `@SuperBuilder` 条目（该场景已回退），改为记录「实体字段映射注解不可随字段搬迁而丢失」；验证条文与代码一致
- [x] 3.2 同步 `AGENTS.md §6.8`：补充「新增/修改实体字段时 SHALL 同步复核 `@TableField`，并运行 `MapFieldColumnMapperIT`」；验证条文可执行
- [x] 3.3 同步 `AGENTS.md §7.1` 与 `§11.3` 的测试清单，登记 `MapFieldColumnMapperIT`；验证条目与实际测试类路径一致
- [x] 3.4 在 `AGENTS.md §7.10` 补齐清单中加入「其余实体的列映射集成测试」条目；验证与 §7.1 空白点一致

## 4. 验证与归档

- [x] 4.1 执行 `./aicode/gradlew test`；验证全部测试通过（含新增集成测试）
- [~] 4.2 手工验证构建链路 —— **未完全达成**：已用修复后的制品在 8090 端口启动实例，日志中 `map_class_table_code` 错误命中数为 **0**；但**未能真正触发一次完整构建**，因验证实例使用全新 H2 库而 `project` / `project_sql` 表按 `db/schema.sql` 每次启动被清空（见 AGENTS.md §9），无可构建项目。替代证据为 `MapFieldColumnMapperIT` 的双向反向对照（任务 2.5），其复现的错误信息与生产日志逐字一致。**该项 SHALL 由使用者在有存量数据的实例上补验。**
- [x] 4.3 执行 `openspec validate --strict fix-map-field-column-tablefield-regression`；验证无校验错误
- [x] 4.4 同步 delta spec 到 `openspec/specs/dependency-hygiene` 并执行 `openspec archive`；验证新 requirement 已物化
