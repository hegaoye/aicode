# Proposal

## Why

`R` 不是泛型类 —— `private Object data = null`。结果是 springdoc 导出的契约里，每个端点的响应都只能写成 `$ref: R`，而 `R.data` 的 schema 是 `{}`（完全无类型）：

```yaml
R:
  type: object
  properties:
    info:  { type: string }
    data:  {}                      # ← 无任何结构
    code:  { type: string }
    success: { type: boolean }
```

这使「契约驱动」失去全部意义：orval 能生成的只是 `{ code: string; data: unknown; info: string; success: boolean }`，**「后端改字段名 → 前端编译失败」这条核心防漂移价值无从产生**。已实测导出的 45 处 `$ref: R` 全部如此。

该问题在规划阶段被遗漏 —— 当时只确认了 `/v3/api-docs.yaml` 可访问，没有检查其内容是否有类型信息。

## What Changes

- `R` 改为泛型 `R<T>`，`data` 字段类型为 `T`；全部静态工厂方法加 `<T>` 类型参数。
- 13 个 Controller / Service 中的 **38 个方法**改为声明具体返回类型（如 `R<List<ProjectVO>>`、`R<String>`、`R<Void>`）。
- 契约中 `R` 的 `data` 将携带真实 schema，orval 可据此生成字段级类型与 mock。

**BREAKING**：`R` 的类型签名变化会传导到全部调用方（本仓库内 13 个文件、57 个 `R.success/failed` 调用点）。由于泛型仅影响编译期，**运行期 JSON 结构完全不变** —— 这点由 `ApiContractIT` 的 44 个可达性断言与响应结构断言守护。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

- `api-contract`: 新增要求 —— 统一响应包装的 `data` SHALL 携带真实结构而非无类型占位；接口 SHALL 以泛型返回类型声明其出参形状，使跨语言契约可推导。

## Impact

- **代码**：`common/src/main/java/com/aicode/core/R.java`；12 个 Controller + `LogsSVImpl` 的 38 个方法签名；57 处 `R.success/failed` 调用点（多数无需改动，仅在方法返回类型变窄时需要类型见证）。
- **契约**：`contracts/openapi/openapi.yaml` 中 `R.data` 由 `{}` 变为真实 schema；各端点响应将出现具体的 `$ref`。
- **测试**：现有 222 个用例应全部保持通过（运行期结构不变），其中 `ApiContractIT` 的响应结构断言是本变更的主要安全网。
- **风险**：`R.success()`（无参）与 `R.failed(...)` 在方法返回 `R<Void>` 时需类型推断正确；若某方法在不同分支返回不同形态的 `data`，需统一为 `R<Object>` 或收窄该方法。
- **依赖**：本变更是 M0（契约链）的前置 —— 未完成前 orval 生成不出字段级类型。