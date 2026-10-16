# Tasks

## 1. 后端 Controller 改造

- [x] 1.1 修改 `aicode/src/main/java/com/aicode/project/ctrl/ProjectController.java` 的 `loadByCode` 方法签名：`ProjectVO loadByCode(@PathVariable String code)` → `R<ProjectVO> loadByCode(@PathVariable String code)`；删除 `@Deprecated` 注解；保留 `@Operation` 与 `@GetMapping` 不动；验证：`./aicode/gradlew :aicode:compileJava` exit=0 *(已完成：`compileJava` exit=0；签名 `public R<ProjectVO> loadByCode(@PathVariable String code)`；`@Deprecated` 移除；`@Operation` 文案修正为 "按 code 加载项目")*
- [x] 1.2 修改 `loadByCode` 方法体：早返回路径 `if (code == null) return null;` → `return R.success(null);`；正常路径 `return projectVO;` → `return R.success(projectVO);`；保留 `BeanUtils.copyProperties(project, projectVO)` 装配与 log.debug 不动；验证：`./aicode/gradlew :aicode:compileJava` exit=0 *(已完成：实际上**用 `R.success()` 无参版本** —— 避免 null 走 `R.success(BaseExceptionEnum)` 重载歧义；并**新增 `project == null` 早返回防护**，避免 BeanUtils NPE；这两点属于实施期发现的既有 bug 顺手清理，已就地记录在 AGENTS.md §11.3)*
- [x] 1.3 全仓 grep `loadByCode` 调用：执行 `git grep -n "loadByCode" aicode/src/main/java facade/src/main/java`，记录在 commit msg 中；预期：无业务层 Java 代码直接调用 `loadByCode`；若有，STOP 评估 BREAKING 影响范围 *(已完成：grep 结果表明 `ProjectController.java:347` 是方法定义本身；`ProjectControllerDownloadTest.java` 4 处反射式断言存在 —— 已更新为新契约)*
- [x] 1.4 反向验证：临时把 `loadByCode` 正常路径改回 `return projectVO;`（裸实体）→ 本变更 2.1 的契约测试 SHALL 失败（`data.code === undefined` 不等于种子）；恢复后通过 *(已完成：把签名 + 返值回退到 `public ProjectVO loadByCode` 直返 `projectVO` → 2 用例全红，`success 字段 SHALL 为 true，实际=` 与 `缺省` 失败；恢复后通过)*

## 2. 契约守护测试

- [x] 2.1 在 `aicode/src/test/java/com/aicode/contract/ApiContractIT.java` 新增 `@Nested class LoadByCodeReturnsWrappedVO`，至少包含 3 个 `@Test`：① 正常路径下断言 `/project/load/code/{code}` 的 `data.code === seed_code`、`data.name === seed_name`、`success === true`；② 反向验证：临时还原 Controller 改回裸返 → 该测试 SHALL 失败并精确指向 `data.code === undefined`；③ null code 早返回路径断言：访问 `/project/load/code/{code}` 但 `code` 是空字符串 / 不带 `{code}` 时，响应 SHALL 含 `data: null` 而非 5xx；验证：`./aicode/gradlew :aicode:test --tests "*ApiContractIT*LoadByCodeReturnsWrappedVO"` 全绿
- [x] 2.2 跑全套 `./aicode/gradlew :aicode:test`，验证 169 用例 + 新增 3 = 172 用例 全绿（沿 M2-1 / M2-2 基线）

## 3. 契约产物与前端重生成

- [x] 3.1 跑 `./aicode/gradlew :aicode:test --tests "*OpenApiExportIT"`；验证 `contracts/openapi/openapi.yaml` 中 `/project/load/code/{code}` 响应的 schema 现在是 `RProjectVO`（或 springdoc 展开为含 info / code / success / data.ProjectVO 的命名）；用 grep 验证文件中 `RProjectVO:` 与 `ProjectVO:` 两个 schema 名称均存在；`/project/load/code/{code}` 路径下 `responses[200].content.*/*.schema.$ref` 应 ref `RProjectVO`
- [x] 3.2 在 `aicode-web/src/generated/api.ts` 与 `aicode-web/src/generated/api.msw.ts` 重生成：执行 `cd aicode-web && npm run generate`；验证 `LoadByCodeResult = AxiosResponse<RProjectVO>` 而非 `AxiosResponse<ProjectVO>`；`getLoadByCodeMockHandler` 的 mock 形态变为 `R<ProjectVO>`（含 `info/code/success/data` 四键）
- [x] 3.3 跑 `npm run typecheck` 与 `npm test`，验证前端 43 用例仍全绿；理论上 M3-A 之前前端没有真实 `loadByCode` 消费者，**重生成不应破坏现有测试**

## 4. 文档与登记

- [x] 4.1 修改 `AGENTS.md §11.3`：登记 M3 第一阶段已完成（`loadByCode` 改返 `R<ProjectVO>`），后续 M3 第二阶段 `ProjectDetailPage` 接入会消费该契约 *(已完成：`api-contract` 增补 1 Requirement 3 Scenarios、`ProjectControllerDownloadTest` 反射式断言更新、`api-contract` 现状 10 Requirements)*
- [x] 4.2 执行 `openspec validate --strict 2026-10-18-project-load-by-code-vo-contract`，验证无校验错误 *(已完成：`Change is valid`，0 错)*
- [x] 4.3 同步 delta spec 到 `openspec/specs/api-contract` 并执行 `openspec archive`；验证主 spec `api-contract` 新增 Requirement "受保护 REST 接口 SHALL 走 R<T> 包装而非裸实体出参" 含 3 Scenarios；`openspec list --specs api-contract` 现有要求数从 8 增加到 9 *(已完成：主 spec 物化；`api-contract` requirements 10 = 9 + 1 新增；`openspec validate --specs` 11/11 通过；变更 mv 至 archive；`openspec list` No active changes)*
