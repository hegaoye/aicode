# Spec Delta

## MODIFIED Requirements

### Requirement: 业务页面 SHALL 把统一响应解析与控件分发抽象为可复用单元

业务页面 SHALL 使用薄封装层将 `R.success.data` 取出并把 `R.failed` 转为标准错误；展示属性 SHALL 通过统一的「控件分发器」按 `displayType` 渲染对应控件，SHALL NOT 在每个字段处用 `if/else` 重复硬编码字段名与控件的映射。

**分发器的 key SHALL 与后端 `displayType` 枚举对齐**（大小写归一后逐一匹配）：SHALL NOT 用一套与后端值域无关的"自造 key"（如 `text` / `date` 对应后端的 `Input` / `DatePicker`）—— 那样会让绝大多数枚举值永远走降级分支，而表面上"四类都已实现"。

未实现对应控件的枚举值 SHALL 渲染带**可见降级标识**的安全占位（SHALL NOT 静默失败，SHALL NOT 抛未捕获异常）。

> **本 Req 的 2026-10-18 修订说明**：原文只要求「至少覆盖 `text` / `number` / `date` /
> `select` 四类」，未要求这四个 key 与后端枚举对齐 —— 而后端 `DisplayAttributeDisplayType`
> 实际有 24 个 PascalCase 枚举（`Input` / `DatePicker` / `InputNumber` / `Select` …）。
> 实测按原文实现后，24 个枚举里**只有 `Select` 能命中**，其余 23 个全部走降级分支，
> 而"四类已实现"的表面合规掩盖了这一点。原文的漏洞在 spec，不在实现。

#### Scenario: R 的成功路径直接吐出 data

- **WHEN** 后端返回 `R` 形态的成功响应（含 `code: "0000"` 与 `data` 字段）
- **THEN** 封装层 SHALL 把 `data` 直接交给调用者，且 SHALL NOT 把 `R` 整体透传给上层组件

#### Scenario: R 的失败路径被归一化抛出

- **WHEN** 后端返回 `R.failed` 形态的响应（`success: false`）或 HTTP 层错误
- **THEN** 封装层 SHALL 抛出含 `code` 与 `info` 的错误对象，调用方 SHALL 可凭类型保护访问这两个字段

#### Scenario: 控件选择不依赖字段名硬编码

- **WHEN** 后端给一个字段新增一种 `displayType` 值
- **THEN** 前端业务页面 SHALL 通过修改控件分发器中的映射即可支持，SHALL NOT 在每个组件里出现"按字段名判断"的代码

#### Scenario: 控件分发器按 displayType 选控件

- **WHEN** 业务页面给出一个具体的 `displayType` 字符串
- **THEN** 控件分发器 SHALL 归一大小写后在映射表中查找并渲染对应控件
- **AND** 对后端 `DisplayAttributeDisplayType` 枚举中的值 SHALL 至少正确命中
  `Input`（单行文本）、`InputNumber`（数字）、`DatePicker`（日期）、`Select`（选择器）四类
- **AND** 未识别的 `displayType` SHALL 渲染为带可见降级标识的安全占位，SHALL NOT 让浏览器抛未捕获异常

#### Scenario: 控件选择不依赖字段名硬编码

- **WHEN** 后端给一个字段新增一种 `displayType` 值
- **THEN** 前端业务页面 SHALL 通过修改控件分发器中的映射即可支持，SHALL NOT 在每个组件里出现"按字段名判断"的代码

#### Scenario: 分发器 key 与后端枚举的对应关系 SHALL 可核对

- **WHEN** 评审或回归测试控件分发器
- **THEN** SHALL 存在一条断言，把**后端枚举全集**与**分发器能命中的子集**做集合比较并列出未命中项
- **AND** 断言 SHALL NOT 是"断言某个字符串能渲染"这种单点用例 —— 单点用例无法发现
  「其余 23 个枚举全部失配」这类系统性偏差
- **AND** 未命中项 SHALL 在界面上有可见的降级标识，让用户知道自己配的类型暂不支持渲染
