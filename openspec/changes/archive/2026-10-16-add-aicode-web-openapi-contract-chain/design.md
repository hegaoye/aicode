# Design

## Context

见 `proposal.md - Why`。设计上需要处理的现状约束：

- springdoc 已启用且 `application-test.yml` 未关闭它，因此 `/v3/api-docs.yaml` 在测试上下文中可用（已实测：HTTP 200、`application/vnd.oai.openapi`、45,604 字节、43 个 path）。
- Gradle 9.8.1、Node 24.17.0 / npm 11.13.0 均可用。
- 根 `settings.gradle` 目前只 include `common` / `facade` / `aicode`。
- Spring Boot 从 classpath 的 `/static/`、`/public/`、`/resources/`、`/META-INF/resources/` 提供静态资源 —— 这是 `aicode-web` 能以普通 Gradle 模块接入、**无需任何资源拷贝逻辑**的依据。

## Goals / Non-Goals

**Goals:**

- 让 `contracts/openapi/openapi.yaml` 成为可信、可 diff、不会过期��契约。
- 让前端类型与 mock 由该契约生成。
- 打通 `aicode-web` 到 Spring Boot 的静态资源链路。

**Non-Goals:**

- 本阶段**不实现任何业务页面**（登录/Dashboard/向导等属 M1–M3）。
- 本阶段**不删除** 13 MB 旧 `static/`。
- 不引入 CI 流水线（AGENTS.md §11.4 建议但当前仓库无 CI）。

## Decisions

### D1：契约由集成测试导出，而非 Gradle 启停应用

**决定**：`OpenApiExportIT` 在既有 `@SpringBootTest` 上下文中通过 `MockMvc` 请求 `/v3/api-docs.yaml`，规范化后写文件。

**理由**：`springdoc-openapi-gradle-plugin` 不在 Maven Central（实测 404，仅在 Gradle Plugin Portal），引入它等于新增一个插件源依赖。仓库已有 `measurement/` 脚本用「启停应用 + curl」的先例，但那需要占用端口、约 20 秒启动、且依赖外部编排脚本，无法作为 `./gradlew test` 的自然一环。

集成测试路径无额外依赖、随测试自动执行、且复用已有的 test profile 与 schema 初始化。

**备选方案**：(a) `springdoc-openapi-gradle-plugin` —— 新增插件源依赖；(b) measurement 脚本模式 —— 需要独立编排，脱离测试生命周期。

### D2：剥离 `servers` 而非固定其值

**决定**：导出后移除 `servers` 节点。

**理由**：springdoc 按本次请求的 host/port 生成 `servers[0].url`（实测为 `http://127.0.0.1:8090`）。无论填什么固定值，契约里都会留一个「假的服务器地址」误导读者 —— 前端走 Vite dev proxy，根本不使用 spec 的 server。删掉最诚实。

规范化在测试内用 Jackson 3 完成（`ObjectNode.remove("servers")`），再用 `tools.jackson.dataformat.yaml.YAMLMapper` 序列化回 YAML。

**备选方案**：保留但置为 `/` —— 语义上仍会误导。

### D3：守卫用 `git diff --exit-code`，而非重新导出比对

**决定**：`:aicode-web:verifyApi` 执行 `git diff --exit-code -- contracts/openapi/openapi.yaml`。

**理由**：契约入库后，测试再次运行就会刷新它。因此「当前文件内容」与「提交态内容」的差异**恰好**等价于「代码改了但契约没提交」。git 是天然的比较基准，无需另建快照机制，也不依赖 node（满足 spec 的「守卫不依赖前端工具链」）。

**备选方案**：(a) 另存一份基线文件比对 —— 多一份冗余资产，且基线本身也可能忘记更新。

### D4：`aicode-web` 产出含 `static/**` 的 Jar

**决定**：`aicode-web` 的 `Jar` 任务把 `build/dist` 的内容以 `static/` 为前缀打进 jar；`aicode` 加 `implementation project(':aicode-web')`。

**理由**：Spring Boot 的静态资源locations 包含 classpath 的 `/static/`，因此**无需**任何 copy 任务或资源目录配置。这是让 Gradle 多模块与 Spring Boot 资源服务对接的最短路径。

### D5：node 弱依赖 + 缺件硬失败

**决定**：三态处理 —— 有 node 则构建；无 node 但 `build/dist` 存在则复用；两者皆无则**失败并提示先执行 `:aicode-web:buildUi`**。

**理由**：无 node 时「保留旧产物」正是既定决策。但 M4 之后旧的 13 MB `static/` 会被删除，届时「旧产物」不再存在 —— 若静默跳过，产物将**完全不含前端**且构建报成功。这是唯一入口，必须堵死。

失败而非警告，是为了让「制品缺前端」不可能悄悄合入。

### D6：生成物放在 `src/generated`，并可一键重建

**决定**：orval 输出到 `aicode-web/src/generated/`，由 `npm run generate` 重建。

**理由**：把生成物与手写代码物理分开，review 时能一眼看出哪些是契约推导的、哪些是人写的。生成物是否入库见下（当前入库，便于 CI 无 node 环境下也能做类型检查）。

## Risks / Trade-offs

**[`git diff` 在未提交状态下永远为空，守卫失效]** → 守卫应在 CI 或「代码已提交」的时机运行。`./gradlew verifyApi` 作为独立任务，由 CI 调用；本地开发者在改完后端尚未提交时运行它会得到「无差异」的假阴性。因此任务的 Javadoc SHALL 明确这一前提。

**[`npm ci` 需要 lock 文件]** → 首次提交需生成 `package-lock.json`，之后用 `npm ci` 而非 `npm install` 以保证可复现。若 lock 缺失，`npm ci` 会失败 —— 这是期望行为。

**[orval 生成的 mock 多半不够用]** → 预期如此。生成的 mock 覆盖「契约形状」，业务语义场景（边界、时序）仍需用手工 override 覆盖。这一点 SHALL 写进 `aicode-web/README.md`，避免误以为「有 mock 就不用写测试数据」。

**[Spring Boot 静态资源与旧 static 并存]** → M0 阶段旧的 13 MB 仍在 `aicode/src/main/resources/static/`。两个来源同时提供 `/index.html` 时行为不确定 —— 因此 M0 阶段 `aicode-web` 的 Jar **先不加入** `aicode` 的依赖，等 M4 切换时再接。这让 M0 处于「链路可验证但不影响运行时」的状态。

**[`tools.jackson.dataformat.yaml` 是否在依赖中]** → 需实测。若不在，则改用 snakeyaml 或直接对 YAML 文本做行级剥离（`servers:` 块是连续的顶层行）。

## Migration Plan

单批交付：

1. `settings.gradle` 增 `include 'aicode-web'`
2. 建 `aicode-web/`（build.gradle + package.json + vite.config.ts + tsconfig.json + 最小骨架）
3. `OpenApiExportIT` 导出并规范化契约
4. `.gitignore` 增 `node_modules/`
5. orval 配置与生成链跑通
6. `AGENTS.md` §2 登记 `contracts/`

**回滚**：单一提交，revert 即可；契约文件一并移除。

## Open Questions

- `tools.jackson.dataformat.yaml` 的可用性在实施时验证（见 Risks）。若不可用，本设计的方法二选一均成立，不影响任务拆分。
- 生成物是否入库：当前按「入库」实施以支持无 node 的类型检查；若后续认为噪音过大，可在 M1 复议。