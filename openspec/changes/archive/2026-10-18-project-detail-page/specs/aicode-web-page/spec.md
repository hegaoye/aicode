# Spec Delta

## ADDED Requirements

### Requirement: 详情类业务页面 SHALL 通过 URL 参数驱动 unwrapSuccess 消费 R 包络

按 URL 参数（如 `:code`）定位单条资源的详情页 SHALL 用 `useParams` 取得参数，调用契约产物中的对应请求函数（如 `loadByCode(code)`），经 `unwrapSuccess(resp)` 拆出 `R<T>` 中的 `T`，再交给组件渲染；当 `R.data` 为 `null`（找不到 code 但 success） SHALL 不报错、不渲染"5xx-like"内容、而是以友好"项目不存在"占位提示。

本 Requirement 把现有 Requirement 「业务页面 SHALL 把统一响应解析与控件分发抽象为可复用单元」延伸到"按 URL 参数驱动的详情页"场景，与列表 / 详情 / 占位升级为详情三类页面形态共同覆盖样板层。

#### Scenario: 详情页通过 useParams 取 :code 并 unwrapSuccess 消费 R<ProjectVO>

- **WHEN** 用户访问 `/project/{code}` 详情页（如 `/project/1882812765213425001`）
- **THEN** 页面 SHALL 用 `useParams<{ code: string }>()` 取 `:code`，调 `api.loadByCode(code)`，经 `unwrapSuccess(resp)` 取 `ProjectVO`，再以表格渲染关键字段（`code` / `name` / `englishName` / `description` / `databaseType` / `language` / `state` / `copyright` / `author` / `phone` / `basePackage` / `buildNumber` / `isRepository` / `isParseTable` / `isParseClass` / `createTime` / `updateTime` / `accountCode`）
- **AND** 加载与错误状态分别渲染"加载中…"与 `<p role="alert" data-testid="project-detail-error">`；SHALL NOT 让"找不到 code"路径走 NPE

#### Scenario: 找不到 code 但 R.code === "0000" 应被识别为 业务空态而非失败

- **WHEN** `loadByCode(code)` 返回 `R{ success: true, code: "0000", data: null }`（M3-A-1 引入的"找不到 project"早返路径）
- **THEN** 详情页 SHALL 渲染一个友好"项目不存在"占位（或等价文案），SHALL NOT 显示错误气泡 `data-testid="project-detail-error"`
- **AND** 业务上视作"业务空态"而非"鉴权失败"或"服务器错误"

#### Scenario: 详情页与 MSW 单测无缝接入

- **WHEN** 单元测试渲染 `ProjectDetailPage` 并用 `getLoadByCodeMockHandler` 覆盖一组固定 `RProjectVO` 数据
- **THEN** 表格 SHALL 含种子项目的 `code` / `name` 等关键字段，SHALL NOT 让 MSW 拦截穿透到真实网络
- **AND** 反向验证：当响应体**不**满足 R 包络（缺 `success` / `code` / `success` ≠ true） 时，详情页 SHALL 显示错误气泡 `data-testid="project-detail-error"`，SHALL NOT 渲染 `data-testid="project-detail-content"` —— 该反向证明本 Requirement 与 `api-contract` R 包络约束联动，确保前端消费层能在接口违规时立刻可见
