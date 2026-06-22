# AI-Code 测试规范（Testing）

> 本文定义 AI-Code 的**测试策略、组织方式与编写约定**。
> 配套文档：[architecture.md](architecture.md)（架构）、[code-style.md](code-style.md)（编码规范）。

- 文档版本：v1.0
- 适用分支：`dev`
- 运行环境：Java 21、Spring Boot 3.3.9、Gradle 多模块

---

## 目录

1. [现状](#1-现状)
2. [测试技术栈](#2-测试技术栈)
3. [测试分层与目标](#3-测试分层与目标)
4. [目录与命名约定](#4-目录与命名约定)
5. [单元测试规范](#5-单元测试规范)
6. [集成测试规范](#6-集成测试规范)
7. [代码生成主链路测试策略（重点）](#7-代码生成主链路测试策略重点)
8. [测试数据与环境](#8-测试数据与环境)
9. [运行与覆盖率](#9-运行与覆盖率)
10. [优先补齐清单](#10-优先补齐清单)

---

## 1. 现状

> 诚实说明：**当前仓库未见任何测试代码**，三模块均无 `src/test` 实现；`aicode` 模块仅声明了 `testImplementation 'org.springframework.boot:spring-boot-starter-test'` 依赖，`common`/`facade` 未引入测试依赖。

因此本文是**目标规范（to-be）**：约定后续测试应如何组织与编写，并在 §10 给出优先补齐清单。新增功能应同步补测试，逐步建立安全网。

---

## 2. 测试技术栈

| 用途 | 选型 | 说明 |
|------|------|------|
| 测试框架 | JUnit 5（Jupiter） | 随 `spring-boot-starter-test` 引入 |
| 断言 | AssertJ / JUnit 5 Assertions | 优先 AssertJ 链式断言，可读性好 |
| Mock | Mockito | 随 starter 引入，隔离依赖 |
| Spring 测试 | `@SpringBootTest` / `@MockBean` | 集成测试上下文 |
| Web 测试 | `MockMvc`（`@AutoConfigureMockMvc`）/ `WebTestClient` | Controller 层 |
| 数据库 | H2（MySQL 兼容模式） | 与运行时一致，天然适合做测试库 |

> `common`、`facade` 模块若要写单元测试，需各自在 `build.gradle` 补 `testImplementation 'org.junit.jupiter:junit-jupiter'`（或对应 BOM）。

---

## 3. 测试分层与目标

遵循测试金字塔，重心放在「快而多」的单元测试，集成测试覆盖关键链路：

| 层级 | 范围 | 是否启动 Spring | 典型对象 |
|------|------|----------------|---------|
| 单元测试 | 单类/单方法，依赖全部 mock | 否 | `common` 工具类、枚举、`TemplateData` 装配逻辑、`StringTools`/`StringHelper` |
| 切片测试 | 单层 + 部分上下文 | 部分 | Controller（`MockMvc`）、Mapper（`@MybatisPlusTest` 风格 + H2） |
| 集成测试 | 跨层、近真实 | 是（`@SpringBootTest`） | 登录→建项目→导 SQL→init→构建 全链路 |

各模块测试侧重：
- **common**：纯函数工具最易测，应做到高覆盖（类型映射、字符串/拼音、SQL 转义、zip、JWT）。
- **facade**：以 POJO 为主，测试枚举转换（如 `TemplateEngineEnum.getTemplate`、各 `*State`、`ModuleEnum.getEnum`）。
- **aicode**：测试 service 编排与生成主链路（见 §7）。

---

## 4. 目录与命名约定

- 测试源码放各模块 `src/test/java`，包路径与被测类**镜像一致**（`com.aicode.<domain>...`）。
- 测试类命名：被测类名 + 后缀。
  - 单元测试：`XxxTest`（如 `StringToolsTest`、`TemplateDataTest`）。
  - 集成测试：`XxxIT` 或 `XxxIntegrationTest`（如 `GeneratorIT`）。
- 测试方法名用「行为_条件_期望」或中文 `@DisplayName` 描述场景，例如：
  - `getTemplate_givenLowerCaseName_returnsFreemarker()`
  - `@DisplayName("非法列名 order 应被纠正为合法变量名")`
- 测试资源放 `src/test/resources`（独立 schema/data、样例 SQL、样例模板）。

---

## 5. 单元测试规范

- **隔离**：被测类的外部依赖（Mapper、`GitTools`、`TemplateHelper`、文件系统）一律 `@Mock`/`@MockBean`，不触网、不落盘。
- **三段式**：Arrange–Act–Assert（准备—执行—断言），一个测试只验证一个行为点。
- **断言具体**：断言到具体值/结构，不要只断言「非空」。
- **不依赖顺序**：测试间相互独立，禁止共享可变静态状态。
- **覆盖边界**：空集合、null、非法输入、大小写、特殊字符（尤其字符串/SQL 处理类）。
- 纯函数优先无 Spring 上下文，保证毫秒级执行。

---

## 6. 集成测试规范

- 用 `@SpringBootTest` 启动真实上下文，数据源指向**独立 H2 测试库**（不要污染 `/tmp/aicode` 默认库，见 §8）。
- Controller 用 `MockMvc` 验证：HTTP 状态、统一响应体 `R` 结构（`code/success/data`）、错误码（`0000/9003/9004/9007/9999`）。
- 涉及 `@Async` 的构建：测试中应能等待任务完成（轮询 `ProjectJob.state` 至 `Completed/Error`，设超时），不要 `sleep` 固定时长。
- 涉及 WebSocket 日志：可对 `WSClientManager.sendMessage` 做验证替身，断言关键节点消息被推送。
- 集成测试默认不连真实远程 Git；`GitTools` 的 clone/push 用 mock 或本地裸仓库（`file://`）替代，避免外网依赖与不稳定。

---

## 7. 代码生成主链路测试策略（重点）

生成主链路（`GeneratorSVImpl.aiCode()`，见 architecture.md 第 3 章）是核心资产，也是当前最大测试空白。建议分层覆盖：

### 7.1 纯逻辑单元测试（最高优先级，无 IO）

可在不启动 Spring、不碰 Git/文件的前提下测：
- **`TemplateData` 装配**：给定 `MapClassTable` + `MapFieldColumn` 列表，验证主键/非主键/表格字段拆分、`model` 推导（表名含 `_` 取前缀）、`classNameLower`/`dashedCaseName` 生成、1:1 / 1:N 关系装配、显示属性 → `Field` 映射。
- **路径占位符替换**：抽取/验证 `${basepackage}`/`${className}`/`${module}`/`${model}`、Beetl `$xxx$`、`$classNameState$` 的替换结果（建议把 `generator()` 中的路径拼装逻辑重构为可独立测试的纯方法）。
- **引擎适配**：`adapterTemplateEngine` 给定不同 `aicode.json`（含错误文件名兼容 `ai-code.json` 等）返回正确 `TemplateEngineEnum`，缺失时回退 Freemarker。
- **类型映射**：`DatabaseDataTypesUtils`/`JavaPrimitiveTypeMapping` 的 SQL→Java 类型转换。

### 7.2 模板渲染测试（`TemplateHelper`）

- 准备最小样例模板（`.ftl` / `.btl`）+ 构造好的 `TemplateData`，调用 `FreemarkerHelper.generate` / `BeetlHelper.generate`，断言生成文件内容符合预期。
- 输出目录用 JUnit 5 `@TempDir`，测试后自动清理。

### 7.3 端到端集成测试（mock 外部依赖）

- 用 `@SpringBootTest` 走 `execute → @Async → aiCode` 全流程，但：
  - `GitTools` 用本地裸仓库或 mock，模板「下载」改为指向 `src/test/resources` 内置模板目录；
  - 断言：`ProjectJob` 最终状态 `Completed`、产物目录结构正确、关键文件生成、ZIP 生成、`buildNumber` 自增。
- 失败路径：构造异常（如模板缺失），断言 `ProjectJob.state=Error` 且不抛出到线程顶层。

> 建议：为提升可测性，逐步把 `GeneratorSVImpl` 中的「文件系统/ Git / 设置读取」抽象为可注入接口，降低对 `Setting` 表与真实路径的硬依赖。

---

## 8. 测试数据与环境

- **独立测试库**：测试 `application-test.yml` 覆盖数据源 URL，使用内存或临时文件 H2（如 `jdbc:h2:mem:aicode_test;MODE=MYSQL;DB_CLOSE_DELAY=-1`），避免写入默认 `/tmp/aicode`。
- **Schema/Data**：复用 `db/schema.sql`，测试专用初始化数据放 `src/test/resources`，每个测试类/方法保证数据独立（`@Transactional` 回滚或显式清理）。
- **临时目录**：所有产物/克隆目录用 `@TempDir`，禁止写死绝对路径。
- **样例 SQL**：准备覆盖单表、多表、关联、含状态枚举字段、含非法命名（如 `order`）的建表脚本作为测试夹具。

---

## 9. 运行与覆盖率

- 运行全部测试：`./gradlew test`；单模块：`./gradlew :aicode:test`。
- 集成测试（`*IT`）建议绑定到 `integrationTest` 任务或用标签 `@Tag("it")` 区分，CI 中分阶段执行。
- 覆盖率：引入 JaCoCo（`jacoco` 插件），关注**核心包**（`com.aicode.project.service`、`com.aicode.config.template`、`com.aicode.core.tools`）的行/分支覆盖，而非整体百分比。
- 约定：新增/修改生成主链路、工具类的 PR 必须附带测试；CI 测试失败禁止合并。

---

## 10. 优先补齐清单

按投入产出比排序（从易到难、从核心到外围）：

1. `common` 工具纯函数单测：`StringTools`、`StringHelper`、`DatabaseDataTypesUtils`、`ZipTools`、`JwtToken`。
2. `facade` 枚举转换单测：`TemplateEngineEnum`、`ModuleEnum`、各 `*State`。
3. `TemplateData` 装配单测（§7.1）——保护生成逻辑的核心数据模型。
4. `TemplateHelper` 渲染单测（§7.2，Freemarker + Beetl 各一）。
5. Controller 切片测试（`MockMvc`）：登录、建项目、导 SQL、init 的响应体与错误码。
6. 生成主链路端到端集成测试（§7.3，mock Git/模板）。

---

