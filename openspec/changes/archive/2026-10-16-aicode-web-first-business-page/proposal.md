# Proposal

## Why

M0（`add-aicode-web-openapi-contract-chain`）已经让仓库具备一条可执行契约链：后端在测试期导出 → `contracts/openapi/openapi.yaml` 入库 → orval 生成 TS 类型与 axios 请求函数 → MSW handlers 提供桩数据 → `verifyApi` 守护契约 diff。但**这条链现在还没有任何消费者**——前端只能访问一个极简的"aicode web 骨架占位页"，还看不到任何后端真实形状。

没有消费者，契约质量、生成物可读性、开发体验都无从评估。M1 把第一个真实业务页面接到这条链上，让以下问题第一次得到可观测的答案：
- 生成的类型（如 `DisplayAttributeVO`、`mapFieldColumnVO`）是否好用？字段命名是否需要第二批微调？
- 生成的 axios 请求函数与生成错误响应解构是否顺手？
- MSW 在 React + Vitest 单元测试里走通还是需要在浏览器层拦截？
- Vite dev proxy 指向 8090 时与 Axum-style Suffix `.shtml` 的对接是否还要单独处理？

只有在这些答案拿到之后，M2 才好动笔把更多业务接过来。

## What Changes

- 在 `aicode-web/src/` 下新增一个**契约驱动的标准接入样板**：
  - 路由 `/displayAttributes` 列表页：用 orval 生成类型渲染后端 `GET /displayAttribute/list` 的响应；
  - 一个**控件分发器**：根据返回的 `displayType` 决定渲染 `input` / `select` / `date-picker` / 数字输入等基础控件；
  - 一个**请求封装层**：把 orval 生成的 `getDisplayAttributeList` 包一层，统一解构 `R.success` 并抛出业务错误，把"使用生成物"的样板代码抽象掉。
- Vitest 单元测试覆盖：
  - 解析 `R<DisplayAttributeVO[]>` + 错误分支（含 `R.failed` 路径）；
  - 控件分发器对 `displayType` 的全覆盖；
  - 请求封装层对 axios 错误响应的归一化。
- Vite dev proxy：把 `/api/*` 与 `/displayAttribute/*` 等带前缀的 API 请求转发到 `:8090`（与 Angular 旧前端约定的同源开发链路相容），便于联调。
- README 在 `aicode-web/README.md` 增加"如何接下一个端点"的章节，把这次踩出的样板沉淀为后续 M2/M3 的复用准则。

**BREAKING**：无。本变更不修改后端任何接口或字段，不修改 `api-contract` / `api-contract-artifact` / `api-data-object` 三份已有 spec。运行时也不会替换任何东西——13 MB 旧 Angular 7.0.4 产物仍为 `aicode` 的默认首页，M1 的新页面只在 `npm run dev` 下可达。

## Capabilities

### New Capabilities

- `aicode-web-page`：定义 aicode-web 中"契约驱动的业务页面"的可执行形状——前端 SHALL 从契约产物（`aicode-web/src/generated/**`）取得类型与请求函数，前端 SHALL NOT 直接手写对应类型；业务页面 SHALL 包含契约请求层、控件分发器或同类最小抽象；业务页面 SHALL 可被同源开发链路访问且 SHALL 在无后端时由 MSW 提供桩数据。本变更只引入这一能力的最小可验证切片。

### Modified Capabilities

（无。M0 已落地的 `api-contract-artifact` 与 `display-attribute-vo-contract` 的 `api-data-object` 仍覆盖所有契约层要求。`api-contract` Purpose 中关于"Angular 7.0.4 静态前端"的文字按既定登记留到 M4 切换运行时再做 MODIFIED。）

## Impact

- **代码**：新增 `aicode-web/src/pages/DisplayAttributeListPage/`（含 `index.tsx`、子组件、解构模块、控件分发器）与对应 Vitest 单测。修改 `aicode-web/vite.config.ts` 增 dev proxy；修改 `aicode-web/src/main.tsx` 注册路由；修改 `aicode-web/README.md`。
- **构建**：Vite dev 与 build 任务均无需新增依赖；当前 React 19 + TS 7 + react-router-dom 7 已满足。`npm run typecheck` 须继续过，且应能在缺少后端时由 MSW 提供桩数据。
- **不影响**：后端无变化、`aicode-web/src/generated/**` 不变、`contracts/openapi/openapi.yaml` 不变、`:aicode-web:verifyApi` 不变、13 MB 旧 Angular 产物不变。
- **风险**：极低。M1 完全发生在 dev 模式与单元测试层面，不进入 Spring Boot 静态资源服务的运行时路径。
