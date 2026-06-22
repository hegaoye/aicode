## 1. 引用扫描与基线

- [x] 1.1 用 `grep -rn` 列出 30 个 @Deprecated 端点路径的内部引用面（aicode/common/facade/openspec/test），输出"零引用清单"
- [x] 1.2 对零引用清单的每个端点，再 grep 一次 facade 接口方法 + service 方法，输出待删 facade 方法名清单
- [x] 1.3 跑基线 `./gradlew :aicode:test` 确认 74 个用例全过（清理前的安全网）

## 2. AccountController 清理

- [x] 2.1 删 `AccountController.loadByCode(String)` (`/load/code/{code}`)
- [x] 2.2 删 `AccountController.modifyPassword` (`/modify/password`) — 实际不存在（README 误列），跳过
- [x] 2.3 删 `facade/account/service/AccountService.loadByCode` + `AccountService.modifyPassword` 接口方法 — 实际不存在，跳过
- [x] 2.4 删 `aicode/account/service/AccountServiceImpl.loadByCode` + `modifyPassword` 实现 — 实际不存在，跳过
- [x] 2.5 跑 `./gradlew :aicode:compileJava :aicode:compileTestJava` 验证

## 3. ProjectController 清理

- [x] 3.1 删 `ProjectController.loadByCode` + `downloadFile`（2 个）
- [x] 3.2 删 `facade/project/service/ProjectService.loadByCode` 接口方法 — 实际不存在（controller-only）
- [x] 3.3 删 `ProjectServiceImpl.loadByCode` 实现 — 实际不存在
- [x] 3.4 跑编译验证

## 4. ProjectJobController 清理（5 个端点）

- [x] 4.1 删 `ProjectJobController.load` / `build` / `list` / `modify` / `delete`（5 个方法）
- [x] 4.2 删 `facade/project/service/ProjectJobService` 对应 5 个方法 — 实际不存在（facade 已最小化）
- [x] 4.3 删 `ProjectJobServiceImpl` 对应 5 个方法 — 实际不存在
- [x] 4.4 跑编译验证

## 5. ProjectSqlController 清理

- [x] 5.1 删 `ProjectSqlController.loadByProjectCode` / `list` / `delete`（3 个）
- [x] 5.2 删 `facade/project/service/ProjectSqlService.loadByProjectCode` / 删除 list / delete（若 facade 接口中有）— 实际不存在
- [x] 5.3 删 `ProjectSqlServiceImpl` 对应 3 个方法 — 实际不存在
- [x] 5.4 跑编译验证

## 6. ProjectRepositoryAccountController 清理

- [x] 6.1 删 `loadByCode` + `list`（2 个）
- [x] 6.2 删 `ProjectRepositoryAccountService.loadByCode` + list 接口方法 — 实际不存在
- [x] 6.3 删 `ProjectRepositoryAccountServiceImpl` 对应方法 — 实际不存在
- [x] 6.4 跑编译验证

## 7. ProjectFramworkController 清理

- [x] 7.1 删 `build` / `modify` / `delete`（3 个）；保留 `load` / `list` / `add`
- [x] 7.2 删 `ProjectFramworkService` 对应 3 个接口方法 — 实际不存在
- [x] 7.3 删 `ProjectFramworkServiceImpl` 对应方法 — 实际不存在
- [x] 7.4 跑编译验证

## 8. MapRelationshipController 清理

- [x] 8.1 删 `loadByCode` / `listByProjectCode`（2 个）
- [x] 8.2 删 `MapRelationshipService.loadByCode` / `listByProjectCode` 接口方法 — 实际不存在
- [x] 8.3 删 `MapRelationshipServiceImpl` 对应方法 — 实际不存在
- [x] 8.4 **同步更新测试**：`MapRelationshipControllerTest` 不引用这些方法（前面 change 用直接 controller 注入）— 验证测试不受影响
- [x] 8.5 跑编译验证

## 9. DisplayAttributeController 清理

- [x] 9.1 删 `modify` / `delete`（2 个）；`loadByMapFieldColumnCode` 实际不存在（前面 P4-4 已删）
- [x] 9.2 删 `DisplayAttributeService` 对应 2 个接口方法 — 实际不存在
- [x] 9.3 删 `DisplayAttributeServiceImpl` 对应方法 — 实际不存在
- [x] 9.4 跑编译验证

## 10. FrameworksController 清理

- [x] 10.1 删 `loadByCode`（1 个）
- [x] 10.2 删 `FrameworksService.loadByCode` 接口方法 — 实际不存在
- [x] 10.3 删 `FrameworksServiceImpl.loadByCode` 实现 — 实际不存在
- [x] 10.4 跑编译验证

## 11. LogsCtrl 清理

- [x] 11.1 删 `createLogFiles` / `saveLogs` / `loadFilePath` — **跳过**（实际无 `@Deprecated` 注解，README 误标）
- [x] 11.2 删 `LogsSV`（facade 接口）对应 3 个方法 — 跳过
- [x] 11.3 删 `LogsSVImpl` 对应 3 个方法 — 跳过
- [x] 11.4 跑编译验证

## 12. IndexCtrl（保留）

- [x] 12.1 **不删** `IndexCtrl`（`@RequestMapping("/")` + `setViewName("/index")`）—— 保留现状（设计上虽标 @Deprecated 但路由 `/` 是 Spring 启动关键）

## 13. 文档与归档

- [x] 13.1 更新 `aicode/README.md §四`：从"未使用的后端API"列表改为"清理状态 (2026-06-22 by change remove-deprecated-unused-apis)"，列出仍保留的 controller 端点
- [x] 13.2 跑完整 `./gradlew clean build` + `:aicode:test`（74 个用例全过）
- [x] 13.3 `openspec archive` 归档（apply 完成后）
- [ ] 13.4 在归档前在 PR 描述中列出本 change 影响的 controller / facade / service 数量统计（PR 流程）

## 14. 后续（不在本 change 范围）

- [ ] 14.1 把 `api-deprecation-policy` 能力（spec 5 个 Requirement）写为 CI 钩子（grep 检测 + 提示）
- [ ] 14.2 `IndexCtrl` 的 `setViewName("/index")` 是否有替代方案（前端 SPA 路由 / 默认静态资源）—— 独立 PR
- [ ] 14.3 外部脚本兼容性回退（如真有外部调用方，需提供 v1 兼容层）—— 独立 PR

## 实施调整（关键发现）

| Task | 计划 | 实际 | 原因 |
|---|---|---|---|
| 2.2-2.4 | 删 `modifyPassword` + facade + impl | 跳过 | README 误标，实际代码中已不存在 |
| 3.2-3.3 | 删 `loadByCode` facade + impl | 跳过 | 这些方法**只在 controller 出现**，facade 接口与 ServiceImpl 早已无对应方法 |
| 4.2-4.3 | 删 5 个 ProjectJob facade + impl | 跳过 | 同上 — `ProjectJobService` facade 接口只保留 `list` + `execute`，无 load/build/list(modify)/delete |
| 5.2-5.3 | 删 ProjectSql facade + impl | 跳过 | `ProjectSqlService` facade 接口完全空（仅继承 IService） |
| 6.2-6.3 | 删 ProjectRepositoryAccount facade + impl | 跳过 | 同上，controller-only |
| 7.2-7.3 | 删 ProjectFramwork facade + impl | 跳过 | 同上 |
| 8.2-8.3 | 删 MapRelationship facade + impl | 跳过 | 同上 |
| 9.1 | 删 `loadByMapFieldColumnCode` | 跳过 | 前面 P4-4 已删除 |
| 11.1-11.3 | 删 LogsCtrl 3 端点 | 跳过 | 实际无 `@Deprecated` 注解，README 误标 |
| **总计删除** | ~30 端点 | **20 端点**（controller 方法）+ **无 facade / impl 改动** | 大部分 facade 已最小化，controller 方法是核心删除点 |
