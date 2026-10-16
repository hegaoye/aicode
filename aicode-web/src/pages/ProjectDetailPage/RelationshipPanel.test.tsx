import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { beforeEach, describe, expect, it } from 'vitest'
import { server } from '../../test-setup'
import RelationshipPanel from './RelationshipPanel'

/**
 * 「关联关系」区块的测试 —— 代码生成配置流程的第 ⑤ 步。
 *
 * 前 4 条对应 delta spec `aicode-web-page` 的
 * 「多级联动的写侧表单 SHALL 按依赖顺序加载候选并可重置下游选择」的 4 个 Scenario：
 *   ① 上游变化清空下游   ② 下游候选项在上游选定后才加载
 *   ③ 两组级联互不干扰   ④ 提交前校验必填项已选
 * 后 3 条覆盖：保存后重拉、upsert 码失效、表名映射回落。
 */

const PROJECT_CODE = '1882812765213425001'

const TABLES = [
  { code: 'ct-user', tableName: 'user', className: 'User' },
  { code: 'ct-order', tableName: 't_order', className: 'Order' },
]

/** 记录 build 请求的参数。 */
let lastBuildParams: Record<string, string> = {}
/** build 被调次数 —— 用于验证「保存后重拉」而非「本地追加」。 */
let buildCount = 0
/** list 被调次数。 */
let listCount = 0

/** 拦截读表候选。 */
function useTables() {
  server.use(
    http.get('*/project/relationship/listMapClassTable*', () =>
      HttpResponse.json({ success: true, code: '0000', info: 'success', data: TABLES }),
    ),
  )
}

/** 拦截读字段候选（一次返回主表 + 从表两组）。 */
function useFieldCandidates() {
  server.use(
    http.get('*/project/relationship/listMapFieldColumn*', (info) => {
      const url = new URL(String(info.request.url))
      const main = url.searchParams.get('mapClassTableCode')
      const assoc = url.searchParams.get('associateCode')
      const fieldsOf = (prefix: string) =>
        main === prefix
          ? [
              { code: 'f1', mapClassTableCode: prefix, column: 'id', field: 'id', fieldType: 'java.lang.Long', isPrimaryKey: 'Y', isDate: 'N', isState: 'N' },
              { code: 'f2', mapClassTableCode: prefix, column: 'user_id', field: 'userId', fieldType: 'java.lang.Long', isPrimaryKey: 'N', isDate: 'N', isState: 'N' },
            ]
          : [
              { code: 'g1', mapClassTableCode: assoc, column: 'id', field: 'id', fieldType: 'java.lang.Long', isPrimaryKey: 'Y', isDate: 'N', isState: 'N' },
            ]
      return HttpResponse.json({
        success: true,
        code: '0000',
        info: 'success',
        data: {
          mainFields: fieldsOf('ct-user'),
          associateFields: fieldsOf('ct-order'),
          mapRelationship: null,
        },
      })
    }),
  )
}

/** 拦截保存。 */
function useBuild() {
  server.use(
    http.post('*/project/relationship/build*', async (info) => {
      buildCount += 1
      lastBuildParams = {}
      const url = new URL(String(info.request.url))
      url.searchParams.forEach((v, k) => {
        lastBuildParams[k] = v
      })
      return HttpResponse.json({ success: true, code: '0000', info: 'success', data: null })
    }),
  )
}

/** 拦截读已配关系。 */
function useRelationships(rows: Array<Record<string, string>>) {
  server.use(
    // pattern 刻意用 `list?` 而非 `list*` —— 后者的 `*` 会一并匹配
    // `listMapClassTable` 与 `listMapFieldColumn`，把表候选/字段候选的请求也劫持掉
    // （MSW 后注册的 handler 优先，实测会让整个面板退化成空态）。
    http.get('*/project/relationship/list?*', () => {
      listCount += 1
      return HttpResponse.json({ success: true, code: '0000', info: 'success', data: rows })
    }),
  )
}

function renderPanel() {
  return render(<RelationshipPanel projectCode={PROJECT_CODE} />)
}

/** 等表候选加载完。 */
async function waitForTables() {
  await waitFor(() => {
    expect(screen.getByTestId('relationship-main-table').querySelectorAll('option').length)
      .toBeGreaterThan(1)
  })
}

/** 依次选主表、从表（会触发字段候选加载）。 */
async function pickBothTables() {
  await waitForTables()
  fireEvent.change(screen.getByTestId('relationship-main-table'), {
    target: { value: 'ct-user' },
  })
  fireEvent.change(screen.getByTestId('relationship-associate-table'), {
    target: { value: 'ct-order' },
  })
  await waitFor(() => {
    expect(
      screen.getByTestId('relationship-main-field').querySelectorAll('option').length,
    ).toBeGreaterThan(1)
  })
}

describe('RelationshipPanel（关联关系）', () => {
  beforeEach(() => {
    lastBuildParams = {}
    buildCount = 0
    listCount = 0
    useTables()
    useFieldCandidates()
    useBuild()
    useRelationships([])
  })

  // ---- Scenario ②：下游候选项在上游选定后才加载 ----

  it('未选表时字段下拉为空且给出「请先选择主表」提示，不发字段请求', async () => {
    let fieldReq = 0
    server.use(
      http.get('*/project/relationship/listMapFieldColumn*', () => {
        fieldReq += 1
        return HttpResponse.json({ success: true, code: '0000', info: 'success', data: {} })
      }),
    )
    renderPanel()
    await waitForTables()

    expect(screen.getByTestId('relationship-main-field')).toBeDisabled()
    expect(screen.getByTestId('relationship-associate-field')).toBeDisabled()
    // 提示语要让用户知道为什么不能选
    expect(screen.getByTestId('relationship-main-field').textContent).toContain('请先选择主表')
    expect(fieldReq).toBe(0)
  })

  it('两侧表选定后才拉字段候选（一次请求同时喂两个下拉）', async () => {
    let fieldReq = 0
    server.use(
      http.get('*/project/relationship/listMapFieldColumn*', () => {
        fieldReq += 1
        return HttpResponse.json({
          success: true,
          code: '0000',
          info: 'success',
          data: {
            mainFields: [
              { code: 'f1', field: 'id' },
              { code: 'f2', field: 'userId' },
            ],
            associateFields: [{ code: 'g1', field: 'id' }],
            mapRelationship: null,
          },
        })
      }),
    )
    renderPanel()
    await waitForTables()

    // 只选主表 → 从表未定，仍不请求
    fireEvent.change(screen.getByTestId('relationship-main-table'), {
      target: { value: 'ct-user' },
    })
    expect(fieldReq).toBe(0)

    fireEvent.change(screen.getByTestId('relationship-associate-table'), {
      target: { value: 'ct-order' },
    })
    await waitFor(() => {
      expect(fieldReq).toBe(1)
    })
    // 两个下拉都被这一次请求填上
    const mainOptions = screen
      .getByTestId('relationship-main-field')
      .querySelectorAll('option')
    const assocOptions = screen
      .getByTestId('relationship-associate-field')
      .querySelectorAll('option')
    expect(mainOptions.length).toBe(3) // 占位 + 2
    expect(assocOptions.length).toBe(2) // 占位 + 1
  })

  // ---- Scenario ①：上游变化清空下游 ----

  it('改主表 SHALL 清空已选的主表字段', async () => {
    renderPanel()
    await pickBothTables()

    fireEvent.change(screen.getByTestId('relationship-main-field'), {
      target: { value: 'userId' },
    })
    expect(
      (screen.getByTestId('relationship-main-field') as HTMLSelectElement).value,
    ).toBe('userId')

    // 改主表
    fireEvent.change(screen.getByTestId('relationship-main-table'), {
      target: { value: 'ct-order' },
    })

    expect(
      (screen.getByTestId('relationship-main-field') as HTMLSelectElement).value,
    ).toBe('__unset__')
  })

  // ---- Scenario ③：两组级联互不干扰 ----

  it('改主表 SHALL NOT 清空从表与从字段', async () => {
    renderPanel()
    await pickBothTables()

    fireEvent.change(screen.getByTestId('relationship-associate-field'), {
      target: { value: 'id' },
    })
    fireEvent.change(screen.getByTestId('relationship-main-field'), {
      target: { value: 'userId' },
    })

    // 改主表
    fireEvent.change(screen.getByTestId('relationship-main-table'), {
      target: { value: 'ct-order' },
    })

    // 从表那组原封不动
    expect(
      (screen.getByTestId('relationship-associate-table') as HTMLSelectElement).value,
    ).toBe('ct-order')
    expect(
      (screen.getByTestId('relationship-associate-field') as HTMLSelectElement).value,
    ).toBe('id')
  })

  // ---- Scenario ④：提交前校验 ----

  it('缺主表字段时 SHALL 阻止提交并指明缺哪项', async () => {
    renderPanel()
    await pickBothTables()

    fireEvent.click(screen.getByTestId('relationship-save'))

    await waitFor(() => {
      expect(screen.getByTestId('relationship-error')).toBeInTheDocument()
    })
    expect(screen.getByTestId('relationship-error').textContent).toContain('请先选择主表字段')
    expect(buildCount).toBe(0)
  })

  it('一张表都没选时 SHALL 提示先选主表', async () => {
    renderPanel()
    await waitForTables()

    fireEvent.click(screen.getByTestId('relationship-save'))

    await waitFor(() => {
      expect(screen.getByTestId('relationship-error').textContent).toContain('请先选择主表')
    })
    expect(buildCount).toBe(0)
  })

  // ---- 提交参数 ----

  it('提交 SHALL 带齐 6 个参数，且「一对多」映射为 oneToMany=Y（design D5）', async () => {
    renderPanel()
    await pickBothTables()

    fireEvent.change(screen.getByTestId('relationship-main-field'), {
      target: { value: 'userId' },
    })
    fireEvent.change(screen.getByTestId('relationship-associate-field'), {
      target: { value: 'id' },
    })
    // 默认基数是一对多
    expect(screen.getByTestId('relationship-cardinality-many')).toBeChecked()

    fireEvent.click(screen.getByTestId('relationship-save'))

    await waitFor(() => {
      expect(buildCount).toBe(1)
    })
    expect(lastBuildParams).toEqual({
      mapClassTableCode: 'ct-user',
      associateCode: 'ct-order',
      oneToOne: 'N',
      oneToMany: 'Y',
      mainField: 'userId',
      joinField: 'id',
    })
  })

  it('切到「一对一」SHALL 映射为 oneToOne=Y 且 oneToMany=N（不产生 Y/Y 非法组合）', async () => {
    renderPanel()
    await pickBothTables()
    fireEvent.change(screen.getByTestId('relationship-main-field'), {
      target: { value: 'userId' },
    })
    fireEvent.change(screen.getByTestId('relationship-associate-field'), {
      target: { value: 'id' },
    })

    fireEvent.click(screen.getByTestId('relationship-cardinality-one'))
    fireEvent.click(screen.getByTestId('relationship-save'))

    await waitFor(() => {
      expect(buildCount).toBe(1)
    })
    expect(lastBuildParams.oneToOne).toBe('Y')
    expect(lastBuildParams.oneToMany).toBe('N')
  })

  // ---- 保存后重拉（upsert 的 code 会变，本地追加会显示失效 code） ----

  it('保存成功后 SHALL 重拉关系列表（build 是 upsert，本地追加会显示失效 code）', async () => {
    renderPanel()
    await pickBothTables()
    const listBefore = listCount

    fireEvent.change(screen.getByTestId('relationship-main-field'), {
      target: { value: 'userId' },
    })
    fireEvent.change(screen.getByTestId('relationship-associate-field'), {
      target: { value: 'id' },
    })
    fireEvent.click(screen.getByTestId('relationship-save'))

    await waitFor(() => {
      expect(buildCount).toBe(1)
    })
    await waitFor(() => {
      expect(listCount).toBeGreaterThan(listBefore)
    })
  })

  // ---- 表名映射回落 ----

  it('关系列表 SHALL 用本地映射显示从表名；映射不到时回落显示 associateCode', async () => {
    useRelationships([
      {
        code: 'r1',
        mapClassTableCode: 'ct-user',
        associateCode: 'ct-order',
        isOneToOne: 'N',
        isOneToMany: 'Y',
        mainField: 'userId',
        joinField: 'id',
      },
      {
        code: 'r2',
        mapClassTableCode: 'ct-user',
        // ct-ghost 不在表候选里 —— 映射不到
        associateCode: 'ct-ghost',
        isOneToOne: 'Y',
        isOneToMany: 'N',
        mainField: 'profileId',
        joinField: 'id',
      },
    ])
    renderPanel()
    await pickBothTables()

    await waitFor(() => {
      expect(screen.getByTestId('relationship-list')).toBeInTheDocument()
    })
    const cells = Array.from(
      screen.getByTestId('relationship-list').querySelectorAll('tbody td'),
    ).map((td) => td.textContent)
    // 已知 code → 表名；未知 code → 原值回落
    expect(cells).toContain('t_order')
    expect(cells).toContain('ct-ghost')
    expect(cells).toContain('一对多')
    expect(cells).toContain('一对一')
  })

  it('无表可关联时 SHALL 给出引导空态', async () => {
    server.use(
      http.get('*/project/relationship/listMapClassTable*', () =>
        HttpResponse.json({ success: false, code: '9006', info: '不存在', data: null }),
      ),
    )
    renderPanel()

    await waitFor(() => {
      expect(screen.getByTestId('relationship-no-tables')).toBeInTheDocument()
    })
    expect(screen.getByTestId('relationship-no-tables').textContent).toContain('先在上方导入 SQL')
  })
})