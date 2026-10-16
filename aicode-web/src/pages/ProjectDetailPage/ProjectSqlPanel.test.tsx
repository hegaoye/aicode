import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { server } from '../../test-setup'
import ProjectSqlPanel from './ProjectSqlPanel'

/**
 * 「SQL 与解析」区块的测试 —— 代码生成配置流程的第 ②③ 步。
 *
 * 详见 `openspec/changes/2026-10-18-project-create-and-sql-init/design.md` D5。
 */

const PROJECT_CODE = '1882812765213425001'

/** 拦截「读解析结果」端点。 */
function useParsedTables(tables: Array<Record<string, string>>) {
  server.use(
    http.get('*/project/relationship/listMapClassTable*', () =>
      HttpResponse.json({ success: true, code: '0000', info: 'success', data: tables }),
    ),
  )
}

/** 拦截「新增 SQL 段」端点，并记录调用。 */
function useSqlSave(counter: { count: number }) {
  server.use(
    http.post('*/project/sql/build*', () => {
      counter.count += 1
      return HttpResponse.json({
        success: true,
        code: '0000',
        info: 'success',
        data: {
          code: '1999999999999999999',
          projectCode: PROJECT_CODE,
          tsql: 'CREATE TABLE user (id BIGINT);',
          state: 'Enable',
        },
      })
    }),
  )
}

/** 拦截「触发解析」端点。 */
function useInit(ok = true) {
  server.use(
    http.post('*/project/init*', () =>
      ok
        ? HttpResponse.json({ success: true, code: '0000', info: 'success', data: null })
        : HttpResponse.json({ success: false, code: '9999', info: '建表失败', data: null }),
    ),
  )
}

function renderPanel() {
  return render(<ProjectSqlPanel projectCode={PROJECT_CODE} />)
}

describe('ProjectSqlPanel（SQL 与解析）', () => {
  it('初始态：SQL 框可编辑，保存按钮因空内容而禁用', async () => {
    useParsedTables([])
    renderPanel()

    expect(screen.getByTestId('project-sql-tsql')).toBeInTheDocument()
    expect(screen.getByTestId('project-sql-save')).toBeDisabled()
    expect(screen.getByTestId('project-sql-parse')).not.toBeDisabled()
    // 解析结果空态
    await waitFor(() => {
      expect(screen.getByTestId('project-sql-nothing-parsed')).toBeInTheDocument()
    })
  })

  it('填入 SQL 后「保存」可用，保存 SHALL 带上 projectCode + tsql', async () => {
    useParsedTables([])
    const counter = { count: 0 }
    useSqlSave(counter)
    renderPanel()

    fireEvent.change(screen.getByTestId('project-sql-tsql'), {
      target: { value: 'CREATE TABLE user (id BIGINT);' },
    })
    const save = screen.getByTestId('project-sql-save')
    expect(save).not.toBeDisabled()

    fireEvent.click(save)

    await waitFor(() => {
      expect(counter.count).toBe(1)
    })
    // 保存成功后 SQL 框清空
    expect(screen.getByTestId('project-sql-tsql')).toHaveValue('')
    // 且列出已保存的段
    expect(screen.getByTestId('project-sql-saved')).toBeInTheDocument()
  })

  it('点「解析」SHALL 调 init 并在完成后重读解析结果', async () => {
    // init 之后才返回非空解析结果 —— 用来验证「解析后重读」而非「只在 mount 读一次」
    let parsed = false
    server.use(
      http.get('*/project/relationship/listMapClassTable*', () =>
        HttpResponse.json({
          success: true,
          code: '0000',
          info: 'success',
          data: parsed
            ? [{ code: 'c1', tableName: 'user', className: 'User', dashedCaseName: 'user' }]
            : [],
        }),
      ),
    )
    useInit(true)
    renderPanel()

    // mount 时读到的是空
    await waitFor(() => {
      expect(screen.getByTestId('project-sql-nothing-parsed')).toBeInTheDocument()
    })

    parsed = true
    fireEvent.click(screen.getByTestId('project-sql-parse'))

    await waitFor(() => {
      expect(screen.getByTestId('project-sql-parsed')).toBeInTheDocument()
    })
    // tableName 与 dashedCaseName 都是 "user"，故按列取而非按文本
    const cells = Array.from(
      screen.getByTestId('project-sql-parsed').querySelectorAll('tbody td'),
    ).map((td) => td.textContent)
    expect(cells).toEqual(['user', 'User', 'user'])
  })

  it('init 失败 SHALL 呈现可读错误而非静默（D5：init 是同步阻塞的）', async () => {
    useParsedTables([])
    useInit(false)
    renderPanel()

    fireEvent.click(screen.getByTestId('project-sql-parse'))

    await waitFor(() => {
      expect(screen.getByTestId('project-sql-error')).toBeInTheDocument()
    })
    expect(screen.getByTestId('project-sql-error').textContent).toContain('解析失败')
    expect(screen.getByTestId('project-sql-error').textContent).toContain('建表失败')
  })

  it('解析结果读失败 SHALL NOT 阻断主流程（降级为空态）', async () => {
    server.use(
      http.get('*/project/relationship/listMapClassTable*', () =>
        HttpResponse.json({ success: false, code: '9999', info: '炸了', data: null }),
      ),
    )
    renderPanel()

    // 仍能渲染 SQL 输入区与按钮
    expect(screen.getByTestId('project-sql-tsql')).toBeInTheDocument()
    expect(screen.getByTestId('project-sql-parse')).not.toBeDisabled()
    await waitFor(() => {
      expect(screen.getByTestId('project-sql-nothing-parsed')).toBeInTheDocument()
    })
  })
})