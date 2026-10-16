import { useCallback, useEffect, useRef, useState } from 'react'
import { ContractError, unwrapSuccess } from '../contract-response'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import { connect, type WSClient } from './ws-client'

/**
 * `useBuildLog` —— 协同 `api.execute` + ws 实时日志的 React hook。
 *
 * 详见 `openspec/specs/aicode-web-realtime/spec.md` 的 Requirement
 * "useBuildLog SHALL 协同 api.execute 与 ws 接收，返回 4 状态"。
 *
 * 状态机：
 *   - `idle`：未启动；初始态
 *   - `running`：execute 成功 + ws 连接建立；ws 推送逐条追加 entries
 *   - `error`：execute 抛错 / ws 二次失败；entries 含失败描述条目
 *   - `running → idle`：用户 `clear()` 或组件 unmount（cleanup）
 *
 * 重要取舍：状态机 **不** 含 `completed`。后端 `GeneratorSVImpl.aiCode` 推送
 * free-form 日志（实测 line 117/129/138/155 等），无 `[DONE]` sentinel——详情
 * "ProjectJob.state = Completed" 由前端后续拉取承担，非实时主题，详见 design D1。
 */

export type BuildLogStatus = 'idle' | 'running' | 'error'

export interface BuildLogEntry {
  at: number
  text: string
}

export interface UseBuildLogReturn {
  status: BuildLogStatus
  entries: BuildLogEntry[]
  startedAt: number | null
  startStream: () => Promise<void>
  clear: () => void
}

function wsUrl(code: string): string {
  const proto = typeof window !== 'undefined' && window.location.protocol === 'https:' ? 'wss' : 'ws'
  // 用相对路径让 vite proxy 在 dev 接管；production 由 nginx 转发到 backend
  return `${proto}://${typeof window !== 'undefined' ? window.location.host : '127.0.0.1:8080'}/websocket.shtml?buildId=${encodeURIComponent(code)}`
}

export function useBuildLog(projectCode: string | null | undefined): UseBuildLogReturn {
  const [status, setStatus] = useState<BuildLogStatus>('idle')
  const [entries, setEntries] = useState<BuildLogEntry[]>([])
  const [startedAt, setStartedAt] = useState<number | null>(null)
  const wsRef = useRef<WSClient | null>(null)
  const startTimeRef = useRef<number | null>(null)

  const appendEntry = useCallback((text: string) => {
    setEntries((prev) => [...prev, { at: Date.now(), text }])
  }, [])

  const reset = useCallback(() => {
    wsRef.current?.close()
    wsRef.current = null
    setStatus('idle')
    setStartedAt(null)
    startTimeRef.current = null
  }, [])

  const clear = useCallback(() => {
    reset()
    setEntries([])
  }, [reset])

  const startStream = useCallback(async () => {
    if (!projectCode) {
      appendEntry('构建失败: projectCode 为空')
      setStatus('error')
      return
    }
    if (status === 'running') {
      // 重复启动：忽略，避免泄漏既有 ws
      return
    }
    // 先清空 entries——重新开始
    setEntries([])
    setStatus('running')
    const startedAtValue = Date.now()
    setStartedAt(startedAtValue)
    startTimeRef.current = startedAtValue
    appendEntry('开始构建...')

    try {
      const api = getAicodeRESTfulAPIs()
      // 注：axios 拦截器（M2-2 aicode-web-auth）已自动注入 `Authorization: Bearer <token>`；
      // 这里只调 execute({ code })，由 tokenStore→拦截器链路完成鉴权。
      const resp = await api.execute({ code: projectCode })
      // unwrapSuccess 校验 R.success 包装；抛错则进入 catch
      unwrapSuccess(resp)
    } catch (err) {
      const msg = err instanceof ContractError ? err.info || err.code : String(err)
      appendEntry(`构建失败: ${msg}`)
      setStatus('error')
      return
    }

    // execute 成功不代表构建完成；接 ws 让实时日志流进来
    const ws = connect(wsUrl(projectCode), (msg) => {
      if (msg === null) {
        // ws 连接二次失败 → 标记 error
        appendEntry('构建日志连接已断开（已尝试一次重连）')
        setStatus('error')
        return
      }
      appendEntry(msg)
    })
    wsRef.current = ws
  }, [projectCode, status, appendEntry])

  // cleanup：组件 unmount 时关 ws
  useEffect(() => {
    return () => {
      wsRef.current?.close()
      wsRef.current = null
      startTimeRef.current = null
    }
  }, [])

  return { status, entries, startedAt, startStream, clear }
}
