# Tasks

## 1. Gradle 升级

- [ ] 1.1 更新 `aicode/gradle/wrapper/gradle-wrapper.properties`：`gradle-8.14.3-bin.zip` → `gradle-9.x-bin.zip`（当前稳定版 9.8.1）
- [ ] 1.2 执行 `./aicode/gradlew --version`，确认输出中不再有「incompatible with Gradle 9.0」一类的弃用告警
- [ ] 1.3 处理 Gradle 9 的严格性变化：确认无已被移除的 DSL（`compile` / `testCompile` / `<<` 左移 / `maven` 插件 / `jcenter()`）
- [ ] 1.4 清理 Gradle 9 报告的新增弃用项，逐条用 `--warning-mode all` 确认

## 2. Java Toolchain

- [ ] 2.1 `aicode/build.gradle`：删除 `java { sourceCompatibility = '21' }`，改为 `java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }`
- [ ] 2.2 `common/build.gradle`：同上
- [ ] 2.3 `facade/build.gradle`：同上
- [ ] 2.4 确认三个模块使用同一 toolchain 配置，无任何模块残留 `sourceCompatibility` / `targetCompatibility`
- [ ] 2.5 执行 `./aicode/gradlew -q javaToolchains` 打印已探测到的 JDK，确认 Corretto 25 被识别
- [ ] 2.6 执行 `./aicode/gradlew :aicode:compileJava`，用 `-Pkotlin` 或临时 task 打印实际编译 JDK 的 `java.version`，留档确认确为 25

## 3. Lombok

- [ ] 3.1 三个模块各自删除 Lombok 的显式版本：`compileOnly 'org.projectlombok:lombok:1.18.32'` → `compileOnly 'org.projectlombok:lombok'`，`annotationProcessor` 同理
- [ ] 3.2 确认 `aicode` 模块（应用了 dependency-management 插件）能通过 BOM 解析 Lombok 版本
- [ ] 3.3 若 `common` / `facade` 未应用 `io.spring.dependency-management`，则为其补上该插件与 Spring Boot BOM import；否则保留无版本声明（前提是三模块都能解析）
- [ ] 3.4 执行 `./aicode/gradlew dependencyInsight --configuration compileClasspath --dependency lombok`，确认三模块解析到的版本 ≥ 1.18.40
- [ ] 3.5 执行 `./aicode/gradlew compileJava`，确认全模块编译通过（验证 Lombok 对 Java 25 字节码的处理正常）

## 4. 容器与云构建

- [ ] 4.1 `aicode/Dockerfile`：`FROM amazoncorretto:21.0.11-alpine3.23` → `FROM amazoncorretto:25`
- [ ] 4.2 `aicode/build.gradle` 的 `bootBuildImage`：`'BP_JVM_VERSION': '21'` → `'25'`
- [ ] 4.3 确认 `BP_JVM_CDS_ENABLED` 与 `JAVA_TOOL_OPTIONS` 保持原样（本变更只重测，不改配置）
- [ ] 4.4 执行 `docker pull amazoncorretto:25` 确认镜像可用
- [ ] 4.5 执行 `./aicode/gradlew :aicode:bootBuildImage` 构建镜像
- [ ] 4.6 启动镜像并验证：`GET /actuator/health/liveness` 在 3 秒内返回 `UP`

## 5. 性能基线采集

- [ ] 5.1 改造 `measurement/startup-time.sh`：输出结构化 JSON，字段含 `javaVersion`、`javaVendor`、`buildId`、`measuredAt`、`startup.wallClockMs`、`startup.livenessReadyMs`
- [ ] 5.2 改造 `measurement/metaspace-after-up.sh`：输出结构化 JSON，字段含同样四个标识字段 + `metaspace.usedBytes` / `metaspace.committedBytes` / `metaspace.maxBytes`
- [ ] 5.3 确认两个脚本的标识字段命名完全一致，保证可逐字段比对
- [ ] 5.4 每个指标采样 N 次取中位数，将 N 与采样方法写入脚本注释
- [ ] 5.5 在 Java 21 环境下（用 `JAVA_HOME` 指向 corretto-21）采集一份「升级前基线」并留档
- [ ] 5.6 在 Java 25 环境下采集「升级后基线」
- [ ] 5.7 输出两份 JSON 的逐字段 diff，作为本变更的可信度证据

## 6. 文档

- [ ] 6.1 重写 `docs/standards/cds.md`：删除 Java 21 的旧数字（Metaspace ↓78%、wall-clock ↓17%），写入第 5 步实测的新值与测量方法；旧数字 SHALL NOT 以「历史数据」形式保留
- [ ] 6.2 更新 `AGENTS.md` §3：构建命令段落补充 toolchain 说明与「需要本地安装 JDK 25」
- [ ] 6.3 更新 `AGENTS.md` §9：Dockerfile 基础镜像与 `BP_JVM_VERSION` 的描述改为 25
- [ ] 6.4 更新 `AGENTS.md` §11.10：把「Java 21 LTS → 关注 25 LTS 升级窗口」改为已完成，并记录实测结论
- [ ] 6.5 在 `docs/standards/architecture.md` 中记录 toolchain 的引入及其带来的「三模块语言级别不可能漂移」的保证

## 7. 验收

- [ ] 7.1 执行 `./aicode/gradlew test`，全部测试 SHALL 通过（18 个既有测试类）
      > 实测依据：`find aicode/src/test common/src/test facade/src/test -name '*.java' | wc -l` → 18（`aicode` 10 / `common` 8 / `facade` 0）
- [ ] 7.2 执行 `./aicode/gradlew :aicode:test --tests "com.aicode.contract.*"`，契约测试 SHALL 全绿
- [ ] 7.3 执行生成主链路端到端（新建项目 → 导入 SQL → init → 构建），确认产物与升级前一致
- [ ] 7.4 验证虚拟线程语义未变：触发一次异步构建，确认日志中线程名为虚拟线程、链路标识与触发请求一致
- [ ] 7.5 确认 `./aicode/gradlew` 的输出中无 Gradle 兼容性弃用告警
- [ ] 7.6 确认 `openspec validate 2026-10-14-upgrade-gradle-9-jdk-25 --strict` 通过
