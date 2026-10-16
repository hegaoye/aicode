import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { HashRouter } from 'react-router-dom'
import { installAuthInterceptor } from './lib/auth/auth-interceptor'
import { installShtmlSuffixInterceptor } from './lib/api/shtml-suffix'
import App from './App'
import './styles/tokens.css'
import './styles/base.css'
import './styles/AppShell.css'
import './styles/SharedPages.css'
import './styles/BuildLogPanel.css'
import './styles/FrameworkListPage.css'
import './styles/LoginPage.css'
import './styles/NewProjectPage.css'
import './styles/ProjectSqlPanel.css'
import './styles/RelationshipPanel.css'

// 应用启动 —— 见 openspec/specs/aicode-web-auth/spec.md
// 在 React 树挂载之前注册一次拦截器；多次调用幂等。
installAuthInterceptor()

// M4 切运行时（D5）：接口路径统一补 `.shtml` 后缀，与后端
// ShtmlSuffixRewriteFilter 对接。详见 openspec/specs/api-contract/spec.md。
installShtmlSuffixInterceptor()

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    {/* M4（D5）：BrowserRouter → HashRouter。
        Spring Boot 4 对未匹配路径抛 NoResourceFoundException（不 fallback 到
        index.html），因此前端页面路由必须走 hash 段（`/#/login`）才能深链直达
        而不经服务端路由。详见 openspec/specs/api-contract/spec.md 的
        "M4 切运行时 React 入口优先 Angular 静态产物" Requirement。 */}
    <HashRouter>
      <App />
    </HashRouter>
  </StrictMode>,
)