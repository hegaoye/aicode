# Tasks

## 1. 建立仓库内工作区

- [x] 1.1 执行 `openspec init --tools opencode --language zh --force --no-animation`，在仓库根生成 `openspec/`
- [x] 1.2 确认 `openspec/config.yaml` 已生成，且写入中文产出约定（artifacts 用中文书写，OpenSpec 结构性标题与 SHALL/MUST 保留英文）
- [x] 1.3 确认 `.opencode/skills/openspec-*/SKILL.md`（5 个）与 `.opencode/commands/opsx-*.md`（5 个）已刷新到 CLI 1.13.2
- [x] 1.4 确认新增 `.opencode/commands/opsx-write-plan.md`、`opsx-executing-plans.md` 及对应 skills 目录

## 2. 补建能力基线

- [x] 2.1 依据 `LoginCtrl`、`LoginInterceptor`、`JwtToken`、`Md5` 的实际实现编写 `openspec/specs/auth-account/spec.md`
- [x] 2.2 依据 `AGENTS.md` §8 废弃流程编写 `openspec/specs/api-deprecation-policy/spec.md`，并补充「前端静态产物调用计入核查范围」要求

## 3. 修正文档过时描述

- [x] 3.1 修正 `AGENTS.md` §7.1：把「三模块均无 src/test 实现」改为实际统计（`aicode` 10 个 + `common` 8 个 = **18** 个测试类，`facade` 0 个），并如实记录 `facade` 已声明 `junit-jupiter` 但无测试
- [x] 3.2 修正 `AGENTS.md` §11.3：把「当前只有 SmokeTest 占位」改为已落地的测试清单（Controller 切片 / Service 并发 / 生成主链路 / 模板引擎适配 / SQL 逆解析 / 工具与枚举单测 / JGit 克隆完整性），并注明 `GitToolsCloneCompletenessTest` 受 `-Dnetwork.tests=true` 守卫、默认不执行
- [x] 3.3 在 `AGENTS.md` §8 补一段：能力基线位于 `openspec/specs/`；新增变更前先 `openspec list --specs` 查重、再 `openspec show` 通读；并说明跨变更的归档顺序依赖（`MODIFIED` 指向的 capability 须先由前序变更归档物化）

## 4. 验收

- [x] 4.1 执行 `openspec list --specs --json`，确认 `root.path` 为 `/Users/bruce/IdeaProjects/aicode`（而非 `/Users/bruce`）—— 实测 `{'path': '/Users/bruce/IdeaProjects/aicode', 'source': 'nearest'}` ✅
- [x] 4.2 执行 `openspec list --specs`，确认列出 `auth-account` 与 `api-deprecation-policy` 两个能力 ✅（各 4 条 requirements）
- [x] 4.3 执行 `openspec validate --changes --specs --strict`，确认全部通过 ✅（7 changes + 2 specs，0 failed）
- [x] 4.4 执行 `git check-ignore` 与 `git status`，确认 `openspec/` 与 `.opencode/` 未被 `.gitignore` 吞掉 ✅（check-ignore 退出码 1 = 未被忽略）
- [x] 4.5 确认 `./aicode/gradlew :aicode:compileJava` 仍通过 ✅（BUILD SUCCESSFUL；构建仍输出 Gradle 9 不兼容告警，属 `upgrade-spring-boot-3-5` 变更的处理范围）
