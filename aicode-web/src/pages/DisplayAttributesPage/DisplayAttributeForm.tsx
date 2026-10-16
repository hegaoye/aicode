import { useCallback } from 'react'
import { DisplayAttributeVODisplayType } from '../../generated/model/displayAttributeVODisplayType'
import type { DisplayAttribute } from '../../generated/model/displayAttribute'
import type { DisplayAttributeDisplayType } from '../../generated/model/displayAttributeDisplayType'
import type { MapFieldColumnVO } from '../../generated/model/mapFieldColumnVO'

/**
 * `DisplayAttributeForm` —— 单个字段的展示属性编辑表单。
 *
 * <p>代码生成配置流程第 ④ 步的编辑单元（`2026-10-18-display-attribute-write`）。
 * 8 个高频字段的取舍理由见 design D5；刻意排除的 11 个字段里，
 * `displayCss` / `validateText` 是给高级用户直接写代码的，
 * `matchType` / `fieldValidationMode` 需要配套的查询表单才能体现价值。</p>
 *
 * <p><b>视觉形态</b>：原型没有「字段属性」页，本组件按基元拼装 ——
 * `.panel` + `.panel-head`（列名 + 当前显示名）+ `.panel-body` 内的
 * `.form-row`（左标签 + 右输入，视觉规则 4「表单即视觉」）。原先的
 * `<fieldset>` / `<legend>` 与行内 `style` 由此替换 —— 语义分组由面板承担。</p>
 *
 * <p><b>提交时机</b>：本组件<b>只改本地 state，不自己发请求</b> ——
 * 由页面级「保存全部」批量提交（design D4：后端端点是逐条 upsert 的批量接口，
 * 逐行提交会产生 N 个请求且中途失败无法表达"哪些成功了"）。</p>
 *
 * <p><b>后端语义的诚实边界</b>：`save` 对找不到 `mapFieldColumn` 的行是静默
 * `continue`，且<b>没有事务</b>。所以"提交成功"不等于"全部写入" ——
 * 页面 SHALL 在提交后重新拉取列表，让实际结果可见（design D4）。</p>
 */

interface Props {
  /** 该行在 `/displayAttribute/list` 响应里的数据。 */
  row: MapFieldColumnVO
  /** 当前编辑值（本地 state）。 */
  value: DisplayAttribute
  /**
   * 上报变更 —— 传 **patch** 而非完整对象。
   *
   * <p>传 patch 是为了避免 stale closure：本组件的多个控件（显示名 / 必填 / …）
   * 共用一份 `value`，若 `onChange` 收完整对象，就必须依赖闭包里的 `value` ——
   * 而同一批次内的连续两次编辑会用到同一份旧快照，第二次会把第一次的改动覆盖回去
   * （实测：`displayName` 改完被 `isRequired` 的编辑抹掉）。</p>
   *
   * <p>传 patch 后，调用方用 `prev => ...(prev)` 在<b>最新</b> state 上 merge，
   * 结构性地消除该问题。</p>
   */
  onChange: (patch: Partial<DisplayAttribute>) => void
}

/**
 * `displayType` 下拉的选项 —— **从契约生成物读，不手写副本**。
 *
 * <p>M1 时代的教训：手写副本会在后端新增枚举时静默过期。</p>
 */
const DISPLAY_TYPE_OPTIONS = Object.values(DisplayAttributeVODisplayType)

/** 入参实体侧的枚举类型（与 VO 侧是两个生成物，但取值同源）。 */
const DISPLAY_TYPE_SET = new Set<string>(DISPLAY_TYPE_OPTIONS)

/**
 * 归一化 `displayType` —— **只接受契约枚举内的值**。
 *
 * <p>选择用类型断言（`as`）会把脏数据放进去：契约若将来收窄枚举，历史脏值
 * 就会以非法字符串提交给后端。用运行时过滤既满足 TS，又真正挡住脏数据。</p>
 */
function normalizeDisplayType(raw: string): DisplayAttributeDisplayType | undefined {
  return DISPLAY_TYPE_SET.has(raw) ? (raw as DisplayAttributeDisplayType) : undefined
}

export default function DisplayAttributeForm({ row, value, onChange }: Props) {
  const set = useCallback(
    (patch: Partial<DisplayAttribute>) => {
      onChange(patch)
    },
    [onChange],
  )

  const toggle = useCallback(
    (field: string, current: string | undefined) => {
      // 后端用 Y/N 字符串表达布尔语义（见 api-data-object）
      set({ [field]: current === 'Y' ? 'N' : 'Y' } as Partial<DisplayAttribute>)
    },
    [set],
  )

  return (
    <section className="panel" data-testid={`display-attr-form-${row.code ?? row.field ?? ''}`}>
      <div className="panel-head">
        <span className="title">{row.column ?? row.field ?? '—'}</span>
        <span className="right">
          {row.displayAttribute?.displayName ? (
            <span className="muted">当前显示名：{row.displayAttribute.displayName}</span>
          ) : null}
        </span>
      </div>

      <div className="panel-body">
        <div className="form-row">
          <label className="label" htmlFor={`display-attr-displayType-${row.code}`}>
            控件类型
            <span className="hint">决定生成代码里用哪种表单控件</span>
          </label>
          <div className="field">
            <select
              id={`display-attr-displayType-${row.code}`}
              className="form-input"
              data-testid="display-attr-displayType-input"
              value={value.displayType ?? ''}
              onChange={(e) => set({ displayType: normalizeDisplayType(e.target.value) })}
            >
              <option value="">（未设置）</option>
              {DISPLAY_TYPE_OPTIONS.map((opt) => (
                <option key={opt} value={opt}>
                  {opt}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="form-row">
          <label className="label" htmlFor={`display-attr-displayName-${row.code}`}>
            显示名
          </label>
          <div className="field">
            <input
              id={`display-attr-displayName-${row.code}`}
              className="form-input"
              data-testid="display-attr-displayName-input"
              value={value.displayName ?? ''}
              onChange={(e) => set({ displayName: e.target.value })}
            />
          </div>
        </div>

        <div className="form-row">
          <label className="label" htmlFor={`display-attr-displayNo-${row.code}`}>
            显示顺序
          </label>
          <div className="field">
            <input
              id={`display-attr-displayNo-${row.code}`}
              className="form-input"
              data-testid="display-attr-displayNo-input"
              type="number"
              value={value.displayNo ?? ''}
              onChange={(e) =>
                set({ displayNo: e.target.value === '' ? undefined : Number(e.target.value) })
              }
            />
          </div>
        </div>

        <div className="form-row">
          {/* 勾选组无单一关联控件，故不写 htmlFor —— 与原型 radio-group 行同构 */}
          <label className="label">约束</label>
          <div className="field">
            <div className="radio-group attr-constraints" role="group" aria-label="约束">
              {(
                [
                  ['isRequired', '必填'],
                  ['isInsert', '可插入'],
                  ['isAllowUpdate', '可更新'],
                  ['isListPageDisplay', '列表页显示'],
                  ['isDetailPageDisplay', '详情页显示'],
                ] as const
              ).map(([field, label]) => (
                <label key={field}>
                  <input
                    type="checkbox"
                    data-testid={`display-attr-${field}-input`}
                    checked={value[field] === 'Y'}
                    onChange={() => toggle(field, value[field])}
                  />{' '}
                  {label}
                </label>
              ))}
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}