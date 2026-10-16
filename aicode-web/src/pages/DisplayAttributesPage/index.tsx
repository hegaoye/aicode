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
      <table aria-busy="true">
        <caption>展示属性列表（加载中）</caption>
      </table>
    )
  }

  if (state.kind === 'error') {
    return (
      <p role="alert" data-testid="display-attributes-error">
        加载失败：{state.error.info || state.error.code}
      </p>
    )
  }

  return (
    <section>
      <table>
        <caption>展示属性列表</caption>
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
                <td>{row.column ?? '—'}</td>
                <td>{row.field ?? '—'}</td>
                <td>{da?.displayName ?? '—'}</td>
                <td>
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

      {/* ---------- 编辑区（2026-10-18-display-attribute-write） ---------- */}
      <section style={{ marginTop: 'var(--space-5)' }}>
        <h2>编辑展示属性</h2>
        <p className="muted" data-testid="display-attr-rebuild-hint">
          展示属性决定**生成代码**里表单的控件与校验 —— 改完需回到项目详情页重新构建，
          才能在生成的代码里看到效果。
        </p>

        {rows.length === 0 ? (
          <p className="muted" data-testid="display-attr-empty">
            （暂无字段可配置 —— 先在项目详情页导入 SQL 并解析）
          </p>
        ) : (
          <>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 'var(--space-3)' }}>
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
            </div>

            {errorMsg ? (
              <p role="alert" data-testid="display-attr-error" style={{ color: 'var(--accent)' }}>
                {errorMsg}
              </p>
            ) : null}
            <button
              type="button"
              className="btn-primary"
              data-testid="display-attr-save-all"
              disabled={saving}
              onClick={() => void handleSaveAll()}
              style={{ marginTop: 'var(--space-3)' }}
            >
              {saving ? '保存中…' : '保存全部'}
            </button>
          </>
        )}
      </section>
    </section>
  )
}
