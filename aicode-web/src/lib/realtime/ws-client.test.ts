import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { connect } from './ws-client'

/**
 * `ws-client` 三个测试：
 * ① token 注入 URL；② 首连 close → 500ms 后重连一次，再失败 onMessage(null)；
 * ③ close() 后再收消息被忽略 + connect 反复调不泄漏。
 *
 * jsdom 不实现 WebSocket——stub 一个最小可行的兼容事件收发类。
 */

type Listener = (ev: Event | MessageEvent) => void

class FakeWebSocket {
  static instances: FakeWebSocket[] = []
  static CONNECTING = 0
  static OPEN = 1
  static CLOSING = 2
  static CLOSED = 3
  url: string
  readyState = FakeWebSocket.OPEN
  listeners: Record<string, Listener[]> = {}
  sentFrames: string[] = []
  constructor(url: string) {
    this.url = url
    FakeWebSocket.instances.push(this)
  }
  addEventListener(name: string, fn: Listener) {
    ;(this.listeners[name] ??= []).push(fn)
  }
  removeEventListener(name: string, fn: Listener) {
    const arr = this.listeners[name] ?? []
    const idx = arr.indexOf(fn)
    if (idx >= 0) {
      arr.splice(idx, 1)
    }
  }
  send(data: string) {
    this.sentFrames.push(data)
  }
  close() {
    this.readyState = FakeWebSocket.CLOSED
    const evt = new Event('close')
    this.listeners.close?.forEach((fn) => fn(evt))
  }
  // 测试辅助
  emitMessage(text: string) {
    const evt = new MessageEvent('message', { data: text })
    this.listeners.message?.forEach((fn) => fn(evt))
  }
}

beforeEach(() => {
  FakeWebSocket.instances = []
  vi.stubGlobal('WebSocket', FakeWebSocket)
})

afterEach(() => {
  vi.useRealTimers()
  vi.unstubAllGlobals()
})

describe('ws-client', () => {
  it('持有 token 时把 token 注入 URL（?token=...）', () => {
    window.localStorage.setItem('aicode.token', 'jwt-ws-test')
    const onMessage = vi.fn()
    connect('ws://localhost:8080/websocket.shtml', onMessage)
    expect(FakeWebSocket.instances.length).toBe(1)
    expect(FakeWebSocket.instances[0]!.url).toBe(
      'ws://localhost:8080/websocket.shtml?token=jwt-ws-test',
    )
  })

  it('无可用 token 时 URL 保持原状', () => {
    window.localStorage.clear()
    const onMessage = vi.fn()
    connect('ws://localhost:8080/websocket.shtml', onMessage)
    expect(FakeWebSocket.instances[0]!.url).toBe('ws://localhost:8080/websocket.shtml')
  })

  it('剥离 "# " 前缀后传给 onMessage；保留其余文本', () => {
    window.localStorage.clear()
    const onMessage = vi.fn()
    connect('ws://localhost:8080/websocket.shtml', onMessage)
    const sock = FakeWebSocket.instances[0]!
    sock.emitMessage('# git 努力下载中.....')
    sock.emitMessage('plain message without prefix')
    expect(onMessage).toHaveBeenCalledTimes(2)
    expect(onMessage).toHaveBeenNthCalledWith(1, 'git 努力下载中.....')
    expect(onMessage).toHaveBeenNthCalledWith(2, 'plain message without prefix')
  })

  it('首连 close 触发一次重连（500ms 间隔），再失败 onMessage(null)', () => {
    vi.useFakeTimers()
    window.localStorage.clear()
    const onMessage = vi.fn()
    connect('ws://localhost:8080/websocket.shtml', onMessage)
    expect(FakeWebSocket.instances.length).toBe(1)

    // 第一次 close：触发 500ms 后重连
    const first = FakeWebSocket.instances[0]!
    first.close()
    expect(FakeWebSocket.instances.length).toBe(1)
    // 推进 500ms，重连发生
    vi.advanceTimersByTime(500)
    expect(FakeWebSocket.instances.length).toBe(2)
    expect(onMessage).not.toHaveBeenCalledWith(null)

    // 第二次 close：不再重连；onMessage(null) 被调
    const second = FakeWebSocket.instances[1]!
    second.close()
    expect(FakeWebSocket.instances.length).toBe(2)
    expect(onMessage).toHaveBeenCalledWith(null)
  })

  it('close() 后再 emit 消息被忽略；后续 close 也不重连', () => {
    window.localStorage.clear()
    const onMessage = vi.fn()
    const client = connect('ws://localhost:8080/websocket.shtml', onMessage)
    const sock = FakeWebSocket.instances[0]!

    client.close()
    sock.emitMessage('# late message')
    sock.close()

    // emit 不传消息（onMessage 仍 0 调）；再次 close 不重连
    expect(onMessage).not.toHaveBeenCalled()
    expect(FakeWebSocket.instances.length).toBe(1)
  })

  it('多次 connect 调时，前一次连接 SHALL 被 close（避免泄漏）', () => {
    window.localStorage.clear()
    const onMessage = vi.fn()
    const c1 = connect('ws://localhost:8080/websocket.shtml', onMessage)
    const first = FakeWebSocket.instances[0]!
    const c2 = connect('ws://localhost:8080/websocket.shtml', onMessage)
    const second = FakeWebSocket.instances[1]!
    expect(first.readyState).toBe(FakeWebSocket.CLOSED)
    expect(second.readyState).toBe(FakeWebSocket.OPEN)

    c1.close() // 已 close，不重连
    c2.close()
  })

  it('send 在 OPEN 时透传；CLOSED 时忽略', () => {
    window.localStorage.clear()
    const onMessage = vi.fn()
    const client = connect('ws://localhost:8080/websocket.shtml', onMessage)
    const sock = FakeWebSocket.instances[0]!
    client.send('client-msg-1')
    expect(sock.sentFrames).toEqual(['client-msg-1'])
    sock.close() // readyState 变 CLOSED
    client.send('client-msg-2') // 关闭后忽略，无 effect
    expect(sock.sentFrames).toEqual(['client-msg-1'])
  })
})
