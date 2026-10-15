# runtime-platform Specification

## Purpose
定义 aicode 平台的构建与运行时基线：Java 语言级别、构建工具版本、注解处理器版本约束、容器基础镜像，以及性能基线的采集方式与可比性要求。本能力的核心目的是让「升级是否可靠」这个问题**可被验证**，而不是依赖主观判断。

## Requirements

### Requirement: 统一的 Java 语言级别基线

系统 SHALL 在全部三个构建模块（common / facade / aicode）上使用同一 Java 语言级别进行编译，且该级别 SHALL 由构建工具链声明。应用 SHALL 运行在与其编译语言级别一致的 Java 运行时上，并 SHALL 满足目标框架的最低版本要求。

#### Scenario: 三个模块语言级别一致

- **WHEN** 检查任意模块的编译配置
- **THEN** 其 Java 语言级别 SHALL 与其余模块相同

#### Scenario: 语言级别由工具链声明

- **WHEN** 检查编译任务配置
- **THEN** Java 语言级别 SHALL 通过工具链机制声明

#### Scenario: 使用本地已安装的 JDK

- **WHEN** 开发者在未设置 `JAVA_HOME` 的情况下执行构建
- **THEN** 构建工具 SHALL 自动选用满足要求的本地 JDK，SHALL NOT 要求额外安装步骤

#### Scenario: 运行时满足框架最低要求

- **WHEN** 应用启动
- **THEN** 所用 Java 运行时版本 SHALL 同时满足应用编译语言级别与目标框架的最低要求

### Requirement: 注解处理器与语言级别兼容

系统 SHALL 使用支持当前 Java 语言级别的注解处理器版本编译。若构建工具的依赖管理已提供满足约束的版本，SHALL NOT 显式锁定更旧的版本。

#### Scenario: 注解处理器版本满足语言级别要求

- **WHEN** 编译使用较新 Java 语言级别的模块
- **THEN** 注解处理器 SHALL 处于支持该语言级别的版本区间，编译 SHALL NOT 因 class 文件版本不支持而失败

#### Scenario: 优先使用构建工具管理的版本

- **WHEN** 构建工具的依赖管理已提供满足约束的注解处理器版本
- **THEN** 构建 SHALL NOT 显式锁定更旧的版本，以免覆盖受支持的版本

### Requirement: 构建工具版本基线

系统 SHALL 使用支持目标 Java 语言级别与目标框架的构建工具版本。构建过程 SHALL NOT 输出与目标构建工具主版本不兼容的弃用告警。

#### Scenario: 构建工具支持目标语言级别

- **WHEN** 构建工具解析目标 Java 语言级别的编译任务
- **THEN** 解析 SHALL 成功

#### Scenario: 构建工具支持目标框架

- **WHEN** 应用目标框架的构建插件
- **THEN** 插件版本 SHALL 处于构建工具的官方支持范围内

#### Scenario: 无构建工具兼容性告警

- **WHEN** 执行一次完整构建
- **THEN** 输出 SHALL NOT 包含「incompatible with Gradle <主版本>」一类的弃用告警

### Requirement: 容器运行时基线

系统 SHALL 以与应用编译语言级别一致的 Java 运行时版本构建容器镜像。镜像内运行时 SHALL 满足目标框架的 Servlet 容器最低版本要求。

#### Scenario: 镜像内 JDK 版本匹配

- **WHEN** 检查容器镜像内安装的 Java 版本
- **THEN** 其版本 SHALL 与应用编译语言级别一致

#### Scenario: 容器内运行时满足框架要求

- **WHEN** 应用在容器内启动
- **THEN** 内嵌 Servlet 容器 SHALL 处于框架支持的版本区间，SHALL NOT 低于最低要求

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

### Requirement: 嵌入式数据库与驱动版本基线

系统 SHALL 使用与目标框架兼容的嵌入式数据库版本。数据库连接参数 SHALL NOT 使用目标数据库版本已明确拒绝的参数组合。Schema 与初始化数据 SHALL 能在目标数据库版本下原样执行。**升级数据库版本时，系统 SHALL 复核所有对数据库元数据（`INFORMATION_SCHEMA`）的查询，确保其使用的列名与取值形式与新版本实际返回的一致。**

#### Scenario: 数据库版本与框架兼容

- **WHEN** 应用启动并初始化数据库
- **THEN** 数据库 SHALL 以框架依赖管理所声明的版本运行

#### Scenario: 连接参数不被数据库拒绝

- **WHEN** 应用建立数据库连接
- **THEN** 连接参数组合 SHALL 被数据库接受，SHALL NOT 触发「特性不支持」错误

#### Scenario: Schema 原样执行

- **WHEN** 应用启动时执行数据库初始化脚本
- **THEN** 初始化脚本 SHALL 在当前数据库版本与兼容模式下执行成功，SHALL NOT 需要修改脚本文本

#### Scenario: 自增主键在复合主键下可用

- **WHEN** 某表使用「自增列 + 多列复合主键」定义，且系统以兼容模式运行
- **THEN** 自增序列 SHALL 正常工作

#### Scenario: 升级后存量数据库文件的处理方式明确

- **WHEN** 数据库版本升级导致旧版本创建的数据文件不可用
- **THEN** 变更文档 SHALL 明确要求删除旧数据文件并重建，SHALL NOT 让使用者在启动失败后自行猜测

#### Scenario: 升级后元数据查询仍然有效

- **WHEN** 数据库版本升级，且该升级改变了 `INFORMATION_SCHEMA` 中某列的名称或取值形式
- **THEN** 依赖该列的查询 SHALL 仍能返回正确结果，SHALL NOT 静默返回空集或抛出 SQL 错误

#### Scenario: 元数据列名到实体属性的映射显式声明

- **WHEN** 元数据查询返回的列名与目标实体的属性名不是驼峰对应关系
- **THEN** 查询 SHALL 显式声明列别名，SHALL NOT 依赖自动映射，SHALL NOT 出现「查询成功但实体属性为 null」的静默失败



### Requirement: 管理端点配置使用目标框架的属性命名

系统 SHALL 使用目标框架认可的属性名来启用管理端点。属性名 SHALL NOT 使用已废弃的旧命名形式。

#### Scenario: 环境端点启用方式正确

- **WHEN** 需要暴露环境端点
- **THEN** 配置 SHALL 使用目标框架的端点访问控制属性，SHALL NOT 使用已移除的启用开关属性

#### Scenario: 关闭端点启用方式正确

- **WHEN** 需要暴露关闭端点
- **THEN** 配置 SHALL 使用目标框架的端点访问控制属性

#### Scenario: 健康详情展示方式正确

- **WHEN** 需要在健康端点中展示详情
- **THEN** 配置 SHALL 使用目标框架认可的健康端点属性路径

#### Scenario: 无效属性不影响启动

- **WHEN** 配置文件中存在目标框架已移除的属性
- **THEN** 应用 SHALL 正常启动，但该属性 SHALL 不生效；变更 SHALL 通过配置迁移诊断工具确认无遗留无效属性

### Requirement: API 文档由受支持的组件提供

系统 SHALL 使用与目标框架兼容的 API 文档组件提供交互式文档。系统 SHALL NOT 依赖已停止维护且不支持目标框架的第三方 UI 组件。

#### Scenario: 交互式文档可访问

- **WHEN** 访问 API 文档 UI 地址
- **THEN** 页面 SHALL 正常加载并展示接口列表

#### Scenario: 不依赖已停止维护的组件

- **WHEN** 检查构建依赖
- **THEN** 依赖列表 SHALL NOT 包含面向旧框架版本、已停止维护的 API 文档 UI 组件

#### Scenario: 移除第三方 UI 后清理其专用路由

- **WHEN** 移除第三方 API 文档 UI
- **THEN** 平台 SHALL 同时移除为该 UI 保留的静态资源映射与拦截豁免条目，SHALL NOT 留下无效路由
