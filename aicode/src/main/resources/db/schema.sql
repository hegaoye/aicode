-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`
(
    `id`       bigint(20) NOT NULL AUTO_INCREMENT,
    `code`     varchar(64)  NOT NULL COMMENT '账户编码',
    `account`  varchar(64)  NULL DEFAULT NULL COMMENT '账户',
    `password` varchar(64)  NULL DEFAULT NULL COMMENT '密码',
    PRIMARY KEY (`id`, `code`)
) ;

-- ----------------------------
-- Table structure for display_attribute
-- ----------------------------
DROP TABLE IF EXISTS `display_attribute`;
CREATE TABLE `display_attribute`
(
    `id`                  bigint(20) NOT NULL AUTO_INCREMENT,
    `mapClassTableCode`   varchar(64)  NULL DEFAULT '' COMMENT '类模型编码',
    `mapFieldColumnCode`  varchar(64)  NOT NULL COMMENT '字段编码',
    `isRequired`          varchar(2)  NULL DEFAULT '' COMMENT '是否必填 Y,N',
    `isInsert`            varchar(2)  NULL DEFAULT '' COMMENT '是否插入',
    `isDeleteCondition`   varchar(2)  NULL DEFAULT '' COMMENT '是否是删除条件',
    `isAllowUpdate`       varchar(2)  NULL DEFAULT '' COMMENT '是否允许修改 Y,N',
    `isListPageDisplay`   varchar(2)  NULL DEFAULT '' COMMENT '是否分页列表显示 Y,N',
    `isDetailPageDisplay` varchar(2)  NULL DEFAULT '' COMMENT '是否详情页显示 Y,N',
    `isQueryRequired`     varchar(2)  NULL DEFAULT '' COMMENT '是否是查询条件 Y,N',
    `isLineNew`           varchar(2)  NULL DEFAULT '' COMMENT '是否换行',
    `matchType`           varchar(16)  NULL DEFAULT '' COMMENT '匹配方式 =,!=,>=,<=,>,<,like,左like，右like,between,in',
    `displayType`         varchar(32)  NULL DEFAULT '' COMMENT 'Autocomplete 自动完成, Cascader 级联选择 , DatePicker 日期选择框 , TimePicker 时间选择框 ,Input 单行文本框 , Textarea 多行文本框 , InputNumber 数字输入框 , Mobile 手机 , Phone 固话 , MobileOrPhone 手机或固话 ,Password 密码 , Email 邮箱 , Website 网址 , IdCard 身份证 , Mention 提及(@) ,Select 单项选择器 , MultiSelect 多项选择器 , Radio 单选按钮 , Checkbox 复选框 , Rate 评分 , Silder 滑动条 ,Switch 开关按钮 , TreeSelect 选择树 , Upload 上传 ',
    `displayName`         varchar(64)  NULL DEFAULT '' COMMENT '显示列名称',
    `displayNo`           int(11) NULL DEFAULT NULL COMMENT '显示顺序',
    `fieldValidationMode` varchar(32)  NULL DEFAULT '' COMMENT 'Email 邮件, Address 地址, Telephone 电话 , Password 密码, Date  日期 , Number 数值 , Integer 整数 , Positive_Integer 正整数 , text 文本 ,IdCard 身份证 , Website 网址',
    `validateText`        varchar(64)  NULL DEFAULT '' COMMENT '验证提示语',
    `displayCss`          varchar(32)  NULL DEFAULT '' COMMENT '显示css样式',
    PRIMARY KEY (`id`, `mapFieldColumnCode`)
) ;

-- ----------------------------
-- Table structure for frameworks
-- ----------------------------
DROP TABLE IF EXISTS `frameworks`;
CREATE TABLE `frameworks`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `code`        varchar(64)  NOT NULL COMMENT '技术编码',
    `name`        varchar(256)  NULL DEFAULT NULL COMMENT '技术名称',
    `description` varchar(256)  NULL DEFAULT NULL COMMENT '技术描述',
    `gitHome`     varchar(256)  NULL DEFAULT NULL,
    `account`     varchar(32)  NULL DEFAULT NULL,
    `password`    varchar(255)  NULL DEFAULT NULL,
    `isPublic`    varchar(2)  NULL DEFAULT NULL,
    PRIMARY KEY (`id`, `code`)
) ;

-- ----------------------------
-- Table structure for frameworks_template
-- ----------------------------
DROP TABLE IF EXISTS `frameworks_template`;
CREATE TABLE `frameworks_template`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT,
    `code`          varchar(64)  NULL DEFAULT NULL COMMENT '模板编码',
    `frameworkCode` varchar(64)  NULL DEFAULT '' COMMENT '框架编码',
    `path`          varchar(256)  NULL DEFAULT NULL COMMENT '模板路径',
    PRIMARY KEY (`id`)
) ;

-- ----------------------------
-- Table structure for map_class_table
-- ----------------------------
DROP TABLE IF EXISTS `map_class_table`;
CREATE TABLE `map_class_table`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `code`      varchar(64)  NOT NULL COMMENT '映射编码',
    `tableName` varchar(64)  NULL DEFAULT NULL COMMENT '表名',
    `className` varchar(64)  NULL DEFAULT NULL COMMENT '类名',
    `notes`     varchar(512)  NULL DEFAULT '' COMMENT '注释',
    PRIMARY KEY (`id`, `code`)
) ;

-- ----------------------------
-- Table structure for map_field_column
-- ----------------------------
DROP TABLE IF EXISTS `map_field_column`;
CREATE TABLE `map_field_column`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `mapClassTableCode` varchar(64)  NULL DEFAULT NULL COMMENT '映射编码',
    `code`              varchar(64)  NULL DEFAULT NULL COMMENT '字段属性映射编码',
    `column`            varchar(64)  NULL DEFAULT NULL COMMENT '字段',
    `field`             varchar(64)  NULL DEFAULT NULL COMMENT '属性',
    `sqlType`           varchar(32)  NULL DEFAULT NULL COMMENT '字段类型',
    `fieldType`         varchar(64)  NULL DEFAULT NULL COMMENT '属性类型',
    `notes`             varchar(512)  NULL DEFAULT '' COMMENT '注释',
    `defaultValue`      varchar(32)  NULL DEFAULT NULL COMMENT '字段默认值',
    `isPrimaryKey`      varchar(1)  NULL DEFAULT 'N' COMMENT '是否是主键',
    `isDate`            varchar(1)  NULL DEFAULT 'N' COMMENT '是否是时间类型',
    `isState`           varchar(1)  NULL DEFAULT 'N' COMMENT '是否是状态',
    PRIMARY KEY (`id`)
) ;

-- ----------------------------
-- Table structure for map_relationship
-- ----------------------------
DROP TABLE IF EXISTS `map_relationship`;
CREATE TABLE `map_relationship`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `code`              varchar(64)  NOT NULL COMMENT '关系编码',
    `mapClassTableCode` varchar(64)  NULL DEFAULT '' COMMENT '映射编码',
    `associateCode`     varchar(64)  NULL DEFAULT NULL COMMENT '被关联编码',
    `isOneToOne`        varchar(1)  NULL DEFAULT 'N' COMMENT '是否一对一 Y N',
    `isOneToMany`       varchar(1)  NULL DEFAULT 'N' COMMENT '是否一对多Y N',
    `mainField`         varchar(32)  NULL DEFAULT NULL COMMENT '主表关联属性',
    `joinField`         varchar(32)  NULL DEFAULT NULL COMMENT '从表关联属性',
    PRIMARY KEY (`id`, `code`)
) ;

-- ----------------------------
-- Table structure for module
-- ----------------------------
DROP TABLE IF EXISTS `module`;
CREATE TABLE `module`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `code`        varchar(64)  NOT NULL COMMENT '模块编码',
    `name`        varchar(64)  NULL DEFAULT NULL COMMENT '模块名',
    `description` varchar(256)  NULL DEFAULT NULL COMMENT '模块说明',
    PRIMARY KEY (`id`, `code`)
) ;

-- ----------------------------
-- Table structure for module_file
-- ----------------------------
DROP TABLE IF EXISTS `module_file`;
CREATE TABLE `module_file`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `moudleCode` varchar(64)  NULL DEFAULT NULL COMMENT '模块编码',
    `path`       varchar(256)  NULL DEFAULT NULL COMMENT '文件路径',
    PRIMARY KEY (`id`)
) ;

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `code`         varchar(64)  NOT NULL COMMENT '项目编码',
    `name`         varchar(256)  NULL DEFAULT NULL COMMENT '项目名',
    `description`  varchar(512)  NULL DEFAULT NULL COMMENT '项目描述',
    `englishName`  varchar(256)  NULL DEFAULT NULL COMMENT '项目英文名',
    `databaseType` varchar(16)  NULL DEFAULT NULL COMMENT '数据库类型:Mysql,Oracle',
    `language`     varchar(32)  NULL DEFAULT NULL COMMENT '项目语言:Java,Python,Js',
    `state`        varchar(16)  NULL DEFAULT NULL COMMENT '项目状态：停用[Disenable]，启用[Enable]',
    `copyright`    varchar(512)  NULL DEFAULT NULL COMMENT '项目版权文字信息',
    `author`       varchar(32)  NULL DEFAULT NULL COMMENT '作者',
    `phone`        varchar(16)  NULL DEFAULT NULL COMMENT '联系方式',
    `basePackage`  varchar(256)  NULL DEFAULT NULL COMMENT '项目基础包名',
    `sqlFile`      varchar(256)  NULL DEFAULT NULL COMMENT '数据库脚本文件地址',
    `downloadUrl`  varchar(256)  NULL DEFAULT NULL COMMENT '项目下载地址',
    `buildNumber`  int(11) NULL DEFAULT NULL COMMENT '生成次数',
    `isRepository` varchar(1)  NULL DEFAULT 'N' COMMENT '是否仓库管理',
    `isParseTable` varchar(1)  NULL DEFAULT 'N' COMMENT '是否已经解析表',
    `isParseClass` varchar(1)  NULL DEFAULT 'N' COMMENT '是否已经解析类',
    `createTime`   datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `updateTime`   datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `accountCode`  varchar(64)  NULL DEFAULT NULL COMMENT '账户编码',
    `isIncrement`  varchar(16)  NULL DEFAULT NULL COMMENT '是否增量生成',
    PRIMARY KEY (`id`, `code`)
) ;

-- ----------------------------
-- Table structure for project_code_catalog
-- ----------------------------
DROP TABLE IF EXISTS `project_code_catalog`;
CREATE TABLE `project_code_catalog`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `code`         varchar(64)  NOT NULL COMMENT '编码',
    `projectCode`  varchar(64)  NOT NULL COMMENT '项目编码',
    `basePackage`  varchar(256)  NULL DEFAULT NULL COMMENT '项目基础包名',
    `fileName`     varchar(128)  NULL DEFAULT NULL COMMENT '文件名',
    `fileSuffix`   varchar(16)  NULL DEFAULT NULL COMMENT '文件后缀',
    `relativePath` varchar(256)  NULL DEFAULT NULL COMMENT '相对路径',
    `absolutePath` varchar(256)  NULL DEFAULT NULL COMMENT '绝对路径',
    PRIMARY KEY (`id`, `projectCode`, `code`)
) ;

-- ----------------------------
-- Table structure for project_framwork
-- ----------------------------
DROP TABLE IF EXISTS `project_framwork`;
CREATE TABLE `project_framwork`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `frameworkCode` varchar(64)  NULL DEFAULT NULL COMMENT '技术编码',
    `projectCode`   varchar(64)  NULL DEFAULT NULL COMMENT '项目编码',
    PRIMARY KEY (`id`)
) ;

-- ----------------------------
-- Table structure for project_job
-- ----------------------------
DROP TABLE IF EXISTS `project_job`;
CREATE TABLE `project_job`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `projectCode` varchar(64)  NULL DEFAULT NULL COMMENT '项目编码',
    `code`        varchar(64)  NOT NULL COMMENT '任务编码',
    `number`      varchar(64)  NULL DEFAULT NULL COMMENT '第多少次执行',
    `state`       varchar(16)  NULL DEFAULT '' COMMENT '任务状态: {创建:Create} , {执行中:Executing}, {完成:Completed} ,{失败:Error}, {警告:Waring}',
    `createTime`  datetime(0) NULL DEFAULT NULL COMMENT '执行任务时间',
    PRIMARY KEY (`id`, `code`)
) ;

-- ----------------------------
-- Table structure for project_job_logs
-- ----------------------------
DROP TABLE IF EXISTS `project_job_logs`;
CREATE TABLE `project_job_logs`
(
    `id`   bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `code` varchar(64)  NULL DEFAULT NULL COMMENT '任务编码',
    `log`  varchar(1024)  NULL DEFAULT '' COMMENT '日志',
    PRIMARY KEY (`id`)
) ;

-- ----------------------------
-- Table structure for project_map
-- ----------------------------
DROP TABLE IF EXISTS `project_map`;
CREATE TABLE `project_map`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `projectCode`       varchar(64)  NULL DEFAULT NULL COMMENT '项目编码',
    `mapClassTableCode` varchar(64)  NULL DEFAULT NULL COMMENT '字段属性映射编码',
    PRIMARY KEY (`id`)
);

-- ----------------------------
-- Table structure for project_model
-- ----------------------------
DROP TABLE IF EXISTS `project_model`;
CREATE TABLE `project_model`
(
    `id`       bigint(20) NOT NULL,
    `code`     varchar(64)  NOT NULL COMMENT '模块编码',
    `pre_code` varchar(64)  NULL DEFAULT NULL COMMENT '上级模块编码',
    `name`     varchar(64)  NULL DEFAULT NULL COMMENT '模块显示名称',
    `route`    varchar(64)  NULL DEFAULT NULL COMMENT '模块路由',
    `css`      varchar(32)  NULL DEFAULT NULL COMMENT '模块css样式',
    `is_menu`  varchar(2)  NULL DEFAULT NULL COMMENT '是否是菜单 Y,N',
    `ico`      varchar(64)  NULL DEFAULT NULL COMMENT '模块图标',
    PRIMARY KEY (`id`, `code`)
) ;

-- ----------------------------
-- Table structure for project_model_class
-- ----------------------------
DROP TABLE IF EXISTS `project_model_class`;
CREATE TABLE `project_model_class`
(
    `id`                bigint(20) NOT NULL,
    `mapClassTableCode` varchar(64)  NOT NULL COMMENT '类编码',
    `projectModelCode`  varchar(64)  NOT NULL COMMENT '模块编码',
    PRIMARY KEY (`id`, `mapClassTableCode`, `projectModelCode`)
) ;

-- ----------------------------
-- Table structure for project_module
-- ----------------------------
DROP TABLE IF EXISTS `project_module`;
CREATE TABLE `project_module`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `projectCode` varchar(64)  NULL DEFAULT NULL COMMENT '项目编码',
    `moduleCode`  varchar(64)  NULL DEFAULT '' COMMENT '模块编码',
    PRIMARY KEY (`id`)
) ;

-- ----------------------------
-- Table structure for project_repository_account
-- ----------------------------
DROP TABLE IF EXISTS `project_repository_account`;
CREATE TABLE `project_repository_account`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `code`        varchar(64)  NOT NULL COMMENT '版本管理编码',
    `projectCode` varchar(64)  NULL DEFAULT NULL COMMENT '项目编码',
    `account`     varchar(64)  NULL DEFAULT NULL COMMENT '帐户名',
    `password`    varchar(32)  NULL DEFAULT NULL COMMENT '密码',
    `home`        varchar(256)  NULL DEFAULT NULL COMMENT '仓库地址',
    `description` varchar(256)  NULL DEFAULT NULL COMMENT '仓库说明',
    `state`       varchar(16)  NULL DEFAULT NULL COMMENT '状态：停用[Disenable]，启用[Enable]',
    `type`        varchar(16)  NULL DEFAULT NULL COMMENT '仓库类型:Git, Svn',
    PRIMARY KEY (`id`, `code`)
) ;

-- ----------------------------
-- Table structure for project_sql
-- ----------------------------
DROP TABLE IF EXISTS `project_sql`;
CREATE TABLE `project_sql`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `projectCode` varchar(64)  NOT NULL COMMENT '项目编码',
    `code`        varchar(64)  NULL DEFAULT NULL COMMENT 'tsql编码',
    `tsql`        text  NULL DEFAULT NULL COMMENT 'sql脚本',
    `state`       varchar(16)  NULL DEFAULT NULL COMMENT '状态：停用[Disenable]，启用[Enable]',
    PRIMARY KEY (`id`, `projectCode`)
) ;

-- ----------------------------
-- Table structure for setting
-- ----------------------------
DROP TABLE IF EXISTS `setting`;
CREATE TABLE `setting`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `k`           varchar(64)  NULL DEFAULT NULL COMMENT '键',
    `v`           varchar(64)  NULL DEFAULT NULL COMMENT '值',
    `description` varchar(256)  NULL DEFAULT NULL COMMENT '说明',
    PRIMARY KEY (`id`)
) ;



DROP TABLE IF EXISTS `WORKER_NODE`;
CREATE TABLE `WORKER_NODE`
(
    `ID`          bigint(20)   NOT NULL auto_increment COMMENT 'auto increment id',
    `HOST_NAME`   varchar(64)  NOT NULL COMMENT 'host name',
    `PORT`        varchar(64)  NOT NULL COMMENT 'port',
    `TYPE`        int(11)      NOT NULL COMMENT 'node type: ACTUAL or CONTAINER',
    `LAUNCH_DATE` date         NOT NULL COMMENT 'launch date',
    `MODIFIED`    timestamp(0) NOT NULL COMMENT 'modified time',
    `CREATED`     timestamp(0) NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'created time',
    PRIMARY KEY (`ID`)
);
