# Proposal

## Why

前端要用 React + TypeScript 重写，并采用**契约驱动**：由后端导出的 OpenAPI 契约生成前端类型、请求函数与 mock，从而让「前后端不一致」在编译期与测试期就失败。

但目前不存在这条链。仓库里没有任何产物能回答「后端到底对外暴露了什么」，前端只能靠读 Controller 源码或猜。若让每个前端成员各自手写请求与类型，字段改名、参数改名、端点新增/删除这类最常见的漂移，要等到运行时或联调才会暴露。

同时本仓库的前端资产是一份 13 MB 的 Angular 7 webpack 产物，**源码已不可维护**（`10.460f310dc07dcf545995.js` 这类哈希命名、无法有效修改）。要重写前端，必须先建立一条「后端契约 → 前端类型与 mock」的自动化链路，否则重写只是把不可维护的静态资源换成不可维护的静态资源。

## What Changes

- **导出契约**：`OpenApiExportIT` 在既有 Spring 上下文中调用 `/v3/api-docs.yaml`，规范化后写入 `contracts/openapi/openapi.yaml`（**入库**，与你的指定路径一致）。
- **规范化**：剥离 springdoc 按运行时生成的 `servers[0].url`（形如 `http://127.0.0.1:8090`）。该值与运行机器相关，若入库会让每次不同端口的本地运行都产生一条无意义 diff，契约文件很快会被「无脑 accept」淹没。
- **漂移守卫**：新增 `:aicode-web:verifyApi` 任务，执行 `git diff --exit-code contracts/openapi/openapi.yaml`，有差异即失败。使「改后端忘了更新入库契约」在 CI 阶段变红。
- **`aicode-web` Gradle 模块**：建 `aicode-web/`，含 `build.gradle` / `package.json` / `vite.config.ts` / `tsconfig.json` / 最小页面骨架。Jar 内布局为 `static/**`，`aicode` 加一行 `implementation project(':aicode-web')` 即可被 Spring Boot 服务。
- **orval 生成链**：`orval` 读 `contracts/openapi/openapi.yaml`，生成 TypeScript 类型、请求函数与 MSW mock handlers。
- **node 弱依赖**：`node` 缺失但已有构建产物时复用；`node` 与产物**都**缺失时构建失败并给出可执行提示 —— 堵住「悄悄产出无前端制品」这一入口。

## Capabilities

### New Capabilities

- `api-contract-artifact`: 定义跨语言契约的可执行产物要求 —— 契约 SHALL 由后端在测试期自动导出并入库；契约 SHALL NOT 含与运行机器相关的取值；入库契约 SHALL 有漂移守卫；前端 SHALL 从该契约生成类型与 mock 而非手写。

### Modified Capabilities

（无。`api-contract` 的 Purpose 在 React 真正落地（M4）时再改，见 `AGENTS.md §11.5` 已登记的待办。本变更只建立契约产物链，不改变任何既有可观测行为。）

## Impact

- **代码**：新增 `aicode-web/`（Gradle 模块 + Node 工程 + 最小 React 骨架）、`OpenApiExportIT`、`settings.gradle` 增 `include 'aicode-web'`、`aicode/build.gradle` 增一行模块依赖、`.gitignore` 增 `node_modules/`。
- **契约产物**：新增入库文件 `contracts/openapi/openapi.yaml`（约 45 KB，43 个 path）。
- **构建**：`./gradlew build` 在有 node 时会调 `npm ci && vite build`；无 node 且无产物时**明确失败**。
- **规范**：`AGENTS.md` §2 需把 `contracts/` 登记为松散目录；§7.1 / §11.3 需登记新增测试类。
- **不变更**：现有 13 MB `static/` **本阶段不删**（按既定决策，M4 且人工全流程走查通过后才删）。
- **风险**：M0 只产出最小骨架与生成链，不实现任何业务页面，因此不改变现有前端行为。