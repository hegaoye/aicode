import type { AxiosResponse } from 'axios'
import { ContractError, unwrapSuccess, type EnvelopeLike } from './index'

// 解构函数只在形状层操作；测试用形如 R<T> 的最小包装手搓输入即可，不必真发请求。

interface Row {
  id?: number
  name?: string
}

const rows: Row[] = [{ id: 1, name: 'a' }]

// 显式 E 形参 + 类型断言，使不同形状的 fixture 都能通过类型检查并
// 把数据形态清楚地表达出来。
function makeResp<E extends EnvelopeLike<unknown>>(env: E, status = 200): AxiosResponse<E> {
  return { data: env, status, statusText: 'OK', headers: {}, config: {} as never } as AxiosResponse<E>
}

describe('unwrapSuccess', () => {
  it('成功解构 data', () => {
    const resp = makeResp({ success: true, code: '0000', info: 'success', data: rows })
    expect(unwrapSuccess(resp)).toEqual(rows)
  })

  it('success=false 抛错且错误携带 code / info', () => {
    const resp = makeResp({ success: false, code: '9003', info: '参数为空', data: [] })
    let caught: unknown
    try {
      unwrapSuccess(resp)
    } catch (e) {
      caught = e
    }
    expect(caught).toBeInstanceOf(ContractError)
    expect((caught as ContractError).code).toBe('9003')
    expect((caught as ContractError).info).toBe('参数为空')
  })

  it('success=true 但 code 不是 "0000" 时视为失败', () => {
    const resp = makeResp({ success: true, code: '9004', info: '参数非法', data: [] })
    expect(() => unwrapSuccess(resp)).toThrow(ContractError)
  })

  it('code 与 info 都缺省时视为失败，错误携带 UNKNOWN 占位 code', () => {
    const resp = makeResp({ success: true, data: rows })
    let caught: unknown
    try {
      unwrapSuccess(resp)
    } catch (e) {
      caught = e
    }
    expect(caught).toBeInstanceOf(ContractError)
    expect((caught as ContractError).code).toBe('UNKNOWN')
    expect((caught as ContractError).info).toBe('')
  })

  it('success 缺省视为失败', () => {
    const resp = makeResp({ code: '0000', info: 'success', data: rows })
    expect(() => unwrapSuccess(resp)).toThrow(ContractError)
  })

  it('ContractError 的 message 在 info 为空时回退到 code', () => {
    const resp = makeResp({ success: false, code: '9999' })
    let caught: unknown
    try {
      unwrapSuccess(resp)
    } catch (e) {
      caught = e
    }
    expect((caught as ContractError).message).toBe('9999')
  })
})
