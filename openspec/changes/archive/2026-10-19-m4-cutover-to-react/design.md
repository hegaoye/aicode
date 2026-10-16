# Design

## Context

M3 整组（详情页 / 构建日志 / 字段裁切 / 账户信息）已落地——React 前端业务功能完整。但 M0 README 中标注的"13 MB Angular 静态产物由 M4 删除"未执行，`aicode/build.gradle` 也未引入 `aicode-web` 模块依赖。当前 `./gradlew :aicode:bootRun` 启动后访问 `/` 仍命中 Angular 7 老产物（`aicode/src/main/resources/static/index.html` + 一堆 `*.js`），React 仅在 `npm run dev`（Vite dev server）下可见。

`api-contract` 主能力 Purpose 文字描述"仓库内静态前端（Angular 7.0.4 + ng-zorro + jQuery 的打包产物）"——M4 后该描述失效。AGENTS §11.5 明确"M4 SHALL 对 `api-contract` 的 Purpose 做 MODIFIED"。

## Goals / Non-Goals

**Goals:**

- 后端 `aicode/build.gradle` 增 `implementation project(':aicode-web')`
- 删除 13 MB Angular 7 静态产物（`aicode/src/main/resources/static/*`）
- `api-contract` 主能力 Purpose 文字改写 + 新增 3 个 Scenario 钉运行时切换
- 集成测试 `M4CutoverIntegrationTest` 守护 4 个关键不变量

**Non-Goals:**

- 不动 `aicode-web` 模块代码（M3 整组稳定）
- 不动 `ShtmlSuffixRewriteFilter`（M0 落地，保留 `.shtml` 老 API 兼容）
- 不接 npm run dev 行为变化（dev 模式仍走 Vite proxy）
- 不删 13 MB 之外其它"老产物"——M0 阶段已处理 `assets/` `favicon.ico` 等

## Decisions

### D1：M4 切运行时仅 1 行 build.gradle + 1 个目录删除

**决定**：`aicode/build.gradle` 增 1 行 `implementation project(':aicode-web')`；`aicode/src/main/resources/static/*` 整体 `rm -rf`；其它文件零改动。

**理由**：
- `aicode-web/build.gradle` 自带注释明示"产物是一个内部布局为 `static/**` 的 Jar"，M0 阶段已落地（详见 design.md - D4 / openspec/changes/add-aicode-web-openapi-contract-chain/）
- 仅 1 行声明 + 1 个目录删除是**最小变更**——符合"不破坏既有行为"原则
- 后端 `dependencies` 块其它已含 `common` / `facade`；`aicode-web` 同层

**备选方案**：(a) 改 `aicode-web` 命名 / 路径——超出 scope；(b) 拆 `aicode-web` 为子模块——M5+ 议程；(c) 1 行 + 1 目录删除 —— 本变更采用。

### D2：删 Angular 静态产物时同时彻底——不部分保留

**决定**：`aicode/src/main/resources/static/*` 全部删除（含 index.html + 10 个 `*.js` + assets/ + favicon.ico + 3rdpartylicenses.txt）。

**理由**：
- 若保留 `assets/` `favicon.ico` 等 Angular 资源，Spring Boot 静态资源加载顺序会优先这些（classpath 顺序），React 入口可能被遮蔽
- 13 MB 文件全删 + aicode-web 提供新 `static/**` 是**单源**最稳
- 旧资源前端用户在迁移期可从 git 历史或 npm 包里拿到；M4 是"切运行时"不是"保留老资源"

**备选方案**：(a) 部分保留——增加 classpath 顺序风险；(b) 全删——本变更采用。

### D3：aicode-web jar 嵌入后，`/` 路由由 Spring Boot 解析优先级决定

**决定**：不写 custom ResourceHandler，按 Spring Boot 默认 `ResourceHttpRequestHandler` 行为——classpath 顺序在 jar 列表里找到第一个 `static/index.html`。

**理由**：
- Spring Boot 默认 static resources locations 包含 `classpath:/static/`、`classpath:/public/`、`classpath:/resources/`、`classpath:/META-INF/resources/`——**M0 README 注释已明示**
- Gradle `implementation` 把 `aicode-web` jar 放在 aicode 模块 `runtimeClasspath`——M0 build.gradle 已配置这一行为
- classpath 顺序：aicode-web jar 的 `static/index.html` 应在 aicode 模块自身的 `static/` 之前被加载——但**这取决于 Gradle 依赖图解析**—— task 4.x 用集成测试断言

**备选方案**：(a) 自定义 `WebMvcConfigurer` 显式优先级——超 scope；(b) 默认行为 + 集成测试断言——本变更采用。

### D4：ApiContractIT 新增 4 用例覆盖 M4 关键不变量

**决定**：M4CutoverIntegrationTest 4 用例：
1. `GET /` 返 React HTML（`id="root"`）不返 Angular `*.js`
2. `GET /api/list.shtml` 仍返 Spring MVC R 包络 JSON
3. `GET /login` 与 `GET /account/me` 仍由 React Router 渲染（无 404 / 500）
4. `GET /nonexistent-path` 仍由 aicode-web jar 的 SPA fallback 返 `index.html`（M4 后无 404 — 全 fallback）

**理由**：
- 4 用例覆盖"`/` 是 React 而非 Angular" / "`/api/*` 仍 MVC" / "业务路由正常" / "SPA fallback 正常" 4 维度
- 集成测试用 `@SpringBootTest(webEnvironment = RANDOM_PORT)` —— 与现有 ApiContractIT 一致 bootRun harness

**备选方案**：(a) 单元测试 mock static resources——不能真验证"切运行时"；(b) 集成测试 4 用例——本变更采用。

### D5：接口/页面路径分离 —— 不做 SPA fallback，改用「接口 .shtml 后缀 + 页面客户端路由」

**决定**（2026-10-16 实施期追加，用户决策）：**不做** SPA fallback（把未匹配路径 forward 到 `index.html`），改为**接口与页面显式分离**：

| 类别 | 路径形态 | 谁处理 |
|---|---|---|
| **后端 API** | `/project/list.shtml`、`/login/signin.shtml`（**统一带 `.shtml` 后缀**） | `ShtmlSuffixRewriteFilter`（M0 已落地）剥后缀 → Spring MVC Controller |
| **前端页面** | `/`（**唯一 server 入口**）+ 之后全部客户端路由 | `index.html` + `HashRouter` 客户端导航 |
| **前端静态资源** | `/assets/*` | Spring Boot static resources（默认行为） |

前端配套改动：
- 新增 `src/lib/api/shtml-suffix.ts`：一个 axios **request 拦截器**，把 `/api` 路径（非 `.shtml` 结尾、非静态资源路径）追加 `.shtml` 后缀。与 `auth-interceptor` **并列而非合并**（分层原则：`auth` 管鉴权、`api` 管 URL 形态）。
- `main.tsx` 除 `installAuthInterceptor()` 外再调 `installShtmlSuffixInterceptor()`。
- `main.tsx` 的 `BrowserRouter` 改 **`HashRouter`**：URL 形态为 `/#/login`、`/#/project/list` —— hash 段不经服务端，`/login` 直达不会 400。

**理由**：
- Spring Boot 4 对未匹配路径抛 `NoResourceFoundException`（**不** fallback 到 index.html）—— 实测 `GET /login` 与 `GET /nonexistent-path` 均返 `400 {"code":"9999"}`。
- 用户的方案「接口自动方向到接口，是页面的自动到静态资源中，可以考虑增加接口请求后缀，或者路由规则区分」正是这条：**接口加后缀**（`.shtml`）+ **页面走 hash**。二者都不依赖 server 端 fallback，语义零歧义。
- 与 M0 `ShtmlSuffixRewriteFilter` 天然复用——该过滤器已把 `.shtml` 视为"这是 API"的标记，前端补后缀即与之对接，后端 45 个 `@RequestMapping` 一行不用改。
- 避免新增 `SPAForwardController` / 改 `ContextConfiguration` excludePathPatterns（那会与 M0 记录的「`**/*` 通配绕过鉴权」问题纠缠）。

**代价**：前端 URL 带 `#`（`/#/login`）——可接受（内部工具，非对外产品）。深链直达体验良好（`/#/project/list` 直达可用）。分享链接需带 hash。

**备选方案**：(a) SPA fallback（未匹配路径 forward 到 index.html）—— 需改后端 filter + 新增 Controller，与 M0 记录的鉴权通配问题纠缠，**用户已否决**；(b) 前端路由加 `/ui/**` 前缀 —— 需后端 `ContextConfiguration` 排除 `/ui/**` + 前端全部路由加前缀，改动面更大（5 个路由 × 2 处），**未选**；(c) 本决策 —— 最小改动且语义最清晰。

**[aicode-web jar 必须先 build 后 aicode 依赖]**：M0 build.gradle `aicode-web:build` 顺序由 `task.dependsOn` 显式声明。若顺序错位——`/aicode/build/libs/aicode.jar` 不含 `aicode-web/build/libs/aicode-web.jar` 的 `static/` 资源。**集成测试 4 用例会立刻发现**（D4），bootRun 启动后 `/` 返 Angular 表明 classpath 顺序错。

**[ShtmlSuffixRewriteFilter 与 React 路径]**：React `index.html` 不带 `.shtml` 后缀；ShtmlSuffixRewriteFilter 只重写 `*.shtml` 路径。**没有冲突**——但旧 Angular 内部 hash 路径（`/home` 等）M4 后返 404（被 ShtmlSuffixRewriteFilter 改写为 `/home` 走 React — React 路由表不含该路径 → SPA fallback 返 `index.html`）。**反向**：与 M3 整组的 React Router 兼容，老 Angular 路径被 SPA fallback 吸收，不破坏契约。

**[ApiContractIT 现有 172 用例**端到端 bootRun 启动时若 M4 切错导致资源覆盖顺序反转]，部分 Scenario ⑨ / ⑧ / ⑦ 等会因 `/api/list.shtml` 路径返 Angular `*.js` 而失败——这是**反向验证**机制，不需专门写。

## Migration Plan

单批交付（一个 feature commit）：

1. **修改 `aicode/build.gradle`**：在 `dependencies` 块加 `implementation project(':aicode-web')` 一行
2. **删除 `aicode/src/main/resources/static/*`**：13 MB Angular 7 产物整体移除
3. **`api-contract/spec.md` Purpose 改写**（同步 tasks 4.x）
4. **新增 `M4CutoverIntegrationTest`**：4 用例（用 `@SpringBootTest(webEnvironment = RANDOM_PORT)` + MockMvc）
5. **跑全套测试**：
   - `./aicode/gradlew :aicode:test` —— 期待 172+ 用例（基线 172 + M4 集成测试 4 用例）
   - `cd aicode-web && npm run typecheck && npm test` —— 期待全 72 用例 / 0 失败
6. **同步 delta spec → archive**

**回滚**：单 commit revert 即恢复 Angular 静态产物 + 删 build.gradle 声明 + 改回 api-contract Purpose。`aicode-web` 模块代码不动。

## Open Questions

- M4 切换后**前端 dev 模式**（`npm run dev`）与**生产 jar 模式**（`./gradlew :aicode:bootRun`）的差异——dev 模式走 Vite dev server 单独工作；jar 模式把 aicode-web 嵌入 aicode。当前 dev 模式依赖 `vite.config.ts` 的 `/api/*` 与 `/websocket` proxy 转发到 `:8080`；M4 切运行时该配置仍有效（dev 模式不被 M4 触动）。
- M4 切换后 `aicode-web` jar 内的 `index.html` 与 SPA fallback（任意未匹配路径都 fallback 到 `index.html`）——这是 Vite build 默认配置 `historyApiFallback` 在 aicode 集成静态资源后**自动生效**（因为 Spring Boot 对未匹配路径的 default 行为是 no static resource found → 404）。`M4CutoverIntegrationTest` 用例 4 显式验证 SPA fallback。
- 13 MB Angular 静态产物删除后，`aicode/src/main/resources/` 是否还有其它"Angular-era"资源（如 `application*.yml` 之类）——M0 README 备注 Angular 产物仅在 `static/` 目录，无其它残留。**实施时 task 2.2 显式 `ls` 验证 `aicode/src/main/resources/` 仅剩 yml 与 properties**。
