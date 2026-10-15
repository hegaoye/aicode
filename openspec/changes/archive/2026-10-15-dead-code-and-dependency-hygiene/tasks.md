# Tasks

## 1. 防回流断言（批次 0a）

- [x] 1.1 在 `aicode/build.gradle` 增加 `com.tngtech.archunit:archunit-junit5` 的 `testImplementation`，显式写版本号；验证 `./aicode/gradlew :aicode:dependencies --configuration testRuntimeClasspath` 中出现该构件
- [x] 1.2 新增断言测试，禁止 `com.aicode.facade` 与 `com.aicode.aicode` 的源码引用 `com.aicode.core.tools.core..`；验证该测试在当前代码上通过
- [x] 1.3 断言通过后，将其结果记入 `openspec/changes/dead-code-and-dependency-hygiene/design.md - D1` 的执行记录，作为「该区域无编译期引用」的机器证据
- [x] 1.4 登记该断言测试（落点为 `AGENTS.md §7.1` 现状表与 §11.3 已落地清单，而非 §7.10 —— §7.10 是「待补齐清单」，收录已存在的测试语义不符）；验证条目与实际测试类路径一致

## 2. 命名契约快照测试与测试运行期修复

- [x] 2.1 为 `StringHelper.toJavaClassName` / `toJavaVariableName` 补齐行为快照测试，覆盖多段下划线、全大写缩写、首字母已大写等形态，并显式断言 `a_b_c_d` → `Abcd`；验证测试通过
- [x] 2.2 为 `StringHelper.capitalize` 与 `StringTools.humpToLine` 补齐行为快照测试，显式断言 `aBC` → `ABC`、`URL` → `u-r-l`；验证测试通过
- [x] 2.3 在 `design.md - D2` 记录快照已固化的具体形态清单，作为后续替换第三方等价 API 的比对基准
- [x] 2.4 修复 `common` 与 `facade` 缺失 `junit-platform-launcher` 导致的测试无法启动（**本变更新增范围**，用户 2026-10-15 确认纳入）；验证 `./aicode/gradlew :common:test` 可正常执行
- [x] 2.5 确认 `common` 8 个测试类首次真实运行的结果并处置既有失败；验证全部通过（48 例，`GitToolsCloneCompletenessTest` 按网络守卫预期跳过）

## 3. 删除死代码（批次 0b）

- [x] 3.1 删除前人工核查并记录结果：反射调用点、注解使用点、资源文件引用；验证核查结论为「无使用方」并写入 `design.md - D1`（**结果见下方说明**）
- [x] 3.2 删除 `common/core/tools/core/` 下的死代码区域；验证 `common` 模块编译通过（**实际删除 43 个死类 + `core/annotation` 整个包，含 `paranamer` / `model/java` / `sqlparse` / `AntPathMatcher` / `XMLHelper` / `BeanHelper` 等，见下方说明**）
- [x] 3.3 删除活类中的零调用方法（**迭代删除至不动点，共 43 个方法**，见下方说明）；验证全量编译通过
- [x] 3.4 删除全部 `main()` 调试残留（保留 `Application.main`），并将 `e.printStackTrace()` 改为 `log.error(msg, e)`；验证全仓无残留
- [x] 3.5 执行 `./aicode/gradlew test`；验证全部测试通过（**204 用例 0 失败，2 例按预期跳过**）
- [x] 3.6 将 `PasswordCrypto.getKey()` 改为包私有（暴露密钥，仅同包测试需要）；验证测试仍通过

> **执行说明（任务 3.1–3.6）**
>
> - **3.1 人工核查结论**：反射调用命中全部落在死集内部（自引用）；`@CacheEnum` / `@PK` / `@IdempotentLock` / `@Ignore` 真实使用点 0 个；资源文件命中 0。无外部引用方。
> - **3.2 实际删除范围超出原任务清单**：`core/annotation` 整个包（`CacheEnum` / `CacheEnumTools` / `IdempotentLock` / `Ignore` / `PK`，均为零引用）原仅列入 `CacheEnumTools`，实际 5 个类一并删除；`StringTools` / `Md5` / `JwtToken` 等原清单标注的若干死方法在核查后确认**仍被内部调用**，予以保留（保守处理）。
> - **3.3 采用了迭代删除**：直接检测存在级联误判（删除 `removeCrlf` 后其内部依赖 `tokenizeToStringArray` / `join` 才变为零引用）。最终以「外部零引用 **且** 文件内零调用」为判据，迭代至不动点。
> - **规模**：`common` 从 11244 行降至 **2911 行**，累计删除 **8333 行（74%）**。

## 4. Field 与 MapFieldColumn 去重

- [x] 4.1 在 `common` 新增无框架注解的 `AbstractField`，承载 17 个共有字段与 5 个派生方法；验证 `common` 模块编译通过且未引入 MyBatis-Plus 依赖
- [x] 4.2 `Field` 改为继承 `AbstractField`，删除重复的字段与 `getCheckDate()` / `getCheckState()` / `getCheckPk()` / `getCheckDigit()` / `getUpper()`；验证编译通过
- [x] 4.3 `MapFieldColumn` 改为继承 `AbstractField`，保留自身的 `@TableField` / `@Accessors`；验证编译期暴露全部受影响的构造调用点并逐个修正
- [x] 4.4 执行 `./aicode/gradlew test`；验证全部测试通过（**204 用例 0 失败**）

> **执行说明（任务 4.1–4.4）**
>
> - **重复消除量**：字段声明 34 → 17，派生方法 10 → 5。三个文件合计 255 行（含大量 Javadoc），行数未明显下降但重复已消除。
> - **`toJava()` 未上提到基类 —— 两个类的该方法语义本就不同**：`Field` 在字段名为 `id` 时固定类型为 `java.lang.Long`，`MapFieldColumn` 的该分支被注释掉、一律走类型映射表。原分析称二者「逐字相同」有误。
> - **Lombok 迁移的实际代价**：`@Builder` → `@SuperBuilder`（`builder()` 实测 0 调用点，迁移透明）；`@AllArgsConstructor` 已移除 —— 继承后其签名会静默变为「含基类字段」，保留会造成误导（实测 0 调用点）；`@EqualsAndHashCode(callSuper)` 由 `false` 改为 `true`；`checkDigit` 补 `@Builder.Default` 以保留 `= false` 默认值。
> - **架构断言按预期拦截了新引用**：`AbstractField` 落地后断言立即变红，需显式登记进豁免集。随后又因 Lombok 生成的嵌套 builder 类（`AbstractField$AbstractFieldBuilder`）再次变红，改为豁免「活类及其 `$` 嵌套类」后通过 —— 这验证了 D1 断言确实在起作用，而非形同虚设。
> - **子类需经 getter/setter 访问基类字段**：`AbstractField` 字段为 private，`Field.toJava()` / `MapFieldColumn.toJava()` / 7 参构造器已改为 setter 形式。

## 5. 依赖收敛与规范同步（批次 1）

- [x] 5.1 核实 `commons-io` 实际生效版本并记入 `design.md - D7`；验证结论有命令输出支撑而非推断（**已落锤：2.4 真实生效**）
- [x] 5.2 按 5.1 结论处理 `commons-io` 声明；验证依赖树中不再出现 2.4（**升级至 2.20.0**，非删除）
- [x] 5.3 三个模块的 `hutool-all` 替换为 `hutool-core`，并把 `facade` 对齐到 5.8.40；验证全量编译通过且 `IdUtil` / `DateUtil` / `FileUtil` 调用点无报错
- [x] 5.4 `facade` 的 `springdoc-openapi-starter-webmvc-ui` 替换为 `swagger-annotations-jakarta:2.2.55`；验证全量编译通过
- [x] 5.5 删除 `aicode` 的 `spring-boot-starter-validation`；验证全仓 Bean Validation 注解检索仍为 0 命中
- [x] 5.6 删除 `druid:1.2.22` 与 `junit-jupiter:5.10.2` 两处死锁版；验证制品内 `druid-1.2.28.jar` 与 starter 版本一致
- [x] 5.7 启动应用并访问 API 文档，确认接口列表正常渲染；验证文档可访问（**独立端口 8090 起实例，未干扰用户正在运行的 IDE 实例**）
- [x] 5.8 执行 `./aicode/gradlew test`；验证全部测试通过
- [x] 5.9 对比制品体积；验证体积变化已量化并记入 `design.md - D7 附记`
- [x] 5.10 同步改写 `AGENTS.md §6.4`：标注 Bean Validation 未启用，并写明重新启用必须先恢复依赖
- [x] 5.11 同步改写 `AGENTS.md §6.5`：移除对零调用的 `StringTools.convertObject/convertList` 的推荐表述

> **执行说明（任务 5.1–5.11）**
>
> - **5.1/5.2 `commons-io` 判定推翻另一分析的结论**：`javap` 证实 `commons-io-2.4.jar` **含** `readFileToString(File)` 单参重载，故「2.4 从未生效」的推断不成立。实测 `resolvedArtifacts` 为 `commons-io-2.4.jar`，且 Boot 4.1.1 BOM 不管理该依赖 → 属真实生效版本，改为**升级至 2.20.0**（2.4 发布于 2012 年且有已知 CVE）。
> - **5.9 制品体积预测被实测推翻**：提案阶段预测 -5 MB，**实测净减 0.56 MB**（61.2 → 60.7 MB）。主因是 `hibernate-validator`(1.37 MB) 的真正来源为 `springdoc-openapi-starter-common` → `spring-boot-validation`，**移除 starter 不产生任何体积收益**；且基线取值曾误用过期构建产物（59 MB → 真实 61.2 MB）。详见 `design.md - D7 附记`。
> - **5.5 移除 validation 的理由是正确性而非体积** —— 它确实零使用，保留即违反「依赖声明 SHALL 有实际使用」。若以省体积为由决策会得出错误结论。
> - **5.7 运行时验证**：`/v3/api-docs` 返回 200（36 KB、OpenAPI 3.1.0、43 个 path），`/swagger-ui.html` 返回 302；抽查 `MapRelationshipVO` 各字段的 `@Schema` description 均正常渲染，**证实 `facade` 换包后注解未失效**。

## 6. BOM 提升（批次 1b，单独回归）

- [x] 6.1 将 `common` 与 `facade` 的 `spring-boot-dependencies` 由 3.5.16 提升至 4.1.1；验证两个模块导入的 BOM 版本一致
- [x] 6.2 单独执行 `./aicode/gradlew test`；验证测试框架跨主版本后全部测试通过

> **执行说明（任务 6.1–6.2）**：`junit-jupiter` 与 `junit-platform-launcher` 由 **5.12.2 跨主版本升至 6.0.3**，全量 **204 用例 0 失败**，无回归。这是本变更中唯一有真实回归风险的改动，已按设计单独成批验证通过。AGENTS.md §10 此前声称「BOM 版本需与 aicode 保持一致」而实际不一致的状态就此消除。

## 7. 低风险重构（批次 2）

- [x] 7.1 将 3 处 `com.alibaba.druid.util.StringUtils` 改为 `org.apache.commons.lang3.StringUtils`，逐处核对 `isEmpty` 与 `isBlank` 的语义差异；验证编译通过且行为不变
- [x] 7.2 为 `DatabaseDataTypesUtils` 的判定方法补 null 防护并返回 false；验证新增 null 入参的测试用例通过
- [x] 7.3 `SqlEmitter` 写文件改为显式 UTF-8 编码；验证产出 SQL 文件的中文内容不乱码
- [x] 7.4 验证模板数据展开方式的键集一致性；验证结论有逐键比对测试支撑
- [x] 7.5 执行 `./aicode/gradlew test`；验证全部测试通过（**215 用例 0 失败**）

> **执行说明（任务 7.1–7.5）**
>
> - **7.1 存在语义陷阱，已按等价关系处理**：6 处调用全为 `StringUtils.isEmpty(String)`。druid 的 `isEmpty` 把「仅空白字符」也视作空，commons-lang3 的 `isEmpty` **不**视作空 —— 直接换会让 `"   "` 的 projectCode 通过校验，**行为被放宽**。故改用 `isBlank` 保持原校验强度。
> - **7.2 测试抓到一个任务描述外的遗漏**：`getPreferredJavaType(String)` 自身也缺 null 防护（`sqlType.toLowerCase()` NPE），已一并补上。新增 `DatabaseDataTypesUtilsTest`（8 例），`common` 测试数从 48 增至 56。同时删除零调用的 3 参重载 `getPreferredJavaType(String,int,int)`（其 map 查找缺 `toLowerCase`，`VARCHAR` 会误返回 `Object`）。
> - **7.4 结论与设计预判相反：SHALL NOT 替换，保留 fastjson2 往返**。详见 `design.md - D5` 的执行结论 —— fastjson2 按 getter 名产出键，hutool 按字段名产出键，键集不一致会让外部模板的所有变量取不到值。`isIgnoreNull=true` 也不能复刻 fastjson2 的 null 省略语义。已新增 `TemplateDataExpansionTest` 固化该结论。
> - **7.3** `SqlEmitter` 原用 `tsql.getBytes()`（平台默认编码），非 UTF-8 平台会把生成 SQL 里的中文注释写成乱码，已显式指定 UTF-8。

## 8. 归档

- [x] 8.1 执行 `openspec validate --strict dead-code-and-dependency-hygiene`；验证无校验错误
- [x] 8.2 执行 `openspec archive dead-code-and-dependency-hygiene`；验证 `dependency-hygiene` 与 `runtime-platform` 的 delta 已并入 `openspec/specs/`
- [x] 8.3 复核 `AGENTS.md` 与实际状态一致；验证无残留失效表述

> **8.3 同步范围**：§6.3（继承实体禁用 `@Builder`，须 `@SuperBuilder`）、§6.4（Bean Validation 未启用 + 重新启用前置条件）、§6.5（移除零调用死方法推荐 + 模板展开方式说明）、§6.10（lombok 不写版本号，BOM 管理 1.18.46）、§7.1 与 §11.3（测试类数 24 / 用例 215）、§10（新增 5 条踩坑点：BOM 已统一、commons-io 必须显式写版本、`resolvedArtifacts` 才是实际生效版本、`junit-platform-launcher` 缺失的症状与排查、ArchUnit 豁免集）。
