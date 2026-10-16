# Tasks

## 1. R 泛型化

- [x] 1.1 `R` 改为 `R<T>`，`data` 字段类型为 `T`，7 个静态工厂全部加 `<T>` 类型参数；验证编译通过
- [x] 1.2 若 Lombok `@Data` 在泛型类上产生问题，收窄为 `@Getter`/`@Setter`；验证编译通过

## 2. Controller 签名收敛（编译器驱动）

- [x] 2.1 `ProjectController` 的 7 个方法；验证编译通过
- [x] 2.2 `FrameworksController` 的 5 个方法；验证编译通过
- [x] 2.3 `MapRelationshipController` 的 5 个方法；验证编译通过
- [x] 2.4 `ProjectRepositoryAccountController` 的 3 个方法；验证编译通过
- [x] 2.5 `ProjectSqlController` 的 3 个方法；验证编译通过
- [x] 2.6 `ProjectFramworkController` / `ProjectJobController` / `ProjectModuleController` 各 2 个方法；验证编译通过
- [x] 2.7 `AccountController` 的 2 个方法 + `LoginCtrl` 的 2 个方法；验证编译通过
- [x] 2.8 `DisplayAttributeController#list` 设为 `R<List<MapFieldColumnVO>>`；验证编译通过
- [x] 2.10 4 个 `list` 方法的 `R<PageVO>` 修正为 `R<com.aicode.core.Page<XxxPageVO>>`（原判定有误：`pageVO` 变量实为 `core.Page`，而 `core.PageVO` 是另一个未被使用的类）
- [x] 2.9 `LogsCtrl` / `LogsSVImpl` 等横切点；验证编译通过

## 3. 横切点

- [x] 3.1 `ExceptionHandle` 的返回类型按 D5 处理；验证编译通过
- [x] 3.2 `LoginInterceptor` 等其他返回 `R` 的位置；验证编译通过
- [x] 3.3 全仓检索确认无遗漏的 `public R ` 裸返回；验证为 0

## 4. 契约验证

- [x] 4.1 执行 `./aicode/gradlew test`；验证 222 个用例全绿（运行期结构不变）
- [x] 4.2 重跑 `OpenApiExportIT`，检查契约中 `R.data` 已引用具体 schema；验证不再是 `{}`
- [x] 4.3 抽查 `/displayAttribute/list` 的响应定义，确认 `data` 指向 `MapFieldColumnVO` 的数组；验证 VO 的 13 个字段出现在 `components.schemas` 中

## 5. 规范同步与归档

- [x] 5.1 同步 `AGENTS.md §6.6`，补充「统一响应 SHALL 以泛型返回类型声明出参形状」；验证条文与代码一致
- [x] 5.2 执行 `openspec validate --strict`，同步 delta 到 `openspec/specs/api-contract` 并归档；验证新要求已物化
- [x] 5.3 重跑 `OpenApiExportIT` 并提交更新后的 `contracts/openapi/openapi.yaml`；验证该变更被包含在提交中

> **执行说明**
>
> - **38 个方法全部改完，编译 0 错误，运行期结构未变**（223 用例 0 失败）。
> - **契约效果已验证**：`$ref: R` 从 **45 处降为 0**，springdoc 改为按端点生成内联 schema；`components.schemas` 从 8 个增至 **57 个**，含 `RListMapFieldColumnVO` / `RProject` / `RString` / `RVoid` / `RObject` / `RPageProjectModulePageVO` 等。其 `data` 正确指向具体类型 —— 例如 `/displayAttribute/list` 的 `data` 为 `array of MapFieldColumnVO`，且 VO 字段的 description 来自其 `@Schema`。
> - **一处判定修正**：4 个 `list` 方法最初写成 `R<PageVO>`，但 `PageVO` 是未被使用的另一个类，且未导入；`pageVO` 变量的真实类型是 `com.aicode.core.Page<XxxPageVO>`，已改正。
> - **设计 D3 的例外已按规则处理**：`ProjectController#scanPath` 与 `LogsSVImpl#scanPath` 各有 3 种返回形态（`List<Map<String,String>>` / `String` / `String`），按规则显式写成 `R<Object>`，并在契约中体现为 `RObject`（该端点契约无类型，是已知且被接受的例外）。
> - **编译器不是本次的驱动力**：Java 允许裸 `R`，泛型化后编译即通过，38 处签名必须主动逐个修改。这一点与 design D1 的预期（「编译器是主要防线」）不符 —— 实际防线是本任务的逐方法核查与契约产出验证。
