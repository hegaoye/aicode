# Tasks

## 1. 契约导出

- [x] 1.1 新增 `OpenApiExportIT`：`@SpringBootTest(MOCK)` + MockMvc 请求 `/v3/api-docs.yaml`，断言 HTTP 200 且内容以 `openapi:` 开头
- [x] 1.2 规范化：解析为 JSON 树后**移除 `servers` 节点**，再序列化回 YAML 写入 `contracts/openapi/openapi.yaml`；验证写出的文件不含任何 `http://127.0.0.1` 之类的取值
- [x] 1.3 确认 YAML 序列化依赖可用（`tools.jackson.dataformat.yaml`）；若不可用则改用行级剥离 `servers:` 块；验证两种路径产出等价结果
- [x] 1.4 执行 `./aicode/gradlew test`；验证 `contracts/openapi/openapi.yaml` 被生成且含 43 个 path

## 2. Gradle 模块接线

- [x] 2.1 `settings.gradle` 增 `include`；验证 `aicode-web` 被纳入构建
- [x] 2.2 `build.gradle` 落地：npm 任务（`onlyIf` 跳过）、`jar` 以 `static/**` 打包、`buildUi`、`:verifyApi`、`:generateApi`
- [x] 2.3 `.gitignore` 增 `node_modules/`；验证 `git status` 不再列出它
- [x] 2.4 新增 `:aicode-web:verifyApi`（`git diff --exit-code`）；验证契约未变时通过
- [x] 2.5 **反向验证已执行**：篡改契约 title → 任务失败并输出「契约已变更但未提交」；恢复后通过

## 3. aicode-web 骨架

- [x] 3.1 `package.json` + `npm install` 生成 lock 文件
- [x] 3.2 `vite.config.ts`（outDir / base / dev proxy）与 `tsconfig.json`
- [x] 3.3 最小 React 骨架；验证 `npm run build` 产出 `build/dist`
- [x] 3.4 `README.md` 含运行/构建/预览/验证命令 + 两条须知（mock 质量、契约剥离）

## 4. 契约生成链

- [x] 4.1 `orval.config.ts`，输入指向 `../contracts/openapi/openapi.yaml`
- [x] 4.2 `npm run generate` 产出 103 个文件
- [x] 4.3 `npm run typecheck` **exit=0**；`MapFieldColumnVO` 已生成为 TS interface（`src/generated/model/mapFieldColumnVO.ts`）
- [x] 4.4 `npm test` 2 例通过（含一个直接使用契约生成类型的用例）

## 5. 验证与归档

- [x] 5.1 执行 `./aicode/gradlew test`；验证全部后端测试通过且契约文件无未预期变化
- [x] 5.2 验证 node 缺失场景：临时改 `PATH` 使 node 不可见后运行 Gradle，确认在无产物时报出可执行提示而非静默成功
- [x] 5.3 同步 `AGENTS.md` §2（登记 `contracts/` 为松散目录）与 §7.1 / §11.3（登记 `OpenApiExportIT`）
- [x] 5.4 执行 `openspec validate --strict`，同步 delta spec 并归档；验证新能力已物化

> **阶段性记录（2026-10-16）**
>
> - **1.1–1.4 已完成**：`OpenApiExportIT` 导出契约到 `contracts/openapi/openapi.yaml`（45,531 字节、43 个 path），`servers` 块已剥离，实测无 `127.0.0.1` 残留，`openapi/info/tags/paths/components` 五个顶层节点完整。
> - **1.3 采用行级剥离而非 YAML 反序列化**：项目运行时是 Jackson 3（`tools.jackson`），而测试类路径上的 `jackson-dataformat-yaml` 是 Jackson 2（经 snakeyaml 传递）。若用后者做「解析→删节点→重新序列化」，整个文件会被按 Jackson 2 排版重写，契约 diff 被格式噪声淹没 —— 而该文件存在的意义就是让接口变更可评审。
> - **⚠️ 该变更被一个未预见的前置问题阻断**：首次导出的契约里所有端点的响应都是 `$ref: R` 且 `R.data` 为 `{}` —— 因为 `R` 不是泛型类（`private Object data`），38 个 Controller 方法全部返回裸 `R`。契约链做出来了，但喂给它的契约是空的。
> - **该前置问题已用独立变更解决**：`genericize-r-response-envelope`（已归档）。泛型化后 `$ref: R` 从 45 处降为 0，`components.schemas` 从 8 个增至 57 个，`/displayAttribute/list` 的 `data` 已正确指向 `array of MapFieldColumnVO`。
> - **2.x–5.x 尚未开始**：Gradle 模块接线、`aicode-web` 骨架、orval 生成链均未实施。本变更保持未归档状态。


> **收尾执行说明（2026-10-16）**
>
> - **5.2 node 三态已实测**（用受限 PATH 模拟）：
>   - 无 node + 无 dist → 构建**失败**，输出「未找到 node，且不存在既有前端构建产物…请先安装 Node.js 后执行 `./aicode/gradlew :aicode-web:buildUi`」
>   - 无 node + 有 dist → 输出「复用既有构建产物」并成功
>   - 有 node → 正常走前端构建
> - **三态逻辑踩了两个坑并已修正**：
>   1. 最初把判定放在 `buildUi.doFirst`，但它**排在依赖任务之后** —— node 缺失时已先崩在 `npmInstall`（exit 127），提示根本没机会输出。改为 `npmInstall`/`npmBuild`/`generateApi` 加 `onlyIf { nodeBin != null }` 跳过，并把判定移到 `jar.doFirst`。
>   2. Groovy 对 `File[]` **没有** `isEmpty()` 方法，`dist.listFiles().isEmpty()` 抛 `No signature of method`；改用 `?.length > 0`。
> - **2.1/2.4 修正**：`workingDir = rootProject` 传的是 Project 对象而非 File，Gradle 无法转换为 File；改为 `rootProject.projectDir`。
> - **契约当前状态**：54,642 字节、43 paths、47 schemas；`$ref: R` 为 0（`R` 泛型化后 springdoc 改为按端点生成内联 schema）。
