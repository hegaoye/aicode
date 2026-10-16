import { render } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { MemoryRouter } from 'react-router-dom'
import AppShell from './AppShell'

/**
 * AppShell 2 用例：
 * ① isAuthenticated === true 时挂载 <AccountMenu /> → data-testid="account-menu" 在 DOM
 * ② isAuthenticated === false 时不挂载 AccountMenu
 *
 * `useAuth` 是被 mock 的对象；`useCurrentAccount` 内部仍会调 useAuth，
 * 我们只关心 isAuthenticated 派生（`token !== null`）。
 */

let mockAuthState: { token: string | null } = { token: null }

vi.mock('../lib/auth/use-auth', () => ({
  useAuth: () => ({
    token: mockAuthState.token,
    signIn: () => undefined,
    signOut: () => undefined,
    isAuthenticated: mockAuthState.token !== null,
  }),
}))

// useCurrentAccount 内部仍调 api.loadByCode1() —— 在 AppShell 层 AccountMenu 触发时
// 会发起 HTTP 请求。我们用 vi.mock 替身 hook 让 component 不发起请求。
vi.mock('../lib/account/use-current-account', () => ({
  useCurrentAccount: () => ({
    account: 'admin',
    code: '1882812765213425002',
    id: 42,
    isLoading: false,
    error: null,
    refresh: () => Promise.resolve(),
  }),
}))

beforeEach(() => {
  mockAuthState = { token: null }
  window.localStorage.clear()
})

afterEach(() => {
  vi.clearAllMocks()
})

describe('AppShell', () => {
  it('isAuthenticated === true 时挂载 <AccountMenu />', () => {
    mockAuthState = { token: 'jwt-test' }
    const { container } = render(
      <MemoryRouter>
        <AppShell>
          <div data-testid="child">content</div>
        </AppShell>
      </MemoryRouter>,
    )
    expect(container.querySelector('[data-testid="account-menu"]')).not.toBeNull()
    expect(container.querySelector('[data-testid="child"]')).not.toBeNull()
  })

  it('isAuthenticated === false 时不挂载 AccountMenu', () => {
    mockAuthState = { token: null }
    const { container } = render(
      <MemoryRouter>
        <AppShell>
          <div data-testid="child">content</div>
        </AppShell>
      </MemoryRouter>,
    )
    expect(container.querySelector('[data-testid="account-menu"]')).toBeNull()
    expect(container.querySelector('[data-testid="child"]')).not.toBeNull()
  })
})
