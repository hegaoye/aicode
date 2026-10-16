import { describe, expect, it } from 'vitest'
import {
  parseProgressEvent,
  PROGRESS_EVENT_FIELD_NAMES,
} from './progress-event'

/**
 * `progress-event` 的**消息解析**单测 —— 该模块被 `useInitProgress` 与
 * `useBuildLog` **共用**，故它是两个消费方的共同防线。
 *
 * <p>只测纯函数 `parseInitEvent` —— 它承载了 spec「消费端 SHALL 按消息形态分流」的
 * 全部判定逻辑，而 hook 的状态机需要 WebSocket 环境（jsdom 不实现，见 AGENTS §7）。</p>
 *
 * <p>Hook 层面的联动（连 ws → 调 init → 状态流转）由
 * `ProjectSqlPanel.test.tsx` 覆盖（那里 mock 掉 `ws-client` 后直接投递消息）。</p>
 */

describe('parseProgressEvent（ws 消息分流 —— 协议的唯一判定处）', () => {
  // ---- 结构化事件被正确识别 ----

  it('progress 事件 SHALL 被解析并保留全部字段', () => {
    const raw = JSON.stringify({
      type: 'progress',
      phase: 'parse',
      current: 3,
      total: 12,
      message: '正在解析第 4/12 张表：t_order',
    })
    expect(parseProgressEvent(raw)).toEqual({
      type: 'progress',
      phase: 'parse',
      current: 3,
      total: 12,
      message: '正在解析第 4/12 张表：t_order',
    })
  })

  it('done / error 事件 SHALL 被识别', () => {
    expect(parseProgressEvent(JSON.stringify({ type: 'done', message: '解析完成' }))).toEqual({
      type: 'done',
      message: '解析完成',
    })
    expect(parseProgressEvent(JSON.stringify({ type: 'error', message: '已解析 4/12 张表后失败' }))).toEqual({
      type: 'error',
      message: '已解析 4/12 张表后失败',
    })
  })

  // ---- 自由文本按文本处理（返回 null，调用方静默忽略） ----

  it('自由文本日志 SHALL 返回 null（不当结构化事件）', () => {
    // 构建日志的真实形态（AGENTS §11.3 记：GeneratorSVImpl 17 处 sendMessage）
    for (const line of [
      'git 努力下载中.....',
      '开始生成源码...',
      '转化数据库结构与类模型成功！',
      '结束 下载技术模板成功',
      '',
      '   ',
    ]) {
      expect(parseProgressEvent(line)).toBeNull()
    }
  })

  it('以 { 开头但非法 JSON SHALL 返回 null 而非抛错', () => {
    expect(parseProgressEvent('{ 这不是合法 JSON')).toBeNull()
    expect(parseProgressEvent('{"unclosed": ')).toBeNull()
    expect(parseProgressEvent('{')).toBeNull()
  })

  it('形似 JSON 但无 type 字段 SHALL 返回 null', () => {
    expect(parseProgressEvent('{"foo":1}')).toBeNull()
    expect(parseProgressEvent('[1,2,3]')).toBeNull()
    expect(parseProgressEvent('null')).toBeNull()
    expect(parseProgressEvent('42')).toBeNull()
  })

  it('type 不是三个合法值之一 SHALL 返回 null', () => {
    // 否则消费方会把别的任务的事件当成本任务的进度
    expect(parseProgressEvent(JSON.stringify({ type: 'warning', message: 'x' }))).toBeNull()
    expect(parseProgressEvent(JSON.stringify({ type: 'build-progress' }))).toBeNull()
  })

  // ---- 前导空白容忍 ----

  it('前导空白 SHALL 容忍（判据用 trimStart 后再判首字符）', () => {
    const raw = `  ${JSON.stringify({ type: 'done', message: '解析完成' })}`
    expect(parseProgressEvent(raw)).toEqual({ type: 'done', message: '解析完成' })
  })

  // ---- 字段名与后端契约的对应（ws 无契约守护，靠本断言钉住） ----

  it('事件字段名 SHALL 与后端 ProjectInitProgress 一致', () => {
    // 后端：facade/project/vo/ProjectInitProgress.java 的字段是
    //   type / phase / current / total / message
    // 若前端改了字段名（手抄错），本断言列出实际拿到的键，便于 review 时对照。
    const parsed = parseProgressEvent(
      JSON.stringify({
        type: 'progress',
        phase: 'parse',
        current: 1,
        total: 2,
        message: 'x',
      }),
    )
    expect(Object.keys(parsed ?? {}).sort()).toEqual(
      [...PROGRESS_EVENT_FIELD_NAMES].sort(),
    )
  })

  it('缺失可选字段的事件 SHALL 不报错（phase/current/total 可省）', () => {
    expect(parseProgressEvent(JSON.stringify({ type: 'done' }))).toEqual({ type: 'done' })
    expect(parseProgressEvent(JSON.stringify({ type: 'progress' }))).toEqual({ type: 'progress' })
  })
})