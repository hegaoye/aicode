# Tasks

## 1. 核实影响面

- [x] 1.1 grep 全仓（`common` / `facade` / `aicode` 的 main 与 test、Angular 打包产物）确认 `/displayAttribute/list` 的调用方，及其读取的字段集合；验证结论为「不依赖将被移除的派生字段与 `mapRelationship`」
- [x] 1.2 确认 `MapClassTable` 是否持有 `List<MapFieldColumn>`，以证实「嵌套实体形成对象树」的判断；验证结论写入 `design.md - D3`

## 2. 新增 VO

- [x] 2.1 新增 `facade/src/main/java/com/aicode/display/vo/DisplayAttributeVO.java`，声明与 `DisplayAttribute` 相同的 18 个字段；验证编译通过且无框架注解泄漏
- [x] 2.2 **实施时发现 `MapFieldColumnVO` 与 `DisplayAttributeVO` 已存在且完全未被使用**（上一轮死代码扫描只覆盖 `common/core/tools/`，漏了 `facade`）。二者字段集合恰好正确 —— 已排除 5 个派生字段与 `mapRelationship`。因此本任务从「新增」变为「给已有 VO 补 `displayAttribute` 一个字段」
- [x] 2.3 确认两个 VO 均不包含 `checkDate` / `checkState` / `checkPk` / `checkDigit` / `upper` / `mapRelationship`；验证 grep 无命中

## 3. Controller 改造

- [x] 3.1 `DisplayAttributeController.list` 改为返回 `List<MapFieldColumnVO>`，逐字段手工装配；验证 `/displayAttribute/list` 的 `data` 元素字段集合等于 VO 声明
- [x] 3.2 保留 `@Operation` / `@Parameter` 注解与端点路径不变；验证 `FrontendEndpointCatalog` 无需改动

## 4. 契约守护测试

- [x] 4.1 新增断言：`/displayAttribute/list` 响应中每个元素**不含** 6 个派生字段与 `mapRelationship`；验证测试通过
- [x] 4.2 新增断言：响应中**包含** `displayAttribute` 及其 `displayType` / `isRequired` / `matchType` / `validateText` / `displayNo`，证明单次请求即可渲染表单；验证测试通过
- [x] 4.3 **反向验证**：临时把 `upper` / `mapRelationship` 加回 VO，确认 4.1 的断言变红；恢复后变绿

## 5. 验证与归档

- [x] 5.1 执行 `./aicode/gradlew test`；验证全部测试通过（含 `ApiContractIT` 44 个可达性断言）
- [x] 5.2 执行 `openspec validate --strict display-attribute-vo-contract`；验证无校验错误
- [x] 5.3 同步 delta spec 到 `openspec/specs/api-data-object` 并执行 `openspec archive`；验证新能力已物化
- [ ] 5.4 在 `AGENTS.md` §11.4 或验收清单中登记「M4 时需 MODIFIED `api-contract` 的 Purpose（现写死 Angular 7 与静态前端）」；验证条目可被检索到

> **执行说明**
>
> - **1.1 影响面核实结论**：源码中**无任何生产调用方**（仅 `FrontendEndpointCatalog` 与 `ShtmlSuffixRewriteFilterTest` 引用路径字符串）。Angular 产物确实调用该端点（`attributes.fieldsList = "/displayAttribute/list"`），但**不读取任何将被移除的字段**：`checkDate`/`checkState`/`checkPk`/`checkDigit` 在产物中 **0 命中**；`upper` 的 2 次命中全部来自 ng-zorro slider 的 `this.bounds={lower:null,upper:null}`，与本实体无关；`mapRelationship` 的 10 次命中全部是 `URL.mapRelationship.*` 常量。
> - **1.2 递归对象树已证实**：`MapFieldColumn.mapRelationship` → `MapRelationship.associateClass`(`MapClassTable`) → `mapClassTableList`/`mapFieldColumnList`(`List<MapFieldColumn>`) → 回到起点。
> - **2.x 工作量远小于设计预期**：两个 VO 已存在且字段集合正确，仅需补 `displayAttribute` 一个字段。这也暴露了上一轮死代码清理的盲区（只扫了 `common/core/tools/`，未扫 `facade` 的 vo 目录）。
> - **4.3 反向验证**：把 `checkDate` / `upper` 塞回 VO → 两条断言立即变红（`[派生字段 checkDate 不应出现]` / 字段集合不匹配），恢复后变绿。断言确实在守护该属性。
> - **踩坑记录**：Spring Boot 4 用 Jackson 3（`tools.jackson`），`JsonNode.propertyNames()` 返回 `Collection<String>`（Jackson 2 只有返回 Iterator 的 `fieldNames()`）；`@AutoConfigureMockMvc` 的包路径已变，MockMvc 须按 `ApiContractIT` 的方式用 `MockMvcBuilders.webAppContextSetup(wac)` 手工构造；`map_class_table.code` 非空且不由 `MetaObjectHandler` 填充，测试种子须显式赋雪花值。

- [x] 5.5 同步 delta spec 到 `openspec/specs/api-data-object` 并归档；验证 8 个 spec 全量 `--strict` 通过
