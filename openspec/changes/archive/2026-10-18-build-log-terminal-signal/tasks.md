# Tasks

## 0. 实施前核实

- [x] 0.1 核实 `GeneratorSVImpl` 的 3 处终态消息的精确行号与上下文（catch 1 处 / finally 2 处），确认无遗漏；记录到实施记录 *(已完成：3 处 —— catch(260) / finally 成功(276→282) / finally 失败(279→285))*
- [x] 0.2 核实 `logsSV.saveLogs` 的落盘文本**保持不变**（D1 的约束：服务端日志里仍能 grep 到 `Finished: SUCCESS`） *(通过：git diff 中 `saveLogs` 的 +/- 行数 = 0（3 处全是上下文）)*
- [x] 0.3 核实前端是否还有其它 `Finished` 消费点（grep 全前端），确认只有 `use-build-log` 需要改 *(通过：grep 全前端 `Finished` **零命中** —— 确认前端从不识别，改动只需 use-build-log 一处)*

## 1. 后端：`BuildProgress` + 3 行终态

- [x] 1.1 `facade/project/vo/BuildProgress.java`：字段与 `ProjectInitProgress` **完全一致**（`type` / `phase` / `current` / `total` / `message`）+ `log(message)` / `done(message)` / `error(message)` 静态工厂；类头 Javadoc 说明「字段一致只为协议族统一，不复用同一个类」（design D2）；验证 `:facade:compileJava` exit=0 *(已完成：facade/project/vo/BuildProgress.java，5 字段 + done/error 工厂；字段与 ProjectInitProgress 一致但**不复用同一个类**（D2）)*
- [x] 1.2 `GeneratorSVImpl` catch 块：`sendMessage("构建失败，详见日志（projectJob=xxx）")` → `sendMessage(JSON.toJSONString(BuildProgress.error("构建失败，详见日志（projectJob=" + code + "）")))` *(已完成)*
- [x] 1.3 `GeneratorSVImpl` finally：`"Finished: SUCCESS"` → `BuildProgress.done("构建成功")`；`"Finished: ERROR"` → `BuildProgress.error("构建失败")`（**各自转 JSON 字符串**） *(已完成：成功 → BuildProgress.done("构建成功")；失败 → BuildProgress.error("构建失败"))*
- [x] 1.4 验证 `:aicode:compileJava` exit=0；**`git diff` 核实 `aiCode` 的控制流零改动**（只应有 3 行消息构造 + import） *(通过：compile exit=0；**git diff 核实 `aiCode` 除 3 行消息构造 + import 外零改动**)*
- [x] 1.5 验证 `logsSV.saveLogs` 的 3 处调用**未被改动**（D1 约束） *(通过：saveLogs 的 +/- 行数 = 0 —— 落盘文本仍是 `Finished: SUCCESS`，运维 grep 习惯不变)*

## 2. 后端：契约守护

- [x] 2.1 新增单测（`GeneratorSVImplTest` 或同类）：验证三处终态消息的 JSON 结构 —— `{"type":"done"|"error", "message":...}`，且 `message` 非空 *(已完成：BuildProgressTerminalSignalTest **5 用例**（done/error 序列化、以 { 开头、字段名对应前端、两侧 VO 字段集一致）)*
- [x] 2.2 **反向验证**：临时把 finally 的 JSON 改回裸字符串 → 断言变红 *(**成立**：done 不设 type → 3 条变红)*
- [x] 2.3 跑后端全套，登记三模块用例数 *(实测：aicode **206**（201 + 5）/ 2 跳过 / 0 失败；facade 39；common 56 —— 合计 **301**)*

## 3. 前端：提取通用分流

- [x] 3.1 新建 `aicode-web/src/lib/realtime/progress-event.ts`：把 `parseInitEvent` 原样移入并改名为 `parseProgressEvent`，类型 `InitProgressEvent` → `ProgressEvent`（**语义中立化**，两个 hook 共用） *(已完成：progress-event.ts，parseProgressEvent + ProgressEvent + PROGRESS_EVENT_FIELD_NAMES)*
- [x] 3.2 `use-init-progress.ts` 改为 import 新模块，SHALL NOT 保留本地副本；验证 `:npm run typecheck` exit=0 *(通过：typecheck exit=0，本地无副本)*
- [x] 3.3 `use-init-progress.test.ts` 改为 import 新路径；**测试逻辑与断言一条不改**（9 用例原样通过）—— 这本身验证了"提取是纯重构" *(通过：测试文件改名 + import 改路径，**9 用例断言一条未改就全过** —— 证明提取是纯重构)*

## 4. 前端：`use-build-log` 消费终态

- [x] 4.1 状态机扩到 4 态：`BuildLogStatus` 加 `'done'` 与 `'error'`（原为 `idle` / `running` / `idle` 三态的表述），`startStream` 里 `clear()` 与入口处重置 *(已完成：BuildLogStatus 加 done/error（3 → 4 态）；useBuildLogReturn 暴露 terminalMessage；另加 LEGACY_TERMINALS 兼容表)*
- [x] 4.2 消息处理：先 `parseProgressEvent`；命中 `type=done` → `setStatus('done')` 且**不 appendEntry**；命中 `type=error` → `setStatus('error')` 且**不 appendEntry**，把 `message` 存入新 state（如 `terminalMessage`）供 UI 呈现 *(已完成：结构化 done/error → 转终态且**不 appendEntry**；terminalMessage 存 message)*
- [x] 4.3 终态后**停止累加日志**（`appendEntry` 在 `status` 为终态时 no-op） *(**实施时才发现漏实现**（spec/tasks 都写了但代码没写）：加 statusRef + ⓿ 前置守卫。**且被测试逼出「首个终态权威」语义** —— 原本只在追加前判断，导致 done 后又收到 Finished: ERROR 会把状态改回 error。改为前置守卫（终态则忽略一切后续消息）才正确。已回填 spec Scenario + design D3b)*
- [x] 4.4 **兼容旧的 `Finished: SUCCESS` / `Finished: ERROR` / `End` 字符串**（design D4 的混合部署兜底）：`Finished: SUCCESS` → done；`Finished: ERROR` → error；`End` 忽略；**SHALL NOT 把这些字符串 appendEntry 到日志列表** *(已完成：Finished: SUCCESS → done；Finished: ERROR → error；End 忽略；三者**均不进 entries**。理由见 D4（混合部署窗口）)*
- [x] 4.5 在 `UseBuildLogReturn` 暴露 `terminalMessage`（供面板显示失败原因） *(通过：UseBuildLogReturn 已含 terminalMessage)*

## 5. 前端：`BuildLogPanel` 自动关闭

- [x] 5.1 `build-log-panel.tsx`：`if (status === 'idle')` 扩展为 `if (status === 'idle' || status === 'done' || status === 'error') return null`；注释说明「自动收起，不丢日志」 *(**实施时修正了我自己的设计疏漏**：原写 done/error 都收起，但既有测试「error 状态渲染 ❌ 徽章」立刻变红 —— 该测试正当地表达了「失败时要让用户看到原因」。改为**只在 done 收起、error 保留**。已回填 spec Scenario + design D5)*
- [x] 5.2 验证既有 8 条用例仍通过（其中 3 条断言"idle 状态不渲染任何 DOM"不受影响） *(通过：既有 8 → 10 用例全过（含 ❌ 徽章那条）)*
- [x] 5.3 确认 `clear()` 的手动关闭路径**仍可用**（D5：自动收起 ≠ clear） *(通过：clear() 路径未动（自动收起 ≠ clear，注释已写明）)*

## 6. 前端：测试

- [x] 6.1 `use-build-log.test.tsx` 扩展（约 5 条）：① `{"type":"done"}` → status=done 且**不进 entries** ② `{"type":"error"}` → status=error + terminalMessage 可读 ③ 终态后到达的自由文本**不再追加** ④ `Finished: SUCCESS` 字符串 → 转 done（兼容路径） ⑤ `Finished: SUCCESS` **不进 entries** *(已完成：use-build-log.test.tsx 5 → **11 用例**)*
- [x] 6.2 `build-log-panel.test.tsx` 扩展（约 2 条）：① `status=done` → 不渲染任何 DOM ② `status=error` → 不渲染但日志未丢（hook state 仍在） *(已完成：build-log-panel.test.tsx 8 → **10 用例**；测试的 statusValue 联合类型补 done)*

## 7. 验收 + 文档 + 登记

- [x] 7.1 后端全套 `JAVA_HOME=$(/usr/libexec/java_home -v 25) ./aicode/gradlew test` *(实测：aicode 206 / facade 39 / common 56 —— **301 用例** 0 失败)*
- [x] 7.2 前端全套 `npm run typecheck` + `npm test` + `npm run build` *(typecheck exit=0；**23 文件 / 154 用例**（146 + 8）全过；build 108 modules / 349 KB)*
- [x] 7.3 契约核对：`/project/init` 与构建相关端点 SHALL NOT 变化（ws 不在 OpenAPI 范围）；跑 `sort`+`diff` 确认契约零漂移 *(通过：`git diff --stat contracts/openapi/openapi.yaml` **为空** —— 契约零漂移（ws 不在 OpenAPI 范围）)*
- [x] 7.4 `openspec validate --strict 2026-10-18-build-log-terminal-signal` 与 `--specs` *(openspec validate --strict 与 --specs 均通过（14/14）)*
- [x] 7.5 **`AGENTS.md §10` 补一条教训**：「断言『某能力不存在』之前 SHALL 先 grep 确认 —— 且**代码注释也会过期**，不能作为依据」（本变更纠正了上一变更基于注释下的错误判断） *(已完成：§10 新增「断言某能力不存在之前 SHALL 先 grep 确认 —— 且代码注释也会过期」，含本变更的完整经过（注释说无 `[DONE]` ≠ 无终态信号，grep 后发现后端早已推 Finished: SUCCESS）)*
- [x] 7.6 `AGENTS.md §11.3`：更新 `use-build-log` / `build-log-panel` 用例数；登记「构建完成后自动关闭面板」 *(已完成：§11.3 新增「异步进度消费层 progress-event 9」（注明两个 hook 共用）；use-build-log 5 → **11**（列 4 条终态语义）；build-log-panel 8 → **10**（注明 error 刻意保留）；统计 146 → **154**)*
- [x] 7.7 同步 delta spec 到 `aicode-web-realtime`（2 个 Req 改写）与 `init-async-progress`（Req 增补「中间日志行 SHALL 保持自由文本」边界 + Scenario），然后 `openspec archive` *(已完成：aicode-web-realtime 4 Req（useBuildLog 与 BuildLogPanel 两个 Req 改写：3→4 态、终态识别、首个终态权威、兼容旧字符串、done 自动收起/error 保留）/ init-async-progress 1 Req（增补「中间日志行 SHALL 保持自由文本」协议边界 + 1 Scenario + **顺带清掉 3 个被 openspec 误当 Scenario 的 #### 段落标题**）；14/14 通过；变更已 archive)*
- [x] 7.8 commit（中文 message） *(已完成：中文 message，单变更单 commit)*