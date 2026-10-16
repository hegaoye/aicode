import { useCallback, useEffect, useMemo, useState } from 'react'
import { ContractError, unwrapSuccess } from '../../lib/contract-response'
import { DisplayControl } from '../../lib/display-controls'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import type { MapFieldColumnVO } from '../../generated/model/mapFieldColumnVO'
import DisplayAttributeForm from './DisplayAttributeForm'

/**
 * 元数据展示属性列表 —— M1 接入的首个契约驱动业务页面。
 *
 * 数据流：
 *   1. `getAicodeRESTfulAPIs().list5(...)` 拿到 `AxiosResponse<R<List<MapFieldColumnVO>>>`；
 *   2. `unwrapSuccess` 把它拆成 `MapFieldColumnVO[]`，失败时抛 `ContractError`；
 *   3. 每个字段用 `<DisplayControl>` 按 `displayAttribute.displayType` 分发渲染。
 *
 * 业务页面不直接访问 `resp.data.data` —— 这是封装层存在的目的。
 *
 * 同源开发链路：`npm run dev` 时 Vite proxy 把 `/displayAttribute/*` 转到
 * `:8080`（详见 `vite.config.ts`），无需 CORS 配置；后端未启动时单元测试
 * 由 MSW 拦截（`npm test`）。
 */

type LoadState =
  | { kind: 'loading' }
  | { kind: 'ready'; rows: MapFieldColumnVO[] }
  | { kind: 'error'; error: ContractError }

export default function DisplayAttributesPage() {
  // 全局 axios 实例在 orval 生成物内部使用即可；每次渲染创建一份 api 是廉价的。
  const api = useMemo(() => getAicodeRESTfulAPIs(), [])
  const [state, setState] = useState<LoadState>({ kind: 'loading' })
  /**
   * 编辑中的行 —— 与 `state.rows` 分离，避免编辑中的未保存值污染读态表格
   * （表格是「当前已存配置」的预览，用户改了但没保存时不该看到中间态）。
   */
  const [rows, setRows] = useState<MapFieldColumnVO[]>([])
  const [saving, setSaving] = useState(false)
  const [errorMsg, setErrorMsg] = useState<string | null>(null)

  /** 重新拉取列表 —— 读态与编辑态一起刷新。 */
  const reload = useCallback(() => {
    return api
      .list5({ mapClassTableCode: '' })
      .then((resp) => {
        const loaded = unwrapSuccess(resp)
        setState({ kind: 'ready', rows: loaded })
        setRows(loaded)
      })
      .catch((err: unknown) => {
        if (err instanceof ContractError) {
          setState({ kind: 'error', error: err })
        } else {
          setState({ kind: 'error', error: new ContractError('UNKNOWN', String(err)) })
        }
      })
  }, [api])

  useEffect(() => {
    let cancelled = false
    api
      .list5({ mapClassTableCode: '' })
      .then((resp) => {
        if (cancelled) {
          return
        }
        const loaded = unwrapSuccess(resp)
        setState({ kind: 'ready', rows: loaded })
        setRows(loaded)
      })
      .catch((err: unknown) => {
        if (cancelled) {
          return
        }
        if (err instanceof ContractError) {
          setState({ kind: 'error', error: err })
        } else {
          setState({ kind: 'error', error: new ContractError('UNKNOWN', String(err)) })
        }
      })
    return () => {
      cancelled = true
    }
  }, [api])

  /**
   * 批量保存全部行（design D4）。
   *
   * <p>后端 `save` 是逐条 upsert 的批量接口，且对找不到 `mapFieldColumn` 的行
   * **静默 `continue`**，**没有事务**。所以「提交成功」≠「全部写入」——
   * 成功后 SHALL 重新拉取列表，让实际结果可见，而不是乐观更新。</p>
   */
  const handleSaveAll = useCallback(async () => {
    if (saving) {
      return
    }
    setSaving(true)
    setErrorMsg(null)
    try {
      const payload = rows.map((row) => ({
        ...(row.displayAttribute ?? {}),
        mapFieldColumnCode: row.code ?? '',
        mapClassTableCode: row.mapClassTableCode ?? '',
      }))
      const resp = await api.build6(payload)
      // 必须 unwrapSuccess：axios 对 HTTP 200 不 reject，后端 R.failed 同样 resolve
      unwrapSuccess(resp)
      // 重拉而非乐观更新 —— 见上
      await reload()
    } catch (err: unknown) {
      setErrorMsg(
        err instanceof ContractError ? `保存失败：${err.info || err.code}` : '保存失败',
      )
    } finally {
      setSaving(false)
    }
  }, [api, reload, rows, saving])

  if (state.kind === 'loading') {
    return (
      <div className="container">
        <section className="panel" aria-busy="true">
          <div className="panel-head">
            <span className="title">展示属性</span>
          </div>
          <div className="panel-body empty-state">加载中…</div>
        </section>
      </div>
    )
  }

  if (state.kind === 'error') {
    return (
      <div className="container">
        <section className="panel">
          <div className="panel-head">
            <span className="title">展示属性</span>
          </div>
          <div className="panel-body">
            <p role="alert" className="field-error show" data-testid="display-attributes-error">
              加载失败：{state.error.info || state.error.code}
            </p>
          </div>
        </section>
      </div>
    )
  }

  return (
    <div className="container">
      <div className="page-head">
        <div>
          <h1>展示属性</h1>
          <p className="meta">字段的显示名与控件类型 —— 生成代码里表单的来源</p>
        </div>
      </div>

      <div className="tip-panel">
        <div className="tip-title">操作提示</div>
        <ul>
          <li>展示属性决定生成代码里表单的控件与校验规则</li>
          <li data-testid="display-attr-rebuild-hint">
            改完需重新构建才生效 —— 回到项目详情页重新构建，才能在生成的代码里看到效果
          </li>
        </ul>
      </div>

      {/* ---------- 读态表格：当前已存配置的预览 ---------- */}
      <div className="section-title">字段一览</div>
      <div className="table-card">
        <table className="data-table" aria-label="展示属性列表">
          <thead>
            <tr>
              <th scope="col">数据库列名</th>
              <th scope="col">Java 属性名</th>
              <th scope="col">显示名称</th>
              <th scope="col">控件预览</th>
            </tr>
          </thead>
          <tbody>
            {state.rows.map((row) => {
              const da = row.displayAttribute
              // 以 (mapClassTableCode, code) 复合作为 key，确保行稳定可比较。
              const key = `${row.mapClassTableCode ?? ''}::${row.code ?? ''}`
              return (
                <tr key={key} data-testid="display-attributes-row">
                  <td>
                    <span className="name">{row.column ?? '—'}</span>
                  </td>
                  <td>
                    <span className="name">{row.field ?? '—'}</span>
                  </td>
                  <td>{da?.displayName ?? '—'}</td>
                  <td>
                    {/* .row-actions 是原型的行内操作条形态（inline-flex + 对齐），
                        此处复用它承载行内的控件预览 —— 表格本身是只读的，
                        不引入「点击跳到编辑区」这类新交互。 */}
                    <span className="row-actions">
                      {da ? (
                        <DisplayControl
                          attribute={da}
                          value=""
                          onChange={() => {
                            /* 只读预览；编辑走下方的表单区 */
                          }}
                        />
                      ) : (
                        '—'
                      )}
                    </span>
                  </td>
                </tr>
              )
            })}
            {state.rows.length === 0 ? (
              <tr>
                <td colSpan={4}>（暂无数据）</td>
              </tr>
            ) : null}
          </tbody>
        </table>
      </div>

      {/* ---------- 编辑区（2026-10-18-display-attribute-write） ---------- */}
      <div className="section-title">编辑展示属性</div>
      {rows.length === 0 ? (
        /* 空态不带 panel-head —— 标题已由上面的 .section-title 承担，
           避免「编辑展示属性」在同一屏出现两次。形态取自原型 project-review.html
           的 <div class="panel-body empty-state">尚未配置表模型关系</div>。 */
        <div className="panel">
          <div className="panel-body empty-state" data-testid="display-attr-empty">
            （暂无字段可配置 —— 先在项目详情页导入 SQL 并解析）
          </div>
        </div>
      ) : (
        <>
          {rows.map((row) => {
            const key = `${row.mapClassTableCode ?? ''}::${row.code ?? ''}`
            const da = row.displayAttribute
            return (
              <DisplayAttributeForm
                key={key}
                row={row}
                value={{
                  // 隐藏的定位键（design D6）：让用户编辑它等于改主键，
                  // 出错会静默跳过且无提示
                  mapFieldColumnCode: row.code ?? '',
                  mapClassTableCode: row.mapClassTableCode ?? '',
                  ...(da ?? {}),
                }}
                onChange={(patch) => {
                  // 用函数式更新在**最新** state 上 merge —— 见
                  // DisplayAttributeForm 的 Props.onChange Javadoc：
                  // 收完整对象会因 stale closure 丢改动。
                  setRows((prev) =>
                    prev.map((r) =>
                      `${r.mapClassTableCode ?? ''}::${r.code ?? ''}` === key
                        ? {
                            ...r,
                            displayAttribute: {
                              mapFieldColumnCode: r.code ?? '',
                              mapClassTableCode: r.mapClassTableCode ?? '',
                              ...(r.displayAttribute ?? {}),
                              ...patch,
                            } as never,
                          }
                        : r,
                    ),
                  )
                }}
              />
            )
          })}

          {errorMsg ? (
            <p role="alert" className="field-error show" data-testid="display-attr-error">
              {errorMsg}
            </p>
          ) : null}
          <div className="form-foot">
            <button
              type="button"
              className="btn-primary"
              data-testid="display-attr-save-all"
              disabled={saving}
              onClick={() => void handleSaveAll()}
            >
              {saving ? '保存中…' : '保存全部'}
            </button>
          </div>
        </>
      )}
    </div>
  )
}
