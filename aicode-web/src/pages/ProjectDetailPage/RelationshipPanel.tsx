import { useCallback, useEffect, useMemo, useState } from 'react'
import { ContractError, unwrapSuccess } from '../../lib/contract-response'
import { DisplayControl } from '../../lib/display-controls'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import { Build2OneToOne } from '../../generated/model/build2OneToOne'
import type { MapClassTableVO } from '../../generated/model/mapClassTableVO'
import type { MapFieldColumnReadVO } from '../../generated/model/mapFieldColumnReadVO'
import type { MapFieldColumnVO } from '../../generated/model/mapFieldColumnVO'
import type { MapRelationshipReadVO } from '../../generated/model/mapRelationshipReadVO'

/**
 * `RelationshipPanel` —— `ProjectDetailPage` 的「关联关系」区块。
 *
 * <p>代码生成配置流程的第 ⑤ 步（`2026-10-18-relationship-config`）：
 * `MapRelationship` 是「三层映射」的第三层（AGENTS §5.4），通过 `mainField` /
 * `joinField` 定义 1:1 与 1:N，生成器据此产出关联查询与导航代码。
 * **不配它，生成的代码就是一堆孤立 CRUD。**</p>
 *
 * <p>视觉基准：OpenDesign 原型 `wizard-step3-models.html`（向导第 3 步「数据管理」）。
 * 该原型把本面板表达为「操作提示 → 数据库模型表（两列：表 / 操作）→ 行内两个入口」，
 * 本文件 100% 还原这三段；原型未画出展开区（静态稿只画了收起态），
 * 展开区按任务要求放在表格下方，由行内两个入口切换：</p>
 *
 * <ul>
 *   <li>「模型关系」—— 两组级联 + 基数 + 提交（默认视图）；</li>
 *   <li>「字段属性」—— 该表字段的展示属性控件预览，复用 `lib/display-controls` 的
 *       `<DisplayControl>`（原型同一行上的另一个入口，对应流程第 ④ 步）。</li>
 * </ul>
 *
 * <p>三个端点：</p>
 * <ul>
 *   <li>`GET /project/relationship/listMapClassTable` —— 表候选（与 `ProjectSqlPanel` 同源）；</li>
 *   <li>`GET /project/relationship/listMapFieldColumn` —— 选表后懒加载该表的字段候选
 *       （响应是 `RelationshipFieldsVO`，含 `mainFields` / `associateFields` /
 *       `mapRelationship` 三个 key）；</li>
 *   <li>`POST /project/relationship/build` —— 保存（upsert 语义，见下）；</li>
 *   <li>`GET /project/relationship/list` —— 该表已有的关系。</li>
 * </ul>
 *
 * <p><b>两组独立级联</b>（design D4）：主表→主字段、从表→从字段，改主表 SHALL NOT
 * 动从表那组。基数用 radio 而非两个 checkbox —— 后端虽有 4 个 `required` 参数，
 * 但「基数」只有一个，允许同时 `oneToOne=Y, oneToMany=Y` 是无效状态。</p>
 *
 * <p><b>保存后必须重拉列表</b>：`build` 是 upsert（后端 `load()` 先 `remove` 再 `new`），
 * 每次保存都生成**新的雪花 `code`**，旧 code 失效。本地追加会显示已失效的 code。</p>
 */

/** 基数：UI 上的一个选择，映射为后端的两个 required 参数。 */
type Cardinality = 'oneToMany' | 'oneToOne'

/** 展开区当前展示的视图 —— 对应表格行内的两个操作入口。 */
type ExpandView = 'relationship' | 'fields'

interface Props {
  projectCode: string
}

const PLACEHOLDER = '__unset__'

export default function RelationshipPanel({ projectCode }: Props) {
  const api = useMemo(() => getAicodeRESTfulAPIs(), [])

  // ---- 表候选（与 ProjectSqlPanel 同源，但各自独立拉取，互不干扰） ----
  const [tables, setTables] = useState<MapClassTableVO[]>([])

  // ---- 两组级联，各自独立 ----
  const [mainTableCode, setMainTableCode] = useState<string>(PLACEHOLDER)
  const [mainField, setMainField] = useState<string>(PLACEHOLDER)
  const [associateTableCode, setAssociateTableCode] = useState<string>(PLACEHOLDER)
  const [associateField, setAssociateField] = useState<string>(PLACEHOLDER)
  const [cardinality, setCardinality] = useState<Cardinality>('oneToMany')

  const [mainFields, setMainFields] = useState<MapFieldColumnReadVO[]>([])
  const [associateFields, setAssociateFields] = useState<MapFieldColumnReadVO[]>([])
  const [relationships, setRelationships] = useState<MapRelationshipReadVO[]>([])

  const [errorMsg, setErrorMsg] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)

  // ---- 展开区视图（纯展示态，不参与上面任何级联/保存逻辑） ----
  const [expandView, setExpandView] = useState<ExpandView>('relationship')
  /** 「字段属性」视图当前针对哪张表。 */
  const [fieldTableCode, setFieldTableCode] = useState<string>('')
  const [fieldRows, setFieldRows] = useState<MapFieldColumnVO[]>([])
  const [fieldLoading, setFieldLoading] = useState(false)
  const [fieldErrorMsg, setFieldErrorMsg] = useState<string | null>(null)

  useEffect(() => {
    if (!projectCode) {
      return
    }
    api
      .listMapClassTable({ projectCode })
      .then((resp) => {
        const list = unwrapSuccess(resp)
        setTables((list ?? []) as MapClassTableVO[])
      })
      .catch(() => {
        // 尚未 init 解析出表时该端点会失败 —— 用空态即可，不打扰用户
        setTables([])
      })
  }, [api, projectCode])

  /**
   * 拉取某张表的字段候选（懒加载）。
   *
   * <p>注意 `listMapFieldColumn` 需要**两个**参数（`mapClassTableCode` +
   * `associateCode`），且它一次返回两组字段（主表 + 从表）。所以一次请求即可
   * 同时喂两个下拉 —— 不必各拉一次。</p>
   */
  const loadFieldCandidates = useCallback(
    async (mainCode: string, associateCode: string) => {
      if (!projectCode || !mainCode || !associateCode) {
        setMainFields([])
        setAssociateFields([])
        return
      }
      try {
        const resp = await api.listMapFieldColumn({
          mapClassTableCode: mainCode,
          associateCode,
        })
        const vo = unwrapSuccess(resp)
        setMainFields(vo?.mainFields ?? [])
        setAssociateFields(vo?.associateFields ?? [])
        // 已有关系则回填基数与字段，省得用户重复选（这是 upsert 的自然语义）
        const existing = vo?.mapRelationship
        if (existing) {
          setCardinality(existing.isOneToMany === 'Y' ? 'oneToMany' : 'oneToOne')
          setMainField(existing.mainField ?? PLACEHOLDER)
          setAssociateField(existing.joinField ?? PLACEHOLDER)
        }
      } catch (err: unknown) {
        setMainFields([])
        setAssociateFields([])
        setErrorMsg(
          err instanceof ContractError
            ? `读取字段候选失败：${err.info || err.code}`
            : '读取字段候选失败',
        )
      }
    },
    [api, projectCode],
  )

  // 两侧表都选定后才拉候选；任一侧变化则重拉（并清空已选字段）
  useEffect(() => {
    if (mainTableCode === PLACEHOLDER || associateTableCode === PLACEHOLDER) {
      setMainFields([])
      setAssociateFields([])
      return
    }
    void loadFieldCandidates(mainTableCode, associateTableCode)
  }, [loadFieldCandidates, mainTableCode, associateTableCode])

  const loadRelationships = useCallback(
    (tableCode: string) => {
      if (!tableCode || tableCode === PLACEHOLDER) {
        setRelationships([])
        return
      }
      api
        .list({ classTableCode: tableCode })
        .then((resp) => {
          const list = unwrapSuccess(resp)
          setRelationships((list ?? []) as MapRelationshipReadVO[])
        })
        .catch(() => {
          setRelationships([])
        })
    },
    [api],
  )

  // 已配关系跟随主表变化
  useEffect(() => {
    loadRelationships(mainTableCode)
  }, [loadRelationships, mainTableCode])

  /** 提交前的必填校验，返回缺项说明（null = 通过）。 */
  const validate = useCallback((): string | null => {
    if (mainTableCode === PLACEHOLDER) {
      return '请先选择主表'
    }
    if (associateTableCode === PLACEHOLDER) {
      return '请先选择从表'
    }
    if (mainField === PLACEHOLDER) {
      return '请先选择主表字段'
    }
    if (associateField === PLACEHOLDER) {
      return '请先选择从表字段'
    }
    return null
  }, [associateField, associateTableCode, mainField, mainTableCode])

  const handleSubmit = useCallback(async () => {
    if (saving) {
      return
    }
    const missing = validate()
    if (missing) {
      setErrorMsg(missing)
      return
    }
    setSaving(true)
    setErrorMsg(null)
    try {
      const resp = await api.build2({
        mapClassTableCode: mainTableCode,
        associateCode: associateTableCode,
        // 基数 → 两个 required 参数的映射（design D5）
        oneToOne: cardinality === 'oneToOne' ? Build2OneToOne.Y : Build2OneToOne.N,
        oneToMany: cardinality === 'oneToMany' ? Build2OneToOne.Y : Build2OneToOne.N,
        mainField,
        joinField: associateField,
      })
      unwrapSuccess(resp)
      // build 是 upsert，code 会变 —— 必须重拉，不能本地追加
      loadRelationships(mainTableCode)
    } catch (err: unknown) {
      setErrorMsg(
        err instanceof ContractError ? `保存失败：${err.info || err.code}` : '保存失败',
      )
    } finally {
      setSaving(false)
    }
  }, [
    api,
    associateField,
    associateTableCode,
    cardinality,
    loadRelationships,
    mainField,
    mainTableCode,
    saving,
    validate,
  ])

  /**
   * 「字段属性」视图的数据 —— 表格行内点「字段属性」才懒加载。
   *
   * <p>与「模型关系」完全隔离：不碰级联、不碰保存、不影响上面任何 state。
   * 走契约生成的 `list5`（`GET /displayAttribute/list`），由 orval 实例发出，
   * 不手写 axios。</p>
   */
  const openFieldAttributes = useCallback(
    async (tableCode: string) => {
      setExpandView('fields')
      setFieldTableCode(tableCode)
      setFieldLoading(true)
      setFieldErrorMsg(null)
      try {
        const resp = await api.list5({ mapClassTableCode: tableCode })
        setFieldRows(unwrapSuccess(resp) ?? [])
      } catch (err: unknown) {
        setFieldRows([])
        setFieldErrorMsg(
          err instanceof ContractError
            ? `读取字段属性失败：${err.info || err.code}`
            : '读取字段属性失败',
        )
      } finally {
        setFieldLoading(false)
      }
    },
    [api],
  )

  /** 展开区顶部回显当前针对的表名，让用户知道自己在配哪张表。 */
  const activeTableName = useMemo(() => {
    const code = expandView === 'fields' ? fieldTableCode : mainTableCode
    if (!code || code === PLACEHOLDER) {
      return '（未选择表）'
    }
    return tables.find((t) => t.code === code)?.tableName ?? code
  }, [expandView, fieldTableCode, mainTableCode, tables])

  /** code → tableName 的本地映射表，供关系列表显示被关联的表名。 */
  const tableNameByCode = useMemo(() => {
    const map = new Map<string, string>()
    for (const t of tables) {
      if (t.code) {
        map.set(t.code, t.tableName ?? t.code)
      }
    }
    return map
  }, [tables])

  return (
    <section className="relationship-panel" data-testid="relationship-panel">
      <h2>关联关系</h2>

      {/* ---------- (a) 操作提示（原型 .tip-panel，文案逐字照抄） ---------- */}
      <div className="tip-panel">
        <div className="tip-title">操作提示</div>
        <ul>
          <li>表模型之间的不能同时满足两种关系，修改关系时，请先取消原来的关系</li>
          <li>修改字段属性时请及时保存，切换表将不会保留当前表的修改</li>
        </ul>
      </div>

      {/* ---------- (b) 数据库模型表 ---------- */}
      <div className="section-title">数据库模型</div>
      {tables.length === 0 ? (
        <p className="muted" data-testid="relationship-no-tables">
          尚未解析出表 —— 先在上方导入 SQL 并点「解析」。
        </p>
      ) : (
        <div className="table-card" data-testid="relationship-model-table">
          <table className="data-table">
            <thead>
              <tr>
                <th scope="col" className="col-table">
                  表
                </th>
                <th scope="col">操作</th>
              </tr>
            </thead>
            <tbody>
              {tables.map((t) => (
                <tr
                  key={t.code ?? t.tableName}
                  className={
                    expandView === 'fields' && fieldTableCode === t.code ? 'is-active' : undefined
                  }
                  data-testid={`relationship-table-row-${t.code ?? ''}`}
                >
                  <td>
                    <span className="name">{t.tableName ?? '—'}</span>
                  </td>
                  <td>
                    <span className="row-actions">
                      <a
                        role="button"
                        tabIndex={0}
                        data-testid="relationship-open-fields"
                        onClick={() => void openFieldAttributes(t.code ?? '')}
                        onKeyDown={(e) => {
                          if (e.key === 'Enter' || e.key === ' ') {
                            e.preventDefault()
                            void openFieldAttributes(t.code ?? '')
                          }
                        }}
                      >
                        字段属性
                      </a>
                      <a
                        role="button"
                        tabIndex={0}
                        data-testid="relationship-open-relations"
                        onClick={() => setExpandView('relationship')}
                        onKeyDown={(e) => {
                          if (e.key === 'Enter' || e.key === ' ') {
                            e.preventDefault()
                            setExpandView('relationship')
                          }
                        }}
                      >
                        模型关系
                      </a>
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* ---------- (c) 展开区（原型未画，按行内两个入口切换） ---------- */}
      {tables.length === 0 ? null : (
        <div className="panel" data-testid="relationship-expand">
          <div className="panel-head">
            <span className="title">
              {expandView === 'fields' ? '字段属性' : '模型关系'}
            </span>
            <span className="right">
              <span className="edit" data-testid="relationship-expand-table">
                {activeTableName}
              </span>
            </span>
          </div>

          <div className="panel-body">
            {expandView === 'fields' ? (
              <>
                {fieldLoading ? (
                  <p className="muted" data-testid="relationship-fields-loading">
                    读取中…
                  </p>
                ) : fieldErrorMsg ? (
                  <p role="alert" className="relationship-error" data-testid="relationship-fields-error">
                    {fieldErrorMsg}
                  </p>
                ) : fieldRows.length === 0 ? (
                  <p className="muted" data-testid="relationship-fields-empty">
                    该表尚未配置字段属性 —— 可到「展示属性」页配置控件、必填与查询方式。
                  </p>
                ) : (
                  <table className="data-table" data-testid="relationship-fields-list">
                    <thead>
                      <tr>
                        <th scope="col">数据库列名</th>
                        <th scope="col">Java 属性名</th>
                        <th scope="col">控件预览</th>
                      </tr>
                    </thead>
                    <tbody>
                      {fieldRows.map((row) => {
                        const key = `${row.mapClassTableCode ?? ''}::${row.code ?? ''}`
                        const da = row.displayAttribute
                        return (
                          <tr key={key}>
                            <td>
                              <span className="name">{row.column ?? '—'}</span>
                            </td>
                            <td>{row.field ?? '—'}</td>
                            <td className="col-control">
                              {da ? (
                                <DisplayControl
                                  attribute={da}
                                  value=""
                                  onChange={() => {
                                    /* 只读预览；改属性走「展示属性」页 */
                                  }}
                                />
                              ) : (
                                '—'
                              )}
                            </td>
                          </tr>
                        )
                      })}
                    </tbody>
                  </table>
                )}
              </>
            ) : (
              <>
                {/* 两组级联（视觉规则 4「表单即视觉」：左标签 + 右输入） */}
                <div className="form-row">
                  <label className="label" htmlFor="rel-main-table">
                    主表
                  </label>
                  <select
                    id="rel-main-table"
                    className="form-input"
                    data-testid="relationship-main-table"
                    value={mainTableCode}
                    onChange={(e) => {
                      setMainTableCode(e.target.value)
                      // 级联清空下游（design D4 / delta spec Scenario 1），
                      // 但 SHALL NOT 动从表那组（Scenario 3）
                      setMainField(PLACEHOLDER)
                    }}
                  >
                    <option value={PLACEHOLDER}>请选择主表</option>
                    {tables.map((t) => (
                      <option key={t.code ?? t.tableName} value={t.code ?? ''}>
                        {t.tableName ?? '—'}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-row">
                  <label className="label" htmlFor="rel-main-field">
                    主表字段
                  </label>
                  <select
                    id="rel-main-field"
                    className="form-input"
                    data-testid="relationship-main-field"
                    value={mainField}
                    disabled={mainTableCode === PLACEHOLDER || mainFields.length === 0}
                    onChange={(e) => setMainField(e.target.value)}
                  >
                    <option value={PLACEHOLDER}>
                      {mainTableCode === PLACEHOLDER ? '请先选择主表' : '请选择主表字段'}
                    </option>
                    {mainFields.map((f) => (
                      <option key={f.code ?? f.field} value={f.field ?? ''}>
                        {f.field ?? '—'}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-row">
                  <label className="label" htmlFor="rel-associate-table">
                    从表
                  </label>
                  <select
                    id="rel-associate-table"
                    className="form-input"
                    data-testid="relationship-associate-table"
                    value={associateTableCode}
                    onChange={(e) => {
                      setAssociateTableCode(e.target.value)
                      setAssociateField(PLACEHOLDER)
                    }}
                  >
                    <option value={PLACEHOLDER}>请选择从表</option>
                    {tables.map((t) => (
                      <option key={t.code ?? t.tableName} value={t.code ?? ''}>
                        {t.tableName ?? '—'}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-row">
                  <label className="label" htmlFor="rel-associate-field">
                    从表字段
                  </label>
                  <select
                    id="rel-associate-field"
                    className="form-input"
                    data-testid="relationship-associate-field"
                    value={associateField}
                    disabled={
                      associateTableCode === PLACEHOLDER || associateFields.length === 0
                    }
                    onChange={(e) => setAssociateField(e.target.value)}
                  >
                    <option value={PLACEHOLDER}>
                      {associateTableCode === PLACEHOLDER
                        ? '请先选择从表'
                        : '请选择从表字段'}
                    </option>
                    {associateFields.map((f) => (
                      <option key={f.code ?? f.field} value={f.field ?? ''}>
                        {f.field ?? '—'}
                      </option>
                    ))}
                  </select>
                </div>

                {/* 基数：radio 而非两个 checkbox（见类注释） */}
                <div className="form-row">
                  <span className="label">基数</span>
                  <div className="radio-group" data-testid="relationship-cardinality-group">
                    <label>
                      <input
                        type="radio"
                        name="relationship-cardinality"
                        data-testid="relationship-cardinality-many"
                        checked={cardinality === 'oneToMany'}
                        onChange={() => setCardinality('oneToMany')}
                      />
                      一对多
                    </label>
                    <label>
                      <input
                        type="radio"
                        name="relationship-cardinality"
                        data-testid="relationship-cardinality-one"
                        checked={cardinality === 'oneToOne'}
                        onChange={() => setCardinality('oneToOne')}
                      />
                      一对一
                    </label>
                  </div>
                </div>

                {errorMsg ? (
                  <p role="alert" className="relationship-error" data-testid="relationship-error">
                    {errorMsg}
                  </p>
                ) : null}

                <div className="form-foot">
                  <button
                    type="button"
                    className="btn-primary"
                    data-testid="relationship-save"
                    disabled={saving}
                    onClick={() => void handleSubmit()}
                  >
                    {saving ? '保存中…' : '保存关联关系'}
                  </button>
                </div>

                <div className="section-title">已配关系（主表）</div>
                {relationships.length === 0 ? (
                  <p className="muted" data-testid="relationship-none">
                    该表尚未配关联关系。
                  </p>
                ) : (
                  <table className="data-table" data-testid="relationship-list">
                    <thead>
                      <tr>
                        <th scope="col">主表字段</th>
                        <th scope="col">从表</th>
                        <th scope="col">从表字段</th>
                        <th scope="col">基数</th>
                      </tr>
                    </thead>
                    <tbody>
                      {relationships.map((r) => (
                        <tr key={r.code ?? `${r.associateCode}-${r.mainField}`}>
                          <td>{r.mainField ?? '—'}</td>
                          {/* 映射不到时回落显示 associateCode 原值 */}
                          <td>
                            {tableNameByCode.get(r.associateCode ?? '') ?? r.associateCode ?? '—'}
                          </td>
                          <td>{r.joinField ?? '—'}</td>
                          <td>{r.isOneToMany === 'Y' ? '一对多' : '一对一'}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
              </>
            )}
          </div>
        </div>
      )}
    </section>
  )
}