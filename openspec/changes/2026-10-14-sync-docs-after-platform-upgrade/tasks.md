# Tasks

## 0. 前置检查

- [ ] 0.1 确认前 5 个升级变更（`add-api-contract-tests`、`upgrade-spring-boot-3-5`、`replace-baidu-uid-generator`、`upgrade-gradle-9-jdk-25`、`upgrade-spring-boot-4-1`）均已归档到 `openspec/changes/archive/`
- [ ] 0.2 确认 `docs/standards/` 下的四份基线文档（`architecture.md`、`code-style.md`、`testing.md`、`cds.md`）是否存在
- [ ] 0.3 若 0.2 结果为「不存在」，在变更说明中记录该缺失，并把「补建四份基线文档」列为独立后续事项（本变更 SHALL NOT 凭空重写其内容）
- [ ] 0.4 记录当前各组件的实际版本，作为文档更新的事实来源：`java -version`、`./aicode/gradlew --version`、Boot 插件版本、`grep` 各依赖声明

## 1. AGENTS.md — 架构与运行时事实

- [ ] 1.1 §2 模块结构表：核对「唯一可运行且有测试的模块」的表述，改为准确描述（`aicode` 可运行且有测试；`common` 有测试；`facade` 无测试）
- [ ] 1.2 §3 构建/开发命令：补充 toolchain 说明与「需要本地安装 JDK 25」的前置条件；更新 Gradle wrapper 版本
- [ ] 1.3 §4 运行时事实：**删除**「存在两个 DataSource Bean，不要顺手去重」整段，改为单数据源说明
- [ ] 1.4 §4 运行时事实：更新 H2 版本（1.4.200 → 2.4.240），**删除**「H2 版本锁死 1.4.200」这条红线及其理由（该约束针对 Boot 3 的 console 路由，Boot 4 下已由独立 h2console 模块解决）
- [ ] 1.5 §4 运行时事实：更新 JDBC URL 说明（删除 `DB_CLOSE_ON_EXIT=FALSE`，说明与 `AUTO_SERVER` 互斥）；提示升级后需删除存量库文件
- [ ] 1.6 §4 运行时事实：更新容器基础镜像与云构建包的 JDK 版本
- [ ] 1.7 §4 运行时事实：在「默认账户 / JWT」条目后补一句「该行为的正式契约见 `openspec/specs/auth-account/spec.md`」

## 2. AGENTS.md — 架构与编码规范

- [ ] 2.1 §5.2 模块依赖：核对 `common` 不得引入 web 依赖的红线是否仍成立（升级后需确认 `spring-boot-*` 未渗入 common）
- [ ] 2.2 §5.6 关键架构决策表：**删除**「双数据源」行；**新增**「统一工具链语言级别」行，说明三个模块的 Java 版本由 toolchain 统一声明
- [ ] 2.3 §5.7 部署架构：更新 CDS 相关表述指向重测后的 `docs/standards/cds.md`
- [ ] 2.4 §6.6 接口与统一响应：核对错误码表与 `api-contract` 能力一致；补充「`code`/`info`/`data`/`success` 四键结构由 `api-contract` 守护」
- [ ] 2.5 §6.8 持久层：更新「主键/分布式 ID 用 `UidGenerator.getUID()`」的表述，改为「主键由数据库自增，业务编码由内存雪花生成」（详见 `identifier-generation` 能力）
- [ ] 2.6 §6.12 模板生成规范：确认模板侧规范未受本系列影响（本系列不改变生成产物的 Boot 版本）
- [ ] 2.7 §6.13 安全红线：核对 H2 控制台在生产关闭的要求仍然成立

## 3. AGENTS.md — 测试规范

- [ ] 3.1 §7.1 现状：改写「三模块均无 `src/test` 实现」为实测统计（`aicode` 10 个 + `common` 8 个 = 18 个测试类，`facade` 0 个）
- [ ] 3.2 §7.2 技术栈：更新 JUnit 版本描述（由 BOM 管理）、Mockito / AssertJ 版本
- [ ] 3.3 §7.5 单元测试规范：把 `@MockBean` 的用法描述更新为 `@MockitoBean`，并注明「不允许用于 `@Configuration` 类」
- [ ] 3.4 §7.10 优先补齐清单：新增「契约测试（`ApiContractIT`）」条目，并标注为**升级验收基准，SHALL NOT 跳过**

## 4. AGENTS.md — 坑位与演进

- [ ] 4.1 §10 容易踩坑：更新第 3 条（Gradle 配置在子模块，根 build.gradle 为空）——确认升级后仍然成立
- [ ] 4.2 §10 容易踩坑：**删除**「commons-lang3 在 build.gradle 中被声明了两次，不要顺手清理」整条（已去重）
- [ ] 4.3 §10 容易踩坑：新增一条「Hutool 雪花默认从 MAC/PID 派生 workerId，多副本部署前需显式指定」
- [ ] 4.4 §10 容易踩坑：新增一条「`.shtml` 后缀兼容由 `ShtmlSuffixRewriteFilter` 提供，新增 API 前缀时需同步维护其白名单」
- [ ] 4.5 §11.3 测试演进：改写「当前只有 SmokeTest 占位」为已落地的测试清单
- [ ] 4.6 §11.10 升级路径：把「Java 21 LTS → 关注 25 LTS 升级窗口」改为已完成并记录实测结论；把「H2 1.4.200 锁死 → 需找兼容窗口再升」改为已完成并记录 URL 参数互斥问题
- [ ] 4.7 §8 OpenSpec 工作流：补充「能力基线位于 `openspec/specs/`，新增变更前先执行 `openspec list --specs` 查重」
- [ ] 4.8 §11.1 / §11.2 / §11.3 / §11.5 等演进章节：确认其内容为路线图而非现状陈述，SHALL NOT 改动

## 5. docs/standards/

- [ ] 5.1 `cds.md`：**删除**「Metaspace 下降 78%、wall-clock 加速约 17%」，写入 `2026-10-14-upgrade-gradle-9-jdk-25` 实测的新值
- [ ] 5.2 `cds.md`：补充测量方法（采样次数、硬件环境、JVM 参数），使数字可复现
- [ ] 5.3 `architecture.md` §5.6：同步决策表的删除与新增（与 2.2 一致）
- [ ] 5.4 `architecture.md`：更新版本号（Spring Boot、Spring Framework、Java、Jakarta EE 基线）
- [ ] 5.5 `architecture.md`「后续演进」：记录 Hutool 雪花的多副本部署约束
- [ ] 5.6 `code-style.md`：新增「starter 命名以 `spring-boot-starter-<technology>` 为准，新增依赖 SHALL 交由 BOM 管理版本」条目
- [ ] 5.7 `code-style.md`：新增「JSON 转换规则 SHALL 通过当前默认序列化库的构建器扩展点注册，SHALL NOT 构造旁路映射实例」条目
- [ ] 5.8 `testing.md` §7.10：同步 3.4 的清单更新

## 6. README.md

- [ ] 6.1 §四 废弃端点清单：与 `openspec/specs/api-deprecation-policy` 对齐，补入「前端静态产物调用计入核查范围」的要求
- [ ] 6.2 本地部署章节：更新 API 文档访问地址（`doc.html` → `/swagger-ui.html`），并说明 Knife4j 已移除
- [ ] 6.3 本地部署章节：更新 JDK 版本要求与 H2 存量库的清理提示

## 7. 验收

- [ ] 7.1 逐条核对：`AGENTS.md` 与 `docs/standards/` 中出现的每个版本号，都能在本仓库的构建文件或制品中检索到对应值
- [ ] 7.2 确认 `AGENTS.md` 中不再出现「两个 DataSource」「H2 版本锁死」「commons-lang3 被声明了两次」「三模块均无 src/test」「只有 SmokeTest 占位」等已失效表述
- [ ] 7.3 确认 §11 路线图类条目未被误删
- [ ] 7.4 确认 `openspec validate 2026-10-14-sync-docs-after-platform-upgrade --strict` 通过
