# Spec Delta

## Purpose

定义平台从目标数据库的元数据（`INFORMATION_SCHEMA`）中探测表与列的契约。代码生成主链路的第一步「解析数据库信息」完全依赖该探测能力 —— 探测失效会导致生成功能整体不可用，且可能表现为与「SQL 执行失败」完全相同的错误码。本能力同时要求：探测行为 SHALL 有集成测试守护，使数据库版本升级造成的元数据语义变化在测试阶段即被发现，而不是等到使用者手工触发。

## ADDED Requirements

### Requirement: 基础表类型值随数据库实际返回

系统 SHALL 按数据库**实际返回**的基础表类型值来识别表，SHALL NOT 依赖未经核实的常量。当目标数据库的版本改变了该值的取值时，系统 SHALL 随之调整，SHALL NOT 因此使表探测恒定返回空结果。

#### Scenario: 探测能识别当前数据库返回的基础表

- **WHEN** 目标数据库中存在一张普通表
- **THEN** 表探测 SHALL 返回该表，SHALL NOT 返回空结果

#### Scenario: 数据库版本改变取值后探测仍有效

- **WHEN** 目标数据库的主版本升级，且该升级改变了基础表类型的取值形式
- **THEN** 表探测 SHALL 仍能返回已创建的表

#### Scenario: 探测范围排除非表对象

- **WHEN** 目标数据库中存在视图等非表对象
- **THEN** 表探测 SHALL NOT 将其作为表返回

### Requirement: 探测失败须暴露可定位的真实原因

当建库或建表失败时，系统 SHALL 抛出携带可定位信息的异常（至少包含 schema 名与原始异常），SHALL NOT 仅记录一行不含堆栈的消息后继续。调用方据此 SHALL 能区分「用户 SQL 有误」与「schema 已存在但内容为空」等不同成因，SHALL NOT 将全部成因收敛为同一个兜底错误码。

#### Scenario: 建表失败时异常上抛

- **WHEN** 用户导入的 SQL 在目标数据库中执行失败
- **THEN** 系统 SHALL 抛出包含 schema 名与原始异常的错误，SHALL NOT 静默吞掉

#### Scenario: 不同成因可被区分

- **WHEN** 表探测返回空结果
- **THEN** 系统 SHALL 能据此区分「建表未成功」与「schema 已存在但无表」，SHALL NOT 二者返回同一错误码

#### Scenario: 失败日志保留可定位信息

- **WHEN** 建库或建表过程发生异常
- **THEN** 日志 SHALL 包含足以定位到具体 schema 与原始异常的信息

### Requirement: 已存在的 schema 需校验内容而非盲目跳过

当目标 schema 已存在时，系统 SHALL 校验其中是否包含可探测的表。校验不通过时 SHALL 视为异常处理（提示 schema 已存在但为空），SHALL NOT 直接跳过建表步骤后继续后续流程，最终报出与「建表失败」无法区分的错误。

#### Scenario: 已存在且含表的 schema 正常跳过

- **WHEN** 目标 schema 已存在且其中已有表
- **THEN** 系统 SHALL 跳过重复建表，后续解析 SHALL 正常进行

#### Scenario: 已存在但为空的 schema 被识别

- **WHEN** 目标 schema 已存在但其中没有任何表
- **THEN** 系统 SHALL 识别该状态，SHALL NOT 盲目跳过后让用户在后续步骤收到语义不明的错误

### Requirement: 元数据探测须有集成测试守护

系统 SHALL 提供覆盖「建 schema → 建表 → 探测」的集成测试，断言探测能返回刚创建的表与列。该测试 SHALL 使用真实的嵌入式数据库实例，SHALL NOT 仅以 mock 替代元数据查询。

#### Scenario: 探测链路端到端可验证

- **WHEN** 在集成测试中创建 schema 与表后执行表探测
- **THEN** 探测 SHALL 返回该表，测试 SHALL 通过

#### Scenario: 列探测返回真实列信息

- **WHEN** 对已创建的表执行列探测
- **THEN** 探测 SHALL 返回该表的列定义，SHALL NOT 返回空结果

#### Scenario: 数据库升级导致的元数据语义变化被捕获

- **WHEN** 数据库升级改变了元数据查询依赖的取值形式
- **THEN** 集成测试 SHALL 失败，SHALL NOT 等到使用者在运行环境中手工触发才发现
