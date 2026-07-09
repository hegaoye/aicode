# frameworks-template

## MODIFIED Requirements

### Requirement: 框架池 CRUD

The system SHALL expose create / read / update / delete / toggle-public endpoints for the framework pool.

#### Scenario: create framework
- **WHEN** `POST /framework/build` receives a `Frameworks` payload
- **THEN** the system generates a `code` and persists `gitHome`, `account`, `password`, `isPublic` etc.

#### Scenario: load framework
- **WHEN** `GET /framework/load?code=<code>` is called
- **THEN** the system returns the framework details

#### Scenario: modify framework
- **WHEN** `PUT/POST /framework/modify` receives a `Frameworks` payload
- **THEN** the system updates and returns `R`

#### Scenario: delete framework
- **WHEN** `POST /framework/delete` receives a `Frameworks` payload
- **THEN** the system deletes the framework and returns `R`

#### Scenario: toggle-public framework
- **WHEN** `POST /framework/build` receives a `Frameworks` payload with `isPublic=Y/N`
- **THEN** the system updates `isPublic` and returns `R`

## ADDED Requirements

### Requirement: HTTP transport 不依赖 sun.* 内部类

The system SHALL clone external template repositories via a JGit HTTP transport that does NOT depend on `sun.net.www.protocol.http.HttpURLConnection` or `sun.net.www.protocol.https.HttpsURLConnection`, so that the operation works correctly in both source mode and CDS archive mode (where the `<clinit>` of `sun.*` classes is skipped when classes are loaded from the shared archive).

#### Scenario: 源码模式 clone 完整
- **WHEN** `prepareframeworksTemplateList(projectFramworkList, logsPath)` runs in source mode (no CDS)
- **THEN** the recursive file walk over `template_root_path` MUST enumerate every file in the cloned template repository (verified by `FileUtil.getDirFiles` returning > 50 entries for `aicode-tamplate.git`)

#### Scenario: CDS 镜像模式 clone 完整
- **WHEN** `prepareframeworksTemplateList` runs inside a CDS-enabled container image
- **THEN** the `frameworks_template` table MUST receive > 50 rows (one per template file)
- **AND** at least one row MUST contain the path `<framework-name>/aicode.json` (so the engine detection can locate the engine declaration)

#### Scenario: clone 不完整时模板引擎 fallback 应被阻止
- **WHEN** `GitTools.cloneGit` returns success but only produces ≤ 1 root entry (broken HTTP transport)
- **THEN** `prepareframeworksTemplateList` MUST log an ERROR "模板克隆不完整：仅 N 个文件" with N being the actual count
- **AND** `templateEngineEnum` MUST remain null (forcing Freemarker fallback is unacceptable when the cause is clone failure rather than engine absence)

### Requirement: 模板仓库克隆完整性的可观测验收

The system SHALL expose a startup-time health indicator that confirms the HTTP transport switch was successful, so that operators can diagnose CDS-related clone failures without needing to reproduce the full `aiCode` flow.

#### Scenario: 启动日志含 transport 切换标记
- **WHEN** the application starts (source or CDS mode)
- **THEN** stdout MUST contain the literal line "JGit HTTP transport switched to Apache HttpClient 4.x (CDS-compatible)"
- **AND** this line MUST appear before the first `Started Application` log entry

#### Scenario: 切换失败时健康检查标记异常
- **WHEN** the static initializer of `GitTools` catches a `Throwable` during transport switch
- **THEN** stdout MUST contain a WARN line with the failure reason
- **AND** `/actuator/health` MUST return `{"status":"UP"}` with `details.jgitTransport.status = "DEGRADED"` (graceful degradation, not failure)