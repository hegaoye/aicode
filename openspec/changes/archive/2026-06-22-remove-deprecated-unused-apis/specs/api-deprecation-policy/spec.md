## ADDED Requirements

### Requirement: 系统 SHALL 制定 @Deprecated 端点的处理流程
The system SHALL define a policy for handling `@Deprecated` endpoints: mark → 30 days observation → secondary reference check (grep internal + frontend) → delete. This change is the first execution of the policy.

#### Scenario: 流程首次执行（本 change 范围）
- **WHEN** a `@Deprecated` endpoint has been marked for 30+ days and the reference scan returns zero internal callers (aicode / common / facade / openspec)
- **AND** `aicode/README.md` §四 declares the endpoint is "前端未调用"
- **THEN** the change may remove the controller method, the facade interface method, and the service implementation method in a single atomic commit

#### Scenario: 不删除规范要求的端点
- **WHEN** an endpoint is listed in any `openspec/specs/<capability>/spec.md` Scenario (e.g. `GET /login/signin` in `auth-account`)
- **THEN** the endpoint MUST NOT be removed even if marked `@Deprecated`

#### Scenario: 端点删除后更新 README §四
- **WHEN** a `@Deprecated` endpoint is removed
- **THEN** `aicode/README.md §四` MUST be updated to mark the endpoint as "已删除 (YYYY-MM-DD) by change remove-deprecated-unused-apis"

#### Scenario: 单元测试同步更新
- **WHEN** a removed endpoint is referenced by any test class (e.g. via `@MockBean`, `MockMvc`, or direct bean call)
- **THEN** the test MUST be updated or removed before `./gradlew test` passes

#### Scenario: 全 build 与全测试通过
- **WHEN** the change is complete
- **THEN** `./gradlew clean build` MUST succeed AND all 74 existing test cases MUST still pass
