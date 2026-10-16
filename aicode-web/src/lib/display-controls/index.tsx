import type { ComponentType } from 'react'
import type { DisplayAttributeVO } from '../../generated/model/displayAttributeVO'
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
 * 控件分发器 —— 由展示属性（`DisplayAttributeVO.displayType`）决定底层
 * 控件。新增一种 `displayType` 支持时，只需要在表 `CONTROLS` 里加一行
 * 并实现对应组件；业务页面不需要改动。
 *
 * `displayType` 在契约层当前是无约束字符串，因此不命中的情形走
 * `UnknownControl` 兜底，避免 React 直接抛 `<undefined>` 渲染错误。
 */
const CONTROLS: Record<string, ComponentType<{ value: string | number; onChange: (s: string) => void; placeholder?: string; options?: string }>> = {
  text: TextControl,
  number: NumberControl,
  date: DateControl,
  select: SelectControl,
}

/**
 * 公开、可见的、支持类型集合。把这条字符序列写在这里而不是 `CONTROLS` 里，
 * 是为了让「我们的控件分发器在 M1 阶段支持这些类型」这一事实在源码里
 * 直接可读。一处声明、两处表用——`CONTROLS` 引用此集合来保证表与文档同源。
 */
export const SUPPORTED_DISPLAY_TYPES = Object.keys(CONTROLS) as Array<keyof typeof CONTROLS>

export function DisplayControl({ attribute, value, onChange, placeholder }: Props) {
  const key = (attribute.displayType ?? '').toLowerCase()
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
  return <UnknownControl displayType={attribute.displayType} />
}

export default DisplayControl
