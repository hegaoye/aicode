# Proposal

## Why

`GET /displayAttribute/list` 当前**直接返回持久化实体** `List<MapFieldColumn>`，违反 `AGENTS.md §6.5`「实体不要直接暴露给前端；Controller 返回 VO」。

这不是洁癖问题，而是即将发生的实际故障：本分支要把前端换成 React + TypeScript，并采用「`openapi.yaml` 入库 → orval 生成类型 → 前端按类型开发」的契约驱动方式。一旦契约文件生成，**实体里未被 VO 声明的字段也会成为契约的一部分** —— `MapFieldColumn` 内嵌 `MapRelationship`（其内又嵌 `MapClassTable`），前端会拿到一棵本不该暴露的对象树；而 `MapFieldColumn` 上那些 `@TableField(exist = false)` 的派生字段（`checkDate` / `upper` 等）也会一并进入契约，让前端误以为它们是需要处理的数据。

在引入契约生成之前必须先把这个端点理干净，否则错误会被固化进 `contracts/openapi/openapi.yaml`，之后再改就要同时动契约、前端类型和 mock。

## What Changes

- 新增 `MapFieldColumnVO`，只声明元驱动表单**实际需要**的字段：标识与命名（`code` / `column` / `field` / `fieldType`）、以及内嵌的 `DisplayAttributeVO`（控件类型、必填、查询匹配方式、校验提示、显示序、列表/详情可见性等）。
- `GET /displayAttribute/list` 改返回 `List<MapFieldColumnVO>`，不再序列化实体。
- 新增 `DisplayAttributeVO`，同上，替代 `DisplayAttribute` 实体的直接序列化。
- 响应中**不出现**派生字段（`checkDate` / `checkState` / `checkPk` / `checkDigit` / `upper`）与嵌套的关联对象。

**BREAKING**：`data` 数组中每个元素的字段集合会变化 —— 移除派生字段与嵌套的 `mapRelationship`。当前无生产调用方（`/displayAttribute/*` 虽在 `FrontendEndpointCatalog` 的可达性断言中，但现有 Angular 前端使用 `url.attributes.list` 读取的是 `displayAttribute` 子对象，不依赖被移除的顶层字段）。变更实施时 SHALL 以 grep 核实无其他调用方。

## Capabilities

### New Capabilities

- `api-data-object`: 定义接口出参的数据对象契约 —— Controller SHALL 返回 VO 而非持久化实体；VO SHALL NOT 序列化派生字段与嵌套关联对象；元驱动表单所需的展示属性 SHALL 完整出现在 VO 中。

### Modified Capabilities

（无。本变更不改变 `api-contract` 的任何要求。）

> **备注**：`api-contract` 的 Purpose 写死了「仓库内静态前端（Angular 7.0.4 + ng-zorro + jQuery 的打包产物）」与「后端以可执行测试的方式显式守护」。当前前端尚未替换，该表述暂时仍成立。待 React 前端落地（M4）时 SHALL 对该 Purpose 做 MODIFIED —— 届时跨语言约定将由「TypeScript 类型 + 契约文件」而非「仅后端测试」来守护。此项已记入验收清单，**不在本变更范围**。

## Impact

- **代码**：新增 2 个 VO（`facade/src/main/java/com/aicode/display/vo/`）；`DisplayAttributeController.list` 改返回 VO；`MapFieldColumnMapperIT` 与 `ApiContractIT` 需确认仍通过。
- **契约**：`contracts/openapi/openapi.yaml`（M0 产出）中的 `/displayAttribute/list` 响应 schema 将从实体的完整形状变为 VO 的裁剪形状 —— 这正是期望结果。
- **规范**：AGENTS.md §6.5 已写明该规则，无需修改；本变更是让代码追上规范。
- **风险**：低。唯一实质变化是响应字段集合收窄，且已核实无生产调用方。