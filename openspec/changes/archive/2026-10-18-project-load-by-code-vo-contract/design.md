# Design

## Context

仓库现状（实测，2026-10-16 M2-2 commit 之后）：

- `ProjectController.loadByCode(@PathVariable String code)`（详见 `aicode/src/main/java/com/aicode/project/ctrl/ProjectController.java:340`）当前返 `ProjectVO`（裸实体）而非 `R<ProjectVO>`，签名处带 `@Deprecated`。
- 签名无 `token` 入参 —— **不依赖显式 token 解析**，仅受全局 `LoginInterceptor` 拦截保证非匿名访问。
- 前端 `aicode-web/src/generated/api.ts:844` 的 `loadByCode(code)` 生成物返 `Promise<AxiosResponse<ProjectVO>>`（裸），对应 `LoadByCodeResult = AxiosResponse<ProjectVO>`（line 1403）。
- M0 阶段引入的 `api-contract` Requirement "统一响应包装的 data SHALL 携带真实结构"（line 156）已明确"所有 Controller 出参走 R<T>" —— 但 `loadByCode` 是该 Requirement 唯一的反向实例。
- `auth-account` Requirement "凭据传输方式的演进约束"（R5）已规定新接口禁止 `?token=` URL 参数 —— 而 `loadByCode` 因签名无 token 走过拦截器即可顺带满足。
- M0 已在 `ApiContractIT.java:107` 起维护 60+ 用例守护契约层，本变更将新增一个 `@Nested` Scenario 组钉死本变更的行为。

M2-2 之后：`ProjectPlaceholderPage` 占位详情页在前端路由表里等着真接入；M3-A 阶段（紧随本变更）会建立 `ProjectDetailPage`，消费 `loadByCode` 改造后的 `R<ProjectVO>` 形态。

## Goals / Non-Goals

**Goals:**

- `ProjectController.loadByCode` 改返 `R<ProjectVO>`；删除 `@Deprecated`；保留 `@PathVariable String code` 单一入参。
- 让前端 M3-A `ProjectDetailPage` 能用 `unwrapSuccess(resp)` 样板（M2-2 已落地的"任何出参都包 `R<T>`"规约）的同一份代码消费该端点。
- 让契约守护测试 (`ApiContractIT`) 反向钉死"任何受保护接口不可直返实体"。
- 让 orval 自动重新生成 `aicode-web/src/generated/` 下 `loadByCode` 的类型形态变为 `AxiosResponse<RProjectVO>`，`api.msw.ts` 中 `getLoadByCodeMockHandler` 的 override 形态变为 `R<ProjectVO>`。
- 让仓库 grep 全仓无 `loadByCode` 业务层调用（Angular 7.0.4 静态产物外）—— 排除既有调用方受 `R<T>` 包络引入 BREAKING 影响。

**Non-Goals:**

- 不实现"前端 React 详情页"。该工作留到 M3-A `2026-10-18-project-detail-page` 变更。
- 不扩接口签名到含 `token` query/header 入参 —— `LoginInterceptor` 已全局读取 token；`loadByCode` 也走拦截器显式保护，调用方不必显式塞 token。
- 不引入新注解或新依赖；本变更纯重构。
- 不动 `@Operation` 注解的 summary 文本（"创建Project"是历史错误 docstring，与 `loadByCode` 实际语义不符；但不属于本变更范围 —— 留作 future cleanup）。
- 不重命名方法或路径。

## Decisions

### D1：保留 `BeanUtils.copyProperties` 装配而非引入手工映射

**决定**：`loadByCode` 内仍使用 `BeanUtils.copyProperties(project, projectVO)` 装配 `ProjectVO`；不引入手工逐字段 `setXxx`。

**理由**：

- `loadByCode` 是**单条数据加载**而非列表映射，22 字段的逐字段 set 不会带来 `display-attribute-vo-contract` 那种"业务语义不可控"风险；
- 现有 `BeanUtils.copyProperties` 装配已经被当前生产代码使用，不引入新错误面；
- 与 M2-1 引入的"列表 VO 化"采用同一种 `BeanUtils.copyProperties` 风格保持一致（M2-1 是有手工显式的特例，见 `2026-10-17-project-list-vo-contract` design §D3）。

**备选方案**：(a) 手工逐字段 `setXxx` —— 与 M2-1 形成不一致；(b) `MapStruct` —— 仓库无此依赖；(c) 沿用 `BeanUtils` —— 本变更采用。

### D2：早返回 null 改成 `R.success(null)`

**决定**：`code == null` 早返回路径，由 `return null` 改为 `return R.success(null)`（即 `R<ProjectVO>` 实例，`data` 字段为 `null`）。

**理由**：

- `R<T>` 包装后的接口 **任何返回都不能是 `null`**（否则调用方 `await resp.json()` 拿到 `null` 后访问 `.data` 抛 `TypeError`）；
- `R.success(null)` 与现有契约层"data 为空时 SHALL 以 null 呈现，SHALL NOT 省略该键"（`api-contract` line 82）一致 —— 即响应里仍含 `"data": null` 字段；
- 前端 `unwrapSuccess` 看到 `data === undefined` 即抛 `ContractError`，含 `code`/`info` 不带 wrapper code —— 但实际上 `R.success(null)` 的 data 是 `null`（不是 `undefined`），前端拿到 `null` 时仍然能进入 `setState({kind:'ready', rows: null})`；为防御起见，调用方应 `data ?? []`/`data ?? {}` 处理，但本变更的契约责任止步于 `data: null`。

**备选方案**：(a) 抛 `BaseException` 走 `R.failed(...)` —— 但 `loadByCode` 历史上对 null code 是"软返回"，改成硬失败会让前后端行为漂移；(b) 直接返 null —— 破坏 R 包络规约；(c) 返 `R.success(null)` —— 本变更采用。

### D3：`ApiContractIT` 新增 Scenario 组而非修改既有 Scenario

**决定**：在 `ApiContractIT.java` 内新增一个 `@Nested class LoadByCodeReturnsWrappedVO`，覆盖三个用例：

1. `loadByCode_returns_R_wrapped_ProjectVO`：正常路径下断言 `data.code === 'contract-demo'` 等；
2. `loadByCode_when_code_404_returns_R_with_data_null`：（受种子空表影响）—— 暂不强求 404；可断言 `code !== "0000"` 或 `data` 不含 `code` 字段；
3. `loadByCode_regression_when_controller_bypasses_R_package`：反向验证 —— 临时把 Controller 改回直返 ProjectVO，本测试 SHALL 失败。

而非修改既有 ApiContractIT 任何一个原有用例。

**理由**：

- 既有 60+ 用例的功能边界已清晰（标识符序列化、统一响应结构、`.shtml` 兼容性等），不应与 `loadByCode` 行为混在一起；
- 新 `@Nested` 是 spec 5 个 Requirement 中"场景⑧"在测试中的镜像；
- 反向验证要可被独立运行，单独 `@Nested` 比内嵌更可读。

**备选方案**：(a) 改既有某个测试 —— 污染既有验证目标；(b) 新文件 `LoadByCodeIT.java` —— 同行级独立测试，但 `ApiContractIT` 已有同质 MockMvc + Redis 上下文，新 `@Nested` 重复配置低。

### D4：orval 生成物自动刷新，无需前端手改

**决定**：`OpenApiExportIT`（既有契约导出集成测试）会自动让 `contracts/openapi/openapi.yaml` 中 `/project/load/code/{code}` 响应的 schema 变化；前端无需手动改 `aicode-web/src/generated/api.ts` 内 `loadByCode` 的代码 —— `npm run generate` 会重生成 `api.ts` 与 `api.msw.ts`。

**理由**：

- 与 M0/M1/M2-1/M2-2 已建立的"契约变化 → 生成物变化"链路完全一致；
- 但**前端 React 代码不消费 `loadByCode`（当前只有 `ProjectPlaceholderPage` 占位）**，所以**前端 React 改动一并由 M3-A 变更承担**，本变更在生成物角度只是"重生成 + 复核无破坏"。

**备选方案**：(a) 手动 patch `api.ts` —— 工作量高、违反入库约定；(b) 重新跑 `npm run generate` —— 本变更采用。

### D5：仓库 grep 反向验证无业务层 `loadByCode` 调用

**决定**：实施 task 1.4 中加一步 `git grep -n loadByCode aicode/src/main/java` + 仓库 main + facade 全量扫描，确认无业务层独立调用该端点。

**理由**：

- 实施 M2-1 时 `ProjectController.list` 改造引入了 BREAKING 的字段名迁移，未发现业务层消费 —— 但 `loadByCode` 与 `list` 不同：`loadByCode` 是单条数据加载（非列表），改造粒度更细（从裸 ProjectVO 到 R<ProjectVO> 包络）；
- Angular 7.0.4 静态产物里必然有"调用 `loadByCode` 取 `result.name` 这种直接字段"的代码 —— 但 M4 才切运行时，本变更与之无冲突。

**备选方案**：(a) 跳过 grep —— 与现有 `AGENTS.md §11` "简化原则"冲突；(b) 跑 grep 但不视为 BLOCKER —— 本变更采用，记 grep 结果写入 commit msg。

## Risks / Trade-offs

**[BREAKING 引入：访问 `result.name` 的旧调用方需要改 `result.data.name`]** → 当前仅 Angular 7.0.4 静态产物在用此模式；M4 时统一迁移。新增 ApiContractIT Scenario 钉这一 BREAKING 让回归立即可见。

**[orval 重生成可能影响其他无关消费方]** → `R<ProjectVO>` 是新引入 schema，与现有 `RString`/`RVoid` 等不冲突；orval 默认会把新 schema 加进 `components.schemas` 块。验证：`/project/load/code/{code}` 的 schema 在 `components.schemas.RProjectVO` 下。

**[`@Operation(summary = "创建Project")` 与方法语义不符]** → 这是历史 docstring 错误，本变更不动 —— 避免扩大 scope。Future cleanup 项已记入 `Non-Goals`。

**[反向验证（决策 D3 Scenario 3）临时改 Controller 改回 `return projectVO`]** → 该反向验证与 `loadByCode_regression_when_controller_bypasses_R_package` 用例配对，通过临时 patch 验证后立即还原 —— 由本变更 task 1.3 钉死。

## Migration Plan

单批交付（一个 feature commit）：

1. **改后端**：`ProjectController.loadByCode` 改签名为 `R<ProjectVO>`，删除 `@Deprecated`，调整 null 早返回路径；
2. **跑 gradle test**：176 用例（含本变更新增 3 个） → exit=0；
3. **跑 OpenApiExportIT**：让 `contracts/openapi/openapi.yaml` 刷新；
4. **前端 generate**：`cd aicode-web && npm run generate` 重生 `api.ts` 与 `api.msw.ts`；
5. **复核前端 typecheck 与 vitest**：43 用例仍全绿；
6. **同步 delta spec 到 `openspec/specs/api-contract`** 并 archive 该变更。

**回滚**：单 commit revert 即恢复 `loadByCode` 直返 ProjectVO；前端生成物会被前端后续 M3-A 变更一并校验。

## Open Questions

- 是否在 spec 5 个 Scenario 中纳入 `loadByCode` 当返回 code 不存在时（数据库无对应记录）的契约层语义？目前 `loadByCode` 实现里 `service.getOne(...)` 返 `null`，再 `copyProperties` 到 vo 时 NPE —— 这是既有 bug，可能要顺手修。但属于"discovery → archive → cleanup"议题，**本变更不动**。M3-A 接入详情页时如果发现 NPE，可单独开小变更修。
