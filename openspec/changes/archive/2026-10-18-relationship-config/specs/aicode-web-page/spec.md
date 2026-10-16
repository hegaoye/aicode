# Spec Delta

## ADDED Requirements

### Requirement: 多级联动的写侧表单 SHALL 按依赖顺序加载候选并可重置下游选择

存在**级联依赖**的写侧表单（某下拉的可选项取决于另一处的选择）SHALL 按依赖顺序加载候选：上游选择变化后，SHALL 重新拉取下游候选项；SHALL NOT 在上游尚未选定时就加载全部下游候选，也 SHALL NOT 在上游变化后保留属于旧上游的下游选择。

典型场景：关联关系配置需要「主表 → 主表字段」与「从表 → 从表字段」两组级联，且两组互相独立。

切换上游选择时，SHALL 清空依赖它的下游选择（连同该下游自身依赖的更深层选择），SHALL NOT 留下"字段 A 属于旧表、但配着新表"这种不一致的组合。SHALL 在用户可见的位置表达这一点（如提示「请先选择表」），SHALL NOT 静默禁用而不给理由。

提交时 SHALL 校验全部必填项已选，SHALL NOT 依赖后端报错来提示用户遗漏。

> 该 Requirement 是「写侧表单页面 SHALL 提交契约生成物驱动的表单并消费写侧 VO」（`2026-10-18-project-create-and-sql-init` 落地）的**联动加强形态** —— 前者覆盖字段集合与提交，本条覆盖字段之间的依赖。

#### Scenario: 上游变化 SHALL 清空下游选择而非保留旧值

- **WHEN** 用户已选定「主表 A」并选好了主字段，随后把主表改为「主表 B」
- **THEN** 主字段选择 SHALL 被清空（回落为未选定态）
- **AND** 从字段选择 SHALL 同样被清空（若它依赖于被改动的上游）
- **AND** UI SHALL 明确表达"需要重新选择"，SHALL NOT 静默把旧值带进提交

#### Scenario: 下游候选项 SHALL 在上游选定后才加载

- **WHEN** 主表尚未选定
- **THEN** 主字段下拉 SHALL 处于空候选态（SHALL NOT 拉取全部表的字段）
- **AND** UI SHALL 给出「请先选择主表」之类的提示，SHALL NOT 让用户面对一个空白下拉而不知原因

#### Scenario: 两组级联 SHALL 互不干扰

- **WHEN** 表单有两组独立级联（主表→主字段、从表→从字段）
- **THEN** 改动主表 SHALL NOT 清空从表及从字段的选择
- **AND** 改动从表 SHALL NOT 清空主表及主字段的选择

#### Scenario: 提交前 SHALL 校验必填项已选

- **WHEN** 任一必填项未选（如未选主字段）
- **THEN** 提交 SHALL 被阻止，SHALL NOT 发出缺字段的请求
- **AND** UI SHALL 指明缺的是哪一项