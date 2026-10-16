import axios from 'axios'
import { afterEach, beforeEach, describe, expect, it } from 'vitest'
import {
  __resetAuthInterceptorForTests,
  installAuthInterceptor,
} from './auth-interceptor'
import { getToken, setToken } from './token-store'

/**
 * 拦截器单例在测试间需要重置；axios 单例自身的 interceptors 列表也需要清掉，
 * 否则跨文件复用会叠加（虽然 axios 默认就是单例，但因 vitest 的 `setupFiles`
 * 在每个测试进程启动时执行一次）。
 */
function resetAxios(): void {
  axios.interceptors.request.clear()
  axios.interceptors.response.clear()
  __resetAuthInterceptorForTests()
}

/** 取出注册到 axios 的最新 request 拦截器。 */
function getRequestHandler(): { fulfilled: (cfg: unknown) => unknown } {
  // axios 内部使用一个有序 handler 列表；这里通过任意 cast 拿最新一条
  // 这是测试代码专用的访问方式，避免引入 axios 私有 API 的耦合。
  const handlers = (axios.interceptors.request as unknown as {
    handlers: Array<{ fulfilled: (cfg: unknown) => unknown; rejected?: unknown }>
  }).handlers
  return handlers[handlers.length - 1]
}

function getResponseHandler() {
  const handlers = (axios.interceptors.response as unknown as {
    handlers: Array<{
      fulfilled: (resp: unknown) => unknown
      rejected?: (err: unknown) => unknown
    }>
  }).handlers
  return handlers[handlers.length - 1]
}

describe('auth-interceptor', () => {
  beforeEach(() => {
    window.localStorage.clear()
    resetAxios()
  })

  afterEach(() => {
    window.localStorage.clear()
    resetAxios()
  })

  it('持有 token 时注入 Authorization: Bearer <token>', () => {
    setToken('jwt-bearer-test')
    installAuthInterceptor()

    const handler = getRequestHandler()
    const cfg = handler.fulfilled({ headers: {} as Record<string, string> })
    const headers = cfg as { headers: Record<string, string> }
    expect(headers.headers.Authorization).toBe('Bearer jwt-bearer-test')
  })

  it('无 token 时不附加 Authorization 头', () => {
    installAuthInterceptor()

    const handler = getRequestHandler()
    const cfg = handler.fulfilled({ headers: {} as Record<string, string> })
    const headers = cfg as { headers: Record<string, string> }
    expect(headers.headers.Authorization).toBeUndefined()
  })

  it('响应拦截器：R.code === "9007" 时清空 token', () => {
    setToken('jwt-before-9007')
    installAuthInterceptor()

    const handler = getResponseHandler()
    const fakeResp = { data: { code: '9007', info: '未授权' } }
    handler.fulfilled(fakeResp)

    expect(getToken()).toBeNull()
  })

  it('响应拦截器：非 9007 不清 token', () => {
    setToken('jwt-should-survive')
    installAuthInterceptor()

    const handler = getResponseHandler()
    const fakeResp = { data: { code: '9999', info: '服务器错误' } }
    handler.fulfilled(fakeResp)

    expect(getToken()).toBe('jwt-should-survive')
  })

  it('重复安装幂等：第二次调用不会叠加', () => {
    setToken('jwt-once')
    installAuthInterceptor()
    installAuthInterceptor()
    installAuthInterceptor()

    const handlers = (axios.interceptors.request as unknown as {
      handlers: unknown[]
    }).handlers
    expect(handlers.length).toBe(1)
  })

  it('AxiosHeaders 形式也能注入', () => {
    setToken('jwt-headers-set')
    installAuthInterceptor()

    const handler = getRequestHandler()
    const fakeHeaders: Record<string, string> & {
      set?: (k: string, v: string) => void
    } = {} as Record<string, string> & { set?: (k: string, v: string) => void }
    fakeHeaders.set = (k: string, v: string) => {
      // AxiosHeaders.set 的实际行为类似：内部累加到一个 map
      ;(fakeHeaders as Record<string, string>)[k.toLowerCase()] = v
    }
    const cfg = handler.fulfilled({ headers: fakeHeaders })
    const headers = cfg as { headers: unknown }
    const authHeader = (headers.headers as Record<string, string>).authorization
    expect(authHeader).toBe('Bearer jwt-headers-set')
  })
})
