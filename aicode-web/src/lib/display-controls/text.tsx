import type { ChangeEvent } from 'react'

interface Props {
  value: string | number
  onChange: (next: string) => void
  placeholder?: string
}

/**
 * 单行文本框 —— 对应 `displayType: 'Input'` 与其他以 "Input" 开始的
 * displayType（`InputNumber` 等除外，由 number 控件负责）。
 *
 * 由 `<DisplayControl>` 在 `displayType` 命中 `"text"` 时调用，业务页面
 * 不直接引入本组件。
 */
export default function TextControl({ value, onChange, placeholder }: Props) {
  return (
    <input
      type="text"
      value={value ?? ''}
      onChange={(e: ChangeEvent<HTMLInputElement>) => onChange(e.target.value)}
      placeholder={placeholder}
    />
  )
}
