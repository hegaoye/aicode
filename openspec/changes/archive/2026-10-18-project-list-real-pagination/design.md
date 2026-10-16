# Design

## Context

M5-A2 框架页的翻页实测暴露出 `/project/list` 是**假分页**：

```java
// ProjectController.java:274（现状）
public R<PageVO<ProjectSummaryVO>> list(String token, Integer curPage, Integer pageSize) {
    PageVO<ProjectSummaryVO> pageVO = new PageVO<>();
    if (StringUtils.isBlank(token) || "null".equals(token)) {
        return R.success(pageVO);
    }
    String accountCode = JwtToken.getTokenValue(token, Constants.AccountCode.val.toString());
    List<Project> projectList = projectService.lambdaQuery()
            .eq(Project::getAccountCode, accountCode)
            .orderByDesc(Project::getCreateTime)
            .list();                                    // ← 拉全量，入参被忽略
    List<ProjectSummaryVO> records = new ArrayList<>(projectList.size());
    for (Project project : projectList) {
        records.add(toSummary(project));
    }
    pageVO.setTotalRow(projectList.size());
    pageVO.setRecords(records);                          // ← curPage / pageSize / totalPage 从不 set
    return R.success(pageVO);
}
```

对比同仓库 `/framework/list`（M5-A1 `e6184d3` 已改为真分页）：

```java
IPage<Frameworks> page = new Page<>(curPage, pageSize);
IPage<Frameworks> frameworksPage = frameworksService.page(page, queryWrapper);
pageVO.setCurPage(...); pageVO.setPageSize(...); pageVO.setTotalPage(...);
```

## Goals / Non-Goals

**Goals:**

- `ProjectController.list` 用 MyBatis-Plus `page(page, queryWrapper)` 做真分页；
- 显式 set `curPage` / `pageSize` / `totalRow` / `totalPage`；
- 入参兜底（null 或 ≤ 0 → `curPage = 1` / `pageSize = 10`）；
- `ApiContractIT` 场景⑦ 补分页字段断言（防再次退化）。

**Non-Goals:**

- 不改前端 `ProjectListPage`（紧随的独立变更做）。
- 不改 `ApiContractIT` 的既有断言（`records` 字段集合等），只补分页字段。
- 不动契约 schema（`PageVO<ProjectSummaryVO>` 已含三字段）。
- 不动 `ProjectController` 的其他方法。

## Decisions

### D1：真分页用 MyBatis-Plus `page(page, queryWrapper)`，与 `/framework/list` 同形态

**决定**：

```java
int effectiveCurPage = (curPage == null || curPage <= 0) ? 1 : curPage;
int effectivePageSize = (pageSize == null || pageSize <= 0) ? 10 : effectivePageSize...;
IPage<Project> page = new Page<>(effectiveCurPage, effectivePageSize);
long total = projectService.count(queryWrapper);
pageVO.setCurPage(effectiveCurPage);
pageVO.setPageSize(effectivePageSize);
pageVO.setTotalRow(total);
if (total > 0) {
    queryWrapper.lambda().orderByDesc(Project::getCreateTime);
    IPage<Project> p = projectService.page(page, queryWrapper);
    List<ProjectSummaryVO> records = new ArrayList<>(p.getRecords().size());
    for (Project project : p.getRecords()) {
        records.add(toSummary(project));
    }
    pageVO.setRecords(records);
    pageVO.setTotalPage((int) Math.ceil((double) total / effectivePageSize));
}
```

**理由**：
- `ProjectService extends IService<Project>`（`IService#page` 自带），与 `/framework/list` 完全同形态 —— 一致性；
- `count()` 与 `page()` 分两次查询（与 `/framework/list` 同），接受这个轻微开销：项目表小、且能拿到精确 `totalRow`；
- `totalPage` 用 `ceil(total / pageSize)` 显式算，不依赖 `IPage#getPages()`（后者在 `total` 为 0 时行为随版本变）。

**备选方案**：(a) 用 `IPage#getPages()` —— 需验证版本行为（AGENTS §10 有 MyBatis-Plus 3.5.7 → 3.5.17 的迁移记录，行为未测）；(b) 显式 `ceil` —— 可预测，本变更采用。

### D2：`pageSize` 默认 10（与前端 `ProjectListPage` 的 `PAGE_SIZE` 对齐）

**决定**：默认 `pageSize = 10`。

**理由**：
- 前端 `ProjectListPage` 的 `PAGE_SIZE = 10`，两者对齐使"后端兜底值 = 前端默认值"在客户端改 `pageSize` 前保持一致；
- `com.aicode.core.Page.limit` 也是 25，但框架页（M5-A1）用的是 25 的字面值而前端是 20 —— 已有的不一致点，本变更不再扩大。

### D3：`count()` 与 `page()` 共用同一个 `queryWrapper`，但排序只在 `page()` 前加

**决定**：`count()` 在排序前调用；`page()` 在 `orderByDesc` 后调用。

**理由**：
- `count()` 不需要排序（ORDER BY 对 COUNT(*) 无意义，且会浪费索引）；
- 与 `/framework/list` 完全同顺序。

### D4：契约测试只补断言，不改既有断言

**决定**：在 `ApiContractIT` 场景⑦ 新增 1 个 `@Test`（`project_list_reflects_paginationParams`），断言 `data.curPage` / `data.pageSize` / `data.totalPage` 反映入参；既有 2 个用例（`records` 字段集合 / 关键字段）不动。

**理由**：既有断言守的是"元素字段集合"，新断言守的是"容器字段值"，两者职责不同，合并会让失败信息不聚焦。

## Risks / Trade-offs

**[当前 1 条种子下行为等价]** → `totalPage` 从 0 变 1、`curPage` 从 0 变 1、`pageSize` 从 0 变 10。前端 `ProjectListPage` 读 `totalRow` 与 `records`（语义不变），额外读的 `curPage`（作为标题 `共 N 条` 旁边无展示）不受影响 —— 现有 5 用例应全绿。若有用例红，说明它读了那三个字段中某个。

**[`page()` 的 `IPage` 与 `PageVO` 字段名不同]** → `IPage` 用 `records` / `total` / `current` / `size` / `pages`；`PageVO` 用 `records` / `totalRow` / `curPage` / `pageSize` / `totalPage`。需手工映射（不引 `BeanUtils`）。已在 D1 的代码形态里显式写出。

**[排序稳定性]** → `orderByDesc(Project::getCreateTime)` 在 `createTime` 相同的行之间无稳定序，翻页时可能出现行重复/遗漏。真实修法是加 `id` 作为 tiebreaker（`orderByDesc(createTime).orderByDesc(id)`）。**本变更顺带加上** —— 否则翻页功能上线即有缺陷。记录为 D3 的扩展。

## Migration Plan

单批交付（一个 feature commit）：

1. 修改 `ProjectController.list` 真分页（D1 代码形态 + 入参兜底 + tiebreaker 排序）；
2. `ApiContractIT` 场景⑦ 新增 1 个用例 `project_list_reflects_paginationParams`；
3. 跑 `./aicode/gradlew :aicode:test`（含 `OpenApiExportIT` 重导契约，确认 schema diff 为空）；
4. 跑前端 `npm test`（确认 85 用例仍绿）；
5. 同步 delta spec 到 `api-contract` 并 archive。

**回滚**：单 commit revert。

## Open Questions

- **`ProjectService` 是否已有 count 缓存 / 自定义分页拦截器？** —— task 1.2 实施时确认（`MybatisPlusConfig` 已注册 `PaginationInnerInterceptor`，本变更走标准 `page()`，无需额外配置）。
- **前端 `ProjectListPage` 的翻页**（`curPage` 进 `useState` + 翻页控件）—— 紧随的独立变更 `2026-10-18-project-list-pagination`（本变更的前置）。
- **`/account/list` 等其他端点是否也是假分页？** —— `AccountController.list` 用了 `accountService.page(page, queryWrapper)` + `setTotalRow`，但**同样没 set `curPage` / `pageSize` / `totalPage`**。属同类缺陷，本变更只修 `/project/list`；`/account/list` 的同类修法见后续决策。
