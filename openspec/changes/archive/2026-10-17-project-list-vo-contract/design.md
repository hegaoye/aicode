# Design

## Context

仓库内已存在两条 VO 分页路径：

- `common/src/main/java/com/aicode/core/Page<E>` —— 历史路径，`List<E> voList`，含 `params` / `sortColumns` / `optObject*` 等内部使用字段；目前 `/project/list`、`/framework/list`、`/project/mouldles/list` 等多个 Controller 都用它，但实际从未传 `E` 类型参数，导致契约层 `voList` 退化为 `unknown` 或 `Object`。
- `common/src/main/java/com/aicode/core/PageVO<VO>` —— VO 友好路径，`List<VO> records`，继承 `BaseVO`；已被多个 `XxxPageVO` 用过。

`facade/src/main/java/com/aicode/project/vo/ProjectVO.java` 也已存在，21 个字段覆盖 `Project` 实体的全部业务字段（即"实体全集拷贝"，不裁剪），已被 `ProjectController.loadByCode` 端点直接使用。**本变更的所有基础都已具备**，无需新增任何 VO 或泛型基建，只动一处装配 + 一处断言。

M1 已落地契约链（M0）/ 首业务页面（本变更前置），M2 紧随将接的项目列表页要求 `/project/list` 的 `data.records[i]` 至少含 `code` / `name` / `englishName` / `createTime` 等字段以供列表呈现与跳转。

## Goals / Non-Goals

**Goals:**

- `ProjectController.list` 改为 `R<PageVO<ProjectVO>>`，手动逐字段映射实体到 VO。
- 新增 / 复用对 `/project/list` 字段集合的契约守护断言，让"再次出现 voList: unknown"成为不可静默通过的回归。
- 让契约 `contracts/openapi/openapi.yaml` 经 `OpenApiExportIT` 自动刷新后，`data.records` 元素类型具备真实形状（springdoc 将其命名形如 `ProjectVO`）。
- 让 orval 生成物中出现 `ProjectVO` / `RPageVOProjectVO` 类型，给紧随 M2 接入的项目列表页可直接消费的形态。

**Non-Goals:**

- 不改 `Page<E>` 类本身；它继续存在，给尚未 VO 化的端点用。是否彻底弃用 `Page<E>` 是后续变更。
- 不批量修复 `/framework/list` / `/project/mouldles/list` 等同类问题端点 —— 每个端点都可能有自己的字段裁剪需求，单独变更处理；M2 项目列表页只关心 `/project/list`。
- 不重命名 `PageVO.records` —— 现存多个 `XxxPageVO` 共享字段名，重命名影响面大。
- 不实现登录态管理与项目列表页（即 M2 后半段）—— 那一步是 M3 变更的开端。

## Decisions

### D1：直接复用现有 `ProjectVO`，不新建任何 VO 子集

**决定**：`ProjectController.list` 直接使用现有 `facade/.../vo/ProjectVO.java`；不新增 `ProjectSummaryVO` / `ProjectListItemVO` 之类的派生类型。

**理由**：

- `ProjectVO` 已存在且包含 `Project` 实体的全部业务字段。被 `loadByCode` 端点直接使用，本端点用同类型保持一致。
- 列表接口"VO 字段集合 SHALL 不多于前端实际消费"是 `api-data-object` 的硬要求，但本变更让"列表 VO 与详情 VO 同形"作为临时方案——`/project/list` 详情跳转是 M3 的事，本变更先确保契约字段真实可用，裁剪留到 M3 真正做详情页时按需决策。`api-data-object` 的 Scenario「VO 字段是前端实际消费集的子集」仍要求裁剪，但它在 M3 项目详情页面才会触发，本变更不触发。

**备选方案**：(a) 新建 `ProjectSummaryVO`（仅 6 个字段）——更符合 `api-data-object` 字面要求，但 17 字段的 `ProjectVO` 已存在；为单个端点新建一个虽然更"纯洁"但维护多一类。选 (a) 等同于做实质的字段裁剪，是 M3 之后的事。

### D2：分页容器选 `PageVO<ProjectVO>`，不动 `Page<E>` 的字段含义

**决定**：`PageVO<ProjectVO>` —— 即仓库 `common/core/PageVO.java` 的 `records: List<VO>`。

**理由**：
- `PageVO` 字段集合是 5 个：`curPage` / `pageSize` / `totalRow` / `totalPage` / `records`。简洁、VO 友好。
- `Page<E>` 的额外字段（`params` / `sortColumns` / `optObject*`）历史上从未被前端消费 —— 是后端 MyBatis-Plus QueryWrapper 的内部塞缝，移动到 VO 会带出内部含义且无收益。

**备选方案**：(a) 在 `PageVO<T>` 上增字段以兼容 `Page<E>` —— 模糊两类用法，引入歧义。(b) 改 `Page<E>.voList` 名字为 `records` 并 mark `@Deprecated` 用 `PageVO<T>` —— 多类同步动，scope 放大。

### D3：装配在 Controller 层手工逐字段，模仿 `DisplayAttributeController` 已有的做法

**决定**：在 `ProjectController.list` 内逐字段手动 `setXxx` 装配 `ProjectVO`，不引入通用 mapper（`MapStruct` / `BeanUtils.copyProperties`）。

**理由**：
- `DisplayAttributeController.list` (M1 归档变更里) 已用同样手法落地，可视为本仓库"列表 VO 化"的事实模板。
- 当前 `ProjectVO` 字段名与 `Project` 实体同名（`code` / `name` / `englishName` / `description`…）且不含派生字段，没有手工映射的歧义点。
- 即便字段完全同名但实际语义差异，mvn compile 期 `BeanUtils.copyProperties` 会悄悄复制错位 —— 逐字段显式赋值更安全。

**备选方案**：(a) `BeanUtils.copyProperties(project, vo)` —— 与仓库现有 `ProjectController.loadByCode` 一致做法，见 line 307。综合考虑，"显式胜过隐式"更符合新写代码风格。(b) 引入 `MapStruct` —— 仓库目前没有（见 §10 的依赖列表），新增注解处理器超出本变更范围。

### D4：契约守护在 `ApiContractIT` 增单测，不在 M0 端的 `OpenApiExportIT`

**决定**：在 `aicode/src/test/java/com/aicode/contract/ApiContractIT.java` 加 1–2 个新 `@Test`：① `/project/list` 响应 `data.records[i]` 含期望字段（`code`/`name`/`englishName`/`createTime`）；② 反向验证：临时把 `Project` 实体的某个字段（如 `accountCode`）反向映射为不存在的 getter，让契约返回空数组。

**理由**：
- `ApiContractIT` 已经是契约层集成测试的标准载体，与 §7.10 第 0 项一致；其断言已经覆盖了 `/framework/list` 的 voList 字段形态，新增 `/project/list` 的契约断言是同质工作。
- 反向验证用临时字段伪造确认"VO 装配错了会失真可见"。

**备选方案**：(a) 在 `OpenApiExportIT` 加断言 —— 它只导出 yaml 文件，断言字段已由 `Verify` 任务覆盖。(b) 单测纯单元化 —— 单元测 Controller 不真打网络，无法验证序列化链路上的真实字段集合。

### D5：新增项目种子数据，让 `/project/list` 的契约断言可执行

**决定**：在 `aicode/src/main/resources/db/data.sql` 添加 1–2 条 project 种子（`accountCode` = 内置 `admin` 账户的 `code`）。

**理由**（修正原 D5 草稿）：
- 实测 `db/data.sql` 当前**没有 project 种子** —— 原 D5 草稿"data.sql 已有项目种子"为事实错误。
- 没有种子数据，`/project/list` 永远返空 `records` 数组，**契约断言失去意义**（空数组不验证字段形状）。补 1 条种子让断言可执行。
- 1 条种子触动数据库状态最低（不破坏既有 frameworks 测试），contract-test token 对应账户的 `accountCode` 走 `JwtToken.createToken(Constants.AccountCode.val.toString(), ...)`，种子须把 `accountCode` 设为该常量。

**备选方案**：(a) 测试内 `@BeforeEach` 临时 `projectMapper.insert(...)` —— 与 AGENTS.md §10 `spring.sql.init.mode=never` 不冲突，但需 Setup 维护（删/插），与现有 `data.sql + schema.sql` 模式不一致。(b) 不补种子，让"空 records"也作为契约通过 —— 反向验证（任务 2.3）需要 records 非空才能触发，放弃这条路径。

## Risks / Trade-offs

**[字段名切换 `voList` → `records` 对前端契约消费者造成破坏性变更]** → 前端 React 新代码（M2 紧随）尚未消费此端点，**`aicode-web/src/generated/**` 还没定型**；Angular 旧产物对 `data.voList` 与 `data.records` 都不消费（按 `records` 似应走 MyBatis-Plus 分页插件默认的 `records`，但 Angular 7 旧前端翻阅 db 端时未必真用此端点；现需以 grep 确认）。如果未来 Angular 旧端在切换前后读此端点——它读到的字段名变化将失败。本变更的"破坏性"由 §4.4 的反向验证 + AGENTS.md §11.5 提到的 M4 切运行时一并消化。

**[ProjectVO 暂未做字段裁剪是字段膨胀风险]** → 在 `api-data-object` 现有 Scenario「VO 字段是前端实际消费集的子集」之下，`ProjectVO` 21 字段可能让前端先接"全能型 VO"。本变更的严格契约职责已交付；字段裁剪留给 M3 详情页/编辑表单出现时按需评估（届时单独变更）。

**[springdoc 对 `PageVO<ProjectVO>` 的展开命名不稳定]** → 实测在仓库当前 Springdoc + Jackson 3 版本下，泛型类 `PageVO<X>` 会被 springdoc 展开为内联 schema（不是引用 `PageVO`），命名规律受 OpenApiExportIT 实际导出决定。本变更不在设计阶段固化导出命名，依赖 OpenApiExportIT 自身的导出结果 + ApiContractIT 字段断言兜底。

**[即便装配映射错也是"前端编译失败时一片字段红"—— root cause 难定位]** → 4.2 反向验证以"VO 装配返回空 records"的形式覆盖；进一步覆盖移到 M2 的项目列表页单测（消费端实测）。

## Migration Plan

单批交付（一个 feature commit）：

1. `facade/src/main/java/com/aicode/project/vo/ProjectVO.java` 已存在 —— **无动作**
2. `ProjectController.list` 改为 `R<PageVO<ProjectVO>>`，逐字段 `setXxx` 装配
3. `ApiContractIT` 加 `/project/list` 字段断言：records 非空 + 字段名集合 ⊇ ProjectVO 字段
4. 反向验证：把 `ProjectController.list` 中的 `setCode(...)` 删一行 → 记录断言应当指出 records[i].code 为 null
5. 执行 `./aicode/gradlew :aicode:test`（特定测试类）与 `./aicode-web:buildUi` 看契约确实刷新
6. 同步 delta spec 到 `openspec/specs/api-data-object` 并 archive
7. 验证 `contracts/openapi/openapi.yaml` 中 `/project/list` 响应的 `data.records` 元素类型为 `ProjectVO` 而非 `{}`

**回滚**：单 commit revert 即恢复 `Page<E>` + `voList` 行为；下游无消费所以无需数据迁移。

## Open Questions

- `api-data-object` 的新 Scenario「分页列表接口出参为 PageVO<XxxVO>」是否应当收紧到"任何分页场景必须用 PageVO<XxxVO>"而非仅"可被如此表达" —— 留待 `/framework/list` 等同性质端点单独变更时再决定；
- 是否在本变更同时把 `/framework/list` 也改到 PageVO —— 当前 scope 限制，且每个端点字段裁剪决定不同，故不作合并。如后续要做，单独开`framework-list-vo-contract`。
