# aicode-web-realtime Specification

## Purpose

定义 aicode-web 中"前端实时事件通道（WebSocket）"的可执行形状：前端 SHALL 通过 `ws-client`（轻量 `WebSocket` 包装）消费后端 `/websocket.shtml` 推送；`useBuildLog` hook SHALL 协同 `api.execute` 与 ws 接收构成 `idle | running | error` 状态机；`<BuildLogPanel>` UI 组件 SHALL 渲染实时条目与运行时长并支持手动关闭。本能力的形态是 M2-2 `aicode-web-auth` 在 WebSocket 维度的镜像（薄客户端 + 集成 hook），未来接其它实时事件（如系统通知、构建完成后 `ProjectJobVO.state` 变更推送）都按这套样板扩展。

## Requirements

### Requirement: ws-client SHALL 是受鉴权 + 一次重连的轻 WebSocket 包装

`ws-client` SHALL 接受 `onMessage` 回调并返 `{ close(), send() }`；建立连接时 SHALL 把当前 `token` 注入到 URL 的 `?token=`（与 `auth-account` spec 中"URL 参数兼容"对齐）；首次连接掉线 SHALL 尝试重连一次（间隔 500ms，最多 1 次），同一页面多次调 `connect` SHALL NOT 泄漏之前的连接。

#### Scenario: ws-client 持有 token 时把 token 注入 URL

- **WHEN** `connect('ws://localhost:8080/websocket.shtml')` 在 token 存在时被调
- **THEN** 实际连接的 URL SHALL 含 `?token=<token>`，SHALL NOT 含 token 在 WebSocket 子协议字段之外的位置

#### Scenario: ws-client 首次连接掉线时尝试一次重连

- **WHEN** 首次 `WebSocket.onclose` 触发
- **THEN** ws-client SHALL 在 500ms 后发起一次重连（仍带相同 token 与 onMessage）
- **AND** 重连后再次掉线 SHALL NOT 再尝试连接，事件流回到 `idle`

#### Scenario: 同一组件多次连接互不泄漏

- **WHEN** 同 `useBuildLog` hook 内连续两次 `startStream(code)` 调用
- **THEN** 第一次的 ws 连接 SHALL 被 `close()` 释放，SHALL NOT 与第二次并存导致两条独立日志线

### Requirement: useBuildLog SHALL 协同 api.execute 与 ws 接收，返回 3 状态

`useBuildLog(projectCode)` React hook 返 `{ status, entries, startedAt, startStream(), clear() }`，其中：
- `status ∈ { 'idle', 'running', 'error' }` —— **不**含 `completed`（设计决定见下方 blockquote）；
- `startStream()` SHALL 调 `api.execute({ code: projectCode })`，收到 `R.success` 后 SHALL 设 `status = 'running'` 并保持 ws 连入；
- ws 收到的每条原始文本 SHALL 移除后端 `WSClientManager.sendMessage(...)` 加的 `"# "` 前缀后，追加为一条 entry（时间戳 + 文本）；
- `api.execute` 抛 `ContractError` 或 ws 连续断连（不含重连）时 SHALL 设 `status = 'error'`，追加一条错误 entry；
- `status = 'running'` SHALL 持续到用户手动 `clear()` 或组件 unmount（cleanup），而非依赖后端发"sentinel 字符串"——本仓库后端 `GeneratorSVImpl.aiCode` 的 ws 推送是 free-form 日志（实测 line 117/129/138/155/163-165/169/180 等），没有 `DONE` 标识；
- 同 `useEffect` cleanup SHALL 调用 `ws-client.close()` 并重置 `status = 'idle'`。

> **设计决定（completed 状态刻意砍掉）**：构建完成的"completed"语义不在本能力范围。后端 `ProjectJob.state` 从 `Executing` 变 `Completed` 是另一回事，由前端拉取 `ProjectJobVO` 详情（未来的"项目构建完成"详情面板，M3-E 议程）才能看到。Real-time panel 只展示"进行中条目 + 主动关闭"。本能力不再加 `completed` 状态，由"ws 仍在收 / ws 已关"两类视觉区分。

#### Scenario: startStream 立即把 status 切到 running 并追加起始条目

- **WHEN** 用户点"构建"按钮触发 `startStream()`
- **THEN** `status` SHALL 立即从 `idle` 变为 `running`，`entries` SHALL 至少含一条 "开始构建" 系统提示条目，`startedAt` SHALL 被赋值
- **AND** `api.execute({ code })` SHALL 被调用一次（用 MSW 拦截验证）

#### Scenario: ws 推送逐条追加至 entries（剥离 `# ` 前缀）

- **WHEN** 后端 `GeneratorSVImpl.aiCode` 推送 `"# git 努力下载中....."` 形式消息
- **THEN** `useBuildLog` SHALL 解析为 `entries += { at, text: 'git 努力下载中.....' }`，SHALL NOT 在前端列表里出现 `# ` 前缀

#### Scenario: api.execute 抛 ContractError 时 status = error

- **WHEN** `api.execute` 返回 `R{ success: false, code: '9999', info: '...' }` 或抛 HTTP 异常
- **THEN** `status` SHALL 变 `error`，entries SHALL 含 "构建失败: <info>" 一条
- **AND** ws 连接如有 SHALL 被关闭

#### Scenario: useBuildLog 卸载时关闭 ws

- **WHEN** 持有 `useBuildLog()` 结果的组件 unmount
- **THEN** 任何 `running` 状态的 ws 连接 SHALL 在 cleanup 函数内被 `close()` 并设 `status = 'idle'`
- **AND** 重新挂载组件 SHALL NOT 让 ws 在新挂载之外保留

#### Scenario: 手动 clear() 不丢弃已收 entries 但停 ws

- **WHEN** 用户在 `running` 状态点击 `<BuildLogPanel>` 的"关闭"按钮调 `clear()`
- **THEN** `status` SHALL 变 `idle`，ws 连接 SHALL 被关闭；然而本约束**不**清除已有 entries（用户可以再次 click "构建"续接）

### Requirement: BuildLogPanel SHALL 是 useBuildLog 的浅渲染，不持业务逻辑

`<BuildLogPanel projectCode>` UI 组件 SHALL 直接消费 `useBuildLog(projectCode)` 返回值并渲染条目；mount 时 SHALL 调 `useBuildLog().startStream()` 触发构建；`status === 'idle'` 时组件 SHALL NOT 渲染任何 DOM（返回 null）；其它状态 SHALL 渲染条目列表与关闭按钮；`status === 'error'` 时 SHALL 额外显示 ❌ 徽章。

#### Scenario: BuildLogPanel 状态机与条目渲染

- **WHEN** 组件被挂载，`status = 'running'` 且有 ws 推送正在续
- **THEN** 组件 SHALL 渲染：① 标题"构建日志：<projectCode>" ② "已运行 N 秒" ③ 条目列表按时间倒序 ④ "关闭"按钮（调用 `useBuildLog().clear()` 重置 `status` 并 close ws）
- **WHEN** `status` 变 `error`，则 SHALL 额外显示最终状态徽章（❌）
- **WHEN** `status` 变 `idle`，则 SHALL NOT 渲染任何 DOM

#### Scenario: 关闭按钮释放 ws 与状态

- **WHEN** 用户点 "关闭"按钮（`status` 为 `running`/`error`）
- **THEN** 调用 `useBuildLog().clear()` SHALL 重置 `status` 到 `idle` 并 close 任何打开的 ws 连接；点击之后 SHALL NOT 在页面残留任何 `data-testid="build-log-panel"`
