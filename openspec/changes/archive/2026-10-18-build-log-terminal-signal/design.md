# Design

## Context

构建完成后 `BuildLogPanel` 一直挂着，用户不知道好了没。

## 更正一个此前的错误判断

`2026-10-18-init-async-progress` 里我写了「构建日志**无完成信号**」。**这个判断不准确。**

`GeneratorSVImpl.aiCode()` 的 `finally` 块**早已推送**终态：

```java
if (Completed) { WSClientManager.sendMessage("Finished: SUCCESS"); }
else           { WSClientManager.sendMessage("Finished: ERROR"); }
WSClientManager.sendMessage("End");
```

catch 块里也有 `构建失败，详见日志（projectJob=xxx）`。

**真正的情况是前端完全不识别** —— `use-build-log.tsx` 对每条消息一律 `appendEntry(msg)`，
把 `Finished: SUCCESS` 当普通日志行显示。grep 该文件 `Finished` **零命中**。

所以这不是后端缺功能，而是**前端漏消费**。改动量比预想小一个量级。

> **教训**：写 spec/design 时断言「某能力不存在」之前，SHALL 先 grep 确认。上次是凭注释
> （`use-build-log.tsx` 里的「无 `[DONE]` sentinel」）下的结论，而**注释说的是没有 `[DONE]`，
> 不是没有终态** —— 终态用的是 `Finished: SUCCESS` 这个名字。**注释也会过期。**

## Goals / Non-Goals

**Goals:**

- 后端 3 行终态消息升级为结构化 JSON（与 init 协议统一）；
- 前端识别终态 → 转 4 态状态机；
- `BuildLogPanel` 收到 `done` 后**自动关闭**。

**Non-Goals:**

- **不改 17 处中间日志行**（用户已拍板）。它们保持自由文本 —— 包装成 JSON 只增加噪音，
  零消费方使用。
- 不给构建任务加阶段进度（当前无法从 `GeneratorSVImpl` 的散点日志里可靠切分阶段）。
- 不引入事务、不改生成主链路控制流。
- 不做关系删除 UI 等其它遗留。

## Decisions

### D1：后端只改 3 行 —— catch 1 行 + finally 2 行

**决定**：

| 位置 | 改前 | 改后 |
|---|---|---|
| catch | `"构建失败，详见日志（projectJob=xxx）"` | `{"type":"error","message":"构建失败，详见日志（projectJob=xxx）"}` |
| finally 成功 | `"Finished: SUCCESS"` | `{"type":"done","message":"构建成功"}` |
| finally 失败 | `"Finished: ERROR"` | `{"type":"error","message":"构建失败"}` |

**理由**：`aiCode` 是代码生成主链路（AGENTS §5.5），17 处 `sendMessage` 散落各处，
逐处改成结构化需要判断"哪些是阶段边界" —— 靠日志文案猜**极易出错**，且要动 17 处。
终态是**有明确代码位置**的（catch / finally），改动可精确界定。

**⚠️ 生成主链路零改动**：3 行都是消息构造，`aiCode` 的控制流、`ProjectJobState`
状态更新、`finally` 的模板清理逻辑全部不动。

**`logsSV.saveLogs` 的落盘文本保持不变** —— 服务端日志文件里仍能 grep 到
`Finished: SUCCESS`，运维习惯不变。

### D2：`BuildProgress` 与 `ProjectInitProgress` 字段一致，但不复用同一个类

**决定**：新建 `BuildProgress`（facade），字段与 `ProjectInitProgress` 完全相同
（`type` / `phase` / `current` / `total` / `message`），但类名不同。

**不复用的理由**：两个是**不同任务域**的进度（构建 vs 解析）。共用一个类会让人误以为
可以互相消费对方的进度事件。字段一致只是为了**协议族统一**（消费端一套分流逻辑）。

**`phase` / `current` / `total` 在 build 侧不使用** —— 构建日志没有阶段切分。
字段保留是为了让前端一套类型处理两者，且未来若给构建加阶段进度，无需改协议。

### D3：前端提取通用分流 `progress-event.ts`

**决定**：把 `useInitProgress` 里的 `parseInitEvent` 提取到
`lib/realtime/progress-event.ts`，导出 `parseProgressEvent` 与 `ProgressEvent` 类型；
两个 hook 共用。

**理由**：分流判据（首字符 `{` → JSON.parse → 校验 `type`）是**协议的核心不变式**，
两处各写一份必然漂移。且它已有 9 条单测，提取后复用同一套测试。

**`use-init-progress.test.ts` 改为 import 新路径** —— 测试逻辑不变，只改 import。

### D3b：首个终态权威（实施期发现）

**决定**：消费端在已处于终态时，**忽略后续一切消息** —— 日志行、`End`、甚至矛盾的终态。

**为何需要**（实施期被测试逼出来）：我最初只在"日志行追加前"加了终态判断，结果
「done 之后又收到 `Finished: ERROR`」会把状态改回 error，用户看到「已完成后又变成失败」。
改为**前置守卫**（先判终态、终态则直接 return）后语义才正确。

`status` 的判断必须读 **ref** 而非 state —— ws 回调闭包捕获的是 connect 时的 `status`，
是过期值。

### D4：兼容旧的 `Finished: *` 字符串（混合部署）

**决定**：`use-build-log` **额外**识别 `Finished: SUCCESS` / `Finished: ERROR` /
`Finished: ERROR` 三种字符串。

**理由**：本变更后端与前端**不在同一个 commit 部署**（前端是静态资源，经
`:aicode-web` 打包进 Jar）—— 存在**新旧混跑窗口**：旧前端 + 新后端，或新前端 + 旧后端。
若前端只认 JSON，混跑时新前端会退回到"永不自动关闭"（与现状相同，不算退化）；
若只认字符串，改完后端 JSON 消息又会让旧前端把 JSON 当日志行显示（更糟）。

**两套都认** → 混跑期行为与各自现状一致，部署完成后自动收敛到 JSON 优先。

**⚠️ 字符串匹配虽是脆弱手段**（我在 init 的 spec 里刚写过"不依赖字符串匹配"），
但这里是**刻意的兼容兜底**，且有测试守护。终态判定的主路径仍是结构化 JSON。

### D5：`BuildLogPanel` 仅在 `done` 时自动收起，且**不丢弃日志**

**决定**：`status === 'done'` 时面板 `return null`（自动收起）；`error` 时**保持渲染**。
日志条目在两种情况下都保留在 `useBuildLog` 的 state 里。

**为何 `error` 不收起**（实施期发现并修正的设计疏漏）：我最初写的是「`done` / `error` 都收起」，
实施时既有测试「error 状态渲染 ❌ 徽章」立刻变红 —— 该测试正当地表达了
**构建失败时用户需要看到失败原因**。把错误面板自动藏掉是反直觉的，
且会让「构建失败」看起来像是「什么都没发生」。

**理由**：
- 自动关闭解决"面板一直占空间 + 用户不知道好了没"；
- 不丢日志解决"构建失败想回头查日志时没了"。

实现上 `BuildLogPanel` 已经 `if (status === 'idle') return null`，只需把条件扩展为
`if (status === 'idle' || status === 'done' || status === 'error') return null`。

**重新打开的入口**：列表行的「构建」按钮（`ProjectListPage` 的 `buildingCodes` 驱动）。
注意 `buildingCodes` 是**页面级**状态，构建结束后不会被自动清除 —— 见 Risks。

## Risks / Trade-offs

**[`ProjectListPage.buildingCodes` 不会因终态自动清除]** → 用户点过构建后，
该行的「构建中…」标记**只**在 `useBuildLog.clear()` 时消失（用户点关闭按钮）。
自动关闭面板**不会**触发 `clear()`（那会丢掉日志），所以**列表行的标记仍在**。

**处置**：本次**不改** `ProjectListPage`（那是 M3-B 的既有设计，且用户可以点关闭按钮
手动清）。但这是本变更后**用户体验上的一处不协调**，须在 design 与 tasks 中标注，
留给后续变更：让 `BuildLogPanel` 在终态时回调 `ProjectListPage` 清除对应 code。

**[17 处日志行仍是自由文本]** → 这是刻意选择（D1）。代价：构建任务**没有阶段进度**，
用户只能看到滚动的日志而非百分比条。可接受 —— 日志本身信息量已足够。

**[`GeneratorSVImpl` 是生成主链路]** → 改动限定在 3 行消息构造，且
`git diff` 可核实控制流未变。测试上，`CodeGenPipelineTest` 等既有测试仍全绿即为佐证。

**[`logsSV.saveLogs` 的文本与 ws 消息不再一致]** → 落盘仍是 `Finished: SUCCESS`，
ws 是 JSON。运维看服务端日志不受影响；若将来要做"日志与 UI 严格一致"的校验，
需另开变更对齐两者。

## Migration Plan

单批交付（一个 commit）：

1. `facade`：新建 `BuildProgress`；
2. 后端：`GeneratorSVImpl` 3 行改为 JSON；
3. 前端：提取 `progress-event.ts` + `use-init-progress` 改用它；
   `use-build-log` 消费终态 + 兼容旧字符串；`BuildLogPanel` 自动关闭；
4. 测试：后端 3 条（终态消息形态）+ 前端分流 9 条（改路径）+ `use-build-log` /
   `build-log-panel` 扩展；
5. 顺带修上一变更遗留的 spec 噪音（3 个被 openspec 误当 Scenario 的 `####` 段落标题）；
6. AGENTS.md 登记 + validate + archive。

**回滚**：单 commit revert。

## Open Questions

- **`ProjectListPage.buildingCodes` 不随终态清除** —— 见 Risks，留给后续变更。
- **构建任务的阶段进度** —— 若要做，需在 `GeneratorSVImpl` 的 7 个步骤边界加
  `{"type":"progress","phase":"...","message":"..."}`，是独立变更。
- **`generator` 阶段（AGENTS §5.5 三重循环）最耗时** —— 若用户反馈"构建日志看不出进度"，
  优先在该处加阶段上报。