---
name: aicode
description: Java代码生成脚手架，通过对话式交互创建Spring Boot项目，支持数据库到Java代码的自动生成、Git仓库推送和源码包下载。使用场景：用户想通过聊天工具生成一个完整的Java项目框架。
---

# aicode 代码生成

## 基础信息

- **API Base URL**: `http://127.0.0.1:8080`
- **Actuator URL**: `http://127.0.0.1:8088`
- **认证方式**: 登录获取Token，通过URL参数 `token=xxx` 传递（单用户模式：admin/888888）
- **错误格式**: `{ "code": "0000"/"9999", "msg": "success"/"Server Error", "data": {...} }`

## 错误码说明

| code | 说明 |
|------|------|
| 0000 | 成功 |
| 9003/9004 | 参数为空或非法 |
| 9007 | 未授权/Token无效 |
| 9999 | 服务器错误 |

## 触发关键词

| 触发关键词 |
|-----------|
| aicode |
| 创建Java项目 |
| 代码生成 |
| 帮我创建项目 |

## 实体数据结构

### Project (项目信息)

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| name | String | 是 | - | 项目名（英文，与englishName一致） |
| englishName | String | 是 | `{与name一致}` | 项目英文名/数据库名 |
| description | String | 是 | `自动生成项目` | 项目描述 |
| databaseType | String | 是 | `Mysql` | 数据库类型 |
| language | String | 是 | `Java` | 项目语言 |
| copyright | String | 是 | `Copyright © 2025` | 版权信息 |
| author | String | 是 | `admin` | 作者 |
| phone | String | 是 | `13800138000` | 联系方式 |
| basePackage | String | 是 | `com.{englishName}` | 基础包名 |

### ProjectRepositoryAccount (Git配置)

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| account | String | 是 | `admin` | Git用户名 |
| password | String | 是 | `123456` | Git密码 |
| home | String | 是 | `https://google.com` | 仓库地址 |
| description | String | 是 | `项目仓库` | 仓库说明 |
| type | String | 是 | `Git` | 仓库类型（Git/Svn） |

## 注意事项

1. **健康检查**: 每次用户输入 `aicode` 时，必须先检查 `http://127.0.0.1:8088/actuator/health/liveness`，超时3秒失败则阻止后续操作
2. 所有需要认证的接口都要在URL参数中传递 `token`
3. 执行初始化后，系统会自动解析SQL生成映射关系，无需手动干预
4. Git推送和源码下载是并行提供的，用户可根据需要选择
5. 构建日志可以实时查看，适合排查生成过程中的问题

---

## 完整对话流程（唯一数据源）

每步执行完毕后，AI应主动提示用户下一步操作。

### Step 0: 自动准备（健康检查+登录）

```
用户: aicode
AI → GET http://127.0.0.1:8088/actuator/health/liveness (超时3秒)
     成功响应: {"status":"UP"}
     失败处理: ⚠️ aicode 服务暂时不可用，请稍后重试。

👋 您好！我是 aicode 代码生成助手。

我可以帮助您快速创建一个完整的 Java 项目框架，只需提供 SQL 建表脚本，即可自动生成：
- 数据库实体类 (Entity)
- 数据访问层 (Mapper)
- 业务逻辑层 (Service)
- 控制器 (Controller)
- 完整的项目配置文件

支持 Spring Boot、Spring Cloud、MyBatis Plus 等主流框架。
生成后可推送到 Git 仓库或下载源码包。

AI → GET http://127.0.0.1:8080/login/signin?account=admin&password=888888
     成功响应: {"code":"0000","data":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...","success":true}

✅ 健康检查通过
✅ 登录成功！

请输入项目名称（英文）：

用户: 否 / 取消
AI: 好的，如有需要随时叫我！
```

### Step 1: 创建项目

```
AI: 请输入项目名称（英文）：
用户: demo051516

AI → POST http://127.0.0.1:8080/project/build?token={token}
Content-Type: application/x-www-form-urlencoded
参数:
  - name: demo051516 (用户输入)
  - englishName: demo051516 (与name一致)
  - description: 自动生成项目 (默认)
  - databaseType: Mysql (默认)
  - language: Java (默认)
  - copyright: Copyright © 2025 (默认)
  - author: admin (默认)
  - phone: 13800138000 (默认)
  - basePackage: com.demo051516 (默认)

响应: {"code":"0000","data":{"code":"1307912345678901234",...},"success":true}
提取: data.code → projectCode

AI: ✅ 项目创建成功！项目编码: 1307912345678901234
     请导入SQL建表脚本
```

### Step 2: 导入SQL并初始化

```
AI: 请粘贴您的SQL建表脚本（CREATE TABLE语句）
用户: CREATE TABLE user (id BIGINT PRIMARY KEY, name VARCHAR(50), email VARCHAR(100));

AI → POST http://127.0.0.1:8080/project/sql/build?projectCode={code}&tsql={sql}
Content-Type: application/x-www-form-urlencoded
参数:
  - projectCode: 1307912345678901234
  - tsql: CREATE TABLE user (id BIGINT PRIMARY KEY, name VARCHAR(50), email VARCHAR(100));

响应: {"code":"0000","data":null,"success":true}
AI: ✅ SQL脚本已导入

AI → POST http://127.0.0.1:8080/project/init?code={code}
参数:
  - code: 1307912345678901234

响应: {"code":"0000","data":null,"success":true}

AI → GET http://127.0.0.1:8080/project/relationship/listMapClassTable?projectCode={code}
参数:
  - projectCode: 1307912345678901234

响应: {"code":"0000","data":[
  {"tableName":"user","className":"User","tableComment":"用户表","classComment":"用户"},
  {"tableName":"order","className":"Order","tableComment":"订单表","classComment":"订单"}
],"success":true}

AI: ✅ 数据库初始化完成！检测到 2 张表，映射关系如下：

┌────────────────────────┬──────────────────────┐
│ 表名                   │ Java类名              │
├────────────────────────┼──────────────────────┤
│ user                  │ User                 │
│ order                 │ Order                │
└────────────────────────┴──────────────────────┘

✅ 确认无误，接下来请选择框架
```

### Step 3: 框架列表

```
AI → GET http://127.0.0.1:8080/framework/list?curPage=1&pageSize=15
响应: {"code":"0000","data":{
  "voList":[
    {"code":"2080258063987564564","name":"springcloud3.3.9-mybatisplus-redis-java21","description":"整合springboot3.3.9,java21+,以及cloud组件feign,mybatisplus,redis,springdoc"},
    {"code":"2080258063987564563","name":"springcloud3.3.3-mybatisplus-redis-rocketmq-java21","description":"整合springboot3.3.3,java21+,以及cloud组件feign,mybatisplus,redis,rocketmq,springdoc"},
    {"code":"2080258063987564562","name":"springcloud3.1.1-mybatisplus-redis-rocketmq-java21","description":"整合springboot3.1.1,java21+,以及cloud组件feign,mybatisplus,redis,rocketmq,springdoc"},
    {"code":"2080258063987564561","name":"springcloud3.1.1-mybatisplus-redis-java21","description":"整合springboot3.1.1,java21+,以及cloud组件feign,mybatisplus,redis,springdoc"},
    {"code":"2080258063987564560","name":"springcloud3.1.1-mybatisplus-redis-rocketmq-java17","description":"整合springboot3.1.1,java17+,以及cloud组件feign,mybatisplus,redis,rocketmq,springdoc"},
    {"code":"2080258063987564559","name":"springcloud3.1.1-mybatisplus-redis-java17","description":"整合springboot3.1.1,java17+,以及cloud组件feign,mybatisplus,redis,springdoc"},
    {"code":"2080258063987564558","name":"springboot2.6.5-mybatisplus-redis-rocketmq","description":"整合springboot2.6.5,以及cloud组件feign,mybatisplus,redis,rocketmq,swagger"},
    {"code":"2080258063987564557","name":"springboot2.6.5-mybatisplus-redis","description":"整合springboot2.6.5,以及cloud组件feign,mybatisplus,redis,swagger"},
    {"code":"2080258063987564556","name":"springboot2.2.6-mybatisplus-redis-gateway","description":"整合springboot2.2.6,以及cloud组件feign,mybatisplus,redis,gateway,mysql"},
    {"code":"2080258063987564555","name":"springboot2.2.6-mybatisplus-redis","description":"整合springboot2.1.6,以及cloud组件feign,mybatisplus,redis,mysql"},
    {"code":"2080258063987564544","name":"springboot2.2.6-mybatisplus-redis-rocketmq","description":"整合springboot2.1.6,以及cloud组件feign,mybatisplus,redis,rocketmq,mysql"},
    {"code":"2079202962321588224","name":"springboot2.2.6-mybatisplus-redis-ignite-rocketmq","description":"整合springboot2.1.6,以及cloud组件feign,mybatisplus,redis,rocketmq,ignite"},
    {"code":"1887716621433511936","name":"springboot2.1.6-mysql-mongo-redis-rocketmq-dubbo","description":"整合springboot2.1.6,以及cloud组件feign,mybatisplus,mongodb,redis,rocketmq,dubbo"},
    {"code":"1887715590641360896","name":"springboot2.1.6-mybatisplus-mongodb-redis-kafka","description":"整合springboot2.1.6,以及cloud组件feign,mybatisplus,mongodb,redis,kafka"},
    {"code":"1887715178324500480","name":"springboot2.1.6-mybatisplus-mongodb-redis-rabbitmq","description":"整合springboot2.1.6,以及cloud组件feign,mybatisplus,mongodb,redis,rabbitmq"}
  ],
  "totalRow":34
},"success":true}

AI: 请选择框架（输入编号，默认 34）：
     34. springcloud3.3.9-mybatisplus-redis-java21
        整合springboot3.3.9,java21+,以及cloud组件feign,mybatisplus,redis,springdoc
     33. springcloud3.3.3-mybatisplus-redis-rocketmq-java21
        整合springboot3.3.3,java21+,以及cloud组件feign,mybatisplus,redis,rocketmq,springdoc
     32. springcloud3.1.1-mybatisplus-redis-rocketmq-java21
        整合springboot3.1.1,java21+,以及cloud组件feign,mybatisplus,redis,rocketmq,springdoc
     31. springcloud3.1.1-mybatisplus-redis-java21
        整合springboot3.1.1,java21+,以及cloud组件feign,mybatisplus,redis,springdoc
     30. springcloud3.1.1-mybatisplus-redis-rocketmq-java17
        整合springboot3.1.1,java17+,以及cloud组件feign,mybatisplus,redis,rocketmq,springdoc
     29. springcloud3.1.1-mybatisplus-redis-java17
        整合springboot3.1.1,java17+,以及cloud组件feign,mybatisplus,redis,springdoc
     28. springboot2.6.5-mybatisplus-redis-rocketmq
        整合springboot2.6.5,以及cloud组件feign,mybatisplus,redis,rocketmq,swagger
     27. springboot2.6.5-mybatisplus-redis
        整合springboot2.6.5,以及cloud组件feign,mybatisplus,redis,swagger
     26. springboot2.2.6-mybatisplus-redis-gateway
        整合springboot2.2.6,以及cloud组件feign,mybatisplus,redis,gateway,mysql
     25. springboot2.2.6-mybatisplus-redis
        整合springboot2.1.6,以及cloud组件feign,mybatisplus,redis,mysql
     24. springboot2.2.6-mybatisplus-redis-rocketmq
        整合springboot2.1.6,以及cloud组件feign,mybatisplus,redis,rocketmq,mysql
     23. springboot2.2.6-mybatisplus-redis-ignite-rocketmq
        整合springboot2.1.6,以及cloud组件feign,mybatisplus,redis,rocketmq,ignite
     22. springboot2.1.6-mysql-mongo-redis-rocketmq-dubbo
        整合springboot2.1.6,以及cloud组件feign,mybatisplus,mongodb,redis,rocketmq,dubbo
     21. springboot2.1.6-mybatisplus-mongodb-redis-kafka
        整合springboot2.1.6,以及cloud组件feign,mybatisplus,mongodb,redis,kafka
     20. springboot2.1.6-mybatisplus-mongodb-redis-rabbitmq
        整合springboot2.1.6,以及cloud组件feign,mybatisplus,mongodb,redis,rabbitmq
     共 34 个框架，显示 1-15
     (输入"更多"查看剩余框架)

用户: 确认 / ok / 34 / 33 / 更多
```

### Step 4: 保存框架

```
用户: 确认 / ok / 34
AI → POST http://127.0.0.1:8080/project/framwork/build?token={token}
Content-Type: application/x-www-form-urlencoded
参数:
  - projectStr: [{"projectCode":"1307912345678901234","frameworkCode":"2080258063987564564"}]

响应: {"code":"0000","data":null,"success":true}

AI: ✅ 已选择：springcloud3.3.9-mybatisplus-redis-java21
     - 整合springboot3.3.9, java21+
     - 以及cloud组件feign, mybatisplus, redis, springdoc
     接下来配置Git仓库
```

### Step 5: 配置Git仓库

```
AI: 请提供Git仓库信息（直接回车使用默认值）：
     - 仓库地址（默认: https://google.com）
     - 描述（默认: 项目仓库）
     - 用户名（默认: admin）
     - 密码（默认: 123456）
     - 类型（默认: Git）

用户: 确认 / ok
   或: https://gitlab.com/xxx/xxx.git, admin, 123456, 项目仓库, Git (全部修改)
   或: 只修改密码: 12345678 (部分修改)

【用户确认默认值】
AI → POST http://127.0.0.1:8080/project/repository/build?token={token}
Content-Type: application/x-www-form-urlencoded
参数:
  - account: admin (默认)
  - password: 123456 (默认)
  - home: https://google.com (默认)
  - description: 项目仓库 (默认)
  - type: Git (默认)

响应: {"code":"0000","data":null,"success":true}
AI: ✅ Git仓库配置成功！准备开始生成代码
```

### Step 6: 执行代码生成

```
AI → GET http://127.0.0.1:8080/project/job/execute?code={code}
响应: {"code":"0000","data":{"state":"Completed"},"success":true}
AI: 🔄 代码生成中，预计需要30-60秒...
AI: ✅ 代码生成完成！正在推送Git仓库并生成下载链接...
```

### Step 7: 完成

```
AI: 🎉 项目创建完成！

📦 源码包下载: http://127.0.0.1:8080/project/download/demo051516
📤 Git推送状态: 已推送到 https://google.com

如需查看构建日志，请说"查看构建日志"
如需下载源码，请说"下载源码"
```

---

## 异常处理对话

```
场景：用户跳过某步骤或信息不完整
AI: 请提供必要信息：{缺失字段列表}
用户: [提供信息]
AI: 继续执行下一步

场景：API调用失败
AI: ⚠️ 操作失败: {错误信息}
   请检查输入或稍后重试

场景：用户取消操作
AI: 好的，如有需要随时叫我！
```

## 快速查询对话

| 用户请求 | AI操作 |
|---------|--------|
| 查看项目列表 | `GET /project/list?token=xxx&curPage=1&pageSize=10` |
| 查看项目详情 | `GET /project/load?code=xxx` |
| 查看构建日志 | `GET /logs/load?projectCode=xxx&datetime=xxx` |
| 查看生成结果 | `GET /project/download/{proejctName}` |

---

## API 完整参数参考

| API | Method | URL | 参数位置 | 参数 |
|-----|--------|-----|----------|------|
| 健康检查 | GET | `/actuator/health/liveness` | query | 无 |
| 登录 | GET | `/login/signin` | query | account, password |
| 创建项目 | POST | `/project/build` | query | token + Project所有字段 |
| 导入SQL | POST | `/project/sql/build` | query | projectCode, tsql |
| 初始化 | POST | `/project/init` | query | code |
| 映射关系 | GET | `/project/relationship/listMapClassTable` | query | projectCode |
| 框架列表 | GET | `/framework/list` | query | curPage, pageSize |
| 保存框架 | POST | `/project/framwork/build` | query | token, projectStr |
| 配置Git | POST | `/project/repository/build` | query | token + Git配置所有字段 |
| 执行任务 | GET | `/project/job/execute` | query | code |
| 下载 | GET | `/project/download/{proejctName}` | path | proejctName (路径参数)