# init-async-progress

## Purpose

长耗时端点（逆解析表结构、批量生成代码）SHALL 异步接受并经 WebSocket 推结构化进度，
SHALL NOT 在 HTTP 请求线程里同步执行到结束；消费端 SHALL 能据此判断进度、
判定完成、以及在失败时得知已完成的工作量。

## Requirements

### Requirement: 长耗时端点 SHALL 异步接受并经 WebSocket 推结构化进度

预计耗时不可预估的端点（建库、逆解析表结构、批量生成代码等）SHALL NOT 在 HTTP 请求线程里
同步执行到结束。SHALL 立即返回「已接受」，实际执行转移到异步执行器，结果经 WebSocket 推送。

异步执行 SHALL 经 **Spring 代理调用**（即执行逻辑放在独立的 `@Component` 里，由 Controller
或另一个 Bean 注入调用），SHALL NOT 在同一个对象内部用 `this.xxx()` 调用 —— 那会绕过 AOP
代理使 `@Async` 静默失效、请求继续同步阻塞。

**进度消息 SHALL 投递到与该任务相关的连接**：服务端 SHALL 按**房间**路由
（房间标识由客户端在建连时携带，见 `aicode-web-realtime` 的 ws-client Requirement），
SHALL NOT 把某个任务的进度推送给其他连接的持有者 —— 否则用户在 A 页面会看到 B 页面的
进度。

**阶段 SHALL 可观测**

长耗时任务若由**多个顺序步骤**组成（代码生成的 7 步：建工作空间 / 转换模型 / 下载模板 /
生成源码 / 生成附加 SQL / 版本控制 / 打包），服务端 SHALL 在**每个步骤边界**上报一次
`type=progress` 事件，含：

- `phase`（该步骤的**稳定标识**，如 `downloadTemplate`）—— SHALL 稳定，
  SHALL NOT 用会随文案变化的字符串；
- `current` / `total`（步骤序号与总数），让消费端能渲染「第 3/7 步」；
- `message`（该步骤的可读名称）。

**SHALL NOT 要求把中间日志行也结构化** —— 日志行承载的是阶段标识无法表达的细节
（git 输出、生成的文件清单、ZIP 下载链接）。协议边界仍是
「**终态与阶段结构化、中间日志自由文本**」。

消费端 SHALL 把阶段事件与日志行**分开呈现**：阶段用于显示"到哪一步了"，日志用于呈现细节。
SHALL NOT 把阶段事件混进日志列表（那会让用户看到重复的内容）。

#### Scenario: 端点 SHALL 立即返回而非同步阻塞到结束

- **WHEN** 用户调用一个长耗时端点（如导入 SQL 后触发解析）
- **THEN** 服务端 SHALL 立即返回「已接受」的响应，SHALL NOT 等到执行结束才返回
- **AND** 实际执行 SHALL 在异步执行器上进行，SHALL NOT 占用 HTTP 请求线程

#### Scenario: 进度 SHALL 含阶段与逐项计数

- **WHEN** 执行进入某个阶段，且该阶段内有 N 项待处理
- **THEN** 服务端 SHALL 推送 `type=progress` 的消息，含 `phase` 标识与 `current` / `total`
- **AND** 在循环推进过程中 SHALL 至少推送一次带递增 `current` 的消息 ——
  用户 SHALL 能在长循环期间看到变化，SHALL NOT 只看到一条「处理中」然后长时间静默

#### Scenario: 完成 SHALL 有且仅有一个终态消息

- **WHEN** 异步执行成功结束
- **THEN** 服务端 SHALL 推送恰好一条 `type=done` 的消息
- **AND** 消费端 SHALL 以该消息作为「刷新结果」或「收起面板」的触发时机
- **AND** 当执行失败时 SHALL 推送恰好一条 `type=error` 的消息，SHALL NOT 既不推 done 也不推 error

#### Scenario: 中间日志行 SHALL 保持自由文本且被正确分流

- **WHEN** 服务端推送逐步骤的自由文本日志行（如「git 努力下载中.....」）
- **THEN** 该行 SHALL 保持自由文本形态，SHALL NOT 被包装成带 `type` / `phase` 的 JSON
- **AND** 消费端 SHALL 把它识别为日志行而非终态事件（按首字符 `{` 分流）

#### Scenario: 失败 SHALL 报告已完成工作量

- **WHEN** 逐项写入的无事务执行在第 i 项失败（共 N 项，前 i-1 项已写入且不回滚）
- **THEN** `error` 消息 SHALL 包含失败原因与已完成/总数（`i-1` / `N`）
- **AND** SHALL NOT 暗示「全部未执行」而让用户误以为需要重跑全部

#### Scenario: 结构化事件与自由文本日志 SHALL 共存且可分流

- **WHEN** 同一 WebSocket 通道上既有结构化进度事件、又有构建日志的自由文本
- **THEN** 消费端 SHALL 能无歧义地区分两者（结构化事件以 `{` 开头）
- **AND** 既有构建日志消费方 SHALL NOT 因新增的结构化事件而把进度事件当日志行渲染，
  也 SHALL NOT 把日志行当进度事件解析而报错

#### Scenario: 端点 SHALL 立即返回而非同步阻塞到结束

- **WHEN** 用户调用一个长耗时端点（如导入 SQL 后触发解析）
- **THEN** 服务端 SHALL 立即返回「已接受」的响应，SHALL NOT 等到执行结束才返回
- **AND** 实际执行 SHALL 在异步执行器上进行，SHALL NOT 占用 HTTP 请求线程

#### Scenario: 进度 SHALL 含阶段与逐项计数

- **WHEN** 执行进入某个阶段，且该阶段内有 N 项待处理
- **THEN** 服务端 SHALL 推送 `type=progress` 的消息，含 `phase` 标识与 `current` / `total`
- **AND** 在循环推进过程中 SHALL 至少推送一次带递增 `current` 的消息 ——
  用户 SHALL 能在长循环期间看到变化，SHALL NOT 只看到一条「处理中」然后长时间静默

#### Scenario: 完成 SHALL 有且仅有一个终态消息

- **WHEN** 异步执行成功结束
- **THEN** 服务端 SHALL 推送恰好一条 `type=done` 的消息
- **AND** 消费端 SHALL 以该消息作为「刷新结果」或「收起面板」的触发时机
- **AND** 当执行失败时 SHALL 推送恰好一条 `type=error` 的消息，SHALL NOT 既不推 done 也不推 error

#### Scenario: 中间日志行 SHALL 保持自由文本且被正确分流

- **WHEN** 服务端推送逐步骤的自由文本日志行（如「git 努力下载中.....」）
- **THEN** 该行 SHALL 保持自由文本形态，SHALL NOT 被包装成带 `type` / `phase` 的 JSON
- **AND** 消费端 SHALL 把它识别为日志行而非终态事件（按首字符 `{` 分流）

#### Scenario: 失败 SHALL 报告已完成工作量

- **WHEN** 逐项写入的无事务执行在第 i 项失败（共 N 项，前 i-1 项已写入且不回滚）
- **THEN** `error` 消息 SHALL 包含失败原因与已完成/总数（`i-1` / `N`）
- **AND** SHALL NOT 暗示「全部未执行」而让用户误以为需要重跑全部

#### Scenario: 结构化事件与自由文本日志 SHALL 共存且可分流

- **WHEN** 同一 WebSocket 通道上既有结构化进度事件、又有构建日志的自由文本
- **THEN** 消费端 SHALL 能无歧义地区分两者（结构化事件以 `{` 开头）
- **AND** 既有构建日志消费方 SHALL NOT 因新增的结构化事件而把进度事件当日志行渲染，
  也 SHALL NOT 把日志行当进度事件解析而报错

#### Scenario: 消息 SHALL 只送达订阅了该任务房间的连接

- **WHEN** 用户开了两个标签页，标签页 A 订阅项目 P1 的房间，标签页 B 订阅项目 P2 的房间
- **AND** 项目 P1 的构建产生日志
- **THEN** 该日志 SHALL 只送达标签页 A 的连接
- **AND** 标签页 B SHALL NOT 收到该日志（否则用户在 P2 页面看到 P1 的构建日志）
- **AND** 无房间上下文的推送（如 shell 消息）SHALL 广播给所有连接，作为兜底
  —— SHALL NOT 因此让现有功能静默失效

#### Scenario: 端点 SHALL 立即返回而非同步阻塞到结束

- **WHEN** 用户调用一个长耗时端点（如导入 SQL 后触发解析）
- **THEN** 服务端 SHALL 立即返回「已接受」的响应，SHALL NOT 等到执行结束才返回
- **AND** 实际执行 SHALL 在异步执行器上进行，SHALL NOT 占用 HTTP 请求线程

#### Scenario: 进度 SHALL 含阶段与逐项计数

- **WHEN** 执行进入某个阶段，且该阶段内有 N 项待处理
- **THEN** 服务端 SHALL 推送 `type=progress` 的消息，含 `phase` 标识与 `current` / `total`
- **AND** 在循环推进过程中 SHALL 至少推送一次带递增 `current` 的消息 ——
  用户 SHALL 能在长循环期间看到变化，SHALL NOT 只看到一条「处理中」然后长时间静默

#### Scenario: 多步骤任务 SHALL 在每个步骤边界上报阶段

- **WHEN** 一个长耗时任务由 N 个顺序步骤组成
- **THEN** 服务端 SHALL 在每个步骤开始时推送一条 `type=progress`，其 `current` 从 1 递增到 N
- **AND** 每条的 `phase` SHALL 是该步骤的**稳定标识**（SHALL NOT 是随文案变化的字符串）
- **AND** 每条的 `total` SHALL 恒为步骤总数
- **AND** 用户 SHALL 能在界面上看到当前处于第几步、这一步叫什么

#### Scenario: 阶段事件 SHALL NOT 混入日志列表

- **WHEN** 消费端收到 `type=progress` 的阶段事件
- **THEN** 该事件 SHALL 用于更新「当前阶段」展示，SHALL NOT 出现在日志条目列表里
- **AND** 阶段事件 SHALL NOT 改变任务的终态（终态仅由 `done` / `error` 决定）

#### Scenario: 完成 SHALL 有且仅有一个终态消息

- **WHEN** 异步执行成功结束
- **THEN** 服务端 SHALL 推送恰好一条 `type=done` 的消息
- **AND** 消费端 SHALL 以该消息作为「刷新结果」或「收起面板」的触发时机
- **AND** 当执行失败时 SHALL 推送恰好一条 `type=error` 的消息，SHALL NOT 既不推 done 也不推 error

#### Scenario: 中间日志行 SHALL 保持自由文本且被正确分流

- **WHEN** 服务端推送逐步骤的自由文本日志行（如「git 努力下载中.....」）
- **THEN** 该行 SHALL 保持自由文本形态，SHALL NOT 被包装成带 `type` / `phase` 的 JSON
- **AND** 消费端 SHALL 把它识别为日志行而非阶段事件（按首字符 `{` 分流）

#### Scenario: 失败 SHALL 报告已完成工作量

- **WHEN** 逐项写入的无事务执行在第 i 项失败（共 N 项，前 i-1 项已写入且不回滚）
- **THEN** `error` 消息 SHALL 包含失败原因与已完成/总数（`i-1` / `N`）
- **AND** SHALL NOT 暗示「全部未执行」而让用户误以为需要重跑全部

#### Scenario: 结构化事件与自由文本日志 SHALL 共存且可分流

- **WHEN** 同一 WebSocket 通道上既有结构化进度事件、又有构建日志的自由文本
- **THEN** 消费端 SHALL 能无歧义地区分两者（结构化事件以 `{` 开头）
- **AND** 既有构建日志消费方 SHALL NOT 因新增的结构化事件而把进度事件当日志行渲染，
  也 SHALL NOT 把日志行当进度事件解析而报错

#### Scenario: 消息 SHALL 只送达订阅了该任务房间的连接

- **WHEN** 用户开了两个标签页，标签页 A 订阅项目 P1 的房间，标签页 B 订阅项目 P2 的房间
- **AND** 项目 P1 的构建产生日志
- **THEN** 该日志 SHALL 只送达标签页 A 的连接
- **AND** 标签页 B SHALL NOT 收到该日志（否则用户在 P2 页面看到 P1 的构建日志）
- **AND** 无房间上下文的推送（如 shell 消息）SHALL 广播给所有连接，作为兜底
  —— SHALL NOT 因此让现有功能静默失效

