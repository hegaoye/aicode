import { useCallback, useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ContractError, unwrapSuccess } from '../../lib/contract-response'
import { getToken } from '../../lib/auth/token-store'
import { getAicodeRESTfulAPIs } from '../../generated/api'
import type { Build4Params } from '../../generated/model/build4Params'

/**
 * `NewProjectPage` —— 本仓**第一个写侧页面**（2026-10-18-project-create-and-sql-init），
 * 视觉与布局对齐 OpenDesign 原型 `new-project.html`（五步向导的第 1 步）。
 *
 * 在此之前 7 个页面全是只读的：用户能看项目、看框架、配显示属性，却没法建项目。
 * 本页补上流程的第一环，让「建项目 → 导 SQL → 解析 → 配显示属性 → 构建」链条打通。
 *
 * 数据流：
 *   1. 表单 9 个字段对应 `ProjectController.build` 的 9 个 `@RequestParam`
 *      （`name` / `englishName` / `author` / `phone` / `basePackage` /
 *      `copyright` / `description` / `databaseType` / `language`）；
 *   2. `getAicodeRESTfulAPIs().build4(params)` 拿到 `AxiosResponse<RProjectCreateVO>`；
 *   3. `unwrapSuccess` 拆出 `ProjectCreateVO`；失败抛 `ContractError` → 错误气泡；
 *   4. 成功后 `navigate('/project/' + data.code)` 跳详情页。
 *
 * 字段清单不是手写的：与 `Build4Params`（orval 生成）一一对应，测试用集合断言钉死 ——
 * 后端加一个必填而前端漏了，该用例立刻红。
 *
 * 校验策略见 design D4：后端已有 9 个 `Assert.hasText`，前端不再重复实现一套
 * 校验库，只用原生 `required` 属性做最低限度提示（浏览器自会把焦点落到第一个
 * 不合法控件上），真实拦截在后端。原型 `new-project.html` 那套
 * `enName` / `basePkg` 正则是**纯前端装饰性演示**（它连字段名都与契约不同），
 * 照搬会引入第二套真相源，故不移植 —— 视觉还原不等于逻辑还原。
 *
 * 鉴权：本路由被 `<RequireAuth>` 包裹。`token` 仍作为显式参数传入
 * （后端现状是 query 参数，见 design 的 Open Questions），Header 由
 * axios 拦截器另行注入。
 */

/** 与 `Build4Params` 对齐的可编辑字段（`token` 由页面自动取，不进表单）。 */
type FormField = Exclude<keyof Build4Params, 'token'>

interface FieldMeta {
  field: FormField
  label: string
  type?: 'text' | 'textarea'
  options?: readonly string[]
  /** label 下方的灰色补充说明，顺序即显示顺序；逐字取自原型 */
  hints?: readonly string[]
  placeholder?: string
}

/**
 * 字段元数据：`databaseType` / `language` 用下拉（D9），其余自由输入。
 *
 * **下拉选项是保守取值，不是穷举**：这两个字段后端只校验非空、不校验值域，
 * 实际取值由外部模板仓库（gitee）决定，本地不可见。D9 记录了无法核实的结论。
 * 下拉的价值在于挡住明显非法的值（中文、空格），而非枚举完备性。
 *
 * 原型把这两项画成 `.radio-group`，但契约测试把控件形态钉死为 `<select>`
 * （`tagName === 'SELECT'`，见 index.test.tsx）—— 下拉比单选更省横向空间，
 * 且能挡住自由输入，故沿用下拉。取值沿用现有保守集合，不因视觉还原而扩到原型值域。
 *
 * 字段顺序逐行对齐原型（项目名 → 项目英文名 → 数据库类型 → 项目版权文字信息 →
 * 代码作者名 → 代码作者联系方式 → 项目语言 → 项目基础包名 → 项目描述），
 * 与 `Build4Params` 的声明顺序无关：请求参数按名提交，顺序不影响契约。
 */
const FIELD_META = [
  {
    field: 'name',
    label: '项目名',
    placeholder: '请填写项目名',
    hints: ['中文 / 英文皆可，将显示在工作台列表'],
  },
  {
    field: 'englishName',
    label: '项目英文名',
    placeholder: '请填写项目英文名（与数据库一致）',
    // 第二条是行为契约（D8）而非原型文案：`englishName` 同时是数据库 schema 名，
    // 必须全局唯一 —— 后端 ProjectServiceImpl.save() 有重复校验，重了抛 Exists。
    // 用户不知道这个约束就无从下手，故与原型的命名建议并列展示。
    hints: ['建议全小写、单词用短横线分隔', '将作为数据库 schema 名，需全局唯一'],
  },
  {
    field: 'databaseType',
    label: '数据库类型',
    options: ['Mysql', 'Oracle'],
  },
  {
    field: 'copyright',
    label: '项目版权文字信息',
    type: 'textarea',
    placeholder: '请填写项目版权文字信息',
    hints: ['会作为头部注释写入每个生成文件'],
  },
  { field: 'author', label: '代码作者名', placeholder: '请填写代码作者名' },
  {
    field: 'phone',
    label: '代码作者联系方式',
    placeholder: '请填写代码作者联系方式',
    hints: ['邮箱 / 工号 / IM 均可'],
  },
  {
    field: 'language',
    label: '项目语言',
    options: ['Java', 'Python', 'Js'],
  },
  {
    field: 'basePackage',
    label: '项目基础包名',
    placeholder: '请填写项目基础包名',
    hints: ['结尾不要带「.」'],
  },
  {
    field: 'description',
    label: '项目描述',
    type: 'textarea',
    placeholder: '请填写项目描述',
    hints: ['仅用于本次生成任务，不进入代码'],
  },
] satisfies FieldMeta[]

/**
 * 类型级反向断言：`FIELD_META` SHALL 覆盖 `Build4Params` 的每个非 token 字段。
 *
 * <p><b>为什么不用 `as Record<FormField, true>`</b>：类型断言会**禁用**检查 ——
 * 写 `{} as Record<K, true>` 时 TS 直接相信断言，漏字段也照样通过（实测：删掉
 * `phone` 后 `tsc` 零报错）。这是"看起来有守护、实则没有"的典型陷阱。</p>
 *
 * <p>改用「差集必须为 never」的写法：`Exclude<FormField, CoveredField>` 在
 * `FIELD_META` 漏字段时不再是 `never`，于是 `true` 无法赋给它 → `tsc` 报错。
 * 零运行时开销、无需重复维护字段清单。</p>
 *
 * <p><b>另一处必需的前提</b>：`FIELD_META` 用 `satisfies FieldMeta[]` 而**不是**
 * `: FieldMeta[]` 标注。显式标注会让 TS 用 `FieldMeta` 而非字面量联合描述数组，
 * 于是 `(typeof FIELD_META)[number]['field']` 拿到的是 `FormField` 整体，
 * `Exclude<FormField, FormField>` 恒为 `never` —— 断言静默失效（同样实测踩过）。
 * `satisfies` 既校验约束又保留字面量推导。</p>
 *
 * <p>触发路径：后端加一个必填参数 → orval 重新生成 → `Build4Params` 多一个键 →
 * `FormField` 多一项 → 此处类型不匹配 → `npm run typecheck` 阶段就红。</p>
 */
type CoveredField = (typeof FIELD_META)[number]['field']
type MissingField = Exclude<FormField, CoveredField>
// 若无遗漏，MissingField 为 never，true 可赋值；有遗漏则赋值失败。
const _typeLevelCoverage: MissingField extends never ? true : never = true
void _typeLevelCoverage

/** 五步向导的步骤条 —— 逐字取自原型 `new-project.html`。 */
const WIZARD_STEPS = [
  { num: 1, label: '项目信息' },
  { num: 2, label: 'sql 信息' },
  { num: 3, label: '数据管理' },
  { num: 4, label: '技术选定' },
  { num: 5, label: '仓库信息' },
] as const

/** 操作提示面板的 4 条 —— 逐字取自原型。 */
const OPERATION_TIPS = [
  '项目英文名将作为数据库名使用，请遵循命名规范避免失败',
  '版权文字将注释在文件头部',
  '项目基础包名范例：com.rzhk 的格式，结尾不要有「.」点出现',
  '项目描述仅描述生成任务内容，不会参与代码生成任务',
] as const

/** 初始值：全部空串，`select` 的首项由浏览器自动选中。 */
function emptyForm(): Record<FormField, string> {
  return FIELD_META.reduce(
    (acc, meta) => {
      acc[meta.field] = meta.options?.[0] ?? ''
      return acc
    },
    {} as Record<FormField, string>,
  )
}

export default function NewProjectPage() {
  const navigate = useNavigate()
  // 全局 axios 实例在 orval 生成物内部使用即可；每次渲染创建一份 api 是廉价的
  const api = useMemo(() => getAicodeRESTfulAPIs(), [])
  const [form, setForm] = useState<Record<FormField, string>>(emptyForm)
  const [errorMsg, setErrorMsg] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const handleChange = useCallback((field: FormField, value: string) => {
    setForm((prev) => ({ ...prev, [field]: value }))
  }, [])

  const handleSubmit = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault()
      if (submitting) {
        return
      }
      setSubmitting(true)
      setErrorMsg(null)
      try {
        const resp = await api.build4({
          ...form,
          token: getToken() ?? '',
        })
        const created = unwrapSuccess(resp)
        // 跳详情页确认结果 —— 写侧页面不该让用户停在原地猜成功没有
        navigate(`/project/${created.code ?? ''}`)
      } catch (err: unknown) {
        if (err instanceof ContractError) {
          // englishName 重复时后端抛 Exists；该文案必须点明原因，
          // 否则用户不知道该改哪个字段（design D8）
          setErrorMsg(
            err.code === '9005'
              ? `项目英文名（数据库名）已被占用：${err.info}`
              : err.info || err.code,
          )
        } else {
          setErrorMsg(String(err))
        }
      } finally {
        setSubmitting(false)
        // 失败时表单内容保留在 state 里，不清空 —— 用户只需改出错的那一项
      }
    },
    [api, form, navigate, submitting],
  )

  return (
    // 原型本页 --container 为 920px（表单流页面刻意收窄），用 .page-narrow 局部覆盖
    <div className="page-narrow">
      <div className="container">
        {/* 五步向导：只有第 1 步可达（步骤 2~5 的页面尚不存在，故用 <span> 而非
            可点元素 —— 不做「看着能点、点了没反应」的死交互）。 */}
        <ol className="stepper" aria-label="新建项目 5 步">
          {WIZARD_STEPS.map((step) => (
            <li key={step.num} className={step.num === 1 ? 'step active' : 'step'}>
              {step.num === 1 ? (
                <Link className="step-link" to="/project/new">
                  <span className="num">{step.num}</span>
                  <span className="label">{step.label}</span>
                </Link>
              ) : (
                <span className="step-link">
                  <span className="num">{step.num}</span>
                  <span className="label">{step.label}</span>
                </span>
              )}
            </li>
          ))}
        </ol>

        {/* 操作提示 —— 视觉规则 1「单红预算」里的一格 */}
        <div className="tip-panel">
          <div className="tip-title">操作提示</div>
          <ul>
            {OPERATION_TIPS.map((tip) => (
              <li key={tip}>{tip}</li>
            ))}
          </ul>
        </div>

        <form className="form-section" onSubmit={handleSubmit} data-testid="new-project-form">
          {FIELD_META.map((meta) => {
            const inputId = `new-project-${meta.field}`
            return (
              <div className="form-row" key={meta.field}>
                <label className="label" htmlFor={inputId}>
                  {/* 9 个字段全部必填（后端逐一 Assert.hasText），故每行都带星号 */}
                  <span className="req" aria-hidden="true">
                    *
                  </span>
                  {meta.label}
                  {meta.hints?.map((hint) => (
                    <span className="hint" key={hint}>
                      {hint}
                    </span>
                  ))}
                </label>
                <div className="field">
                  {meta.options ? (
                    <select
                      className="form-input form-select"
                      id={inputId}
                      name={meta.field}
                      data-testid={`${inputId}-input`}
                      required
                      value={form[meta.field]}
                      onChange={(e) => handleChange(meta.field, e.target.value)}
                    >
                      {meta.options.map((opt) => (
                        <option key={opt} value={opt}>
                          {opt}
                        </option>
                      ))}
                    </select>
                  ) : meta.type === 'textarea' ? (
                    <textarea
                      className="form-textarea"
                      id={inputId}
                      name={meta.field}
                      data-testid={`${inputId}-input`}
                      required
                      placeholder={meta.placeholder}
                      value={form[meta.field]}
                      onChange={(e) => handleChange(meta.field, e.target.value)}
                    />
                  ) : (
                    <input
                      className="form-input"
                      id={inputId}
                      name={meta.field}
                      type="text"
                      data-testid={`${inputId}-input`}
                      required
                      placeholder={meta.placeholder}
                      value={form[meta.field]}
                      onChange={(e) => handleChange(meta.field, e.target.value)}
                    />
                  )}
                </div>
              </div>
            )
          })}

          {/* 校验在后端，错误是整表单级的；放在 .form-foot 正上方与主按钮同轴 */}
          {errorMsg ? (
            <p className="form-error" role="alert" data-testid="new-project-error">
              {errorMsg}
            </p>
          ) : null}

          <div className="form-foot">
            <button
              className="btn-primary"
              type="submit"
              data-testid="new-project-submit"
              disabled={submitting}
            >
              {submitting ? '保存中…' : '保存并进入下一步 →'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}