# aicode-web —— React 前端

前端类型、请求函数与 mock **全部由后端契约生成**，不手工维护。

## 契约链

```
后端 Controller  ──►  OpenApiExportIT（aicode 的测试）
                      │  调 /v3/api-docs.yaml
                      │  剥离 servers 块（随运行端口变化，入库会产生无意义 diff）
                      │  剥离无 schema 的装饰性参数条目（见下）
                      ▼
        contracts/openapi/openapi.yaml   ← 入库，可评审、可 diff
                      │
                      ▼
              orval（npm run generate）
                      ├─► src/generated/model/**        TypeScript 类型
                      ├─► src/generated/api.ts          请求函数
                      └─► src/generated/api.msw.ts      MSW mock handlers
```

**防漂移的价值**：后端改字段 → 契约变 → 前端类型变 → 用了旧字段的代码**编译失败**，
不必等到联调或运行时才发现。

### 契约漂移守卫

```bash
./aicode/gradlew :aicode-web:verifyApi
```

契约文件已入库，因此「当前内容」与「提交态」的 git 差异恰好等价于
「代码改了但契约没提交」。CI 应在提交后运行本任务。
⚠️ 代码尚未提交时运行会得到**假阴性**（git diff 为空）。

## 样式架构（三层，2026-10-16 视觉重写后）

页面的视觉与布局基准是 OpenDesign 项目「ai-code 页面设计」的原型。

```
src/styles/
  tokens.css    唯一 token 源 —— 色板 / 字体 / 5 条视觉规则。跨页改色只改这里
  base.css      跨页复用基元（.container .panel .stepper .data-table .grid-2 …）
  AppShell.css  topnav / 账户徽章
  <Page>.css    每页一个，只放该页专属的类
```

四条约定：

1. **类名沿用原型原名，不得重命名。** 验收方式是「逐页与原型并排比对」，
   改名会让比对失效。原型目录见
   `/Users/bruce/Library/Application Support/Open Design/namespaces/release-stable/data/projects/4cc16725-afb7-4882-bf88-fc8eec4f1b05/`
2. **新增跨页类必须先登记进 `base.css`。** 不许在页面级 `.css` 里私造 ——
   页面级 CSS 里定义的类会被别的页面「恰好引用」，形成无声的跨页耦合
   （本轮真实发生过：`.sr-only` 起初只定义在 `FrameworkListPage.css`，
   却被另两页引用，等于那两页的可用性依赖技术架构页的样式被加载）。
3. **TSX 中禁止用于布局的内联 `style={{}}`。** 只允许动态值
   （当前唯一一处：SQL 解析进度条的宽度百分比）。
4. **`@keyframes` 全局同名会互相覆盖。** 同名 keyframes 在同一文档里
   「后者全局生效」，两个页面各写一个会静默改掉对方的动画
   （本轮真实发生过：`BuildLogPanel.css` 与 `ProjectListPage.css` 都叫 `pulse`，
   已改名 `status-pulse`）。

### 死按钮规则

契约缺入参、后端能力未落地的操作（当前：`framework/build`、`framework/delete`、
`project/repository/*`、`project/framwork/add`、`generated-files` 目录树），
一律**渲染完整视觉但 `disabled` + `title="接口待修复，验收后接入"`，不绑 `onClick`**。
不做「看着能点、点了没反应、无任何提示」的按钮。

### 与契约相关的已知缺口

原型是照着 **M3-C 裁剪前**的完整 `Project` 实体设计的。当前两个读投影里
缺原型要用的字段，导致下列 UI 永久无内容（等契约补字段后自动点亮）：

| 位置 | 原型要显示 | `ProjectSummaryVO`（5 字段） | `ProjectDetailVO`（12 字段） |
|---|---|---|---|
| 工作台卡片标签 | 语言 / 数据库 | ✗ 缺 `language`、`databaseType` | — |
| 工作台卡片副标 | Java 包名 | ✗ 缺 `basePackage` | — |
| 项目确认 8 栅格 | 数据库类型 / SQL 类型 | — | ✗ 缺 `databaseType` |

填表时**不要**为此手写 axios 或绕过契约 —— 按上面第 1 条规则，先改后端。

## 命令

| 命令 | 说明 |
|---|---|
| `npm install` | 安装依赖（首次；生成 `package-lock.json`） |
| `npm run dev` | 开发服务器（:5173），API 与 WebSocket 代理到 :8080 |
| `npm run build` | 生产构建 → `build/dist` |
| `npm run preview` | 预览构建产物 |
| `npm run generate` | 由契约重新生成类型 / 请求函数 / mock |
| `npm run typecheck` | 类型检查 |
| `npm test` | Vitest 组件测试 |

### Gradle 集成

`aicode-web` 是 Gradle 模块，其 Jar 内部布局为 `static/**`。
`aicode` 以 `implementation project(':aicode-web')` 引入后，
Spring Boot 会从 classpath 的 `/static/` 提供页面。

**node 是弱依赖**：找不到 node 但存在 `build/dist` 时复用旧产物；
两者都缺失时构建**失败并提示先执行 `./aicode/gradlew :aicode-web:buildUi`**
—— 避免产出「不含前端」的制品却报成功。

## ⚠️ 两条须知

### 1. orval 生成的 mock 只覆盖「契约形状」

自动生成的 MSW mock 由契约的 example schema 推导，适合兜底**结构**，
但**业务语义场景（边界值、时序、空态）几乎都需要手工 override**。
不要因为「已经生成了 mock」就以为不需要写测试数据。

### 2. 契约里被剥离的 119 个参数条目是错的，已登记待修

导出的 163 个参数中有 119 个缺少 `schema`（违反 OpenAPI 规范，orval 会拒绝生成）。
它们全部来自源码里 24 处 `@Parameters({@Parameter(name=..., description=...)})` 块，
且**语义错误** —— 例如：

```java
public R<Frameworks> updateIsPublic(@Parameter(hidden = true) Frameworks frameworks)
```

方法签名里根本没有 `code` / `isPublic` 参数（后端从请求体读），
注解却把它们声明成 query 参数。导出时剥离它们等于**移除错误信息**。

**待修**：这些 `@Parameters` 块应逐个复核 —— 删除与签名不符者、
为确有其事者补 `schema = @Schema(type = ...)`。

## M2 已对齐的端点形状

为方便 M2 阶段在"如何接下一个业务端点"样板之上接入 `/login/signin`
与 `/project/list`，相关后端已做以下准备：

- **`GET /login/signin`** —— 详见 `openspec/specs/auth-account/spec.md`（既有能力）。
  当前契约已含 `RString` 出参，前端将取得 JWT 字面。
- **`GET /project/list`** —— 已在 `2026-10-17-project-list-vo-contract` 变更中由
  `R<Page>` 改造为 `R<PageVO<ProjectVO>>`。契约里 `data.records` 的元素类型
  已经是 `ProjectVO`（22 字段），`aicode-web/src/generated/model/` 下
  `projectVO.ts` / `pageVOProjectVO.ts` / `rPageVOProjectVO.ts` 都已生成。

- **`GET /framework/list`** —— 已在 `2026-10-18-framework-list-vo-contract` 变更中由
  `R<Page<FrameworksPageVO>>` 收敛为 `R<PageVO<FrameworkSummaryVO>>`（7 字段，无 Git 口令）。
  **M5-A2（`2026-10-18-framework-list-page`）已接入前端**：`/frameworks` 路由渲染
  4 列表格（`code` / `name` / `description` / `isPublic`）+ 分页信息。

接入这些端点的具体业务步骤与 M1 相同，按"How to wire next endpoint"
五步走即可。

## 受保护端点（鉴权接入）

`aicode-web` 已具备完整前端登录态管理（详见
`openspec/specs/aicode-web-auth/spec.md`），接入一个"受保护"的业务页面
需依次：

1. **在路由表里用 `<RequireAuth>` 包裹**：`App.tsx` 中把 `<Route path="..." element={<RequireAuth>...</RequireAuth>}>` 即可；无 token 时跳 `/login`，并把原路径通过 `state.from` 传给 LoginPage（M3 一并接）。
2. **页面里调 `getToken()` 显式填 `params.token`**：仓库 `orval` 生成的 `list2({ token, ... })` 仍然要求参数形式 `token`，调用方用 `getToken() ?? ""` 填充；axios 拦截器同时会在 `Authorization: Bearer <token>` 头里再带一份，双轨兼容。
3. **`auth-interceptor` 已由 `main.tsx` 启动时一次性安装**，业务代码不必显式关心，但**新增依赖 axios 的拦截器场景**（如 `manualCreate`）需自己调 `installAuthInterceptor()`。
4. **MSW handler 必须在 `test-setup.ts` 注册**：与 M1 通用规则一致——`getSigninMockHandler` / `getList2MockHandler` 等都已注册，新增端点接时按 neighbors 原则一起 append。
5. **失败以 `ContractError` 气泡呈现**：与 M1 `DisplayAttributesPage` 一致——`unwrapSuccess` 失败抛 `ContractError`，被 `unwrapSuccess` 后的 `.catch(err: ContractError)` 捕获，写入 `{kind:'error'}` 状态后渲染 `<p role="alert" data-testid="...">`。

**`signin` 端点不在守卫范围**：登录接口本身不被 `<RequireAuth>` 包裹（避免循环跳），登录成功后由 `LoginPage` 调 `useAuth().signIn(token)` 后跳原路径。

## 实时推送（WebSocket）

新增 `aicode-web-realtime` 能力（`src/lib/realtime/`）让前端可以消费后端 ws 推送
的实时日志。当业务页面要"按下按钮触发后台 + 实时查看该后台进度"时，按下面四步：

1. **后端**：仓库 ws 路径 `/websocket.shtml`，前缀 `# `；后端代码用
   `WSClientManager.sendMessage(...)` 推送 free-form 文本（无需约定 sentinel）。
2. **`ws-client`**（`src/lib/realtime/ws-client.ts`）：薄 `connect(url, onMessage)` 包装，
   自动注入 `?token=<token>` URL 参数（`auth-account` R5 URL 参数兼容分支），
   首连接掉线 500ms 后一次重连。
3. **`useBuildLog(projectCode)`**（hook）：`startStream()` 调 `api.execute({ code })`
   → 经 `unwrapSuccess` 验真 → 启动 ws；ws 推送逐条进 `entries`，4 状态机
   `idle | running | error`（详见 spec）。
4. **`<BuildLogPanel projectCode />`**（UI）：组件 mount 时自动调 hook 的 `startStream`；
   `idle` 时 `return null`，`running/error` 渲染日志面板（标题 + 已运行时长 + 倒序条目 + 关闭按钮）。

**新接实时页面仅需**：
1. 路由表按"受保护端点"规则包 `<RequireAuth>`；
2. 业务页面 import `{ useBuildLog, BuildLogPanel } from '<lib>/realtime/...'`；
3. 触发构建后 render `<BuildLogPanel projectCode={...} />`；
4. 测试用 `vi.mock('<lib>/realtime/ws-client', ...)` 把 ws-client 替身（jsdom 不实现 WebSocket）。

## 如何接下一个业务端点（M1 沉淀的样板）

`DisplayAttributesPage` 是契约链上**第一个**真实业务页面，把"读取契约生成物 →
触发请求 → 解构 `R` → 字段渲染"的整套样板沉淀了下来。后续接入（login / 项目
列表 / 工程 job 等）按下面五步走，每步都对应一处固定代码位置：

1. **在 `contracts/openapi/openapi.yaml` 找到 path**
   - 路径以 `/<domain>/<action>` 形式出现。后端 Controller 改完之后已自动入库；
     手动加端点不更新契约是反模式。
2. **拷一份 orval 生成的请求签名作为入口**
   - 在 `src/generated/api.ts` 找 `getAicodeRESTfulAPIs().<opName>(params)`；
     这是 orval 从 `operationId` 派生的固定标识。
   - 业务**不**手写 `axios.get(...)`，**不**复制 `AxiosResponse<R<T>>` 接口。
3. **包 `unwrapSuccess` 取 `data`**
   - 从 `src/lib/contract-response` 导入 `unwrapSuccess`，
     自动从 `resp.data.data` 推导返回类型；失败抛 `ContractError`，
     含 `code` 与 `info`。
4. **决定是否要走 `<DisplayControl>` 分发**
   - 表单/输入场景：把 `displayAttribute` 字段包到 `<DisplayControl>` 里。
   - 表格/展示场景：直接渲染 VO 字段即可，不需要分发器。
   - 新增一种 `displayType` 支持时，在 `src/lib/display-controls/index.tsx`
     的 `CONTROLS` 表里加一行 + 新建一个最小受控组件。`unknown.tsx` 是兜底。
5. **加 MSW handler 与单测**
   - 第一步：在 `src/test-setup.ts` 追加 `get<OpName>MockHandler()`（已有端点也
     可一并预注册，避免后续页面忘注册导致穿透网络）。
   - 第二步：在测试里用 `server.use(get<OpName>MockHandler(fixedResp))` 覆盖
     特定数据；不覆写就吃 orval 默认的 faker mock。
   - 第三步：渲染 → `findByText` 验证成功路径；让 mock 返回 `success=false`
     验证 `ContractError` 的 `info` 进入错误气泡。

**反向验证**：临时在 `src/test-setup.ts` 把 `beforeAll(() => server.listen())` 注释
掉，跑 `npm test`，应当看到 `AxiosError: Network Error` —— 证明 MSW 真的在拦。
恢复 listen 后 17 用例全绿。
