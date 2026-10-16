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
    <span data-testid="account-menu" className="account-menu">
      <button
        type="button"
        className="account-badge"
        data-testid="account-menu-badge"
        onClick={() => setOpen((o) => !o)}
      >
        {account ?? '…'}
      </button>
      {open ? (
        <ul role="menu" className="account-dropdown">
          <li>
            <Link
              to="/account/me"
              role="menuitem"
              data-testid="account-menu-detail"
              onClick={() => setOpen(false)}
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
            >
              退出
            </button>
          </li>
        </ul>
      ) : null}
    </span>
  )
}
