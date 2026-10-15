# Proposal

## Why

本变更是 Java 25 / Spring Boot 4.1 升级系列中的**第一个技术台阶**，目标平台是 Spring Boot 4.1.1（Jakarta EE 11 / Spring Framework 7 / Jackson 3）。直接从 3.3.9 跨到 4.1.1 会一次性引爆跨越 3.4、3.5、4.0 三个版本的全部废弃 API 变更，故障域过大、无法定位是哪一类变更导致的。

Spring Boot 的官方迁移指引明确建议：「在开始升级前，先升级到最新的 3.5.x，确保你针对的是该分支最新的依赖，并复查所有已废弃方法的调用——这些会在 4.0 中被移除。」

先落到 3.5.16（当前 3.x 分支最新维护版）可以把故障切成两类：

- **本变更捕获**：3.x 内部的小版本行为调整、已废弃 API 的首次告警
- **后续变更捕获**：Spring Boot 4 特有的模块化拆包、Jakarta EE 11、Jackson 3、Spring Framework 7 的 API 移除

同时本变更是 `@MockBean` → `@MockitoBean` 迁移的唯一窗口——`@MockBean` 在 Boot 4.0 已被移除，若拖到 4.1 变更里改，会与包迁移、starter 改名混在一起。

## What Changes

- Spring Boot Gradle 插件 `3.3.9` → `3.5.16`；`io.spring.dependency-management` `1.1.4` → `1.1.7`
- Gradle Wrapper `8.5` → `8.14.3`（Spring Boot 4.1 的最低要求是 8.14+；同时 Gradle 8.5 已在构建输出中声明与 Gradle 9 不兼容）
- **BREAKING**（测试代码）：`org.springframework.boot.test.mock.mockito.MockBean` → `org.springframework.test.context.bean.override.mockito.MockitoBean`，涉及 6 个测试类共 54 处（`grep -rh '^\s*@MockBean' --include='*.java' aicode/src/test \| wc -l`）
- springdoc-openapi `2.3.0` → `2.8.17`（3.x 分支最新兼容版，为后续 Boot 4 的 3.x 切换铺路）
- 删除 6 个代码中零引用的依赖：`commons-fileupload:1.2.1`（javax.servlet 基座）、`commons-collections:3.2.1`、`caffeine:3.1.8`、`pagehelper:6.0.0`、`commons-lang:20030203`、`mybatis-plus-generator`
- 消除 `commons-lang3` 的重复声明（当前同时声明 3.4 与 3.18.0，后者生效）
- 修复 `ApplicationClosedEventListener` 中 `connector` 为 null 时的 NPE（实测在 `@SpringBootTest(MOCK)` 下必现）
- 清理 Gradle 8.x 废弃 DSL，为后续 Gradle 9 升级铺路

本变更 SHALL NOT 改变任何对外可观测行为；验收由 `2026-10-14-add-api-contract-tests` 建立的契约测试守护。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

无。

> 本变更的 `.openspec.yaml` 设置了 `skip_specs: true`。同分支内的依赖版本推进不产生可观测的行为契约变化；对外契约由 `api-contract` 能力（已由上一变更建立）持续守护，SHALL NOT 为满足校验而编造 spec。

## Impact

- **修改文件**：
  - `aicode/build.gradle`（Boot 插件版本、springdoc、删除依赖、lang3 去重）
  - `aicode/gradle/wrapper/gradle-wrapper.properties`（Gradle 8.5 → 8.14.3）
  - `common/build.gradle`、`facade/build.gradle`（清理废弃 DSL / 依赖）
  - 5 个测试类：`LoginCtrlShtmlPathTest`、`MapRelationshipControllerTest`、`ProjectControllerDownloadTest`、`ProjectInitEndpointTest`、`ProjectJobServiceImplConcurrencyTest`、`SqlReverseParseTest` 中的 `@MockBean` 迁移
  - `aicode/src/main/java/com/aicode/config/ApplicationClosedEventListener.java`（NPE 修复）
- **不改**：生产逻辑、API 契约、前端静态资源、生成主链路
- **兼容性风险点**：`commons-lang3` 从解析结果中移除 3.4，需确认无代码依赖 3.4 独有 API；`commons-fileupload` 删除前需二次确认（含反射调用）
- **验收命令**：`./aicode/gradlew test`
