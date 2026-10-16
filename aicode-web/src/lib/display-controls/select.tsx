import type { ChangeEvent } from 'react'

interface Props {
  value: string | number
  onChange: (next: string) => void
  /**
   * 候选项来源 —— `fieldValidationMode` 在原契约里是单数字枚举字符串（如
   * `'Email / Address / Telephone / ...'` 这种斜线分隔的多值字符串），不是
   * 标准 JSON 数组。本组件按 `/` 拆分并去除空白，作为 `<option>` 列表。
   * 缺省时使用「未知 / 是 / 否」三选——这是 Y/N 字段最常见的语义。
   */
  options?: string
}

/**
 * 单项选择器 —— 对应 `displayType: 'Select'` / `'Radio'` 等。
 *
 * 注意：业务中的 `fieldValidationMode` 是一个用 `/` 串起来的多值字符串
 * （参见仓库后端的 `database-init.sql` 与 `DatabaseDataTypesUtils` 历史
 * 约定），因此在解析时按 `/` 拆分并去除空白。如果将来 metadata 改成
 * JSON 数组，由 `<DisplayControl>` 上层直接给它即可。
 */
const FALLBACK_OPTIONS = ['未知', '是', '否']

export default function SelectControl({ value, onChange, options }: Props) {
  const items = options && options.trim().length > 0
    ? options
        .split('/')
        .map((s) => s.trim())
        .filter((s) => s.length > 0)
    : FALLBACK_OPTIONS

  return (
    <select
      value={value === undefined || value === null ? '' : String(value)}
      onChange={(e: ChangeEvent<HTMLSelectElement>) => onChange(e.target.value)}
    >
      <option value="" disabled>
        请选择
      </option>
      {items.map((it) => (
        <option key={it} value={it}>
          {it}
        </option>
      ))}
    </select>
  )
}
