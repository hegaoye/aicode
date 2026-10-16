import { act, renderHook, waitFor } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { server } from '../../test-setup'
import { getLoadByCode1MockHandler } from '../../generated/api.msw'
import type { RAccountPublicVO } from '../../generated/model/rAccountPublicVO'
import { setToken, clearToken } from '../auth/token-store'
import { useCurrentAccount } from './use-current-account'

/**
 * useCurrentAccount 3 用例：
 * ① token 存在时成功 load + 返回 account/code/id
 * ② 401 时 useAuth.token 应当被清空（但本 hook 不直接 signOut，依赖 useAuth
 *   自身的 storage 事件触发重渲染 —— 这条不直接测试，由 M2-2 已落地的
 *   auth-interceptor.test.ts 覆盖 token 清空 + RequireAuth 跳 /login）
 * ③ 服务器 500 时 error 字段有值
 *
 * 用 MSW 拦截 loadByCode1 端点（getAccountLoadMockHandler M0 已注册）。
 */

beforeEach(() => {
  setToken('jwt-test-token')
})

afterEach(() => {
  clearToken()
  vi.clearAllMocks()
})

describe('useCurrentAccount', () => {
  it('token 存在时成功 load + 返回 account / code / id', async () => {
    const fixed: RAccountPublicVO = {
      success: true,
      code: '0000',
      info: 'success',
      data: {
        id: 42,
        code: '1882812765213425002',
        account: 'admin',
      },
    }
    server.use(getLoadByCode1MockHandler(fixed))

    const { result } = renderHook(() => useCurrentAccount())

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false)
    })

    expect(result.current.account).toBe('admin')
    expect(result.current.code).toBe('1882812765213425002')
    expect(result.current.id).toBe(42)
    expect(result.current.error).toBeNull()
  })

  it('loadByCode1 端点 500 错误时，error 字段有值且 isLoading 释放', async () => {
    server.use(
      getLoadByCode1MockHandler({
        success: false,
        code: '9999',
        info: '服务器内部错误',
      } as unknown as RAccountPublicVO),
    )

    const { result } = renderHook(() => useCurrentAccount())

    await waitFor(() => {
      expect(result.current.error).not.toBeNull()
    })
    expect(result.current.error!.code).toBe('9999')
    expect(result.current.error!.info).toBe('服务器内部错误')
    expect(result.current.isLoading).toBe(false)
    expect(result.current.account).toBeNull()
  })

  it('token 不存在时 hook 不发起 load（保持空数据）', async () => {
    clearToken() // 主动清 token
    const { result } = renderHook(() => useCurrentAccount())

    // 等一帧让 useEffect 跑完 —— 但因 token === null 应该不调 load
    await act(async () => {
      await new Promise((r) => setTimeout(r, 50))
    })

    expect(result.current.account).toBeNull()
    expect(result.current.code).toBeNull()
    expect(result.current.id).toBeNull()
    expect(result.current.isLoading).toBe(false)
    expect(result.current.error).toBeNull()
  })
})
