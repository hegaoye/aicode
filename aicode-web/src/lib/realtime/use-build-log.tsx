import { useCallback, useEffect, useRef, useState } from 'react'
import { ContractError, unwrapSuccess } from '../contract-response'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import { connect, type WSClient } from './ws-client'
import { parseProgressEvent } from './progress-event'

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

/**
 * 构建状态机 —— **4 态**（2026-10-18-build-log-terminal-signal 起）。
 *
 * <p>此前只有 `idle` / `running` / `error` 三态，<b>没有「成功结束」态</b> ——
 * 因为服务端早已推送 `Finished: SUCCESS`，但本 hook 对每条消息一律 `appendEntry`，
 * 从不识别（grep `Finished` 零命中）。故成功与失败对用户是同一个信号。</p>
 */
export type BuildLogStatus = 'idle' | 'running' | 'done' | 'error'

export interface BuildLogEntry {
  at: number
  text: string
}

/**
 * 当前构建阶段（由服务端的 `type=progress` 事件给出）。
 *
 * <p>2026-10-18-build-stage-progress：代码生成有 7 个步骤，此前前端**看不到任何阶段信息**
 * —— 用户只能盯着一串滚动日志。本字段让面板能显示「第 N/7 步：<步骤名>」。</p>
 */
export interface BuildStage {
  phase: string | null
  current: number
  total: number
  message: string | null
}

export interface UseBuildLogReturn {
  status: BuildLogStatus
  entries: BuildLogEntry[]
  startedAt: number | null
  /** 终态消息的文案（done / error 时可读）；供 UI 呈现失败原因 */
  terminalMessage: string | null
  /** 当前阶段；未收到阶段事件时为 null */
  stage: BuildStage | null
  startStream: () => Promise<void>
  clear: () => void
}

/**
 * 旧版服务端的终态字符串 —— **混合部署兜底**（design D4）。
 *
 * <p>前端静态资源经 `:aicode-web` 打包进 Jar，与后端**不在同一个部署单元**，
 * 存在新旧混跑窗口。字符串匹配虽是脆弱手段（协议主路径是结构化 JSON），
 * 但两套都认可让混跑期行为与各自现状一致，部署完成后自动收敛到 JSON 优先。</p>
 */
const LEGACY_TERMINALS: ReadonlyArray<[RegExp, BuildLogStatus, string]> = [
  [/^Finished:\s*SUCCESS$/i, 'done', '构建成功'],
  [/^Finished:\s*ERROR$/i, 'error', '构建失败'],
]

function wsUrl(code: string): string {
  const proto = typeof window !== 'undefined' && window.location.protocol === 'https:' ? 'wss' : 'ws'
  // 用相对路径让 vite proxy 在 dev 接管；production 由 nginx 转发到 backend
  return `${proto}://${typeof window !== 'undefined' ? window.location.host : '127.0.0.1:8080'}/websocket.shtml?buildId=${encodeURIComponent(code)}`
}

export function useBuildLog(projectCode: string | null | undefined): UseBuildLogReturn {
  const [status, setStatus] = useState<BuildLogStatus>('idle')
  const [entries, setEntries] = useState<BuildLogEntry[]>([])
  const [startedAt, setStartedAt] = useState<number | null>(null)
  const [terminalMessage, setTerminalMessage] = useState<string | null>(null)
  const [stage, setStage] = useState<BuildStage | null>(null)
  const wsRef = useRef<WSClient | null>(null)
  const startTimeRef = useRef<number | null>(null)
  /**
   * 当前状态镜像 —— 供 ws 回调读取。
   *
   * <p>回调闭包里的 `status` 可能是过期的（connect 时捕获的那一份），
   * 故终态判断必须读 ref 而非 state。</p>
   */
  const statusRef = useRef<BuildLogStatus>('idle')

  const appendEntry = useCallback((text: string) => {
    setEntries((prev) => [...prev, { at: Date.now(), text }])
  }, [])

  const reset = useCallback(() => {
    wsRef.current?.close()
    wsRef.current = null
    setStatus('idle')
    statusRef.current = 'idle'
    setStartedAt(null)
    setTerminalMessage(null)
    setStage(null)
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
    statusRef.current = 'running'
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

      // ---- 2026-10-18-build-log-terminal-signal：先分流，再决定是否当日志行 ----
      // 顺序很重要：终态事件绝不能被 appendEntry 当成普通日志行。

      // ⓿ 终态已定则忽略后续一切消息。构建已结束，之后到达的无论是日志行、
      //    'End' 等收尾噪声、还是**矛盾的终态**（先 done 后 error），一律不处理 ——
      //    首个终态是权威的。若允许覆盖，用户会看到"已完成后又变成失败"的诡异现象。
      if (statusRef.current === 'done' || statusRef.current === 'error') {
        return
      }

      // ① 结构化终态事件（{"type":"done"|"error", ...}）
      const event = parseProgressEvent(msg)
      if (event && event.type === 'progress') {
        // 阶段事件：**只更新阶段展示，不进日志列表**（阶段已在面板顶部结构化展示，
        // 再塞进日志列表会造成重复内容），也不改变终态（终态仅由 done / error 决定）
        setStage({
          phase: event.phase ?? null,
          current: event.current ?? 0,
          total: event.total ?? 0,
          message: event.message ?? null,
        })
        return
      }
      if (event && event.type !== 'progress') {
        setStatus(event.type)
        statusRef.current = event.type
        setTerminalMessage(event.message ?? null)
        return   // 不 appendEntry
      }
      // 构建场景不会有 progress 事件（中间日志是自由文本）；若真收到，
      // 当作收尾噪声忽略，不进日志列表。

      // ② 旧版字符串终态（混合部署兜底，见 LEGACY_TERMINALS 的说明）
      const legacy = LEGACY_TERMINALS.find(([re]) => re.test(msg.trim()))
      if (legacy) {
        setStatus(legacy[1])
        statusRef.current = legacy[1]
        setTerminalMessage(legacy[2])
        return   // 不 appendEntry
      }

      // ③ 其余都是自由文本日志行
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

  return { status, entries, startedAt, terminalMessage, stage, startStream, clear }
}
