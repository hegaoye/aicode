import { Link, Navigate, Route, Routes } from 'react-router-dom'
import AppShell from './components/AppShell'
import LoginPage from './pages/LoginPage'
import DisplayAttributesPage from './pages/DisplayAttributesPage'
import ProjectListPage from './pages/ProjectListPage'
import ProjectPlaceholderPage from './pages/ProjectPlaceholderPage'
import AccountPage from './pages/AccountPage'
import FrameworkListPage from './pages/FrameworkListPage'
import RequireAuth from './components/RequireAuth'

/**
 * 路由表 —— 与 OpenDesign 原型的 6 个页面对应。
 *
 * 原型的 `index.html` 是设计稿自身的导航页（列出三张核心页面的入口卡片），
 * 不是产品的一部分，因此不作为路由存在。
 *
 * 路由守卫：
 * - /login、/login.shtml 不守卫；
 * - /displayAttributes、/project/list、/project/:code、/account/me、/frameworks
 *   都用 <RequireAuth> 包裹；
 * - `*` → `/` 占位路由仍然不守卫（M0 老路径）。
 */
export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      <Route
        path="/displayAttributes"
        element={
          <RequireAuth>
            <AppShell>
              <DisplayAttributesPage />
            </AppShell>
          </RequireAuth>
        }
      />
      <Route
        path="/project/list"
        element={
          <RequireAuth>
            <AppShell>
              <ProjectListPage />
            </AppShell>
          </RequireAuth>
        }
      />
      <Route
        path="/project/:code"
        element={
          <RequireAuth>
            <AppShell>
              <ProjectPlaceholderPage />
            </AppShell>
          </RequireAuth>
        }
      />

      {/* M3-D：账户详情页 */}
      <Route
        path="/account/me"
        element={
          <RequireAuth>
            <AppShell>
              <AccountPage />
            </AppShell>
          </RequireAuth>
        }
      />

      {/* M5-A2：框架技术池列表 */}
      <Route
        path="/frameworks"
        element={
          <RequireAuth>
            <AppShell>
              <FrameworkListPage />
            </AppShell>
          </RequireAuth>
        }
      />

      {/* 占位路由：受 M1 起的真实页面逐步替换 —— 当前不守卫；M3 一并讨论守卫范围 */}
      <Route
        path="/"
        element={
          <AppShell>
            <p className="muted">代码生成工作台（待实现）</p>
            <p>
              M1 已接入：
              {' '}
              <Link to="/displayAttributes">查看展示属性列表</Link>
            </p>
            <p>
              M2 已接入：
              {' '}
              <Link to="/project/list">查看项目列表</Link>
            </p>
            <p>
              M5 已接入：
              {' '}
              <Link to="/frameworks">查看框架列表</Link>
            </p>
          </AppShell>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
