# jgit-transport-config

## Purpose

定义 JGit HTTP transport 在 aicode 启动期的运行时切换行为，确保 git clone 不再依赖 `sun.net.www.protocol.*` `HttpURLConnection`（其 `<clinit>` 静态初始化器在 CDS archive 加载时不会被执行），从而在源码模式与 CDS 镜像模式下都能完整下载外部模板仓库。

## ADDED Requirements

### Requirement: JGit HTTP transport 启动期切换为 Apache HttpClient 4.x

The system SHALL switch JGit's default `HttpConnectionFactory` to `org.eclipse.jgit.transport.http.apache.HttpClientConnectionFactory` during class initialization of `com.aicode.core.tools.GitTools`, so that all HTTPS git clone / pull / push operations use Apache HttpClient 4.x (not `sun.net.www.protocol.*` `HttpURLConnection`).

#### Scenario: GitTools 类加载时执行切换
- **WHEN** any code first references `com.aicode.core.tools.GitTools` (e.g., `GeneratorSVImpl.aiCode` calling `GitTools.cloneGit`)
- **THEN** the static initializer MUST call `HttpTransport.setConnectionFactory(new HttpClientConnectionFactory())` exactly once
- **AND** an INFO log line "JGit HTTP transport switched to Apache HttpClient 4.x (CDS-compatible)" MUST be emitted

#### Scenario: 切换失败回退默认 transport
- **WHEN** `new HttpClientConnectionFactory()` throws (e.g., missing dependency at runtime)
- **THEN** the static initializer MUST log a WARN line with the failure reason and MUST NOT propagate the exception
- **AND** subsequent JGit operations MUST fall back to the default `JDKHttpConnectionFactory`

#### Scenario: 幂等性
- **WHEN** the static initializer runs multiple times (class reloaded in test scenarios)
- **THEN** `HttpTransport.setConnectionFactory(...)` MUST be safe to call repeatedly with the same argument
- **AND** the INFO log line SHOULD appear each time

### Requirement: 完整 clone 验收基线

The system SHALL provide a unit test that verifies a known git clone returns a non-trivial directory tree, so that future regressions of the HTTP transport (re-introduction of `HttpURLConnection`-only path, or new CDS archive compatibility issues) are caught.

#### Scenario: clone aicode-tamplate.git 完整
- **WHEN** `GitTools.cloneGit("https://github.com/hegaoye/aicode-tamplate.git", target, null, null)` runs in a JUnit 5 test
- **THEN** the returned `target.listFiles()` MUST contain more than 5 entries (CDS archive bug would yield exactly 1: `tidb.yml`)
- **AND** `target/springcloud3.3.9-mybatisplus-redis-java21/aicode.json` MUST exist as a regular file
- **AND** the test MUST be runnable with `gradlew :common:test --tests GitToolsCloneCompletenessTest`

#### Scenario: 网络失败时测试优雅降级
- **WHEN** the clone fails with `IOException` due to network unavailability
- **THEN** the test MUST be marked `@EnabledIfSystemProperty` or use `Assumptions.assumeTrue(...)` so CI without network access does not fail
- **AND** a clear log message MUST indicate the test was skipped

### Requirement: common 模块依赖包含 http.apache 子模块

The system SHALL declare `org.eclipse.jgit:org.eclipse.jgit.http.apache:6.6.0.202305301015-r` as an `implementation` dependency in `common/build.gradle`, alongside `org.eclipse.jgit:org.eclipse.jgit:6.6.0.202305301015-r`, so that the `HttpClientConnectionFactory` class is on the runtime classpath.

#### Scenario: Gradle 依赖解析结果
- **WHEN** `./gradlew :common:dependencies --configuration runtimeClasspath` runs
- **THEN** `org.eclipse.jgit:org.eclipse.jgit:6.6.0.202305301015-r` MUST appear
- **AND** `org.eclipse.jgit:org.eclipse.jgit.http.apache:6.6.0.202305301015-r` MUST appear
- **AND** `org.apache.httpcomponents:httpclient:4.5.14` MUST appear (explicit pin to override transitive)

#### Scenario: fat jar 包含两个 jgit jar
- **WHEN** `./gradlew :aicode:bootJar` runs
- **THEN** `aicode/build/libs/aicode.jar` MUST contain both `BOOT-INF/lib/org.eclipse.jgit-6.6.0.202305301015-r.jar` and `BOOT-INF/lib/org.eclipse.jgit.http.apache-6.6.0.202305301015-r.jar`
- **AND** MUST NOT contain `org.eclipse.jgit-4.9.2.201712150930-r.jar`