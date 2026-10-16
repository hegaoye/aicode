# Spec Delta

## MODIFIED Requirements

### Requirement: BuildLogPanel SHALL 是 useBuildLog 的浅渲染，不持业务逻辑

`<BuildLogPanel projectCode>` UI 组件 SHALL 直接消费 `useBuildLog(projectCode)` 返回值并渲染条目；mount 时 SHALL 调 `useBuildLog().startStream()` 触发构建；`status === 'idle'` 时组件 SHALL NOT 渲染任何 DOM（返回 null）；其它状态 SHALL 渲染条目列表与关闭按钮；`status === 'error'` 时 SHALL 额外显示 ❌ 徽章。

组件 SHALL 接受两个**可选** prop 用于"重新挂载"场景（列表页翻页会让行内面板卸载再挂载）：

- `autoStart?: boolean`（默认 `true`）—— mount 时是否自动 `startStream()`。传 `false` 时 SHALL NOT 触发构建，SHALL 仍按当前 `status` 正常渲染（防护 SHALL NOT 让面板消失）；
- `onMounted?: () => void` —— 每次 mount 恰好回调一次，供调用方记录"该 code 的面板曾挂载过"，从而在下次挂载时传 `autoStart={false}`。

> **为何需要**：翻回"构建中"所在页时面板会重新挂载，若仍自动 `startStream()` 则会重复调
> `api.execute({ code })`；而后端 `ProjectJobServiceImpl#execute` 对已有 `Executing` 任务的
> `projectCode` 会抛 `Server_Error`，用户看到误导性的"构建失败"。

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