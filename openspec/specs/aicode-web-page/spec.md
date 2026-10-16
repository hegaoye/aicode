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
