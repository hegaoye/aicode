import { useEffect, useRef, useState } from 'react'
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
  /**
   * mount 时是否自动触发 `startStream()`（默认 `true`）。
   *
   * <p>**何时传 `false`**：列表页翻回"构建中"所在页时，`BuildLogPanel` 会重新挂载。
   * 若此时自动 `startStream()`，会重新调 `api.execute({ code })` —— 而后端
   * `ProjectJobServiceImpl#execute` 有并发检查（同一 projectCode 已有 `Executing`
   * 任务则抛 `Server_Error`），用户会看到误导性的"构建失败"。</p>
   *
   * <p>调用方（如 `ProjectListPage`）用它区分"首次点击构建"与"翻回已构建页"。</p>
   */
  autoStart?: boolean
  /**
   * 首次挂载后回调一次（每次 mount 调一次）。调用方用它把 code 记进
   * "已挂载过"集合，从而在下次挂载时传 `autoStart={false}`。
   * 与 `autoStart` 配合实现"首次启动 / 翻回不重启"。
   */
  onMounted?: () => void
  /**
   * **面板从 DOM 消失时**回调一次（2026-10-18-building-codes-release-on-terminal）。
   *
   * <p>两条触发路径，二者对调用方的意义完全相同（该行的构建流程已结束或已被放弃）：</p>
   * <ul>
   *   <li>构建到达终态 —— {@code done} 时本面板自动收起（见组件内的注释）；</li>
   *   <li>用户点面板上的「关闭」按钮 —— 触发 {@code useBuildLog().clear()}。</li>
   * </ul>
   *
   * <p>调用方（{@code ProjectListPage}）据此释放该行的「构建中」态 ——
   * 否则该行会**永久**卡在禁用态、失去再次构建的能力（构建一次即报废）。</p>
   *
   * <p>⚠️ <b>不会</b>在「翻回正在构建的行」时触发 —— 那种情况下本面板以
   * {@code autoStart={false}} 挂载、{@code status} 初始即 {@code idle}，
   * 若无守卫会立刻误触发，让用户得以重复触发构建（后端并发检查会拒绝）。</p>
   */
  onClosed?: (projectCode: string) => void
}

function formatElapsed(ms: number): string {
  const total = Math.floor(ms / 1000)
  const m = Math.floor(total / 60)
  const s = total % 60
  return m > 0 ? `${m}m ${s}s` : `${s}s`
}

export default function BuildLogPanel({
  projectCode,
  autoStart = true,
  onMounted,
  onClosed,
}: Props) {
  const { status, entries, startedAt, stage, startStream, clear } = useBuildLog(projectCode)
  const [now, setNow] = useState<number>(Date.now())

  // 每次 mount 通知调用方一次。声明在 autoStart 效果之前 —— 顺序不改变
  // autoStart 的取值（prop 在 render 期已定型），仅为可读性。
  useEffect(() => {
    onMounted?.()
    // 仅在 mount 时触发一次；onMounted 是内联箭头函数，故意不进依赖
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  /**
   * 「面板曾显示过内容，现在消失了」的守卫 —— `onClosed` 的触发条件。
   *
   * <p><b>为什么不能简单判「当前不是 running」</b>：翻回正在构建的行时，本面板以
   * `autoStart={false}` 挂载（调用方的 `mountedPanelsRef` 已有该 code），
   * `useBuildLog` 的 `status` 初始即 `idle`，面板本就渲染 `null`。
   * 若那样写，翻回的**第一次渲染**就会触发 `onClosed` → 释放该行的进行态 →
   * 用户可重复触发构建（后端 Executing 检查拒绝，用户看到「点了没反应」）。</p>
   *
   * <p>故用 `hasShownRef`：只有「本面板实例确实显示过运行态」才认为它「消失过」。
   * 触发后复位，使同一个面板实例最多触发一次。</p>
   */
  const hasShownRef = useRef(false)
  useEffect(() => {
    if (status === 'running') {
      hasShownRef.current = true
      return
    }
    // error 刻意不触发：面板保留让用户看失败原因，用户点「关闭」后 status 转 idle 才触发
    if (status === 'idle' || status === 'done') {
      if (hasShownRef.current) {
        hasShownRef.current = false
        onClosed?.(projectCode ?? '')
      }
    }
    // onClosed 是内联箭头函数，故意不进依赖（否则每次父组件渲染都会重跑本效果）
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [status, projectCode])

  // mount 时自动启动构建流（autoStart=false 时不启 —— 见 Props 的 Javadoc）
  useEffect(() => {
    if (projectCode && autoStart) {
      void startStream()
    }
    // startStream 是稳定引用；projectCode / autoStart 变化才重启动
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [projectCode, autoStart])

  useEffect(() => {
    if (status !== 'running' || startedAt === null) {
      return
    }
    const id = setInterval(() => setNow(Date.now()), 1000)
    return () => clearInterval(id)
  }, [status, startedAt])

  /**
 * 自动收起 —— 2026-10-18-build-log-terminal-signal。
 *
 * <p>此前只判 `idle`，所以构建**成功**后���板一直挂在列表行下方，用户不知道好了没、
 * 只能自己点「关闭」。那时没有可用终态信号（服务端虽有 `Finished: SUCCESS`，
 * 但本 hook 从不识别），故 design D1 把"用户主动关闭"写成了既定设计。</p>
 *
 * <p><b>⚠️ 只在 `done`（成功）时收起，`error` 刻意保留</b>：构建失败时用户<b>需要</b>
 * 看到失败原因与 ❌ 徽章 —— 把错误面板自动藏掉是反直觉的，且会让"构建失败"看起来
 * 像是"什么都没发生"。用户看完错误后仍可点「关闭」手动收起。</p>
 *
 * <p><b>自动收起 ≠ clear</b>：`clear()` 会清空日志条目，手动关闭按钮仍走它。
 * 收起只是「从 DOM 移除」，日志仍留在 hook 的 state 里，重新打开可见。</p>
 */
if (status === 'idle' || status === 'done') {
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
      {/* 构建阶段（2026-10-18-build-stage-progress）：此前用户只能盯着一串滚动日志，
          不知道"到哪一步了、是不是卡住了"。这里显示「第 N/7 步：<步骤名>」+ 进度条。
          stage 为 null（未收到阶段事件）时不显示，不占位。 */}
      {stage ? (
        <div data-testid="build-stage" style={{ margin: 'var(--space-2) 0' }}>
          {stage.total > 0 ? (
            <p className="muted" data-testid="build-stage-text">
              第 {stage.current}/{stage.total} 步：{stage.message ?? stage.phase ?? ''}
            </p>
          ) : (
            <p className="muted" data-testid="build-stage-text">
              {stage.message ?? stage.phase ?? '处理中…'}
            </p>
          )}
          {stage.total > 0 ? (
            <progress
              data-testid="build-stage-bar"
              value={stage.current}
              max={stage.total}
              style={{ width: '100%' }}
            />
          ) : null}
        </div>
      ) : null}
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
