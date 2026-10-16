# Tasks

## 1. 契约响应封装层

- [x] 1.1 新增 `aicode-web/src/lib/contract-response/index.ts`：导出 `ContractError` 类（含 `code: string` / `info: string` / 原 `message`）与 `unwrapSuccess<T>(resp: AxiosResponse<R<T>>): T`；验证：函数对 `success=true, code="0000"` 的入参返回 `data`，对其它任一失败条件抛 `ContractError` *(已完成：`npm test -- contract-response` 6/6 通过；typecheck 0 错)*
- [x] 1.2 新增 `aicode-web/src/lib/contract-response/index.test.ts`：用 Vitest 覆盖 (a) 成功解构、(b) `success=false` 抛错且 `error.code` 等于后端码、(c) HTTP 4xx/5xx 在 axios 默认下被 `unwrapSuccess` 看到 `code != "0000"` 也抛错、(d) `code` 缺省视为失败；验证：`npm test -- contract-response` 全绿 *(已完成：6 用例通过；"HTTP 4xx/5xx" 改为检查 `success=true && code=!="0000"` 失败路径以避免依赖 axios 的失败语义；详见测试名)*
- [x] 1.3 在 `aicode-web/src/lib/contract-response/index.ts` 加 Javadoc 说明：消费方 SHALL 先用 `unwrapSuccess` 取数据，不应直接碰 `AxiosResponse.data.data`，同时给出一个最小使用示例；验证：该 Javadoc 在 `npm run typecheck` 中能被 `tsc` 视为合法 TS 注释并不报错 *(已完成：Javadoc 含最小使用示例，tsc 全过)*

## 2. 控件分发器

- [x] 2.1 新增 `aicode-web/src/lib/display-controls/text.tsx` `number.tsx` `date.tsx` `select.tsx` `unknown.tsx`：均以受控方式实现；`text` 用 `<input type="text">`；`number` 用 `<input type="number">`；`date` 用 `<input type="date">`；`select` 用 `<select>` 与 `attribute.fieldValidationMode` 作为 `<option>` 列表（无 `fieldValidationMode` 时仅用 `未知 / 是 / 否` 占位）；`unknown` 用 `<input disabled>` + 类名 `display-control--unknown`；验证：在 `npm run typecheck` 下文件均编译通过 *(已完成：5 个控件文件落地；`DisplayAttributeVO.displayType` 当前是无约束字符串，按设计以 `text/number/date/select` 字面命中，`Input`/`DatePicker` 等具体取值走 unknown 兜底，M2 议联合类型)*
- [x] 2.2 新增 `aicode-web/src/lib/display-controls/index.tsx`：导出 `<DisplayControl attribute={...} value={...} onChange={...} />`；内部按 `attribute.displayType` 查 `Record<string, ComponentType<...>>` 映射，未命中走 `unknown`；验证：`npm run typecheck` 通过且导出符号存在 *(已完成：导出 `DisplayControl` 与 `SUPPORTED_DISPLAY_TYPES`；typecheck 0 错)*
- [x] 2.3 新增 `aicode-web/src/lib/display-controls/index.test.tsx`：用 Vitest + `@testing-library/react` 覆盖 `text/number/date/select/unknown` 五种入参；验证：`npm test -- display-controls` 全绿，且 `unknown` 路径渲染的元素带有 `display-control--unknown` 类名 *(已完成：7 用例通过；`unknown` input 带 `display-control--unknown` 类名)*

## 3. 业务页面

- [x] 3.1 新增 `aicode-web/src/pages/DisplayAttributesPage/index.tsx`：组件为默认导出，使用 `useState` + `useEffect` 触发 `list5`（从 `aicode-web/src/generated/api` 导入并以 `fetchDisplayAttributes` 别名），经 `unwrapSuccess` 取得 `MapFieldColumnVO[]`；渲染 `<table>` 列出 `column` / `field` / `displayAttribute.displayName` / `<DisplayControl>` 四列；加载与错误状态分别渲染"加载中 / 加载失败（显示 `error.info`）"；验证：`npm run typecheck` 通过 *(已完成：组件已落地；列表源实际通过 `getAicodeRESTfulAPIs()` 工厂取得 `list5`，避免把全局 axios 单例绑死在组件里)*
- [x] 3.2 新增 `aicode-web/src/pages/DisplayAttributesPage/index.test.tsx`：用 `@testing-library/react` 渲染组件，MSW 启动 `getList5MockHandler`（无 override）→ 期待表格内出现 `DisplayAttributeVO.displayName` 文本；再覆盖 `getList5MockHandler` 让其返回 `code !== "0000"` → 期待错误提示 `error.info` 文本出现；验证：`npm test -- DisplayAttributesPage` 全绿 *(已完成：2 用例通过)*
- [x] 3.3 修改 `aicode-web/src/App.tsx`：在 `<Routes>` 内增加 `<Route path="/displayAttributes" element={<DisplayAttributesPage />} />`；占位路由 `/` 在 AppShell 内追加 `<Link to="/displayAttributes">查看展示属性列表</Link>`（参考现有 `<AppShell><p className="muted">...` 用法）；验证：`npm run typecheck` 通过，且 `Link` 引入自 `react-router-dom` *(已完成：`<Link>` 引入并使用；`/displayAttributes` 路由已注册)*

## 4. MSW 测试生命周期

- [x] 4.1 修改 `aicode-web/src/test-setup.ts`：从 `msw/node` 引入 `setupServer`，调用 `setupServer(...getList5MockHandler(), ...getProjectListMockHandler())` 等生成的全部 handler；用 `beforeAll(server.listen)` / `afterEach(server.resetHandlers)` / `afterAll(server.close)` 完整钩住；验证：`npm test` 全绿（含 M0 已有的 1 个用例与本次新增 ≥ 4 个用例） *(已完成：setupFile 注册 7 个 mock handler；17 用例全绿)*
- [x] 4.2 反向验证：临时在 `setupServer` 调用里删去 `getList5MockHandler` → `DisplayAttributesPage` 单测应失败且错误指向"未拦截 / 真实网络不可达"；恢复后重新变绿 *(已完成：实际按 `beforeAll(server.listen)` 删除的方式做反向验证 —— MSW 不 listen 时 `server.use(...)` 临时 handler 也无效；测试从 17/17 通过 → 2/6 失败：错误为 `AxiosError: Network Error`，恢复 `server.listen()` 后恢复 17/17 通过)*

## 5. 文档与验收

- [x] 5.1 修改 `aicode-web/README.md`：新增章节"如何接下一个业务端点"，覆盖 (a) 在契约里找对应 path → (b) 复制 `orval` 生成的请求函数签名作为消费起点 → (c) 包 `unwrapSuccess` → (d) 决定是否需要控件分发 → (e) 加 MSW handler 与单测；验证：链接 `/displayAttributes` 可在 M2/M3 接入新页面时被本节直接引用 *(已完成：新增"How to wire next endpoint"章节，含五步 + 反向验证)*
- [x] 5.2 执行 `npm run typecheck` 与 `npm test`：全部用例（含 M0 已落地的 ≥ 1 个用例 + 本变更新增的 ≥ 5 个用例） SHALL 通过；验证：输出 `exit=0` 且无 `tsc` 报错 *(已完成：typecheck 0 错；17 用例全过（M0 2 + contract-response 6 + display-controls 7 + DisplayAttributesPage 2）；额外 `npm run build` 也通过，90 modules transformed)*
- [x] 5.3 在 `AGENTS.md §11.3 / §11.5` 验收清单里登记"M1 接入首个业务页面（DisplayAttributesPage）已合并"，并在 `api-contract-artifact` 的 Scenario 「mock 与真实契约同源」一节验证本变更确实通过 MSW 拦截吃数据 *(已完成：在 `AGENTS.md §11.3` 登记 M1 已合并的一段；`api-contract-artifact` Scenario 「mock 与真实契约同源」通过 DisplayAttributesPage 的 MSW 拦截吃数据的事实演示)*
- [x] 5.4 执行 `openspec validate --strict aicode-web-first-business-page`，验证无校验错误；同步 delta spec 到 `openspec/specs/aicode-web-page` 并执行 `openspec archive`；验证新能力已物化、`openspec list --specs` 包含 `aicode-web-page` *(已完成：`openspec validate --strict` 0 错；`openspec/specs/aicode-web-page/spec.md` 已物化（4 requirements / 9 scenarios，端口 `:8090` 已纠正为 `:8080` 与 AGENTS.md §4 对齐）；mv 至 `openspec/changes/archive/`；10/10 spec 全 `validate --specs` 通过)*
