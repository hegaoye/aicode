import { Navigate, Route, Routes } from 'react-router-dom'
import AppShell from './components/AppShell'
import LoginPage from './pages/LoginPage'
import DisplayAttributesPage from './pages/DisplayAttributesPage'
import ProjectListPage from './pages/ProjectListPage'
import NewProjectPage from './pages/NewProjectPage'
import ProjectDetailPage from './pages/ProjectDetailPage'
import AccountPage from './pages/AccountPage'
import FrameworkListPage from './pages/FrameworkListPage'
import RequireAuth from './components/RequireAuth'
import { useAuth } from './lib/auth/use-auth'

/**
 * 路由表 —— 与 OpenDesign「ai-code 页面设计」原型的应用页 1:1 对应。
 *
 * 原型的 `index.html` 是设计稿自身的导航页（列出三张核心页面的入口卡片），
 * 不是产品的一部分，因此不作为路由存在。
 *
 * 路由守卫：
 * - /login 不守卫；
 * - 其余全部用 <RequireAuth> 包裹；
 * - `*` → `/` 兜底。
 */
export default function App() {
  const { isAuthenticated } = useAuth()

  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      {/*
        顺序敏感：`/project/new` MUST 声明在 `/project/:code` 之前 ——
        React Router v6 按声明顺序匹配，若放在后面，"new" 会被当作
        projectCode 吃掉，渲染出「项目不存在」。
        App.test.tsx 的「顺序防线」用例专门守这一条。
      */}
      <Route
        path="/project/new"
        element={
          <RequireAuth>
            <AppShell>
              <NewProjectPage />
            </AppShell>
          </RequireAuth>
        }
      />
      <Route
        path="/project/:code"
        element={
          <RequireAuth>
            <AppShell>
              <ProjectDetailPage />
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
        path="/account/me"
        element={
          <RequireAuth>
            <AppShell>
              <AccountPage />
            </AppShell>
          </RequireAuth>
        }
      />
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

      {/* 首页按登录态分流：原型没有「已登录首页」，工作台即入口 */}
      <Route
        path="/"
        element={<Navigate to={isAuthenticated ? '/project/list' : '/login'} replace />}
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}