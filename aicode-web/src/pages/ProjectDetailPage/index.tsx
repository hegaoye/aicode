import { useEffect, useMemo, useState } from 'react'
import { useParams } from 'react-router-dom'
import { ContractError, unwrapSuccess } from '../../lib/contract-response'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import type { ProjectVO } from '../../generated/model/projectVO'

/**
 * `ProjectDetailPage` —— M3-A 第 2 阶段：契约驱动的项目详情页。
 *
 * 数据流：
 *   1. `useParams<{ code: string }>()` 取 URL `:code` 入参；
 *   2. `getAicodeRESTfulAPIs().loadByCode(code)` 拿到
 *      `AxiosResponse<RProjectVO>`（M3-A-1 已统一包络）；
 *   3. `unwrapSuccess(resp)` 拆出 `ProjectVO`；失败抛 `ContractError`；
 *   4. 按"标识 + 细节 + 时间 + 关联"四组，键值列表渲染。
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
 */

type LoadState =
  | { kind: 'loading' }
  | { kind: 'ready'; data: ProjectVO | null }
  | { kind: 'error'; error: ContractError }

/**
 * 显示约定：把 `ProjectVO` 的 22 个持久化字段按用户语义分组。
 * 字段名直接来自后端（即"V 字段"），不在前端再翻译；表格展示用
 * `<dl>/<dt>/<dd>` 语义，让浏览器原生可读、对辅助技术友好。
 */
const FIELD_GROUPS: Array<{ title: string; fields: Array<keyof ProjectVO> }> = [
  {
    title: '标识',
    fields: ['code', 'name', 'englishName'],
  },
  {
    title: '细节',
    fields: [
      'description',
      'databaseType',
      'language',
      'state',
      'copyright',
      'author',
      'phone',
      'basePackage',
      'sqlFile',
      'downloadUrl',
      'isRepository',
      'isParseTable',
      'isParseClass',
      'isIncrement',
    ],
  },
  {
    title: '技术计数',
    fields: ['buildNumber'],
  },
  {
    title: '时间',
    fields: ['createTime', 'updateTime'],
  },
  {
    title: '关联',
    fields: ['accountCode'],
  },
]

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

export default function ProjectDetailPage() {
  const params = useParams<{ code: string }>()
  const code = params.code ?? ''
  // 全局 axios 实例在 orval 生成物内部使用即可；每次渲染创建一份 api 是廉价的
  const api = useMemo(() => getAicodeRESTfulAPIs(), [])
  const [state, setState] = useState<LoadState>({ kind: 'loading' })

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
        // unwrapSuccess 的返回类型是 NonNullable<ProjectVO>，但实际运行时 data 可能为 null
        // （M3-A-1 引入的"找不到 project"早返路径）—— 视作业务空态
        setState({ kind: 'ready', data: (projectVO ?? null) as ProjectVO | null })
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

  if (state.kind === 'loading') {
    return (
      <section aria-busy="true">
        <h1>项目详情</h1>
        <p className="muted">加载中…</p>
      </section>
    )
  }

  if (state.kind === 'error') {
    return (
      <section>
        <h1>项目详情</h1>
        <p role="alert" data-testid="project-detail-error">
          加载失败：{state.error.info || state.error.code}
        </p>
      </section>
    )
  }

  if (state.kind === 'ready' && state.data === null) {
    return (
      <section>
        <h1>项目详情</h1>
        <p className="muted" data-testid="project-detail-empty">
          项目不存在（code：<code>{code}</code>）
        </p>
      </section>
    )
  }

  // state.kind === 'ready' && state.data !== null —— 真实数据
  const data = (state as { kind: 'ready'; data: ProjectVO }).data

  return (
    <section data-testid="project-detail-content">
      <h1>项目详情</h1>
      <p className="muted">
        code: <code>{data.code ?? '—'}</code>
      </p>
      {FIELD_GROUPS.map((group) => (
        <section key={group.title}>
          <h2>{group.title}</h2>
          <dl>
            {group.fields.map((field) => (
              <div
                key={field}
                style={{ display: 'flex', gap: 'var(--space-4)' }}
                data-testid={`project-detail-field-${field}`}
              >
                <dt style={{ minWidth: '12em', color: 'var(--muted)' }}>{field}</dt>
                <dd style={{ flex: 1 }}>{renderValue(data[field])}</dd>
              </div>
            ))}
          </dl>
        </section>
      ))}
    </section>
  )
}
