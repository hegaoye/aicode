import { Link } from 'react-router-dom'
import { useCurrentAccount } from '../../lib/account/use-current-account'

/**
 * `<AccountPage>` —— 账户详情页。
 *
 * 详见 `openspec/specs/aicode-web-account/spec.md` 的 Requirement
 * "AccountPage SHALL 是 useCurrentAccount 的浅渲染，3 字段受 password 防护"：
 * - 3 状态机 loading | ready | error（M2-2 样板）
 * - ready 状态渲染 3 字段（id / code / account）
 * - **关键**：data SHALL NOT 含 password 字段（M3-D 防护重点，详见反向验证）
 *
 * 视觉形态：原型无对应页面，故只复用 base.css 基元 —— `.container` 外壳 +
 * `.panel` / `.panel-head` / `.panel-body`，3 字段用 `.grid-2` + `.field`
 * 的 k/v 形态（与原型 project-review.html 的「项目信息」面板同构）。
 * 三个状态共用同一层面板，避免出错/加载时页面骨架跳变。
 */

export default function AccountPage() {
  const { account, code, id, isLoading, error } = useCurrentAccount()

  if (isLoading) {
    return (
      <div className="container">
        <section className="panel" aria-busy="true">
          <div className="panel-head">
            <span className="title">账户信息</span>
          </div>
          <div className="panel-body empty-state">加载中…</div>
        </section>
      </div>
    )
  }

  if (error) {
    return (
      <div className="container">
        <section className="panel">
          <div className="panel-head">
            <span className="title">账户信息</span>
            <span className="right">
              <span className="badge pending">加载失败</span>
            </span>
          </div>
          <div className="panel-body">
            <p role="alert" className="field-error show" data-testid="account-error">
              加载失败：{error.info || error.code}
            </p>
          </div>
        </section>
      </div>
    )
  }

  return (
    <div className="container">
      <section className="panel" data-testid="account-page-content">
        <div className="panel-head">
          <span className="title">账户信息</span>
        </div>
        <div className="panel-body">
          {/* k/v 用语义化 dl/dt/dd，视觉由 .grid-2 + .field 承担。
              password 不在此列 —— 接口契约本就不返回该字段（M3-D）。 */}
          <dl className="grid-2">
            <div className="field" data-testid="account-field-id">
              <dt className="k">id</dt>
              <dd className="v">{id ?? '—'}</dd>
            </div>
            <div className="field" data-testid="account-field-code">
              <dt className="k">code</dt>
              <dd className="v">{code ?? '—'}</dd>
            </div>
            <div className="field" data-testid="account-field-account">
              <dt className="k">account</dt>
              <dd className="v">{account ?? '—'}</dd>
            </div>
          </dl>
        </div>
      </section>
      <div className="form-foot">
        <Link className="btn-ghost" to="/">
          返回列表
        </Link>
      </div>
    </div>
  )
}