/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/11/30 16:27:11                          */
/*==============================================================*/

create DATABASE  hegaoye;
use hegaoye;
drop table if exists class_attributes;

drop table if exists class_info;

drop table if exists class_relationship;

drop table if exists column_info;

drop table if exists framework_attribute;

drop table if exists framework_attribute_value;

drop table if exists frameworks;

drop table if exists moudles;

drop table if exists moudles_files;

drop table if exists project;

drop table if exists project_class;

drop table if exists project_files;

drop table if exists project_framwork;

drop table if exists project_job;

drop table if exists project_job_logs;

drop table if exists project_moudles;

drop table if exists project_repository_account;

drop table if exists project_tables;

drop table if exists project_tools;

drop table if exists table_info;

drop table if exists table_relationship;

drop table if exists tempalte_category;

drop table if exists template_framework;

drop table if exists templates;

drop table if exists tools;

drop table if exists tools_category;

drop table if exists setting;

/*==============================================================*/
/* Table: class_attributes                                      */
/*==============================================================*/
create table class_attributes
(
   id                   bigint not null auto_increment comment 'id',
   columnCode           varchar(64) not null comment '字段编码',
   code                 varchar(64) not null comment '属性编码',
   name                 varchar(64) comment '属性名',
   type                 varchar(64) comment '属性类型:String,int,boolean,float,double,Date,byte,short,long',
   notes                varchar(256) comment '属性注释',
   isPrimaryKey         varchar(1) default 'N' comment '是否是主键',
   isDate               varchar(1) default 'N' comment '是否是时间类型',
   isState              varchar(1) default 'N' comment '是否是状态',
   primary key (id, code)
);

alter table class_attributes comment '类属性信息';

/*==============================================================*/
/* Table: class_info                                            */
/*==============================================================*/
create table class_info
(
   id                   bigint not null auto_increment comment 'id',
   tableCode            varchar(64) not null comment '表编码',
   code                 varchar(64) not null comment '类编码',
   package              varchar(256) comment '包名',
   notes                varchar(256) comment '类注释',
   className            varchar(64) comment '类名',
   primary key (id, code)
);

alter table class_info comment '类映射表信息';

/*==============================================================*/
/* Table: class_relationship                                    */
/*==============================================================*/
create table class_relationship
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) not null comment '关系编码',
   classCode            varchar(64) comment '类编码',
   classRelationshipCode varchar(64) comment '关系类编码',
   relationship         varchar(16) comment '关系为：1对1  OneOnOne，1对多 OneOnMany，多对多 ManyOnMany',
   primary key (id, code)
);

alter table class_relationship comment '类关系';

/*==============================================================*/
/* Table: column_info                                           */
/*==============================================================*/
create table column_info
(
   id                   bigint not null auto_increment comment 'id',
   tableCode            varchar(64) comment '表编码',
   code                 varchar(64) comment '字段编码',
   name                 varchar(64) comment '字段名',
   type                 varchar(32) comment '字段类型',
   notes                varchar(128) comment '字段注释',
   defaultValue         varchar(32) comment '字段默认值',
   isPrimaryKey         varchar(1) default 'N' comment '是否是主键',
   isDate               varchar(1) default 'N' comment '是否是时间类型',
   isState              varchar(1) default 'N' comment '是否是状态',
   primary key (id)
);

alter table column_info comment '字段信息';

/*==============================================================*/
/* Table: framework_attribute                                   */
/*==============================================================*/
create table framework_attribute
(
   id                   bigint not null auto_increment comment 'id',
   frameworkCode        varchar(64) comment '技术编码',
   attribute            varchar(64) comment '属性',
   primary key (id)
);

alter table framework_attribute comment '技术设置属性键';

/*==============================================================*/
/* Table: framework_attribute_value                             */
/*==============================================================*/
create table framework_attribute_value
(
   id                   bigint not null auto_increment comment 'id',
   frameworkCode        varchar(64) comment '技术编码',
   code                 varchar(64) comment '属性值编码',
   attribute            varchar(64) comment '属性名',
   attributeValue       varchar(128) comment '属性值',
   primary key (id)
);

alter table framework_attribute_value comment '技术设置属性值';

/*==============================================================*/
/* Table: frameworks                                            */
/*==============================================================*/
create table frameworks
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) not null comment '技术编码',
   description          varchar(256) comment '技术描述',
   state                varchar(16) comment '状态：停用[Disenable]，启用[Enable]',
   primary key (id, code)
);

alter table frameworks comment '框架技术池';

/*==============================================================*/
/* Table: moudles                                               */
/*==============================================================*/
create table moudles
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) not null comment '模块编码',
   name                 varchar(64) comment '模块名',
   path                 varchar(256) comment '模块名',
   description          varchar(256) comment '模块说明',
   state                varchar(16) comment '状态：停用[Disenable]，启用[Enable]',
   primary key (id, code)
);

alter table moudles comment '第三方模块池';

/*==============================================================*/
/* Table: moudles_files                                         */
/*==============================================================*/
create table moudles_files
(
   id                   bigint not null auto_increment comment 'id',
   moudleCode           varchar(64) comment '模块编码',
   moudleFileCode       varchar(64) not null comment '文件编码',
   name                 varchar(64) comment '文件类名',
   path                 varchar(256) comment '文件路径',
   primary key (id, moudleFileCode)
);

alter table moudles_files comment '模块文件信息';

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
   package              varchar(256) comment '项目基础包名',
   `sql`                varchar(256) comment '数据库脚本文件地址',
   downloadUrl          varchar(256) comment '项目下载地址',
   isRepository         varchar(1) default 'N' comment '是否仓库管理',
   createTime           datetime comment '创建时间',
   updateTime           datetime comment '更新时间',
   primary key (id, code)
);

alter table project comment '项目信息';

/*==============================================================*/
/* Table: project_class                                         */
/*==============================================================*/
create table project_class
(
   id                   bigint not null auto_increment comment 'id',
   projectCode          varchar(64) comment '项目编码',
   classCode            varchar(64) comment '类编码',
   primary key (id)
);

alter table project_class comment '项目数据类';

/*==============================================================*/
/* Table: project_files                                         */
/*==============================================================*/
create table project_files
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) not null comment '源码文件编码',
   templateCode         varchar(64) comment '模板编码',
   projectCode          varchar(64) comment '项目编码',
   path                 varchar(256) comment '源文件路径',
   description          varchar(256) comment '源文件说明',
   createTime           datetime comment '创建时间',
   primary key (id, code)
);

alter table project_files comment '项目模型源码文件';

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
   code                 varchar(64) not null comment '任务编码',
   name                 varchar(64) comment '任务名',
   description          varchar(512) comment '任务描述',
   number               int default 0 comment '任务执行次数',
   state                varchar(16) comment '任务状态: 创建[Create] , 执行中[Executing], 完成[C ompleted] ,失败[Error] 警告 [Waring]',
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
   result               varchar(128) comment '执行结果,任务状态+文字描述',
   info                 varchar(256) comment '执行信息',
   createTime           datetime comment '执行时间',
   primary key (id)
);

alter table project_job_logs comment '任务日志';

/*==============================================================*/
/* Table: project_moudles                                       */
/*==============================================================*/
create table project_moudles
(
   id                   bigint not null auto_increment comment 'id',
   projectCode          varchar(64) comment '项目编码',
   moudleCode           varchar(64) comment '模块编码',
   primary key (id)
);

alter table project_moudles comment '项目选择模块';

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
/* Table: project_tables                                        */
/*==============================================================*/
create table project_tables
(
   id                   bigint not null auto_increment comment 'id',
   projectCode          varchar(64) comment '项目编码',
   tableCode            varchar(64) comment '表编码',
   primary key (id)
);

alter table project_tables comment '项目数据表';

/*==============================================================*/
/* Table: project_tools                                         */
/*==============================================================*/
create table project_tools
(
   id                   bigint not null auto_increment comment 'id',
   projectCode          varchar(64) comment '项目编码',
   toolsCode            varchar(64) comment '工具编码',
   primary key (id)
);

alter table project_tools comment '项目选择工具';

/*==============================================================*/
/* Table: table_info                                            */
/*==============================================================*/
create table table_info
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) not null comment '表编码',
   notes                varchar(256) comment '表注释',
   type                 varchar(64) comment '表类型',
   columnNumber         int comment '表字段数',
   primary key (id, code)
);

alter table table_info comment '表信息';

/*==============================================================*/
/* Table: table_relationship                                    */
/*==============================================================*/
create table table_relationship
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) not null comment '关系编码',
   tableCode            varchar(64) comment '表编码',
   relationship         varchar(16) comment '关系为：1对1  OneOnOne，1对多 OneOnMany，多对多 ManyOnMany',
   primary key (id, code)
);

alter table table_relationship comment '模型关系';

/*==============================================================*/
/* Table: tempalte_category                                     */
/*==============================================================*/
create table tempalte_category
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) not null comment '类型编码',
   name                 varchar(256) comment '类型名',
   description          varchar(256) comment '类型说明',
   primary key (id, code)
);

alter table tempalte_category comment '模板类型';

/*==============================================================*/
/* Table: template_framework                                    */
/*==============================================================*/
create table template_framework
(
   id                   bigint not null auto_increment comment 'id',
   frameworkCode        varchar(64) comment '技术编码',
   templateCode         varchar(64) comment '模板编码',
   primary key (id)
);

alter table template_framework comment '技术与模板关系';

/*==============================================================*/
/* Table: templates                                             */
/*==============================================================*/
create table templates
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) not null comment '模板编码',
   name                 varchar(128) comment '模板名称',
   description          varchar(256) comment '模板说明',
   path                 varchar(256) comment '模板路径',
   state                varchar(16) comment '状态：停用[Disenable]，启用[Enable]',
   primary key (id, code)
);

alter table templates comment '模板池';

/*==============================================================*/
/* Table: tools                                                 */
/*==============================================================*/
create table tools
(
   id                   bigint not null auto_increment comment 'id',
   toolsCategoryCode    varchar(64) comment '工具类型编码',
   code                 varchar(64) not null comment '工具编码',
   name                 varchar(64) comment '工具名',
   path                 varchar(256) comment '工具类存放路径',
   description          varchar(256) comment '工具说明',
   state                varchar(16) comment '状态：停用[Disenable]，启用[Enable]',
   primary key (id, code)
);

alter table tools comment '工具池';

/*==============================================================*/
/* Table: tools_category                                        */
/*==============================================================*/
create table tools_category
(
   id                   bigint not null auto_increment comment 'id',
   code                 varchar(64) not null comment '类型编码',
   name                 varchar(64) comment '类型名',
   description          varchar(128) comment '类型说明',
   state                varchar(16) comment '状态：停用[Disenable]，启用[Enable]',
   primary key (id, code)
);

alter table tools_category comment '工具分类';

drop table if exists setting;

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
