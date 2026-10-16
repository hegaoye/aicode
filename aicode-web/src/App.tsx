import { Navigate, Route, Routes } from 'react-router-dom'
import AppShell from './components/AppShell'
import LoginPage from './pages/LoginPage'

/**
 * 路由表 —— 与 OpenDesign 原型的 6 个页面对应。
 *
 * 原型的 `index.html` 是设计稿自身的导航页（列出三张核心页面的入口卡片），
 * 不是产品的一部分，因此不作为路由存在。
 *
 * 本阶段（M0）仅落地登录页骨架；其余页面在 M1–M3 依次实现。
 */
export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      {/* 占位路由：M1 起逐步替换为真实页面 */}
      <Route path="/" element={<AppShell><p className="muted">代码生成工作台（待实现）</p></AppShell>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
