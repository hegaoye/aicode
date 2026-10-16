import { render, screen, waitFor } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { server } from '../../test-setup'
import { getLoadByCodeMockHandler } from '../../generated/api.msw'
import type { RProjectDetailVO } from '../../generated/model/rProjectDetailVO'
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

describe('ProjectDetailPage（M3-C 字段裁切后）', () => {
  it('默认 mock 命中后渲染：12 字段（ProjectDetailVO）testid 全在', async () => {
    const fixed: RProjectDetailVO = {
      success: true,
      code: '0000',
      info: 'success',
      data: {
        code: '1882812765213425001',
        name: 'contract-demo',
        englishName: 'contract-demo',
        description: '契约回归测试种子项目',
        copyright: 'MIT',
        author: 'aicode',
        phone: '',
        basePackage: 'com.example',
        buildNumber: 0,
        createTime: '2026-10-16 13:11:15',
        updateTime: '2026-10-16 13:11:15',
        accountCode: '1882812765213425002',
      },
    }
    server.use(getLoadByCodeMockHandler(fixed))

    renderDetail('/project/1882812765213425001')

    // 等 ready 状态
    await waitFor(() => {
      expect(screen.getByTestId('project-detail-content')).toBeInTheDocument()
    })

    // 头部
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

    // 12 字段全在（与 ProjectDetailVO 持久化字段一致）
    const fields = [
      'code',
      'name',
      'englishName',
      'description',
      'copyright',
      'author',
      'phone',
      'basePackage',
      'buildNumber',
      'createTime',
      'updateTime',
      'accountCode',
    ] as const
    for (const f of fields) {
      expect(screen.getByTestId(`project-detail-field-${f}`)).toBeInTheDocument()
    }

    // 反向：M3-C 裁切掉的字段不应渲染
    // 即：M3-A-2 详情页曾展示的 22 字段中，被裁的 10 字段
    // （databaseType / language / state / sqlFile / downloadUrl /
    //  isRepository / isParseTable / isParseClass / isIncrement / id）不出现
    expect(
      screen.queryByTestId('project-detail-field-databaseType'),
    ).toBeNull()
    expect(
      screen.queryByTestId('project-detail-field-language'),
    ).toBeNull()
    expect(screen.queryByTestId('project-detail-field-state')).toBeNull()
    expect(
      screen.queryByTestId('project-detail-field-sqlFile'),
    ).toBeNull()
    expect(
      screen.queryByTestId('project-detail-field-downloadUrl'),
    ).toBeNull()
    expect(
      screen.queryByTestId('project-detail-field-isRepository'),
    ).toBeNull()
    expect(
      screen.queryByTestId('project-detail-field-isParseTable'),
    ).toBeNull()
    expect(
      screen.queryByTestId('project-detail-field-isParseClass'),
    ).toBeNull()
    expect(
      screen.queryByTestId('project-detail-field-isIncrement'),
    ).toBeNull()
  })

  it('R.data === null 走业务空态：显示"项目不存在"，不显示错误气泡', async () => {
    const empty: RProjectDetailVO = {
      success: true,
      code: '0000',
      info: 'success',
      data: undefined,
    } as unknown as RProjectDetailVO
    server.use(getLoadByCodeMockHandler(empty))
    renderDetail('/project/__no-such-code-9999')

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
      } as unknown as RProjectDetailVO),
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

  it('反向验证：后端直返裸 ProjectDetailVO（破坏 R 包络）—— frontend 应正确识别"找不到/数据异常"', async () => {
    server.use(
      http.get('*/project/load/code/:code', () =>
        HttpResponse.json({
          code: 'naked',
          name: 'naked-demo',
          englishName: 'naked',
        }),
      ),
    )

    renderDetail('/project/naked-demo')

    await waitFor(() => {
      expect(screen.getByTestId('project-detail-error')).toBeInTheDocument()
    })
    expect(screen.queryByTestId('project-detail-content')).toBeNull()
  })
})
