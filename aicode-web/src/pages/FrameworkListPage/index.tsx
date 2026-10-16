import { useEffect, useMemo, useState } from 'react'
import { ContractError, unwrapSuccess } from '../../lib/contract-response'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import type { PageVOFrameworkSummaryVO } from '../../generated/model/pageVOFrameworkSummaryVO'

/**
 * `FrameworkListPage` —— 框架技术池列表（M5-A2）。
 *
 * 数据流（详见 `openspec/specs/aicode-web-page/spec.md` 的
 * 「列表类业务页面 SHALL 通过 mount 即拉取并 unwrapSuccess 消费 PageVO<T> 包络」）：
 *   1. `useEffect` mount 时调 `api.list4({ curPage, pageSize })`
 *      → `AxiosResponse<RPageVOFrameworkSummaryVO>`；
 *   2. `unwrapSuccess(resp)` 拆出 `PageVOFrameworkSummaryVO`；
 *   3. 渲染 `data.records[]` 4 列 + `totalRow` / `curPage` / `totalPage` 分页信息。
 *
 * 契约前置：M5-A1（`2026-10-18-framework-list-vo-contract`）把
 * `/framework/list` 收敛为 `R<PageVO<FrameworkSummaryVO>>`（7 字段、无 Git 口令、
 * 容器为 `records`）。
 *
 * 鉴权：本路由由 `<RequireAuth>` 包裹；axios 拦截器自动注入
 * `Authorization: Bearer <token>`；`shtml-suffix` 拦截器自动补 `.shtml` 后缀
 * （见 `src/lib/auth/auth-interceptor.ts` 与 `src/lib/api/shtml-suffix.ts`）。
 */

/** 页大小：覆盖 db/data.sql 的 10 条框架种子，首屏一次拉全（design D3）。 */
const PAGE_SIZE = 20

type LoadState =
  | { kind: 'loading' }
  | { kind: 'ready'; data: PageVOFrameworkSummaryVO }
  | { kind: 'error'; error: ContractError }

/**
 * 把 `isPublic` 的存储值映射为用户可读文案（design D2）。
 * 存储语义见 `aicode/src/main/resources/db/data.sql`：`'Y'` 公开 / `'N'` 非公开。
 */
function renderIsPublic(value: string | undefined): string {
  if (value === 'Y') return '公开'
  if (value === 'N') return '非公开'
  return '—'
}

export default function FrameworkListPage() {
  // 全局 axios 实例在 orval 生成物内部使用即可；每次渲染创建一份 api 是廉价的
  const api = useMemo(() => getAicodeRESTfulAPIs(), [])
  // 当前页码：初值 1。db/data.sql 有 34 条框架种子 / PAGE_SIZE 20 = 2 页，
  // 翻页控件据此启用（详见 openspec/changes/2026-10-18-framework-list-pagination/）。
  const [curPage, setCurPage] = useState<number>(1)
  const [state, setState] = useState<LoadState>({ kind: 'loading' })

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

  if (state.kind === 'loading') {
    return (
      <section aria-busy="true">
        <h1>框架技术池</h1>
        <p className="muted">加载中…</p>
      </section>
    )
  }

  if (state.kind === 'error') {
    return (
      <section>
        <h1>框架技术池</h1>
        <p role="alert" data-testid="framework-list-error">
          加载失败：{state.error.info || state.error.code}
        </p>
      </section>
    )
  }

  // 只读 records（PageVO 字段），不读 voList（历史 Page 容器字段，已由
  // api-contract 的「分页接口 SHALL 统一用 PageVO<T>」Requirement 收敛掉），
  // 也不读 createTimeBegin/End、updateTimeBegin/End（BaseVO 查询辅助字段）。
  const records = state.data.records ?? []
  const totalRow = state.data.totalRow ?? records.length
  // 后端返回的页码用于纠正本地状态（正常与本地 curPage 相同；不一致时以本地为准）
  const serverTotalPage = state.data.totalPage ?? 1
  const totalPage = Math.max(serverTotalPage, curPage)

  return (
    <section>
      <h1>
        框架技术池
        <small
          className="muted"
          style={{ marginLeft: '0.5em', fontWeight: 'normal' }}
          data-testid="framework-list-total"
        >
          共 {totalRow} 条
        </small>
      </h1>
      {records.length === 0 ? (
        <p className="muted" data-testid="framework-list-empty">
          （暂无数据）
        </p>
      ) : (
        <table>
          <thead>
            <tr>
              <th scope="col">技术编码</th>
              <th scope="col">技术名称</th>
              <th scope="col">技术描述</th>
              <th scope="col">是否公开</th>
            </tr>
          </thead>
          <tbody>
            {records.map((row) => (
              <tr key={row.code ?? row.id} data-testid="framework-list-row">
                <td>{row.code ?? '—'}</td>
                <td>{row.name ?? '—'}</td>
                <td
                  style={{
                    maxWidth: '32rem',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    whiteSpace: 'nowrap',
                  }}
                >
                  {row.description ?? '—'}
                </td>
                <td>{renderIsPublic(row.isPublic)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {/*
        翻页控件。单页时（totalPage <= 1）两按钮均禁用但控件仍渲染 ——
        隐藏会让布局在"翻到第 2 页时按钮突然出现"时跳动（design D4）。
      */}
      <nav
        data-testid="framework-list-pager"
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: 'var(--space-3)',
          marginTop: 'var(--space-4)',
        }}
      >
        <button
          type="button"
          className="btn-primary"
          data-testid="framework-list-prev"
          disabled={curPage <= 1}
          onClick={() => setCurPage((p) => Math.max(1, p - 1))}
        >
          上一页
        </button>
        <span className="muted" data-testid="framework-list-paging">
          第 {curPage}/{totalPage} 页
        </span>
        <button
          type="button"
          className="btn-primary"
          data-testid="framework-list-next"
          disabled={curPage >= totalPage}
          onClick={() => setCurPage((p) => p + 1)}
        >
          下一页
        </button>
      </nav>
    </section>
  )
}
