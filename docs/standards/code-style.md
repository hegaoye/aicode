# AI-Code 编码规范（Code Style）

> 本文是 AI-Code 工程的**编码风格与约定基准**。
> 配套文档：[architecture.md](architecture.md)（架构）、[testing.md](testing.md)（测试）。

- 文档版本：v1.0
- 适用分支：`dev`
- 运行环境：Java 21、Spring Boot 3.3.9、Gradle 多模块

---

## 目录

1. [通用约定](#1-通用约定)
2. [模块与依赖规范](#2-模块与依赖规范)
3. [包与命名规范](#3-包与命名规范)
4. [分层职责规范](#4-分层职责规范)
5. [数据对象（Entity/DTO/VO）规范](#5-数据对象entitydtovo规范)
6. [接口与统一响应规范](#6-接口与统一响应规范)
7. [异常处理规范](#7-异常处理规范)
8. [持久层（MyBatis-Plus）规范](#8-持久层mybatis-plus规范)
9. [横切关注点规范](#9-横切关注点规范)
10. [Lombok 与样板代码](#10-lombok-与样板代码)
11. [日志规范](#11-日志规范)
12. [代码生成模板规范](#12-代码生成模板规范)
13. [安全编码红线](#13-安全编码红线)

---

## 1. 通用约定

- **语言/编译**：Java 21，源码与编译统一 **UTF-8**（各模块 `build.gradle` 已配置 `options.encoding = 'UTF-8'`）。
- **构建**：Gradle 多模块；新增第三方依赖统一加在对应模块的 `build.gradle`，并对齐既有版本风格（显式写版本号，不依赖隐式传递）。
- **缩进**：4 个空格，禁止使用 Tab。
- **行尾/文件**：LF 行尾，文件以空行结尾。
- **注释语言**：业务注释使用中文，与现有代码保持一致；类头保留 `Created by xxx on date` 风格或补充 Javadoc。
- **禁止提交**：`build/`、`.gradle/`、`bin/`、`*.iml`、`.idea/`、`*.log`（已在 `.gitignore` 覆盖）。

---

## 2. 模块与依赖规范

三模块依赖方向**单向、无环**，新增代码必须遵守：

```
aicode (Spring Boot 应用)  ──→  facade (契约)  ──→  common (基础工具)
```

| 模块 | 只能放什么 | 禁止放什么 |
|------|-----------|-----------|
| `common` | 与业务无关的工具、注解、枚举、`R`/`Page`/`BaseVO`/`BaseException` 等基类 | 任何业务实体、Spring Web 依赖、`@Controller`/`@Service` |
| `facade` | `entity` / `dto` / `vo` / `service` 接口 / `exceptions` | Service 实现、Controller、DAO 实现 |
| `aicode` | `*Controller` / `*ServiceImpl` / `*DAO` / `mapper` / `config` | 反向依赖（common/facade 不得依赖 aicode） |

**红线**：
- `common` 不得引入 `spring-boot-starter-web`。
- 业务接口（`*Service`）声明在 `facade`，实现（`*ServiceImpl`）放在 `aicode`，**接口与实现物理分离**。
- 不得在模块间制造循环依赖。

---

## 3. 包与命名规范

### 3.1 包结构：package-by-feature（按业务域分包）

根包 `com.aicode`，按业务域（feature）分一级包，域内再按层分子包：

```
com.aicode.<domain>.controller          控制器
com.aicode.<domain>.service       Service 实现 (aicode 模块)
com.aicode.<domain>.dao           DAO
com.aicode.<domain>.dao.mapper    MyBatis-Plus Mapper
com.aicode.<domain>.entity        实体 (facade 模块)
com.aicode.<domain>.dto / .vo     入参 / 出参 (facade 模块)
com.aicode.<domain>.service       Service 接口 (facade 模块)
```

现有业务域：`account`、`database`、`map`、`display`、`frameworks`、`module`、`project`、`setting`、`session`。新增功能**优先归入已有域**，确需新域时按上表完整建子包。

### 3.2 类命名后缀约定（强制）

| 后缀             | 含义 | 所在模块 | 示例 |
|----------------|------|---------|------|
| `*Controller`  | REST 控制器 | aicode | `ProjectController` |
| `*Service`     | 业务接口 | facade | `ProjectService` |
| `*ServiceImpl` | 业务实现 | aicode | `ProjectServiceImpl` |
| `*DAO`         | 数据访问封装 | aicode | `ProjectDAO` |
| `*Mapper`      | MyBatis-Plus Mapper | aicode | `ProjectMapper` |
| `*Exception`   | 域异常 | facade | `ProjectException` |
| `*Status`      | 状态枚举 | facade | `ProjectJobStatus` |

### 3.3 命名风格

- 类名 `UpperCamelCase`，方法/变量 `lowerCamelCase`，常量 `UPPER_SNAKE_CASE`。
- 数据库表/列 `snake_case`；映射到 Java 时由生成器转 `UpperCamelCase`/`lowerCamelCase`，破折号命名（前端）用 `dashedCaseName`（kebab-case）。
- 枚举值首字母大写（与 `ProjectJobState.Executing/Completed/Error`、`TemplateEngineEnum.Freemarker/Beetl` 一致）。

---

## 4. 分层职责规范

| 层                | 职责 | 不允许 |
|------------------|------|--------|
| `controller` 控制器 | 参数校验、调用 service、包装 `R` 返回 | 写业务逻辑、直接访问 Mapper |
| `service` 实现     | 业务编排、事务边界、组装 VO | 处理 HTTP 细节（request/response）|
| `dao` / `mapper` | 数据访问、条件构造 | 包含业务规则 |

- Controller 入参校验用 `spring-boot-starter-validation`（`@Valid` + DTO 上的约束注解）或显式断言；空值/非法参数返回标准错误码（见 §6）。
- 跨域调用通过 `facade` 的 `*Service` 接口，不得直接 `new` 实现类或跨域引用 `*ServiceImpl`。
- 三分层依赖方向**单向、无环**，新增代码必须遵守，单向调用 controller -> service -> dao。

---

## 5. 数据对象（Entity/DTO/VO）规范

严格区分入参与出参，不要用一个对象贯穿全层：

| 类型 | 用途 | 放置 |
|------|------|------|
| `entity`（PO） | 对应数据库表，MyBatis-Plus 映射 | `facade/<domain>/entity` |
| `*DTO` | 入参（请求），细分 `SaveDTO` / `ModifyDTO` / `PageDTO` | `facade/<domain>/dto` |
| `*VO` | 出参（响应），细分 `VO` / `SaveVO` / `ModifyVO` / `PageVO` | `facade/<domain>/vo` |

规范：
- **新增**用 `*SaveDTO`，**修改**用 `*ModifyDTO`，**分页查询**用 `*PageDTO`，避免单一大对象。
- VO 继承 `BaseVO`；分页响应统一用 `PageVO`（`records / totalRow / totalPage / curPage / pageSize`）。
- Entity ↔ DTO/VO 转换可用 `BeanUtils.copyProperties` 或 `StringTools` 提供的 `convertObject/convertList`（fastjson2 round-trip），转换逻辑放 service 层。
- 实体不要直接暴露给前端；Controller 返回 VO。

---

## 6. 接口与统一响应规范

### 6.1 统一响应体 `R`

所有 REST 接口返回 `common` 的 `R`：

```json
{ "code": "0000", "info": "success", "data": { }, "success": true }
```

错误码约定：

| code | 含义 |
|------|------|
| `0000` | 成功 |
| `9003` / `9004` | 参数为空 / 参数非法 |
| `9007` | 未授权 / Token 无效 |
| `9999` | 服务器错误 |

规范：
- 成功用 `R.success()` / `R.success(data)`；失败用 `R.failed(...)`，**禁止**裸返回实体或字符串。
- `GlobalResponseBodyAdvice` 当前**注释关闭**，因此各 Controller 须**显式**包装 `R`；若后续启用统一包装，须全局移除手动包装，避免双重包裹。

### 6.2 REST 约定

- 路径以业务域为前缀：`/project/...`、`/framework/...`、`/project/relationship/...`。
- 用 SpringDoc OpenAPI 3 注解（`@Tag`/`@Operation`/`@Schema`）描述接口，配合 Knife4j UI。
- 认证接口在请求中携带 `token`（现状为 URL 参数，安全改进见 §13）。

---

## 7. 异常处理规范

- 业务异常抛 `facade/exceptions` 下的域异常（继承 `BaseException`），按 `BaseExceptionEnum` 携带标准错误码与消息。
- 全局由 `ExceptionHandle`（`@ControllerAdvice`）统一捕获，转为标准 `R`，HTTP 400。**不要**在 Controller 里 `try/catch` 后吞掉异常或自行拼装错误结构。
- **唯一例外**：异步构建主链路 `GeneratorSVImpl.aiCode()` 因运行在 `@Async` 虚拟线程、脱离请求上下文，须在方法内 `try/catch` 并把失败写入 `ProjectJob.state=Error` + WebSocket/日志，不能让异常逃逸到线程顶层。
- 禁止 `e.printStackTrace()` 作为唯一处理（现存代码有此遗留，新代码改用 `log.error(msg, e)`）。

---

## 8. 持久层（MyBatis-Plus）规范

- 简单 CRUD 用 MyBatis-Plus 内置方法 + **Lambda 条件构造器** `LambdaQueryWrapper`（类型安全，避免裸字符串列名）。
- 复杂 SQL 写在 `resources/mapper/<domain>/*.xml`，与 Mapper 接口对应。
- 分页用 MyBatis-Plus 分页插件（`MybatisPlusInterceptor`），不要手写 limit 拼接。
- 创建/更新时间由 `MybatisPlusMetaObjectHandler` 自动填充，**不要**在业务里手动 set。
- **双数据源**：业务 Mapper 归 `tidbDataSource`（`MybatisPlusConfigTidb` 扫描 `com.aicode.*.dao.mapper`）；分布式 ID 的 `worker_node` 归 `uidDataSource`。新增业务 Mapper 默认进 tidb，**不要**误放到 uid 数据源扫描路径。
- 主键/分布式 ID 用 `UidGenerator.getUID()`（百度 UID），不要自造序列。

---

## 9. 横切关注点规范

- **链路追踪**：所有请求经 `ContextInterceptor` 注入 traceId 到 MDC；异步任务必须用 `MdcTaskDecorator` 传递 MDC，保证日志链路连续。
- **事务**：由 `TransactionalAopConfig` 的 AOP 切面统一管理，按约定切入 service 方法；避免在一个事务里做 git clone、文件 IO 等长耗时操作。
- **异步**：耗时构建用 `@Async`，由 `TaskExecutorConfig`（Java 21 虚拟线程）承载；`@Async` 方法须是 public 且经 Spring 代理调用（不要类内自调）。
- **实时日志**：构建过程进度统一经 `WSClientManager.sendMessage()` 推送，并 `logsSV.saveLogs()` 落盘，二者成对出现。

---

## 10. Lombok 与样板代码

- 实体/DTO/VO 用 `@Data`；需要构造器链式构建用 `@Builder`（现有 `Project.builder()...build()` 风格）。
- 日志用 `@Slf4j`，不要手写 `LoggerFactory.getLogger`。
- Lombok 仅作 `compileOnly + annotationProcessor`，不传递到运行期。
- 不要为 Lombok 已生成的 getter/setter 再手写重复代码。

---

## 11. 日志规范

- 框架：Logback，配置 `logback-debug.xml` / `logback-info.xml`；`com.aicode` 包默认 debug。
- 用占位符而非字符串拼接：`log.info("project: {}", project)`。
- 级别：调试细节 `debug`；关键流程节点 `info`；可恢复异常 `warn`；错误 `error` 并带异常对象。
- 切换日志级别时同步修改 `logging.config` 指向的文件名（见 application.yml 注释约定）。
- 禁止打印敏感信息（Git/SVN 密码、token）到日志。

---

## 12. 代码生成模板规范

生成器的「输出代码风格」由**外部模板**决定，模板编写须遵守：

- **模板数据唯一来源**是 `TemplateData`；新增可用变量须先在 `TemplateData` 加字段（一处），再在模板引用。可用变量见 `TemplateData` 类头 Javadoc（如 `${basePackage}`、`${className}`、`${model}`、`${columns}`、`${displayAttributes}` 等）。
- **占位符双风格**：Freemarker 用 `${xxx}`，Beetl 用 `$xxx$`；路径占位符（`${basepackage}`/`${className}`/`${module}`/`${model}`/`$classNameState$`）由 `GeneratorSVImpl.generator()` 统一替换，模板目录/文件名须使用这些约定名。
- **引擎声明**：模板仓库根放 `aicode.json`，用 `engine` 字段声明 `Freemarker`/`Beetl`；未声明默认 Freemarker。
- 模板文件后缀 `.ftl`（Freemarker）/ `.btl`（Beetl），生成时自动去除后缀。
- 新增模板引擎只需实现 `TemplateHelper` 接口（`generate(templateData, targetFilePath, templatePath)`）并注册为 `@Service`，不改生成主链路。

---

## 13. 安全编码红线

以下为新代码与改造须遵守：

- **凭据**：Git/SVN/账户密码**禁止明文**入库或入日志；逐步迁移到加密存储（如 Jasypt）。
- **认证**：新接口的 token **优先走请求头**（`Authorization`），不要新增「token 走 URL 参数」的接口；推动启用 `LoginInterceptor`。
- **默认口令**：生产环境禁止保留默认 `admin/888888`，须强制改密。
- **H2 控制台**：生产环境关闭（`spring.h2.console.enabled=false`，禁用 `web-allow-others`）。
- **SQL**：用户提供的建表 SQL 在受控库执行前须校验合法性（参考 README 关于非法 `"order"` 的说明），避免注入与执行失败。

---

> 规范如与现有代码冲突，遵循「就近一致 + 不扩散坏味道」：局部修改保持与周边代码一致，整体演进通过专门重构 PR 推进，并同步更新本文件。
