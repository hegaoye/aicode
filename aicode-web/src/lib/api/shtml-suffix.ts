import axios, { type InternalAxiosRequestConfig } from 'axios'

/**
 * `.shtml` 后缀拦截器 —— 前端侧。
 *
 * M4 切运行时的「接口 / 页面路径分离」方案（详见
 * `openspec/changes/archive/2026-10-19-m4-cutover-to-react/design.md` - D5）：
 *
 * | 类别 | 路径 | 谁处理 |
 * |---|---|---|
 * | 后端 API | `/project/list.shtml` | `ShtmlSuffixRewriteFilter`（M0 已落地）剥后缀 → Spring MVC |
 * | 前端页面 | `/` + `#/...` 客户端 hash 路由 | `index.html` + `HashRouter` |
 * | 静态资源 | `/assets/*` | Spring Boot static |
 *
 * 本拦截器把 orval 生成的 API 路径（`/project/list`）统一补 `.shtml` 后缀，
 * 与后端 `ShtmlSuffixRewriteFilter.API_PREFIXES` 白名单对接。后端 45 个
 * `@RequestMapping` 一行不用改。
 *
 * 与 `lib/auth/auth-interceptor.ts` **并列而非合并** —— 分层原则：
 * `auth` 管鉴权（Authorization 头），`api` 管 URL 形态（后缀）。
 */

const SHTML_SUFFIX = '.shtml'

/**
 * 后端 API 一级路由前缀白名单 —— 必须与后端
 * `com.aicode.filter.ShtmlSuffixRewriteFilter#API_PREFIXES` 保持一致。
 * 不在该白名单内的路径（如 `/assets/*`、`/actuator/*`）不加后缀。
 *
 * 导出供测试钉死：新增页面若引入新的后端一级路由（如 `/module/xxx`），
 * 必须同步这里，否则该路径的请求不会补 `.shtml` → 后端
 * `NoResourceFoundException`（400），而**没有任何其他测试会红**。
 */
export const API_PREFIXES = [
  '/project',
  '/framework',
  '/displayAttribute',
  '/logs',
  '/login',
  '/account',
] as const

/** 是否是「已注册 API 前缀」的完整路径段命中（`/project` 与 `/project/x` 命中，`/projects` 不命中）。 */
function startsWithSegment(path: string, prefix: string): boolean {
  if (!path.startsWith(prefix)) {
    return false
  }
  return path.length === prefix.length || path.charAt(prefix.length) === '/'
}

/**
 * 是否需要补 `.shtml` 后缀。
 *
 * 纯函数，无副作用，便于单元测试。
 */
export function shouldAddShtmlSuffix(url: string): boolean {
  if (!url || url.startsWith('http://') || url.startsWith('https://')) {
    // 绝对 URL（跨域）不在本拦截器职责内
    return false
  }
  const pathOnly = url.split('?')[0] ?? ''
  if (pathOnly.endsWith(SHTML_SUFFIX)) {
    // 已带后缀，不重复添加
    return false
  }
  return API_PREFIXES.some((prefix) => startsWithSegment(pathOnly, prefix))
}

let installed = false

/**
 * 一次性安装拦截器。多次调用仅首次生效（幂等）。
 *
 * 在 `main.tsx` 中与 `installAuthInterceptor()` 一并调用。
 */
export function installShtmlSuffixInterceptor(): void {
  if (installed) {
    return
  }
  installed = true

  axios.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    if (typeof config.url === 'string' && shouldAddShtmlSuffix(config.url)) {
      // 保留 query string：后缀插在 path 之后、query 之前
      const qIdx = config.url.indexOf('?')
      if (qIdx >= 0) {
        const path = config.url.slice(0, qIdx)
        const query = config.url.slice(qIdx)
        config.url = `${path}${SHTML_SUFFIX}${query}`
      } else {
        config.url = `${config.url}${SHTML_SUFFIX}`
      }
    }
    return config
  })
}

/**
 * 测试辅助：把「已安装」标志位复位。
 * 内部使用，SHALL NOT 被产品代码调用。
 */
export function __resetShtmlSuffixForTests(): void {
  installed = false
}