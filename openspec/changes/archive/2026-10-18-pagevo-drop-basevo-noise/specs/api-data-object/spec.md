# Spec Delta

## MODIFIED Requirements

### Requirement: 继承 BaseVO 的 VO SHALL NOT 把查询辅助字段带入响应

视图对象（VO）SHALL NOT 继承携带**查询辅助字段**的基类（如 `com.aicode.core.BaseVO` 的
`createTimeBegin` / `createTimeEnd` / `updateTimeBegin` / `updateTimeEnd`）。这些字段的语义是
"入参：按时间范围过滤"，被 Jackson 序列化进响应即构成派生字段泄漏。

**分页容器层同样 SHALL NOT 继承该基类**：`PageVO<T>` 是所有分页端点的 `data` 类型，
它一旦继承 `BaseVO`，每个分页响应都会携带这 4 个恒为 `null` 的字段。

**出参与入参是两种职责，SHALL 分别对待**：

| 位置 | 能否继承带查询辅助字段的基类 |
|---|---|
| **出参** VO / 分页容器 | **SHALL NOT** —— 查询条件在响应里恒为 null，纯噪音 |
| **入参** DTO / 查询对象 | **MAY** —— 时间范围过滤正是其用途 |

判据 SHALL 是「该类型出现在响应的 `data` 上，还是作为请求参数被绑定」，SHALL NOT 是
「它叫 VO 还是叫 DTO」—— 命名不构成职责区分。

#### Scenario: 框架列表的 VO 字段集合不含 BaseVO 查询辅助字段

- **WHEN** 评审 `GET /framework/list` 响应的 `data.records[*]` 字段集合
- **THEN** 该集合 SHALL NOT 含 `createTimeBegin` / `createTimeEnd` / `updateTimeBegin` / `updateTimeEnd`
- **AND** 对应 VO 类 SHALL NOT 声明 `extends BaseVO`

#### Scenario: 分页容器的 data SHALL NOT 含查询辅助字段

- **WHEN** 评审任何分页端点响应的 `data` 节点字段集合（如 `RPageVOProjectSummaryVO.data`）
- **THEN** 该集合 SHALL NOT 含 `createTimeBegin` / `createTimeEnd` / `updateTimeBegin` / `updateTimeEnd`
- **AND** 分页容器类型 SHALL NOT 声明 `extends BaseVO`
- **AND** 该约束 SHALL 覆盖**所有**分页端点（`/project/list` / `/account/list` /
  `/framework/list` …），SHALL NOT 只修其中一个端点

#### Scenario: 入参 DTO SHALL 仍可继承查询辅助字段基类

- **WHEN** 某类型作为**请求参数**被绑定（`@RequestBody` 或按字段绑定的实体形参），
  且其字段承载时间范围过滤
- **THEN** 该类型 **MAY** 继承携带查询辅助字段的基类 —— 那是这些字段的正当用途
- **AND** 判据 SHALL 是「出现在响应的 `data` 上」还是「作为请求参数被绑定」，
  SHALL NOT 是「叫 VO 还是叫 DTO」

#### Scenario: 已存在继承 BaseVO 的 VO SHALL 在后续改造中被替换而非沿用

- **WHEN** 某个继承 `BaseVO` 的 VO 所在端点因其它理由被改造（如 M5-A1 的密码泄漏）