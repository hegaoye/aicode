# api-contract Specification

## Purpose
定义 aicode 后端 REST 接口与前端模块之间的可观测契约。

M4 之前：前端为 `src/main/resources/static/` 下的 Angular 7.0.4 + ng-zorro + jQuery 打包产物，无法在 Java 侧做类型检查，因此所有跨语言约定（ID 序列化形态、路径后缀、响应结构、日期格式）必须由后端以可执行测试的方式显式守护。

M4 之后（`feature/20261016/react-web-rewrite` 分支 + `2026-10-19-m4-cutover-to-react` 变更）：前端为 `aicode-web/` 模块下的 React 19 + TypeScript 实现，跨语言约定改由「`contracts/openapi/openapi.yaml` 契约文件 + orval 生成的 TypeScript 类型 + Vitest 跑 MSW mock」共同守护：① 后端契约 schema 由 `OpenApiExportIT` 在测试期自动导出并与入库 schema diff 守护；② 前端类型与请求函数由 orval 在 `aicode-web/build` 时从契约 schema 重生成；③ 前端 mock 与单测由 MSW handler（`aicode-web/src/generated/api.msw.ts`）拦截；④ 后端契约层仍以 `ApiContractIT` 集成测试守护（响应结构 + 错误码 + 日期格式 + 标识符字符串化）。本能力在 M4 后**同时**承担"契约 schema 守门"与"跨语言约定守门"两个角色。

本能力在 M0 / M1 / M2 / M3 阶段是 Java 25 / Spring Boot 4.1 升级系列变更的验收基准；在 M4 之后是"React 前端落地 + 切运行时"系列变更的验收基准。

## Requirements

### Requirement: 标识符以 JSON 字符串形式序列化

系统 SHALL 将所有 `Long` / `long` 类型的字段（尤其是主键 `id` 与业务编码 `code`）序列化为 JSON **字符串**，SHALL NOT 输出为 JSON 数字。原因：业务编码为 19 位雪花值，且历史数据中的主键同样为 19 位，二者在过渡期内共存；统一字符串化可保证任何取值都不会被客户端的 `JSON.parse` 截断。

序列化层的实现 SHALL 使用当前运行时的默认 JSON 库提供的扩展点来注册转换规则，SHALL NOT 通过构造一个与默认序列化链路无关的独立映射实例来实现——后者会导致注册静默失效。

#### Scenario: 列表接口的 id 为字符串

- **WHEN** 客户端请求任意列表接口并读取 `data.records[]` 中每一项的 `id`
- **THEN** 每个 `id` 的 JSON 类型 SHALL 为字符串，SHALL NOT 为数字

#### Scenario: 标识符精度不被截断

- **WHEN** 系统返回一个 19 位数字的标识符
- **THEN** 返回的字符串 SHALL 与数据库中的原值逐字符相同，SHALL NOT 出现末位被改写为 `0` 的情况

#### Scenario: 标识符形态为纯数字

- **WHEN** 系统生成业务编码 `code`
- **THEN** 该值 SHALL 匹配正则 `^\d{1,19}$`

#### Scenario: 装箱与基本类型均被覆盖

- **WHEN** 某字段声明为 `Long`（装箱类型）或 `long`（基本类型）
- **THEN** 两种声明 SHALL 均被序列化为字符串

#### Scenario: 新生成的自增主键同样是字符串

- **WHEN** 系统新增一条记录（主键为自增小整数）后返回该记录
- **THEN** 其主键的 JSON 类型 SHALL 仍为字符串

#### Scenario: 转换规则确实作用于默认序列化链路

- **WHEN** 运行时切换到新的默认 JSON 库
- **THEN** 已注册的字符串化规则 SHALL 仍然生效，SHALL NOT 出现「注册成功但响应中仍为数字」的静默失效

### Requirement: 路径后缀兼容能力

系统 SHALL 支持前端使用的 `.shtml` 路径后缀。带后缀路径与无后缀路径 SHALL 指向同一处理逻辑。该能力 SHALL 由平台自身提供的兼容层实现，SHALL NOT 依赖框架的默认路由匹配行为——后者在框架版本间已发生变更。

兼容层 SHALL 只作用于平台已注册的 API 路径前缀，SHALL NOT 改写静态资源的解析路径。

#### Scenario: 带后缀路径可达

- **WHEN** 客户端以 `/project/list.shtml` 形式请求任意已注册端点
- **THEN** 系统 SHALL 返回与无后缀请求 `/project/list` 完全一致的状态码与响应体

#### Scenario: 无后缀路径仍可达

- **WHEN** 客户端以 `/project/list` 形式请求任意已注册端点
- **THEN** 系统 SHALL 正常处理该请求

#### Scenario: 后缀兼容不覆盖静态资源

- **WHEN** 请求的资源路径以 `.css` / `.js` / `.svg` 等静态资源后缀结尾
- **THEN** 兼容层 SHALL NOT 改写该路径

#### Scenario: 后缀兼容不改变拦截器匹配

- **WHEN** 兼容层剥离了 `.shtml` 后缀
- **THEN** 鉴权拦截器的路径匹配 SHALL 在剥离后的路径上进行，与无后缀请求一致

#### Scenario: WebSocket 端点带后缀可达

- **WHEN** 客户端以 `/websocket.shtml` 连接 WebSocket 端点
- **THEN** 连接 SHALL 成功建立

#### Scenario: 兼容层不引入前缀外副作用

- **WHEN** 请求路径不在平台已注册的 API 前缀白名单内
- **THEN** 兼容层 SHALL NOT 改写该路径，即使它以 `.shtml` 结尾

### Requirement: 统一响应结构

系统 SHALL 始终使用统一响应包装结构返回结果，包含 `code`（业务错误码）、`info`（提示信息）、`data`（业务数据）、`success`（布尔标记）四个键。`data` 为空时 SHALL 以 `null` 呈现，SHALL NOT 省略该键。

#### Scenario: 成功响应结构

- **WHEN** 任意接口返回成功
- **THEN** 响应体 SHALL 同时含 `code` / `info` / `data` / `success` 四个键，且 `success` 为 `true`

#### Scenario: 失败响应使用标准错误码

- **WHEN** 任意接口返回业务失败
- **THEN** `code` SHALL 取自平台标准错误码集合（`9003` / `9004` / `9007` / `9999` 等），`success` 为 `false`

#### Scenario: AJAX 请求失败时返回 JSON 而非 HTML

- **WHEN** 前端以 AJAX 方式请求接口且请求失败
- **THEN** 响应 SHALL 为 JSON 格式的错误结构，SHALL NOT 返回 HTML 登录页

### Requirement: 日期格式与时区

系统 SHALL 以固定格式 `yyyy-MM-dd HH:mm:ss` 序列化所有日期字段，时区固定为 GMT+8，SHALL NOT 输出 ISO 8601 格式或时间戳数值。

#### Scenario: 日期字段格式

- **WHEN** 响应中包含日期字段
- **THEN** 该字段 SHALL 匹配正则 `^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$`

#### Scenario: 日期不以时间戳形式输出

- **WHEN** 响应中包含日期字段
- **THEN** 该字段 SHALL 为字符串，SHALL NOT 为 JSON 数字

#### Scenario: 时区固定为 GMT+8

- **WHEN** 服务端构造日期值
- **THEN** 该值 SHALL 按 GMT+8 时区解释与序列化，SHALL NOT 依赖服务器本地时区

### Requirement: null 字段保留

系统 SHALL 在序列化响应对象时保留值为 `null` 的字段，SHALL NOT 因全局序列化配置而省略这些键。

#### Scenario: 空值字段仍出现在 JSON 中

- **WHEN** 响应对象的某字段值为 `null`
- **THEN** 序列化后的 JSON SHALL 包含该键且值为 `null`，SHALL NOT 直接省略该键

#### Scenario: 响应顶层 data 为 null 时保留

- **WHEN** 接口返回成功但无业务数据
- **THEN** 响应体 SHALL 包含 `"data": null`

#### Scenario: 默认序列化库换代后语义不变

- **WHEN** 默认 JSON 库发生版本升级
- **THEN** null 字段的保留行为 SHALL 与切换前一致

### Requirement: 契约测试在升级前先行落地

系统 SHALL 提供可执行测试覆盖上述所有跨语言约定，且这些测试 SHALL 在每次框架升级**之前**就已全部通过，作为升级验收的基准。

#### Scenario: 契约测试在升级前全绿

- **WHEN** 框架升级前的基线提交
- **THEN** 全部契约测试 SHALL 通过

#### Scenario: 升级引入契约回归时测试失败

- **WHEN** 某次框架升级改变了响应结构、日期格式或标识符序列化
- **THEN** 对应契约测试 SHALL 失败，SHALL NOT 静默通过

#### Scenario: 前端端点清单被显式登记

- **WHEN** 新增或删除前端调用的端点
- **THEN** 该端点 SHALL 在端点清单中被显式登记，可通过交叉校验断言发现遗漏

### Requirement: 统一响应包装的 data SHALL 携带真实结构

系统 SHALL 以泛型 `R<T>` 声明接口出参类型，使 `data` 字段在 OpenAPI 契约中携带 `T` 的真实结构，SHALL NOT 为空对象 `{}`。

#### Scenario: 契约中的 data 有具体 schema

- **WHEN** 某接口声明 `R<SomeVO>`
- **THEN** 契约中该接口响应的 `data` 节点 SHALL ref 到 `SomeVO` 的 schema，SHALL NOT 为 `{}`

#### Scenario: 列表接口的 data 表达元素数组

- **WHEN** 某接口声明 `R<List<XxxVO>>`
- **THEN** 契约中 `data` SHALL 为 array of `XxxVO`

#### Scenario: 无数据接口的 data 表达为空

- **WHEN** 某接口声明 `R<Void>`
- **THEN** 契约中 `data` SHALL 为空或无结构（不携带误导性的具体 schema）

### Requirement: 出参形状 SHALL 由声明类型承载而非运行期推断

系统 SHALL 以方法签名上的泛型返回类型承载出参形状，SHALL NOT 依赖运行期反射推断，SHALL NOT 让同一方法的不同分支返回不同形状而不显式收窄。

#### Scenario: 方法签名即出参契约

- **WHEN** 某 Controller 方法声明 `R<XxxVO>`
- **THEN** springdoc 导出的契约 SHALL 以 `XxxVO` 作为该方法的 `data` schema

#### Scenario: 分支形态不一致时显式收窄

- **WHEN** 某方法的多个返回分支形状不同
- **THEN** 该方法的返回类型 SHALL 显式表达二者的共同类型，SHALL NOT 让契约出现二义

### Requirement: 泛型化 SHALL NOT 改变运行期 JSON 结构

将响应包装改为泛型 SHALL 仅影响编译期类型检查，SHALL NOT 改变任何接口在运行期返回的 JSON 字段名、字段类型与取值。

#### Scenario: 泛型化后响应结构不变

- **WHEN** 完成泛型化并执行既有的接口契约测试
- **THEN** 全部响应结构断言 SHALL 通过，SHALL NOT 因签名变化而出现差异

### Requirement: 受保护 REST 接口 SHALL 走 R<T> 包装而非裸实体出参

凡受保护（受 `LoginInterceptor` 拦截）的 REST 接口，其 Controller 方法的出参 SHALL 是 `R<T>` 形式（`T` 为 VO 或 PageVO<T>），SHALL NOT 直接序列化持久化实体或自定义类。本 Requirement 是对现有 Requirement 「统一响应包装的 data SHALL 携带真实结构」的强约束延伸 — 该 Requirement 要求 `data` 携带真实结构，本 Requirement 钉死"任何受保护接口 SHALL NOT 跨过 `R` 包络直接出参"。

**`R` 包络 SHALL 包含一切出参，容器也不例外**：`PageVO<T>` / `List<T>` / 布尔值虽是"容器或标量"而非实体，但它们同样 SHALL 落在 `R` 内。SHALL NOT 因为"分页容器是本仓自定义类型、布尔值更轻量"而让它们直接成为方法的返回类型 —— 裸出参会绕过 `R` 的 `code` / `info` / `success` 三元组，前端的 `unwrapSuccess` 无从工作，该端点只能被特殊处理。

**入参侧 SHALL NOT 因形参被 `@Parameter(hidden = true)` 隐藏而从契约里消失**：形参被隐藏时 springdoc 会忽略整个形参，连方法级 `@Parameters` 块也会被盖掉，于是契约里该端点**没有任何参数**，前端无从构造请求。该注解 SHALL NOT 用于会接收客户端输入的形参。

历史遗留端点（如 `GET /project/load/code/{code}` 在 2026-10-18 之前返裸 `ProjectVO`、`GET /project/relationship/listMapFieldColumn` 返 `R<Object>`、`GET /account/list` 返裸 `PageVO<AccountVO>`）应在本能力落地后逐步收回；本变更起，任何新引入或变更的受保护接口 SHALL 满足本 Requirement。

> **本 Requirement 已连续三次被违反**：① `loadByCode` 直返裸 `ProjectVO`（M3-A-1 修）
> ② `listMapFieldColumn` 返 `R<Object>`（`2026-10-18-relationship-config` 修）
> ③ `/account/list` 返裸 `PageVO` + `/account/modify` 返裸 `boolean`（本变更修）。
> 反复违反说明**只写"SHALL 走 R<T>"不足以被守住**，故本次补充"容器也不例外"与
> "入参形参不得被 hidden 隐藏"两条可判定的细则。

#### Scenario: 任何受保护端点的契约 schema SHALL NOT 直接 ref 实体类型

- **WHEN** 评审或回归测试任何受保护 REST 接口的契约
- **THEN** 契约 schema 中响应的顶层 schema SHALL 为 R-包络（如 `R<VO>` 渲染后的 `RProjectVO`、`R<PageVO<T>>` 渲染后的 `RPageVOProjectSummaryVO` 等），SHALL NOT 直接 ref 到实体类型（如 `Project`、`MapFieldColumn`、`Page`）
- **AND** 响应字段的 `data` 节点 SHALL 是该 VO / PageVO 的 schema，且 SHALL NOT 是 `{}` 或 `object` 这种无约束形态

#### Scenario: 分页与标量出参 SHALL NOT 裸返

- **WHEN** 某受保护端点的返回类型是 `PageVO<T>` / `List<T>` 等容器
- **THEN** 契约里该端点的响应顶层 SHALL 是对应的 R-包络（`RPageVO<T>` / `RList<T>`）
- **AND** `GET /account/list`（曾返裸 `PageVO<AccountVO>`）SHALL 已包入 `R<PageVO<AccountPublicVO>>`

#### Scenario: 端点 SHALL NOT 裸返 boolean 等非 R 包络类型

- **WHEN** 某 Controller 方法的返回类型不是 `R<...>`
- **THEN** 契约里该端点的响应 SHALL NOT 出现顶层 `type: boolean` / `type: string` 这类无包络 schema
- **AND** `PUT /project/relationship/modify`（曾裸返 `boolean`）与 `PUT /account/modify` SHALL 返 `R<Boolean>`，使前端能用统一的 `unwrapSuccess` 消费

#### Scenario: 入参形参 SHALL NOT 被 hidden 隐藏而从契约消失

- **WHEN** 某端点的方法形参承载客户端输入（`@RequestBody` 或按字段绑定的实体形参）
- **THEN** 该形参 SHALL NOT 标注 `@Parameter(hidden = true)` —— 隐藏后契约里该端点 SHALL NOT 完全没有参数
- **AND** `DELETE /account/delete`（曾把承载删除条件的 `AccountVO` 形参标为 hidden，
  导致契约里无任何参数、前端无法发起删除）SHALL 已在契约中导出 `id` / `code`

#### Scenario: 历史遗漏端点（如 loadByCode）改返 R<T> 后契约形状收敛

- **WHEN** `GET /project/load/code/{code}` 由原本直返 `ProjectVO` 改造为返 `R<ProjectDetailVO>`
- **THEN** 契约 schema 中响应的 `data` 节点 SHALL ref `ProjectDetailVO` 而不是裸 ref，SHALL NOT 出现 "ProjectVO 作为响应 schema 整体" 这种无包络形态
- **AND** 任何"过去写过 R<T>"的客户端代码 SHALL 在无修改前提下仍能成功解构；存量"直接吃实体"的旧代码 SHALL 让 `resp.data.code` 取到 `undefined` 而非业务值，触发类型守护

#### Scenario: 反向验证：以裸实体出参接入受保护接口 SHALL 立即失真

- **WHEN** 临时把 Controller 装配改回直返持久化实体（破坏本 Requirement）
- **THEN** `api-contract` 系列契约断言（如 `ApiContractIT` 期待 `data.code === 种子编码` 的断言） SHALL 失败，错误 SHALL 指向裸实体出参而非 `code` 字段缺省
- **AND** 该反向验证保证本 Requirement 不会被后续回归悄悄破坏

#### Scenario: R<Object> 出参 SHALL 被识别为契约缺陷而非合法写法

- **WHEN** 某端点的返回类型泛型是 `Object` / `JSONObject` / `Map<String, Object>` 等无约束类型
- **THEN** 契约里该端点的 `data` 节点 SHALL NOT 渲染为无约束的 `object` —— 前端拿不到内部字段名，无法消费
- **AND** 该端点 SHALL 被归类为缺陷并改造为具名 VO
- **AND** 判定依据 SHALL 可自动化：`GET /project/relationship/listMapFieldColumn` 曾是本仓**唯一**的 `R<Object>` 端点，本 Scenario 为其回归防线

### Requirement: M4 切运行时 React 入口优先 Angular 静态产物

切运行时后端 jar 含 `aicode-web` 模块提供的 `static/index.html`（React 19 + TypeScript 入口），`/` 路径 SHALL 由 Spring Boot 从 classpath 加载此 HTML；Angular 7 静态产物 SHALL 被删除，**不得**优先于 React 入口。

#### Scenario: M4 后 `/` 命中 React 入口而非 Angular 老产物

- **WHEN** 启动 `./gradlew :aicode:bootRun`（M4 后 aicode 依赖 aicode-web）后 GET `http://localhost:8080/`
- **THEN** 响应 200 + Content-Type `text/html`，HTML body SHALL 含 `id="root"`（aicode-web React Vite 入口标记）且 SHALL NOT 含 Angular 老产物 hash（如 `10.460f310dc07dcf545995.js`）
- **AND** `aicode/src/main/resources/static/` SHALL 已删除（Angular 产物不共存，避免 classpath 顺序歧义）

#### Scenario: M4 后带 `.shtml` 后缀的 API 路径仍由 Spring MVC 处理

- **WHEN** GET `http://localhost:8080/project/list.shtml?token=...&curPage=1&pageSize=5`（`*.shtml` 路径由 `ShtmlSuffixRewriteFilter` 重写为无后缀）
- **THEN** 响应 200 + Content-Type `application/json`（非 `text/html`），body 为 Spring MVC R 包络 JSON 而**不**是 React `index.html`
- **AND** React 入口不占用 `/api/*` 或 `/project/*` 等后端 API 路径（前端页面路由走 hash 段，不与 API 路径争用）

#### Scenario: M4 后前端页面路由走 hash 段（`/#/...`）而非服务端路径

- **WHEN** 用户在浏览器地址栏访问 `http://localhost:8080/#/login`、`/#/project/list`、`/#/account/me`
- **THEN** hash 段不经服务端匹配，React Router（`HashRouter`）SHALL 在客户端渲染对应页面
- **AND** 无 hash 的服务端路径（如 `GET /login`）SHALL 返回 400 JSON（R 包络错误），**不** SHALL fallback 到 `index.html` —— Spring Boot 4 对未匹配路径抛 `NoResourceFoundException`（不自动 SPA fallback）
- **AND** 前端所有 API 请求 SHALL 统一带 `.shtml` 后缀（由 `src/lib/api/shtml-suffix.ts` axios 拦截器追加），与后端 `ShtmlSuffixRewriteFilter` 对接
### Requirement: 分页接口 SHALL 统一用 PageVO<T> 而非 Page<T>

凡返回分页数据的接口，其出参容器 SHALL 是 `com.aicode.core.PageVO<T>`（列表字段名为 `records`，并携带 `totalRow` / `totalPage` / `curPage` / `pageSize`），SHALL NOT 使用 `com.aicode.core.Page<E>`（列表字段名为 `voList`，且携带 `params` / `sortColumns` / `optObject` 等内部字段）。

两种容器并存会让契约层出现**两种分页字段名**（`records` vs `voList`），前端消费时需按端点分别记忆，且 `Page` 的内部字段（`params` / `optObject`）也会进入响应。

> 该约束由 M2-1 在 `/project/list` 上落地（收敛到 `R<PageVO<ProjectSummaryVO>>`），M5-A1 在 `/framework/list` 上完成剩余收敛。

#### Scenario: 框架列表接口的 records 取代 voList

- **WHEN** 客户端请求 `GET /framework/list`，响应的 `data` 节点 SHALL 含 `records` 数组（元素为框架 VO）以及 `totalRow` / `curPage` / `pageSize` / `totalPage`
- **AND** 契约 schema 中响应容器 SHALL ref `PageVO<FrameworkSummaryVO>` 展开形态，SHALL NOT ref 旧的 `Page<FrameworksPageVO>`

#### Scenario: 无 Page<E> 容器残留于对外契约

- **WHEN** 遍历 `contracts/openapi/openapi.yaml` 的 `components.schemas`
- **THEN** SHALL NOT 出现由 `com.aicode.core.Page` 展开的 schema（其特征是含 `voList` / `params` / `sortColumns` / `optObject` 字段）
- **AND** 仓库内已无 Controller 以 `R<Page<...>>` 作为出参返回类型

#### Scenario: 反向验证：改回 Page<E> 容器 SHALL 使契约断言失败

- **WHEN** 临时把某分页接口的出参容器从 `PageVO<T>` 改回 `Page<T>`
- **THEN** 契约守护断言 SHALL 失败，错误信息 SHALL 明确指出 `voList` 不在期望字段集合内（或 `records` 缺失）

### Requirement: 分页接口的 PageVO SHALL 回填实际查询参数而非默认值

接口声明 `curPage` / `pageSize` 入参时，响应的 `data` 节点 SHALL 回填**实际生效**的 `curPage` / `pageSize`，并 SHALL 给出与 `totalRow` 相符的 `totalPage`（`ceil(totalRow / pageSize)`）。SHALL NOT 把这三个字段留为 `PageVO` 的 int 默认值 0 —— 那会让前端算出的翻页边界全部失效。

该 Requirement 补齐既有 Requirement「分页接口 SHALL 统一用 PageVO<T> 而非 Page<T>」的最后一块：容器形态已统一，但容器内的**值**此前无人守护（`/project/list` 曾忽略入参拉全量且从不 set 这三个字段）。

#### Scenario: 响应回填与实际查询一致的 curPage / pageSize / totalPage

- **WHEN** 客户端请求分页接口并携带 `curPage = 3` / `pageSize = 20`
- **THEN** 响应的 `data.curPage` SHALL 等于 3，`data.pageSize` SHALL 等于 20
- **AND** `data.totalPage` SHALL 等于 `ceil(data.totalRow / 20)`，SHALL NOT 为 0（当 `totalRow > 0` 时）

#### Scenario: 入参缺省或非法时 SHALL 回退到默认值而非报错

- **WHEN** 客户端未携带 `curPage` / `pageSize`，或携带 0 / 负数
- **THEN** 服务端 SHALL 回退到默认页（`curPage = 1`）与默认页大小（`pageSize = 10`），并把回退后的值回填进响应
- **AND** SHALL NOT 抛异常，也 SHALL NOT 拉取全表

#### Scenario: 数据量超过一页时 SQL SHALL 只取该页

- **WHEN** 某账户名下有 25 个项目，客户端请求 `curPage = 2` / `pageSize = 10`
- **THEN** 服务端 SQL SHALL 只返回 10 条（该页区间），SHALL NOT 返回全部 25 条
- **AND** `data.totalRow` SHALL 等于 25，`data.totalPage` SHALL 等于 3
