# aicode-web —— React 前端

前端类型、请求函数与 mock **全部由后端契约生成**，不手工维护。

## 契约链

```
后端 Controller  ──►  OpenApiExportIT（aicode 的测试）
                      │  调 /v3/api-docs.yaml
                      │  剥离 servers 块（随运行端口变化，入库会产生无意义 diff）
                      │  剥离无 schema 的装饰性参数条目（见下）
                      ▼
        contracts/openapi/openapi.yaml   ← 入库，可评审、可 diff
                      │
                      ▼
              orval（npm run generate）
                      ├─► src/generated/model/**        TypeScript 类型
                      ├─► src/generated/api.ts          请求函数
                      └─► src/generated/api.msw.ts      MSW mock handlers
```

**防漂移的价值**：后端改字段 → 契约变 → 前端类型变 → 用了旧字段的代码**编译失败**，
不必等到联调或运行时才发现。

### 契约漂移守卫

```bash
./aicode/gradlew :aicode-web:verifyApi
```

契约文件已入库，因此「当前内容」与「提交态」的 git 差异恰好等价于
「代码改了但契约没提交」。CI 应在提交后运行本任务。
⚠️ 代码尚未提交时运行会得到**假阴性**（git diff 为空）。

## 命令

| 命令 | 说明 |
|---|---|
| `npm install` | 安装依赖（首次；生成 `package-lock.json`） |
| `npm run dev` | 开发服务器（:5173），API 与 WebSocket 代理到 :8080 |
| `npm run build` | 生产构建 → `build/dist` |
| `npm run preview` | 预览构建产物 |
| `npm run generate` | 由契约重新生成类型 / 请求函数 / mock |
| `npm run typecheck` | 类型检查 |
| `npm test` | Vitest 组件测试 |

### Gradle 集成

`aicode-web` 是 Gradle 模块，其 Jar 内部布局为 `static/**`。
`aicode` 以 `implementation project(':aicode-web')` 引入后，
Spring Boot 会从 classpath 的 `/static/` 提供页面。

**node 是弱依赖**：找不到 node 但存在 `build/dist` 时复用旧产物；
两者都缺失时构建**失败并提示先执行 `./aicode/gradlew :aicode-web:buildUi`**
—— 避免产出「不含前端」的制品却报成功。

## ⚠️ 两条须知

### 1. orval 生成的 mock 只覆盖「契约形状」

自动生成的 MSW mock 由契约的 example schema 推导，适合兜底**结构**，
但**业务语义场景（边界值、时序、空态）几乎都需要手工 override**。
不要因为「已经生成了 mock」就以为不需要写测试数据。

### 2. 契约里被剥离的 119 个参数条目是错的，已登记待修

导出的 163 个参数中有 119 个缺少 `schema`（违反 OpenAPI 规范，orval 会拒绝生成）。
它们全部来自源码里 24 处 `@Parameters({@Parameter(name=..., description=...)})` 块，
且**语义错误** —— 例如：

```java
public R<Frameworks> updateIsPublic(@Parameter(hidden = true) Frameworks frameworks)
```

方法签名里根本没有 `code` / `isPublic` 参数（后端从请求体读），
注解却把它们声明成 query 参数。导出时剥离它们等于**移除错误信息**。

**待修**：这些 `@Parameters` 块应逐个复核 —— 删除与签名不符者、
为确有其事者补 `schema = @Schema(type = ...)`。
