# Design

## Context

M2-2 落地了 `aicode-web-auth` 三件套（tokenStore / auth-interceptor / useAuth）+ `<RequireAuth>` 守卫。token claim 含 `accountCode`（M2-2 落地，详见 `LoginCtrl` line 57：`JwtToken.createToken(Constants.AccountCode.val.toString(), accountObj.getCode())`）。`AppShell` 顶部无账户 UI，`/account` 现有 4 端点都是管理面（list 全表 / build 注册 / modify 改 / delete 删），**没有"按 code 单条加载"**端点。

## Goals / Non-Goals

**Goals:**

- 后端新增 `GET /account/load?code=` 端点：返 `R<AccountPublicVO>`（3 字段：`id` / `code` / `account`，**剔除 `password`**）
- 前端 `aicode-web-account` 新能力：hook + 组件 + 详情页
- 上导航 `<AccountMenu>` + 详情页 `<AccountPage>` 接入 `<AppShell>` 与 `App.tsx`
- 全程复用 M2-2 的 `useAuth`（token 持有） + 拦截器（Authorization Bearer）
- password 字段全程不暴露（前端 0 引用 + 后端 VO 不包含 + ApiContractIT 反向验证）

**Non-Goals:**

- 不动既有 `AccountVO`（含 `password` 字段仍被 build/list 端点用）
- 不引入 jwtutil 库到前端（依赖后端 `/account/load` 拿账户详情）
- 不改 `LoginCtrl` / token claim 形态
- 不做密码修改 UI（modify 端点已存在但本变更不动）
- 不做账户注册 UI（build 端点已存在）

## Decisions

### D1：新建 `AccountPublicVO` 不复用 `AccountVO`

**决定**：新建 `AccountPublicVO`（3 字段：`id` / `code` / `account`）用于 profile；不动既有 `AccountVO`（4 字段含 `password`）。

**理由**：

- 沿用 M3-C 的"VO 字段是前端实际消费集子集"原则：详情页消费 3 字段，不应收到 `password` 字段（即便前端忽略——hash 也泄漏给前端 bundle 风险）
- 既有 `AccountVO` 仍被 list / build / modify 端点用——list 需要 `password` 是历史遗留（list 不应回显 password hash，但前端 list 用 `password` 字段做什么未明），**不在本变更 scope 修**，与 M3-C 区分"详情/列表裁切"独立议题
- 避免 BeanUtils.copyProperties 把 password 字段意外带入新端点响应

**备选方案**：(a) 复用 `AccountVO` + Jackson `@JsonIgnore` password —— 改动全 4 端点，风险过大；(b) `AccountVO` 砍 password —— 影响 list/build 端点；(c) 新 `AccountPublicVO` —— 本变更采用。

### D2：token 中 accountCode 作为 `/account/load?code=` 入参

**决定**：前端从 `useAuth()` 拿 token → token claim 解析 accountCode → 调 `/account/load?code={accountCode}`。

**理由**：

- token claim 已有 `accountCode`（M2-2 落地）；不引入 jwtutil，**信任** token 内的 `accountCode`（这是 token 客户端的合法操作）
- `/account/load?code=` 端点受全局 `LoginInterceptor` 拦截（与其他 /account 端点同一组）—— 服务端再次校验 token 真实有效
- 双层防护：客户端用 token claim 拿 code（无 token 不能调），服务端用 token 校验 + 业务层 `accountService.getByCode`（无效 code 返 null）
- **不**接受用户手动输入 code——`/account/load?code=` 是受保护端点，前端只对当前登录账户自调用

**备选方案**：(a) 后端从 token 抽 accountCode 不接受 code 入参——后端会少一个端点但灵活性差（未来"管理员查任意账户"扩展时要改端点形态）；(b) JWT 抽 accountCode 限前端——前端无 jwt 库。本变更采用 D2 (a) 折中。

### D3：`useCurrentAccount` 4 状态归并 3 状态

**决定**：`useCurrentAccount` 返回 `{ account, code, id, isLoading, error, refresh }` 6 字段（无 status 字段），状态由 `isLoading` / `error` 派生。3 状态机 `loading | ready | error` 与 M2-2 `ProjectDetailPage` / M3-B `useBuildLog` 一致。

**理由**：

- `account` / `code` / `id` 为 data 派生字段（数据加载成功后即有）
- `isLoading` 覆盖 `loading` 状态，`error` 覆盖 `error` 状态
- 6 字段 < `useBuildLog` 的 5 字段（`status`+`entries`+`startedAt`+`startStream`+`clear`）—— 本能力更简单

**备选方案**：(a) 显式 `status: 'idle' | 'loading' | 'ready' | 'error'` —— 与 M2-2 模式镜像但当前 hooks 已习惯 2 个 boolean；(b) 二元 model —— 不够精确。本变更采用 (a) 折中。

### D4：`<AccountMenu>` 在 isAuthenticated === false 时返回 null

**决定**：`AppShell` mount 时若未登录，`<AccountMenu>` 不渲染任何 DOM。

**理由**：

- 未登录页面无 token / 无账户可言，渲染"用户徽章"是空 UI 噪声
- 配合 M2-2 `<RequireAuth>` 已把未登录用户跳到 `/login` ——`<AccountMenu>` 在 `/login` 页 mount 时是 null

**备选方案**：(a) 永远渲染一个"请登录"链接 —— 与 `<RequireAuth>` 跳登录逻辑冗余；(b) 永远渲染"账户"占位 —— UI 噪声。本变更采用 D4 (a)。

### D5：`AccountPage` mount 自动 load

**决定**：`<AccountPage>` mount 时调 `useCurrentAccount()`，hook 内部 useEffect 触发 load。**不**暴露手动 `load()` 方法。

**理由**：

- 详情页用户进入即看详情数据，不需要"刷新"按钮（`<AppShell>` 顶部徽章也带"账户详情"链接——那里刷新即可）
- M3-A-2 `ProjectDetailPage` 已是同样模式（mount 即 load）

**备选方案**：(a) 暴露 `load()` —— 与 M3-A-2 不一致；(b) 暴露 `refresh()` 隐藏在 hook 中——`<AccountPage>` 不传它。本变更采用 D5 (a) 折中。

## Risks / Trade-offs

**[password 泄漏防护]**：D1 已隔离 `AccountPublicVO`/`AccountVO` 两个 VO；task 1.x grep 钉"零引用 AccountVO 旧形态"前提；ApiContractIT 反向验证 (task 4.x) 临时让 Controller 返 AccountVO（含 password）→ 场景⑨ "data 不含 password" 失败。

**[accountCode 来自 token claim 的信任]**：token 是后端签发，客户端无法伪造；token 失活时 401 → 触发 useAuth signOut + RequireAuth 跳登录。这与 M2-2 既有 token 持有机制一致。

**[前端 jwtutil 缺位]**：未来若需要 token 携带更多 claim 给前端使用，要引入 jwtutil 库——本变更不引依赖（保持 devDependencies 不变）。

**[AppShell 在所有页面 mount 都跑 useCurrentAccount]**：M0 阶段就有"未登录页面无 UI 噪声"设计要求——本变更 D4 保证 isAuthenticated === false 时 `<AccountMenu>` 返 null。`<RequireAuth>` 在已 login 之前会跳走，未 login 页面 AppShell 仍 mount 但 useCurrentAccount hook 不发起 fetch（无 token 调 api）。

## Migration Plan

单批交付（一个 feature commit）：

1. **新增 `AccountPublicVO`**（3 字段）
2. **新增 `/account/load` 端点**（`AccountController.loadByCode`）
3. **新增 `aicode-web-account` 能力**（`useCurrentAccount` hook + 测）
4. **新增 `<AccountMenu>`**（上导航）与 `<AccountPage>`（详情页）
5. **改 `AppShell` 挂 `<AccountMenu>` + `App.tsx` 注册 `/account/me` 路由**
6. **`OpenApiExportIT` 重导契约**
7. **跑 `npm run generate` 重生前端生成物**
8. **跑全套测试**
9. **同步 delta spec → archive**

**回滚**：单 commit revert 即恢复；M2-2 鉴权能力 / M3-A-1 R 包络规则 / M3-C 字段裁切都不动。

## Open Questions

- 是否要支持"管理员在 accountPage 查任意 account"——本变更不接；`/account/list` 已存在是另一种入口
- 退出按钮是否要"登出 + 清空 localStorage 一切"——`useAuth().signOut()` 已清 token + 走 401 拦截器；其余 localStorage 暂无
- account 详情页是否要"修改密码"入口——本变更不动；modify 端点已存在
