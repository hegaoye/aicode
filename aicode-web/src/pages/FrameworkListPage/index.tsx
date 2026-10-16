import { useEffect, useMemo, useState } from 'react'
import { ContractError, unwrapSuccess } from '../../lib/contract-response'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import type { PageVOFrameworkSummaryVO } from '../../generated/model/pageVOFrameworkSummaryVO'
import type { FrameworkSummaryVO } from '../../generated/model/frameworkSummaryVO'

/**
 * `FrameworkListPage` —— 技术架构管理（框架技术池列表 + 写侧）。
 *
 * 数据流（详见 `openspec/specs/aicode-web-page/spec.md` 的
 * 「列表类业务页面 SHALL 通过 mount 即拉取并 unwrapSuccess 消费 PageVO<T> 包络」）：
 *   1. `useEffect` mount 时调 `api.list4({ curPage, pageSize })`
 *      → `AxiosResponse<RPageVOFrameworkSummaryVO>`；
 *   2. `unwrapSuccess(resp)` 拆出 `PageVOFrameworkSummaryVO`；
 *   3. 渲染 `data.records[]` 的 7 列 + `.pager` 分页条。
 *
 * 视觉基准：OpenDesign 原型 `tech-architecture.html`，页面级样式见
 * `src/styles/FrameworkListPage.css`（类名沿用原型原名，数值未改）。
 *
 * 契约前置：M5-A1（`2026-10-18-framework-list-vo-contract`）把
 * `/framework/list` 收敛为 `R<PageVO<FrameworkSummaryVO>>`（7 字段、无 Git 口令、
 * 容器为 `records`）；M5-A3（`framework-write-endpoints`）把 `/framework/modify`
 * 与 `/framework/updateIsPublic` 改成显式 `@RequestParam`，两者入参完整可用。
 *
 * 【部分可用页】4 个写侧端点里只有 2 个契约入参完整：
 *   - `api.modify1`（PUT /framework/modify）—— 可用，本页「编辑」与「是否公开」用它；
 *   - `api.updateIsPublic1`（PUT /framework/updateIsPublic）—— 可用，行内开关用它；
 *   - `api.build5`（POST /framework/build）—— 契约无入参（后端 `build(@Parameter(hidden=true) Frameworks)`），
 *     「添加」按钮渲染完整视觉但 disabled，等接口修复后接入；
 *   - `api.delete1`（POST /framework/delete）—— 契约无入参，行内「删除」同上。
 *   ⚠️ 不得为了调用这两个端点手写 axios —— 前端一切由 orval 契约生成
 *   （见 `aicode-web/README.md` 第一原则）。
 *
 * 鉴权：本路由由 `<RequireAuth>` 包裹；axios 拦截器自动注入
 * `Authorization: Bearer <token>`；`shtml-suffix` 拦截器自动补 `.shtml` 后缀
 * （见 `src/lib/auth/auth-interceptor.ts` 与 `src/lib/api/shtml-suffix.ts`）。
 */

/** 页大小：覆盖 db/data.sql 的 10 条框架种子，首屏一次拉全（design D3）。 */
const PAGE_SIZE = 20

/** 待接入端点的统一标注 —— 视觉完整、行为禁用，让「为什么点不动」一眼可见。 */
const PENDING_API_TITLE = '接口待修复，验收后接入'

type LoadState =
  | { kind: 'loading' }
  | { kind: 'ready'; data: PageVOFrameworkSummaryVO }
  | { kind: 'error'; error: ContractError }

/** 弹窗表单的可编辑字段 —— 与原型的 5 字段表单一一对应。 */
type FormState = {
  name: string
  gitHome: string
  account: string
  description: string
  isPublic: 'Y' | 'N'
}

/** 原型 validate() 只标这三个必填项（name / repo / account）。 */
type InvalidFields = Partial<Record<'name' | 'gitHome' | 'account', true>>

/**
 * 把 `isPublic` 的存储值映射为用户可读文案（design D2）。
 * 存储语义见 `aicode/src/main/resources/db/data.sql`：`'Y'` 公开 / `'N'` 非公开。
 */
function renderIsPublic(value: string | undefined): string {
  if (value === 'Y') return '公开'
  if (value === 'N') return '非公开'
  return '—'
}

/**
 * 把写侧响应合并回该行。
 *
 * 「按响应刷新该行」而不是重拉整页（少一次请求、且不会因翻页竞态丢行）。
 * 响应体里可能出现 `null` 字段，故逐字段取 `响应 ?? 原值`，
 * 避免把列表里已渲染的内容抹掉。
 */
function mergeRow(row: FrameworkSummaryVO, vo: FrameworkSummaryVO): FrameworkSummaryVO {
  return {
    id: vo.id ?? row.id,
    code: vo.code ?? row.code,
    name: vo.name ?? row.name,
    description: vo.description ?? row.description,
    gitHome: vo.gitHome ?? row.gitHome,
    account: vo.account ?? row.account,
    isPublic: vo.isPublic ?? row.isPublic,
  }
}

export default function FrameworkListPage() {
  // 全局 axios 实例在 orval 生成物内部使用即可；每次渲染创建一份 api 是廉价的
  const api = useMemo(() => getAicodeRESTfulAPIs(), [])
  // 当前页码：初值 1。db/data.sql 有 34 条框架种子 / PAGE_SIZE 20 = 2 页，
  // 翻页控件据此启用（详见 openspec/changes/2026-10-18-framework-list-pagination/）。
  const [curPage, setCurPage] = useState<number>(1)
  const [state, setState] = useState<LoadState>({ kind: 'loading' })

  // ── 写侧状态 ────────────────────────────────────────────────────────────
  // 正在编辑的行（null = 弹窗关闭）。原型弹窗由「添加」打开，本页「添加」
  // 尚未可用（契约无入参），故由「编辑」驱动，两种模式共用同一个弹窗结构。
  const [editing, setEditing] = useState<FrameworkSummaryVO | null>(null)
  const [form, setForm] = useState<FormState>({
    name: '',
    gitHome: '',
    account: '',
    description: '',
    isPublic: 'N',
  })
  const [invalid, setInvalid] = useState<InvalidFields>({})
  const [submitting, setSubmitting] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)
  // 行内写操作（是否公开）在途的行 code —— 防重复点击
  const [busyCodes, setBusyCodes] = useState<ReadonlySet<string>>(new Set())
  // 列表级操作失败提示（切换公开状态失败时）
  const [actionError, setActionError] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    // 切页期间先回到 loading —— 避免"上一页的表格行 + 新页码的分页信息"瞬时错配
    setState({ kind: 'loading' })
    api
      .list4({ curPage, pageSize: PAGE_SIZE })
      .then((resp) => {
        if (cancelled) {
          return
        }
        const data = unwrapSuccess(resp)
        setState({ kind: 'ready', data })
      })
      .catch((err: unknown) => {
        if (cancelled) {
          return
        }
        if (err instanceof ContractError) {
          setState({ kind: 'error', error: err })
        } else {
          setState({
            kind: 'error',
            error: new ContractError('UNKNOWN', String(err)),
          })
        }
      })
    return () => {
      cancelled = true
    }
  }, [api, curPage])

  /** 把写侧响应合并回该行。 */
  function patchRow(code: string, vo: FrameworkSummaryVO) {
    setState((s) =>
      s.kind === 'ready'
        ? {
            kind: 'ready',
            data: {
              ...s.data,
              records: (s.data.records ?? []).map((row) =>
                row.code === code ? mergeRow(row, vo) : row,
              ),
            },
          }
        : s,
    )
  }

  function markBusy(code: string, busy: boolean) {
    setBusyCodes((prev) => {
      const next = new Set(prev)
      if (busy) {
        next.add(code)
      } else {
        next.delete(code)
      }
      return next
    })
  }

  /** 打开编辑弹窗并带出该行现值。 */
  function openEditor(row: FrameworkSummaryVO) {
    setEditing(row)
    setForm({
      name: row.name ?? '',
      gitHome: row.gitHome ?? '',
      account: row.account ?? '',
      description: row.description ?? '',
      isPublic: row.isPublic === 'Y' ? 'Y' : 'N',
    })
    setInvalid({})
    setFormError(null)
  }

  function closeModal() {
    setEditing(null)
    setInvalid({})
    setFormError(null)
  }

  /** 提交编辑 → `api.modify1`（PUT /framework/modify，显式 @RequestParam 入参完整）。 */
  async function submitEdit() {
    const code = editing?.code
    if (!code || submitting) {
      return
    }
    // 原型的 validate()：三个必填项为空即标红并中止提交
    const bad: InvalidFields = {}
    if (!form.name.trim()) {
      bad.name = true
    }
    if (!form.gitHome.trim()) {
      bad.gitHome = true
    }
    if (!form.account.trim()) {
      bad.account = true
    }
    if (Object.keys(bad).length > 0) {
      setInvalid(bad)
      return
    }
    setInvalid({})
    setSubmitting(true)
    setFormError(null)
    try {
      const resp = await api.modify1({
        code,
        name: form.name.trim(),
        gitHome: form.gitHome.trim(),
        account: form.account.trim(),
        isPublic: form.isPublic,
        description: form.description.trim(),
        // ⚠️ password 刻意不传：契约里它是可选字段，且 FrameworkSummaryVO
        // 不含 Git 口令（凭据不进任何读响应），前端没有可回填的口令来源 ——
        // 也不能为此再发一次请求去把口令取回来（那等于把它重新送进前端）。
      })
      const vo = unwrapSuccess(resp)
      patchRow(code, vo)
      setEditing(null)
    } catch (err: unknown) {
      setFormError(
        err instanceof ContractError ? err.info || err.code : String(err),
      )
    } finally {
      setSubmitting(false)
    }
  }

  /** 行内「是否公开库」开关 → `api.updateIsPublic1`（PUT /framework/updateIsPublic）。 */
  async function togglePublic(row: FrameworkSummaryVO) {
    const code = row.code
    if (!code || busyCodes.has(code)) {
      return
    }
    const next = row.isPublic === 'Y' ? 'N' : 'Y'
    markBusy(code, true)
    setActionError(null)
    try {
      const resp = await api.updateIsPublic1({ code, isPublic: next })
      const vo = unwrapSuccess(resp)
      patchRow(vo.code ?? code, vo)
    } catch (err: unknown) {
      setActionError(
        err instanceof ContractError ? err.info || err.code : String(err),
      )
    } finally {
      markBusy(code, false)
    }
  }

  // 只读 records（PageVO 字段），不读 voList（历史 Page 容器字段，已由
  // api-contract 的「分页接口 SHALL 统一用 PageVO<T>」Requirement 收敛掉），
  // 也不读 createTimeBegin/End、updateTimeBegin/End（BaseVO 查询辅助字段）。
  const ready = state.kind === 'ready' ? state.data : null
  const records = ready?.records ?? []
  const totalRow = ready?.totalRow ?? records.length
  // 后端返回的页码用于纠正本地状态（正常与本地 curPage 相同；不一致时以本地为准）
  const serverTotalPage = ready?.totalPage ?? 1
  const totalPage = Math.max(serverTotalPage, curPage)
  const pageNumbers = useMemo(
    () => Array.from({ length: Math.max(1, totalPage) }, (_, i) => i + 1),
    [totalPage],
  )

  return (
    <div className="container">
      <div className="page-head">
        <div>
          <h1>技术架构管理</h1>
          {/* 原型的 class 是 `lead`，base.css 已把该槽位收敛为 `.page-head .meta`
              （取值逐字相同：muted / 13px / margin-top 6px），故沿用基元名。 */}
          <p className="meta">维护代码生成所使用的技术栈与模板仓库信息</p>
        </div>
        {/* 「添加」走 POST /framework/build —— 契约无入参，接口修复前只渲染视觉。 */}
        <button
          type="button"
          className="btn-primary"
          data-testid="framework-list-add"
          disabled
          title={PENDING_API_TITLE}
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2.2}>
            <path d="M12 5v14M5 12h14" />
          </svg>
          添加
        </button>
      </div>

      {actionError ? (
        <p className="page-error" role="alert" data-testid="framework-list-action-error">
          操作失败：{actionError}
        </p>
      ) : null}

      <div className="panel">
        <div className="panel-head">
          <div>
            {/* 原型用 `.panel-head h2` 元素选择器，base.css 收敛为
                `.panel-head .title`（14px / 600，取值相同），故挂类不重复定义。 */}
            <h2 className="title">已注册的技术</h2>
            {/*
              副文案照搬原型的「共 18 项 · 最后更新于 2024-10-08」。
              ⚠️「最后更新于」当前取不到真值：/framework/list 返回的
              FrameworkSummaryVO 只有 7 个字段、不含 updateTime（api-data-object
              的读投影刻意裁掉了）。契约补上时间字段前保持占位符，
              不另发请求去猜一个时间。
            */}
            <p className="lead" data-testid="framework-list-lead">
              共 {totalRow} 项 · 最后更新于 —
            </p>
          </div>
        </div>

        {state.kind === 'loading' ? (
          <p className="empty-state" data-testid="framework-list-loading">
            加载中…
          </p>
        ) : state.kind === 'error' ? (
          <p className="page-error" role="alert" data-testid="framework-list-error">
            加载失败：{state.error.info || state.error.code}
          </p>
        ) : records.length === 0 ? (
          <p className="empty-state" data-testid="framework-list-empty">
            暂无数据
          </p>
        ) : (
          <div className="table-wrap">
            <table className="data-table framework-table">
              <thead>
                <tr>
                  <th scope="col" className="col-index">
                    序号
                  </th>
                  <th scope="col">技术名称</th>
                  <th scope="col">模板仓库地址</th>
                  <th scope="col">模板仓库账户</th>
                  <th scope="col">是否公开库</th>
                  <th scope="col">技术描述</th>
                  <th scope="col" className="col-actions">
                    操作
                  </th>
                </tr>
              </thead>
              <tbody>
                {records.map((row, index) => {
                  const code = row.code ?? ''
                  const isPublic = row.isPublic === 'Y'
                  return (
                    <tr key={code || row.id} data-testid="framework-list-row">
                      <td className="col-index">
                        {(curPage - 1) * PAGE_SIZE + index + 1}
                        {/* 原型的 7 列里没有「编码」列，但它是对外契约的一部分
                            （行内写操作全靠 code 定位）。放 .sr-only 里 →
                            视觉 100% 还原原型，文本仍可被断言与朗读。 */}
                        {code ? <span className="sr-only">{code}</span> : null}
                      </td>
                      <td>{row.name ?? '—'}</td>
                      <td className="mono truncate" title={row.gitHome ?? undefined}>
                        {row.gitHome ?? '—'}
                      </td>
                      <td>{row.account ?? '—'}</td>
                      <td>
                        <button
                          type="button"
                          className={`toggle${isPublic ? '' : ' off'}`}
                          role="switch"
                          aria-checked={isPublic}
                          data-testid="framework-list-toggle"
                          disabled={!code || busyCodes.has(code)}
                          onClick={() => void togglePublic(row)}
                        >
                          <span className="sr-only">
                            是否公开库：{renderIsPublic(row.isPublic)}
                          </span>
                        </button>
                      </td>
                      <td className="truncate" title={row.description ?? undefined}>
                        {row.description ?? '—'}
                      </td>
                      <td className="col-actions">
                        <div className="row-actions">
                          <button
                            type="button"
                            className="edit"
                            aria-label="编辑"
                            data-testid="framework-list-edit"
                            disabled={!code}
                            onClick={() => openEditor(row)}
                          >
                            <svg
                              viewBox="0 0 24 24"
                              fill="none"
                              stroke="currentColor"
                              strokeWidth={1.8}
                            >
                              <path d="M12 20h9" />
                              <path d="M16.5 3.5a2.121 2.121 0 1 1 3 3L7 19l-4 1 1-4 12.5-12.5z" />
                            </svg>
                          </button>
                          {/* 「删除」走 POST /framework/delete —— 契约无入参 */}
                          <button
                            type="button"
                            className="del"
                            aria-label="删除"
                            data-testid="framework-list-delete"
                            disabled
                            title={PENDING_API_TITLE}
                          >
                            <svg
                              viewBox="0 0 24 24"
                              fill="none"
                              stroke="currentColor"
                              strokeWidth={1.8}
                            >
                              <path d="M3 6h18" />
                              <path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                              <path d="M5 6l1 14a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2l1-14" />
                            </svg>
                          </button>
                        </div>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}

        {/*
          分页条。单页时（totalPage <= 1）两按钮均禁用但控件仍渲染 ——
          隐藏会让布局在"翻到第 2 页时按钮突然出现"时跳动（design D4）。
        */}
        <nav className="pager" data-testid="framework-list-pager">
          <span data-testid="framework-list-total">共 {totalRow} 条</span>
          <div className="pages">
            <button
              type="button"
              aria-label="上一页"
              data-testid="framework-list-prev"
              disabled={curPage <= 1}
              onClick={() => setCurPage((p) => Math.max(1, p - 1))}
            >
              <svg viewBox="0 0 24 24" width={14} height={14} fill="none" stroke="currentColor" strokeWidth={2}>
                <path d="M15 18l-6-6 6-6" />
              </svg>
            </button>
            {pageNumbers.map((n) => (
              <button
                key={n}
                type="button"
                className={n === curPage ? 'current' : undefined}
                aria-current={n === curPage ? 'page' : undefined}
                aria-label={`第 ${n} 页`}
                onClick={() => setCurPage(n)}
              >
                {n}
              </button>
            ))}
            <button
              type="button"
              aria-label="下一页"
              data-testid="framework-list-next"
              disabled={curPage >= totalPage}
              onClick={() => setCurPage((p) => p + 1)}
            >
              <svg viewBox="0 0 24 24" width={14} height={14} fill="none" stroke="currentColor" strokeWidth={2}>
                <path d="M9 6l6 6-6 6" />
              </svg>
            </button>
            {/* 原型的分页条只有页码方块，没有「第 X/Y 页」文字。这里用
                .sr-only 补一句状态播报：视觉零变化，但读屏用户与契约回归测试
                都能拿到「第 X/Y 页」这个信息。 */}
            <span className="sr-only" role="status" data-testid="framework-list-paging">
              第 {curPage}/{totalPage} 页
            </span>
          </div>
        </nav>
      </div>

      {/*
        弹窗：结构照搬原型的「添加技术架构」弹窗（同样的 .modal-mask / .modal /
        .modal-head / .modal-body / .modal-foot 与 5 字段表单）。
        当前只有「编辑」可用，故标题与主按钮用编辑态文案；「添加」接入
        `api.build5` 后只需在同一结构上加一个模式分支，不必重写结构。
      */}
      <div
        className={`modal-mask${editing ? ' open' : ''}`}
        data-testid="framework-modal-mask"
      >
        <div className="modal" role="dialog" aria-modal="true" aria-labelledby="framework-modal-title">
          <div className="modal-head" id="framework-modal-title">
            编辑技术架构
          </div>
          <div className="modal-body">
            <form
              noValidate
              onSubmit={(e) => {
                e.preventDefault()
                void submitEdit()
              }}
            >
              <div className={`form-field${invalid.name ? ' invalid' : ''}`} data-field="name">
                <label htmlFor="framework-form-name">
                  技术名称 <span className="req">*</span>
                </label>
                <input
                  id="framework-form-name"
                  data-testid="framework-form-name"
                  type="text"
                  placeholder="例如 springcloud3.3.9"
                  value={form.name}
                  onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
                />
                <div className="err">请输入技术名称</div>
              </div>
              <div className={`form-field${invalid.gitHome ? ' invalid' : ''}`} data-field="repo">
                <label htmlFor="framework-form-git-home">
                  模板仓库地址 <span className="req">*</span>
                </label>
                <input
                  id="framework-form-git-home"
                  data-testid="framework-form-git-home"
                  type="url"
                  placeholder="https://gitee.com/saas/xxx.git"
                  value={form.gitHome}
                  onChange={(e) => setForm((f) => ({ ...f, gitHome: e.target.value }))}
                />
                <div className="err">请输入合法的仓库地址</div>
              </div>
              <div className={`form-field${invalid.account ? ' invalid' : ''}`} data-field="account">
                <label htmlFor="framework-form-account">
                  模板仓库账户 <span className="req">*</span>
                </label>
                <input
                  id="framework-form-account"
                  data-testid="framework-form-account"
                  type="text"
                  placeholder="账户名"
                  value={form.account}
                  onChange={(e) => setForm((f) => ({ ...f, account: e.target.value }))}
                />
                <div className="err">请输入账户名</div>
              </div>
              <div className="form-field">
                {/* 两处 label 与原型的写法一致：首行是纯说明，下方 label 包住
                    开关与文案（隐式关联，点文案即点开关）。 */}
                <label>是否公开库</label>
                <label className="switch-label">
                  <button
                    id="framework-form-public"
                    type="button"
                    className={`toggle${form.isPublic === 'Y' ? '' : ' off'}`}
                    role="switch"
                    aria-checked={form.isPublic === 'Y'}
                    data-testid="framework-form-public-toggle"
                    onClick={() =>
                      setForm((f) => ({ ...f, isPublic: f.isPublic === 'Y' ? 'N' : 'Y' }))
                    }
                  >
                    <span className="sr-only">
                      是否公开库：{form.isPublic === 'Y' ? '公开' : '非公开'}
                    </span>
                  </button>
                  <span>对外可见</span>
                </label>
              </div>
              <div className="form-field">
                <label htmlFor="framework-form-description">技术描述</label>
                <textarea
                  id="framework-form-description"
                  data-testid="framework-form-description"
                  rows={3}
                  placeholder="一句话描述该技术栈"
                  value={form.description}
                  onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
                />
              </div>
              {formError ? (
                <p className="page-error" role="alert" data-testid="framework-modal-error">
                  保存失败：{formError}
                </p>
              ) : null}
            </form>
          </div>
          <div className="modal-foot">
            <button
              type="button"
              className="btn-ghost"
              data-testid="framework-modal-cancel"
              onClick={closeModal}
            >
              取消
            </button>
            <button
              type="button"
              className="btn-primary"
              data-testid="framework-modal-confirm"
              disabled={submitting}
              onClick={() => void submitEdit()}
            >
              保存
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}