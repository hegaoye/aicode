# Design

## Context

代码生成是最耗时的操作（clone 外部模板仓 + 三重循环生成源码），但前端看不到阶段信息 ——
用户只能盯着一串滚动的自由文本日志。

现状核实（`GeneratorSVImpl.aiCode`，7 个步骤）：第 1、2、3、4、7 步有零散日志，
**第 5、6 步完全静默**，且**没有统一的阶段标识**，前端无法把日志归类到步骤。

地基已就位：`init-async-progress`（结构化协议）、`websocket-room-routing`（房间路由）。

用户已拍板：**7 步全上报**。

## Goals / Non-Goals

**Goals:**

- 7 个步骤各上报一次 `progress`（`phase` 稳定标识 + `current` / `total` + `message`）；
- 前端显示「第 N/7 步：<阶段名>」+ 进度条；
- **生成主链路只增日志上报，控制流一行不动**。

**Non-Goals:**

- 不把 17 处中间日志行也结构化（承载的是阶段标识无法表达的细节：git 输出、ZIP 链接）。
- 不给阶段加耗时统计（"这一步花了 23 秒"）—— 需要在每步前后打时间戳 + 客户端算差，
  收益有限，留待有反馈再定。
- 不做阶段的取消 / 重试。

## Decisions

### D1：7 个步骤的 `phase` 用**稳定英文标识**，与展示文案分离

| # | `phase` | `message`（展示） |
|---|---|---|
| 1 | `buildProject` | 创建工作空间 |
| 2 | `convertModel` | 转换模型 |
| 3 | `downloadTemplate` | 下载技术模板 |
| 4 | `generateSource` | 生成源码 |
| 5 | `readModule` | 获取模块信息 |
| 6 | `versionControl` | 版本控制与推送 |
| 7 | `packageZip` | 打包交付 |

**为何分离**：`phase` 是给程序判别的（测试可断言、前端可映射图标），`message` 是给人看的。
若用中文文案当 `phase`，文案一改测试就红。

### D2：在每个步骤的**第一行**上报，不在末尾

**决定**：把 `report(...)` 插在步骤注释之后、该步骤的第一条业务语句之前。

**理由**：用户关心的是"现在卡在哪"，而不是"刚做完什么"。步骤开始时上报，
配合"上一步完成时日志已滚过"的时间线，用户能立刻定位当前阻塞点。

### D3：用一个私有方法收敛上报，避免 7 处重复序列化

```java
private void reportStage(String phase, int current, int total, String message) {
    WSClientManager.sendMessage(JSON.toJSONString(
            BuildProgress.progress(phase, current, total, message)));
}
```

**理由**：7 处各写一遍 `JSON.toJSONString(BuildProgress.progress(...))` 会在将来改协议时
漏改。其余 17 处 `sendMessage` **一行不动**（它们是自由文本日志）。

### D4：`useBuildLog` 新增 `stage` 状态，progress 事件不进 entries

```ts
stage: { phase, current, total, message } | null
```

**progress 事件不 `appendEntry`** —— 与终态同理。理由：阶段信息已在面板顶部结构化展示，
再塞进日志列表会造成重复内容。

**progress 事件不改变 `status`** —— 终态仍只由 `done` / `error` 决定。
（`use-build-log` 现有逻辑是 `if (event && event.type !== 'progress')`，本变更要把
progress 分支单独处理。）

### D5：`BuildProgress` 补 `progress` 工厂

当前只有 `done` / `error`（上一个变更加的，因为只发终态）。本变更补
`progress(phase, current, total, message)`，与 `ProjectInitProgress.progress` 同形 ——
**两侧字段刻意一致**，让前端的 `ProgressEvent` 一套类型通吃。

## Risks / Trade-offs

**[动生成主链路]** → `aiCode` 是 AGENTS §5.5 的核心资产。本变更只**插入** 7 行上报，
不删除/修改任何既有语句。**验证方式**：
- `git diff` 核实控制流未变（只剩新增行）；
- `BuildProgressTerminalSignalTest` 补 progress 用例；
- **反向验证**：临时去掉某一步的上报 → 前端阶段用例变红。

**[老前端遇到 progress 事件]** → 会把它当普通日志行显示（`appendEntry`）——
**无感但会多出一行 JSON 文本**。混合部署窗口内可接受（上一变更的终态事件同理）。

**[`JSON` import]** → `GeneratorSVImpl` 已 import `com.alibaba.fastjson2.JSON`（我核实过），
无需新增依赖。

## Migration Plan

单批（一个 commit）：

1. `BuildProgress` 加 `progress` 工厂 + 单测；
2. `GeneratorSVImpl` 加 `reportStage` 私有方法 + 7 处调用；
3. 前端 `useBuildLog` 加 `stage` 状态 + progress 分支；
4. 前端 `BuildLogPanel` 加阶段显示；
5. 测试（后端 + 前端）+ 反向验证；
6. 文档 + validate + archive。

**回滚**：单 commit revert。

## Open Questions

- **阶段耗时统计**（"下载模板花了 12 秒"）—— 需要客户端在两次 progress 之间计时。
  当前无需求信号。
- **阶段与模板仓库的映射** —— `downloadTemplate` 阶段内可能 clone 多个框架仓库，
  细到"正在下载 SpringBoot 模板"需要改 `prepareframeworksTemplateList` 内部循环。
  当前 7 步粒度已能定位问题。