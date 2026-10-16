import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { unwrapSuccess } from '../../lib/contract-response'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import type { ProjectSqlVO } from '../../generated/model/projectSqlVO'
import type { MapClassTableVO } from '../../generated/model/mapClassTableVO'
import { useInitProgress } from '../../lib/realtime/use-init-progress'

/**
 * `ProjectSqlPanel` —— `ProjectDetailPage` 的「SQL 与解析」区块。
 *
 * <p>补上代码生成配置流程的第 ②③ 步（2026-10-18-project-create-and-sql-init）：
 * 导入建表 SQL → 触发逆解析 → 看解析出哪些表对应哪些类。</p>
 *
 * <p><b>2026-10-18 异步化</b>（init-async-progress）：「解析」不再同步阻塞 ——
 * 后端改为「立即接受 + WebSocket 推结构化进度」，本组件用 `useInitProgress` 消费。
 * 进度与终态的消息形态、失败语义见该变更的 spec。</p>
 *
 * <p>三个端点：</p>
 * <ul>
 *   <li>`POST /project/sql/build`（`projectCode` + `tsql`）—— 新增一段 SQL；</li>
 *   <li>`POST /project/init`（`code`）—— 触发逆解析（**异步**，进度经 ws）；</li>
 *   <li>`GET /project/relationship/listMapClassTable`（`projectCode`）—— 读解析结果。</li>
 * </ul>
 *
 * <p><b>已知局限（design D5）</b>：建库阶段内部无可切分的进度点（它一次性执行用户的全部
 * 建表 SQL），后端如实报 {@code total=0}，故此时显示不确定进度文案而非空进度条。</p>
 *
 * <p><b>为何不列出现有 SQL 段</b>：后端没有「按项目列出所有 SQL 段」的端点 ——
 * `GET /project/sql/load` 需要单个 `code`。本区块只展示本次会话中新加的段，
 * 刷新后消失。这是后端能力缺口，不是本实现偷懒。</p>
 *
 * <p><b>视觉基准</b>：OpenDesign 原型 `wizard-step2-sql.html`。编辑器外壳
 * （`.editor-wrap` / `.editor-bar` / `.editor` / `.gutter`）逐字沿用原型类名与数值，
 * 样式集中在 `styles/ProjectSqlPanel.css`。原型硬编码了 28 行行号，实现必须随内容
 * 动态计算 —— 否则用户粘贴超过 28 行就整体错位。</p>
 */

interface Props {
  /** 项目编码：取自 URL 的 `:code`，用作 `projectCode` 入参。 */
  projectCode: string
}

export default function ProjectSqlPanel({ projectCode }: Props) {
  const api = useMemo(() => getAicodeRESTfulAPIs(), [])
  const [tsql, setTsql] = useState('')
  /** 本次会话新增的 SQL 段（刷新即丢 —— 后端无「按项目列表」端点）。 */
  const [addedSegments, setAddedSegments] = useState<ProjectSqlVO[]>([])
  const [saving, setSaving] = useState(false)
  const [saveError, setSaveError] = useState<string | null>(null)
  const [mapTables, setMapTables] = useState<MapClassTableVO[]>([])
  const [parseResultState, setParseResultState] = useState<'loading' | 'done'>('loading')
  /** 行号槽的 ref —— textarea 滚动时把 scrollTop 同步过去，行号才不会与代码错位。 */
  const gutterRef = useRef<HTMLDivElement>(null)

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

  // 解析成功 → 重读结果（init 可能刚刚产出了新的表 → 类映射）。
  // 这是 done 的唯一触发时机：改造前是"init 调用返回后"，
  // 改造后必须等 ws 的 done，因为解析在异步线程上跑。
  const onParseDone = useCallback(() => {
    loadParseResult()
  }, [loadParseResult])

  const progress = useInitProgress(onParseDone)

  const handleSaveSql = useCallback(async () => {
    if (!tsql.trim() || saving) {
      return
    }
    setSaving(true)
    setSaveError(null)
    try {
      const resp = await api.build({ projectCode, tsql })
      const saved = unwrapSuccess(resp)
      setAddedSegments((prev) => [...prev, saved])
      setTsql('')
    } catch (err: unknown) {
      setSaveError(err instanceof Error ? err.message : String(err))
    } finally {
      setSaving(false)
    }
  }, [api, projectCode, saving, tsql])

  const handleParse = useCallback(() => {
    if (!projectCode) {
      return
    }
    // 异步：先连 ws 再调 init（由 useInitProgress 内部保证顺序），
    // 进度与终态经 ws 到达，故这里不 await 也不处理 HTTP 失败（那在 hook 里）
    void progress.start(projectCode)
  }, [progress, projectCode])

  const parseBusy =
    progress.status === 'connecting' || progress.status === 'running'
  const showProgressArea =
    progress.status !== 'idle' && progress.status !== 'done'
  const progressPercent =
    progress.total > 0 ? Math.round((progress.current / progress.total) * 100) : 0

  // 行号随内容动态计算（原型硬编码 28 行是静态稿做法，粘贴超过 28 行就错位）。
  // 空内容算 1 行 —— 空 textarea 在视觉上就是一行。
  const lineCount = tsql === '' ? 1 : tsql.split('\n').length
  const lineNumbers = useMemo(
    () => Array.from({ length: lineCount }, (_, i) => i + 1),
    [lineCount],
  )

  // textarea 是唯一真正滚动的容器（.gutter 自己 overflow:hidden），
  // 故滚动时同步行号槽的 scrollTop。
  const syncGutterScroll = useCallback((e: React.UIEvent<HTMLTextAreaElement>) => {
    const gutter = gutterRef.current
    if (gutter) {
      gutter.scrollTop = e.currentTarget.scrollTop
    }
  }, [])

  return (
    <section data-testid="project-sql-panel">
      {/* ---------- SQL 编辑器（原型 .editor-wrap） ---------- */}
      <div className="editor-wrap">
        <div className="editor-bar">
          <span className="tab">
            <span className="dot live" aria-hidden="true" />
            <span className="name">schema.sql</span>
          </span>
          <span className="meta">UTF-8 · {lineCount} 行 · MySQL</span>
        </div>
        <div className="editor">
          <div className="gutter" ref={gutterRef} aria-hidden="true">
            {lineNumbers.map((n) => (
              <span key={n}>{n}</span>
            ))}
          </div>
          <textarea
            data-testid="project-sql-tsql"
            aria-label="建表 SQL 脚本"
            spellCheck={false}
            placeholder="在此粘贴或编辑 SQL 脚本…"
            value={tsql}
            onChange={(e) => setTsql(e.target.value)}
            onScroll={syncGutterScroll}
          />
        </div>
      </div>

      <div className="form-foot">
        <button
          type="button"
          className="btn-ghost"
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
          disabled={parseBusy}
          onClick={handleParse}
        >
          {parseBusy ? '解析中…' : '初始化数据库并进入下一步'}
        </button>
      </div>

      {saveError ? (
        <p role="alert" className="sql-error" data-testid="project-sql-error">
          {saveError}
        </p>
      ) : null}

      {/* ---------- 异步进度区 ----------
          三条语义防线：total=0 显示不确定进度文案（不得出 0% 空进度条）；
          error 时进度区保留不自动消失，让用户看清失败位置。 */}
      {showProgressArea ? (
        <div
          className="parse-progress"
          data-testid="project-parse-progress"
          data-status={progress.status}
        >
          {progress.indeterminate ? (
            // total 未知（建库阶段内部无可切分点）—— 显示文案而非 0/0 的空进度条
            <p className="parse-indeterminate" data-testid="project-parse-indeterminate">
              {progress.message || '正在处理…'}
            </p>
          ) : (
            <div
              className="parse-bar"
              data-testid="project-parse-bar"
              role="progressbar"
              aria-valuemin={0}
              aria-valuenow={progress.current}
              aria-valuemax={progress.total > 0 ? progress.total : 1}
            >
              <div className="parse-bar-fill" style={{ width: `${progressPercent}%` }} />
            </div>
          )}
          {/* error 态由下面的 parse-error 承载同一句话，此处不再重复一遍 */}
          {progress.status !== 'error' ? (
            <p className="parse-msg" data-testid="project-parse-message">
              {progress.message}
            </p>
          ) : null}
          {progress.status === 'error' ? (
            <p className="parse-error" role="alert" data-testid="project-parse-error">
              {progress.message}
            </p>
          ) : null}
          {!progress.indeterminate && progress.total > 0 ? (
            <small className="parse-count" data-testid="project-parse-count">
              {progress.current}/{progress.total}（{progressPercent}%）
            </small>
          ) : null}
          {progress.status === 'error' ? (
            <button
              type="button"
              className="btn-ghost parse-dismiss"
              data-testid="project-parse-dismiss"
              onClick={progress.clear}
            >
              关闭
            </button>
          ) : null}
        </div>
      ) : null}

      {progress.status === 'done' ? (
        <p className="parse-done" data-testid="project-parse-done">
          {progress.message || '解析完成'}
        </p>
      ) : null}

      {addedSegments.length > 0 ? (
        <ul className="sql-saved" data-testid="project-sql-saved">
          {addedSegments.map((seg) => (
            <li key={seg.code ?? Math.random().toString()}>
              已保存 SQL 段 <code>{seg.code ?? '—'}</code>（{seg.state ?? '—'}）
            </li>
          ))}
        </ul>
      ) : null}

      <div className="sql-result">
        <div className="section-title">解析结果</div>
        {parseResultState === 'loading' ? (
          <p className="muted">读取中…</p>
        ) : mapTables.length === 0 ? (
          <p className="muted" data-testid="project-sql-nothing-parsed">
            尚未解析出表 —— 点上方「初始化数据库并进入下一步」让 aicode 逆解析你导入的 SQL。
          </p>
        ) : (
          <div className="table-card">
            <table className="data-table" data-testid="project-sql-parsed">
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
          </div>
        )}
      </div>
    </section>
  )
}
