# Proposal

## Why

M0–M3-B 阶段已经把 token 持久化、`<RequireAuth>` 守卫、登录页接入都做好了，但**前端没有任何"当前登录账户"**的可视化：

- `AppShell` 顶部仅 `AI-CODE` brand + 空白 spacer，登录后用户看不到自己是谁；
- 没有账户详情页（要查自己账号信息只能从后端 SQL 走）；
- 现有 `/account/build|list|modify|delete` 端点都是 **管理面**（list 返回全账户、build 是注册流程、delete 是注销），**没有按 code 单条加载**——前端无法"以当前 token 拿当前账户"。

token claim 里含 `accountCode`（M2-2 已落地 `useAuth.token`），但前端**无 jwtutil 库**且不应承担解密责任。**唯一可靠路径是后端新增 `GET /account/load?code=` 端点**，让前端按 `accountCode` 调一次拿账户详情（含 `account` 字段 = "admin" 之类的展示名）。

按 brainstorming 选项 (a) "上导航 + 账户详情页"——本变更**只走前端 + 一条 R 包络端点**，不做大改动：

## What Changes

- **后端**（`aicode/src/main/java/com/aicode/account/ctrl/AccountController.java`）：
  - 新增 `GET /account/load?code=` 端点：返 `R<AccountPublicVO>`（**新增 VO**，3 字段 `id` / `code` / `account`，**剔除 `password` 字段**——profile 页面不能回显 hash）；空 code 路径走 `R.success()` 早返（M3-A-1 已落地 R 包络机制）。
  - 复用 `accountService.getByCode(code)` —— 服务层 `IService` 自带。
- **契约**：`OpenApiExportIT` 重导 `contracts/openapi/openapi.yaml`——`/account/load` schema 形如 `RAccountPublicVO`（springdoc 展开命名）。
- **前端生成物**：orval 重生 `accountPublicVO.ts` / `rAccountPublicVO.ts`；新增 `getAccountLoadMockHandler` 由 `getAccountMockHandler` 重生成同时补全（M0 阶段 `account` 端点 MSW 已存在但用旧 `R<Account>` 形态——本变更后 list 与 load 各自独立 mock）。
- **新能力 `aicode-web-account`**（`aicode-web/src/lib/account/`）：
  - `useCurrentAccount()` hook：调 `getAccountLoad({ code: accountCode })` 拿当前账户信息；返回 `{ account, code, id, isLoading, error, refresh }`；
  - 复用 M2-2 的 `useAuth()` 读 `accountCode`（不需要本地存——token claim 已经有）；
  - 复用 M2-2 的 `axios 拦截器` 注入 Authorization Bearer（与 M3-B 一致）。
- **`<AccountMenu>` 组件**（`aicode-web/src/components/AccountMenu.tsx`）：在 `AppShell` 顶部 spacer 之前渲染——`<UserBadge>` 圆形 + 下拉列表（"账户详情"链接 + "退出"按钮）。下拉点击账户名跳 `/account/me`；点退出调 `useAuth().signOut()` 后跳 `/login`。
- **`<AccountPage>` 组件**（`aicode-web/src/pages/AccountPage/index.tsx`）：新页面 `/account/me`，受 `<RequireAuth>` 守卫；调 `useCurrentAccount()` 渲染 3 字段（`code` / `account` / `id`）；加载 / 错误状态按 M2-2 样板的 3 状态机（同 `ProjectDetailPage` 形态）；含一个"返回列表"链接（指 `/`）。
- **`AppShell` 改造**：把 `<AccountMenu />` 挂在 `<span className="topnav-spacer" />` 之前；该组件自身在 `isAuthenticated === false` 时不渲染（保持"未登录页面无 UI 噪声"）。
- **`App.tsx` 路由**：新增 `/account/me` 路径 → 渲染 `<AccountPage />`（受 `<RequireAuth>` 包裹）。

**BREAKING**：无——只是新增端点与前端页面，不动现有任何端点形态或前端代码路径。

**M2-2 兼容性**：`<AccountMenu>` 与 M2-2 的 `<RequireAuth>` 协作：`<RequireAuth>` 已确保无 token 跳 `/login`——`<AccountMenu>` 自身加 isAuthenticated 守卫冗余但安全。

## Capabilities

### New Capabilities

- `aicode-web-account`：定义前端账户接入的可执行形状——`useCurrentAccount` hook 协同 `GET /account/load?code=`，UI 端 `<AccountMenu>` 上导航与 `<AccountPage>` 详情页都消费 `AccountPublicVO` 形态（**不**含 `password`）；M2-2 的 `useAuth()` 仍负责 token 生命周期，本能力不重复。

### Modified Capabilities

- `api-contract`：现有 Requirement "受保护 REST 接口 SHALL 走 R<T> 包装而非裸实体出参" 已覆盖本变更新增的 `/account/load` 端点——本变更**只是该 Requirement 覆盖范围的扩展**，不需新增 Scenario（按 M3-A-1 同一类反例钉死）。

## Impact

- **代码**：
  - 新增 `facade/src/main/java/com/aicode/account/vo/AccountPublicVO.java`（3 字段，无 `password`）
  - 修改 `aicode/src/main/java/com/aicode/account/ctrl/AccountController.java`：新增 `loadByCode` 端点
  - 新增 `aicode-web/src/lib/account/use-current-account.tsx`（hook）
  - 新增 `aicode-web/src/components/AccountMenu.tsx`（上导航）
  - 新增 `aicode-web/src/pages/AccountPage/index.tsx`（详情页）
  - 修改 `aicode-web/src/components/AppShell.tsx`（挂 AccountMenu）
  - 修改 `aicode-web/src/App.tsx`（注册 `/account/me` 路由）
- **契约产物**：`contracts/openapi/openapi.yaml` 由 `OpenApiExportIT` 自动刷新；orval 重生 `aicode-web/src/generated/`。
- **不影响**：`aicode-web/src/lib/auth/`（M2-2 已落地的 tokenStore / 拦截器 / useAuth 三件套）完全复用，不动。
- **风险**：
  - **password 字段泄漏防护**：`AccountPublicVO` 是新建 VO，**不**复用 `AccountVO`——后者含 `password`。`/account/load` 端点用 `AccountPublicVO` 装配，**绝不** `BeanUtils.copyProperties` 旧 `AccountVO` 形态。grep 钉这一前提。
  - **token 失活 / 越权防护**：`/account/load?code=` 用全局 `LoginInterceptor` 拦截（同 `/account/list`），无新增鉴权逻辑；前端 `accountCode` 从 `useAuth.token` 读，**不是**用户手动输入，**前端不可越权**读别人账户。
  - **多账户并发**：`accountService.getByCode` 走 MyBatis-Plus，事务隔离在 MySQL 默认级别（仓库未声明 `@Transactional`）—— 这是既有 list/build 端点同样状态，本变更**不引入新并发语义**。
- **测试**：
  - 后端 `ApiContractIT` 新增场景⑨ `AccountLoadReturnsAccountPublicVO`（断言 `data` 不含 `password` 字段 + `data.account === 'admin'`）；`/account/load` 反向验证：临时让 Controller 直接返 `AccountVO`（含 password） → 断言失败钉 4 字段超集。
  - 前端 `useCurrentAccount.test.tsx`：3 用例（成功 / 401 → useAuth 跳登录 / 500 → error）；`AccountPage.test.tsx`：3 用例（loading / ready+data / error）；`AppShell.test.tsx`（**新增**）：`isAuthenticated=true` 时含 `data-testid="account-menu"` / `false` 时不含。

