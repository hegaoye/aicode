import { render, screen } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import BuildLogPanel from './build-log-panel'

/**
 * Mock `./use-build-log` 模块返回受控对象。让 panel 渲染时不依赖
 * ws-client / MSW。关闭按钮的行为（clear 改 status）由 useBuildLog.test.tsx
 * 单独覆盖（hook 测试范围），这里只验 panel 渲染层。
 */

let statusValue: 'idle' | 'running' | 'done' | 'error' = 'running'
let entriesValue: { at: number; text: string }[] = []
let startedAtValue: number | null = Date.now()
/** startStream 被调次数 —— 用于验 autoStart 语义 */
const startStreamMock = vi.fn(() => Promise.resolve())

vi.mock('./use-build-log', () => ({
  useBuildLog: () => ({
    status: statusValue,
    entries: entriesValue,
    startedAt: startedAtValue,
    startStream: () => startStreamMock(),
    clear: () => undefined,
  }),
}))

beforeEach(() => {
  statusValue = 'running'
  entriesValue = [
    { at: 1000, text: '开始构建...' },
    { at: 2000, text: 'git 努力下载中.....' },
    { at: 3000, text: '生成数据库结构与类模型成功！' },
  ]
  startedAtValue = 1000
})

afterEach(() => {
  vi.clearAllMocks()
})

describe('BuildLogPanel', () => {
  it('running 状态渲染：含标题、已运行时长、条目列表与关闭按钮', () => {
    render(<BuildLogPanel projectCode="contract-demo" />)
    const panel = screen.getByTestId('build-log-panel')
    expect(panel).toBeInTheDocument()
    expect(panel.getAttribute('aria-label')).toBe('构建日志 contract-demo')
    expect(screen.getByTestId('build-log-close')).toBeInTheDocument()
    const items = screen.getAllByTestId('build-log-entry')
    expect(items.length).toBe(3)
    expect(items[0]!.textContent).toBe('生成数据库结构与类模型成功！')
    expect(items[2]!.textContent).toBe('开始构建...')
  })

  it('idle 状态不渲染任何 DOM', () => {
    statusValue = 'idle'
    entriesValue = []
    startedAtValue = null
    const { container } = render(<BuildLogPanel projectCode="contract-demo" />)
    expect(screen.queryByTestId('build-log-panel')).toBeNull()
    expect(container.firstChild).toBeNull()
  })

  it('error 状态渲染 ❌ 徽章', () => {
    statusValue = 'error'
    entriesValue = [{ at: 1000, text: '构建失败: 服务器内部错误' }]
    render(<BuildLogPanel projectCode="contract-demo" />)
    expect(screen.getByTestId('build-log-status-error')).toBeInTheDocument()
    expect(screen.getByTestId('build-log-status-error').textContent).toBe('❌')
  })

  // ---- autoStart / onMounted（2026-10-18-project-list-pagination 新增）----
  //
  // 背景：列表页翻回"构建中"所在页时 panel 会重新挂载。若此时仍自动
  // startStream()，就会重复调 api.execute，后端 Executing 检查会拒绝。
  // 见 openspec/changes/2026-10-18-project-list-pagination/design.md - D3。

  it('autoStart 缺省（true）时 mount SHALL 调 startStream', () => {
    render(<BuildLogPanel projectCode="contract-demo" />)
    expect(startStreamMock).toHaveBeenCalledTimes(1)
  })

  it('autoStart={false} 时 mount SHALL NOT 调 startStream（仍正常渲染面板）', () => {
    render(<BuildLogPanel projectCode="contract-demo" autoStart={false} />)
    expect(startStreamMock).toHaveBeenCalledTimes(0)
    // 防护不应让面板消失 —— 日志仍可见
    expect(screen.getByTestId('build-log-panel')).toBeInTheDocument()
  })

  it('projectCode 为空时即便 autoStart=true 也 SHALL NOT 调 startStream', () => {
    render(<BuildLogPanel projectCode={null} />)
    expect(startStreamMock).toHaveBeenCalledTimes(0)
  })

  it('onMounted 每次 mount SHALL 恰好调一次（与 autoStart 无关）', () => {
    const onMounted = vi.fn()
    const { unmount } = render(
      <BuildLogPanel projectCode="contract-demo" autoStart={false} onMounted={onMounted} />,
    )
    expect(onMounted).toHaveBeenCalledTimes(1)
    unmount()
    // 重新挂载（模拟列表页翻回）→ 再调一次，供调用方写"已挂载过"集合
    render(<BuildLogPanel projectCode="contract-demo" autoStart={false} onMounted={onMounted} />)
    expect(onMounted).toHaveBeenCalledTimes(2)
  })

  it('未传 onMounted 时 SHALL NOT 抛错（prop 可选）', () => {
    expect(() => render(<BuildLogPanel projectCode="contract-demo" />)).not.toThrow()
  })

  // ================================================================
  // 自动收起（2026-10-18-build-log-terminal-signal）
  //
  // 此前只判 idle，构建成功后面板一直挂着、用户不知道好了没 —— 那时
  // 没有可用终态信号（服务端虽有 Finished: SUCCESS，但 hook 从不识别）。
  //
  // ⚠️ **error 刻意不收起**：构建失败时用户需要看到失败原因与 ❌ 徽章，
  // 把错误面板自动藏掉是反直觉的，且会让「构建失败」看起来像「什么都没发生」。
  // ================================================================

  it('done 状态 SHALL 自动收起（不渲染任何 DOM）', () => {
    statusValue = 'done'
    const { container } = render(<BuildLogPanel projectCode="contract-demo" />)
    expect(screen.queryByTestId('build-log-panel')).toBeNull()
    expect(container.firstChild).toBeNull()
  })

  it('error 状态 SHALL 保留面板（不自动收起，让用户看到失败原因）', () => {
    statusValue = 'error'
    entriesValue = [{ at: 1000, text: '构建失败: 建表失败' }]
    render(<BuildLogPanel projectCode="contract-demo" />)
    // ❌ 徽章与失败条目都还在
    expect(screen.getByTestId('build-log-panel')).toBeInTheDocument()
    expect(screen.getByTestId('build-log-status-error')).toBeInTheDocument()
    expect(screen.getByText('构建失败: 建表失败')).toBeInTheDocument()
  })
})
