import { afterEach, beforeEach, describe, expect, it } from 'vitest'
import { clearToken, getToken, setToken } from './token-store'

const KEY = 'aicode.token'

describe('token-store', () => {
  beforeEach(() => {
    window.localStorage.clear()
  })

  afterEach(() => {
    window.localStorage.clear()
  })

  it('round-trip：写入后能取同一字符串', () => {
    setToken('jwt-abc-123')
    expect(getToken()).toBe('jwt-abc-123')
  })

  it('缺省值：无 token 时返 null', () => {
    expect(getToken()).toBeNull()
  })

  it('清除后再次读取返 null', () => {
    setToken('first')
    clearToken()
    expect(getToken()).toBeNull()
  })

  it('空字符串视为无效（setToken 时清除既有值）', () => {
    setToken('first')
    setToken('')
    expect(getToken()).toBeNull()
  })

  it('写入字符串自动 trim 空白', () => {
    setToken('  jwt-trimmed  ')
    expect(getToken()).toBe('jwt-trimmed')
  })

  it('读取时把纯空白字符串视为 null', () => {
    window.localStorage.setItem(KEY, '   ')
    expect(getToken()).toBeNull()
  })

  it('不抛错：storage 抛错时 getToken 返 null，setToken / clearToken 不抛', () => {
    const original = window.localStorage
    const faulty = {
      getItem: () => {
        throw new Error('SecurityError')
      },
      setItem: () => {
        throw new Error('SecurityError')
      },
      removeItem: () => {
        throw new Error('SecurityError')
      },
      clear: () => {
        throw new Error('SecurityError')
      },
      key: () => null,
      length: 0,
    }
    Object.defineProperty(window, 'localStorage', { configurable: true, value: faulty })
    expect(() => getToken()).not.toThrow()
    expect(getToken()).toBeNull()
    expect(() => setToken('x')).not.toThrow()
    expect(() => clearToken()).not.toThrow()
    Object.defineProperty(window, 'localStorage', { configurable: true, value: original })
  })
})
