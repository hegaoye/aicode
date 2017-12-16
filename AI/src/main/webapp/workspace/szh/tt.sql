/*
Navicat MariaDB Data Transfer

Source Server         : 192.168.10.250
Source Server Version : 100208
Source Host           : 192.168.10.250:3306
Source Database       : tt

Target Server Type    : MariaDB
Target Server Version : 100208
File Encoding         : 65001

Date: 2017-12-16 10:15:00
*/

SET FOREIGN_KEY_CHECKS=0;
DROP  database tt;
Create database tt;
use tt;
-- ----------------------------
-- Table structure for class_attributes
-- ----------------------------
DROP TABLE IF EXISTS `class_attributes`;
CREATE TABLE `class_attributes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `columnCode` varchar(64) NOT NULL COMMENT '字段编码',
  `code` varchar(64) NOT NULL COMMENT '属性编码',
  `name` varchar(64) DEFAULT NULL COMMENT '属性名',
  `type` varchar(64) DEFAULT NULL COMMENT '属性类型:String,int,boolean,float,double,Date,byte,short,long',
  `notes` varchar(256) DEFAULT NULL COMMENT '属性注释',
  `isPrimaryKey` varchar(1) DEFAULT 'N' COMMENT '是否是主键',
  `isDate` varchar(1) DEFAULT 'N' COMMENT '是否是时间类型',
  `isState` varchar(1) DEFAULT 'N' COMMENT '是否是状态',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='类属性信息';

-- ----------------------------
-- Records of class_attributes
-- ----------------------------

-- ----------------------------
-- Table structure for class_info
-- ----------------------------
DROP TABLE IF EXISTS `class_info`;
CREATE TABLE `class_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tableCode` varchar(64) NOT NULL COMMENT '表编码',
  `code` varchar(64) NOT NULL COMMENT '类编码',
  `basePackage` varchar(256) DEFAULT NULL COMMENT '包名',
  `notes` varchar(256) DEFAULT NULL COMMENT '类注释',
  `className` varchar(64) DEFAULT NULL COMMENT '类名',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='类映射表信息';

-- ----------------------------
-- Records of class_info
-- ----------------------------

-- ----------------------------
-- Table structure for class_relationship
-- ----------------------------
DROP TABLE IF EXISTS `class_relationship`;
CREATE TABLE `class_relationship` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '关系编码',
  `classCode` varchar(64) DEFAULT NULL COMMENT '类编码',
  `classRelationshipCode` varchar(64) DEFAULT NULL COMMENT '关系类编码',
  `relationship` varchar(16) DEFAULT NULL COMMENT '关系为：1对1  OneOnOne，1对多 OneOnMany，多对多 ManyOnMany',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='类关系';

-- ----------------------------
-- Records of class_relationship
-- ----------------------------

-- ----------------------------
-- Table structure for column_info
-- ----------------------------
DROP TABLE IF EXISTS `column_info`;
CREATE TABLE `column_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tableCode` varchar(64) DEFAULT NULL COMMENT '表编码',
  `code` varchar(64) DEFAULT NULL COMMENT '字段编码',
  `name` varchar(64) DEFAULT NULL COMMENT '字段名',
  `type` varchar(32) DEFAULT NULL COMMENT '字段类型',
  `notes` varchar(128) DEFAULT NULL COMMENT '字段注释',
  `defaultValue` varchar(32) DEFAULT NULL COMMENT '字段默认值',
  `isPrimaryKey` varchar(1) DEFAULT 'N' COMMENT '是否是主键',
  `isDate` varchar(1) DEFAULT 'N' COMMENT '是否是时间类型',
  `isState` varchar(1) DEFAULT 'N' COMMENT '是否是状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段信息';

-- ----------------------------
-- Records of column_info
-- ----------------------------

-- ----------------------------
-- Table structure for frameworks
-- ----------------------------
DROP TABLE IF EXISTS `frameworks`;
CREATE TABLE `frameworks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '技术编码',
  `description` varchar(256) DEFAULT NULL COMMENT '技术描述',
  `state` varchar(16) DEFAULT NULL COMMENT '状态：停用[Disenable]，启用[Enable]',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='框架技术池';

-- ----------------------------
-- Records of frameworks
-- ----------------------------

-- ----------------------------
-- Table structure for framework_attribute
-- ----------------------------
DROP TABLE IF EXISTS `framework_attribute`;
CREATE TABLE `framework_attribute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `frameworkCode` varchar(64) DEFAULT NULL COMMENT '技术编码',
  `attribute` varchar(64) DEFAULT NULL COMMENT '属性',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技术设置属性键';

-- ----------------------------
-- Records of framework_attribute
-- ----------------------------

-- ----------------------------
-- Table structure for framework_attribute_value
-- ----------------------------
DROP TABLE IF EXISTS `framework_attribute_value`;
CREATE TABLE `framework_attribute_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `frameworkCode` varchar(64) DEFAULT NULL COMMENT '技术编码',
  `code` varchar(64) DEFAULT NULL COMMENT '属性值编码',
  `attribute` varchar(64) DEFAULT NULL COMMENT '属性名',
  `attributeValue` varchar(128) DEFAULT NULL COMMENT '属性值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技术设置属性值';

-- ----------------------------
-- Records of framework_attribute_value
-- ----------------------------

-- ----------------------------
-- Table structure for moudles
-- ----------------------------
DROP TABLE IF EXISTS `moudles`;
CREATE TABLE `moudles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '模块编码',
  `name` varchar(64) DEFAULT NULL COMMENT '模块名',
  `path` varchar(256) DEFAULT NULL COMMENT '模块名',
  `description` varchar(256) DEFAULT NULL COMMENT '模块说明',
  `state` varchar(16) DEFAULT NULL COMMENT '状态：停用[Disenable]，启用[Enable]',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方模块池';

-- ----------------------------
-- Records of moudles
-- ----------------------------

-- ----------------------------
-- Table structure for moudles_files
-- ----------------------------
DROP TABLE IF EXISTS `moudles_files`;
CREATE TABLE `moudles_files` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `moudleCode` varchar(64) DEFAULT NULL COMMENT '模块编码',
  `moudleFileCode` varchar(64) NOT NULL COMMENT '文件编码',
  `name` varchar(64) DEFAULT NULL COMMENT '文件类名',
  `path` varchar(256) DEFAULT NULL COMMENT '文件路径',
  PRIMARY KEY (`id`,`moudleFileCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模块文件信息';

-- ----------------------------
-- Records of moudles_files
-- ----------------------------

-- ----------------------------
-- Table structure for project
-- ----------------------------
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
  `isRepository` varchar(1) DEFAULT 'N' COMMENT '是否仓库管理',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目信息';

-- ----------------------------
-- Records of project
-- ----------------------------

-- ----------------------------
-- Table structure for project_class
-- ----------------------------
DROP TABLE IF EXISTS `project_class`;
CREATE TABLE `project_class` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  `classCode` varchar(64) DEFAULT NULL COMMENT '类编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目数据类';

-- ----------------------------
-- Records of project_class
-- ----------------------------

-- ----------------------------
-- Table structure for project_files
-- ----------------------------
DROP TABLE IF EXISTS `project_files`;
CREATE TABLE `project_files` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '源码文件编码',
  `templateCode` varchar(64) DEFAULT NULL COMMENT '模板编码',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  `path` varchar(256) DEFAULT NULL COMMENT '源文件路径',
  `description` varchar(256) DEFAULT NULL COMMENT '源文件说明',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目模型源码文件';

-- ----------------------------
-- Records of project_files
-- ----------------------------

-- ----------------------------
-- Table structure for project_framwork
-- ----------------------------
DROP TABLE IF EXISTS `project_framwork`;
CREATE TABLE `project_framwork` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `frameworkCode` varchar(64) DEFAULT NULL COMMENT '技术编码',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目应用技术';

-- ----------------------------
-- Records of project_framwork
-- ----------------------------

-- ----------------------------
-- Table structure for project_job
-- ----------------------------
DROP TABLE IF EXISTS `project_job`;
CREATE TABLE `project_job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '任务编码',
  `name` varchar(64) DEFAULT NULL COMMENT '任务名',
  `description` varchar(512) DEFAULT NULL COMMENT '任务描述',
  `number` int(11) DEFAULT 0 COMMENT '任务执行次数',
  `state` varchar(16) DEFAULT NULL COMMENT '任务状态: 创建[Create] , 执行中[Executing], 完成[C ompleted] ,失败[Error] 警告 [Waring]',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务';

-- ----------------------------
-- Records of project_job
-- ----------------------------

-- ----------------------------
-- Table structure for project_job_logs
-- ----------------------------
DROP TABLE IF EXISTS `project_job_logs`;
CREATE TABLE `project_job_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) DEFAULT NULL COMMENT '任务编码',
  `result` varchar(128) DEFAULT NULL COMMENT '执行结果,任务状态+文字描述',
  `info` varchar(256) DEFAULT NULL COMMENT '执行信息',
  `createTime` datetime DEFAULT NULL COMMENT '执行时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务日志';

-- ----------------------------
-- Records of project_job_logs
-- ----------------------------

-- ----------------------------
-- Table structure for project_moudles
-- ----------------------------
DROP TABLE IF EXISTS `project_moudles`;
CREATE TABLE `project_moudles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  `moudleCode` varchar(64) DEFAULT NULL COMMENT '模块编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目选择模块';

-- ----------------------------
-- Records of project_moudles
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='版本控制管理';

-- ----------------------------
-- Records of project_repository_account
-- ----------------------------

-- ----------------------------
-- Table structure for project_tables
-- ----------------------------
DROP TABLE IF EXISTS `project_tables`;
CREATE TABLE `project_tables` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  `tableCode` varchar(64) DEFAULT NULL COMMENT '表编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目数据表';

-- ----------------------------
-- Records of project_tables
-- ----------------------------

-- ----------------------------
-- Table structure for project_tools
-- ----------------------------
DROP TABLE IF EXISTS `project_tools`;
CREATE TABLE `project_tools` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  `toolsCode` varchar(64) DEFAULT NULL COMMENT '工具编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目选择工具';

-- ----------------------------
-- Records of project_tools
-- ----------------------------

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设置';

-- ----------------------------
-- Records of setting
-- ----------------------------

-- ----------------------------
-- Table structure for table_info
-- ----------------------------
DROP TABLE IF EXISTS `table_info`;
CREATE TABLE `table_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '表编码',
  `notes` varchar(256) DEFAULT NULL COMMENT '表注释',
  `type` varchar(64) DEFAULT NULL COMMENT '表类型',
  `columnNumber` int(11) DEFAULT NULL COMMENT '表字段数',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表信息';

-- ----------------------------
-- Records of table_info
-- ----------------------------

-- ----------------------------
-- Table structure for table_relationship
-- ----------------------------
DROP TABLE IF EXISTS `table_relationship`;
CREATE TABLE `table_relationship` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '关系编码',
  `tableCode` varchar(64) DEFAULT NULL COMMENT '表编码',
  `relationship` varchar(16) DEFAULT NULL COMMENT '关系为：1对1  OneOnOne，1对多 OneOnMany，多对多 ManyOnMany',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模型关系';

-- ----------------------------
-- Records of table_relationship
-- ----------------------------

-- ----------------------------
-- Table structure for tempalte_category
-- ----------------------------
DROP TABLE IF EXISTS `tempalte_category`;
CREATE TABLE `tempalte_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '类型编码',
  `name` varchar(256) DEFAULT NULL COMMENT '类型名',
  `description` varchar(256) DEFAULT NULL COMMENT '类型说明',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板类型';

-- ----------------------------
-- Records of tempalte_category
-- ----------------------------

-- ----------------------------
-- Table structure for templates
-- ----------------------------
DROP TABLE IF EXISTS `templates`;
CREATE TABLE `templates` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '模板编码',
  `name` varchar(128) DEFAULT NULL COMMENT '模板名称',
  `description` varchar(256) DEFAULT NULL COMMENT '模板说明',
  `path` varchar(256) DEFAULT NULL COMMENT '模板路径',
  `state` varchar(16) DEFAULT NULL COMMENT '状态：停用[Disenable]，启用[Enable]',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板池';

-- ----------------------------
-- Records of templates
-- ----------------------------

-- ----------------------------
-- Table structure for template_framework
-- ----------------------------
DROP TABLE IF EXISTS `template_framework`;
CREATE TABLE `template_framework` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `frameworkCode` varchar(64) DEFAULT NULL COMMENT '技术编码',
  `templateCode` varchar(64) DEFAULT NULL COMMENT '模板编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技术与模板关系';

-- ----------------------------
-- Records of template_framework
-- ----------------------------

-- ----------------------------
-- Table structure for tools
-- ----------------------------
DROP TABLE IF EXISTS `tools`;
CREATE TABLE `tools` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `toolsCategoryCode` varchar(64) DEFAULT NULL COMMENT '工具类型编码',
  `code` varchar(64) NOT NULL COMMENT '工具编码',
  `name` varchar(64) DEFAULT NULL COMMENT '工具名',
  `path` varchar(256) DEFAULT NULL COMMENT '工具类存放路径',
  `description` varchar(256) DEFAULT NULL COMMENT '工具说明',
  `state` varchar(16) DEFAULT NULL COMMENT '状态：停用[Disenable]，启用[Enable]',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具池';

-- ----------------------------
-- Records of tools
-- ----------------------------

-- ----------------------------
-- Table structure for tools_category
-- ----------------------------
DROP TABLE IF EXISTS `tools_category`;
CREATE TABLE `tools_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '类型编码',
  `name` varchar(64) DEFAULT NULL COMMENT '类型名',
  `description` varchar(128) DEFAULT NULL COMMENT '类型说明',
  `state` varchar(16) DEFAULT NULL COMMENT '状态：停用[Disenable]，启用[Enable]',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具分类';

-- ----------------------------
-- Records of tools_category
-- ----------------------------

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
) ENGINE=InnoDB AUTO_INCREMENT=994 DEFAULT CHARSET=utf8 COMMENT='DB WorkerID Assigner for UID Generator';

-- ----------------------------
-- Records of worker_node
-- ----------------------------
INSERT INTO `worker_node` VALUES ('988', '192.168.10.173', '1512094548581-22846', '2', '2017-12-01', '2017-12-01 02:15:48', '2017-12-01 02:15:48');
INSERT INTO `worker_node` VALUES ('989', '192.168.10.173', '1512094698043-95119', '2', '2017-12-01', '2017-12-01 02:18:18', '2017-12-01 02:18:18');
INSERT INTO `worker_node` VALUES ('990', '192.168.10.173', '1512094773022-77517', '2', '2017-12-01', '2017-12-01 02:19:33', '2017-12-01 02:19:33');
INSERT INTO `worker_node` VALUES ('991', '192.168.10.173', '1512095014813-19004', '2', '2017-12-01', '2017-12-01 02:23:35', '2017-12-01 02:23:35');
INSERT INTO `worker_node` VALUES ('992', '192.168.10.173', '1513309884397-46106', '2', '2017-12-15', '2017-12-15 03:51:26', '2017-12-15 03:51:26');
INSERT INTO `worker_node` VALUES ('993', '192.168.10.173', '1513310135170-98647', '2', '2017-12-15', '2017-12-15 03:55:36', '2017-12-15 03:55:36');
