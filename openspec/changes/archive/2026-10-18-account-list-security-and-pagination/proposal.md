# Proposal

## Why

`GET /account/list` 一个方法里叠了 **4 个缺陷**，其中一个是**安全缺陷**。前端目前未消费该端点，
所以是修它的最佳时机。

### 缺陷 0（安全）：响应泄漏所有账户的 password

契约里的响应 schema 是 `PageVOAccountVO`，而 `AccountVO` 的字段是：

```
id / code / account / password      ← 泄漏
```

密码按 `Md5.md5(password)` 存储（`LoginCtrl:54`）—— **MD5 无盐**，可离线爆破
（彩虹表 + GPU 秒级破解常见口令）。

**这是 AGENTS §11.1 记过的那类泄漏在 `/account/list` 上重演** —— `2026-10-18-framework-list-vo-contract`
（M5-A1）已修过 `/framework/list` 的同类问题，本次是同一类问题的第二次出现。

好消息：合规 VO `AccountPublicVO`（3 字段 `id` / `code` / `account`，不含 password）
是 M3-D 刚落地的，`AccountController#loadByCode` 已在用 —— **修法现成，复用它即可**。

### 缺陷 1：返回裸 `PageVO`，不在 `R` 包络内

```java
public PageVO<AccountVO> list(Integer curPage, Integer pageSize)   // ← 不是 R<...>
```

违反 `api-contract` 的既有 Requirement「受保护 REST 接口 SHALL 走 `R<T>` 包装而非裸实体出参」。
前端若接这个端点，`unwrapSuccess` 对它不工作。

### 缺陷 2：入参拆箱 NPE（已实测）

```java
IPage<Account> page = new Page<>(curPage, pageSize);   // Integer → long 自动拆箱
```

`Page(long, long)` 收 `long`，传 `Integer` 会自动拆箱。**实测**：

```
THROWS: java.lang.NullPointerException ——
        Cannot invoke "java.lang.Integer.intValue()" because "<local1>" is null
```

**不带 `curPage` / `pageSize` 访问该端点必然 500。**

### 缺陷 3：三个分页字段从不 set，恒为 0

只有 `setTotalRow` 与 `setRecords`，`curPage` / `pageSize` / `totalPage` 从不 set ——
这正是 `2026-10-18-project-list-real-pagination`（commit `0960cc0`）给 `/project/list`
修过的**同一个缺陷**（`api-contract` 已物化 Requirement「分页接口的 PageVO SHALL 回填实际
查询参数」，本端点**再次违反**）。

### 缺陷 4：`PUT /account/modify` 返裸 `boolean`

不在 `R` 包络内，前端无法用统一的 `unwrapSuccess` 消费。

## What Changes

| 项 | 修复前 | 修复后 |
|---|---|---|
| 响应类型 | `PageVO<AccountVO>`（裸，4 字段含 password）| `R<PageVO<AccountPublicVO>>`（3 字段）|
| 入参 | 直接拆箱 → NPE | 兜底 `curPage<=0→1` / `pageSize<=0→10` |
| 分页字段 | 三个恒 0 | 显式 set，`totalPage = ceil(totalRow/pageSize)` |
| 排序 | `orderByDesc(id)` | 保留（已是稳定序）|
| `modify` 返回 | 裸 `boolean` | `R<Boolean>` |

**契约**：`PageVOAccountVO` → `PageVOAccountPublicVO`；`AccountVO` 若无其它消费者则从契约消失。
orval 重新生成。

**BREAKING**：`/account/list` 的响应类型变更 —— 前端未消费（实测确认），无实际破坏。

## Capabilities

### Modified Capabilities

- **`api-data-object`** —— MODIFIED Req「Controller SHALL 返回 VO 而非持久化实体」：
  补 `AccountPublicVO` 的字段清单与「凭据字段 SHALL NOT 出现在列表元素里」这一实例
  （既有 Req「凭据字段 SHALL NOT 出现在任何接口响应中」已泛化表述，本次补具体证据）。
- **`api-contract`** —— MODIFIED Req「受保护 REST 接口 SHALL 走 `R<T>` 包装而非裸实体出参」：
  补 Scenario「分页端点 SHALL NOT 返回裸容器」（`PageVO<T>` 本身也是容器，同样应在 `R` 内）。
  **注意**：这是该 Requirement 第三次被违反（前两次：`loadByCode` 返裸 `ProjectVO`、
  `listMapFieldColumn` 返 `R<Object>`）。

### New Capabilities

（无。）

## Impact

- **后端**：`AccountController.list` 与 `modify` 的签名与装配；`AccountVO` / `AccountPageVO`
  若无其它消费者则可删除（需核实）；`contracts/openapi/openapi.yaml`。
- **测试**：`ApiContractIT` 新增场景⑭ 3 用例（password 不泄漏 / 包络与分页字段 / NPE 兜底）。
- **前端**：零改动（未消费该端点）。orval 重新生成后需确认无 stale model。
- **文档**：`AGENTS.md §11.1` 补运维建议（轮换密码 + 清理已落盘的 MD5）。
- **风险**：**低**。前端未消费；修法与 `0960cc0` 同一套已验证的模式。