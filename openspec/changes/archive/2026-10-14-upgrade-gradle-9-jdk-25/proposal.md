# Proposal

## Why

把运行时从 Java 21 推进到 Java 25 LTS，动机是**验证升级的可靠性与兼容性**，而不是追逐某个具体性能指标。这决定了本变更的验收标准：不是「跑起来了」，而是「18 个既有测试 + 契约测试全绿、生成主链路产物一致、冷启动基线可比、镜像可构建」。

之所以把 Java 25 与 Spring Boot 4 拆成两个变更：

1. **故障域隔离**。ByteBuddy（Mockito 底层）、H2、JGit、Beetl、FreeMarker 在 Java 25 上的兼容性是独立于 Spring 版本的一类问题。与 Boot 4 的包迁移、starter 改名混在一起时，无法判断失败是「Java 25 不兼容」还是「Boot 4 删了 API」。
2. **Spring Boot 3.5.16 本身支持 Java 25**，因此本变更可以独立成立、独立验收、独立回滚。
3. 本变更与 `2026-10-14-replace-baidu-uid-generator` **无依赖关系**，两者可并行推进。

同时，本变更有两项容易被忽略的连带工作：

- **CDS 基线作废**。`docs/standards/cds.md` 记录的「Metaspace 下降 78%、wall-clock 加速约 17%」是 Java 21 的测量结果。JVM 在 25 上对 CDS 的实现有变化，这些数字若不重测就继续留在文档里，会变成误导。
- **Gradle 必须先到 9.x**。Gradle 8.14 拒绝 Java 25 的字节码工具链（`sourceCompatibility` / `targetCompatibility` 走 javac 21 尚可，但 toolchain 模式下无法解析 Java 25），且实测构建输出已明确声明 8.5 与 Gradle 9 不兼容。

## What Changes

- Gradle Wrapper `8.14.3` → `9.x`（当前稳定版 9.8.1）
- 构建改用 Java toolchain：三个模块的 `sourceCompatibility = '21'` → `java.toolchain.languageVersion = JavaLanguageVersion.of(25)`
- Lombok 显式版本 `1.18.32` → 删除显式版本，交给 Spring Boot BOM 管理（1.18.46+）。**Java 25 要求 Lombok ≥ 1.18.40**，1.18.32 无法处理新 class 文件版本
- `aicode/Dockerfile` 基础镜像 `amazoncorretto:21.0.11-alpine3.23` → `amazoncorretto:25`
- `bootBuildImage` 中 `BP_JVM_VERSION` `21` → `25`
- `measurement/startup-time.sh` 与 `measurement/metaspace-after-up.sh` 的输出扩展为**可跨版本对比的结构化 JSON**（记录 JDK 版本、Boot 版本、冷启动 wall-clock、Metaspace 用量）
- 重测并重写 `docs/standards/cds.md` 的全部性能数字

本变更 SHALL NOT 改变任何接口契约、生成逻辑或前端行为。

## Capabilities

### New Capabilities

- `runtime-platform`: 定义平台构建与运行时的基线——Java 语言级别、构建工具版本、Lombok 版本约束、容器基础镜像、性能基线的采集方式与可比性要求。

### Modified Capabilities

无。

## Impact

- **修改文件**：
  - `aicode/gradle/wrapper/gradle-wrapper.properties`（8.14.3 → 9.x）
  - `aicode/build.gradle`、`common/build.gradle`、`facade/build.gradle`（toolchain + Lombok 版本）
  - `aicode/Dockerfile`
  - `measurement/startup-time.sh`、`measurement/metaspace-after-up.sh`
  - `docs/standards/cds.md`
  - `AGENTS.md` §3（构建命令）、§9（容器/部署）、§11.10（升级路径）
- **不改**：任何 `src/main` / `src/test` 下的 Java 源文件
- **本地前置条件**：已安装 Corretto 25.0.4.1（`/usr/libexec/java_home -V` 已确认）；toolchain 模式下 Gradle 可自动探测本地 JDK，无需额外安装
- **风险点**：ByteBuddy（Mockito 5.x 底层）对 Java 25 class 文件版本的支持；H2 1.4.200 在 Java 25 上的运行；JIT 层面的行为差异可能暴露既有代码中的竞态
- **验收命令**：`./aicode/gradlew test` + `./aicode/gradlew :aicode:bootBuildImage`
