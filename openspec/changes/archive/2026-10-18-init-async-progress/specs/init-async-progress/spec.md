# Spec Delta

## ADDED Requirements

### Requirement: 长耗时端点 SHALL 异步接受并经 WebSocket 推结构化进度

预计耗时不可预估的端点（建库、逆解析表结构、批量生成代码等）SHALL NOT 在 HTTP 请求线程里
同步执行到结束。SHALL 立即返回「已接受」，实际执行转移到异步执行器，结果经 WebSocket 推送。

异步执行 SHALL 经 **Spring 代理调用**（即执行逻辑放在独立的 `@Component` 里，由 Controller
或另一个 Bean 注入调用），SHALL NOT 在同一个对象内部用 `this.xxx()` 调用 —— 那会绕过 AOP
代理使 `@Async` 静默失效、请求继续同步阻塞。

#### 进度消息的形态

进度消息 SHALL 结构化且含工作量

服务端推送的进度消息 SHALL 是**结构化对象**（JSON），SHALL NOT 是无结构的自由文本。至少
区分三种 `type`：

| type | 语义 | 必含字段 |
|---|---|---|
| `progress` | 执行中 | `phase`（阶段标识）、`message`（可读文案）、`current` / `total`（已完成 / 总数；不可知时为 0）|
| `done` | 成功结束 | `message` |
| `error` | 失败结束 | `message`（含失败原因与**已完成的工作量**）|

进度 SHALL 报到**可反映真实耗时的粒度**：既有阶段划分（建库 / 解析 / 生成），也有循环内的
逐项进度（第 i/N 项）。SHALL NOT 只报「处理中」而让用户在长循环期间看不到任何变化。

`done` 与 `error` SHALL 各自只推一次，且二者必有其一 —— 否则消费端无法判断何时该刷新结果。

#### 失败时的语义

失败 SHALL 如实上报已完成工作量

长耗时执行常是「逐项写入且无事务」的实现（每项独立提交）。这类执行在中途失败时，
已写入的部分**不回滚**。此时错误消息 SHALL 明确告知**已完成多少项 / 共多少项**，
SHALL NOT 让消费端以为「全都没做」而引导用户重跑全部，也 SHALL NOT 谎报成功。

> 是否引入事务以保证原子性是**独立议题**，不在本 Requirement 范围。

#### 与既有日志共存

结构化事件 SHALL 与既有自由文本日志共存于同一通道

既有构建日志（`GeneratorSVImpl`）走的是自由文本推送。结构化进度事件 SHALL 与之共存于
**同一个 WebSocket 通道**，SHALL NOT 独占新通道 —— 独占会让消费端需要维护两条连接与两套
鉴权/重连逻辑。

两者 SHALL 可无歧义分流：结构化事件 SHALL 以 JSON 对象开头（`{`），SHALL NOT 与自由文本
混淆。既有消费方（构建日志）SHALL NOT 因新增事件而受影响。

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
- **AND** 消费端 SHALL 以该消息作为「刷新结果」的触发时机
- **AND** 当执行失败时 SHALL 推送恰好一条 `type=error` 的消息，SHALL NOT 既不推 done 也不推 error

#### Scenario: 失败 SHALL 报告已完成工作量

- **WHEN** 逐项写入的无事务执行在第 i 项失败（共 N 项，前 i-1 项已写入且不回滚）
- **THEN** `error` 消息 SHALL 包含失败原因与已完成/总数（`i-1` / `N`）
- **AND** SHALL NOT 暗示「全部未执行」而让用户误以为需要重跑全部

#### Scenario: 结构化事件与自由文本日志 SHALL 共存且可分流

- **WHEN** 同一 WebSocket 通道上既有结构化进度事件、又有构建日志的自由文本
- **THEN** 消费端 SHALL 能无歧义地区分两者（结构化事件以 `{` 开头）
- **AND** 既有构建日志消费方 SHALL NOT 因新增的结构化事件而把进度事件当日志行渲染，
  也 SHALL NOT 把日志行当进度事件解析而报错