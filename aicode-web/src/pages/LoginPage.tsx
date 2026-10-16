import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { ContractError, unwrapSuccess } from '../lib/contract-response'
import { useAuth } from '../lib/auth/use-auth'
import { getAicodeRESTfulAPIs } from '../generated/api'

/**
 * `LoginPage` —— M2 第 2 阶段接入 `/login/signin`：
 *
 *   1. 提交表单 → `api.signin({ account, password })`
 *   2. `unwrapSuccess(resp)` 取 JWT 字符串（见 `auth-account` spec）：
 *      - `code: "0000"` + `data` 为 JWT 时 → 写入 token + 跳目标；
 *      - 否则 → `ContractError` 气泡展示。
 *
 * 成功后跳：
 *   - 默认 `/project/list`；
 *   - 若路由 state 中携带 `from`（由 `<RequireAuth>` 注入），则跳回该页面。
 *
 * 视觉规则 5：提交按钮在 loading 时显示动效，且禁用一切弹窗 —— 沿用 M0 骨架。
 */
export default function LoginPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const { signIn } = useAuth()

  const [account, setAccount] = useState('')
  const [password, setPassword] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [errorMsg, setErrorMsg] = useState<string | null>(null)

  const fromPath =
    typeof location.state === 'object' &&
    location.state !== null &&
    'from' in location.state &&
    typeof (location.state as { from: unknown }).from === 'string'
      ? (location.state as { from: string }).from
      : '/project/list'

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (submitting) {
      return
    }
    setErrorMsg(null)
    setSubmitting(true)
    try {
      const api = getAicodeRESTfulAPIs()
      const resp = await api.signin({ account, password })
      const token = unwrapSuccess(resp)
      if (!token || token.trim().length === 0) {
        // auth-account spec：口令错误时 `code:0000` + 空字符串 —— 视为失败
        setErrorMsg('账号或密码错误')
        setSubmitting(false)
        return
      }
      signIn(token)
      navigate(fromPath, { replace: true })
    } catch (err) {
      const msg = err instanceof ContractError ? err.info || err.code : String(err)
      setErrorMsg(msg || '登录失败')
      setSubmitting(false)
    }
  }

  return (
    <div className="login-stage">
      <form className="login-card" onSubmit={handleSubmit}>
        <div className="brand" style={{ marginBottom: 'var(--space-4)' }}>
          AI-CODE
        </div>
        <div className="field">
          <label htmlFor="account">账号</label>
          <input
            id="account"
            value={account}
            onChange={(e) => setAccount(e.target.value)}
            autoComplete="username"
          />
        </div>
        <div className="field">
          <label htmlFor="password">密码</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
          />
        </div>
        {errorMsg ? (
          <p role="alert" data-testid="login-error" style={{ color: 'var(--accent)' }}>
            {errorMsg}
          </p>
        ) : null}
        <button
          className="btn-primary"
          type="submit"
          disabled={submitting}
          style={{ width: '100%' }}
        >
          {submitting ? '登录中…' : '登录'}
        </button>
      </form>
    </div>
  )
}
