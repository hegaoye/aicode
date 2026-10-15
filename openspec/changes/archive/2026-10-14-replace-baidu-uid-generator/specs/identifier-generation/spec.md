# Spec Delta

## Purpose

定义 aicode 平台标识符（主键与业务编码）的生成策略与可观测形态。标识符贯穿持久层、REST 接口与代码生成产物，是前后端与模板三方共享的契约，因此其生成方式与输出形态必须被显式约束，而非依赖某个库的默认行为。

## ADDED Requirements

### Requirement: 主键由数据库自增赋值

系统 SHALL 由数据库的自增机制为主键列赋值，应用层 SHALL NOT 在插入前为主键显式赋值。数据库表 SHALL 为主键列声明自增属性。

#### Scenario: 新增记录的主键由数据库分配

- **WHEN** 系统新增一条业务记录且未显式指定主键值
- **THEN** 该记录的主键 SHALL 由数据库自增序列分配，SHALL NOT 由应用层生成

#### Scenario: 主键自增序列从小值开始

- **WHEN** 系统新增一条业务记录
- **THEN** 该记录的主键 SHALL 为小整数（远小于 19 位），SHALL NOT 呈雪花形态

#### Scenario: 主键序列在单表内单调递增

- **WHEN** 系统连续向同一张表插入多条记录
- **THEN** 各记录的主键 SHALL 构成严格递增的整数序列

#### Scenario: 应用层不覆盖主键

- **WHEN** 系统构建待插入的实体对象
- **THEN** 该实体的主键字段 SHALL 保持为空，SHALL NOT 被赋值框架或持久层框架预先填充

### Requirement: 业务编码由内存雪花算法生成

系统 SHALL 使用进程内的雪花算法生成业务编码，SHALL NOT 依赖外部 ID 服务、外部数据库表或网络调用。业务编码 SHALL 与主键独立生成，二者 SHALL NOT 存在派生关系。

#### Scenario: 业务编码为纯数字字符串

- **WHEN** 系统生成一条业务编码
- **THEN** 该值 SHALL 匹配正则 `^\d{1,19}$`

#### Scenario: 业务编码与主键相互独立

- **WHEN** 系统新增一条同时拥有主键与业务编码的记录
- **THEN** 业务编码 SHALL NOT 等于该记录主键的字符串形式

#### Scenario: 编码生成不依赖数据库往返

- **WHEN** 系统为一条待插入记录生成业务编码
- **THEN** 该生成过程 SHALL NOT 触发额外的数据库查询，SHALL NOT 依赖任何工作节点分配表

#### Scenario: 编码在并发下保持唯一

- **WHEN** 多个请求并发为不同记录生成业务编码
- **THEN** 生成的编码 SHALL 互不相同

### Requirement: 存量数据零迁移

系统 SHALL NOT 对存量记录的主键或业务编码执行数据迁移。变更前后的两种编码形态 SHALL 共存于同一列，且下游消费者 SHALL 能无差别处理。

#### Scenario: 存量编码保持不变

- **WHEN** 系统完成策略切换
- **THEN** 变更前已存在的记录，其主键与业务编码 SHALL 保持原值不变

#### Scenario: 新旧编码共存可被正常消费

- **WHEN** 某列表接口同时返回存量记录与新生成的记录
- **THEN** 两类记录的业务编码 SHALL 以相同形态呈现给客户端

### Requirement: 移除外部标识符组件

系统 SHALL NOT 依赖任何外部的标识符生成组件、专用数据源或工作节点分配表。系统 SHALL 仅保留单一业务数据源。

#### Scenario: 应用仅配置一个数据源

- **WHEN** 系统完成启动
- **THEN** 应用上下文中 SHALL 仅存在一个业务数据源，SHALL NOT 存在专用于标识符分配的第二个数据源

#### Scenario: 标识符分配表不再存在

- **WHEN** 初始化数据库结构
- **THEN** 系统 SHALL NOT 创建工作节点分配表

#### Scenario: 外部组件已从构建产物移除

- **WHEN** 检查构建依赖
- **THEN** 应用 SHALL NOT 包含任何本地 vendored 的标识符生成 jar
