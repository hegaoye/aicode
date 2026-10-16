import { getToken } from '../auth/token-store'

/**
 * `ws-client` —— 薄 WebSocket 包装。
 *
 * 详见 `openspec/specs/aicode-web-realtime/spec.md` 的 Requirement
 * "ws-client SHALL 是受鉴权 + 一次重连的轻 WebSocket 包装"。
 *
 * 行为：
 * - `connect(url, onMessage)` 返 `{ close(), send() }`；
 * - URL 自动注入 `?token=<token>`（与 `auth-account` spec R5 URL 参数兼容对齐）；
 * - 首连接 close（含 401 / 网络断）触发一次 500ms 后的重连，再失败转交 onMessage(null)；
 * - `close()` 同步调用 socket.close，禁止新消息；
 * - 多次 connect 调时，前一次连接 SHALL 被 close（避免泄漏）。
 */

export type WSMessageHandler = (msg: string | null) => void
export type WSCloseReason = 'closed' | 'reconnect-failed'

export interface WSClient {
  close: () => void
  send: (data: string) => void
}

interface ConnectOptions {
  /** 后端 ws 推送前缀（默认 `# `，与 `WSClientManager.sendMessage` 对齐） */
  prefix?: string
  /** 注入到 URL 的 token getter（默认 `getToken()`） */
  getToken?: () => string | null
  /** 单次重连间隔（默认 500ms） */
  retryDelayMs?: number
}

function stripPrefix(raw: string, prefix: string): string {
  return raw.startsWith(prefix) ? raw.slice(prefix.length) : raw
}

/**
 * 在 jsdom / 测试环境下 `window.WebSocket` 可能不存在；调用方负责在测试里提供
 * 全局 stub 或用 `vi.mock('./ws-client')` 隔离。
 */
function getWebSocketCtor(): typeof WebSocket {
  if (typeof window === 'undefined') {
    throw new Error('ws-client: window.WebSocket 在当前环境不可用')
  }
  return window.WebSocket
}

export function connect(
  url: string,
  onMessage: WSMessageHandler,
  options: ConnectOptions = {},
): WSClient {
  const prefix = options.prefix ?? '# '
  const tokenGetter = options.getToken ?? getToken
  const retryDelayMs = options.retryDelayMs ?? 500

  // 模块作用域：上次未关闭的 socket 引用；新 connect 前主动 close（防泄漏）
  if (lastActiveClient) {
    lastActiveClient.close()
    lastActiveClient = null
  }
  const ref: { current: WSClient | null } = { current: null }
  lastActiveClient = { close: () => ref.current?.close() }

  let attempts = 0
  let socket: WebSocket | null = null
  let closedExternally = false
  let reconnectTimer: ReturnType<typeof setTimeout> | null = null
  let messageHandler: ((ev: MessageEvent) => void) | null = null

  const fullUrl = (): string => {
    const token = tokenGetter()
    if (!token) {
      return url
    }
    // 已有 query 的：& 拼接；没有的：? 拼接
    const sep = url.includes('?') ? '&' : '?'
    return `${url}${sep}token=${encodeURIComponent(token)}`
  }

  const clearReconnect = () => {
    if (reconnectTimer !== null) {
      clearTimeout(reconnectTimer)
      reconnectTimer = null
    }
  }

  const open = () => {
    const WS = getWebSocketCtor()
    socket = new WS(fullUrl())

    messageHandler = (ev: MessageEvent) => {
      if (closedExternally) {
        return
      }
      const data = typeof ev.data === 'string' ? ev.data : String(ev.data ?? '')
      onMessage(stripPrefix(data, prefix))
    }
    socket.addEventListener('message', messageHandler)

    socket.addEventListener('close', () => {
      // 移除 message handler 防 close() 后 emit 仍能传出去
      if (messageHandler) {
        socket?.removeEventListener('message', messageHandler)
      }
      if (closedExternally) {
        return
      }
      attempts += 1
      if (attempts === 1) {
        // 一次重连
        reconnectTimer = setTimeout(() => {
          reconnectTimer = null
          open()
        }, retryDelayMs)
      } else {
        // 第二次以上失败：让 hook 知道流已终止（onMessage(null) 哨兵）
        onMessage(null)
      }
    })
  }

  open()

  const client: WSClient = {
    close() {
      closedExternally = true
      clearReconnect()
      if (socket && socket.readyState <= WebSocket.OPEN) {
        socket.close()
      }
      if (lastActiveClient && ref.current === null) {
        // ref.current 还没设上，wrapper 仍指向我们；清理
        lastActiveClient = null
      }
    },
    send(data: string) {
      if (socket && socket.readyState === WebSocket.OPEN) {
        socket.send(data)
      }
    },
  }
  ref.current = client
  return client
}

/**
 * 模块作用域 active 引用：每次 connect() 调用前先关掉之前未关闭的，
 * 保证"新连接开 + 老连接关"互不泄漏（详见 spec Scenario "同一组件多次连接互不泄漏"）。
 */
let lastActiveClient: { close: () => void } | null = null
