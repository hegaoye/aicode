import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import LoginPage from './pages/LoginPage'
import type { MapFieldColumnVO } from './generated/model/mapFieldColumnVO'

describe('契约驱动的最小组件测试（M0 骨架）', () => {
  it('登录页渲染出账号与密码输入框', () => {
    render(<LoginPage />)
    expect(screen.getByLabelText('账号')).toBeInTheDocument()
    expect(screen.getByLabelText('密码')).toBeInTheDocument()
  })

  it('前端类型来自契约导出的模型：字段集合与后端 VO 一致', () => {
    // 若后端删掉 /displayAttribute/list 的某个字段，契约变化 -> 此处类型变化 -> 编译失败。
    // 这就是契约驱动要提供的价值。
    const field: MapFieldColumnVO = {
      code: 'col-1',
      column: 'gift_name',
      field: 'giftName',
      displayAttribute: { displayType: 'Input', isRequired: 'Y', matchType: 'like' },
    }
    expect(field.displayAttribute?.displayType).toBe('Input')
  })
})
