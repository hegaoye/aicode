# Tasks

## 1. 后端 VO 与 Controller 改造

- [ ] 1.1 新增 `facade/src/main/java/com/aicode/project/vo/ProjectSummaryVO.java`：7 字段（`code` / `name` / `englishName` / `state` / `createTime` / `updateTime` / `accountCode`）；含 `@Data` 与显式 `@Schema(description = ...)`；验证：`./aicode/gradlew :aicode:compileJava` exit=0
- [ ] 1.2 新增 `facade/src/main/java/com/aicode/project/vo/ProjectDetailVO.java`：12 字段（`code` / `name` / `englishName` / `description` / `copyright` / `author` / `phone` / `basePackage` / `buildNumber` / `createTime` / `updateTime` / `accountCode`）；验证：`./aicode/gradlew :aicode:compileJava` exit=0
- [ ] 1.3 修改 `ProjectController.list`：返 `R<PageVO<ProjectSummaryVO>>`，装配改 `toSummary(Project)` 抽取 7 字段；`PageVO.records` 类型变 `List<ProjectSummaryVO>`；验证：`./aicode/gradlew :aicode:compileJava` exit=0
- [ ] 1.4 修改 `ProjectController.loadByCode`：返 `R<ProjectDetailVO>`；装配改 `toDetail(Project)` 抽取 12 字段；保留 M3-A-1 的"project == null"防护与 `R.success()` 早返；验证：`./aicode/gradlew :aicode:compileJava` exit=0
- [ ] 1.5 全仓 grep `ProjectVO` 引用：`git grep -n "ProjectVO" aicode/src facade/src aicode-web/src -- "*.java" "*.ts" "*.tsx" | grep -v "/generated/"`；无业务层独立引用即可删除 `ProjectVO`；若有，STOP 评估 BREAKING 影响范围，记录到 commit msg
- [ ] 1.6 删除 `facade/src/main/java/com/aicode/project/vo/ProjectVO.java`（前提：1.5 通过）；验证：`./aicode/gradlew :aicode:compileJava` exit=0
- [ ] 1.7 反向验证：临时把 `toSummary` 改回旧 `toProjectVO` 全 22 字段映射（投影到 `ProjectSummaryVO` 但用 setter 全 22 字段）→ 后端测试有 ui 项目列表字段断言（`ProjectSummaryVO.englishName`）仍绿，但前端 `ProjectListPage` `englishName` 字段出现不在其订阅集合的字段 ✓；恢复后通过

## 2. 契约产物与前端重生成

- [ ] 2.1 跑 `./aicode/gradlew :aicode:test --tests "*OpenApiExportIT"`；验证 `contracts/openapi/openapi.yaml` 中 `/project/list` 的 `data.records.items` 改 ref `ProjectSummaryVO`，`/project/load/code/{code}` 改 ref `ProjectDetailVO`；用 grep 验证文件包含 `ProjectSummaryVO:` 与 `ProjectDetailVO:` 两个 schema
- [ ] 2.2 跑 `cd aicode-web && npm run generate`；验证 `aicode-web/src/generated/model/{projectSummaryVO.ts, projectDetailVO.ts, rProjectSummaryVO.ts, rProjectDetailVO.ts}` 出现；`projectVO.ts` 不再生成（删除）
- [ ] 2.3 确认前端无 `ProjectVO` 引用：`git grep -n "ProjectVO" aicode-web/src -- ':!*/generated/*'`；若有则同步迁移

## 3. 前端代码改造

- [ ] 3.1 修改 `aicode-web/src/pages/ProjectListPage/index.tsx`：import `ProjectSummaryVO` 替代 `ProjectVO`；`state.data.records` 类型为 `ProjectSummaryVO[]`；表格渲染按 7 字段（`code`/`name`/`englishName`/`state`/`createTime`/`updateTime`/`accountCode`）调整；`disabled` 判断用 `row.state`（已在）；验证：`npm run typecheck` 0 错
- [ ] 3.2 修改 `aicode-web/src/pages/ProjectDetailPage/index.tsx`：import `ProjectDetailVO`；按 12 字段分 5 组（标识 / 细节 / 技术计数 / 时间 / 关联）渲染；`createTime` / `updateTime` 仍走 `formatDate`；验证：`npm run typecheck` 0 错

## 4. 测试改造

- [ ] 4.1 修改 `aicode-web/src/pages/ProjectListPage/index.test.tsx`：默认 mock 改返 `ProjectSummaryVO` 形态（`code` / `name` / `englishName` / `state: 'Enable'` / `createTime` 等）；既有"row 含 contract-demo 文本"断言仍绿；新增 `do not render `downloadUrl`/databaseType` 等 SummaryVO 不包含的字段"断言
- [ ] 4.2 修改 `aicode-web/src/pages/ProjectDetailPage/index.test.tsx`：默认 mock 改返 `ProjectDetailVO` 形态；现有 22 字段断言改用 12 字段并声明不渲染 `databaseType` 等；`data-testid="project-detail-field-downloadUrl"` 类似 testid 不应存在
- [ ] 4.3 修改 `aicode/src/test/java/com/aicode/contract/ApiContractIT.java` 场景⑧ `LoadByCodeReturnsWrappedVO`：断言字段改用 `ProjectDetailVO.data.description === '契约回归测试种子项目'`（强等值）；`ProjectListPageVoContract` 场景⑦ 改用 `ProjectSummaryVO.data.records[*]` 字段集合断言
- [ ] 4.4 反向验证：临时把 `ProjectDetailVO` 类补全 22 字段全集合 → 场景⑧ 反向断言 SHALL 失败（字段集合不超集）

## 5. 验证 + 文档与登记

- [ ] 5.1 跑全套 `./aicode/gradlew :aicode:test`：预期 169+ 用例 / 0 失败；反向断言成立
- [ ] 5.2 跑 `npm run typecheck && npm test`：预期全 64+ 用例 / 0 失败；新增反向断言成立
- [ ] 5.3 跑 `npm run build`：预期产物大小变化 < 5 KB
- [x] 5.4 修改 `AGENTS.md §11.3`：登记 M3-C 已合并（`ProjectSummaryVO`/`ProjectDetailVO` 拆分，`api-data-object` 增 2 Requirement 4 Scenario） *(已完成：登记段就地落进 AGENTS.md M3 段落)*
- [x] 5.5 执行 `openspec validate --strict 2026-10-17-project-vo-trim`：校验通过 *(已完成：`Change is valid`，0 错)*
- [x] 5.6 同步 delta spec 到 `openspec/specs/api-data-object` 并 `openspec archive`；验证主能力增 2 Requirement 4 Scenario；`openspec list --specs api-data-object` 现状从 4 → 6 Requirements *(已完成：主 spec 物化 2 Requirement 4 Scenario（实际合并后是 6 Requirements / 12 Scenarios from 4 → 6）；`openspec validate --specs` 12/12 通过；变更 mv 至 archive；`openspec list` No active changes)*
