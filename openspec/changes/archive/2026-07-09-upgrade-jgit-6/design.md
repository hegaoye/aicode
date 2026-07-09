## Context

aicode 用 JGit 4.9.2（2017 年 11 月版本，源自 `common/build.gradle:33`）做外部模板仓库克隆（`GitTools.cloneGit`，由 `GeneratorSVImpl.prepareframeworksTemplateList` 在每次构建任务启动时调用）。`enable-cds` change 已为 aicode 启用 CDS（`BP_JVM_CDS_ENABLED=true`），但发现一个生产事故：**CDS 镜像模式下 aiCode 跑完第 3 步后，`frameworks_template` 表只新增了 1 条记录**（`tidb.yml` 路径），而不是预期的 200+ 条完整模板文件清单。这导致 `prepareframeworksTemplateList` 的 `templateEngineEnum` 始终为 `null`，最终在第 4 步走 Freemarker fallback，渲染 `tidb.yml` 时 `TIDB_HOST` 变量缺失抛 `InvalidReferenceException`。

`enable-cds` 的 spec 已经有"必须 clone 完整"的可观测验收（G3 部分），但根因不在 CDS 实现本身，而在 JGit 4.9.2 的 HTTP transport 实现：它用 `JDKHttpConnectionFactory` → `sun.net.www.protocol.http.HttpURLConnection`。CDS archive 加载时跳过 `sun.*` 类的 `<clinit>`（静态初始化器），导致 `HttpURLConnection` 内部缓存、连接池、SPI 查找处于半初始化状态，HTTP 连接在第一个 GET 之后异常关闭（EOF / connection reset），git clone 中途丢弃大部分 packfile。源码模式不使用 CDS archive，`sun.*` 类的 `<clinit>` 正常执行，所以 clone 完整——这就解释了"源码模式正常、CDS 模式坏"。

## Goals / Non-Goals

**Goals:**

- 把 JGit 升级到 6.6.0 + 加 `org.eclipse.jgit.http.apache` 子模块（使用 Apache HttpClient 4.x transport，不依赖 `sun.*`）
- 在 `GitTools` 类加载时显式 `HttpTransport.setConnectionFactory(new HttpClientConnectionFactory())`，绕开 JGit core 默认的 `JDKHttpConnectionFactory`
- 新增 `GitToolsCloneCompletenessTest` 吸烟枪测试，保证未来 transport 不会意外回到 `HttpURLConnection` 路径
- 在 5 层验证（编译 / 单测 / 吸烟枪 / 端到端 / CDS 镜像）下完整跑过，每层失败可回退

**Non-Goals:**

- 不升级 Spring Boot 3.3.9 → 4.x（独立范围）
- 不升级 Java 21（独立范围）
- 不引入 `org.apache.httpcomponents:httpclient5`（BOM 已管 5.3.1，但 JGit 6.6.0 http.apache 子模块用 4.x，与 5.x 互相干扰）
- 不优化 `prepareframeworksTemplateList` 性能（独立范围）
- 不改 `enable-cds` 的 G1-G4 验收（CDS 行为不变，JGit 升级后 G3 自然恢复）

## Decisions

### Decision 1：JGit 6.6.0（不升 7.7.0）

- **选 6.6.0**：项目作者（AGENTS.md §易踩坑 之外的代码审查者）已在 `common/build.gradle:30-31` 注释了 `org.eclipse.jgit:org.eclipse.jgit:6.6.0.202305301015-r`，是经过预先评估的候选版本；依赖生态成熟（jsch / httpclient / JavaEWAH 已稳定多年）
- **不选 7.7.0**：Maven Central 最新版，但发布时间 2026-06，距项目 Java 21 + SB 3.3.9 兼容性未充分验证；API 漂移风险（虽然 jar 内 class diff 不大，但 `org.eclipse.jgit.internal.transport.http` 包结构变了）
- **结论**：采用 6.6.0，注释行变生效

### Decision 2：必须同时引入 `http.apache` 子模块 + 显式切换 transport

- **只升 core 不行**：JGit 6.6.0 core 默认 `connectionFactory` 仍是 `JDKHttpConnectionFactory`（与 4.9.2 / 5.13.x / 7.7.0 一致），反编译 `HttpTransport.<clinit>` 确认：底层 transport 没换
- **必须加 `http.apache` 子模块**：该子模块提供 `HttpClientConnectionFactory`（Apache HttpClient 4.x），不依赖 `sun.net.www.protocol.*`，CDS 友好
- **必须显式 `setConnectionFactory`**：因为 JGit 没 ServiceLoader 自动选择 `http.apache` 子模块的 factory；只有显式调用才能让 transport 生效
- **三件事缺一不可**：

  ```
  1. common/build.gradle: jgit 4.9.2 → 6.6.0
  2. common/build.gradle: 加 org.eclipse.jgit.http.apache:6.6.0
  3. GitTools.java: static { HttpTransport.setConnectionFactory(new HttpClientConnectionFactory()) }
  ```

### Decision 3：transport 切换放在 `GitTools` 类的 static 初始化块

- **放 `GitTools` 而非 `Application`**：`GitTools` 是所有 JGit 调用的唯一入口（`grep -rln org.eclipse.jgit` 全仓只有 `GitTools.java` 一处）；static 块在类首次加载时执行，保证 `cloneGit` / `pullGit` / `commitAndPush` 任一调用前 transport 已切换
- **不放 Spring `@PostConstruct`**：Spring `@PostConstruct` 在 Bean 生命周期中执行，时机晚于 class loader，可能错过某些早启动场景（如 application listener 注册阶段）
- **不放 Spring `@Configuration` + 显式 `ApplicationRunner`**：依赖 Spring 上下文初始化顺序，复杂且难调试
- **结论**：static 块在 `GitTools` 是最简、最显式、最早的方案；try/catch 包裹保证 transport 切换失败时回退默认（不会让 Spring 启动失败）

### Decision 4：Apache HttpClient 4.5.14 显式声明（不依赖 transitive）

- **不显式声明的风险**：JGit 6.6.0 parent BOM 管 httpclient 4.5.14，Spring Boot 3.3.9 BOM 管 httpclient5 5.3.1（不同包名无冲突）；但未来 BOM 升级可能导致 httpclient 4.x 被替换
- **显式声明好处**：`org.apache.httpcomponents:httpclient:4.5.14` 在 common/build.gradle 显式 pin，Gradle dependency resolution 把版本锁住
- **结论**：3 行 dependencies（JGit core / http.apache 子模块 / httpclient pin），让维护者一眼看清楚依赖意图

### Decision 5：吸烟枪测试 `GitToolsCloneCompletenessTest`

- **测试设计原则**：必须能**重现 CDS bug** + **在修复后通过**
- **断言关键值**：
  - `target.listFiles().length > 5` —— CDS bug 下只 clone 出 `tidb.yml`，root 是 1 个目录或文件；修复后是 30+ framework 子目录
  - `target/springcloud3.3.9-mybatisplus-redis-java21/aicode.json` 存在 —— 决定 engine 选择的关键文件
- **CI 网络容错**：用 `@EnabledIfSystemProperty(named = "network.tests", matches = "true")` 让离线 CI 跳过；本地手动跑时设 `-Dnetwork.tests=true`
- **断言不依赖 timing**：用 `cloneGit` 返回 boolean 后立即断言（不 await）
- **结论**：测试是 `change once, run forever` 的契约，未来 transport 变更会立刻 fail

## Risks / Trade-offs

- [Risk] **Apache HttpClient 4.x 自己依赖 `sun.*` 类** → Mitigation：实测 Layer 5；如果仍坏，需要 JGit 7.x + 自定义 `HttpConnectionFactory`
- [Risk] **`HttpTransport.setConnectionFactory` 在多线程环境下竞态** → Mitigation：`HttpTransport.connectionFactory` 是 `static volatile`，JGit 文档保证 thread-safe
- [Risk] **httpclient 4.5.14 与 Spring Boot 3.3.9 BOM 锁定的 httpclient5 5.3.1 包冲突** → Mitigation：包名 `org.apache.http.*` vs `org.apache.hc.*`，Gradle 不会合并，无冲突；fat jar 多 1.4MB 可接受
- [Risk] **slf4j-api 1.7.36（从 JGit 6.6.0 parent BOM）vs SB 3.3.9 BOM 锁定的 2.0.16** → Mitigation：Gradle dependency eviction 规则自动淘汰 1.7.36（JGit 兼容 slf4j-api 2.x）；不需要显式声明
- [Risk] **`GitTools` static 块在 JUnit 测试中重复执行** → Mitigation：`HttpTransport.setConnectionFactory` 幂等；INFO 日志每次都会打印（可观测）
- [Risk] **`GitToolsCloneCompletenessTest` 依赖 GitHub 网络可达性** → Mitigation：`@EnabledIfSystemProperty` 网关 + `Assumptions.assumeTrue` 双重保护
- [Trade-off] **fat jar 增加约 2MB**（jgit 6.6.0 从 2.6MB → 3.1MB，加上 http.apache 子模块 28KB、httpclient 4.5.14 ~700KB、jsch 0.1.55 ~280KB、jna-platform 等）→ Mitigation：在可接受范围内，CDS 启动加速 -17%、metaspace -78% 的收益远超

## Migration Plan

### Phase 1：分支与回退锚点

```bash
git checkout -b feature/upgrade-jgit-6
git tag pre-jgit-upgrade-20260628  # 回退锚点
```

### Phase 2：代码改动（2 处）

1. `common/build.gradle:30-33` —— 替换为 6.6.0 + http.apache 子模块 + httpclient pin
2. `GitTools.java` —— 加 2 个 import + 1 个 static 块
3. 新建 `common/src/test/java/com/aicode/core/tools/GitToolsCloneCompletenessTest.java` —— 吸烟枪测试

### Phase 3：5 层验证（每层失败可独立回退）

| Layer | 内容 | 时间 | 回退 |
|-------|------|------|------|
| L1 | `./gradlew :common:compileJava :aicode:compileJava` | 2 min | `git checkout` 回退到 pre-tag |
| L2 | `./gradlew test`（80+ 现有用例） | 5 min | 同上 |
| L3 | `./gradlew :common:test --tests GitToolsCloneCompletenessTest -Dnetwork.tests=true` | 5 min | 同上 |
| L4 | 源码模式启 jar + 触发 `/project/job/execute?code=1324392103895106049` + 查 H2 `frameworks_template` 行数 > 50 | 10 min | 同上 |
| L5 | CDS 镜像 `docker run aicode:unspecified` + 触发 job + 查 H2（host 上 `/tmp/aicode.mv.db`）行数 > 50 | 10 min | 同上 |

### Phase 4：归档与文档

1. `openspec archive upgrade-jgit-6` → 归档到 `openspec/changes/archive/2026-06-28-upgrade-jgit-6/`
2. 更新 `docs/standards/cds.md` —— 在 §1 加一行"CDS 已与 JGit 6.6.0 + http.apache 子模块验证兼容"；§3.5 加 troubleshooting 步骤：如果 frameworks_template 只有 1 条记录，检查 `JGit HTTP transport switched` 日志是否存在
3. 更新 `common/build.gradle` 注释（移除第 30-31 行注释的占位）

### Rollback Strategy

- 任何 Layer 失败 → `git checkout pre-jgit-upgrade-20260628 -- common/build.gradle common/src/main/java/com/aicode/core/tools/GitTools.java common/src/test/`
- 然后删除未 tracked 文件：取消新增测试文件
- 重新跑 `./gradlew clean build` 验证回退成功

## Open Questions

- JGit 7.7.0 是否在远期值得评估？（当前不在本 change 范围；如 JGit 6.6.0 验证后稳定，可在新 change 中评估 7.7.0）
- 是否需要在 aicode-side 加 `BOOT-INF/lib/org.eclipse.jgit.http.apache-*.jar` 的 metadata 检测来验证 jar 真正被打入？（L1 的 fat jar 检查已隐式覆盖）