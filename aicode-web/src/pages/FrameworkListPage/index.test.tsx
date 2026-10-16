import { render, screen, waitFor } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it } from 'vitest'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { server } from '../../test-setup'
import { getList4MockHandler } from '../../generated/api.msw'
import type { RPageVOFrameworkSummaryVO } from '../../generated/model/rPageVOFrameworkSummaryVO'
import { setToken, clearToken } from '../../lib/auth/token-store'
import FrameworkListPage from './index'

/**
 * `FrameworkListPage` 3 用例（详见 `openspec/specs/aicode-web-page/spec.md` 的
 * 「列表类业务页面 SHALL 通过 mount 即拉取并 unwrapSuccess 消费 PageVO<T> 包络」）：
 *   1. 成功渲染 —— `records` 含 1 条，表格可见 code / name / description / 中文「公开」，
 *      分页信息含「共 1 条」
 *   2. 空态 —— `records: []`，空态文案出现且错误气泡不出现
 *   3. 错误 —— `R.code !== "0000"`，错误气泡含 info 且无列表行
 *
 * 全部用 `server.use(getList4MockHandler(fixed))` 覆盖确定性数据，
 * 避免 orval 默认 mock 用 faker 随机生成 `success` 布尔导致偶发失败。
 *
 * 路由：组件用 `useEffect` + axios（不依赖路由），但沿 `ProjectDetailPage.test.tsx`
 * 的形态用 `MemoryRouter` + `<Route path="/frameworks">` 包裹以保持一致性。
 */

function renderPage() {
  return render(
    <MemoryRouter initialEntries={['/frameworks']}>
      <Routes>
        <Route path="/frameworks" element={<FrameworkListPage />} />
      </Routes>
    </MemoryRouter>,
  )
}

describe('FrameworkListPage', () => {
  beforeEach(() => {
    // 预设 token 使 useAuth().isAuthenticated 为 true（页面自身不依赖它，
    // 但保持与真实使用一致，避免将来引入 RequireAuth 时测试假绿）
    setToken('jwt-for-framework-list-test')
  })

  afterEach(() => {
    clearToken()
  })

  it('成功渲染：表格可见 code / name / description / 中文「公开」+ 分页信息', async () => {
    const fixed: RPageVOFrameworkSummaryVO = {
      success: true,
      code: '0000',
      info: 'success',
      data: {
        curPage: 1,
        pageSize: 20,
        totalRow: 1,
        totalPage: 1,
        records: [
          {
            id: 1,
            code: '1882812765213425664',
            name: 'springboot2.1.6-mybatisplus-redis-provider',
            description: '此框架以 springcloud Greenwich.RELEASE 为基础进行整合',
            gitHome: 'https://github.com/hegaoye/aicode-template.git',
            account: 'hegaoye@qq.com',
            isPublic: 'Y',
          },
        ],
      },
    }
    server.use(getList4MockHandler(fixed))

    renderPage()

    const row = await screen.findByTestId('framework-list-row')
    expect(row.textContent).toContain('1882812765213425664')
    expect(row.textContent).toContain('springboot2.1.6-mybatisplus-redis-provider')
    expect(row.textContent).toContain('此框架以 springcloud Greenwich.RELEASE 为基础进行整合')
    // isPublic 的 'Y' SHALL 渲染为中文「公开」，SHALL NOT 渲染存储值 'Y'
    expect(row.textContent).toContain('公开')
    expect(row.textContent).not.toContain('>Y<')

    // 分页信息
    const paging = screen.getByTestId('framework-list-paging')
    expect(paging.textContent).toContain('共 1 条')
    expect(paging.textContent).toContain('第 1/1 页')
  })

  it('records 为空时显示业务空态，不显示错误气泡', async () => {
    const empty: RPageVOFrameworkSummaryVO = {
      success: true,
      code: '0000',
      info: 'success',
      data: {
        curPage: 1,
        pageSize: 20,
        totalRow: 0,
        totalPage: 0,
        records: [],
      },
    }
    server.use(getList4MockHandler(empty))

    renderPage()

    expect(await screen.findByTestId('framework-list-empty')).toBeInTheDocument()
    expect(screen.queryByTestId('framework-list-error')).toBeNull()
    expect(screen.queryByTestId('framework-list-row')).toBeNull()
  })

  it('R.code !== "0000" 走错误气泡：含 ContractError.info 且不渲染列表行', async () => {
    server.use(
      getList4MockHandler({
        success: false,
        code: '9999',
        info: '服务器内部错误',
      } as unknown as RPageVOFrameworkSummaryVO),
    )

    renderPage()

    await waitFor(() => {
      expect(screen.getByTestId('framework-list-error')).toBeInTheDocument()
    })
    expect(screen.getByTestId('framework-list-error').textContent).toContain(
      '服务器内部错误',
    )
    expect(screen.queryByTestId('framework-list-row')).toBeNull()
  })
})
