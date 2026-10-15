# Tasks

## 1. Gradle 构建链

- [ ] 1.1 更新 `aicode/gradle/wrapper/gradle-wrapper.properties`：`gradle-8.5-bin.zip` → `gradle-8.14.3-bin.zip`
- [ ] 1.2 执行 `./aicode/gradlew --version`，确认版本为 8.14.3，且不再输出「incompatible with Gradle 9.0」告警
- [ ] 1.3 清理 `aicode/build.gradle` 中重复的 `configurations { compileOnly { extendsFrom annotationProcessor } }` 块（当前声明两次）
- [ ] 1.4 删除 `facade/build.gradle` 中的 `jcenter()` 仓库声明（已废弃）
- [ ] 1.5 执行 `./aicode/gradlew :aicode:dependencies --configuration runtimeClasspath` 留档，作为后续变更的对比基线

## 2. Spring Boot 版本

- [ ] 2.1 `aicode/build.gradle`：`org.springframework.boot` 插件 `3.3.9` → `3.5.16`
- [ ] 2.2 `aicode/build.gradle`：`io.spring.dependency-management` `1.1.4` → `1.1.7`
- [ ] 2.3 `org.springdoc:springdoc-openapi-starter-webmvc-ui`：`2.3.0` → `2.8.17`（`aicode` 与 `facade` 两处）
- [ ] 2.4 执行 `./aicode/gradlew :aicode:compileJava`，确认生产代码编译通过且无新增告警

## 3. @MockBean 迁移

> 逐类计数实测依据：`grep -c '^\s*@MockBean' <file>`；合计依据：`grep -rh '^\s*@MockBean' --include='*.java' aicode/src/test | wc -l` → **54**

- [ ] 3.1 迁移 `LoginCtrlShtmlPathTest`（3 处 `@MockBean`）
- [ ] 3.2 迁移 `MapRelationshipControllerTest`（6 处）
- [ ] 3.3 迁移 `ProjectControllerDownloadTest`（14 处）
- [ ] 3.4 迁移 `ProjectInitEndpointTest`（12 处）
- [ ] 3.5 迁移 `ProjectJobServiceImplConcurrencyTest`（3 处）
- [ ] 3.6 迁移 `SqlReverseParseTest`（16 处）
- [ ] 3.7 全仓 grep 确认 `org.springframework.boot.test.mock.mockito.MockBean` 零残留
- [ ] 3.8 确认无任何 `@MockitoBean` 被标注在 `@Configuration` 类上（该用法不被支持）

## 4. 依赖清理

- [ ] 4.1 删除 `commons-fileupload:commons-fileupload:1.2.1`，删除前 grep `Multipart|ServletFileUpload|DiskFileItem|FileItem` 确认零引用
- [ ] 4.2 删除 `commons-collections:commons-collections:3.2.1`
- [ ] 4.3 删除 `com.github.ben-manes.caffeine:caffeine:3.1.8`
- [ ] 4.4 删除 `com.github.pagehelper:pagehelper:6.0.0`
- [ ] 4.5 删除 `commons-lang:commons-lang:20030203`（`common/build.gradle`）
- [ ] 4.6 删除 `com.baomidou:mybatis-plus-generator:3.5.7`（`aicode` 与 `facade` 两处）
- [ ] 4.7 逐条删除后执行 `./aicode/gradlew :aicode:compileJava`，确认每次删除均不破坏编译
- [ ] 4.8 `commons-lang3` 去重：grep 3.4 独有 API 确认可删后，删除 `aicode/build.gradle` 中的 `3.4` 声明行，保留 `3.18.0`
- [ ] 4.9 执行 `./aicode/gradlew dependencyInsight --configuration runtimeClasspath --dependency commons-lang3`，确认最终解析版本为 3.18.0

## 5. 既有缺陷修复

- [ ] 5.1 在 `ApplicationClosedEventListener.GracefulShutdown.onApplicationEvent` 开头增加 `connector == null` 判空并 `log.debug` 后返回
- [ ] 5.2 确认该修复 SHALL NOT 掩盖真实异常：connector 非 null 时的原有逻辑保持不变
- [ ] 5.3 执行任意一个 `@SpringBootTest(MOCK)` 测试类，确认日志中不再出现 `GracefulShutdown` 的 NPE 堆栈

## 6. 验收

- [ ] 6.1 执行 `./aicode/gradlew test`，全部测试 SHALL 通过（含 18 个既有测试类）
      > 实测依据：`find aicode/src/test common/src/test facade/src/test -name '*.java' | wc -l` → 18（`aicode` 10 / `common` 8 / `facade` 0）
- [ ] 6.2 执行 `./aicode/gradlew :aicode:test --tests "com.aicode.contract.*"`，契约测试 SHALL 全绿
- [ ] 6.3 执行 `measurement/startup-time.sh`，记录本变更后的冷启动基线（供 Boot 4 变更对比）
- [ ] 6.4 确认 `./aicode/gradlew :aicode:bootBuildImage` 可用（`BP_JVM_VERSION` 本变更保持 21）
- [ ] 6.5 确认 `openspec validate 2026-10-14-upgrade-spring-boot-3-5 --strict` 通过
