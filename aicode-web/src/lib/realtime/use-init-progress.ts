import { useCallback, useEffect, useRef, useState } from 'react'
import { unwrapSuccess } from '../../lib/contract-response'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import { connect, type WSClient } from './ws-client'
import {
  parseProgressEvent,
  type ProgressEvent,
  type ProgressPhase,
} from './progress-event'

/**
 * `useInitProgress` —— 消费 `POST /project/init` 的异步进度（2026-10-18-init-async-progress）。
 *
 * <p>`init` 已改为「立即接受 + WebSocket 推结构化进度」。本 hook 负责：
 * 连 ws → 调 init → 按消息形态分流 → 收到 `done` 通知调用方刷新解析结果。</p>
 *
 * ## 为什么不复用 `useBuildLog`
 *
 * <p>两者形态与生命周期都不同：`useBuildLog` 的状态机与「先调 execute 再订阅」的时序耦合。
 * `init` 需要的是结构化事件 + `done` 后触发重拉。硬合并会让构建日志被迫接受结构化事件。</p>
 *
 * ## 消息分流
 *
 * <p>判据在 {@link parseProgressEvent} 里（协议的唯一判定处，`useBuildLog` 也复用它）——
 * 首字符 `{` 即结构化，否则按自由文本处理。</p>
 */

export type { ProgressEvent, ProgressPhase }

export type InitProgressStatus =
  /** 尚未开始 */
  | 'idle'
  /** 正在建立 ws 连接（连接失败时也落到这里，由 error 承载原因）*/
  | 'connecting'
  /** 任务已被服务端接受，执行中 */
  | 'running'
  /** 成功结束 */
  | 'done'
  /** 失败结束 */
  | 'error'

export interface UseInitProgressReturn {
  status: InitProgressStatus
  phase: ProgressPhase | null
  current: number
  total: number
  message: string
  /** 是否显示不确定进度（total 未知，如建库阶段）*/
  indeterminate: boolean
  start: (projectCode: string) => Promise<void>
  clear: () => void
}

function initWsUrl(projectCode: string): string {
  const proto = window.location.protocol === 'https:' ? 'wss' : 'ws'
  const host = typeof window !== 'undefined' ? window.location.host : '127.0.0.1:8080'
  return `${proto}://${host}/websocket.shtml?buildId=${encodeURIComponent(projectCode)}`
}

export function useInitProgress(onDone?: () => void): UseInitProgressReturn {
  const [status, setStatus] = useState<InitProgressStatus>('idle')
  const [phase, setPhase] = useState<ProgressPhase | null>(null)
  const [current, setCurrent] = useState(0)
  const [total, setTotal] = useState(0)
  const [message, setMessage] = useState('')
  const wsRef = useRef<WSClient | null>(null)
  // 用 ref 持有回调，避免 onDone 变化导致重连
  const onDoneRef = useRef(onDone)
  onDoneRef.current = onDone

  const reset = useCallback(() => {
    wsRef.current?.close()
    wsRef.current = null
    setStatus('idle')
    setPhase(null)
    setCurrent(0)
    setTotal(0)
    setMessage('')
  }, [])

  const clear = useCallback(() => {
    reset()
  }, [reset])

  // 卸载时断开，避免连接泄漏
  useEffect(() => {
    return () => {
      wsRef.current?.close()
      wsRef.current = null
    }
  }, [])

  const start = useCallback(
    async (projectCode: string) => {
      if (!projectCode) {
        return
      }
      wsRef.current?.close()
      setStatus('connecting')
      setPhase(null)
      setCurrent(0)
      setTotal(0)
      setMessage('正在连接…')

      // 先连 ws 再调 init —— 若顺序反了，init 期间的首批进度消息会丢
      // （无 ws 连接时 sendMessage 静默丢弃，界面上会「请求成功但不动」）
      const client = connect(initWsUrl(projectCode), (raw) => {
        if (raw === null) {
          // 自由文本（构建日志等）—— 本 hook 不关心，静默忽略
          return
        }
        const event = parseProgressEvent(raw)
        if (!event) {
          return
        }
        if (event.type === 'progress') {
          setStatus('running')
          if (event.phase) {
            setPhase(event.phase)
          }
          setCurrent(event.current ?? 0)
          setTotal(event.total ?? 0)
          setMessage(event.message ?? '')
          return
        }
        if (event.type === 'done') {
          setStatus('done')
          setPhase(null)
          setCurrent(0)
          setTotal(0)
          setMessage(event.message ?? '解析完成')
          onDoneRef.current?.()
          return
        }
        // error —— 进度区 SHALL 保留（不自动消失），让用户看清失败位置
        setStatus('error')
        setMessage(event.message ?? '解析失败')
      })
      wsRef.current = client

      try {
        const api = getAicodeRESTfulAPIs()
        const resp = await api.init({ code: projectCode })
        // 必须在 unwrapSuccess —— axios 对 HTTP 200 不 reject，
        // 后端 R.failed（如 code 为空的 9003）同样 resolve，不解包会当成功
        unwrapSuccess(resp)
        // 服务端已接受；状态切到 running（真正的 running 由 ws 的 progress 事件确认）
        setStatus((prev) => (prev === 'connecting' ? 'running' : prev))
      } catch (err) {
        wsRef.current?.close()
        wsRef.current = null
        setStatus('error')
        setMessage(err instanceof Error ? err.message : String(err))
      }
    },
    [],
  )

  return {
    status,
    phase,
    current,
    total,
    message,
    // total 未知时（如建库阶段内部无可切分点）显示不确定进度，而非 0/0 的空进度条
    indeterminate: status === 'running' && total <= 0,
    start,
    clear,
  }
}