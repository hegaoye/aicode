# Spec Delta

## ADDED Requirements

### Requirement: 写侧表单页面 SHALL 提交契约生成物驱动的表单并消费写侧 VO

创建 / 修改类业务页面 SHALL 提供表单提交能力，其字段 SHALL 来自契约生成物（orval 生成的参数类型）SHALL NOT 手写平行类型；提交后的响应 SHALL 按既有统一响应规则经 `unwrapSuccess` 解包消费。

表单 SHALL 覆盖后端声明的全部必填参数：后端以 `Assert.hasText` 校验的每个字段，前端 SHALL 有对应的输入控件；SHALL NOT 让用户提交后才被后端以参数为空的错误码拒绝。

提交失败时页面 SHALL 呈现可读的错误信息（沿用既有 `ContractError` → 错误气泡的链路），SHALL NOT 静默失败。提交失败时用户已填的其它字段 SHALL 保留，SHALL NOT 被清空。

提交成功后 SHALL 导航到能确认结果的页面（详情页或列表页），SHALL NOT 停留在原地让用户不知道是否成功。

> **当前所有已接入页面都是只读的** —— 本 Requirement 是该能力**第一个覆盖写侧**的场景。此前 7 个页面无一有提交能力。

#### Scenario: 表单字段集合 SHALL 覆盖后端全部必填参数

- **WHEN** 一个写侧端点声明了 N 个必填参数
- **THEN** 对应表单页面 SHALL 提供 N 个输入控件，且每个控件 SHALL 绑定到契约生成物里的对应参数名
- **AND** SHALL NOT 出现前端自有、后端不接收的参数（会污染请求体）

#### Scenario: 提交成功 SHALL 导航到可确认结果的页面

- **WHEN** 用户填完必填字段并提交，后端返回 `R.success`
- **THEN** 页面 SHALL 导航到该实体的详情页或列表页
- **AND** 新建的实体 SHALL 能在目标页面被看到

#### Scenario: 提交失败 SHALL 呈现可读错误而非静默

- **WHEN** 后端返回 `R.failed`（如必填参数为空 `9003`）
- **THEN** 页面 SHALL 呈现错误气泡，内容含后端返回的 `info` 文案
- **AND** 用户已填的其它字段 SHALL 保留，SHALL NOT 被清空