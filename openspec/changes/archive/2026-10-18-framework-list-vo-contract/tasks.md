# Tasks

## 1. 后端 VO 与 Controller 改造

- [ ] 1.1 新增 `facade/src/main/java/com/aicode/frameworks/vo/FrameworkSummaryVO.java`：7 字段 `id` / `code` / `name` / `description` / `gitHome` / `account` / `isPublic`，**不含** `password`，**不继承** `BaseVO`；含 `@Data` + `@Schema(description = ...)`；验证：`./aicode/gradlew :aicode:compileJava` exit=0
- [ ] 1.2 修改 `FrameworksController` 新增私有 `toSummary(Frameworks)` 逐字段装配方法；验证：`./aicode/gradlew :aicode:compileJava` exit=0
- [ ] 1.3 修改 `FrameworksController.load`：`R<Frameworks> load(String code)` → `R<FrameworkSummaryVO> load(String code)`；`code == null` 早返改 `R.success()`（避开 `R.success(BaseExceptionEnum)` 重载 NPE，见 M3-A-1 记录）；装配改 `R.success(toSummary(frameworks))`；`frameworks == null` 时同样返 `R.success()`；验证：`./aicode/gradlew :aicode:compileJava` exit=0
- [ ] 1.4 修改 `FrameworksController.list`：`R<com.aicode.core.Page<FrameworksPageVO>> list(...)` → `R<PageVO<FrameworkSummaryVO>> list(...)`；容器改 `PageVO<FrameworkSummaryVO>`；`setVoList` → `setRecords`；装配元素改 `toSummary`；保留 `ne(Frameworks::getId, 0)` 过滤与 `orderByDesc(id)` 排序；验证：`./aicode/gradlew :aicode:compileJava` exit=0
- [ ] 1.5 grep `FrameworksPageVO` 全仓引用：`git grep -n "FrameworksPageVO" aicode/src facade/src`。若无其他引用则删除 `facade/src/main/java/com/aicode/frameworks/vo/FrameworksPageVO.java`；若有，改为保留并标 `@Deprecated`，同时在 commit msg 记录待收敛点；验证：删除后 `./aicode/gradlew :aicode:compileJava` exit=0

## 2. 契约守护测试

- [x] 2.1 在 `aicode/src/test/java/com/aicode/contract/ApiContractIT.java` 新增 `@Nested class FrameworkListNeverLeaksGitPassword`：
  - 用例① `/framework/list.shtml?curPage=1&pageSize=10` → `data.records[*]` 字段集合 ⊆ `{id, code, name, description, gitHome, account, isPublic}`；显式断言不含 `password` 与 4 个 `*Begin/*End`
  - 用例② `/framework/load.shtml?code={种子code}` → `data` 字段集合 ⊆ 同 7 字段；显式断言不含 `password`
  - 用例③ `contracts/openapi/openapi.yaml` 的 `components.schemas` SHALL NOT 含 `FrameworksPageVO`（旧形态残留检查）
  - 验证：`./aicode/gradlew :aicode:test --tests "*ApiContractIT*FrameworkListNeverLeaksGitPassword"` 全绿
  *(已完成：3/3 通过。**实施期发现**：容器 `PageVO<VO> extends BaseVO`，故 `data`（容器层）本身含 4 个 `*Begin/*End` —— 这是**全仓库所有分页端点的共性问题**（`/project/list`、`/account/list` 同样），与 `records[*]`（元素层）是两个不同层面。断言已按 spec 语义收敛到"元素层不含 BaseVO 字段"；容器层的共性问题记入本变更 design 的 Open Questions，不在本变更 scope)*
- [x] 2.2 **反向验证 A**：临时把 `load` 的返回改回实体直出（`R.success(frameworks)` + 强制类型转换）→ 用例③ SHALL 失败且错误明确指出 `password` 是多余字段；恢复后通过 *(已完成：错误信息精确列出 `实际=[account, code, description, gitHome, id, isPublic, name, password]`)*
- [x] 2.3 **反向验证 B**：临时把 `list` 的容器改回 `com.aicode.core.Page<...>` + `setVoList` → 用例① SHALL 失败（`records` 缺失 / `voList` 多余）；恢复后通过 *(实施调整：`PageVO` 无 `voList` 字段，最小化注入需改动 `common` 的 `PageVO` 类（影响**全仓库所有分页端点**），超出单端点反向验证范围。故反向验证 B 改为**正向断言 + 契约层断言**：用例① 已含 `data.has("records") && !data.has("voList")` 双断言，该组合即为容器形态的守护线。如需更强反向验证，应另开"PageVO 全仓收敛"变更)*

## 3. 契约产物与前端重生成

- [x] 3.1 跑 `./aicode/gradlew :aicode:test --tests "*OpenApiExportIT"`；验证 `contracts/openapi/openapi.yaml` 中：
  - `/framework/list` 响应 schema ref 由 `RPageFrameworksPageVO` 改为含 `FrameworkSummaryVO` 的分页容器展开形态
  - `/framework/load` 响应 schema ref 由 `RFrameworks`（实体）改为 `RFrameworkSummaryVO`
  - `components.schemas` 中 `FrameworksPageVO` 已消失、`FrameworkSummaryVO` 出现
  *(已完成：`grep -c FrameworksPageVO` = 0；`grep -c FrameworkSummaryVO` = 9；`FrameworkSummaryVO` schema 精确 7 字段无 password)*
- [x] 3.2 跑 `cd aicode-web && npm run generate`；验证 `frameworkSummaryVO.ts` / `rFrameworkSummaryVO.ts` / `pageVOFrameworkSummaryVO.ts`（或等价命名）生成出现；`frameworksPageVO.ts` 不再生成
  *(已完成：4 个新文件生成；手工清 4 个 stale 文件 + `model/index.ts` 中 4 行 stale `export *`)*
- [x] 3.3 确认前端生成类型不含凭据字段：`grep -r "password" aicode-web/src/generated/model/framework*.ts aicode-web/src/generated/model/rFramework*.ts` 应无输出（除非其他端点如 `/login/signin` 的入参含 password，那是入参不是出参，允许）
  *(已完成：`frameworkSummaryVO.ts` 无 password；含 password 的 5 个文件全是 design 已记的 Open Question：`accountVO` / `accountSaveVO` / `frameworks`（实体，modify 与 updateIsPublic 仍直出）/ `projectRepositoryAccount` / `signinParams`（入参）)*
- [x] 3.4 跑 `npm run typecheck` 与 `npm test`：预期 80 用例 / 0 失败（M5-A2 未接 `/framework/*`，前端无消费方，生成物变化不应破坏现有测试）
  *(已完成：typecheck exit=0；17 文件 / 80 用例通过；build 通过 336 KB / gzip 107 KB)*

## 4. 验证 + 文档 + 登记

- [x] 4.1 跑全套 `./aicode/gradlew :aicode:test`：预期 176 + 3 = 179 用例 / 0 失败（场景⑩ 3 用例新增）
  *(已完成：179 用例 / 1 跳过 / 0 失败。**实施期发现**：既有场景①（标识符序列化）与场景⑤（null 字段保留）也查 `/framework/list`，其断言读 `data.voList` —— 容器收敛后必然变红。已同步改为 `records`；`firstRecord()` helper 改为同时识别 `records` / `voList` 两种容器名（向前兼容）。场景⑤ 的字段清单从 8 字段（含 `password`）收窄为 7 字段 —— 凭据字段不参与 null 保留规则，见 `api-data-object` 新增 Requirement「凭据字段 SHALL NOT 出现在任何响应」*)*
- [x] 4.2 修改 `AGENTS.md §11.1 安全加固`：登记「Git 仓库密码曾通过 `/framework/list` 与 `/framework/load` 明文泄漏到前端，已由 `2026-10-18-framework-list-vo-contract` 堵住；历史泄漏无法追溯，建议轮换模板仓库凭据」
  *(已完成：登记 3 条遗留 —— ① modify/updateIsPublic 仍直出实体；② PageVO extends BaseVO 的全仓共性问题；③ 历史泄漏建议轮换凭据)*
- [x] 4.3 执行 `openspec validate --strict 2026-10-18-framework-list-vo-contract`：校验通过 *(已完成：`Change is valid`，0 错)*
- [x] 4.4 同步两个 delta spec 到主 spec 并 `openspec archive`；验证：
  - `api-data-object` 新增 2 Requirement（凭据字段不出现在响应 / 继承 BaseVO 的 VO 不带查询辅助字段），requirements 现状 6 → 8
  - `api-contract` 新增 1 Requirement（分页接口统一用 PageVO<T>），requirements 现状 11 → 12
  *(已完成：主 spec 物化；`api-data-object` 8 / `api-contract` 12；`openspec validate --specs` 13/13 通过；变更 mv 至 archive；`openspec list` No active changes)*
