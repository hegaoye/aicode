import { act, fireEvent, render, screen, waitFor } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { MemoryRouter } from 'react-router-dom'
import { server } from '../../test-setup'
import { getList2MockHandler } from '../../generated/api.msw'
import type { RPageVOProjectSummaryVO } from '../../generated/model/rPageVOProjectSummaryVO'
import { setToken } from '../../lib/auth/token-store'
import ProjectListPage from './index'

/**
 * Mock ws-client 避免真实连接 —— BuildLogPanel 内部会调用 connect()。
 * `vi.mock` 顶层注册（必须顶层）；mock 让 connect 不真发连接而只记录调用。
 */
// 注意：路径字符串字面必须匹配被 mock 文件的"模块标识"——
// `useBuildLog.tsx` 里 `import { connect } from './ws-client'`，
// vitest 内 vi.mock 用 './ws-client' 字面指向 `./ws-client.ts` 文件。
// 不走绝对路径，避免模块解析差异。
vi.mock(
  // 同一文件的不同字面路径被 vite 当作同一模块
  '../../lib/realtime/ws-client',
  () => ({
    connect: vi.fn(() => ({
      close: vi.fn(),
      send: vi.fn(),
    })),
  }),
)

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
    const fixed: RPageVOProjectSummaryVO = {
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
            state: 'Enable',
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
    // M3-C：list 表格新增 状态 / 更新时间 两列
    expect(row.textContent).toContain('Enable')
    // 反向验证：被裁切字段不应渲染（M3-C 严格只渲染 5 字段 + 操作列）
    expect(row.textContent).not.toContain('description')
    expect(row.textContent).not.toContain('databaseType')
    expect(row.textContent).not.toContain('author')
  })

  it('点击「构建」按钮展开 BuildLogPanel 下方面板', async () => {
    server.use(
      getList2MockHandler({
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
              state: 'Enable',
            },
          ],
        },
      } as unknown as RPageVOProjectSummaryVO),
    )

    renderPage()
    const buildBtn = await screen.findByTestId('project-list-build-button')
    expect(buildBtn).toBeInTheDocument()
    expect(buildBtn).not.toBeDisabled()

    // 点"构建"：触发展开 + 调用 execute + 启动 ws（mock 后无 effect）
    await act(async () => {
      fireEvent.click(buildBtn)
      await Promise.resolve()
      await Promise.resolve()
    })

    // 展开后渲染 project-list-log-row + BuildLogPanel 进入 running
    await waitFor(() => {
      expect(screen.getByTestId('build-log-panel')).toBeInTheDocument()
    })
  })

  it('state !== Enable 时构建按钮禁用', async () => {
    server.use(
      getList2MockHandler({
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
              state: 'Disenable',
            },
          ],
        },
      } as unknown as RPageVOProjectSummaryVO),
    )

    renderPage()

    const btn = await screen.findByTestId('project-list-build-button')
    expect(btn).toBeDisabled()
  })

  it('records 为空时显示「（暂无数据）」', async () => {
    const fixed: RPageVOProjectSummaryVO = {
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
      } as unknown as RPageVOProjectSummaryVO),
    )

    renderPage()

    await waitFor(() => {
      expect(screen.getByTestId('project-list-error')).toBeInTheDocument()
    })
    expect(screen.getByTestId('project-list-error').textContent).toContain('未授权')
  })
})
