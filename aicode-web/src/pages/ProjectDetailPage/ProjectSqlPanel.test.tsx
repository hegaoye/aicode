import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { server } from '../../test-setup'
import ProjectSqlPanel from './ProjectSqlPanel'
import type { WSClient } from '../../lib/realtime/ws-client'

/**
 * 「SQL 与解析」区块的测试 —— 代码生成配置流程的第 ②③ 步。
 *
 * ## 2026-10-18 异步化带来的改造（init-async-progress）
 *
 * <p>`init` 从「同步执行完毕返回结果」改为「立即接受 + ws 推结构化进度」，故：</p>
 * <ul>
 *   <li>解析完成不再是"HTTP 返回后"触发，而是**等 ws 的 `done` 事件**；</li>
 *   <li>解析失败不再是 HTTP 错误，而是**ws 的 `error` 事件**（且含已完成工作量）。</li>
 * </ul>
 *
 * <p>原有 2 条用例（"解析后重读"、"init 失败呈现错误"）因此改为**驱动 ws 消息**，
 * 语义保留、触发方式更新。</p>
 *
 * <p>ws-client 在此被 mock —— jsdom 不实现 WebSocket（AGENTS §7 已记），且这样能
 * 精确控制消息到达时机。真正的分流逻辑在 `use-init-progress.test.ts` 里单独测。</p>
 */

const PROJECT_CODE = '1882812765213425001'

/** 被 mock 的 ws-client 记录的消息投递器。 */
let emitMessage: ((msg: string | null) => void) | null = null
/** 记录 connect 次数与 URL —— 验证"先连 ws 再调 init"。 */
let connectCalls: string[] = []
let initCalls: number = 0

vi.mock('../../lib/realtime/ws-client', () => ({
  connect: (url: string, onMessage: (msg: string | null) => void): WSClient => {
    connectCalls.push(url)
    emitMessage = onMessage
    return { close: vi.fn(), send: vi.fn() }
  },
}))

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
    http.post('*/project/init*', () => {
      initCalls += 1
      return ok
        ? HttpResponse.json({ success: true, code: '0000', info: 'success', data: null })
        : HttpResponse.json({ success: false, code: '9003', info: '参数为空', data: null })
    }),
  )
}

/** 投递一条 ws 消息。 */
function push(msg: string | null) {
  emitMessage?.(msg)
}

/** 进度事件工厂。 */
function progressEvent(
  current: number,
  total: number,
  message = `正在解析第 ${current + 1}/${total} 张表：t_${current}`,
) {
  return JSON.stringify({ type: 'progress', phase: 'parse', current, total, message })
}

describe('ProjectSqlPanel（SQL 与解析，init 已异步化）', () => {
  beforeEach(() => {
    emitMessage = null
    connectCalls = []
    initCalls = 0
  })

  it('初始态：SQL 框可编辑，保存按钮因空内容而禁用', async () => {
    useParsedTables([])
    render(<ProjectSqlPanel projectCode={PROJECT_CODE} />)

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
    render(<ProjectSqlPanel projectCode={PROJECT_CODE} />)

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

  it('点「解析」SHALL 先连 ws 再调 init（顺序反了会丢首批进度消息）', async () => {
    useParsedTables([])
    useInit(true)
    render(<ProjectSqlPanel projectCode={PROJECT_CODE} />)

    fireEvent.click(screen.getByTestId('project-sql-parse'))

    await waitFor(() => {
      expect(initCalls).toBe(1)
    })
    // 连接已建立且 URL 带项目编码
    expect(connectCalls.length).toBe(1)
    expect(connectCalls[0]).toContain(encodeURIComponent(PROJECT_CODE))
  })

  it('ws 推进度 SHALL 显示进度条与计数', async () => {
    useParsedTables([])
    useInit(true)
    render(<ProjectSqlPanel projectCode={PROJECT_CODE} />)

    fireEvent.click(screen.getByTestId('project-sql-parse'))
    await waitFor(() => expect(initCalls).toBe(1))

    push(
      JSON.stringify({
        type: 'progress',
        phase: 'parse',
        current: 0,
        total: 12,
        message: '共 12 张表待解析',
      }),
    )

    await waitFor(() => {
      expect(screen.getByTestId('project-parse-bar')).toBeInTheDocument()
    })
    expect(screen.getByTestId('project-parse-count').textContent).toContain('0/12')
    expect(screen.getByTestId('project-parse-count').textContent).toContain('0%')

    push(progressEvent(3, 12))

    await waitFor(() => {
      expect(screen.getByTestId('project-parse-count').textContent).toContain('3/12')
    })
    expect(screen.getByTestId('project-parse-count').textContent).toContain('25%')
  })

  it('建库阶段（total=0）SHALL 显示不确定进度文案而非空进度条（D5）', async () => {
    useParsedTables([])
    useInit(true)
    render(<ProjectSqlPanel projectCode={PROJECT_CODE} />)

    fireEvent.click(screen.getByTestId('project-sql-parse'))
    await waitFor(() => expect(initCalls).toBe(1))

    push(
      JSON.stringify({
        type: 'progress',
        phase: 'createDatabase',
        current: 0,
        total: 0,
        message: '正在创建数据库并执行建表脚本…',
      }),
    )

    await waitFor(() => {
      expect(screen.getByTestId('project-parse-indeterminate')).toBeInTheDocument()
    })
    // 不显示 0/0 的空进度条，也不显示计数
    expect(screen.queryByTestId('project-parse-bar')).toBeNull()
    expect(screen.queryByTestId('project-parse-count')).toBeNull()
    expect(screen.getByTestId('project-parse-indeterminate').textContent).toContain('执行建表脚本')
  })

  it('收到 done 后 SHALL 重读解析结果并显示解析完成', async () => {
    // init 之后才返回非空解析结果 —— 用来验证「done 后重读」而非「只在 mount 读一次」
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
    render(<ProjectSqlPanel projectCode={PROJECT_CODE} />)

    // mount 时读到的是空
    await waitFor(() => {
      expect(screen.getByTestId('project-sql-nothing-parsed')).toBeInTheDocument()
    })

    fireEvent.click(screen.getByTestId('project-sql-parse'))
    await waitFor(() => expect(initCalls).toBe(1))

    parsed = true
    push(JSON.stringify({ type: 'done', message: '解析完成' }))

    await waitFor(() => {
      expect(screen.getByTestId('project-sql-parsed')).toBeInTheDocument()
    })
    // tableName 与 dashedCaseName 都是 "user"，故按列取而非按文本
    const cells = Array.from(
      screen.getByTestId('project-sql-parsed').querySelectorAll('tbody td'),
    ).map((td) => td.textContent)
    expect(cells).toEqual(['user', 'User', 'user'])
    expect(screen.getByTestId('project-parse-done')).toBeInTheDocument()
  })

  it('ws 推 error SHALL 呈现可读错误并保留进度区（让用户看清失败位置）', async () => {
    useParsedTables([])
    useInit(true)
    render(<ProjectSqlPanel projectCode={PROJECT_CODE} />)

    fireEvent.click(screen.getByTestId('project-sql-parse'))
    await waitFor(() => expect(initCalls).toBe(1))

    push(
      JSON.stringify({
        type: 'error',
        message: '已解析 4/12 张表后失败：建表失败（已写入的表不会回滚，可重新解析）',
      }),
    )

    await waitFor(() => {
      expect(screen.getByTestId('project-parse-error')).toBeInTheDocument()
    })
    // 错误消息含已完成工作量 —— 后端如实上报，用户不会误以为要重跑全部
    expect(screen.getByTestId('project-parse-error').textContent).toContain('4/12')
    // 进度区保留（不自动消失），并提供关闭按钮
    expect(screen.getByTestId('project-parse-progress')).toBeInTheDocument()
    expect(screen.getByTestId('project-parse-dismiss')).toBeInTheDocument()
  })

  it('init 的 HTTP 调用本身失败 SHALL 进 error 态（R.failed 9003）', async () => {
    useParsedTables([])
    useInit(false)
    render(<ProjectSqlPanel projectCode={PROJECT_CODE} />)

    fireEvent.click(screen.getByTestId('project-sql-parse'))

    await waitFor(() => {
      expect(screen.getByTestId('project-parse-progress')).toBeInTheDocument()
    })
    await waitFor(() => {
      expect(screen.getByTestId('project-parse-error').textContent).toContain('参数为空')
    })
  })

  it('自由文本日志消息 SHALL 被静默忽略：不抛错、不进进度、不显示', async () => {
    // 通道上同时有构建日志的 free-form 文本 —— 那是别的任务的，不该污染本面板
    useParsedTables([])
    useInit(true)
    render(<ProjectSqlPanel projectCode={PROJECT_CODE} />)

    fireEvent.click(screen.getByTestId('project-sql-parse'))
    await waitFor(() => expect(initCalls).toBe(1))
    push(progressEvent(2, 12))

    // 不抛错
    expect(() => push('git 努力下载中.....')).not.toThrow()
    // 以 { 开头但非法 JSON 也按文本处理，不抛错、不中断订阅
    expect(() => push('{ 这不是合法 JSON')).not.toThrow()
    // 形似 JSON 但无 type 字段 → 同样按文本处理
    expect(() => push('{"foo":1}')).not.toThrow()

    // 进度仍停在真正的进度事件上，未被文本污染
    await waitFor(() => {
      expect(screen.getByTestId('project-parse-count').textContent).toContain('2/12')
    })
    expect(screen.queryByTestId('project-parse-error')).toBeNull()
    // 文本内容不出现在 DOM 里
    expect(screen.queryByText(/git 努力下载中/)).toBeNull()
  })

  it('解析结果读失败 SHALL NOT 阻断主流程（降级为空态）', async () => {
    server.use(
      http.get('*/project/relationship/listMapClassTable*', () =>
        HttpResponse.json({ success: false, code: '9999', info: '炸了', data: null }),
      ),
    )
    render(<ProjectSqlPanel projectCode={PROJECT_CODE} />)

    expect(screen.getByTestId('project-sql-tsql')).toBeInTheDocument()
    expect(screen.getByTestId('project-sql-parse')).not.toBeDisabled()
    await waitFor(() => {
      expect(screen.getByTestId('project-sql-nothing-parsed')).toBeInTheDocument()
    })
  })
})