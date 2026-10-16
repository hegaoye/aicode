import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import type { DisplayAttributeVO } from '../../generated/model/displayAttributeVO'
import DisplayControl, { SUPPORTED_DISPLAY_TYPES } from './index'

function attr(over: Partial<DisplayAttributeVO> = {}): DisplayAttributeVO {
  return { ...over }
}

describe('DisplayControl 分发器', () => {
  it('text —— displayType 字面为 text 渲染原生 text input', () => {
    render(<DisplayControl attribute={attr({ displayType: 'text' })} value="hello" onChange={() => {}} />)
    expect((screen.getByDisplayValue('hello') as HTMLInputElement).type).toBe('text')
  })

  it('number —— displayType 字面为 number 渲染原生 number input', () => {
    render(<DisplayControl attribute={attr({ displayType: 'number' })} value={42} onChange={() => {}} />)
    expect((screen.getByDisplayValue('42') as HTMLInputElement).type).toBe('number')
  })

  it('date —— displayType 字面为 date 渲染原生 date input', () => {
    render(<DisplayControl attribute={attr({ displayType: 'date' })} value="2026-10-16" onChange={() => {}} />)
    expect((screen.getByDisplayValue('2026-10-16') as HTMLInputElement).type).toBe('date')
  })

  it('select —— displayType 字面为 select 时按 fieldValidationMode 渲染 options', () => {
    render(
      <DisplayControl
        attribute={attr({ displayType: 'select', fieldValidationMode: '未知/是/否' })}
        value="是"
        onChange={() => {}}
      />,
    )
    const select = screen.getByRole('combobox') as HTMLSelectElement
    expect(select.tagName.toLowerCase()).toBe('select')
    expect(Array.from(select.options).map((o) => o.text)).toEqual(['请选择', '未知', '是', '否'])
  })

  it('select —— 缺省 fieldValidationMode 时回退到「未知/是/否」', () => {
    render(<DisplayControl attribute={attr({ displayType: 'select' })} value="" onChange={() => {}} />)
    const select = screen.getByRole('combobox') as HTMLSelectElement
    expect(Array.from(select.options).map((o) => o.text)).toEqual(['请选择', '未知', '是', '否'])
  })

  it('unknown —— 未识别的 displayType（"Switch"、"Input" 等）走 unknown 兜底', () => {
    const onChange = vi.fn()
    render(<DisplayControl attribute={attr({ displayType: 'Switch' })} value="" onChange={onChange} />)
    const input = document.querySelector('input.display-control--unknown') as HTMLInputElement
    expect(input).not.toBeNull()
    expect(input.disabled).toBe(true)
    expect(input.title).toContain('Switch')
  })

  it('SUPPORTED_DISPLAY_TYPES 当前为 text / number / date / select', () => {
    expect(SUPPORTED_DISPLAY_TYPES).toEqual(['text', 'number', 'date', 'select'])
  })
})

