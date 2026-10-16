import { useEffect, useMemo, useState } from 'react'
import { ContractError, unwrapSuccess } from '../../lib/contract-response'
import { DisplayControl } from '../../lib/display-controls'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import type { MapFieldColumnVO } from '../../generated/model/mapFieldColumnVO'

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

  useEffect(() => {
    let cancelled = false
    api
      .list5({ mapClassTableCode: '' })
      .then((resp) => {
        if (cancelled) {
          return
        }
        const rows = unwrapSuccess(resp)
        setState({ kind: 'ready', rows })
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
    <table>
      <caption>展示属性列表</caption>
      <thead>
        <tr>
          <th scope="col">数据库列名</th>
          <th scope="col">Java 属性名</th>
          <th scope="col">显示名称</th>
          <th scope="col">控件</th>
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
                      /* M2：编辑表单提交 */
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
  )
}
