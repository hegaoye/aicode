# Design

## Context

见 `proposal.md - Why`。设计上需要处理的现状约束（实测统计）：

| 指标 | 数量 |
|---|---|
| 返回裸 `R` 的方法 | 38（跨 13 个文件） |
| 其中返回带数据的 | 31 |
| 纯 Void 返回 | 5 |
| 无 `return R.`（返回他物） | 2 |
| `R.success/failed` 调用点 | 57 |
| 契约中 `$ref: R` | 45 处 |

- `R` 现有 7 个静态工厂方法，其中 `failed(e, Object data)` 与 `success(e, Object data)` 的第二参为 `Object`，是泛型化的主要收敛点。
- `R` 是 `final`，构造器私有，由静态工厂创建。

## Goals / Non-Goals

**Goals:**

- 让契约中 `R.data` 携带真实 schema，使 orval 能生成字段级类型。
- 运行期 JSON 结构**完全不变**。

**Non-Goals:**

- 不改变 `code` / `info` / `success` 三个字段的语义或取值。
- 不改动统一响应的四键结构（`api-contract` 已有该要求）。
- 本变更**不**引入 orval / 前端工程 —— 那属 M0。

## Decisions

### D1：`R<T>` + 静态工厂带 `<T>` 类型参数

**决定**：

```java
public final class R<T> implements Serializable {
    private String info = ...;
    private T data = null;
    private String code = ...;
    private boolean success = false;

    public static <T> R<T> failed(String code, String msg) { ... }
    public static <T> R<T> failed(BaseExceptionEnum e) { ... }
    public static <T> R<T> failed(BaseExceptionEnum e, T data) { ... }
    public static <T> R<T> success(BaseExceptionEnum e, T data) { ... }
    public static <T> R<T> success(BaseExceptionEnum e) { ... }
    public static <T> R<T> success(T data) { ... }
    public static <T> R<T> success() { ... }
}
```

**理由**：由方法的**目标返回类型**驱动类型推断。`public R<List<ProjectVO>> list()` 体内的 `return R.success(voList);` 会把 `T` 推断为 `List<ProjectVO>`，无需在调用点写类型见证。这是把 57 个调用点的改动降到最小的关键 —— 大多数调用点**一行都不用改**，只有返回类型声明处要改。

**备选方案**：(a) 用 `R<Object>` 一律了事 —— 编译通过但契约里 `data` 仍是 `{}`，等于没做；(b) 强制调用点写 `R.<List<ProjectVO>>success(...)` —— 冗余且易与实际类型脱节。

### D2：`failed(...)` 保持与 `success(...)` 同构

**决定**：`failed` 的工厂同样带 `<T>`，失败分支通常返回 `null` data，因此可推断为方法声明的同一 `T`。

**理由**：一个方法体内的成功与失败分支必须能返回**同一个** `R<T>`，否则 Java 无法编译。这是把「分支形态不一致」的问题在编译期强制暴露出来的机制 —— 正符合 spec 的「分支形态不一致时显式收窄」。

### D3：`T` 的取值规则

| 实际返回 | `T` |
|---|---|
| `service.list(...)` 返回 `List<XxxVO>` | `List<XxxVO>` |
| `service.list(...)` 返回 `List<Map<String,String>>` | `List<Map<String,String>>` |
| `service.getById(...)` 返回实体或 VO | 该实体 / VO 类型 |
| `String`（token、文件内容、路径） | `String` |
| 仅 `R.success()` / `R.failed(...)`，无数据 | `Void` |
| 成功带数据、失败不带（如 `signin`、`modify`） | 数据的类型；失败分支 `T` 推断为 `null`，合法 |

**关键约束**：凡 `data` 实际类型为基本类型包装类之外的**多态集合**（如 `List<?>`），`T` 写成其声明类型，SHALL NOT 写 `Object` 敷衍 —— 否则契约无意义。

### D4：保留 `R` 的 `final` 与私有构造器

**决定**：不改可见性与构造策略。

**理由**：泛型化不需要放松封装；改动越大越难回退。

### D5：`ExceptionHandle` 的返回类型

**决定**：全局异常处理器返回 `R<Object>`（其 `data` 通常为异常信息或 null）。

**理由**：异常分支的 data 形态多样，无法收敛到单一具体类型。这是唯一允许 `Object` 的位置，且它不进正常业务路径。

## Risks / Trade-offs

**[38 处签名改动可能漏改或改错]** → 编译器是主要防线：方法体内若 `T` 推断不出声明的类型会直接编译失败。`R<Object>` 兜底处已在 D3 限定为仅异常处理器。

**[`R.success()` 无参调用在 `R<Void>` 方法中能否推断]** → `T` 在目标类型 `R<Void>` 下推断为 `Void`，`null` 合法。若编译失败则改为 `R.<Void>success()`。

**[泛型 `R` 与 `@Data` 的交互]** → Lombok 会生成基于 `T` 的 `equals/hashCode/toString`。现有断言不比较 `R` 对象本身（只比较 JSON），故无影响；若出现编译问题则把 `R` 的 `@Data` 收窄为 `@Getter/@Setter`。

**[运行期结构变化]** → 不可能：泛型被擦除，`data` 字段的运行期类型仍是 `Object`。由 `ApiContractIT` 全量守护。

## Migration Plan

单批交付：

1. `R.java` 泛型化
2. 逐 Controller 改 38 个方法签名（编译器驱动）
3. `ExceptionHandle` / `LoginInterceptor` 等返回 `R` 的横切点按 D5 / D3 处理
4. 全量回归
5. 重跑 `OpenApiExportIT`，确认 `R.data` 出现具体 schema
6. 同步 `api-contract` delta 并归档

**回滚**：单一提交，revert 即可（`R` 的泛型化与调用点改动在同一提交）。

## Open Questions

- 部分 `load(...)` 类方法可能返回聚合对象（如把 entity 与其关联列表组装），其 `T` 需逐一读方法体确定。实施时以编译器反馈 + 方法体为准，不预设。