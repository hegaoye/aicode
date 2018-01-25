/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/12/27 13:43:38                          */
/*==============================================================*/


drop table if exists account;

drop table if exists frameworks;

drop table if exists frameworks_template;

drop table if exists map_class_table;

drop table if exists map_field_column;

drop table if exists module;

drop table if exists module_file;

drop table if exists project;

drop table if exists project_code_catalog;

drop table if exists project_framwork;

drop table if exists project_job;

drop table if exists project_job_logs;

drop table if exists project_map;

drop table if exists project_module;

drop table if exists project_repository_account;

drop table if exists project_sql;

drop table if exists setting;

drop table if exists table_relationship;

/*==============================================================*/
/* Table: account                                               */
/*==============================================================*/
create table account
(
   id                   bigint not null auto_increment,
   code                 varchar(64) not null comment '账户编码',
   account              varchar(64) comment '账户',
   password             varchar(64) comment '密码',
   primary key (id, code)
);

alter table account comment '账户';

/*==============================================================*/
/* Table: frameworks                                            */
/*==============================================================*/
create table frameworks
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) not null comment '技术编码',
   name                 varchar(256) comment '技术名称',
   description          varchar(256) comment '技术描述',
   primary key (id, code)
);

alter table frameworks comment '框架技术池';

/*==============================================================*/
/* Table: frameworks_template                                   */
/*==============================================================*/
create table frameworks_template
(
   id                   bigint not null auto_increment,
   code                 varchar(64) comment '模板编码',
   frameworksCode       varchar(64) comment '框架编码',
   path                 varchar(256) comment '模板路径',
   primary key (id)
);

alter table frameworks_template comment '框架配置文件模板';

/*==============================================================*/
/* Table: map_class_table                                       */
/*==============================================================*/
create table map_class_table
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) not null comment '映射编码',
   tableName            varchar(64) comment '表名',
   className            varchar(64) comment '类名',
   notes                varchar(256) comment '注释',
   primary key (id, code)
);

alter table map_class_table comment '类表映射信息';

/*==============================================================*/
/* Table: map_field_column                                      */
/*==============================================================*/
create table map_field_column
(
   id                   bigint not null auto_increment comment 'id',
   mapClassTableCode    varchar(64) comment '映射编码',
   code                 varchar(64) comment '字段属性映射编码',
   `column`               varchar(64) comment '字段',
   field                varchar(64) comment '属性',
   sqlType              varchar(32) comment '字段类型',
   fieldType            varchar(64) comment '属性类型',
   notes                varchar(128) comment '注释',
   defaultValue         varchar(32) comment '字段默认值',
   isPrimaryKey         varchar(1) default 'N' comment '是否是主键',
   isDate               varchar(1) default 'N' comment '是否是时间类型',
   isState              varchar(1) default 'N' comment '是否是状态',
   primary key (id)
);

alter table map_field_column comment '字段属性映射信息';

/*==============================================================*/
/* Table: module                                                */
/*==============================================================*/
create table module
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) not null comment '模块编码',
   name                 varchar(64) comment '模块名',
   description          varchar(256) comment '模块说明',
   primary key (id, code)
);

alter table module comment '第三方模块池';

/*==============================================================*/
/* Table: module_file                                           */
/*==============================================================*/
create table module_file
(
   id                   bigint not null auto_increment comment 'id',
   moudleCode           varchar(64) comment '模块编码',
   path                 varchar(256) comment '文件路径',
   primary key (id)
);

alter table module_file comment '模块文件信息';

/*==============================================================*/
/* Table: project                                               */
/*==============================================================*/
create table project
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) not null comment '项目编码',
   name                 varchar(256) comment '项目名',
   description          varchar(512) comment '项目描述',
   englishName          varchar(256) comment '项目英文名',
   databaseType         varchar(16) comment '数据库类型:Mysql,Oracle',
   language             varchar(32) comment '项目语言:Java,Python,Js',
   state                varchar(16) comment '项目状态：停用[Disenable]，启用[Enable]',
   copyright            varchar(512) comment '项目版权文字信息',
   author               varchar(32) comment '作者',
   phone                varchar(16) comment '联系方式',
   basePackage          varchar(256) comment '项目基础包名',
   sqlFile              varchar(256) comment '数据库脚本文件地址',
   downloadUrl          varchar(256) comment '项目下载地址',
   buildNumber          int comment '生成次数',
   isRepository         varchar(1) default 'N' comment '是否仓库管理',
   isParseTable         varchar(1) default 'N' comment '是否已经解析表',
   isParseClass         varchar(1) default 'N' comment '是否已经解析类',
   createTime           datetime comment '创建时间',
   updateTime           datetime comment '更新时间',
   accountCode          varchar(64) comment '账户编码',
   primary key (id, code)
);

alter table project comment '项目信息';

/*==============================================================*/
/* Table: project_code_catalog                                  */
/*==============================================================*/
create table project_code_catalog
(
   id                   bigint not null auto_increment,
   code                 varchar(64) not null comment '编码',
   projectCode          varchar(64) not null comment '项目编码',
   basePackage          varchar(256) comment '项目基础包名',
   fileName             varchar(128) comment '文件名',
   fileSuffix           varchar(16) comment '文件后缀',
   relativePath         varchar(256) comment '相对路径',
   absolutePath         varchar(256) comment '绝对路径',
   primary key (id, projectCode, code)
);

alter table project_code_catalog comment '生成源码资料';

/*==============================================================*/
/* Table: project_framwork                                      */
/*==============================================================*/
create table project_framwork
(
   id                   bigint not null auto_increment comment 'id',
   frameworkCode        varchar(64) comment '技术编码',
   projectCode          varchar(64) comment '项目编码',
   primary key (id)
);

alter table project_framwork comment '项目应用技术';

/*==============================================================*/
/* Table: project_job                                           */
/*==============================================================*/
create table project_job
(
   id                   bigint not null auto_increment comment 'id',
   projectCode          varchar(64) comment '项目编码',
   code                 varchar(64) not null comment '任务编码',
   number               varchar(64) comment '第多少次执行',
   state                varchar(16) comment '任务状态: 创建[Create] , 执行中[Executing], 完成[C ompleted] ,失败[Error] 警告 [Waring]',
   createTime           datetime comment '执行任务时间',
   primary key (id, code)
);

alter table project_job comment '任务';

/*==============================================================*/
/* Table: project_job_logs                                      */
/*==============================================================*/
create table project_job_logs
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) comment '任务编码',
   log                  varchar(256) comment '日志',
   primary key (id)
);

alter table project_job_logs comment '任务日志';

/*==============================================================*/
/* Table: project_map                                           */
/*==============================================================*/
create table project_map
(
   id                   bigint not null auto_increment comment 'id',
   projectCode          varchar(64) comment '项目编码',
   mapClassTableCode    varchar(64) comment '字段属性映射编码',
   primary key (id)
);

alter table project_map comment '项目数据表';

/*==============================================================*/
/* Table: project_module                                        */
/*==============================================================*/
create table project_module
(
   id                   bigint not null auto_increment comment 'id',
   projectCode          varchar(64) comment '项目编码',
   moudleCode           varchar(64) comment '模块编码',
   primary key (id)
);

alter table project_module comment '项目选择模块';

/*==============================================================*/
/* Table: project_repository_account                            */
/*==============================================================*/
create table project_repository_account
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) not null comment '版本管理编码',
   projectCode          varchar(64) comment '项目编码',
   account              varchar(64) comment '帐户名',
   password             varchar(32) comment '密码',
   home                 varchar(256) comment '仓库地址',
   description          varchar(256) comment '仓库说明',
   state                varchar(16) comment '状态：停用[Disenable]，启用[Enable]',
   type                 varchar(16) comment '仓库类型:Git, Svn',
   primary key (id, code)
);

alter table project_repository_account comment '版本控制管理';

/*==============================================================*/
/* Table: project_sql                                           */
/*==============================================================*/
create table project_sql
(
   id                   bigint not null auto_increment,
   projectCode          varchar(64) not null comment '项目编码',
   code                 varchar(64) comment 'tsql编码',
   tsql                 text comment 'sql脚本',
   state                varchar(16) comment '状态：停用[Disenable]，启用[Enable]',
   primary key (id, projectCode)
);

alter table project_sql comment '项目sql脚本';

/*==============================================================*/
/* Table: setting                                               */
/*==============================================================*/
create table setting
(
   id                   bigint not null auto_increment comment 'id',
   k                    varchar(64) comment '键',
   v                    varchar(64) comment '值',
   description          varchar(256) comment '说明',
   primary key (id)
);

alter table setting comment '设置';

/*==============================================================*/
/* Table: table_relationship                                    */
/*==============================================================*/
create table table_relationship
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) not null comment '关系编码',
   mapClassTableCode    varchar(64) comment '映射编码',
   isOneToOne           varchar(1) default 'N' comment '是否一对一 Y N',
   isOneToMany          varchar(1) default 'N' comment '是否一对多Y N',
   primary key (id, code)
);

alter table table_relationship comment '模型关系';

