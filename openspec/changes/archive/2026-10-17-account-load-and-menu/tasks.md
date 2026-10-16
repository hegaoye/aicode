# Tasks

## 1. 后端 VO 与 Controller 改造

- [x] 1.1 新增 `facade/src/main/java/com/aicode/account/vo/AccountPublicVO.java`：3 字段 `id` / `code` / `account`，**不含** `password`；含 `@Data` 与 `@Schema(description=...)`；验证：`./aicode/gradlew :aicode:compileJava` exit=0
- [x] 1.2 修改 `aicode/src/main/java/com/aicode/account/ctrl/AccountController.java`：新增 `@GetMapping("/load") public R<AccountPublicVO> loadByCode(@RequestParam("code") String code)`；装配走 `BeanUtils.copyProperties(account, publicVo)` 仍合理（3 字段 target 不含 password setter，password 自动丢弃）；`code == null` 早返 `R.success()`；验证：`./aicode/gradlew :aicode:compileJava` exit=0
- [x] 1.3 全仓 grep `password` 引用确认 `AccountPublicVO` 与 `AccountVO` 隔离：执行 `git grep -n "AccountPublicVO\|AccountVO\|password" aicode/src facade/src aicode-web/src -- "*.java" "*.ts" "*.tsx" | grep -v "/generated/"`；确认前端 0 处 `password` 引用、`AccountPublicVO` 在 profile 端点上唯一使用
- [x] 1.4 反向验证：临时把 `loadByCode` 改返 `AccountVO`（含 `password` 字段）→ `ApiContractIT` 场景⑨ 断言 `data` 不含 `password` 字段失败；恢复后通过

## 2. 契约产物与前端重生成

- [x] 2.1 跑 `./aicode/gradlew :aicode:test --tests "*OpenApiExportIT"`；验证 `contracts/openapi/openapi.yaml` 中 `/account/load` schema 含 `AccountPublicVO` 与 `RAccountPublicVO`（springdoc 展开命名）；用 grep 验证文件包含 `AccountPublicVO:`
- [x] 2.2 跑 `cd aicode-web && npm run generate`；验证 `accountPublicVO.ts` 与 `rAccountPublicVO.ts` 生成出现；`api.ts` 内 `accountLoad` 函数返 `AxiosResponse<RAccountPublicVO>`（不带 `password` 字段）
- [x] 2.3 确认前端无 `AccountVO`（旧全字段含 password）引用：`git grep -n "AccountVO" aicode-web/src -- ':!*/generated/*'`

## 3. 前端能力落地

- [x] 3.1 新增 `aicode-web/src/lib/account/use-current-account.tsx`：导出 `useCurrentAccount()` hook；返回 `{ account, code, id, isLoading, error, refresh }`；mount 时调 `api.accountLoad({ code: getToken() })` 拿 R<AccountPublicVO>；401 不抛错（清 token）；验证：`npm run typecheck` 0 错
- [x] 3.2 新增 `aicode-web/src/lib/account/use-current-account.test.tsx`：3 用例 ① token 存在时成功 load + 返回 account/code/id ② 401 时 signOut 调 ③ 服务器 500 时 error 字段有值；用 `vi.mock('../../lib/realtime/ws-client' ...)` 不相关 + `vi.mock('../../generated/api')` 替身 `accountLoad`
- [x] 3.3 新增 `aicode-web/src/components/AccountMenu.tsx`：默认导出；`isAuthenticated === false` 时 `return null`；否则渲染 `<span data-testid="account-menu">` 包裹徽章 + 下拉（账户详情链接 + 退出按钮）；徽章显示 `useCurrentAccount().account`；验证：`npm run typecheck` 0 错
- [x] 3.4 新增 `aicode-web/src/pages/AccountPage/index.tsx`：默认导出；3 状态机 `loading | ready | error`；ready 状态渲染 3 字段（id / code / account）+ "返回列表"链接；含 1 个 `data-testid="account-field-password"` **绝不**出现的负断言（`expect(queryByTestId('account-field-password')).toBeNull()`）
- [x] 3.5 新增 `aicode-web/src/pages/AccountPage/index.test.tsx`：3 用例 ① loading 状态渲染"加载中…" ② ready 状态渲染 3 字段 + 不含 password testid ③ error 状态渲染 `<p data-testid="account-error">`；用 `vi.mock` 替身 `useCurrentAccount` 与 `useAuth`

## 4. AppShell 与 App.tsx 接入

- [x] 4.1 修改 `aicode-web/src/components/AppShell.tsx`：在 `<span className="topnav-spacer" />` **之前**插入 `<AccountMenu />`
- [x] 4.2 修改 `aicode-web/src/App.tsx`：新增 `<Route path="/account/me" element={<RequireAuth><AppShell><AccountPage /></AppShell></RequireAuth>} />`；import `AccountPage`
- [x] 4.3 新增 `aicode-web/src/components/AppShell.test.tsx`（**新文件**）：2 用例 ① `isAuthenticated=true` 时 `data-testid="account-menu"` 存在 ② `isAuthenticated=false` 时不含；用 `vi.mock('../../lib/auth/use-auth')` 替身

## 5. 验证与登记

- [x] 5.1 跑全套 `./aicode/gradlew :aicode:test`：预期 171+ 用例 / 0 失败（含 ApiContractIT 场景⑨ 1 用例 + 场景⑧ 反向验证 1 用例）
- [x] 5.2 跑 `npm run typecheck && npm test`：预期全 64+ 用例 / 0 失败（useCurrentAccount 3 + AccountMenu via AppShell 2 + AccountPage 3 = 8 新增）
- [x] 5.3 跑 `npm run build`：产物大小变化 < 2 KB
- [x] 5.4 修改 `AGENTS.md §11.3`：登记 M3-D 已合并（`/account/load?code=` 端点 + `aicode-web-account` 能力 + 上导航 + 详情页） *(已完成：M3-D 段就地写进 AGENTS.md §11.3 段尾，含 password 防护与设计偏差说明)*
- [x] 5.5 执行 `openspec validate --strict 2026-10-17-account-load-and-menu`：校验通过 *(已完成：`Change is valid`，0 错)*
- [x] 5.6 同步 delta spec 到 `openspec/specs/aicode-web-account`（新建能力）并 `openspec archive`；验证新能力物化（3 Requirements / 7 Scenarios）；`openspec list --specs` 含 `aicode-web-account`；12 → 13 specs *(已完成：主 spec 物化 3 Requirements（合并时校正 delta 文字："前端用 code 入参" → "前端不传 code，后端从 token 抽"，D2 设计偏差就地记录）；`openspec validate --specs` 13/13 通过；变更 mv 至 archive；`openspec list` No active changes)*
