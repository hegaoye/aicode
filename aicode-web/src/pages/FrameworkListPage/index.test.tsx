import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it } from 'vitest'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { server } from '../../test-setup'
import {
  getBuild5MockHandler,
  getDelete1MockHandler,
  getList4MockHandler,
  getModify1MockHandler,
  getUpdateIsPublic1MockHandler,
} from '../../generated/api.msw'
import type { RPageVOFrameworkSummaryVO } from '../../generated/model/rPageVOFrameworkSummaryVO'
import type { RFrameworkSummaryVO } from '../../generated/model/rFrameworkSummaryVO'
import type { FrameworkSummaryVO } from '../../generated/model/frameworkSummaryVO'
import { setToken, clearToken } from '../../lib/auth/token-store'
import FrameworkListPage from './index'

/**
 * `FrameworkListPage` 9 用例：
 *   1. 成功渲染 —— `records` 含 1 条，表格可见 code / name / description / 中文「公开」，
 *      分页信息含「共 1 条」
 *   2. 空态 —— `records: []`，空态文案出现且错误气泡不出现
 *   3. 错误 —— `R.code !== "0000"`，错误气泡含 info 且无列表行
 *   4. 翻页 —— 点「下一页」整页替换
 *   5. 末页 —— 「下一页」禁用且禁用态点击不触发新请求
 *   6. 「添加」按钮 —— 渲染完整视觉但 disabled + title 标注接口待修复，点击不发请求
 *   7. 行内「删除」 —— 同样 disabled + title，点击不发请求
 *   8. 「编辑」 —— 打开弹窗并带出该行现值，必填校验 + 提交走 modify1
 *   9. 「是否公开」 —— toggle 调 updateIsPublic1 并按响应刷新该行
 *
 * 1–5 为视觉重写前既有的契约回归用例：本次重写只调整了选择器
 * （原型多了 `.container` / `.panel` / `.table-wrap` / `.pager` 等包裹层，
 * 且「技术编码」从独立列改为 `.sr-only`），**断言语义一个字未改**。
 *
 * 全部用 `server.use(getList4MockHandler(fixed))` 覆盖确定性数据，
 * 避免 orval 默认 mock 用 faker 随机生成 `success` 布尔导致偶发失败。
 *
 * 路由：组件用 `useEffect` + axios（不依赖路由），但沿 `ProjectDetailPage.test.tsx`
 * 的形态用 `MemoryRouter` + `<Route path="/frameworks">` 包裹以保持一致性。
 */

/** 一条固定记录，供写侧用例复用。 */
const SAMPLE: FrameworkSummaryVO = {
  id: 1,
  code: '1882812765213425664',
  name: 'springboot2.1.6-mybatisplus-redis-provider',
  description: '此框架以 springcloud Greenwich.RELEASE 为基础进行整合',
  gitHome: 'https://github.com/hegaoye/aicode-template.git',
  account: 'hegaoye@qq.com',
  isPublic: 'Y',
}

/** 把 SAMPLE 裹进 `R<PageVO<FrameworkSummaryVO>>`。 */
function pageOf(records: FrameworkSummaryVO[], overrides?: Partial<{ curPage: number; pageSize: number; totalRow: number; totalPage: number }>): RPageVOFrameworkSummaryVO {
  return {
    success: true,
    code: '0000',
    info: 'success',
    data: {
      curPage: 1,
      pageSize: 20,
      totalRow: records.length,
      totalPage: 1,
      records,
      ...overrides,
    },
  }
}

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

  /* -------------------------------------------------------------------------
   * 写侧用例（视觉重写新增）—— 4 个写端点里只有 2 个契约入参完整：
   *   可用：modify1（编辑）、updateIsPublic1（是否公开）
   *   待修：build5（添加）、delete1（删除）—— 契约无入参，按钮渲染视觉但禁用
   * ---------------------------------------------------------------------- */

  it('「添加」按钮 SHALL 渲染完整视觉但 disabled 且 title 标注接口待修复', async () => {
    // 计数型 handler：一旦「添加」真的发出请求，这里就会被记下来
    let buildCalls = 0
    server.use(
      getList4MockHandler(pageOf([SAMPLE])),
      getBuild5MockHandler(() => {
        buildCalls += 1
        return { success: true, code: '0000', info: 'success' }
      }),
    )

    renderPage()
    await screen.findByTestId('framework-list-row')

    const add = screen.getByTestId('framework-list-add')
    // 视觉完整：原型的主按钮样式 + 文字 + 加号图标都在
    expect(add).toHaveClass('btn-primary')
    expect(add.textContent).toContain('添加')
    expect(add.querySelector('svg')).not.toBeNull()
    // 行为禁用 + 自解释
    expect(add).toBeDisabled()
    expect(add).toHaveAttribute('title', '接口待修复，验收后接入')

    // 点击禁用按钮 SHALL NOT 产生任何请求
    fireEvent.click(add)
    await new Promise((r) => setTimeout(r, 30))
    expect(buildCalls).toBe(0)
  })

  it('行内「删除」按钮 SHALL 同样 disabled 且点击不发请求', async () => {
    let deleteCalls = 0
    server.use(
      getList4MockHandler(pageOf([SAMPLE])),
      getDelete1MockHandler(() => {
        deleteCalls += 1
        return { success: true, code: '0000', info: 'success', data: 'ok' }
      }),
    )

    renderPage()
    await screen.findByTestId('framework-list-row')

    const del = screen.getByTestId('framework-list-delete')
    // 视觉完整：原型的垃圾桶图标 + .del 着色（红色动作）
    expect(del).toHaveClass('del')
    expect(del.querySelector('svg')).not.toBeNull()
    expect(del).toBeDisabled()
    expect(del).toHaveAttribute('title', '接口待修复，验收后接入')

    // 点击禁用按钮 SHALL NOT 产生任何请求，且该行 SHALL 仍在
    fireEvent.click(del)
    await new Promise((r) => setTimeout(r, 30))
    expect(deleteCalls).toBe(0)
    expect(screen.getByTestId('framework-list-row')).toBeInTheDocument()
  })

  it('「编辑」SHALL 打开弹窗并带出该行现值，提交走 modify1', async () => {
    const seen: URLSearchParams[] = []
    server.use(
      getList4MockHandler(pageOf([SAMPLE])),
      getModify1MockHandler((info) => {
        seen.push(new URL(String(info.request.url)).searchParams)
        return {
          success: true,
          code: '0000',
          info: 'success',
          data: { ...SAMPLE, name: '改名后的技术', isPublic: 'N' },
        } satisfies RFrameworkSummaryVO
      }),
    )

    renderPage()
    await screen.findByTestId('framework-list-row')

    // 初始关闭
    expect(screen.getByTestId('framework-modal-mask')).not.toHaveClass('open')

    fireEvent.click(screen.getByTestId('framework-list-edit'))

    // 弹窗打开（.modal-mask.open），5 个字段全部带出该行现值
    expect(screen.getByTestId('framework-modal-mask')).toHaveClass('open')
    expect(screen.getByTestId('framework-form-name')).toHaveValue(SAMPLE.name)
    expect(screen.getByTestId('framework-form-git-home')).toHaveValue(SAMPLE.gitHome)
    expect(screen.getByTestId('framework-form-account')).toHaveValue(SAMPLE.account)
    expect(screen.getByTestId('framework-form-description')).toHaveValue(SAMPLE.description)
    expect(screen.getByTestId('framework-form-public-toggle')).toHaveClass('toggle')
    expect(screen.getByTestId('framework-form-public-toggle')).toHaveAttribute(
      'aria-checked',
      'true',
    )

    // 必填校验：清空「技术名称」后提交 SHALL 标红（.form-field.invalid + .err）且不发请求
    fireEvent.change(screen.getByTestId('framework-form-name'), { target: { value: '' } })
    fireEvent.click(screen.getByTestId('framework-modal-confirm'))
    expect(screen.getByText('请输入技术名称').closest('.form-field')).toHaveClass('invalid')
    expect(seen).toHaveLength(0)

    // 补回 + 改名 + 关掉「对外可见」后提交
    fireEvent.change(screen.getByTestId('framework-form-name'), {
      target: { value: '改名后的技术' },
    })
    fireEvent.click(screen.getByTestId('framework-form-public-toggle'))
    fireEvent.click(screen.getByTestId('framework-modal-confirm'))

    await waitFor(() => {
      expect(seen).toHaveLength(1)
    })
    const params = seen[0]
    expect(params.get('code')).toBe(SAMPLE.code)
    expect(params.get('name')).toBe('改名后的技术')
    expect(params.get('gitHome')).toBe(SAMPLE.gitHome)
    expect(params.get('account')).toBe(SAMPLE.account)
    expect(params.get('isPublic')).toBe('N')
    expect(params.get('description')).toBe(SAMPLE.description)
    // ⚠️ 凭据防线：SHALL NOT 把 password 送回后端 —— 列表 VO 里根本没有它，
    // 前端拿不到也不该回填（api-data-object 的读投影已刻意裁掉）。
    expect(params.has('password')).toBe(false)

    // 提交成功：弹窗关闭 + 该行按响应刷新
    await waitFor(() => {
      expect(screen.getByTestId('framework-modal-mask')).not.toHaveClass('open')
    })
    expect(screen.getByTestId('framework-list-row').textContent).toContain('改名后的技术')
    expect(screen.getByTestId('framework-list-toggle')).toHaveClass('off')
  })

  it('「是否公开」toggle SHALL 调 updateIsPublic1 并按响应刷新', async () => {
    const seen: URLSearchParams[] = []
    server.use(
      getList4MockHandler(pageOf([SAMPLE])),
      getUpdateIsPublic1MockHandler((info) => {
        seen.push(new URL(String(info.request.url)).searchParams)
        // 后端回 N —— 前端 SHALL 按响应刷新，而不是自己乐观翻转
        return {
          success: true,
          code: '0000',
          info: 'success',
          data: { ...SAMPLE, isPublic: 'N' },
        } satisfies RFrameworkSummaryVO
      }),
    )

    renderPage()
    const toggle = await screen.findByTestId('framework-list-toggle')
    expect(toggle).not.toHaveClass('off')
    expect(toggle).toHaveAttribute('aria-checked', 'true')

    fireEvent.click(toggle)

    await waitFor(() => {
      expect(seen).toHaveLength(1)
    })
    expect(seen[0].get('code')).toBe(SAMPLE.code)
    expect(seen[0].get('isPublic')).toBe('N')

    // 按响应刷新：isPublic = 'N' → 开关落到 .off，文案变「非公开」
    await waitFor(() => {
      expect(screen.getByTestId('framework-list-toggle')).toHaveClass('off')
    })
    expect(screen.getByTestId('framework-list-toggle')).toHaveAttribute(
      'aria-checked',
      'false',
    )
    expect(screen.getByTestId('framework-list-row').textContent).toContain('非公开')
  })
})
