import { act, renderHook } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it } from 'vitest'
import { useAuth } from './use-auth'

describe('useAuth', () => {
  beforeEach(() => {
    window.localStorage.clear()
  })

  afterEach(() => {
    window.localStorage.clear()
  })

  it('初始无 token 时 isAuthenticated 为 false', () => {
    const { result } = renderHook(() => useAuth())
    expect(result.current.isAuthenticated).toBe(false)
    expect(result.current.token).toBeNull()
  })

  it('signIn 后 isAuthenticated 为 true，token 同步到 localStorage', () => {
    const { result } = renderHook(() => useAuth())
    act(() => {
      result.current.signIn('jwt-1')
    })
    expect(result.current.isAuthenticated).toBe(true)
    expect(result.current.token).toBe('jwt-1')
    expect(window.localStorage.getItem('aicode.token')).toBe('jwt-1')
  })

  it('signOut 后清 localStorage，isAuthenticated 变 false', () => {
    const { result } = renderHook(() => useAuth())
    act(() => {
      result.current.signIn('jwt-to-clear')
    })
    act(() => {
      result.current.signOut()
    })
    expect(result.current.isAuthenticated).toBe(false)
    expect(result.current.token).toBeNull()
    expect(window.localStorage.getItem('aicode.token')).toBeNull()
  })

  it('初始 token 已被持久化时，第一帧即感知', () => {
    window.localStorage.setItem('aicode.token', 'pre-existing')
    const { result } = renderHook(() => useAuth())
    expect(result.current.token).toBe('pre-existing')
    expect(result.current.isAuthenticated).toBe(true)
  })

  it('signIn / signOut 引用稳定：多次渲染期间同一引用', () => {
    const { result, rerender } = renderHook(() => useAuth())
    const signInRef1 = result.current.signIn
    const signOutRef1 = result.current.signOut
    rerender()
    expect(result.current.signIn).toBe(signInRef1)
    expect(result.current.signOut).toBe(signOutRef1)
  })

  it('其他 tab 修改 token 后本 tab 也同步', () => {
    const { result } = renderHook(() => useAuth())
    expect(result.current.isAuthenticated).toBe(false)
    act(() => {
      window.localStorage.setItem('aicode.token', 'other-tab-write')
      window.dispatchEvent(
        new StorageEvent('storage', {
          key: 'aicode.token',
          newValue: 'other-tab-write',
        }),
      )
    })
    expect(result.current.token).toBe('other-tab-write')
    expect(result.current.isAuthenticated).toBe(true)
  })

  it('hook 返回值结构稳定', () => {
    const { result: h } = renderHook(() => useAuth())
    expect(h.current).toHaveProperty('token')
    expect(h.current).toHaveProperty('signIn')
    expect(h.current).toHaveProperty('signOut')
    expect(h.current).toHaveProperty('isAuthenticated')
  })
})
