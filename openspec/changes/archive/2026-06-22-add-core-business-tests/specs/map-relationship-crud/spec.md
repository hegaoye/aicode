## ADDED Requirements

### Requirement: MapRelationshipController.build 建立正向关联
The system SHALL accept `mapClassTableCode`, `associateCode`, `oneToOne`, `oneToMany`, `mainField`, `joinField` and create (or update) a `MapRelationship` row, generating a fresh `code` if no existing match is found.

#### Scenario: 新建一对多关联
- **WHEN** `POST /project/relationship/build` is called with `oneToMany=Y` and `oneToOne=N` and `mainField=userId` and `joinField=orderUserId`
- **THEN** a `MapRelationship` row is persisted with the given fields, and a `code` is assigned

### Requirement: MapRelationshipController.build 反向关联方向保持
The system SHALL, after creating the forward relationship, create a reverse `MapRelationship` whose direction is the inverse of the forward: a forward `oneToMany` MUST yield a reverse `oneToOne`, and a forward `oneToOne` MUST yield a reverse `oneToOne`.

#### Scenario: 正向 OneToMany 的反向是 OneToOne
- **WHEN** the forward relationship is `oneToMany=Y, oneToOne=N`
- **THEN** the reverse `MapRelationship` MUST have `isOneToOne=Y` and `isOneToMany=N` (with `mainField` and `joinField` swapped)

#### Scenario: 正向 OneToOne 的反向是 OneToOne
- **WHEN** the forward relationship is `oneToOne=Y` (regardless of oneToMany)
- **THEN** the reverse `MapRelationship` MUST have `isOneToOne=Y` and `isOneToMany=N`

#### Scenario: 反向关联的 mainField/joinField 互换
- **WHEN** the forward is `mainField=A, joinField=B`
- **THEN** the reverse MUST have `mainField=B, joinField=A`
