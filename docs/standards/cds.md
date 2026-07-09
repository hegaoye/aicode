# AI-Code CDS 规范（Class Data Sharing）

> 本文定义 `aicode` 镜像启用 Class Data Sharing（CDS）的**操作步骤、回滚路径、调优开关、可观测方法与基线数据**。
> 配套文档：[architecture.md](architecture.md)（架构）、[code-style.md](code-style.md)（编码规范）。
> 关联 OpenSpec：`openspec/changes/enable-cds/`（已归档后位于 `archive/2026-06-28-enable-cds/`）、
> `openspec/changes/upgrade-jgit-6/`（已归档后位于 `archive/2026-06-28-upgrade-jgit-6/`）。

- 文档版本：v1.1（2026-07-09：新增 §10 已知依赖约束——CDS 要求 JGit ≥ 6.6.0 + http.apache 子模块）
- 适用分支：`dev`
- 运行环境：Java 21（Corretto）、Spring Boot 3.3.9、Paketo Java buildpack
- 关键依赖：**JGit 6.6.0 + `org.eclipse.jgit.http.apache` 子模块**（详见 §10）

---

## 目录

1. [现状与动机](#1-现状与动机)
2. [快速核查清单（G1–G4）](#2-快速核查清单g1g4)
3. [操作步骤](#3-操作步骤)
4. [回滚路径](#4-回滚路径)
5. [调优开关](#5-调优开关)
6. [故障排查](#6-故障排查)
7. [Measurement 脚本使用](#7-measurement-脚本使用)
8. [基线数据](#8-基线数据)
9. [互引](#9-互引)

---

## 1. 现状与动机

**部署现状**：`aicode/Dockerfile` + `amazoncorretto:21.0.11-alpine3.23` + 67 MB fat jar，**无 CDS**。每次冷启动都要从零把 `BOOT-INF/lib/` 101 个 jar 与 `BOOT-INF/classes/` 应用类装入 metaspace。

**动机**：多副本冷启动、容器重启场景下 JVM 类加载与解析开销完全重复。启用 CDS 后，启动期 metaspace 占用下降、冷启动 wall-clock 下降。

**实现路径**：走 [Paketo Spring Boot buildpack](https://github.com/paketo-buildpacks/spring-boot) 的 `BP_JVM_CDS_ENABLED=true` 开关，由 buildpack 在构建期自动 `java -Xshare:dump`，运行时自动挂 `-Xshare:on`。**零业务代码改动**。

---

## 2. 快速核查清单（G1–G4）

来源：`openspec/changes/enable-cds/proposal.md` 验收门槛。每条都对应一个可执行的命令：

| 编号 | 标准 | 核查命令 |
|------|------|---------|
| **G1** | 启动日志同时含 `Spring CDS Enabled` 与 `Picked up JAVA_TOOL_OPTIONS: ... -XX:SharedArchiveFile=application.jsa ...` | `docker run --rm aicode:VERSION 2>&1 \| grep -E "Spring CDS Enabled\|SharedArchiveFile=application.jsa"` |
| **G2** | 关闭 CDS 仍 200 | `BP_JVM_CDS_ENABLED=false ./gradlew bootBuildImage` 后 `curl /login/signin?account=admin\&password=888888` 与 `curl /actuator/health/liveness` |
| **G3** | 镜像内含 `/workspace/application.jsa` | `CID=$(docker create aicode:VERSION); docker export "$CID" \| tar tf - \| grep application.jsa` 应输出 `workspace/application.jsa` |
| **G4** | 重启镜像 CDS 仍命中 | `docker restart aicode-test` 后 stdout 再次含 G1 关键字 |

---

## 3. 操作步骤

### 3.1 一次性构建镜像

```bash
cd aicode/
./gradlew bootBuildImage
```

首次执行会从 Docker Hub 拉取：
- `paketobuildpacks/builder-jammy-java-tiny:latest` ≈ 70 MB
- `paketobuildpacks/run-jammy-base:latest`（由 builder 拉取）
- `paketobuildpacks/java` buildpack 元数据

构建产物在本地：
```bash
docker images aicode:0.0.1-SNAPSHOT
# 或 docker images | grep aicode
```

### 3.2 启动容器

```bash
docker run --rm -p 8080:8080 aicode:0.0.1-SNAPSHOT
```

启动日志应该**在 `Started com.aicode.Application` 之前**出现两行：

```
Spring CDS Enabled, contributing -XX:SharedArchiveFile=application.jsa to JAVA_TOOL_OPTIONS
Picked up JAVA_TOOL_OPTIONS: ... -XX:SharedArchiveFile=application.jsa ...
```

可选（加诊断开关后）：

```
[info][cds] trying to map application.jsa
[info][cds] Opened archive application.jsa.
```

### 3.3 验收 G1

```bash
docker run --rm aicode:0.0.1-SNAPSHOT 2>&1 | grep -E "Spring CDS Enabled|SharedArchiveFile=application.jsa"
```

两行都命中即 G1 通过。

### 3.4 加诊断开关看 archive 详情

```bash
docker run --rm -e JAVA_TOOL_OPTIONS="-Xlog:cds=info" aicode:0.0.1-SNAPSHOT 2>&1 | grep "cds"
```

会看到 `Opened archive application.jsa.` + `Mapped static region` 等详细行。

### 3.5 镜像运行（必须显式发布端口）

CNB/Paketo 构建的镜像**不会自动暴露端口**。`docker run aicode:0.0.1-SNAPSHOT` 启动后容器内 Tomcat 监听着 8080，但宿主机**无法访问**——Docker 默认 iptables DROP 未通过 `-p` 发布的入站连接；同 docker bridge 网络（如 `curl 172.17.0.x:8080`）也连不上。

#### 最小启动命令

```bash
# 1. 启动（必需 -p 8080:8080；-p 8088:8088 用于 actuator/metrics）
docker run -d \
  --name aicode-app \
  -p 8080:8080 \
  -p 8088:8088 \
  -e LOG_HOME=/tmp/aicode-logs \
  aicode:0.0.1-SNAPSHOT

# 2. 验证端口映射
docker ps --filter "name=aicode-app" \
  --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
# 期望 PORTS 列：0.0.0.0:8080->8080/tcp, 0.0.0.0:8088->8088/tcp

sleep 5 && curl -i http://localhost:8088/actuator/health/liveness
# 期望：HTTP/1.1 200 + {"status":"UP"}
```

#### 浏览器访问

- 业务：`http://localhost:8080/`
- 健康检查：`http://localhost:8088/actuator/health/liveness`
- 指标：`http://localhost:8088/actuator/metrics`

#### 常见错误

| 现象 | 原因 | 排查 |
|------|------|------|
| `curl: (7) Failed to connect to localhost port 8080` | 启动时漏 `-p 8080:8080` | `docker ps` 看 PORTS 列是否空；删容器重 run |
| `Connection refused` 但 `docker ps` 显示 PORTS 正常 | 容器内应用未启动完成 | `docker logs aicode-app` 查 `Started Application` 时间戳；再 `sleep 5` 重试 |
| `curl 172.17.0.x:8080` 也连不上 | iptables 默认 DROP 未发布端口 | 必须 `-p` 映射；同网络也救不了 |
| `lsof` 报 8080 被占用 | 宿主机已有进程占 8080 | `lsof -nP -iTCP:8080 -sTCP:LISTEN` 查谁占用；换端口（如 `-p 9090:8080`）或停冲突进程 |

#### 替代启动方式

**Docker Desktop GUI**：Images → `aicode:0.0.1-SNAPSHOT` → Run → Optional settings 填两条端口映射（8080↔8080、8088↔8088）。

**docker compose**（推荐日常开发，配置可复用）：

```yaml
# devops/docker/docker-compose.local.yml
services:
  aicode:
    image: aicode:0.0.1-SNAPSHOT
    ports: ["8080:8080", "8088:8088"]
    environment:
      LOG_HOME: /tmp/aicode-logs
    restart: unless-stopped
```

```bash
docker compose -f devops/docker/docker-compose.local.yml up -d
```

> 注：`docker-compose.local.yml` 不入 `archive/`——仅本地开发使用；k8s 用 `Deployment` + `Service` 暴露，无需在 docker 层配端口。

#### 容器端口 vs 宿主机端口

| 容器内端口 | 用途 | 来源 | 是否必需 -p |
|----------|------|------|------------|
| 8080 | Spring Boot 业务 | `application.yml:1` `server.port: 8080` | **是**（用户访问） |
| 8088 | Actuator | `application.yml:108` `management.server.port: 8088` | 推荐（探针/metrics） |

> 修改任一端口后必须重新 `./gradlew bootBuildImage`，否则镜像内仍是旧端口。

---

## 4. 回滚路径

### 4.1 临时回滚（不重建镜像）

只回滚运行时（不改构建配置）：

```bash
docker run -e BP_JVM_CDS_ENABLED=false aicode:0.0.1-SNAPSHOT
# 但 Paketo 在构建期已决定是否打包 archive；
# 运行时开关不会让运行期"按需"重新 dump
```

**结论**：运行时开关无效。**回滚必须重建镜像**。

### 4.2 完整回滚（重建镜像）

1. 编辑 `aicode/build.gradle`，在 `bootBuildImage.environment` 块把 `'BP_JVM_CDS_ENABLED' : 'true'` 改成 `'BP_JVM_CDS_ENABLED' : 'false'`（或直接删掉该行）。
2. `./gradlew clean bootBuildImage`
3. 重建后无 `classes.jsa` 层；运行时走 `-Xshare:auto`（即不命中但也不报错）。

### 4.3 整体回退（用 aicode/Dockerfile）

不动构建配置、改走旧路径：

```bash
docker build -f aicode/Dockerfile -t aicode:fallback aicode/
docker run -p 8080:8080 aicode:fallback
```

此路径**完全不走 CNB/Paketo**，与 CDS 无关；与 `devops/docker/docker-compose.yml` 现有编排一致。

---

## 5. 调优开关

`aicode/build.gradle` 中 `bootBuildImage.environment` 控制的关键环境变量：

| 变量 | 当前值 | 含义 | 调优建议 |
|------|--------|------|---------|
| `BP_JVM_VERSION` | `21` | JDK 版本 | 必须与现有 Gradle 编译版本一致（Corretto 21） |
| `BP_JVM_TYPE` | `JDK` | JDK / JRE | CDS 必须 JDK；改 JRE 会导致 dump 失败 |
| `BP_JVM_CDS_ENABLED` | `true` | CDS 总开关 | 改 `false` 即关闭 CDS |
| `BP_JVM_HEAP_OOM_PERCENTAGE` | `80` | 触发 OOM 的堆使用率 | 默认 80 较保守；调高至 90 留给 GC 缓冲更小 |
| `JAVA_TOOL_OPTIONS` | `-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Dfile.encoding=UTF-8` | 启动期 JVM 参数 | CDS 兼容；如要观察 CDS 命中可加 `-Xlog:cds=info` |

### 5.1 调优示例：观察 CDS 命中详情

```groovy
'JAVA_TOOL_OPTIONS' : '-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Dfile.encoding=UTF-8 -Xlog:cds=info'
```

启动日志会多出 `CDS archive(s) mapped: <size> KB, <n> classes` 等更详细行。

### 5.2 调优示例：减小 CDS archive 体积

默认 archive 30–80 MB。如需更小：
- 排除调试符号：`-Xshare:dump -Xshare:on` 本身不包含 `debug-info`，无需配置
- 减小 layers：`bootJar` 已启用 layers；不要合并 `BOOT-INF/lib/`

---

## 6. 故障排查

| 现象 | 可能原因 | 行动 |
|------|---------|------|
| 启动日志 `Unable to map CDS archive: wrong class path` | dump 与 run JDK 路径不同 | 确认 builder 与 run-image 同 Ubuntu 系列；不要在 `JAVA_TOOL_OPTIONS` 改 `-Xbootclasspath` |
| 启动日志 `Unable to map CDS archive: archive is malformed` | archive 损坏 | 重新 `bootBuildImage`；检查 builder 拉取完整性（`docker pull paketobuildpacks/builder-jammy-java-tiny:latest`） |
| 启动日志 `classes.jsa not found` | archive 层未打包 | 确认 `BP_JVM_CDS_ENABLED=true` 与 `BP_JVM_TYPE=JDK` 都已设 |
| 启动时 archive 命中但服务 500 | 与业务无关 | CDS 不应影响业务；走常规故障排查路径 |
| `bootBuildImage` 报错 `Cannot connect to Docker daemon` | Docker Desktop 未运行 | 启动 Docker Desktop；或临时用 `aicode/Dockerfile` 路径 |
| `application.jsa` > 100 MB | archive 含异常符号 | 重新构建；用 `docker run --rm aicode:VERSION jar tf /workspace/application.jsa \| head` 验证 |

---

## 7. Measurement 脚本使用

非验收工具，仅用于采集启动 wall-clock 与 metaspace 占用数据。后续 AOT、原生镜像、镜像瘦身等迭代可复用。

### 7.1 `measurement/startup-time.sh`

```bash
# 用法：bash measurement/startup-time.sh <image-tag> [runs]
bash measurement/startup-time.sh aicode:0.0.1-SNAPSHOT 5
```

输出 JSON：
```json
{"image":"aicode:0.0.1-SNAPSHOT","runs":5,"samples_ms":[3200,3150,3220,3180,3190],"median_ms":3190}
```

依赖：`docker` CLI；镜像已 build 出来。

### 7.2 `measurement/metaspace-after-up.sh`

```bash
# 容器启动并 liveness 200 后：
docker run -d --name aicode-test -p 8080:8080 aicode:0.0.1-SNAPSHOT
bash measurement/metaspace-after-up.sh aicode-test
```

输出 JSON：
```json
{"container":"aicode-test","pid":42,"committed_mb":14,"used_mb":12,"raw":"..."}
```

依赖：`jcmd`（Paketo 镜像自带）；容器运行中。

### 7.3 配合基线对照

```bash
# 1. 构建旧路径基线
docker build -f aicode/Dockerfile -t aicode:baseline aicode/

# 2. 测量基线
bash measurement/startup-time.sh aicode:baseline 5
docker run -d --name aicode-baseline -p 8081:8080 aicode:baseline
bash measurement/metaspace-after-up.sh aicode-baseline

# 3. 测量 CDS
bash measurement/startup-time.sh aicode:0.0.1-SNAPSHOT 5
docker run -d --name aicode-cds -p 8082:8080 aicode:0.0.1-SNAPSHOT
bash measurement/metaspace-after-up.sh aicode-cds
```

---

## 8. 基线数据

> 2026-06-28 实测。本节是**附录**，不作为验收门槛，仅记录首次启用 CDS 时的实测基线。

### 8.1 测试环境

- **宿主机**：macOS 14.x（arm64），Docker Desktop 28.3.3
- **JDK**：Corretto 21.0.11（CDS）/ Eclipse Temurin 21-jdk（baseline）
- **Spring Boot**：3.3.9
- **aicode.jar**：67 MB fat jar，101 个 `BOOT-INF/lib/*.jar`

### 8.2 镜像

| Tag | 构建路径 | 基础镜像 | CDS |
|-----|---------|---------|-----|
| `aicode:baseline` | 临时 `Dockerfile`（用 `eclipse-temurin:21-jdk` 替代失效的 `amazoncorretto:21.0.11-alpine3.23`），**不入仓** | eclipse-temurin:21-jdk | 关闭 |
| `aicode:0.0.1-SNAPSHOT` | `./gradlew bootBuildImage`（CNB/Paketo） | paketobuildpacks/run-jammy-base | **开启** |

### 8.3 启动 wall-clock（5 次中位数）

| 镜像 | 中位 ms | 5 次采样 | Δ |
|------|--------|---------|---|
| `aicode:baseline` | 3571 | [3571, 3557, 3581, 3569, 3583] | — |
| `aicode:0.0.1-SNAPSHOT` | **2965** | [2965, 2964, 2954, 2975, 2970] | **-17.0%** |

测量方法：`bash measurement/startup-time.sh <image> 5`，从 `docker run` 到 `/actuator/health/liveness` 200 的 wall-clock。

### 8.4 Metaspace 与类加载（`jcmd <pid> VM.metaspace`）

| 指标 | baseline | CDS | Δ |
|------|---------|-----|---|
| Total classes loaded | 14057 | 13741 | -316 (-2.2%) |
| **Shared classes（来自 archive）** | 1412 (10%) | **12019 (87.5%)** | **+10607** |
| Non-Class committed | 58.83 MB | 13.19 MB | **-78%** |
| Class committed | 8.24 MB | 1.23 MB | **-85%** |
| **Total metaspace committed** | **67.07 MB** | **14.42 MB** | **-78%** |
| Non-Class used | 58.36 MB | 13.19 MB | -77% |
| Class used | 7.87 MB | 0.95 MB | -88% |

### 8.5 解读

- **87.5% 类从 archive 加载**：CDS 命中率高，主要因为 fat jar 中 101 个 `BOOT-INF/lib/*.jar` 包含的类元数据在 archive 中已预解析；运行时只需加载 `Spring`、`MyBatis`、`Hibernate` 等反射生成的代理类（约 1700 个）。
- **metaspace 占用下降 78%**：直接降低容器 RSS 占用，对内存受限环境（k8s HPA 缩容阈值）影响显著。
- **wall-clock 加速 17%**：低于某些教材引用的 20-35% 范围，可能与 Spring Boot fat jar 启动期仍要做大量 bean 装配有关，类加载只占总时间一部分。
- **不建议作为绝对数字承诺**：基线在不同宿主机、容器、Spring Boot 版本下波动；后续 AOT、原生镜像等迭代应重新采集。

---

## 9. 互引

- [AGENTS.md §易踩坑](../../AGENTS.md)：H2 1.4.200 锁定但**不与 CDS 冲突**（CDS 只读类元数据，不触碰 H2 引擎）
- [architecture.md](architecture.md)：构建主链路、构建产物、`aicode.jar` 67 MB
- [code-style.md](code-style.md)：业务编码规范（CDS 不影响）
- [openspec/changes/enable-cds/](../../openspec/changes/enable-cds/)：proposal / design / tasks / specs
- [openspec/changes/upgrade-jgit-6/](../../openspec/changes/upgrade-jgit-6/)：JGit 6.6.0 升级配套（详见 §10）

---

## 10. 已知依赖约束：JGit ≥ 6.6.0 + http.apache 子模块

### 10.1 问题背景

JGit 4.9.2（2017 版）默认 HTTP transport 用 `JDKHttpConnectionFactory`，基于 `sun.net.www.protocol.*` `HttpURLConnection`。**CDS archive 加载时跳过 sun.\* 类的 `<clinit>` 静态初始化器**，导致：
- HTTP keep-alive 缓存未初始化
- 连接池半成品
- HTTPS 请求第一个 GET 之后 EOF / connection reset

**现象**：`/project/job/execute` 在 CDS 镜像下只生成 1 个文件（`tidb.yml`），`frameworks_template` 表 ~1 行，`templateEngineEnum` 走 Freemarker fallback，最终 `TIDB_HOST` 抛 `InvalidReferenceException`。

### 10.2 修复（`upgrade-jgit-6` change 已交付）

1. `common/build.gradle`：
   - `org.eclipse.jgit:org.eclipse.jgit:4.9.2.201712150930-r` → `6.6.0.202305301015-r`
   - 新增 `org.eclipse.jgit:org.eclipse.jgit.http.apache:6.6.0.202305301015-r`
   - 新增 `org.apache.httpcomponents:httpclient:4.5.14`
2. `GitTools.java` 加 `static { HttpTransport.setConnectionFactory(new HttpClientConnectionFactory()); }` —— 在类加载时切到 Apache HttpClient 4.x transport（不依赖 sun.\*）
3. 新增 `GitToolsCloneCompletenessTest`：clone `aicode-tamplate.git` 断言 `listFiles().length > 5`（CDS bug 下永远 = 1）

### 10.3 验证记录（2026-07-09）

| 验证层 | 结果 |
|--------|------|
| 编译 | 通过 |
| 现有单元测试（80+ 用例） | 全部通过 |
| 吸烟枪测试（`GitToolsCloneCompletenessTest`） | 通过（11.6s clone，listFiles > 5） |
| 源码模式端到端（`/project/job/execute` + H2） | `project_job.state = Completed`，模板全部生成 |
| CDS 镜像端到端 | 受 H2 mv_store 锁限制未能严格触发 job；间接验证（容器内 22 张表 + 34 frameworks seed + CDS archive 静态分析 `sun.*` 不在 archive、`http.apache` 在 archive）通过 |

### 10.4 故障排查

| 现象 | 排查 |
|------|------|
| CDS 镜像下 `frameworks_template` 表只有 1 行 | 检查 `JGit HTTP transport switched to Apache HttpClient 4.x` 日志是否存在；不存在则 `GitTools` 类未加载，确认 `common/build.gradle` 已升级到 6.6.0 + `http.apache` 子模块 |
| 升级后源码模式仍报 `InvalidReferenceException` for `TIDB_HOST` | 这是 template 工程 `tidb.yml` 期望的占位符，与 JGit 升级无关；属 `enable-cds` change 内遗留，按 §6 流程排查 `prepareframeworksTemplateList` fallback |

---

## 附录 A：版本历史

| 版本 | 日期 | 变更 |
|------|------|------|
| v1.0 | 2026-06-28 | 初版；随 `enable-cds` change 一并交付 |
| v1.1 | 2026-07-09 | 新增 §10：JGit ≥ 6.6.0 + http.apache 子模块依赖约束；新增 `JGit HTTP transport switched` 日志可观测项；互引 `upgrade-jgit-6` change |
