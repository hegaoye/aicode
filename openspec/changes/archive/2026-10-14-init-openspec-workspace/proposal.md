# Proposal

## Why

仓库的 `AGENTS.md` §8 声明「变更管理走规范驱动模式」，并引用了 `openspec/specs/auth-account/spec.md`、`openspec/specs/api-deprecation-policy/spec.md` 与 `openspec/changes/upgrade-jgit-6/`。但在当前 checkout 中，仓库根目录下**根本不存在 `openspec/` 目录**——`openspec list` 解析出的根是 `/Users/bruce`（home 目录下一个空工作区）。这意味着：

- 规范驱动流程在实际协作中并未真正生效，后续会话按 AGENTS.md 指引会找不到任何能力基线；
- 所有提案 / 任务 / 归档产物都不在 git 版本控制内，无法评审、无法回滚、无法追溯；
- 多个项目共用同一个 home 级工作区，能力边界互相污染。

在启动 Java 25 / Spring Boot 4.1 升级系列变更之前，必须先把规范驱动的基础设施在本仓库内落地，否则这 7 个变更无处安放。

## What Changes

- **BREAKING**（对协作流程）：在仓库内建立 `openspec/` 工作区，使 `openspec list` / `validate` / `archive` 的根目录解析到本仓库，而非 home 目录。
- 新增 `openspec/config.yaml`，声明 `schema: spec-driven` 与中文产出约定（artifacts 用中文书写，OpenSpec 结构性标题与 SHALL/MUST 关键词保留英文）。
- 补建两个被 `AGENTS.md` §8 / §11.1 / §11.7 引用、但实体缺失的能力基线：
  - `openspec/specs/auth-account/spec.md` —— 记录单用户模式、MD5 口令散列、JWT 60 分钟有效期、双来源令牌解析、拦截器豁免区间等既有行为；
  - `openspec/specs/api-deprecation-policy/spec.md` —— 记录「标记 → 30 天观察 → 内部核查 → 删除」四阶段废弃流程。
- 刷新 `.opencode/` 下 5 个 opsx 技能与 5 个 opsx 命令到 CLI 1.13.2 的模板，并引入 2 个新工作流（`opsx-write-plan`、`opsx-executing-plans`）。
- 修正 `AGENTS.md` §7.1 / §11.3 关于「三模块均无 src/test」「当前只有 SmokeTest 占位」的过时描述（实际已有 17 个测试类）。

本变更不涉及任何应用代码、依赖或运行时行为。

## Capabilities

### New Capabilities

无。本变更为纯工具链 / 文档基础设施，不改变任何系统行为。

### Modified Capabilities

无。

> 本变更的 `.openspec.yaml` 已设置 `skip_specs: true`：工作区初始化不产生任何可观测的行为契约变化，按 OpenSpec 规则不应为其编造 spec。

## Impact

- **新增文件**：`openspec/config.yaml`、`openspec/specs/auth-account/spec.md`、`openspec/specs/api-deprecation-policy/spec.md`
- **修改文件**：`.opencode/commands/opsx-*.md`（5 个）、`.opencode/skills/openspec-*/SKILL.md`（5 个）、`.opencode/commands/opsx-write-plan.md`（新增）、`.opencode/commands/opsx-executing-plans.md`（新增）、`.opencode/skills/openspec-write-plan/`（新增）、`.opencode/skills/openspec-executing-plans/`（新增）、`AGENTS.md` §7.1 / §11.3
- **不影响**：Gradle 构建、`aicode` 应用运行、生成主链路、前端静态资源
- **验收命令**：`openspec list --specs` 输出根目录为仓库路径，且列出 `auth-account` 与 `api-deprecation-policy`
