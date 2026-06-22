## ADDED Requirements

### Requirement: POST /project/init 触发两阶段执行
The system SHALL, when `POST /project/init?code=<projectCode>` is called, delegate to `ProjectServiceImpl.execute(code)` which runs `createDatabase` then `parse`, and return `R.success()` on success.

#### Scenario: 正常 init 流程
- **WHEN** the project exists, has a stored SQL, and the schema does not exist
- **THEN** both `createDatabase` and `parse` run, and the response is `R.success()`

#### Scenario: 空 code 参数
- **WHEN** `code` is empty or null
- **THEN** the system returns `R.failed(Empty_Param)`

#### Scenario: 项目不存在
- **WHEN** the `code` does not match any project
- **THEN** the system throws `Result_Not_Exist`

### Requirement: ProjectJobServiceImpl.execute 用 DB 状态作并发锁
The system SHALL reject `execute(projectCode)` if there is already a `ProjectJob` row with `state=Executing` for the same `projectCode`; concurrent second calls MUST throw `Server_Error` (not silently overlap).

#### Scenario: 串行两次 execute 都成功
- **WHEN** `execute` is called, then `aiCode` finishes (state moves to Completed/Error), then `execute` is called again
- **THEN** both `execute` calls return a `ProjectJob` and the second is independent of the first

#### Scenario: 并发两次 execute 第二次被拒
- **WHEN** `execute(code)` is called twice in rapid succession before the first finishes
- **THEN** the first call returns a `ProjectJob`; the second call throws `BaseException(Server_Error)` (because a row with `state=Executing` for the same code already exists)

### Requirement: ProjectJobServiceImpl.execute 不通过 this. 自调触发 @Async
The system SHALL dispatch the actual `aiCode` work via a separate `@Service` bean (`ProjectJobExecutor`) so Spring AOP intercepts the `@Async` annotation; the HTTP request thread MUST NOT block on the seven-step pipeline.

#### Scenario: execute 立即返回 ProjectJob
- **WHEN** `execute(projectCode)` is called
- **THEN** the method returns within ~100ms with a `ProjectJob(state=Executing)`; the seven-step pipeline runs on a virtual thread afterwards
