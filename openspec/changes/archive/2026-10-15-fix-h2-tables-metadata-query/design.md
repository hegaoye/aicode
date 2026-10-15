# Design

## Context

见 proposal.md 的 Why —— 根因是 H2 1.4.200 → 2.4.240 期间 `INFORMATION_SCHEMA.TABLES.TABLE_TYPE` 的取值由 `'TABLE'` 变为 `'BASE TABLE'`，而 `table.xml` 硬编码了旧值。

本文件只记录**有多个合理选项、需要写明「为什么不用更简单的做法」**的决策。纯查表改值不在此列。

## Goals / Non-Goals

**Goals:**

- 让表/列元数据探测在 H2 2.4.240 下正确工作
- 让同类失败在**测试阶段**而非使用者手工触发时暴露
- 让错误可定位（不再出现「原因 A 与原因 B 报同一个码」）

**Non-Goals:**

- 不支持多数据库（H2 / MySQL / PostgreSQL）。`AGENTS.md` §11.2 把「持久化升级到 MySQL/PG」列为演进方向，属独立事项
- 不引入 ORM 元数据 API（如 JDBC `DatabaseMetaData`）替代 `INFORMATION_SCHEMA` SQL
- 不改造 `SqlConvertEnum`（本次实测该转换层对用户提供的 SQL **一字未改**，与本缺陷无关；其自身存在的其他规则缺陷另立事项）
- 不处理 `parse()` 中「schema 存在但表为空」之外的其它幂等性问题

## Decisions

### 决策 1：`TABLE_TYPE` 用 H2 2.x 的字面量，而非去掉条件

**选择**：`table.xml` 三处 `TABLE_TYPE = 'TABLE'` → `TABLE_TYPE = 'BASE TABLE'`。

**备选与取舍**：

| 方案 | 优点 | 缺点 | 决定 |
|------|------|------|------|
| A. 改为 `'BASE TABLE'` | 一行、语义精确、行为可预测 | 绑死 H2；换库需再次修改 | **采用** |
| B. 去掉 `TABLE_TYPE` 条件 | 换库时不易失效 | 会把**视图**也当作表返回，而 `parse()` 随后按表名取列，视图场景下会产出错误的映射数据 | 拒绝 |
| C. `IN ('TABLE','BASE TABLE')` | 新旧 H2 都兼容 | 显式承认脆弱点，且把「旧 H2」写成永久分支 | 拒绝 |

选 A 的核心理由：**当前 H2 是唯一目标数据库**（`AGENTS.md` §4、§11.2）。为尚不存在的多库需求牺牲当前正确性，是本末倒置。

**但必须在文件里留下痕迹** —— 否则下一个做 H2 升级的人会重蹈覆辙。为此在 `table.xml` 中加注释，指明该字面量与 H2 版本绑定，并指向本变更。

### 决策 1b：实施中发现的第二处同类变更（决策 1 的范围被扩大）

起草阶段的人工审查只检查了 `table.xml` 的 `TABLE_TYPE`，得出「`colum.xml` 不受影响，无需修正」的结论 —— 该结论是**错的**。

新增的 `TableMapperIT` 首次运行时立刻暴露：`colum.xml` 查询的 `TYPE_NAME` 在 H2 2.x 已被移除（改用 `DATA_TYPE`），必然抛 `BadSqlGrammarException: Column "TYPE_NAME" not found`。

更隐蔽的是第二层：把列名改对**还不够**。`Column` 实体的属性名是 `typeName`（由 `@TableField` 指定），与 `DATA_TYPE` 并非驼峰对应，MyBatis 的 `resultType` 自动映射会静默留下 `null`，随后在 `DatabaseDataTypesUtils.isDate()` 触发 NPE：

```
NullPointerException: Cannot invoke "String.endsWith(String)" because "javaType" is null
```

**即：一个 SQL 层的列名问题，被伪装成了业务代码里的空指针。** 若没有集成测试，这条链路要走三轮「查业务代码」才能定位到 SQL。

处置：`colum.xml` 的每一列都用 `AS` 显式声明别名，不再依赖自动映射。

**这个发现本身验证了决策 2 的价值**：集成测试在首次运行就抓到了人工审查漏掉的第二处缺陷。

### 决策 2：探测回归测试放在集成层而非单元层

**选择**：新增 `TableMapperIT`，用 `@SpringBootTest` + 真实 H2 内存库，走完「建 schema → 建表 → `list`/`load`/`count`」。

**理由**：本次缺陷的本质是**数据库的元数据语义变了**，而元数据语义只有连真实数据库才能观测。单元测试若 mock 掉 `TableMapper`，等于把要守护的东西 mock 掉了 —— 这正是本次事故的成因（`ApiContractIT` 覆盖了 REST 层，却没覆盖到元数据层）。

**与 `ApiContractIT` 的分工**：

```
ApiContractIT          REST 层：路径后缀、响应结构、ID 字符串化
TableMapperIT   ←新增   元数据层：INFORMATION_SCHEMA 查询语义
```
两者都不可省略。

**为什么不放到 `CodeGenPipelineTest`**：那个测试类测的是「占位符替换」「引擎探测」等纯逻辑，不碰数据库。新建独立类边界更清晰。

### 决策 3：异常上抛用「包装 + 保留 cause」，而非直接 `throw e`

`DatabaseDAO.createDatabase` 当前：

```java
} catch (Exception e) {
    log.error("{}", e.getMessage());     // 无堆栈
    databaseMapper.dropDatabase(database); // 删库
} finally {
    databaseMapper.useDatabase(defaultDatabase);
}
```

三个独立问题：

1. **异常被吞** —— 上层 `execute()` 以为成功，继续往下走
2. **日志无堆栈** —— 只有 `getMessage()`，排查时看不到 SQL 失败的位置
3. **`dropDatabase` 后无提示** —— 库被删了但没人知道

**选择**：捕获后记录**带堆栈**的日志、清理已创建的 schema、再抛出携带上下文的异常（schema 名 + 原始 cause）。

**为什么保留 `dropDatabase` 的清理动作**：它本身是合理的（不留下半成品 schema）。问题不是「清理」，而是「清理完还装作成功」。因此保留清理、补上日志与异常。

**为什么不用 `throw new ProjectException(...)` 直接替换**：那是上层异常的语义，而 DAO 层不应感知业务异常类型。选择让 `DatabaseException`（或复用现有 `BaseException` 体系中的合适类型）承载，具体类型在实现时依据现有异常体系确定。

### 决策 4：「库已存在」分支改为校验而非盲跳

当前：

```java
long i = databaseDAO.count(database);
if (i > 0) {
    log.info("数据库已存在，跳过建库：{}", database);
    return;                    // ← 不校验里面有没有表
}
```

这条与本次 9006 是**第二条独立成因路径**：若 schema 存在但表被删过、或上次建表失败留下了空 schema，这里静默跳过，`parse()` 随后报出与「建表失败」一模一样的 9006，排障时无法区分。

**选择**：命中「已存在」分支时，用 `tableDAO.list(database)` 校验是否真有表；有表则跳过（正常幂等），无表则抛出明确错误，提示「schema 已存在但为空」。

**代价**：多一次元数据查询。相对于「排障时无法区分成因」的代价可以忽略 —— 且这正是决策 2 的集成测试要覆盖的路径之一。

## Risks / Trade-offs

| 风险 | 影响 | 缓解 |
|------|------|------|
| `'BASE TABLE'` 再次绑死 H2 | 未来换库需再次修改 | 决策 1 已说明取舍；`table.xml` 内留注释；换库时由独立变更处理（届时决策 B 的权衡会反转） |
| 异常上抛后改变 `execute()` 的错误码 | 前端可能收到新错误码 | 需在实现时确认抛出的异常最终映射到哪个 `BaseException` 枚举值；若会改变对外错误码，SHALL 同步更新 `api-contract` 的错误码场景 |
| `dropDatabase` 在事务内执行，若异常上抛则回滚 | 半成品 schema 可能残留 | 需在实现时确认事务边界：`@Transactional` 下 DDL 在 H2 中是否可回滚。若不可回滚，清理逻辑须移到事务外 |
| 集成测试依赖真实 H2，CI 环境差异 | 测试在本地过、CI 挂 | 测试使用内存库 + 与 `application-test.yml` 一致的连接参数；`GitToolsCloneCompletenessTest` 已有「需要网络的测试用 `@EnabledIfSystemProperty` 守卫」的先例可循 |
| 修复后旧的空 schema 仍留在用户库中 | 用户需手工清理 | 变更说明中提示；或在异常信息中直接给出可执行的清理 SQL |
