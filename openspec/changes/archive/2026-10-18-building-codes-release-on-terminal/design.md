# Design

## Context

`ProjectListPage` 的 `buildingCodes: Set<string>` **只有加入、没有移除** —— 点击「构建」
时 `startBuildFor(code)` 加入，此后没有任何代码路径把它删掉。

后果是**两个场景都会让该行永久失去构建能力**：

| 场景 | 过程 | 结果 |
|---|---|---|
| 构建成功 | 收到 `done` → `BuildLogPanel` **自动收起**（上一变更引入）| `buildingCodes` 仍含 code → 按钮**永久**显示「构建中…」且禁用 |
| 用户手动关闭 | 点面板「关闭」→ 面板消失 | 同上 |
| 唯一恢复方式 | — | **刷新页面** |

对代码生成工具来说这是很痛的：用户对同一个项目要**反复构建**直到满意
（改 SQL → 重新解析 → 重新构建 → 不满意 → 再改）。构建一次后那一行就废了。

## 触发这个变更的原因

上一变更（`build-log-terminal-signal`）引入了「面板自动收起」，让这个缺陷**从隐蔽变明显** ——
原先用户还可以点「关闭」按钮（虽然 `clear()` 也不清 `buildingCodes`，所以其实一样卡住）。
design 的 Risks 段记录了这一点，本变更收尾。

## Goals / Non-Goals

**Goals:**

- 构建终态后释放该行的「构建中」态；
- 用户主动关闭面板后同样释放；
- **翻回正在构建的行时不得误释放**（这是本变更最关键的约束）。

**Non-Goals:**

- 不做「同一行并发多次构建」—— 现有禁用态已阻止。
- 不做构建历史的持久化（跨页面会话）。
- 不改后端（后端的并发检查 `ProjectJobServiceImpl.execute` 是既有的第二道防线）。
- 不改 `BuildLogPanel` 的自动收起语义（上一变更已定：`done` 收起 / `error` 保留）。

## Decisions

### D1：`BuildLogPanel` 加 `onClosed?: (projectCode: string) => void`

**决定**：`BuildLogPanel` 新增可选回调 `onClosed`，在**面板从 DOM 消失**时调用一次。

**为什么不叫 `onTerminal`**：触发它的两条路径不止终态 —— 还有用户主动点「关闭」。
`onClosed` 覆盖「面板消失」这个统一语义，而两条路径对父组件的意义**完全相同**
（释放该行的进行态）。

### D2：触发条件是「从曾显示变为消失」，而非「当前不是运行态」

**这是本变更最容易做错的地方。** 若直接写成：

```tsx
useEffect(() => {
  if (status === 'idle' || status === 'done') { onClosed?.(projectCode) }
}, [status])
```

会**误伤翻回场景** —— 上一变更已确立：翻回正在构建的行时，`BuildLogPanel` 以
`autoStart={false}` 挂载（`ProjectListPage` 的 `mountedPanelsRef` 已有该 code），
hook 的 `status` 初始就是 **`idle`**，面板本就 `return null`。
上面那段代码会在翻回的**第一次渲染**就触发 `onClosed` → 清掉 `buildingCodes`
→ 按钮恢复可点 → 用户可重复触发构建（后端 Executing 检查会拒绝，用户看到「点了没反应」）。

**故用 `hasShownRef`**：

```tsx
const hasShownRef = useRef(false)
useEffect(() => {
  if (status === 'running') { hasShownRef.current = true; return }
  if (status === 'idle' || status === 'done') {
    if (hasShownRef.current) { hasShownRef.current = false; onClosed?.(projectCode) }
  }
}, [status])
```

语义精确为「**这个面板实例曾经显示过内容，现在消失了**」：

- 翻回挂载（`autoStart=false`，status=idle）→ `hasShownRef` 为 false → **不触发** ✓
- 正常构建（status 走 running → done）→ `hasShownRef` 置 true 后再触发 ✓
- 构建中用户点关闭（status running → clear() → idle）→ 触发 ✓
- 构建失败（status 走 running → error）→ `error` **不在**消失条件里（面板刻意保留）→ 不触发
  —— 这正确吗？

**D2 的一个推论需要审视**：`error` 时面板保留（上一变更的决策），此时**不该**释放进行态 ——
用户还要看错误信息。所以 `error` 不触发 `onClosed` 是对的。但用户看完错误后点「关闭」
→ status 变 `idle` → 此时 `hasShownRef` 为 true（running 时置的）→ **触发** ✓

**用户构建失败后必须再点一次「关闭」才能重新构建** —— 这是可接受的：失败时面板刻意保留，
用户看完自然要点关闭。

### D3：`ProjectListPage` 传回调并移除 code

```tsx
onClosed={(code) => setBuildingCodes((prev) => {
  const next = new Set(prev); next.delete(code); return next
})}
```

用**函数式更新**而非闭包里的 `buildingCodes`（多个面板并发关闭时，逐个基于旧快照会丢删除）。

### D4：`startBuildFor` 的 `useCallback` 依赖可以简化

`startBuildFor` 当前依赖 `[buildingCodes]`，每次集合变化都重建回调 → `ProjectSqlPanel`
之类的 memo 失效。既然改成函数式更新，`buildingCodes.has(code)` 的守卫仍需最新值，
保留依赖即可（本变更不动它）。

## Risks / Trade-offs

**[用户在构建**进行中**主动点「关闭」→ 进行态被释放 → 按钮恢复可点]** →
用户可重复点构建，后端 `ProjectJobServiceImpl.execute` 的 Executing 检查会抛
`Server_Error`，用户看到「构建失败」但其实是被并发检查拒绝。

**这是可接受的**：用户主动关闭面板就表达了"我不想看了"，恢复可点符合直觉；
后端拒绝是明确的第二道防线（且已有）。**不引入"关闭时真的取消构建"** ——
那需要后端支持取消，是独立议题。

**[若将来给构建加"取消"能力，此处语义要重新审视]** —— 已记入 Open Questions。

**[`onClosed` 回调可能触发父组件重渲染** → `setBuildingCodes` 每次都产生新 Set 对象
→ `isBuilding` 重算 → 该行重渲染。但面板此时已从 DOM 消失，无实际副作用。

## Migration Plan

单批交付（一个 commit）：

1. `BuildLogPanel`：加 `onClosed` prop + `hasShownRef` 守卫（D2）；
2. `ProjectListPage`：传 `onClosed` 并移除 code（D3）；
3. 测试：`build-log-panel` 补 `onClosed` 触发用例；`ProjectListPage` 补
   「构建成功后按钮恢复」端到端用例（用 mock ws 投递终态）；
4. AGENTS.md 登记 + validate + archive。

**回滚**：单 commit revert。注意它依赖上一变更的 4 态状态机与自动收起。

## Open Questions

- **构建取消** —— 用户点「关闭」时是否该真的取消后台构建？需要后端支持
  （`ProjectJob` 加 `Cancelled` 态 + 协作式中断），属独立议题。
- **`buildingCodes` 是否需要持久化** —— 刷新页面后进行态全丢，用户可以对同一项目
  重复触发构建（后端会拒绝）。当前可接受。
- **构建历史** —— 用户可能想看"这个项目上次构建是什么时候、成功还是失败"。
  `ProjectJob` 表已在记录，但前端未消费。属功能补全。