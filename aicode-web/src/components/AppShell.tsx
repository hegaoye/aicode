import type { ReactNode } from 'react'
import { Link } from 'react-router-dom'
import AccountMenu from './AccountMenu'

/**
 * 应用外壳 —— sticky topnav + 1px 品牌红 hairline。
 *
 * 视觉规则 2（细红线分隔）：topnav 下沿的 1px 品牌红 hairline 是页面签名。
 * 视觉规则 3（空白克制）：不放左侧导航，靠信息密度传达工具属性。
 *
 * 右上角图标行对齐原型（首页 / 新建）。原型还有「文档」与「退出」两个图标：
 *   - 文档 → 原型指向 template-docs.html，该页对应后端无任何端点，已列入
 *     本次延后范围。渲染出来就是个点不动的死按钮（设计反目标），故不做。
 *   - 退出 → 已有 AccountMenu 下拉里的「退出」，两处并存属冗余，故不做。
 * 二者待各自能力落地后再补。
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
          <nav className="topnav-icons" aria-label="主导航">
            <Link
              to="/project/list"
              className="ic"
              aria-label="首页"
              title="首页"
            >
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="1.6"
                aria-hidden="true"
              >
                <path d="M3 11l9-8 9 8" />
                <path d="M5 10v10h14V10" />
              </svg>
            </Link>
            <span className="divider" aria-hidden="true" />
            <Link
              to="/project/new"
              className="ic"
              aria-label="新建项目"
              title="新建项目"
            >
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="1.6"
                aria-hidden="true"
              >
                <path d="M12 5v14M5 12h14" />
              </svg>
            </Link>
          </nav>
          <span className="divider" aria-hidden="true" />
          <AccountMenu />
        </div>
      </header>
      <main className="page">{children}</main>
      <footer className="pagefoot">© 2017 – AI-Code · code-gen.workbench</footer>
    </div>
  )
}