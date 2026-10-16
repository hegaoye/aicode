import type { ComponentType } from 'react'
import type { DisplayAttributeVO } from '../../generated/model/displayAttributeVO'
import { DisplayAttributeVODisplayType } from '../../generated/model/displayAttributeVODisplayType'
import TextControl from './text'
import NumberControl from './number'
import DateControl from './date'
import SelectControl from './select'
import UnknownControl from './unknown'

interface Props {
  attribute: DisplayAttributeVO
  value: string | number
  onChange: (next: string) => void
  placeholder?: string
}

/**
 * 控件分发器 —— 由展示属性（`DisplayAttributeVO.displayType`）决定底层控件。
 *
 * ## key 必须与后端枚举对齐（2026-10-18 修订）
 *
 * <p>M1 版本的 `CONTROLS` 用的是自造 key（`text` / `number` / `date` / `select`），
 * 而后端 `DisplayAttributeDisplayType` 有 24 个 PascalCase 枚举（`Input` /
 * `InputNumber` / `DatePicker` / `Select` …）。分发时做 `.toLowerCase()`，于是：</p>
 *
 * ```
 * 'Input'.toLowerCase()      === 'input'      ≠ 'text'
 * 'InputNumber'.toLowerCase()=== 'inputnumber' ≠ 'number'
 * 'DatePicker'.toLowerCase() === 'datepicker' ≠ 'date'
 * 'Select'.toLowerCase()     === 'select'      ✓ 唯一命中
 * ```
 *
 * <p><b>24 个枚举里只有 `Select` 能命中，其余 23 个永远走 `UnknownControl`</b> ——
 * 而"四类都已实现"的表面合规掩盖了这一点。M1 当时的 spec 只要求
 * 「至少覆盖 `text`/`number`/`date`/`select` 四类」，未要求与后端枚举对齐，
 * <b>漏洞在 spec</b>（见
 * `openspec/specs/aicode-web-page/spec.md` 的
 * 「业务页面 SHALL 把统一响应解析与控件分发抽象为可复用单元」）。</p>
 *
 * <p>本版的 key 是<b>后端枚举的小写形式</b>，仍以 `.toLowerCase()` 查表 ——
 * 大小写容错这层保留，故契约里未来出现 `INPUT` / `input` 两种写法都能命中。</p>
 *
 * <p>刻意<b>不</b>改后端枚举：它会波及生成代码（模板侧可能在拼 `displayType` 字符串）
 * 与既有数据；改前端 key 零风险。</p>
 *
 * <p>未实现的枚举值走 `UnknownControl` 兜底，避免 React 抛 `<undefined>` 渲染错误。</p>
 */
const CONTROLS: Record<
  string,
  ComponentType<{
    value: string | number
    onChange: (s: string) => void
    placeholder?: string
    options?: string
  }>
> = {
  // key = 后端枚举的小写形式（display 时统一 .toLowerCase() 后查表）
  input: TextControl,
  inputnumber: NumberControl,
  datepicker: DateControl,
  select: SelectControl,
}

/**
 * 支持类型集合 —— 从 `CONTROLS` 的 key 派生（单一来源）。
 *
 * <p>M1 版本这里是一个手写常量数组的副本，与 `CONTROLS` 有漂移风险；现在改为派生。</p>
 */
export const SUPPORTED_DISPLAY_TYPES = Object.keys(CONTROLS)

/**
 * 契约里的**全部** `displayType` 取值（24 个，来自 orval 生成的枚举对象）。
 *
 * <p>直接从契约生成物读，SHALL NOT 在前端另写一份副本 —— 那份副本会在后端
 * 新增枚举时静默过期。</p>
 */
export const ALL_DISPLAY_TYPES = Object.values(DisplayAttributeVODisplayType)

export function DisplayControl({ attribute, value, onChange, placeholder }: Props) {
  const raw = attribute.displayType ?? ''
  const key = raw.toLowerCase()
  if (key in CONTROLS) {
    const Component = CONTROLS[key]
    return (
      <Component
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        options={attribute.fieldValidationMode}
      />
    )
  }
  // 降级：区分两种「未命中」——
  //   ① 该值在契约枚举里（后端支持）但前端尚无对应控件实现 → 提示「前端暂未支持」；
  //   ② 连契约枚举里都没有（脏数据）→ 提示「未知类型」。
  // 让用户能分辨是自己的配置问题还是平台能力缺口（delta spec 要求可见降级标识）。
  const isKnownEnum = (ALL_DISPLAY_TYPES as string[]).some((t) => t.toLowerCase() === key)
  return <UnknownControl displayType={raw} isKnownEnum={isKnownEnum} />
}

export default DisplayControl