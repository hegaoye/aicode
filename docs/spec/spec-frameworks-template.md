# 功能规格：框架技术池与模板（spec-frameworks-template）

> 通用约定见 [spec-overview.md §4](spec-overview.md)。本域定义「技术栈即模板」的可插拔机制——技术框架以外部 Git 模板仓库形式存在，与生成器解耦。

---

## 1. 功能目标

- 维护「框架技术池」（`frameworks`）：每个框架是一个指向 Git 模板仓库的技术栈定义（如 `springcloud3.3.9-mybatisplus-redis-java21`）。
- 支持公有/私有模板仓库（私有带账号密码）。
- 为项目选择一个或多个框架（`project_framwork`），生成时各框架生成到独立子目录。
- 定义模板引擎抽象（Freemarker / Beetl），运行期由模板内 `aicode.json` 声明。

---

## 2. 数据模型

### 表 `frameworks`（框架技术池）

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | bigint PK | 主键 |
| `code` | varchar(64) PK | 技术编码 |
| `name` | varchar(256) | 技术名称（同时是模板仓库内的目录名，生成时用于路径匹配） |
| `description` | varchar(256) | 技术描述 |
| `gitHome` | varchar(256) | 模板仓库 Git 地址 |
| `account` | varchar(32) | 私有仓库账号 |
| `password` | varchar(255) | 私有仓库密码 |
| `isPublic` | varchar(2) | 是否公有 Y/N |

### 表 `frameworks_template`（模板文件清单，运行期临时表）

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | bigint PK | 主键 |
| `code` | varchar(64) | 模板编码 |
| `frameworkCode` | varchar(64) | 所属框架编码 |
| `path` | varchar(256) | 模板文件相对路径 |

> `frameworks_template` 在每次构建时由生成器克隆模板仓库后**动态登记**，构建结束后**清空**（见 [spec-codegen.md](spec-codegen.md) §4.3）。

### 表 `project_framwork`（项目↔框架）

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | bigint PK | 主键 |
| `frameworkCode` | varchar(64) | 技术编码 |
| `projectCode` | varchar(64) | 项目编码 |

### 实体非表字段

- `Frameworks`：无额外。`ProjectFramwork.frameworks:Frameworks`（聚合）。

### 枚举 `TemplateEngineEnum`：`Freemarker`、`Beetl`；`getTemplate(String)` 不区分大小写。

---

## 3. API 端点

### 3.1 框架技术池（`FrameworksController`，`@RequestMapping("/framework")`）

| 方法 | 路径 | 入参 | 返回 | 说明 |
|------|------|------|------|------|
| GET | `/framework/list` | `curPage:Integer`, `pageSize:Integer` | `R`(分页) | 框架技术池分页列表【核心】 |
| GET | `/framework/load` | `code` | `R` | 框架详情 |
| POST | `/framework/build` | `Frameworks`(隐藏) | `R` | 新增框架（生成 code，保存 gitHome 等） |
| PUT/POST | `/framework/modify` | `Frameworks`(隐藏) | `R` | 修改框架 |
| PUT/POST | `/framework/updateIsPublic` | `Frameworks`(隐藏) | `R` | 切换公有/私有 |
| POST | `/framework/delete` | `FrameworksVO`(隐藏) | `R` | 删除框架 |
| GET | `/framework/load/code/{code}` | `@PathVariable code` | `FrameworksVO` | 按编码查（**@Deprecated**） |

`/framework/list` 响应示例（`data.voList` 元素）：
```json
{ "code":"2080258063987564564", "name":"springcloud3.3.9-mybatisplus-redis-java21",
  "description":"整合springboot3.3.9,java21+,以及cloud组件feign,mybatisplus,redis,springdoc" }
```

### 3.2 项目选用框架（`ProjectFramworkController`，`@RequestMapping("/project/framwork")`）

| 方法 | 路径 | 入参 | 返回 | 说明 |
|------|------|------|------|------|
| POST | `/project/framwork/add` | `projectStr:String`(隐藏) | `R` | 为项目设置框架【核心】：先 `remove`（按项目清旧）再 `saveBatch`（批量保存所选框架） |
| GET | `/project/framwork/load` | `projectCode`, `frameworkCode` | `R` | 查询项目某框架 |
| GET | `/project/framwork/list` | 分页参数 | `R` | 项目框架列表 |
| POST | `/project/framwork/build` | `projectStr:String` | `R` | 同 add 语义（**@Deprecated**） |
| PUT | `/project/framwork/modify` | `@RequestBody ProjectFramworkVO` | `boolean` | 修改（**@Deprecated**） |
| DELETE | `/project/framwork/delete` | `ProjectFramworkVO` | `R` | 删除（**@Deprecated**） |

> `projectStr` 为前端传入的项目+所选框架编码集合（JSON/约定字符串），服务端解析后批量建立 `project_framwork` 关系。

---

## 4. 模板引擎抽象（重建必需）

### 4.1 接口与实现

- 接口 `TemplateHelper`：`String generate(TemplateData data, String targetFilePath, String templatePath)`。
- 实现 `FreemarkerHelper`（`@Service`）：用 Freemarker `Configuration` 加载 `.ftl`，`Template.process` 渲染到目标文件（UTF-8）。
- 实现 `BeetlHelper`（`@Service`）：读取模板文本，`GroupTemplate.getTemplate(text)` 渲染。
- 两实现均把 `TemplateData` 经 JSON round-trip 转 Map 作为数据绑定。
- 注入：生成器同时持有 `freemarkerHelper` 与 `beetlHelper` 两个 `TemplateHelper`，按引擎类型选择。

### 4.2 模板仓库结构约定

- 模板仓库内按框架 `name` 分目录（生成时据 `frameworks.name` 匹配/裁剪无关目录）。
- 根目录放 `aicode.json` 声明引擎：`{ "engine": "Freemarker" | "Beetl" }`；缺失/无法识别时**默认 Freemarker**。识别文件名兼容：`aicode.json`、`aicode`、`ai-code.json`、`ai-code`（`adapterTemplateEngine` 实现）。
- 模板文件后缀：Freemarker `.ftl`、Beetl `.btl`，生成时去后缀。
- 模板路径/文件名使用占位符（生成时替换）：`${basepackage}`/`${className}`/`${classNameLower}`/`${dashedCaseName}`/`${module}`/`${model}`、状态类 `$classNameState$`；Beetl 风格 `$xxx$`。
- `README.md`、`.git` 目录在登记时跳过。

### 4.3 模板可用数据变量（`TemplateData`，模板编写契约）

模板内可引用（部分）：`${projectName}`、`${basePackage}`、`${module}`、`${model}`、`${table}`、`${clazz}`、`${tableName}`、`${className}`、`${classNameLower}`、`${dashedCaseName}`、`${notes}`、`${copyright}`、`${author}`、`${classes}`、`${columns}`、`${pkColumns}`、`${notPkColumns}`、`${fields}`、`${pkFields}`、`${notPkFields}`、`${tableFields}`、`${modelClasses}`、`${modelDatas}`、`${oneToOneList}`、`${oneToManyList}`、`${displayAttributes}`、`${states}`、`${classNameState}`、`${isRelation}`、`${mainField}`、`${joinField}`。

> 新增可用变量须先在 `TemplateData` 加字段（唯一扩展点），见 [code-style.md §12](../standards/code-style.md)。

---

## 5. 验收标准

- [ ] `/framework/list` 返回预置框架池（含 gitHome）。
- [ ] `/project/framwork/add` 能为项目批量设置框架，重复调用先清后存（幂等）。
- [ ] 公有仓库 clone 不需账号；私有仓库用 `account/password`。
- [ ] 模板含 `aicode.json` 时按声明引擎渲染；缺失时用 Freemarker。

---

## 6. 关联规格

- 模板如何被克隆、登记、渲染、清理 → [spec-codegen.md](spec-codegen.md)
- 框架仓库凭据与项目产物仓库凭据区分 → [spec-settings-repository.md](spec-settings-repository.md)
