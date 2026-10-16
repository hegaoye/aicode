# Spec Delta

## ADDED Requirements

### Requirement: 列表页翻页 SHALL 重新拉取并更新表格，且边界 SHALL 禁用按钮

分页列表页面 SHALL 把当前页码（`curPage`）作为组件状态持有（而非常量），并把它列入数据拉取 effect 的依赖；用户点击「上一页 / 下一页」时 SHALL 更新该状态并触发重新拉取，表格内容 SHALL 被新页数据替换（SHALL NOT 保留上一页的可见行）。

翻页控件 SHALL 在边界禁用：首页时「上一页」SHALL 禁用，末页（`curPage >= totalPage`）时「下一页」SHALL 禁用；SHALL NOT 因点击边界按钮而发出越界页码请求。

切换页码期间页面 SHALL 进入加载态，SHALL NOT 让"上一页的表格数据"与"新页码的分页信息"同时呈现（避免瞬时错配误导用户）。

#### Scenario: 点击下一页 SHALL 重新拉取并替换表格内容

- **WHEN** 用户在第 1 页点击「下一页」，且 `totalPage > 1`
- **THEN** 组件 SHALL 以 `curPage = 2` 重新调用分页请求函数（请求参数中的页码 SHALL 为 2）
- **AND** 加载完成后表格 SHALL 展示第 2 页的数据，SHALL NOT 仍显示第 1 页的行

#### Scenario: 翻页边界 SHALL 禁用对应按钮且不发出越界请求

- **WHEN** 组件处于第 1 页
- **THEN** 「上一页」按钮 SHALL 为禁用态（`disabled`）
- **AND** 当组件处于末页（`curPage === totalPage`）时「下一页」按钮 SHALL 为禁用态
- **AND** 禁用态按钮被点击 SHALL NOT 触发任何数据请求

#### Scenario: 翻页期间 SHALL 进入加载态，不呈现旧页数据与新页码的错配

- **WHEN** 用户点击翻页按钮
- **THEN** 在新页数据到达前，页面 SHALL 呈现加载态，SHALL NOT 同时显示上一页的表格行与新页码的分页信息
