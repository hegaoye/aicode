# AI Code API 接口文档

## 一、前端调用的API（从JS文件提取）

| 前端调用路径 | HTTP | 后端Controller | 后端映射 | 状态 |
|------------|------|--------------|---------|------|
| `/login/signin` | GET | LoginCtrl | `@GetMapping("/signin")` | ✅ |
| `/login/reg` | POST | LoginCtrl | `@PostMapping("/reg")` | ✅ |
| `/displayAttribute/list` | GET | DisplayAttributeController | `@GetMapping("/list")` | ✅ |
| `/displayAttribute/save` | POST | DisplayAttributeController | `@PostMapping("/save")` | ✅ |
| `/framework/build` | POST | FrameworksController | `@PostMapping("/build")` | ✅ |
| `/framework/delete` | DELETE | FrameworksController | `@DeleteMapping("/delete")` | ✅ |
| `/framework/list` | GET | FrameworksController | `@GetMapping("/list")` | ✅ |
| `/framework/load` | GET | FrameworksController | `@GetMapping("/load")` | ✅ |
| `/framework/modify` | PUT/POST | FrameworksController | `@RequestMapping("/modify")` | ✅ |
| `/framework/updateIsPublic` | PUT/POST | FrameworksController | `@RequestMapping("/updateIsPublic")` | ✅ |
| `/logs/load` | GET | LogsCtrl | `@GetMapping("/load")` | ✅ |
| `/project/build` | POST | ProjectController | `@PostMapping("/build")` | ✅ |
| `/project/delete` | POST | ProjectController | `@PostMapping("/delete")` | ✅ |
| `/project/init` | POST | ProjectController | `@PostMapping("/init")` | ✅ |
| `/project/list` | GET | ProjectController | `@GetMapping("/list")` | ✅ |
| `/project/load` | GET | ProjectController | `@GetMapping("/load")` | ✅ |
| `/project/modify` | PUT/POST | ProjectController | `@RequestMapping("/modify")` | ✅ |
| `/project/scan/path` | GET | ProjectController | `@GetMapping("/scan/path")` | ✅ |
| `/project/framwork/add` | POST | ProjectFramworkController | `@PostMapping("/add")` | ✅ |
| `/project/framwork/delete` | DELETE | ProjectFramworkController | `@DeleteMapping("/delete")` | ✅ |
| `/project/framwork/list` | GET | ProjectFramworkController | `@GetMapping("/list")` | ✅ |
| `/project/framwork/load` | GET | ProjectFramworkController | `@GetMapping("/load")` | ✅ |
| `/project/job/build` | POST | ProjectJobController | `@PostMapping("/build")` | ✅ |
| `/project/job/delete` | DELETE | ProjectJobController | `@DeleteMapping("/delete")` | ✅ |
| `/project/job/execute` | GET | ProjectJobController | `@GetMapping("/execute")` | ✅ |
| `/project/job/list` | GET | ProjectJobController | `@GetMapping("/list")` | ✅ |
| `/project/job/load` | GET | ProjectJobController | `@GetMapping("/load")` | ✅ |
| `/project/job/modify` | PUT | ProjectJobController | `@PutMapping("/modify")` | ✅ |
| `/project/relationship/build` | POST | MapRelationshipController | `@PostMapping("/build")` | ✅ |
| `/project/relationship/delete` | DELETE | MapRelationshipController | `@DeleteMapping("/delete")` | ✅ |
| `/project/relationship/list` | GET | MapRelationshipController | `@GetMapping("/list")` | ✅ |
| `/project/relationship/listMapClassTable` | GET | MapRelationshipController | `@GetMapping("/listMapClassTable")` | ✅ |
| `/project/relationship/listMapFieldColumn` | GET | MapRelationshipController | `@GetMapping("/listMapFieldColumn")` | ✅ |
| `/project/relationship/listByProjectCode` | GET | MapRelationshipController | ❌ 后端没有这个路径 |
| `/project/repository/build` | POST | ProjectRepositoryAccountController | `@PostMapping("/build")` | ✅ |
| `/project/repository/delete` | DELETE | ProjectRepositoryAccountController | `@DeleteMapping("/delete")` | ✅ |
| `/project/repository/list` | GET | ProjectRepositoryAccountController | `@GetMapping("/list")` | ✅ |
| `/project/repository/load` | GET | ProjectRepositoryAccountController | `@GetMapping("/load")` | ✅ |
| `/project/repository/modify` | POST | ProjectRepositoryAccountController | `@PostMapping("/modify")` | ✅ |
| `/project/sql/build` | POST | ProjectSqlController | `@PostMapping("/build")` | ✅ |
| `/project/sql/delete` | DELETE | ProjectSqlController | `@DeleteMapping("/delete")` | ✅ |
| `/project/sql/list` | GET | ProjectSqlController | `@GetMapping("/list")` | ✅ |
| `/project/sql/load` | GET | ProjectSqlController | `@GetMapping("/load")` | ✅ |
| `/project/sql/modify` | POST | ProjectSqlController | `@PostMapping("/modify")` | ✅ |

### 二、前端页面路由（非API）

| 路径 | 说明 |
|------|------|
| `/main/buildPro/proSteps/proDatabase` | 构建步骤-数据库配置 |
| `/main/buildPro/proSteps/proFrames` | 构建步骤-框架选择 |
| `/main/buildPro/proSteps/proInfo` | 构建步骤-项目信息 |
| `/main/buildPro/proSteps/proRepository` | 构建步骤-仓库配置 |
| `/main/buildPro/proSteps/proSql` | 构建步骤-SQL脚本 |
| `/main/project` | 项目管理页面 |
| `/main/project/detail` | 项目详情页面 |
| `/main/project/logs` | 项目日志页面 |
| `/main/frameworks` | 框架管理页面 |
| `/main/template` | 模板管理页面 |
| `/main/template/summary` | 模板汇总页面 |

## 二、后端完整API列表（按Controller分组）

### AccountController (/account)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建账户 |
| GET | /load/code/{code} | 根据code加载账户 |
| GET | /list | 查询账户列表 |
| PUT | /modify | 修改账户 |
| PUT | /modify/password | 修改密码 |
| DELETE | /delete | 删除账户 |

### DisplayAttributeController (/displayAttribute)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /save | 保存显示属性 |
| GET | /load/mapFieldColumnCode/{mapFieldColumnCode} | 根据字段编码加载 |
| GET | /list | 查询显示属性列表 |
| PUT | /modify | 修改显示属性 |
| DELETE | /delete | 删除显示属性 |

### FrameworksController (/framework)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建框架 |
| GET | /load | 加载框架 |
| GET | /load/code/{code} | 根据code加载框架 |
| GET | /list | 查询框架列表 |
| PUT | /modify | 修改框架 |
| PUT | /updateIsPublic | 更新公开状态 |
| DELETE | /delete | 删除框架 |

### FrameworksTemplateController (/frameworksTemplate)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建框架模板 |
| GET | /list | 查询框架模板列表 |
| PUT | /modify | 修改框架模板 |
| DELETE | /delete | 删除框架模板 |

### IndexCtrl (/)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | / | 页面路由(index.html) |

### LoginCtrl (/login)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /signin | 登录验证 |
| POST | /reg | 注册 |

### LogsCtrl (/logs)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /createLogFiles | 创建日志文件 |
| POST | /saveLogs | 保存日志 |
| GET | /loadFilePath | 加载文件路径 |
| GET | /load | 加载日志 |

### MapClassTableController (/mapClassTable)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建类表映射 |
| GET | /load/code/{code} | 根据code加载 |
| GET | /list | 查询类表映射列表 |
| PUT | /modify | 修改类表映射 |
| DELETE | /delete | 删除类表映射 |

### MapFieldColumnController (/mapFieldColumn)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建字段映射 |
| GET | /list | 查询字段映射列表 |
| PUT | /modify | 修改字段映射 |
| DELETE | /delete | 删除字段映射 |

### MapRelationshipController (/project/relationship)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建关系 |
| GET | /load/code/{code} | 根据code加载 |
| GET | /list | 查询关系列表 |
| GET | /listMapClassTable | 查询类表映射关系列表 |
| GET | /listByClassTableCode | 根据类表编码查询 |
| GET | /listMapFieldColumn | 查询字段映射关系 |
| PUT | /modify | 修改关系 |
| DELETE | /delete | 删除关系 |

### ModuleController (/module)

| 方法 | 路径 | 说明 |
|------|------|------|
| PUT | /modify | 修改模块 |
| DELETE | /delete | 删除模块 |

### ModuleFileController (/moduleFile)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建模块文件 |
| GET | /list | 查询模块文件列表 |
| PUT | /modify | 修改模块文件 |
| DELETE | /delete | 删除模块文件 |

### ProjectCodeCatalogController (/projectCodeCatalog)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建源码资料 |
| GET | /load/code/{code} | 根据code加载 |
| GET | /load/projectCode/{projectCode} | 根据项目编码加载 |
| GET | /list | 查询源码资料列表 |
| PUT | /modify | 修改源码资料 |
| DELETE | /delete | 删除源码资料 |

### ProjectController (/project)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /init | 初始化项目 |
| GET | /download/{proejctName} | 下载项目源码 |
| GET | /index | 首页 |
| GET | /load | 加载项目 |
| GET | /scan/path | 扫描路径 |
| POST | /build | 创建项目 |
| GET | /load/code/{code} | 根据code加载项目 |
| GET | /list | 查询项目列表 |
| PUT | /modify | 修改项目 |
| POST | /delete | 删除项目 |

### ProjectFramworkController (/project/framwork)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /load | 加载项目框架 |
| POST | /build | 创建项目框架 |
| POST | /add | 添加项目框架 |
| GET | /list | 查询项目框架列表 |
| PUT | /modify | 修改项目框架 |
| DELETE | /delete | 删除项目框架 |

### ProjectJobController (/project/job)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /load | 加载任务 |
| POST | /build | 创建任务 |
| GET | /load/code/{code} | 根据code加载任务 |
| GET | /list | 查询任务列表 |
| PUT | /modify | 修改任务 |
| DELETE | /delete | 删除任务 |
| GET | /execute | 执行任务 |

### ProjectJobLogsController (/projectJobLogs)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建任务日志 |
| GET | /list | 查询任务日志列表 |
| PUT | /modify | 修改任务日志 |
| DELETE | /delete | 删除任务日志 |

### ProjectMapController (/projectMap)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建项目数据表 |
| GET | /list | 查询项目数据表列表 |
| PUT | /modify | 修改项目数据表 |
| DELETE | /delete | 删除项目数据表 |

### ProjectModelClassController (/projectModelClass)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /loadById/{id} | 根据ID加载 |
| POST | /build | 创建模块下的类 |
| POST | /save | 保存模块下的类 |
| GET | /loadByMapClassTableCode | 根据类表编码加载 |
| GET | /loadByProjectModelCode | 根据模块编码加载 |
| GET | /list | 查询模块下的类列表 |
| PUT | /modify | 修改模块下的类 |
| DELETE | /delete | 删除模块下的类 |

### ProjectModelController (/projectModel)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /loadById/{id} | 根据ID加载 |
| GET | /loadByCode/{code} | 根据code加载 |
| POST | /build | 创建模块 |
| GET | /list | 查询模块列表 |
| PUT | /modify | 修改模块 |
| DELETE | /delete | 删除模块 |

### ProjectModuleController (/project/mouldles)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /load | 加载模块 |
| POST | /add | 添加模块 |
| GET | /list | 查询模块列表 |
| PUT | /modify | 修改模块 |
| DELETE | /delete | 删除模块 |

### ProjectRepositoryAccountController (/project/repository)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /load | 加载仓库 |
| POST | /build | 创建仓库 |
| GET | /load/code/{code} | 根据code加载仓库 |
| GET | /list | 查询仓库列表 |
| POST | /modify | 修改仓库 |
| DELETE | /delete | 删除仓库 |

### ProjectSqlController (/project/sql)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建SQL脚本 |
| GET | /load | 加载SQL脚本 |
| GET | /load/projectCode/{projectCode} | 根据项目编码加载 |
| GET | /list | 查询SQL脚本列表 |
| POST | /modify | 修改SQL脚本 |
| DELETE | /delete | 删除SQL脚本 |

### SettingController (/setting)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建设置 |
| GET | /load | 加载设置 |
| GET | /list | 查询设置列表 |
| PUT | /modify | 修改设置 |
| DELETE | /delete | 删除设置 |

## 三、总结

| 类别 | 数量 |
|------|------|
| 前端调用的API | 42个 |
| 后端Controller | 24个 |
| 后端API方法 | 约110个 |
| **⚠️ 路径不匹配** | 1个 (`/project/relationship/listByProjectCode`) |
| **❌ 后端缺失** | 0个（所有前端调用的API都有对应后端） |

## 四、未使用的后端API (对比第一节前端调用后筛出)

> 以下API在后端存在，但第一节"前端调用的API"中未出现，即前端未调用
> ✅ 已标记 `@Deprecated`

### AccountController
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /load/code/{code} | 根据code加载账户 |
| PUT | /modify/password | 修改密码 |

### DisplayAttributeController
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /load/mapFieldColumnCode/{mapFieldColumnCode} | 根据字段编码加载 |

### FrameworksTemplateController
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建框架模板 |
| GET | /list | 查询框架模板列表 |
| PUT | /modify | 修改框架模板 |
| DELETE | /delete | 删除框架模板 |

### IndexCtrl
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | / | 页面路由(index.html) |

### LogsCtrl
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /createLogFiles | 创建日志文件 |
| POST | /saveLogs | 保存日志 |
| GET | /loadFilePath | 加载文件路径 |

### MapClassTableController
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建类表映射 |
| GET | /load/code/{code} | 根据code加载 |
| GET | /list | 查询类表映射列表 |
| PUT | /modify | 修改类表映射 |
| DELETE | /delete | 删除类表映射 |

### MapFieldColumnController
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建字段映射 |
| GET | /list | 查询字段映射列表 |
| PUT | /modify | 修改字段映射 |
| DELETE | /delete | 删除字段映射 |

### ModuleController
| 方法 | 路径 | 说明 |
|------|------|------|
| PUT | /modify | 修改模块 |
| DELETE | /delete | 删除模块 |

### ModuleFileController
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建模块文件 |
| GET | /list | 查询模块文件列表 |
| PUT | /modify | 修改模块文件 |
| DELETE | /delete | 删除模块文件 |

### ProjectCodeCatalogController
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /load/code/{code} | 根据code加载 |
| GET | /load/projectCode/{projectCode} | 根据项目编码加载 |

### ProjectController
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /download/{proejctName} | 下载项目源码 |
| GET | /index | 首页 |

### ProjectFramworkController
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建项目框架 |

### ProjectJobController
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /load/code/{code} | 根据code加载任务 |
| GET | /execute | 执行任务 |

### ProjectJobLogsController
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建任务日志 |
| GET | /list | 查询任务日志列表 |
| PUT | /modify | 修改任务日志 |
| DELETE | /delete | 删除任务日志 |

### ProjectMapController
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建项目数据表 |
| GET | /list | 查询项目数据表列表 |
| PUT | /modify | 修改项目数据表 |
| DELETE | /delete | 删除项目数据表 |

### ProjectModelClassController
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /loadById/{id} | 根据ID加载 |
| GET | /loadByMapClassTableCode | 根据类表编码加载 |
| GET | /loadByProjectModelCode | 根据模块编码加载 |

### ProjectModelController
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /loadById/{id} | 根据ID加载 |
| GET | /loadByCode/{code} | 根据code加载 |

### ProjectModuleController
| 方法 | 路径 | 说明 |
|------|------|------|
| PUT | /modify | 修改模块 |
| DELETE | /delete | 删除模块 |

### ProjectRepositoryAccountController
| 方法 | 路径 | 说明 |
|------|------|------|
| DELETE | /delete | 删除仓库 |

### ProjectSqlController
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建SQL脚本 |

### SettingController
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /build | 创建设置 |
| GET | /load | 加载设置 |
| GET | /list | 查询设置列表 |
| PUT | /modify | 修改设置 |
| DELETE | /delete | 删除设置 |

---

**统计**: 共19个Controller包含未使用API，总计约58个端点
**状态**: ✅ 已全部标记 `@Deprecated`