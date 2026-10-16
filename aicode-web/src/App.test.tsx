import { render, screen, waitFor } from '@testing-library/react'
import { afterEach, describe, expect, it } from 'vitest'
import { MemoryRouter } from 'react-router-dom'
import App from './App'
import LoginPage from './pages/LoginPage'
import { server } from './test-setup'
import { getLoadByCodeMockHandler } from './generated/api.msw'
import { clearToken, setToken } from './lib/auth/token-store'
import type { MapFieldColumnVO } from './generated/model/mapFieldColumnVO'

describe('契约驱动的最小组件测试（M0 骨架）', () => {
  it('登录页渲染出账号与密码输入框', () => {
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    )
    expect(screen.getByLabelText('账号')).toBeInTheDocument()
    expect(screen.getByLabelText('密码')).toBeInTheDocument()
  })

  it('前端类型来自契约导出的模型：字段集合与后端 VO 一致', () => {
    // 若后端删掉 /displayAttribute/list 的某个字段，契约变化 -> 此处类型变化 -> 编译失败。
    // 这就是契约驱动要提供的价值。
    const field: MapFieldColumnVO = {
      code: 'col-1',
      column: 'gift_name',
      field: 'giftName',
      displayAttribute: { displayType: 'Input', isRequired: 'Y', matchType: 'like' },
    }
    expect(field.displayAttribute?.displayType).toBe('Input')
  })
})

/**
 * 路由表本身的可达性 —— 视觉重写之前 `/project/:code` 挂的还是 M2 占位页，
 * 即「列表 → 详情」这条路在界面上根本走不通（组件与用例都存在，只是没接线）。
 */
describe('路由接线', () => {
  afterEach(() => {
    clearToken()
  })

  /**
   * orval 的 faker mock 生成的 `code` 是随机字符串而非 '0000'，
   * `unwrapSuccess` 会抛错 —— 路由可达性用例必须显式给一份成功响应。
   */
  const okDetail = {
    success: true,
    code: '0000',
    info: 'success',
    data: {
      code: '1882812765213425001',
      name: 'contract-demo',
      englishName: 'contract-demo',
      description: '路由可达性测试种子项目',
      copyright: 'MIT',
      author: 'aicode',
      phone: '',
      basePackage: 'com.example',
      buildNumber: 0,
      databaseType: 'mysql',
      language: 'java',
    },
  }

  it('路由 SHALL 把 /project/:code 接到 ProjectDetailPage（而非占位页）', async () => {
    setToken('test-token')
    server.use(getLoadByCodeMockHandler(okDetail))

    render(
      <MemoryRouter initialEntries={['/project/abc123']}>
        <App />
      </MemoryRouter>,
    )

    await waitFor(() => {
      expect(screen.getByTestId('project-detail-content')).toBeInTheDocument()
    })
    expect(screen.queryByTestId('project-placeholder-code')).not.toBeInTheDocument()
  })

  it('顺序防线：/project/new SHALL 不被 /project/:code 吃掉', async () => {
    // React Router 按声明顺序匹配 —— 若 /project/:code 声明在前，
    // 字符串 new 会被当作 projectCode，详情页拿到 code=new。
    setToken('test-token')
    server.use(getLoadByCodeMockHandler(okDetail))

    render(
      <MemoryRouter initialEntries={['/project/new']}>
        <App />
      </MemoryRouter>,
    )

    await waitFor(() => {
      expect(screen.getByTestId('new-project-submit')).toBeInTheDocument()
    })
    expect(screen.queryByTestId('project-detail-content')).not.toBeInTheDocument()
    expect(screen.queryByTestId('project-placeholder-code')).not.toBeInTheDocument()
  })
})