import { fireEvent, render, screen, waitFor } from '@testing-library/react'
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

    // 分页信息：「共 N 条」在标题区（framework-list-total），
    // 「第 X/Y 页」在表格下方的翻页控件（framework-list-paging）
    expect(screen.getByTestId('framework-list-total').textContent).toContain('共 1 条')
    expect(screen.getByTestId('framework-list-paging').textContent).toContain('第 1/1 页')
    // 单页时两个翻页按钮均禁用（design D4）
    expect(screen.getByTestId('framework-list-prev')).toBeDisabled()
    expect(screen.getByTestId('framework-list-next')).toBeDisabled()
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

  it('点击「下一页」SHALL 重新拉取并替换表格内容（首页「上一页」禁用）', async () => {
    // 用 overrideResponse 函数形式按请求的 curPage 返回不同数据
    server.use(
      getList4MockHandler(async (info) => {
        const url = String(info.request.url)
        const isPage2 = url.includes('curPage=2')
        return {
          success: true,
          code: '0000',
          info: 'success',
          data: {
            curPage: isPage2 ? 2 : 1,
            pageSize: 20,
            totalRow: 34,
            totalPage: 2,
            records: [
              {
                id: isPage2 ? 21 : 1,
                code: isPage2 ? 'page2-code' : 'page1-code',
                name: isPage2 ? '第二页框架' : '第一页框架',
                description: isPage2 ? '第 2 页描述' : '第 1 页描述',
                isPublic: 'Y',
              },
            ],
          },
        } as RPageVOFrameworkSummaryVO
      }),
    )

    renderPage()

    // 第 1 页
    await screen.findByTestId('framework-list-row')
    expect(screen.getByText('page1-code')).toBeInTheDocument()
    expect(screen.getByTestId('framework-list-paging').textContent).toContain('第 1/2 页')
    // 首页：「上一页」禁用，「下一页」可用
    expect(screen.getByTestId('framework-list-prev')).toBeDisabled()
    expect(screen.getByTestId('framework-list-next')).not.toBeDisabled()

    // 点击「下一页」
    fireEvent.click(screen.getByTestId('framework-list-next'))

    // 第 2 页数据替换表格内容
    await waitFor(() => {
      expect(screen.getByText('page2-code')).toBeInTheDocument()
    })
    expect(screen.getByText('第 2 页描述')).toBeInTheDocument()
    // 第 1 页的行 SHALL 已消失（整页替换而非追加）
    expect(screen.queryByText('page1-code')).toBeNull()
    expect(screen.getByTestId('framework-list-paging').textContent).toContain('第 2/2 页')
  })

  it('末页「下一页」SHALL 禁用；禁用态点击 SHALL NOT 触发新请求', async () => {
    let requestCount = 0
    server.use(
      getList4MockHandler(async () => {
        requestCount += 1
        return {
          success: true,
          code: '0000',
          info: 'success',
          data: {
            curPage: 1,
            pageSize: 20,
            totalRow: 34,
            totalPage: 2,
            records: [
              {
                id: 1,
                code: 'only-page',
                name: '单页框架',
                description: '描述',
                isPublic: 'Y',
              },
            ],
          },
        } as RPageVOFrameworkSummaryVO
      }),
    )

    renderPage()
    await screen.findByTestId('framework-list-row')

    const countBefore = requestCount

    // 第 1 页：「下一页」可用
    expect(screen.getByTestId('framework-list-next')).not.toBeDisabled()
    fireEvent.click(screen.getByTestId('framework-list-next'))
    await waitFor(() => {
      expect(screen.getByTestId('framework-list-paging').textContent).toContain('第 2/2 页')
    })
    expect(requestCount).toBe(countBefore + 1)

    // 第 2 页（末页）：「下一页」禁用
    const next = screen.getByTestId('framework-list-next')
    expect(next).toBeDisabled()

    // 点击禁用按钮 SHALL NOT 触发新请求
    fireEvent.click(next)
    await new Promise((r) => setTimeout(r, 30))
    expect(requestCount).toBe(countBefore + 1)
    expect(screen.getByTestId('framework-list-paging').textContent).toContain('第 2/2 页')
  })
})
