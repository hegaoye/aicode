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