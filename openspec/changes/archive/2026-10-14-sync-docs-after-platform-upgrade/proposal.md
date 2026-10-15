# Proposal

## Why

Java 25 / Spring Boot 4.1 升级系列（前 5 个变更）会在代码与构建配置中留下大量与既有文档冲突的事实。`AGENTS.md` 是后续所有协作会话的**唯一入口文档**，其中的过时描述会直接误导后续会话，代价是按错误的基线做决策。

已识别的过时项（在本系列开始前就已存在，本变更是集中清理）：

| 文档位置 | 过时内容 | 实际情况 |
|---------|---------|---------|
| `AGENTS.md` §2 | `aicode` 是「唯一可运行且有测试的模块」 | 表述模糊；`common` 也有 8 个测试类 |
| `AGENTS.md` §3 | 构建命令段落无 toolchain 说明 | 升级后需要本地 JDK 25 |
| `AGENTS.md` §4 | 「存在两个 DataSource Bean，**不要**顺手去重」 | uid 数据源将随百度 UID jar 一并删除 |
| `AGENTS.md` §4 | 「H2 版本**锁死 1.4.200**（新版会破坏 Spring Boot 3 的 H2 console 路由）」 | 该约束的适用对象是 Boot 3，Boot 4 下已失效 |
| `AGENTS.md` §4 | 「admin / 888888」「JWT 默认 60 分钟过期」 | 属实，但需补「已记录在 `openspec/specs/auth-account`」 |
| `AGENTS.md` §6.6 | 统一响应示例 | 需与 `api-contract` 能力对齐 |
| `AGENTS.md` §7.1 | 「当前仓库三模块均无 `src/test` 实现」 | 实测 `aicode` 10 个 + `common` 8 个 = **18 个测试类**（`facade` 0 个） |
| `AGENTS.md` §9 | Dockerfile 基础镜像 `amazoncorretto:21.0.11-alpine3.23` | 将变为 25 |
| `AGENTS.md` §10 | 「commons-lang3 在 build.gradle 中被声明了两次，**不要**顺手清理」 | 去重后已不适用 |
| `AGENTS.md` §11.3 | 「当前只有 `SmokeTest` 占位」 | 同 §7.1，实为 18 个测试类 |
| `AGENTS.md` §11.4 | 「当前**没有 CI**」 | 若本系列顺带引入 CI 则需更新 |
| `AGENTS.md` §11.10 | 「Java 21 LTS → 关注 25 LTS 升级窗口」「H2 1.4.200 锁死 → 需找 Spring Boot 3 兼容窗口再升」 | 两项均已完成 |
| `docs/standards/cds.md` | 「Metaspace 下降 78%、wall-clock 加速约 17%」 | Java 21 的测量值，Java 25 下已不成立 |
| `docs/standards/architecture.md` §5.6 | 「双数据源」决策行 | 将随 uid 数据源删除而失效 |
| `README.md` §四 | 废弃端点清单 | 需与 `api-deprecation-policy` 能力对齐 |

## What Changes

- 更新 `AGENTS.md` 中所有因升级而失效的版本号、架构决策、命令与坑位提示
- 重写 `docs/standards/cds.md` 的性能数据（数据来源为 `2026-10-14-upgrade-gradle-9-jdk-25` 实测输出）
- 更新 `docs/standards/architecture.md` §5.6 关键架构决策表，移除「双数据源」，新增「工具链统一语言级别」
- 更新 `docs/standards/code-style.md`：starter 命名约定、Jackson 3 转换规则注册方式、Lombok 版本约束
- 更新 `docs/standards/testing.md` §7.10：把契约测试标注为升级验收基准
- 更新 `README.md` §四 与 API 文档访问方式（Knife4j 已移除）

本变更**不修改任何代码、依赖或运行时行为**，只改文档。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

无。

> 本变更的 `.openspec.yaml` 设置了 `skip_specs: true`。文档同步不产生任何可观测的行为契约变化。

## Impact

- **修改文件**：
  - `AGENTS.md` §2、§3、§4、§6、§7.1、§9、§10、§11.3、§11.4、§11.10
  - `docs/standards/architecture.md`（§5.6 决策表、§5.7 部署、双数据源相关叙述）
  - `docs/standards/code-style.md`（starter 命名、Jackson、依赖版本声明规范）
  - `docs/standards/testing.md`（§7.10 优先补齐清单）
  - `docs/standards/cds.md`（全部性能数字）
  - `README.md` §四、本地部署章节
- **前置依赖**：SHALL 在前 5 个升级变更全部归档后执行，SHALL NOT 提前——否则会记录下随后又被改掉的中间状态
- **验收方式**：文档中的每个版本号 SHALL 能在本仓库的构建文件或制品中检索到对应值
