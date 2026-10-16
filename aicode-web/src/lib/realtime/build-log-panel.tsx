import { useEffect, useRef, useState } from 'react'
import { useBuildLog } from './use-build-log'

/**
 * `BuildLogPanel` —— useBuildLog hook 的浅渲染。
 *
 * 详见 `openspec/specs/aicode-web-realtime/spec.md` 的 Requirement
 * "BuildLogPanel SHALL 是 useBuildLog 的浅渲染，不持业务逻辑"。
 *
 * 视觉：原型 `build-log.html` 的日志窗（类名与数值见
 * `src/styles/BuildLogPanel.css`，此处不重复说明）。
 *
 * ```
 * .log-wrap
 *   .log-head  → .left(.dot + .name) / .right(已运行时长 + .status + .log-close)
 *   .stage     → 第 N/M 步 + 进度条（stage 为 null 时不渲染，不占位）
 *   .log-body  → 每条日志一行 .ln，按语义分色
 * .footer-bar  → 返回工作台 / 查看生成的文件
 * ```
 *
 * 状态机（**本组件不持业务逻辑，一行未改**）：
 * - `idle`：`return null`（不渲染任何 DOM —— 按钮已是 idle 视觉）
 * - `running`：头部 + 阶段区 + 日志行（每条打 `.ln-event` / `.ln-ok` / `.ln-warn`
 *   / `.ln-error` / `.ln-sql` / `.ln-done` 之一）
 * - `error`：上述 + ❌ 状态徽章与 `.ln-error` 行
 *
 * `done` 时本面板自动收起（见下方注释），因此 `.log-head .status` 的
 * 成功态样式在运行时不会露出 —— 它保留在此只为与原型逐条对得上。
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

/**
 * 日志行的语义分类 —— 把后端的自由文本映射到原型的 6 种行样式。
 *
 * <p>顺序即优先级：**终态 &gt; 失败 &gt; SQL &gt; 警告 &gt; 成功 &gt; 普通**。
 * 失败排在 SQL 与警告之前，因为后端失败日志常带着表名与栈信息
 * （例如「建表失败」会引出后续 SQL 行），先判失败才不会把错误行染成普通灰。</p>
 */
type LineKind = 'event' | 'ok' | 'warn' | 'error' | 'sql' | 'done'

function classifyLine(text: string): LineKind {
  const t = text.trim()
  if (/^#?\s*Finished:\s*SUCCESS/i.test(t)) return 'done'
  if (/Finished:\s*ERROR/i.test(t)) return 'error'
  if (/失败|错误|异常|ERROR|Exception/i.test(t)) return 'error'
  if (/^(CREATE|ALTER|DROP|USE|INSERT|UPDATE|DELETE|TRUNCATE)\b/i.test(t)) return 'sql'
  if (/未配置|未设置|跳过|WARN|警告/i.test(t)) return 'warn'
  // 后端文案里「已提交」有两种写法（「代码已提交 到」/「代码已经提交到」），
  // 故用 `已(?:经)?` 一并覆盖
  if (/【已经生成】|已完成|成功|已(?:经)?(?:提交|打包|推送)/i.test(t)) return 'ok'
  return 'event'
}

/**
 * 后端在日志里**内联 HTML 锚点**（见 `GeneratorSVImpl` 第 251 / 259 行：
 * 仓库地址与 ZIP 下载各自塞了一个 `<a href='...' target='_blank'>…</a>`）。
 *
 * <p>原型把这两行画成 `<span class="ln-link"><a …>…</a></span>`；本组件照此
 * 把内联锚点解析成真正的可点链接 —— **不用 `dangerouslySetInnerHTML`**，
 * 只认 `<a href>` 一种标签，其余一律当纯文本，故后端塞什么都渲染不成标签。</p>
 */
const ANCHOR_RE = /<a\s[^>]*href=(['"])(.*?)\1[^>]*>([\s\S]*?)<\/a>/i

/** 只放行 http/https 与站内相对地址 —— `javascript:` 之类一律降级为纯文本 */
function isSafeHref(href: string): boolean {
  return /^https?:\/\//i.test(href) || href.startsWith('/') || href.startsWith('#')
}

interface ParsedLine {
  /** 锚点之前的纯文本 */
  head: string
  /** 锚点：href + 文案（无锚点时为 null） */
  link: { href: string; text: string } | null
  /** 锚点之后的纯文本 */
  tail: string
}

function parseLine(text: string): ParsedLine {
  const m = ANCHOR_RE.exec(text)
  if (!m || !isSafeHref(m[2] ?? '')) return { head: text, link: null, tail: '' }
  return {
    head: text.slice(0, m.index),
    link: { href: m[2] ?? '', text: m[3] ?? '' },
    tail: text.slice(m.index + m[0].length),
  }
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
  * <p>此前只判 `idle`，所以构建**成功**后面板一直挂在列表行下方，用户不知道好了没、
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
    <>
      <section
        className="log-wrap"
        data-testid="build-log-panel"
        aria-label={`构建日志 ${projectCode ?? ''}`}
      >
        <div className="log-head">
          <span className="left">
            <span className="dot" aria-hidden="true" />
            {/* 原型写的是 `build · {englishName}`；本组件的 props 只有 projectCode，
                拿不到 englishName（改 props 会牵动调用方与测试契约），
                故以 projectCode 顶替 —— 视觉占位一致，取值如实不同。 */}
            <span className="name">build · {projectCode ?? '—'}</span>
          </span>
          <span className="right">
            <span>已运行 {formatElapsed(elapsed)}</span>
            {status === 'error' ? (
              <span
                className="status status-error"
                data-testid="build-log-status-error"
                aria-label="失败"
              >
                ❌
              </span>
            ) : (
              <span className="status status-running">
                <span className="dot" aria-hidden="true" />
                running
              </span>
            )}
            <button
              type="button"
              className="log-close"
              data-testid="build-log-close"
              onClick={clear}
            >
              关闭
            </button>
          </span>
        </div>

        {/* 构建阶段（2026-10-18-build-stage-progress）：此前用户只能盯着一串滚动日志，
            不知道"到哪一步了、是不是卡住了"。这里显示「第 N/7 步：<步骤名>」+ 进度条。
            stage 为 null（未收到阶段事件）时不显示，不占位。 */}
        {stage ? (
          <div className="stage" data-testid="build-stage">
            {stage.total > 0 ? (
              <p className="stage-text" data-testid="build-stage-text">
                第 {stage.current}/{stage.total} 步：{stage.message ?? stage.phase ?? ''}
              </p>
            ) : (
              <p className="stage-text" data-testid="build-stage-text">
                {stage.message ?? stage.phase ?? '处理中…'}
              </p>
            )}
            {stage.total > 0 ? (
              <progress
                className="stage-bar"
                data-testid="build-stage-bar"
                value={stage.current}
                max={stage.total}
              />
            ) : null}
          </div>
        ) : null}

        <div className="log-body">
          {entries
            .slice()
            .reverse()
            .map((entry, idx) => {
              const parsed = parseLine(entry.text)
              return (
                <span
                  key={entry.at + '-' + idx}
                  className={`ln ln-${classifyLine(entry.text)}`}
                  data-testid="build-log-entry"
                >
                  {parsed.head}
                  {parsed.link ? (
                    <span className="ln-link">
                      <a
                        href={parsed.link.href}
                        target="_blank"
                        rel="noreferrer noopener"
                      >
                        {parsed.link.text}
                      </a>
                    </span>
                  ) : null}
                  {parsed.tail}
                </span>
              )
            })}
        </div>
      </section>

      <nav className="footer-bar" aria-label="构建日志后续操作">
        {/* 原型这里是 <a href="dashboard.html">。这里改用原生 <a href="#/project/list">：
            本面板必须能在**没有 Router 上下文**的环境里渲染（它是一个内嵌面板，
            单元测试就是裸 render 的），用 <Link>/useNavigate 会在无 Router 时直接抛错。
            应用是 HashRouter（M4 D5 决策），hash 跳转由 router 自己接住。 */}
        <a className="btn btn-ghost" href="#/project/list">
          返回工作台
        </a>
        {/* 「查看生成的文件」的目标页 generated-files 本次不实现 —— 后端还没有
            目录树端点（无契约可消费）。按钮按原型渲染完整视觉，但 disabled 且
            不绑 onClick：给用户一个明确的「快了」信号，好过一个点了没反应的按钮。 */}
        <button
          type="button"
          className="btn btn-solid"
          disabled
          title="接口待修复，验收后接入"
        >
          查看生成的文件 →
        </button>
      </nav>
    </>
  )
}