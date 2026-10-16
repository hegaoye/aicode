# Proposal

## Why

代码生成是**最耗时**的操作（clone 外部模板仓 + 三重循环生成源码），但前端**看不到任何阶段信息** ——
用户只能盯着一串滚动的自由文本日志，不知道"到哪一步了""还要多久""是不是卡住了"。

现状核实（`GeneratorSVImpl.aiCode`，7 个步骤）：

| 步骤 | 现有日志 |
|---|---|
| 1. 创建项目 | 有（`Start By AI-Code...`）|
| 2. 获取类信息 | 有 |
| 3. 获取模板信息 | 有多条（下载模板、git 努力下载中…）|
| 4. 生成源码 | 有（`开始生成源码...`）|
| **5. 获取模块信息** | **无** |
| **6. 获取版本控制信息** | **无** |
| 7. 创建压缩文件 | 有（ZIP 下载链接）|

**没有统一的阶段标识**，第 5、6 步甚至完全静默。前端无法把日志归类到步骤。

而地基都已就位：`2026-10-18-init-async-progress` 建立了结构化事件协议
（`progress` / `done` / `error`），`2026-10-18-websocket-room-routing` 做了房间路由。

## What Changes

### 后端：7 个步骤各加一行 progress 上报

- `BuildProgress` 补 `progress(phase, current, total, message)` 工厂（当前只有 `done` / `error`）；
- `GeneratorSVImpl.aiCode` 在 7 个步骤开头各推一条
  `{"type":"progress","phase":"...","current":N,"total":7,"message":"..."}`。

**⚠️ 生成主链路（AGENTS §5.5 称其为核心资产）只增日志上报**：
控制流、状态更新（`ProjectJobState.Completed` / `Error`）、`finally` 的模板清理全部不动。
现有自由文本日志**一行不删** —— 它们承载了 progress 事件无法表达的细节（git 输出、ZIP 链接）。

### 前端：识别 progress 并显示阶段

- `useBuildLog` 识别 `type=progress` → 记入 `stage`（`phase` / `current` / `total` / `message`），
  **不进 `entries`**（与终态同理 —— 阶段信息不是日志行）；
- `BuildLogPanel` 顶部显示「**第 N/7 步：<阶段名>**」+ 进度条；
- 阶段事件**不改变终态**（终态仍只由 `done` / `error` 决定）。

## BREAKING

无。新增的是**增量消息**，旧版前端会把它当普通文本行（无感）。

## Capabilities

### Modified Capabilities

- **`init-async-progress`** —— MODIFIED 该 Req：补「长耗时任务 SHALL 在每个阶段边界上报阶段标识与序号」，
  让消费端能显示"到哪一步了"，SHALL NOT 要求把所有日志行都结构化（那会徒增噪音）。

### New Capabilities

（无。）

## Impact

- **后端**：`BuildProgress` 加一个工厂方法；`GeneratorSVImpl` 加 7 行上报。
- **前端**：`useBuildLog` 加 stage 状态；`BuildLogPanel` 加阶段显示。
- **测试**：后端 `BuildProgressTerminalSignalTest` 补 progress 用例；
  前端 `use-build-log` / `build-log-panel` 各补用例。
- **契约 / 生成物**：零变化（ws 不在 OpenAPI 范围）。
- **风险**：**中低**。唯一动核心资产的是 7 行日志上报；**反向验证 SHALL 覆盖** ——
  临时把某一阶段的上报去掉，前端用例应变红。