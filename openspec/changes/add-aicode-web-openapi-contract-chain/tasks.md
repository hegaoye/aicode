# Tasks

## 1. 契约导出

- [x] 1.1 新增 `OpenApiExportIT`：`@SpringBootTest(MOCK)` + MockMvc 请求 `/v3/api-docs.yaml`，断言 HTTP 200 且内容以 `openapi:` 开头
- [x] 1.2 规范化：解析为 JSON 树后**移除 `servers` 节点**，再序列化回 YAML 写入 `contracts/openapi/openapi.yaml`；验证写出的文件不含任何 `http://127.0.0.1` 之类的取值
- [x] 1.3 确认 YAML 序列化依赖可用（`tools.jackson.dataformat.yaml`）；若不可用则改用行级剥离 `servers:` 块；验证两种路径产出等价结果
- [x] 1.4 执行 `./aicode/gradlew test`；验证 `contracts/openapi/openapi.yaml` 被生成且含 43 个 path

## 2. Gradle 模块接线

- [ ] 2.1 `settings.gradle` 增 `include 'aicode-web'`
- [ ] 2.2 建 `aicode-web/build.gradle`：含 npm 构建（弱依赖三态）与 Jar 任务（`build/dist/**` → `static/**`）
- [ ] 2.3 `.gitignore` 增 `node_modules/`；验证 `git status` 不再把 node_modules 列为待添加
- [ ] 2.4 新增 `:aicode-web:verifyApi` 任务（`git diff --exit-code -- contracts/openapi/openapi.yaml`），Javadoc 写明「代码未提交时运行会得到假阴性」；验证该任务在契约未变时通过
- [ ] 2.5 **验证守卫真的会失败**：手工改一行 `contracts/openapi/openapi.yaml`，运行 `verifyApi`；验证任务失败；恢复后通过

## 3. aicode-web 骨架

- [ ] 3.1 建 `package.json`（react / react-dom / react-router-dom / vite / typescript / vitest / @testing-library/react / msw / orval），执行 `npm install` 生成 `package-lock.json`
- [ ] 3.2 建 `vite.config.ts`：`build.outDir` 指向 `build/dist`，`base: '/'`，dev server 配 proxy 到后端 8080
- [ ] 3.3 建 `tsconfig.json` 与最小 React 骨架（`main.tsx` / `App.tsx`），页面为占位内容；验证 `npm run build` 产出 `build/dist/index.html`
- [ ] 3.4 建 `aicode-web/README.md`：说明运行 / 构建 / 预览 / 验证命令，并写明「orval 生成的 mock 只覆盖契约形状，业务场景仍需手调」

## 4. 契约生成链

- [ ] 4.1 建 `orval.config.ts`，输入指向 `../contracts/openapi/openapi.yaml`，输出到 `src/generated`（类型 + 请求函数 + MSW mock）
- [ ] 4.2 执行 `npm run generate`；验证 `src/generated` 下产出类型与 mock 文件
- [ ] 4.3 验证生成类型可编译（`npm run typecheck`）；确认 `/displayAttribute/list` 的响应类型是 **VO 的 13 个字段**，而非实体形状 —— 这直接验证变更一的效果已传导到契约
- [ ] 4.4 建 `npm test`（vitest）与一个使用 MSW mock 的最小组件测试；验证测试通过

## 5. 验证与归档

- [ ] 5.1 执行 `./aicode/gradlew test`；验证全部后端测试通过且契约文件无未预期变化
- [ ] 5.2 验证 node 缺失场景：临时改 `PATH` 使 node 不可见后运行 Gradle，确认在无产物时报出可执行提示而非静默成功
- [ ] 5.3 同步 `AGENTS.md` §2（登记 `contracts/` 为松散目录）与 §7.1 / §11.3（登记 `OpenApiExportIT`）
- [ ] 5.4 执行 `openspec validate --strict`，同步 delta spec 并归档；验证新能力已物化

> **阶段性记录（2026-10-16）**
>
> - **1.1–1.4 已完成**：`OpenApiExportIT` 导出契约到 `contracts/openapi/openapi.yaml`（45,531 字节、43 个 path），`servers` 块已剥离，实测无 `127.0.0.1` 残留，`openapi/info/tags/paths/components` 五个顶层节点完整。
> - **1.3 采用行级剥离而非 YAML 反序列化**：项目运行时是 Jackson 3（`tools.jackson`），而测试类路径上的 `jackson-dataformat-yaml` 是 Jackson 2（经 snakeyaml 传递）。若用后者做「解析→删节点→重新序列化」，整个文件会被按 Jackson 2 排版重写，契约 diff 被格式噪声淹没 —— 而该文件存在的意义就是让接口变更可评审。
> - **⚠️ 该变更被一个未预见的前置问题阻断**：首次导出的契约里所有端点的响应都是 `$ref: R` 且 `R.data` 为 `{}` —— 因为 `R` 不是泛型类（`private Object data`），38 个 Controller 方法全部返回裸 `R`。契约链做出来了，但喂给它的契约是空的。
> - **该前置问题已用独立变更解决**：`genericize-r-response-envelope`（已归档）。泛型化后 `$ref: R` 从 45 处降为 0，`components.schemas` 从 8 个增至 57 个，`/displayAttribute/list` 的 `data` 已正确指向 `array of MapFieldColumnVO`。
> - **2.x–5.x 尚未开始**：Gradle 模块接线、`aicode-web` 骨架、orval 生成链均未实施。本变更保持未归档状态。
