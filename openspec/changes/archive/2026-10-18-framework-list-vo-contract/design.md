# Design

## Context

M5-A（接 React 框架选择页）实施前的侦察发现两个安全问题：

| 端点 | 当前出参 | 泄漏 |
|---|---|---|
| `GET /framework/load?code=` | `R<Frameworks>`（**实体直出**） | `password`（Git 仓库密码） |
| `GET /framework/list?curPage=&pageSize=` | `R<Page<FrameworksPageVO>>` | `password` + `BaseVO` 的 4 个 `*Begin/*End` 查询辅助字段 |

`FrameworksPageVO extends BaseVO`，`BaseVO`（`common/src/main/java/com/aicode/core/BaseVO.java`）携带 4 个 `Date` 字段，语义是"入参：按时间范围过滤"。此外容器用的是 `com.aicode.core.Page<E>`（列表字段 `voList`，含 `params` / `sortColumns` / `optObject` 等内部字段）而非 `PageVO<T>`（列表字段 `records`）。

M2-1 已把 `/project/list` 收敛到 `R<PageVO<ProjectSummaryVO>>`，M3-D 已在 `/account/load` 堵掉 `AccountVO.password`。本变更把这两条规则推广到框架域，并补齐 `/framework/list` 遗留的容器退化。

## Goals / Non-Goals

**Goals:**

- 堵住 Git 仓库密码泄漏（`/framework/list` + `/framework/load`）；
- 让框架 VO 不继承 `BaseVO`，不把查询辅助字段带入响应；
- 把 `/framework/list` 的容器从 `Page<E>` 收敛到 `PageVO<T>`；
- 用 `ApiContractIT` 场景⑩ 把这三条钉成可执行事实（含反向验证）。

**Non-Goals:**

- 不接 React 框架选择页（M5-A2 变更）。
- 不改 `/framework/build`（注册框架）、`/framework/modify`（改框架）、`/framework/delete`、`/framework/updateIsPublic` 的出参 —— 它们各自另有形态，本变更只覆盖 `load` 与 `list` 这两个"读"端点（`build` / `modify` 是管理面写入，其返回含 password 亦是问题，但见 Open Questions）。
- 不改 `com.aicode.core.Page` 与 `BaseVO` 本身（其他 VO 仍在继承）。
- 不改前端任何代码（生成物由 orval 重生，属契约产物而非手写代码）。

## Decisions

### D1：新建 `FrameworkSummaryVO` 而非改 `FrameworksPageVO`

**决定**：新建 7 字段 VO，不 `extends BaseVO`；删除 `FrameworksPageVO`。

**理由**：
- 沿 M3-C 的 `ProjectSummaryVO` / `ProjectDetailVO` 模式 —— 列表页与详情页用不同 VO，各自对齐消费场景。
- 框架"列表"与"详情"消费字段不同：列表只需 `code` / `name` / `description` / `isPublic`（4 个），详情需加 `gitHome` / `account`（6 个）。本变更**统一用一个 VO**（7 字段含 `gitHome` / `account`）以避免 M5-A2 接页面时再拆一次；M5-A2 若发现列表不需要 `gitHome` / `account`，可在该变更里再收窄。
- `gitHome` / `account` 保留是合理的：前端建项目时可能需要展示"模板来自哪个仓库"，便于用户判断信任度。`password` 绝不可留。

**备选方案**：(a) 改 `FrameworksPageVO` 加 `@JsonIgnore password` + `@JsonIgnore` 掉 BaseVO 4 字段 —— 改动小但保留"实体全集拷贝"形态，且 `@JsonIgnore` 数量随字段增长；(b) 新建 VO —— 本变更采用。

### D2：`toSummary` 手工逐字段装配，不复用 `BeanUtils.copyProperties`

**决定**：在 `FrameworksController` 加私有 `toSummary(Frameworks)`，逐字段 `setXxx`。

**理由**：
- 沿 M3-C `toSummary(Project)` 的做法 —— `BeanUtils.copyProperties(entity, target)` 在 target 字段是 entity 字段子集时是安全的（不会多复制），但**显式胜过隐式**：新增实体字段时不会静默进入响应。
- M3-D 的 `loadByCode` 用了 `BeanUtils.copyProperties`（因为 `AccountPublicVO` 与 `Account` 字段同名且是子集），本变更选择手工以保持与 M3-C 一致并强化"新增字段必须显式 opt-in"的纪律。

### D3：`/framework/list` 的 `records` 装配沿用现有 `queryWrapper.lambda().ne(Frameworks::getId, 0)` 过滤

**决定**：保留 `ne(id, 0)`（排除 id=0 的哨兵行）与 `orderByDesc(id)` 排序，只改容器与元素类型。

**理由**：这两条是既有业务语义（过滤哨兵 + 倒序），本变更不动。

### D4：`/framework/load` 的 null 早返用 `R.success()`

**决定**：`code == null` 时 `return R.success()`（`data=null`）。

**理由**：沿 M3-A-1 的记录 —— `R.success(null)` 会走 `R.success(BaseExceptionEnum)` 重载导致 NPE（`common/src/main/java/com/aicode/core/R.java:61`）。无参 `R.success()` 是安全写法。

## Risks / Trade-offs

**[Git 密码此前已泄漏多久]** → 无法追溯（无访问日志）。本变更堵住未来泄漏，但不回溯历史暴露。这条应记入 `AGENTS.md §11.1 安全加固` —— 但历史数据的补救（轮换所有模板仓库密码）不在本变更范围。

**[`FrameworksPageVO` 删除后若有其他引用会编译失败]** → task 1.5 用 grep 钉这一前提；若发现引用则改为"保留类但加 `@Deprecated`"，而非硬删。

**[`/framework/build` 与 `/framework/modify` 仍可能返含 password 的对象]** → 见 Open Questions。它们是**写入**端点（管理面），当前无前端消费者；若 M5-A2 之后需要接管理面，须先做同样的 VO 收敛。

**[前端生成物减少 schema 可能引发 typecheck 失败]** → M5-A2 尚未接 `/framework/*`，前端无消费方，`npm run typecheck` 应不受影响。task 2.3 会验证。

## Migration Plan

单批交付（一个 feature commit）：

1. 新增 `facade/.../frameworks/vo/FrameworkSummaryVO.java`（7 字段，不继承 BaseVO，不含 password）；
2. `FrameworksController` 加 `toSummary(Frameworks)`；`load` 改返 `R<FrameworkSummaryVO>`；`list` 改返 `R<PageVO<FrameworkSummaryVO>>`；
3. grep 确认 `FrameworksPageVO` 无其他引用后删除；
4. `ApiContractIT` 新增 `@Nested class FrameworkListNeverLeaksGitPassword`（3 用例含反向验证）；
5. 跑 `./aicode/gradlew :aicode:test`（含 `OpenApiExportIT` 刷新契约）；
6. `cd aicode-web && npm run generate` 重生；`npm run typecheck` + `npm test` 验证；
7. 同步两个 delta spec 到主 spec 并 archive。

**回滚**：单 commit revert。BREAKING 面为空（无 Java 消费方 + Angular 已删 + React 未接）。

## Open Questions

- **`/framework/build` 与 `/framework/modify` 的出参是否也在本变更收敛？** 二者当前返含 `password` 的 `R<Frameworks>` 实体（`modify` line 94、`updateIsPublic` line 107 亦同）。它们是**管理面写入端点**，当前无前端消费者。M5-A2 接框架选择页时只读 `list` 与 `load`，故**本变更不动**；若未来接管理面（增删改框架），须另开变更做同样收敛。
- **`PageVO<VO> extends BaseVO` —— 容器层也带 4 个查询辅助字段**（**实施期新发现**）：`common/src/main/java/com/aicode/core/PageVO.java:16` 显示 `PageVO` 继承 `BaseVO`，因此**所有分页端点**（`/project/list`、`/framework/list`、`/account/list` …）的响应 `data` 层都含 `createTimeBegin` / `createTimeEnd` / `updateTimeBegin` / `updateTimeEnd` 四个 `null` 字段。这是**全仓库共性问题**，非本变更引入。彻底修法有两种：① `PageVO` 不继承 `BaseVO`（但 `BaseVO` 的设计意图是"查询条件载体"，被 20+ VO 继承）；② 在 `BaseVO` 的 4 个字段加 `@JsonIgnore`（Jackson 层屏蔽，最小改动，但会让「入参用 BaseVO 承载查询条件」的意图在序列化层不可见）。二者都影响全仓库，**应另开变更**。本变更的 spec Scenario 只约束**元素层**（`records[*]`），容器层的共性问题记入此 Open Question。
- **`FrameworksPageVO` 是否还有其他 Controller 在用？** task 1.5 grep 已确认：除自身定义与本变更的注释外无引用，已安全删除。
- **历史泄漏的 Git 密码是否需要轮换？** 属运维动作，需用户决策（不在代码变更内）。
