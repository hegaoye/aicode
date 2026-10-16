# Proposal

## Why

**前端目前是只读的 —— 用户打开 aicode 无法创建任何项目。**

这是接完 M1–M5（7 个页面、94 个用例）后暴露出的结构性缺口：

| 流程步骤 | 端点 | 前端状态 |
|---|---|---|
| 1. 建项目 | `POST /project/build` | ❌ **无入口** |
| 2. 导 SQL | `POST /project/sql/build` | ❌ 无入口 |
| 3. 触发解析 | `POST /project/init` | ❌ 无入口 |
| 4. 配显示属性 | `/displayAttribute/*` | ✅ 已接入（`DisplayAttributesPage`）|
| 5. 配关联关系 | `/project/relationship/*` | ❌ 无入口 |
| 6. 构建 | `GET /project/job/execute` | ✅ 已接入（`ProjectListPage`）|

**关键后果**：`DisplayAttributesPage`（M1 的第一个页面）是一个**孤儿页** —— 用户得先手工调 `POST /project/build` 和 `POST /project/init` 才能到达它，否则进去看到的是空列表。整个代码生成配置流程在 React 前端上**只有末端（构建）可达，起点和中段全断**。

用户要完成「粘一段建表 SQL → 得到一套能跑的 Java + 前端 CRUD 代码」这件事，今天**做不到**。

## What Changes

打通流程的**前 3 步**（建项目 → 导 SQL → 触发解析），让已接入的第 4 步（配显示属性）变得可达：

### 后端（3 处 VO 收敛）

现有 3 个写侧端点全部**违反既有 Requirement** `api-data-object` 的「Controller SHALL 返回 VO 而非持久化实体」——它们返裸实体：

| 端点 | 现状 | 改为 |
|---|---|---|
| `POST /project/build` | `R<Project>`（29 字段，含 7 个 null list）| `R<ProjectCreateVO>`（6 字段）|
| `POST /project/sql/build` | `R<ProjectSql>`（5 字段）| `R<ProjectSqlVO>`（4 字段）|
| `POST /project/sql/modify` | `R<ProjectSqlVO>`（已有 VO，但字段与实体不一致）| 对齐 |

### 前端（1 个新页面 + 1 个详情页扩展）

- **`NewProjectPage`**（`/#/project/new`）—— 建项目表单，**9** 个必填字段（`name` / `englishName` / `author` / `phone` / `basePackage` / `copyright` / `description` / `databaseType` / `language`），提交后跳详情页；
- **`ProjectDetailPage` 扩展「SQL 与解析」区块** —— 列出该项目已导入的 SQL 段、新增一段、点「解析」调 `POST /project/init`，解析后展示 `GET /project/relationship/listMapClassTable` 的结果（解析出哪些表 → 哪些类）；
- **路由表 + 列表页入口** —— `/project/new` 路由；`ProjectListPage` 加「新建项目」按钮。

**契约 / 生成物**：orval 重新生成，MSW mock 自动获得 3 个新端点的桩。

## BREAKING

`POST /project/build` 与 `POST /project/sql/build` 的**响应类型**从裸实体改为 VO —— 前端尚未消费这两个端点，无实际破坏。

## Capabilities

### Modified Capabilities

- **`api-data-object`** —— 新增 1 Requirement「写侧端点 SHALL 返回写侧 VO 而非实体全集」，含 2 Scenarios。既有 Req 1 是泛化表述，本 Req 把它**具体化到写侧**并给出字段清单（与 Req 65「裁切反向验证」配套）。

### New Capabilities

（无。页面形态归既有 `aicode-web-page` —— 本变更是该能力**新增 1 个 Requirement**「写侧表单页面 SHALL 提交契约生成物驱动的表单并消费写侧 VO」，故实为 Modified。）

## Impact

- **后端**：`ProjectController.build` / `ProjectSqlController.build` 的返回类型 + 2 个新 VO（`facade` 模块）+ `contracts/openapi/openapi.yaml`。
- **前端**：`NewProjectPage`（新文件 + 测试）、`ProjectDetailPage` 扩展、`ProjectListPage` 加按钮、`App.tsx` 路由。
- **测试**：后端 `ApiContractIT` 场景⑪（写侧 VO 投影）2 用例；前端新增页面测试 + 详情页测试扩展。预计前端 94 → 约 100 用例。
- **风险**：**中高**。写侧端点首次接入，涉及表单校验、错误处理、跳转。两处已知风险：① `POST /project/build` 的 9 个 `Assert.hasText` 字段与前端表单的**一一对应**（漏一个后端直接报 `9003`）② `@Parameter(hidden = true)` 把请求参数从契约里抹掉（实施期发现，见 design）。