# Tasks

## 1. ProjectDetailPage 替换占位

- [x] 1.1 重写 `aicode-web/src/pages/ProjectDetailPage/index.tsx`：保留 `useParams<{ code: string }>()` 取 `:code`；新增 `useState<LoadState>` + `useEffect` 调 `api.loadByCode(code)` 经 `unwrapSuccess` 取 `ProjectVO`；渲染：① `<h1>项目详情</h1>` ② 加载中状态 ③ 错误气泡 `data-testid="project-detail-error"` ④ data 为 null 的业务空态：`data-testid="project-detail-empty"` ⑤ 用 `<dl>/<dt>/<dd>` 键值列表展示 22 字段（按"标识 + 细节 + 技术计数 + 时间 + 关联"五组）；验证：`npm run typecheck` exit=0 *(已完成：`tsc --noEmit` exit=0；22 字段分 5 组 dl 渲染；`state as { kind: 'ready'; data: ProjectVO }` 收窄 null)*
- [x] 1.2 表格行 SHALL NOT 直接读 `null`（用 `data &&` 防御 runtime null）；验证：MSW 单测中给出 `data: null` 时不抛 React 渲染错 *(已完成：`state.kind === 'ready' && state.data === null` 提前渲染"项目不存在"分支；`renderValue()` 把 `null/undefined` 映射为 em-dash；`data-testid="project-detail-empty"` 与 `data-testid="project-detail-field-{name}"` 双轨断言)*

## 2. 单元测试

- [x] 2.1 新增 `aicode-web/src/pages/ProjectDetailPage/index.test.tsx`：3 用例 ① 默认 mock 命中（含确定 `code` / `name` / `englishName`）→ 表格可见关键文本 ② `data: null` 场景 → "项目不存在" 友好提示（无 `project-detail-error` testid） ③ `code: "9999"` 场景 → `project-detail-error` testid 含 `ContractError.info`；验证：`npm test -- ProjectDetailPage` 全绿 *(已完成：3 用例通过；22 字段全部 testid 校验)*
- [x] 2.2 反向验证：临时让 controller 改回直返 `ProjectVO`（破坏 R 包络） → 测试 SHALL 失败 —— MSW 返回的 `R{...}` 包络中 data.code 仍按 R 中约定；该反向验证保证 `api-contract` R 约束被前端消费层实际触发；恢复后通过 *(已完成：基于 msw 直接注入裸 ProjectVO 响应体（不走 orval handler）；前端 `unwrapSuccess` 看到 success 不是 true → 抛 ContractError → 走错误气泡 path；新增 1 个反向用例通过；4 用例全绿；后端 controller 已还原)*

## 3. 验收

- [x] 3.1 跑 `npm run typecheck` 与 `npm test`：全绿；预期 46 用例 / 9 文件（M2-2 基线 43 + 本变更 3） *(已完成：实测 47 用例 / 10 文件 —— M2-2 43 + 新增 3 + 1 反向验证用例；typecheck exit=0)*
- [x] 3.2 跑 `npm run build`：产物大小变化 < 1 KB（不影响构建规模）；验证 M1 / M2-2 既有 43 用例仍全绿 *(已完成：`npm run build` 96 modules transformed；产物 index 328 KB / gzip 105 KB —— 与 M2-2 一致；新页面被合并入主 chunk < 1 KB)*
- [x] 3.3 跑 `openspec validate --strict 2026-10-18-project-detail-page`：校验通过 *(已完成：`Change is valid`，0 错)*
- [x] 3.4 同步 delta spec 到 `openspec/specs/aicode-web-page` 并 `openspec archive`；验证主 spec `aicode-web-page` 新增 Requirement "详情类业务页面 SHALL 通过 URL 参数驱动 unwrapSuccess 消费 R 包络" 含 3 Scenarios；`openspec list --specs aicode-web-page` 现状从 4 → 5 Requirements *(已完成：主 spec `aicode-web-page` 增 1 Requirement 3 Scenarios（requirements 4 → 5）；`openspec validate --specs` 11/11 通过；变更 mv 至 archive；`openspec list` No active changes)*
