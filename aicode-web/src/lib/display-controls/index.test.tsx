import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import type { DisplayAttributeVO } from '../../generated/model/displayAttributeVO'
import { DisplayAttributeVODisplayType } from '../../generated/model/displayAttributeVODisplayType'
import DisplayControl, {
  ALL_DISPLAY_TYPES,
  SUPPORTED_DISPLAY_TYPES,
} from './index'

/**
 * 控件分发器的测试 —— **2026-10-18 大幅重写**。
 *
 * ## 为什么重写
 *
 * M1 版本的测试**全部用假值**（`text` / `number` / `date` / `'未知/是/否'`），
 * 而后端 `DisplayAttributeDisplayType` 的真实取值是 24 个 PascalCase 枚举
 * （`Input` / `InputNumber` / `DatePicker` / `Select` …）。
 *
 * 结果：分发器的 key 与后端枚举**全不匹配**（只有 `Select` 巧合命中），
 * 而这 7 条测试**全绿** —— 因为测试断言的是"我造的值能命中我造的 key"，
 * 从未拿真实枚举验证过。
 *
 * **教训**：断言"某个字符串能渲染"这种单点用例，无法发现系统性偏差。
 * 下面的 `未命中的枚举集合` 用例就是为此而生。
 */

function attr(over: Partial<DisplayAttributeVO> = {}): DisplayAttributeVO {
  return { ...over }
}

/** 后端枚举的全集（从契约生成物读）。 */
const ALL = ALL_DISPLAY_TYPES as string[]

describe('DisplayControl 分发器（key 已与后端枚举对齐）', () => {
  // ---- 大小写容错（design D1 保留 .toLowerCase() 查找） ----

  it('Input / INPUT / input 三种写法 SHALL 全部命中文本控件', () => {
    for (const v of ['Input', 'INPUT', 'input']) {
      const { unmount } = render(
        <DisplayControl attribute={attr({ displayType: v as never })} value="hi" onChange={() => {}} />,
      )
      expect((screen.getByDisplayValue('hi') as HTMLInputElement).type).toBe('text')
      unmount()
    }
  })

  it('InputNumber SHALL 命中数字控件', () => {
    render(
      <DisplayControl attribute={attr({ displayType: 'InputNumber' })} value={42} onChange={() => {}} />,
    )
    expect((screen.getByDisplayValue('42') as HTMLInputElement).type).toBe('number')
  })

  it('DatePicker SHALL 命中日期控件', () => {
    render(
      <DisplayControl attribute={attr({ displayType: 'DatePicker' })} value="2026-10-18" onChange={() => {}} />,
    )
    expect((screen.getByDisplayValue('2026-10-18') as HTMLInputElement).type).toBe('date')
  })

  it('Select SHALL 命中选择器，并按 fieldValidationMode 渲染 options', () => {
    render(
      <DisplayControl
        attribute={attr({ displayType: 'Select', fieldValidationMode: 'Date' })}
        value="是"
        onChange={() => {}}
      />,
    )
    const select = screen.getByRole('combobox') as HTMLSelectElement
    expect(Array.from(select.options).map((o) => o.text)).toEqual(['请选择', 'Date'])
  })

  it('Select 缺省 fieldValidationMode 时回退到「未知/是/否」', () => {
    render(<DisplayControl attribute={attr({ displayType: 'Select' })} value="" onChange={() => {}} />)
    const select = screen.getByRole('combobox') as HTMLSelectElement
    expect(Array.from(select.options).map((o) => o.text)).toEqual(['请选择', '未知', '是', '否'])
  })

  // ---- 契约值域来自生成物，不手写副本 ----

  it('ALL_DISPLAY_TYPES SHALL 等于契约生成物枚举的取值全集（24 个）', () => {
    expect(ALL).toEqual(Object.values(DisplayAttributeVODisplayType))
    expect(ALL).toHaveLength(24)
  })

  it('SUPPORTED_DISPLAY_TYPES SHALL 是 CONTROLS 的 key（派生而非手写副本）', () => {
    expect(SUPPORTED_DISPLAY_TYPES).toEqual(['input', 'inputnumber', 'datepicker', 'select'])
  })

  // ---- 反向用例：未命中的枚举集合（delta spec 的核心守护） ----

  it('【反向】未命中的枚举集合 SHALL 可被列出，且其大小被写死 —— 后端加枚举时立刻红', () => {
    // 这条用例是 M1 测试债的直接解药：M1 的 7 条断言全用假值，
    // 于是「24 个枚举只有 1 个能命中」这个系统性偏差一路绿灯通过。
    //
    // 现在把「哪些后端枚举在前端没有对应控件」显式钉住。后端新增枚举而前端
    // 没实现时，用例会红，提示需要补控件或更新预期数字。
    const hit = new Set(SUPPORTED_DISPLAY_TYPES)
    const missing = ALL.filter((t) => !hit.has(t.toLowerCase()))

    // 反向用例（反向断言）：把某个已实现的枚举挪出 CONTROLS
    expect(missing).not.toContain('Input')
    expect(missing).not.toContain('InputNumber')
    expect(missing).not.toContain('DatePicker')
    expect(missing).not.toContain('Select')

    // 未命中数写死：当前 4/24 已实现 → 20 未命中。
    // 实现更多控件时**主动更新这个数字** —— 更新本身即是一次「我知道我在扩范围」的确认。
    expect(missing).toHaveLength(20)
  })

  // ---- 降级标识可见且可辨（delta spec） ----

  it('未实现但属契约枚举的值 SHALL 降级为「平台暂未支持」并标记 known=true', () => {
    render(<DisplayControl attribute={attr({ displayType: 'Cascader' })} value="" onChange={() => {}} />)
    const input = screen.getByTestId('display-control-unknown')
    expect(input).toBeDisabled()
    expect(input.getAttribute('data-known-enum')).toBe('true')
    expect(input.title).toContain('平台暂未支持该控件类型')
    expect(input.title).toContain('Cascader')
  })

  it('连契约枚举都没有的值 SHALL 降级为「未知类型」并标记 known=false', () => {
    render(
      <DisplayControl attribute={attr({ displayType: 'NotATypeAtAll' as never })} value="" onChange={() => {}} />,
    )
    const input = screen.getByTestId('display-control-unknown')
    expect(input.getAttribute('data-known-enum')).toBe('false')
    expect(input.title).toContain('未知控件类型')
  })

  it('displayType 缺省时 SHALL 降级而不抛异常', () => {
    expect(() =>
      render(<DisplayControl attribute={attr({})} value="" onChange={() => {}} />),
    ).not.toThrow()
    expect(screen.getByTestId('display-control-unknown')).toBeDisabled()
  })

  it('onChange SHALL 被透传给命中的控件', () => {
    const onChange = vi.fn()
    render(<DisplayControl attribute={attr({ displayType: 'Input' })} value="" onChange={onChange} />)
    const input = screen.getByDisplayValue('') as HTMLInputElement
    fireEvent.change(input, { target: { value: 'x' } })
    expect(onChange).toHaveBeenCalledWith('x')
  })
})