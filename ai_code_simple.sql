DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) NOT NULL COMMENT '账户编码',
  `account` varchar(64) DEFAULT NULL COMMENT '账户',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COMMENT='账户';


INSERT INTO `account` VALUES ('1', '21218cca77804d2ba1', 'lixin', '21218cca77804d2ba1922c33e0151105');



DROP TABLE IF EXISTS `frameworks`;
CREATE TABLE `frameworks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '技术编码',
  `name` varchar(256) DEFAULT NULL COMMENT '技术名称',
  `description` varchar(256) DEFAULT NULL COMMENT '技术描述',
  `gitHome` varchar(256) DEFAULT NULL,
  `account` varchar(32) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `isPublic` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='框架技术池';

INSERT INTO `frameworks` VALUES ('5', '123456', 'ssm-redis-swagger-lombok', 'ssm+redis+swagger+lombok', 'https://gitee.com/helixin/aicode_template.git', '', '', 'Y');
INSERT INTO `frameworks` VALUES ('6', '456789', 'ssm-dubbo-redis-swagger-lombok-disconf-sentry', '此项目整合了ssm+dubbo+redis+swagger+lombok几大框架便于实现分布式，采用gradle管理构建项目，lombok使用请安装ide的插件并进行设置即可完成javabean的简化', null, null, null, null);
INSERT INTO `frameworks` VALUES ('7', '74108520', 'ssh+redis+swagger+lombok', 'ssh+redis+swagger+lombok', null, null, null, null);
INSERT INTO `frameworks` VALUES ('8', '888888', 'spring-cloud-redis-lombok-sentry', '此框架整合了spring-cloud、redis、lombok，swagger等最新版本，可以方便的实现单项目开发，分布式开发的需求；需要注意的是需要对spring boot有所了解将有助于使用此套框架，框架自己包含了工具包，配置好了分层，需要修改数据库连接地址以及一些自己内网的信息接口生成后直接运行使用，免测试。', 'https://gitee.com/helixin/aicode_template.git', null, null, 'Y');
INSERT INTO `frameworks` VALUES ('9', '999999', 'ssm-redis-swagger-lombok-pitop', 'ssm+redis+swagger+lombok', 'https://gitee.com/helixin/aicode_template.git', '', '', 'Y');

DROP TABLE IF EXISTS `frameworks_template`;
CREATE TABLE `frameworks_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) DEFAULT NULL COMMENT '模板编码',
  `frameworkCode` varchar(64) DEFAULT '' COMMENT '框架编码',
  `path` varchar(256) DEFAULT NULL COMMENT '模板路径',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=134456 DEFAULT CHARSET=utf8mb4 COMMENT='框架配置文件模板';

DROP TABLE IF EXISTS `map_class_table`;
CREATE TABLE `map_class_table` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '映射编码',
  `tableName` varchar(64) DEFAULT NULL COMMENT '表名',
  `className` varchar(64) DEFAULT NULL COMMENT '类名',
  `notes` varchar(256) DEFAULT NULL COMMENT '注释',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=748 DEFAULT CHARSET=utf8mb4 COMMENT='类表映射信息';

DROP TABLE IF EXISTS `map_field_column`;
CREATE TABLE `map_field_column` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mapClassTableCode` varchar(64) DEFAULT NULL COMMENT '映射编码',
  `code` varchar(64) DEFAULT NULL COMMENT '字段属性映射编码',
  `column` varchar(64) DEFAULT NULL COMMENT '字段',
  `field` varchar(64) DEFAULT NULL COMMENT '属性',
  `sqlType` varchar(32) DEFAULT NULL COMMENT '字段类型',
  `fieldType` varchar(64) DEFAULT NULL COMMENT '属性类型',
  `notes` varchar(128) DEFAULT NULL COMMENT '注释',
  `defaultValue` varchar(32) DEFAULT NULL COMMENT '字段默认值',
  `isPrimaryKey` varchar(1) DEFAULT 'N' COMMENT '是否是主键',
  `isDate` varchar(1) DEFAULT 'N' COMMENT '是否是时间类型',
  `isState` varchar(1) DEFAULT 'N' COMMENT '是否是状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7033 DEFAULT CHARSET=utf8mb4 COMMENT='字段属性映射信息';


DROP TABLE IF EXISTS `map_relationship`;
CREATE TABLE `map_relationship` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '关系编码',
  `mapClassTableCode` varchar(64) DEFAULT NULL COMMENT '映射编码',
  `isOneToOne` varchar(1) DEFAULT 'N' COMMENT '是否一对一 Y N',
  `isOneToMany` varchar(1) DEFAULT 'N' COMMENT '是否一对多Y N',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模型关系';

DROP TABLE IF EXISTS `module`;
CREATE TABLE `module` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '模块编码',
  `name` varchar(64) DEFAULT NULL COMMENT '模块名',
  `description` varchar(256) DEFAULT NULL COMMENT '模块说明',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='第三方模块池';

INSERT INTO `module` VALUES ('1', '679039706804969422', '工具', '工具');

DROP TABLE IF EXISTS `module_file`;
CREATE TABLE `module_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `moudleCode` varchar(64) DEFAULT NULL COMMENT '模块编码',
  `path` varchar(256) DEFAULT NULL COMMENT '文件路径',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模块文件信息';



DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '项目编码',
  `name` varchar(256) DEFAULT NULL COMMENT '项目名',
  `description` varchar(512) DEFAULT NULL COMMENT '项目描述',
  `englishName` varchar(256) DEFAULT NULL COMMENT '项目英文名',
  `databaseType` varchar(16) DEFAULT NULL COMMENT '数据库类型:Mysql,Oracle',
  `language` varchar(32) DEFAULT NULL COMMENT '项目语言:Java,Python,Js',
  `state` varchar(16) DEFAULT NULL COMMENT '项目状态：停用[Disenable]，启用[Enable]',
  `copyright` varchar(512) DEFAULT NULL COMMENT '项目版权文字信息',
  `author` varchar(32) DEFAULT NULL COMMENT '作者',
  `phone` varchar(16) DEFAULT NULL COMMENT '联系方式',
  `basePackage` varchar(256) DEFAULT NULL COMMENT '项目基础包名',
  `sqlFile` varchar(256) DEFAULT NULL COMMENT '数据库脚本文件地址',
  `downloadUrl` varchar(256) DEFAULT NULL COMMENT '项目下载地址',
  `buildNumber` int(11) DEFAULT NULL COMMENT '生成次数',
  `isRepository` varchar(1) DEFAULT 'N' COMMENT '是否仓库管理',
  `isParseTable` varchar(1) DEFAULT 'N' COMMENT '是否已经解析表',
  `isParseClass` varchar(1) DEFAULT 'N' COMMENT '是否已经解析类',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `accountCode` varchar(64) DEFAULT NULL COMMENT '账户编码',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8mb4 COMMENT='项目信息';


DROP TABLE IF EXISTS `project_code_catalog`;
CREATE TABLE `project_code_catalog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) NOT NULL COMMENT '编码',
  `projectCode` varchar(64) NOT NULL COMMENT '项目编码',
  `basePackage` varchar(256) DEFAULT NULL COMMENT '项目基础包名',
  `fileName` varchar(128) DEFAULT NULL COMMENT '文件名',
  `fileSuffix` varchar(16) DEFAULT NULL COMMENT '文件后缀',
  `relativePath` varchar(256) DEFAULT NULL COMMENT '相对路径',
  `absolutePath` varchar(256) DEFAULT NULL COMMENT '绝对路径',
  PRIMARY KEY (`id`,`projectCode`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生成源码资料';

DROP TABLE IF EXISTS `project_framwork`;
CREATE TABLE `project_framwork` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `frameworkCode` varchar(64) DEFAULT NULL COMMENT '技术编码',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8mb4 COMMENT='项目应用技术';



DROP TABLE IF EXISTS `project_job`;
CREATE TABLE `project_job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  `code` varchar(64) NOT NULL COMMENT '任务编码',
  `number` varchar(64) DEFAULT NULL COMMENT '第多少次执行',
  `state` varchar(16) DEFAULT '' COMMENT '任务状态: {创建:Create} , {执行中:Executing}, {完成:Completed} ,{失败:Error}, {警告:Waring}',
  `createTime` datetime DEFAULT NULL COMMENT '执行任务时间',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=448 DEFAULT CHARSET=utf8mb4 COMMENT='任务';



DROP TABLE IF EXISTS `project_job_logs`;
CREATE TABLE `project_job_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) DEFAULT NULL COMMENT '任务编码',
  `log` varchar(1024) DEFAULT '' COMMENT '日志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31433 DEFAULT CHARSET=utf8mb4 COMMENT='任务日志';


DROP TABLE IF EXISTS `project_map`;
CREATE TABLE `project_map` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  `mapClassTableCode` varchar(64) DEFAULT NULL COMMENT '字段属性映射编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=749 DEFAULT CHARSET=utf8mb4 COMMENT='项目数据表';


-- ----------------------------
-- Table structure for project_module
-- ----------------------------
DROP TABLE IF EXISTS `project_module`;
CREATE TABLE `project_module` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  `moduleCode` varchar(64) DEFAULT '' COMMENT '模块编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目选择模块';

-- ----------------------------
-- Records of project_module
-- ----------------------------

-- ----------------------------
-- Table structure for project_repository_account
-- ----------------------------
DROP TABLE IF EXISTS `project_repository_account`;
CREATE TABLE `project_repository_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '版本管理编码',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  `account` varchar(64) DEFAULT NULL COMMENT '帐户名',
  `password` varchar(32) DEFAULT NULL COMMENT '密码',
  `home` varchar(256) DEFAULT NULL COMMENT '仓库地址',
  `description` varchar(256) DEFAULT NULL COMMENT '仓库说明',
  `state` varchar(16) DEFAULT NULL COMMENT '状态：停用[Disenable]，启用[Enable]',
  `type` varchar(16) DEFAULT NULL COMMENT '仓库类型:Git, Svn',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb4 COMMENT='版本控制管理';



-- ----------------------------
-- Table structure for project_sql
-- ----------------------------
DROP TABLE IF EXISTS `project_sql`;
CREATE TABLE `project_sql` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectCode` varchar(64) NOT NULL COMMENT '项目编码',
  `code` varchar(64) DEFAULT NULL COMMENT 'tsql编码',
  `tsql` text DEFAULT NULL COMMENT 'sql脚本',
  `state` varchar(16) DEFAULT NULL COMMENT '状态：停用[Disenable]，启用[Enable]',
  PRIMARY KEY (`id`,`projectCode`)
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8mb4 COMMENT='项目sql脚本';



-- ----------------------------
-- Table structure for setting
-- ----------------------------
DROP TABLE IF EXISTS `setting`;
CREATE TABLE `setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `k` varchar(64) DEFAULT NULL COMMENT '键',
  `v` varchar(64) DEFAULT NULL COMMENT '值',
  `description` varchar(256) DEFAULT NULL COMMENT '说明',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='设置';

-- ----------------------------
-- Records of setting
-- ----------------------------
INSERT INTO `setting` VALUES ('1', 'DefaultDatabase', 'ai_code', '默认链接数据库');
INSERT INTO `setting` VALUES ('2', 'Gradle_Directory_Structure', '[\"src/main/java\",\"src/main/resources/framework\"]', 'gradle目录结构');
INSERT INTO `setting` VALUES ('3', 'Workspace', '/workspace/', '工作目录');
INSERT INTO `setting` VALUES ('4', 'Package_entity', 'po', '实体目录命名');
INSERT INTO `setting` VALUES ('5', 'Template_Path', '/templates/', '模板默认路径');
INSERT INTO `setting` VALUES ('6', 'Repository_Path', '/repository/', 'zip仓库');
INSERT INTO `setting` VALUES ('7', 'GitHome_Default', 'https://gitee.com/helixin/aicode_template.git', '默认系统模板仓库路径');

-- ----------------------------
-- Table structure for worker_node
-- ----------------------------
DROP TABLE IF EXISTS `worker_node`;
CREATE TABLE `worker_node` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',
  `HOST_NAME` varchar(64) NOT NULL COMMENT 'host name',
  `PORT` varchar(64) NOT NULL COMMENT 'port',
  `TYPE` int(11) NOT NULL COMMENT 'node type: ACTUAL or CONTAINER',
  `LAUNCH_DATE` date NOT NULL COMMENT 'launch date',
  `MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT 'modified time',
  `CREATED` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'created time',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1503 DEFAULT CHARSET=utf8 COMMENT='DB WorkerID Assigner for UID Generator';
