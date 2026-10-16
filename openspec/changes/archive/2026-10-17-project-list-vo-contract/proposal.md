# Proposal

## Why

`GET /project/list` 当前返 `R<Page>`，而 `Page.voList` 字段类型在契约里是 `unknown[]` —— 等价于把 `List<Object>` 暴露给契约消费者。其结果是：

- 即使前端用契约层样板接入这个端点，`data.voList[0]` 也是 `unknown`，无法保证字段形状；
- React + TypeScript 的"端点字段改了会编译失败"防漂移机制在这条端点上失灵 —— 当前端读取 `voList[i].name` / `.code` 等字段时不会有任何静态检查；
- 这与 `api-data-object` 当前 Scenario「列表接口出参为 VO」要求不符：该 Scenario 是无条件成立的，但分页场景需要更具体的形态（`PageVO<XxxVO>`），否则守不住。

本变更只补齐这一个端点的 VO 化，不动其他列表接口。客户端页面接入（登录页 + 项目列表页）放在紧随的 `2026-10-17-login-and-project-list-pages` 变更里，那一步将依赖本变更产生的契约形状。

## What Changes

- **新增 `ProjectVO`**：`facade/src/main/java/com/aicode/project/vo/ProjectVO.java`，从 `Project` 实体裁剪出"项目列表 + 项目详情"最小消费集 —— 至少含 `code` / `name` / `englishName` / `description` / `accountCode` / `createTime`；不含派生字段、不含嵌套关联、不含内部实现细节。
- **新增 `PageVO<T>` 通用化基线**：复用仓库内 `R<T>` 的同种泛型化思路，把当前 `R.success.data` 默认绑死成 `Object` 的问题一并解决 —— 但本变更**只动** `PageVO` 与 `ProjectVO`，`PageVO` 将在契约里生成 `array of ProjectVO`。
- **修改 `ProjectController.list` 出参类型**：`R<Page>` → `R<PageVO<ProjectVO>>`，手工装配每一行的 VO（参考 `display-attribute-vo-contract` 中 `DisplayAttributeController` 的实现方式）。
- **同步契约**：`OpenApiExportIT` 在测试期自动刷新 `contracts/openapi/openapi.yaml`，让前端可生成 `RPageProjectVOList` 与 `pageVOProjectVOList` 之类的类型名（具体以 `PageVO<ProjectVO>` 在 springdoc 下的展开为准）。
- **契约守护测试**新增断言：`/project/list` 响应的 `data.voList` 元素 SHALL 包含 `ProjectVO` 字段集（含 `code` / `name` / `englishName` / `createTime` 等）；修改后的契约 SHALL NOT 仍含 `voList: unknown`。
- **`api-data-object` 在 Capability 中增补一个 Requirement**："分页列表接口出参 SHALL 为 `PageVO<XxxVO>`，SHALL NOT 为 `Page<Object>` 类型裸出参"。

**BREAKING**：`/project/list` 响应中 `data` 的形态从 `Page{voList: Object[]}` 变为 `PageVO<ProjectVO>`，调用方必须按新字段读取。仓库内查询本端点的代码：`aicode-web/src/generated/**`（自动同步）、`ApiContractIT`（同步断言）、Angular 旧产物（M4 时一并处理）。当前未发现业务层代码调用此端点。

## Capabilities

### New Capabilities

（无。本变更走实现而非新增能力面。）

### Modified Capabilities

- `api-data-object`：在「Controller SHALL 返回 VO 而非持久化实体」这一 Requirement 下新增一个 Scenario——「分页列表接口出参为 PageVO<XxxVO>」。该 Scenario 把当前"列表接口出参为 VO"的覆盖收紧到分页形态，避免再出现 `voList: unknown` 的契约漏出。

## Impact

- **代码**：
  - `facade/src/main/java/com/aicode/project/vo/ProjectVO.java`（新增）
  - `facade/src/main/java/com/aicode/core/PageVO.java`（新增，作为分页 VO 的泛型容器）
  - `aicode/src/main/java/com/aicode/project/ctrl/ProjectController.java`（修改 `list` 方法签名与装配）
  - `aicode/src/test/java/com/aicode/contract/ApiContractIT.java`（新增断言）
  - `aicode/src/test/java/com/aicode/project/service/ProjectServiceTest.java` 之类（如果存在，可能要补 mock 校验）
- **契约**：`contracts/openapi/openapi.yaml` —— `/project/list` 响应的 `data` schema 从 `Page` 内嵌 `voList: {}`（其内是 anything）变为 `PageVOProjectVO` 或类似 springdoc 展开形态，含真实字段。
- **前端生成物**：`aicode-web/src/generated/model/` 内出现新的 `ProjectVO` / `PageVOProjectVO` 类型；`api.ts` 内 `list2` 的返回类型从 `RPage` 变为具体类型。
- **不影响**：所有其他列表接口（`/framework/list`, `/project/mouldles/list` 等）的当前形态；这些接口如果相同问题需要后续各自单独变更，本变更不预收。
- **风险**：低。`/project/list` 当前生产端调用方 = Angular 静态产物（按字段名 `records` 之类读取，与 VO 字段名无重叠）；无仓库内业务代码消费。
