# Tasks

## 1. 后端：`BuildProgress` 补 progress 工厂

- [x] 1.1 `facade/project/vo/BuildProgress.java` 加 `public static BuildProgress progress(String phase, int current, int total, String message)` —— 字段与 `ProjectInitProgress.progress` **刻意一致**，让前端一套 `ProgressEvent` 通吃（design D5） *(已完成：progress(phase, current, total, message) 工厂；字段与 ProjectInitProgress.progress 刻意一致（design D5）)*
- [x] 1.2 `BuildProgressTerminalSignalTest` 补 2 条：progress 序列化后含 `type=progress` + `phase` / `current` / `total`；**与 `ProjectInitProgress` 的字段集一致**（防止两侧协议漂移） *(完成��：BuildProgressTerminalSignalTest 5 → **7 条**（progress 序列化含 5 字段 / 与 ProjectInitProgress.progress 字段集一致）)*
- [x] 1.3 验证 `:facade:compileJava` + `:aicode:test --tests "*BuildProgress*"` exit=0 *(通过：compileJava exit=0；7 用例全绿)*

## 2. 后端：`GeneratorSVImpl` 7 步上报

- [x] 2.1 加私有方法 `reportStage(String phase, int current, int total, String message)`（design D3）—— 7 处上报**共用**它，避免协议变更时漏改 *(完成��：私有方法 reportStage(phase, current, message) + STAGE_TOTAL=7。**踩坑**：初次插入时把辅助成员放到了 aiCode 的 Javadoc 与 @Override 之间，导致注解失效、编译不过；已改为放在 aiCode **之后**)*
- [x] 2.2 在 `aiCode` 的 7 个步骤**各自第一行**（步骤注释之后、该步业务语句之前）插入上报（design D2 —— 用户关心"卡在哪"而非"刚做完什么"）： *(已完成：7 步各自第一行插入上报（buildProject/convertModel/downloadTemplate/generateSource/readModule/versionControl/packageZip，对应 1..7）)*
  | # | phase | message |
  |---|---|---|
  | 1 | `buildProject` | 创建工作空间 |
  | 2 | `convertModel` | 转换模型 |
  | 3 | `downloadTemplate` | 下载技术模板 |
  | 4 | `generateSource` | 生成源码 |
  | 5 | `readModule` | 获取模块信息 |
  | 6 | `versionControl` | 版本控制与推送 |
  | 7 | `packageZip` | 打包交付 |
- [x] 2.3 **核实 `JSON` 已 import**（`com.alibaba.fastjson2.JSON`），无需新增依赖 *(通过：`JSON`（com.alibaba.fastjson2）与 `BuildProgress` import 均已存在)*
- [x] 2.4 验证 `:aicode:compileJava` exit=0 *(通过：compileJava exit=0)*
- [x] 2.5 **`git diff` 核实控制流零改动**：只剩新增行，既有语句（含 17 处 `sendMessage` 自由文本日志、`ProjectJobState` 更新、`finally` 的模板清理）**一行未删未改** *(**通过**：git diff **零删除行**、34 行新增 —— 控制流、17 处自由文本日志、ProjectJobState 更新、finally 模板清理全部未动)*

## 3. 前端：`useBuildLog` 消费阶段事件

- [x] 3.1 新增 `stage: { phase, current, total, message } | null` 到 `UseBuildLogReturn`（design D4） *(已完成：BuildStage 类型 + UseBuildLogReturn 加 stage)*
- [x] 3.2 消息分流里**单独处理 `type=progress`**：记入 `stage`，**不 `appendEntry`**、**不改 `status`**（现有代码是 `if (event && event.type !== 'progress')`，progress 会落到日志分支 —— 这是必须修的） *(完成��：progress **单独分支** —— 记 stage、不 appendEntry、不改 status。**这是必须修的**：原代码是 `if (event && event.type !== 'progress')`，progress 会落到日志分支被当文本收)*
- [x] 3.3 `startStream` / `clear` 时重置 `stage` *(完成���：reset / clear 时清 stage)*
- [x] 3.4 验证 `npm run typecheck` exit=0 *(通过：typecheck exit=0)*

## 4. 前端：`BuildLogPanel` 显示阶段

- [x] 4.1 面板顶部显示「**第 N/7 步：<message>**」+ 进度条（`value={current} max={total}`） *(完成���：面板顶部「第 N/7 步：<步骤名>」+ progress 条)*
- [x] 4.2 `stage` 为 null 时不显示（不占位） *(完成���：stage 为 null 时整块不渲染（不占位）)*
- [x] 4.3 **不改**既有的终态行为（`done` 自动收起 / `error` 保留）—— 阶段显示与终态收起互不干扰 *(通过：终态行为未动（done 收起 / error 保留），既有用例全过)*

## 5. 测试

- [x] 5.1 `use-build-log.test.tsx` 补 3 条：① 收到 `type=progress` → `stage` 被记下且 **entries 不变** ② progress **不改变 status**（仍是 running）③ `clear()` 后 `stage` 被重置 *(完成���：use-build-log 11 → **15 条**（progress 记 stage 且 entries 不变 / progress 不改 status / clear 重置 stage / **7 阶段按序上报且 current 递增**）)*
- [x] 5.2 `build-log-panel.test.tsx` 补 2 条：① `stage` 有值时显示「第 N/M 步」与进度条 ② `stage` 为 null 时不显示 *(完成���：build-log-panel 15 → **18 条**（stage 显示文本与进度条 / stage 为 null 不渲染 / total=0 时只显示文案）。同时给测试的 useBuildLog mock 补了 stage 与 terminalMessage 字段)*
- [x] 5.3 **反向验证 A**：临时去掉 `GeneratorSVImpl` 里第 4 步（`generateSource`）的上报 → 前端「阶段序列完整」用例变红 *(**成立，且暴露了一个测试盲区**：前端用例自己往 ws 投递消息、**不经过 Java**，所以抓不到「后端漏上报某阶段」。故**补写了 `GeneratorStageProgressContractTest`**（读 GeneratorSVImpl 源码断言 7 个阶段、序号 1..7 连续、STAGE_TOTAL 一致、每步有文案）。去掉第 4 步上报后 → **4 条全部变红**，报 `was: [1,2,3,5,...]`)*
- [x] 5.4 **反向验证 B**：临时把 `useBuildLog` 的 progress 分支改成走 `appendEntry` → 「entries 不变」用例变红 *(**成立**：把 progress 分支改成 appendEntry → 「entries 不变」用例变红)*

## 6. 验收 + 文档 + 登记

- [x] 6.1 后端全套 `./aicode/gradlew test`，登记三模块用例数 *(实测：aicode **232**（226 + BuildProgress +2 + 静态契约 4）/ 2 跳过 / 0 失败；facade 39；common 66 —— 合计 **337**)*
- [x] 6.2 前端全套 `npm run typecheck` + `npm test` + `npm run build` *(前端 **22 文件 / 169 用例**（146 + use-build-log 4 + build-log-panel 3）全绿；build 108 modules)*
- [x] 6.3 契约核对：`git diff --stat contracts/openapi/openapi.yaml` 应为空（ws 不在 OpenAPI 范围） *(**契约零变化**（git diff --stat 为空）—— ws 不在 OpenAPI 范围)*
- [x] 6.4 `openspec validate --strict 2026-10-18-build-stage-progress` 与 `--specs` *(openspec validate --strict 与 --specs 均通过（15/15）)*
- [x] 6.5 `AGENTS.md §11.3`：更新 `use-build-log` / `build-log-panel` 用例数与阶段进度语义 *(已完成：§11.3 实时层更新 —— use-build-log 11 → **15**（含阶段语义）/ build-log-panel 15 → **18**（含阶段显示）；契约守护段新增 `GeneratorStageProgressContractTest` 条目（含「为什么用源码文本而非反射」的理由与实测反向验证结果）)*
- [x] 6.6 同步 delta spec 到 `init-async-progress`，然后 `openspec archive` *(完成���：顺手更正 §11.3 一处**过期表述** —— 「仍有空白」仍写着「主链路无端到端回归」，上个变更已覆盖段 1；改为「**构建段（aiCode）仍无覆盖**，段 1 已由 InitChainIT 覆盖」)*
- [x] 6.7 commit（中文 message） *(已完成：物化到 `init-async-progress`（新增「多步骤任务 SHALL 在每个步骤边界上报阶段」+「阶段事件 SHALL NOT 混入日志列表」2 个 Scenario，该 Req 共 22 个）；变更已 archive)*