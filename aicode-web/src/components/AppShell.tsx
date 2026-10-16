import type { ReactNode } from 'react'

/**
 * 应用外壳 —— topnav + 1px 品牌红 hairline。
 * 规则 3（空白克制）：不放左侧导航，靠信息密度传达工具属性。
 */
export default function AppShell({ children }: { children: ReactNode }) {
  return (
    <div>
      <header className="topnav">
        <span className="brand">AI-CODE</span>
        <span className="topnav-spacer" />
      </header>
      <main className="page">{children}</main>
    </div>
  )
}
