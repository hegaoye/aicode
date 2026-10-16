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
 */

export default function AccountPage() {
  const { account, code, id, isLoading, error } = useCurrentAccount()

  if (isLoading) {
    return (
      <section aria-busy="true">
        <h1>账户详情</h1>
        <p className="muted">加载中…</p>
      </section>
    )
  }

  if (error) {
    return (
      <section>
        <h1>账户详情</h1>
        <p role="alert" data-testid="account-error">
          加载失败：{error.info || error.code}
        </p>
      </section>
    )
  }

  return (
    <section data-testid="account-page-content">
      <h1>账户详情</h1>
      <dl style={{ marginTop: 'var(--space-3)' }}>
        <div
          style={{ display: 'flex', gap: 'var(--space-4)' }}
          data-testid="account-field-id"
        >
          <dt style={{ minWidth: '8em', color: 'var(--muted)' }}>id</dt>
          <dd style={{ flex: 1 }}>{id ?? '—'}</dd>
        </div>
        <div
          style={{ display: 'flex', gap: 'var(--space-4)' }}
          data-testid="account-field-code"
        >
          <dt style={{ minWidth: '8em', color: 'var(--muted)' }}>code</dt>
          <dd style={{ flex: 1 }}>{code ?? '—'}</dd>
        </div>
        <div
          style={{ display: 'flex', gap: 'var(--space-4)' }}
          data-testid="account-field-account"
        >
          <dt style={{ minWidth: '8em', color: 'var(--muted)' }}>account</dt>
          <dd style={{ flex: 1 }}>{account ?? '—'}</dd>
        </div>
      </dl>
      <p style={{ marginTop: 'var(--space-4)' }}>
        <Link to="/">返回列表</Link>
      </p>
    </section>
  )
}
