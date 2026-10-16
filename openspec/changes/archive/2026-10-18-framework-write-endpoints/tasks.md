# Tasks

## 0. 实施前核实

- [x] 0.1 核实去掉 `hidden` 后 `updateIsPublic` 的 `@Parameters` 块（`code` / `isPublic`）**是否真的生效** —— 前次实测 `/account/delete` 在 `hidden` 存在时该块被盖掉；若去掉后仍不生效，改用显式 `@RequestParam` 形参（design D3 的备选） *(**结论：`@Parameters` 块不生效**。去掉 hidden 后 springdoc 仍把 `@RequestParam` 显式形参导出为**单个**名为 `frameworks` 的 query 对象；试过 `@ModelAttribute` 也无效 → 改用**显式 `@RequestParam` 形参**（design D3 的备选方案）)*
- [x] 0.2 核实 `modify` 去掉 `hidden` 后契约导出的是**实体字段集**还是单对象参数；记录实际形态（design D2 的已知代价） *(已记录：改造前契约是「响应 `RFrameworks` + 无 parameters」)*
- [x] 0.3 记录改动前两个端点的契约形态（响应 ref + 有无 parameters），供事后比对 *(已完成：改动前 `/framework/modify` 与 `/framework/updateIsPublic` 均 `响应=RFrameworks  有参数=False`)*

## 1. 后端：两个端点改造

- [x] 1.1 `FrameworksController.modify`：形参去掉 `@Parameter(hidden = true)`；返回类型 `R<Frameworks>` → `R<FrameworkSummaryVO>`；用 `BeanUtils.copyProperties` 或逐字段装配（跟随本仓约定：`FrameworksController#list` 已用**逐字段 setXxx**） *(已完成：出参 R<FrameworkSummaryVO>；形参改 7 个显式 @RequestParam（code + 6 可选，含 password 作为**入参**）)*
- [x] 1.2 `FrameworksController.updateIsPublic`：同样去 `hidden` + 改出参 *(已完成：出参同上；形参改 2 个显式 @RequestParam（code + isPublic）)*
- [x] 1.3 **⚠️ 关键**：`update` 之后应重新查库再装配 VO —— 当前实现是 `R.success(frameworks)` *(**已实现（重要）**：新增 `reload(code)` 私有方法 —— update 后按 code 重查再装配 VO，不再直返入参实体（入参只带客户端提交的字段，password 未传时为 null，直返会让响应不完整）)*
  （直接返回**入参实体**），而入参可能不含某些字段（如 `password` 未传时为 null），
  返回的 VO 会不完整。**须改为 `update` 后按 code 重查**，让响应反映**持久化后的真实状态**
- [x] 1.4 验证 `./aicode/gradlew :aicode:compileJava` exit=0 *(通过：compileJava exit=0)*

## 2. 契约守护

- [x] 2.1 `ApiContractIT` 新增 `@Nested` 场景⑰ `FrameworkWriteEndpoints`： *(已完成：场景⑰ **2 用例** —— 用例 1 凭据（两处 ref FrameworkSummaryVO、不 ref RFrameworks、VO 本身不含 password）/ 用例 2 参数（**逐 operation** 检查含扁平 code、不含单对象 frameworks）)*
  - 用例 1（**凭据**）：`/framework/modify` 与 `/framework/updateIsPublic` 的响应 schema SHALL ref `FrameworkSummaryVO`，SHALL NOT 含 `password`；且 SHALL 与 `/framework/list` 指向**同一个** schema（同一 Controller 共用读投影）
  - 用例 2（**参数**）：两个端点在契约里 SHALL 有 `parameters` 段；`updateIsPublic` SHALL 至少含 `code` 与 `isPublic`
- [x] 2.2 **反向验证 A**：临时把 `modify` 返回类型改回 `R<Frameworks>` → 用例 1 变红 *(**成立**：modify 改回 R<Frameworks> → 用例 1 变红)*
- [x] 2.3 **反向验证 B**：临时把 `hidden` 加回 → 用例 2 变红（契约里重新无参数） *(**验证方式修正后才成立**：原计划"加回 hidden → 变红"**不成立** —— 实测**显式 `@RequestParam` 会覆盖 `@Parameter(hidden=true)`**（契约仍导出参数）。这反而说明显式形参更稳健，已记入 AGENTS §10。同时**用例 2 因此从"扫整个 path 片段"改为"逐 operation 切片"** —— 原写法会让 PUT 无参数而 POST 从块继承时假通过)*

## 3. 契约产物 + 前端生成物

- [x] 3.1 跑 `OpenApiExportIT` 刷新契约；核实两个端点的响应 ref 与 parameters 段（对照 task 0.3 的记录） *(通过：两处响应均为 RFrameworkSummaryVO；参数 `modify=[code,name,description,gitHome,account,password,isPublic]`、`updateIsPublic=[code,isPublic]`)*
- [x] 3.2 **抖动判定**：两版契约各自 `sort` 后 `comm` 比对（AGENTS §10 方法）—— 只提交语义变化 *(**零抖动**：删除 13 行 / 新增 100 行，`comm` 比对确认无行被删了又加回)*
- [x] 3.3 跑 `npm run generate`；确认 `modify` / `updateIsPublic` / `updateIsPublic1` 三个函数**从无参数变为带参数** *(通过：modify / updateIsPublic / updateIsPublic1 三函数从无参数变为带参数)*
- [x] 3.4 清理 `src/generated/model/index.ts` 的 stale export（若 orval 未清） *(通过：无 stale export)*
- [x] 3.5 跑 `npm run typecheck`：预期 exit=0（前端零消费这些端点）；若报错说明有隐藏消费者，SHALL 先查明 *(**typecheck exit=0** —— 顺带证实前端**零消费**这两个端点)*

## 4. 验收 + 文档 + 登记

- [x] 4.1 后端全套 `JAVA_HOME=$(/usr/libexec/java_home -v 25) ./aicode/gradlew test` *(实测：aicode **210**（208 + 场景⑰ 2）/ 2 跳过 / 0 失败；facade 39；common 56 —— 合计 **305**)*
- [x] 4.2 前端全套 `npm run typecheck` + `npm test` + `npm run build` *(**前端 22 文件 / 162 用例零改动全绿**；build 108 modules)*
- [x] 4.3 `openspec validate --strict 2026-10-18-framework-write-endpoints` 与 `--specs` *(openspec validate --strict 与 --specs 均通过（14/14）)*
- [x] 4.4 `AGENTS.md §11.1`：该条遗留项第 1 条（`/framework/modify` 与 `/framework/updateIsPublic` 泄漏 password）改为「**已修复**」+ 注明 commit 与守护测试 *(已完成：§11.1 遗留第 1 条改为「已修复」+ 记录"去 hidden 不够、实体形参会被导出为单对象"的坑与守护测试名)*
- [x] 4.5 `AGENTS.md §10` 或 §11.3：登记 `@Parameter(hidden = true)` 的**残留分布**（14 → 12 处，逐个列出所在 Controller），避免后人重复排查 *(已完成：§10 新增两条实测坑 —— ① 「断言契约结构时扫描范围 SHALL 精确到最小单元」（三次踩坑的对照表 + 三条处方）② 「显式 @RequestParam 会覆盖 @Parameter(hidden=true)」；§11.3 加场景⑰ 条目)*
- [x] 4.6 同步 delta spec 到 `api-data-object`（凭据 Req 增补写侧端点 + 共用读投影）与 `api-contract`（新增 hidden Req），然后 `openspec archive` *(已完成：主 spec 物化 —— api-data-object 凭据 Req 增补写侧端点与共用读投影（+2 Scenario）/ api-contract 13 → **14** Req（新增「会接收客户端输入的形参 SHALL NOT 被隐藏而从契约消失」含 3 Scenarios）；14/14 通过；变更已 archive)*
- [x] 4.7 commit（中文 message） *(已完成：中文 message，单变更单 commit)*