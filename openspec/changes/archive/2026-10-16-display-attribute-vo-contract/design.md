# Design

## Context

见 `proposal.md - Why`。设计上需要处理的现状约束：

- `MapFieldColumn` 有 24 个声明字段 + 5 个 `@TableField(exist = false)` 派生字段 + 2 个嵌套实体引用（`displayAttribute` / `mapRelationship`）。
- `DisplayAttribute` 有 18 个字段，全部是表单渲染所需的 UI 契约属性 —— 它**本身**就是「展示属性」的载体，不需要裁剪。
- `MapRelationship` 内嵌 `MapClassTable`，而 `MapClassTable` 又持有 `List<MapFieldColumn>`（很可能）—— 若整树序列化，深度与数据量都不受控。
- 现有 Angular 前端用 `url.attributes.list` 取 `displayAttribute` 子对象（依据 proposal 的分析），不依赖顶层派生字段。

## Goals / Non-Goals

**Goals:**

- 让 `/displayAttribute/list` 的响应恰好等于「元驱动表单渲染所需的最小数据集」。
- 为后续 `contracts/openapi/openapi.yaml` 的自动生成提供干净的契约面。

**Non-Goals:**

- 不动 `/displayAttribute/save` 的入参（当前收 `List<DisplayAttribute>` 实体，属入参问题，另议）。
- 不重构 `MapFieldColumn` 实体本身。
- 不引入通用 VO 转换框架（MapStruct 等）—— 一个端点不值得，且会与 MyBatis-Plus 的映射配置纠缠。

## Decisions

### D1：手工声明 VO，不用反射拷贝

**决定**：`MapFieldColumnVO` 与 `DisplayAttributeVO` **手工声明字段**，在 Controller 内逐字段装配。

**理由**：自动拷贝（`BeanUtils.copyProperties`）虽然省事，但会让「VO 是实体的字段子集」这一约束**失效** —— 加一个实体字段就可能被悄悄带进响应，而本变更的全部意义就是阻止这件事。手工声明让契约面**在代码里显式可见**，与 spec 的「VO 字段是前端实际消费集的子集」直接对应。

**备选方案**：(a) `BeanUtils.copyProperties` —— 省代码，但约束落空；(b) MapStruct —— 需要新增依赖与注解处理器，对 2 个 VO 属过度工程。

### D2：`DisplayAttributeVO` 保留全部 18 个字段，不裁剪

**决定**：`DisplayAttributeVO` 声明与 `DisplayAttribute` 相同的字段集合。

**理由**：`DisplayAttribute` 本身就是「前端表单的 UI 契约」，它的字段**全部**是渲染所需（控件类型、必填、匹配方式、校验模式、提示语、显示序、列表/详情可见性、换行、CSS…）。裁剪它等于裁剪功能。spec 中「VO SHALL NOT 包含派生字段」针对的是**数据库实体的计算字段**（如 `checkDate` 由 `isDate` 推导），不适用于这个专门的契约对象。

命名保持与实体一致，是为了让前端配置界面的表述与接口字段一一对应（spec 的「展示属性的嵌套表达保持扁平可读」）。

### D3：`MapFieldColumnVO` 只保留 6 个字段 + 展示属性

**决定**：`MapFieldColumnVO` 声明 `id`、`code`、`column`、`field`、`fieldType`、`sqlType` 六个标量字段，加一个 `DisplayAttributeVO displayAttribute`。

**排除的字段及理由**：

| 排除 | 理由 |
|---|---|
| `checkDate` / `checkState` / `checkPk` / `checkDigit` / `upper` | 派生字段（`@TableField(exist=false)`），由 `isDate` / `isState` / `isPrimaryKey` / `fieldType` 推导，符合 spec 排除项 |
| `mapRelationship` | 嵌套实体（内嵌 `MapClassTable`，其内又持字段列表），形成不受控对象树 |
| `mapClassTableCode` / `notes` / `defaultValue` / `isPrimaryKey` / `isDate` / `isState` | 渲染动态表单不需要；但 `isDate` / `isState` / `isPrimaryKey` 是派生标记的**来源**，前端若要显示「是否必填」等无需它们 |

> **待实施时复核**：`isPrimaryKey` / `isDate` / `isState` 是否被前端某个控件（如 `Switch` 的禁用态）需要。若需要则补入，spec 允许 —— 它们不是派生字段本身，是源数据。

### D4：不改 `/displayAttribute/save`

**决定**：入参保持 `List<DisplayAttribute>` 实体不变。

**理由**：入参侧的「实体直接暴露」问题性质不同 —— 那是**前端写入**的数据，直接对应一张表的持久化形态，用实体反而自然。spec 本次只约束出参。改入参会牵动前端保存逻辑，超出范围。

### D5：单测断言「响应中不出现派生字段」

**决定**：新增一条断言，检查 `/displayAttribute/list` 的响应 JSON 中**不含** `checkDate` / `checkState` / `checkPk` / `checkDigit` / `upper` / `mapRelationship`。

**理由**：这类字段「多出来」在运行期不会报错，只会让契约文件越来越臃肿。**不写断言就没有防线** —— 与上一轮 `MapFieldColumnMapperIT` 的教训同理（当时的回归之所以全绿通过，正是因为没有测试断言「不该出现的字段」）。该断言直接对应 spec 的前两条 Scenario。

## Risks / Trade-offs

**[字段裁剪过度导致前端缺字段]** → D3 已标注待复核项；M1/M2 实现 MetaForm 时若发现缺字段，按 spec 允许的方式补入（作为标量字段），而不是退回实体。

**[VO 与实体字段漂移]** → 手工声明的代价是可能漏更新。缓解：VO 只声明前端消费集，实体字段变动时需人工判断是否同步；`api-data-object` 的 spec 已把「VO 是子集」写成约束。

**[Angular 旧前端若真的依赖了被移除的字段]** → 实施时以 grep 核实。proposal 已把它列为 BREAKING 并要求核实无调用方。

## Migration Plan

单批交付，无数据迁移：

1. 新增两个 VO
2. `DisplayAttributeController.list` 改装配 VO 返回
3. 新增断言测试（响应字段集合正确 + 派生字段缺席）
4. 全量回归，确认 `ApiContractIT` 仍绿
5. 归档

**回滚**：单一提交，revert 即可。

## Open Questions

- `contracts/openapi/openapi.yaml` 的产出与 orval 链属于 M0，不在本变更；本变更完成后响应面即已就绪，可被 M0 直接纳入契约生成。