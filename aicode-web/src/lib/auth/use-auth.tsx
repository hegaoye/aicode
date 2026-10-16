import { useCallback, useEffect, useState } from 'react'
import { clearToken, getToken, setToken } from './token-store'

/**
 * `useAuth` hook —— 业务页面消费登录态的标准入口。
 *
 * 详见 `openspec/specs/aicode-web-auth/spec.md`：
 * - token 由 token-store 一处管理，本 hook 是 React 视图层的薄包装；
 * - `signIn(token)` 写入 token，`signOut()` 清除；
 * - `isAuthenticated` 是 `token != null` 的派生布尔。
 *
 * 实现使用 `useSyncExternalStore`-like 模式：在 `localStorage` 监听
 * `storage` 事件以应对多 tab 同步（本仓库单 tab 为主，但保留扩展位）。
 */

export interface AuthState {
  /** 当前 token；`null` 表示无登录态。 */
  token: string | null
  /** 写入 token；等价于 `tokenStore.set(token)`。 */
  signIn: (token: string) => void
  /** 清除 token；等价于 `tokenStore.clear()`。 */
  signOut: () => void
  /** `token != null` 的派生标志。 */
  isAuthenticated: boolean
}

export function useAuth(): AuthState {
  const [token, setLocalToken] = useState<string | null>(() => getToken())

  // 监听其他 tab 写入 / 清除；当下业务同 tab 内 signIn / signOut
  // 也通过 setLocalToken 同步刷新，避免"打 storage 事件但状态滞后"。
  useEffect(() => {
    const onStorage = (ev: StorageEvent) => {
      if (ev.key === null || ev.key === 'aicode.token') {
        setLocalToken(getToken())
      }
    }
    window.addEventListener('storage', onStorage)
    return () => {
      window.removeEventListener('storage', onStorage)
    }
  }, [])

  const signIn = useCallback((next: string) => {
    setToken(next)
    setLocalToken(getToken())
  }, [])

  const signOut = useCallback(() => {
    clearToken()
    setLocalToken(null)
  }, [])

  return {
    token,
    signIn,
    signOut,
    isAuthenticated: token !== null,
  }
}
