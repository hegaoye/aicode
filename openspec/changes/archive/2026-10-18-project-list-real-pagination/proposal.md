# Proposal

## Why

给 `ProjectListPage` 加翻页时发现：`ProjectController.list` 是**假分页**。

实测 `aicode/src/main/java/com/aicode/project/ctrl/ProjectController.java:274-297`：

```java
public R<PageVO<ProjectSummaryVO>> list(String token, Integer curPage, Integer pageSize) {
    ...
    List<Project> projectList = projectService.lambdaQuery()
            .eq(Project::getAccountCode, accountCode)
            .orderByDesc(Project::getCreateTime)
            .list();                      // ← 拉全量，curPage / pageSize 形同虚设
    ...
    pageVO.setTotalRow(projectList.size());
    pageVO.setRecords(records);
    // ← 未 setCurPage / setPageSize / setTotalPage，三者恒为默认值 0
}
```

对比同仓库 `/framework/list`（M5-A1 刚改过）：

```java
IPage<Frameworks> page = new Page<>(curPage, pageSize);
IPage<Frameworks> frameworksPage = frameworksService.page(page, queryWrapper);  // ← 真分页
pageVO.setCurPage(...); pageVO.setPageSize(...); pageVO.setTotalPage(...);
```

三个具体缺陷：

1. **`curPage` / `pageSize` 入参被完全忽略** —— 无论前端传什么，SQL 都拉全表；
2. **`curPage` / `pageSize` / `totalPage` 从不 set** —— 响应里这三个字段恒为 `0`（`PageVO` 的 int 字段默认值），前端算出的翻页边界全是错的；
3. **全量拉取无上限** —— 项目表随使用增长后，单次请求把该账户全部项目 `JSON.parseArray` 到内存（`toSummary` 逐条装配），数据量大时是无界响应体。

当前 `db/data.sql` 只有 1 条项目种子（M2-1 为 `ApiContractIT` 加的 `contract-demo`），所以这个缺陷**尚未暴露**；但一旦用户创建超过 `PAGE_SIZE = 10` 个项目，`ProjectListPage` 会出现"显示 11 条但声称分页信息为 0"的错配，且**无论前端怎么改都翻不了页**。

本变更**只修后端**。前端 `ProjectListPage` 加翻页是紧随的独立变更。

## What Changes

- **`ProjectController.list` 改为真分页**：
  - 用 `projectService.page(new Page<>(curPage, pageSize), queryWrapper)` 取代 `lambdaQuery().list()`；
  - 显式 `setCurPage` / `setPageSize` / `setTotalRow` / `setTotalPage`（`totalPage = ceil(totalRow / pageSize)`）；
  - 入参兜底：`curPage` / `pageSize` 为 null 或 ≤ 0 时取默认值（`curPage = 1`、`pageSize = 10`）—— 沿 `/framework/list`（M5-A1）的同一处理；
  - 元素装配仍走 `toSummary(Project)`（不变）。
- **`ApiContractIT` 场景⑦ 补断言**：验证 `/project/list` 响应的 `curPage` / `pageSize` / `totalPage` 不再为 0。
- **契约产物不变**：`PageVO<ProjectSummaryVO>` 的 schema 形状本已包含这三个字段 —— 本变更只是让运行时真正填上它们，**不改变 schema**。

**BREAKING**：**是**，但对当前消费者零影响：
- `ProjectListPage`（M2-2）读的是 `records` 与 `totalRow`，两者语义不变（`totalRow` 仍是全量计数）；它额外读 `curPage` 做标题展示 —— 该值从"恒 0"变为"真实页码"，是修正而非破坏；
- 无其他后端调用方（grep 确认）。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

- `api-contract`：在 Requirement「分页接口 SHALL 统一用 PageVO<T> 而非 Page<T>」下新增 1 个 Scenario —— **分页字段 SHALL 反映实际查询参数**：接口声明了 `curPage` / `pageSize` 入参时，响应 SHALL 回填对应的 `curPage` / `pageSize` / `totalPage`，SHALL NOT 留默认值 0。该 Scenario 补齐了既有 Requirement 的最后一块：容器形态统一了，但**容器内的值**此前无人守护。

## Impact

- **代码**：
  - 修改 `aicode/src/main/java/com/aicode/project/ctrl/ProjectController.java`（`list` 方法体）；
  - 修改 `aicode/src/test/java/com/aicode/contract/ApiContractIT.java`（场景⑦ 补 3 条分页字段断言）。
- **契约产物**：**不变** —— schema 已含这三个字段，`OpenApiExportIT` 重导后 diff 为空。
- **前端**：**不变** —— `ProjectListPage` 现有 5 用例仍应全绿（`totalRow` / `records` 语义不变）。
- **风险**：低。当前 1 条种子下行为等价（`totalPage` 从 0 变 1，`curPage` 从 0 变 1）—— 前端 `FrameworkListPage` 式的 `Math.max(serverTotalPage, curPage)` 类兜底暂不需要，因为后端现在给的是真值。
