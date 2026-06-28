## Why

在 `feature/20260622/delta` 上线验证期间，dev 分支正常的几个核心业务流程在优化后分支出现运行时异常：登录入口返回 9007、Angular 静态资源加载 0 字节、代码生成主链路抛 NPE、项目源码下载返回 9999。本 change 在保留此前 P0/P1/P2/P3/P4 + add-core-business-tests 累计 24 项优化基础上，回退 / 修复 4 个回归，确立"稳定基线"。

## What Changes

- **登录入口回退**：`excludePathPatterns` 从 `["/login/signin", "/login/reg"]` 扩展为 `["/login/**"]`，覆盖前端的 `.shtml` 后缀调用（`/login/signin.shtml`）。
- **静态资源白名单扩展**：新增 `/assets/**` + `/*` + `/**/*` + 9 个多段后缀模式（`/**/*.png` 等），覆盖 Angular 子目录静态资源（`/assets/monaco/...` 等）。
- **代码生成 NPE 修复**：`MapClassTable.getClassModel()` 撤销 P0-4 纯 getter 改造，改为懒计算自愈（classModel=null 时按 tableName 推算）；`GeneratorSVImpl:544` 改 `Objects.equals` 防御 DB 脏数据。
- **项目下载 9999 修复**：恢复 `ProjectController.downloadFile` 与 `loadByCode` 两个 @Deprecated 端点（被 `remove-deprecated-unused-apis` 误删）；`excludePathPatterns` 同步加 `/project/download/**` 与 `/project/load/code/**`。
- **回归测试**：新增 `LoginCtrlShtmlPathTest` (3) / `MapClassTableTest` (5) / `ProjectControllerDownloadTest` (6) 三个测试类，共 14 个用例覆盖 4 个修复点。

## Capabilities

### New Capabilities

无。所有修复都属于现有 spec 的 requirement 调整或实现细节。

### Modified Capabilities

- `project`: 恢复 `downloadFile` 与 `loadByCode` 端点（dev 分支保留的兜底）；`LoginInterceptor` 白名单排除 `/project/download/**` + `/project/load/code/**` 防止 static resource handler 接管。
- `code-gen-pipeline`: `MapClassTable.getClassModel()` 自愈（DB 脏数据场景下 classModel=null 时按 tableName 懒计算）；`GeneratorSVImpl:544` 用 `Objects.equals` 防御 NPE。
- `auth-account`: `LoginInterceptor` 白名单扩展为 `/login/**` 覆盖前端 `.shtml` 后缀调用；新增 `assets/**` 排除规则覆盖 Angular 静态资源。

## Impact

- **生产代码改动**（commit `292b429`）：
  - `aicode/src/main/java/com/aicode/filter/ContextConfiguration.java`（+27 / -9 行）
  - `aicode/src/main/java/com/aicode/project/ctrl/ProjectController.java`（+73 行）
  - `aicode/src/main/java/com/aicode/project/service/GeneratorSVImpl.java`（+4 行）
  - `facade/src/main/java/com/aicode/map/entity/MapClassTable.java`（+7 行）
- **测试代码新增**（commit `50db4de`，共 315 行）：
  - `aicode/src/test/java/com/aicode/session/ctrl/LoginCtrlShtmlPathTest.java`
  - `aicode/src/test/java/com/aicode/map/entity/MapClassTableTest.java`
  - `aicode/src/test/java/com/aicode/project/ctrl/ProjectControllerDownloadTest.java`
- **dev 分支影响**：零。修改文件均为当前分支独有变更；dev 的 `ProjectController` 本就有 `downloadFile` + `loadByCode` 端点，`ContextConfiguration` 未挂载 LoginInterceptor，`MapClassTable` 保留原副作用 setter 版本。
- **外部 API 变化**：零。所有修复是恢复 / 防御性调整，不引入新端点、不改契约。
- **测试统计**：80 个测试用例全过（common 29 + aicode 51，新增 14 个回归测试）。
