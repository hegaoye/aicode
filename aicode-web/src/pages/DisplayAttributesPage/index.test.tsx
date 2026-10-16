import { render, screen, waitFor } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { server } from '../../test-setup'
import { getList5MockHandler } from '../../generated/api.msw'
import type { RListMapFieldColumnVO } from '../../generated/model/rListMapFieldColumnVO'
import DisplayAttributesPage from './index'

/**
 * MSW 生命周期由 `src/test-setup.ts` 在 vitest 全局钩子里启停，
 * 本测试只针对该页面的渲染与错误分支做断言。
 */

describe('DisplayAttributesPage', () => {
  it('默认 mock 命中后渲染表格行（displayName 文本可见）', async () => {
    // 默认 mock 用 faker 随机数据，displayName 可能是 undefined；
    // 这里直接覆写 handler 拿到确定性的两条记录。
    const fixed: RListMapFieldColumnVO = {
      success: true,
      code: '0000',
      info: 'success',
      data: [
        {
          mapClassTableCode: 'mct1',
          code: 'fc1',
          column: 'name',
          field: 'name',
          displayAttribute: {
            displayType: 'text',
            displayName: '名称',
          },
        },
        {
          mapClassTableCode: 'mct1',
          code: 'fc2',
          column: 'age',
          field: 'age',
          displayAttribute: {
            displayType: 'number',
            displayName: '年龄',
          },
        },
      ],
    }
    server.use(getList5MockHandler(fixed))

    render(<DisplayAttributesPage />)

    expect(await screen.findByText('名称')).toBeInTheDocument()
    expect(screen.getByText('年龄')).toBeInTheDocument()
    // 数据库列名/Java 属性名 列各有相同字串两次（行重复出现），用 getAllByText 校验存在即可
    expect(screen.getAllByText('name').length).toBeGreaterThan(0)
    expect(screen.getAllByText('age').length).toBeGreaterThan(0)
  })

  it('失败响应（R.code !== "0000"）被解构成错误气泡', async () => {
    server.use(
      getList5MockHandler({ success: false, code: '9007', info: '未授权', data: [] }),
    )

    render(<DisplayAttributesPage />)

    await waitFor(() => {
      expect(screen.getByTestId('display-attributes-error')).toBeInTheDocument()
    })
    expect(screen.getByTestId('display-attributes-error').textContent).toContain('未授权')
  })
})
