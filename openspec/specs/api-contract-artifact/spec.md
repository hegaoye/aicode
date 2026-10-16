# api-contract-artifact Capability

## Purpose

定义 aicode 跨语言契约的**可执行产物**要求：后端 SHALL 在测试期自动导出 OpenAPI 契约并入库，契约 SHALL NOT 含与运行机器相关的取值，入库契约 SHALL 有漂移守卫以防过期，前端 SHALL 从该契约生成类型、请求函数与 mock 而非手写。

本能力是 React 前端重写的前置条件。没有这条链，字段改名、参数改名、端点增删这类最常见的前后端漂移只能等到联调或运行时暴露；有了这条链，它们在编译期与测试期即失败。

## Requirements

### Requirement: 契约 SHALL 由后端在测试期自动导出并入库

系统 SHALL 在既有的集成测试上下文中调用后端自身的 OpenAPI 端点，把导出结果规范化后写入仓库中的契约文件。契约文件 SHALL 纳入版本控制，使「接口形状」成为可评审、可 diff 的仓库资产。

#### Scenario: 执行测试即刷新契约

- **WHEN** 执行后端测试
- **THEN** 仓库中的契约文件 SHALL 被刷新为当次运行的实际接口形状

#### Scenario: 契约变化在评审中可见

- **WHEN** 某次提交改变了接口
- **THEN** 契约文件的差异 SHALL 以可读的逐行形式呈现，SHALL NOT 需要人工比对压缩后的单行内容

### Requirement: 契约 SHALL NOT 含与运行机器相关的取值

契约 SHALL NOT 包含随运行环境变化的取值（典型如按本次请求 host/port 生成的 server URL）。此类取值 SHALL 在导出时被剥离或替换为稳定值。

#### Scenario: 换端口重跑不产生差异

- **WHEN** 在不同端口启动应用并重复导出契约
- **THEN** 契约文件 SHALL 与此前一致，SHALL NOT 产生仅因端口不同的差异

### Requirement: 入库契约 SHALL 有漂移守卫

系统 SHALL 提供一条可执行检查，用于比对「重新导出的契约」与「入库的契约」。两者不一致时该检查 SHALL 失败。

仅在测试中刷新契约 SHALL NOT 足够 —— 那会使契约自动跟着代码变化，评审者无从察觉接口是否被改动。因此守卫必须是独立的、可在无 node 环境下运行的检查。

#### Scenario: 改了接口但未提交契约变化时失败

- **WHEN** 后端接口已变更而契约文件未一并提交
- **THEN** 漂移检查 SHALL 失败并指出契约文件存在差异

#### Scenario: 守卫不依赖前端工具链

- **WHEN** 在没有 node / npm 的环境中执行漂移检查
- **THEN** 该检查 SHALL 仍可运行，SHALL NOT 依赖前端构建

### Requirement: 前端 SHALL 从契约生成类型与 mock

前端的接口类型、请求函数与测试用 mock SHALL 由契约自动生成，SHALL NOT 手工维护。生成物 SHALL 位于明确的生成目录，SHALL 可通过单一命令重建。

#### Scenario: 接口变更导致前端编译失败

- **WHEN** 后端删除或改名字段，而前端代码正在使用该字段
- **THEN** 前端类型检查 SHALL 失败，SHALL NOT 让错误延后到运行时

#### Scenario: 新增端点可直接被前端使用

- **WHEN** 后端新增端点并更新契约
- **THEN** 前端 SHALL 无需手写类型或请求函数即可调用该端点

#### Scenario: mock 与真实契约同源

- **WHEN** 前端测试所需的接口桩数据
- **THEN** 其 SHALL 源自同一份契约，SHALL NOT 由人工另行编写形状

### Requirement: 前端构建 SHALL 对 node 缺失给出明确行为

系统 SHALL 区分「node 缺失但已有构建产物」与「node 与产物均缺失」两种情形：前者 SHALL 复用既有产物继续构建，后者 SHALL 失败并给出可执行的修复提示。

#### Scenario: 无 node 但有产物时复用

- **WHEN** 构建时找不到 node，但已存在先前的前端构建产物
- **THEN** 构建 SHALL 复用该产物继续，SHALL NOT 失败

#### Scenario: 无 node 且无产物时失败并给出提示

- **WHEN** 构建时既找不到 node，也不存在任何前端构建产物
- **THEN** 构建 SHALL 失败，SHALL NOT 产出一个不含前端的制品，错误信息 SHALL 指明如何先构建前端
