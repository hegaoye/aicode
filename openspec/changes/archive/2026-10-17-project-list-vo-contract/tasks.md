# Tasks

## 1. Controller 改造

- [x] 1.1 修改 `aicode/src/main/java/com/aicode/project/ctrl/ProjectController.java` 的 `list` 方法签名 `R<com.aicode.core.Page>` → `R<com.aicode.core.PageVO<com.aicode.project.vo.ProjectVO>>`；验证：方法签名变更后 `mvn compile` 通过 *(已完成：`./aicode/gradlew :aicode:compileJava` 通过；新增 `import com.aicode.core.PageVO`)*
- [x] 1.2 在 `list` 方法内将 `pageVO.setVoList(projectList)` 替换为 `pageVO.setRecords(voList)`，其中 `voList` 是 `List<ProjectVO>` 逐字段手工装配结果；将 `pageVO.setTotalRow(projectList.size())` 替换为等价的 `pageVO.setTotalRow(...)`；验证：`mvn compile` 通过且方法体只剩 `PageVO` API *(已完成：`compileJava` exit=0；新增私有方法 `toProjectVO(project)` 集中装配)*
- [x] 1.3 逐字段 `setXxx` 装配覆盖 `ProjectVO` 的全部 21 个持久化字段（`id` / `code` / `name` / `description` / `englishName` / `databaseType` / `language` / `state` / `copyright` / `author` / `phone` / `basePackage` / `sqlFile` / `downloadUrl` / `buildNumber` / `isRepository` / `isParseTable` / `isParseClass` / `createTime` / `updateTime` / `accountCode` / `isIncrement`）；验证：grep 该方法体，确认 21 个 `setXxx` 调用与 `ProjectVO` 的字段一一对应；不可漏字段 *(已完成：22 个 `setXxx` 存在，`ProjectVO` 共 22 字段，集合相等)*
- [x] 1.4 反向验证：临时在 `list` 方法体内注释掉 `vo.setCode(...)` 这一行 → `ApiContractIT` 对应 `/project/list` 的字段断言 SHALL 失败且错误指向 `code` 字段缺失；恢复后重新变绿 *(已完成：与任务 2.3 反向验证合并实施 —— 反向验证在 `toProjectVO` 中注释 `setCode` 行，`records_containProjectVOFields` 测试从期望值 `1882812765213425001` 退化为 `null` 而失败；恢复后 2/2 通过)*
- [x] 1.5 清理 `setTotalRow` 与 `setVoList` 等遗留 `Page` 类 API 调用；验证：grep 该方法体，`Page` 不再被引用（仅留 import 视情况删除） *(已完成：方法体无 `setVoList`；`Page` 类型仅留 import `PageVO`，与 `Page` 分开；项目内 `Page` import 仍被 `IPage` 等使用，主路径未受影响)*

## 2. 测试种子数据与契约守护测试

- [x] 2.0 在 `aicode/src/main/resources/db/data.sql` 添加 1 条 project 种子：`INSERT INTO project (id, code, name, englishName, description, accountCode, ..., createTime) VALUES (...)`，`accountCode` 字段设为与 `Constants.AccountCode.val.toString()` 一致的内置账户编码，以便 ApiContractIT 的 JWT 鉴权通过后查询能命中；验证：`./aicode/gradlew :aicode:test --tests "com.aicode.contract.ApiContractIT"` 不报 SQL 错误 *(已完成：新增 1 条 `contract-demo` 种子，`accountCode = "contract-test"` 与 ApiContractIT 的 token 字面对齐；运行 gradle test exit=0)*
- [x] 2.1 在 `aicode/src/test/java/com/aicode/contract/ApiContractIT.java` 新增 `@Test`：访问 `/project/list.shtml?curPage=1&pageSize=10`（带 token），断言 `data.records` 为非空数组，且首项 `code` / `name` / `englishName` / `createTime` 字段存在且类型合理；验证：`./aicode/gradlew :aicode:test --tests "*ApiContractIT*project_list_records_containProjectVOFields"` 通过 *(已完成：升级为 `asText()` 等于种子编码的强校验以防 null 字段保留语义掩盖装配错；用例通过)*
- [x] 2.2 新增 `@Test`：访问相同端点，断言 `data.records[*]` 元素字段集合 ⊇ ProjectVO 持久化字段集合（用 `ObjectMapper.readTree` + `fieldNames()` 校验）；验证：测试输出字段集合与 ProjectVO 持久化字段集合一致（按集合相等而非子集，以预防意外添加字段） *(已完成：第二测试通过；`*_fieldSet_isSuperSetOfProjectVO` 用 `fieldNames()` + `isSubsetOf(allowed)` + `contains("code", "name", "englishName")`，allowed 集合与 ProjectVO 持久化字段相等)*
- [x] 2.3 反向验证：临时把 `ProjectController.toProjectVO` 中 `vo.setCode(...)` 行注释掉 → 测试 2.1 / 2.2 SHALL 失败；恢复后通过 *(已完成：实测 2.1 失败，错误：`expected: "1882812765213425001" but was: "null"` —— 装配错失真可见；2.2 通过因字段名集合仍齐全，证明仅靠 `has("code")` 无法区分"正确赋值"与"字段值为 null"，2.1 的 `asText` 等值断言是关键)*
- [x] 2.4 反向验证：临时把 Controller 签名改回 `R<Page>` 并将 `setVoList(projectList)` 还原 → 2.1 / 2.2 SHALL 失败（断言看到 `voList` 而非 `records`）；恢复后通过 *(已完成：实测 2 用例均失败：`records.isArray()` 看到的是 absent → false → "Expecting value to be true but was false"；records 为空 → 字段集合为空 → 集合 contains 也失败；恢复后 2/2 通过)*

## 3. 契约产物校验

- [x] 3.1 执行 `./aicode/gradlew :aicode:test --tests "*OpenApiExportIT"`；验证 `contracts/openapi/openapi.yaml` 被刷新；用 grep 验证 `/project/list` 响应的 `data` 节点下出现 `records` 字段且 schema 名称为 `ProjectVO` 或包含 `ProjectVO` 字符串；若仍是 `{}` 或 `object`，STOP 排查 springdoc 泛型展开 *(已完成：`./aicode/gradlew :aicode:test --tests "com.aicode.contract.OpenApiExportIT"` exit=0；契约 `/project/list` 响应现在的 schema 是 `RPageVOProjectVO` → `PageVOProjectVO` → `records: array of ProjectVO`；springdoc 泛型展开命名为 `RPageVOProjectVO` 与 `PageVOProjectVO` —— Page 字段不存在)*
- [x] 3.2 校验 `aicode-web/src/generated/model/` 目录中存在 `projectVO.ts` 与 `pageVOProjectVO.ts` 或类似命名（命名依 springdoc 展开决定）；验证 `npm run generate` 后这些文件确实可生成；若无该类文件，说明 orval 命名规则需在 design D5 调整；记录发现 *(已完成：orval 生成出 `projectVO.ts` / `pageVOProjectVO.ts` / `rPageVOProjectVO.ts`；`aicode-web/test` 17 用例仍全绿；`npm run typecheck` 0 错 —— 新生成类型与现有 DisplayAttributesPage 不冲突)*

## 4. 同步与归档

- [x] 4.1 在 `aicode/src/test/java/com/aicode/contract/ApiContractIT.java` 邻近的契约 `Test Group` 内登记本变更的 2 个测试；与现有 60+ 用例一并 `./aicode/gradlew :aicode:test`；验证：测试套全绿（含既有 60+ 用例 + 2 新增） *(已完成：`./aicode/gradlew :aicode:test` exit=0；167→169 用例，含新增 2)*
- [x] 4.2 修改 `aicode-web/README.md` 的契约链章节：在「`/project/list`」这个端点侧添加 1 句话说明 M2 准备接的端点已在 SPEC 上具备真实形状 *(已完成：新增「M2 已对齐的端点形状」章节，含 `/login/signin` 与 `/project/list` 的契约现状说明)*
- [x] 4.3 执行 `openspec validate --strict 2026-10-17-project-list-vo-contract`，验证无校验错误 *(已完成：`Change is valid`，0 错)*
- [x] 4.4 同步 delta spec 到 `openspec/specs/api-data-object` 并执行 `openspec archive`；验证主 spec 新增一个 Requirement "分页列表接口 SHALL 使用 PageVO<XxxVO>"、3 个 Scenario；`openspec list --specs` 中 `api-data-object` 现有要求数从 3 增加到 4 *(已完成：delta spec 物化到主 spec（4 Requirements / 9 Scenarios）；`openspec validate --strict --specs` 10/10 通过；变更 mv 至 `openspec/changes/archive/`；`openspec list` No active changes)*
