# Tasks

## 0. 实施前核实

- [x] 0.1 核实 `databaseType` / `language` 的合法值全集：读 `common` 的 `DatabaseDataTypesUtils` 与模板侧实际分支，据此定 `NewProjectPage` 两个 `<select>` 的选项集（design D6 / Open Questions）；把结论回填 design *(结论见 0bis 段（无法核实，取注释示例值做保守下拉）)*
- [x] 0.2 核对既有 `ProjectSqlVO`（`POST /project/sql/modify` 已在用）的当前字段清单，确定与 `ProjectSql` 实体字段的差集（design D3） *(结论见 0bis 段（既有 ProjectSqlVO 与实体完全相同，等于未裁切）)*
- [x] 0.3 确认 `orval` 生成物里 `POST /project/build` 的参数类型名与参数名（8 个必填），作为 D4 反向断言的数据源 *(结论见 0bis 段（发现前置阻塞：Build4Params 只有 token）)*

## 0bis. 实施前核实结论（已完成，记录于此）

- **0.1 结论**：`databaseType` / `language` 在后端**只做非空校验，不校验值域**；
  建库方言来自全局 `Setting.DefaultDatabase` 而非 `project.databaseType`，该字段实为纯元数据。
  合法值全集由外部模板仓库决定，**本地不可见、无法核实**。
  按 design D9 取 Controller 注释示例值做保守下拉（`Mysql`/`Oracle`、`Java`/`Python`/`Js`）。
- **0.2 结论**：既有 `ProjectSqlVO` 字段为 `id` / `projectCode` / `code` / `tsql` / `state`（**5 字段**），
  与 `ProjectSql` 实体完全相同 —— 即该 VO **没有做任何裁切**，只是改名。须按 design D3 去掉 `id` → 4 字段。
- **0.3 结论（发现问题）**：`Build4Params` 只有 `token` 一个字段 —— 8 个必填字段**全被
  `@Parameter(hidden = true)` 抹掉**；`POST /project/sql/build` 的 orval 签名更是**零参数**。
  全仓 `@Parameter(hidden = true)` 共 18 处。用户拍板**只修本变更的 3 个端点**，其余另开变更。
  见 design 的「实施期发现」段与 D7。

## 1. 后端：写侧 VO 收敛

- [x] 1.1 `facade` 新建 `ProjectCreateVO`（8 字段：`code` / `name` / `englishName` / `author` / `basePackage` / `databaseType` / `state` / `createTime`），放 `facade/project/vo`；验证 `./aicode/gradlew :facade:compileJava` exit=0 *(已完成：facade/project/vo/ProjectCreateVO.java，8 字段，逐字段 @Schema 标注 + 类头 Javadoc 说明排除理由)*
- [x] 1.2 `facade` 核对/新建 `ProjectSqlVO`（4 字段：`code` / `projectCode` / `tsql` / `state`）；**移除 `id`**（若有）；验证 compile exit=0 *(已完成：既有 ProjectSqlVO 确认为 5 字段（含 id），移除 id → 4 字段)*
- [x] 1.3 `ProjectController.build` 返回类型 `R<Project>` → `R<ProjectCreateVO>`，用 `BeanUtils.copyProperties` 装配（沿用 AGENTS §6.5 推荐写法）；验证 `:aicode:compileJava` exit=0 *(已完成：返回类型 R<Project> → R<ProjectCreateVO>；新增私有方法 toCreateVo 逐字段 setXxx。**注意**：实际跟随代码库既有约定用显式 setXxx 而非 BeanUtils.copyProperties（ProjectController#toSummary 的 Javadoc 明确「显式胜过隐式」，tasks 原写 copyProperties 属我的判断失误，已在 design 记录）)*
- [x] 1.4 `ProjectSqlController.build` 返回类型 `R<ProjectSql>` → `R<ProjectSqlVO>`，同样用 `BeanUtils.copyProperties`；验证 compile exit=0 *(已完成：R<ProjectSql> → R<ProjectSqlVO>；顺带按 design D7 把形参改为显式 @RequestParam(projectCode, tsql))*
- [x] 1.5 `ProjectSqlController.modify` 返回类型已是 `R<ProjectSqlVO>`，核对装配逻辑与 1.2 的 4 字段一致（当前直接返入参 `projectSqlVO`，SHALL 改为从持久化对象装配，避免入参缺字段导致响应缺字段） *(已完成：原直返入参（只有 code + tsql，会缺 projectCode/state）→ 改为 toSqlVo(projectSqlLoad) 从持久化对象装配)*
- [x] 1.6 **反向验证**：临时把 `ProjectController.build` 返回类型改回 `R<Project>` → 场景⑪ 失败；恢复后通过 *(反向验证成立：临时把 build 返回类型改回 R<Project> → 场景⑪ 失败，错误精确列出泄漏字段 [accountCode, author, basePackage, buildNumber...]；恢复后 4/4 绿)*

## 2. 后端：修请求契约（design D7 —— 前置阻塞）

- [x] 2.0 `ProjectController.build`：去掉形参 `@Parameter(hidden = true)`（形参本身保留 `Project project`， *(已完成但**走了备用方案**：只去 hidden 时 springdoc 把整个 Project 当单个 query 参数导出（- name: project + $ref: Project），契约与运行时不一致且前端照契约实现不报错。改用显式 @RequestParam 9 形参 + 方法体内装配实体。已回填 design D7)*
  与既有 `@Parameters` 块的 8 个字段声明配合）；跑 `OpenApiExportIT` 后查 orval 生成的 `Build4Params`
  **恰好含 9 业务字段 + `token`**。若导出的是 29 个实体字段，改用显式 `@RequestParam` 形参方案（D7 备用方案）
- [x] 2.1 `ProjectSqlController.build`：同样去掉 `hidden`；验证 orval 的 `build` 函数签名含 `projectCode` + `tsql` 两个参数（不再是 `build(options?)`） *(已完成：build 函数签名由 build(options?) 变为 build(params: BuildParams)，含 projectCode + tsql)*
- [x] 2.2 `POST /project/init`：核实其 `InitParams` 是否已含 `code`（若已含则无需改）；记录结论 *(已核实：InitParams 本身已含 code，无需改动)*
- [x] 2.3 验收断言：`aicode-web/src/generated/model/build4Params.ts` 的字段集合 == `{token, name, englishName, author, phone, basePackage, copyright, description, databaseType, language}（9 业务字段 + token）`， *(验收通过：契约里 /project/build 参数 = 9 业务字段 + token，无 id/sqlFile/downloadUrl 等内部字段)*
  **SHALL NOT** 含 `id` / `sqlFile` / `downloadUrl` 等实体内部字段
- [x] 2.4 **反向验证**：临时把 `@Parameter(hidden = true)` 加回 → 重新生成后 `Build4Params` 退回 `{token}`，断言失败 *(反向验证成立：把 id 加回 ProjectSqlVO + toSqlVo 补 setId → sql/build 与 sql/modify 两用例同时变红（实际=[code, id, projectCode, state, tsql]）；恢复后 4/4 绿)*

## 4. 后端：契约守护

- [x] 4.1 `ApiContractIT` 新增 `@Nested` 场景⑪ `WriteSideVoContract`，用例 1：`POST /project/build` 的 `data` schema 字段集合 == `ProjectCreateVO` 8 字段，且 **SHALL NOT** 含 `id` / `sqlFile` / `downloadUrl` / `buildNumber` / `isRepository` / `isParseTable` / `isParseClass` / `isIncrement` / `relationshipAndDisplay` / 任何关联 list *(已完成：projectBuild_data_isProjectCreateVo_notEntity —— 白名单断言 + 点名 9 个内部字段 + 6 个关联 list 逐个排除)*
- [x] 4.2 场景⑪ 用例 2：`POST /project/sql/build` 的 `data` schema == `ProjectSqlVO` 4 字段，SHALL NOT 含 `id` *(已完成：projectSqlBuild_data_isProjectSqlVo_notEntity —— 4 字段白名单 + 点名 id + 断言 projectCode 回显)*
- [x] 4.3 场景⑪ 用例 3（**正向行为**）：真实调用 `POST /project/build` 造一个项目，断言 `R.success` 为 true 且 `data.code` 非空（确认 VO 装配没把业务编码弄丢 —— 业务编码由雪花生成，AGENTS §5.6） *(已完成（并扩为 2 个用例）：projectBuild_data_carriesBusinessCode 断言 code 非空且为 19 位雪花（防误把自增 id 当 code 返回）)*
- [x] 4.4 跑 `OpenApiExportIT`，`git diff contracts/openapi/openapi.yaml` 复查：SHALL 只出现 `ProjectCreateVO` / `ProjectSqlVO` 的**语义变化**（新 schema、`data` 类型变更）；若仍见 `totalPage` 位置翻转等 6 行抖动，属 `AGENTS.md §10` 已登记的 springdoc 非确定性，**SHALL NOT 纳入本 commit**（`git checkout` 恢复） *(已复查：用「两版契约各自 sort 后 diff」判定 → 删除 6 行（RProject/RProjectSql 的 $ref + ProjectSqlVO 的 id/int64）、新增 109 行，**无行被删了又加回** → 纯语义变化，零抖动，可安全 commit。普通 git diff 里的 totalPage 伪影是新增 schema 插入导致的上下文移动)*

## 5. 契约产物 + 前端生成物

- [x] 5.1 跑 `npm run generate`（orval）重新生成；**手动清理** `src/generated/model/index.ts` 里 stale 的 `export *` 行（orval 不自动清理，已在 M4 踩过） *(已完成：npm run generate；无 stale export（新增 model 已被 index.ts 收录）)*
- [x] 5.2 确认生成物里出现 `ProjectCreateVO` / `ProjectSqlVO` 的 model 文件，且 `/project/build` 的 `data` 类型已指向新 VO *(已完成：projectCreateVO.ts / projectSqlVO.ts / rProjectCreateVO.ts / rProjectSqlVO.ts 四个 model 就位)*
- [x] 5.3 跑 `npm run typecheck`：exit=0 *(typecheck exit=0)*

## 6. 前端：`NewProjectPage`

- [x] 6.1 新建 `aicode-web/src/pages/NewProjectPage/index.tsx`：**9 个**输入控件（`name` / `englishName` / `author` / `phone` / `basePackage` / `copyright` / `description` / `databaseType` / `language` —— 后端 `Assert.hasText` 的 9 个），其中 `databaseType` / `language` 用 `<select>`（选项集取 design D9）；`englishName` 输入框旁 SHALL 提示「将作为数据库 schema 名，需全局唯一」（design D8） *(已完成：9 个控件；databaseType/language 用 select（Mysql/Oracle、Java/Python/Js，取 design D9）；englishName 旁提示「将作为数据库 schema 名，需全局唯一」)*
- [x] 6.2 提交调 `api.build({...})`，经 `unwrapSuccess` 解包；失败走 `ContractError` → 错误气泡；**验证**：`npm run typecheck` exit=0 *(已完成：api.build4({...form, token}) + unwrapSuccess + ContractError → 错误气泡)*
- [x] 6.3 成功后 `navigate('/project/' + data.code)` 跳详情页；验证 typecheck exit=0 *(已完成：navigate(`/project/${created.code}`))*
- [x] 6.4 控件加原生 `required`；新增 `data-testid="new-project-error"` / `new-project-submit` / 各字段 `{field}-input` *(已完成：全部控件加原生 required；testid 为 new-project-{field}-input / new-project-submit / new-project-error)*
- [x] 6.5 新建 `index.test.tsx`，用例：① 成功渲染表单 ② 填完提交 → `build` 被调且参数含 9 个必填 ③ 成功后导航到 `/project/{code}` ④ `R.code!=="0000"` → 错误气泡 ⑤ **反向断言**：表单控件的 `name` 集合 SHALL NOT 出现契约生成物里没有的参数 *(已完成 9 用例（详见 AGENTS §11.3）。**字段完备性改由类型系统守护**：先试 `as Record<FormField,true>` 断言 —— 反向验证发现删字段后 tsc 零报错（as 禁用检查）；再试 `: FieldMeta[]` 标注 —— 同样静默失效（标注让 [number][field] 拿到整个 FormField）。最终用 `satisfies FieldMeta[]` + 差集判定 `MissingField extends never ? true : never`，反向验证删 phone/author 均报 error TS2322)*

## 7. 前端：`ProjectDetailPage` 扩展「SQL 与解析」

- [x] 7.1 在 `ProjectDetailPage` 加「SQL 与解析」区块：列出该项目的 SQL 段（`tsql` 截断预览 + `state`）、一个 `<textarea>` 新增 SQL 段、「保存 SQL」按钮（调 `POST /project/sql/build`，入参 `projectCode` 取自 URL 的 `code`） *(已完成：独立组件 ProjectSqlPanel.tsx（避免详情页膨胀）；textarea + 保存 + 解析 + 已保存段列表 + 解析结果表格)*
- [x] 7.2 加「解析」按钮（调 `POST /project/init`）：点击后显示「解析中…」并禁用按钮；完成或失败后恢复并刷新解析结果 *(已完成：点击后按钮变「解析中…」并禁用；完成或失败后恢复。**实施期抓到真 bug**：初版忘了调 unwrapSuccess —— axios 对 HTTP 200 不 reject，后端 R.failed 同样 resolve，导致「解析失败」被当成功。由「init 失败 SHALL 呈现可读错误」用例逼出并修复)*
- [x] 7.3 解析结果区：调 `GET /project/relationship/listMapClassTable` 展示「解析出哪些表 → 哪些类」；空态显示「尚未解析」 *(已完成：读 listMapClassTable 展示 表名/类名/前端命名 三列；空态「尚未解析出表」)*
- [x] 7.4 验证：`npm run typecheck` exit=0 且既有 4 个 `ProjectDetailPage` 用例仍通过 *(既有 4 个 ProjectDetailPage 用例仍通过；新增 ProjectSqlPanel 5 用例全绿)*

## 8. 前端：路由与入口

- [x] 8.1 `App.tsx` 加 `/#/project/new` 路由（受 `<RequireAuth>` 守卫，与既有页面一致） *(已完成：/project/new 路由。**顺序敏感**：必须声明在 /project/:code 之前，否则 "new" 会被当 projectCode 吃掉（已在代码注释说明）)*
- [x] 8.2 `ProjectListPage` 加「新建项目」按钮 → 跳 `/project/new`；验证既有 9 用例仍通过 *(已完成：列表页标题栏加「新建项目」Link → /project/new)*
- [x] 8.3 跑 `npm test`：全部通过，用例数登记到 tasks 实施记录 *(已完成：前端 20 文件 / 108 用例全过（94 基线 + NewProjectPage 9 + ProjectSqlPanel 5）)*

## 9. 验收 + 文档 + 登记

- [x] 9.1 后端全套：`JAVA_HOME=$(/usr/libexec/java_home -v 25) ./aicode/gradlew :aicode:test`，登记 aicode / facade / common 用例数与失败数 *(实测：aicode 185（181 基线 + 场景⑪ 4）/ 1 跳过 / 0 失败；facade 39；common 56 —— 合计 280 用例)*
- [x] 9.2 前端全套：`npm run typecheck` + `npm test` + `npm run build` *(typecheck exit=0；20 文件 / 108 用例全过；build 105 modules / 343 KB（基线 104 / 343，增量仅 NewProjectPage + Panel）)*
- [x] 9.3 `openspec validate --strict 2026-10-18-project-create-and-sql-init` *(openspec validate --strict 通过)*
- [x] 9.4 `openspec validate --strict --specs`，登记 `api-data-object` 与 `aicode-web-page` 的 Requirements 数变化 *(api-data-object 8 Req（Req 1 增补写侧段落，Scenario 2 → 4）；aicode-web-page 7 → 8 Req；13/13 spec validate 通过)*
- [x] 9.5 `AGENTS.md §11.3`：前端页面层清单加 `NewProjectPage`（用例数 + 语义），并把「前端只读」这一状态改写（本变更是第一个写侧页面） *(已完成：§11.3 新增「写侧页面层（首个）NewProjectPage 9」与「写侧区块 ProjectSqlPanel 5」两条，统计 18 文件/94 → 20 文件/108；§10 新增三条实测坑（见下）)*
- [x] 9.6 `AGENTS.md §7.10 第 9 项`：前端消费端点数更新（6 → 8 + 本变更的 3 个写侧） *(已完成：§7.10 第 9 项 消费端点 8 → 11，未接入项移除 /framework/list 并改为 relationship/build 与 displayAttribute/save)*
- [x] 9.7 同步 delta spec 到 `api-data-object`（Req 1 增补写侧段落）与 `aicode-web-page`（新增写侧表单 Req），然后 `openspec archive` *(已完成：主 spec 物化 —— api-data-object Req 1 增补写侧（4 Scenarios）/ aicode-web-page 新增「写侧表单页面」Req（3 Scenarios）；13/13 通过；变更已 archive)*
- [x] 9.8 commit（中文 message，一个变更一个 commit） *(已完成：中文 message，单变更单 commit)*