# aicode-web 视觉重写 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 把 `aicode-web` 的 8 个页面按 OpenDesign「ai-code 页面设计」原型做 100% 视觉与布局还原，业务流程与契约驱动链路一行不改。

**Architecture:** 三层样式（`tokens.css` 唯一 token 源不动 + 新增 `base.css` 跨页基元 + 每页一个 `.css`），类名沿用原型原名不做重命名，TSX 中用于布局的内联 `style` 全部移除。8 个现有页面 1:1 对应原型页，业务逻辑（hook / `unwrapSuccess` / orval 生成物 / MSW / ws）全部保留，只换 DOM 结构与样式。5 个契约坏掉的写侧端点不做后端修复，改为 `disabled` + `title` 提示。

**Tech Stack:** React 19.3.1 / react-router-dom 7.18.4 / axios ^1.20.0 / TypeScript 7.0.2 / Vite 8.3.4 / Vitest 5.0.5 + Testing Library 16.3 + MSW 3.0.3

**Spec:** `docs/superpowers/specs/2026-10-16-aicode-web-visual-rewrite-design.md`

**原型目录（只读，每次任务从这里转录 CSS）：**
`/Users/bruce/Library/Application Support/Open Design/namespaces/release-stable/data/projects/4cc16725-afb7-4882-bf88-fc8eec4f1b05`

---

## Global Constraints

以下每条对所有任务生效：

- **依赖版本一律不动** —— 不新增、不升级、不降级任何 npm 包
- **`src/generated/` 是纯生成物** —— 禁止手改，且**本次不重新运行 `npm run generate`**（契约未变，重跑会引入 AGENTS.md §10 记的 schema properties 顺序抖动，污染 `contracts/openapi/openapi.yaml`）
- **不改后端任何文件** —— 5 处 `@Parameter(hidden = true)` 是契约缺陷，本次只做前端降级
- **业务逻辑零改动** —— `src/lib/**` 全部文件（`unwrapSuccess`、两个 axios 拦截器、`token-store`、`ws-client`、`progress-event`、`use-build-log`、`use-init-progress`、`display-controls`、`use-auth`、`use-current-account`）不改一行
- **`src/test-setup.ts` 的 9 个 MSW handler 注册不动**
- **CSS 类名沿用原型原名**，不做重命名 —— 逐页比对是验收手段，改名会让比对失效
- **禁止手写 `axios` 调用** —— 一切走 `src/generated/api.ts` 的 orval 生成函数
- **界面文案中文**
- 每个任务结束时 `npm run typecheck && npm test` 必须全绿
- 提交信息用中文，格式 `feat(web): …` / `refactor(web): …` / `fix(web): …`

---

## Review Focus

规格是愿景文档，它没说的事不等于可以做。以下五类是**最可能咬到真实使用者、但没有任何任务的测试覆盖**的输入。每个任务自己补上对应测试。

| # | 输入 / 失败模式 | 一个理性的人会期望什么 |
|---|---|---|
| 1 | **新页面的请求路径不在 `shouldAddShtmlSuffix` 的 6 个前缀内**（`/project` `/framework` `/displayAttribute` `/logs` `/login` `/account`） | 重写 DOM 时若引入新路径前缀，`.shtml` 后缀不会被补 → 运行时 `NoResourceFoundException`（400），但**没有任何前端测试会红**。Task 1 必须加一条断言把这条清单钉死 |
| 2 | **项目列表空 `records`** —— `dashboard.html` 的卡片网格假定有数据 | 空态该显示什么？现有断言是纯文本 `（暂无数据）`，改成卡片网格后这个文本可能消失 → 用户看到一片空白以为是坏了 |
| 3 | **`buildingCodes` 按索引而非按 `code` 存储** —— 卡片网格重排后行顺序变了 | 翻页后「构建中」态贴到别的项目上。AGENTS.md §10 记过「构建一次该行就报废」的同类事故 |
| 4 | **构建日志「首个终态权威」被改成「后到覆盖先到」** | `done` 之后到达的 `error` 会把成功覆盖成失败。重写日志渲染时最容易碰坏这条 |
| 5 | **`/project/:code` 路由与 `/project/new` 的声明顺序颠倒** | React Router 按声明顺序匹配，`new` 会被当作 projectCode 吃掉 → 详情页显示「项目不存在」。`App.tsx` 现有注释已标注此陷阱，Task 1 改路由时必须保留该注释与顺序 |

---

## 文件结构

**新建：**
```
src/styles/base.css                 全局重置 + 跨页复用基元
src/styles/AppShell.css             topnav / page / pagefoot
src/styles/LoginPage.css            登录页
src/styles/NewProjectPage.css       五步向导步骤 1
src/styles/ProjectListPage.css      工作台卡片网格
src/styles/ProjectDetailPage.css    项目确认 6 面板
src/styles/ProjectSqlPanel.css      SQL 编辑器
src/styles/RelationshipPanel.css    数据管理
src/styles/BuildLogPanel.css        构建日志
src/styles/FrameworkListPage.css    技术架构管理
src/styles/SharedPages.css          原型无对应的两页复用基元
```

**修改：**（业务逻辑不变，只换 DOM + 样式）
```
src/App.tsx                         路由接线：/project/:code 换成 ProjectDetailPage；/ 改重定向
src/components/AppShell.tsx
src/pages/LoginPage.tsx
src/pages/NewProjectPage/index.tsx
src/pages/ProjectListPage/index.tsx
src/pages/ProjectDetailPage/index.tsx
src/pages/ProjectDetailPage/ProjectSqlPanel.tsx
src/pages/ProjectDetailPage/RelationshipPanel.tsx
src/lib/realtime/build-log-panel.tsx
src/pages/FrameworkListPage/index.tsx
src/pages/DisplayAttributesPage/index.tsx
src/pages/AccountPage/index.tsx
```

**删除：** `src/pages/ProjectPlaceholderPage/`（目录 + 整个文件，路由不再引用）

**不动：** `src/lib/**`（全部）、`src/generated/**`、`src/test-setup.ts`、`src/styles/tokens.css`、所有 `package.json` / 配置文件

---

### Task 1: 样式地基、AppShell 与路由接线

所有后续任务的依赖。必须先落地 `base.css` 的类名清单，后续任务才能引用。

**Files:**
- Create: `src/styles/base.css`
- Create: `src/styles/AppShell.css`
- Modify: `src/main.tsx`（在挂载前 import 两个新 CSS）
- Modify: `src/components/AppShell.tsx`
- Modify: `src/App.tsx:1-126`
- Delete: `src/pages/ProjectPlaceholderPage/index.tsx`
- Test: `src/components/AppShell.test.tsx`（改，不动用例数）
- Test: `src/App.test.tsx`（扩 1 条）
- Test: `src/lib/api/shtml-suffix.test.ts`（扩 1 条，钉死 Review Focus #1）

**Interfaces:**
- Consumes: `tokens.css` 的全部变量（`--bg` `--surface` `--fg` `--muted` `--border` `--accent` `--accent-soft` `--tip-bg` `--login-bg` `--radius` `--space-*` `--font-display` `--font-mono`）
- Produces: `base.css` 导出以下类，后续 9 个任务全部引用，**不得再新增未在此清单的跨页类**：
  `.container` `.page-head` `.pagefoot` `.panel` `.panel-head` `.panel-body`
  `.btn-primary` `.btn-ghost` `.btn-solid` `.btn-outline`
  `.form-row` `.form-foot` `.form-input` `.form-textarea` `.field` `.field-error` `.radio-group`
  `.stepper` `.step` `.tip-panel` `.data-table` `.table-card` `.row-actions` `.muted` `.page`
  `.section-title` `.empty-state`

- [ ] **Step 1: 写失败测试 —— 钉死 `.shtml` 前缀清单**（Review Focus #1）

在 `src/lib/api/shtml-suffix.test.ts` 追加用例：

```ts
it('API 前缀清单 SHALL 与后端 Controller 的 @RequestMapping 一致（新增前缀必须同步这里）', () => {
  // 断言源码级事实：shtml-suffix.ts 里的前缀数组恰好等于这 6 个
  expect(API_PREFIXES).toEqual([
    '/project', '/framework', '/displayAttribute', '/logs', '/login', '/account',
  ])
})
```

需要 `shtml-suffix.ts` 导出该常量（若尚未导出，导出它 —— 这是唯一允许的改动）。

- [ ] **Step 2: 运行，确认新用例通过（现有行为）、其余 8 条仍绿**

Run: `npm test -- src/lib/api/shtml-suffix.test.ts`
Expected: PASS，9 cases。

- [ ] **Step 3: 写失败测试 —— 路由可达性与声明顺序**

在 `src/App.test.tsx` 追加两条：

```ts
it('路由 SHALL 把 /project/:code 接到 ProjectDetailPage（而非占位页）', () => {
  // MemoryRouter 渲染 App，已登录（setToken），访问 /project/abc
  // 断言：出现 ProjectDetailPage 的特征元素，且不出现占位页文案「项目不存在」
})

it('【顺序防线】/project/new SHALL 不被 /project/:code 吃掉', () => {
  // 已登录，访问 /project/new
  // 断言：渲染 NewProjectPage 的特征元素，且 NOT 出现「项目不存在」
  // 顺序颠倒时 "new" 会被当作 projectCode → 这条立刻红（Review Focus #5）
})
```

- [ ] **Step 4: 运行，确认它失败**

Run: `npm test -- src/App.test.tsx`
Expected: FAIL —— 当前 `/project/:code` 渲染 `ProjectPlaceholderPage`，找不到特征元素。

- [ ] **Step 5: 创建 `src/styles/base.css`**

从原型转录跨页复用类。数值**原样照抄，不要调**。来源：

- `.container` `.page-head` `.pagefoot` `.btn-primary` `.btn-ghost` `.form-row` `.form-foot` `.form-input` `.form-textarea` `.field` `.field-error` `.radio-group` `.stepper` `.step` `.tip-panel` → `new-project.html` 的 `<style>` 块
- `.panel` `.panel-head` `.panel-body` `.empty-state` `.section-title` → `project-review.html`
- `.data-table` `.table-card` `.row-actions` → `wizard-step3-models.html`
- `.btn-solid` `.btn-outline` → `build-log.html`

文件头注释必须写明：类名沿用原型原型不做重命名；数值是原型值，改动会影响逐页比对。

- [ ] **Step 6: 创建 `src/styles/AppShell.css`**

转录 `.topnav` `.topnav-inner` `.brand` `.topnav-spacer` `.page`。原型 `.topnav` 是 **sticky + `backdrop-filter: blur(12px)` + `border-bottom: 1px solid var(--accent)`**（现有 `tokens.css` 的 `.topnav` 是非 sticky 的白底实现 —— 本次以原型为准，改 `AppShell.css` 而**不改 `tokens.css`**）。

- [ ] **Step 7: 在 `src/main.tsx` import 两个新 CSS**

放在 `installAuthInterceptor()` / `installShtmlSuffixInterceptor()` 调用之前、任何 import 之后。

- [ ] **Step 8: 重写 `src/components/AppShell.tsx`**

DOM 对齐原型 topnav：

```tsx
<div>
  <header className="topnav">
    <div className="topnav-inner">
      <a href="#/project/list" className="brand">AI-CODE</a>
      <span className="topnav-spacer" />
      <AccountMenu />
    </div>
  </header>
  <main className="page">{children}</main>
  <footer className="pagefoot">© 2017 – AI-Code · code-gen.workbench</footer>
</div>
```

`<AccountMenu />` 必须保留（2 个现有用例守它）。brand 从 `<span>` 改为 `<a>`，目标 `/project/list`。

- [ ] **Step 9: 改 `src/App.tsx` 路由**

三处改动：
1. 第 7 行 import 换成 `import ProjectDetailPage from './pages/ProjectDetailPage'`
2. `/project/:code` 的 element 换成 `<ProjectDetailPage />`
3. `/` 的 element 换成按登录态重定向：未登录 `<Navigate to="/login" replace />`，已登录 `<Navigate to="/project/list" replace />`（用 `useAuth().isAuthenticated`）

**保留第 49-53 行那段顺序敏感注释**（Review Focus #5）。`/project/new` 必须仍在 `/project/:code` 之前。

- [ ] **Step 10: 删除 `src/pages/ProjectPlaceholderPage/index.tsx`**

删整个文件（该目录只有这一个文件，无测试文件）。

- [ ] **Step 11: 跑全量测试**

Run: `npm run typecheck && npm test`
Expected: PASS。注意 `ProjectDetailPage/index.test.tsx` 的 4 条用例现在首次通过路由可达性，但仍由 `MemoryRouter` 直接渲染组件，不经 `App.tsx` —— 不受影响。

- [ ] **Step 12: 提交**

```bash
git add src/styles/base.css src/styles/AppShell.css src/main.tsx \
        src/components/AppShell.tsx src/App.tsx src/App.test.tsx \
        src/lib/api/shtml-suffix.ts src/lib/api/shtml-suffix.test.ts
git rm -r --cached src/pages/ProjectPlaceholderPage 2>/dev/null || rm -rf src/pages/ProjectPlaceholderPage
git commit -m "refactor(web): 样式地基 + AppShell + 路由接上详情页

- base.css 抽跨页复用类，类名沿用原型原名
- topnav 改 sticky + 红色 hairline（对齐原型）
- /project/:code 从 M2 占位页换成 ProjectDetailPage（此前走不到）
- / 改为按登录态重定向"
```

---

### Task 2: LoginPage 还原

**Files:**
- Create: `src/styles/LoginPage.css`
- Modify: `src/pages/LoginPage.tsx`
- Test: `src/pages/LoginPage.test.tsx`（改选择器，用例数不变）

**Interfaces:**
- Consumes: `base.css` 的 `.btn-primary`；`tokens.css` 的 `--login-bg` `--fg-soft`
- Produces: 无下游依赖（叶子页）

- [ ] **Step 1: 记录现有 3 条用例的断言目标**

`默认 mock 返回有效 token 时写入 localStorage`、`返回空字符串时显示…`、`R.code !== '0000' …`。三条守的是行为，本次**不改断言语义**，只让选择器还能命中新 DOM。

- [ ] **Step 2: 创建 `src/styles/LoginPage.css`**

从 `login.html` 的 `<style>` 原样转录这 12 个类：
`.auth-stage` `.auth-card` `.auth-fields` `.input-wrap` `.auth-input` `.auth-row-between`
`.remember-row` `.link-register` `.auth-actions` `.auth-foot` `.pixel-cluster`

注意：`tokens.css` 里已有 `.login-stage`（旧的类名）和 `.login-card`。本文件用原型名 `.auth-stage` / `.auth-card`，两者并存不冲突。`.auth-stage` 的背景必须包含 `tokens.css` 已定义的三层网格 + `breathe` 动画。

- [ ] **Step 3: 重写 `src/pages/LoginPage.tsx` 的 JSX**

DOM 层级对齐 `login.html`：`<main class="auth-stage">` 内含三个 `.pixel-cluster`（`tl` `bl` `br`）与 `<form class="auth-card">`；卡内 `.auth-title` → `.auth-fields`（两个 `.field` + `.input-wrap` + 线性图标 + `.auth-input` + `.field-error`）→ `.auth-row-between`（`remember-row` checkbox）→ `.btn-primary` submit → `.auth-actions`（`.link-register`）→ `.auth-foot`。

**保留**：`useAuth().signIn(token)`、`api.signin({account, password})`、`unwrapSuccess`、空 token 视为失败、`state.from` 回跳、`submitBtn.disabled` + `loading` + 「正在登录」文案、9007 错误气泡。

- [ ] **Step 4: 改测试选择器**

3 条用例的选择器改为命中新 DOM 的 class（`auth-input`、`field-error`、`btn-primary`）。断言内容不变。

- [ ] **Step 5: 跑测试**

Run: `npm run typecheck && npm test -- src/pages/LoginPage.test.tsx`
Expected: PASS，3 cases。

- [ ] **Step 6: 提交**

```bash
git add src/styles/LoginPage.css src/pages/LoginPage.tsx src/pages/LoginPage.test.tsx
git commit -m "refactor(web): 登录页还原原型视觉

深蓝科技底 + 极简登录卡，三个像素簇背景，12 个 auth-* 类对齐 login.html。
行为逻辑（signin / 空 token 判失败 / state.from 回跳）未动。"
```

---

### Task 3: NewProjectPage 还原（五步向导步骤 1）

**Files:**
- Create: `src/styles/NewProjectPage.css`
- Modify: `src/pages/NewProjectPage/index.tsx`
- Test: `src/pages/NewProjectPage/index.test.tsx`（改选择器，用例数不变）

**Interfaces:**
- Consumes: `base.css` 的 `.stepper` `.step` `.tip-panel` `.form-row` `.form-input` `.form-textarea` `.radio-group` `.form-foot` `.btn-primary` `.btn-ghost` `.field-error`
- Produces: `.stepper` / `.step.active` / `.step.done` 的 DOM 结构 —— **Task 5 的详情页不复用 stepper**（原型 `project-review.html` 没有 stepper）

- [ ] **Step 1: 记录现有 9 条用例**

重点两条反向用例必须原样保留：
- `【反向】SHALL NOT 提交契约之外的参数（不污染请求体）` —— 靠 `Build4Params` 类型级完备性断言 + 测试侧字段集合比对
- `【反向】SHALL NOT 渲染 token 输入框（token 由页面自动取）`

- [ ] **Step 2: 创建 `src/styles/NewProjectPage.css`**

`base.css` 已有 `.stepper` `.step` `.form-row` 等；本文件只补**单页专属**的：
`.req`（必填红星）、`.hint`（label 右侧灰色提示）、`.label`

- [ ] **Step 3: 重写 JSX —— 顶部 stepper**

对齐 `new-project.html`：`<ol class="stepper" aria-label="新建项目 5 步">`，5 个 `<li class="step">`，**只有第 1 个是 `active`**，其余无修饰类。第 1 个是 `<a>`，其余 `<span>`（原型里未走到的步骤不可点）。

5 个 label 依次：项目信息 / sql 信息 / 数据管理 / 技术选定 / 仓库信息。

- [ ] **Step 4: 重写 JSX —— 操作提示面板**

`.tip-panel` + `.tip-title`（操作提示）+ `<ul>`，4 条提示文案照抄 `new-project.html`：

- 项目英文名将作为数据库名使用，请遵循命名规范避免失败
- 版权文字将注释在文件头部
- 项目基础包名范例：com.rzhk 的格式，结尾不要有「.」点出现
- 项目描述仅描述生成任务内容，不会参与代码生成任务

- [ ] **Step 5: 重写 JSX —— 9 个表单字段**

对齐原型的 `.form-row` 结构：`<label class="label">`（含 `.req` 红星 + 可选 `.hint`）左侧，`.field` + `.form-input` / `.form-textarea` / `.radio-group` 右侧。字段顺序：项目名 → 项目英文名 → 数据库类型 → 项目版权文字信息 → 代码作者名 → 代码作者联系方式 → 项目语言 → 项目基础包名 → 项目描述。

hint 文案照抄原型（如「中文 / 英文皆可，将显示在工作台列表」「将作为数据库名使用」）。

- [ ] **Step 6: 改测试选择器，断言不变**

9 条用例的选择器改为命中新 class。**类型级完备性断言与两条反向用例一字不改。**

- [ ] **Step 7: 跑测试**

Run: `npm run typecheck && npm test -- src/pages/NewProjectPage/index.test.tsx`
Expected: PASS，9 cases。

- [ ] **Step 8: 提交**

```bash
git add src/styles/NewProjectPage.css src/pages/NewProjectPage/index.tsx src/pages/NewProjectPage/index.test.tsx
git commit -m "refactor(web): 新建项目页还原五步向导步骤 1

stepper + 红色操作提示面板 + 9 字段左标签右输入。
两条反向用例（不提交契约外参数、不渲染 token 框）原样保留。"
```

---

### Task 4: ProjectListPage 还原（卡片网格）

视觉变化最大的一页：从 7 列表格改成 `.card-new` + `.proj` 卡片网格。**逻辑最敏感的一页** —— `buildingCodes` / `hasShownRef` / 首个终态释放。

**Files:**
- Create: `src/styles/ProjectListPage.css`
- Modify: `src/pages/ProjectListPage/index.tsx`
- Test: `src/pages/ProjectListPage/index.test.tsx`（改选择器，用例数不变；**新增 2 条见 Step 4**）

**Interfaces:**
- Consumes: `base.css` 的 `.container` `.page-head` `.btn-primary`；**已存在的** `BuildLogPanel`（Task 8 会重塑其内部，但本任务只渲染不改动它）
- Produces: `.project-grid`（`repeat(4, 1fr)`，`aspect-ratio: 16/9`）、`.card-new`、`.proj`

- [ ] **Step 1: 记录 12 条现有用例**

必须保住的两组：
- 释放路径：`收到 done 终态后该行 SHALL 释放「构建中」`、`收到 error 终态时面板保留，按钮仍禁用`、`翻回正在构建的行 SHALL NOT 误释放`
- 防重复：`翻回已构建页 SHALL NOT 重复调 execute`
- 跨页保持：`buildingCodes 跨翻页保留`

- [ ] **Step 2: 创建 `src/styles/ProjectListPage.css`**

从 `dashboard.html` 原样转录：`.project-grid` `.card-new`（含 `.card-new .plus` 与 `:hover`）`.proj`（含 `:hover` 的 `translateY(-1px)` + shadow）`.proj .tag-row` `.tag` `.proj h3` `.proj .id` `.proj .meta-row` `.proj .meta-row .status` 及其 `::before` 圆点、`.status.draft::before`、`.status.building::before`（含 `@keyframes pulse`）、`.toolbar` `.tabs` `.search`

`.proj .meta-row .status::before` 是「已生成=绿 / 草稿=黄 / 生成中=红色脉冲」的圆点，颜色值照抄。

- [ ] **Step 3: 重写 JSX —— 工具栏**

`.page-head`（标题「我的项目」+ 副文案 + `.btn-primary` 新建项目）→ `.toolbar`（`.tabs` 四个：`全部` / `最近编辑` / `已生成` / `草稿`，role=tablist；右侧 `.search` 带放大镜图标 + `type="search"` input）。

tab 点击行为**保持现状**：只切 `aria-selected`，不做真筛选。

- [ ] **Step 4: 重写 JSX —— 卡片网格 + 写两条新测试**

`.project-grid` 内首个是 `.card-new`（加号 + 「创建新项目」，链接到 `/project/new`），其后每个项目一张 `.proj`：

```
.tag-row  → .tag.lang-java|js|py（语言）+ .tag.db（数据库）
h3        → 「订单中台 · order-hub」
.id       → com.rzhk.order（mono 字体）
.meta-row → 相对时间 + .status（已生成 / 生成中 / 草稿）
```

**新增 2 条测试**：

```ts
it('空 records SHALL 显示卡片网格空态提示而非一片空白', () => {
  // records: [] → 断言 .card-new 仍在，且有明确的空态文本
})

it('buildingCodes SHALL 以 project code 为键而非行索引（翻页后不贴错行）', () => {
  // 翻到第 2 页 → 断言第 1 页那个项目的卡片在翻回后仍带「构建中」态
  // 若实现改成按索引存，这条会红
})
```

第二条是 Review Focus #3 的守护。

- [ ] **Step 5: 改 12 条现有用例的选择器，断言不变**

表格语义（`默认 mock 命中后渲染表格行` 等）改为卡片语义时，**断言的业务事实保持不变** —— 例如「渲染表格行」改为「渲染对应项目的 `.proj` 卡片」。

- [ ] **Step 6: 跑测试**

Run: `npm run typecheck && npm test -- src/pages/ProjectListPage/index.test.tsx`
Expected: PASS，14 cases（12 改 + 2 新）。

- [ ] **Step 7: 反向验证一次**

注释掉「翻回已构建页 SHALL NOT 重复调 execute」的断言体 → 跑测试 → 确认变红 → 恢复。

- [ ] **Step 8: 提交**

```bash
git add src/styles/ProjectListPage.css src/pages/ProjectListPage/index.tsx src/pages/ProjectListPage/index.test.tsx
git commit -m "refactor(web): 工作台还原为卡片网格

7 列表格 → .card-new + .proj 卡片网格，状态点沿用原型三色。
新增两条测试：空态提示、buildingCodes 以 code 为键（翻页不贴错行）。"
```

---

### Task 5: ProjectDetailPage 还原（6 面板）

**Files:**
- Create: `src/styles/ProjectDetailPage.css`
- Modify: `src/pages/ProjectDetailPage/index.tsx`
- Test: `src/pages/ProjectDetailPage/index.test.tsx`（改选择器；**新增 2 条见 Step 5**）

**Interfaces:**
- Consumes: `base.css` 的 `.panel` `.panel-head` `.panel-body` `.badge` `.empty-state` `.grid-2`；`tokens.css` 的 `--tip-bg`（`.sql-block` 底色）
- Produces: 供 Task 6 / Task 7 的 `ProjectSqlPanel` 与 `RelationshipPanel` 作为 slot 嵌入 `.panel-body`

- [ ] **Step 1: 记录现有 4 条用例**

`默认 mock 命中后渲染：12 字段（ProjectDetailVO）testid 全在`、`R.data === null 走业务空态`、`R.code !== '0000' …`、`反向验证：后端直返裸 ProjectDetailVO`。**4 条的断言语义全部不变**，尤其那条反向验证。

- [ ] **Step 2: 创建 `src/styles/ProjectDetailPage.css`**

从 `project-review.html` 原样转录：`.panel` `.panel-head` `.panel-body` `.grid-2` `.badge`（`.badge.ok` / `.badge.pending`）`.empty-state` `.sql-block`（含 `.kw` 关键字色 / `.ty` 类型色 / `.str` 字符串色）`.tech-line` `.build-bar` `.btn-build`

`.panel-head` 内含 `.title` 左侧与 `.right`（badge + `a.edit` 「编辑 →」）右侧。

- [ ] **Step 3: 重写 JSX —— 6 个面板骨架**

对齐 `project-review.html`，每个面板结构：

```
<section class="panel">
  <div class="panel-head">
    <span class="title">面板名</span>
    <span class="right"><span class="badge ok|pending">已配置|待配置</span><a class="edit">编辑 →</a></span>
  </div>
  <div class="panel-body">…</div>
</section>
```

六个面板与状态：

| 面板 | 状态 | body 内容 | 「编辑 →」指向 |
|---|---|---|---|
| 项目信息 | `.badge.ok` | `.grid-2` 8 个 `.field`（k/v）：项目名 / 项目英文名 / 数据库类型 / 项目版权信息 / 作者 / 作者联系方式 / 项目描述 / SQL 类型 | `/project/new` |
| SQL 信息 | `.badge.ok` | `.sql-block`（`<ProjectSqlPanel/>` 嵌入） | `/project/:code`（无跳转，仅 badge） |
| 数据管理 | 取决于是否配了关系 | `.grid-2` 或 `.empty-state` | 同上 |
| 技术信息 | **`.badge.pending`** | **`.empty-state`，文案「接口待修复，验收后接入」** | 不渲染 `.edit` |
| 仓库信息 | **`.badge.pending`** | **`.empty-state`，同上** | 不渲染 `.edit` |
| 构建历史 | — | 表格（`序号/时间/状态/操作`），空时「暂无数据」 | — |

**项目信息面板的 8 个字段必须全部来自 `ProjectDetailVO`**，不得新造字段、不得回落到 `ProjectVO` 的 22 字段（这是 M3-C 定的 VO 投影边界，`ProjectDetailPage/index.test.tsx` 的反向验证守着）。

⚠️ **`ProjectSqlPanel` 与 `RelationshipPanel` 当前已在被渲染**（`index.tsx` 第 187、189 行，紧跟 5 个 `<dl>` 分组之后）。重组为 6 面板时：

- `<ProjectSqlPanel projectCode={code} />` 移入**「SQL 信息」**面板的 `.panel-body`
- `<RelationshipPanel projectCode={code} />` 移入**「数据管理」**面板的 `.panel-body`

两个面板的 `projectCode` prop **一个都不能丢** —— Task 6 / Task 7 只改它们的内部 DOM 与样式，不改 props 签名。

- [ ] **Step 4: 重写 JSX —— 底部构建条**

`.build-bar` 内 `.btn-build`「构建项目」。点击 → `api.execute({code})` → 挂 `BuildLogPanel`。按钮在构建期间禁用并显示「正在构建…」。

- [ ] **Step 5: 新增 2 条测试**

```ts
it('技术信息与仓库信息面板 SHALL 渲染为 pending 态并标注接口待修复', () => {
  // 断言两个 .badge.pending 都在
  // 断言两个面板的 .panel-body 都含「接口待修复」字样
})

it('项目信息面板 SHALL 只用 ProjectDetailVO 的 12 字段（不得出现 ProjectVO 的裁切字段）', () => {
  // 注入 ProjectDetailVO 全集 → 断言 8 个展示字段 testid 在
  // 断言 ProjectVO 独有的 10 个裁切字段 testid 任何一个都不在
})
```

第二条是 M3-C 裁切边界的守护，防止重写时「顺手」改成 22 字段。

- [ ] **Step 6: 改 4 条现有用例的选择器，断言不变**

- [ ] **Step 7: 跑测试**

Run: `npm run typecheck && npm test -- src/pages/ProjectDetailPage/index.test.tsx`
Expected: PASS，6 cases（4 改 + 2 新）。

- [ ] **Step 8: 反向验证一次**

临时把「项目信息」面板的 `.grid-2` 换成 `.panel-body` 直排（破坏布局断言）→ 确认测试红 → 恢复。

- [ ] **Step 9: 提交**

```bash
git add src/styles/ProjectDetailPage.css src/pages/ProjectDetailPage/index.tsx src/pages/ProjectDetailPage/index.test.tsx
git commit -m "refactor(web): 项目确认页还原为 6 面板

项目信息/SQL/数据管理/技术信息/仓库信息/构建历史，各带配置状态徽章。
技术信息与仓库信息渲染为 pending + 接口待修复标注，等后端契约修复后接入。
新增测试守住 ProjectDetailVO 的 12 字段边界。"
```

---

### Task 6: ProjectSqlPanel 还原（SQL 编辑器）

**Files:**
- Create: `src/styles/ProjectSqlPanel.css`
- Modify: `src/pages/ProjectDetailPage/ProjectSqlPanel.tsx`
- Test: `src/pages/ProjectDetailPage/ProjectSqlPanel.test.tsx`（改选择器，用例数不变）

**Interfaces:**
- Consumes: `base.css` 的 `.btn-primary` `.btn-ghost`；`tokens.css` 的 `--font-mono`
- Produces: 无（叶子组件）

- [ ] **Step 1: 记录现有 10 条用例**

两条顺序 / 语义防线必须保住：
- `点「解析」SHALL 先连 ws 再调 init（顺序反了会丢首批进度消息）`
- `建库阶段（total=0）SHALL 显示不确定进度文案而非空进度条（D5）`

- [ ] **Step 2: 创建 `src/styles/ProjectSqlPanel.css`**

从 `wizard-step2-sql.html` 原样转录：`.editor-wrap` `.editor-bar` `.editor-bar .tab` `.dot.live` `.editor` `.gutter`

`.gutter` 是行号槽，`.editor` 是 `display: grid; grid-template-columns: auto 1fr`，左槽右 textarea。`.dot.live` 是文件名 tab 前的状态点（原型有呼吸动画）。

- [ ] **Step 3: 重写 JSX —— 编辑器外壳**

```
.editor-wrap
  .editor-bar → .tab(.dot.live + 文件名) + .meta(UTF-8 · N 行 · MySQL)
  .editor
    .gutter (行号 span × N)
    textarea (SQL 输入)
```

行号数量随内容行数动态计算（原型硬编码 28 行，实现必须动态 —— 否则用户输入超过 28 行就错位）。

- [ ] **Step 4: 重写 JSX —— 底部动作条与进度区**

`.form-foot`：左侧 `.btn-ghost`「上一步」，右侧 `.btn-primary`「初始化数据库并进入下一步」。

ws 进度区按 `useInitProgress` 的状态渲染：`total > 0` 显示进度条 + 「N/M（百分比）」；`total === 0` 显示不确定进度文案（**不得显示 0% 空进度条**）；`error` 时**保留进度区**并显示「已解析 N/M 张表后失败」。

- [ ] **Step 5: 改 10 条用例的选择器，断言不变**

- [ ] **Step 6: 跑测试**

Run: `npm run typecheck && npm test -- src/pages/ProjectDetailPage/ProjectSqlPanel.test.tsx`
Expected: PASS，10 cases。

- [ ] **Step 7: 提交**

```bash
git add src/styles/ProjectSqlPanel.css src/pages/ProjectDetailPage/ProjectSqlPanel.tsx src/pages/ProjectDetailPage/ProjectSqlPanel.test.tsx
git commit -m "refactor(web): SQL 编辑器还原原型

行号槽随内容动态计算（原型硬编码 28 行，实现不能照抄）。
先连 ws 再调 init 的顺序、total=0 的不确定进度、error 保留进度区三条语义未动。"
```

---

### Task 7: RelationshipPanel 还原（数据管理）

**Files:**
- Create: `src/styles/RelationshipPanel.css`
- Modify: `src/pages/ProjectDetailPage/RelationshipPanel.tsx`
- Test: `src/pages/ProjectDetailPage/RelationshipPanel.test.tsx`（改选择器，用例数不变）

**Interfaces:**
- Consumes: `base.css` 的 `.table-card` `.data-table` `.row-actions` `.section-title` `.form-row` `.btn-primary` `.btn-ghost`
- Produces: 无（叶子组件）

- [ ] **Step 1: 记录现有 11 条用例**

四条级联 / 基数防线必须保住：
- `改主表 SHALL 清空已选的主表字段`
- `改主表 SHALL NOT 清空从表与从字段`
- `一张表都没选时 SHALL 提示先选主表`
- `切到「一对一」SHALL 映射为 oneToOne=Y 且 oneToMany=N（不产生 Y/Y 非法组合）`
- `保存成功后 SHALL 重拉关系列表（build 是 upsert，本地追加会显示失效 code）`

- [ ] **Step 2: 创建 `src/styles/RelationshipPanel.css`**

从 `wizard-step3-models.html` 原样转录：`.table-card` `.data-table` `.row-actions` `.section-title`

原型该页的表格只有两列（表 / 操作），操作列内是「字段属性」「模型关系」两个链接。实现须在「字段属性」侧嵌入 `DisplayControl` 表单、在「模型关系」侧嵌入级联选择器 —— 但**保持 `.table-card` 的表格外壳**。

- [ ] **Step 3: 重写 JSX —— 顶部操作提示**

`.tip-panel`，2 条文案照抄 `wizard-step3-models.html`：

- 表模型之间的不能同时满足两种关系，修改关系时，请先取消原来的关系
- 修改字段属性时请及时保存，切换表将不会保留当前表的修改

- [ ] **Step 4: 重写 JSX —— 表格与两组级联**

`.section-title`（「数据库模型」）→ `.table-card` → `table.data-table`（`<th>` 表 / 操作）→ 每行一个 `<tr>`：`<span class="name">` 表名 + `.row-actions` 两个操作入口。

展开「字段属性」→ `DisplayControl` 列表（复用 `src/lib/display-controls`，**不改**）
展开「模型关系」→ 两组级联（主表→主表字段 / 从表→从表字段）+ 基数单选 + 提交

**保持**：`listMapClassTable` / `listMapFieldColumn` 一次请求喂两个下拉、基数映射、upsert 后重拉、本地 code→tableName 映射回落 `associateCode`。

- [ ] **Step 5: 改 11 条用例的选择器，断言不变**

- [ ] **Step 6: 跑测试**

Run: `npm run typecheck && npm test -- src/pages/ProjectDetailPage/RelationshipPanel.test.tsx`
Expected: PASS，11 cases。

- [ ] **Step 7: 提交**

```bash
git add src/styles/RelationshipPanel.css src/pages/ProjectDetailPage/RelationshipPanel.tsx src/pages/ProjectDetailPage/RelationshipPanel.test.tsx
git commit -m "refactor(web): 数据管理页还原原型表格

table-card + data-table 外壳，两组级联与基数映射语义未动。
upsert 后重拉、本地表名映射回落 associateCode 两条仍在。"
```

---

### Task 8: BuildLogPanel 还原（日志窗）

**Files:**
- Create: `src/styles/BuildLogPanel.css`
- Modify: `src/lib/realtime/build-log-panel.tsx`
- Test: `src/lib/realtime/build-log-panel.test.tsx`（改选择器，用例数不变）

**Interfaces:**
- Consumes: `useBuildLog`（**不改**）、`progress-event`（**不改**）
- Produces: `.ln-error` 类（本次新增，见 Step 2）

- [ ] **Step 1: 记录现有 18 条用例**

三组必须保住：
- 状态机：`idle 状态不渲染任何 DOM` / `done 状态 SHALL 自动收起` / `error 状态 SHALL 保留面板`
- `onClosed` 触发矩阵（7 条，含 `【关键防线】初始就 idle SHALL NOT 触发 onClosed`）
- 阶段区：`stage 有值时 SHALL 显示「第 N/M 步」` / `stage 为 null SHALL NOT 显示阶段区（不占位）` / `stage.total 为 0 时只显示文案不显示进度条`

- [ ] **Step 2: 创建 `src/styles/BuildLogPanel.css`**

从 `build-log.html` 原样转录：`.log-wrap` `.log-head`（含 `.dot` `.name` `.status`）`.log-body` `.ln` `.ln-event` `.ln-ok` `.ln-warn` `.ln-sql` `.ln-done` `.ln-link` `.footer-bar`

**新增 `.ln-error`**：原型没定义（原型只有 success 态），但实现有 error 态。用比 `.ln-warn` 更重的品牌红 + 浅红底。文件头注释写明「`.ln-error` 为本次新增，原型无对应，因原型的错误态靠面板顶部 ❌ 徽章表达」。

- [ ] **Step 3: 重写 JSX —— 日志窗**

```
.log-wrap
  .log-head → .dot + .name(build · {englishName}) + .status(running 打转 / done ✓ Finished: SUCCESS / error ❌)
  .log-body → 每条 entry 一个 .ln，按类型加 .ln-event / .ln-ok / .ln-warn / .ln-error / .ln-sql / .ln-done
              末尾 .ln-done「# Finished: SUCCESS」
              .ln-link 内含可点 <a>（ZIP 下载 / 仓库地址）
  阶段区 → stage 非 null 时显示「第 N/M 步：<步骤名>」+ 进度条（total=0 时不显示进度条）
```

- [ ] **Step 4: 重写 JSX —— 底部动作条**

`.footer-bar`：左侧 `.btn-ghost`「返回工作台」（→ `/project/list`），右侧 `.btn-solid`「查看生成的文件 →」。

⚠️ 「查看生成的文件」的目标页 `generated-files` **本次不实现**（无目录树端点）。该按钮渲染完整视觉但 `disabled` + `title="接口待修复，验收后接入"`，**不绑 onClick**（spec §5 死按钮规则）。Task 4 的「返回工作台」同款。

- [ ] **Step 5: 改 18 条用例的选择器，断言不变**

- [ ] **Step 6: 跑测试**

Run: `npm run typecheck && npm test -- src/lib/realtime/build-log-panel.test.tsx src/lib/realtime/use-build-log.test.tsx`
Expected: PASS，18 + 15 cases。

**必须同时跑 `use-build-log.test.tsx`** —— Review Focus #4「首个终态权威」的逻辑住在 `use-build-log.tsx`，本任务不改它，但若 Task 8 误改了 `build-log-panel.tsx` 与它的 props 契约，这 15 条会红。不要跳过。

- [ ] **Step 7: 反向验证一次**

把 `stage != null` 的渲染条件改成无条件渲染 → 跑测试 → 确认 `stage 为 null 时 SHALL NOT 显示阶段区（不占位）` 变红 → 恢复。

- [ ] **Step 8: 提交**

```bash
git add src/styles/BuildLogPanel.css src/lib/realtime/build-log-panel.tsx src/lib/realtime/build-log-panel.test.tsx
git commit -m "refactor(web): 构建日志面板还原原型

log-wrap + 分色 .ln-* 日志行 + 底部动作条。
新增 .ln-error（原型无对应，原型的错误态靠顶部徽章表达）。
「查看生成的文件」按钮 disabled + 接口待修复 —— generated-files 无目录树端点。"
```

---

### Task 9: FrameworkListPage 还原（技术架构管理）

唯一需要「部分可用」的页面：`modify` 与 `updateIsPublic` 能用，`build` / `delete` 契约坏掉。

**Files:**
- Create: `src/styles/FrameworkListPage.css`
- Modify: `src/pages/FrameworkListPage/index.tsx`
- Test: `src/pages/FrameworkListPage/index.test.tsx`（改选择器；**新增 4 条见 Step 5**）

**Interfaces:**
- Consumes: `base.css` 的 `.page-head` `.panel` `.panel-head` `.data-table` `.btn-primary` `.btn-ghost`；`api.modify1` / `api.updateIsPublic1` / `api.list4`
- Produces: `.toggle` `.modal` `.pager` `.pages` `.table-wrap` `.row-actions` 类

- [ ] **Step 1: 记录现有 5 条用例**

`成功渲染：表格可见 code / name / description / 中文「公开」+ 分页信息` / `records 为空时显示业务空态，不显示错误气泡` / `R.code !== '0000' …` / `点击「下一页」SHALL 重新拉取并替换表格内容（首页「上一页」禁用）` / `末页「下一页」SHALL 禁用；禁用态点击 SHALL NOT 触发新请求`

- [ ] **Step 2: 创建 `src/styles/FrameworkListPage.css`**

从 `tech-architecture.html` 原样转录：`.page-head` `.panel` `.panel-head` `.table-wrap` `.toggle`（含 `.toggle::after` 滑块 + `:hover`）`.row-actions`（含 `.edit` `.del` 着色）`.pager` `.pager .pages` `.pager .pages button`（含 `.current`）`.modal-mask`（含 `.open`）`.modal` `.modal-head` `.modal-body` `.modal-foot` `.form-field`（含 `.invalid` `.err`）

- [ ] **Step 3: 重写 JSX —— 7 列表格**

列：序号（固定宽 64px）/ 技术名称 / 模板仓库地址（`.mono` 等宽字体）/ 模板仓库账户 / 是否公开库（`.toggle`）/ 技术描述 / 操作（`.row-actions`）

面板头：`.panel-head` 含标题「已注册的技术」+ 副文案（共 N 项 · 最后更新于 …）。

- [ ] **Step 4: 重写 JSX —— 添加 / 删除 / 编辑 / toggle**

| 元素 | 行为 |
|---|---|
| `.btn-primary`「添加」（`.page-head` 右侧） | `disabled` + `title="接口待修复，验收后接入"`。**不绑 onClick** |
| 行内 `.row-actions .del` 删除 | `disabled` + 同款 title。**不绑 onClick** |
| 行内 `.row-actions .edit` 编辑 | **可用** —— 打开 `.modal-mask.open` + `.modal`，复用原型 5 字段表单（技术名称 / 模板仓库地址 / 模板仓库账户 / 是否公开库 / 技术描述），提交调 `api.modify1`。必填校验用 `.form-field.invalid` + `.err` |
| `.toggle` 是否公开 | **可用** —— 点击调 `api.updateIsPublic1({code, isPublic})` |
| `.pager` 分页 | **可用** —— `api.list4({curPage, pageSize})` |

`.modal` 结构完整保留（即使「添加」按钮 disabled），供 Task 后续接 `framework/build`。

**响应不得出现 `password`** —— `FrameworkSummaryVO` 已无该字段，但实现**不得**因此新增请求去取密码填进表单（ApiContractIT 场景⑩ 守着）。

- [ ] **Step 5: 新增 4 条测试**

```ts
it('「添加」按钮 SHALL 渲染完整视觉但 disabled 且 title 标注接口待修复', () => {
  // 断言按钮可见、未 disabled=false、title 含「接口待修复」
  // 点击它 SHALL NOT 产生任何网络请求
})

it('行内「删除」按钮 SHALL 同样 disabled 且点击不发请求', () => { /* 同上 */ })

it('「编辑」SHALL 打开弹窗并带出该行现值，提交走 modify1', () => {
  // 点 .row-actions .edit → .modal-mask 拿到 .open
  // 断言输入框预填该行的 name / gitHome / account / description
  // 改值后提交 → 断言发出 modify1 请求且带上新值
})

it('「是否公开」toggle SHALL 调 updateIsPublic1 并按响应刷新', () => {
  // 点 .toggle → 断言请求带 code 与翻转后的 isPublic
})
```

- [ ] **Step 6: 改 5 条现有用例的选择器，断言不变**

- [ ] **Step 7: 跑测试**

Run: `npm run typecheck && npm test -- src/pages/FrameworkListPage/index.test.tsx`
Expected: PASS，9 cases（5 改 + 4 新）。

- [ ] **Step 8: 提交**

```bash
git add src/styles/FrameworkListPage.css src/pages/FrameworkListPage/index.tsx src/pages/FrameworkListPage/index.test.tsx
git commit -m "refactor(web): 技术架构页还原为 7 列表格

只读 → 可编辑：modify1（弹窗）与 updateIsPublic1（toggle）接通。
build / delete 的契约因 @Parameter(hidden=true) 缺入参，按钮 disabled + 接口待修复标注。
新增 4 条测试，含两条「死按钮不发请求」。"
```

---

### Task 10: 原型无对应的两页统一到基元

`DisplayAttributesPage`（10 用例）与 `AccountPage`（3 用例）在原型 12 页里没有对应页面。
**不重新设计**（无基准），但必须复用 `base.css` 基元，否则应用内会出现一个视觉上「没跟上」的页面。

**Files:**
- Create: `src/styles/SharedPages.css`
- Modify: `src/pages/DisplayAttributesPage/index.tsx`
- Modify: `src/pages/DisplayAttributesPage/DisplayAttributeForm.tsx`
- Modify: `src/pages/AccountPage/index.tsx`
- Test: `src/pages/DisplayAttributesPage/index.test.tsx`（改选择器，用例数不变）
- Test: `src/pages/AccountPage/index.test.tsx`（改选择器，用例数不变）

**Interfaces:**
- Consumes: `base.css` 全部；`display-controls`（**不改**）；`useCurrentAccount`（**不改**）
- Produces: 无（叶子页）

- [ ] **Step 1: 创建 `src/styles/SharedPages.css`**

只用 `base.css` 已有的类组合，**不新增类**。文件头注释写明「原型无对应页面，本文件仅保证与重写页共享基元观感，不做独立设计」。

- [ ] **Step 2: 重写 DisplayAttributesPage JSX**

外壳换成 `.panel` + `.panel-head` + `.panel-body`，内部沿用 `.data-table` + `.row-actions`。字段编辑表单沿用现有 `DisplayAttributeForm` 的 8 个控件与 `DisplayControl` 列表。

**必须保住的两条**：
- `displayType 下拉选项 SHALL 等于契约枚举全集（24 个，不手写副本）`
- `无字段时 SHALL 给出引导空态而非空白表单区`

- [ ] **Step 3: 重写 AccountPage JSX**

外壳换成 `.panel` + `.grid-2`（3 个 `.field`：id / code / account）。
**必须保住**：`ready 状态渲染 3 字段 + 不含 password testid`。

- [ ] **Step 4: 改 13 条用例的选择器，断言不变**

- [ ] **Step 5: 跑测试**

Run: `npm run typecheck && npm test -- src/pages/DisplayAttributesPage src/pages/AccountPage`
Expected: PASS，13 cases。

- [ ] **Step 6: 提交**

```bash
git add src/styles/SharedPages.css src/pages/DisplayAttributesPage src/pages/AccountPage
git commit -m "refactor(web): 显示属性与账户页统一到 base.css 基元

原型无对应页面，不做独立设计，只保证与重写页共享 topnav/面板/表格观感。
24 个 displayType 枚举全集、password 不外泄两条断言未动。"
```

---

### Task 11: 全量验证与反向验证

**Files:**
- Modify: `aicode-web/README.md`（补本次的样式架构约定）
- Verify: 全仓

**Interfaces:**
- Consumes: Task 1-10 的全部产物
- Produces: 验收所需的证据

- [ ] **Step 1: 全量类型检查与测试**

Run: `cd aicode-web && npm run typecheck && npm test`
Expected: PASS，**用例总数 ≥ 177**（原 169 + Task 4 的 2 条 + Task 5 的 2 条 + Task 9 的 4 条 = 177；实现中若另加则更多）。0 failed。

- [ ] **Step 2: 确认没有回归到内联 style**

Run: `grep -rn "style={{" src/pages src/components src/lib --include=*.tsx | grep -v ".test."`
Expected: 只允许**动态值**（如进度条宽度百分比、日志行的 className 切换辅助）。任何用于布局的静态 `style={{ marginTop: 18 }}` 都是违规，必须移到对应 `.css`。

- [ ] **Step 3: 确认 `src/generated/` 与契约未被污染**

Run: `cd /Users/bruce/IdeaProjects/aicode && git status --short`
Expected: `contracts/openapi/openapi.yaml` 与 `src/generated/**` **不出现在变更列表**（本次未重新 generate）。

- [ ] **Step 4: 跑契约漂移守卫**

Run: `./aicode/gradlew :aicode-web:verifyApi`
Expected: PASS（无漂移）。

- [ ] **Step 5: 确认完整后端构建仍绿**

Run: `./aicode/gradlew :aicode-web:build`
Expected: PASS —— jar 内 `static/**` 布局正确。

- [ ] **Step 6: 反向验证 —— 全局确认新增断言不是假通过**

至少对 3 处各做一次「破坏 → 确认红 → 恢复」：

1. 注释掉 Task 4 新增的「buildingCodes 以 code 为键」断言体 → 确认红
2. 把 Task 5 的「项目信息只用 ProjectDetailVO 12 字段」断言改为查 `ProjectVO` 独有字段 → 确认红
3. 把 Task 9「添加按钮 disabled」改成 `disabled={false}` → 确认红

每处恢复后重跑，确认全绿。

- [ ] **Step 7: 更新 README 的样式架构章节**

在 `aicode-web/README.md` 补一节，说明：

- 三层样式架构（`tokens.css` 唯一 token 源 / `base.css` 跨页基元 / 每页一个 `.css`）
- 类名沿用 OpenDesign 原型原名，**不得重命名**（逐页比对是验收手段）
- 新增类必须先加到 `base.css` 的基元清单，不得在页面里新造跨页类
- 死按钮规则：`disabled` + `title` 标注接口待修复，不绑 `onClick`
- 原型目录路径（供后续转录 CSS 用）

- [ ] **Step 8: 提交**

```bash
git add aicode-web/README.md
git commit -m "docs(web): 记录样式架构与死按钮约定

三层样式、类名沿用原型原名、基元清单、接口待修复标注规则。"
```

---

## 验收清单（供人工比对）

逐页与 OpenDesign 原型并排比对：

| 路由 | 原型文件 | 状态 |
|---|---|---|
| `#/login` | `login.html` | 需完全可用 |
| `#/project/list` | `dashboard.html` | 需完全可用 |
| `#/project/new` | `new-project.html` | 需完全可用 |
| `#/project/:code` | `project-review.html` | 项目信息/SQL/数据管理可用；技术信息/仓库信息 pending |
| `#/frameworks` | `tech-architecture.html` | 列表/分页/编辑/toggle 可用；添加/删除 disabled |
| 详情页内 SQL 面板 | `wizard-step2-sql.html` | 需完全可用 |
| 详情页内关系面板 | `wizard-step3-models.html` | 需完全可用 |
| 构建日志面板 | `build-log.html` | 日志可用；「查看生成的文件」disabled |
| `#/displayAttributes` | 无（统一到基元） | 需完全可用 |
| `#/account/me` | 无（统一到基元） | 需完全可用 |

延后页面（本次不实现，等契约修复）：`wizard-step4-stack.html` `wizard-step5-repo.html`
`generated-files.html` `template-docs.html`