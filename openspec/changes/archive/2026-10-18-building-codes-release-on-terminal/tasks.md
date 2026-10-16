# Tasks

## 1. `BuildLogPanel` 加 `onClosed`

- [x] 1.1 `build-log-panel.tsx`：Props 加 `onClosed?: (projectCode: string) => void`，Javadoc 说明「面板从 DOM 消失时调用一次」并区分两条触发路径（终态自动收起 / 用户手动关闭） *(已完成：Props 加 onClosed?: (projectCode: string) => void，Javadoc 说明两条触发路径（终态自动收起 / 手动关闭）)*
- [x] 1.2 加 `hasShownRef` 守卫（D2 的关键）：`status === 'running'` 时置 true；`status` 变为 `idle` / `done` 且 `hasShownRef` 为 true 时**触发一次并复位**。**SHALL NOT** 写成「当前非 running 就触发」—— 翻回正在构建的行时面板以 `autoStart={false}` 挂载、status 初始即 `idle`，那样会误清进行态 *(**已完成（D2 的关键）**：hasShownRef —— running 置 true；idle/done 且曾为 true 时触发一次并复位。**不能简单判「当前非 running」**：翻回时面板以 autoStart=false 挂载、status 初始即 idle，那样会误清)*
- [x] 1.3 `status === 'error'` **不触发** `onClosed` —— 面板刻意保留让用户看失败原因；用户看完点「关闭」（status 转 `idle`）时才触发 *(通过：error 不在消失条件里（面板保留）；用户点关闭 → status 转 idle → 此时 hasShownRef 为 true（running 时置）→ 触发。**反向验证**：去掉守卫 → 6 条同时变红（含既有的「翻回不重复调 execute」—— 两个防护是耦合的）)*
- [x] 1.4 验证 `npm run typecheck` exit=0 *(typecheck exit=0)*

## 2. `ProjectListPage` 释放进行态

- [x] 2.1 `index.tsx`：`<BuildLogPanel projectCode={rowCode} … />` 加 `onClosed={(code) => setBuildingCodes((prev) => { const next = new Set(prev); next.delete(code); return next })}` —— **用函数式更新**（多个面板并发关闭时逐个基于旧快照会丢删除，D3） *(已完成：ProjectListPage 传 onClosed → setBuildingCodes 函数式更新删除（D3：多行面板并发关闭时逐个基于旧快照会漏删）)*
- [x] 2.2 验证 typecheck exit=0 且既有 9 用例仍通过 *(通过：typecheck exit=0；既有 9 用例仍过)*

## 3. 前端测试

- [x] 3.1 `build-log-panel.test.tsx` 补（约 3 条）：① `running → done` 触发一次 `onClosed` ② `running → clear()`（status 转 `idle`）触发一次 ③ **初始就 `idle`（翻回场景）SHALL NOT 触发** —— 本变更最关键的回归防线 *(已完成：build-log-panel.test.tsx 10 → **15 用例**（running→done 触发 / running→clear 触发 / **初始就 idle 不触发**（关键防线）/ error 不触发 / prop 可选不抛错）。为测 clear 路径把 mock 的 `clear` 改为可替换实现)*
- [x] 3.2 `ProjectListPage.test.tsx` 补（约 2 条）：① 投递 `{"type":"done"}` 后该行构建按钮**恢复可点**且文案从「构建中…」变回「构建」（端到端：ws 终态 → hook 终态 → 面板消失 → onClosed → 释放）② **翻回该行时按钮仍禁用**（不误释放） *(已完成：ProjectListPage.test.tsx 9 → **12 用例**（done 后按钮恢复可点且文案复位 / error 时面板保留按钮仍禁用 / 翻回不误释放）。**ws mock 改为可投递消息**形态（记录 onMessage）+ 补 execute 端点 mock)*
- [x] 3.3 `ProjectListPage.test.tsx` 的 mock 结构：现有测试已 mock ws-client 为 `connect: vi.fn(...)`，需改为**可投递消息**的形态（记录 onMessage） *(已完成：vi.mock 的 connect 改为记录 onMessage 的可投递替身 + pushWsMessage helper + useExecuteOk helper + SINGLE_ROW 常量)*

## 4. 验收 + 文档 + 登记

- [x] 4.1 前端全套 `npm run typecheck` + `npm test` + `npm run build` *(typecheck exit=0；**23 文件 / 162 用例**（154 + 8）全过；build 108 modules)*
- [x] 4.2 后端全套（确认零改动仍全绿）：`JAVA_HOME=$(/usr/libexec/java_home -v 25) ./aicode/gradlew test` *(实测：aicode 206 / facade 39 / common 56 —— **301 用例** 0 失败，且本变更**零后端改动**（git status 已核实）)*
- [x] 4.3 `openspec validate --strict 2026-10-18-building-codes-release-on-terminal` 与 `--specs` *(openspec validate --strict 与 --specs 均通过（14/14）)*
- [x] 4.4 `AGENTS.md §11.3`：更新 `ProjectListPage` 与 `build-log-panel` 用例数与语义（注明「构建终态后释放进行态」） *(已完成：§11.3 ProjectListPage 9 → **12**（列 3 条新增语义）、build-log-panel 10 → **15**（列 onClosed 的 5 条触发规则）；统计 154 → **162**)*
- [x] 4.5 `AGENTS.md §11.1` 或 §10：登记「列表页的异步任务状态机（进入 / 释放）SHALL 有 spec 覆盖」—— 本变更前 `buildingCodes` 完全未被 spec 覆盖，是 spec 缺口 *(已完成：§10 新增「列表页/容器的异步任务进行态 SHALL 有 spec 覆盖 —— 尤其**释放**路径」，含完整经过 + 2 条处方（进入/释放都要写进 spec；测试要覆盖释放）+ 连带教训（不能用「当前非运行态」当判据，用 hasShownRef）)*
- [x] 4.6 同步 delta spec 到 `aicode-web-page`，然后 `openspec archive` *(已完成：主 spec 物化 —— aicode-web-page 9 → **10** Req（新增「列表页的构建中状态 SHALL 在构建终态后释放」含 5 Scenarios）；14/14 通过；变更已 archive)*
- [x] 4.7 commit（中文 message） *(已完成：中文 message，单变更单 commit)*