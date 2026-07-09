## Why

JGit 4.9.2（2017 年版）在 CDS archive 模式下 git clone 残缺：HTTP transport 使用 `JDKHttpConnectionFactory`（基于 `sun.net.www.protocol.*` `HttpURLConnection`），CDS archive 加载时跳过 `sun.*` 类的 `<clinit>` 静态初始化器，导致 HTTP 连接中途 EOF，`GitTools.cloneGit()` 仅写入 1 个文件（`tidb.yml`），从而使 `prepareframeworksTemplateList()` 找不到 `aicode.json`、engine 检测 fallback 到 Freemarker、最终在第 4 步渲染触发 `InvalidReferenceException: TIDB_HOST`。

需要把 JGit 升级到 6.6.0 并加 `org.eclipse.jgit.http.apache` 子模块，通过 `HttpTransport.setConnectionFactory(new HttpClientConnectionFactory())` 切到 Apache HttpClient 4.x transport（不依赖 `sun.*`），同时新增吸烟枪测试 `GitToolsCloneCompletenessTest` 保证未来不再回归。

## What Changes

- **common/build.gradle**：JGit `4.9.2 → 6.6.0`，新增 `org.eclipse.jgit.http.apache:6.6.0` 子模块，显式 pin `org.apache.httpcomponents:httpclient:4.5.14`
- **GitTools.java**：加 static 初始化块，在类加载时把 JGit HTTP transport 切换为 `HttpClientConnectionFactory`（Apache HttpClient 4.x）；导入 `HttpTransport` 与 `HttpClientConnectionFactory`
- **新增 `common/src/test/java/com/aicode/core/tools/GitToolsCloneCompletenessTest.java`**：clone `hegaoye/aicode-tamplate.git` 后断言 `listFiles().length > 5`（CDS bug 下是 1，修复后是 30+ framework 子目录），同时校验 `aicode.json` 被 clone 下来

## Capabilities

### New Capabilities

- `jgit-transport-config`: 定义 JGit HTTP transport 的运行时切换行为（启动期替换默认 `JDKHttpConnectionFactory` 为 Apache HttpClient 4.x `HttpClientConnectionFactory`），并声明「必须 clone 完整」的可观测验收标准。

### Modified Capabilities

- `frameworks-template`: 增加 Requirement "HTTP transport 不依赖 sun.* 内部类"，要求 clone 在 CDS archive 模式下与源码模式行为一致（下载完整仓库），避免后续 transport 实现变更导致 `prepareframeworksTemplateList` 数据不完整。

## Impact

- **common/build.gradle**：依赖树变更（jsch 0.1.54→0.1.55、JavaEWAH 1.1.6→1.2.3、httpclient 4.3.6→4.5.14、jakarta.servlet-api、bouncycastle jdk18on、jna-platform、jzlib 引入）；fat jar 增加约 2MB
- **GitTools.java**：1 个 static 块 + 2 个 import；行为从「默认 HttpURLConnection」改为「显式 Apache HttpClient 4.x」
- **新增测试**：1 个 JUnit 5 测试类，依赖 GitHub 网络可达
- **Spring Boot 3.3.9 BOM 与 httpclient 4.x 共存**：包名 `org.apache.http.*` vs `org.apache.hc.*` 不冲突；fat jar 同时包含两个 jar，但功能隔离
- **不影响的子系统**：MyBatis-Plus、Freemarker、Beetl、H2、uid-generator；Spring Boot / Spring Framework / Java 21 都不动
- **回退成本**：3 处改动 < 1 分钟可回退到 4.9.2