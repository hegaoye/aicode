DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;

/*==============================================================*/
/* Table: QRTZ_JOB_DETAILS                                      */
/*==============================================================*/
create table QRTZ_JOB_DETAILS
(
   SCHED_NAME           VARCHAR(120) not null comment '调度器名称',
   JOB_NAME             VARCHAR(200) not null comment '集群中job的名字,该名字用户自己可以随意定制,无强行要求',
   JOB_GROUP            VARCHAR(200) not null comment '集群中job的所属组的名字,该名字用户自己随意定制,无强行要求',
   DESCRIPTION          VARCHAR(250) comment '触发器功能描述',
   JOB_CLASS_NAME       VARCHAR(250) not null comment '需要作业的类精确到类名',
   IS_DURABLE           VARCHAR(1) not null comment '是否持久化,把该属性设置为1，quartz会把job持久化到数据库中',
   IS_NONCONCURRENT     VARCHAR(1) not null comment '是否连续',
   IS_UPDATE_DATA       VARCHAR(1) not null,
   REQUESTS_RECOVERY    VARCHAR(1) not null comment '作业故障实例恢复，如果作业发生故障，则自动恢复继续执行',
   JOB_DATA             BLOB comment '一个blob字段，存放持久化job对象',
   primary key (SCHED_NAME, JOB_NAME, JOB_GROUP)
)
ENGINE=InnoDB;

alter table QRTZ_JOB_DETAILS comment '作业信息表，存放一个jobDetail信息，存储每一个已配置的Job的详细信息';

/*==============================================================*/
/* Table: QRTZ_TRIGGERS                                         */
/*==============================================================*/
create table QRTZ_TRIGGERS
(
   SCHED_NAME           VARCHAR(120) not null comment '调度器名称',
   TRIGGER_NAME         VARCHAR(200) not null comment ' trigger的名字,该名字用户自己可以随意定制,无强行要求',
   TRIGGER_GROUP        VARCHAR(200) not null comment 'trigger所属组的名字,该名字用户自己随意定制,无强行要求',
   JOB_NAME             VARCHAR(200) not null comment 'qrtz_job_details表job_name的外键',
   JOB_GROUP            VARCHAR(200) not null comment 'qrtz_job_details表job_group的外键',
   DESCRIPTION          VARCHAR(250) comment '触发器功能描述 DESCRIPTION',
   NEXT_FIRE_TIME       BIGINT(13) comment '下次执行时间',
   PREV_FIRE_TIME       BIGINT(13) comment '上次执行时间',
   PRIORITY             INTEGER comment '优先级',
   TRIGGER_STATE        VARCHAR(16) not null comment '触发器状态，等待中 WAITING ，正常执行 ACQUIRED，执行中 EXECUTING，执行完成  COMPLETE，阻塞中 BLOCKED，错误 ERROR，暂停 PAUSED',
   TRIGGER_TYPE         VARCHAR(8) not null comment '触发器类型',
   START_TIME           BIGINT(13) not null comment '开始时间',
   END_TIME             BIGINT(13) comment '结束时间',
   CALENDAR_NAME        VARCHAR(200) comment '日历名称',
   MISFIRE_INSTR        SMALLINT(2) comment '错失触发，哑火标示',
   JOB_DATA             BLOB comment '作业数据对象',
   primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
)
ENGINE=InnoDB;

alter table QRTZ_TRIGGERS comment '存储已配置的?Trigger的信息';


/*==============================================================*/
/* Table: QRTZ_SIMPLE_TRIGGERS                                  */
/*==============================================================*/
create table QRTZ_SIMPLE_TRIGGERS
(
   SCHED_NAME           VARCHAR(120) not null comment '调度器名称',
   TRIGGER_NAME         VARCHAR(200) not null comment '触发器名称',
   TRIGGER_GROUP        VARCHAR(200) not null comment '触发器分组',
   REPEAT_COUNT         BIGINT(7) not null comment '设置重复次数',
   REPEAT_INTERVAL      BIGINT(12) not null comment '设置重复间隔时间',
   TIMES_TRIGGERED      BIGINT(10) not null comment '已触发的次数',
   primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
)
ENGINE=InnoDB;

alter table QRTZ_SIMPLE_TRIGGERS comment '存储简单的 Trigger，包括重复次数，间隔，以及已触的次数 ';



/*==============================================================*/
/* Table: QRTZ_CRON_TRIGGERS                                    */
/*==============================================================*/
create table QRTZ_CRON_TRIGGERS
(
   SCHED_NAME           VARCHAR(120) not null comment '调度器名称',
   TRIGGER_NAME         VARCHAR(200) not null comment 'qrtz_triggers表trigger_name的外键',
   TRIGGER_GROUP        VARCHAR(200) not null comment 'qrtz_triggers表trigger_group的外键',
   CRON_EXPRESSION      VARCHAR(120) not null comment 'cron表达式* * * * * ?',
   TIME_ZONE_ID         VARCHAR(80) comment '时区',
   primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
)
ENGINE=InnoDB;

alter table QRTZ_CRON_TRIGGERS comment '存储Cron Trigger，包括Cron表达式和时区信息
CronTrigger 支持比 SimpleTr';


/*==============================================================*/
/* Table: QRTZ_SIMPROP_TRIGGERS                                 */
/*==============================================================*/
create table QRTZ_SIMPROP_TRIGGERS
(
   SCHED_NAME           VARCHAR(120) not null,
   TRIGGER_NAME         VARCHAR(200) not null,
   TRIGGER_GROUP        VARCHAR(200) not null,
   STR_PROP_1           VARCHAR(512),
   STR_PROP_2           VARCHAR(512),
   STR_PROP_3           VARCHAR(512),
   INT_PROP_1           INT,
   INT_PROP_2           INT,
   LONG_PROP_1          BIGINT,
   LONG_PROP_2          BIGINT,
   DEC_PROP_1           NUMERIC(13,4),
   DEC_PROP_2           NUMERIC(13,4),
   BOOL_PROP_1          VARCHAR(1),
   BOOL_PROP_2          VARCHAR(1),
   primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
)
ENGINE=InnoDB;

/*==============================================================*/
/* Table: QRTZ_BLOB_TRIGGERS                                    */
/*==============================================================*/
create table QRTZ_BLOB_TRIGGERS
(
   SCHED_NAME           VARCHAR(120) not null comment '调度器名称',
   TRIGGER_NAME         VARCHAR(200) not null comment '触发器名称',
   TRIGGER_GROUP        VARCHAR(200) not null comment '触发器组',
   BLOB_DATA            BLOB,
   primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
   key AK_Key_2 (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
)
ENGINE=InnoDB;

alter table QRTZ_BLOB_TRIGGERS comment 'Trigger作为Blob类型存储(用于Quartz用户用JDBC创建他们自己定制的Trigger类型，JobStore';



/*==============================================================*/
/* Table: QRTZ_CALENDARS                                        */
/*==============================================================*/
create table QRTZ_CALENDARS
(
   SCHED_NAME           VARCHAR(120) not null,
   CALENDAR_NAME        VARCHAR(200) not null,
   CALENDAR             BLOB not null,
   primary key (SCHED_NAME, CALENDAR_NAME)
)
ENGINE=InnoDB;

alter table QRTZ_CALENDARS comment '存放日历信息， quartz可配置一个日历来指定一个时间范围。';


/*==============================================================*/
/* Table: QRTZ_PAUSED_TRIGGER_GRPS                              */
/*==============================================================*/
create table QRTZ_PAUSED_TRIGGER_GRPS
(
   SCHED_NAME           VARCHAR(120) not null,
   TRIGGER_GROUP        VARCHAR(200) not null,
   primary key (SCHED_NAME, TRIGGER_GROUP)
)
ENGINE=InnoDB;

alter table QRTZ_PAUSED_TRIGGER_GRPS comment '已暂停触发器表';


/*==============================================================*/
/* Table: QRTZ_FIRED_TRIGGERS                                   */
/*==============================================================*/
create table QRTZ_FIRED_TRIGGERS
(
   SCHED_NAME           VARCHAR(120) not null comment '调度器名称',
   ENTRY_ID             VARCHAR(95) not null,
   TRIGGER_NAME         VARCHAR(200) not null,
   TRIGGER_GROUP        VARCHAR(200) not null,
   INSTANCE_NAME        VARCHAR(200) not null,
   FIRED_TIME           BIGINT(13) not null,
   SCHED_TIME           BIGINT(13) not null,
   PRIORITY             INTEGER not null comment '优先级',
   STATE                VARCHAR(16) not null comment '状态',
   JOB_NAME             VARCHAR(200) comment '集群中job的名字,该名字用户自己可以随意定制,无强行要求。',
   JOB_GROUP            VARCHAR(200) comment '集群中job的所属组的名字,该名字用户自己随意定制,无强行要求',
   IS_NONCONCURRENT     VARCHAR(1),
   REQUESTS_RECOVERY    VARCHAR(1),
   primary key (SCHED_NAME, ENTRY_ID)
)
ENGINE=InnoDB;

alter table QRTZ_FIRED_TRIGGERS comment '存储与已触发的Trigger相关的状态信息，以及相联Job的执行信息';



/*==============================================================*/
/* Table: QRTZ_SCHEDULER_STATE                                  */
/*==============================================================*/
create table QRTZ_SCHEDULER_STATE
(
   SCHED_NAME           VARCHAR(120) not null,
   INSTANCE_NAME        VARCHAR(200) not null comment '配置文件中org.quartz.scheduler.instanceId配置的名字，就会写 入该字段，如果设置为AUTO,quartz会根据物理机名和当前时间产生一个名字',
   LAST_CHECKIN_TIME    BIGINT(13) not null comment '上次检查时间',
   CHECKIN_INTERVAL     BIGINT(13) not null comment '检查间隔时间',
   primary key (SCHED_NAME, INSTANCE_NAME)
)
ENGINE=InnoDB;

alter table QRTZ_SCHEDULER_STATE comment '存储少量的有关 Scheduler的状态信息，和别的 Scheduler 实例(假如是用于一个集群中)';




/*==============================================================*/
/* Table: QRTZ_LOCKS                                            */
/*==============================================================*/
create table QRTZ_LOCKS
(
   SCHED_NAME           VARCHAR(120) not null,
   LOCK_NAME            VARCHAR(40) not null,
   primary key (SCHED_NAME, LOCK_NAME)
)
ENGINE=InnoDB;

alter table QRTZ_LOCKS comment '存储程序的悲观锁的信息(假如使用了悲观锁)';



CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY ON QRTZ_JOB_DETAILS(SCHED_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_J_GRP ON QRTZ_JOB_DETAILS(SCHED_NAME,JOB_GROUP);

CREATE INDEX IDX_QRTZ_T_J ON QRTZ_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_JG ON QRTZ_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_C ON QRTZ_TRIGGERS(SCHED_NAME,CALENDAR_NAME);
CREATE INDEX IDX_QRTZ_T_G ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_T_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_G_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME ON QRTZ_TRIGGERS(SCHED_NAME,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME);
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_FT_J_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_JG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_T_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_FT_TG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);

commit; 

