## 1. 引用扫描与基线

- [x] 1.1 grep 内部 4 个 bug 修复点的所有引用面（ContextConfiguration / ProjectController / MapClassTable / GeneratorSVImpl）
- [x] 1.2 对照 dev 分支确认 4 个端点是 dev 保留的（downloadFile / loadByCode），属于"误删"恢复
- [x] 1.3 跑基线 `./gradlew :aicode:test` 确认 51 + 29 = 80 个用例全过

## 2. 登录 + 静态资源白名单

- [x] 2.1 `ContextConfiguration.excludePathPatterns` 扩展为 `/login/**` 覆盖 `.shtml` 后缀
- [x] 2.2 同一列表新增 `/assets/**` + `/*` + `/**/*` + 9 个多段后缀（`/**/*.png` 等）
- [x] 2.3 编译通过

## 3. NPE 修复（classModel 自愈 + Objects.equals）

- [x] 3.1 `MapClassTable.getClassModel()` 撤销 P0-4 纯 getter，改为懒计算自愈
- [x] 3.2 `GeneratorSVImpl:544` 改 `Objects.equals(...)` 防御 NPE
- [x] 3.3 编译通过

## 4. 下载 9999 修复

- [x] 4.1 恢复 `ProjectController.downloadFile`（dev 版本）
- [x] 4.2 恢复 `ProjectController.loadByCode`（dev 版本）
- [x] 4.3 `ContextConfiguration.excludePathPatterns` 加 `/project/download/**` + `/project/load/code/**`
- [x] 4.4 编译通过

## 5. 回归测试（14 个新用例全过）

- [x] 5.1 `LoginCtrlShtmlPathTest`（3 用例）覆盖 `/login/signin.shtml` + `/login/signin` + `/login/signin/anything` 子路径
- [x] 5.2 `MapClassTableTest`（5 用例）覆盖 `toJava` 路径 / classModel=null 自愈 / tableName=null 边界
- [x] 5.3 `ProjectControllerDownloadTest`（6 用例）覆盖 downloadFile / loadByCode 方法 + 注解 + @GetMapping 路径契约

## 6. 完整验证

- [x] 6.1 `./gradlew :aicode:test` 全过（80 个用例 = 51 aicode + 29 common）
- [x] 6.2 `./gradlew clean build` SUCCESS（13 个任务）
- [x] 6.3 改动只落在 `feature/20260622/delta` 分支独有的文件（dev 不动）

## 7. 归档

- [x] 7.1 `openspec new change hotfix-2026-06-22-stability`
- [x] 7.2 写 proposal / design / specs（auth-account + project MODIFIED Requirements）/ tasks
- [x] 7.3 `openspec archive` 归档到主 specs/

## 8. 后续（不在本 change 范围）

- [ ] 8.1 `downloadFile` 的 IO 改 NIO（独立 PR）
- [ ] 8.2 `IndexCtrl` 是否能删（独立 PR，保守保留）
- [ ] 8.3 真把 `downloadFile` / `loadByCode` 删除（独立 PR，需先确认前端无引用）
- [ ] 8.4 提交 + push 当前分支（git 流程）
