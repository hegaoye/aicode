# Proposal

## Why

M3 整组（M3-A / M3-B / M3-C / M3-D）已落地，React 前端业务页面（详情 / 构建日志 / 字段裁切 / 账户信息）**功能完整**。但**生产运行时未切换**：

- `aicode-web/build/dist/static/` 已有 React 19 产物，但 `aicode/build.gradle` **未**声明 `implementation project(':aicode-web')`——后端 jar 不含 `aicode-web` 模块
- 13 MB Angular 7 静态产物仍在 `aicode/src/main/resources/static/`——M0 README 备注的"M4 删除"未执行
- 启动 `./gradlew :aicode:bootRun` 时，`/` 命中 Angular `index.html`（旧 jQuery + ng-zorro），而非 React；前端业务**仅**在 `npm run dev`（Vite 独立 dev server）下可见
- `api-contract` 主能力 Purpose 文字写死「仓库内静态前端（Angular 7.0.4 + ng-zorro + jQuery 的打包产物）」与「所有跨语言约定必须由后端以可执行测试的方式显式守护」——AGENTS §11.5 明确"M4 SHALL 对 `api-contract` 的 Purpose 做 MODIFIED"，因为跨语言约定将改由「`contracts/openapi/openapi.yaml` 契约文件 + orval 生成类型 + Vitest 跑 MSW mock」共同守护

**M4 = 切运行时**——使 `./gradlew :aicode:bootRun` 启动后，`/` 命中 React，`/api/*` 走 Spring MVC 后端 API；13 MB Angular 产物删除；`api-contract` Purpose 文字修订。

## What Changes

- **后端 `aicode/build.gradle`**：在 `dependencies` 块加 `implementation project(':aicode-web')`。M0 README 备注 + aicode-web/build.gradle 自带注释都明确："产物是一个内部布局为 `static/**` 的 Jar"——`implementation` 即可让 Spring Boot 从 classpath `/static/` 提供页面。
- **删除 `aicode/src/main/resources/static/` 13 MB Angular 7 产物**：10 个 `*.js`（`10.460f.../3rdpartylicenses.txt/5.650.../...`） + `assets/` + `favicon.ico` + `index.html`（`M0 README` 中描述为"Angular 7 引导页"）。M4 后 `/` 唯一来源是 `aicode-web/build/dist/static/index.html`。
- **`api-contract` 主能力 Purpose 修订**：把"仓库内静态前端（Angular 7.0.4 + ng-zorro + jQuery 的打包产物）"与"所有跨语言约定必须由后端以可执行测试的方式显式守护"两段改写为"M4 后由 `contracts/openapi/openapi.yaml` 契约文件 + orval 生成类型 + Vitest 跑 MSW mock 共同守护"。
- **`api-contract` 新增 Scenario**："M4 切运行时 `/` 命中 React 而非 Angular"——对 Spring Boot 资源 locations 优先级硬约束（`aicode-web/build/dist/static/` 应在 `aicode/src/main/resources/static/` 之前被加载），用集成测试保证。
- **`ShtmlSuffixRewriteFilter` 与 `LoginCtrlShtmlPathTest` 保留**——M0 已落地，作用 `/api/list.shtml` → `/api/list` 重写；与 React 无关，M4 切运行时仍需兼容老链路（Angular 老路径暂不支持，但 `.shtml` 是 ApiContractIT 守的门面，删去会失契约层防线）。
- **集成测试 `M4CutoverIntegrationTest`（新文件）**：用 `@SpringBootTest(webEnvironment = RANDOM_PORT)` 启动完整 Spring Boot + aicode-web jar 嵌入；模拟浏览器 GET `/` 拿 HTML 响应，断言：① 响应 200 + Content-Type `text/html`；② HTML body 含 React 入口（`<div id="root">`——aicode-web Vite 默认入口）与 buildNumber hash 字符；③ 响应不包含 Angular 老产物（`10.460f310dc07dcf545995.js`）；④ 同时 GET `/api/list.shtml?curPage=1&pageSize=5` 仍走 Spring MVC 返回 `R<...>` JSON 不被 React 入口拦截。
- **`AppShell` / `App.tsx` 不变**——React 路由表 M2-2 已建，`/` 路径被 React `<Route path="/" element={...} />` 占位；M4 切运行时后端静态资源 `/` 仍由 Spring Boot 提供 index.html（aicode-web jar），但**aicode-web 入口走的是 `index.html` 而不是 `*.shtml`**——React Router 在客户端处理路径。
- **`vite.config.ts` 的 dev proxy 保留**——M4 切运行时 dev 模式仍需 `npm run dev` + Vite dev proxy 走 `:8080` 后端；jar 模式与 dev 模式互斥。
- **`AGENTS.md §11.5` 登记**——M4 切运行时已合并，含 `api-contract` Purpose 文字修订说明 + 13 MB Angular 删除登记。

**BREAKING**：
- M4 切运行时后，**唯一** `npm run dev` 仍可访问 React（带 Vite proxy）；`./gradlew :aicode:bootRun` 启动后访问 `http://localhost:8080/` 即 React。
- 老链路 `*.shtml` 后端 API 路径仍兼容——`ShtmlSuffixRewriteFilter` 保留。
- Angular 7 路由表（如 `/home`、`/login` 老路径）被删除——M3 整组接的是 `*.html`（或无后缀）路径，React Router 接管。
- `aicode-web` 生产构建依赖 node + npm——CI 流水线需保证有 node 工具链；M0 阶段已落地 `node 弱依赖三态`（D5），无 node 时构建失败。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

- `api-contract`：Purpose 文字修订 + 新增 1 Scenario "M4 切运行时 `/` 命中 React 而非 Angular"。这是 AGENTS §11.5 明示的"M4 SHALL MODIFIED"动作。

## Impact

- **后端 `aicode/build.gradle`**：1 行 `implementation` 声明新增；总代码量 +1 行。
- **删除 13 MB Angular 7 静态产物**：`aicode/src/main/resources/static/*` 整体删除（含 index.html、10 个 `*.js`、assets/、favicon.ico）。该目录被 `aicode-web/build/dist/static/` 替代（Spring Boot static resources locations 中 classpath 优先级：先 aicode-web jar，后 aicode 模块自身 resources）。
- **`api-contract/spec.md` Purpose 修订**：2 段文字改写。
- **新增集成测试 `M4CutoverIntegrationTest`**：用 MockMvc 启 bootRun，4 用例。
- **不影响**：
  - `aicode/src/main/java/.../filter/ShtmlSuffixRewriteFilter.java`（保留，兼容 `.shtml` 老路径）
  - `LoginCtrlShtmlPathTest`（保留）
  - `aicode-web` 模块代码（M3 整组已稳定）
  - 后端所有 API 端点（不受 M4 切换影响）
- **风险**：
  - **`/` 路由冲突**：Spring Boot static resources 加载顺序——`aicode-web/build/dist/static/index.html` 与 `aicode/src/main/resources/static/index.html` 同时存在时，Spring Boot 默认按 classpath 顺序加载先到先得。`aicode-web` jar 在 build 时**先于** aicode 模块自身 resources 进入 classpath（取决于 gradle `project()` 依赖图）——需要 build 测试验证；若反向则**必须**彻底删 Angular 静态产物（不可部分保留）。
  - **aicode-web build 时序**：M0 build.gradle 已落地 "node 弱依赖三态"——`aicode-web:build` 在 `:aicode:build` 依赖图之前跑，无 node 时 aicode-web build 失败继而 aicode build 也失败。CI 流水线需保证有 node。AGENTS §3 已是文档要求。
  - **ShtmlSuffixRewriteFilter 与 React 冲突**：React `index.html` 不带 `.shtml` 后缀；ShtmlSuffixRewriteFilter 只对 `*.shtml` 路径重写，**不会**影响 React 入口。
