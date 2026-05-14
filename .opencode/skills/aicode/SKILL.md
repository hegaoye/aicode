---
name: aicode
description: Java代码生成脚手架，通过对话式交互创建Spring Boot项目，支持数据库到Java代码的自动生成、Git仓库推送和源码包下载。使用场景：用户想通过聊天工具生成一个完整的Java项目框架。
---

# aicode 代码生成

## 基础信息

- **API Base URL**: `http://127.0.0.1:8080`
- **认证方式**: 登录获取Token，通过URL参数 `token=xxx` 传递（单用户模式）
- **错误格式**: `{ "code": 0, "msg": "success", "data": {...} }` 或 `{ "code": 500, "msg": "错误信息", "data": null }`

## 错误码说明

| code     | 说明         |
|----------|------------|
| 0000     | 成功         |
| 9003/9004       | 参数为空或非法 |
| 9007      | 未授权/Token无效 |
| 9999      | 服务器错误      |

## 工作流程

完整的项目创建流程包含以下步骤：

### Step 1: 登录

用户输入账号密码进行登录，获取后续操作的Token凭证。

- **API**: `GET /login/signin`
- **参数**: `account`, `password`
- **返回**: `{ "code": 0, "data": "token字符串" }`

### Step 2: 创建项目

录入项目的基础信息，创建一个新项目。

- **API**: `POST /project/build`
- **参数**:
  - `name` - 项目名（最长128个汉字）
  - `englishName` - 项目英文名/数据库名（最长256个英文字符）
  - `author` - 作者
  - `basePackage` - 项目基础包名（如：com.example）
  - `databaseType` - 数据库类型（如：Mysql, Oracle）
  - `language` - 项目语言（如：Java）
  - `copyright` - 版权信息
  - `phone` - 联系方式
  - `token` - 登录获取的Token

### Step 3: 导入TSQL脚本并初始化

分两个小步骤完成：先导入SQL脚本，再执行数据库初始化并生成映射关系。

#### Step 3.1: 导入TSQL脚本

将标准SQL建表脚本导入系统存储。

- **API**: `POST /project/sql/build`
- **参数**:
  - `projectCode` - 项目编码（Step 2返回的code）
  - `tsql` - SQL脚本内容（完整的CREATE TABLE语句）

#### Step 3.2: 执行数据库初始化并生成映射

系统根据TSQL脚本创建数据库，解析表结构，生成Java类映射关系并存储到数据库。

- **API**: `POST /project/init`
- **参数**:
  - `code` - 项目编码
  - `token` - Token

**内部处理逻辑**:
1. 根据TSQL脚本创建数据库和表
2. 解析数据库表结构（表名、列名、类型、备注等）
3. 生成Java类映射信息（MapClassTable、MapFieldColumn、MapRelationship）
4. 存储映射关系到数据库

### Step 4: 确认类表映射关系

查看根据TSQL自动生成的数据库表和Java类的映射关系，确认无误后进入下一步。

- **API**: `GET /project/relationship/listMapClassTable`
- **参数**: `projectCode` - 项目编码
- **返回**: 包含该项目的所有类表映射信息列表

### Step 5: 选择技术框架

分两个小步骤：先查询可选的技术框架列表，再选择需要的框架。

#### Step 5.1: 查询可选框架列表

获取系统支持的所有技术框架选项。

- **API**: `GET /framework/list`
- **参数**: 无
- **返回**: 技术框架列表（包含code、name等字段）

#### Step 5.2: 保存技术框架选择

将用户选择的技术框架关联到项目。

- **API**: `POST /project/framwork/build`
- **参数**:
  - `projectStr` - JSON数组格式，如：`[{"projectCode":"xxx","frameworkCode":"yyy"}]`
  - `token` - Token

### Step 6: 配置Git仓库

填写Git账号密码和仓库地址，保存版本控制配置。

- **API**: `POST /project/repository/build`
- **参数**:
  - `projectCode` - 项目编码
  - `account` - Git用户名
  - `password` - Git密码
  - `home` - 仓库地址（如：https://gitlab.com/xxx/xxx.git）
  - `type` - 仓库类型（如：Git）
  - `token` - Token

### Step 7: 执行代码生成任务

开始执行项目构建，系统会自动下载框架，通过Beetl、Freemarker等模板引擎完成代码生成。

- **API**: `GET /project/job/execute`
- **参数**: `code` - 任务编码（可通过 `POST /project/job/build` 创建任务获取）

### Step 8: 下载源码包或推送Git

代码生成完成后，根据配置自动推送到Git仓库，同时提供源码包下载。

- **下载源码包API**: `GET /project/download/{projectName}`
- **参数**: `projectName` - 项目英文名

### Step 9: 查看构建日志

查看项目构建过程中的日志信息，便于排查问题。

- **API**: `GET /logs/load`
- **参数**:
  - `projectCode` - 项目编码
  - `datetime` - 时间戳

## 实体数据结构

### Project (项目信息)

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 项目名 |
| englishName | String | 是 | 项目英文名/数据库名 |
| author | String | 是 | 作者 |
| basePackage | String | 是 | 基础包名 |
| databaseType | String | 是 | 数据库类型 |
| language | String | 是 | 项目语言 |
| copyright | String | 是 | 版权信息 |
| phone | String | 是 | 联系方式 |

### ProjectSql (SQL脚本)

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| projectCode | String | 是 | 项目编码 |
| tsql | String | 是 | SQL脚本内容 |

### ProjectFramwork (技术框架)

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| projectCode | String | 是 | 项目编码 |
| frameworkCode | String | 是 | 技术框架编码 |

### ProjectRepositoryAccount (Git配置)

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| projectCode | String | 是 | 项目编码 |
| account | String | 是 | Git用户名 |
| password | String | 是 | Git密码 |
| home | String | 是 | 仓库地址 |
| type | String | 是 | 仓库类型（Git/Svn） |

## 示例对话流程

### 场景1：创建一个Spring Boot项目

**用户**: 帮我创建一个Spring Boot项目，项目名叫"测试项目"，英文名test_project，数据库用MySQL，基础包com.example

**AI执行步骤**:
1. 登录: `GET /login/signin?account=xxx&password=xxx` → 获取token
2. 创建项目: `POST /project/build` + 项目信息 + token
3. 导入SQL: `POST /project/sql/build` + SQL脚本
4. 初始化: `POST /project/init` + code
5. 查询映射: `GET /project/relationship/listMapClassTable?projectCode=xxx`
6. 查询框架: `GET /framework/list`
7. 选择框架: `POST /project/framwork/build` + 框架数组
8. 配置Git: `POST /project/repository/build` + Git信息
9. 执行任务: `GET /project/job/execute?code=xxx`
10. 下载: `GET /project/download/test_project`

### 场景2：查看构建日志

**用户**: 查看一下测试项目的构建日志

**AI执行**: `GET /logs/load?projectCode=xxx&datetime=xxx`

## 常用查询接口

| 功能 | API |
|------|-----|
| 获取项目详情 | `GET /project/load?code=xxx` |
| 获取项目列表 | `GET /project/list?token=xxx&curPage=1&pageSize=10` |
| 获取类表映射列表 | `GET /project/relationship/listMapClassTable?projectCode=xxx` |
| 获取表字段映射 | `GET /project/relationship/listMapFieldColumn?mapClassTableCode=xxx&associateCode=yyy` |
| 获取任务详情 | `GET /project/job/load?code=xxx` |
| 获取任务列表 | `GET /project/job/list?projectCode=xxx&curPage=1&pageSize=10` |

## 注意事项

1. 所有需要认证的接口都要在URL参数中传递 `token`
2. Step 3.2 执行初始化后，系统会自动解析SQL生成映射关系，无需手动干预
3. Git推送和源码下载是并行提供的，用户可根据需要选择
4. 构建日志可以实时查看，适合排查生成过程中的问题

## AI与用户对话流程

每步执行完毕后，AI应主动提示用户下一步操作。以下是完整的对话交互流程：

### Step 1: 登录

```
AI: 请输入账号密码进行登录（默认: admin / 888888）
用户: admin / 888888
AI → 调用登录API
AI: ✅ 登录成功！接下来开始创建项目
```

### Step 2: 创建项目

```
AI: 请提供项目信息：
     - 项目名称
     - 项目英文名（数据库名）
     - 数据库类型（MySQL/Oracle等）
     - 基础包名（如：com.example）
     - 作者、版权、联系方式
用户: 项目名=用户管理, 英文名=user_mg, 数据库=MySQL, 包=com.test, 作者=张三
AI → 调用创建项目API
AI: ✅ 项目创建成功！项目编码: {code}
     接下来请导入SQL建表脚本
```

### Step 3: 导入SQL并初始化

```
AI: 请粘贴您的SQL建表脚本（CREATE TABLE语句）
用户: CREATE TABLE user (
       id BIGINT PRIMARY KEY,
       name VARCHAR(50),
       email VARCHAR(100)
     );
AI → 调用导入SQL API
AI: SQL脚本已导入，正在初始化数据库并生成映射关系...
AI → 调用初始化API (/project/init)
AI: ✅ 数据库创建成功！
     检测到 {n} 张表，映射关系已生成
     请确认类表映射关系，确认后进入下一步
```

### Step 4: 确认类表映射

```
AI: 检测到以下类表映射关系：
     - user → User (Java类)
     - role → Role
     - permission → Permission
AI: 映射关系确认无误！接下来选择技术框架
```

### Step 5: 选择技术框架

```
AI → 调用框架列表API (/framework/list)
AI: 系统支持以下技术框架，请选择（输入编号，多选用逗号分隔）：
     1. Spring Boot + MyBatis Plus
     2. Spring Boot + JPA
     3. Spring Boot + MyBatis Plus + Redis
     4. Spring Boot + MyBatis Plus + Shiro
用户: 1, 3
AI → 调用保存框架API
AI: ✅ 已选择 2 个框架
     接下来配置Git仓库
```

### Step 6: 配置Git仓库

```
AI: 请提供Git仓库信息：
     - 仓库地址（如：https://gitlab.com/xxx/xxx.git）
     - 用户名
     - 密码
用户: https://gitlab.com/myrepo/user_mg.git, admin, 123456
AI → 调用保存Git配置API
AI: ✅ Git仓库配置成功！
     准备开始生成代码
```

### Step 7: 执行代码生成

```
AI → 调用执行任务API (/project/job/execute)
AI: 🔄 代码生成中，预计需要30-60秒...
AI: ✅ 代码生成完成！正在推送Git仓库并生成下载链接...
```

### Step 8: 完成

```
AI: 🎉 项目创建完成！

📦 源码包下载: GET /project/download/user_mg
📤 Git推送状态: 已推送到 https://gitlab.com/myrepo/user_mg.git

如需查看构建日志，请说"查看构建日志"
如需下载源码，请说"下载源码"
```

---

### AI提示词模板

| 场景 | AI提示词 |
|------|---------|
| 开始登录 | `请输入账号密码进行登录（默认: admin / 888888）` |
| 项目创建成功 | `✅ 项目创建成功！项目编码: {code}，接下来请导入SQL建表脚本` |
| SQL导入成功 | `✅ SQL脚本已导入，正在初始化数据库并生成映射关系...` |
| 初始化完成 | `✅ 数据库初始化完成！检测到 {n} 张表，映射关系已生成` |
| 确认映射 | `检测到以下类表映射关系，请确认是否正确：` |
| 列出框架 | `系统支持以下技术框架，请选择（输入编号，多选用逗号分隔）：` |
| 框架保存成功 | `✅ 已选择 {n} 个框架，接下来配置Git仓库` |
| Git配置成功 | `✅ Git仓库配置成功！准备开始生成代码` |
| 生成中 | `🔄 代码生成中，预计需要30-60秒...` |
| 生成完成 | `✅ 代码生成完成！正在推送Git仓库并生成下载链接...` |
| 任务完成 | `🎉 项目创建完成！源码包和Git推送均已就绪` |

---

### 简化对话流程（用户跳过某些步骤时）

```
用户：帮我快速创建一个项目
AI: 好的，请提供SQL建表脚本，其他信息我将使用默认值
用户: [粘贴SQL]
AI: 正在处理...
AI: ✅ 项目创建完成！
     📦 下载: /project/download/{englishName}
     📤 Git: 已推送（如果已配置）
```

### 异常处理对话

```
场景：用户跳过某步骤或信息不完整
AI: 请提供必要信息：{缺失字段列表}
用户: [提供信息]
AI: 继续执行下一步

场景：API调用失败
AI: ⚠️ 操作失败: {错误信息}
   请检查输入或稍后重试
```

---

### 快速查询对话

| 用户请求 | AI操作 |
|---------|--------|
| 查看项目列表 | `GET /project/list?token=xxx` |
| 查看项目详情 | `GET /project/load?code=xxx` |
| 查看构建日志 | `GET /logs/load?projectCode=xxx&datetime=xxx` |
| 查看生成结果 | `GET /project/download/{projectName}` |