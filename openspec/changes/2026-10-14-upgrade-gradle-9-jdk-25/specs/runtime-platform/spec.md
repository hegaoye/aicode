# Spec Delta

## Purpose

定义 aicode 平台的构建与运行时基线：Java 语言级别、构建工具版本、注解处理器版本约束、容器基础镜像，以及性能基线的采集方式与可比性要求。本能力的核心目的是让「升级是否可靠」这个问题**可被验证**，而不是依赖主观判断。

## ADDED Requirements

### Requirement: 统一的 Java 语言级别基线

系统 SHALL 在全部三个构建模块（common / facade / aicode）上使用同一 Java 语言级别进行编译，且该级别 SHALL 由构建工具链声明，而非由各模块独立设置。

#### Scenario: 三个模块语言级别一致

- **WHEN** 检查任意模块的编译配置
- **THEN** 其 Java 语言级别 SHALL 与其余模块相同

#### Scenario: 语言级别由工具链声明

- **WHEN** 检查编译任务配置
- **THEN** Java 语言级别 SHALL 通过工具链机制声明，SHALL NOT 依赖各模块分别声明 `sourceCompatibility`

#### Scenario: 使用本地已安装的 JDK

- **WHEN** 开发者在未设置 `JAVA_HOME` 的情况下执行构建
- **THEN** 构建工具 SHALL 自动选用满足要求的本地 JDK，SHALL NOT 要求额外安装步骤

### Requirement: 注解处理器与语言级别兼容

系统 SHALL 使用支持当前 Java 语言级别的注解处理器版本编译。若构建工具的依赖管理已提供满足约束的版本，SHALL NOT 显式锁定更旧的版本。

#### Scenario: 注解处理器版本满足语言级别要求

- **WHEN** 编译使用较新 Java 语言级别的模块
- **THEN** 注解处理器 SHALL 处于支持该语言级别的版本区间，编译 SHALL NOT 因 class 文件版本不支持而失败

#### Scenario: 优先使用构建工具管理的版本

- **WHEN** 构建工具的依赖管理已提供满足约束的注解处理器版本
- **THEN** 构建 SHALL NOT 显式锁定更旧的版本，以免覆盖受支持的版本

### Requirement: 构建工具版本基线

系统 SHALL 使用支持目标 Java 语言级别的构建工具版本。构建过程 SHALL NOT 输出「与目标构建工具主版本不兼容」的弃用告警。

#### Scenario: 构建工具支持目标语言级别

- **WHEN** 构建工具解析目标 Java 语言级别的编译任务
- **THEN** 解析 SHALL 成功，SHALL NOT 报「不支持该语言级别」

#### Scenario: 无构建工具兼容性告警

- **WHEN** 执行一次完整构建
- **THEN** 输出 SHALL NOT 包含「incompatible with Gradle <主版本>」一类的弃用告警

### Requirement: 容器运行时基线

系统 SHALL 以与应用编译语言级别一致的 Java 运行时版本构建容器镜像。

#### Scenario: 镜像内 JDK 版本匹配

- **WHEN** 检查容器镜像内安装的 Java 版本
- **THEN** 其版本 SHALL 与应用编译语言级别一致

#### Scenario: 云构建包 JDK 版本匹配

- **WHEN** 通过云构建包构建镜像
- **THEN** 构建配置的 JDK 版本 SHALL 与应用编译语言级别一致

#### Scenario: 镜像内应用正常启动

- **WHEN** 启动构建出的容器镜像
- **THEN** 应用 SHALL 正常启动且存活探针在约定时限内返回健康状态

### Requirement: 性能基线可跨版本对比

系统 SHALL 提供结构化的性能基线采集脚本，其输出 SHALL 包含运行时标识信息（Java 版本、构建标识）与被测量指标，并 SHALL 支持在两个版本之间做机器可读的比较。

#### Scenario: 基线输出包含运行时标识

- **WHEN** 执行性能基线采集脚本
- **THEN** 输出 SHALL 以结构化格式包含被测运行时的 Java 版本与构建标识

#### Scenario: 两个版本的基线可比

- **WHEN** 分别在升级前后的版本上采集基线
- **THEN** 两份输出 SHALL 具有相同的结构，SHALL 可直接逐字段比较

#### Scenario: 基线回归可被机器判定

- **WHEN** 升级后基线相较升级前超出约定阈值
- **THEN** 比较过程 SHALL 能以机器可读的方式标识出该项回归

#### Scenario: 文档中的性能数字与实测一致

- **WHEN** 性能相关文档中记录了具体数值
- **THEN** 该数值 SHALL 来自实际采集的基线输出，SHALL NOT 是沿用旧版本的估计值

### Requirement: 升级后行为一致性可验证

Java 语言级别升级 SHALL NOT 改变系统的可观测行为。系统 SHALL 通过既有测试与接口契约测试验证这一点。

#### Scenario: 既有测试在升级后全部通过

- **WHEN** 完成语言级别升级后执行全部测试
- **THEN** 全部测试 SHALL 通过，无新增失败项

#### Scenario: 接口契约在升级后保持

- **WHEN** 完成语言级别升级后执行接口契约测试
- **THEN** 全部契约场景 SHALL 通过

#### Scenario: 代码生成产物一致

- **WHEN** 在升级前后分别执行一次完整的代码生成
- **THEN** 两次生成的产物目录结构与文件内容 SHALL 一致

### Requirement: 异步任务在升级后行为一致

系统 SHALL 在升级后保持虚拟线程异步执行的语义，包括跨线程的上下文传递。

#### Scenario: 异步任务仍运行于虚拟线程

- **WHEN** 触发一次异步构建任务
- **THEN** 该任务 SHALL 运行于虚拟线程上

#### Scenario: 跨线程的链路标识得以传递

- **WHEN** 异步任务执行日志输出
- **THEN** 日志中的链路标识 SHALL 与触发请求的链路标识一致
