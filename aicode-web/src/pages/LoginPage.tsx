import { useState } from 'react'

/**
 * 登录页 —— M0 阶段的骨架。
 *
 * 视觉规则 5：提交按钮在 loading 时显示动效，且**禁用一切弹窗**。
 * 后续 M1 接入真实 /login/signin：token 走 `Authorization: Bearer`
 * （LoginInterceptor 已同时支持请求头与 ?token=，新前端按 AGENTS.md §6.13 优先用请求头）。
 */
export default function LoginPage() {
  const [account, setAccount] = useState('')
  const [password, setPassword] = useState('')
  const [submitting, setSubmitting] = useState(false)

  return (
    <div className="login-stage">
      <form
        className="login-card"
        onSubmit={(e) => {
          e.preventDefault()
          setSubmitting(true)
          // M1 接入真实接口
        }}
      >
        <div className="brand" style={{ marginBottom: 'var(--space-4)' }}>AI-CODE</div>
        <div className="field">
          <label htmlFor="account">账号</label>
          <input id="account" value={account} onChange={(e) => setAccount(e.target.value)} />
        </div>
        <div className="field">
          <label htmlFor="password">密码</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <button className="btn-primary" type="submit" disabled={submitting} style={{ width: '100%' }}>
          {submitting ? '登录中…' : '登录'}
        </button>
      </form>
    </div>
  )
}
