# Design

## Context

平台当前有 6 处调用百度 UID 生成器，实测后可以精确分类——**其中只有 3 处生成的是主键**：

| 调用点 | 生成目标 | 处置 |
|--------|---------|------|
| `ModuleServiceImpl:35` `setId(uid)` | 主键 | 交给数据库自增 |
| `ProjectServiceImpl:117` `setId(uid)` | 主键 | 交给数据库自增 |
| `ProjectFramworkServiceImpl:33` `setId(uid)` | 主键 | 交给数据库自增 |
| `ProjectServiceImpl:118` `setCode(String.valueOf(getId()))` | 业务编码 | Hutool 雪花，与 id 解耦 |
| `ProjectServiceImpl:337` `new MapClassTable(String.valueOf(uid), ...)` | 业务编码 | Hutool 雪花 |
| `ProjectRepositoryAccountServiceImpl:49` `setCode(...)` | 业务编码 | Hutool 雪花 |
| `GeneratorSVImpl:382` `setCode(...)` | 业务编码 | Hutool 雪花 |

（末两行与前三行合计为 6 处唯一 `getUID()` 调用点，此表按语义而非按行数列举。）

数据库侧的前置条件已全部就绪——`schema.sql` 中所有相关表的 `id` 列**早已**声明为 `bigint(20) NOT NULL AUTO_INCREMENT`，因此「主键走自增」是**零 schema 改动**。已实测 H2 2.4.240 在 `MODE=MYSQL` 下支持「自增列 + `PRIMARY KEY(id, code)` 复合主键」，自增序列正常工作：

```
$ RunScript -url "jdbc:h2:mem:ai;MODE=MYSQL;DATABASE_TO_UPPER=false" -script schema.sql
INSERT ... → --> 1 c1
INSERT ... → --> 2 c2
```

## Goals / Non-Goals

**Goals:**

- 主键由数据库自增分配，应用层不介入
- 业务编码由进程内算法生成，无外部依赖
- 移除 vendored jar、第二数据源、`worker_node` 表
- 存量数据零迁移，前端与生成产物零感知

**Non-Goals:**

- 不做存量数据的编码格式统一或重排
- 不引入分布式 ID 的 workerId 外部协调机制（单实例部署，用不上）
- 不重构 `schema.sql` 的表结构（自增声明已就位）
- 不处理前端 bundle 中残留的 `/upload/basic/uid` 配置（属前端债）

## Decisions

### 决策 1：主键走自增，且**必须**显式设置 `IdType.AUTO`

这是本变更最大的陷阱。MyBatis-Plus 的 `GlobalConfig.DbConfig` 默认主键策略是 `ASSIGN_ID`（雪花）——字节码实证：

```
$ javap -c 'com.baomidou.mybatisplus.core.config.GlobalConfig$DbConfig'
  5: getstatic  // Field IdType.ASSIGN_ID
  8: putfield   // Field idType
```

而本项目：

| 检查 | 结果 |
|------|------|
| 实体上有 `@TableId` 注解吗？ | **一个都没有**（全仓 grep 为空） |
| `MybatisPlusConfigTidb` 配了 `idType` 吗？ | `new GlobalConfig()`，**未设置 `dbConfig`** |

```
  删掉 entity.setId(uidGenerator.getUID()) 之后会怎样？

  INSERT INTO project (code, ...) VALUES (?, ...)
            │
            ▼
  MyBatis-Plus 看到 id 为 null
            │
            ▼
  IdType.ASSIGN_ID（默认）→ DefaultIdentifierGenerator → IdWorker.getId()
            │
            ▼
  仍写入 19 位雪花 ID ❌   schema.sql 的 AUTO_INCREMENT 从未被触发
```

**代码删干净了、jar 删了、测试还绿，但雪花逻辑只是从百度 UID 搬进了 MyBatis-Plus 内部，且全程零报错。**

因此本变更 SHALL 显式设置：

```java
GlobalConfig globalConfig = new GlobalConfig()
        .setBanner(false)
        .setMetaObjectHandler(mybatisPlusMetaObjectHandler);
globalConfig.setDbConfig(new GlobalConfig.DbConfig()
        .setIdType(IdType.AUTO));          // ← 关键
```

**备选**：给每个实体加 `@TableId(type = IdType.AUTO)`。拒绝——6 个实体 × 未来新增实体，遗漏一个就静默回退；全局配置是一处生效、无法遗漏。

### 决策 2：验收断言必须锁定「形态」，而不能只断言「有值」

雪花 ID 和自增 ID 都是合法 `Long`，只断言 `isNotNull()` 的测试在两种策略下都绿，抓不住决策 1 的失败。验收断言 SHALL 同时检查形态：

```java
assertThat(project.getId()).isLessThan(1000L);              // 自增 = 小整数序列
assertThat(project.getCode()).matches("^\\d{19}$");         // code = 独立雪花
assertThat(project.getCode()).isNotEqualTo(String.valueOf(project.getId()));
```

第三条断言专门守护决策 3 的解耦——若有人「顺手」把 code 改回从 id 派生，这条会红。

### 决策 3：解开 `Project.code = String.valueOf(Project.id)`

现状：

```java
project.setId(uidGenerator.getUID());
project.setCode(String.valueOf(project.getId()));   // code 是 id 的字符串
```

主键改自增后，`id` 在 insert **之前**不可知，`code` 无从派生。两条路：

| 方案 | 做法 | 代价 |
|------|------|------|
| **A（推荐）** 解耦 | `code` 由 Hutool 雪花独立生成 | 无。两者本无语义关联，只是历史实现耦合 |
| B 两段式 | insert 拿到自增 id 后，update `code = String.valueOf(id)` | 多一次 UPDATE；id 与 code 在事务内短暂不一致；若 insert 后事务回滚则不一致残留 |

选 A。`code` 的语义本就是「业务编码」，与「物理主键」无关；A 同时消除了一个隐式耦合。

### 决策 4：`code` 用 Hutool 而非 MyBatis-Plus 的雪花

两者都已在 classpath 上，零新增依赖：

| 维度 | **Hutool** `IdUtil.getSnowflakeNextIdStr()` | MyBatis-Plus `IdWorker.getIdStr()` |
|------|------------------------------------------|-----------------------------------|
| 依赖现状 | `hutool-all` 已在 common/aicode/facade | `mybatis-plus-core` 传递依赖 |
| 项目内既有惯例 | `cn.hutool.core.io.FileUtil`、`DateUtil` 已在用 | 业务代码零使用 |
| 模块隔离 | facade/common 均可用，不绑 Spring | `IdWorker` 是静态单例，测试间会串状态 |
| 可读性 | 一眼看出是「业务编码」 | 与主键策略混在一起，易重犯决策 1 的错 |

选 Hutool。**关键理由是认知隔离**：本变更正在把「主键策略」从 MyBatis-Plus 里拆出来，若 `code` 又用 MyBatis-Plus 的 `IdWorker` 生成，两个概念会在代码里纠缠不清。

epoch 两者均为 2010-01-01，产出同为 19 位纯数字，与历史数据同构。

### 决策 5：编码格式不变，避免新旧混排

`code` 保持纯数字字符串而非改成 UUID/短随机串，代价是「看起来不够简洁」，收益是**存量与新数据同构**——同一列里不会同时存在两种格式，后续排查、排序、关联都不用考虑格式分支。

已实证 `code` 不流入生成的 Java 标识符：

```
MapClassTable.toJava():
    this.className  = StringHelper.toJavaClassName(this.tableName);   ← 来自表名
    this.classModel = tableName.contains("_") ? tableName.substring(...) : tableName;

TemplateData 中无任何 getCode() 引用
GeneratorSVImpl 中 code 仅作外键与查询条件：
    ProjectMap::getProjectCode              == projectCode
    MapFieldColumn::getMapClassTableCode    == mapClassTable.getCode()
logsSV.createLogFiles(projectCode) → 用 code 查 project，再取 project 信息拼路径
```

因此 `code` 只需满足「唯一 + 非空 + 稳定」，格式完全自由——但保持不变仍是最优解。

### 决策 6：删除第二数据源是有依据的，不是「顺手去重」

AGENTS.md §4 写「存在两个 DataSource Bean，**不要**顺手去重，需先理解下游生成工程的依赖」。核查结论：

- uid 数据源的**唯一**用途是承载 `worker_node` 表（百度 UID 的 workerId 分配）
- 该表随 jar 一起删除后，uid 数据源**不再有任何表**
- 生成主链路 `GeneratorSVImpl` 读取的是 `frameworks` / `project_sql` 等业务表，走 tidb 数据源
- `com.baidu.fsg.uid.worker.dao` 的 Mapper 由 `@MapperScan` 扫描，随类删除而失效

因此删除是**有依赖分析支撑的**，符合 AGENTS.md 的要求。

### 决策 7：workerId 分配的演进风险显式记录

Hutool 雪花默认从 MAC 地址与进程 PID 派生 workerId。当前单实例部署无碰撞风险。若未来扩到多副本（如 K8s replicas > 1、Pod 重建后 MAC 变化），存在碰撞可能。

**本变更不实现** workerId 外部协调（当前无需求），但 SHALL 在 `docs/standards/architecture.md` 的「后续演进」中记录该约束，避免将来扩副本时无声踩坑。

## Risks / Trade-offs

| 风险 | 影响 | 缓解 |
|------|------|------|
| 有人只删 `setId()` 而漏掉 `IdType.AUTO` | 雪花静默回归，且所有测试仍绿 | 决策 2 的形态断言专门抓这个；tasks 中把「验证 id 为小整数」列为独立验收项 |
| `GlobalConfig` 需在 `MybatisPlusConfigTidb` 与 `MybatisPlusConfigUid` 两处设置 | 漏改一处 | 本变更删除 `MybatisPlusConfigUid`，只剩一处需改 |
| 自增 ID 语义变化影响按 ID 排序/分页的假设 | 列表顺序与预期不符 | 自增严格递增，`ORDER BY id` 行为稳定；SHALL NOT 依赖此点，契约测试不覆盖排序语义 |
| 删除 `worker_node` 表后若将来回滚本变更 | 启动失败 | 回滚需同时恢复 jar 与第二数据源；已在 tasks 中记录该回滚前提 |
| Hutool 雪花在多副本部署下碰撞 | ID 重复 | 决策 7：显式记录演进约束；当前单实例无风险 |
| `code` 仍是 19 位数字，与 `id` 形态差异增大，调试时易混淆 | 排查成本 | tasks 中要求在 `code` 生成处加注释说明「业务编码，非主键」 |
