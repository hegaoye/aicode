# Spec Delta

## ADDED Requirements

### Requirement: M4 切运行时 React 入口优先 Angular 静态产物

切运行时后端 jar 含 `aicode-web` 模块提供的 `static/index.html`（React 19 + TypeScript 入口），`/` 路径 SHALL 由 Spring Boot 从 classpath 加载此 HTML；Angular 7 静态产物 SHALL 被删除，**不得**优先于 React 入口。

#### Scenario: M4 后 `/` 命中 React 入口而非 Angular 老产物

- **WHEN** 启动 `./gradlew :aicode:bootRun`（M4 后 aicode 依赖 aicode-web）后 GET `http://localhost:8080/`
- **THEN** 响应 200 + Content-Type `text/html`，HTML body SHALL 含 `id="root"`（aicode-web React Vite 入口标记）且 SHALL NOT 含 Angular 老产物 hash（如 `10.460f310dc07dcf545995.js`）
- **AND** classpath 中 `aicode-web/build/dist/static/index.html` SHALL 在 `aicode/src/main/resources/static/index.html` 之前被 Spring Boot 加载（否则两个 index.html 共存会让前端变回 Angular）

#### Scenario: M4 后 `/api/*` 路径仍由 Spring MVC 处理（不与 React 入口冲突）

- **WHEN** GET `http://localhost:8080/api/list.shtml?curPage=1&pageSize=5`（`*.shtml` 路径由 `ShtmlSuffixRewriteFilter` 重写）
- **THEN** 响应 200 + Content-Type `application/json`（非 `text/html`），body 为 Spring MVC R 包络 JSON 而**不**是 React `index.html`
- **AND** React 入口不占用 `/api/*` 路径（React Router 路由表仅含业务页面路径如 `/login` / `/project/*` / `/account/*`）

#### Scenario: React Router 路由表覆盖所有业务页面

- **WHEN** 启动 M4 后前端 dev server `npm run dev`（Vite proxy 后端）或生产 jar 模式
- **THEN** 业务页面路径 `/login` / `/displayAttributes` / `/project/list` / `/project/:code` / `/account/me` SHALL 由 React Router 渲染（`<App />` 已 M2-2 注册）
- **AND** 老 Angular 路径（如 `/home` / `/` Angular 内部 hash）不再响应
