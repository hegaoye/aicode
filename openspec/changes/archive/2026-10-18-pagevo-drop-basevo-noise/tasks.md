# Tasks

## 0. 实施前核实

- [x] 0.1 核实 `PageVO` 对 `BaseVO` 的 4 个字段是否**零引用** —— 已初步 grep 确认为 0，但实施时再确认一次（若非 0，删继承会编译失败） *(通过：grep 确认 `PageVO` 对 4 个字段零引用 → 删继承编译零影响)*
- [x] 0.2 核实另外两个仍在用的 `*PageVO`（`ProjectModulePageVO` / `ProjectFramworkPageVO`）确为**入参**用法，删继承对它们无影响 *(通过：`ProjectModulePageVO` / `ProjectFramworkPageVO` 的引用点均在 `@Parameter(hidden = true)` 的**入参**位置，删继承对它们无影响)*
- [x] 0.3 记录改动前契约里所有 `PageVO*` schema 的字段集合（用于事后比对） *(已完成：改动前 3 个 `PageVO*` schema 各 9 字段（含 4 噪音）)*

## 1. 后端：删继承

- [x] 1.1 `common/src/main/java/com/aicode/core/PageVO.java`：`extends BaseVO` → 无（保留 `implements Serializable`） *(已完成：`extends BaseVO` 去掉；**连带**发现 `@EqualsAndHashCode(callSuper=true)` 引用父类，一并移除 + 清理其 import)*
- [x] 1.2 验证 `./aicode/gradlew :aicode:compileJava` 与 `:facade:compileJava` 均 exit=0 *(通过：`:common` / `:facade` / `:aicode` 三模块 compileJava 全 exit=0)*
- [x] 1.3 **确认 `BaseVO` 类本身未被删** —— 9 个 `*PageDTO` 入参仍在用它（design D1） *(通过：`BaseVO` 类未删（9 个 `*PageDTO` 入参仍在用）)*

## 2. 契约守护

- [x] 2.1 `ApiContractIT` 场景⑯ `PageVoQueryFieldNoise`： *(已完成：场景⑯ **2 用例** —— 用例 1 数据层（不含 4 噪音 + 仍含 5 分页字段，**防过度删除**）/ 用例 2 扫描型（遍历每个 `PageVO*` schema）)*
  - 用例 1（**数据层**）：实际调 `GET /project/list`，断言 `data` 字段集合 **NOT** 含 `createTimeBegin` / `createTimeEnd` / `updateTimeBegin` / `updateTimeEnd`，且 `data` 仍含 `curPage` / `pageSize` / `totalRow` / `totalPage` / `records`（防"过度删除"）
  - 用例 2（**扫描型**，覆盖所有分页端点）：遍历导出契约里**每一个** `PageVO*` schema，逐个断言不含那 4 个字段 —— 一次覆盖 `/project/list` / `/account/list` / `/framework/list` 等，将来新增分页端点自动纳入（design D3）
- [x] 2.2 **反向验证**：临时把 `extends BaseVO` 加回 → 场景⑯ 用例 1 与 2 变红 *(**成立且暴露了我自己的一个 bug**：加回 extends 后 2 条变红，数据层报「实际字段集含 createTimeBegin」、扫描层列出 12 个违规（3 schema × 4 字段）。**且第一次跑时扫描断言是假通过的** —— 我用 `indexOf("\n    ")` 找下一个 schema 头，8 空格缩进的属性行也以 4 空格为前缀，片段被截断。改为行首锚定正则后重跑才真正生效（同一陷阱我在 fieldBlock() 踩过一次，已写进注释与 AGENTS §11.3）)*

## 3. 契约产物 + 前端生成物

- [x] 3.1 跑 `OpenApiExportIT` 刷新契约 *(已完成（OpenApiExportIT 随测试自动跑）)*
- [x] 3.2 **抖动判定**：两版契约各自 `sort` 后用 `comm` 比对（AGENTS §10 已登记方法）—— 确认无字段顺序抖动，只提交 `PageVO*` 字段减少这一语义变化 *(通过：**删除 36 行 / 新增 0 行**，`comm` 比对确认无行被删了又加回 → 零抖动；删除内容全是噪音字段（12 个字段名 + 12×2 行类型定义）)*
- [x] 3.3 跑 `npm run generate`；确认所有 `PageVO*` model 的 TS 类型少 4 个可选键 *(通过：3 个 `PageVO*` 的 TS 类型均降到 5 字段)*
- [x] 3.4 清理 `src/generated/model/index.ts` 的 stale export（若 orval 未清） *(通过：无 stale export)*
- [x] 3.5 跑 `npm run typecheck`：预期 exit=0（那 4 个键是**可选**且前端零消费）；若报错说明有代码消费了它们，SHALL 先查明再处理 *(**typecheck exit=0** —— 顺带证实前端**零消费**那 4 个字段（若是可选键消失导致类型错误，tsc 会报）)*

## 4. 验收 + 文档 + 登记

- [x] 4.1 后端全套 `JAVA_HOME=$(/usr/libexec/java_home -v 25) ./aicode/gradlew test` *(实测：aicode **208**（206 + 场景⑯ 2）/ 2 跳过 / 0 失败；facade 39；common 56 —— 合计 **303**)*
- [x] 4.2 前端全套 `npm run typecheck` + `npm test` + `npm run build` *(**前端 22 文件 / 162 用例零改动全绿**；build 108 modules / 349 KB)*
- [x] 4.3 `openspec validate --strict 2026-10-18-pagevo-drop-basevo-noise` 与 `--specs` *(openspec validate --strict 与 --specs 均通过（14/14）)*
- [x] 4.4 `AGENTS.md §11.1`：该条列了「`PageVO<T> extends BaseVO` 导致所有分页端点带 4 个噪音字段」的遗留项，改为「**已修复**」并注明 commit *(已完成：§11.1 该遗留项改为「已修复」+ 注明 commit、守护测试名、以及「入参 *PageDTO 仍继承 BaseVO」的原因)*
- [x] 4.5 `AGENTS.md §11.3`：登记契约层新增场景⑯ *(已完成：§11.3 契约层清单新增「分页容器噪音字段防线」条目，含扫描断言的 indexOf 陷阱)*
- [x] 4.6 同步 delta spec 到 `api-data-object`（Req 增补分页容器层 + 出参/入参职责区分），然后 `openspec archive` *(已完成：主 spec 物化 —— api-data-object 的该 Req 从 2 → **4** Scenario（新增分页容器层 + 出参/入参职责区分）；14/14 通过；变更已 archive)*
- [x] 4.7 commit（中文 message） *(已完成：中文 message，单变更单 commit)*