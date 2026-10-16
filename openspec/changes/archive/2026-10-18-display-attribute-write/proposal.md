# Proposal

## Why

接完 M5 第 ⑤ 步后，代码生成流程六步全部可达，但第 ④ 步（配显示属性）**仍是只读的** ——
`/displayAttribute/list` 已接入（M1），`POST /displayAttribute/save` 从未被前端调用。

接上它之前，核实发现**分发层从建立起就是断裂的**：

| | 数量 |
|---|---|
| 后端 `DisplayAttributeDisplayType` 枚举 | **24** 个（`Input` / `DatePicker` / `InputNumber` / `Select` / `Cascader` …）|
| 前端 `CONTROLS` 支持的 key | **4** 个（`text` / `number` / `date` / `select`），且用 `.toLowerCase()` 匹配 |
| **实际能命中** | **1** 个 —— 只有 `Select` |

```
'Input'.toLowerCase()      === 'input'      ≠ 'text'
'DatePicker'.toLowerCase() === 'datepicker' ≠ 'date'
'InputNumber'.toLowerCase()== 'inputnumber' ≠ 'number'
```

**其余 23 个枚举值永远走 `UnknownControl`。** M1 的 `DisplayAttributePage` 只有 1 个用例，
从未用真实数据验证过这条链路。

### 而且 spec 本身允许了这种不一致

`aicode-web-page` 的既有 Requirement「业务页面 SHALL 把统一响应解析与控件分发抽象为可复用单元」
的 Scenario 原文是「控件分发器 SHALL 渲染对应的基础控件（**至少覆盖 `text` / `number` /
`date` / `select` 四类**）」—— M1 的实现**严格符合 spec**。

问题在于 spec **假设了 `displayType` 的值域就是那四个小写串**，而实际后端是 24 个 PascalCase
枚举。**这是 spec 的漏洞，不是实现的 bug。** 本变更修 spec。

### 同时：契约里三个受约束字段全是无约束字符串

`displayType` / `matchType` / `fieldValidationMode` 在 `contracts/openapi/openapi.yaml` 里
只有 `type: string`，值域写在 `description` 的自然语言里（如「匹配方式 =,!=,>=,<=,>,<,like,
左like，右like,...」）。前端无法据此生成下拉或做类型校验。

## What Changes

### 1. 契约：三个字段的值域进契约

给 `DisplayAttribute.displayType` / `matchType` / `fieldValidationMode` 加
`@Schema(allowableValues = {...})`，让 springdoc 导出为 `enum`，前端据此生成下拉。

### 2. 前端：修分发层（本变更的重点）

- `CONTROLS` 的 key 从 `text` / `number` / `date` / `select` 改为**与后端枚举对齐**：
  `input` / `inputnumber` / `datepicker` / `select`（仍用 `.toLowerCase()` 归一，
  故大小写不敏感 —— 这层容错保留）；
- 补齐其余常用枚举的实现，先覆盖这四类（用户拍板范围），未实现的仍走
  `UnknownControl` 但**降级标识要能显示"该类型尚未支持"**；
- `SUPPORTED_DISPLAY_TYPES` 改为从**契约生成物**读枚举，不再手写常量 ——
  这样后端加枚举时前端自动知道，但没实现控件的类型仍走 `UnknownControl`。

### 3. 前端：接 `POST /displayAttribute/save`（8 个高频字段）

`DisplayAttributesPage` 从只读列表升级为**可编辑表单**（用户拍板范围）：

| 字段 | 控件 | 说明 |
|---|---|---|
| `displayType` | 下拉 | 选项来自契约枚举 |
| `displayName` | 文本 | 列表/详情里显示的名字 |
| `isRequired` / `isInsert` / `isAllowUpdate` | checkbox ×3 | 是否必填 / 可插入 / 可更新 |
| `isListPageDisplay` / `isDetailPageDisplay` | checkbox ×2 | 列表页 / 详情页显隐 |
| `displayNo` | 数字 | 显示顺序 |
| `mapFieldColumnCode` | 隐藏（取自行） | 提交的定位键 |

**刻意不做**的 11 个字段：`matchType` / `fieldValidationMode` / `validateText` /
`displayCss` / `isDeleteCondition` / `isQueryRequired` / `isLineNew` / `mapClassTableCode` /
`id` / `displayType` 之外的元数据。其中 `displayCss` / `validateText` 对普通用户无意义，
`matchType` / `fieldValidationMode` 留待有真实需求再上。

提交按**当前页的所有行**批量发（后端入参是 `List<DisplayAttribute>`，语义是 upsert）。

**契约**：orval 重新生成（`displayType` 变枚举）。

## BREAKING

无。前端此前从未消费 `save`；`DisplayControl` 的 key 变更是内部实现，M1 唯一的
`DisplayAttributesPage` 用例会同步更新。

## Capabilities

### Modified Capabilities

- **`aicode-web-page`** —— MODIFIED Req「业务页面 SHALL 把统一响应解析与控件分发抽象为可复用单元」：
  **补上既有 Scenario 的漏洞** —— 原文只说「至少覆盖 `text`/`number`/`date`/`select` 四类」，
  未要求这些 key 与后端 `displayType` 枚举对齐，导致 24 个枚举里只有 `Select` 能命中。
  改为明确要求「分发器的 key SHALL 与后端枚举逐一对齐，不可达枚举 SHALL 有可见降级标识」。
  并 ADDED 1 个 Requirement「列表类配置页 SHALL 支持批量提交配置项」。
- **`api-data-object`** —— MODIFIED Req「元驱动表单所需的展示属性 SHALL 完整出现在 VO 中」：
  补上「受约束字段（枚举型）SHALL 在契约中以 `enum` 表达值域，SHALL NOT 只写在 description 里」。

### New Capabilities

（无。）

## Impact

- **后端**：`DisplayAttribute` 实体的 3 个 `@Schema` 注解 + `contracts/openapi/openapi.yaml`。
- **前端**：`display-controls`（4 个控件 key + index 映射 + unknown 降级文案）、
  `DisplayAttributesPage`（升级为可编辑）、`DisplayAttributeForm`（新组件）、测试。
- **测试**：前端 119 → 约 130 用例；后端零新增测试（注解改动），但 `ApiContractIT` 可加一条
  断言 `displayType` 在契约里是 `enum` 而非裸 `string`。
- **风险**：**中**。① `DisplayControl` 是 M1 起就存在但从未被真实数据验证的代码，改它要补足测试
  ② 批量提交语义（后端逐条 upsert，部分失败无回滚）需要在 UI 上如实反映