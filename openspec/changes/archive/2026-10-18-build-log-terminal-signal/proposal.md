# Proposal

## Why

构建完成后，`BuildLogPanel` **一直挂在界面上**，用户不知道构建好了没 —— 只能自己点「关闭」
按钮才能收起。`build-log-panel.tsx` 的 design D1 把这写成了既定设计（「`running → idle` 由用户
主动关闭触发」），但那是在**当时没有可用终态信号**的前提下的将就。

## 更正一个此前的错误判断

我在 `2026-10-18-init-async-progress` 里说「构建日志**无完成信号**，前端不知道何时结束」。
**这个判断不准确。** 核实后：

`GeneratorSVImpl.aiCode()` 的 `finally` 块里**早已推送**终态消息：

```java
if (Completed) { WSClientManager.sendMessage("Finished: SUCCESS"); }
else           { WSClientManager.sendMessage("Finished: ERROR"); }
WSClientManager.sendMessage("End");
```

catch 块里也有 `构建失败，详见日志（projectJob=xxx）`。

**问题是前端完全不识别它们** —— `use-build-log.tsx` 对每条消息一律 `appendEntry(msg)`，
把 `Finished: SUCCESS` 当普通日志行显示。grep 该文件，`Finished` **零命中**。

**所以这不是后端缺功能，而是前端漏消费** —— 改动量比预想小一个量级。

## What Changes

### 后端：把 3 行终态消息升级为结构化事件（业务逻辑零改动）

`GeneratorSVImpl` 的 3 处终态消息改为与 `init` 相同的 JSON 结构：

| 位置 | 改前 | 改后 |
|---|---|---|
| `catch` 块 | `"构建失败，详见日志（projectJob=xxx）"` | `{"type":"error","message":"构建失败…"}` |
| `finally` 成功 | `"Finished: SUCCESS"` | `{"type":"done","message":"构建成功"}` |
| `finally` 失败 | `"Finished: ERROR"` | `{"type":"error","message":"构建失败"}` |

**17 处中间日志行仍是自由文本**（用户已拍板范围）。故 ws 通道上**两类消息并存** ——
分流判据沿用 `2026-10-18-init-async-progress` 的「首字符 `{`」。

**⚠️ 生成主链路零改动**：3 行都是消息构造，`aiCode` 的控制流、状态机
（`ProjectJobState.Completed` / `Error`）、`finally` 的清理逻辑全部不动。

### 前端：通用分流 + 自动关闭

- 把 `parseInitEvent` 提取为通用的 `parseProgressEvent`（放 `lib/realtime/progress-event.ts`），
  两个 hook 共用一套分流判据；
- `use-build-log` 消费 `done` / `error`：置终态、记录成功/失败、**停止累加日志**；
- `BuildLogPanel` 收到终态后**自动关闭**（可重新点开查看完整日志）。

## BREAKING

`Finished: SUCCESS` / `Finished: ERROR` 两条消息的**文本格式**变化。但它们此前从未被前端识别，
**无实际破坏**。`logsSV.saveLogs` 落盘的文本**保持不变**（服务端日志里仍可 grep 到）。

## Capabilities

### Modified Capabilities

- **`aicode-web-realtime`** —— MODIFIED Req「useBuildLog SHALL 协同 api.execute 与 ws 接收，返回 3 状态」：
  补上「SHALL 识别结构化终态事件并转入终态」+ 4 状态机（`idle`/`running`/`done`/`error`，
  原 Req 说的 3 状态不足）；`BuildLogPanel` Req 补「收到终态 SHALL 自动关闭」。
- **`init-async-progress`** —— MODIFIED Req「长耗时端点 SHALL 异步接受并经 WebSocket 推结构化进度」：
  该 Requirement 原本只覆盖「请求侧长耗时端点」，本次把**已推送日志流**纳入同一协议族，
  并明确「日志行保持自由文本、只有终态结构化」这条边界（避免未来有人把 17 处日志全改 JSON）。

### New Capabilities

（无。）

## Impact

- **后端**：`facade` 新建 `BuildProgress`（与 `ProjectInitProgress` 字段一致）+
  `GeneratorSVImpl` 3 行消息构造；**契约零变化**（ws 不在 OpenAPI 范围）。
- **前端**：`progress-event.ts`（新，通用分流）+ `use-build-log` 改造 + `BuildLogPanel` 自动关闭
  + 测试。
- **测试**：后端 `GeneratorSVImpl` 的终态消息单测（3 条）；前端 `progress-event` ~9 条 +
  `use-build-log` / `build-log-panel` 扩展。
- **风险**：**低**。生成主链路只改 3 行消息构造；前端改动是消费侧增强（此前完全没消费）。