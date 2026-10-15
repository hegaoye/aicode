# Tasks

## 1. 测试基础设施

- [ ] 1.1 新增 `aicode/src/test/resources/application-test.yml`，数据源指向 `jdbc:h2:mem:aicode_test;MODE=MYSQL;DATABASE_TO_UPPER=false`，SHALL NOT 写入 `/tmp/aicode` 默认库
- [ ] 1.2 确认 `spring.sql.init` 在 test profile 下仍加载 `db/schema.sql` + `db/data.sql`（契约测试需要真实表结构与样例数据）
- [ ] 1.3 确认 test profile 下 `management.server.port` 与 H2 控制台关闭，避免测试间端口/H2 冲突

## 2. 固化前端端点清单

- [ ] 2.1 新增 `aicode/src/test/java/com/aicode/contract/FrontendEndpointCatalog.java`，登记从 `static/main.*.js` 的 `URL` 配置对象提取的全部端点
- [ ] 2.2 在类注释中标明来源模块（bundle 内 `c2t9` 模块）与提取日期，SHALL NOT 写成「从后端枚举」
- [ ] 2.3 对后端无对应 Controller 的端点（当前已确认 `/upload/basic/uid`）以常量 + `@Disabled` + 原因注释显式登记，SHALL NOT 静默省略

## 3. 实现契约断言

- [ ] 3.1 新增 `aicode/src/test/java/com/aicode/contract/ApiContractIT.java`，采用 `@SpringBootTest(webEnvironment = MOCK)` + `@ActiveProfiles("test")` + `webAppContextSetup`
- [ ] 3.2 使用 `@MockitoBean`（SHALL NOT 使用 `@MockBean`）打桩业务层 Service，加载真实 `WebMvcConfigurer`
- [ ] 3.3 实现 `jsonStringEquals(path, expectedLong)` 自定义 matcher：读 `contentAsString()` 断言 JSON 类型为字符串且值与数据库原值逐字符相同
- [ ] 3.4 场景①ID 字符串：`$.data.records[*].id` 全部为字符串 + 与 DB 原值比对 + `code` 匹配 `^\d{1,19}$`
- [ ] 3.5 场景②后缀双路径：对 `FrontendEndpointCatalog` 中每个端点，`.shtml` 与无后缀两次请求的状态码与响应体一致
- [ ] 3.6 场景③R 结构：成功路径断言 `code == "0000"`、`success == true`、含 `data` 键；失败路径断言 `code ∈ {9003,9004,9007,9999}`
- [ ] 3.7 场景④日期格式与时区：断言 `^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$`，并用一条固定时刻的记录断言 GMT+8 偏移正确
- [ ] 3.8 场景⑤null 保留：断言响应原始 JSON 含 `"<字段>": null`，且顶层含 `"data": null` 时该键不被省略
- [ ] 3.9 场景⑥WebSocket 端点：验证 `/websocket.shtml` 可达（若 MOCK 环境无法建立真实 WS 连接，则改为断言该路径未被静态资源处理器吞掉，并在注释说明）

## 4. 文档与既有测试

- [ ] 4.1 修正 `LoginCtrlShtmlPathTest` 类注释中关于 `excludePathPatterns` 的过时描述，补充指向 `api-contract` 能力的引用
- [ ] 4.2 在 `docs/standards/testing.md` §7.10 优先补齐清单中，把 `ApiContractIT` 标注为「升级验收基准，SHALL NOT 跳过」

## 5. 验收

- [ ] 5.1 执行 `./aicode/gradlew :aicode:test --tests "com.aicode.contract.*"`，全部场景 SHALL 通过
- [ ] 5.2 执行 `./aicode/gradlew test`，确认 18 个既有测试类仍全绿，总耗时未显著劣化
      > 实测依据：`find aicode/src/test common/src/test facade/src/test -name '*.java' | wc -l` → 18（`aicode` 10 / `common` 8 / `facade` 0）
- [ ] 5.3 确认测试过程 SHALL NOT 写入 `/tmp/aicode`（测试后该文件 SHALL NOT 出现新的 mtime）
- [ ] 5.4 确认 `openspec validate 2026-10-14-add-api-contract-tests --strict` 通过
