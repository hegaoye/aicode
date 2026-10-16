# Design

## Context

`POST /project/init` 同步阻塞：`ProjectServiceImpl.execute()` 在请求线程里建库、跑用户建表 SQL、
逆解析表结构。大 SQL 会让界面白屏数秒到数十秒，且**零进度反馈**。

同仓库的构建（`POST /project/job/execute`）早已是 `ProjectJobExecutor`（独立 Bean + `@Async`）+
ws 推日志，唯独 `init` 没跟上。

**但现有 ws 协议撑不起"让前端知道何时刷新"**：`WSClientManager.sendMessage(String)` 只推
free-form 文本，`GeneratorSVImpl` 17 处调用全是日志行、**无完成信号**；
`use-build-log.tsx` 的注释如实记录了这点，构建完成后只能靠用户手点关闭按钮。

## Goals / Non-Goals

**Goals:**

- `init` 改「立即接受 + ws 推结构化进度」，进度报到**阶段 + 逐表**；
- 引入 `progress` / `done` / `error` 三态消息，前端能可靠判断何时刷新；
- 失败时如实上报已完成工作量（用户已拍板不引入事务）。

**Non-Goals:**

- **不引入事务** —— `parse()` 内有大量 delete/insert，事务包裹会改变其行为且有长事务风险。
- **不改构建日志链路** —— 它仍是 free-form 文本、无完成信号。改造它属另开变更
  （本变更的协议形态已为那次改造铺好路，见 D4）。
- 不新建状态表、不做轮询。
- 不改 `WSClientManager` 的单 Session 限制（见 Risks）。
- 不动 `parse()` 的业务逻辑，只加进度上报点。

## Decisions

### D1：`ProjectInitExecutor` 独立成 Bean（`@Async` 的硬约束）

**决定**：新建 `@Component ProjectInitExecutor`，`@Async` 方法内调 `projectService.execute(code, progressCallback)`。

**理由**：`@Async` 经 Spring 代理生效。若把 `@Async` 加在 `ProjectServiceImpl.execute()` 上而由
`ProjectController` 注入调用，能生效；但若由**同类内部**调用就会绕过代理、静默变同步。
`ProjectJobExecutor` 的 Javadoc 明确记了这个坑：

> 独立成 Bean 是为了让 `@Async` 经 Spring 代理拦截：`ProjectJobServiceImpl` 内部
> `this.generateCode(...)` 会绕过 AOP，导致构建同步阻塞 HTTP 线程。

**沿用同一模式**，且让 Controller 只管"转交"，执行逻辑集中在 executor。

**载体**：`TaskExecutorConfig` 的 `threadPoolExecutor`（`SimpleAsyncTaskExecutor` + VTH）。
IO 密集（建库跑 SQL + 大量 DB 查询）适合虚拟线程。**不指定 executor 名**，走默认。

### D2：进度回调用 `Consumer<InitProgress>`，不用事件总线

**决定**：`ProjectService.execute(String code, Consumer<InitProgress> onProgress)`，
`parse` 在循环内调 `onProgress.accept(...)`。

**理由**：只有 executor 与 service 两方通信，**引事件总线是为不存在的问题增加间接层**。
回调让上报点直接写在业务代码旁边，一眼可见。

**保持 `void execute(String code)` 不变**：新增**重载**而非改签名 —— 该方法在 `ProjectService`
接口上虽只有一个调用者，但保留无参版可让"同步执行"这条路径仍可用（测试里可能用）。

### D3：消息协议 —— JSON，以 `{` 开头

**决定**：

```json
{"type":"progress","phase":"createDatabase","current":0,"total":0,"message":"..."}
{"type":"progress","phase":"parse","current":3,"total":12,"message":"正在解析第 3/12 张表：t_order"}
{"type":"done","message":"解析完成，共 12 张表"}
{"type":"error","message":"解析第 5 张表 t_item 时失败：..."}
```

**分流依据**：以 `{` 开头即结构化。既有 free-form 日志行以中文/英文散文开头（`git 努力下载中`、
`开始生成源码...`），不会撞上。**无需改 `WSClientManager` 的签名**（它仍是 `sendMessage(String)`，
只是这次传 JSON 字符串）。

**否掉的备选**：
- 文本 + 约定结束标记（`"[init] 解析完成"`）—— 前端靠字符串匹配判完成，脆弱；
  且「解析第 5 张表失败」这种带变量的消息无法可靠识别。
- 新建专用 ws endpoint —— 要维护第二套鉴权/重连，且 `WSClientManager` 是单 Session 存量。

### D4：`InitProgress` 放 `facade`，与 `Project` 同域

**决定**：`ProjectInitProgress`（POJO，4 字段：`type` / `phase` / `current` / `total` / `message`）
放 `facade/project/vo`（与既有 VO 同域），由 `ProjectInitExecutor` 序列化成 JSON 推送。

**为何不由前端"猜"协议**：前端 hook 需要 TS 类型。契约管不到 ws 通道（本仓 ws 无 OpenAPI 定义），
故 TS 类型在前端手写并由测试钉住其与后端字段名一致 —— 与 `build2OneToOne` 枚举的处理思路一致
（值域来自契约，形状手写 + 测试守护）。

**为改构建日志铺路**：一旦 `GeneratorSVImpl` 也改发 JSON，`use-build-log` 就能同样受益于
「有完成信号」。本变更不动它，但协议形态先立起来。

### D5：上报点 —— 阶段 2 个 + 逐表循环

| 时机 | `phase` | `current/total` |
|---|---|---|
| 进 `createDatabase` | `createDatabase` | 0 / 0（建库内部无细粒度）|
| 进 `parse`，拿到 `tableList` 后 | `parse` | 0 / N |
| `tableList.forEach` 每张表**开始前** | `parse` | i / N |
| 全部完成 | — | 推 `done` |

**为何在建库阶段只报 0/0**：建库是单条 `databaseDAO.createDatabase(...)` 执行用户全部建表 SQL，
内部没有可切分的进度点。要细粒度就得改 `DatabaseDAO`，那是**执行用户的任意 SQL**，
无法在不改语义的前提下切分。诚实报"未知"好过假报一个会跳变的数字。

**`current` 在循环里是"已完成数"还是"进行中索引"**：取**已完成数**（i 从 0 开始，上报前先 `i`），
这样前端可直接渲染 `current/total` 而无需 +1。

### D6：前端 —— 新 hook `useInitProgress`，面板内联进度

**决定**：`useInitProgress` 复用现有 `ws-client` 的 `connect()`，在 `ProjectSqlPanel` 的
「解析」按钮旁显示进度条与文案。

**不复用 `useBuildLog`**：它的状态机与「先调 execute 再订阅」的时序耦合，且只认自由文本
（`appendEntry(msg)`）。`init` 需要的是结构化事件 + `done` 后触发重拉。**两者形态不同、生命周期不同，
硬合并会让构建日志被迫接受结构化事件。**

**时序**：`connect()` → 调 `api.init({code})` → 收到 `progress` 更新 UI → 收到 `done` 重拉解析结果并关闭。
（先连后调，避免竞态丢首批消息。）

## Risks / Trade-offs

**[ws 是单 Session（`WSClientManager` 单例 `put`/`get`）]** → 若用户同时开了两个标签页，
或 init 解析期间又点了构建，两者的消息会混在同一条连接上。**本协议靠 `type` 分流**，
但无法把 A 页面的进度送到 B 页面。当前是单用户工具场景，实际不会发生。
彻底解法是按 token + 项目分房间（`@ServerEndpoint` 的 session 路由），属独立议题 —— 已标注。

**[没有 ws 连接时进度会静默丢弃]** → `sendMessage` 在 `get()` 返回 null 时什么都不做。
此时用户点了「解析」但没连 ws，会看到"请求成功但界面不动"。**处置**：前端 `useInitProgress`
在调 `init` 前**必须先 `connect()` 并等 `onopen`**，让连接失败可感知。

**[`@Async` 方法内的异常会逃逸到 VTH 顶层]** → AGENTS §6.7 明确要求：异步构建须在方法内
`try/catch`，不能让异常逃逸。`ProjectInitExecutor` SHALL 照做，且 catch 里推 `error` 消息。

**[`parse()` 中途失败留下半成品]** → 用户已拍板不引入事务。`error` 消息 SHALL 带已完成数
（如"已解析 4/12 张表后失败"）。⚠️ **半成品状态下 `listMapClassTable` 会返回部分表**，
前端刷新后看到不完整的表列表 —— 这是如实反映，用户可重新解析（`parse()` 开头会先删旧映射）。

**[前端类型与后端字段名可能漂移]** → ws 通道无契约守护。缓解：`useInitProgress` 的测试里
**手写一份与后端字段名对应的期望**，并在 design 里显式记录该协议（便于下次 review 核对）。

## Migration Plan

单批交付（一个 commit）：

1. `facade`：`ProjectInitProgress`（POJO）；
2. 后端：`ProjectService.execute` 加回调重载 + `parse`/`createDatabase` 上报点 +
   `ProjectInitExecutor`（`@Async`）+ `ProjectController.init` 改调 executor；
3. `ApiContractIT` 加 1 用例（init 立即返回 + 响应仍是 `R<Void>`）；
4. 前端：`useInitProgress` hook + 测试；`ProjectSqlPanel` 改造 + 测试扩展；
5. AGENTS.md 登记 + validate + archive。

**回滚**：单 commit revert。注意前端 `ProjectSqlPanel` 依赖它。

## Open Questions

- **构建日志是否同步改造成结构化协议** —— 那样 `BuildLogPanel` 才能自动关闭（当前要用户手点）。
  协议形态已为本变更铺好路，但属另开变更。
- **ws 按项目分房间** —— 见 Risks 的单 Session 局限。
- **`DatabaseDAO.createDatabase` 的进度切分** —— 需要改用户 SQL 的执行语义，暂不考虑。