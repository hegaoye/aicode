## 1. 分支与回退锚点

- [ ] 1.1 从当前分支（`feature/20260622/delta`）创建 `feature/upgrade-jgit-6` 分支
- [ ] 1.2 在 pre-jgit-upgrade 状态打 tag `pre-jgit-upgrade-20260628`（回退锚点）

## 2. 依赖升级（common/build.gradle）

- [ ] 2.1 替换第 30-33 行 4 行（含 1 行注释 + 1 行注释候选 + 1 行空行 + 1 行实现）为 3 行新依赖：
  - `org.eclipse.jgit:org.eclipse.jgit:6.6.0.202305301015-r`
  - `org.eclipse.jgit:org.eclipse.jgit.http.apache:6.6.0.202305301015-r`
  - `org.apache.httpcomponents:httpclient:4.5.14`
- [ ] 2.2 验证 `./gradlew :common:dependencies --configuration runtimeClasspath` 包含两个 jgit jar 与 httpclient 4.5.14

## 3. GitTools static 初始化块

- [ ] 3.1 在 `common/src/main/java/com/aicode/core/tools/GitTools.java` 加 2 个 import：`HttpTransport`、`HttpClientConnectionFactory`
- [ ] 3.2 在 `@Slf4j public class GitTools {` 之后加 static 初始化块（含 try/catch + INFO/WARN 日志）

## 4. 吸烟枪测试（GitToolsCloneCompletenessTest）

- [ ] 4.1 新建 `common/src/test/java/com/aicode/core/tools/GitToolsCloneCompletenessTest.java`
- [ ] 4.2 测试断言：`target.listFiles().length > 5`（CDS bug 下是 1，修复后是 30+ framework 子目录）
- [ ] 4.3 测试断言：`target/springcloud3.3.9-mybatisplus-redis-java21/aicode.json` 存在
- [ ] 4.4 加 `@EnabledIfSystemProperty(named = "network.tests", matches = "true")` + `Assumptions.assumeTrue(...)` 网络容错

## 5. Layer 1 验证（编译）

- [ ] 5.1 跑 `./gradlew :common:compileJava :aicode:compileJava`
- [ ] 5.2 通过：所有模块编译通过
- [ ] 5.3 失败回退：`git checkout pre-jgit-upgrade-20260628 -- common/`

## 6. Layer 2 验证（现有单元测试）

- [ ] 6.1 跑 `./gradlew test`
- [ ] 6.2 通过：80+ 现有测试用例全过
- [ ] 6.3 失败回退：同 L1

## 7. Layer 3 验证（吸烟枪测试）

- [ ] 7.1 跑 `./gradlew :common:test --tests GitToolsCloneCompletenessTest -Dnetwork.tests=true`
- [ ] 7.2 通过：clone 成功 + `listFiles().length > 5` + aicode.json 存在
- [ ] 7.3 失败排查：检查 `target.listFiles()` 实际数量；查看测试日志
- [ ] 7.4 失败回退：同 L1

## 8. Layer 4 验证（源码模式端到端）

- [ ] 8.1 跑 `./gradlew :aicode:clean :aicode:bootJar`
- [ ] 8.2 后台启 jar：`java -jar aicode/build/libs/aicode.jar &`，sleep 30
- [ ] 8.3 触发 `curl 'http://localhost:8080/project/job/execute?code=1324392103895106049'`，sleep 15
- [ ] 8.4 用 H2 客户端查 `frameworks_template` 表行数（应 > 50）
- [ ] 8.5 通过：行数 > 50 + 无 `InvalidReferenceException`
- [ ] 8.6 失败排查：看 stdout 中 "JGit HTTP transport switched" 日志
- [ ] 8.7 失败回退：同 L1

## 9. Layer 5 验证（CDS 镜像端到端）

- [ ] 9.1 跑 `./gradlew :aicode:bootBuildImage`
- [ ] 9.2 启容器：`docker run -d --name aicode-cds-test -p 8080:8080 -p 8088:8088 -e LOG_HOME=/tmp/aicode-logs aicode:unspecified`
- [ ] 9.3 sleep 25 + 触发 job（同 L4）
- [ ] 9.4 在 host 上用 H2 客户端查 `frameworks_template`（应 > 50）
- [ ] 9.5 `docker logs aicode-cds-test | grep "JGit HTTP transport"` 确认 transport 切换日志
- [ ] 9.6 通过：行数 > 50 + transport 切换日志出现
- [ ] 9.7 失败排查：检查 CDS archive 是否包含 http.apache 子模块 jar：`unzip -l aicode.jar | grep jgit.http.apache`
- [ ] 9.8 失败回退：保留 JGit 升级 + 归档 `enable-cds` 为"已知 transport 不兼容"，或彻底回退

## 10. 性能基线对比（可选）

- [ ] 10.1 跑 `bash measurement/startup-time.sh aicode:unspecified 5`
- [ ] 10.2 与 `docs/standards/cds.md §8.3` 基线对比（允许偏差：启动 ±10%、metaspace ±15%）

## 11. 文档更新

- [ ] 11.1 更新 `docs/standards/cds.md §1`：加 "CDS 已与 JGit 6.6.0 + http.apache 子模块验证兼容"
- [ ] 11.2 更新 `docs/standards/cds.md §3.5`：加 troubleshooting 步骤——"如果 frameworks_template 只有 1 条记录：检查 `JGit HTTP transport switched` 日志是否存在；不存在则 `GitTools` 类未加载到，需要排查依赖"
- [ ] 11.3 更新 `common/build.gradle` 注释：删除第 30-31 行占位注释（已升级生效）

## 12. OpenSpec 归档

- [ ] 12.1 跑 `openspec validate upgrade-jgit-6 --type change`
- [ ] 12.2 跑 `openspec archive upgrade-jgit-6`
- [ ] 12.3 验证归档：`ls openspec/changes/archive/2026-06-28-upgrade-jgit-6/`

## 13. Git 提交与合并（待用户授权）

- [ ] 13.1 `git add . && git commit -m "feat(common): upgrade JGit 4.9.2 → 6.6.0 + http.apache 子模块 + GitTools transport 切换"`
- [ ] 13.2 （待用户授权合并后）`git checkout feature/20260622/delta && git merge feature/upgrade-jgit-6`
- [ ] 13.3 （待用户授权后）`git push origin feature/upgrade-jgit-6`