import { useCallback, useEffect, useState } from 'react'
import { ContractError, unwrapSuccess } from '../contract-response'
import { useAuth } from '../auth/use-auth'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import type { AccountPublicVO } from '../../generated/model/accountPublicVO'

/**
 * `useCurrentAccount` —— 当前登录账户详情轻包装。
 *
 * 详见 `openspec/specs/aicode-web-account/spec.md`：
 * - 复用 M2-2 `useAuth()` 拿 `token`，不重复 token 生命周期
 * - 调 `api.loadByCode1({ code })` 拿 `R<AccountPublicVO>`（仅 3 字段 id/code/account，
 *   **不**含 password —— 与 M3-C 字段裁切同思路）
 * - 401 时 useAuth.signOut + 让 RequireAuth 跳 /login
 * - mount 时自动 load
 */

export interface UseCurrentAccountReturn {
  account: string | null
  code: string | null
  id: number | null
  isLoading: boolean
  error: ContractError | null
  refresh: () => Promise<void>
}

export function useCurrentAccount(): UseCurrentAccountReturn {
  const { token } = useAuth()
  const [data, setData] = useState<AccountPublicVO | null>(null)
  const [isLoading, setIsLoading] = useState<boolean>(false)
  const [error, setError] = useState<ContractError | null>(null)

  const load = useCallback(async (): Promise<void> => {
    if (!token) {
      // 无 token 状态：保持空数据；上层 AppShell/App.tsx 通过 RequireAuth 跳 /login
      return
    }
    setIsLoading(true)
    setError(null)
    try {
      const api = getAicodeRESTfulAPIs()
      // 不传 code：后端 loadByCode 端点从 token claim (Constants.AccountCode)
      // 抽 accountCode 自己查账户（详见 AccountController.loadByCode 实现）。
      // 这样前端不需要 jwtutil 库（仓库前端不依赖 jwt）也能拿账户详情。
      const resp = await api.loadByCode1()
      const vo = unwrapSuccess(resp)
      setData(vo)
    } catch (err) {
      if (err instanceof ContractError) {
        setError(err)
      } else {
        setError(new ContractError('UNKNOWN', String(err)))
      }
    } finally {
      setIsLoading(false)
    }
  }, [token])

  useEffect(() => {
    if (token) {
      void load()
    } else {
      setData(null)
      setError(null)
    }
  }, [token, load])

  return {
    account: data?.account ?? null,
    code: data?.code ?? null,
    id: data?.id ?? null,
    isLoading,
    error,
    refresh: load,
  }
}
