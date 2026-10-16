import type { ReactNode } from 'react'
import AccountMenu from './AccountMenu'

/**
 * 应用外壳 —— topnav + 1px 品牌红 hairline。
 * 规则 3（空白克制）：不放左侧导航，靠信息密度传达工具属性。
 *
 * M3-D：在 spacer 之前挂 <AccountMenu> —— 已登录显示徽章 + 下拉，未登录 null。
 */
export default function AppShell({ children }: { children: ReactNode }) {
  return (
    <div>
      <header className="topnav">
        <span className="brand">AI-CODE</span>
        <AccountMenu />
        <span className="topnav-spacer" />
      </header>
      <main className="page">{children}</main>
    </div>
  )
}
