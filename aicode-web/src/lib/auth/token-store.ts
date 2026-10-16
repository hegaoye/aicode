/**
 * JWT 本地持久化 —— `aicode-web` 唯一的 token 入口。
 *
 * 详见 `openspec/specs/aicode-web-auth/spec.md`：
 * - `localStorage["aicode.token"]` 是该前端唯一的 token 持久化入口；
 * - 其他位置 SHALL NOT 单独缓存 token；
 * - 任何修改 SHALL 经过 set / clear 方法，SHALL NOT 直接写 `localStorage`。
 *
 * 这一约定让单元测试、SSR-friendly 包装、未来的 HttpOnly 迁移都只动这一个文件。
 */

const STORAGE_KEY = 'aicode.token'

/**
 * 读取当前 token。`localStorage` 中无值、解析失败、或为空字符串均返 `null`。
 *
 * 我们把"空字符串视为无"也归入 null，因为：
 * 1. 路由守卫 `RequireAuth` 只关心"有没有 token"；
 * 2. 任何业务代码看到 `''` 与 `null` 应当行为一致。
 */
export function getToken(): string | null {
  if (typeof window === 'undefined') {
    return null
  }
  try {
    const raw = window.localStorage.getItem(STORAGE_KEY)
    if (!raw) {
      return null
    }
    const trimmed = raw.trim()
    return trimmed.length === 0 ? null : trimmed
  } catch (_e) {
    // 隐私模式 / 浏览器禁用 storage —— 视作无 token
    return null
  }
}

/**
 * 写入 token。空字符串与 `null` 都被视作"清除"语义（不写入、亦不清除显式 key，
 * 但仍调用一次 `removeItem` 以兜底历史值）。
 */
export function setToken(token: string): void {
  if (typeof window === 'undefined') {
    return
  }
  if (!token || token.trim().length === 0) {
    clearToken()
    return
  }
  try {
    window.localStorage.setItem(STORAGE_KEY, token.trim())
  } catch (_e) {
    // 写入失败（quota / disabled）—— 静默：业务调用方应捕获该情况
    // 但本仓库不引入额外的错误层，沿用最小实现。
  }
}

/**
 * 清除 token。即使 key 不存在也不会抛错。
 */
export function clearToken(): void {
  if (typeof window === 'undefined') {
    return
  }
  try {
    window.localStorage.removeItem(STORAGE_KEY)
  } catch (_e) {
    // 见 setToken 的注释；清除失败不抛错
  }
}
