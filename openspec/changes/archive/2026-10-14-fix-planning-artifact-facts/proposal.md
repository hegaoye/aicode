# Proposal

## Why

Java 25 / Spring Boot 4.1 升级系列的 6 个活跃变更，其 proposal / design / tasks 中引用了一批**未经实测的计数**。这些数字在起草时是目测的，直到执行 `2026-10-14-init-openspec-workspace` 的任务 3.1（修正 `AGENTS.md` §7.1 测试统计）时才被发现全部是错的。

实测结果与提案中的记载：

| 项 | 提案中写的 | 实测 | 偏差 |
|----|-----------|------|------|
| 测试类总数 | 17 | **18** | 漏算 `MapClassTableTest` |
| 其中 `aicode` 模块 | 9 | **10** | 同上 |
| `@MockBean` 总数 | 「约 45 处」 | **54** | 逐类目测，未 grep 计数 |
| `ProjectControllerDownloadTest` | 15 | **14** | +1 |
| `SqlReverseParseTest` | 17 | **16** | +1 |
| `ProjectInitEndpointTest` | 13 | **12** | +1 |
| `MapRelationshipControllerTest` | 7 | **6** | +1 |
| `LoginCtrlShtmlPathTest` | 3 | 3 | ✅ |
| `ProjectJobServiceImplConcurrencyTest` | 3 | 3 | ✅ |

共 10 处错误，分布在 6 个变更的 7 个文件中（`add-api-contract-tests` ×1、`upgrade-spring-boot-3-5` ×3、`upgrade-gradle-9-jdk-25` ×2、`sync-docs-after-platform-upgrade` ×3，另有 1 处 `@MockBean` 总数）。

**为什么必须现在修**：这些数字是后续变更的**验收标准**（"全部测试 SHALL 通过（含 17 个既有测试类）"）。一旦 `add-api-contract-tests` 落地并归档，它所依赖的"既有测试基线"就会以错误的数字固化，后续每个变更收尾时都会对不上账。

**为什么可以现在修**：6 个变更**均未开始执行**（全部 0/N tasks），修正的边际成本为零。若等到执行开始后再改，规划产物与实际执行记录混在一起，评审时无法区分「初始意图」与「事后修正」。

## What Changes

- 修正 7 处「17 个测试类」为「18 个」，相关分项同步改为「`aicode` 10 个 + `common` 8 个」
- 修正 `upgrade-spring-boot-3-5` 中 `@MockBean` 的逐类计数与总数（5 个测试类约 45 处 → 6 个测试类 54 处）
- 在修正处附上实测命令，使数字可被复核

本变更**不修改任何代码、依赖或运行时行为**，也不改变任何 requirement 的语义——修正对象全部是描述性计数，SHALL NOT 触碰任何 `### Requirement:` / `#### Scenario:` 块。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

无。

> 本变更的 `.openspec.yaml` 设置了 `skip_specs: true`。规划产物中的事实性计数错误不构成任何行为契约变化。

## Impact

- **修改文件**（7 个，全部位于 `openspec/changes/`）：
  - `2026-10-14-add-api-contract-tests/tasks.md`（1 处）
  - `2026-10-14-upgrade-spring-boot-3-5/proposal.md`（1 处，`@MockBean` 总数）
  - `2026-10-14-upgrade-spring-boot-3-5/tasks.md`（6 处：验收标准 + 3.1–3.6 逐类计数）
  - `2026-10-14-upgrade-gradle-9-jdk-25/proposal.md`（1 处）
  - `2026-10-14-upgrade-gradle-9-jdk-25/tasks.md`（1 处）
  - `2026-10-14-sync-docs-after-platform-upgrade/proposal.md`（2 处）
  - `2026-10-14-sync-docs-after-platform-upgrade/tasks.md`（1 处）
- **不改**：任何 `src/` 源码、`build.gradle`、`Dockerfile`、`AGENTS.md`
- **不改**：任何 `### Requirement:` / `#### Scenario:` 块
- **已归档的 `init-openspec-workspace` 不在修正范围**——其自身的 2 处在归档前已随任务 3.1 / 3.2 一并改对
- **验收命令**：`openspec validate --changes --strict` + 全量 grep 确认零残留
