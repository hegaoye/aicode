# Design

## Context

本项目当前处于 Spring Boot 3.3.9 + Spring Framework 6.1.16 + Gradle 8.5 + Java 21。目标链路是 4.1.1 + SF 7.0.9 + Gradle 9.x + Java 25。

跨越 3 个大版本直升的风险是可量化的。已实证的 Spring Framework 7 破坏性变更里，有一条是**编译期爆炸**、有两条是**静默回归**：

| 变更 | 类型 | 影响 |
|------|------|------|
| `PathMatchConfigurer.setUseSuffixPatternMatch/setUseTrailingSlashMatch` 被移除 | 编译失败 | `ContextConfiguration` |
| `@MockBean`/`@SpyBean` 被移除 | 编译失败 | 5 个测试类 |
| Jackson 3 成为默认，自建 Jackson2 `ObjectMapper` bean 失效 | **静默** | 前端 ID 精度 |
| `.shtml` 后缀匹配能力消失 | **静默** | 前端 100% 不可用 |

如果一次性直升，这四类变更会在同一个 PR 里互相掩盖：编译错误修完之后，静默回归无从察觉。

## Goals / Non-Goals

**Goals:**

- 落在 Spring Boot 3.5.16，同分支内的版本推进尽量零行为变化
- 清空 3.x 分支内的全部废弃 API 用法（尤其 `@MockBean`），使后续 Boot 4 变更只剩下 Boot 4 特有的问题
- 清理历史遗留依赖与重复声明，减少后续升级的解析噪声
- 在本变更阶段就暴露 Gradle 8.5 → 9.x 的迁移障碍

**Non-Goals:**

- 不引入 Jackson 3、不改 Jackson 配置（那是 Boot 4 变更的事）
- 不动 `.shtml` 后缀兼容实现（现在是能工作的，只在 Boot 4 变更里被动调整）
- 不引入 `spring-boot-properties-migrator`（3.x 内不需要）
- 不重构业务代码

## Decisions

### 决策 1：Gradle 先到 8.14.3，再到 9.x

Spring Boot 4.1 的系统要求是「Gradle 8.x（8.14 或更高）与 9.x」。实测当前构建已输出：

```
Deprecated Gradle features were used in this build,
making it incompatible with Gradle 9.0.
```

即 Gradle 8.5 **拒绝** 9.x 语义，不是警告而是失败。把 Gradle 提到 8.14.3 有三重收益：
1. 满足 Boot 4.1 的最低要求，后续 Boot 4 变更不需要再动 wrapper
2. 8.14 已经吸收了大部分废弃 API 修复
3. 它是一个**独立可验证**的步骤——wrapper 升级失败能立刻定位，不会与代码变更混淆

**备选**：直接上 9.x。拒绝，因为届时 wrapper 变更与 JDK 25 变更会同时发生，失败时无法判断是 Gradle 语义问题还是 Java 25 兼容问题（ByteBuddy / Mockito 对新 class 文件版本的支持）。

### 决策 2：`@MockBean` → `@MockitoBean` 在本变更完成

`@MockBean` 在 Spring Boot 3.4 被废弃、4.0 被移除。放在本变更做有三个好处：
1. Spring Boot 3.4+ 已提供 `@MockitoBean`，编译通过即验证了替换写法正确
2. 这是一次纯机械替换（`import` 换行 + 注解名），与 Boot 4 的包迁移分离后，每个 PR 的 diff 都可读
3. 若 `@MockBean` 迁移存在语义差异（如按类型 vs 按名称注入），能在 3.5 上单独观察，不会与 Boot 4 的其他断裂混在一起

**已知约束**（须写入 tasks）：`@MockitoBean` / `@MockitoSpyBean` **不允许标注在 `@Configuration` 类上**，只能作为测试类字段。现有用法全部是测试类字段，无冲突。

**需注意的行为差异**：迁移指引指出 `@MockBean` 与 `@MockitoBean` 在「一个字段 mock 多个同类型 bean」的解析方式上不同。当前测试均为「一个字段对应一个具体类型」，无此场景；但 `ProjectInitEndpointTest` 等文件里连续 12 个 `@MockBean` 指向不同接口，迁移后需确认逐字段注入仍成立。

### 决策 3：springdoc 停在 2.8.17 而非直上 3.1.1

springdoc-openapi 3.x 是为 Spring Boot 4 设计的，2.x 与 4.x 不兼容。本变更只推进到 2.x 分支的最后一版（2.8.17），把「2.x → 3.x」这个大跳跃留给 Boot 4 变更，与 Jackson 3 迁移同批处理。

### 决策 4：一次性删除 6 个零引用依赖，但 commons-lang3 分两步

六个依赖经全仓 grep 确认代码零引用：

| 依赖 | 版本 | 删除理由 |
|------|------|---------|
| `commons-fileupload` | 1.2.1 | **javax.servlet 基座**，在 Jakarta 项目中是 classpath 定时炸弹；已确认无 `MultipartFile` / `ServletFileUpload` 引用 |
| `commons-collections` | 3.2.1 | 3.x 版本，已被 commons-lang3 取代 |
| `caffeine` | 3.1.8 | 无引用 |
| `pagehelper` | 6.0.0 | 无引用（分页走 MyBatis-Plus `PaginationInnerInterceptor`） |
| `commons-lang` | 20030203 | 2013 年的遗留 |
| `mybatis-plus-generator` | 3.5.7 | 无引用（`aicode` 用它生成代码，但生成器本身不是运行期依赖；`facade` 也无引用） |

**`commons-lang3` 单独处理**：当前 `aicode/build.gradle` 同时声明 3.4 与 3.18.0，Gradle 解析取高版本（3.18.0）。AGENTS.md §10 明确警告「不要顺手清理，旧版 API 表面可能被代码依赖」。因此本变更**只删除 3.4 那行声明**，不改最终解析版本——这既去掉了重复声明，又不改变运行时行为。删除前需 grep 3.4 独有 API（如 `StringUtils.containsIgnoreCase` 等早期新增方法）在 3.18 中仍存在。

**备选**：保留所有依赖只改版本。拒绝——`commons-fileupload` 的 javax.servlet 基座在后续 Jakarta 生态升级中会持续制造告警与潜在冲突，现在清掉成本最低。

### 决策 5：`GracefulShutdown` 的 NPE 顺带修掉

实测每个 `@SpringBootTest(MOCK)` 用例结束时都会打出：

```
WARN  ApplicationClosedEventListener - Exception thrown from ApplicationListener handling ContextClosedEvent
java.lang.NullPointerException: Cannot invoke "Connector.pause()" because "this.connector" is null
	at GracefulShutdown.onApplicationEvent(ApplicationClosedEventListener.java:34)
```

原因：`connector` 仅在 `TomcatConnectorCustomizer.customize()` 中赋值，MOCK 环境下嵌入式 Tomcat 不启动，该回调不触发。

**在本变更修的理由**：Boot 4 变更本来就要改这个文件的 import（`TomcatConnectorCustomizer` 包迁移），两个变更碰同一个文件不如一次做完；且 NPE 属于既有缺陷，与升级无关，不应污染升级 diff。

**修法**：`onApplicationEvent` 开头加 `if (this.connector == null) { log.debug(...); return; }`。SHALL NOT 吞掉真实异常——只在 connector 确实为 null 时短路。

### 决策 6：Gradle 废弃 DSL 清理

`AGENTS.md` §10 提示根 `build.gradle` 为空、所有配置在子模块。本变更顺带清理：
- `aicode/build.gradle` 中 `configurations { compileOnly { extendsFrom annotationProcessor } }` **重复声明两次**
- `facade/build.gradle` 中的 `jcenter()`（已废弃且已停止服务）
- `sourceCompatibility = '21'` → 保留（改 `toolchain` 属于 JDK 25 变更的范围，避免越界）

## Risks / Trade-offs

| 风险 | 影响 | 缓解 |
|------|------|------|
| 依赖删除后编译失败（漏检的反射/动态引用） | 构建中断 | 每个依赖删除后立即 `./gradlew compileJava` 验证；`commons-fileupload` 额外 grep `Multipart` / `DiskFileItem` |
| `@MockitoBean` 迁移后某些 mock 失效（按类型解析差异） | 测试假绿 | 迁移后逐个测试类跑全量，SHALL NOT 只跑冒烟 |
| Gradle 8.14 与 Spring Boot 3.5.16 插件的兼容性 | 构建失败 | 两项均可在 Boot 官方 system requirements 中查到支持范围；失败时可单独回退其中一项 |
| commons-lang3 去重后暴露 3.4 独有 API | 编译失败 | 删除前 grep 已知 3.4→3.18 新增 API；若发现依赖，只保留 3.4 声明并在本变更 tasks 中记录为「后续由专门变更处理」 |
| springdoc 2.3.0 → 2.8.17 改变了 `/v3/api-docs` 的输出结构 | Knife4j UI 展示变化 | Knife4j 将在 Boot 4 变更中移除；本变更只需保证 `/v3/api-docs` 返回 200 |
