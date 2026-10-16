# Spec Delta

## MODIFIED Requirements

### Requirement: ws-client SHALL 是受鉴权 + 一次重连的轻 WebSocket 包装

ws 客户端 SHALL 建立到后端 WebSocket 端点的轻量连接，自动注入鉴权参数、失败时**重试一次**。

连接 SHALL 携带**房间标识**（当前用查询参数 `buildId`，值为项目编码），使服务端能把
消息投递到与该任务相关的连接。

**同页面的多次连接 SHALL 可共存**：用户开多个标签页 / 同一页面在不同时刻建立的连接
SHALL NOT 互相顶掉 —— 服务端 SHALL 按房间分别持有会话，关闭其中一个连接 SHALL NOT 影响
其他连接。消费端 SHALL NOT 因通道上出现其他连接的消息而把内容当自己的渲染。

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

#### Scenario: 连接自动注入鉴权参数

- **WHEN** 客户端建立 ws 连接
- **THEN** 连接 URL SHALL 携带 token 参数
- **AND** 失败时 SHALL 最多重试一次，SHALL NOT 无限重连

#### Scenario: 连接 SHALL 携带房间标识

- **WHEN** 客户端为某项目建立 ws 连接
- **THEN** 连接 URL SHALL 携带该项目的房间标识（`buildId`）
- **AND** 服务端 SHALL 按该标识把消息只投递给对应房间的连接

#### Scenario: 同页面多连接 SHALL 共存且互不顶掉

- **WHEN** 用户开了两个标签页访问同一项目，各自建立 ws 连接
- **THEN** 两个连接 SHALL 都保持可用
- **AND** 关闭其中一个 SHALL NOT 导致另一个收不到推送
