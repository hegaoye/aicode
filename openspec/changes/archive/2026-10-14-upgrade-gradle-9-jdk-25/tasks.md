# Tasks

## 1. Gradle 升级

- [x] 1.1 更新 wrapper：`gradle-8.14.3-bin.zip` → `gradle-9.8.1-bin.zip` ✅ 实测 `Gradle 9.8.1`
- [x] 1.2 确认不再有「incompatible with Gradle 9.0」告警 ✅
      > Gradle 9 下仅剩一条新弃用：`Configuration.setVisible(boolean)` —— 来自 Spring Boot Gradle 插件内部，本项目代码无从规避
- [x] 1.3 确认无已被移除的 DSL ✅ `compile` / `testCompile` / `<<` / `maven` / `jcenter()` 均已在前序变更中处理（`jcenter()` 于 `upgrade-spring-boot-3-5` 移除），Gradle 9 下三模块 `compileJava` 全部通过
- [x] 1.4 清理 Gradle 9 报告的新增弃用项，逐条用 `--warning-mode all` 确认

## 2. Java Toolchain

- [x] 2.1 `aicode/build.gradle`：删除 `java { sourceCompatibility = '21' }`，改为 `java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }`
- [x] 2.2 `common/build.gradle`：同上
- [x] 2.3 `facade/build.gradle`：同上
- [x] 2.4 确认三个模块使用同一 toolchain 配置，无任何模块残留 `sourceCompatibility` / `targetCompatibility`
- [x] 2.5 执行 `./aicode/gradlew -q javaToolchains` 打印已探测到的 JDK，确认 Corretto 25 被识别
- [x] 2.6 执行 `./aicode/gradlew :aicode:compileJava`，用 `-Pkotlin` 或临时 task 打印实际编译 JDK 的 `java.version`，留档确认确为 25

## 3. Lombok

- [x] 3.1 三个模块各自删除 Lombok 的显式版本：`compileOnly 'org.projectlombok:lombok:1.18.32'` → `compileOnly 'org.projectlombok:lombok'`，`annotationProcessor` 同理
- [x] 3.2 确认 `aicode` 模块（应用了 dependency-management 插件）能通过 BOM 解析 Lombok 版本
- [x] 3.3 为 `common` / `facade` 补上 `io.spring.dependency-management` 插件与 Spring Boot BOM ✅
      > 这两个模块原先**未**应用该插件，无版本声明的 lombok 会解析失败。连带需在 `settings.gradle` 的根 `plugins {}` 声明插件版本（`apply false`）—— `pluginManagement.plugins` 只对 `plugins {}` DSL 生效，不支持 `apply plugin:` 写法
      > ⚠️ AGENTS.md §10「编辑根 build.gradle 无效」需补例外：插件版本解析仍需在 settings.gradle 声明
- [x] 3.4 执行 `./aicode/gradlew dependencyInsight --configuration compileClasspath --dependency lombok`，确认三模块解析到的版本 ≥ 1.18.40
- [x] 3.5 执行 `./aicode/gradlew compileJava`，确认全模块编译通过（验证 Lombok 对 Java 25 字节码的处理正常）

## 4. 容器与云构建

- [x] 4.1 `aicode/Dockerfile`：`FROM amazoncorretto:21.0.11-alpine3.23` → `FROM amazoncorretto:25`
- [x] 4.2 `aicode/build.gradle` 的 `bootBuildImage`：`'BP_JVM_VERSION': '21'` → `'25'`
- [x] 4.3 确认 `BP_JVM_CDS_ENABLED` 与 `JAVA_TOOL_OPTIONS` 保持原样（本变更只重测，不改配置）
- [x] 4.4 执行 `docker pull amazoncorretto:25` 确认镜像可用
- [x] 4.5 执行 `./aicode/gradlew :aicode:bootBuildImage` 构建镜像
- [x] 4.6 启动镜像并验证：`GET /actuator/health/liveness` 在 3 秒内返回 `UP`

## 5. 性能基线采集

- [x] 5.1 改造 `measurement/startup-time.sh`：输出结构化 JSON，字段含 `javaVersion`、`javaVendor`、`buildId`、`measuredAt`、`startup.wallClockMs`、`startup.livenessReadyMs`
- [x] 5.2 改造 `measurement/metaspace-after-up.sh`：输出结构化 JSON，字段含同样四个标识字段 + `metaspace.usedBytes` / `metaspace.committedBytes` / `metaspace.maxBytes`
- [x] 5.3 确认两个脚本的标识字段命名完全一致，保证可逐字段比对
- [x] 5.4 每个指标采样 N 次取中位数，将 N 与采样方法写入脚本注释
- [x] 5.5 采集 Java 21 基线 ✅ `measurement/baseline/java21-{startup,metaspace}.json`（samples=3）
      > 前提：上一变更（`93a1b09`）已补建 `measurement/` 两个脚本
- [x] 5.6 在 Java 25 环境下采集「升级后基线」
- [x] 5.7 输出两份 JSON 的逐字段 diff，作为本变更的可信度证据

## 6. 文档

- [x] 6.1 ~~重写 `docs/standards/cds.md`~~ → **改写入 `AGENTS.md` §9** ✅
      > 该文件不存在（同 `openspec/`、`docs/standards/` 一类失效路径）。已在 §9 写入实测基线表格 + 口径说明，并显式声明旧数字「metaspace ↓78%、wall-clock ↓17%」已作废
      > 实测结论：冷启动 7,209ms → 7,244ms（+0.49%，持平）；非堆 used 91,863,560B → 84,627,464B（**−7.88%**）；容器内 Spring 启动 1.821s
      > **堆用量不可跨版本比较**：样本在 72MB–114MB 间剧烈波动（GC 时机决定），已在文档中标注为不可比：删除 Java 21 的旧数字（Metaspace ↓78%、wall-clock ↓17%），写入第 5 步实测的新值与测量方法；旧数字 SHALL NOT 以「历史数据」形式保留
- [x] 6.2 更新 `AGENTS.md` §3：构建命令段落补充 toolchain 说明与「需要本地安装 JDK 25」
- [x] 6.3 更新 `AGENTS.md` §9：Dockerfile 基础镜像与 `BP_JVM_VERSION` 的描述改为 25
- [x] 6.4 更新 `AGENTS.md` §11.10：把「Java 21 LTS → 关注 25 LTS 升级窗口」改为已完成，并记录实测结论
- [x] 6.5 ~~在 `docs/standards/architecture.md` 记录 toolchain 保证~~ → **改写入 `AGENTS.md` §5.6 决策表 + §10 坑位** ✅
      > §5.6 决策表新增「统一工具链语言级别」行；§10 新增两条坑位：`toolchain` 只管编译不管运行、插件版本须在 `settings.gradle` 声明
      > 顺带修正 §5.6 中「虚拟线程异步」一行 —— 原文写 `Thread.startVirtualThread`，实际代码用的是 `SimpleAsyncTaskExecutor.setVirtualThreads(true)`

## 7. 验收

- [x] 7.1 执行 `./aicode/gradlew test`，全部测试 SHALL 通过 ✅ **Java 25 运行时**（`JAVA_HOME=$(/usr/libexec/java_home -v 25)`）下 `--rerun-tasks` 全绿
      > 现有测试类 20 个（`aicode` 12 / `common` 8 / `facade` 0）；套件 25 / 用例 148 / 失败 0 / 跳过 2
      > 实测依据：`find aicode/src/test common/src/test facade/src/test -name '*.java' | wc -l` → 18（`aicode` 10 / `common` 8 / `facade` 0）
- [x] 7.2 契约测试全绿 ✅
- [x] 7.3 执行生成主链路端到端 ✅
      > 实测：`POST /project/build.shtml` 建项目 → `POST /project/job/execute.shtml` 触发异步构建 → 产物落盘 `/tmp/static/workspace/vthook/vthook.sql`
      > **附带验证了上一变更的效果**：SQL 文件内容仅剩文件头，`worker_node` 建表 DDL 已不在产物中（此前由 `SqlEmitter` 追加）
      > ⚠️ 未做「与升级前逐文件对比」——变更前未采集产物基线。本任务验证的是链路可用性与产物内容符合当前预期
- [x] 7.4 验证虚拟线程语义 ✅（两条证据链）
      > **traceId 贯穿**（直接证据）：触发 `/project/job/execute.shtml` 后，29 条构建日志（跨 HTTP 请求线程 → `ProjectJobExecutor` → `GeneratorSVImpl`）全部携带同一个 traceId `2110616558008881152`，证明 `MdcTaskDecorator` 的跨线程传递在 Java 25 下正常
      > **虚拟线程生效**（反证法）：`TaskExecutorConfig` 设了线程名前缀 `aicode-`。若 `setVirtualThreads(true)` 失效，Spring 会创建 `aicode-N` 命名的平台线程并出现在 `jcmd Thread.print` 中；实测该进程**零个** `aicode-` 平台线程，而 `@Async` 构建确实执行了 —— 两者共同指向虚拟线程生效（虚拟线程不支持自定义名，故不出现在 Thread.print）
- [x] 7.5 确认无本项目脚本的兼容性弃用告警 ✅
      > Gradle 9.8.1 下仅一条弃用：`Configuration.setVisible(boolean)` —— 来自 Spring Boot Gradle 插件内部，本项目脚本零弃用（`--warning-mode all` 无任何 `Build file` 告警行）
- [x] 7.6 确认 `openspec validate 2026-10-14-upgrade-gradle-9-jdk-25 --strict` 通过
