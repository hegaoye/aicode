# Spec Delta

## ADDED Requirements

### Requirement: 实体与数据库列的映射 SHALL 由真实 SQL 测试守护

实体类上把 Java 字段映射到数据库列的注解（列名映射、排除持久化）SHALL 由**真正执行到数据库**的集成测试覆盖 —— 该测试 SHALL 使用真实的 schema 与真实的 Mapper 调用路径。

SHALL NOT 认为以下测试覆盖了列映射：仅做内存对象装配的测试；把 Mapper 打桩为 mock 的测试；只断言业务方法返回值而不触碰 SQL 的测试。

#### Scenario: 字段映射注解变更后集成测试变红

- **WHEN** 实体上用于映射列名的注解被删除、改名，或因字段被移入父类而丢失
- **THEN** 真实 SQL 集成测试 SHALL 失败并指出列名不匹配，SHALL NOT 让该问题留到运行时由用户触发

#### Scenario: 打桩 mapper 的测试不构成映射覆盖

- **WHEN** 一组测试把 Mapper 替换为 mock，使其从不生成或执行 SQL
- **THEN** 该组测试 SHALL NOT 被视为已覆盖实体与列的映射关系

#### Scenario: 排除持久化的字段不出现在 SQL 中

- **WHEN** 某字段被声明为不参与持久化
- **THEN** 由该实体生成的 SELECT 语句 SHALL NOT 包含该列

#### Scenario: Java 字段名与列名不一致时映射明确

- **WHEN** 实体字段名与数据库列名不同（如 Java 侧为 camelCase、数据库侧亦为 camelCase 而非默认的 snake_case）
- **THEN** 实体 SHALL 以显式注解声明该映射，SHALL NOT 依赖框架默认命名策略

#### Scenario: 继承结构调整不得静默丢失映射注解

- **WHEN** 实体字段被移入父类或从父类移出
- **THEN** 原有映射注解 SHALL 随之迁移并保持有效，SHALL NOT 因注解无法在子类补声明而丢失
