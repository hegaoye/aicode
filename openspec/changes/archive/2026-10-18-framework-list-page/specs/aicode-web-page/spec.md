# Spec Delta

## ADDED Requirements

### Requirement: 列表类业务页面 SHALL 通过 mount 即拉取并 unwrapSuccess 消费 PageVO<T> 包络

以分页列表为形态的业务页面（无 URL 参数驱动，由页面自身在 mount 时拉取）SHALL 用 `useEffect` + `useState` 在组件 mount 时调用契约产物中的分页请求函数（如 `api.list4({ curPage, pageSize })`），并经 `unwrapSuccess(resp)` 拆出 `PageVO<T>` 的 `data`，再逐项渲染 `records`。

页面 SHALL 呈现 `totalRow` / `curPage` / `totalPage` 三个分页信息（用户需要知道"共多少条 / 当前第几页 / 共几页"），SHALL NOT 只渲染列表而隐藏总量 —— 这会让用户误判数据是否完整。

#### Scenario: 列表页 mount 即拉取并渲染 records

- **WHEN** 用户访问列表页（如 `/frameworks`）且 `useAuth.isAuthenticated === true`
- **THEN** 组件 SHALL 在 mount 时调 `api.list4({ curPage: 1, pageSize: N })`，经 `unwrapSuccess(resp)` 取 `PageVO<T>`，渲染 `data.records[]` 每一项
- **AND** SHALL 同时渲染 `totalRow` / `curPage` / `totalPage` 三个分页信息

#### Scenario: records 为空时 SHALL 显示业务空态而非错误

- **WHEN** 后端返回 `R{ success: true, code: "0000", data: { records: [], totalRow: 0 } }`
- **THEN** 页面 SHALL 渲染"（暂无数据）"之类的空态文案
- **AND** SHALL NOT 渲染 `data-testid="...-error"` 错误气泡 —— 空列表不是失败

#### Scenario: R.code !== "0000" SHALL 走错误气泡且不渲染 records

- **WHEN** 后端返回 `R{ success: false, code: "9999", info: "..." }`（HTTP 仍 200）
- **THEN** 页面 SHALL 经 `unwrapSuccess` 抛出 `ContractError` 并渲染含 `info` 的错误气泡
- **AND** SHALL NOT 渲染任何列表行

#### Scenario: 分页字段名 SHALL 是 records 而非 voList

- **WHEN** 页面渲染分页数据
- **THEN** SHALL 读取 `data.records`；SHALL NOT 读取 `data.voList`（后者是历史 `Page<E>` 容器的字段名，已由 `api-contract` 的「分页接口 SHALL 统一用 PageVO<T>」Requirement 收敛掉）
