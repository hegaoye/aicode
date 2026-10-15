# Design

## Context

`openspec` CLI 的根目录解析规则是「nearest」——从当前目录向上查找 `openspec/`。仓库根没有该目录，于是解析落到 `/Users/bruce/openspec`。那个目录是历史遗留的空壳：

```
/Users/bruce/openspec/
├── config.yaml          # schema: spec-driven，无 context
├── specs/.gitkeep       # 空
└── changes/archive/.gitkeep  # 空
```

它不在任何 git 仓库内，也不带项目上下文。本次升级系列共 7 个变更、约 30 个 artifact 文件，全部需要落在仓库内。

本仓库已有的 AI 协作配置（`.opencode/`、`.claude/`、`.agents/`）证明这是多工具并行使用的项目，`openspec init` 会同时刷新这些目录下的 opsx 工作流，需要与变更一起提交以免后续会话拿到过期的 CLI 指引。

## Goals / Non-Goals

**Goals:**

- `openspec` 的根目录解析到本仓库，且该目录纳入 git 版本控制
- 产出物语言约定明确（中文书写 + 英文结构性标题/SHALL 关键词），避免后续会话风格分裂
- 补齐 `AGENTS.md` 已声明但实体缺失的能力基线，让规范引用可解析
- 把「OpenSpec CLI 版本升级」与「能力基线补建」一次性做完，不留下半初始化状态

**Non-Goals:**

- 不在本变更中创建 `api-contract` / `runtime-platform` / `identifier-generation` 三个新能力基线——它们分别属于 `2026-10-14-add-api-contract-tests`、`2026-10-14-upgrade-gradle-9-jdk-25`、`2026-10-14-replace-baidu-uid-generator` 三个变更的 delta，归档时才物化
- 不回填 `AGENTS.md` §8 引用的 `openspec/changes/upgrade-jgit-6/`。该变更在当前 checkout 无任何实体，且其内容（JGit 4.9.2 → 6.6.0 修复 CDS 兼容）已由 `common/build.gradle` 注释记录，重建的准确性无法保证
- 不修改任何 Gradle 配置或应用代码
- 不重写 `AGENTS.md` 中除 §7.1 / §11.3 以外的其他章节（其余过时内容由 `2026-10-14-sync-docs-after-platform-upgrade` 统一处理）

## Decisions

### 决策 1：在仓库内新建工作区，而非迁移 home 级工作区

**选择**：在 `/Users/bruce/IdeaProjects/aicode/openspec/` 新建，`config.yaml` 写入项目上下文。

**备选方案**：
- *(a)* 继续用 `~/openspec` 并注册为 standalone store —— 拒绝：产物仍不在 git 内，PR 评审看不到变更，违反「规范驱动」的初衷
- *(b)* 把 `~/openspec` 的内容 move 进仓库 —— 拒绝：该目录是空的（仅 `.gitkeep`），无内容可迁移，反而多一步无意义的删除操作

**代价**：`openspec list` 在仓库外的目录执行时仍会落到 home 级旧工作区。缓解：本次已在 `config.yaml` 写入 context，CI 与后续会话从仓库根执行即可。

### 决策 2：补建能力基线时「记录现状」而非「修正现状」

`auth-account` 的基线如实记录 MD5 口令散列、JWT 60 分钟有效期、令牌走 URL 参数等**已知的安全债**，并在 `AGENTS.md` §11.1 已有的演进方向之外不做任何美化。

**理由**：spec 是行为契约，不是目标状态。把「应该用 BCrypt」写进 spec 会让后续的安全加固变更失去变更起点——加固变更需要知道「现在到底是不是 MD5」才能写出 `MODIFIED` delta。

**代价**：基线读起来像是「认可这些做法」。缓解：每条安全债相关的 requirement 都在描述里显式标注为待演进。

### 决策 3：`api-deprecation-policy` 增加一条「前端调用计入核查范围」

`AGENTS.md` §8 只描述了「内部 grep 二次核查」。但本项目的真实调用方包含 `src/main/resources/static/` 下的 Angular 打包产物——后端 grep 找不到 `.shtml` 调用点。

**理由**：升级/废弃流程如果只看后端代码，会漏掉整个前端。建议中的「删除」动作很可能造成生产故障。

**代价**：这条 requirement 比 `AGENTS.md` 更严格，执行成本更高（需要额外扫描前端产物）。可接受，因为漏检的代价是生产事故。

### 决策 4：修正 AGENTS.md 中两处过时描述

`§7.1` 写「当前仓库三模块均无 `src/test` 实现」、`§11.3` 写「当前只有 `SmokeTest` 占位」。实测：`aicode` 模块 9 个测试类 + `common` 模块 8 个测试类 = **17 个**，含 `LoginCtrlShtmlPathTest`、`SqlReverseParseTest`、`GitToolsCloneCompletenessTest` 等有实质断言的类。

**理由**：这两处过时描述会误导后续会话对测试覆盖度的判断，进而低估升级风险。

## Risks / Trade-offs

| 风险 | 影响 | 缓解 |
|------|------|------|
| `.opencode/` 下 10 个文件被 CLI 刷新，diff 较大 | 评审噪音 | 这些文件是 CLI 生成物，diff 不需逐行评审；在 commit message 中说明来源 |
| `.claude/` 与 `.agents/` 未被 `--tools opencode` 覆盖 | 两个工具的 opsx 指引仍是旧版 | 本次升级系列开始前，另开一次 `openspec init --tools claude --tools amazon-q` 刷新；不与能力基线补建混在一个变更 |
| home 级 `~/openspec` 残留造成困惑 | 在错误目录执行 CLI 时产出落到仓库外 | 本变更的 tasks 里加一条「确认 `openspec list` 输出根目录」作为验收闸门 |
| 补建的 spec 与 AGENTS.md 叙述存在细微出入 | 后续变更基于错误基线 | spec 中每条 requirement 都可回溯到具体代码位置；归档前由人工复核一次 |
