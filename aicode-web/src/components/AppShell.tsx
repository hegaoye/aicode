import type { ReactNode } from 'react'
import { Link } from 'react-router-dom'
import AccountMenu from './AccountMenu'

/**
 * 应用外壳 —— sticky topnav + 1px 品牌红 hairline。
 *
 * 视觉规则 3（空白克制）：不放左侧导航，靠信息密度传达工具属性。
 * 视觉规则 2（细红线分隔）：topnav 下沿的 1px 品牌红 hairline 是页面签名。
 *
 * `<AccountMenu />` 在 spacer 之前 —— 未登录时它渲染 null（见其实现）。
 */
export default function AppShell({ children }: { children: ReactNode }) {
  return (
    <div>
      <header className="topnav">
        <div className="topnav-inner">
          <Link to="/project/list" className="brand">
            AI-CODE
          </Link>
          <span className="topnav-spacer" />
          <AccountMenu />
        </div>
      </header>
      <main className="page">{children}</main>
      <footer className="pagefoot">© 2017 – AI-Code · code-gen.workbench</footer>
    </div>
  )
}