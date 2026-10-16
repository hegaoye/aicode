# Tasks

## 1. 后端真分页改造

- [x] 1.1 修改 `aicode/src/main/java/com/aicode/project/ctrl/ProjectController.java` 的 `list` 方法：入参兜底 —— `curPage == null || curPage <= 0` → `1`，`pageSize == null || pageSize <= 0` → `10`（design D2，与前端 `PAGE_SIZE = 10` 对齐）；提取为局部变量 `effectiveCurPage` / `effectivePageSize`；验证：`./aicode/gradlew :aicode:compileJava` exit=0 *(已完成：effectiveCurPage / effectivePageSize 局部变量，null/≤0 回退 1 / 10；compile exit=0)*
- [x] 1.2 同一方法内：`pageVO.setCurPage(effectiveCurPage)` / `setPageSize(effectivePageSize)`；用 `projectService.count(queryWrapper)` 取 `totalRow` 并 `pageVO.setTotalRow(total)`；用 `projectService.page(new Page<>(effectiveCurPage, effectivePageSize), queryWrapper)` 取当前页（取代 `lambdaQuery().list()` 全量拉取）；排序改为 `orderByDesc(Project::getCreateTime).orderByDesc(Project::getId)`（tiebreaker，防 `createTime` 相同时翻页行重复/遗漏，见 design Risks）；`totalPage = ceil(total / effectivePageSize)` 显式算；验证：`./aicode/gradlew :aicode:compileJava` exit=0 *(已完成：count() + page(new Page<>(curPage,pageSize), queryWrapper) 真分页；显式 set 四个字段；totalPage 用 ceil 显式算；排序加 id tiebreaker 防 createTime 同值翻页重复/遗漏)*
- [x] 1.3 元素装配仍走 `toSummary(Project)`（不变）；确认 `Page` import 已存在（`com.baomidou.mybatisplus.extension.plugins.pagination.Page`，与 `/framework` 用的同）；验证：`./aicode/gradlew :aicode:compileJava` exit=0 *(已完成：元素装配仍走 toSummary(Project)；Page / IPage / QueryWrapper import 已存在)*
- [x] 1.4 反向验证：临时把 `page(page, queryWrapper)` 改回 `lambdaQuery().list()` 并 `setCurPage` / `setPageSize` / `setTotalPage` 去掉 → `ApiContractIT` 场景⑦ 新增用例 SHALL 失败（`data.curPage` 期望 1 实得 0）；恢复后通过 *(已完成：临时改回全量 + 去掉三个 set → 场景⑦ 失败（data.curPage 期望 1 实得 0）；恢复后 4/4 通过)*

## 2. 契约守护测试

- [x] 2.1 在 `aicode/src/test/java/com/aicode/contract/ApiContractIT.java` 场景⑦ `ProjectListPageVoContract` 新增 1 个 `@Test` `project_list_reflectsPaginationParams`：请求 `/project/list.shtml?token=...&curPage=1&pageSize=10`，断言 `data.curPage == 1`、`data.pageSize == 10`、`data.totalPage == ceil(totalRow / 10)`（`totalRow > 0` 时 `totalPage SHALL NOT 为 0`）；验证：新增用例通过 *(已完成：project_list_reflectsPaginationParams 断言 curPage==1 / pageSize==10 / totalPage==ceil(totalRow/10)（totalRow>0 时）)*
- [x] 2.2 新增 1 个 `@Test` `project_list_fallsBackToDefaultsWhenParamsInvalid`：请求 `?curPage=0&pageSize=-1`，断言 `data.curPage == 1`、`data.pageSize == 10`（spec Scenario 2）；验证：通过 *(已完成：project_list_fallsBackToDefaultsWhenParamsInvalid 断言 curPage=0→1 / pageSize=-1→10 且 R.success 为 true（修了一处断言读 data.success 的错误，success 在 R 顶层）)*
- [x] 2.3 确认既有 2 个用例（`records` 字段集合 / 关键字段强等值）不受影响，仍通过 *(已完成：既有 2 用例（records 字段集合 / 关键字段强等值）仍通过，场景⑦ 共 4/4 绿)*

## 3. 契约产物验证

- [x] 3.1 跑 `./aicode/gradlew :aicode:test --tests "*OpenApiExportIT"`；验证 `contracts/openapi/openapi.yaml` 的 `git diff` **为空**（schema 形状本已含 `curPage` / `pageSize` / `totalPage`，本变更只让运行时填上真值）；若 diff 非空说明 schema 有意外变化，需复查 *(已完成：契约 git diff 仅 6 行且为**纯字段顺序变化**（PageProjectModulePageVO / ProjectFramworkPageVO 的 totalPage 位置移动，springdoc 字段顺序不稳定，与本变更无语义关系）；已一并 commit 保持契约与代码同步)*
- [x] 3.2 前端零改动验证：跑 `cd aicode-web && npm run typecheck && npm test`；预期 85 用例仍全绿（`totalRow` / `records` 语义不变，`ProjectListPage` 现有 5 用例不受影响） *(已完成：前端零改动；typecheck exit=0；18 文件 / 85 用例仍全绿（totalRow / records 语义不变）)*

## 4. 验收 + 文档 + 登记

- [x] 4.1 跑全套 `./aicode/gradlew :aicode:test`：预期 179 + 2 = **181 用例** / 0 失败 *(实测：aicode 181 用例 / 1 跳过 / 0 失败（179 基线 + 场景⑦ 2 新增）)*
- [x] 4.2 修改 `AGENTS.md §11.3`：在契约层描述里补一句"`/project/list` 曾是假分页（忽略 curPage/pageSize、三个分页字段恒 0），已由 `2026-10-18-project-list-real-pagination` 修成真分页" *(已完成：AGENTS.md §11.3 契约层清单新增「假分页回归防线」条目)*
- [x] 4.3 修改 `AGENTS.md §11.1 安全加固` 或 §7.10 第 9 项：登记"`/account/list` 同样只 set `totalRow` 不 set `curPage` / `pageSize` / `totalPage`"为待收敛点 *(已完成：同一条目末尾登记「/account/list 同类待收敛」)*
- [x] 4.4 执行 `openspec validate --strict 2026-10-18-project-list-real-pagination`：校验通过
- [x] 4.5 同步 delta spec 到 `openspec/specs/api-contract` 并 `openspec archive`；验证主能力新增 1 Requirement「分页接口的 PageVO SHALL 回填实际查询参数而非默认值」含 3 Scenarios；requirements 现状 12 → **13** *(已完成：主 spec 物化 1 Requirement 3 Scenarios；api-contract requirements 12 → 13；13/13 通过；变更 mv 至 archive；No active changes)*
