## MODIFIED Requirements

### Requirement: 嵌入式数据库与驱动版本基线

系统 SHALL 使用与目标框架兼容的嵌入式数据库版本。数据库连接参数 SHALL NOT 使用目标数据库版本已明确拒绝的参数组合。Schema 与初始化数据 SHALL 能在目标数据库版本下原样执行。**升级数据库版本时，系统 SHALL 复核所有对数据库元数据（`INFORMATION_SCHEMA`）的查询，确保其使用的列名与取值形式与新版本实际返回的一致。**

#### Scenario: 数据库版本与框架兼容

- **WHEN** 应用启动并初始化数据库
- **THEN** 数据库 SHALL 以框架依赖管理所声明的版本运行

#### Scenario: 连接参数不被数据库拒绝

- **WHEN** 应用建立数据库连接
- **THEN** 连接参数组合 SHALL 被数据库接受，SHALL NOT 触发「特性不支持」错误

#### Scenario: Schema 原样执行

- **WHEN** 应用启动时执行数据库初始化脚本
- **THEN** 初始化脚本 SHALL 在当前数据库版本与兼容模式下执行成功，SHALL NOT 需要修改脚本文本

#### Scenario: 自增主键在复合主键下可用

- **WHEN** 某表使用「自增列 + 多列复合主键」定义，且系统以兼容模式运行
- **THEN** 自增序列 SHALL 正常工作

#### Scenario: 升级后存量数据库文件的处理方式明确

- **WHEN** 数据库版本升级导致旧版本创建的数据文件不可用
- **THEN** 变更文档 SHALL 明确要求删除旧数据文件并重建，SHALL NOT 让使用者在启动失败后自行猜测

#### Scenario: 升级后元数据查询仍然有效

- **WHEN** 数据库版本升级，且该升级改变了 `INFORMATION_SCHEMA` 中某列的名称或取值形式
- **THEN** 依赖该列的查询 SHALL 仍能返回正确结果，SHALL NOT 静默返回空集或抛出 SQL 错误

#### Scenario: 元数据列名到实体属性的映射显式声明

- **WHEN** 元数据查询返回的列名与目标实体的属性名不是驼峰对应关系
- **THEN** 查询 SHALL 显式声明列别名，SHALL NOT 依赖自动映射，SHALL NOT 出现「查询成功但实体属性为 null」的静默失败
