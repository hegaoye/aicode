# api-data-object Capability

## Purpose

定义 aicode 接口出参的数据对象契约：Controller SHALL 返回视图对象（VO）而非持久化实体，VO SHALL NOT 序列化不参与持久化的派生字段与嵌套关联对象。元驱动表单所需的展示属性 SHALL 完整出现在 VO 中，使接口响应恰好等于前端渲染所需的最小数据集。

本能力的存在是因为接口响应一旦被导出为契约文件（见 `api-contract`），实体中的一切都会成为契约的一部分 —— 包括本不该外泄的派生字段与对象树。因此「返回 VO 而非实体」不是风格偏好，而是**契约质量的决定性因素**。

## Requirements

### Requirement: Controller SHALL 返回 VO 而非持久化实体

系统 SHALL 以视图对象作为接口出参的类型，SHALL NOT 直接序列化持久化实体（Entity / PO）。视图对象 SHALL 按前端消费场景裁剪字段，SHALL NOT 是实体的字段全集拷贝。

#### Scenario: 列表接口出参为 VO

- **WHEN** 某列表接口需要返回数据库中的多行记录
- **THEN** 其出参类型 SHALL 为视图对象列表，SHALL NOT 为实体列表

#### Scenario: VO 字段是前端实际消费集的子集

- **WHEN** 某视图对象完成定义
- **THEN** 其字段集合 SHALL 不多于前端渲染所需的字段，SHALL NOT 等于实体的全部持久化字段

### Requirement: VO SHALL NOT 序列化派生字段与嵌套关联对象

视图对象 SHALL NOT 包含以下两类成员：不参与持久化的**计算型 / 派生字段**（由其他字段推导得出、或仅供服务端内部使用的标记位），以及**其他实体的嵌套引用**（可能进一步引发递归与过度取数）。

若前端确有需要某类字段，SHALL 以专用的、语义明确的投影形式提供，SHALL NOT 直接内嵌实体。

#### Scenario: 派生字段不出现在响应中

- **WHEN** 实体上存在由其他字段推导得到的标记位（如「是否为日期类型」「是否主键」的布尔标记）
- **THEN** 该标记位 SHALL NOT 出现在出参中，SHALL NOT 令前端误以为它是需要处理的数据

#### Scenario: 无嵌套实体对象树

- **WHEN** 出参中需要表达关联关系
- **THEN** 该关系 SHALL 以标量字段或专用投影对象表达，SHALL NOT 内嵌另一个实体，SHALL NOT 形成对象树

#### Scenario: 避免递归与过度取数

- **WHEN** 某实体引用了其他实体，且后者又引用了更多实体
- **THEN** 该引用链 SHALL NOT 出现在响应中，SHALL NOT 造成序列化深度不受控或单次查询返回远超前端所需的数据量

### Requirement: 元驱动表单所需的展示属性 SHALL 完整出现在 VO 中

当接口服务于**元数据驱动的动态表单**时，出参 SHALL 包含渲染控件所需的全部属性：控件类型、是否必填、查询匹配方式、校验提示、显示顺序、以及在列表页与详情页的可见性。出参 SHALL NOT 要求前端回查第二个端点才能完成渲染。

#### Scenario: 单次请求即可完成表单渲染

- **WHEN** 前端需要按后端配置渲染一个动态表单
- **THEN** 单次接口响应 SHALL 包含渲染所需的全部属性，SHALL NOT 需要额外请求补全

#### Scenario: 控件类型可直接决定渲染组件

- **WHEN** 出参中包含控件类型属性
- **THEN** 前端 SHALL 能据此选择渲染组件，SHALL NOT 需要硬编码字段名到控件的映射

#### Scenario: 展示属性的嵌套表达保持扁平可读

- **WHEN** 展示属性以嵌套对象形式出现在出参中
- **THEN** 其字段名与语义 SHALL 与前端配置界面的表述一致，SHALL NOT 因实体内部命名而歧义

### Requirement: Project 详情页与列表页 SHALL 用不同 VO 投影

`Project` 实体的 22 个持久化字段对应到前端两类页面（列表 + 详情）时 SHALL 拆为两种 VO，其字段集合 SHALL 分别反映两类页面的实际消费场景，不应是实体的全集拷贝。同一份 VO 不应同时给两种页面消费。

具体清单（与 M3-C 实施对齐）：

- **`ProjectSummaryVO`**（列表页消费集合；详见 list 页 `ProjectListPage`）：`code` / `name` / `englishName` / `state` / `createTime`，共 5 字段
- **`ProjectDetailVO`**（详情页消费集合；详见 detail 页 `ProjectDetailPage`）：`code` / `name` / `englishName` / `description` / `copyright` / `author` / `phone` / `basePackage` / `buildNumber` / `createTime` / `updateTime` / `accountCode`，共 12 字段

列表 VO 字段数量 SHALL NOT 大于 5（详情 VO 不大 12）；两类 VO 字段集合 SHALL NOT 完全相等（语义不同）。

> **后续变更原则**：每加入一个对应列表 / 详情消费场景的新端点，须重新审视该 VO 是否仍满足本 Rule。新增字段须先有前端消费需求作为依据。

#### Scenario: Project 列表接口的 records 元素 SHALL 是 ProjectSummaryVO 而非 Project 实体全字段

- **WHEN** `/project/list` 端点的 `data.records[]` 元素被导出契约 schema 检查
- **THEN** 元素 schema SHALL 是 `ProjectSummaryVO`（5 字段），SHALL NOT 包含实体的全 22 字段
- **AND** 类型为 `ProjectSummaryVO` 的字段 SHALL NOT 等于项目详情页消费的 `ProjectDetailVO` 的字段集合

#### Scenario: Project 单条详情接口的元素 SHALL 是 ProjectDetailVO 而非 Project 实体全字段

- **WHEN** `/project/load/code/{code}` 端点的 `data` 元素被导出契约 schema 检查
- **THEN** 元素 schema SHALL 是 `ProjectDetailVO`（12 字段），SHALL NOT 包含实体的全 22 字段
- **AND** 该 schema SHALL NOT 与 `ProjectSummaryVO` schema 完全一致（语义不同：列表 vs 详情）

### Requirement: VO 字段裁切反向验证 —— 列表与详情 VO SHALL NOT 退化成全字段

当新加入端点或修改字段时 SHALL NOT 退化为"返 Project 实体全集"。反向验证 SHALL 钉死这一约束。

#### Scenario: 列表 VO 字段集合 SHALL NOT 等于 Project 实体 22 字段全集

- **WHEN** 评审或回归测试 `ProjectSummaryVO` 字段集合
- **THEN** 字段集合 SHALL 与 `Project` 实体的 22 字段全集合做集合差——`ProjectSummaryVO` SHALL 不包含 `description` `databaseType` `language` `copyright` `author` `phone` `basePackage` `sqlFile` `downloadUrl` `buildNumber` `isRepository` `isParseTable` `isParseClass` `isIncrement` `updateTime` `accountCode`（除已有的 `code` `name` `englishName` `state` `createTime` 之外）
- **AND** 该断言 SHALL 在前端生成物 `aicode-web/src/generated/model/projectSummaryVO.ts` 自动从 orval 取中验证

#### Scenario: 详情 VO 字段集合 SHALL NOT 等于 Project 实体 22 字段全集

- **WHEN** 评审或回归测试 `ProjectDetailVO` 字段集合
- **THEN** 字段集合 SHALL 与 `Project` 实体的 22 字段全集合做集合差——`ProjectDetailVO` SHALL 不包含 `databaseType` `language` `state` `sqlFile` `downloadUrl` `isRepository` `isParseTable` `isParseClass` `isIncrement`（除详情页消费的 12 字段外）
- **AND** 该断言 SHALL 在 orval 生成物中验证

### Requirement: 分页列表接口 SHALL 使用 PageVO<XxxVO> 而非 Page<Object>

涉及分页返回的列表接口 SHALL 以 `PageVO<T>` 作为出参的元素容器（`data` 字段即是该 `PageVO<T>`），其元素 `T` SHALL 为某一 VO 类型，SHALL NOT 为 `Object` / `unknown`。该约束与本能力已有的"列表接口出参为 VO"Scenario 互补，专门收紧分页形态，避免 `voList: unknown` 的契约漏出。

#### Scenario: 分页列表接口的 data 元素带真实类型

- **WHEN** 某分页列表接口（如 `GET /project/list`）返回
- **THEN** 契约 schema 中 `data.records` 的元素类型 SHALL 为该接口对应的 VO 类型（如 `ProjectVO`），SHALL NOT 为 `{}` 或 `object` 之类的无约束类型

#### Scenario: 端点字段被改名前端编译失败依然成立

- **WHEN** 后端修改了某分页列表接口的响应字段（并按 `api-contract-artifact` 流程同步刷新了入库契约与 orval 生成物），而前端消费该端点的代码仍按旧字段名读取
- **THEN** `npm run typecheck` SHALL 失败，错误 SHALL 指向被改名的字段，SHALL NOT 让"由 `unknown` 兜底而跳过类型检查"成为绕过路径

#### Scenario: 接口响应不包含 Page<object> 这种不确定结果集

- **WHEN** 评审或测试任何分页列表接口
- **THEN** 导出契约中 SHALL NOT 出现 `Page` 类型本身以未指定 `voList` 元素类型的形式被引用；任何 Page 实例 SHALL 通过 `PageVO<XxxVO>` 出现

### Requirement: 凭据字段 SHALL NOT 出现在任何接口响应中

凡持久化实体（Entity / PO）中的**凭据类字段**——口令散列、Git 仓库密码、访问令牌——SHALL NOT 被序列化进任何接口响应；出参视图对象（VO）SHALL NOT 声明此类字段。本约束**跨领域适用**，不限账户域：任何领域的实体若含凭据列，其 VO 投影 SHALL 一并剔除。

> 该约束由 M3-D 在 `AccountVO.password` 上的单点防护上升为通用约束，M5-A1 在 `Frameworks.password`（Git 仓库密码）上第二次落地。

#### Scenario: Git 仓库密码不随框架列表/详情接口泄漏

- **WHEN** 客户端请求 `GET /framework/list` 或 `GET /framework/load?code=`（框架技术池，含模板仓库 Git 凭据）
- **THEN** 响应的 `data` 节点（列表时为 `data.records[*]`，详情时为 `data` 本身）SHALL NOT 含 `password` 键
- **AND** 契约 schema 中对应的 VO SHALL NOT 声明 `password` 属性

#### Scenario: 凭据字段不出现在前端生成类型中

- **WHEN** orval 从入库契约生成前端类型
- **THEN** 生成物 SHALL NOT 含任何凭据字段名（`password` / `secret` / `token` 等）

#### Scenario: 反向验证：以实体直出接口 SHALL 立即暴露凭据并使断言失败

- **WHEN** 临时把某接口的出参类型从 VO 改回持久化实体（破坏本约束）
- **THEN** 对应的契约守护断言 SHALL 失败，错误信息 SHALL 明确指出 `password` 是多余字段

### Requirement: 继承 BaseVO 的 VO SHALL NOT 把查询辅助字段带入响应

视图对象（VO）SHALL NOT 继承携带**查询辅助字段**的基类（如 `com.aicode.core.BaseVO` 的 `createTimeBegin` / `createTimeEnd` / `updateTimeBegin` / `updateTimeEnd`）。这些字段的语义是"入参：按时间范围过滤"，被 Jackson 序列化进响应即构成派生字段泄漏。

本约束针对**列表元素 VO**（如 `records[*]` 的元素类型）。**分页容器**（`PageVO<T>`）当前也继承 `BaseVO`，导致所有分页端点的 `data` 层携带这 4 个字段 —— 该层属全仓共性问题，彻底修法应另开变更。

#### Scenario: 框架列表的 VO 字段集合不含 BaseVO 查询辅助字段

- **WHEN** 评审 `GET /framework/list` 响应的 `data.records[*]` 字段集合
- **THEN** 该集合 SHALL NOT 含 `createTimeBegin` / `createTimeEnd` / `updateTimeBegin` / `updateTimeEnd`
- **AND** 对应 VO 类 SHALL NOT 声明 `extends BaseVO`

#### Scenario: 已存在继承 BaseVO 的 VO SHALL 在后续改造中被替换而非沿用

- **WHEN** 某个继承 `BaseVO` 的 VO 所在端点因其它理由被改造（如 M5-A1 的密码泄漏）
- **THEN** 该 VO SHALL 被替换为不继承 `BaseVO` 的等价 VO，SHALL NOT 沿用原类（避免"反正已经继承了，改造时忘了这 4 个字段"）
