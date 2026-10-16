import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
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
 * 数据流（**未因视觉重写改动**）：
 *   1. `getAicodeRESTfulAPIs().list2({ token, curPage, pageSize })` 拿到
 *      `AxiosResponse<RPageVOProjectVO>`；
 *   2. `unwrapSuccess(resp)` 拆出 `PageVOProjectVO`，失败抛 `ContractError`；
 *   3. 渲染卡片网格（标签行 / 标题 / 编码 / 相对时间 + 状态 / 详情 + 构建）；
 *   4. 卡片标题与「详情」跳 `/project/:code`。
 *
 * 鉴权：
 *   - token 由 `getToken()` 显式读出后传入 `params.token`（与 §6.13 双轨兼容），
 *     同时 axios 拦截器会在 Header 注入 `Authorization: Bearer <token>`，
 *     后端 `LoginInterceptor` 优先消费 Header。
 *   - 该页已被 `<RequireAuth>` 包裹 —— 无 token 时不会到这里。
 *
 * 同源开发链路：`npm run dev` 时 Vite proxy 把 `/project/*` 转到 `:8080`
 * （详见 `vite.config.ts`）。MSW 在单元测试里拦截网络（`npm test`）。
 *
 * 视觉：OpenDesign 原型 `dashboard.html`，页面级样式见
 * `src/styles/ProjectListPage.css`（类名沿用原型原名，数值未改）。
 *
 * ⚠️ 「构建中」态那套状态机（buildingCodes / mountedPanelsRef / autoStart /
 * onClosed 释放）是本仓最脆弱的一段逻辑（构建一次该行报废的历史 bug 由此而来），
 * 卡片化重排**一行未改**它的语义。
 */

type LoadState =
  | { kind: 'loading' }
  | { kind: 'ready'; data: PageVOProjectSummaryVO }
  | { kind: 'error'; error: ContractError }

const PAGE_SIZE = 10

/** 工具条的四个标签（原型静态稿，**不做真筛选**，点谁就切谁的高亮）。 */
const TABS = ['全部', '最近编辑', '已生成', '草稿'] as const

/** 把后端返回的 `createTime`（ISO 或 `YYYY-MM-DD HH:mm:ss`）显示为 `YYYY-MM-DD HH:mm:ss`。 */
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

/**
 * 卡片 `.meta-row` 左侧的相对时间（原型：「2 分钟前」「今天 09:12」「上周」）。
 *
 * <p>取不到值时回落 `—`；完整时间仍以 `title` 暴露在同一个元素上，
 * 信息不因改成相对显示而丢失。</p>
 */
function formatRelativeTime(input: string | undefined | null): string {
  if (!input) {
    return '—'
  }
  const d = new Date(input.replace(' ', 'T'))
  if (Number.isNaN(d.getTime())) {
    return '—'
  }
  const minutes = Math.floor((Date.now() - d.getTime()) / 60000)
  if (minutes < 1) {
    return '刚刚'
  }
  if (minutes < 60) {
    return `${minutes} 分钟前`
  }
  const hours = Math.floor(minutes / 60)
  if (hours < 24) {
    return `${hours} 小时前`
  }
  const days = Math.floor(hours / 24)
  if (days === 1) {
    return '昨天'
  }
  if (days < 30) {
    return `${days} 天前`
  }
  const months = Math.floor(days / 30)
  return months < 12 ? `${months} 个月前` : `${Math.floor(months / 12)} 年前`
}

/**
 * 语言 → 原型的标签配色类。
 *
 * ⚠️ 列表读投影 `ProjectSummaryVO` 目前**只有 5 个字段**（code / name /
 * englishName / state / createTime），不带 language —— 故今天这张标签不会
 * 出现在真实数据里。映射先按原型备好，等列表契约补上语言字段即可点亮，
 * 不届时再改卡片结构。
 */
function languageTagClass(language: string): string {
  const v = language.toLowerCase()
  if (v.includes('java')) return 'tag lang-java'
  if (v.includes('js') || v.includes('typescript')) return 'tag lang-js'
  if (v.includes('py') || v.includes('python')) return 'tag lang-py'
  return 'tag'
}

/** 页头（原型 `.page-head`）：标题 + 副文案 + 右侧「新建项目」。 */
function ListHead({ count }: { count?: string }) {
  return (
    <div className="page-head">
      <div>
        <h1>我的项目</h1>
        <p className="meta">
          这里列出了你创建的所有代码生成项目。最近编辑的会置顶。
          {count ? (
            <span className="list-total" data-testid="project-list-total">
              {count}
            </span>
          ) : null}
        </p>
      </div>
      <Link to="/project/new" className="btn-primary" data-testid="project-list-new">
        <svg viewBox="0 0 24 24" width={14} height={14} fill="none" stroke="currentColor" strokeWidth={2}>
          <path d="M12 5v14M5 12h14" />
        </svg>
        新建项目
      </Link>
    </div>
  )
}

export default function ProjectListPage() {
  // 全局 axios 实例在 orval 生成物内部使用即可；每次渲染创建一份 api 是廉价的
  const api = useMemo(() => getAicodeRESTfulAPIs(), [])
  const [state, setState] = useState<LoadState>({ kind: 'loading' })
  // 当前页码：后端已修成真分页（2026-10-18-project-list-real-pagination），
  // 翻页只改此状态；buildingCodes 等其它交互状态跨翻页保留（design D2）。
  const [curPage, setCurPage] = useState<number>(1)
  // 工具条选中标签（原型静态稿，只切高亮、不做真筛选）
  const [activeTab, setActiveTab] = useState<string>(TABS[0])
  // 记录哪些项目当前正在"构建中"——BuildLogPanel 应挂载。同时启动 hook 的 startStream。
  // key 是 project code（**不是行索引** —— 翻页后行序会变，按索引存会把
  // 「构建中」贴到别的项目上）。
  const [buildingCodes, setBuildingCodes] = useState<Set<string>>(new Set())
  // 记录哪些 code 的 BuildLogPanel **曾经挂载过**（跨翻页保留）。
  // 用于区分"首次挂载 → 该 autoStart 触发 execute"与"翻回重挂 → 不该再 execute"：
  // 后者若重复调 api.execute，后端 ProjectJobServiceImpl#execute 对 Executing
  // 中的项目会抛 Server_Error，用户看到误导性的"构建失败"。
  // 详见 openspec/changes/2026-10-18-project-list-pagination/design.md - D3。
  const mountedPanelsRef = useRef<Set<string>>(new Set())

  useEffect(() => {
    let cancelled = false
    const token = getToken() ?? ''
    // 切页期间先回到 loading —— 避免"上一页的卡片 + 新页码的分页信息"瞬时错配
    setState({ kind: 'loading' })
    api
      .list2({ token, curPage, pageSize: PAGE_SIZE })
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
      <div className="container">
        <ListHead />
        <p className="empty-state">加载中…</p>
      </div>
    )
  }

  if (state.kind === 'error') {
    return (
      <div className="container">
        <ListHead />
        <p role="alert" className="page-error" data-testid="project-list-error">
          加载失败：{state.error.info || state.error.code}
        </p>
      </div>
    )
  }

  const records: ProjectSummaryVO[] = state.data.records ?? []
  const totalRow = state.data.totalRow ?? records.length
  const serverTotalPage = state.data.totalPage ?? 1
  const totalPage = Math.max(serverTotalPage, curPage)
  // 当前页里正在构建的项目 —— 构建日志窗挂在网格下方（卡片塞不下）
  const buildingRecords = records.filter((row) => buildingCodes.has(row.code ?? ''))

  return (
    <div className="container">
      <ListHead count={`共 ${totalRow} 条`} />

      <div className="toolbar">
        <div className="tabs" role="tablist">
          {TABS.map((tab) => (
            <button
              key={tab}
              type="button"
              role="tab"
              aria-selected={tab === activeTab}
              onClick={() => setActiveTab(tab)}
            >
              {tab}
            </button>
          ))}
        </div>
        {/* 原型是静态稿：无搜索接口、也无列表侧筛选契约，故输入框只渲染视觉。
            与标签页同理，不改业务逻辑。 */}
        <div className="search">
          <span className="ic" aria-hidden="true">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={1.6}>
              <circle cx="11" cy="11" r="7" />
              <path d="M21 21l-4.3-4.3" />
            </svg>
          </span>
          <input type="search" placeholder="搜索项目名或英文名…" title="筛选能力待接入" />
        </div>
      </div>

      <div className="project-grid">
        {/* 网格单元：16/9 卡片 + 卡下的操作行。
            ⚠️ 操作行**不能**放进卡片内 —— 原型卡片是 aspect-ratio 16/9 +
            overflow:hidden 的定高盒子，塞进去会被裁掉（只露出按钮上沿）。
            「创建新项目」卡片也套同款单元（其下为空），保证同行各列等高。 */}
        <div className="proj-cell">
          <Link to="/project/new" className="card-new" aria-label="创建新项目">
            <span className="plus">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M12 5v14M5 12h14" />
              </svg>
            </span>
            <span>创建新项目</span>
          </Link>
        </div>

        {records.map((row) => {
          const rowCode = row.code ?? ''
          const isEnable = row.state === 'Enable'
          const isBuilding = buildingCodes.has(rowCode)
          const detailPath = `/project/${encodeURIComponent(rowCode)}`
          // 列表读投影当前不带语言/数据库字段，缺字段就不渲染标签（见 languageTagClass 注释）
          const extra = row as ProjectSummaryVO & {
            language?: string
            databaseType?: string
          }
          return (
            <div className="proj-cell" key={rowCode || 'row-no-code'} data-testid="project-list-row">
              <article className="proj">
                {extra.language || extra.databaseType ? (
                  <div className="tag-row">
                    {extra.language ? (
                      <span className={languageTagClass(extra.language)}>{extra.language}</span>
                    ) : null}
                    {extra.databaseType ? <span className="tag db">{extra.databaseType}</span> : null}
                  </div>
                ) : null}

                <h3>
                  {row.name ?? '—'}
                  {row.englishName ? ` · ${row.englishName}` : ''}
                </h3>

                {/* 原型这一行是 Java 包名；列表读投影没有 basePackage，改放项目编码
                    —— 同样是等宽小字的标识位，且是对外契约里真正用得上的那个值。 */}
                <span className="id">{rowCode || '—'}</span>

                <div className="meta-row">
                  <span className="meta" title={formatDate(row.createTime)}>
                    {formatRelativeTime(row.createTime)}
                  </span>
                  <span
                    className={`status${isBuilding ? ' building' : isEnable ? '' : ' draft'}`}
                    data-testid="project-list-status"
                  >
                    {isBuilding ? '生成中' : isEnable ? '已生成' : '草稿'}
                    {/* 原型只给中文标签，而契约回归测试按 `textContent` 断言 `state`
                        （Enable / Disenable）—— 放 .sr-only 里，视觉还原的同时文本可断言。 */}
                    {row.state ? <span className="sr-only">{row.state}</span> : null}
                  </span>
                </div>
              </article>

              <div className="proj-actions">
                <Link to={detailPath} className="detail">
                  详情
                </Link>
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
              </div>
            </div>
          )
        })}

        {records.length === 0 ? (
          <p className="empty-state" data-testid="project-list-empty">
            （暂无数据）
          </p>
        ) : null}
      </div>

      {/*
        构建日志窗：卡片是 16/9 定高 + overflow:hidden，塞不下日志，整宽挂在网格
        下方（等价于表格版 colspan 整行的位置）。
        autoStart / onMounted / onClosed 三者的配合（design D3）一行未改 ——
        翻回重挂不重复 execute、到达终态才释放「构建中」。
      */}
      <div className="proj-logs">
        {buildingRecords.map((row) => {
          const rowCode = row.code ?? ''
          return (
            <div
              className="proj-log"
              key={`${rowCode || 'row'}-log`}
              data-testid="project-list-log-row"
            >
              <BuildLogPanel
                projectCode={rowCode}
                autoStart={!mountedPanelsRef.current.has(rowCode)}
                onMounted={() => {
                  mountedPanelsRef.current.add(rowCode)
                }}
                onClosed={(code) => {
                  // 释放该行的「构建中」态。
                  //
                  // 不做的话该行**永久**卡在禁用态 —— buildingCodes 原本
                  // 只有加入没有移除，构建一次即失去再次构建的能力
                  // （用户要反复构建到满意，这是核心工作流）。
                  //
                  // 用**函数式更新**而非闭包里的 buildingCodes：多行的
                  // 面板若并发关闭，逐个基于旧快照会互相覆盖导致漏删。
                  setBuildingCodes((prev) => {
                    const next = new Set(prev)
                    next.delete(code)
                    return next
                  })
                }}
              />
            </div>
          )
        })}
      </div>

      {/*
        翻页控件。与 FrameworkListPage 形态一致（design D1）：单页时（totalPage <= 1）
        两按钮均禁用但控件仍渲染 —— 隐藏会让布局在"翻到第 2 页时按钮突然出现"时跳动。
      */}
      <nav className="list-pager" data-testid="project-list-pager">
        <button
          type="button"
          className="btn-primary"
          data-testid="project-list-prev"
          disabled={curPage <= 1}
          onClick={() => setCurPage((p) => Math.max(1, p - 1))}
        >
          上一页
        </button>
        <span className="muted" data-testid="project-list-paging">
          第 {curPage}/{totalPage} 页
        </span>
        <button
          type="button"
          className="btn-primary"
          data-testid="project-list-next"
          disabled={curPage >= totalPage}
          onClick={() => setCurPage((p) => p + 1)}
        >
          下一页
        </button>
      </nav>
    </div>
  )
}