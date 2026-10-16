import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it } from 'vitest'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { http, HttpResponse } from 'msw'
import { server } from '../../test-setup'
import { getBuild4MockHandler } from '../../generated/api.msw'
import type { RProjectCreateVO } from '../../generated/model/rProjectCreateVO'
import { setToken } from '../../lib/auth/token-store'
import NewProjectPage from './index'

/**
 * `NewProjectPage` 是本仓**第一个写侧页面**的测试。
 *
 * 用例⑤（反向断言）是本组的核心价值：表单字段集合与 orval 生成的
 * `Build4Params` 双向比对 —— 后端加一个必填而前端漏了控件，或前端多传了
 * 后端不接收的参数（会污染请求），都会立刻变红。
 */

/** 记录实际提交给后端的参数，供用例② / ⑤ 断言。 */
let lastSubmitted: Record<string, string> = {}

/** 用 MemoryRouter 把 `/project/:code` 路由到一个可见标记，便于断言导航。 */
function renderPage() {
  return render(
    <MemoryRouter initialEntries={['/project/new']}>
      <Routes>
        <Route path="/project/new" element={<NewProjectPage />} />
        <Route
          path="/project/:code"
          element={<p data-testid="landed-detail">{`已到详情页`}</p>}
        />
      </Routes>
    </MemoryRouter>,
  )
}

/** 9 个字段的样例值（字段名与 Build4Params 一致）。 */
const SAMPLE: Record<string, string> = {
  name: '演示商城',
  englishName: 'demo_mall',
  author: '张三',
  phone: '13800000000',
  basePackage: 'com.demo.mall',
  copyright: 'Copyright (c) 2026',
  description: '用于契约守护的演示项目',
  databaseType: 'Mysql',
  language: 'Java',
}

function fillAll() {
  for (const [field, value] of Object.entries(SAMPLE)) {
    fireEvent.change(screen.getByTestId(`new-project-${field}-input`), {
      target: { value },
    })
  }
}

/** 默认桩：成功返回 8 字段 ProjectCreateVO（见 beforeEach 的 http handler）。 */

describe('NewProjectPage', () => {
  beforeEach(() => {
    window.localStorage.clear()
    setToken('test-token')
    lastSubmitted = {}
    // 捕获真实提交参数：拦在 orval 生成的 mock 之前不可行，故用独立 handler
    server.use(
      http.post('*/project/build*', async (info) => {
        const url = new URL(String(info.request.url))
        url.searchParams.forEach((value, key) => {
          lastSubmitted[key] = value
        })
        return HttpResponse.json({
          success: true,
          code: '0000',
          info: 'success',
          data: {
            code: '1882812765213425999',
            name: SAMPLE.name,
            englishName: SAMPLE.englishName,
            author: SAMPLE.author,
            basePackage: SAMPLE.basePackage,
            databaseType: SAMPLE.databaseType,
            state: 'Enable',
            createTime: '2026-10-18 09:30:00',
          },
        })
      }),
    )
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('渲染 9 个字段控件与提交按钮', () => {
    renderPage()

    expect(screen.getByTestId('new-project-form')).toBeInTheDocument()
    for (const field of Object.keys(SAMPLE)) {
      expect(screen.getByTestId(`new-project-${field}-input`)).toBeInTheDocument()
    }
    expect(screen.getByTestId('new-project-submit')).toBeInTheDocument()
  })

  it('databaseType / language 是下拉而非自由输入（挡非法值，D9）', () => {
    renderPage()

    expect(screen.getByTestId('new-project-databaseType-input').tagName).toBe('SELECT')
    expect(screen.getByTestId('new-project-language-input').tagName).toBe('SELECT')
    // englishName 是自由输入（有唯一性约束，不能枚举）
    expect(screen.getByTestId('new-project-englishName-input').tagName).toBe('INPUT')
  })

  it('englishName 旁 SHALL 提示「将作为数据库 schema 名」（D8）', () => {
    renderPage()
    // 用户必须知道为什么这个字段要求全局唯一
    expect(screen.getByText('将作为数据库 schema 名，需全局唯一')).toBeInTheDocument()
  })

  it('提交 SHALL 带上全部 9 个必填字段 + token', async () => {
    renderPage()
    fillAll()

    fireEvent.click(screen.getByTestId('new-project-submit'))

    await waitFor(() => {
      expect(Object.keys(lastSubmitted).length).toBeGreaterThan(0)
    })
    for (const [field, value] of Object.entries(SAMPLE)) {
      expect(lastSubmitted[field]).toBe(value)
    }
    // token 由页面自动取，SHALL NOT 出现在表单控件里
    expect(lastSubmitted.token).toBe('test-token')
  })

  it('提交成功后 SHALL 导航到 /project/{code}', async () => {
    renderPage()
    fillAll()

    fireEvent.click(screen.getByTestId('new-project-submit'))

    await waitFor(() => {
      expect(screen.getByTestId('landed-detail')).toBeInTheDocument()
    })
  })

  it('R.code !== "0000" SHALL 呈现错误气泡且保留已填字段', async () => {
    server.use(
      getBuild4MockHandler({
        success: false,
        code: '9003',
        info: '参数为空',
        data: undefined,
      } as unknown as RProjectCreateVO),
    )
    renderPage()
    fillAll()

    fireEvent.click(screen.getByTestId('new-project-submit'))

    await waitFor(() => {
      expect(screen.getByTestId('new-project-error')).toBeInTheDocument()
    })
    expect(screen.getByTestId('new-project-error').textContent).toContain('参数为空')
    // 用户填的内容保留，SHALL NOT 被清空
    expect(screen.getByTestId('new-project-name-input')).toHaveValue(SAMPLE.name)
    expect(screen.getByTestId('new-project-englishName-input')).toHaveValue(
      SAMPLE.englishName,
    )
  })

  it('englishName 重复（9005）SHALL 点明「已被占用」而非笼统报错（D8）', async () => {
    server.use(
      getBuild4MockHandler({
        success: false,
        code: '9005',
        info: 'There is already exists',
        data: undefined,
      } as unknown as RProjectCreateVO),
    )
    renderPage()
    fillAll()

    fireEvent.click(screen.getByTestId('new-project-submit'))

    await waitFor(() => {
      expect(screen.getByTestId('new-project-error')).toBeInTheDocument()
    })
    const msg = screen.getByTestId('new-project-error').textContent ?? ''
    expect(msg).toContain('项目英文名')
    expect(msg).toContain('已被占用')
  })

  // ---- 反向断言：表单字段集合 vs 契约生成物 ----
  //
  // 注：字段完备性主要由**类型系统**守护（见 index.tsx 的
  // `_typeLevelCoverage`：后端加必填 → orval 重生成 → `tsc` 直接报错）。
  // 运行时这两条只钉住「不多不少」与「不多传」，因为 `Build4Params` 是
  // type 而非 value，运行时拿不到它的键集合。

  it('【反向】SHALL NOT 提交契约之外的参数（不污染请求体）', async () => {
    renderPage()
    fillAll()

    fireEvent.click(screen.getByTestId('new-project-submit'))

    await waitFor(() => {
      expect(Object.keys(lastSubmitted).length).toBeGreaterThan(0)
    })
    // 实际提交 = 9 个业务字段 + token，与契约完全一致（不多不少）
    expect(Object.keys(lastSubmitted).sort()).toEqual(
      [...Object.keys(SAMPLE), 'token'].sort(),
    )
  })

  it('【反向】SHALL NOT 渲染 token 输入框（token 由页面自动取）', () => {
    renderPage()
    expect(screen.queryByTestId('new-project-token-input')).toBeNull()
  })
})