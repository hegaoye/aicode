import { useCallback, useEffect, useMemo, useState } from 'react'
import { ContractError, unwrapSuccess } from '../../lib/contract-response'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import { Build2OneToOne } from '../../generated/model/build2OneToOne'
import type { MapClassTableVO } from '../../generated/model/mapClassTableVO'
import type { MapFieldColumnReadVO } from '../../generated/model/mapFieldColumnReadVO'
import type { MapRelationshipReadVO } from '../../generated/model/mapRelationshipReadVO'

/**
 * `RelationshipPanel` —— `ProjectDetailPage` 的「关联关系」区块。
 *
 * <p>代码生成配置流程的第 ⑤ 步（`2026-10-18-relationship-config`）：
 * `MapRelationship` 是「三层映射」的第三层（AGENTS §5.4），通过 `mainField` /
 * `joinField` 定义 1:1 与 1:N，生成器据此产出关联查询与导航代码。
 * **不配它，生成的代码就是一堆孤立 CRUD。**</p>
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
    <section data-testid="relationship-panel">
      <h2>关联关系</h2>
      {tables.length === 0 ? (
        <p className="muted" data-testid="relationship-no-tables">
          尚未解析出表 —— 先在上方导入 SQL 并点「解析」。
        </p>
      ) : (
        <>
          <div className="field">
            <label htmlFor="rel-main-table">主表</label>
            <select
              id="rel-main-table"
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

          <div className="field">
            <label htmlFor="rel-main-field">主表字段</label>
            <select
              id="rel-main-field"
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

          <div className="field">
            <label htmlFor="rel-associate-table">从表</label>
            <select
              id="rel-associate-table"
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

          <div className="field">
            <label htmlFor="rel-associate-field">从表字段</label>
            <select
              id="rel-associate-field"
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

          <fieldset style={{ border: 'none', padding: 0, margin: 'var(--space-3) 0' }}>
            <legend className="muted" style={{ color: 'var(--muted)' }}>
              基数
            </legend>
            <label style={{ marginRight: 'var(--space-4)' }}>
              <input
                type="radio"
                name="relationship-cardinality"
                data-testid="relationship-cardinality-many"
                checked={cardinality === 'oneToMany'}
                onChange={() => setCardinality('oneToMany')}
              />{' '}
              一对多
            </label>
            <label>
              <input
                type="radio"
                name="relationship-cardinality"
                data-testid="relationship-cardinality-one"
                checked={cardinality === 'oneToOne'}
                onChange={() => setCardinality('oneToOne')}
              />{' '}
              一对一
            </label>
          </fieldset>

          {errorMsg ? (
            <p role="alert" data-testid="relationship-error" style={{ color: 'var(--accent)' }}>
              {errorMsg}
            </p>
          ) : null}

          <button
            type="button"
            className="btn-primary"
            data-testid="relationship-save"
            disabled={saving}
            onClick={() => void handleSubmit()}
          >
            {saving ? '保存中…' : '保存关联关系'}
          </button>

          <section style={{ marginTop: 'var(--space-4)' }}>
            <h3>已配关系（主表）</h3>
            {relationships.length === 0 ? (
              <p className="muted" data-testid="relationship-none">
                该表尚未配关联关系。
              </p>
            ) : (
              <table data-testid="relationship-list">
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
                      <td>{tableNameByCode.get(r.associateCode ?? '') ?? r.associateCode ?? '—'}</td>
                      <td>{r.joinField ?? '—'}</td>
                      <td>{r.isOneToMany === 'Y' ? '一对多' : '一对一'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </section>
        </>
      )}
    </section>
  )
}