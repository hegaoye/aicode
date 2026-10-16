import { useCallback, useEffect, useMemo, useState } from 'react'
import { ContractError, unwrapSuccess } from '../../lib/contract-response'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import type { ProjectSqlVO } from '../../generated/model/projectSqlVO'
import type { MapClassTableVO } from '../../generated/model/mapClassTableVO'

/**
 * `ProjectSqlPanel` —— `ProjectDetailPage` 的「SQL 与解析」区块。
 *
 * <p>补上代码生成配置流程的第 ②③ 步（2026-10-18-project-create-and-sql-init）：
 * 导入建表 SQL → 触发逆解析 → 看解析出哪些表对应哪些类。</p>
 *
 * <p>三个端点：</p>
 * <ul>
 *   <li>`POST /project/sql/build`（`projectCode` + `tsql`）—— 新增一段 SQL；</li>
 *   <li>`POST /project/init`（`code`）—— 触发逆解析（建库 + 跑 SQL + 解析元数据）；</li>
 *   <li>`GET /project/relationship/listMapClassTable`（`projectCode`）—— 读解析结果。</li>
 * </ul>
 *
 * <p><b>已知局限（design D5）</b>：`init` 是**同步阻塞**的 HTTP 请求 —— 后端
 * `ProjectServiceImpl.execute()` 在请求线程里建库、跑 SQL、解析元数据，耗时
 * 取决于 SQL 复杂度（可能数秒）。这里只能显示「解析中…」并禁用按钮，无法真正
 * 异步化。彻底解法是后端改 `@Async` + WebSocket 推进度，属独立技术债。</p>
 *
 * <p><b>为何不列出现有 SQL 段</b>：后端没有「按项目列出所有 SQL 段」的端点 ——
 * `GET /project/sql/load` 需要单个 `code`。本区块只展示本次会话中新加的段，
 * 刷新后消失。这是后端能力缺口，不是本实现偷懒。</p>
 */

interface Props {
  /** 项目编码：取自 URL 的 `:code`，用作 `projectCode` 入参。 */
  projectCode: string
}

type ParseState =
  | { kind: 'idle' }
  | { kind: 'parsing' }
  | { kind: 'error'; message: string }

/** 解析结果：表 → 类的映射列表（空数组 = 尚未解析或 SQL 未产出表）。 */
type ParseResultState = 'loading' | 'done'

export default function ProjectSqlPanel({ projectCode }: Props) {
  const api = useMemo(() => getAicodeRESTfulAPIs(), [])
  const [tsql, setTsql] = useState('')
  /** 本次会话新增的 SQL 段（刷新即丢 —— 后端无「按项目列表」端点）。 */
  const [addedSegments, setAddedSegments] = useState<ProjectSqlVO[]>([])
  const [saving, setSaving] = useState(false)
  const [parseState, setParseState] = useState<ParseState>({ kind: 'idle' })
  // 用 MapClassTableVO（6 字段）而非 MapClassTable 实体 —— 后者虽仍留在契约
  // components 里（其它端点可能用），但本端点已改返 VO；用实体类型会让
  // 类型检查通过而运行时字段对不上（典型的"契约与运行时不一致"）。
  const [mapTables, setMapTables] = useState<MapClassTableVO[]>([])
  const [parseResultState, setParseResultState] = useState<ParseResultState>('loading')

  const loadParseResult = useCallback(() => {
    if (!projectCode) {
      return
    }
    setParseResultState('loading')
    api
      .listMapClassTable({ projectCode })
      .then((resp) => {
        const list = unwrapSuccess(resp)
        // 后端返回的是裸 List（无 PageVO 包络），可能为 null
        setMapTables((list ?? []) as MapClassTableVO[])
        setParseResultState('done')
      })
      .catch(() => {
        // 解析结果读不到不该阻断主流程 —— 用户仍可继续导 SQL / 重新解析
        setMapTables([])
        setParseResultState('done')
      })
  }, [api, projectCode])

  useEffect(() => {
    loadParseResult()
  }, [loadParseResult])

  const handleSaveSql = useCallback(async () => {
    if (!tsql.trim() || saving) {
      return
    }
    setSaving(true)
    try {
      const resp = await api.build({ projectCode, tsql })
      const saved = unwrapSuccess(resp)
      setAddedSegments((prev) => [...prev, saved])
      setTsql('')
    } catch (err: unknown) {
      setParseState({
        kind: 'error',
        message: err instanceof ContractError ? err.info || err.code : String(err),
      })
    } finally {
      setSaving(false)
    }
  }, [api, projectCode, saving, tsql])

  const handleParse = useCallback(async () => {
    if (parseState.kind === 'parsing') {
      return
    }
    setParseState({ kind: 'parsing' })
    try {
      const resp = await api.init({ code: projectCode })
      // 必须经 unwrapSuccess：axios 对 HTTP 200 不会 reject，后端 R.failed
      // （如建表失败 9999）同样 resolve，不解包就会把失败当成功。
      unwrapSuccess(resp)
      setParseState({ kind: 'idle' })
      // 解析完成后重读结果 —— init 可能刚刚产出了新的表 → 类映射
      loadParseResult()
    } catch (err: unknown) {
      setParseState({
        kind: 'error',
        message:
          err instanceof ContractError
            ? `解析失败：${err.info || err.code}`
            : `解析失败：${String(err)}`,
      })
    }
  }, [api, loadParseResult, parseState.kind, projectCode])

  return (
    <section data-testid="project-sql-panel">
      <h2>SQL 与解析</h2>

      <label htmlFor="project-sql-tsql" style={{ color: 'var(--muted)' }}>
        建表 SQL 脚本
      </label>
      <textarea
        id="project-sql-tsql"
        data-testid="project-sql-tsql"
        rows={6}
        style={{ width: '100%', marginTop: 'var(--space-2)' }}
        placeholder={'CREATE TABLE user (\n  id BIGINT PRIMARY KEY,\n  name VARCHAR(64)\n);'}
        value={tsql}
        onChange={(e) => setTsql(e.target.value)}
      />
      <div style={{ display: 'flex', gap: 'var(--space-3)', marginTop: 'var(--space-2)' }}>
        <button
          type="button"
          className="btn-primary"
          data-testid="project-sql-save"
          disabled={saving || !tsql.trim()}
          onClick={() => void handleSaveSql()}
        >
          {saving ? '保存中…' : '保存 SQL'}
        </button>
        <button
          type="button"
          className="btn-primary"
          data-testid="project-sql-parse"
          disabled={parseState.kind === 'parsing'}
          onClick={() => void handleParse()}
        >
          {parseState.kind === 'parsing' ? '解析中…' : '解析'}
        </button>
      </div>

      {parseState.kind === 'error' ? (
        <p role="alert" data-testid="project-sql-error" style={{ color: 'var(--accent)' }}>
          {parseState.message}
        </p>
      ) : null}

      {addedSegments.length > 0 ? (
        <ul data-testid="project-sql-saved" style={{ marginTop: 'var(--space-3)' }}>
          {addedSegments.map((seg) => (
            <li key={seg.code ?? Math.random().toString()}>
              已保存 SQL 段 <code>{seg.code ?? '—'}</code>（{seg.state ?? '—'}）
            </li>
          ))}
        </ul>
      ) : null}

      <section style={{ marginTop: 'var(--space-4)' }}>
        <h3>解析结果</h3>
        {parseResultState === 'loading' ? (
          <p className="muted">读取中…</p>
        ) : mapTables.length === 0 ? (
          <p className="muted" data-testid="project-sql-nothing-parsed">
            尚未解析出表 —— 点上方「解析」让 aicode 逆解析你导入的 SQL。
          </p>
        ) : (
          <table data-testid="project-sql-parsed">
            <thead>
              <tr>
                <th scope="col">表名</th>
                <th scope="col">类名</th>
                <th scope="col">前端命名</th>
              </tr>
            </thead>
            <tbody>
              {mapTables.map((t) => (
                <tr key={t.code ?? t.tableName ?? Math.random().toString()}>
                  <td>{t.tableName ?? '—'}</td>
                  <td>{t.className ?? '—'}</td>
                  <td>{t.dashedCaseName ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </section>
  )
}