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

  /*
   * 以下两条守护本轮「6 面板视觉重写」引入的两条新边界：
   *   ① 两个 pending 面板不得被「顺手接上接口」而消失 —— 它们是契约缺入参的
   *      显式声明（AGENTS §10），面板必须以 pending + 空态如实呈现；
   *   ② 重组面板不得顺手扩字段 —— M3-C 把 ProjectVO 的 22 字段裁到 12，
   *      重写是最容易「顺手」把裁掉的字段加回来的时机（每加一个 testid 就多一份
   *      前端消费面 = 多一份契约面）。
   */

  it('技术信息与仓库信息面板 SHALL 渲染为 pending 态并标注接口待修复', async () => {
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

    await waitFor(() => {
      expect(screen.getByTestId('project-detail-content')).toBeInTheDocument()
    })

    // 两个面板各自一个 pending 徽标（注意用 >= 2 而非 === 2：「数据管理」的徽标
    // 由 listMapClassTable 探针决定，探针在单测里打不通 → fail-open 落到 pending，
    // 徽标数因此不是常量）
    expect(document.querySelectorAll('.badge.pending').length).toBeGreaterThanOrEqual(2)

    for (const testId of [
      'project-detail-panel-tech',
      'project-detail-panel-repo',
    ]) {
      const panel = screen.getByTestId(testId)
      const badge = panel.querySelector('.badge.pending')
      expect(badge).not.toBeNull()
      expect(badge?.textContent).toBe('待配置')
      // 空态文案来自 page-error 之外的 .empty-state，且逐面板断言 ——
      // 只断言「页面里某处有这句话」会让两个面板共用一条而漏掉其中一个
      const body = panel.querySelector('.panel-body')
      expect(body?.textContent).toContain('接口待修复')
    }

    // 两个 pending 面板都不渲染「编辑 →」（契约缺入参，给了就是死链接）
    expect(
      screen.getByTestId('project-detail-panel-tech').querySelector('.edit'),
    ).toBeNull()
    expect(
      screen.getByTestId('project-detail-panel-repo').querySelector('.edit'),
    ).toBeNull()
  })

  it('项目信息面板 SHALL 只用 ProjectDetailVO 的 12 字段（不得出现 ProjectVO 的裁切字段）', async () => {
    /*
     * 注入「ProjectVO 全集」：12 个 ProjectDetailVO 字段 + 10 个 M3-C 被裁掉的字段。
     * 后者不在 ProjectDetailVO 类型里，故整体 as unknown 绕过 —— 素材要的就是
     * 「后端若把它们塞回来，前端也不认」。
     */
    const full: RProjectDetailVO = {
      success: true,
      code: '0000',
      info: 'success',
      data: {
        code: '1882812765213425001',
        name: 'full-demo',
        englishName: 'full-demo',
        description: '12 字段全集',
        copyright: 'MIT',
        author: 'aicode',
        phone: '13800000000',
        basePackage: 'com.example',
        buildNumber: 3,
        createTime: '2026-10-16 13:11:15',
        updateTime: '2026-10-17 09:02:33',
        accountCode: '1882812765213425002',
        // ↓ M3-C 裁掉的 10 个字段
        id: 42,
        databaseType: 'mysql',
        language: 'java',
        state: 'Enable',
        sqlFile: 'demo.sql',
        downloadUrl: 'https://example.test/demo.zip',
        isRepository: 'Y',
        isParseTable: 'Y',
        isParseClass: 'N',
        isIncrement: 'Y',
      },
    } as unknown as RProjectDetailVO
    server.use(getLoadByCodeMockHandler(full))

    renderDetail('/project/1882812765213425001')

    await waitFor(() => {
      expect(screen.getByTestId('project-detail-content')).toBeInTheDocument()
    })

    // 主栅格的 8 格（对齐原型 project-review.html 的 8 个槽位）
    const infoFields = [
      'name',
      'englishName',
      'code',
      'copyright',
      'author',
      'phone',
      'description',
      'basePackage',
    ] as const
    for (const f of infoFields) {
      expect(screen.getByTestId(`project-detail-field-${f}`)).toBeInTheDocument()
    }
    /*
     * 布局守卫：这 8 格必须落在 `.grid-2`（原型的两列栅格）里，而不是直接平铺在
     * `.panel-body` 上。少了这条，重写时把栅格容器删掉不会有任何用例变红 ——
     * testid 断言与容器无关，视觉却已经塌成单列长条。
     */
    for (const f of infoFields) {
      expect(screen.getByTestId(`project-detail-field-${f}`).closest('.grid-2')).not.toBeNull()
    }
    // 值也要真的落到 DOM（只断言 testid 存在的话，写死占位符也能过）
    expect(screen.getByTestId('project-detail-field-name').textContent).toContain(
      'full-demo',
    )
    expect(
      screen.getByTestId('project-detail-field-basePackage').textContent,
    ).toContain('com.example')
    expect(screen.getByTestId('project-detail-field-phone').textContent).toContain(
      '13800000000',
    )

    // 第二段栅格的 4 格 —— 合起来正好是 ProjectDetailVO 的 12 个字段
    for (const f of ['buildNumber', 'createTime', 'updateTime', 'accountCode'] as const) {
      expect(screen.getByTestId(`project-detail-field-${f}`)).toBeInTheDocument()
    }

    // 反向：10 个裁切字段一个都不许出现（即使后端把它们塞进了响应）
    const trimmedFields = [
      'id',
      'databaseType',
      'language',
      'state',
      'sqlFile',
      'downloadUrl',
      'isRepository',
      'isParseTable',
      'isParseClass',
      'isIncrement',
    ] as const
    for (const f of trimmedFields) {
      expect(screen.queryByTestId(`project-detail-field-${f}`)).toBeNull()
    }
  })
})
