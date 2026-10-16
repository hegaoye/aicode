# Spec Delta

## ADDED Requirements

### Requirement: 消费端 SHALL 按消息形态分流结构化事件与自由文本

WebSocket 通道上可能同时出现两类消息：**结构化事件**（以 `{` 开头的 JSON，用于长耗时任务的
进度与终态）与**自由文本日志行**（既有构建日志）。消费端 SHALL 先判定消息形态再分派：

- 形态判定 SHALL 基于消息是否以 `{` 开头，SHALL NOT 依赖消息内容特征（如关键词匹配）；
- 结构化事件 SHALL 按其 `type` 字段分派到对应处理器；
- 自由文本 SHALL 交给既有的日志累加器。

消费端 SHALL NOT 因通道上出现另一类消息而中断订阅、抛异常或把对方的内容当自己的内容
渲染。

#### Scenario: 结构化事件与日志行各走各的处理器

- **WHEN** 通道上先后到达一条日志行 `git 努力下载中.....` 与一条结构化事件
  `{"type":"progress","phase":"parse","current":3,"total":12}`
- **THEN** 日志行 SHALL 进入日志条目列表
- **AND** 结构化事件 SHALL 触发进度处理器的调用，SHALL NOT 出现在日志条目列表里

#### Scenario: 无法解析为 JSON 的文本 SHALL 仍按日志行处理而非抛错

- **WHEN** 通道上到达一条以非 `{` 开头、或以 `{` 开头但不是合法 JSON 的消息
- **THEN** 消费端 SHALL 把它当作自由文本日志处理
- **AND** SHALL NOT 抛出未捕获异常，SHALL NOT 中断订阅

#### Scenario: 既有日志消费方 SHALL NOT 被新增事件污染

- **WHEN** 只关心构建日志的组件订阅了通道，而通道上到达了别的任务的结构化事件
- **THEN** 该组件 SHALL 忽略这些事件，SHALL NOT 把它们渲染为日志条目