## MODIFIED Requirements

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

## ADDED Requirements

### Requirement: 嵌入式数据库与驱动版本基线

系统 SHALL 使用与目标框架兼容的嵌入式数据库版本。数据库连接参数 SHALL NOT 使用目标数据库版本已明确拒绝的参数组合。Schema 与初始化数据 SHALL 能在目标数据库版本下原样执行。

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
