import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../lib/auth/use-auth'
import { useCurrentAccount } from '../lib/account/use-current-account'

/**
 * `<AccountMenu>` —— 上导航用户徽章 + 下拉。
 *
 * 详见 `openspec/specs/aicode-web-account/spec.md` 的 Requirement
 * "AccountMenu SHALL 是上导航用户区，不参与业务逻辑"：
 * - `isAuthenticated === false` 时不渲染任何 DOM
 * - `isAuthenticated === true` 时渲染徽章（账户显示名）+ 下拉（详情/退出）
 * - 不持业务状态 —— 调 `useAuth().signOut()` + `useNavigate('/login')`
 */

export default function AccountMenu() {
  const { isAuthenticated, signOut } = useAuth()
  const { account } = useCurrentAccount()
  const navigate = useNavigate()
  const [open, setOpen] = useState(false)

  if (!isAuthenticated) {
    return null
  }

  const handleSignOut = () => {
    setOpen(false)
    signOut()
    navigate('/login', { replace: true })
  }

  return (
    <span data-testid="account-menu" style={{ position: 'relative' }}>
      <button
        type="button"
        className="btn-primary"
        data-testid="account-menu-badge"
        onClick={() => setOpen((o) => !o)}
        style={{ padding: '2px 12px', borderRadius: '14px' }}
      >
        {account ?? '…'}
      </button>
      {open ? (
        <ul
          role="menu"
          style={{
            position: 'absolute',
            right: 0,
            top: '100%',
            marginTop: '4px',
            listStyle: 'none',
            padding: '4px 0',
            background: 'var(--surface)',
            border: '1px solid var(--border)',
            borderRadius: 'var(--radius)',
            minWidth: '8em',
            zIndex: 10,
          }}
        >
          <li>
            <Link
              to="/account/me"
              role="menuitem"
              data-testid="account-menu-detail"
              onClick={() => setOpen(false)}
              style={{ display: 'block', padding: '6px 12px' }}
            >
              账户详情
            </Link>
          </li>
          <li>
            <button
              type="button"
              role="menuitem"
              data-testid="account-menu-signout"
              onClick={handleSignOut}
              style={{
                display: 'block',
                width: '100%',
                textAlign: 'left',
                background: 'none',
                border: 'none',
                padding: '6px 12px',
                cursor: 'pointer',
              }}
            >
              退出
            </button>
          </li>
        </ul>
      ) : null}
    </span>
  )
}
