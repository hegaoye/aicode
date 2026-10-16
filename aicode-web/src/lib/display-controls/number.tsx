import type { ChangeEvent } from 'react'

interface Props {
  value: string | number
  onChange: (next: string) => void
  placeholder?: string
}

/**
 * 数字输入框 —— 对应 `displayType: 'InputNumber'`。空值传 `""`，避免
 * 用户清空时被强制变成 0。
 *
 * 由 `<DisplayControl>` 在 `displayType` 命中 `"number"` 时调用。
 */
export default function NumberControl({ value, onChange, placeholder }: Props) {
  return (
    <input
      type="number"
      value={value === undefined || value === null ? '' : String(value)}
      onChange={(e: ChangeEvent<HTMLInputElement>) => onChange(e.target.value)}
      placeholder={placeholder}
    />
  )
}
