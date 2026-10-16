/**
 * WebSocket 结构化进度事件的分流 —— **协议的唯一判定处**。
 *
 * <p>ws 通道上同时有两类消息（见
 * `openspec/specs/init-async-progress/spec.md` 的「结构化事件 SHALL 与既有自由文本日志共存」）：
 * ① 结构化事件（以 `{` 开头的 JSON，用于长耗时任务的进度与终态）
 * ② 自由文本日志行（如构建日志的 `git 努力下载中.....`）。</p>
 *
 * <p>本模块承载「怎么区分」的全部不变式；两个消费方
 * （`useInitProgress` 解析进度、`useBuildLog` 构建日志）共用同一套判据 ——
 * 分流判据若两处各写一份必然漂移。</p>
 */

/** 进度事件类型 —— 与后端 `ProjectInitProgress` / `BuildProgress` 的 `type` 字段对应。 */
export type ProgressEventType = 'progress' | 'done' | 'error'

/** 阶段 —— 对应后端 `phase` 字段。构建场景当前不使用（字段为 null 被 fastjson2 省略）。 */
export type ProgressPhase = 'createDatabase' | 'parse'

/**
 * 进度事件结构 —— 字段名与后端严格对应。
 *
 * <p>⚠️ ws 不在 OpenAPI 覆盖范围内，**无契约守护**。字段名一致性靠
 * `use-init-progress.test.ts` 的「字段名 SHALL 与后端一致」断言 +
 * 后端 `BuildProgressTerminalSignalTest` 双侧钉住。</p>
 *
 * <p>可选性说明：`phase` / `current` / `total` 在**构建**场景不使用 ——
 * 后端 `BuildProgress` 不给 `phase` 赋值，fastjson2 省略 null 字段（AGENTS §6.5），
 * 故该键在消息中**不存在**。</p>
 */
export interface ProgressEvent {
  type: ProgressEventType
  phase?: ProgressPhase
  current?: number
  total?: number
  message?: string
}

/** 三个合法 `type` 值 —— 集合判定用，避免散落字面量。 */
const VALID_TYPES: ReadonlySet<string> = new Set<ProgressEventType>([
  'progress',
  'done',
  'error',
])

/**
 * 解析一条 ws 消息为结构化进度事件。
 *
 * @returns 结构化事件；**非**结构化消息返回 `null`（调用方按自由文本处理）
 *
 * @remarks 判据**只看首字符 `{`**，SHALL NOT 依赖消息内容特征（如关键词匹配）。
 * 以 `{` 开头但 JSON 非法、或形似 JSON 但不是合法事件（如 `{"foo":1}`）、
 * 或 `type` 不在三个合法值内 —— 一律返回 `null` 而**不抛错**，以免中断订阅
 * 或把别的任务的事件当成本任务的进度。
 */
export function parseProgressEvent(raw: string): ProgressEvent | null {
  if (!raw.trimStart().startsWith('{')) {
    return null
  }
  try {
    const parsed = JSON.parse(raw) as unknown
    if (!parsed || typeof parsed !== 'object' || Array.isArray(parsed)) {
      return null
    }
    if (!('type' in parsed)) {
      return null
    }
    const type = (parsed as { type: unknown }).type
    if (typeof type !== 'string' || !VALID_TYPES.has(type)) {
      return null
    }
    return parsed as ProgressEvent
  } catch {
    // 以 { 开头但非法 JSON → 按文本处理，不中断订阅
    return null
  }
}

/** 字段名清单 —— 与后端 VO 一一对应。供测试断言用（运行时无意义）。 */
export const PROGRESS_EVENT_FIELD_NAMES = [
  'type',
  'phase',
  'current',
  'total',
  'message',
] as const