import { useMemo, type ReactNode } from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../lib/auth/use-auth'

/**
 * 路由守卫 —— 见 openspec/specs/aicode-web-auth/spec.md 的 Requirement "受保护页面 SHALL 被路由守卫包裹"。
 *
 * 行为：
 * - 渲染前调 `useAuth()` 取 `token`；
 * - 空字符串 / `null` 时返 `<Navigate to="/login" state={from} replace />`，
 *   `state.from` 让登录成功后回到原路径（M3 接入细节），避免历史栈堆积；
 * - 非空时直接 `children`。
 *
 * `/login` 与 `/login.shtml` 由 AppShell 路由表排除在外，不走守卫。
 */
interface Props {
  children: ReactNode
}

export default function RequireAuth({ children }: Props) {
  const { token, isAuthenticated } = useAuth()
  const location = useLocation()

  const fromPath = useMemo(() => location.pathname + location.search, [location])

  if (!isAuthenticated || !token) {
    return <Navigate to="/login" state={{ from: fromPath }} replace />
  }

  return <>{children}</>
}
