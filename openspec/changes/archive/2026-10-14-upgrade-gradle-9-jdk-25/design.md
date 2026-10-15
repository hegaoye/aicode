# Design

## Context

用户明确本次升级的动机是「研究升级可靠性、兼容性」，而非性能收益。这条约束贯穿本设计的所有取舍：**任何"顺手做了"的优化都会让"升级是否可靠"这个问题变得不可验证。**

当前基线：

| 维度 | 现状 |
|------|------|
| Java | 21（`sourceCompatibility = '21'`，三个模块各自声明） |
| Gradle | 8.5（`2026-10-14-upgrade-spring-boot-3-5` 将其提升至 8.14.3） |
| Lombok | 1.18.32（三处显式声明） |
| 容器镜像 | `amazoncorretto:21.0.11-alpine3.23` |
| 云构建包 | `BP_JVM_VERSION=21` |
| 本地 JDK | Corretto 25.0.4.1 已安装 ✅ |
| CDS 基线 | `docs/standards/cds.md`：Metaspace ↓78%、wall-clock ↓17%（Java 21 下的测量值） |

Spring Boot 4.1.1 的系统要求为「Java 17 及以上，兼容至 Java 26」；Spring Boot 3.5.16 同样支持 Java 25。因此本变更在 Spring 侧**不引入任何新约束**。

## Goals / Non-Goals

**Goals:**

- 三个模块统一到 Java 25 toolchain
- 构建工具升级到支持 Java 25 的版本，且无兼容性弃用告警
- 容器与云构建包的运行时同步到 25
- 性能基线可跨版本机器比较，并据此重写 CDS 文档

**Non-Goals:**

- 不做任何性能调优、不引入新的 JVM 旗标（除保留现有 CDS 配置外）
- 不删除或精简 CDS 配置——本变更只重测，不改动配置本身
- 不修改任何 `src/` 下的 Java 源文件（若升级暴露必须修的兼容问题，另开变更）
- 不升级到 Java 26 或更高（Boot 4.1 的兼容上限是 26，但 25 才是当前 LTS）

## Decisions

### 决策 1：用 `java.toolchain` 统一声明，取代各模块的 `sourceCompatibility`

现状是三个 `build.gradle` 各自声明 `java { sourceCompatibility = '21' }`。改为：

```groovy
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
```

**理由**：`sourceCompatibility` 只控制字节码版本，不控制**用哪个 JDK 编译**。在 Gradle 9 + Java 25 场景下，toolchain 才是正解：它让 Gradle 自动选用满足要求的本地 JDK（已装 Corretto 25.0.4.1），也允许将来用 `--release` 语义交叉验证。

**收益**：三个模块从此不可能语言级别不一致（此前各写各的，理论上可以漂移）。

**备选**：在根 `build.gradle` 用 `allprojects` 统一。拒绝——根 `build.gradle` 当前为空，AGENTS.md §10 提示编辑根 build.gradle 无效（配置都在子模块），且 `allprojects` 会让各模块失去独立声明的清晰度。

### 决策 2：Lombok 交给 BOM，删掉 1.18.32

**硬约束**：Java 25 需要 Lombok ≥ 1.18.40（1.18.40 是首个声明 JDK 25 支持的版本）。当前 1.18.32 会直接编译失败。

Spring Boot 4.1.1 的 BOM 管理 `lombok.version=1.18.46`；Spring Boot 3.5.x 管理一个同样 ≥ 1.18.40 的版本。三个模块声明：

```groovy
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'
```

**理由**：显式锁定 Lombok 版本是本项目的一条隐性技术债——每升一次 JDK 就要手动改三处，且 `io.spring.dependency-management` 已经在管着它。删除显式版本后，BOM 会自动给出满足当前 JDK 的版本。

**备选**：显式升级到 1.18.48。拒绝——下次升 JDK 时还会再撞一次同样的问题。

### 决策 3：CDS 只重测、不改动

`docs/standards/cds.md` 记录的 Java 21 测量值（Metaspace ↓78%、wall-clock ↓17%）在 Java 25 上不再成立——JVM 对 CDS 的实现与默认策略在 23/24/25 上有多轮变化，且 JDK 24 起引入了自动归档相关的新能力。

本变更 SHALL：
1. 用升级后的运行时重新执行 `measurement/metaspace-after-up.sh` 与 `measurement/startup-time.sh`
2. 用新数据重写 `cds.md`，**删除旧数字而非保留为历史**（保留会诱导后来者引用过期结论）
3. 保持 `bootBuildImage` 中的 CDS 环境变量原样

**理由**：用户动机是「可靠性与兼容性」，性能数字只是副产品。保留一份准确的新基线，比保留一份漂亮但过期的旧基线更有价值。

### 决策 4：性能脚本输出结构化 JSON，且自带版本标识

现有脚本输出是面向人读的文本。本变更扩展为 JSON，最低字段：

```json
{
  "javaVersion": "25.0.x",
  "javaVendor": "Amazon.com Inc.",
  "buildId": "<git sha>",
  "measuredAt": "<ISO-8601>",
  "startup": {
    "wallClockMs": 0,
    "livenessReadyMs": 0
  },
  "metaspace": { "usedBytes": 0, "committedBytes": 0, "maxBytes": 0 }
}
```

**理由**：升级「可靠性」的核心验收物是一组可比较的数字。两份同结构 JSON 才能做机器判定；纯文本做不到。两个脚本必须用**完全相同的字段名**，否则无法逐字段比对。

**验收阈值的处理**：本变更只定义采集与可比性，**不设**通过/失败阈值。阈值需要一组真实的历史样本才有意义——单次对比设阈值必然是拍脑袋。阈值定义放在后续 CI 接入变更（AGENTS.md §11.4 已列为待办）。

### 决策 5：镜像与云构建包同步更新

| 位置 | 变更 |
|------|------|
| `aicode/Dockerfile` | `FROM amazoncorretto:21.0.11-alpine3.23` → `FROM amazoncorretto:25` |
| `aicode/build.gradle` `bootBuildImage` | `'BP_JVM_VERSION': '21'` → `'25'` |

**关于 Dockerfile 的镜像标签**：原 pin 了小版本 `21.0.11-alpine3.23`。新 pin 为 `25`（不 pin 小版本），理由是 Docker Hub 的 Corretto 25 标签不保证存在 alpine 变体的小版本号。可选项：
- **A（推荐）** `amazoncorretto:25` —— 简单，牺牲可复现性
- B `amazoncorretto:25.0.x-alpine3.x` —— 可复现，但需先验证标签存在；若不存在会导致构建失败
- C 改用 Paketo 构建包统一产出，废弃手写 Dockerfile —— 范围过大，属独立变更

选 A。若 CI 需要可复现构建，作为后续独立事项处理。

## Risks / Trade-offs

| 风险 | 影响 | 缓解 |
|------|------|------|
| ByteBuddy（Mockito 底层）在 Java 25 上不支持新 class 文件版本 | 所有 `@MockitoBean` 测试失败 | Mockito 5.23.0（Boot BOM）已支持 Java 25；若失败需升级 Mockito，这是可定位的独立问题 |
| H2 1.4.200（2017 年版本）在 Java 25 上运行异常 | 应用启动失败 | 1.4.200 的 `AUTO_SERVER` 模式依赖内部反射，Java 强封装可能破坏它。若失败，H2 升级需提前到本变更（H2 2.4.240 已在 Boot 4 变更的计划中，提前的风险是 `DB_CLOSE_ON_EXIT` 与 `AUTO_SERVER` 互斥问题一并暴露） |
| JIT 在 Java 25 上暴露既有代码的竞态或反射问题 | 生产行为与开发态不一致 | 契约测试 + 生成主链路端到端冒烟作为主要防线；`ProjectJobServiceImplConcurrencyTest` 已覆盖并发路径 |
| toolchain 自动探测到的本地 JDK 与预期不符 | 构建结果不可复现 | tasks 中要求 `tasks 3.1` 显式打印实际使用的 JDK 版本并留档 |
| CDS 测量数据波动大，重测结果不稳定 | 文档数字不可靠 | 每个指标采样 N 次取中位数，N 与采样方法写入 `cds.md` |
| 删除 Lombok 显式版本后，某模块未被 dependency-management 覆盖 | 该模块编译失败 | 三个模块统一在各自 `build.gradle` 删除；若 `common` / `facade` 未应用 dependency-management 插件，则需为其补上——tasks 中留有该分支 |
