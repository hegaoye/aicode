## Context

`aicode/README.md §四` 列出了约 30 个标记 `@Deprecated` 的端点，覆盖 11 个 controller 中的 9 个（`Project` / `ProjectJob` / `ProjectFramwork` / `ProjectSql` / `ProjectRepositoryAccount` / `MapRelationship` / `DisplayAttribute` / `Frameworks` / `Account` / `Logs` / `Index`），均已声明"前端未调用"。`audit-2026-06-22.md §4.3` 明确指出"建议二次扫描后再统一清理"但"未实际删除（只加 `@Deprecated` 注解），路由膨胀、误用风险持续"。

P4-4 之前取消（cancelled）的同类清理已成功完成 4 套整个 controller 端点（`ModuleFile` / `ProjectModel` / `ProjectModelClass` / `ProjectCodeCatalog`），本 change 是它的延续——本次只清"方法级"（保留 controller 类，删 @Deprecated 单端点）。

## Goals / Non-Goals

**Goals:**

- 删除 `aicode/README.md §四` 列出的全部 ~30 个 `@Deprecated` 端点（方法 + 对应 facade 接口方法 + Service 方法）
- 跑全 build + 全测试，74 个用例（含 45 个 aicode 测试）必须仍全过
- 更新 `aicode/README.md §四`：从"未使用的后端API"改为"已删除（YYYY-MM-DD）"
- 新增 `api-deprecation-policy` 能力定义：标记 → 30 天观望 → 二次确认 → 删除 的流程化处理

**Non-Goals:**

- 不删任何 controller 类（只删方法）
- 不删任何业务端点（仅 @Deprecated 端点）
- 不重写 `aicode/README.md` 其它章节
- 不做外部脚本的兼容性回退（不维护 30+ 旧端点别名）
- 不删除 5 个 spec 已覆盖的能力（`auth-account` / `project` / `sql-parse` / `mapping-display` / `frameworks-template` / `codegen` / `settings-repository` / `code-gen-pipeline` / `sql-reverse-parse` / `map-relationship-crud` / `project-init-endpoint`）的端点

## Decisions

### Decision 1: 删除方法本身，保留 controller 类

**理由**：保留 `ProjectController` / `ProjectJobController` 等类，未来如需新端点可在原 controller 扩展。如果连类也删了，会破坏 controller 间的"语义聚类"惯例（同类业务端点应在同一 controller）。

### Decision 2: 同时删 facade 接口方法和 ServiceImpl 方法

**理由**：facade 接口和 `*ServiceImpl implements` 必须同步——否则编译失败。`aicode/.../XxxServiceImpl` 是 facade 接口的实现，删方法必须连 facade 接口的方法一起删。

### Decision 3: 用静态扫描 + 二次确认引用作为删除前置条件

**流程**：
1. `grep -rn "<方法名>" aicode/src facade/src` 检查零引用
2. `grep -rn "<方法名>" openspec/` 确认 spec 未引用
3. `grep -rn "<方法名>" common/src` 检查 common 模块未引用
4. `aicode/README.md §四` 二次声明"前端未调用"作为外部引用佐证

**替代方案**：写一个 `ApiUsageScanner` 工具在 CI 中检查——但工作量较大（需要解析 Spring 注解 + AST），本 change 不引入。

### Decision 4: 测试代码同步更新

**理由**：`@MockBean` 引用的 service 方法若被删，测试编译失败。`MapRelationshipControllerTest` 在前面 change 中已从 `@WebMvcTest` 改为 `@SpringBootTest + 直接注入 controller`（绕过 MockMvc enum 绑定），减少影响面。

### Decision 5: 一次性大批量删除，不分批

**理由**：每个端点删除都涉及 controller + facade + service 三处，分批会让仓库长期处于"半完成"状态、容易引入合并冲突。一次提交完整删除更清晰。

## Risks / Trade-offs

- [外部脚本 404] → 不维护旧别名；`aicode/README.md` §四 升级为"已删除"声明，外部用户能立即看到
- [facade 接口方法签名被删除，外部依赖消费者受影响] → 当前无外部消费者（aicode 单体应用，facade 内部使用）；如未来出现 consumer 需重新加回
- [ServiceImpl 删除方法时漏删 facade 方法导致编译失败] → 用 `gradle compileJava` 在每批删除后立即验证
- [删错方法（如 facade 接口被其他 Service 实现）] → 静态扫描 `implements XxxService` + `extends ServiceImpl`；如有别处实现该接口，需同步
- [测试代码大量调整] → 先用 `grep` 列出所有引用，再批量改

## Migration Plan

**执行顺序**（每个 controller 一组）：

1. **AccountController**：`/load/code/{code}` / `/modify/password` + facade `loadByCode` / `modifyPassword`
2. **ProjectController**：`/load/code/{code}` + facade `loadByCode`
3. **ProjectJobController**：`/load` / `/build` / `/list` / `/modify` / `/delete`（5 个）+ facade 同名方法
4. **ProjectSqlController**：`/load/projectCode/{projectCode}` / `/list` / `/delete` + facade `loadByProjectCode`（3 个）
5. **ProjectRepositoryAccountController**：`/load/code/{code}` / `/list` + facade `loadByCode`（2 个）
6. **ProjectFramworkController**：`/build` / `/modify` / `/delete` + facade 同名方法（3 个）
7. **MapRelationshipController**：`/load/code/{code}` / `/listByClassTableCode` + facade `loadByCode` / `listByProjectCode`（2 个）
8. **DisplayAttributeController**：`/load/mapFieldColumnCode/{...}` / `/modify` / `/delete` + facade `loadByMapFieldColumnCode` / `modify`（3 个）
9. **FrameworksController**：`/load/code/{code}` + facade `loadByCode`（1 个）
10. **LogsCtrl**：`/createLogFiles` / `/saveLogs` / `/loadFilePath` + facade 方法（3 个）
11. **IndexCtrl**：整个 controller（`@RequestMapping("/")` + `setViewName("/index")` 是基础设施，不删）—— **保留**（虽是 @Deprecated 但其路由 `/` 是 Spring 启动关键）
12. **测试代码同步**：每组删除后 `gradle compileTestJava` 验证

每组完成后：
- 跑 `gradle compileJava + gradle compileTestJava`（快）
- 一组完整（11 个 controller）后跑 `gradle test`（慢但完整）

**回滚策略**：Git revert 单 commit 即可（一次性 commit 但清晰标记）。

## Open Questions

- Q1: `IndexCtrl` 的 `setViewName("/index")` 是否真的不能删？—— 看起来是 Spring 启动关键，**保守保留**。如有更深排查可后续 PR 处理。
- Q2: 删 facade 接口方法是否破坏 `*ServiceImpl` 之外的实现？—— `aicode` 仓库内 21 个 `*ServiceImpl` 是唯一实现，无外部依赖。
