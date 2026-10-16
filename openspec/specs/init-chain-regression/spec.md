# init-chain-regression

## Purpose

代码生成主链路（建项目 → 导 SQL → 逆解析 → 生成 → 打包）是核心资产，SHALL 有端到端回归测试在真实数据库上覆盖它 ——
出问题的分支往往只存在于「真实输入 + 真实执行」的组合里，而只覆盖 mock 的测试看不见它们。

## Requirements

### Requirement: 核心生成链路的端到端回归 SHALL 在真实数据库上覆盖

代码生成主链路（**建项目 → 导 SQL → 逆解析 → 生成 → 打包**，AGENTS §5.5 称其为核心资产）
SHALL 有端到端回归测试，且该测试 SHALL 在**真实数据库**上执行完整业务动作 ——
SHALL NOT 以打桩的 Mapper / Service 代替真实执行。

覆盖 SHALL 至少包含：

| 段 | 断言 |
|---|---|
| 建项目 | 项目落库，`code` / `englishName` 等业务字段可按 code 查回 |
| 导 SQL | SQL 段落库并与项目关联 |
| 逆解析 | **`listMapClassTable` 返回真实的表清单**（表名 / 类名 / kebab-case 前端命名）|
| 逆解析（列） | **`listMapFieldColumn` 返回真实的列映射**（列名 → 属性名 / 类型 / 主键标记 / 日期标记）|

**该回归 SHALL 使用真实的数据库方言能力**，包括建 schema、执行用户导入的 DDL、
查询 `INFORMATION_SCHEMA` 等 —— 这些是 mock 覆盖不到的路径。

#### Scenario: 真实导入无 COMMENT 的 DDL SHALL 能完成逆解析

- **WHEN** 用户导入的建表 SQL **不含任何 `COMMENT`**（列注释为 null）
- **THEN** 逆解析 SHALL 正常完成，SHALL NOT 抛异常
- **AND** 产出的表清单 SHALL 含该表，且该表的所有列 SHALL 出现在列映射中
- **AND** 无状态语义注释的列，其「是否状态」标记 SHALL 为 `N`

> **2026-10-18 新增的原因**：`ProjectServiceImpl.parse()` 对每列无条件调
> `StringTools.getStateOrType(column.getRemarks())`，而 `remarks` 在无 COMMENT 时为
> `null` → `Pattern.matcher(null)` → **NPE**，整个逆解析失败。该缺陷此前未暴露，
> 正是因为**没有任何测试用真实 SQL 走过这段**。它与 §7.1 记载的
> 「`MapFieldColumn` 的 `@TableField` 丢失但 215 个测试皆绿」是**同一类问题**：
> 链路只被 mock 覆盖时，真实输入的空值分支从未被触及。

#### Scenario: 逆解析失败 SHALL NOT 留下「看起来成功」的半成品

- **WHEN** 用户导入的 DDL 非法导致逆解析中途失败
- **THEN** 项目的「已解析」标记 SHALL NOT 被置为完成
- **AND** 表清单 SHALL NOT 把未完成的解析结果当作成功产出
- **AND** 失败原因 SHALL 可被观察到（供前端呈现），SHALL NOT 静默

#### Scenario: 异步逆解析的测试 SHALL 用轮询等待而非固定 sleep

- **WHEN** 端到端测试触发**异步**的逆解析端点
- **THEN** 测试 SHALL 轮询结果直到达成终态或超时
- **AND** SHALL NOT 用 `sleep(固定时长)` 充�� —— 那会让测试在慢机器上假失败、在快机器上假通过
- **AND** 超时 SHALL 给出可诊断的失败信息（含已轮询次数与最后一次观测到的状态）
