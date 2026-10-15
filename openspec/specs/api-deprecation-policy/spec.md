# api-deprecation-policy Capability

## Purpose

定义 aicode 平台对 REST 接口下线的治理流程：任何 `@Deprecated` 端点从标记到物理删除 SHALL 经历「标记 → 观察期 → 内部核查 → 删除」四个阶段，避免无预警地破坏存量调用方。本基线是对既有约定的记录（补建于 2026-10-14）。

## Requirements

### Requirement: 四阶段废弃流程

系统 SHALL 对废弃接口执行四阶段流程：① **标记**（加 `@Deprecated` 并在接口文档中说明替代方案）；② **观察期**（不少于 30 天）；③ **内部核查**（全仓库 grep 确认无剩余调用，含前端静态资源与外部模板仓库约定）；④ **删除**（物理移除端点）。

#### Scenario: 废弃接口立即进入观察期

- **WHEN** 某端点被标记为 `@Deprecated`
- **THEN** 该端点 SHALL 保留可用状态至少 30 天，SHALL NOT 在同一变更中被删除

#### Scenario: 观察期满后进行内部核查

- **WHEN** 观察期满、准备执行删除
- **THEN** 发起方 SHALL 在仓库范围内 grep 该端点路径与请求方法，SHALL NOT 存在任何调用点

#### Scenario: 核查通过后方可删除

- **WHEN** 内部核查发现仍有调用点
- **THEN** 删除 SHALL 被推迟，SHALL NOT 强行移除

### Requirement: 删除时同步文档

系统 SHALL 在物理删除端点的同一变更中，同步更新对外接口文档中记载的废弃端点清单。

#### Scenario: 删除端点时文档同步

- **WHEN** 一个废弃端点被物理删除
- **THEN** `aicode/README.md` 的废弃端点章节 SHALL 在同一提交中被更新，SHALL NOT 残留已删除端点的条目

### Requirement: 变更必须走规范驱动流程

系统 SHALL 通过 OpenSpec 变更提案管理接口的废弃与删除，SHALL NOT 直接在生产分支上手工删除端点。

#### Scenario: 废弃走 OpenSpec 变更

- **WHEN** 任何接口被标记废弃或删除
- **THEN** 该操作 SHALL 存在对应的 `openspec/changes/<change-id>/` 提案与任务清单

### Requirement: 存量前端兼容性优先

系统 SHALL 在废弃流程中，把仓库内静态前端资源对被废弃端点的调用视为**有效调用方**，SHALL NOT 仅依据后端代码的 grep 结果判定无人调用。

#### Scenario: 前端调用计入核查范围

- **WHEN** 某端点仅被 `src/main/resources/static/` 下的前端产物调用
- **THEN** 内部核查 SHALL 判定该端点仍被使用，SHALL NOT 执行删除
