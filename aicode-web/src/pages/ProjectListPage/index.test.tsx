import { act, fireEvent, render, screen, waitFor } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { MemoryRouter } from 'react-router-dom'
import { http, HttpResponse } from 'msw'
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
/**
 * 可投递消息的 ws 替身 —— 记录每次 `connect` 的 `onMessage`，
 * 让用例能模拟服务端推送（终态事件）。
 *
 * <p>2026-10-18-building-codes-release-on-terminal 需要它来验证
 * 「构建终态后该行释放『构建中』」这条端到端链路：
 * ws 终态 → hook 终态 → 面板消失 → onClosed → setBuildingCodes 移除。</p>
 */
const wsConnections: Array<{ onMessage: (msg: string | null) => void }> = []
vi.mock(
  // 同一文件的不同字面路径被 vite 当作同一模块
  '../../lib/realtime/ws-client',
  () => ({
    connect: vi.fn((_url: string, onMessage: (msg: string | null) => void) => {
      wsConnections.push({ onMessage })
      return {
        close: vi.fn(),
        send: vi.fn(),
      }
    }),
  }),
)

/** 向最近建立的 ws 连接投递一条消息。 */
function pushWsMessage(msg: string | null) {
  const last = wsConnections[wsConnections.length - 1]
  act(() => {
    last?.onMessage(msg)
  })
}

/**
 * 路由需要 `MemoryRouter` 包裹；token 必须预先存在否则 `<RequireAuth>` 在
 * `App` 之外仍可单独渲染 —— 本测试用例直接渲染 `ProjectListPage` 跳过
 * 守卫，但拦截器仍要 token 存在才能正常请求（否则 axios.get 缺 header 也无所谓）；
 * 为与真实使用一致，预设 localStorage 一个 token。
 */

/** 单行 Enable 数据 —— 「构建中」态的用例用它。 */
const SINGLE_ROW = {
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

/** execute 端点 —— 点「构建」会调它；返回成功即可（构建本身是异步 + ws 的）。 */
function useExecuteOk(counter: { count: number }) {
  server.use(
    http.get('*/project/job/execute*', () => {
      counter.count += 1
      return HttpResponse.json({ success: true, code: '0000', info: 'success', data: null })
    }),
  )
}

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
    wsConnections.length = 0
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

  it('点击「下一页」SHALL 重新拉取并替换表格（首页「上一页」禁用）', async () => {
    // overrideResponse 函数形式按请求的 curPage 返回不同数据
    server.use(
      getList2MockHandler(async (info) => {
        const url = String(info.request.url)
        const isPage2 = url.includes('curPage=2')
        return {
          success: true,
          code: '0000',
          info: 'success',
          data: {
            curPage: isPage2 ? 2 : 1,
            pageSize: 10,
            totalRow: 25,
            totalPage: 3,
            records: [
              {
                code: isPage2 ? 'p2-code' : 'p1-code',
                name: isPage2 ? '第 2 页项目' : '第 1 页项目',
                englishName: isPage2 ? 'page2' : 'page1',
                state: 'Enable',
                createTime: '2026-10-16 13:11:15',
              },
            ],
          },
        } as unknown as RPageVOProjectSummaryVO
      }),
    )

    renderPage()

    // 第 1 页
    await screen.findByTestId('project-list-row')
    expect(screen.getByText('p1-code')).toBeInTheDocument()
    expect(screen.getByTestId('project-list-paging').textContent).toContain('第 1/3 页')
    expect(screen.getByTestId('project-list-total').textContent).toContain('共 25 条')
    // 首页：「上一页」禁用，「下一页」可用
    expect(screen.getByTestId('project-list-prev')).toBeDisabled()
    expect(screen.getByTestId('project-list-next')).not.toBeDisabled()

    // 点击「下一页」
    fireEvent.click(screen.getByTestId('project-list-next'))

    // 第 2 页数据替换表格内容
    await waitFor(() => {
      expect(screen.getByText('p2-code')).toBeInTheDocument()
    })
    expect(screen.queryByText('p1-code')).toBeNull()
    expect(screen.getByTestId('project-list-paging').textContent).toContain('第 2/3 页')
    // 「上一页」此时解除禁用
    expect(screen.getByTestId('project-list-prev')).not.toBeDisabled()
  })

  it('末页「下一页」SHALL 禁用；禁用态点击 SHALL NOT 触发新请求', async () => {
    let requestCount = 0
    server.use(
      getList2MockHandler(async () => {
        requestCount += 1
        return {
          success: true,
          code: '0000',
          info: 'success',
          data: {
            curPage: 1,
            pageSize: 10,
            totalRow: 12,
            totalPage: 2,
            records: [
              {
                code: 'only-page',
                name: '单页项目',
                englishName: 'single',
                state: 'Enable',
                createTime: '2026-10-16 13:11:15',
              },
            ],
          },
        } as unknown as RPageVOProjectSummaryVO
      }),
    )

    renderPage()
    await screen.findByTestId('project-list-row')

    const countBefore = requestCount

    // 第 1 页：「下一页」可用
    expect(screen.getByTestId('project-list-next')).not.toBeDisabled()
    fireEvent.click(screen.getByTestId('project-list-next'))
    await waitFor(() => {
      expect(screen.getByTestId('project-list-paging').textContent).toContain('第 2/2 页')
    })
    expect(requestCount).toBe(countBefore + 1)

    // 第 2 页（末页）：「下一页」禁用
    const next = screen.getByTestId('project-list-next')
    expect(next).toBeDisabled()

    // 点击禁用按钮 SHALL NOT 触发新请求
    fireEvent.click(next)
    await act(async () => {
      await new Promise((r) => setTimeout(r, 30))
    })
    expect(requestCount).toBe(countBefore + 1)
    expect(screen.getByTestId('project-list-paging').textContent).toContain('第 2/2 页')
  })

  it('buildingCodes 跨翻页保留：翻走再翻回，"构建中…"标记仍在', async () => {
    server.use(
      getList2MockHandler(async (info) => {
        const url = String(info.request.url)
        const isPage2 = url.includes('curPage=2')
        return {
          success: true,
          code: '0000',
          info: 'success',
          data: {
            curPage: isPage2 ? 2 : 1,
            pageSize: 10,
            totalRow: 20,
            totalPage: 2,
            records: [
              {
                code: isPage2 ? 'p2-code' : 'p1-code',
                name: isPage2 ? '第 2 页项目' : '第 1 页项目',
                englishName: isPage2 ? 'page2' : 'page1',
                state: 'Enable',
                createTime: '2026-10-16 13:11:15',
              },
            ],
          },
        } as unknown as RPageVOProjectSummaryVO
      }),
    )

    renderPage()
    await screen.findByTestId('project-list-row')

    // 第 1 页点「构建」
    const buildBtn = screen.getByTestId('project-list-build-button')
    await act(async () => {
      fireEvent.click(buildBtn)
      await Promise.resolve()
    })
    // 按钮进入"构建中…"态且被禁用
    await waitFor(() => {
      expect(screen.getByTestId('project-list-build-button')).toBeDisabled()
    })

    // 翻到第 2 页
    fireEvent.click(screen.getByTestId('project-list-next'))
    await waitFor(() => {
      expect(screen.getByText('p2-code')).toBeInTheDocument()
    })
    // 第 2 页是新项目，其构建按钮可用（未被标记）
    expect(screen.getByTestId('project-list-build-button')).not.toBeDisabled()

    // 翻回第 1 页
    fireEvent.click(screen.getByTestId('project-list-prev'))
    await waitFor(() => {
      expect(screen.getByText('p1-code')).toBeInTheDocument()
    })
    // 第 1 页项目的"构建中…"标记仍在（buildingCodes 跨翻页保留，design D2）
    expect(screen.getByTestId('project-list-build-button')).toBeDisabled()
  })

  it('翻回已构建页 SHALL NOT 重复调 execute（BuildLogPanel autoStart 防护）', async () => {
    // design D3 的实施期检查点。
    //
    // 若防护缺失（autoStart 恒为 true），翻回第 1 页时 BuildLogPanel 重新挂载 →
    // startStream() → 第二次 api.execute({ code }) → 后端 ProjectJobServiceImpl#execute
    // 的并发检查发现已有 Executing 任务 → 抛 Server_Error → 用户看到"构建失败"。
    //
    // 本用例直接数 execute 端点被调了几次：首次构建 = 1 次；翻到第 2 页再翻回后
    // SHALL 仍为 1 次。
    let executeCount = 0
    server.use(
      http.get('*/project/job/execute*', () => {
        executeCount += 1
        return HttpResponse.json({
          success: true,
          code: '0000',
          info: 'success',
          data: null,
        })
      }),
    )

    server.use(
      getList2MockHandler(async (info) => {
        const isPage2 = String(info.request.url).includes('curPage=2')
        return {
          success: true,
          code: '0000',
          info: 'success',
          data: {
            curPage: isPage2 ? 2 : 1,
            pageSize: 10,
            totalRow: 20,
            totalPage: 2,
            records: [
              {
                code: isPage2 ? 'p2-code' : 'p1-code',
                name: isPage2 ? '第 2 页项目' : '第 1 页项目',
                englishName: isPage2 ? 'page2' : 'page1',
                state: 'Enable',
                createTime: '2026-10-16 13:11:15',
              },
            ],
          },
        } as unknown as RPageVOProjectSummaryVO
      }),
    )

    renderPage()
    await screen.findByTestId('project-list-row')

    // 首次点击构建 → execute 1 次
    await act(async () => {
      fireEvent.click(screen.getByTestId('project-list-build-button'))
      await Promise.resolve()
    })
    await waitFor(() => {
      expect(executeCount).toBe(1)
    })

    // 翻到第 2 页（面板卸载）
    fireEvent.click(screen.getByTestId('project-list-next'))
    await waitFor(() => {
      expect(screen.getByText('p2-code')).toBeInTheDocument()
    })
    expect(executeCount).toBe(1)

    // 翻回第 1 页（面板重新挂载）—— SHALL NOT 再触发 execute
    fireEvent.click(screen.getByTestId('project-list-prev'))
    await waitFor(() => {
      expect(screen.getByText('p1-code')).toBeInTheDocument()
    })
    // 给潜在的重复 execute 一个机会暴露
    await act(async () => {
      await new Promise((r) => setTimeout(r, 50))
    })

    expect(executeCount).toBe(1)
    // 且面板仍正常挂载（日志面板不因防护而消失）
    expect(screen.getByTestId('project-list-log-row')).toBeInTheDocument()
  })

  // ================================================================
  // 「构建中」态的释放（2026-10-18-building-codes-release-on-terminal）
  //
  // buildingCodes 原本**只有加入没有移除** → 构建结束后该行**永久**卡在
  // 禁用态、失去再次构建的能力（构建一次即报废）。而代码生成的核心
  // 工作流恰恰是「反复构建到满意」。
  //
  // 触发路径：ws 终态 → useBuildLog 终态 → 面板消失 → onClosed →
  // setBuildingCodes 移除该 code。
  // ================================================================

  it('收到 done 终态后该行 SHALL 释放「构建中」，按钮恢复可点', async () => {
    const exec = { count: 0 }
    server.use(getList2MockHandler(SINGLE_ROW))
    useExecuteOk(exec)
    renderPage()
    await screen.findByTestId('project-list-build-button')

    // 点构建 → 进入进行态
    fireEvent.click(screen.getByTestId('project-list-build-button'))
    await waitFor(() => {
      expect(screen.getByTestId('project-list-build-button')).toBeDisabled()
    })
    expect(screen.getByTestId('project-list-build-button').textContent).toContain('构建中')

    // 服务端推送结构化终态
    await act(async () => {
      pushWsMessage(JSON.stringify({ type: 'done', message: '构建成功' }))
      await Promise.resolve()
    })

    // 按钮恢复可点、文案复位 —— 关键断言
    await waitFor(() => {
      expect(screen.getByTestId('project-list-build-button')).not.toBeDisabled()
    })
    expect(screen.getByTestId('project-list-build-button').textContent).not.toContain('构建中')
    // 日志面板已自动收起（上一变更的行为），但这不影响按钮状态
  })

  it('收到 error 终态时面板保留，按钮仍禁用（用户看完错误点关闭才释放）', async () => {
    // 这是刻意设计：构建失败时用户需要看到失败原因，
    // 把错误面板自动藏掉是反直觉的（见 build-log-terminal-signal 的 design D5）。
    const exec = { count: 0 }
    server.use(getList2MockHandler(SINGLE_ROW))
    useExecuteOk(exec)
    renderPage()
    await screen.findByTestId('project-list-build-button')

    fireEvent.click(screen.getByTestId('project-list-build-button'))
    await waitFor(() => {
      expect(screen.getByTestId('project-list-build-button')).toBeDisabled()
    })

    await act(async () => {
      pushWsMessage(JSON.stringify({ type: 'error', message: '构建失败：建表失败' }))
      await Promise.resolve()
    })

    // 面板仍在（可见 ❌ 徽章）→ 按钮仍禁用
    await waitFor(() => {
      expect(screen.getByTestId('build-log-status-error')).toBeInTheDocument()
    })
    expect(screen.getByTestId('project-list-build-button')).toBeDisabled()
  })

  it('翻回正在构建的行 SHALL NOT 误释放（面板以 autoStart=false 挂载、status 初始即 idle）', async () => {
    // 本变更最关键的回归防线：若无 hasShownRef 守卫，翻回的第一次渲染
    // 就会触发 onClosed → 释放进行态 → 用户可重复触发构建
    // （后端 Executing 检查会拒绝，用户看到"点了没反应"）。
    const exec = { count: 0 }
    server.use(
      getList2MockHandler(async (info) => {
        const isPage2 = String(info.request.url).includes('curPage=2')
        return {
          success: true,
          code: '0000',
          info: 'success',
          data: {
            curPage: isPage2 ? 2 : 1,
            pageSize: 10,
            totalRow: 11,
            totalPage: 2,
            records: [
              {
                code: isPage2 ? 'p2-code' : 'p1-code',
                name: isPage2 ? '第 2 页项目' : '第 1 页项目',
                englishName: isPage2 ? 'page2' : 'page1',
                state: 'Enable',
                createTime: '2026-10-16 13:11:15',
              },
            ],
          },
        } as unknown as RPageVOProjectSummaryVO
      }),
    )
    useExecuteOk(exec)
    renderPage()

    await screen.findByTestId('project-list-build-button')
    fireEvent.click(screen.getByTestId('project-list-build-button'))
    await waitFor(() => {
      expect(screen.getByTestId('project-list-build-button')).toBeDisabled()
    })

    // 翻到第 2 页 —— 第 1 页的面板卸载
    fireEvent.click(screen.getByTestId('project-list-next'))
    await waitFor(() => {
      expect(screen.getByText('p2-code')).toBeInTheDocument()
    })
    expect(screen.getByTestId('project-list-build-button')).not.toBeDisabled()

    // 翻回第 1 页 —— 该行面板以 autoStart={false} 重新挂载（mountedPanelsRef 已有该 code）
    fireEvent.click(screen.getByTestId('project-list-prev'))
    await waitFor(() => {
      expect(screen.getByText('p1-code')).toBeInTheDocument()
    })

    // 关键断言：按钮**仍禁用** —— 翻回不等于构建结束
    expect(screen.getByTestId('project-list-build-button')).toBeDisabled()
    expect(screen.getByTestId('project-list-build-button').textContent).toContain('构建中')
  })
})
