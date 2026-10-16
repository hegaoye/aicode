import type { AxiosResponse } from 'axios'

/**
 * 把后端统一响应 `R<T>` 拆成纯 `T` 给业务层用。
 *
 * 仓库后台以 `R.success.data` 形式包裹一切数据；任何不带 `success=true && code==="0000"`
 * 的响应都是失败。消费方 SHALL 用 `unwrapSuccess` 取数据，不应直接访问 `resp.data.data`：
 * 那样会把「`R`」整层泄漏到 UI 组件里，既污染 props 形状，也使失败响应需要手写错误归一化。
 *
 * 用法：
 *
 * ```ts
 * import { list5 as fetchDisplayAttributes } from '../../generated/api'
 * import { unwrapSuccess } from '../contract-response'
 *
 * const resp = await fetchDisplayAttributes({ ... })
 * const rows = unwrapSuccess(resp) // MapFieldColumnVO[]
 * ```
 */

/**
 * 业务错误，含后端统一响应里的 `code` 与 `info` 字段。
 *
 * `code` 是一律字符串（`9003` / `9004` / `9007` / `9999` 等），由后端 `BaseExceptionEnum`
 * 给出，前端可以按需映射到本地提示或跳转。
 */
export class ContractError extends Error {
  override readonly name = 'ContractError'
  readonly code: string
  readonly info: string

  constructor(code: string, info: string) {
    super(info || code)
    this.code = code
    this.info = info
  }
}

/**
 * 兼容统一响应包装器的最小形状：包含 `data`（端点真实数据）与 R 包络层的
 * 三个标准字段（`code` / `success` / `info`）。端点对应的生成类型
 * （`RString` / `RVoid` / `RListMapFieldColumnVO` / `RObject` / ...）
 * 都满足此结构，但表达上不需要知道具体生成类型。
 */
export interface EnvelopeLike<T> {
  data?: T
  code?: string
  success?: boolean
  info?: string
}

/**
 * 成功则返回 `resp.data.data`（其类型由 `E['data']` 推导）；
 * 失败则抛 {@link ContractError}，把 `code` 与 `info` 透传给上层。
 *
 * 判定条件：严格同时满足 `success === true` **且** `code === "0000"`，
 * 任何缺失或不同取值都视为失败。HTTP 4xx / 5xx 不会被 axios 包装成
 * `R`，因此测试若要模拟 HTTP 失败需要走 MSW 的 `status: 500` 路径，
 * `unwrapSuccess` 此时看到的 `resp.status` 已非 2xx，但本函数**不**
 * 校验 HTTP 状态——axios 默认会让 4xx / 5xx 直接 reject，故根本不会
 * 走到这里；该行为由测试覆盖（见 `index.test.ts`）。
 *
 * 调用方无须手写泛型：返回类型由 `resp.data` 的 `data` 字段自动推导：
 *
 * ```ts
 * const rows = unwrapSuccess(resp) // rows 的类型随 resp.data.data 推断
 * ```
 */
export function unwrapSuccess<E extends EnvelopeLike<unknown>>(resp: AxiosResponse<E>): NonNullable<E['data']> {
  const env = resp.data
  if (!env || env.success !== true || env.code !== '0000') {
    throw new ContractError(env?.code ?? 'UNKNOWN', env?.info ?? '')
  }
  return env.data as NonNullable<E['data']>
}
