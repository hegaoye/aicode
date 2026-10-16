# Design

## Context

第 ④ 步（配显示属性）仍是只读 —— `/displayAttribute/list` 已接入（M1），
`POST /displayAttribute/save` 从未被前端调用。

接它之前核实发现分发层从建立起就断裂：后端 `DisplayAttributeDisplayType` 有 **24** 个
PascalCase 枚举，前端 `CONTROLS` 的 4 个 key（`text` / `number` / `date` / `select`）
做 `.toLowerCase()` 匹配 —— **实际只有 `Select` 命中**，其余 23 个永远走 `UnknownControl`。

**而 M1 的实现严格符合 spec** —— `aicode-web-page` 的既有 Scenario 原文是「至少覆盖
`text` / `number` / `date` / `select` 四类」。**漏洞在 spec**：它假设了 `displayType` 的值域
就是那四个小写串。本变更修 spec，而非责怪实现。

## Goals / Non-Goals

**Goals:**

- 修分发层：key 与后端枚举对齐，至少正确命中 `Input` / `InputNumber` / `DatePicker` / `Select`；
- 三个受约束字段的值域进契约（`enum` 而非裸 `string`）；
- 接 `POST /displayAttribute/save`，`DisplayAttributesPage` 升级为可编辑（8 个高频字段）。

**Non-Goals:**

- **不实现全部 24 个枚举的控件**（用户拍板范围）。未实现的仍走 `UnknownControl`，
  但降级标识要能显示"该类型尚未支持"。
- 不做剩余 11 个字段的表单（见 proposal 的字段清单与理由）。
- 不改 `DisplayAttributeController` 的业务逻辑（逐条 upsert、找不到字段就 `continue`）。
- 不做关系的删除 / 回滚 UI。

## Decisions

### D1：分发器 key 用「后端枚举的小写形式」，不改后端枚举

**决定**：`CONTROLS` 的 key 改为 `input` / `inputnumber` / `datepicker` / `select`
（后端枚举的小写），分发时仍用 `attribute.displayType?.toLowerCase()` 查表 ——
大小写容错这层保留。

**理由**：改后端枚举会波及生成代码（模板侧可能在拼 `displayType` 字符串）与既有数据；
改前端 key 是零风险的。`.toLowerCase()` 让契约里未来若出现 `INPUT` / `input` 两种写法都能命中。

**否掉的备选**：前端维护一张「后端枚举 → 我的控件」映射表 —— 那等于把 key 拆成两处，
更容易漂移，且违背 delta spec 的「SHALL 从契约 enum 读值域」。

### D2：值域进契约用 `@Schema(allowableValues = ...)`

**决定**：给 `DisplayAttribute` 实体的 `displayType` / `matchType` / `fieldValidationMode`
三个字段加 `@Schema(allowableValues = {...})`，值域**从枚举类读取**而非手写字面量。

```java
@Schema(description = "控件类型", allowableValues = {"Input", "InputNumber", ...})
private String displayType;
```

**⚠️ 手写字面量的风险**：24 个值手写一遍，枚举加一个就得记得同步这里。**tasks 里加一条核实项** ——
看能否用 `allowableValues` 接枚举类的 `values()`（SpringDoc 支持传入 `Class<? extends Enum>`
的数组，若版本支持则可零手写）。若不支持，手写 + 契约测试的"集合一致"断言兜住。

**为什么必须进契约**：前端要据此生成下拉。值域藏在 `description` 的自然语言里，
前端只能硬编码一份自己的副本 —— 那份副本在后端加枚举时会静默过期。

### D3：契约枚举 → 前端下拉的取值路径（实施期核实结论：比预期好）

**核实结论（Task 0.1）**：orval 把 springdoc 的 `enum` 生成为 **`as const` 对象**，
形态与本仓既有的 `Build2OneToOne` 完全一致：

```ts
export const DisplayAttributeVODisplayType = {
  Autocomplete: 'Autocomplete',
  // ...共 24 项
} as const;
```

**故 D3 走的是"理想路径"而非备选**：前端直接
`Object.values(DisplayAttributeVODisplayType)` 拿到全集，**零手写** ——
delta spec 的「SHALL 从契约 enum 读值域而非硬编码副本」天然满足。

原计划里"若只生成为字符串联合类型，则前端须显式列出并由测试钉住"的备选路径**不需要**。

### D3b：`allowableValues` 必须手写 24 个字面量（Task 0.2 核实结论）

Java 注解属性只接受**编译期常量**。实测两条路都走不通：

| 尝试 | 结果 |
|---|---|
| `allowableValues = DisplayAttributeDisplayType.ALLOWED_VALUES`（引用 `static final String[]`）| ❌ `incompatible types: String[] cannot be converted to String` —— **数组引用不是常量表达式** |
| 抽一个常量数组到枚举类同文件 | ❌ 同上（同一个原因） |
| 在 `@Schema` 处直接写字面量数组 | ✅ 通过 |

**故只能手写 24 个字面量。** 漂移风险的两道防线：
1. `@Schema` 的 `description` 里写明「新增枚举时必须同步此处 + 一致性由 ApiContractIT 守护」；
2. **Task 2.1 用例 2**：`ApiContractIT` 场景⑬ 断言「契约 `enum` 取值集合 == `DisplayAttributeDisplayType.values()` 的名称集合」。

> **反向验证已实测**：删掉字面量 `"Autocomplete"` → 用例 2 立刻红，
> 错误精确指出「缺失=[Autocomplete]」。这是手写字面量唯一可靠的防线。

### D4：表单提交按「当前页所有行」批量发

**决定**：`DisplayAttributesPage` 一次提交整页所有行的 8 个字段，
调 `POST /displayAttribute/save`（入参 `List<DisplayAttribute>`）。

**理由**：后端端点是逐条 upsert 的批量接口，逐行提交会产生 N 个请求且中途失败无法表达
"哪些成功了"。一次提交 → 后端返回单一 `R<Void>` → 结果要么全成要么有异常。

**⚠️ 后端语义的真实边界**：`save` 里对找不到 `mapFieldColumn` 的行是 `continue`（静默跳过），
且**没有事务**。所以"提交成功"不等于"全部写入"。

**处置**：成功后**重新拉取列表**并让用户看到实际结果（而非乐观更新）——
这样即使有行被静默跳过，用户也能从刷新后的数据看出来。若某行的控件类型在页面上
从"未知控件"变成了"文本框"，就是写进去了。

### D5：8 个高频字段的选择

| 字段 | 控件 | 理由 |
|---|---|---|
| `displayType` | 下拉（契约枚举）| 最核心 —— 直接决定生成的表单用什么控件 |
| `displayName` | 文本 | 列表/详情里显示的名字，用户可见度高 |
| `isRequired` | checkbox | 最常用的表单约束 |
| `isInsert` | checkbox | 新增表单里是否出现 |
| `isAllowUpdate` | checkbox | 编辑表单里是否出现 |
| `isListPageDisplay` | checkbox | 列表页显隐 |
| `isDetailPageDisplay` | checkbox | 详情页显隐 |
| `displayNo` | 数字 | 显示顺序 |

**排除的 11 个**：`matchType` / `fieldValidationMode` / `validateText` / `displayCss` /
`isDeleteCondition` / `isQueryRequired` / `isLineNew` / `mapClassTableCode` / `id` +
`checkDate` 等派生位。其中 `displayCss` / `validateText` 是给高级用户直接写代码的，
`matchType` / `fieldValidationMode` 需要配套的查询表单才能体现价值 —— 留待有真实需求再上。

**不做「全选/批量设置」**：一行一表单已经够宽，再加批量 UI 复杂度陡增。

### D6：`mapFieldColumnCode` 是隐藏的定位键

**决定**：表单不暴露 `mapFieldColumnCode` 输入框，从列表行的数据里带出。

**理由**：它是后端 upsert 的定位键（`mapFieldColumnService.getOne(eq(getCode, ...))`），
让用户编辑它等于让用户改主键 —— 出错会静默跳过（`continue`）且无提示。

## Risks / Trade-offs

**[`save` 无事务 + 静默跳过]** → D4 已给处置（提交后重拉，让实际结果可见）。
不在本变更引入事务包装 —— 那是后端行为变更，且当前是单用户工具场景。

**[`allowableValues` 手写 24 个值的漂移风险]** → D2 已标注；tasks 里先核实能否接枚举类，
不行则由契约测试的「集合一致」断言兜住。

**[`displayType` 改了会不会影响已生成的代码]** → `displayType` 只在生成前端代码时消费
（模板拼控件标签）。已生成的项目不受影响；下次构建会按新值生成。
需要提醒用户：**改完展示属性要重新构建才能看到效果** —— 这条提示要写进 UI。

**[orval 对 `allowableValues` 的生成形态未知]** → D3 已列为第一项核实，可能导致 D3 实现路径调整。

## Migration Plan

单批交付（一个 feature commit）：

1. 后端：`DisplayAttribute` 实体 3 个 `@Schema` 注解；
2. 契约产物 + `ApiContractIT` 加一条断言（`displayType` 是 `enum` 而非裸 `string`）；
3. `npm run generate`；
4. 前端：`display-controls` key 对齐 + 未知类型降级文案；
5. 前端：`DisplayAttributeForm`（新组件）+ `DisplayAttributesPage` 升级；
6. AGENTS.md 登记 + validate + archive。

**回滚**：单 commit revert。注意它依赖 M5 的 `ProjectSqlPanel` 等，无回滚冲突。

## Open Questions

- **`matchType` / `fieldValidationMode` 的完整值域** —— 实体的 `description` 里有中文列举
  （「匹配方式 =,!=,>=,<=,>,<,like,左like，右like」），但那是自然语言、可能有笔误。
  是否有对应的枚举类？tasks 里核实；若无，则这两项本变更**只加可用的枚举值、不强行穷举**。
- **24 个控件要不要逐步补齐** —— 本变更只做 4 类。补齐的收益取决于真实项目用到哪些；
  应由使用反馈驱动，不预判。
- **`DisplayAttributesPage` 现在只有 1 个用例** —— M1 遗留的测试债。本变更会大幅扩充它，
  但 M1 当初"1 个用例"这件事本身说明：**只写渲染断言的页面测试很容易漏掉系统性偏差**。