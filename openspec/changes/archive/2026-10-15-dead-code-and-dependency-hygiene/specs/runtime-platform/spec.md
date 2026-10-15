# Spec Delta

## ADDED Requirements

### Requirement: 依赖管理版本与框架版本一致

系统 SHALL 在全部构建模块上导入与目标框架版本一致的依赖管理清单（BOM）。系统 SHALL NOT 出现部分模块使用旧版 BOM、另一部分模块使用新版 BOM 的状态。依赖管理清单 SHALL 由根配置统一声明版本，各模块 SHALL 引用同一版本。

#### Scenario: 三模块导入同一 BOM 版本

- **WHEN** 检查任一模块的依赖管理声明
- **THEN** 其导入的 BOM 版本 SHALL 与其余模块相同

#### Scenario: 版本分裂被发现并修正

- **WHEN** 某模块的 BOM 版本低于应用模块的框架版本
- **THEN** 该模块 SHALL 被提升至一致版本，且其测试 SHALL 通过全量回归

#### Scenario: BOM 提升触发跨大版本回归

- **WHEN** BOM 提升导致某个模块的测试框架跨越主版本
- **THEN** 该模块 SHALL 单独执行完整回归，SHALL NOT 与其他改动混在同一批次中验证

### Requirement: 注解处理器版本不被显式锁版覆盖

构建 SHALL NOT 对已由依赖管理清单提供受支持版本的组件显式锁定更旧版本，包括测试框架与注解处理器。

#### Scenario: 显式旧版本被交由 BOM 决定

- **WHEN** 构建声明中对某组件写了低于 BOM 管理值的显式版本号
- **THEN** 该显式版本号 SHALL 被移除，由依赖管理清单决定最终版本
