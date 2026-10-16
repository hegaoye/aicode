import { render, screen, waitFor } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it } from 'vitest'
import { MemoryRouter } from 'react-router-dom'
import { server } from '../../test-setup'
import { getList2MockHandler } from '../../generated/api.msw'
import type { RPageVOProjectVO } from '../../generated/model/rPageVOProjectVO'
import { setToken } from '../../lib/auth/token-store'
import ProjectListPage from './index'

/**
 * 路由需要 `MemoryRouter` 包裹；token 必须预先存在否则 `<RequireAuth>` 在
 * `App` 之外仍可单独渲染 —— 本测试用例直接渲染 `ProjectListPage` 跳过
 * 守卫，但拦截器仍要 token 存在才能正常请求（否则 axios.get 缺 header 也无所谓）；
 * 为与真实使用一致，预设 localStorage 一个 token。
 */

function renderPage() {
  return render(
    <MemoryRouter>
      <ProjectListPage />
    </MemoryRouter>,
  )
}

describe('ProjectListPage', () => {
  beforeEach(() => {
    setToken('jwt-for-project-list-test')
  })

  afterEach(() => {
    window.localStorage.clear()
  })

  it('默认 mock 命中后渲染表格行', async () => {
    const fixed: RPageVOProjectVO = {
      success: true,
      code: '0000',
      info: 'success',
      data: {
        curPage: 1,
        pageSize: 10,
        totalRow: 1,
        totalPage: 1,
        records: [
          {
            code: '1882812765213425001',
            name: 'contract-demo',
            englishName: 'contract-demo',
            createTime: '2026-10-16 13:11:15',
          },
        ],
      },
    }
    server.use(getList2MockHandler(fixed))

    renderPage()

    // 等待行出现；records 渲染到 <tr data-testid="project-list-row">
    const row = await screen.findByTestId('project-list-row')
    expect(row).toBeInTheDocument()
    expect(row.textContent).toContain('contract-demo')
    expect(row.textContent).toContain('1882812765213425001')
    expect(row.textContent).toContain('详情')
  })

  it('records 为空时显示「（暂无数据）」', async () => {
    const fixed: RPageVOProjectVO = {
      success: true,
      code: '0000',
      info: 'success',
      data: {
        curPage: 1,
        pageSize: 10,
        totalRow: 0,
        totalPage: 0,
        records: [],
      },
    }
    server.use(getList2MockHandler(fixed))

    renderPage()

    expect(await screen.findByText('（暂无数据）')).toBeInTheDocument()
  })

  it('R.code === "9007" 被解构成错误气泡', async () => {
    server.use(
      getList2MockHandler({
        success: false,
        code: '9007',
        info: '未授权',
        data: undefined,
      } as RPageVOProjectVO),
    )

    renderPage()

    await waitFor(() => {
      expect(screen.getByTestId('project-list-error')).toBeInTheDocument()
    })
    expect(screen.getByTestId('project-list-error').textContent).toContain('未授权')
  })
})
