# Design

## Context

M0 已落地：本分支上存在 `aicode-web/`（Gradle 模块 + Vite 8 + React 19 + TS 7），`orval` 从入库的 `contracts/openapi/openapi.yaml` 生成 80 个产物（43 paths、含 47 schemas），包括：

- 类型生成：每个端点的入参 / 出参 / 统一响应 `R` 包装；命名规则受 springdoc 影响呈 `RListMapFieldColumnVO` / `List5Params` / `List5Result` 等形状；
- 请求函数：每个端点对应一个 `const`，返回 `Promise<AxiosResponse<...>>`；通过 `axiosInstance` 单例发起；
- MSW handlers：`getList5MockHandler(...)` 用 `*/displayAttribute/list` 通配并返回 `getList5ResponseMock()`。

M0 还做了两个配套变更：`R` 响应包装泛型化（`c44cd03`，让 `R.data` 在契约里持有真实形状，而非空 `{}`）和 `DisplayAttributeController` 改返 `MapFieldColumnVO`（`fa9e7a7`，让响应数据不含派生字段与嵌套实体树）。这两步合并后，`list5` 的契约已正确定义为「`R<List<MapFieldColumnVO>>`，其中 `MapFieldColumnVO.displayAttribute: DisplayAttributeVO`」。

本设计**只新增一个页面**——`/displayAttributes`，消费契约产物渲染 `GET /displayAttribute/list` 响应，按 `displayType` 把每个字段映射到对应控件。M0 已经把 dev proxy 配好（`/displayAttribute` → `:8080`），M1 不必再动 vite proxy 配置；M0 也已经按设计文档 §D5 让"无 node / 有产物 / 无产物"三态行为固化，M1 不再叠加这些工程基础。

## Goals / Non-Goals

**Goals:**

- 在 `aicode-web/src/pages/DisplayAttributesPage/` 下交付首个契约驱动的业务页面与对应单元测试。
- 把"R.success.data 解构 + 错误归一化"与"displayType → 控件"两件样板沉淀为 `aicode-web/src/lib/` 下的可复用单元，供 M2/M3 复用。
- 单元测试走 MSW，`npm run typecheck` 与 `npm test` 全绿；`npm run dev` 后访问 `/displayAttributes` 可正常加载（不要求真实后端可达，MSW 即可演示）。
- 在 `aicode-web/README.md` 沉淀一页"如何接下一个端点"的复用准则。

**Non-Goals:**

- 不实现登录流程（`LoginPage` 占位与 M1 接入 `/login/signin` 是另一条独立工作，本次不做）。
- 不实现工作台首页、其它列表页与任何 CRUD 页面。
- 不接入实时构建日志的 WebSocket、不接入 token 注入（登录成功后的 `Authorization` 头注入是另一条工作）。
- 不在 Spring Boot 运行时切换前端（M4）。
- 不修改后端任何代码、不修改 `contracts/openapi/openapi.yaml`、不动 `:aicode-web:verifyApi`。
- 不重新生成 orval 产物（无契约变化 → 无重新生成动机）。

## Decisions

### D1：在 `aicode-web/src/lib/contract-response/` 下提供 `unwrapSuccess` 与 `throwIfFailed`

**决定**：新增 `aicode-web/src/lib/contract-response.ts`，导出两个函数：
- `unwrapSuccess<T>(resp: AxiosResponse<R<T>>): T` —— 校验 `success === true` 与 `code === "0000"`，把 `data` 取出；失败时抛 `ContractError`；
- `throwIfFailed<T>(resp: AxiosResponse<R<T>>): T` —— 与前者合并为一个调用，校验后吐 `data`，否则抛错。

并定义 `ContractError extends Error`，含 `code: string` / `info: string` 字段。

**理由**：
- orval 生成的请求函数一律返回 `Promise<AxiosResponse<R<T>>>`；消费方若不抽一层，每个端点都得重复 `resp.data.success ? resp.data.data : throw` 这样的样板。
- 抽到 `lib` 是为了让 M2/M3 接入时直接 `import`；放在 `pages/...` 内只能本页面用，与 M0 文档沉淀"如何接下一个端点"的目标不符。

**备选方案**：(a) 写成 React Query / SWR 的自定义 hook —— 但本变更不自创底层依赖，避免增加 `package.json` 维护面（M2 引入数据请求库时可重构过去）。(b) 直接用 axios interceptor 拆 `R` —— 对 MSW 桩数据不友好，会拦截测试期望。

### D2：控件分发器以 `Map<string, ComponentType<...>>` 形式组织，明文 `text` / `number` / `date` / `select` 各占一行

**决定**：新增 `aicode-web/src/lib/display-controls/` 目录：
- `index.tsx` —— `<DisplayControl attribute={...} value={...} onChange={...} />`，按 `attribute.displayType` 查表渲染；
- `text.tsx` / `number.tsx` / `date.tsx` / `select.tsx` —— 各提供最朴素的 HTML 控件；
- `unknown.tsx` —— 渲染 `<input disabled>` + 类名提示该控件未实现，作为安全降级；
- 表 `Record<DisplayTypeKey, ComponentType<...>>` 在 `index.tsx` 中以字面量方式直接可见。

**理由**：
- 新增一种 `displayType` 时，M2/M3 只需要在表里加一行 + 一个文件；不需要改动既有控件实现。
- 把"未知 displayType → 安全占位"显式化，避免运行时 `undefined` 崩溃或 React key 缺失。
- 不引入 antd / shadcn 等 UI 库 —— 当前 M0 阶段前端依赖表最小化（仅 react / react-dom / react-router-dom / axios / dev-only 测试栈），M1 加入表单元件只允许用受控原生 `<input>` 等，理由与 §D1 同（避免触级依赖）。

### D3：路由注册走「在 `App.tsx` 增加一行 `<Route>`」的最小侵入方式，不引入 `lazy` 与 `Suspense` 边界

**决定**：在 `aicode-web/src/App.tsx` 的 `<Routes>` 内增加 `<Route path="/displayAttributes" element={<DisplayAttributesPage />} />`，并在占位路由 `/` 中加一个链接到该页面的入口（`<AppShell><Link to="/displayAttributes">查看展示属性列表</Link>...</AppShell>`）。

**理由**：
- M1 只有一个新页面，无需为它引入 React.lazy 与 `<Suspense>`。
- 入口放在 `/` 占位页内，便于人工验证时通过根链接点击进入，而不是手动改 URL。

**备选方案**：(a) 引入 `react.lazy(() => import('./pages/DisplayAttributesPage'))` —— 等页面数 ≥ 5 或单个页面体积明显增长时再切。M1 不必。

### D4：MSW 在 `setupFiles` 内对每个测试启动 handlers；测试中允许覆盖特定 handler

**决定**：在 `aicode-web/src/test-setup.ts`（既有）内调用 `setupServer(...allHandlers)`，在 `vitest` 的 `beforeAll/afterEach/afterAll` 中按 MSW 文档模板启停；新测试可以以 `getList5MockHandler([...])` 覆盖特定端点的桩数据。

**理由**：
- 当前 `test-setup.ts` 已存在但只配了 `@testing-library/jest-dom` 与 import 顺序，本变更在该文件内追加 MSW 启动 / 关闭生命周期；
- orval 生成的 `getList5MockHandler` 接受 `overrideResponse`，意味着测试不必写手工桩即可驱动测试确定性场景，但同时允许后续测试在必要时手工指定数据。

### D5：不修改 `vite.config.ts` 的 proxy 段——M0 已配齐

**决定**：本变更不动 `vite.config.ts`。

**理由**：
- M0 已把 `/displayAttribute` 等前缀 proxy 到 `:8080`（参见 `vite.config.ts` 实际内容，与本仓库 AGENTS.md §4「应用端口 8080」一致）。
- Dev proxy 仅在 `npm run dev` 下生效；`build` 产物仍按静态资源方式产出，与本变更"不在 M1 切运行时"目标一致。

### D6：暂不切换 Pinia / Zustand 等全局状态管理

**决定**：`/displayAttributes` 仅用 `useState` + 一次性 `useEffect` 拉取，不引入全局状态。

**理由**：单页面 + 单请求用全局状态只会增加维护面；M2 涉及多页切换或跨页面共享数据时再评估。

## Risks / Trade-offs

**[orval 生成的 `list5` 等无前缀命名易与业务代码混淆]** → 在业务代码中只用 `import { list5 as fetchDisplayAttributes } from './generated/api'` 这种命名别名；并在 `aicode-web/README.md` 的"如何接下一个端点"小节写明这条约定。`aicode-web/src/lib/contract-response.ts` 不直接导出 `list5` 等生成函数，让调用方把 import 局限在 `pages/...` 内部。

**[测试中 MSW 拦截的 URL 通配 `*/displayAttribute/list` 与 dev proxy 目标 (`http://127.0.0.1:8080`) 一致，但 axios 实例相对路径下两者都会落同一通配]** → 无功能差异。Vitest 在 jsdom 中发起请求时不会真的连出网络，MSW 拦截是天然的。Dev proxy 模式则由 Vite 拦截。两者并存且互不干扰，已在 M0 验收并记录。

**[Dev 时如未启后端，浏览器会因 proxy 找不到后端报错]** → M0 测试已记：本变更的单元测试用 MSW 提供数据，不依赖后端可达；若用户用浏览器访问 `/displayAttributes` 且后端未启动，会看到"无法连接"的 axios 错误——在错误气泡中告知"如未启用本机后端，请先 `./gradlew :aicode:bootRun` 启动或临时走 MSW 浏览器模式"。M2 再加浏览器 MSW worker（`mockServiceWorker.js`）。

**[`DisplayAttributeVO` 的 `displayType` 字段是无约束字符串，目前生成的 TS 类型为 `displayType?: string`]** → 控件分发器以映射表查，未命中走 `unknown.tsx` 降级占位。这是合理的最小切片；M2 进一步收紧：在 `MapFieldColumnVO` 的契约端加 `enum:` 限制，orval 将生成 TypeScript 联合类型，控件分发器即可强制覆盖完备性。当前 M1 容忍宽字符串契约。

**[同源 dev proxy 因 Spring Boot 4 已不含 `setUseSuffixPatternMatch`，`/displayAttribute/list.shtml` 与无后缀路径行为有差异]** → 本变更的请求函数走 orval 生成形态，URL 为 `/displayAttribute/list` 不带 `.shtml` 后缀；与 ShtmlSuffixRewriteFilter 不冲突。`.shtml` 兼容层对 React 新前端不构成必要路径。

## Migration Plan

单批交付（一个 feature commit）：

1. 新增 `aicode-web/src/lib/contract-response/` 子目录与 `index.ts`；
2. 新增 `aicode-web/src/lib/display-controls/` 子目录与四个控件文件 + `index.tsx`；
3. 新增 `aicode-web/src/pages/DisplayAttributesPage/`（`index.tsx`、`render-controls.ts`、`<DisplayAttributesPage>` 的逻辑）；
4. 新增 `aicode-web/src/pages/DisplayAttributesPage/index.test.tsx`（MSW + 渲染 + 控件分发器 + 错误分支）；
5. 修改 `aicode-web/src/App.tsx` 注册路由与 `/` 占位页入口链接；
6. 修改 `aicode-web/src/test-setup.ts` 启动 MSW server；
7. 修改 `aicode-web/README.md`（新增"如何接下一个端点"章节）；
8. 执行 `npm run typecheck` 与 `npm test`，确认全绿。

**回滚**：单 commit revert（含新增文件一并删除即可）；本变更不修改 `contracts/openapi/openapi.yaml` 与后端任何文件，回滚后契约验证 / 后端测试不退化。

## Open Questions

- 当未来 React 前端真接入鉴权流（替换 AGENTS.md §11.5 / §6.13 提到的 URL 参数 token）时，`unwrapSuccess` 是否需要在 9007（未授权）码时做"自动跳转 `/login`"——此时再讨论，不在 M1 范围。
- `DisplayAttributeVO` 的 `displayType` 字段是否应升级为联合类型（受契约 schema 限制）——M2 议。
