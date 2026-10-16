import { render, screen } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import BuildLogPanel from './build-log-panel'

/**
 * Mock `./use-build-log` 模块返回受控对象。让 panel 渲染时不依赖
 * ws-client / MSW。关闭按钮的行为（clear 改 status）由 useBuildLog.test.tsx
 * 单独覆盖（hook 测试范围），这里只验 panel 渲染层。
 */

let statusValue: 'idle' | 'running' | 'error' = 'running'
let entriesValue: { at: number; text: string }[] = []
let startedAtValue: number | null = Date.now()

vi.mock('./use-build-log', () => ({
  useBuildLog: () => ({
    status: statusValue,
    entries: entriesValue,
    startedAt: startedAtValue,
    startStream: () => Promise.resolve(),
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
})
