# Design

## Context

`GET /account/list` 一个方法叠了 4 个缺陷（1 个安全 + 3 个功能），同一 Controller 的
`PUT /account/modify` 与 `DELETE /account/delete` 各有 1 个。

前端**未消费**这些端点（实测确认），所以是修的最佳时机：无兼容负担，也无从验证破坏。

## Goals / Non-Goals

**Goals:**

- 堵住 `password`（MD5，无盐）随列表响应泄漏；
- `/account/list` 包入 `R`、补入参兜底、回填三个分页字段；
- `/account/modify` 包入 `R`；
- `/account/delete` 的形参不再被 hidden，契约里能拿到 `id` / `code`。

**Non-Goals:**

- **不删 `AccountVO`** —— 它是 `modify` / `delete` 的**入参**载体，携带 password 是恰当的
  （修改密码、定位账户确需）。只是**不再作为出参**。见 D1。
- 不改 `delete` 的实现逻辑（`remove(eq(getId, ...))`）—— 只让它在契约里可见。
- 不改密码存储方式（MD5 → BCrypt/Argon2 是独立的高风险变更，见 AGENTS §11.1）。
- 不做账户管理前端页面（本变更零前端改动）。

## Decisions

### D1：`AccountVO` 保留（入参），出参改用 `AccountPublicVO`

**决定**：`list` 的出参类型从 `PageVO<AccountVO>` 改为 `R<PageVO<AccountPublicVO>>`。
`AccountVO` 类本身**保留**。

**理由**：`AccountVO` 含 `password`，但它作为 `PUT /account/modify`（改密码）与
`DELETE /account/delete`（定位账户）的**入参**是恰当的。问题只在于它**同时**被用作出参。

删掉 `AccountVO` 会连带改两个入参端点的签名 —— 那是"为了出参干净而动入参"，
属于把两个职责混在一起修。正确做法是**职责分离**：入参载体留，出参换读投影。

`AccountPublicVO`（3 字段 `id` / `code` / `account`）是 M3-D 刚落地的，
`AccountController#loadByCode` 已在用 —— **复用而非新建**，满足"改一处漏另一处"的防线要求
（delta spec 新增 Scenario 要求「该读投影 SHALL 被同一个 Controller 的所有出参端点共用」）。

### D2：省掉 `AccountPageVO` 这个中转层

**现状**：

```java
List<AccountPageVO> voList = JSON.parseArray(JSON.toJSONString(page.getRecords()), AccountPageVO.class);
vo.setRecords(JSON.parseArray(JSON.toJSONString(voList), AccountVO.class));
//                                          ↑ AccountVO 含 password
```

`AccountPageVO` 的字段与 `AccountVO` **完全相同**（`id` / `code` / `account` / `password`），
这一层转换**没有裁切任何东西**，只是白白多做一次 JSON 往返。

**决定**：直接 `entity → AccountPublicVO`，删掉中转。

**理由**：两层转换里唯一"裁 password"的一步是第二步，但它裁的是**已经含 password 的中间对象** ——
password 在中间对象里存在过。若将来有人改成复用 `AccountPageVO`，泄漏会立刻回来。
删掉中间层从结构上消除这个可能。

**为何保留 JSON 往返而非手工 setXxx**：`TemplateDataExpansionTest` 钉死过「模板变量展开必须用
fastjson2 往返而非 hutool `BeanUtil.beanToMap`」（getter 名 vs 字段名键集差异）。
本仓对"实体 → 契约类型"的既定做法是 fastjson2 往返 + 显式 VO 定义，与
`AccountController` 现有代码、`DisplayAttributeController` 一致。**跟随既有约定**。

### D3：入参兜底与 `0960cc0` 完全一致

```java
final int effectiveCurPage   = (curPage   == null || curPage   <= 0) ? 1  : curPage;
final int effectivePageSize  = (pageSize  == null || pageSize  <= 0) ? 10 : pageSize;
```

`totalPage = (int) Math.ceil((double) totalRow / effectivePageSize)` 显式算
（不用 `IPage#getPages()` —— 版本行为未测）。

**理由**：`0960cc0` 刚给 `/project/list` 做过同一套，且已被 `ApiContractIT` 场景⑦ 的两条用例钉住。
**两个分页端点的兜底口径必须一致**，否则前端两个页面行为不同。

### D4：`delete` 只去注解、不动实现

**决定**：去掉 `delete` 形参上的 `@Parameter(hidden = true)`，实现逻辑一行不动。

**理由**：契约里现在**完全没有参数**，前端无从发起删除 —— 这是真缺陷。
但该端点前端未接入，本变更只是让契约如实反映已有的 `@Parameters` 块（声明 `id` / `code`）。
实现层的 `remove(eq(getId, ...))` 在 id 为 null 时的行为属另一问题，不在本变更。

> **⚠️ 已知局限**：`delete` 的形参是 `AccountVO` 实体，Spring 按字段名绑定 query/form 参数。
> 去掉 hidden 后契约导出的是**实体的字段集合**（含 `password`），而运行时只需要 `id`。
> 更彻底的做法是改成显式 `@RequestParam String id`，但那是改签名 —— 属另开变更的范畴。
> 本次先让契约"有参数"，并在 design 标注。

### D5：契约层守护 —— 三条断言分别钉三个缺陷

| 缺陷 | 守护方式 |
|---|---|
| password 泄漏 | 实际调用 `/account/list`，断言 `data.records[]` 每个元素的字段集合 **⊆** `{id, code, account}`，且**点名不含 `password`** |
| 裸 PageVO | schema 层断言：响应顶层 SHALL ref `RPageVOAccountPublicVO` |
| 分页字段恒 0 | 实际调用，断言 `curPage` / `pageSize` / `totalPage` 与实际查询一致 |
| NPE | **实际不带参调用**，断言 `R.success` 为 true 且回退到 1 / 10 |

NPE 那条必须**实际调用**而非 schema 断言 —— 拆箱 NPE 只在运行期出现。
这与 `2026-10-18-project-list-real-pagination` 的 `project_list_fallsBackToDefaultsWhenParamsInvalid`
同一手法。

## Risks / Trade-offs

**[`AccountVO` 留在契约里（作为入参 requestBody / query 参数）仍含 `password` 字段名** → 是的，
`PUT /account/modify` 的 requestBody schema 会含 `password`（合理：改密码要传）。
**这与"响应泄漏"是两回事**：入参 schema 是给客户端构造请求用的，不含真实凭据值。
delta spec 的 Scenario「含凭据字段的入参载体 SHALL NOT 兼作出参」正是这个区分。

**[`delete` 契约导出实体的全字段（含 password）作为入参** → 见 D4 的「已知局限」。
入参 schema 含 `password` 字段名不构成泄漏（无真实值），但契约不够干净。留给后续变更。

**[测试库里的账户种子数据]** → `db/data.sql` 有 `contract-test` 账户（`21218cca77804d2ba2`）。
`/account/list` 不按账户过滤，返回**全部账户**，所以断言可直接用真实种子数据。

**[移除 `AccountPageVO` 类会不会有其它消费者]** → 已核实：全仓仅 `list` 一处使用它。
但它是 `facade` 的公开类，**SHALL NOT 本变更就删类**（facade 是契约模块，删类属破坏性变更）——
本变更只让它不再被使用，留待有清理需求时统一处理。

## Migration Plan

单批交付（一个 commit）：

1. `AccountController.list`：返回类型 + 入参兜底 + 三分页字段 + 中转层去除；
2. `AccountController.modify`：`boolean` → `R<Boolean>`；
3. `AccountController.delete`：去掉 `hidden`；
4. `ApiContractIT` 场景⑭（3 用例）+ 反向验证；
5. 契约产物 + orval 重新生成 + `AGENTS.md §11.1` 运维建议 + validate + archive。

**回滚**：单 commit revert。

## Open Questions

- **密码轮换** —— `password` 曾随 `/account/list` 的响应进入契约，可能已被前端生成物、
  代理日志、浏览器历史记下。本变更只堵住源头，**存量已落盘的 MD5 无法追溯清理**。
  运维动作（轮换全部账户密码 + 检查反向代理访问日志）登记在 `AGENTS.md §11.1`。
- **`delete` 的形参收窄为显式 `@RequestParam String id`** —— 见 D4，另开变更。
- **其余 14 处 `@Parameter(hidden = true)`** —— 本变更只修了 `/account/delete` 一个
  （因为它与另两个缺陷同属一个 Controller）。其余属 B 类，另开变更。
- **MD5 → BCrypt/Argon2** —— AGENTS §11.1 已记，是独立的高风险变更（需平滑迁移方案）。