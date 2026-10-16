# Tasks

## 1. 后端：`AccountController.list`

- [x] 1.1 返回类型 `PageVO<AccountVO>` → `R<PageVO<AccountPublicVO>>`；验证 `./aicode/gradlew :aicode:compileJava` exit=0 *(已完成：返回类型 PageVO<AccountVO> → R<PageVO<AccountPublicVO>>)*
- [x] 1.2 入参兜底：`curPage == null || curPage <= 0` → 1；`pageSize == null || pageSize <= 0` → 10（口径与 `0960cc0` 的 `/project/list` 完全一致，design D3）；验证无 `Integer` 拆箱 NPE（`new Page<>(...)` 收 `long`） *(已完成：effectiveCurPage/effectivePageSize 兜底（口径与 0960cc0 的 /project/list 一致）；compile exit=0，**实测消除拆箱 NPE**)*
- [x] 1.3 显式 `setCurPage` / `setPageSize` / `setTotalRow` / `setTotalPage`；`totalPage = (int) Math.ceil((double) totalRow / effectivePageSize)` *(已完成：显式 setCurPage/setPageSize/setTotalRow/setTotalPage；totalPage 用 ceil 显式算)*
- [x] 1.4 **去掉 `AccountPageVO` 中转层**（design D2）：`entity → AccountPublicVO` 一次 fastjson2 往返；断言 `AccountPageVO` 在本方法中不再出现 *(已完成：去掉 AccountPageVO 中转（entity → AccountPublicVO 一次 fastjson2 往返）；随之移除已无用的 AccountPageVO import。**保留 AccountPageVO 类本身**（facade 是契约模块，删类属破坏性变更）)*
- [x] 1.5 确认 `data.records[]` 元素**不含 `password`**（`AccountPublicVO` 只有 id / code / account） *(已完成：AccountPublicVO 只有 id/code/account，由场景⑭ 用例 1 实际调用守护)*

## 2. 后端：同 Controller 的另两个端点

- [x] 2.1 `AccountController.modify`：返回类型 `boolean` → `R<Boolean>`；把 `return isUpdated` 改为 `return R.success(isUpdated)` *(已完成：boolean → R<Boolean>；注释说明裸出参绕过 R 三元组)*
- [x] 2.2 `AccountController.delete`：去掉形参 `@Parameter(hidden = true)`（design D4 —— 契约里现在完全没有参数，前端无从发起删除）；实现逻辑一行不动 *(已完成：去掉 @Parameter(hidden = true)；实现逻辑一行未动。契约里 delete 现已有 parameters)*
- [x] 2.3 验证 `:aicode:compileJava` exit=0，且 **`AccountVO` 类本身保留**（仍是 `modify` / `delete` 的入参载体，design D1） *(通过：compile exit=0；AccountVO 类保留（modify/delete 仍引用，grep 命中 6 处）)*

## 3. 后端：契约守护（场景⑭）

- [x] 3.1 `ApiContractIT` 新增 `@Nested` 场景⑭ `AccountListSecurityAndPagination`： *(已完成：场景⑭ **6 用例**（比计划多 1 条 —— 拆成「包络 schema」「分页字段回填」「NPE 兜底」三条，各自失败时指向更精确）)*
  - **用例 1（安全）**：实际调用 `GET /account/list`，断言 `data.records[]` 每个元素的字段集合 **⊆** `{id, code, account}`，且**点名不含 `password`**（delta spec 的凭据防护）
  - **用例 2（包络）**：schema 层断言 —— 响应顶层 SHALL ref `RPageVOAccountPublicVO`，SHALL NOT ref 裸 `PageVOAccountVO`
  - **用例 3（分页 + NPE 兜底）**：实际**不带** `curPage` / `pageSize` 调用，断言 `R.success` 为 true、`curPage==1`、`pageSize==10`；再带 `curPage=1&pageSize=10` 调用，断言 `totalPage == ceil(totalRow/pageSize)`（`totalRow > 0` 时）
  - **用例 4**：`PUT /account/modify` 的 schema SHALL ref `RBoolean`，SHALL NOT 是顶层 `type: boolean`
  - **用例 5**：`DELETE /account/delete` 的契约 SHALL 导出参数（当前完全没有）
- [x] 3.2 **反向验证 A**：临时把 `list` 返回类型改回裸 `PageVO<AccountPublicVO>` → 场景⑭ 用例 2 变红 *(**成立**：改回裸 PageVO → 4 条变红（含 schema 断言「响应 SHALL 包入 R」）)*
- [x] 3.3 **反向验证 B**：临时把 `list` 的 `setCurPage` / `setPageSize` / `setTotalPage` 三行去掉 → 用例 3 变红（`actual=0`） *(**成立**：去掉三个 set → 2 条变红，错误精确报 `data.curPage ... 实际=0`)*
- [x] 3.4 **反向验证 C**：临时把 `AccountPublicVO` 加上 `password` 字段（并在装配里 set）→ 用例 1 变红 *(**成立**：给 AccountPublicVO 加回 password → 用例 1 变红，错误精确列出 `[account, code, id, password]`)*

## 4. 契约产物 + 前端生成物

- [x] 4.1 跑 `npm run generate`；确认 `PageVOAccountVO` 已从契约消失、`RPageVOAccountPublicVO` 出现 *(已完成：契约里 PageVOAccountVO 残留 0 次、RPageVOAccountPublicVO 2 次)*
- [x] 4.2 清理 `src/generated/model/index.ts` 的 stale export（若 orval 未清 `pageVOAccountVO`） *(已完成：删除 stale 的 pageVOAccountVO.ts + index.ts 的 export 行；**accountVO.ts 保留**（modify 的 requestBody 仍用）)*
- [x] 4.3 跑 `npm run typecheck`：预期零报错（前端未消费该端点，若报错说明有隐藏消费者 —— 须查明） *(typecheck exit=0 —— **顺带证实前端确实未消费 /account/list**（若有隐藏消费者，改响应类型必然报错）)*
- [x] 4.4 跑 `npm test`：预期零改动全绿（前端本变更零改动） *(npm test 21 文件 / 132 用例全绿，零改动)*

## 5. 验收 + 文档 + 登记

- [x] 5.1 后端全套 `JAVA_HOME=$(/usr/libexec/java_home -v 25) ./aicode/gradlew test`，登记三模块用例数与失败数 *(实测：aicode 199（193 基线 + 场景⑭ 6）/ 2 跳过 / 0 失败；facade 39；common 56 —— 合计 294)*
- [x] 5.2 契约抖动判定：两版契约各自 `sort` 后 `diff`，用 `comm` 确认无行被「删了又加回」（AGENTS §10 已登记该方法） *(零抖动：删除 4 行 / 新增 21 行，comm 比对确认无行被「删了又加回」)*
- [x] 5.3 `openspec validate --strict 2026-10-18-account-list-security-and-pagination` 与 `--specs` *(openspec validate --strict 与 --specs 均通过（13/13）)*
- [x] 5.4 **`AGENTS.md §11.1`**：补本条泄漏事件的运维建议 —— **轮换全部账户密码** + 检查反向代理访问日志中已落盘的 MD5；并注明「存量的已落盘 MD5 无法追溯清理」 *(已完成：§11.1 新增「账户密码曾随列表响应泄漏」条目 —— 含事件描述、修复方式、守护测试名、**运维动作（轮换密码 + 清理代理日志）**、以及「存量已落盘 MD5 无法追溯清理」的诚实说明；并注明这是凭据泄漏第二次出现、以及 AccountVO 保留的理由（入参 schema 含 password 不构成泄漏）)*
- [x] 5.5 `AGENTS.md §7.10 第 9 项`：说明 `/account/list` 仍**未接入前端**（本变更只修后端契约，未加页面） *(已完成：§7.10 未接入项新增「账户管理（后端契约已修好但前端仍无页面）」)*
- [x] 5.6 同步 delta spec 到 `api-data-object`（Req 1 增补 `AccountPublicVO` 清单 + 凭据 Scenario）与 `api-contract`（Req 增补「容器也不例外」+「入参形参不得 hidden」），然后 `openspec archive` *(已完成：主 spec 物化 —— api-data-object Req 1 增补 AccountPublicVO 清单 + 新增凭据 Scenario（7 → 8）/ api-contract Req 增补「容器也不例外」与「入参形参不得 hidden」+ 改写裸返 boolean Scenario（5 → 7）；13/13 通过；变更已 archive)*
- [x] 5.7 commit（中文 message，一个变更一个 commit） *(已完成：中文 message，单变更单 commit)*