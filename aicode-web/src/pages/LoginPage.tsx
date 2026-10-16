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
 *
 * 视觉：DOM 层级与类名逐字对齐 OpenDesign 原型 `login.html`
 * （`.auth-stage` / `.auth-card` / `.auth-fields` …），样式见
 * `styles/LoginPage.css`。业务逻辑一行未改。
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
    <main className="auth-stage">
      {/* 装饰性像素簇（原型 tl / bl / br 三处） */}
      <div className="pixel-cluster tl" />
      <div className="pixel-cluster bl" />
      <div className="pixel-cluster br" />

      <form className="auth-card" onSubmit={handleSubmit} noValidate>
        <h1 className="auth-title">账号登录</h1>

        <div className="auth-fields">
          <div className="field">
            <div className="input-wrap">
              <span className="ic" aria-hidden="true">
                <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth={1.6}
                >
                  <circle cx="12" cy="8" r="4" />
                  <path d="M4 20a8 8 0 0 1 16 0" />
                </svg>
              </span>
              {/* 原型无 label（只有 placeholder），此处用 aria-label 保留
                  无障碍名 —— 视觉零差异，App.test.tsx 的 getByLabelText 仍命中 */}
              <input
                id="account"
                className="auth-input"
                type="text"
                name="username"
                aria-label="账号"
                value={account}
                onChange={(e) => setAccount(e.target.value)}
                autoComplete="username"
                placeholder="请填写用户名"
              />
            </div>
          </div>

          <div className="field">
            <div className="input-wrap">
              <span className="ic" aria-hidden="true">
                <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth={1.6}
                >
                  <rect x="4" y="10" width="16" height="11" rx="2" />
                  <path d="M8 10V7a4 4 0 0 1 8 0v3" />
                </svg>
              </span>
              <input
                id="password"
                className="auth-input"
                type="password"
                name="password"
                aria-label="密码"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                autoComplete="current-password"
                placeholder="密码"
              />
            </div>
            {/* 原型在每个 .field 内预留了 .field-error（display:none 的
                必填校验位，距输入框 6px）。本页不做客户端校验，后端只回
                一条表单级消息，故只在出现时渲染一个，并挂在密码字段下 ——
                与原型渲染该提示的位置、间距完全一致。 */}
            {errorMsg ? (
              <p className="field-error show" role="alert" data-testid="login-error">
                {errorMsg}
              </p>
            ) : null}
          </div>
        </div>

        <div className="auth-row-between">
          <label className="remember-row">
            <input type="checkbox" name="remember" />
            <span>记住我</span>
          </label>
        </div>

        <button
          className={`btn-primary${submitting ? ' loading' : ''}`}
          type="submit"
          disabled={submitting}
        >
          {submitting ? '正在登录' : '登 录'}
        </button>

        <div className="auth-actions">
          {/* 本平台暂无注册流程（原型里也是一条未接线的占位链接），
              故阻止默认导航，保留原型的观感与可聚焦性 */}
          <a
            className="link-register"
            href="#"
            onClick={(e) => e.preventDefault()}
          >
            注册
          </a>
        </div>

        <div className="auth-foot">© 2017 – AI-Code · v2.4</div>
      </form>
    </main>
  )
}