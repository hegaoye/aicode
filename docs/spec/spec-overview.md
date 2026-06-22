# 功能规格总览与通用约定（spec-overview）

> 本文是 AI-Code 功能规格（spec）系列的**索引与通用约定**。各功能域规格见下方清单。
> 目标：任何 AI/开发者依据本系列文档，可在不参考原始源码的情况下**重建出等价的 AI-Code 后端项目**。
> 架构背景见 [architecture.md](../standards/architecture.md)，编码规范见 [code-style.md](../standards/code-style.md)，测试见 [testing.md](../standards/testing.md)。

- 文档版本：v1.0
- 运行环境：Java 21、Spring Boot 3.3.9、Gradle 多模块
- 应用端口：8080（业务）/ 8088（Actuator 管理）

---

## 1. 系统目标（一句话规格）

AI-Code 是一个**由 SQL 反向驱动的全栈代码生成平台后端**：用户导入建表 SQL → 系统在内嵌库建库建表并反向解析出「类↔表 / 字段↔列」映射 → 用户在 Web 维护映射、关联关系、显示属性、选用技术框架模板 → 一键异步生成全套 `dao→service→ctrl→frontend` 可运行代码，自动推送 Git 并打包 ZIP，构建过程经 WebSocket 实时回显。

---

## 2. 功能域与规格文件清单

| 规格文件 | 功能域 | 核心能力 |
|----------|--------|----------|
| [spec-auth-account.md](spec-auth-account.md) | 认证与账户 | 登录（JWT）、注册、账户 CRUD |
| [spec-project.md](spec-project.md) | 项目管理 | 项目创建/查询/修改/删除、详情聚合、文件扫描/下载 |
| [spec-sql-parse.md](spec-sql-parse.md) | SQL 导入与反向解析 | SQL 脚本管理、建库建表、解析生成映射 |
| [spec-mapping-display.md](spec-mapping-display.md) | 模型映射与显示属性 | 类表/字段列映射、1:1/1:N 关联、字段前端显示属性 |
| [spec-frameworks-template.md](spec-frameworks-template.md) | 框架技术池与模板 | 框架技术池 CRUD、Git 模板仓库、模板引擎 |
| [spec-codegen.md](spec-codegen.md) | 代码生成主链路 | 异步构建、模板渲染、产物 Git/ZIP、实时日志 |
| [spec-settings-repository.md](spec-settings-repository.md) | 全局设置与版本控制账户 | 系统 Setting、项目 Git/SVN 凭据 |

> 完整数据库表（22 张）DDL 字段散落于各功能域规格的「数据模型」小节；以各域为准。

---

## 3. 工程结构（重建必需）

三个 Gradle 模块，依赖方向 `aicode → facade → common`（单向无环）：

```
settings.gradle:  include 'common', 'facade', 'aicode'
common/   普通 Java 库：工具(GitTools/JwtToken/ZipTools/StringTools/Md5/SqlExecutorHelper/类型映射)、注解、枚举、R/Page/PageVO/BaseVO/BaseException
facade/   普通 Java 库：各域 entity / dto / vo / service 接口 / exceptions；依赖 common
aicode/   Spring Boot 应用：各域 ctrl / *ServiceImpl / dao+mapper / config / 模板引擎 / websocket；依赖 common + facade
```

主类：`com.aicode.Application`，注解 `@SpringBootApplication + @EnableAsync`。
关键依赖版本见 [architecture.md §6.1](../standards/architecture.md)。

---

## 4. 通用约定（所有功能域共享）

### 4.1 统一响应体 `R`（common 模块）

所有 `@RestController` 返回 `R`：

```json
{ "code": "0000", "info": "success", "data": <任意>, "success": true }
```

- `R.success()`：成功无数据；`R.success(data)`：成功带数据；`R.failed(枚举)`：失败。
- 序列化字段：`code`、`info`（或前端读作 `msg`）、`data`、`success`。
- 全局异常由 `ExceptionHandle`（`@ControllerAdvice`）捕获，`BaseException` 转标准 `R`，HTTP 400。

### 4.2 错误码（`BaseException.BaseExceptionEnum`）

| code | 枚举（语义） |
|------|-------------|
| `0000` | 成功 |
| `9003` | Empty_Param 参数为空 |
| `9004` | Illegal_Param 参数非法 |
| `9007` | 未授权 / Token 无效 |
| 其它 | Exists 已存在、Result_Not_Exist 结果不存在、Server_Error 服务器错误（`9999`） |

> 注：具体枚举名以 `common/core/BaseException` 为准；重建时至少需覆盖上表语义。

### 4.3 认证（JWT）

- 登录成功返回 JWT token；后续需鉴权的请求以 **URL 参数 `token=xxx`** 传递（现状）。
- token 由 `JwtToken.createToken(key, value)` 生成：算法 HMAC256，claim key = `Constants.AccountCode`（值 `accountCode`），value = 账户 `code`；默认 60 分钟过期，默认密钥见 `JwtToken`。
- 校验：`JwtToken.verifier(token)`。`LoginInterceptor` 实现了校验逻辑但**当前未挂载**到拦截链（仅 `ContextInterceptor` 全局生效，注入 traceId 到 MDC）。
- 密码存储：`Md5.md5(password)`（登录比对时对明文 MD5 后与库值比较）。

### 4.4 分页

- 请求参数：`curPage`（页码，从 1 起）、`pageSize`（每页条数，默认 25）。
- 响应用 `PageVO`：`records`(List)、`totalRow`、`totalPage`、`curPage`、`pageSize`。
- 列表实现：`count()` 求总数 + `page()`/`limit offset,size` 取数据。MyBatis-Plus 分页插件已启用。

### 4.5 主键与编码（ID 生成）

- 所有业务主键 `id` 用**百度 UidGenerator**（`uidGenerator.getUID()`，Snowflake 改良，63 位）。
- 业务实体普遍有 `code` 字段作为对外编码，多数取 `String.valueOf(id)` 或独立 UID。
- UID 的 worker 注册表 `worker_node` 由**独立 uid 数据源**管理（见 §4.7）。

### 4.6 实体公共约定（MyBatis-Plus）

- 实体用 Lombok `@Data` / `@Builder`；非表字段标 `@TableField(exist = false)`。
- 创建/更新时间由 `MybatisPlusMetaObjectHandler` 自动填充。
- DAO 层用 `LambdaQueryWrapper` 条件构造；复杂查询写 `resources/mapper/<domain>/*.xml`。
- 表名/列名为 `snake_case` 或保留原驼峰列名（部分表列名直接用驼峰，如 `englishName`，见各域 DDL）。

### 4.7 数据源与数据库

- 默认数据库：**H2 文件库**（MySQL 兼容模式）：
  `jdbc:h2:file:${database:/tmp/aicode};MODE=MYSQL;DATABASE_TO_UPPER=false;AUTO_SERVER=TRUE`，账号 `sa/sa`。
- 连接池：Druid（stat filter）。
- **双数据源**（同库不同 MyBatis 配置）：
  - `tidb` 数据源（`@Primary` 业务）：MyBatis-Plus 扫描 `com.aicode.*.dao.mapper`。
  - `uid` 数据源：扫描 `com.baidu.fsg.uid.worker.dao`，仅管理 `worker_node`。
- Schema/数据初始化：`spring.sql.init` 加载 `classpath:db/schema.sql` + `classpath:db/data.sql`（`mode: always`）。
- H2 控制台：`/h2`（开发期开启，生产须关闭）。

### 4.8 启动配置要点（application.yml）

- `server.port: 8080`，`server.shutdown: graceful`（30s）。
- `spring.threads.virtual.enabled: true`（虚拟线程）。
- `spring.main.allow-bean-definition-overriding: true`。
- `management.server.port: 8088`，`endpoints.web.exposure.include: "*"`，Prometheus 指标开启。
- Jackson 日期 `yyyy-MM-dd HH:mm:ss`，时区 `GMT+8`。
- 日志 Logback（`logback-debug.xml`），`com.aicode` 级别 debug。

### 4.9 默认初始化数据（db/data.sql 语义，重建必备）

- 默认账户：`admin` / 密码 `888888`（库内存 MD5 值），表 `account`。
- 全局设置 `setting` 关键项（见 [spec-settings-repository.md](spec-settings-repository.md)）：`Workspace`（工作空间目录）、`Template_Path`（模板默认路径）、`Repository_Path`（ZIP 仓库路径）、`DefaultDatabase`、`GitHome_Default` 等。
- 框架技术池 `frameworks` 预置若干技术栈（指向 Git 模板仓库），如 `springcloud3.3.9-mybatisplus-redis-java21` 等。

---

## 5. 实时通信（WebSocket）

- 端点：`/websocket.shtml`（Jakarta WebSocket + Spring 配置器注入 Bean）。
- 管理器：`WSClientManager`（`ConcurrentHashMap<String, Session>` 静态持有），`sendMessage(msg)` 向客户端推送构建日志。
- 用途：代码生成主链路每一步进度实时推送（见 [spec-codegen.md](spec-codegen.md)）。

---

## 6. 端到端主流程（用户视角，串联各规格）

```
1. 登录            POST /login/signin            → token                [spec-auth-account]
2. 建项目          POST /project/build           → projectCode          [spec-project]
3. 导入 SQL        POST /project/sql/build       → 保存 tsql            [spec-sql-parse]
4. 初始化/解析     POST /project/init            → 建库+解析出映射       [spec-sql-parse]
5. 查看映射        GET  /project/relationship/listMapClassTable          [spec-mapping-display]
6. 配关联/显示属性 POST /project/relationship/build, /displayAttribute/save [spec-mapping-display]
7. 选框架          GET  /framework/list, POST /project/framwork/add       [spec-frameworks-template]
8. 配 Git 仓库     POST /project/repository/build                         [spec-settings-repository]
9. 执行生成        GET  /project/job/execute?code=<projectCode>           [spec-codegen]
10. 看日志/下载    GET  /logs/load, GET /project/download/{name}          [spec-codegen / spec-project]
```

---

> 各功能域规格独立可读，但共享本文「§4 通用约定」。重建时先实现本文约定（响应、错误码、认证、分页、ID、数据源、初始化数据），再逐域实现。
