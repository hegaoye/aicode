import type { ChangeEvent } from 'react'

interface Props {
  value: string | number
  onChange: (next: string) => void
}

/**
 * 日期输入框 —— 对应 `displayType: 'DatePicker'`。使用原生 `<input type="date">`，
 * 浏览器会按本地化提供日期选择器，值为 `'YYYY-MM-DD'`。
 *
 * 由 `<DisplayControl>` 在 `displayType` 命中 `"date"` 时调用。
 */
export default function DateControl({ value, onChange }: Props) {
  return (
    <input
      type="date"
      value={value === undefined || value === null ? '' : String(value)}
      onChange={(e: ChangeEvent<HTMLInputElement>) => onChange(e.target.value)}
    />
  )
}
