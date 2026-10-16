# Spec Delta

## MODIFIED Requirements

### Requirement: useBuildLog SHALL 协同 api.execute 与 ws 接收，返回 3 状态

`useBuildLog(projectCode)` SHALL 协同 `api.execute({ code })` 与 ws 接收，返回构建日志的
状态与条目。消费方（`BuildLogPanel`）SHALL NOT 自行解释 ws 消息。

**SHALL 识别结构化终态事件**：服务端在构建结束（无论成败）时推送 `type=done` 或
`type=error` 的结构化事件，消费端 SHALL 据此转入终态 —— SHALL NOT 把终态事件当作普通日志行
追加到条目列表里让用户自己读。

状态机 SHALL 为 **4 态**：`idle`（未开始）/ `running`（执行中）/ `done`（成功结束）/
`error`（失败结束）。收到终态后 SHALL **停止累加日志**（构建已结束，后续消息多为收尾噪声）。

> **2026-10-18 修订说明**：原文写「返回 3 状态」，但实际需要区分**成功结束**与
> **失败结束** —— 两者对用户是完全不同的信号（前者可下载 ZIP，后者要查日志）。
> 且原文隐含"终态靠用户点关闭按钮触发"（见 `BuildLogPanel` Req），
> 那是当时没有可用终态信号时的将就；服务端其实**早已推送** `Finished: SUCCESS` /
> `Finished: ERROR`，只是前端从未识别。


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


#### Scenario: 首个终态 SHALL 权威 —— 其后的一切消息均忽略

- **WHEN** 已收到终态（`done` 或 `error`）之后，又收到日志行 / `End` 等收尾噪声 /
  甚至**矛盾的终态**（例如先 `done` 后又来 `error`）
- **THEN** 消费端 SHALL 忽略这些消息：不追加到条目列表，SHALL NOT 改变终态
- **AND** 理由：构建已结束，之后的消息都是收尾噪声或异常残留。允许覆盖会让用户看到
  「已完成后又变成失败」这类自相矛盾的现象

#### Scenario: 收到 done 事件 SHALL 转入成功终态并停止累加日志

- **WHEN** 日志流过程中收到 `{"type":"done","message":"构建成功"}`
- **THEN** 状态 SHALL 变为 `done`
- **AND** 该消息 SHALL NOT 出现在日志条目列表里
- **AND** 后续到达的消息 SHALL NOT 继续追加到条目列表

#### Scenario: 收到 error 事件 SHALL 转入失败终态

- **WHEN** 日志流过程中收到 `{"type":"error","message":"构建失败，详见日志"}`
- **THEN** 状态 SHALL 变为 `error`
- **AND** 该消息 SHALL NOT 出现在日志条目列表里
- **AND** 失败原因 SHALL 可被消费方读取（供 UI 呈现）

#### Scenario: 自由文本日志行 SHALL 照常累加

- **WHEN** 收到 `git 努力下载中.....` 这类自由文本消息
- **THEN** 它 SHALL 被追加到日志条目列表
- **AND** 状态 SHALL 保持 `running`（直到收到终态）

#### Scenario: 兼容旧格式的终态字符串

- **WHEN** 收到 `Finished: SUCCESS` / `Finished: ERROR` 这类**非结构化**的终态字符串
  （旧版服务端，或本变更尚未部署的混合版本）
- **THEN** 消费端 SHALL 仍能识别并转入对应终态，SHALL NOT 把它当普通日志行
- **AND** 该兼容 SHALL 有测试守护 —— 混合部署期间两个版本会并存

### Requirement: BuildLogPanel SHALL 是 useBuildLog 的浅渲染，不持业务逻辑

`<BuildLogPanel projectCode>` SHALL 是 `useBuildLog` 的浅渲染：面板自身 SHALL NOT 解释
ws 消息、SHALL NOT 判断构建是否结束、SHALL NOT 维护独立于 hook 的状态。

`BuildLogPanel` 新增一个 `@Component` 形态的 "done"（构建成功）—— **由 hook 驱动**：
面板消费 `status === 'done'`，SHALL NOT 自己监听 ws。

**构建成功后 SHALL 自动收起面板**（`done` 态返回 `null`，等同 `idle` 的表现），并 SHALL 提供重新打开
的入口（调用 `startStream` 或复用已有日志）。

**构建失败时 SHALL NOT 自动收起**（`error` 态保持渲染，含 ❌ 徽章与失败原因）——
把错误面板自动藏掉是反直觉的，且会让「构建失败」看起来像是「什么都没发生」。
用户看完错误后 SHALL 仍可点「关闭」手动收起。

> **为何自动收起**：构建往往要几十秒，期间面板一直占据列表行下方的空间。原设计让用户手点
> 「关闭」，是因为当时没有可用的终态信号 —— 而服务端其实早已推送（见 `useBuildLog` Req 的
> 修订说明）。有了可靠终态后，「让用户自己判断何时收起」是本可避免的负担。
>
> **自动收起 SHALL NOT 丢弃已收集的日志**：重新打开 SHALL 仍能看到完整条目。


#### Scenario: BuildLogPanel 状态机与条目渲染

- **WHEN** 组件被挂载，`status = 'running'` 且有 ws 推送正在续
- **THEN** 组件 SHALL 渲染：① 标题"构建日志：<projectCode>" ② "已运行 N 秒" ③ 条目列表按时间倒序 ④ "关闭"按钮（调用 `useBuildLog().clear()` 重置 `status` 并 close ws）
- **WHEN** `status` 变 `error`，则 SHALL 额外显示最终状态徽章（❌）
- **WHEN** `status` 变 `idle`，则 SHALL NOT 渲染任何 DOM

#### Scenario: 关闭按钮释放 ws 与状态

- **WHEN** 用户点 "关闭"按钮（`status` 为 `running`/`error`）
- **THEN** 调用 `useBuildLog().clear()` SHALL 重置 `status` 到 `idle` 并 close 任何打开的 ws 连接；点击之后 SHALL NOT 在页面残留任何 `data-testid="build-log-panel"`

#### Scenario: autoStart={false} 时不触发构建但面板照常渲染

- **WHEN** 组件以 `autoStart={false}` 挂载
- **THEN** `useBuildLog().startStream()` SHALL NOT 被调用（即 SHALL NOT 调 `api.execute`）
- **AND** 组件 SHALL 按当前 `status` 正常渲染面板 DOM —— 防护 SHALL NOT 导致面板消失或日志不可见

#### Scenario: onMounted 每次挂载恰好回调一次

- **WHEN** 组件挂载（无论 `autoStart` 取值）
- **THEN** `onMounted` SHALL 恰好被调用 1 次
- **AND** 组件卸载后重新挂载（模拟列表页翻走再翻回），`onMounted` SHALL 再被调用 1 次（累计 2 次）
- **AND** 未传 `onMounted` 时 SHALL NOT 抛错（该 prop 可选）


#### Scenario: running 状态渲染构建日志

- **WHEN** `status === 'running'`，面板渲染日志条目、已运行时长与关闭按钮
- **THEN** 关闭按钮 SHALL 调 `useBuildLog().clear()`，手动收起面板

#### Scenario: 构建成功后面板 SHALL 自动收起

- **WHEN** `status` 从 `running` 变为 `done`
- **THEN** 面板 SHALL 渲染为 `null`（从 DOM 移除），SHALL NOT 继续占据空间
- **AND** 上层列表行的"构建中…"标记 SHALL 随之结束（由 hook 的状态驱动）

#### Scenario: 构建失败时面板 SHALL 保留（不自动收起）

- **WHEN** `status` 变为 `error`
- **THEN** 面板 SHALL 继续渲染，含 ❌ 徽章与失败原因
- **AND** SHALL NOT 自动收起 —— 用户需要看到失败信息

#### Scenario: 自动收起 SHALL NOT 丢弃已收集的日志

- **WHEN** 构建结束后面板自动收起，随后用户再次打开构建流
- **THEN** 面板 SHALL 仍能渲染构建期间收集到的全部日志条目