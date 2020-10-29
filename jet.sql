DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS `count_event`;
DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS event_category;
DROP TABLE IF EXISTS hero;
DROP TABLE IF EXISTS hero_talent;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS league;
DROP TABLE IF EXISTS line_up;
DROP TABLE IF EXISTS logo;
DROP TABLE IF EXISTS map;
DROP TABLE IF EXISTS `match`;
DROP TABLE IF EXISTS match_team;
DROP TABLE IF EXISTS region;
DROP TABLE IF EXISTS round;
DROP TABLE IF EXISTS round_event;
DROP TABLE IF EXISTS round_hero_result;
DROP TABLE IF EXISTS round_item;
DROP TABLE IF EXISTS round_result;
DROP TABLE IF EXISTS schedule;
DROP TABLE IF EXISTS schedule_result;
DROP TABLE IF EXISTS team;
DROP TABLE IF EXISTS team_member;
DROP TABLE IF EXISTS team_member_career;
DROP TABLE IF EXISTS team_member_exploit;
DROP TABLE IF EXISTS team_member_hero_preference;



CREATE TABLE category
(
    id      varchar(64) NOT NULL comment 'id',
    pre_id  varchar(64) comment '上级id',
    cn_name varchar(32) comment '中文名',
    en_name varchar(32) comment '英文名',
    alias   varchar(32) comment '别名',
    status  varchar(16) comment '状态: 启用 Enable,停用 Disable',
    PRIMARY KEY (id),
    INDEX (pre_id)
) comment ='电竞分类' CHARACTER SET = utf8mb4
                  COLLATE = utf8mb4_general_ci;
CREATE TABLE count_event
(
    id                varchar(64) NOT NULL comment 'id',
    category_id       varchar(64) comment '电竞分类id',
    event_category_id varchar(64) comment '事件分类id',
    event_id          varchar(64) comment '事件id',
    status            varchar(16) comment '状态: 启用 Enable,停用 Disable',
    create_time       date comment '创建时间',
    modify_time       date comment '修改时间',
    PRIMARY KEY (id),
    INDEX (category_id),
    INDEX (event_category_id)
) comment ='需要统计事件' CHARACTER SET = utf8mb4
                    COLLATE = utf8mb4_general_ci;
CREATE TABLE event
(
    id                varchar(64) NOT NULL comment 'id',
    category_id       varchar(64) comment '电竞分类id',
    event_category_id varchar(64) comment '事件分类id',
    cn_name           varchar(32) comment '事件中文名',
    en_name           varchar(64) comment '事件英文名',
    alias             varchar(32) comment '事件别名',
    logo_id           varchar(64) comment 'logo',
    status            varchar(16) comment '状态: 启用 Enable ,停用 Disable',
    create_time       date comment '创建时间',
    modify_time       date comment '修改时间',
    summary           varchar(128) comment '说明',
    PRIMARY KEY (id),
    INDEX (category_id),
    INDEX (event_category_id)
) comment ='事件' CHARACTER SET = utf8mb4
                COLLATE = utf8mb4_general_ci;
CREATE TABLE event_category
(
    id          varchar(64) NOT NULL comment 'id',
    category_id varchar(64) comment '电竞分类id',
    cn_name     varchar(32) comment '事件中文名',
    en_name     varchar(32) comment '英文名',
    status      varchar(16) comment '状态: 启用 Enable,停用 Disable',
    PRIMARY KEY (id),
    INDEX (category_id)
) comment ='事件分类' CHARACTER SET = utf8mb4
                  COLLATE = utf8mb4_general_ci;
CREATE TABLE hero
(
    id          varchar(64) NOT NULL comment 'id',
    category_id varchar(64) comment '电竞分类id',
    cn_name     varchar(32) comment '英雄中文名',
    en_name     varchar(32) comment '英雄英文名',
    alias       varchar(32) comment '别名',
    logo_id     varchar(64) comment 'logo id',
    summary     varchar(128) comment '说明 64 个汉字',
    PRIMARY KEY (id),
    INDEX (category_id)
) comment ='英雄' CHARACTER SET = utf8mb4
                COLLATE = utf8mb4_general_ci;
CREATE TABLE hero_talent
(
    id        varchar(64) NOT NULL comment 'id',
    hero_id   varchar(64) comment '英雄id',
    cn_talent varchar(32) comment '中文天赋说明',
    en_talent varchar(128) comment '英文天赋说明',
    logo_id   varchar(64) comment '天赋logo',
    PRIMARY KEY (id),
    INDEX (hero_id)
) comment ='英雄天赋' CHARACTER SET = utf8mb4
                  COLLATE = utf8mb4_general_ci;
CREATE TABLE item
(
    id          varchar(64) NOT NULL comment 'id',
    category_id varchar(64) comment '电竞分类id',
    cn_name     varchar(32) comment '中文名',
    en_name     varchar(64) comment '英文名',
    logo_id     varchar(64) comment 'logo id',
    summary     varchar(128) comment '说明 64 个汉字',
    PRIMARY KEY (id),
    INDEX (category_id)
) comment ='物品装备' CHARACTER SET = utf8mb4
                  COLLATE = utf8mb4_general_ci;
CREATE TABLE league
(
    id           varchar(64) NOT NULL comment 'id',
    category_id  varchar(64) comment '电竞分类id',
    region_id    varchar(64) comment '区域id',
    cn_name      varchar(32) comment '中文名',
    en_name      varchar(32) comment '英文名',
    alias        varchar(32) comment '别名',
    status       varchar(16) comment '状态：启用 Enable,停用 Disable',
    logo_id      varchar(64) comment 'logo',
    founded_date date comment '创立日期',
    create_time  date comment '创建时间',
    modify_time  date comment '修改时间',
    PRIMARY KEY (id),
    INDEX (category_id),
    INDEX (region_id)
) comment ='联赛' CHARACTER SET = utf8mb4
                COLLATE = utf8mb4_general_ci;
CREATE TABLE line_up
(
    id             varchar(64) NOT NULL comment 'id',
    round_id       varchar(64) comment '局id',
    team_id        varchar(64) comment '战队id',
    team_member_id varchar(64) comment '战队成员id',
    hero_id        varchar(64) comment '英雄id',
    PRIMARY KEY (id),
    INDEX (round_id),
    INDEX (team_id),
    INDEX (team_member_id),
    INDEX (hero_id)
) comment ='阵容' CHARACTER SET = utf8mb4
                COLLATE = utf8mb4_general_ci;
CREATE TABLE logo
(
    id     varchar(64) NOT NULL comment 'id',
    type   varchar(16) comment '类型',
    base64 varchar(1024) comment 'base64 字符码',
    PRIMARY KEY (id)
) comment ='图标' CHARACTER SET = utf8mb4
                COLLATE = utf8mb4_general_ci;
CREATE TABLE map
(
    id      varchar(64) NOT NULL comment '地图id',
    cn_name varchar(32) comment '地图中文名',
    en_name varchar(64) comment '地图英文名',
    alias   varchar(32) comment '地图别名',
    summary varchar(128) comment '说明 64个汉字',
    PRIMARY KEY (id)
) comment ='地图' CHARACTER SET = utf8mb4
                COLLATE = utf8mb4_general_ci;
CREATE TABLE `match`
(
    id          varchar(64) NOT NULL comment 'id',
    category_id varchar(64) comment '电竞分类id',
    region_id   varchar(64) comment '区域id',
    cn_name     varchar(32) comment '赛事中文名',
    en_name     varchar(128) comment '赛事英文名',
    alias       varchar(32) comment '别名',
    start_date  date comment '比赛开始日期',
    end_date    date comment '比赛结束日期',
    status      varchar(16) comment '状态:未开始 Not_Started，进行中 Live，已延期  Delay，已结束 End,取消比赛 Cannel，中断 Interrupt，暂停比赛 Time_Out',
    bo          varchar(8) comment '赛制：BO1 一局决胜负 ,BO3 三局二胜, BO5 五局三胜，BO7 七局四胜',
    logo_id     varchar(64) comment 'logo',
    venue       varchar(32) comment '举办地',
    org         varchar(128) comment '举办方',
    create_time date comment '创建时间',
    modify_time date comment '修改时间',
    PRIMARY KEY (id),
    INDEX (category_id),
    INDEX (region_id)
) comment ='赛事' CHARACTER SET = utf8mb4
                COLLATE = utf8mb4_general_ci;
CREATE TABLE match_team
(
    id          varchar(64) NOT NULL comment 'id',
    category_id varchar(64) comment '电竞分类id',
    match_id    varchar(64) comment '赛事id',
    team_id     varchar(64) comment '战队id',
    logo_id     varchar(64) comment '队伍1logo',
    status      varchar(16) comment '状态：参赛 Join,退赛 Quit',
    create_time date comment '创建时间',
    modify_time date comment '修改时间',
    PRIMARY KEY (id),
    INDEX (category_id),
    INDEX (match_id),
    INDEX (team_id)
) comment ='参赛战队' CHARACTER SET = utf8mb4
                  COLLATE = utf8mb4_general_ci;
CREATE TABLE region
(
    id      varchar(64) NOT NULL comment 'id',
    pre_id  varchar(64) comment '上级区域id',
    code    varchar(32) comment '区域代码',
    cn_name varchar(32) comment '区域中文名',
    en_name varchar(32) comment '区域英文名',
    PRIMARY KEY (id),
    INDEX (pre_id)
) comment ='区域' CHARACTER SET = utf8mb4
                COLLATE = utf8mb4_general_ci;
CREATE TABLE round
(
    id           varchar(64) NOT NULL comment 'id',
    schedule_id  varchar(64) comment '赛程id',
    alias        int(10) comment '局别名',
    round_type   int(10) comment '局类型，1~10',
    map_id       varchar(64) comment '地图id',
    team1_id     varchar(64) comment '队伍1id',
    team2_id     varchar(64) comment '队伍2id',
    team1_score  int(10) comment '队伍1比分',
    team2_score  int(10) comment '队伍2比分',
    team_type    varchar(16) comment '队伍类型：Home 主队，Away 客队',
    win_team_id  varchar(64) comment '获胜队伍id',
    lost_team_id varchar(64) comment '输队伍id',
    status       varchar(16) comment '状态:未开始 Not_Started，进行中 Live，已延期  Delay，已结束 Done,取消比赛 Cannel，暂停比赛 Time_Out',
    start_time   date comment '比赛开始时间',
    end_time     date comment '比赛结束时间',
    duration     varchar(10) comment '时长',
    create_time  date comment '创建时间',
    modify_time  date comment '修改时间',
    PRIMARY KEY (id),
    INDEX (schedule_id),
    INDEX (team1_id),
    INDEX (team2_id)
) comment ='赛局' CHARACTER SET = utf8mb4
                COLLATE = utf8mb4_general_ci;
CREATE TABLE round_event
(
    id          varchar(64) NOT NULL comment 'id',
    round_id    varchar(64) comment '局id',
    event_id    varchar(64) comment '事件id',
    event_time  date comment '事件时间',
    create_time date comment '创建时间',
    PRIMARY KEY (id),
    INDEX (round_id),
    INDEX (event_id)
) comment ='局事件记录' CHARACTER SET = utf8mb4
                   COLLATE = utf8mb4_general_ci;
CREATE TABLE round_hero_result
(
    id             varchar(64) NOT NULL comment 'id',
    round_id       varchar(64) comment '局id',
    team_id        varchar(64) comment '战队id',
    hero_id        varchar(64) comment '英雄id',
    team_member_id varchar(64) comment '战队队员id',
    level          int(10) comment '等级',
    kda            int(10) comment 'kda数',
    front_kill     int(10) comment '正补数',
    back_kill      int(10) comment '反补数',
    xpm            float comment 'xpm',
    gpm            float comment 'gpm  gold per minute',
    economy        float comment '经济',
    `kill`         float comment '伤害',
    create_time    date comment '创建时间',
    PRIMARY KEY (id),
    INDEX (round_id),
    INDEX (team_id)
) comment ='局赛果英雄统计' CHARACTER SET = utf8mb4
                     COLLATE = utf8mb4_general_ci;
CREATE TABLE round_item
(
    id       varchar(64) NOT NULL comment 'id',
    round_id varchar(64) comment '局id',
    item_id  varchar(64) comment '物品装备id',
    team_id  varchar(64) comment '战队id',
    hero_id  varchar(64) comment '英雄id',
    PRIMARY KEY (id),
    INDEX (round_id),
    INDEX (item_id),
    INDEX (hero_id)
) comment ='局装备物品' CHARACTER SET = utf8mb4
                   COLLATE = utf8mb4_general_ci;
CREATE TABLE round_result
(
    id          varchar(64) NOT NULL comment 'id',
    category_id varchar(64) comment '电竞分类id',
    round_id    varchar(64) comment '局id',
    team_id     varchar(64) comment '战队id',
    event_id    varchar(64) comment '事件id',
    total       int(10) comment '统计数',
    create_time date comment '创建时间',
    PRIMARY KEY (id),
    INDEX (category_id),
    INDEX (round_id),
    INDEX (team_id)
) comment ='局赛果统计' CHARACTER SET = utf8mb4
                   COLLATE = utf8mb4_general_ci;
CREATE TABLE schedule
(
    id           varchar(64) NOT NULL comment '赛程id',
    match_id     varchar(64) comment '赛事id',
    team1_id     varchar(64) comment '队伍1id',
    team2_id     varchar(64) comment 'team2 id',
    team1_alias  varchar(32) comment '队伍1别名',
    team2_alias  varchar(32) comment '队伍2别名',
    team1_score  int(10) comment '队伍1 比分',
    team2_score  int(10) comment '队伍2比分',
    team_type    varchar(16) comment '队伍类型：home 主队，away 客队',
    bo           varchar(8) comment '赛制',
    status       varchar(16) comment '状态:未开始 Not_Started，进行中 Live，已延期  Delay，已结束 End,取消比赛 Cannel，中断 Interrupt，暂停比赛 Time_Out',
    start_time   date comment '比赛开始时间',
    end_time     date comment '比赛结束时间',
    win_team_id  varchar(64) comment '获胜队伍id',
    lost_team_id varchar(64) comment '负队伍id',
    create_time  date comment '创建时间',
    modify_time  date comment '修改时间',
    PRIMARY KEY (id),
    INDEX (match_id)
) comment ='赛程' CHARACTER SET = utf8mb4
                COLLATE = utf8mb4_general_ci;
CREATE TABLE schedule_result
(
    id          varchar(64) NOT NULL comment 'id',
    schedule_id varchar(64) comment '赛程id',
    category_id varchar(64) comment '电竞分类id',
    team_id     varchar(64) comment '战队id',
    event_id    varchar(64) comment '事件id',
    total       int(10) comment '统计数',
    create_time date comment '创建时间',
    PRIMARY KEY (id),
    INDEX (schedule_id),
    INDEX (category_id)
) comment ='赛程赛果统计' CHARACTER SET = utf8mb4
                    COLLATE = utf8mb4_general_ci;
CREATE TABLE team
(
    id           varchar(64) NOT NULL comment 'id',
    category_id  varchar(64) comment '电竞分类id',
    region_id    varchar(64) comment '区域id',
    cn_name      varchar(32) comment '战队中文名',
    en_name      varchar(64) comment '英文名',
    alias        varchar(32) comment '别名',
    logo_id      varchar(64) comment 'logo',
    status       varchar(16) comment '状态: 启用 Enable,停用 Disable',
    country      varchar(16) comment '国家代码',
    total_bonus  varchar(16) comment '总奖金',
    founded_date date comment '创立日期',
    address      varchar(255) comment '地址',
    rank         int(10) comment '排名',
    integral     int(10) comment '总积分',
    create_time  date comment '创建时间',
    modify_time  int(10) comment '修改时间',
    PRIMARY KEY (id)
) comment ='战队' CHARACTER SET = utf8mb4
                COLLATE = utf8mb4_general_ci;
CREATE TABLE team_member
(
    id          varchar(64) NOT NULL comment 'id',
    category_id varchar(64) comment '电竞分类id',
    cn_name     varchar(32) comment '中文名',
    en_name     varchar(32) comment '英文名',
    alias       varchar(32) comment '别名',
    logo_id     varchar(64) comment '头像id',
    status      varchar(16) comment '状态：在队 Serve，离队 Quit',
    postion     int(10) comment '位置',
    join_date   date comment '入队日期',
    win         int(10) comment '胜战绩',
    lost        int(10) comment '负战绩',
    create_time date comment '创建时间',
    modify_time date comment '修改时间',
    PRIMARY KEY (id),
    INDEX (category_id)
) comment ='战队队员' CHARACTER SET = utf8mb4
                  COLLATE = utf8mb4_general_ci;
CREATE TABLE team_member_career
(
    id                       varchar(64) NOT NULL comment 'id',
    category_id              varchar(64) comment 'id',
    team_member_id           varchar(64) comment '队员id',
    participate              int(10) comment '参赛数',
    win                      int(10) comment '胜场数',
    lost                     int(10) comment '负场数',
    per_kill                 float comment '每场均击杀数',
    per_death                float comment '每场均死亡数',
    per_assist               float comment '每场均助攻数',
    per_make_kill            float comment '每场均补刀数',
    per_minute_make_kill     float comment '每场没分均补刀数',
    per_economy              float comment '每场均贡献经济',
    per_minute_economy       float comment '每场没分均贡献经济',
    join_group_rate          float comment '参团率',
    per_experience           float comment '每场均经验',
    per_output_damage        float comment '每场均输出伤害数',
    per_minute_output_damage float comment '每场每分均输出伤害数',
    PRIMARY KEY (id),
    INDEX (category_id)
) comment ='职业生涯' CHARACTER SET = utf8mb4
                  COLLATE = utf8mb4_general_ci;
CREATE TABLE team_member_exploit
(
    id             varchar(64) NOT NULL comment 'id',
    category_id    varchar(64) comment '电竞分类id',
    match_id       varchar(64) comment '赛事id',
    team_id        varchar(64) comment '战队id',
    team_member_id varchar(64) comment '战队队员id',
    hero_id        varchar(64) comment '英雄id',
    kda            float comment 'kda',
    damage         float comment '伤害',
    win_lost       varchar(8) comment '胜负类型：Win 胜，Lost 负',
    competitor_id  varchar(64) comment '对手战队id',
    create_time    date comment '创建时间',
    PRIMARY KEY (id)
) comment ='队员战绩记录' CHARACTER SET = utf8mb4
                    COLLATE = utf8mb4_general_ci;
CREATE TABLE team_member_hero_preference
(
    id             varchar(64) NOT NULL comment 'id',
    category_id    varchar(64) comment '电竞分类id',
    team_member_id varchar(64) comment '队员id',
    hero_id        varchar(64) comment '英雄id',
    frequency      int(10) comment '使用次数',
    gpm            float comment 'gold per minute',
    kda            float comment 'kda',
    front_kill     float comment '正补',
    back_kill      float comment '反补',
    win_rate       float comment '胜率',
    PRIMARY KEY (id),
    INDEX (category_id),
    INDEX (team_member_id),
    INDEX (hero_id)
) comment ='战队队员英雄偏好统计' CHARACTER SET = utf8mb4
                        COLLATE = utf8mb4_general_ci;
