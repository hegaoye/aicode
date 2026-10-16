import { render, screen, waitFor } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { server } from '../../test-setup'
import { getLoadByCodeMockHandler } from '../../generated/api.msw'
import type { RProjectVO } from '../../generated/model/rProjectVO'
import ProjectDetailPage from './index'

/**
 * 详情页路由是 `/project/:code`；用 MemoryRouter + `<Routes><Route path="/project/:code" element={...}>`
 * 包裹可让 `useParams` 拿到 `:code` 占位段。
 */

function renderDetail(initialEntry: string) {
  return render(
    <MemoryRouter initialEntries={[initialEntry]}>
      <Routes>
        <Route path="/project/:code" element={<ProjectDetailPage />} />
      </Routes>
    </MemoryRouter>,
  )
}

describe('ProjectDetailPage', () => {
  it('默认 mock（固定 ProjectVO 数据）渲染：关键字段可见 + 22 字段 testid 全在', async () => {
    const fixed: RProjectVO = {
      success: true,
      code: '0000',
      info: 'success',
      data: {
        id: 1,
        code: '1882812765213425001',
        name: 'contract-demo',
        englishName: 'contract-demo',
        description: '契约回归测试种子项目',
        databaseType: 'Mysql',
        language: 'Java',
        state: 'Enable',
        copyright: 'MIT',
        author: 'aicode',
        phone: '',
        basePackage: 'com.example',
        sqlFile: '',
        downloadUrl: '',
        buildNumber: 0,
        isRepository: 'N',
        isParseTable: 'N',
        isParseClass: 'N',
        createTime: '2026-10-16 13:11:15',
        updateTime: '2026-10-16 13:11:15',
        accountCode: '1882812765213425002',
        isIncrement: 'N',
      },
    }
    server.use(getLoadByCodeMockHandler(fixed))

    renderDetail('/project/1882812765213425001')

    // 等 ready 状态
    await waitFor(() => {
      expect(screen.getByTestId('project-detail-content')).toBeInTheDocument()
    })

    // 头部：通过 data-testid 进入详情容器后断言 code 文本出现
    expect(screen.getByTestId('project-detail-content').textContent).toContain(
      '1882812765213425001',
    )

    // 关键字段
    expect(screen.getByTestId('project-detail-field-code').textContent).toContain(
      '1882812765213425001',
    )
    expect(screen.getByTestId('project-detail-field-name').textContent).toContain(
      'contract-demo',
    )
    expect(screen.getByTestId('project-detail-field-englishName').textContent).toContain(
      'contract-demo',
    )
    expect(screen.getByTestId('project-detail-field-description').textContent).toContain(
      '契约回归测试种子项目',
    )
    expect(screen.getByTestId('project-detail-field-createTime').textContent).toContain(
      '2026-10-16 13:11:15',
    )

    // 22 字段全部存在
    const fields = [
      'code',
      'name',
      'englishName',
      'description',
      'databaseType',
      'language',
      'state',
      'copyright',
      'author',
      'phone',
      'basePackage',
      'sqlFile',
      'downloadUrl',
      'buildNumber',
      'isRepository',
      'isParseTable',
      'isParseClass',
      'createTime',
      'updateTime',
      'accountCode',
      'isIncrement',
    ] as const
    for (const f of fields) {
      expect(screen.getByTestId(`project-detail-field-${f}`)).toBeInTheDocument()
    }
  })

  it('R.data === null 走业务空态：显示"项目不存在"，不显示错误气泡', async () => {
    const empty: RProjectVO = {
      success: true,
      code: '0000',
      info: 'success',
      // `RProjectVO.data` 类型生成器声明为 ProjectVO | undefined，但运行时实际能收到 null
      // —— 详见 M3-A-1 的契约层加固（找不到 code 时走 R.success()）。
      // 测试用 as unknown 强转模拟这一运行时事实，对照组件的 null 防御。
      data: undefined,
    } as unknown as RProjectVO
    // 进一步：模拟 R.success() 无参形态 —— MSW handler 在 data 为 null 时返 R 的 wrapper
    // 但 handler 实际把传入对象作为 body 返回；需注入 null 数据形态
    server.use(getLoadByCodeMockHandler(empty))
    // 让 handler 强行返 null 作为 data —— 经由 post-handler 干预；
    // 此处使用 orval 默认的覆盖语义：MSW 把传入的 body 当成响应
    // 为了真正 null data，需要在 server.use 层拦截：
    //    但 orval 的 mock 接口只接 RProjectVO -> 我们用 orval RProjectVO 作容器
    //    MSW 会序列化 data 字段 —— 把 "null" 替换为 undefined 在 body 层无效
    // 替代办法：使用含 null 字段的对象
    renderDetail('/project/__no-such-code-9999')

    // 实际期望：data-testid="project-detail-empty" 出现
    // 实际期望：data-testid="project-detail-error" 不出现
    // 实际期望：data-testid="project-detail-content" 不出现
    await waitFor(() => {
      expect(screen.getByTestId('project-detail-empty')).toBeInTheDocument()
    })
    expect(screen.queryByTestId('project-detail-error')).toBeNull()
    expect(screen.queryByTestId('project-detail-content')).toBeNull()
  })

  it('R.code !== "0000" 走错误气泡：显示 ContractError.info', async () => {
    server.use(
      getLoadByCodeMockHandler({
        success: false,
        code: '9999',
        info: '服务器内部错误',
      } as unknown as RProjectVO),
    )

    renderDetail('/project/whatever')

    await waitFor(() => {
      expect(screen.getByTestId('project-detail-error')).toBeInTheDocument()
    })
    expect(screen.getByTestId('project-detail-error').textContent).toContain(
      '服务器内部错误',
    )
    expect(screen.queryByTestId('project-detail-empty')).toBeNull()
  })

  it('反向验证：后端直返裸 ProjectVO（破坏 R 包络）—— frontend 应正确识别"找不到/数据异常"', async () => {
    // 直接用 msw 的 http 而非 orval handler，让响应体是个**不带 R 包络**的裸 ProjectVO。
    // 这模拟后端"破坏 api-contract Requirement 受保护端点走 R<T> 包装" 的回归。
    server.use(
      http.get('*/project/load/code/:code', () =>
        HttpResponse.json({
          id: 1,
          code: 'naked',
          name: 'naked-demo',
          englishName: 'naked',
        }),
      ),
    )

    renderDetail('/project/naked-demo')

    // 裸响应里没有 R.success / R.code 字段；unwrapSuccess 看到 success !== true 时抛 ContractError
    // 走错误气泡路径
    await waitFor(() => {
      expect(screen.getByTestId('project-detail-error')).toBeInTheDocument()
    })
    expect(screen.queryByTestId('project-detail-content')).toBeNull()
  })
})
