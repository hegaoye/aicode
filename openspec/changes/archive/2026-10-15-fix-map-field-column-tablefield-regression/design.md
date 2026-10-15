# Design

## Context

见 `proposal.md - Why`。设计上需要解决的现状约束：

- `MapFieldColumn` 的数据库列名是 camelCase（`mapClassTableCode`、`sqlType`、`isDate`…），而 MyBatis-Plus 的默认命名策略产出 snake_case，故这 7 个字段**必须**有显式 `@TableField("...")`。
- 另有 5 个派生字段（`checkDate`/`checkState`/`checkPk`/`checkDigit`/`upper`）**必须**标 `@TableField(exist = false)`，否则会被当作真实列写进 SQL。
- `MapFieldColumn` 位于 `facade`，`Field` 位于 `common`，模块依赖方向为 `aicode → facade → common`。

## Goals / Non-Goals

**Goals:**

- 恢复代码生成主链路可用性。
- 让「实体字段 ↔ 真实列」的映射成为**被测试断言的属性**，而非靠人工评审保证。

**Non-Goals:**

- 不再尝试以任何形式消除 `MapFieldColumn` / `Field` 的字段重复。
- 不修改数据库 schema、不调整 MyBatis-Plus 全局命名策略。
- 不追查其他实体是否存在同类隐患（本次只修被观测到的那一处；新增测试会自动覆盖其他实体的映射）。

## Decisions

### D1：放弃基类抽取，而非给 `common` 加持久化注解依赖

**决定**：`Field` 与 `MapFieldColumn` 各自独立持有字段；删除 `AbstractField`。

**理由**：Java 中**字段注解只能声明在定义该字段的类上**，子类无法为父类字段补 `@TableField`。因此一旦把 `mapClassTableCode` 等字段移入基类，映射注解就只剩两个选择：把注解搬到基类（要求 `common` 依赖 MyBatis-Plus 注解包），或放弃映射（本次事故）。

这推翻了前一变更 `design.md - D4` 的隐含前提 —— 该设计称「基类刻意不引入框架注解，以保持 `common` 依赖面最小」，但**在 Java 的注解继承规则下，「有框架注解的基类」与「无框架注解的基类」之间没有中间选项**：前者污染 `common`，后者丢失映射。

**备选方案**：(a) 给 `common` 加 `com.baomidou:mybatis-plus-annotation`（仅注解、无 MyBatis 依赖）并把 12 个注解放到基类 —— 保留了去重，但 `Field` 这个模板 DTO 也会被迫继承它完全用不到的持久化注解，`common` 的模块定位被破坏；(b) 配置 MyBatis-Plus 全局 `column-naming` —— 全局影响所有实体，风险外溢，且无法表达 `exist = false`。

**权衡**：明确放弃「消除 17 个重复字段声明 + 5 个重复方法」的收益（约 40 行）。用 40 行重复代码换取代码生成主链路可用，是划算的。

### D2：集成测试走 `@SpringBootTest` + 真实 H2 schema

**决定**：新增 `MapFieldColumnMapperIT`，用 `@SpringBootTest` 启动真实上下文，连真实的 `mapFieldColumnMapper`，对**真实 schema** 执行 select / insert / update。

**理由**：本次事故的根因是「列映射从未被执行过」。任何不真正产生 SQL 的测试都无法捕获它 —— 这一点在 `proposal.md - Why` 中已有实例（215 个测试全绿而线上立即失败）。因此守护该属性的测试**必须**真的生成并执行 SQL。

**关键点**：测试必须使用**真实的 `db/schema.sql`** 建表。若用 H2 默认命名（`MAP_FIELD_COLUMN.MAPCLASSTABLECODE`）或让 H2 自动建表，列名会与生产不符，测试就又变成假阳性。

**备选方案**：仅断言 `TableInfoHelper.getTableInfo(MapFieldColumn.class)` 的列名列表 —— 比真实 SQL 轻，但要求测试知道 MyBatis-Plus 内部 API，且一旦注解机制变化（如 `@TableField` 语义调整）断言可能仍成立而 SQL 已错。真实 SQL 更接近生产。

### D3：显式断言「排除持久化的字段不出现在 SQL 中」

**决定**：集成测试 SHALL 断言由该实体生成的 SQL 中**不含** `check_date` 等 5 个派生列。

**理由**：这 5 个字段与前 7 个是**两类不同的失败模式**。前 7 个是「列名写错」，报错信息直接指向列名；后 5 个是「本不该出现的列混入了 SELECT」，若表的列恰好存在则不会报错、只会让查询变慢并在写入时抛异常。仅断言「能查通」不足以守护后者。

## Risks / Trade-offs

**[回退后重复代码回归]** → 重复的字段与方法是**显式的、可见的**，且现在有集成测试守护映射正确性。相比之下，隐藏的映射错误会直接使功能不可用。此权衡已在上一个变更中被证伪过一次。

**[集成测试可能较慢]** → `@SpringBootTest` 启动真实上下文约数秒。`MapFieldColumnMapperIT` 单个测试类，可接受；后续若测试集整体变慢，可考虑按需切片。

**[测试依赖 `schema.sql` 的列名]** → 若未来调整 schema 列名，测试会失败 —— 这正是期望行为（提示同步更新实体的 `@TableField`）。

**[其他实体可能存在同类隐患]** → 本次只修了被观测到的 `MapFieldColumn`。新增集成测试可按同一模式逐步覆盖其余实体，列入 §7.10 的补齐清单。

## Migration Plan

单批交付，无需数据迁移：

1. 删除 `AbstractField`，恢复两个类的独立字段声明与 12 个 `@TableField`
2. 新增 `MapFieldColumnMapperIT`
3. 更新 ArchUnit 豁免集与 `AGENTS.md`
4. 全量回归 + 手工验证一次构建链路

**回滚策略**：本次修复本身即为回退操作，无需额外回滚路径。

## Open Questions

无。其余实体的映射测试覆盖范围可按 §7.10 的优先级逐步扩展，不影响本批次的做法与验证门。
