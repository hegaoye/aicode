# Tasks

> 修正范围仅限 `openspec/changes/` 下 6 个活跃变更的 **7 个文件**。SHALL NOT 触碰任何 `### Requirement:` / `#### Scenario:` 块、任何 design.md 的决策段、任何已归档的变更。

## 0. 建立实测基线

- [x] 0.1 测量测试类总数并留档：**18** —— `find aicode/src/test common/src/test facade/src/test -name '*.java' | wc -l`
- [x] 0.2 分模块测量留档：`aicode` **10** / `common` **8** / `facade` **0** —— `for m in aicode common facade; do echo "$m: $(find $m/src/test -name '*.java' 2>/dev/null | wc -l | tr -d ' ')"; done`
- [x] 0.3 测量 `@MockBean` 逐类计数留档（`grep -c '^\s*@MockBean' <file>`）：

  | 测试类 | 实测 |
  |--------|------|
  | `ProjectControllerDownloadTest` | **14** |
  | `SqlReverseParseTest` | **16** |
  | `ProjectInitEndpointTest` | **12** |
  | `MapRelationshipControllerTest` | **6** |
  | `ProjectJobServiceImplConcurrencyTest` | **3** |
  | `LoginCtrlShtmlPathTest` | **3** |

- [x] 0.4 测量 `@MockBean` 总数留档：**54** —— `grep -rh '^\s*@MockBean' --include='*.java' aicode/src/test | wc -l`

## 1. 修正测试类计数（7 处）

- [x] 1.1 `add-api-contract-tests/tasks.md` 第 5.2 条：「17 个既有测试类」→「18 个既有测试类」，并附实测命令
- [x] 1.2 `upgrade-spring-boot-3-5/tasks.md` 第 6.1 条：「含 17 个既有测试类」→「含 18 个既有测试类」，并附实测命令
- [x] 1.3 `upgrade-gradle-9-jdk-25/tasks.md` 第 7.1 条：「（17 个既有测试类）」→「（18 个既有测试类）」，并附实测命令
- [x] 1.4 `upgrade-gradle-9-jdk-25/proposal.md` 第 5 行：「17 个既有测试 + 契约测试全绿」→「18 个既有测试 + 契约测试全绿」
- [x] 1.5 `sync-docs-after-platform-upgrade/proposal.md` §Why 表格第 17 行：「`aicode` 9 个 + `common` 8 个 = **17 个测试类**」→「`aicode` 10 个 + `common` 8 个 = **18 个测试类**」（**同一格内两个数字都已改**）
- [x] 1.6 `sync-docs-after-platform-upgrade/proposal.md` §Why 表格第 20 行：「实为 17 个测试类」→「实为 18 个测试类」
- [x] 1.7 `sync-docs-after-platform-upgrade/tasks.md` 第 3.1 条：「`aicode` 9 个 + `common` 8 个 = 17 个测试类」→「`aicode` 10 个 + `common` 8 个 = 18 个测试类」（**同一句内两个数字都已改**）

## 2. 修正 `@MockBean` 计数（4 处 + 6 个分项）

- [x] 2.1 `upgrade-spring-boot-3-5/proposal.md`：「涉及 5 个测试类约 45 处」→「涉及 6 个测试类 54 处」
- [x] 2.2 `upgrade-spring-boot-3-5/tasks.md` 第 3.1 条：`LoginCtrlShtmlPathTest`「3 处」→ 实测仍为 **3 处**（核对通过，无需修改）
- [x] 2.3 第 3.2 条：`MapRelationshipControllerTest`「7 处」→「6 处」
- [x] 2.4 第 3.3 条：`ProjectControllerDownloadTest`「15 处」→「14 处」
- [x] 2.5 第 3.4 条：`ProjectInitEndpointTest`「13 处」→「12 处」
- [x] 2.6 第 3.5 条：`ProjectJobServiceImplConcurrencyTest`「3 处」→ 实测仍为 **3 处**（核对通过，无需修改）
- [x] 2.7 第 3.6 条：`SqlReverseParseTest`「17 处」→「16 处」
- [x] 2.8 在 `upgrade-spring-boot-3-5/tasks.md` 的「## 3. @MockBean 迁移」小节标题下补一行实测命令与合计（54），使分项与总数可互相校验

## 3. 验收

- [x] 3.1 确认「17 个」在**待修正的 6 个活跃变更**中零残留
      > 命令：`find openspec/changes -path '*/archive' -prune -o -path '*fix-planning-artifact-facts*' -prune -o -name '*.md' -print | xargs grep -n "17 个"` → 零命中
      > （本变更自身的 proposal / design / tasks 中出现的「17 个」是错误示例的引述，属预期，SHALL NOT 判为残留）
- [x] 3.2 确认全部活跃变更的 `openspec validate --changes --strict` 仍通过 ✅（Totals: **7 passed, 0 failed**，含本变更自身）
      > 两条 `ℹ [INFO] Archive would refuse this delta` 是预期的顺序提示：`api-contract` / `runtime-platform` 由 `add-api-contract-tests` 与 `upgrade-gradle-9-jdk-25` 在**归档时**才物化，两个变更需按序归档
- [x] 3.3 确认 `openspec validate --specs --strict` 仍通过 ✅（`api-deprecation-policy` 4 条、`auth-account` 4 条，均未受影响）
- [x] 3.4 逐行检查本次修改：SHALL NOT 出现任何 `### Requirement:` / `#### Scenario:` / `## Decisions` 行的增删改
      > `openspec/` 尚未纳入 git 跟踪（`git diff` 不可用），改用 mtime 判定实际被写入的文件。修正窗口内被触及的**仅 6 个文件**，全部是 `proposal.md` 或 `tasks.md`：

      | 文件 | 修正内容 |
      |------|---------|
      | `add-api-contract-tests/tasks.md` | 5.2 测试类数 |
      | `upgrade-spring-boot-3-5/proposal.md` | `@MockBean` 总数 |
      | `upgrade-spring-boot-3-5/tasks.md` | 6.1 测试类数 + 3.2–3.4 / 3.6 逐类计数 + 小节补实测命令 |
      | `upgrade-gradle-9-jdk-25/proposal.md` | 动机句中的测试类数 |
      | `upgrade-gradle-9-jdk-25/tasks.md` | 7.1 测试类数 |
      | `sync-docs-after-platform-upgrade/proposal.md` | §Why 表格 2 处 |
      | `sync-docs-after-platform-upgrade/tasks.md` | 3.1 测试类数 |

      > 6 个变更的 `design.md` 与 `specs/*.md` 全部**未被触碰**（不在写入窗口内），即 Requirement / Scenario / Decisions 零改动 ✅
      > 另：`upgrade-spring-boot-3-5/tasks.md` 的 3.1 / 3.5 经核对本就是 3 处，未修改（任务 2.2 / 2.6）

- [x] 3.5 确认已归档的 `openspec/changes/archive/2026-10-14-init-openspec-workspace/` 未被触碰 ✅
- [x] 3.6 确认 `AGENTS.md`、`src/`、`build.gradle` 未被本变更触碰 ✅
      > `git status --short` 中 ` M AGENTS.md` 来自**已归档的** `init-openspec-workspace` 变更（其任务 3.1–3.3），非本变更
- [x] 3.7 确认 `openspec validate 2026-10-14-fix-planning-artifact-facts --strict` 通过 ✅（`is valid`）
