# Design

## Context

`api-data-object` 主能力 4 个 Requirements 已覆盖"VO 而非实体 + 分页用 PageVO + 字段裁剪意图"，但**没有任何 VO 字段清单的具体化**。M3-A-2（commit `5971b59`）落地的 `ProjectDetailPage` 直接展示 22 字段全集合，是这条 Rule 的**违反实例**：后端 `ProjectVO` 字段全集拷贝 = 列表 + 详情共享，但详情页其实只需要 12 字段，列表页只需要 7 字段。

本变更把"VO 字段与前端消费场景对应"的具体清单落到 spec 主能力，并据此裁切前后端 VO 与生成物。

## Goals / Non-Goals

**Goals:**

- 新增 `ProjectSummaryVO`（7 字段）+ `ProjectDetailVO`（12 字段）；
- 删除原全字段 `ProjectVO`（22 字段）实体全集拷贝形态；
- `ProjectController.list` 返 `R<PageVO<ProjectSummaryVO>>`；`ProjectController.loadByCode` 返 `R<ProjectDetailVO>`；
- 前后端在 orval 重生 + 测试断言同步切换到新 VO；
- 把"清单→VO 字段集合"两层对应关系钉到 `api-data-object` 主能力。

**Non-Goals:**

- 不动后端 `Project` 实体（M3-C 是 VO 投影调整，与实体持久化无关）
- 不动其他领域 VO（Account / DisplayAttribute 等）
- 不为 `ProjectSummaryVO` / `ProjectDetailVO` 引入复杂 mapper 库（沿用 M2-1/M3-A-1 的 `toSummary` / `toDetail` 手工 setXxx 风格）
- 不接前端 VTH 接入调整、后端 ws 调整

## Decisions

### D1：拆 `toProjectVO` 为 `toSummary` + `toDetail` 两个方法

**决定**：将现在 `ProjectController.toProjectVO(Project)` 拆为 `toSummary(Project)` 与 `toDetail(Project)` 两个方法，分别装配两 VO。

**理由**：

- 与 M2-1 / M3-A-1 的 setXxx 手工映射风格一致（避免引入 MapStruct 等新依赖）
- 两方法并行存在让代码意图更清晰：call site 一眼看出"哪类页面消费"
- 单测可独立 mock 任一方法（测试时也可控）

**备选方案**：(a) 用 `BeanUtils.copyProperties` 按字段名挑选 + 反射黑名单 —— 实现复杂且脆弱；(b) 用 MapStruct 注解式映射 —— 仓库未引入该依赖；(c) 两个手工映射方法 —— 本变更采用。

### D2：`ProjectSummaryVO` 字段全小写命名 + 沿用 `MapFieldColumn` 已落地的 `englishName` 等 camelCase

**决定**：VO 字段名直接等于契约 schema 名（snake_case 由 orval 渲染为 camelCase）。`englishName` / `buildNumber` / `isRepository` 等字段名与 `MapFieldColumn` 已落地形态对齐。

**理由**：

- springdoc 把 `englishName` 序列化为 `englishName` (camelCase)，与前端 `MapFieldColumnVO.englishName` 一致
- 沿用现有 VO 命名习惯，避免再次引入项目专有简称风格

### D3：详查 before 删 `ProjectVO` —— 业务层零消费后删除

**决定**：实施 task 1.5 grep 全仓确认无业务层引用 `ProjectVO`，再删除。任务 grep 结果在 commit msg 留痕。

**理由**：

- `ProjectVO` 是 facade 公开 VO，可能被任何外部模块引用——git grep 钉死这一前提
- M2-1 已"顺带删旧 VO" 但未删除 `ProjectVO`，因当时列表端点已用 `ProjectVO` 装配；
- 现在 `ProjectController` 不再"返 `ProjectVO`"，把 VO 删除是自然的

### D4：测试同步改造后**保留**原 ProjectDetailPage 数据全展示 + 补字段裁切断言

**决定**：测试 `ProjectDetailPage` 渲染后除验证字段 testid 存在之外，还需断言"不存在的字段不存在"——即 `databaseType` / `language` / `state` 等不在渲染中（前端不消费）。

**理由**：

- 与 api-data-object 主能力"反向验证" Rule 一致（Rule 4 Scenario 4 自己有反向测试思路）
- 防止后续回归把 `ProjectDetailVO` 改回 22 字段时单测不报警

### D5：ApiContractIT 场景⑧ 反向验证模板复用 `ProjectDetailVO.description` 字段强等值

**决定**：ApiContractIT `LoadByCodeReturnsWrappedVO.loadByCode_returns_R_wrapped_ProjectVO` 用例 `data.description === '契约回归测试种子项目'` 强等值（沿用 M3-A-1 已有的 `data.code === 种子编码` 模式），让反向验证（Controller 退回直返实体）触发"description 字段缺失"路径。

**理由**：

- M3-A-1 反向验证（Controller 直返实体 → 2 用例全红）该 harness 已装备；本变更只需把断言的字段名从 `code` 换成 `description`
- `description` 是 `ProjectDetailVO` 独有字段（不在 `ProjectSummaryVO`），更能区分两类 VO

## Risks / Trade-offs

**[删除旧 `ProjectVO` 引起 break]** → 必须 grep 全仓。已知引用方：`ProjectController`、`ApiContractIT`、`ProjectControllerDownloadTest`（反射断言方法签名而非 import — 它仍引用了 Method 反射路径，不会随 VO 删除编译失败）。本变更 task 1.5 grep 同时捕获反思式 import。

**[前后端测试 fixture 重复改造]** → M2-2 ProjectListPage 测试 + M3-A-2 ProjectDetailPage 测试都会改字段断言。设计接受 —— 5 用例的字段断言改写而不是删除，新增"反向验证断言不存在字段"。

**[springdoc 展开后的命名]** → `ProjectSummaryVO` / `ProjectDetailVO` 经 springdoc 默认展开命名未必含 `${VO}` 后缀？看 M2-1 实测 `RPageVOProjectVO` —— 列表项 VO 用 `PageVOProjectVO` 命名。所以本次应该是 `RProjectSummaryVO` 与 `RProjectDetailVO`（orval 也会按此生成 `rProjectSummaryVO.ts`）。Tasks 里靠 springdoc 重导后看实际名字。

## Migration Plan

单批交付（一个 feature commit）：

1. 新增 `ProjectSummaryVO` (7 字段) + `ProjectDetailVO` (12 字段)；
2. 修改 `ProjectController`：
   - `list` 装配改 `PageVO<ProjectSummaryVO>` + `toSummary`
   - `loadByCode` 装配改 `R<ProjectDetailVO>` + `toDetail`
   - 删除原 `toProjectVO` 与 `PageVO<ProjectVO>` 装配代码
3. 删除 `ProjectVO.java`（grep 后）；
4. `OpenApiExportIT` 重导 `contracts/openapi/openapi.yaml`；
5. `npm run generate` 重生前端生成物，验证类型名是 `RProjectSummaryVO` 与 `RProjectDetailVO`；
6. 前端：`ProjectListPage` 改用 `ProjectSummaryVO`；`ProjectDetailPage` 改用 `ProjectDetailVO`；
7. 测试改造：ProjectListPage 测试用 `ProjectSummaryVO` 字段断言；ProjectDetailPage 测试用 `ProjectDetailVO` 字段断言；ApiContractIT 场景⑧ 反向验证机制不变，更新字段为 `description`；
8. 跑全套 `npm run typecheck && npm test` 与 `./aicode/gradlew :aicode:test`；
9. 同步 delta spec → archive。

**回滚**：单 commit revert 即恢复。

## Open Questions

- 是否在 `ProjectDetailVO` 也包含 `buildUrl`（已构建 zip 包下载地址）—— 实际列表页不展示下载，但详情页"构建完毕"后下载常用。决定：包含（detail 当前 12 字段已包含——隐含在 5 组 details 中可"下载"。先不加，由 M3-E 议程上专门加。设计 D2 暂不展开）。
- `accountCode` 在 Summary 与 Detail 都存在 —— 这是为了日后"谁拥有此项目"的判断（前端不下沉到关联查询），与详情页运营场景相符。
