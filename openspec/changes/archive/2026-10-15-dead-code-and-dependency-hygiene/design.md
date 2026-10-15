# Design

## Context

见 `proposal.md - Why` 与 `specs/dependency-hygiene/spec.md`。本设计需要解决的现状约束：

- 三模块依赖方向单向无环（`aicode → facade → common`），`common` 不得依赖 `facade`。
- `Field` 位于 `common`，`MapFieldColumn` 位于 `facade`，二者 26 个成员逐字重复。
- `FreemarkerHelper` 与 `BeetlHelper` 各有一行相同的 `JSON.parseObject(JSON.toJSONString(templateData), Map.class)`。
- 外部模板仓 `aicode_template` 通过 `${className}`、`${dashedCaseName}`、`${field}` 等变量消费 `TemplateData`，SHALL NOT 由本仓库单方面改变键名集合。

## Goals / Non-Goals

**Goals:**

- 以机器可执行的断言（而非 grep 结论）作为删除 8201 行死代码的依据。
- 消除构建文件与实际解析结果不符的依赖声明。
- 把「什么能被第三方库替换」固化为可执行判据，阻断重构者对命名契约的误伤。

**Non-Goals:**

- 不切换连接池（Druid → HikariCP），不改动序列化库归属（fastjson2 保留），不触碰模板引擎双策略。
- 不做安全加固（MD5 → BCrypt、JWT 密钥外置、H2 控制台关闭），这些是独立议题。
- 不以降低运行时内存为目标。JVM 懒加载，删除死代码不减少 metaspace；本变更对内存的收益仅来自制品体积。

## Decisions

### D1：断言先行，删除在后

**决定**：ArchUnit 防回流断言在死代码删除**之前**落地并跑绿，其通过结果作为「该区域无编译期引用」的机器证据。

**理由**：删除之后再写断言只能证明「现在是死的」，无法证明「删之前没人用」。断言先行把证据链从人工 grep 升级为可执行验证，同时在删除后继续充当防回流围栏。

**备选方案**：(a) 先删后补断言 —— 证据链断裂，拒绝；(b) 仅依赖全量测试 —— 测试不覆盖「未被调用」这一属性，死代码本就不影响测试通过。

**能力边界（必须写入变更文档）**：ArchUnit 只覆盖编译期类型引用。本批死代码中的 `@CacheEnum` / `@PK` / `@IdempotentLock` / `@Ignore` 注解、反射调用、MyBatis XML 引用均不在其覆盖范围内，SHALL 由一次性人工核查补充。

**执行记录（任务 1.2 完成，2026-10-15）**：

- 断言落点 `aicode/src/test/java/com/aicode/architecture/VendoredDeadCodeIsolationTest.java`，依赖 `com.tngtech.archunit:archunit-junit5:1.5.1`（已验证可读取 Java 25 的 class 文件版本）。
- 死代码区域定义：`com.aicode.core.tools.core..`；豁免集为该区域内仍被生产代码引用的 3 个类 —— `StringHelper`、`Field`、`typemapping.DatabaseDataTypesUtils`。
- **正向结果**：断言在删除动作之前通过，用时 1.198 s，构成该区域无编译期引用的机器证据。
- **反向对照**：临时移除豁免集中的 `StringHelper` 后，断言检出 **9 处** 违规（首个为 `com.aicode.config.template.TemplateData.<init>`），随后恢复。反向对照证明断言并非空跑 —— 它确实导入了真实类并能检出真实依赖。

**人工核查执行记录（任务 3.1 完成，2026-10-15）** —— 覆盖 ArchUnit 能力边界之外的三类引用：

| 检查项 | 方法 | 结果 |
|---|---|---|
| 反射调用 | 全仓检索 `Class.forName` / `getDeclaredMethod` / `getDeclaredField` / `newInstance` / `getMethod` | 命中**全部落在死集内部**（`BeanHelper`、`paranamer/DefaultParanamer`、`DateHelper`、`model/java/*`、`XMLHelper`、`CacheEnumTools` 的自引用）；aicode 侧命中的是 `ProjectController` / `TemplateData` / `MapClassTable` 等活类，与死集无关 |
| 注解引用 | 全仓检索 `@CacheEnum` / `@PK` / `@IdempotentLock` / `@Ignore` | 真实使用点 **0 个**（唯一命中是架构断言的 Javadoc 文本）。`core/annotation` 下 5 个类（`CacheEnum`、`IdempotentLock`、`Ignore`、`PK`、`CacheEnumTools`）整体为零引用 |
| 资源文件 | 全仓检索 `*.xml` / `*.yml` / `*.json` / `*.properties` 中的死类名 | **0 命中** |

**结论**：该区域无任何外部引用方，可安全物理删除。

### D2：命名转换保留自研实现，以快照测试固化

**决定**：`StringHelper.toJavaClassName` / `toJavaVariableName` / `capitalize` 与 `StringTools.humpToLine` SHALL NOT 被 hutool `NamingCase` 或 commons-lang3 替换，改为补齐行为快照测试。

**理由**：这四个方法的输出直接是生成工程的类名、字段名、枚举常量名与前端文件名。实测差异：

| 现有行为（已被 `StringHelperTest` / `StringToolsTest` 断言固化） | 第三方等价 API 的行为 |
|---|---|
| `toJavaClassName("a_b_c_d")` → `"Abcd"` | `NamingCase.toPascalCase` → `"ABCD"` |
| `capitalize("aBC")` → `"ABC"`（仅改首字符） | `StringUtils.capitalize` → `"Abc"`（其余全小写） |
| `humpToLine("URL")` → `"u-r-l"` | `NamingCase.toKebabCase` → `"url"` |

替换的后果是所有历史生成工程的类名与前端产物文件名发生不可逆变化，且与外部模板仓的约定脱节。

**备选方案**：(a) 直接替换并同步修改外部模板仓 —— 需协调第三方仓库，超出本变更范围；(b) 替换但保留旧行为做适配层 —— 等于把自研实现包一层，无收益。

### D3：Bean Validation 依赖移除，规范同步记录前置条件

**决定**：移除 `spring-boot-starter-validation`（全仓含测试的 Bean Validation 注解命中数为 0），并同步改写 `AGENTS.md §6.4`，明确记录该机制当前未启用、以及重新启用必须先恢复依赖。

**理由**：依赖白进制品约 1.4 MB。风险不在于移除本身，而在于规范仍将其列为推荐做法 —— 若不记录前置条件，后来者添加 `@Valid` 会**编译通过但完全不生效**（无对应 starter 时注解不被处理），属最难排查的静默失败。

**备选方案**：(a) 保留依赖待补齐校验 —— 保留 1.4 MB 空依赖；(b) 移除依赖并补齐 `@Valid` 校验 —— 工作量显著超出本变更，且会改动 44 个前端端点的入参行为。

### D4：`Field` 与 `MapFieldColumn` 去重采用抽取公共基类，而非继承

**决定**：在 `common` 中新增无框架注解的 `AbstractField`，由 `Field` 与 `MapFieldColumn` 共同继承。SHALL NOT 采用 `Field extends MapFieldColumn`。

**理由**：两条硬约束使直接继承不可行。

1. **模块依赖红线**：`Field` 在 `common`，`MapFieldColumn` 在 `facade`，而依赖方向是 `facade → common`。`Field extends MapFieldColumn` 需要 `common → facade`，直接违反 §5.2 / §6.2 并制造循环依赖。
2. **Lombok 继承**：`MapFieldColumn` 使用 `@Builder`。Lombok 的类级 `@Builder` 不支持继承，子类需要 `@SuperBuilder`，会让两个类的构造方式同时变化，波及所有构造调用点。

抽取基类同时解决两者：基类放在 `common` 不引入 MyBatis-Plus 依赖，`MapFieldColumn` 在 `facade` 侧补 `@TableField` / `@Schema`，各自构造方式保持不变。

**备选方案**：(a) `Field extends MapFieldColumn` —— 违反红线，拒绝；(b) 保持重复不改 —— 保留「改一处漏一处」隐患；(c) 把 `Field` 上移到 `facade` —— `TemplateData` 位于 `aicode`，会新增 `aicode → facade` 之外的新耦合面，收益不足。

### D5：JSON round-trip 的替换单列为独立任务并加模板快照门

**决定**：`FreemarkerHelper` / `BeetlHelper` 的 `JSON.parseObject(JSON.toJSONString(x), Map.class)` 改为 hutool `BeanUtil.beanToMap`，但 SHALL 作为独立任务，且 SHALL 以模板渲染快照测试通过为前提。

**理由**：该转换 wasteful（bean → String → Map），但**并非无行为差异**。fastjson2 的 `JSON.toJSONString` 默认**省略 null 字段**，而 hutool `BeanUtil.beanToMap` 默认**保留 null 值**（键存在、值为 null）。`TemplateData` 存在可为 null 的字段，模板侧若用 `<#if field??>` 判断键存在性，两种行为结果不同。

因此替换必须显式指定忽略 null 的选项以对齐既有语义，且必须验证。这是本变更中**唯一可能影响生成产物内容**的改动，故单独成任务并置于验证门之后。

**备选方案**：(a) 保持不变 —— wasteful 但已被现有测试覆盖，零风险；(b) 换 `DefaultObjectWrapper` 让 Freemarker 直接访问 bean —— 绕开 Map 化，但 Beetl 侧需另一套机制，反而增加双实现分歧。

**执行结论（任务 7.4，2026-10-15）—— 本决策的假设被实测证伪，改选方案 (a) 保持不变**

本设计预判的风险是「null 键存在性」。实际实施时写了逐键比对测试，暴露出**更大的一条**：

| 展开方式 | 键的命名来源 | 实际产出的键 |
|---|---|---|
| fastjson2 `JSON.parseObject(JSON.toJSONString(...))` | **getter 名** | `basePackage` / `className` / `dashedCaseName` |
| hutool `BeanUtil.beanToMap(bean, true, false)` | **字段名** | `base_package` / `class_name` / `dashed_case_name` |

外部模板仓 `aicode_template` 的 `.ftl` / `.btl` 按 getter 名取值（`${className}`、`${dashedCaseName}`）。换成 hutool 会让**所有模板变量取不到值**，生成产物静默改变 —— 而编译与既有测试都可能仍然通过。

次要差异亦被实测推翻：`isIgnoreNull=true` 并不能复刻 fastjson2 的省略语义，hutool 返回的 Map 仍包含值为 `null` 的键。

**处置**：**保留 fastjson2 往返**，两处代码加注说明为何不可替换，并新增 `TemplateDataExpansionTest` 把该结论固化为可执行断言 —— 只要有人再次尝试替换，测试即失败。

**教训**：D5 原本把这次替换评为「低风险」，理由是「已被现有测试覆盖」。实际上**现有测试并未覆盖键名本身** —— `CodeGenPipelineTest` 验证的是占位符替换路径，不校验展开后的键集。真正的防线是本次新写的逐键比对测试。**「已有测试通过」不等于「该改动安全」**，前提是测试真的断言了被改动的那个属性。

### D6：BOM 提升单独成批验证

**决定**：`common` / `facade` 的 BOM 由 3.5.16 提升至 4.1.1 SHALL 作为独立批次，不与其他改动混合验证。

**理由**：该提升会使 `common` / `facade` 的测试框架跨主版本（5.12.2 → 6.0.3），是全批次中唯一有真实回归风险的改动。混入其他批次会让失败归因困难。

**备选方案**：与其他依赖收敛同批 —— 失败时无法定位是 BOM 提升还是依赖移除导致，拒绝。

### D7：`commons-io` 生效版本先实测再决定

**决定**：SHALL NOT 直接采信任何一方结论，SHALL 先执行依赖树查询命令确认 `commons-io` 的实际生效版本，再决定是删除声明还是升级。

**理由**：两个独立分析给出冲突结论 —— 一方认为显式锁定的 2.4 真实生效（依据制品内 jar 名），另一方认为代码使用了 2.5 才引入的 `FileUtils.readFileToString(File)` 单参重载，故 2.4 从未生效（依据 API 可用性推断）。API 可用性推断更硬，但两者均非直接观测。

**备选方案**：直接按「删除死锁版」处理 —— 若判断错误，会在移除后暴露出真实的版本缺口。

**执行结论（任务 5.1 完成，2026-10-15）** —— 两个独立分析结论冲突，以实测落锤：

| 验证手段 | 结果 |
|---|---|
| `dependencyInsight --dependency commons-io` | 显示 `2.4 (selected by rule)`，`Requested` 为空（无请求方可见） |
| 自定义任务打印 `runtimeClasspath.resolvedConfiguration.resolvedArtifacts` | **`RESOLVED: commons-io:commons-io:2.4 -> commons-io-2.4.jar`** |
| Boot 4.1.1 BOM 检索 `commons-io` | **无任何提及** —— BOM 确实不管理该依赖 |
| Gradle 缓存目录 | 存在 `2.4`（同时存在 2.2 / 2.7 / 2.11.0 / 2.15.0 / 2.16.1 / 2.20.0） |
| `javap` 检查 `commons-io-2.4.jar` 的 `FileUtils` | **含** `readFileToString(File)` 单参重载 |

**判定**：另一分析的核心论据（「代码用了 2.5 才有的 API，故 2.4 从未生效」）**不成立** —— 该重载在 2.4 中已存在。`commons-io:2.4` 是**真实生效版本**，且因 BOM 不管理它，构建文件与运行期事实一致，**不属于「死锁版」**。

**处置**：**不走删除，改为升级并显式声明**。2.4 发布于 2012 年、存在已知 CVE，且无 BOM 兜底 —— 一旦其他依赖调整传递版本，会无声漂移到未验证的版本。

> **方法论教训**：这是本次变更中「看似可由静态推断定论、实则必须实测」的一个实例。一方基于 API 可用性推断、另一方基于制品内容观测；**观测胜出**。故 spec 中「移除前的实际版本可被核实」这条要求不是形式条款。

### D7 附记：制品体积实测推翻提案预测（任务 5.9，2026-10-15）

提案阶段预测「fat jar 减少约 5 MB」，**实测净减 0.56 MB**，预测错误近 10 倍。原因拆解：

| 项 | 影响 | 说明 |
|---|---|---|
| 基线取值错误 | 起点被低估 2.2 MB | 会话开始时看到的 `59 MB` 是**过期构建产物**。用 `git stash` 在干净 HEAD 上重建得真实基线 **61.2 MB** |
| `spring-boot-starter-validation` | **0** | `hibernate-validator`(1.37 MB) + `jakarta.validation-api`(104 KB) 仍留在制品内 —— 其真正来源是 `springdoc-openapi-starter-common` → `spring-boot-validation`，而非被移除的 starter。移除该 starter **不产生任何体积收益** |
| `hutool-all` → `hutool-core` | **-1.2 MB** | 唯一的主要收益来源 |
| `commons-io` 2.4 → 2.20.0 | **+0.17 MB** | 升级带来体积增加（2.20.0 = 551 KB） |
| `swagger-annotations-jakarta` | **+50 KB** | 新增的最小注解构件 |
| 删除 8333 行死代码 | 小幅减少 | 影响 `BOOT-INF/classes` 体积 |

**结论修正**：移除 `spring-boot-starter-validation` 的**理由是正确性而非体积** —— 它确实零使用，保留即违反「依赖声明 SHALL 有实际使用」。若改以「省 1.4 MB」为理由做决策，会得出错误结论（实际省 0）。依赖收敛在本项目的真实价值是**消除版本错配与未声明的传递依赖**，而非瘦身。

**对 spec 的影响**：`dependency-hygiene/spec.md` 的各条要求均不依赖体积假设，无需修订。

## Risks / Trade-offs

**[Bean Validation 移除后注解静默失效]** → 规范条文 SHALL 明确记录「重新启用需先恢复依赖」，并在 `§6.4` 保留该前置条件的显式表述。

**[`BeanUtil.beanToMap` 改变 null 键存在性，影响模板渲染]** → 显式配置忽略 null 以对齐 fastjson2 语义；该改动置于独立任务，并以模板渲染快照测试为完成门。

**[BOM 提升导致 `common` / `facade` 测试跨 JUnit 大版本失败]** → 单独成批；若回归失败，先评估是否回退 BOM 提升而不阻塞其余批次。

**[死代码中残留反射 / 注解 / XML 引用，ArchUnit 查不出]** → 删除前执行一次性人工核查（反射调用、`@CacheEnum` / `@PK` / `@IdempotentLock` / `@Ignore` 注解使用点、MyBatis XML 引用），并在变更文档中记录核查结果。

**[`facade` 换成 `swagger-annotations-jakarta` 后，注解在运行时缺失导致 OpenAPI 文档异常]** → `aicode` 仍保留完整的 `springdoc` starter（文档生成在运行时于 `aicode` 侧发生）；验证方式为启动后访问 API 文档 UI 确认渲染正常。

**[`Field` / `MapFieldColumn` 去重引入构造方式变化，波及调用点]** → 基类不含 `@Builder`，两个子类的构建方式保持不变；编译期即可暴露所有受影响的构造调用点。

**[删除 100+ 个 `public` 方法属于对外破坏性变更]** → 已在提案中标记 BREAKING。当前无生产调用方，且 `api-deprecation-policy` 的四阶段流程限定于 REST 端点下线，故不走 30 天观察期；其「内部 grep 二次核查」的实质由删除前的人工核查承担。

## Migration Plan

分批交付，每批独立可验证、独立可回滚：

1. **0a** 防回流断言落地并跑绿（机器证据）
2. **0b** 删除死代码与死方法，人工核查通过后执行
3. **1** 依赖收敛与死锁版移除（0 源文件改动为主）
4. **1b** BOM 提升，单独回归
5. **2** 低风险重构（去重、round-trip 替换、null 防护、编码显式化）
6. **3** 规范条文同步（`AGENTS.md §6.4` / `§6.5` / `§7.10` / `§11.3`）

**回滚策略**：各批为独立的提交，回滚以批次为单位。`0b` 涉及物理删除 8201 行，若需回滚应通过版本控制 revert 而非手工恢复。规范条文同步（批次 6）与代码批次可解耦，即使代码回滚，规范也 SHALL 随代码状态一并回滚以避免再次脱节。

## Open Questions

- `BeanUtil.beanToMap` 的键来源（getter 优先还是字段优先）需在实现时以最小样例实测确认，不改变本设计的方法选择与验证门。
- `commons-io` 的实际生效版本由任务中的依赖树查询命令落锤，其结果只影响「删除声明」与「升级并显式声明」二选一，不影响任务拆分。
