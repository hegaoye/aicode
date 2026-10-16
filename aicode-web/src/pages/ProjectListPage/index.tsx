import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { ContractError, unwrapSuccess } from '../../lib/contract-response'
import { getToken } from '../../lib/auth/token-store'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import BuildLogPanel from '../../lib/realtime/build-log-panel'
import type { ProjectSummaryVO } from '../../generated/model/projectSummaryVO'
import type { PageVOProjectSummaryVO } from '../../generated/model/pageVOProjectSummaryVO'

/**
 * `ProjectListPage` —— M2 第 2 阶段接入的第二个契约驱动业务页面。
 *
 * 数据流：
 *   1. `getAicodeRESTfulAPIs().list2({ token, curPage, pageSize })` 拿到
 *      `AxiosResponse<RPageVOProjectVO>`；
 *   2. `unwrapSuccess(resp)` 拆出 `PageVOProjectVO`，失败抛 `ContractError`；
 *   3. 渲染表格列出 5 列：`code` / `name` / `englishName` / `createTime` / 操作；
 *   4. 行点击跳 `/project/:code`（本变更里的占位详情页，M3 接入）。
 *
 * 鉴权：
 *   - token 由 `getToken()` 显式读出后传入 `params.token`（与 §6.13 双轨兼容），
 *     同时 axios 拦截器会在 Header 注入 `Authorization: Bearer <token>`，
 *     后端 `LoginInterceptor` 优先消费 Header。
 *   - 该页已被 `<RequireAuth>` 包裹 —— 无 token 时不会到这里。
 *
 * 同源开发链路：`npm run dev` 时 Vite proxy 把 `/project/*` 转到 `:8080`
 * （详见 `vite.config.ts`）。MSW 在单元测试里拦截网络（`npm test`）。
 */

type LoadState =
  | { kind: 'loading' }
  | { kind: 'ready'; data: PageVOProjectSummaryVO }
  | { kind: 'error'; error: ContractError }

const PAGE_SIZE = 10

/** 把后端返回的 `createTime`（ISO 或 `YYYY-MM-DDTHH:mm:ssZ`）显示为 `YYYY-MM-DD HH:mm:ss`。 */
function formatDate(input: string | undefined | null): string {
  if (!input) {
    return '—'
  }
  // 后端契约里 `createTime` 序列化为 `2026-10-16T05:11:15.000Z` 这类 ISO，去掉尾段
  const iso = input.replace(' ', 'T')
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) {
    return input
  }
  const yyyy = d.getFullYear()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  const hh = String(d.getHours()).padStart(2, '0')
  const mi = String(d.getMinutes()).padStart(2, '0')
  const ss = String(d.getSeconds()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}:${ss}`
}

export default function ProjectListPage() {
  // 全局 axios 实例在 orval 生成物内部使用即可；每次渲染创建一份 api 是廉价的
  const api = useMemo(() => getAicodeRESTfulAPIs(), [])
  const [state, setState] = useState<LoadState>({ kind: 'loading' })
  // 记录哪些行当前正在"构建中"——BuildLogPanel 应挂载。同时启动 hook 的 startStream。
  // key 是 row.code。
  const [buildingCodes, setBuildingCodes] = useState<Set<string>>(new Set())

  useEffect(() => {
    let cancelled = false
    const token = getToken() ?? ''
    api
      .list2({ token, curPage: 1, pageSize: PAGE_SIZE })
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
  }, [api])

  const startBuildFor = useCallback((code: string) => {
    if (!code || buildingCodes.has(code)) {
      return
    }
    setBuildingCodes((prev) => {
      const next = new Set(prev)
      next.add(code)
      return next
    })
  }, [buildingCodes])

  if (state.kind === 'loading') {
    return (
      <section aria-busy="true">
        <h1>项目列表</h1>
        <p className="muted">加载中…</p>
      </section>
    )
  }

  if (state.kind === 'error') {
    return (
      <section>
        <h1>项目列表</h1>
        <p role="alert" data-testid="project-list-error">
          加载失败：{state.error.info || state.error.code}
        </p>
      </section>
    )
  }

  const records: ProjectSummaryVO[] = state.data.records ?? []

  return (
    <section>
      <h1>
        项目列表
        <small className="muted" style={{ marginLeft: '0.5em', fontWeight: 'normal' }}>
          共 {state.data.totalRow ?? records.length} 条
        </small>
      </h1>
      {records.length === 0 ? (
        <p className="muted">（暂无数据）</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th scope="col">项目编码</th>
              <th scope="col">项目名</th>
              <th scope="col">英文名</th>
              <th scope="col">状态</th>
              <th scope="col">创建时间</th>
              <th scope="col">详情</th>
              <th scope="col">构建</th>
            </tr>
          </thead>
          <tbody>
            {records.map((row) => {
              const rowCode = row.code ?? ''
              const isEnable = row.state === 'Enable'
              const isBuilding = buildingCodes.has(rowCode)
              return (
                <>
                  <tr key={rowCode || 'row-no-code'} data-testid="project-list-row">
                    <td>{rowCode || '—'}</td>
                    <td>{row.name ?? '—'}</td>
                    <td>{row.englishName ?? '—'}</td>
                    <td>{row.state ?? '—'}</td>
                    <td>{formatDate(row.createTime)}</td>
                    <td>—</td>
                    <td>
                      <Link to={`/project/${encodeURIComponent(rowCode)}`}>详情</Link>
                    </td>
                    <td>
                      <button
                        type="button"
                        className="btn-primary"
                        data-testid="project-list-build-button"
                        disabled={!isEnable || isBuilding}
                        title={isEnable ? '开始构建' : '项目未启用'}
                        onClick={() => startBuildFor(rowCode)}
                      >
                        {isBuilding ? '构建中…' : '构建'}
                      </button>
                    </td>
                  </tr>
                  {isBuilding ? (
                    <tr key={`${rowCode || 'row'}-log`} data-testid="project-list-log-row">
                      <td colSpan={6}>
                        <BuildLogPanel projectCode={rowCode} />
                      </td>
                    </tr>
                  ) : null}
                </>
              )
            })}
          </tbody>
        </table>
      )}
    </section>
  )
}
