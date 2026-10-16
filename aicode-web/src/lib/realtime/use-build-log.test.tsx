import { act, renderHook, waitFor } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { http, HttpResponse } from 'msw'
import { server } from '../../test-setup'

/**
 * Mock `./ws-client` 模块的 `connect` 函数 —— 用 `vi.mock` 而非 `vi.doMock`
 * 是因为 `vi.mock` 必须在文件顶层调用以确保模块工厂注册有效。
 */
vi.mock('./ws-client', () => {
  // 单连接记录 bag；每个 connect 调用覆盖；reset 在 beforeEach 完成
  // 这里只给出 connect 的替身；tests 用 vi.mocked 拿到引用
  return {
    connect: vi.fn(),
  }
})

import * as wsClient from './ws-client'
import { useBuildLog } from './use-build-log'
import { getExecuteMockHandler } from '../../generated/api.msw'
import type { RProjectJob } from '../../generated/model/rProjectJob'

const mockedConnect = vi.mocked(wsClient.connect)

interface RecordedConnect {
  url: string
  onMessage: ((msg: string | null) => void) | null
  closed: boolean
}

let lastConnect: RecordedConnect | null = null

beforeEach(() => {
  lastConnect = null
  mockedConnect.mockReset()
  mockedConnect.mockImplementation((url: string, onMessage: (msg: string | null) => void) => {
    lastConnect = { url, onMessage, closed: false }
    return {
      close() {
        if (lastConnect) {
          lastConnect.closed = true
        }
      },
      send() {
        // 不实现
      },
    } as unknown as wsClient.WSClient
  })
  // 默认 mock 默认 faker 随机 success——这里强制 success=true 让 startStream 默认走 running
  server.use(
    getExecuteMockHandler({ success: true, code: '0000', info: 'success' } as unknown as RProjectJob),
  )
})

afterEach(() => {
  vi.clearAllMocks()
})

describe('useBuildLog', () => {
  it('startStream 触发 execute + 启动 ws + 追加起始条目', async () => {
    const { result } = renderHook(() => useBuildLog('contract-demo'))

    await act(async () => {
      await result.current.startStream()
    })

    await waitFor(() => {
      expect(result.current.status).toBe('running')
    })
    expect(result.current.entries[0]?.text).toBe('开始构建...')
    expect(result.current.startedAt).toBeGreaterThan(0)
    expect(lastConnect).not.toBeNull()
    expect(lastConnect!.onMessage).toBeTypeOf('function')
  })

  it('ws 推送逐条追加至 entries', async () => {
    const { result } = renderHook(() => useBuildLog('contract-demo'))

    await act(async () => {
      await result.current.startStream()
    })

    const onMessage = lastConnect!.onMessage!

    act(() => {
      onMessage('git 努力下载中.....')
      onMessage('生成数据库结构与类模型成功！')
    })

    await waitFor(() => {
      expect(result.current.entries.map((e) => e.text)).toContain('git 努力下载中.....')
    })
    expect(result.current.entries.map((e) => e.text)).toContain('生成数据库结构与类模型成功！')
    // 起始条目 + 2 个 ws 推送 = 3 条
    expect(result.current.entries.length).toBe(3)
  })

  it('execute 抛 ContractError → status = error + 错误条目', async () => {
    // 拦截 execute 端点返失败响应
    server.use(
      http.get('*/project/job/execute*', () =>
        HttpResponse.json({
          success: false,
          code: '9999',
          info: '构造失败 demo',
          data: undefined,
        }),
      ),
    )

    const { result } = renderHook(() => useBuildLog('contract-demo'))

    await act(async () => {
      await result.current.startStream()
    })

    await waitFor(() => {
      expect(result.current.status).toBe('error')
    })
    expect(result.current.entries.some((e) => e.text.startsWith('构建失败:'))).toBe(true)
    expect(lastConnect).toBeNull()
  })

  it('unmount 时 ws close 释放（cleanup）', async () => {
    const { result, unmount } = renderHook(() => useBuildLog('contract-demo'))

    await act(async () => {
      await result.current.startStream()
    })

    const conn = lastConnect!
    expect(conn.closed).toBe(false)

    unmount()
    expect(conn.closed).toBe(true)
  })

  it('clear() 重置 status 到 idle + 清空 entries', async () => {
    const { result } = renderHook(() => useBuildLog('contract-demo'))

    await act(async () => {
      await result.current.startStream()
    })
    await waitFor(() => {
      expect(result.current.status).toBe('running')
    })
    // 模拟 ws 推一条消息
    act(() => {
      lastConnect!.onMessage!('git 努力下载中.....')
    })
    await waitFor(() => {
      expect(result.current.entries.length).toBeGreaterThan(1)
    })

    act(() => {
      result.current.clear()
    })

    expect(result.current.status).toBe('idle')
    expect(result.current.entries).toEqual([])
    expect(lastConnect!.closed).toBe(true)
  })
})
