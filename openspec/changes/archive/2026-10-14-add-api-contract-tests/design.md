# Design

## Context

本变更不改变任何行为，只把「现状」固化为可执行断言。因此设计的核心问题是：**如何确保这批测试足够真实，能在升级中真的抓到回归，而不是写出一批永远绿的摆设。**

三个具体的失效风险：

1. **测试用的 mock 会让契约失效**。现有 5 个 `@SpringBootTest` 测试类用 `@MockBean` 把所有 Service / Mapper 换掉，如果契约测试也这么做，它验证的就只是「Jackson 序列化行为」，而不是「真实接口返回」。而 Jackson 3 迁移恰恰是序列化层的问题——这里 mock 掉反而是对的；但 `.shtml` 路径可达性就必须加载真实的 `WebMvcConfigurer`，否则测不出路由层的问题。
2. **断言写成「看起来对」的形态**。例如只断言 `id` 非空，那么雪花被截断成数字后依然非空，测试照样绿。必须断言 JSON 类型与值形态。
3. **端点清单会漂移**。前端是冻结的打包产物，但后端会演进。清单必须来自**前端实际调用的路径**，而不是后端 `@RequestMapping` 的枚举——否则会漏掉前端已在调用但后端已下发的端点（实测已发现 `/upload/basic/uid` 属于此类）。

## Goals / Non-Goals

**Goals:**

- 五组契约场景（ID 字符串 / 后缀双路径 / R 结构 / 日期格式 / null 保留）在 Spring Boot 3.3.9 基线上全绿
- 端点清单来自前端 bundle 的实际配置，而非后端枚举
- 测试能在 Spring Boot 4.1.1 上原样复用，无需重写
- 单次执行时间可接受（当前 `:aicode:test` 约 16s，契约测试 SHALL NOT 使其翻倍）

**Non-Goals:**

- 不修复任何契约偏差。实测发现的 `/upload/basic/uid` 前端配置残留**记录但不处理**——它属于前端债，与后端升级无关
- 不引入 API 契约测试框架（Spring REST Docs / Pact / Schemathesis）。这些框架的收益在单服务场景不足以抵消引入成本
- 不覆盖 WebSocket 消息内容的契约，只覆盖端点可达性
- 不做性能或负载断言

## Decisions

### 决策 1：ID 断言必须落在 JSON 类型与值形态上，而非 Java 类型上

```java
// ✗ 无效：Jackson 3 下 Long 仍是 Long，这个断言照样绿
assertThat(jsonPath("$.data.records[0].id")).isNotNull();

// ✗ 无效：数字 1.88e18 也能匹配 \d+
assertThat(jsonPath("$.data.records[0].id").value().toString()).matches("\\d+");

// ✓ 有效：同时锁定 JSON 类型与完整精度
assertThat(rawJson).contains("\"id\":\"1882812765213425664\"");   // 带引号 = 字符串
```

**理由**：MockMvc 的 `jsonPath("$.x")` 默认把数字与字符串做宽松比较，必须读 `contentAsString()` 做原始文本断言才能区分 `"123"` 与 `123`。而精度断言需要拿数据库原值比对，不能只看位数——`1882812765213425600` 也是 19 位。

**取舍**：读原始字符串断言可读性差。用一个自定义 matcher `jsonStringEquals("$.id", expectedLong)` 封装，兼顾可读性与严格性。

### 决策 2：`.shtml` 双路径测试走真实 `WebMvcConfigurer`，但打桩业务层

契约测试采用 `@SpringBootTest(webEnvironment = MOCK)` + `MockMvcBuilders.webAppContextSetup(wac)`，与现有 `LoginCtrlShtmlPathTest` 一致——这会加载真实的 `ContextConfiguration`（含 `addInterceptors` / `addResourceHandlers`）。

**理由**：`.shtml` 可达性是**路由层**行为，mock 掉 `WebMvcConfigurer` 就测不到了。而业务层（Service）返回什么与路由无关，可以 mock。

**已验证**：该方式在 Boot 3.3.9 上实测通过（`LoginCtrlShtmlPathTest` BUILD SUCCESSFUL）。

**已知副作用**：`ApplicationClosedEventListener.GracefulShutdown.onApplicationEvent` 在 MOCK 环境下会因 `connector == null` 抛 NPE（日志中可见）。这不使测试失败（异常在 context close 阶段），但会污染输出。本变更 SHALL 记录该现象，实际修复放在 `2026-10-14-upgrade-spring-boot-3-5`（因为那个文件本来就要改包名）。

### 决策 3：端点清单从前端 bundle 提取并固化为 Java 常量

从 `static/main.*.js` 的 `c2t9` 模块（`URL` 配置对象）提取出的完整清单：

```
base:      /res/enum/  /upload/basic/uid  ws://<host>/websocket.shtml
project:   /project/{build,delete,init,list,load,modify,scan/path}
job:       /project/job/{build,execute,delete,list,load,modify}
logs:      /logs/load
framework: /framework/{build,delete,list,load,modify,updateIsPublic}
framwork:  /project/framwork/{add,delete,list,load}
repo:      /project/repository/{build,delete,list,load,modify}
sql:       /project/sql/{build,delete,list,load,modify}
relationship: /project/relationship/{listMapClassTable,listByProjectCode,list,listMapFieldColumn,build,delete}
attributes:/displayAttribute/{list,save}
login:     /login/{signin,reg}
```

固化为 `FrontendEndpointCatalog`，理由：前端产物是冻结的，清单一旦确定就不会变；把它变成运行时扫描反而引入脆弱性。

**附带价值**：清单中 `/upload/basic/uid` 在后端无对应 Controller，测试将以「404」记录这条漂移，作为前端债的显式台账。

### 决策 4：日期格式断言同时验证时区稳定性

单测只断言格式 `yyyy-MM-dd HH:mm:ss` 是不够的——格式对但时区变了（UTC vs GMT+8）会导致时间偏移 8 小时，格式依然匹配。

因此补充一条：插入一条 `createTime` 为已知时刻的记录，断言返回值与预期偏移一致。这条依赖固定时刻而非 `now()`，避免测试自身在午夜边界抖动。

### 决策 5：不使用 `@MockBean`

`@MockBean` 在 Spring Boot 4.0 中被移除（改用 `@MockitoBean`）。虽然本变更跑在 3.3.9 上，但契约测试 SHALL 从一开始就用 `@MockitoBean`，避免该变更在升级时被二次返工。

**约束**：`@MockitoBean` / `@MockitoSpyBean` **不允许用于 `@Configuration` 类**，只能作为测试类字段。这与现有测试的用法一致。

## Risks / Trade-offs

| 风险 | 影响 | 缓解 |
|------|------|------|
| 契约测试依赖 H2 内存库 + `test` profile，该 profile 的数据源配置当前未在 `src/test/resources` 中显式定义 | 测试可能污染 `/tmp/aicode` 默认库 | tasks 中显式要求新增 `application-test.yml`，指向 `jdbc:h2:mem:aicode_test` |
| 前端端点清单与后端漂移导致测试长期红 | 契约测试变成噪音而被忽略 | 漂移条目（如 `/upload/basic/uid`）以 `@Disabled` + 注释显式登记并说明原因，SHALL NOT 静默跳过 |
| 45 个端点 × 2 路径 = 90 条断言，测试维护成本上升 | 后续新增端点需同步清单 | 在 `FrontendEndpointCatalog` 的类注释中写明「新增端点须同步此清单」的约定 |
| 断言 `Long → String` 在某些字段上并非期望行为（如 `totalRow` 分页计数） | 过度约束 | 经核查，当前前端对分页计数字段不做精度计算，全局字符串化是既有行为且无害；若将来需要数字，在契约中显式加豁免 |
| Boot 4 迁移后 `MockMvcBuilders.webAppContextSetup` 的 API 可能变化 | 契约测试需小改 | Boot 4 变更的任务清单中预留「契约测试适配」条目 |
