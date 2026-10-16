# aicode-web-page Specification

## Purpose

定义 aicode-web 中"契约驱动的业务页面"的可执行形状：业务页面 SHALL 消费 `aicode-web/src/generated/**` 下由 orval 从 `contracts/openapi/openapi.yaml` 派生的产物，SHALL NOT 在前端代码中为同一端点另行手写对应类型或请求签名；业务页面 SHALL 把"解析统一响应 `R`"与"按展示属性分发控件"这两类样板抽象为可复用单元；业务页面 SHALL 在单元测试中由 MSW 提供桩数据，SHALL NOT 依赖后端可访问方可通过烟测。本能力建立的样板是后续所有页面的复用准则。

## Requirements

### Requirement: 业务页面 SHALL 消费契约生成物而非手写对应类型

业务页面 SHALL 引用 `aicode-web/src/generated/model/**` 下由 orval 为后端接口产出的类型，SHALL NOT 为同一端点另行手写等价类型；引用该端点生成的请求函数，SHALL NOT 用通用 axios 或 `fetch` 自行拼接 URL、参数序列化与响应拆包。

#### Scenario: 端点字段被改名前端编译失败

- **WHEN** 后端修改了 `GET /displayAttribute/list` 的响应字段（同时按既有 `api-contract-artifact` 流程更新了入库契约，并由 orval 重新生成类型），而业务页面仍按旧字段名读取该字段
- **THEN** 执行 `npm run typecheck` SHALL 失败，错误 SHALL 指向被改名的字段，SHALL NOT 让错误延后到运行时

#### Scenario: 新增端点无需手写请求函数

- **WHEN** 后端新增一个端点并随之更新入库契约与生成物
- **THEN** 业务页面 SHALL 仅导入生成物中的对应函数即可调用该端点，SHALL NOT 在源码内出现为该端点新建的 URL 串、参数对象或请求封装

### Requirement: 业务页面 SHALL 把统一响应解析与控件分发抽象为可复用单元

业务页面 SHALL 使用薄封装层将 `R.success.data` 取出并把 `R.failed` 转为标准错误；展示属性 SHALL 通过统一的「控件分发器」按 `displayType` 渲染对应控件，SHALL NOT 在每个字段处用 `if/else` 重复硬编码字段名与控件的映射。

#### Scenario: R 的成功路径直接吐出 data

- **WHEN** 后端返回 `R` 形态的成功响应（含 `code: "0000"` 与 `data` 字段）
- **THEN** 封装层 SHALL 把 `data` 直接交给调用者，且 SHALL NOT 把 `R` 整体透传给上层组件

#### Scenario: R 的失败路径被归一化抛出

- **WHEN** 后端返回 `R.failed` 形态的响应（`success: false`）或 HTTP 层错误
- **THEN** 封装层 SHALL 抛出含 `code` 与 `info` 的错误对象，调用方 SHALL 可凭类型保护访问这两个字段

#### Scenario: 控件分发器按 displayType 选控件

- **WHEN** 业务页面给出一个具体的 `displayType` 字符串
- **THEN** 控件分发器 SHALL 渲染对应的基础控件（至少覆盖 `text` / `number` / `date` / `select` 四类），未识别的 `displayType` SHALL 渲染为带可见降级标识的安全占位，SHALL NOT 让浏览器抛未捕获异常

#### Scenario: 控件选择不依赖字段名硬编码

- **WHEN** 后端给一个字段新增一种 `displayType` 值
- **THEN** 前端业务页面 SHALL 通过修改控件分发器中的映射即可支持，SHALL NOT 在每个组件里出现"按字段名判断"的代码

### Requirement: 业务页面的可观测路径在无后端情况下 SHALL 由 MSW 提供桩数据

业务页面 SHALL 在单元测试中由 MSW handlers 拦截网络请求；handlers SHALL 来自 `aicode-web/src/generated/api.msw.ts`（与正式契约同源）；新增端点的联调 SHALL NOT 要求先访问后端，方可通过单元测试层的烟测。

#### Scenario: 单元测试 MSW 拦截并返回契约形状数据

- **WHEN** 业务页面对一个受 MSW 拦截的端点发起请求
- **THEN** 该请求 SHALL 在测试进程内被 MSW 拦截，MSW 返回的数据 SHALL 与契约 schema 一致，SHALL NOT 让请求穿透到真实网络

#### Scenario: MSW handlers 与契约同源

- **WHEN** 业务页面新增测试
- **THEN** 测试 SHALL 引用 `src/generated/api.msw.ts` 中由 orval 生成的 handler，SHALL NOT 在测试代码或 `__mocks__` 中为同一端点另行编写手工桩数据

### Requirement: 同源开发链路 SHALL 把前端 dev server 的 API 请求转发到本地后端

开发期 SHALL 通过同源代理把前端 dev server 的相对 API 请求转发到本机后端的 `:8080`（与 AGENTS.md §4「应用端口 8080」一致；也是 Angular 旧前端既定的同源约定）；后端 SHALL 沿用其既有的 `.shtml` 兼容层与统一响应 `R`。

#### Scenario: dev 模式下请求被代理到本地后端

- **WHEN** 业务页面在 `npm run dev` 模式下以同源相对路径请求一个后端端点
- **THEN** 该请求 SHALL 经 dev proxy 转发到本地后端并得到与直接访问后端一致的响应，SHALL NOT 出现 CORS 错误或 `Failed to fetch`

#### Scenario: 同源代理不影响构建产物

- **WHEN** 执行 `npm run build` 产出 `aicode-web/build/dist/static/**`
- **THEN** 产物 SHALL 不含任何 dev proxy 配置，SHALL 与生产静态托管兼容

### Requirement: 详情类业务页面 SHALL 通过 URL 参数驱动 unwrapSuccess 消费 R 包络

按 URL 参数（如 `:code`）定位单条资源的详情页 SHALL 用 `useParams` 取得参数，调用契约产物中的对应请求函数（如 `loadByCode(code)`），经 `unwrapSuccess(resp)` 拆出 `R<T>` 中的 `T`，再交给组件渲染；当 `R.data` 为 `null`（找不到 code 但 success） SHALL 不报错、不渲染"5xx-like"内容、而是以友好"项目不存在"占位提示。

本 Requirement 把现有 Requirement 「业务页面 SHALL 把统一响应解析与控件分发抽象为可复用单元」延伸到"按 URL 参数驱动的详情页"场景，与列表 / 详情 / 占位升级为详情三类页面形态共同覆盖样板层。

#### Scenario: 详情页通过 useParams 取 :code 并 unwrapSuccess 消费 R<ProjectVO>

- **WHEN** 用户访问 `/project/{code}` 详情页（如 `/project/1882812765213425001`）
- **THEN** 页面 SHALL 用 `useParams<{ code: string }>()` 取 `:code`，调 `api.loadByCode(code)`，经 `unwrapSuccess(resp)` 取 `ProjectVO`，再以表格渲染关键字段（`code` / `name` / `englishName` / `description` / `databaseType` / `language` / `state` / `copyright` / `author` / `phone` / `basePackage` / `buildNumber` / `isRepository` / `isParseTable` / `isParseClass` / `createTime` / `updateTime` / `accountCode`）
- **AND** 加载与错误状态分别渲染"加载中…"与 `<p role="alert" data-testid="project-detail-error">`；SHALL NOT 让"找不到 code"路径走 NPE

#### Scenario: 找不到 code 但 R.code === "0000" 应被识别为 业务空态而非失败

- **WHEN** `loadByCode(code)` 返回 `R{ success: true, code: "0000", data: null }`（M3-A-1 引入的"找不到 project"早返路径）
- **THEN** 详情页 SHALL 渲染一个友好"项目不存在"占位（或等价文案），SHALL NOT 显示错误气泡 `data-testid="project-detail-error"`
- **AND** 业务上视作"业务空态"而非"鉴权失败"或"服务器错误"

#### Scenario: 详情页与 MSW 单测无缝接入

- **WHEN** 单元测试渲染 `ProjectDetailPage` 并用 `getLoadByCodeMockHandler` 覆盖一组固定 `RProjectVO` 数据
- **THEN** 表格 SHALL 含种子项目的 `code` / `name` 等关键字段，SHALL NOT 让 MSW 拦截穿透到真实网络
- **AND** 反向验证：当响应体**不**满足 R 包络（缺 `success` / `code` / `success` ≠ true） 时，详情页 SHALL 显示错误气泡 `data-testid="project-detail-error"`，SHALL NOT 渲染 `data-testid="project-detail-content"` —— 该反向证明本 Requirement 与 `api-contract` R 包络约束联动，确保前端消费层能在接口违规时立刻可见

### Requirement: 列表类业务页面 SHALL 通过 mount 即拉取并 unwrapSuccess 消费 PageVO<T> 包络

以分页列表为形态的业务页面（无 URL 参数驱动，由页面自身在 mount 时拉取）SHALL 用 `useEffect` + `useState` 在组件 mount 时调用契约产物中的分页请求函数（如 `api.list4({ curPage, pageSize })`），并经 `unwrapSuccess(resp)` 拆出 `PageVO<T>` 的 `data`，再逐项渲染 `records`。

页面 SHALL 呈现 `totalRow` / `curPage` / `totalPage` 三个分页信息（用户需要知道"共多少条 / 当前第几页 / 共几页"），SHALL NOT 只渲染列表而隐藏总量 —— 这会让用户误判数据是否完整。

#### Scenario: 列表页 mount 即拉取并渲染 records

- **WHEN** 用户访问列表页（如 `/frameworks`）且 `useAuth.isAuthenticated === true`
- **THEN** 组件 SHALL 在 mount 时调 `api.list4({ curPage: 1, pageSize: N })`，经 `unwrapSuccess(resp)` 取 `PageVO<T>`，渲染 `data.records[]` 每一项
- **AND** SHALL 同时渲染 `totalRow` / `curPage` / `totalPage` 三个分页信息

#### Scenario: records 为空时 SHALL 显示业务空态而非错误

- **WHEN** 后端返回 `R{ success: true, code: "0000", data: { records: [], totalRow: 0 } }`
- **THEN** 页面 SHALL 渲染"（暂无数据）"之类的空态文案
- **AND** SHALL NOT 渲染 `data-testid="...-error"` 错误气泡 —— 空列表不是失败

#### Scenario: R.code !== "0000" SHALL 走错误气泡且不渲染 records

- **WHEN** 后端返回 `R{ success: false, code: "9999", info: "..." }`（HTTP 仍 200）
- **THEN** 页面 SHALL 经 `unwrapSuccess` 抛出 `ContractError` 并渲染含 `info` 的错误气泡
- **AND** SHALL NOT 渲染任何列表行

#### Scenario: 分页字段名 SHALL 是 records 而非 voList

- **WHEN** 页面渲染分页数据
- **THEN** SHALL 读取 `data.records`；SHALL NOT 读取 `data.voList`（后者是历史 `Page<E>` 容器的字段名，已由 `api-contract` 的「分页接口 SHALL 统一用 PageVO<T>」Requirement 收敛掉）

### Requirement: 列表页翻页 SHALL 重新拉取并更新表格，且边界 SHALL 禁用按钮

分页列表页面 SHALL 把当前页码（`curPage`）作为组件状态持有（而非常量），并把它列入数据拉取 effect 的依赖；用户点击「上一页 / 下一页」时 SHALL 更新该状态并触发重新拉取，表格内容 SHALL 被新页数据替换（SHALL NOT 保留上一页的可见行）。

翻页控件 SHALL 在边界禁用：首页时「上一页」SHALL 禁用，末页（`curPage >= totalPage`）时「下一页」SHALL 禁用；SHALL NOT 因点击边界按钮而发出越界页码请求。

切换页码期间页面 SHALL 进入加载态，SHALL NOT 让"上一页的表格数据"与"新页码的分页信息"同时呈现（避免瞬时错配误导用户）。

该 Requirement SHALL 在**每个**分页列表页面独立成立，不因页面附带额外交互（行内构建等）而弱化。页面内的其它交互状态（构建中的行集合、已展开的行等）SHALL 跨翻页保留 —— 用户翻走再翻回时，先前的交互状态 SHALL 仍然有效。

#### Scenario: 点击下一页 SHALL 重新拉取并替换表格内容

- **WHEN** 用户在第 1 页点击「下一页」，且 `totalPage > 1`
- **THEN** 组件 SHALL 以 `curPage = 2` 重新调用分页请求函数（请求参数中的页码 SHALL 为 2）
- **AND** 加载完成后表格 SHALL 展示第 2 页的数据，SHALL NOT 仍显示第 1 页的行

#### Scenario: 翻页边界 SHALL 禁用对应按钮且不发出越界请求

- **WHEN** 组件处于第 1 页
- **THEN** 「上一页」按钮 SHALL 为禁用态（`disabled`）
- **AND** 当组件处于末页（`curPage === totalPage`）时「下一页」按钮 SHALL 为禁用态
- **AND** 禁用态按钮被点击 SHALL NOT 触发任何数据请求

#### Scenario: 翻页期间 SHALL 进入加载态，不呈现旧页数据与新页码的错配

- **WHEN** 用户点击翻页按钮
- **THEN** 在新页数据到达前，页面 SHALL 呈现加载态，SHALL NOT 同时显示上一页的表格行与新页码的分页信息

#### Scenario: 页面内其它交互状态 SHALL 跨翻页保留

- **WHEN** 用户在第 1 页对某项目触发"构建"（其 code 被记入页面的"构建中"集合），随后翻到第 2 页再翻回第 1 页
- **THEN** 翻回后该项目的"构建"按钮 SHALL 仍呈"构建中…"禁用态
- **AND** 该状态 SHALL NOT 因翻页而被清空

### Requirement: 写侧表单页面 SHALL 提交契约生成物驱动的表单并消费写侧 VO

创建 / 修改类业务页面 SHALL 提供表单提交能力，其字段 SHALL 来自契约生成物（orval 生成的参数类型）SHALL NOT 手写平行类型；提交后的响应 SHALL 按既有统一响应规则经 `unwrapSuccess` 解包消费。

表单 SHALL 覆盖后端声明的全部必填参数：后端以 `Assert.hasText` 校验的每个字段，前端 SHALL 有对应的输入控件；SHALL NOT 让用户提交后才被后端以参数为空的错误码拒绝。

提交失败时页面 SHALL 呈现可读的错误信息（沿用既有 `ContractError` → 错误气泡的链路），SHALL NOT 静默失败。提交失败时用户已填的其它字段 SHALL 保留，SHALL NOT 被清空。

提交成功后 SHALL 导航到能确认结果的页面（详情页或列表页），SHALL NOT 停留在原地让用户不知道是否成功。

> **当前所有已接入页面都是只读的** —— 本 Requirement 是该能力**第一个覆盖写侧**的场景。此前 7 个页面无一有提交能力。

#### Scenario: 表单字段集合 SHALL 覆盖后端全部必填参数

- **WHEN** 一个写侧端点声明了 N 个必填参数
- **THEN** 对应表单页面 SHALL 提供 N 个输入控件，且每个控件 SHALL 绑定到契约生成物里的对应参数名
- **AND** SHALL NOT 出现前端自有、后端不接收的参数（会污染请求体）

#### Scenario: 提交成功 SHALL 导航到可确认结果的页面

- **WHEN** 用户填完必填字段并提交，后端返回 `R.success`
- **THEN** 页面 SHALL 导航到该实体的详情页或列表页
- **AND** 新建的实体 SHALL 能在目标页面被看到

#### Scenario: 提交失败 SHALL 呈现可读错误而非静默

- **WHEN** 后端返回 `R.failed`（如必填参数为空 `9003`）
- **THEN** 页面 SHALL 呈现错误气泡，内容含后端返回的 `info` 文案
- **AND** 用户已填的其它字段 SHALL 保留，SHALL NOT 被清空
