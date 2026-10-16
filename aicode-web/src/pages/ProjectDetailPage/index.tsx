import { useEffect, useMemo, useRef, useState } from 'react'
import { useParams } from 'react-router-dom'
import { ContractError, unwrapSuccess } from '../../lib/contract-response'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import type { ProjectDetailVO } from '../../generated/model/projectDetailVO'
import BuildLogPanel from '../../lib/realtime/build-log-panel'
import ProjectSqlPanel from './ProjectSqlPanel'
import RelationshipPanel from './RelationshipPanel'

/**
 * `ProjectDetailPage` —— 项目详情页（代码生成六步流程的总览与入口）。
 *
 * 数据流（**本轮视觉重写未改业务逻辑，以下与重写前逐行一致**）：
 *   1. `useParams<{ code: string }>()` 取 URL `:code` 入参；
 *   2. `getAicodeRESTfulAPIs().loadByCode(code)` 拿到
 *      `AxiosResponse<RProjectDetailVO>`（M3-A-1 R 包络 + M3-C 12 字段投影）；
 *   3. `unwrapSuccess(resp)` 拆出 `ProjectDetailVO`；失败抛 `ContractError`；
 *   4. 按 5 状态机（loading / ready+data / ready+empty / error）渲染。
 *
 * 错误状态分两类（详见 design D4）：
 *   - `R.code === "0000" && data === null` → 业务空态，渲染"项目不存在"；
 *   - `R.code !== "0000"` 或 HTTP 异常 → 渲染 `<p role="alert" data-testid="project-detail-error">`。
 *
 * 鉴权：本路由已被 `<RequireAuth>` 包裹（M2-2 落地），调用方无须显式 token；
 * axios 拦截器已在请求头注入 `Authorization: Bearer <token>`。
 *
 * 同源 dev：`vite.config.ts` 把 `/project/load/code/*` proxy 到 `:8080`。
 * 单元测试：MSW 拦截（`getLoadByCodeMockHandler` 已在 `test-setup.ts` 注册）。
 *
 * 【视觉基准】OpenDesign 原型 `project-review.html` —— 6 个 `.panel` 依次排列
 * （项目信息 / SQL 信息 / 数据管理 / 技术信息 / 仓库信息 / 构建历史）+ 底部
 * `.build-bar`。类名沿用原型原名，页面级样式见 `styles/ProjectDetailPage.css`；
 * `.panel* / .badge / .empty-state / .grid-2 / .field / .data-table` 全部复用
 * base.css 的跨页基元，本页不重复定义。
 */

type LoadState =
  | { kind: 'loading' }
  | { kind: 'ready'; data: ProjectDetailVO | null }
  | { kind: 'error'; error: ContractError }

/**
 * 「数据管理」面板的徽标依据 —— 该项目是否已经解析出可关联的表。
 *
 * `unknown` 是刻意保留的第三态：探针请求失败时**不得**把 RelationshipPanel
 * 藏起来（那等于因为一次网络抖动就抽走用户的配置入口），故 fail-open。
 */
type RelationsProbe = 'unknown' | 'none' | 'some'

/**
 * 「项目信息」面板主栅格的 8 个字段 —— 逐格对齐原型 project-review.html 的 8 个槽位。
 *
 * ⚠️ 两处槽位替换，原因只有一个：`ProjectDetailVO` 里没有原型那两格的字段。
 * M3-C（`2026-10-17-project-vo-trim`）把 ProjectVO 的 22 字段裁到 12，
 * `databaseType` 正是被裁掉的那 10 个之一；「SQL 类型」在后端没有对应列。
 * 原样留着这两格只能渲染一条恒为「—」的空行（用户会当成 bug），故换成
 * ProjectDetailVO 里真实存在、语义相邻的字段：
 *   · 槽位 3「数据库类型」 → 「项目编码」`code`
 *   · 槽位 8「SQL 类型」   → 「基础包名」`basePackage`
 * 反向守护：index.test.tsx 的「M3-C 字段裁切边界」用例钉死这 10 个裁切字段
 * 一个都不许被渲染出来（含上面这两个槽位）。
 */
const INFO_FIELDS: Array<{ key: keyof ProjectDetailVO; label: string }> = [
  { key: 'name', label: '项目名' },
  { key: 'englishName', label: '项目英文名' },
  { key: 'code', label: '项目编码' },
  { key: 'copyright', label: '项目版权信息' },
  { key: 'author', label: '作者' },
  { key: 'phone', label: '作者联系方式' },
  { key: 'description', label: '项目描述' },
  { key: 'basePackage', label: '基础包名' },
]

/**
 * 同面板第二段栅格：构建计数、时间与归属账户。
 *
 * 原型的「项目信息」只有一段 8 格栅格；本实现要展示 ProjectDetailVO 的全部
 * 12 个字段（既有 4 条用例逐个断言 12 个 `project-detail-field-*`），
 * 余下 4 格接在第二段栅格里，同样复用 `.grid-2`。
 */
const META_FIELDS: Array<{ key: keyof ProjectDetailVO; label: string }> = [
  { key: 'buildNumber', label: '生成次数' },
  { key: 'createTime', label: '创建时间' },
  { key: 'updateTime', label: '更新时间' },
  { key: 'accountCode', label: '账户编码' },
]

/**
 * 待接入端点的统一标注 —— 与 FrameworkListPage 同款文案。
 * 「技术信息」（`/project/framwork/*`）与「仓库信息」（`/project/repository/*`）
 * 的契约都因 `@Parameter(hidden = true)` 缺入参（见 AGENTS §10），
 * 面板先按原型做出来并渲染为 pending 空态，**不编造接口调用**。
 */
const PENDING_PANEL_TEXT = '接口待修复，验收后接入'

/** 字段值显示：null / undefined → em-dash；Date 类型不出现（持久化字段全是 string/number）；其它原样 */
function renderValue(value: unknown): string {
  if (value === null || value === undefined) {
    return '—'
  }
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') {
    return String(value)
  }
  return '—'
}

/** 一个面板：标题 + 右侧徽标（+ 可选「编辑」），结构与原型逐行一致。 */
function PanelHead({
  title,
  badge,
  editHref,
}: {
  title: string
  /** 徽标态：ok = 已配置（绿），pending = 待配置（琥珀）。不传 = 该面板无徽标。 */
  badge?: 'ok' | 'pending'
  /** 「编辑 →」目标。原型指向对应的新建/向导页；本页只有「项目信息」给。 */
  editHref?: string
}) {
  return (
    <div className="panel-head">
      <span className="title">{title}</span>
      <span className="right">
        {badge ? (
          <span className={`badge ${badge}`}>{badge === 'ok' ? '已配置' : '待配置'}</span>
        ) : null}
        {editHref ? (
          <a className="edit" href={editHref}>
            编辑 →
          </a>
        ) : null}
      </span>
    </div>
  )
}

export default function ProjectDetailPage() {
  const params = useParams<{ code: string }>()
  const code = params.code ?? ''
  // 全局 axios 实例在 orval 生成物内部使用即可；每次渲染创建一份 api 是廉价的
  const api = useMemo(() => getAicodeRESTfulAPIs(), [])
  const [state, setState] = useState<LoadState>({ kind: 'loading' })

  // 「数据管理」面板徽标依据（见 RelationsProbe 的注释）
  const [relations, setRelations] = useState<RelationsProbe>('unknown')
  // 是否正在构建 —— 决定 `.btn-build` 的禁用态与 BuildLogPanel 的挂载
  const [building, setBuilding] = useState(false)
  /** 本页是否已经挂载过 BuildLogPanel（配合 autoStart，见 JSX 里的注释） */
  const buildPanelMountedRef = useRef(false)

  useEffect(() => {
    if (!code) {
      // URL 缺少 `:code`：理论上不会发生（路由表不匹配）；出于防御直接走"业务空态"
      setState({ kind: 'ready', data: null })
      return
    }
    let cancelled = false
    api
      .loadByCode(code)
      .then((resp) => {
        if (cancelled) {
          return
        }
        const projectVO = unwrapSuccess(resp)
        // unwrapSuccess 的返回类型是 NonNullable<ProjectDetailVO>，但实际运行时 data 可能为 null
        // （M3-A-1 引入的"找不到 project"早返路径）—— 视作业务空态
        setState({ kind: 'ready', data: (projectVO ?? null) as ProjectDetailVO | null })
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
  }, [api, code])

  /**
   * 探针：项目有没有解析出表（= 有没有可配的关系）。
   *
   * 走的是 `GET /project/relationship/listMapClassTable` —— 两个子面板自己也在拉
   * 这个端点，这里只为驱动徽标与空态，多一次请求换取徽标如实（而不是写死
   * 「待配置」骗人）。失败即 `unknown`（fail-open，见 RelationsProbe）。
   */
  useEffect(() => {
    if (!code) {
      return
    }
    let cancelled = false
    api
      .listMapClassTable({ projectCode: code })
      .then((resp) => {
        if (cancelled) {
          return
        }
        const list = unwrapSuccess(resp)
        setRelations((list ?? []).length > 0 ? 'some' : 'none')
      })
      .catch(() => {
        if (!cancelled) {
          setRelations('unknown')
        }
      })
    return () => {
      cancelled = true
    }
  }, [api, code])

  if (state.kind === 'loading') {
    return (
      <div className="container" aria-busy="true">
        <div className="page-head">
          <div>
            <h1>项目详情</h1>
            <p className="meta">正在读取项目…</p>
          </div>
        </div>
        <p className="empty-state" data-testid="project-detail-loading">
          加载中…
        </p>
      </div>
    )
  }

  if (state.kind === 'error') {
    return (
      <div className="container">
        <div className="page-head">
          <div>
            <h1>项目详情</h1>
            <p className="meta">code: <code>{code}</code></p>
          </div>
        </div>
        <p className="page-error" role="alert" data-testid="project-detail-error">
          加载失败：{state.error.info || state.error.code}
        </p>
      </div>
    )
  }

  if (state.kind === 'ready' && state.data === null) {
    return (
      <div className="container">
        <div className="page-head">
          <div>
            <h1>项目详情</h1>
            <p className="meta">code: <code>{code}</code></p>
          </div>
        </div>
        <p className="empty-state" data-testid="project-detail-empty">
          项目不存在（code：<code>{code}</code>）
        </p>
      </div>
    )
  }

  // state.kind === 'ready' && state.data !== null —— 真实数据
  const data = (state as { kind: 'ready'; data: ProjectDetailVO }).data
  const relationsEmpty = relations === 'none'

  return (
    <div className="container" data-testid="project-detail-content">
      <div className="page-head">
        <div>
          <h1>项目详情</h1>
          <p className="meta">
            code: <code>{data.code ?? '—'}</code>
          </p>
        </div>
      </div>

      {/* ---------- ① 项目信息 ---------- */}
      <section className="panel" data-testid="project-detail-panel-info">
        <PanelHead title="项目信息" badge="ok" editHref="#/project/new" />
        <div className="panel-body detail-fields">
          <div className="grid-2">
            {INFO_FIELDS.map(({ key, label }) => (
              <div className="field" key={key} data-testid={`project-detail-field-${key}`}>
                <span className="k">{label}</span>
                <span className="v">{renderValue(data[key])}</span>
              </div>
            ))}
          </div>
          <div className="grid-2">
            {META_FIELDS.map(({ key, label }) => (
              <div className="field" key={key} data-testid={`project-detail-field-${key}`}>
                <span className="k">{label}</span>
                <span className="v">{renderValue(data[key])}</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ---------- ② SQL 信息（代码生成第 ②③ 步：导 SQL + 逆解析） ---------- */}
      <section className="panel" data-testid="project-detail-panel-sql">
        <PanelHead title="SQL 信息" badge="ok" />
        <div className="panel-body">
          <ProjectSqlPanel projectCode={code} />
        </div>
      </section>

      {/* ---------- ③ 数据管理（代码生成第 ⑤ 步：配三层映射的第三层） ---------- */}
      <section className="panel" data-testid="project-detail-panel-data">
        <PanelHead title="数据管理" badge={relationsEmpty ? 'pending' : 'ok'} />
        <div className="panel-body">
          {relationsEmpty ? (
            <p className="empty-state" data-testid="project-detail-data-empty">
              尚未配置表模型关系
            </p>
          ) : (
            <RelationshipPanel projectCode={code} />
          )}
        </div>
      </section>

      {/* ---------- ④ 技术信息（待接入） ---------- */}
      <section className="panel" data-testid="project-detail-panel-tech">
        <PanelHead title="技术信息" badge="pending" />
        <div className="panel-body">
          <p className="empty-state">{PENDING_PANEL_TEXT}</p>
        </div>
      </section>

      {/* ---------- ⑤ 仓库信息（待接入） ---------- */}
      <section className="panel" data-testid="project-detail-panel-repo">
        <PanelHead title="仓库信息" badge="pending" />
        <div className="panel-body">
          <p className="empty-state">{PENDING_PANEL_TEXT}</p>
        </div>
      </section>

      {/* ---------- ⑥ 构建历史 ----------
          后端没有「按项目列构建记录」的端点（契约里只有 `GET /project/job/execute`
          与 ProjectJob 的实体形态，无 list），因此照原型渲染表头 + 「暂无数据」，
          不编造接口调用。等端点补齐后在同一张表里换数据源即可。 */}
      <section className="panel" data-testid="project-detail-panel-history">
        <PanelHead title="构建历史" />
        <div className="panel-body">
          <table className="data-table" data-testid="project-detail-history-table">
            <thead>
              <tr>
                <th scope="col">序号</th>
                <th scope="col">时间</th>
                <th scope="col">状态</th>
                <th scope="col">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td className="build-history-empty" colSpan={4}>
                  暂无数据
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      {/* ---------- 构建入口 ----------
          「构建项目」点一下即挂 BuildLogPanel；`api.execute({ code })` 由面板内部
          的 `useBuildLog.startStream()` 在 mount 时发出（它必须与 ws 连接同批启动，
          顺序不能反）。本页**不再单独调 execute** —— 两次 execute 会被后端
          ProjectJobServiceImpl 的并发检查拒绝（同一 projectCode 已有 Executing 任务
          → Server_Error），用户看到的是误导性的「构建失败」。 */}
      <div className="build-bar">
        <button
          type="button"
          className="btn-build"
          data-testid="project-detail-build"
          disabled={building}
          onClick={() => setBuilding(true)}
        >
          {building ? '正在构建…' : '构建项目'}
        </button>
      </div>

      {building ? (
        <BuildLogPanel
          projectCode={code}
          /*
            autoStart：首次挂载发 execute；面板关闭（done 自动收起 / 用户点「关闭」）
            后 onClosed 会把标记复位，故下一次点击仍是一次全新的构建。
            与 ProjectListPage 同一套「首次启动 / 不重启」写法。
          */
          autoStart={!buildPanelMountedRef.current}
          onMounted={() => {
            buildPanelMountedRef.current = true
          }}
          onClosed={() => {
            buildPanelMountedRef.current = false
            setBuilding(false)
          }}
        />
      ) : null}
    </div>
  )
}