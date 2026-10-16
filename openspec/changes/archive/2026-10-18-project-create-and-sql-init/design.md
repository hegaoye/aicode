# Design

## Context

接完 M1–M5 的 7 个只读页面后，发现**代码生成流程在 React 前端上只有末端可达**：

```
① 建项目     POST /project/build              ❌ 无入口
② 导 SQL     POST /project/sql/build          ❌ 无入口
③ 触发解析   POST /project/init                ❌ 无入口
④ 配显示属性 /displayAttribute/*               ✅ 已接入（孤儿页）
⑤ 配关联关系 /project/relationship/*          ❌ 无入口
⑥ 构建       GET  /project/job/execute         ✅ 已接入
```

M1 的第一个页面 `DisplayAttributesPage` 处在第 ④ 步 —— 用户必须先手工调三个后端端点才能到达它。这是**结构性缺口**，不是"少一个页面"。

后端三个写侧端点全部**违反既有 Requirement** `api-data-object` Req 1「Controller SHALL 返回 VO 而非持久化实体」—— 它们直出裸实体。

## 实施期发现：请求契约被 `@Parameter(hidden = true)` 抹掉（前置阻塞）

**Task 0.3 核实 orval 生成物时发现的问题**：

| 端点 | orval 生成的签名 | 实际应有参数 |
|---|---|---|
| `POST /project/build` | `build4(params: { token })` | `token` + **9 个必填字段** |
| `POST /project/sql/build` | `build(options?)` | **0 个参数**（`projectCode` + `tsql` 全丢）|

**根因**：Controller 形参上的 `@Parameter(hidden = true)` 让 springdoc 忽略**整个形参**。
`ProjectController.build(@Parameter(hidden = true) Project project, String token)` 的
`Project project` 形参被隐藏 → 9 个字段全不在契约里。
`ProjectSqlController.build` 虽写了 `@Parameters` 块声明字段，但形参上的 `hidden` 把它盖掉了。

**后果**：前端照契约生成，**提交不出任何建项目请求**，只能手写 axios 绕过 orval ——
直接击穿 M4 契约驱动架构的核心前提。

**全仓波及**：`grep "@Parameter(hidden = true)"` 命中 **18 处**（`ProjectController`
3 / `ProjectSqlController` 1 / `ProjectRepositoryAccountController` 2 / `ProjectModuleController` 2 /
`ProjectFramworkController` 2 / `AccountController` 2 / `FrameworksController` 4 /
`LoginCtrl` 1 + 1 处 `String token`）。

**决定（用户拍板）**：**只修本变更要用的 3 个端点**（`POST /project/build`、
`POST /project/sql/build`、`POST /project/init`），其余 15 处另开变更。

> 一次性全修看似更彻底，但每个 Controller 的形参语义不同（`ProjectFramworkController.add`
> 收的是 `String projectStr`、`AccountController.delete` 收 `AccountVO`），需逐个核实真实
> 参数契约；而其中大部分端点前端尚未接入，修了也无从验证。分批更稳。

## Goals / Non-Goals

**Goals:**

- 打通 ① ② ③ 三步，让 ④ 变得可达；
- 3 个写侧端点返回 VO（修复既有 Requirement 违反）；
- **修本变更 3 个端点的请求契约**（去掉 `@Parameter(hidden = true)`，让 springdoc 导出真实参数）—— 否则前端无法用契约生成物提交；
- 建立写侧页面的形态与契约（后续 ⑤ 与更多写侧页面复用）。

**Non-Goals:**

- **不做多步向导**（不做 stepper 状态机）。配置是反复调的事，不是走一遍就完；向导会把"改一个字段要回第 3 步"变成痛点。形态定为「详情页内的区块」。
- 不做 ⑤ `/project/relationship/*`（另开变更）。
- **不修其余 15 处 `@Parameter(hidden = true)`**（另开变更，见上文「实施期发现」）。
- 不改后端业务逻辑（`save` / `execute` / `parse` 一行不动）。
- 不做前端表单校验库（不引 yup / zod 等新依赖）。
- 不改 `POST /project/build` 的 `token` 传参方式（现状是 query 参数，与 §6.13 红线冲突，属独立技术债）。

## Decisions

### D1：形态 —— 建项目用独立页面，其余用详情页区块

**决定**：`NewProjectPage`（`/#/project/new`）独立成页；「导 SQL + 解析」作为 `ProjectDetailPage` 的一个**区块**（不是独立 tab —— 详情页当前是单栏 dl 布局，加区块比加 tab 改动小）。

**理由**：建项目是**从零开始**（列表页无项目时的 CTA），天然独立；导 SQL 是**针对某个已有项目**的操作，挂在详情页符合"对象上下文"直觉，且详情页已有 `code`（URL 参数）可直接用。

**备选**：全部做成向导 stepper —— 否掉，理由见 Non-Goals。

### D2：`ProjectCreateVO` 8 字段 —— 覆盖「写入的」+「确认结果的」

**决定**：`code` / `name` / `englishName` / `author` / `basePackage` / `databaseType` / `state` / `createTime`（8 字段）。

**理由**：写侧返回体的用途是「让前端跳详情页 + 显示一句确认」。跳转需要 `code`；确认需要用户自己填的 6 个字段回显 + 派生的 `state` / `createTime`。

**明确排除**（实体有 29 字段）：

| 排除字段 | 理由 |
|---|---|
| `id` | 数据库自增主键，按既有约定不出现在响应中 |
| `sqlFile` / `downloadUrl` | 构建产物路径，与创建无关 |
| `isParseTable` / `isParseClass` / `isIncrement` | 纯内部状态位 |
| `isRepository` | 内部配置 |
| `relationshipAndDisplay` | 前端不知其语义的派生串 |
| `buildNumber` | 构建时才有意义 |
| 7 个关联 list | 创建时全为 null，传出去是噪音 |
| `description` / `copyright` / `phone` | 用户自己填的，详情页会再拉一次（避免重复） |

### D3：`ProjectSqlVO` 4 字段 —— 去掉 `id`

**决定**：`code` / `projectCode` / `tsql` / `state`。

`POST /project/sql/modify` **已经**返回 `ProjectSqlVO`，但该 VO 的字段与实体不一致（需核对）。本变更统一两处为同一 4 字段投影。

`id` 排除 —— 按 `api-data-object` Req 1「派生字段不出现」，且 `code`（雪花业务编码）才是对外标识。

### D4：表单字段直接对应后端 **9** 个 `Assert.hasText`

**决定**：表单 **9** 个输入框，一一对应 `ProjectController.build` 的 9 个 `Assert.hasText` 字段
（`name` / `englishName` / `author` / `phone` / `basePackage` / `copyright` / `description` /
`databaseType` / `language` —— 注意 `language` 不在 `@Parameter` 注释列出的 8 个 `required` 里，
但 Controller 与 Service **都**校验它）。**不**做前端校验库，用原生 `required` 属性 + 提交前 `if (!x) return`。

**理由**：后端已有校验，前端加校验是**重复实现**且易漂移（后端加一个必填，前端不知道）。原生 `required` 够用；真正的错误码兜底在后端。

**风险与处置**：9 个字段容易漏。tasks 里加一条 —— 建项目测试断言**契约生成物的必填参数名集合 ⊆ 表单控件名集合**（反向：SHALL NOT 出现后端不接收的字段）。

### D5：`POST /project/init` 是长耗时同步调用 —— 不加 loading 转圈以外的复杂度

**决定**：`init` 触发后显示「解析中…」并禁用按钮；解析完成调 `listMapClassTable` 刷新结果。SHALL NOT 加进度条、轮询、超时。

**理由**：`execute()` 里 `createDatabase` + `parse` 跑真实 SQL，耗时取决于 SQL 复杂度（可能数秒）。`parse` 失败会抛 `ProjectException(Server_Error)` → 前端拿到 `R.failed`。

**已知局限**：`init` 是**同步阻塞**的 HTTP 请求。若 SQL 很大，用户会白屏等待。这是**后端架构问题**（应改 `@Async` + WebSocket 推进度），属独立技术债，**本变更不解决**，只在 design 标注。

### D6：`databaseType` / `language` 用下拉而非自由输入

**决定**：`databaseType` 与 `language` 用 `<select>`，选项取自后端既有约定（`databaseType` 注释写 `Mysql,Oracle...`；`language` 写 `Java,Python,Js...`）。先用保守选项集：`databaseType` = `Mysql`；`language` = `Java`。

**理由**：这两个值会被拼进生成代码的 import 语句，自由输入容易产生非法标识符。下拉从源头挡住。

**风险**：若用户要 `PostgreSQL` 就受限。**待实施时核实** `DatabaseDataTypesUtils` / 模板侧实际支持哪些值，再定选项集 —— 见 Open Questions。

### D7：修请求契约 —— 去掉 `@Parameter(hidden = true)`，改为显式 `@RequestParam` 形参

**决定**：3 个端点的形参从 `@Parameter(hidden = true) XxxEntity e` 改为**显式 `@RequestParam` 形参**，在方法体内装配实体。保持 form/query 参数绑定风格（不加 `@RequestBody`）。

**实施时发现的实际形态（比预测的更隐蔽）**：只去掉 `hidden = true` **不够**。实测 springdoc 把整个 `Project` 实体当成**一个** query 参数导出：

```yaml
- name: project          # ← 契约说"传一个叫 project 的对象"
  in: query
  required: true
  schema:
    $ref: "#/components/schemas/Project"   # ← 29 字段
- name: token
```

这比原缺陷更隐蔽：**契约看起来"有参数了"，但与运行时不一致** —— 运行时接受的是 9 个扁平参数（Spring 对无 `@RequestBody` 的实体形参按字段名绑定），契约却说要传一个叫 `project` 的对象。前端若照契约实现会直接坏掉，且**不会报错**（生成的 TS 类型仍合法）。

`@Parameters` 块（含 9 个字段声明）**没能覆盖**形参级行为 —— 方法有多个形参时 springdoc 仍按形参生成。

**故采用显式 `@RequestParam`**：契约与运行时同为 10 个扁平参数（9 业务 + `token`），二者一致。

**验收结果**：orval 生成物 `Build4Params` 字段集 ==
`{token, name, englishName, author, phone, basePackage, copyright, description, databaseType, language}`，
SHALL NOT 含 `id` / `sqlFile` / `downloadUrl` 等实体内部字段。**已实测通过**。

**理由**：
- 加 `@RequestBody` 是更 RESTful 的做法，但会**破坏兼容**（旧调用方发的是 form 参数），且与同 Controller 其他端点风格割裂；
- 显式形参让契约成为运行时行为的**准确描述**，这正是契约驱动架构的前提。

### D8：`englishName` 唯一性 —— 表单必须处理 `Exists` 错误码

**实施期核实发现**：`ProjectController.build` 只做 9 个 `Assert.hasText`（Controller 层校验），
但 `ProjectServiceImpl.save()` **另有一层校验**：

```java
Project projectLoad = projectMapper.selectOne(
    new LambdaQueryWrapper<Project>().eq(Project::getEnglishName, project.getEnglishName()));
if (projectLoad != null) {
    throw new ProjectException(BaseException.BaseExceptionEnum.Exists);
}
```

`englishName` 同时是**项目数据库名**（`createDatabase` 用 `project.getEnglishName()` 作 schema 名），
所以必须全局唯一。

**决定**：`NewProjectPage` 的错误处理 SHALL 覆盖 `Exists` 错误码，并在错误文案里点明
「项目英文名（数据库名）已被占用」。SHALL NOT 只显示笼统的「创建失败」。

**前端辅助**：表单在 `englishName` 输入框旁提示「将作为数据库 schema 名，需全局唯一」。

### D9：`databaseType` / `language` 值域 —— 无法核实，用下拉保守取值

**核实结论（诚实记录）**：
- 这两个字段在后端**只做非空校验，不校验值域** —— `grep getDatabaseType/getLanguage` 在业务逻辑里零命中；
- 建库方言来自**全局** `Setting.DefaultDatabase`（`databaseDAO.createDatabase(database, tsql, setting.getV())`），
  **与 `project.getDatabaseType()` 无关** —— 该字段实际是纯元数据；
- 合法值全集由**外部模板仓库**（gitee.com/helixin/aicode_template）决定，本地不可见，**无法核实**。

**决定**：`<select>` 保守取值 —— `databaseType` = `Mysql` / `Oracle`；`language` = `Java` / `Python` / `Js`
（即取 Controller `@Parameter` 注释里明确写出的示例值：`Mysql,Oracle...` 与 `Java,Python,Js...`）。

**明示这是猜测**：若用户需要 `PostgreSQL` 等，得先核实模板侧支持情况再扩选项。
下拉的价值在于**挡住明显非法的值**（中文、空格），而非枚举完备性。

### D10：类型级反向断言的两处陷阱（实施期实测，踩了两遍）

本变更要求「表单字段集合 SHALL 覆盖 `Build4Params` 的每个非 token 字段」。第一版写成：

```ts
const _cov: Record<FormField, true> = FIELD_META.reduce(..., {} as Record<FormField, true>)
```

**反向验证（临时删掉 `phone` 字段）→ `tsc` 零报错。** 这是「看起来有守护、实则没有」的典型。

两处独立原因，缺一不可地都要修：

| 陷阱 | 现象 | 修法 |
|---|---|---|
| **`as` 类型断言禁用检查** | `{} as Record<K, true>` 让 TS 直接相信断言 | 改用**差集判定** `const _c: MissingField extends never ? true : never = true` |
| **`: FieldMeta[]` 标注丢失字面量** | 标注让 TS 用接口类型描述数组，`(typeof FIELD_META)[number]['field']` 拿到的是**整个 `FormField`** 而非字面量联合，于是 `Exclude<FormField, FormField>` 恒为 `never` | 改用 **`satisfies FieldMeta[]`**（既校验约束又保留字面量推导） |

最终形态（反向验证：删掉 `phone` 或 `author` 均报 `error TS2322: Type 'true' is not assignable to type 'never'`）：

```ts
const FIELD_META = [ /* ... */ ] satisfies FieldMeta[]
type CoveredField = (typeof FIELD_META)[number]['field']
type MissingField = Exclude<FormField, CoveredField>
const _typeLevelCoverage: MissingField extends never ? true : never = true
```

**零运行时开销，且在 `npm run typecheck` 阶段就红**（早于测试）。已记入 `AGENTS.md §10`。

> **教训**：任何"类型级反向断言"都必须做**反向验证**。没验证过的类型断言等于没写 —— 它比没有断言更危险，因为它让人以为有守护。

### D11：`unwrapSuccess` 不能省（实施期抓到的真 bug）

`ProjectSqlPanel` 的「解析」按钮初版写成：

```ts
await api.init({ code: projectCode })   // ❌ 没有 unwrapSuccess
setParseState({ kind: 'idle' })
```

**axios 对 HTTP 200 不 reject**，而后端 `R.failed`（如建表失败 `9999`）同样是 HTTP 200 —— 于是失败被当成功，UI 照常刷新解析结果，用户看不到任何错误。

由「init 失败 SHALL 呈现可读错误」用例逼出并修复（`unwrapSuccess(resp)` 必须在 await 之后）。

> **推广**：**每个 `await api.xxx()` 之后都要有 `unwrapSuccess`**，哪怕该端点返回 `R<Void>`。`unwrapSuccess` 不校验 HTTP 状态（axios 已对 4xx/5xx reject），它校验的是**业务层**的 `R.success` / `code` —— 那是 axios 看不见的一层。

## Risks / Trade-offs

**[`init` 阻塞数秒]** → D5 已标注，界面给「解析中…」但无法真正异步化。技术债登记。

**[`databaseType` 选项集可能不全]** → D9 已给出核实结论：**无法核实**（模板在外部仓库），
改用注释示例值做保守下拉，并明示这是猜测。

**[9 个必填字段漏一个]** → D4 已给反向断言守护。

**[`ProjectDetailPage` 变复杂]** → 现有 4 组 dl + 新增区块。可接受 —— 详情页本就是"该项目的全部信息聚合"。

**[`POST /project/build` 契约可能导出 29 个参数而非 9 个]** → **已发生且形态更隐蔽**（导出成单个
`project` 对象参数，契约与运行时不一致）。D7 的备用方案（显式 `@RequestParam` 形参）已实施并
实测通过。

**[`englishName` 重复只报 `Exists`，用户不知怎么改]** → D8 已要求错误文案点明「英文名（数据库名）已被占用」，
并在输入框旁提示其含义。

## Migration Plan

单批交付（一个 feature commit）：

1. 后端：2 个新 VO（`facade`）+ 2 个 Controller 返回类型改 + `BeanUtils.copyProperties` 装配；
2. 后端：`ApiContractIT` 场景⑪（写侧 VO 投影）2 用例 + 反向验证；
3. `npm run generate` 重新生成 orval（`src/generated/model/index.ts` 须手清 stale export）；
4. 前端：`NewProjectPage` + 测试；
5. 前端：`ProjectDetailPage` 扩展「SQL 与解析」区块 + 测试；
6. 前端：`ProjectListPage` 加「新建项目」按钮 + `App.tsx` 路由；
7. 契约产物 + AGENTS.md 登记 + validate + archive。

**回滚**：单 commit revert。

## Open Questions

- **`databaseType` / `language` 的合法值全集** —— tasks 实施时核实 `DatabaseDataTypesUtils` 与模板侧，据此定下拉选项集。若 `language` 实际只支持 `Java`，下拉就只有一项（那它该不该退化成隐藏字段？—— 倾向保留 select 以显式表达选择）。
- **`init` 异步化** —— 长期应改 `@Async` + ws 推进度，但那是后端架构变更，另开变更。
- **`POST /project/build` 的 token 走 query 参数** —— 违反 §6.13「SHALL NOT 新增 token 走 URL 参数」，但该端点是既有历史。本变更**不新增**此类端点（复用既有）；彻底治理（改 Header + 观察期）属 `api-deprecation-policy` 流程范围。