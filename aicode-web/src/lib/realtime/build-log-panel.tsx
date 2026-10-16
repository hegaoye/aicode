import { useEffect, useState } from 'react'
import { useBuildLog } from './use-build-log'

/**
 * `BuildLogPanel` —— useBuildLog hook 的浅渲染。
 *
 * 详见 `openspec/specs/aicode-web-realtime/spec.md` 的 Requirement
 * "BuildLogPanel SHALL 是 useBuildLog 的浅渲染，不持业务逻辑"。
 *
 * 状态机：
 * - `idle`：`return null`（不渲染任何 DOM —— 按钮已是 idle 视觉）
 * - `running`：标题 + 已运行时长 + entries + 关闭按钮
 * - `error`：上述 + ❌ 徽章
 *
 * 关闭按钮调 `useBuildLog().clear()`，转 idle；`running → idle` 由用户
 * 主动关闭触发（不依赖 "[DONE]" sentinel，详见 design D1）。
 */

interface Props {
  projectCode: string | null | undefined
}

function formatElapsed(ms: number): string {
  const total = Math.floor(ms / 1000)
  const m = Math.floor(total / 60)
  const s = total % 60
  return m > 0 ? `${m}m ${s}s` : `${s}s`
}

export default function BuildLogPanel({ projectCode }: Props) {
  const { status, entries, startedAt, startStream, clear } = useBuildLog(projectCode)
  const [now, setNow] = useState<number>(Date.now())

  // mount 时自动启动构建流
  useEffect(() => {
    if (projectCode) {
      void startStream()
    }
    // startStream 是稳定引用；projectCode 变化才重启动
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [projectCode])

  useEffect(() => {
    if (status !== 'running' || startedAt === null) {
      return
    }
    const id = setInterval(() => setNow(Date.now()), 1000)
    return () => clearInterval(id)
  }, [status, startedAt])

  if (status === 'idle') {
    return null
  }

  const elapsed = startedAt === null ? 0 : Math.max(0, now - startedAt)

  return (
    <section
      data-testid="build-log-panel"
      aria-label={`构建日志 ${projectCode ?? ''}`}
      style={{
        marginTop: 'var(--space-2)',
        padding: 'var(--space-3)',
        background: 'var(--tip-bg)',
        borderRadius: 'var(--radius)',
        maxHeight: '20rem',
        overflowY: 'auto',
      }}
    >
      <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <strong>
          构建日志：<code>{projectCode ?? '—'}</code>{' '}
          {status === 'error' ? (
            <span data-testid="build-log-status-error" aria-label="失败">
              ❌
            </span>
          ) : (
            <span aria-hidden>⏳</span>
          )}
        </strong>
        <button
          type="button"
          className="btn-primary"
          data-testid="build-log-close"
          onClick={clear}
          style={{ padding: '2px 8px' }}
        >
          关闭
        </button>
      </header>
      <p className="muted" style={{ margin: 'var(--space-2) 0' }}>
        已运行 {formatElapsed(elapsed)}
      </p>
      <ol style={{ margin: 0, paddingLeft: '1.5rem' }}>
        {entries.slice().reverse().map((entry, idx) => (
          <li key={entry.at + '-' + idx} data-testid="build-log-entry">
            {entry.text}
          </li>
        ))}
      </ol>
    </section>
  )
}
