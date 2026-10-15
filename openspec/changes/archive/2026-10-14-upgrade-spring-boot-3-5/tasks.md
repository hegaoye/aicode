# Tasks

## 1. Gradle 构建链

- [x] 1.1 更新 `aicode/gradle/wrapper/gradle-wrapper.properties`：`gradle-8.5-bin.zip` → `gradle-8.14.3-bin.zip`
- [x] 1.2 执行 `./aicode/gradlew --version`，确认版本为 8.14.3 ✅ 实测 `Gradle 8.14.3`
      > ⚠️ **仅升级 wrapper 并不足以消除弃用告警**：Gradle 8.14.3 仍报「incompatible with Gradle 9.0」。用 `--warning-mode all` 定位到根因是 Groovy DSL 的**空格赋值语法**（`maven { url "..." }`），涉及 3 个 build.gradle 共 15 处。已在任务 1.3/1.4 一并修复，修复后告警清零
- [x] 1.3 清理 `aicode/build.gradle` 中重复的 `configurations { compileOnly { extendsFrom annotationProcessor } }` 块（声明两次，保留前一处）
- [x] 1.4 删除 `facade/build.gradle` 中的 `jcenter()` 仓库声明（已废弃）
      > 连带修复 Gradle 弃用语法：3 个模块的 `repositories { maven { url … } }` → `url = uri(…)`
- [x] 1.5 执行 `./aicode/gradlew :aicode:dependencies --configuration runtimeClasspath` 留档，作为后续变更的对比基线

## 2. Spring Boot 版本

- [x] 2.1 `aicode/build.gradle`：`org.springframework.boot` 插件 `3.3.9` → `3.5.16` ✅
- [x] 2.2 `aicode/build.gradle`：`io.spring.dependency-management` `1.1.4` → `1.1.7` ✅
- [x] 2.3 `org.springdoc:springdoc-openapi-starter-webmvc-ui`：`2.3.0` → `2.8.17`（`aicode` 与 `facade` 两处）✅
- [x] 2.4 执行 `./aicode/gradlew :aicode:compileJava`，确认生产代码编译通过且无新增告警 ✅
      > 三模块 `compileJava` 全部 BUILD SUCCESSFUL

## 3. @MockBean 迁移

> 逐类计数实测依据：`grep -c '^\s*@MockBean' <file>`；合计依据：`grep -rh '^\s*@MockBean' --include='*.java' aicode/src/test | wc -l` → **54**

- [x] 3.1 迁移 `LoginCtrlShtmlPathTest`（3 处 `@MockBean`）
- [x] 3.2 迁移 `MapRelationshipControllerTest`（6 处）
- [x] 3.3 迁移 `ProjectControllerDownloadTest`（14 处）
- [x] 3.4 迁移 `ProjectInitEndpointTest`（12 处）
- [x] 3.5 迁移 `ProjectJobServiceImplConcurrencyTest`（3 处）
- [x] 3.6 迁移 `SqlReverseParseTest`（16 处）
- [x] 3.7 全仓 grep 确认 `org.springframework.boot.test.mock.mockito.MockBean` 零残留
- [x] 3.8 确认无任何 `@MockitoBean` 被标注在 `@Configuration` 类上（该用法不被支持）
- [x] 3.9 **连带迁移 `ApiContractIT`（1 处）** ✅
      > 该文件由上一变更（`add-api-contract-tests`）新建，当时因 Spring Framework 6.1.16 尚无 `` 而暂用 `` 并留下「待本变更迁正」的注释，本次一并完成
      > 合计 **55 处**（54 原有 + 1 新增）。升级到 Boot 3.5.16 后 Spring Framework 为 **6.2.19**，`@MockitoBean` 可用；Boot 4.0 起 `` 已被移除，本迁移是后续 Boot 4 变更的前置条件

## 4. 依赖清理

- [x] 4.1 删除 `commons-fileupload:commons-fileupload:1.2.1` ✅ 删除前 grep `Multipart|ServletFileUpload|DiskFileItem|FileItem|commons.fileupload` 确认**零引用**（javax.servlet 基座，Jakarta 项目的 classpath 隐患）
- [x] 4.2 删除 `commons-collections:commons-collections:3.2.1` ✅ 零引用
- [x] 4.3 删除 `com.github.ben-manes.caffeine:caffeine:3.1.8` ✅ 零引用
- [x] 4.4 删除 `com.github.pagehelper:pagehelper:6.0.0` ✅ 零引用（分页走 MyBatis-Plus `PaginationInnerInterceptor`）
- [x] 4.5 删除 `commons-lang:commons-lang:20030203.000129`（`common/build.gradle`）✅
      > ⚠️ 唯一命中点在 `FreemarkerHelper` 的 `main()` 调试代码中（构造 import 字符串），已确认 `generate()` 与 `TemplateData` 均不涉及，`main()` 本身也不被任何路径调用 → 删除安全
- [x] 4.6 删除 `com.baomidou:mybatis-plus-generator:3.5.7`（`aicode` 与 `facade` 两处）✅ 零引用
- [x] 4.7 逐条删除后执行编译验证 ✅ 三模块 `compileJava` 全部通过
- [x] 4.8 `commons-lang3` 去重 ✅ 删除 `3.4` 声明行，保留 `3.18.0`
      > **解除 AGENTS.md §10 的历史约束**：该条曾写「commons-lang3 被声明两次，**不要**顺手清理」，本次已完成去重且最终解析版本不变，§10 该条 SHALL 在 `sync-docs` 变更中移除
- [x] 4.9 执行 `dependencyInsight` 确认最终解析版本 ✅
      ```
      org.apache.commons:commons-lang3:3.18.0 (selected by rule)
      org.apache.commons:commons-lang3:3.20.0 -> 3.18.0
      ```
- [x] 4.10 **额外清理（超出原任务清单，经确认后执行）**：两处显式锁版与 BOM 冲突，删除后交由 Boot 3.5.16 BOM 管理
      - `io.micrometer:micrometer-registry-prometheus:1.12.5` → 去掉版本
      - `org.springframework.boot:spring-boot-starter-freemarker:2.1.2.RELEASE` → 去掉版本（**必需依赖，仅去版本，误删过一次已恢复**）
      - `org.junit.jupiter:junit-jupiter:5.10.2` → 去掉版本

## 5. 既有缺陷修复

- [x] 5.1 在 `ApplicationClosedEventListener.GracefulShutdown.onApplicationEvent` 开头增加 `connector == null` 判空并 `log.debug` 后返回
- [x] 5.2 确认该修复 SHALL NOT 掩盖真实异常：connector 非 null 时的原有逻辑保持不变
- [x] 5.3 执行任意一个 `@SpringBootTest(MOCK)` 测试类，确认日志中不再出现 `GracefulShutdown` 的 NPE 堆栈

## 6. 验收

- [x] 6.1 执行 `./aicode/gradlew test`，全部测试 SHALL 通过 ✅
      > 实测 **25 个测试套件 / 148 个用例 / 0 失败 / 2 跳过**
      > 2 跳过 = ① `ApiContractIT` 中以 `@Disabled` 登记的已知行为（鉴权绕过，已确认不修复）② `GitToolsCloneCompletenessTest`（`-Dnetwork.tests=true` 守卫，默认不执行）
      > 测试类统计：`find aicode/src/test common/src/test facade/src/test -name '*.java' | wc -l` → **20**（`aicode` 12 / `common` 8 / `facade` 0）
      > ⚠️ 原任务写的「18 个」已过时：`add-api-contract-tests` 变更新增了 `ApiContractIT` 与 `TestDatabaseInitializer`
- [x] 6.2 执行 `./aicode/gradlew :aicode:test --tests "com.aicode.contract.*"`，契约测试 SHALL 全绿
- [x] 6.3 记录本变更后的冷启动与契约基线（供 Boot 4 变更对比）✅
      > ⚠️ **`measurement/` 目录在仓库中不存在**，原定的 `measurement/startup-time.sh` 无从执行（这是 AGENTS.md §9 / §12.3 引用的第三个失效路径，前两个是 `openspec/` 与 `docs/standards/`）。改为手工测量并留档：
      > ```json
      > {
      >   "measuredAt": "2026-10-15T13:38Z",
      >   "javaVersion": "21.0.12.1", "javaVendor": "Amazon.com Inc.",
      >   "gradleVersion": "8.14.3", "springBootVersion": "3.5.16", "springFrameworkVersion": "6.2.19",
      >   "startup": { "livenessReadySeconds": 3 },
      >   "contractSmoke": { "idType": "str", "codeType": "str",
      >                      "sampleId": "34", "sampleCode": "2080258063987564564" }
      > }
      > ```
      > 真实 HTTP 冒烟：`/login/signin.shtml` → 200 `code=0000`；带 JWT 请求 `/framework/list.shtml` → `code=0000`、`id` 与 `code` **均为字符串**（契约在 Boot 3.5.16 上保持完好）
- [x] 6.4 确认 `./aicode/gradlew :aicode:bootBuildImage` 任务可用（`BP_JVM_VERSION` 本变更保持 21）✅ `gradlew tasks` 中存在 `bootBuildImage` / `bootRun` / `build`；未实际执行镜像构建（耗时且需 Docker daemon）
- [x] 6.5 确认 `openspec validate 2026-10-14-upgrade-spring-boot-3-5 --strict` 通过
