import axios from 'axios'
import { afterEach, beforeEach, describe, expect, it } from 'vitest'
import {
  __resetShtmlSuffixForTests,
  installShtmlSuffixInterceptor,
  shouldAddShtmlSuffix,
} from './shtml-suffix'

/**
 * `shtml-suffix` 4 组用例：
 * ① `shouldAddShtmlSuffix` 纯函数判定（命中 / 未命中 / 已带后缀 / 绝对 URL）
 * ② 拦截器实际改写 URL（含 query string 保留）
 * ③ 不改写非 API 路径（静态资源 / actuator）
 * ④ 幂等：多次 install 只生效一次
 */

function reset(): void {
  axios.interceptors.request.clear()
  __resetShtmlSuffixForTests()
}

beforeEach(() => {
  reset()
})

afterEach(() => {
  reset()
})

describe('shouldAddShtmlSuffix（纯函数判定）', () => {
  it('命中 API 前缀的路径应补后缀', () => {
    expect(shouldAddShtmlSuffix('/project/list')).toBe(true)
    expect(shouldAddShtmlSuffix('/login/signin')).toBe(true)
    expect(shouldAddShtmlSuffix('/account/load')).toBe(true)
    expect(shouldAddShtmlSuffix('/displayAttribute/list')).toBe(true)
  })

  it('未命中 API 前缀的路径不补后缀', () => {
    expect(shouldAddShtmlSuffix('/assets/index-abc.js')).toBe(false)
    expect(shouldAddShtmlSuffix('/actuator/health')).toBe(false)
    expect(shouldAddShtmlSuffix('/v3/api-docs')).toBe(false)
  })

  it('已带 .shtml 后缀不重复添加', () => {
    expect(shouldAddShtmlSuffix('/project/list.shtml')).toBe(false)
  })

  it('段名精确匹配：/projects 不命中 /project', () => {
    expect(shouldAddShtmlSuffix('/projects/list')).toBe(false)
    expect(shouldAddShtmlSuffix('/myproject/list')).toBe(false)
  })

  it('绝对 URL（跨域）不补后缀', () => {
    expect(shouldAddShtmlSuffix('https://api.example.com/project/list')).toBe(false)
    expect(shouldAddShtmlSuffix('http://localhost:8080/project/list')).toBe(false)
  })
})

describe('shtml-suffix 拦截器', () => {
  it('实际改写 URL 且保留 query string', () => {
    installShtmlSuffixInterceptor()
    const handler = (
      axios.interceptors.request as unknown as {
        handlers: Array<{ fulfilled: (cfg: unknown) => unknown }>
      }
    ).handlers[0]

    const cfg = handler.fulfilled({ url: '/project/list?curPage=1&pageSize=10' })
    expect((cfg as { url: string }).url).toBe(
      '/project/list.shtml?curPage=1&pageSize=10',
    )

    const cfg2 = handler.fulfilled({ url: '/login/signin' })
    expect((cfg2 as { url: string }).url).toBe('/login/signin.shtml')
  })

  it('不改写非 API 路径', () => {
    installShtmlSuffixInterceptor()
    const handler = (
      axios.interceptors.request as unknown as {
        handlers: Array<{ fulfilled: (cfg: unknown) => unknown }>
      }
    ).handlers[0]

    const cfg = handler.fulfilled({ url: '/assets/index-abc.js' })
    expect((cfg as { url: string }).url).toBe('/assets/index-abc.js')
  })

  it('幂等：多次 install 只注册一次拦截器', () => {
    installShtmlSuffixInterceptor()
    installShtmlSuffixInterceptor()
    installShtmlSuffixInterceptor()

    const handlers = (
      axios.interceptors.request as unknown as { handlers: unknown[] }
    ).handlers
    expect(handlers.length).toBe(1)
  })
})