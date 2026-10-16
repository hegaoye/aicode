import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it } from 'vitest'
import { MemoryRouter } from 'react-router-dom'
import { server } from '../test-setup'
import { getSigninMockHandler } from '../generated/api.msw'
import type { RString } from '../generated/model/rString'
import LoginPage from './LoginPage'

/**
 * 用 `MemoryRouter` 包裹登录页（页面内部用 `useNavigate` 跳路由）；
 * 渲染时不带 `location.state`，因此 LoginPage 默认跳 `/project/list`。
 *
 * 本仓库 `devDependencies` 未引入 `@testing-library/user-event` —— 用
 * `fireEvent.change / fireEvent.click` 触发事件，已足矣覆盖触发函数与状态。
 */

function renderLogin() {
  return render(
    <MemoryRouter>
      <LoginPage />
    </MemoryRouter>,
  )
}

function fillForm(account: string, password: string) {
  fireEvent.change(screen.getByLabelText('账号'), { target: { value: account } })
  fireEvent.change(screen.getByLabelText('密码'), { target: { value: password } })
}

describe('LoginPage', () => {
  beforeEach(() => {
    window.localStorage.clear()
  })

  afterEach(() => {
    window.localStorage.clear()
  })

  it('默认 mock 返回有效 token 时写入 localStorage', async () => {
    server.use(
      getSigninMockHandler({
        success: true,
        code: '0000',
        info: 'success',
        data: 'jwt-from-test',
      } as RString),
    )

    renderLogin()
    fillForm('admin', '888888')
    fireEvent.click(screen.getByRole('button', { name: /登录/ }))

    await waitFor(() => {
      expect(window.localStorage.getItem('aicode.token')).toBe('jwt-from-test')
    })
  })

  it('返回空字符串时显示"账号或密码错误"且不写入 token', async () => {
    server.use(
      getSigninMockHandler({
        success: true,
        code: '0000',
        info: 'success',
        data: '',
      } as RString),
    )

    renderLogin()
    fillForm('admin', 'wrong')
    fireEvent.click(screen.getByRole('button', { name: /登录/ }))

    expect(await screen.findByTestId('login-error')).toHaveTextContent('账号或密码错误')
    expect(window.localStorage.getItem('aicode.token')).toBeNull()
  })

  it('R.code !== "0000" 时显示 ContractError.info', async () => {
    server.use(
      getSigninMockHandler({
        success: false,
        code: '9004',
        info: '参数非法',
        data: undefined,
      } as RString),
    )

    renderLogin()
    fillForm('admin', '888888')
    fireEvent.click(screen.getByRole('button', { name: /登录/ }))

    expect(await screen.findByTestId('login-error')).toHaveTextContent('参数非法')
    expect(window.localStorage.getItem('aicode.token')).toBeNull()
  })
})
