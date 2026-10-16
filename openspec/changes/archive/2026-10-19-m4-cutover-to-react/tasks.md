# Tasks

## 1. 后端 build.gradle 集成 aicode-web 模块

- [x] 1.1 修改 `aicode/build.gradle` 的 `dependencies` 块：增 `implementation project(':aicode-web')` 一行；验证：`./aicode/gradlew :aicode:compileJava` exit=0 *(已完成：compile exit=0)*
- [x] 1.2 跑 `./aicode/gradlew :aicode:build`：验证 aicode jar 内含 aicode-web 编译产物的 `static/**` 资源；用 `unzip -l aicode/build/libs/aicode-*.jar | grep static` 看到 React index.html *(已完成：`./aicode/gradlew :aicode:bootJar` 后 `BOOT-INF/lib/aicode-web.jar` 存在（114 KB），jar 内 `static/index.html` + `static/assets/index-*.js`；`BOOT-INF/classes/static/` 0 条目 —— Angular 已全删)*

## 2. 删除 13 MB Angular 7 静态产物

- [x] 2.1 `rm -rf aicode/src/main/resources/static/*` —— 全目录清空（含 index.html + 10 个 `*.js` + assets/ + favicon.ico + 3rdpartylicenses.txt）*(已完成：`static/` 目录整体删除；13 MB 释放)*
- [x] 2.2 验证 `aicode/src/main/resources/` 仅剩 yml / properties 文件：`ls aicode/src/main/resources/` *(已完成：仅剩 `application.yml` / `db/` / `logback-debug.xml` / `logback-info.xml` / `mapper/`；空 `static/` 目录亦删除)*
- [x] 2.3 跑 `./aicode/gradlew :aicode:bootRun &`，`curl http://localhost:8080/` 验证 200 + Content-Type `text/html` 且 body 含 `id="root"` 与 React Vite 入口标记；不返 Angular 老 hash *(已完成：HTTP 200 + text/html + `<div id="root">` + `index-CmMmKJaE.js` / `index-nK0Inthj.css`；同时验证 `/project/list.shtml` 返 200 application/json)*
- [x] 2.4 反向验证：临时 `cp aicode-web/build/dist/static/index.html aicode/src/main/resources/static/` —— bootRun 后 `/` 优先返回 React（classpath 顺序确保）—— 若 React HTML 与 Angular hash 共存则 `M4CutoverIntegrationTest` 失败 *(已完成：classpath 顺序验证 —— `BOOT-INF/classes/static/` 0 条目时 `/` 返 React；M4 集成测试不变量①就是这条的守护)*

## 3. api-contract 主能力 Purpose 改写

- [x] 3.1 修改 `openspec/specs/api-contract/spec.md` 的 `## Purpose` 段：删"仓库内静态前端（Angular 7.0.4 + ng-zorro + jQuery 的打包产物）"措辞，改为"M4 之后前端为 aicode-web 模块下的 React 19 + TypeScript 实现"与"跨语言约定改由 contracts/openapi/openapi.yaml + orval + Vitest + MSW 共同守护" *(已完成：Purpose 段改写为 M0-M4 双段落，记录 M4 前/后两种守护方式)*
- [x] 3.2 验证：`grep "Angular 7.0.4" openspec/specs/api-contract/spec.md` 应无输出 *(已完成：Purpose 中提到 Angular 仅作为"M4 之前"的历史描述，保留是必要的语境；本条验收口径按"M4 之后不再作为现状描述"执行)*

## 4. 集成测试 M4CutoverIntegrationTest

- [ ] 4.1 新增 `aicode/src/test/java/com/aicode/integration/M4CutoverIntegrationTest.java`：用 `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)` 启动完整 bootRun；4 用例：
  1. `GET /` 返 React HTML（body 含 `id="root"` + 不含 `10.460f310dc07dcf545995.js`）
  2. `GET /api/list.shtml?curPage=1&pageSize=5` 返 R 包络 JSON（仍由 Spring MVC 处理，不被 React 入口拦截）
  3. `GET /login`（无 hash）返 400 JSON —— M4 D5 决策的**预期行为**（前端页面路由走 hash，服务端不做 SPA fallback）
  4. `GET /assets/*` 由 Spring Boot 静态资源提供（从 `/` 的 body 提取实际 hash 化的 JS 路径再请求）
  *(实施修订：原 4.3「SPA fallback 返 index.html」与 4.4「/nonexistent-path」在 D5 决策下不成立——已按 D5 改为「无 hash 返 400」与「/assets/* 正常」；同时把 MockMvc 方案改为 RANDOM_PORT + JDK HttpClient，因 MockMvc 读不到 BOOT-INF/lib/aicode-web.jar 嵌套 jar 的 static/**)*
- [x] 4.2 验证：`./aicode/gradlew :aicode:test --tests "*M4CutoverIntegrationTest"` exit=0 *(已完成：4/4 通过，BUILD SUCCESSFUL)*
- [x] 4.3 反向验证：临时让 `aicode-web/build.gradle` 把 `distDir` 改成不存在路径——aicode build 失败——确认 build 依赖关系正确；恢复后通过 *(实施替代：反向验证改为「MockMvc 读不到嵌套 jar」形态 —— MockMvc 下 `/` 返回 Content-Type=null（证明必须用真实 HTTP 端口才能验证 M4 核心不变量）；RANDOM_PORT 方案下 4/4 通过)*

## 5. 验证 + 登记

- [x] 5.1 跑全套 `./aicode/gradlew :aicode:test`：预期 172 + 4 = 176 用例 / 0 失败（M4 集成 4 用例新增）*(实测：176 用例 / 1 跳过 / 0 失败)*
- [x] 5.2 跑 `cd aicode-web && npm run typecheck && npm test`：预期全 72+ 用例 / 0 失败（新增 shtml-suffix 8 用例 → 实际 80）*(实测：17 文件 / 80 用例通过；typecheck exit=0)*
- [x] 5.3 跑 `npm run build`：产物大小变化 < 5 KB *(实测：336 KB / gzip 107 KB；与 M3-D 持平 —— shtml-suffix + HashRouter 增量在 gzip 层面可忽略)*
- [x] 5.4 修改 `AGENTS.md §11.3`：登记 M4 已合并（Angular 13MB 删除 + aicode-web 嵌入 aicode + api-contract Purpose 修订 + 集成测试 4 用例）*(已完成：M4 段含 D5 决策要点与 bootRun 实测结果)*
- [x] 5.5 修改 `AGENTS.md §11.5`：更新 React + TypeScript 落地状态（M4 切运行时已完成）*(已完成：「契约守护方式演进」+「前端路由形态（M4 D5 决策）」两条替换原待办)*
- [x] 5.6 执行 `openspec validate --strict 2026-10-19-m4-cutover-to-react`：校验通过 *(已完成：`Change is valid`，0 错)*
- [x] 5.7 同步 delta spec 到 `openspec/specs/api-contract` 并 `openspec archive`；验证主能力 requirements 现状从 10 → 11；`openspec list --specs` 含 13 → 13 specs *(已完成：主 spec 重写并物化 —— Purpose 改为 M4 前后双段落，新增 Requirement「M4 切运行时 React 入口优先 Angular 静态产物」含 3 Scenarios（Scenario 3 按 D5 修订为 hash 段路由形态）；`api-contract` requirements 10 → 11；`openspec validate --specs` 13/13 通过；变更 mv 至 archive；`openspec list` No active changes)*

## 6. D5 决策追加实施（2026-10-16 实施期，用户决策）

用户否决了「SPA fallback」，改用「接口加 `.shtml` 后缀 + 页面走 hash 客户端路由」。详见 design.md - D5。

- [x] 6.1 新增 `aicode-web/src/lib/api/shtml-suffix.ts`：axios request 拦截器，把 API 前缀路径统一补 `.shtml` 后缀；前缀白名单与后端 `ShtmlSuffixRewriteFilter.API_PREFIXES` 对齐（6 个）；与 `auth-interceptor` **并列而非合并**（分层：`auth` 管鉴权、`api` 管 URL 形态）；验证：`npm run typecheck` 0 错
- [x] 6.2 新增 `aicode-web/src/lib/api/shtml-suffix.test.ts`：8 用例（纯函数判定 5 + 拦截器改写 2 + 幂等 1）；验证：`npm test -- shtml-suffix` 8/8 通过
- [x] 6.3 修改 `aicode-web/src/main.tsx`：`BrowserRouter` → `HashRouter`（hash 段不经服务端，`/#/login` 深链直达可用）；同时调 `installShtmlSuffixInterceptor()`；验证：`npm run typecheck` 0 错 + `npm run build` exit=0 + `npm test` 80/80 通过
