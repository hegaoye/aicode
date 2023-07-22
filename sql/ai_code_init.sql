/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.1.221
 Source Server Type    : MariaDB
 Source Server Version : 100406
 Source Host           : 192.168.1.221:3306
 Source Schema         : ai_code

 Target Server Type    : MariaDB
 Target Server Version : 100406
 File Encoding         : 65001

 Date: 09/07/2019 18:17:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账户编码',
  `account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  PRIMARY KEY (`id`, `code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '账户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of account
-- ----------------------------
INSERT INTO `account` VALUES (1, '21218cca77804d2ba1', 'lixin', '21218cca77804d2ba1922c33e0151105');
INSERT INTO `account` VALUES (2, '21218cca77804d2ba2', 'admin', '21218cca77804d2ba1922c33e0151105');
INSERT INTO `account` VALUES (3, '21218cca77804d2ba2', 'aicode', '21218cca77804d2ba1922c33e0151105');

-- ----------------------------
-- Table structure for display_attribute
-- ----------------------------
DROP TABLE IF EXISTS `display_attribute`;
CREATE TABLE `display_attribute`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mapClassTableCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '类模型编码',
  `mapFieldColumnCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字段编码',
  `isRequired` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '是否必填 Y,N',
  `isInsert` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '是否插入',
  `isDeleteCondition` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '是否是删除条件',
  `isAllowUpdate` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '是否允许修改 Y,N',
  `isListPageDisplay` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '是否分页列表显示 Y,N',
  `isDetailPageDisplay` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '是否详情页显示 Y,N',
  `isQueryRequired` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '是否是查询条件 Y,N',
  `isLineNew` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '是否换行',
  `matchType` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '匹配方式 =,!=,>=,<=,>,<,like,左like，右like,between,in',
  `displayType` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'Autocomplete 自动完成, Cascader 级联选择 , DatePicker 日期选择框 , TimePicker 时间选择框 ,Input 单行文本框 , Textarea 多行文本框 , InputNumber 数字输入框 , Mobile 手机 , Phone 固话 , MobileOrPhone 手机或固话 ,Password 密码 , Email 邮箱 , Website 网址 , IdCard 身份证 , Mention 提及(@) ,Select 单项选择器 , MultiSelect 多项选择器 , Radio 单选按钮 , Checkbox 复选框 , Rate 评分 , Silder 滑动条 ,Switch 开关按钮 , TreeSelect 选择树 , Upload 上传 ',
  `displayName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '显示列名称',
  `displayNo` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
  `fieldValidationMode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'Email 邮件, Address 地址, Telephone 电话 , Password 密码, Date  日期 , Number 数值 , Integer 整数 , Positive_Integer 正整数 , text 文本 ,IdCard 身份证 , Website 网址',
  `validateText` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '验证提示语',
  `displayCss` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '显示css样式',
  PRIMARY KEY (`id`, `mapFieldColumnCode`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 559 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '显示属性' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for frameworks
-- ----------------------------
DROP TABLE IF EXISTS `frameworks`;
CREATE TABLE `frameworks`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '技术编码',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '技术名称',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '技术描述',
  `gitHome` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `account` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `isPublic` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '框架技术池' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of frameworks
-- ----------------------------
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (1, '1882812765213425664', 'springboot2.1.6-mybatisplus-redis-provider', '此框架以springcloud Greenwich.RELEASE，springboot 2.1.6.RELEASE 版本为基础进行整合，整合 springcloud的各个组件eureka,feign，mybatisplus,hutool实现分布式微服务', 'https://github.com/hegaoye/aicode-template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (2, '1081629222214967291', 'ssm-dubbo-redis-swagger-lombok-disconf-sentry', '此项目整合了ssm+dubbo+redis+swagger+lombok几大框架便于实现分布式，采用gradle管理构建项目，lombok使用请安装ide的插件并进行设置即可完成javabean的简化', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (3, '1081629222214967292', 'ssh+redis+swagger+lombok', 'ssh+redis+swagger+lombok', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (4, '1081629222214967293', 'spring-cloud-redis-lombok-sentry', '此框架整合了spring-cloud、redis、lombok，swagger等最新版本，可以方便的实现单项目开发，分布式开发的需求；需要注意的是需要对spring boot有所了解将有助于使用此套框架，框架自己包含了工具包，配置好了分层，需要修改数据库连接地址以及一些自己内网的信息接口生成后直接运行使用，免测试。', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (5, '1081629222214967294', 'ssm-redis-swagger-lombok-pitop', 'ssm+redis+swagger+lombok', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (6, '1081629222214967295', 'springboot-redis-swagger-lombok', 'springboot+redis+swagger+lombok', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (7, '1081629222214967296', 'angular-template', 'angular 框架自动与后端接口对接，请配合使用以frontend结尾的后端框架生成使用', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (8, '1081629222214967297', 'springboot-redis-swagger-lombok-frontend', 'springboot-redis-swagger-lombok-frontend', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (9, '1081629222214967298', 'angular-template-i18n', 'angular国际化模板', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (10, '1081629222214967299', 'springcloud2.1.5-redis-swagger-lombok-provider', '此框架以springcloud Greenwich.RELEASE，springboot 2.1.5.RELEASE 版本为基础进行整合，整合 springcloud的各个组件eureka,feign 实现分布式微服务', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (11, '1593730370629427201', 'springcloud2.1.5-provider-config', '此框架以springcloud Greenwich.RELEASE，springboot 2.1.5.RELEASE 版本为基础进行整合，整合 springcloud的各个组件eureka,feign,config 实现分布式微服务', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (12, '1593730370629427202', 'springboot-redis-swagger-lombok-aoplog-jwt', 'Spring cloud + Mybatis + Redis + Swagger + Lombok 基础框架组合 + 自定义 AOP 日志 + JWT 登录验证', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (13, '1593730370629427203', 'ssh-redis-swagger-lombok', '整合springmvc,spring,hibernate,swagger,redis,lombok等多种框架', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (14, '1593730370629427204', 'angular-template-rzhkj', 'angular 框架自动与后端接口对接，请配合使用以frontend结尾的后端框架生成使用', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (15, '1593730370629427205', 'springboot-redis-swagger-lombok-rzhkj', 'rzhkj springboot 版本框架整合了redis,swagger,lombok等', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (16, '1081629222214967290', 'ssm-redis-swagger-lombok', 'ssm+redis+swagger+lombok', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (17, '1884742476840075264', 'springboot2.1.6-mybatisplus-redis-rocketmq', '此框架以springcloud Greenwich.RELEASE，springboot 2.1.6.RELEASE 版本为基础进行整合，整合 springcloud的各个组件eureka,feign，mybatisplus,hutool实现分布式微服务', 'https://github.com/hegaoye/aicode-template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (18, '1886058987395538944', 'springboot2.1.6-mybatisplus-mongodb-redis-rocketmq', '此框架以springcloud Greenwich.RELEASE，springboot 2.1.6.RELEASE 版本为基础进行整合，整合 springcloud的各个组件eureka,feign，mybatisplus,mongodb,redis,hutool实现分布式微服务', 'https://github.com/hegaoye/aicode-template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (19, '1887714594208948224', 'springboot2.1.6-mybatisplus-redis', '整合springboot2.1.6,以及cloud组件feign,mybatisplus,redis主要用于分布式系统中的 ctrl层，用于整合provider层使用，且swagger访问时可以返回R 对象，封装的统一返回类型', 'https://github.com/hegaoye/aicode-template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (20, '1887715178324500480', 'springboot2.1.6-mybatisplus-mongodb-redis-rabbitmq', '整合springboot2.1.6,以及cloud组件feign,mybatisplus,mongodb,redis,rabbitmq,主要用于分布式系统中的 provider层，此时ctrl层仅仅返回实际结果不会被包装为BeanRet的对象', 'https://github.com/hegaoye/aicode-template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (21, '1887715590641360896', 'springboot2.1.6-mybatisplus-mongodb-redis-kafka', '整合springboot2.1.6,以及cloud组件feign,mybatisplus,mongodb,redis,kafka,主要用于分布式系统中的 provider层，此时ctrl层仅仅返回实际结果不会被包装为BeanRet的对象', 'https://github.com/hegaoye/aicode-template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (22, '1887716621433511936', 'springboot2.1.6-mysql-mongo-redis-rocketmq-dubbo', '整合springboot2.1.6,以及cloud组件feign,mybatisplus,mongodb,redis,rocketmq,dubbo,主要用于分布式系统中的 provider层，此时ctrl层仅仅返回实际结果不会被包装为BeanRet的对象', 'https://github.com/hegaoye/aicode-template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (23, '2079202962321588224', 'springboot2.2.6-mybatisplus-redis-ignite-rocketmq', '整合springboot2.1.6,以及cloud组件feign,mybatisplus,redis,rocketmq,ignite,主要用于分布式系统中的 provider层，此时ctrl层仅仅返回实际结果不会被包装为BeanRet的对象', 'https://github.com/hegaoye/aicode-template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (24, '2080258063987564544', 'springboot2.2.6-mybatisplus-redis-rocketmq', '整合springboot2.1.6,以及cloud组件feign,mybatisplus,redis,rocketmq,mysql,主要用于分布式系统中的 provider层，此时ctrl层仅仅返回实际结果不会被包装为BeanRet的对象', 'https://github.com/hegaoye/aicode-template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (25, '2080258063987564555', 'springboot2.2.6-mybatisplus-redis', '整合springboot2.1.6,以及cloud组件feign,mybatisplus,redis,mysql,主要用于分布式系统中的 provider层，此时ctrl层仅仅返回实际结果不会被包装为BeanRet的对象', 'https://github.com/hegaoye/aicode-template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (26, '2080258063987564556', 'springboot2.2.6-mybatisplus-redis-gateway', '整合springboot2.2.6,以及cloud组件feign,mybatisplus,redis,gateway,mysql', 'https://github.com/hegaoye/aicode-template.git', 'hegaoye@qq.com', '888888', 'Y');
INSERT INTO frameworks (id, code, name, description, gitHome, account, password, isPublic) VALUES (29, '2080258063987564559', 'springcloud3.1.1-mybatisplus-redis-java17', '整合springboot3.1.1,java17+,以及cloud组件feign,mybatisplus,redis,rocketmq,springdoc', 'https://github.com/hegaoye/aicode-template.git', 'hegaoye@qq.com', '888888', 'Y');


-- ----------------------------
-- Table structure for frameworks_template
-- ----------------------------
DROP TABLE IF EXISTS `frameworks_template`;
CREATE TABLE `frameworks_template`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板编码',
  `frameworkCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '框架编码',
  `path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板路径',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 205745 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '框架配置文件模板' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for map_class_table
-- ----------------------------
DROP TABLE IF EXISTS `map_class_table`;
CREATE TABLE `map_class_table`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '映射编码',
  `tableName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表名',
  `className` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类名',
  `notes` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '注释',
  PRIMARY KEY (`id`, `code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1441 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '类表映射信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for map_field_column
-- ----------------------------
DROP TABLE IF EXISTS `map_field_column`;
CREATE TABLE `map_field_column`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mapClassTableCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '映射编码',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段属性映射编码',
  `column` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段',
  `field` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性',
  `sqlType` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段类型',
  `fieldType` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性类型',
  `notes` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '注释',
  `defaultValue` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段默认值',
  `isPrimaryKey` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否是主键',
  `isDate` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否是时间类型',
  `isState` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否是状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12181 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字段属性映射信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for map_relationship
-- ----------------------------
DROP TABLE IF EXISTS `map_relationship`;
CREATE TABLE `map_relationship`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关系编码',
  `mapClassTableCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '映射编码',
  `associateCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '被关联编码',
  `isOneToOne` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否一对一 Y N',
  `isOneToMany` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否一对多Y N',
  `mainField` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主表关联属性',
  `joinField` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '从表关联属性',
  PRIMARY KEY (`id`, `code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 236 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模型关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for module
-- ----------------------------
DROP TABLE IF EXISTS `module`;
CREATE TABLE `module`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模块编码',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块名',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块说明',
  PRIMARY KEY (`id`, `code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '第三方模块池' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for module_file
-- ----------------------------
DROP TABLE IF EXISTS `module_file`;
CREATE TABLE `module_file`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `moudleCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块编码',
  `path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件路径',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块文件信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目编码',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目名',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目描述',
  `englishName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目英文名',
  `databaseType` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库类型:Mysql,Oracle',
  `language` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目语言:Java,Python,Js',
  `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目状态：停用[Disenable]，启用[Enable]',
  `copyright` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目版权文字信息',
  `author` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作者',
  `phone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `basePackage` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目基础包名',
  `sqlFile` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库脚本文件地址',
  `downloadUrl` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目下载地址',
  `buildNumber` int(11) NULL DEFAULT NULL COMMENT '生成次数',
  `isRepository` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否仓库管理',
  `isParseTable` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否已经解析表',
  `isParseClass` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否已经解析类',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `accountCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户编码',
  `isIncrement` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否增量生成',
  PRIMARY KEY (`id`, `code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 231 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for project_code_catalog
-- ----------------------------
DROP TABLE IF EXISTS `project_code_catalog`;
CREATE TABLE `project_code_catalog`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码',
  `projectCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目编码',
  `basePackage` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目基础包名',
  `fileName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名',
  `fileSuffix` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件后缀',
  `relativePath` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '相对路径',
  `absolutePath` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绝对路径',
  PRIMARY KEY (`id`, `projectCode`, `code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '生成源码资料' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for project_framwork
-- ----------------------------
DROP TABLE IF EXISTS `project_framwork`;
CREATE TABLE `project_framwork`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `frameworkCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '技术编码',
  `projectCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 241 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目应用技术' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for project_job
-- ----------------------------
DROP TABLE IF EXISTS `project_job`;
CREATE TABLE `project_job`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `projectCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目编码',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务编码',
  `number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第多少次执行',
  `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '任务状态: {创建:Create} , {执行中:Executing}, {完成:Completed} ,{失败:Error}, {警告:Waring}',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '执行任务时间',
  PRIMARY KEY (`id`, `code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1150 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for project_job_logs
-- ----------------------------
DROP TABLE IF EXISTS `project_job_logs`;
CREATE TABLE `project_job_logs`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务编码',
  `log` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '日志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 63597 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for project_map
-- ----------------------------
DROP TABLE IF EXISTS `project_map`;
CREATE TABLE `project_map`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `projectCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目编码',
  `mapClassTableCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段属性映射编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1442 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for project_model
-- ----------------------------
DROP TABLE IF EXISTS `project_model`;
CREATE TABLE `project_model`  (
  `id` bigint(20) NOT NULL,
  `code` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '模块编码',
  `pre_code` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '上级模块编码',
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '模块显示名称',
  `route` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '模块路由',
  `css` varchar(32) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '模块css样式',
  `is_menu` varchar(2) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '是否是菜单 Y,N',
  `ico` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '模块图标',
  PRIMARY KEY (`id`, `code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = '模块' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for project_model_class
-- ----------------------------
DROP TABLE IF EXISTS `project_model_class`;
CREATE TABLE `project_model_class`  (
  `id` bigint(20) NOT NULL,
  `mapClassTableCode` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '类编码',
  `projectModelCode` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '模块编码',
  PRIMARY KEY (`id`, `mapClassTableCode`, `projectModelCode`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = '模块下的类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for project_module
-- ----------------------------
DROP TABLE IF EXISTS `project_module`;
CREATE TABLE `project_module`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `projectCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目编码',
  `moduleCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模块编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目选择模块' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for project_repository_account
-- ----------------------------
DROP TABLE IF EXISTS `project_repository_account`;
CREATE TABLE `project_repository_account`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '版本管理编码',
  `projectCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目编码',
  `account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '帐户名',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `home` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '仓库地址',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '仓库说明',
  `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态：停用[Disenable]，启用[Enable]',
  `type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '仓库类型:Git, Svn',
  PRIMARY KEY (`id`, `code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 136 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '版本控制管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for project_sql
-- ----------------------------
DROP TABLE IF EXISTS `project_sql`;
CREATE TABLE `project_sql`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目编码',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'tsql编码',
  `tsql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'sql脚本',
  `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态：停用[Disenable]，启用[Enable]',
  PRIMARY KEY (`id`, `projectCode`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 193 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目sql脚本' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for setting
-- ----------------------------
DROP TABLE IF EXISTS `setting`;
CREATE TABLE `setting`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `k` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '键',
  `v` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '值',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '说明',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of setting
-- ----------------------------
INSERT INTO `setting` VALUES (1, 'DefaultDatabase', 'ai_code', '默认链接数据库');
INSERT INTO `setting` VALUES (2, 'Gradle_Directory_Structure', '[\"src/main/java\",\"src/main/resources/framework\"]', 'gradle目录结构');
INSERT INTO `setting` VALUES (3, 'Workspace', '/static/workspace/', '工作目录');
INSERT INTO `setting` VALUES (4, 'Package_entity', 'po', '实体目录命名');
INSERT INTO `setting` VALUES (5, 'Template_Path', '/static/templates/', '模板默认路径');
INSERT INTO `setting` VALUES (6, 'Repository_Path', '/static/repository/', 'zip仓库');
INSERT INTO `setting` VALUES (7, 'GitHome_Default', 'https://gitee.com/helixin/aicode_template.git', '默认系统模板仓库路径');
INSERT INTO `setting` VALUES (8, 'SandBox_Path', '/sandbox/', '沙箱环境目录');

-- ----------------------------
-- Table structure for worker_node
-- ----------------------------
DROP TABLE IF EXISTS `worker_node`;
CREATE TABLE `worker_node`  (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',
  `HOST_NAME` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'host name',
  `PORT` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'port',
  `TYPE` int(11) NOT NULL COMMENT 'node type: ACTUAL or CONTAINER',
  `LAUNCH_DATE` date NOT NULL COMMENT 'launch date',
  `MODIFIED` timestamp(0) NOT NULL DEFAULT current_timestamp ON UPDATE CURRENT_TIMESTAMP COMMENT 'modified time',
  `CREATED` timestamp(0) NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'created time',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2285 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'DB WorkerID Assigner for UID Generator' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
