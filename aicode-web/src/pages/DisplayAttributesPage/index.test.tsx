import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { beforeEach, describe, expect, it } from 'vitest'
import { MemoryRouter } from 'react-router-dom'
import { server } from '../../test-setup'
import { getList5MockHandler } from '../../generated/api.msw'
import { DisplayAttributeVODisplayType } from '../../generated/model/displayAttributeVODisplayType'
import type { RListMapFieldColumnVO } from '../../generated/model/rListMapFieldColumnVO'
import DisplayAttributesPage from './index'

/**
 * `DisplayAttributesPage` 的写侧测试 —— 代码生成配置流程第 ④ 步
 * （`2026-10-18-display-attribute-write`）。
 *
 * <p>M1 时代本页只有 1 个用例（只验渲染），且 mock 数据用的是**假枚举值**
 * （`text` / `number`）—— 本组全部改用契约真实枚举值。</p>
 */

const ROWS = [
  {
    mapClassTableCode: 'mct1',
    code: 'fc1',
    column: 'name',
    field: 'name',
    fieldType: 'java.lang.String',
    displayAttribute: { displayType: 'Input', displayName: '名称' },
  },
  {
    mapClassTableCode: 'mct1',
    code: 'fc2',
    column: 'age',
    field: 'age',
    fieldType: 'java.lang.Integer',
    displayAttribute: { displayType: 'InputNumber', displayName: '年龄' },
  },
]

function useList(rows = ROWS) {
  server.use(
    getList5MockHandler({
      success: true,
      code: '0000',
      info: 'success',
      data: rows,
    } as unknown as RListMapFieldColumnVO),
  )
}

/** 拦截 save，并记录提交内容与调用次数。 */
function useSave(counter = { count: 0 }, ok = true) {
  const submitted: unknown[][] = []
  server.use(
    http.post('*/displayAttribute/save*', async (info) => {
      counter.count += 1
      submitted.push((await info.request.json()) as unknown[])
      return ok
        ? HttpResponse.json({ success: true, code: '0000', info: 'success', data: null })
        : HttpResponse.json({ success: false, code: '9999', info: '保存炸了', data: null })
    }),
  )
  return submitted
}

function renderPage() {
  return render(
    <MemoryRouter>
      <DisplayAttributesPage />
    </MemoryRouter>,
  )
}

describe('DisplayAttributesPage（写侧）', () => {
  beforeEach(() => {
    window.localStorage.clear()
  })

  // ---- 表单渲染 ----

  it('为每行渲染一个编辑表单，含 8 个字段控件', async () => {
    useList()
    renderPage()

    await screen.findByTestId('display-attr-form-fc1')
    expect(screen.getByTestId('display-attr-form-fc2')).toBeInTheDocument()
    // 每行一组控件（2 行）
    expect(screen.getAllByTestId('display-attr-displayType-input')).toHaveLength(2)
    expect(screen.getAllByTestId('display-attr-displayName-input')).toHaveLength(2)
    expect(screen.getAllByTestId('display-attr-displayNo-input')).toHaveLength(2)
    for (const f of [
      'isRequired',
      'isInsert',
      'isAllowUpdate',
      'isListPageDisplay',
      'isDetailPageDisplay',
    ]) {
      expect(screen.getAllByTestId(`display-attr-${f}-input`)).toHaveLength(2)
    }
  })

  it('displayType 下拉选项 SHALL 等于契约枚举全集（24 个，不手写副本）', async () => {
    useList()
    renderPage()

    await screen.findByTestId('display-attr-form-fc1')
    const select = screen.getAllByTestId('display-attr-displayType-input')[0]
    const options = Array.from((select as HTMLSelectElement).options).map((o) => o.value)
    // 首项是「（未设置）」空占位
    expect(options[0]).toBe('')
    expect(options.slice(1)).toEqual(Object.values(DisplayAttributeVODisplayType))
    expect(options).toHaveLength(25)
  })

  it('UI SHALL 提示「改完需重新构建才生效」', async () => {
    useList()
    renderPage()

    await screen.findByTestId('display-attr-rebuild-hint')
    expect(screen.getByTestId('display-attr-rebuild-hint').textContent).toContain('重新构建')
  })

  // ---- 提交 ----

  it('「保存全部」SHALL 批量提交整页，每项带隐藏的 mapFieldColumnCode', async () => {
    useList()
    const submitted = useSave()
    renderPage()

    await screen.findByTestId('display-attr-form-fc1')
    fireEvent.click(screen.getByTestId('display-attr-save-all'))

    await waitFor(() => {
      expect(submitted).toHaveLength(1)
    })
    const payload = submitted[0] as Array<Record<string, unknown>>
    expect(payload).toHaveLength(2)
    // 隐藏定位键（design D6）—— 后端按它定位 upsert
    expect(payload[0]).toMatchObject({
      mapFieldColumnCode: 'fc1',
      mapClassTableCode: 'mct1',
      displayType: 'Input',
      displayName: '名称',
    })
    expect(payload[1]).toMatchObject({ mapFieldColumnCode: 'fc2', displayType: 'InputNumber' })
  })

  it('编辑后提交 SHALL 带上新值（改显示名 + 勾必填）', async () => {
    useList()
    const submitted = useSave()
    renderPage()

    await screen.findByTestId('display-attr-form-fc1')
    const nameInputs = screen.getAllByTestId('display-attr-displayName-input')
    fireEvent.change(nameInputs[0], { target: { value: '用户昵称' } })
    // 第一行的「必填」勾上
    const requiredBoxes = screen.getAllByTestId('display-attr-isRequired-input')
    fireEvent.click(requiredBoxes[0])

    fireEvent.click(screen.getByTestId('display-attr-save-all'))

    await waitFor(() => {
      expect(submitted).toHaveLength(1)
    })
    expect((submitted[0] as Array<Record<string, unknown>>)[0]).toMatchObject({
      displayName: '用户昵称',
      isRequired: 'Y',
    })
  })

  it('改 displayType 下拉后 SHALL 以新值提交（验证契约枚举在链路里真的通）', async () => {
    useList()
    const submitted = useSave()
    renderPage()

    await screen.findByTestId('display-attr-form-fc1')
    const select = screen.getAllByTestId('display-attr-displayType-input')[0]
    fireEvent.change(select, { target: { value: 'DatePicker' } })

    fireEvent.click(screen.getByTestId('display-attr-save-all'))

    await waitFor(() => {
      expect(submitted).toHaveLength(1)
    })
    expect((submitted[0] as Array<Record<string, unknown>>)[0]).toMatchObject({
      displayType: 'DatePicker',
    })
  })

  it('提交成功后 SHALL 重新拉取列表（后端 save 无事务且静默 continue，不做乐观更新）', async () => {
    useList()
    let listCalls = 0
    server.use(
      http.get('*/displayAttribute/list*', () => {
        listCalls += 1
        return HttpResponse.json({
          success: true,
          code: '0000',
          info: 'success',
          data: ROWS,
        })
      }),
    )
    useSave()
    renderPage()

    await screen.findByTestId('display-attr-form-fc1')
    const before = listCalls
    fireEvent.click(screen.getByTestId('display-attr-save-all'))

    await waitFor(() => {
      expect(listCalls).toBeGreaterThan(before)
    })
  })

  it('提交中 SHALL 禁用按钮以防重复提交', async () => {
    useList()
    const counter = { count: 0 }
    useSave(counter)
    renderPage()

    await screen.findByTestId('display-attr-form-fc1')
    const btn = screen.getByTestId('display-attr-save-all')
    fireEvent.click(btn)

    await waitFor(() => {
      expect(btn).toBeDisabled()
    })
    await waitFor(() => {
      expect(btn).not.toBeDisabled()
    })
    expect(counter.count).toBe(1)
  })

  // ---- 错误 ----

  it('R.code !== "0000" SHALL 呈现错误气泡且保留用户已改的值', async () => {
    useList()
    useSave({ count: 0 }, false)
    renderPage()

    await screen.findByTestId('display-attr-form-fc1')
    const nameInputs = screen.getAllByTestId('display-attr-displayName-input')
    fireEvent.change(nameInputs[0], { target: { value: '不该丢的值' } })

    fireEvent.click(screen.getByTestId('display-attr-save-all'))

    await waitFor(() => {
      expect(screen.getByTestId('display-attr-error')).toBeInTheDocument()
    })
    expect(screen.getByTestId('display-attr-error').textContent).toContain('保存炸了')
    // 失败时用户已改的值 SHALL 保留，不被清空
    expect(screen.getAllByTestId('display-attr-displayName-input')[0]).toHaveValue(
      '不该丢的值',
    )
  })

  it('无字段时 SHALL 给出引导空态而非空白表单区', async () => {
    useList([])
    renderPage()

    await screen.findByTestId('display-attr-empty')
    expect(screen.getByTestId('display-attr-empty').textContent).toContain('先在项目详情页导入 SQL')
    expect(screen.queryByTestId('display-attr-save-all')).toBeNull()
  })
})