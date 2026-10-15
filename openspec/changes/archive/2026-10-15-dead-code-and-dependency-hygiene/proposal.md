# Proposal

## Why

`common` 模块中约 **73%** 的代码（8201 / 11244 行）为 2010 年前后从「仁中和 AI-Code」、Apache MyBatis Generator、Velocity 及 Spring 3.0 源码逐字 vendored 进来的死代码，经调用图验证无任何生产调用方。同时存在多处**构建文件与实际解析结果不符**的依赖问题：显式锁死的旧版本被传递依赖静默覆盖、无任何注解使用的 Bean Validation 依赖白进制品、以及 `common` / `facade` 与 `aicode` 使用不同 Boot BOM 导致的版本分裂。

这些问题共同导致：制品无谓膨胀、规范条文与代码事实脱节、且重构者容易误把「承担生成产物命名的转换函数」替换成行为不等价的第三方 API，从而**静默改变所有生成工程的类名与文件名**。本变更的目标是让「删除无用、收敛重叠、冻结命名契约」这三条规则可被机器验证。

## What Changes

- **删除死代码**：移除 `common/core/tools/core/` 下 24 个死类（`paranamer` / `model/java` / `sqlparse` / `AntPathMatcher` 等）及 `HandleFuncs`、`CacheEnumTools`，以及 100+ 个零调用方法与全部 `main()` 调试残留；清理违反 §6.7 的 `e.printStackTrace()`。
- **收敛重叠依赖**：`hutool-all`（2.6 MB，仅用 3 个 core 类）→ `hutool-core`；`facade` 的 `springdoc-openapi-starter-webmvc-ui` → `swagger-annotations-jakarta`（facade 全库对 Spring / jakarta 的 import 数为 0，不应拖入 Web 全栈）。
- **移除无用依赖**：删除 `spring-boot-starter-validation`（全仓 Bean Validation 注解命中数为 0，含测试代码）。
- **修正版本错配**：删除 `druid:1.2.22`、`commons-io:2.4`、`junit-jupiter:5.10.2` 三处被传递依赖静默覆盖的死锁版；统一 `facade` 的 hutool 版本至 5.8.40。
- **对齐 BOM 版本**：`common` / `facade` 的 `spring-boot-dependencies` 由 3.5.16 提升至 4.1.1，消除 JUnit 5.12.2 vs 6.0.3、Spring Framework 6.2.19 vs 7.0.9 的分裂。
- **新增防回流断言**：引入 ArchUnit 断言，禁止 `facade` / `aicode` 引用 `common/core/tools/core` 下的类型。
- **冻结命名契约**：为 `toJavaClassName` / `toJavaVariableName` / `capitalize` / `humpToLine` 建立行为快照测试，明令禁止用行为不等价的第三方 API 替换。
- **低风险重构**：`Field` 继承 `MapFieldColumn` 消除 26 个成员逐字重复；`FreemarkerHelper` / `BeetlHelper` 的 JSON round-trip 改为 hutool `BeanUtil.beanToMap`；3 处 `com.alibaba.druid.util.StringUtils` 改用 commons-lang3；`DatabaseDataTypesUtils` 五个判定方法补 null 防护；`SqlEmitter` 写文件编码显式化。
- **同步规范条文**：移除依赖时，同步修正引用了该依赖的 `AGENTS.md §6.4`（Bean Validation）与 `§6.5`（`StringTools.convertObject/convertList` 为零调用死代码）。

**BREAKING**：`common` 模块对外暴露的 100+ 个工具方法被物理移除。这些方法当前无任何生产调用方，且 `api-deprecation-policy` 的四阶段流程限定于 REST 端点下线，故不走 30 天观察期；但需在删除前完成反射 / 注解 / MyBatis XML 的人工核查。

## Capabilities

### New Capabilities

- `dependency-hygiene`: 约束依赖声明的最小性、功能重叠依赖的收敛、版本错配的消除、无用依赖移除时的规范同步义务、死代码防回流断言，以及承担生成产物命名的转换函数不得被行为不等价的第三方 API 替换。

### Modified Capabilities

- `runtime-platform`: 新增「依赖管理版本一致性」要求 —— 全部模块导入的 BOM 版本 SHALL 与应用模块的框架版本一致，禁止显式锁版覆盖构建工具已管理的受支持版本。

## Impact

- **代码**：`common` 减少约 8201 行（-73%）；`Field` / `MapFieldColumn` 去重；`FreemarkerHelper` / `BeetlHelper` / `SqlEmitter` / `DatabaseDataTypesUtils` / 3 个 ServiceImpl 的 import 调整。
- **依赖**：移除 4 项（`spring-boot-starter-validation`、`hutool-all`、`mybatis-plus-extension`（facade）、3 处死锁版）；`hutool-all` → `hutool-core`；新增 `com.tngtech.archunit:archunit-junit5`（仅 test，不进制品）。
- **制品**：fat jar **实测净减 0.56 MB**（64,253,259 → 63,661,939 字节，-0.9%）。⚠️ 提案阶段的「预计减 5 MB」预测**已被实测推翻**，详见 `design.md - D7 附记`。JVM 懒加载，删除死代码**不会**减少 metaspace，内存收益仅来自 jar 映射页。
- **风险**：BOM 3.5.16 → 4.1.1 会使 `common` / `facade` 的测试跨 JUnit 大版本（5.12.2 → 6.0.3），是本变更唯一需要真实回归的改动，SHALL 单独成批验证。
- **规范**：`AGENTS.md` §6.4、§6.5、§7.10、§11.3 需同步更新。
- **明确不动**：`StringHelper` / `StringTools` 的命名转换实现、`PasswordCrypto`（IV 派生与失败回退语义不可替代）、fastjson2（与外部模板仓构成事实契约）、Freemarker + Beetl 双引擎（§5.6 明文决策）、JGit `HttpClientConnectionFactory`（CDS 兼容修复）、Druid → HikariCP 切换（单独立项）。
