import { render, screen, waitFor } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { server } from '../../test-setup'
import { getLoadByCode1MockHandler } from '../../generated/api.msw'
import type { RAccountPublicVO } from '../../generated/model/rAccountPublicVO'
import { setToken } from '../../lib/auth/token-store'
import AccountPage from './index'

/**
 * `<AccountPage>` 3 用例：
 * ① loading 状态
 * ② ready 状态：3 字段（id / code / account）渲染，且不含 password testid
 * ③ error 状态：`<p data-testid="account-error">` 渲染
 *
 * 用 `vi.mock` 替身 useCurrentAccount —— 但也可直接走 MSW 端点路径。
 * 这里走 MSW 更真实：M2-2 useAuth 已在 jsdom + MemoryRouter 中工作。
 */

function renderPage() {
  return render(
    <MemoryRouter initialEntries={['/account/me']}>
      <Routes>
        <Route path="/account/me" element={<AccountPage />} />
      </Routes>
    </MemoryRouter>,
  )
}

describe('AccountPage', () => {
  it('loading 状态渲染"加载中…"', async () => {
    setToken('jwt-test')
    // 不覆盖 getLoadByCode1MockHandler：默认 faker 响应可能失败，
    // 但 component 在 useEffect 跑完前先渲染 loading 状态。
    renderPage()
    expect(screen.getByText('加载中…')).toBeInTheDocument()
  })

  it('ready 状态渲染 3 字段 + 不含 password testid', async () => {
    setToken('jwt-test')
    server.use(
      getLoadByCode1MockHandler({
        success: true,
        code: '0000',
        info: 'success',
        data: { id: 42, code: '1882812765213425002', account: 'admin' },
      } as unknown as RAccountPublicVO),
    )

    renderPage()
    await waitFor(() => {
      expect(screen.getByTestId('account-page-content')).toBeInTheDocument()
    })
    expect(screen.getByTestId('account-field-id').textContent).toContain('42')
    expect(screen.getByTestId('account-field-code').textContent).toContain(
      '1882812765213425002',
    )
    expect(screen.getByTestId('account-field-account').textContent).toContain('admin')
    // M3-D 反向：password 字段 SHALL NOT 出现
    expect(screen.queryByTestId('account-field-password')).toBeNull()
  })

  it('error 状态渲染 `<p data-testid="account-error">`', async () => {
    setToken('jwt-test')
    server.use(
      getLoadByCode1MockHandler({
        success: false,
        code: '9999',
        info: '服务器内部错误',
      } as unknown as RAccountPublicVO),
    )

    renderPage()
    await waitFor(() => {
      expect(screen.getByTestId('account-error')).toBeInTheDocument()
    })
    expect(screen.getByTestId('account-error').textContent).toContain('服务器内部错误')
    // password testid 在 error 路径下也不应出现
    expect(screen.queryByTestId('account-field-password')).toBeNull()
  })
})
