# Spec Delta

## ADDED Requirements

### Requirement: 分页接口的 PageVO SHALL 回填实际查询参数而非默认值

接口声明 `curPage` / `pageSize` 入参时，响应的 `data` 节点 SHALL 回填**实际生效**的 `curPage` / `pageSize`，并 SHALL 给出与 `totalRow` 相符的 `totalPage`（`ceil(totalRow / pageSize)`）。SHALL NOT 把这三个字段留为 `PageVO` 的 int 默认值 0 —— 那会让前端算出的翻页边界全部失效。

该 Scenario 补齐既有 Requirement「分页接口 SHALL 统一用 PageVO<T> 而非 Page<T>」的最后一块：容器形态已统一，但容器内的**值**此前无人守护（`/project/list` 曾忽略入参拉全量且从不 set 这三个字段）。

#### Scenario: 响应回填与实际查询一致的 curPage / pageSize / totalPage

- **WHEN** 客户端请求分页接口并携带 `curPage = 3` / `pageSize = 20`
- **THEN** 响应的 `data.curPage` SHALL 等于 3，`data.pageSize` SHALL 等于 20
- **AND** `data.totalPage` SHALL 等于 `ceil(data.totalRow / 20)`，SHALL NOT 为 0（当 `totalRow > 0` 时）

#### Scenario: 入参缺省或非法时 SHALL 回退到默认值而非报错

- **WHEN** 客户端未携带 `curPage` / `pageSize`，或携带 0 / 负数
- **THEN** 服务端 SHALL 回退到默认页（`curPage = 1`）与默认页大小（`pageSize = 10`），并把回退后的值回填进响应
- **AND** SHALL NOT 抛异常，也 SHALL NOT 拉取全表

#### Scenario: 数据量超过一页时 SQL SHALL 只取该页

- **WHEN** 某账户名下有 25 个项目，客户端请求 `curPage = 2` / `pageSize = 10`
- **THEN** 服务端 SQL SHALL 只返回 10 条（该页区间），SHALL NOT 返回全部 25 条
- **AND** `data.totalRow` SHALL 等于 25，`data.totalPage` SHALL 等于 3
