import axios, { type AxiosResponse } from 'axios'
import { clearToken, getToken } from './token-store'

/**
 * 全局 axios 拦截器 —— 让 token 自动注入 `Authorization: Bearer <token>`，
 * 并在收到 `R.code === "9007"` 或 HTTP 401 时清空本地 token 与导航到 `/login`。
 *
 * 详见 `openspec/specs/aicode-web-auth/spec.md`：
 * - Request 拦截器：token 非空时设 `Authorization: Bearer` 头；
 * - Response 拦截器：9007 / 401 时 clear + 跳 `/login`，不抛错。
 *
 * 设计取舍（详见 design D1、D5）：
 * - 拦截器**只动 Header**，不动 `params.token` —— 避免污染 `signin` URL；
 * - 9007 处理在响应层完成、不上抛 —— 让业务代码不必重复判别授权错误。
 */

let installed = false

const AUTH_HEADER = 'Authorization'
const BEARER_PREFIX = 'Bearer '
const UNAUTHORIZED_CODE = '9007'

/**
 * 一次性安装拦截器。多次调用仅首次生效，后续调用为幂等空操作。
 *
 * 在 `main.tsx` 创建 React 根节点之前调用一次。
 */
export function installAuthInterceptor(): void {
  if (installed) {
    return
  }
  installed = true

  // Request：若本地有 token，则注入 Authorization 头
  axios.interceptors.request.use((config) => {
    const token = getToken()
    if (token) {
      // AxiosHeaders 实例或普通对象都允许；这里用 headers.set 形式最安全
      const headers = config.headers as Record<string, string> & {
        set?: (k: string, v: string) => void
      }
      if (typeof headers.set === 'function') {
        headers.set(AUTH_HEADER, `${BEARER_PREFIX}${token}`)
      } else {
        headers[AUTH_HEADER] = `${BEARER_PREFIX}${token}`
      }
    }
    return config
  })

  // Response：检测 R.code === "9007" 后清 token 并跳登录
  // 不抛错 —— 业务代码看到的就是"已经处理掉"，避免 UI 一帧渲染错误再被踢走
  axios.interceptors.response.use(
    (resp) => {
      const body = resp.data as { code?: unknown } | null | undefined
      if (body && typeof body === 'object' && body.code === UNAUTHORIZED_CODE) {
        handleUnauthorized()
      }
      return resp
    },
    // HTTP 4xx / 5xx 的 reject 也走这里；axios 默认行为是把网络/HTTP 错误 reject 出去
    (error: unknown) => {
      // 仅当有 response 时才检测（网络错误如 ECONNABORTED 没有 response）
      const response = (error as { response?: AxiosResponse<{ code?: unknown }> })?.response
      if (response?.status === 401) {
        handleUnauthorized()
      }
      throw error
    },
  )
}

function handleUnauthorized() {
  clearToken()
  if (typeof window !== 'undefined') {
    // 用 replaceState 避免历史栈堆积；用户被踢回登录后不会"后退"到已被收起的页面。
    window.history.replaceState(null, '', '/login')
    // 触发 react-router 重新评估 location —— 派发 popstate 让 Routes 重新匹配
    window.dispatchEvent(new PopStateEvent('popstate'))
  }
}

/**
 * 测试辅助：把"已安装"标志位复位，便于多个测试各自重新注册避免重复。
 * 内部使用，SHALL NOT 被产品代码调用。
 */
export function __resetAuthInterceptorForTests(): void {
  installed = false
}
