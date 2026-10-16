# Tasks

## 1. 后端：进度 POJO

- [x] 1.1 `facade/project/vo/ProjectInitProgress.java`：5 字段（`type` / `phase` / `current` / `total` / `message`），Lombok `@Data`，逐字段 `@Schema`；含两个工厂方法 `progress(phase, current, total, message)` 与 `done(message)` / `error(message)`；验证 `./aicode/gradlew :facade:compileJava` exit=0 *(已完成：facade/project/vo/ProjectInitProgress.java，5 字段（type/phase/current/total/message），含 progress/done/error 三个静态工厂)*
- [x] 1.2 `type` 的三个合法值（`progress` / `done` / `error`）SHALL NOT 散落成裸字符串 —— 评估是否需要枚举类；**注意 Java 契约类无枚举校验**，且 ws 通道无 OpenAPI（design D4） *(已核实：三个 type 值集中在工厂方法里（grep 命中 3 处，均在工厂内），未散落。ws 无 OpenAPI，故类型约束只能靠 TS 类型 + 测试)*

## 2. 后端：Service 上报点（业务逻辑一行不改）

- [x] 2.1 `ProjectService` 接口 + `ProjectServiceImpl`：**新增**重载 `void execute(String code, Consumer<ProjectInitProgress> onProgress)`；**保留**原 `void execute(String code)`（委托给新重载传 no-op） *(已完成：ProjectService 接口 + Impl **新增重载** `execute(code, Consumer<ProjectInitProgress>)`；**保留**原 `execute(code)`（委托传 no-op）)*
- [x] 2.2 `execute` 在调 `createDatabase` 前上报 `{phase:"createDatabase", current:0, total:0}`（design D5：建库内部无细粒度点，诚实报未知） *(已完成：调 createDatabase 前上报 {phase:"createDatabase", current:0, total:0} —— 建库内部无可切分点（design D5），诚实报未知)*
- [x] 2.3 `parse` 签名加回调参数；拿到 `tableList` 后上报 `{phase:"parse", current:0, total:N}` *(已完成：parse 加回调参数；拿到 tableList 后上报 {phase:"parse", current:0, total:N})*
- [x] 2.4 `tableList.forEach` **每张表开始前**上报 `{phase:"parse", current:i, total:N}` + `message` 含表名；**`current` 取已完成数**（前端可直接 `current/total`） *(已完成：forEach 内每张表**开始前**上报，current 取**已完成数**（doneTables[0]，batchInsert 之后才 ++，故失败时如实反映已写入数）)*
- [x] 2.5 验证：`:aicode:compileJava` exit=0；**核实 `parse()` 的业务逻辑零改动**（只增上报调用，`diff` 里除回调参数与 `onProgress.accept(...)` 外无其他变更） *(通过：compile exit=0；**git diff 核实业务逻辑零改动** —— 剩余 diff 仅为签名变更（Consumer import / 委托 / @Override / parse 签名）)*

## 3. 后端：异步执行器 + Controller

- [x] 3.1 新建 `aicode/project/service/ProjectInitExecutor.java`：`@Component`，`@Async public void execute(String code)`，方法体 **try/catch 包裹**，catch 里推 `error` 消息（AGENTS §6.7：异步方法 SHALL NOT 让异常逃逸到线程顶层） *(已完成：ProjectInitExecutor（@Component + @Async），try/catch 包裹，catch 里推 error)*
- [x] 3.2 executor 把 `ProjectInitProgress` 序列化成 JSON 字符串，调 `WSClientManager.sendMessage(json)`；成功末尾推 `type=done` *(已完成：JSON.toJSONString + WSClientManager.sendMessage；成功末尾推 done)*
- [x] 3.3 `ProjectController.init` 改为注入 `ProjectInitExecutor` 并调它，**立即返回 `R<Void>`**；响应类型不变（契约零变更） *(已完成：Controller 注入 executor 并调用，立即返回 R.success()；**响应类型与入参都没动** → 契约零变化)*
- [x] 3.4 验证 `:aicode:compileJava` exit=0，且 **`@Async` 确实生效** —— 关键检查：executor 是**独立 Bean**（`@Component`），由 Controller **注入**调用，SHALL NOT 出现同类内部 `this.xxx()` 调用（design D1 的坑） *(通过：compile exit=0；grep 确认 Controller 是 `projectInitExecutor.execute(code)`（注入调用，无 this.xxx()）；executor 是 @Component + @Async)*

## 4. 后端：契约守护

- [x] 4.1 `ApiContractIT` 加 1 用例：`POST /project/init` 的响应 SHALL 仍是 `R<Void>` 且 `success=true`（语义从"执行完毕"变为"已接受"，但响应形状不变）；同时断言响应**快速返回**（可断言耗时 < 阈值，如 2 秒 —— 同步执行会超时） *(已完成：场景⑮ **2 用例** —— ① 快速返回（耗时预算 2000ms，实测 **144ms**）且 success=true（用不存在的编码，顺带验证异步异常不逃逸到 HTTP）② 响应契约仍是 RVoid 且导出 parameters)*
- [x] 4.2 **反向验证**：临时把 `ProjectController.init` 改回直接调 `projectService.execute(code)`（同步）→ 4.1 的耗时断言变红 *(**成立**：改回 `projectService.execute(code)`（同步）→ 用例 1 变红，报 `init SHALL 返回「已接受」… 实际="Server Error"`（异常直接逃逸），144ms 的预算断言也随之失去意义)*

## 5. 前端：`useInitProgress` hook

- [x] 5.1 新建 `aicode-web/src/lib/realtime/use-init-progress.ts`： *(已完成：复用 ws-client；**先 connect 再调 init**；按 `{` 开头分流；非法 JSON 按文本处理不抛错；done 时回调 onDone)*
  - 复用 `ws-client` 的 `connect()`；
  - **先 connect 并等 `onopen`，再调 `api.init({code})`**（design Risks：没连 ws 时进度会静默丢弃，必须让连接失败可感知）；
  - 消息分流：`msg.startsWith('{')` → `JSON.parse` → 按 `type` 分派（delta spec：形态判定 SHALL 基于 `{` 开头，SHALL NOT 关键词匹配）；**解析失败时按自由文本处理而非抛错**；
  - 状态机：`idle` / `connecting` / `running` / `done` / `error`，附 `phase` / `current` / `total` / `message`
  - `done` 时通过回调通知调用方重拉解析结果
- [x] 5.2 定义 TS 类型 `InitProgressEvent`，字段名与后端 `ProjectInitProgress` 一一对应（ws 无契约守护，design Risks 已标注；由测试钉住） *(已完成：InitProgressEvent 字段名与后端一一对应，由 use-init-progress.test.ts 的「字段名 SHALL 与后端一致」断言钉住（ws 无契约守护）)*
- [x] 5.3 导出 `unsupportedDisplayType` 之类无关注释，SHALL NOT 留死代码 *(通过：无死代码（typecheck 的 noUnusedLocals 会拦）；中途误写的 `await import(...)` 动态 import 已改回静态)*

## 6. 前端：`ProjectSqlPanel` 改造

- [x] 6.1 「解析」按钮改走 `useInitProgress`：点击 → 显示「连接中…」→ 调用 init → 显示进度条（`current/total`）+ 文案 → `done` 后重拉 `listMapClassTable` 并关闭进度区 *(已完成：按钮走 useInitProgress，显示进度条 + 文案；done 后重拉并显示完成)*
- [x] 6.2 `total === 0` 时（建库阶段）SHALL 显示不确定进度（如文案「正在创建数据库并执行建表脚本…」而**不是** 0/0 的进度条） *(已完成：total===0 时显示 project-parse-indeterminate 文案而非空进度条)*
- [x] 6.3 `error` 时显示错误 + `message`（含已完成工作量）；进度区保留不自动消失，让用户看清失败位置 *(已完成：error 时显示错误 + message（含已完成工作量）；**进度区保留不自动消失**，提供「关闭」按钮)*
- [x] 6.4 保留既有的「初始化失败 SHALL 呈现可读错误」用例（同步→异步后错误来源从 HTTP 变为 ws，该用例需相应改造 —— 记录在实施记录里） *(已完成：既有用例「解析后重读」「init 失败呈现错误」改为**驱动 ws 消息**（错误来源从 HTTP 变 ws），语义保留)*
- [x] 6.5 组件卸载时 SHALL 断开 ws（`useEffect` cleanup），避免泄漏连接 *(已完成：useEffect cleanup 里 wsRef.current?.close())*

## 7. 前端：测试

- [x] 7.1 `use-init-progress.test.ts`（约 6 用例）：① `connect` 成功后调 init ② `{type:"progress"}` 更新 phase/current/total ③ `{type:"done"}` 触发 onDone ④ `{type:"error"}` 进 error 态且带 message ⑤ **非 `{` 开头的消息按文本处理不抛错** ⑥ `{` 开头但非法 JSON 时按文本处理不抛错（delta spec 的两个 Scenario） *(已完成 use-init-progress.test.ts **9 用例**（纯函数 parseInitEvent）。hook 状态机由 ProjectSqlPanel.test.tsx 覆盖（那里 mock ws-client 后直接投递消息）)*
- [x] 7.2 `ProjectSqlPanel.test.tsx` 扩展：① 点解析 → 显示「连接中」→ 进度条 ② `total=0` 时不显示进度条 ③ `done` 后重拉解析结果 ④ `error` 显示错误且进度区不消失 ⑤ 组件卸载时断开 ws *(ProjectSqlPanel 5 → **10 用例**，含「先连 ws 再调 init」（验证 connectCalls）、total=0 不确定进度、error 保留进度区、自由文本静默忽略)*

## 8. 验收 + 文档 + 登记

- [x] 8.1 后端全套 `JAVA_HOME=$(/usr/libexec/java_home -v 25) ./aicode/gradlew test`，登记三模块用例数 *(实测：aicode **201**（199 + 场景⑮ 2）/ 2 跳过 / 0 失败；facade 39；common 56 —— 合计 **296**)*
- [x] 8.2 前端全套 `npm run typecheck` + `npm test` + `npm run build` *(typecheck exit=0；**23 文件 / 146 用例**（132 + 14）全过；build 107 modules / 348 KB)*
- [x] 8.3 契约抖动判定（`sort` 后 `diff` + `comm` 确认无行被删了又加回） *(**契约零变化** —— init 的响应形状（RVoid）与入参（code）都没动，git diff --stat 为空)*
- [x] 8.4 `openspec validate --strict 2026-10-18-init-async-progress` 与 `--specs` *(openspec validate --strict 与 --specs 均通过（**14/14**，新增能力后）)*
- [x] 8.5 `AGENTS.md §11.1` 的「异步任务写法」补 `ProjectInitExecutor` 一例（`@Async` 必须独立 Bean，AGENTS §6.9 已写，登记实例） *(已完成：§6.9 异步条目下补「两个 @Async 执行器」+「独立成 Bean 是硬约束（绕过 AOP 会静默失效）」+「SHALL try/catch（异常逃逸到 VTH 顶层会静默消失）」)*
- [x] 8.6 `AGENTS.md §10` 新增坑：**ws 是单 Session**，多标签页 / 并发任务会混流，靠 `type` 分流但无法定向 *(已完成：§10 新增「WebSocket 是单 Session」条目 —— 含单 Session 后果、type 分流只能解决「能不能分辨」而非「送到哪个标签页」、彻底修法（分房间）、以及「无连接时 sendMessage 静默丢弃 → 前端 SHALL 先连 ws 再调端点」)*
- [x] 8.7 `AGENTS.md §11.3`：前端清单加 `use-init-progress` 用例数；`ProjectSqlPanel` 5 → N *(已完成：§11.3 加「异步进度消费层 use-init-progress 9」并逐条列语义；ProjectSqlPanel 5 → **10**；统计 22 文件/132 → **23 文件/146**)*
- [x] 8.8 同步 delta spec 到新建能力 `init-async-progress` 与 `aicode-web-realtime`，然后 `openspec archive` *(已完成：新能力 `init-async-progress` 物化（1 Req 4 Scenarios）/ aicode-web-realtime 3 → **4** Req；14/14 通过；变更已 archive)*
- [x] 8.9 commit（中文 message） *(已完成：中文 message，单变更单 commit)*