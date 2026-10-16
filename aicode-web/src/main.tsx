import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { installAuthInterceptor } from './lib/auth/auth-interceptor'
import App from './App'
import './styles/tokens.css'

// 应用启动 —— 见 openspec/specs/aicode-web-auth/spec.md
// 在 React 树挂载之前注册一次拦截器；多次调用幂等。
installAuthInterceptor()

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </StrictMode>,
)
