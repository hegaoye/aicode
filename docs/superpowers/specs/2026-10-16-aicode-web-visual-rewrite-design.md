# aicode-web 视觉重写设计

- 日期：2026-10-16
- 分支：`feature/20261016/react-web-rewrite`
- 视觉基准：OpenDesign 项目「ai-code 页面设计」（UUID `4cc16725-afb7-4882-bf88-fc8eec4f1b05`），原型目录
  `/Users/bruce/Library/Application Support/Open Design/namespaces/release-stable/data/projects/4cc16725-afb7-4882-bf88-fc8eec4f1b05`

---

## 1. 意图

现有 `aicode-web` 的 8 个页面业务逻辑是对的（契约驱动链路完整、169 个行为用例守着真问题），
但 DOM 结构是随手写的、布局靠 TSX 里内联 `style={{}}` 堆出来的，交互密度和视觉秩序都不像
一个给人用的研发工具。

本次把 OpenDesign 原型的 8 个页面作为**唯一视觉与布局基准**逐页还原，业务流程一行不改。

## 2. 目标 / 非目标 / 反目标

### 目标

- 8 个页面的视觉与布局 100% 还原原型
- 信息架构、路由语义、业务流程保持现状
- 契约驱动链路完整 —— `src/generated/` 保持纯生成物
- 169 个行为用例的覆盖不退化

### 非目标（本次明确不做）

- 修 5 处 `@Parameter(hidden = true)` 契约缺陷（见 §6），留待验收后单独开 OpenSpec 变更
- 新增接口消费：`generated-files` 目录树、技术栈编辑、仓库信息编辑
- 引入新运行时依赖：不装 Tailwind、不装 react-query / SWR
- 国际化（原型本身中文硬编码）
- 补响应式（原型 13 个文件里有 5 个零 `@media`，本次以原型观感为准）

### 反目标（做到即算失败）

- 为了「看起来像」而手写 `axios.post` 绕过 orval
- 死按钮做成「看着能点、点了没反应、无任何提示」
- 重写过程中把 `unwrapSuccess` / 两个 axios 拦截器 / `ws-client` 换成别的实现
- 继续用内联 `style` 做布局，导致样式无法集中修改

## 3. 约束

| 项 | 值 |
|---|---|
| 运行时 | React 19.3.1 / react-router-dom 7.18.4 / axios ^1.20.0 |
| 构建 | TypeScript 7.0.2 / Vite 8.3.4，`tsc --noEmit && vite build` → `build/dist` |
| 测试 | Vitest 5.0.5 + Testing Library 16.3 + MSW 3.0.3 |
| 打包 | Gradle 取 `build/dist`，jar 内布局 `static/**` |
| 红线 | `src/generated/` 禁手改；`:aicode-web:verifyApi` 不得红；依赖版本不动 |
| 语言 | 界面文案中文（原型即中文硬编码） |

## 4. 样式架构（核心工程决策）

现状是「1 个 `tokens.css`（约 10 个类）+ 其余全在 TSX 里内联」。原型每页还有 40+ 个类。
中间态最糟，故直接定成三层：

```
src/styles/
  tokens.css            唯一 token 源，已是原型 brand-spec.md 落地，不动
  base.css              全局重置 + 跨页复用类
  AppShell.css
  LoginPage.css
  NewProjectPage.css
  ProjectListPage.css
  ProjectDetailPage.css
  ProjectSqlPanel.css
  RelationshipPanel.css
  BuildLogPanel.css
  FrameworkListPage.css
  SharedPages.css       原型无对应页面的两页（DisplayAttributes / Account）复用基元
```

**类名沿用原型原名**（`.panel` `.panel-head` `.stepper` `.proj` `.editor-wrap` `.ln-warn` …），
不做重命名 —— 改名唯一的收益是「好看」，代价是逐页比对时对不上原型，而本次验收的核心正是逐页比对。

TSX 中不再出现用于布局的内联 `style`；只允许动态值（日志行 `className` 切换、构建中的脉冲态）。

**跨页复用类**（进 `base.css`）：
`.topnav` `.topnav-inner` `.container` `.page-head` `.pagefoot` `.panel` `.panel-head`
`.panel-body` `.btn-primary` `.btn-ghost` `.btn-solid` `.form-row` `.form-foot`
`.form-input` `.form-textarea` `.field` `.field-error` `.radio-group` `.stepper` `.step`
`.tip-panel` `.data-table` `.table-card` `.row-actions` `.muted` `.page`

### 原型的类被复制了 13 遍且略有出入

抽取时以语义最全的一版为准：

| 类族 | 最全的来源 |
|---|---|
| `.proj` `.tag` `.status` `.toolbar` `.tabs` `.search` | `dashboard.html` |
| `.panel` `.panel-body` `.grid-2` `.badge` `.empty-state` `.sql-block` `.tech-line` `.build-bar` | `project-review.html` |
| `.stepper` `.step` `.tip-panel` `.form-row` `.form-foot` `.radio-group` | `new-project.html` |
| `.editor` `.editor-bar` `.editor-wrap` `.gutter` | `wizard-step2-sql.html` |
| `.data-table` `.table-card` `.row-actions` `.section-title` | `wizard-step3-models.html` |
| `.log-wrap` `.log-head` `.log-body` `.ln-*` `.footer-bar` `.fab` | `build-log.html` |
| `.toggle` `.pager` `.modal` `.modal-*` `.table-wrap` | `tech-architecture.html` |
| `.auth-*` `.pixel-cluster` `.remember-row` | `login.html` |

**`build-log.html` 定义了 `.ln-event/.ln-ok/.ln-warn/.ln-sql/.ln-done/.ln-link` 但没有
`.ln-error`**。现状的 error 态复用 `.ln-warn`。本次**新增 `.ln-error`**（比 `.ln-warn` 更重的
红色），因为 error 与 warn 在日志里的可读性要求不同。

## 5. 逐页规格

| 原型页 → 改造对象 | 原型专属类 | 保留的行为 | 死的交互 |
|---|---|---|---|
| `login.html` → `LoginPage` | `.auth-stage` `.auth-card` `.auth-fields` `.input-wrap` `.auth-input` `.auth-row-between` `.remember-row` `.link-register` `.auth-actions` `.auth-foot` `.pixel-cluster` `.btn-primary` | `api.signin` → `unwrapSuccess` → `signIn(token)`；空 token 视为失败；`R.code !== '0000'` 错误气泡；回跳 `state.from` | 无 |
| `dashboard.html` → `ProjectListPage` | `.project-grid` `.card-new` `.proj` `.tag-row` `.tag.lang-*` `.status` `.status.building` `.status.draft` `.toolbar` `.tabs` `.search` | `api.list2` 分页、翻页整页替换、末页禁用、`buildingCodes` 跨翻页保留、`hasShownRef` 防误释放、首个终态释放「构建中」 | tab 点击当前只切 `aria-selected`，不做真筛选（现状如此，保持） |
| `new-project.html` → `NewProjectPage` | `.stepper` `.step` `.step.active` `.step.done` `.tip-panel` `.form-row` `.form-input` `.form-textarea` `.radio-group` `.form-foot` `.btn-ghost` | 9 字段、类型级完备性断言、`9005` 英文名占用文案、`R.code !== '0000'` 保留已填值、成功后导航 `/project/{code}` | 无 |
| `wizard-step2-sql.html` → `ProjectSqlPanel` | `.editor-wrap` `.editor-bar` `.tab` `.dot.live` `.gutter` `.editor` | `api.build` → **先连 ws 再** `api.init` → 进度条 → 解析结果重读；`error` 保留进度区并显示「已解析 N/M 张表后失败」；`total=0` 显示不确定进度 | 无 |
| `wizard-step3-models.html` → `RelationshipPanel` | `.table-card` `.data-table` `.row-actions` `.section-title` | 两组独立级联、上游变更清空下游、基数映射（`一对多`↔`oneToMany=Y`／`一对一`↔`oneToOne=Y`+`oneToMany=N`）、`build` 是 upsert 故保存后重拉、本地 code→tableName 映射回落 `associateCode` | 无 |
| `project-review.html` → `ProjectDetailPage` | `.panel` `.panel-head` `.panel-body` `.grid-2` `.badge.ok` `.badge.pending` `.empty-state` `.sql-block` `.tech-line` `.build-bar` `.btn-build` | `api.loadByCode` 12 字段分组；构建按钮 → `api.execute` → `BuildLogPanel` | 「技术信息」「仓库信息」两面板渲染为 `.badge.pending` + `.empty-state`，文案标注接口待修复 |
| `build-log.html` → `BuildLogPanel` | `.log-wrap` `.log-head` `.log-body` `.ln-event` `.ln-ok` `.ln-warn` `.ln-sql` `.ln-done` `.ln-error` `.ln-link` `.footer-bar` `.btn-solid` `.btn-outline` `.fab` | 4 态终态机、首个终态权威、`{"type":"progress"}` 进 `stage` 不进日志、旧 `Finished:` 字符串兼容、`error` 刻意保留面板 | 无 |
| `tech-architecture.html` → `FrameworkListPage` | `.page-head` `.table-wrap` `.toggle` `.row-actions` `.pager` `.pages` `.modal-mask` `.modal` `.modal-head` `.modal-body` `.modal-foot` `.form-field` `.err` | `api.list4` 分页、`api.modify1` 编辑、`api.updateIsPublic` toggle；响应无 `password` | 「添加」「删除」按钮渲染完整视觉但 `disabled` + `title="接口待修复"`；`.modal` 保留结构供后续接 `framework/build` |

**死按钮的处理**：`disabled` + `title` 提示，不绑 `onClick`。视觉 100% 在，点击给明确原因 ——
避免验收时反复点它然后困惑。

### 原型无对应的两页

`DisplayAttributesPage`（10 用例）与 `AccountPage`（3 用例）在原型 12 页里没有对应页面。
本次**不重新设计它们**（无基准），但必须复用 `base.css` 基元，让它们与重写后的 8 页
共享同一套 topnav / 面板 / 表格观感，否则应用内会出现一个视觉上「没跟上」的页面。

## 6. 契约缺陷（本次不修，但决定「死交互」清单）

同一处反模式导致 5 个写侧端点在契约里**完全没有入参**：
`build(@Parameter(hidden = true) XxxEntity)` —— springdoc 忽略整个形参，
而运行时按字段名绑定 N 个扁平 query 参数。

| 端点 | 影响页面 | 本次表现 |
|---|---|---|
| `POST /framework/build` | 技术架构页「添加」 | 按钮 `disabled` |
| `POST /framework/delete` | 技术架构页「删除」 | 按钮 `disabled` |
| `POST /project/repository/build` | 仓库信息面板 | 面板 `.badge.pending` |
| `POST /project/repository/modify` | 仓库信息面板 | 同上 |
| `POST /project/framwork/add` | 技术选定页（整页延后） | 页面不实现 |

`GET /project/download/{projectName}` 在契约里标 `deprecated: true`，`generated-files` 的目录树
**没有任何端点** —— 整页延后。

`2026-10-18-framework-write-endpoints` 曾把 `framework/modify` 与 `framework/updateIsPublic`
改成显式 `@RequestParam`，`build` / `delete` 漏掉了 —— 这就是同一页面一半能用一半不能的原因。

## 7. 路由变更

```
/login             LoginPage                不变
/                  未登录→/login；已登录→/project/list   （原为内联占位 + 3 个 Link）
/project/list      ProjectListPage          不变
/project/new       NewProjectPage           不变
/project/:code     ProjectDetailPage        ← 改！原为 ProjectPlaceholderPage
/frameworks        FrameworkListPage        不变
/displayAttributes DisplayAttributesPage    不变
/account/me        AccountPage              不变
*                  → /                      不变
```

`/project/new` 必须声明在 `/project/:code` 之前（React Router 按声明顺序匹配）。

**顺带修一个现在走不到的死页面**：`ProjectDetailPage` 有 4 个用例、组件完整，
但 `App.tsx` 里 `/project/:code` 渲染的是 M2 占位页 —— 用户点项目列表进不去详情。

## 8. 测试策略（方案 A：行为测试保底 + 肉眼逐页比对）

- **169 个用例全部保住行为覆盖**，只改选择器对齐新 DOM
- **不新增视觉测试** —— 视觉由人逐页比对原型验收
- `src/test-setup.ts` 的 9 个 MSW handler 注册不动
- `src/generated/` 不重新生成 —— 契约没变，重跑只会引入 §10 记的 properties 顺序抖动

必须保住的行为断言（不能为了改 DOM 顺手删）：

| 行为 | 守护用例所在 |
|---|---|
| `9007` → 清 token + 跳登录 | `auth-interceptor.test.ts` |
| `9005` → 英文名已被占用 | `NewProjectPage/index.test.tsx` |
| `buildingCodes` 的进入与**释放**两条路径 | `ProjectListPage/index.test.tsx` |
| 翻回已构建页不重复调 `execute` | `ProjectListPage/index.test.tsx` |
| 构建日志首个终态权威 | `use-build-log.test.tsx` |
| `progress` 事件进 `stage` 不进日志列表 | `use-build-log.test.tsx` |
| 先连 ws 再调 `init`（顺序反了丢首批消息） | `ProjectSqlPanel.test.tsx` |
| 24 个 `displayType` 枚举来自 `Object.values()`，未命中数写死 | `display-controls/index.test.tsx` |
| 空 `records` 走业务空态而非错误气泡 | `FrameworkListPage/index.test.tsx`、`ProjectListPage/index.test.tsx` |

**反向验证**：至少对 2 个页面各做一次「删掉一个面板断言 → 确认测试变红 → 恢复」，
证明这些断言真能抓到回归（AGENTS.md §10 记的三次「假通过」教训）。

## 9. 验收方式

```bash
cd aicode-web && npm install && npm run dev    # :5173，代理到 127.0.0.1:8080
npm run typecheck && npm test
```

逐页与原型并排比对：

| 路由 | 对照原型文件 |
|---|---|
| `#/login` | `login.html` |
| `#/project/list` | `dashboard.html` |
| `#/project/new` | `new-project.html` |
| `#/project/:code` | `project-review.html` |
| `#/frameworks` | `tech-architecture.html` |

SQL 编辑器、关联关系、构建日志是内嵌面板，对照 `wizard-step2-sql.html` /
`wizard-step3-models.html` / `build-log.html`。

## 10. 不在范围

- `index.html` 落地页是第 13 个原型文件、纯营销页，不实现为路由
- `wizard-step4-stack.html`（技术选定）、`wizard-step5-repo.html`（仓库信息）、
  `generated-files.html`、`template-docs.html` 四个页面延后到契约修复后