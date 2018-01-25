/*
Navicat MariaDB Data Transfer

Source Server         : 192.168.10.250
Source Server Version : 100208
Source Host           : 192.168.10.250:3306
Source Database       : ai_code_simple

Target Server Type    : MariaDB
Target Server Version : 100208
File Encoding         : 65001

Date: 2018-01-25 17:04:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) NOT NULL COMMENT '账户编码',
  `account` varchar(64) DEFAULT NULL COMMENT '账户',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户';

-- ----------------------------
-- Records of account
-- ----------------------------

-- ----------------------------
-- Table structure for frameworks
-- ----------------------------
DROP TABLE IF EXISTS `frameworks`;
CREATE TABLE `frameworks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '技术编码',
  `name` varchar(256) DEFAULT NULL COMMENT '技术名称',
  `description` varchar(256) DEFAULT NULL COMMENT '技术描述',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='框架技术池';

-- ----------------------------
-- Records of frameworks
-- ----------------------------
INSERT INTO `frameworks` VALUES ('5', '123456', 'ssm+redis+swagger+lombok', 'ssm+redis+swagger+lombok');

-- ----------------------------
-- Table structure for frameworks_template
-- ----------------------------
DROP TABLE IF EXISTS `frameworks_template`;
CREATE TABLE `frameworks_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) DEFAULT NULL COMMENT '模板编码',
  `frameworkCode` varchar(64) DEFAULT '' COMMENT '框架编码',
  `path` varchar(256) DEFAULT NULL COMMENT '模板路径',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=123458 DEFAULT CHARSET=utf8mb4 COMMENT='框架配置文件模板';

-- ----------------------------
-- Records of frameworks_template
-- ----------------------------
INSERT INTO `frameworks_template` VALUES ('1', '123456', '123456', '/ssm/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java');
INSERT INTO `frameworks_template` VALUES ('2', '456789', '123456', '/ssm/${module}/src/main/resources/mybatis/${model}/${className}.xml');
INSERT INTO `frameworks_template` VALUES ('3', '111111', '123456', '/ssm/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java');
INSERT INTO `frameworks_template` VALUES ('4', '222222', '123456', '/ssm/${module}/src/main/java/${basepackage}/${model}/entity/${className}.java');
INSERT INTO `frameworks_template` VALUES ('5', '333333', '123456', '/ssm/${module}/src/main/java/${basepackage}/${model}/facade/${className}SV.java');
INSERT INTO `frameworks_template` VALUES ('6', '77777', '123456', '/ssm/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java');
INSERT INTO `frameworks_template` VALUES ('7', '888888', '123456', '/ssm/${module}/src/main/resources/frameworks/spring-cache-uid.xml');
INSERT INTO `frameworks_template` VALUES ('8', '99999', '123456', '/ssm/${module}/src/main/resources/frameworks/spring-data-redis.xml');
INSERT INTO `frameworks_template` VALUES ('9', '00000', '123456', '/ssm/${module}/src/main/resources/frameworks/spring-datasource.xml');
INSERT INTO `frameworks_template` VALUES ('10', '101010', '123456', '/ssm/${module}/src/main/resources/frameworks/spring-import.xml');
INSERT INTO `frameworks_template` VALUES ('11', '11111010', '123456', '/ssm/${module}/src/main/resources/frameworks/spring-mybatis.xml');
INSERT INTO `frameworks_template` VALUES ('12', '121212121', '123456', '/ssm/${module}/src/main/resources/frameworks/spring-uid.xml');
INSERT INTO `frameworks_template` VALUES ('13', '13131313', '123456', '/ssm/${module}/src/main/resources/disconf.properties');
INSERT INTO `frameworks_template` VALUES ('14', '14141414', '123456', '/ssm/${module}/src/main/resources/jdbc.properties');
INSERT INTO `frameworks_template` VALUES ('15', '15151515', '123456', '/ssm/${module}/src/main/resources/log4j.properties');
INSERT INTO `frameworks_template` VALUES ('16', '161616', '123456', '/ssm/${module}/src/main/resources/redis-manager-config.properties');
INSERT INTO `frameworks_template` VALUES ('17', '171717741', '123456', '/ssm/${module}/src/main/resources/sentry.properties');
INSERT INTO `frameworks_template` VALUES ('18', '181818', '123456', '/ssm/${module}/src/main/resources/spring-servlet.xml');
INSERT INTO `frameworks_template` VALUES ('19', '191919', '123456', '/ssm/${module}/src/main/resources/upload_config.properties');
INSERT INTO `frameworks_template` VALUES ('20', '202020', '123456', '/ssm/${module}/src/main/webapp/WEB-INF/web.xml');
INSERT INTO `frameworks_template` VALUES ('21', '2121212', '123456', '/ssm/settings.gradle');
INSERT INTO `frameworks_template` VALUES ('22', '222212122121', '123456', '/ssm/build.gradle');
INSERT INTO `frameworks_template` VALUES ('23', '232323', '123456', '/ssm/${module}/build.gradle');
INSERT INTO `frameworks_template` VALUES ('24', '24242424', '123456', '/ssm/${module}/src/main/resources/mybatis/mybatis-config.xml');
INSERT INTO `frameworks_template` VALUES ('25', '25252525', '123456', '/ssm/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAO.java');
INSERT INTO `frameworks_template` VALUES ('26', '26262626', '123456', '/ssm/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAOImpl.java');
INSERT INTO `frameworks_template` VALUES ('27', '27272727', '123456', '/ssm/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSV.java');
INSERT INTO `frameworks_template` VALUES ('28', '28282828', '123456', '/ssm/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSVImpl.java');
INSERT INTO `frameworks_template` VALUES ('29', '29292929', '123456', '/ssm/${module}/src/main/java/${basepackage}/core/base/BeanRet.java');
INSERT INTO `frameworks_template` VALUES ('30', '30303030', '123456', '/ssm/${module}/src/main/java/${basepackage}/core/base/Page.java');
INSERT INTO `frameworks_template` VALUES ('31', '31313131', '123456', '/ssm/${module}/src/main/java/${basepackage}/core/base/BaseEntity.java');
INSERT INTO `frameworks_template` VALUES ('32', '32323232', '123456', '/ssm/${module}/src/main/java/${basepackage}/core/tools/redis/MybatisRedisCache.java');
INSERT INTO `frameworks_template` VALUES ('33', '333333333', '123456', '/ssm/${module}/src/main/java/${basepackage}/core/tools/redis/RedisCacheTransfer.java');
INSERT INTO `frameworks_template` VALUES ('34', '3434343434', '123456', '/ssm/${module}/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java');
INSERT INTO `frameworks_template` VALUES ('35', '35353535', '123456', '/ssm/${module}/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java');
INSERT INTO `frameworks_template` VALUES ('36', '36363636', '123456', '/ssm/${module}/src/main/java/${basepackage}/core/tools/SwaggerConfig.java');
INSERT INTO `frameworks_template` VALUES ('37', '37373737', '123456', '/ssm/${module}/src/main/java/${basepackage}/core/base/ExceptionHandler.java');

-- ----------------------------
-- Table structure for map_class_table
-- ----------------------------
DROP TABLE IF EXISTS `map_class_table`;
CREATE TABLE `map_class_table` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '映射编码',
  `tableName` varchar(64) DEFAULT NULL COMMENT '表名',
  `className` varchar(64) DEFAULT NULL COMMENT '类名',
  `notes` varchar(256) DEFAULT NULL COMMENT '注释',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=195 DEFAULT CHARSET=utf8mb4 COMMENT='类表映射信息';

-- ----------------------------
-- Records of map_class_table
-- ----------------------------
INSERT INTO `map_class_table` VALUES ('176', '728013741413752832', 'admin', 'Admin', '管理员表');
INSERT INTO `map_class_table` VALUES ('177', '728013741413752844', 'basic_settings', 'BasicSettings', '系统基础关键配置表');
INSERT INTO `map_class_table` VALUES ('178', '728013741413752851', 'coupon', 'Coupon', '代金券释放计划（已完成的礼包中所有返还计划转移至历史表）');
INSERT INTO `map_class_table` VALUES ('179', '728013741413752868', 'coupon_his', 'CouponHis', '代金券释放计划历史表（已完成的礼包中所有返还计划转移至此）');
INSERT INTO `map_class_table` VALUES ('180', '728013741413752885', 'finace_plat', 'FinacePlat', '平台财务账户');
INSERT INTO `map_class_table` VALUES ('181', '728013741413752893', 'finace_plat_rec', 'FinacePlatRec', '平台财务流水');
INSERT INTO `map_class_table` VALUES ('182', '728013741413752906', 'goods', 'Goods', '商品');
INSERT INTO `map_class_table` VALUES ('183', '728013741413752916', 'goods_buy_record', 'GoodsBuyRecord', '商品购买记录（释放完毕的礼包中对应的所有代金券转移至代金券历史表，复投时改变用户表is_need_cycle）');
INSERT INTO `map_class_table` VALUES ('184', '728013741413752933', 'orders', 'Orders', '用户购物订单表');
INSERT INTO `map_class_table` VALUES ('185', '728013741413752947', 'user', 'User', '用户');
INSERT INTO `map_class_table` VALUES ('186', '728013758593622016', 'user_account', 'UserAccount', '用户账户');
INSERT INTO `map_class_table` VALUES ('187', '728013758593622024', 'user_account_rec', 'UserAccountRec', '用户账户流水');
INSERT INTO `map_class_table` VALUES ('188', '728013758593622037', 'user_activation_apply', 'UserActivationApply', '激活码申请表');
INSERT INTO `map_class_table` VALUES ('189', '728013758593622052', 'user_activation_code', 'UserActivationCode', '激活码(1 平台拿到用户的钱生成激活码，2平台审核商务中心申请并核账生成激活码)');
INSERT INTO `map_class_table` VALUES ('190', '728013758593622066', 'user_draw_rec', 'UserDrawRec', '用户账户提现表');
INSERT INTO `map_class_table` VALUES ('191', '728013758593622087', 'user_fund', 'UserFund', '用户基金账户');
INSERT INTO `map_class_table` VALUES ('192', '728013758593622095', 'user_fund_rec', 'UserFundRec', '用户基金账户流水');
INSERT INTO `map_class_table` VALUES ('193', '728013758593622108', 'user_parent', 'UserParent', '用户接点关系(用户A的出局,A的两个下属之一向上B填缺,B之外的C换主为B,为每一个关系变动者做好变动记录)');
INSERT INTO `map_class_table` VALUES ('194', '728013758593622114', 'user_parent_change_rec', 'UserParentChangeRec', '用户接点关系变动记录');

-- ----------------------------
-- Table structure for map_field_column
-- ----------------------------
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
) ENGINE=InnoDB AUTO_INCREMENT=1419 DEFAULT CHARSET=utf8mb4 COMMENT='字段属性映射信息';

-- ----------------------------
-- Records of map_field_column
-- ----------------------------
INSERT INTO `map_field_column` VALUES ('1200', '728013741413752832', '728013741413752833', 'id', 'id', 'bigint', 'java.lang.Long', '主键ID', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1201', '728013741413752832', '728013741413752834', 'code', 'code', 'char', 'java.lang.String', '管理员编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1202', '728013741413752832', '728013741413752835', 'name', 'name', 'varchar', 'java.lang.String', '管理员名称', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1203', '728013741413752832', '728013741413752836', 'login_account', 'loginAccount', 'varchar', 'java.lang.String', '登录账户(长度限制4-16)', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1204', '728013741413752832', '728013741413752837', 'pwd', 'pwd', 'char', 'java.lang.String', '密码(MD5加密32位)', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1205', '728013741413752832', '728013741413752838', 'phone', 'phone', 'char', 'java.lang.String', '手机号码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1206', '728013741413752832', '728013741413752839', 'idcard', 'idcard', 'char', 'java.lang.String', '身份证号', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1207', '728013741413752832', '728013741413752840', 'role', 'role', 'varchar', 'java.lang.String', '角色（NORMAL 普通，SUPER 高级）', '\'NORMAL\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1208', '728013741413752832', '728013741413752841', 'state', 'state', 'char', 'java.lang.String', '状态（OPEN 开启，DELETE 删除）', '\'OPEN\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1209', '728013741413752832', '728013741413752842', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '创建时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1210', '728013741413752832', '728013741413752843', 'update_time', 'updateTime', 'datetime', 'java.sql.Date', '更新时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1211', '728013741413752844', '728013741413752845', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1212', '728013741413752844', '728013741413752846', 'k', 'k', 'varchar', 'java.lang.String', '键', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1213', '728013741413752844', '728013741413752847', 'v', 'v', 'text', 'java.lang.Object', '值', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1214', '728013741413752844', '728013741413752848', 'remark', 'remark', 'varchar', 'java.lang.String', '备注', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1215', '728013741413752844', '728013741413752849', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '创建时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1216', '728013741413752844', '728013741413752850', 'update_time', 'updateTime', 'datetime', 'java.sql.Date', '修改时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1217', '728013741413752851', '728013741413752852', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1218', '728013741413752851', '728013741413752853', 'tc', 'tc', 'char', 'java.lang.String', '交易码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1219', '728013741413752851', '728013741413752854', 'record_code', 'recordCode', 'char', 'java.lang.String', '购买礼包编号', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1220', '728013741413752851', '728013741413752855', 'user_code', 'userCode', 'char', 'java.lang.String', '用户编码A|B', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1221', '728013741413752851', '728013741413752856', 'release_user_code', 'releaseUserCode', 'char', 'java.lang.String', '释放人编码（直接触发人:对A说是B的编码，对B说是C的编码）', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1222', '728013741413752851', '728013741413752857', 'indirect_user_code', 'indirectUserCode', 'char', 'java.lang.String', '间接释放人编码（第二层购买时产生：对A说是C的编码，对B说无编码）', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1223', '728013741413752851', '728013741413752858', 'remark', 'remark', 'varchar', 'java.lang.String', '释放说明（您A推广的B成功推广了C，为您释放16张代金券）', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1224', '728013741413752851', '728013741413752859', 'coupon_code', 'couponCode', 'char', 'java.lang.String', '代金券编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1225', '728013741413752851', '728013741413752860', 'num', 'num', 'smallint', 'java.lang.Short', '代金券期数', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1226', '728013741413752851', '728013741413752861', 'coupon_price', 'couponPrice', 'decimal', 'java.math.BigDecimal', '代金券面额', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1227', '728013741413752851', '728013741413752862', 'fund_rate', 'fundRate', 'smallint', 'java.lang.Short', '基金抽取比例(1700代表17%)', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1228', '728013741413752851', '728013741413752863', 'fund', 'fund', 'decimal', 'java.math.BigDecimal', '抽取基金', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1229', '728013741413752851', '728013741413752864', 'price', 'price', 'decimal', 'java.math.BigDecimal', '实际释放额', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1230', '728013741413752851', '728013741413752865', 'is_release', 'isRelease', 'char', 'java.lang.String', '状态(Y: 已释放,N: 未释放)', '\'N\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1231', '728013741413752851', '728013741413752866', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '创建时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1232', '728013741413752851', '728013741413752867', 'release_time', 'releaseTime', 'datetime', 'java.sql.Date', '释放时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1233', '728013741413752868', '728013741413752869', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1234', '728013741413752868', '728013741413752870', 'tc', 'tc', 'char', 'java.lang.String', '交易码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1235', '728013741413752868', '728013741413752871', 'record_code', 'recordCode', 'char', 'java.lang.String', '购买礼包编号', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1236', '728013741413752868', '728013741413752872', 'user_code', 'userCode', 'char', 'java.lang.String', '用户编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1237', '728013741413752868', '728013741413752873', 'release_user_code', 'releaseUserCode', 'char', 'java.lang.String', '释放人编码（直接触发人）', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1238', '728013741413752868', '728013741413752874', 'indirect_user_code', 'indirectUserCode', 'char', 'java.lang.String', '间接释放人编码（第二层购买时产生）', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1239', '728013741413752868', '728013741413752875', 'remark', 'remark', 'varchar', 'java.lang.String', '释放说明（您推广的B成功推广了C，为您释放16张代金券）', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1240', '728013741413752868', '728013741413752876', 'coupon_code', 'couponCode', 'char', 'java.lang.String', '代金券编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1241', '728013741413752868', '728013741413752877', 'num', 'num', 'smallint', 'java.lang.Short', '代金券期数', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1242', '728013741413752868', '728013741413752878', 'coupon_price', 'couponPrice', 'decimal', 'java.math.BigDecimal', '代金券面额', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1243', '728013741413752868', '728013741413752879', 'fund_rate', 'fundRate', 'smallint', 'java.lang.Short', '基金抽取比例(1700代表17%)', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1244', '728013741413752868', '728013741413752880', 'fund', 'fund', 'decimal', 'java.math.BigDecimal', '抽取基金', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1245', '728013741413752868', '728013741413752881', 'price', 'price', 'decimal', 'java.math.BigDecimal', '实际释放额', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1246', '728013741413752868', '728013741413752882', 'is_release', 'isRelease', 'char', 'java.lang.String', '状态(Y: 已释放,N: 未释放)', '\'Y\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1247', '728013741413752868', '728013741413752883', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '创建时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1248', '728013741413752868', '728013741413752884', 'release_time', 'releaseTime', 'datetime', 'java.sql.Date', '释放时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1249', '728013741413752885', '728013741413752886', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1250', '728013741413752885', '728013741413752887', 'balance', 'balance', 'decimal', 'java.math.BigDecimal', '账户余额', '0.00', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1251', '728013741413752885', '728013741413752888', 'income', 'income', 'decimal', 'java.math.BigDecimal', '总入账', '0.00', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1252', '728013741413752885', '728013741413752889', 'expenditure', 'expenditure', 'decimal', 'java.math.BigDecimal', '总出账', '0.00', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1253', '728013741413752885', '728013741413752890', 'vn', 'vn', 'bigint', 'java.lang.Long', '版本号', '1', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1254', '728013741413752885', '728013741413752891', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '创建时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1255', '728013741413752885', '728013741413752892', 'update_time', 'updateTime', 'datetime', 'java.sql.Date', '更新时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1256', '728013741413752893', '728013741413752894', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1257', '728013741413752893', '728013741413752895', 'sn', 'sn', 'char', 'java.lang.String', '流水码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1258', '728013741413752893', '728013741413752896', 'tc', 'tc', 'char', 'java.lang.String', '交易码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1259', '728013741413752893', '728013741413752897', 'user_code', 'userCode', 'char', 'java.lang.String', '用户编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1260', '728013741413752893', '728013741413752898', 'income', 'income', 'decimal', 'java.math.BigDecimal', '收入', '0.00', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1261', '728013741413752893', '728013741413752899', 'expenditure', 'expenditure', 'decimal', 'java.math.BigDecimal', '支出', '0.00', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1262', '728013741413752893', '728013741413752900', 'subject', 'subject', 'varchar', 'java.lang.String', '会计科目', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1263', '728013741413752893', '728013741413752901', 'subject_name', 'subjectName', 'varchar', 'java.lang.String', '会计科目名称', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1264', '728013741413752893', '728013741413752902', 'direction', 'direction', 'char', 'java.lang.String', '记录方向（SINGLE 单方记录；DOUBLE 双方记录）', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1265', '728013741413752893', '728013741413752903', 'pay_way', 'payWay', 'varchar', 'java.lang.String', '交易方式（REMIT 线下支付，TRANSFER 账户转账，ACCOUNT 账户支付 等枚举）', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1266', '728013741413752893', '728013741413752904', 'remark', 'remark', 'varchar', 'java.lang.String', '备注', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1267', '728013741413752893', '728013741413752905', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '创建时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1268', '728013741413752906', '728013741413752907', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1269', '728013741413752906', '728013741413752908', 'goods_code', 'goodsCode', 'char', 'java.lang.String', '商品编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1270', '728013741413752906', '728013741413752909', 'goods_name', 'goodsName', 'varchar', 'java.lang.String', '商品名称', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1271', '728013741413752906', '728013741413752910', 'goods_jingle', 'goodsJingle', 'varchar', 'java.lang.String', '商品广告词', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1272', '728013741413752906', '728013741413752911', 'goods_image', 'goodsImage', 'varchar', 'java.lang.String', '商品主图', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1273', '728013741413752906', '728013741413752912', 'price', 'price', 'decimal', 'java.math.BigDecimal', '价格', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1274', '728013741413752906', '728013741413752913', 'state', 'state', 'varchar', 'java.lang.String', '状态 DOWN下架，NORMAL正常', '\'NORMAL\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1275', '728013741413752906', '728013741413752914', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '商品添加时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1276', '728013741413752906', '728013741413752915', 'update_time', 'updateTime', 'datetime', 'java.sql.Date', '修改时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1277', '728013741413752916', '728013741413752917', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1278', '728013741413752916', '728013741413752918', 'record_code', 'recordCode', 'char', 'java.lang.String', '购买礼包编号', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1279', '728013741413752916', '728013741413752919', 'user_code', 'userCode', 'char', 'java.lang.String', '用户编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1280', '728013741413752916', '728013741413752920', 'goods_code', 'goodsCode', 'char', 'java.lang.String', '商品编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1281', '728013741413752916', '728013741413752921', 'goods_name', 'goodsName', 'varchar', 'java.lang.String', '商品名称', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1282', '728013741413752916', '728013741413752922', 'goods_image', 'goodsImage', 'varchar', 'java.lang.String', '商品主图', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1283', '728013741413752916', '728013741413752923', 'is_first_buy', 'isFirstBuy', 'char', 'java.lang.String', '是否首次购买（Y 是，N否）当为Y时，需要用户完善收货信息及平台发货', '\'N\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1284', '728013741413752916', '728013741413752924', 'is_delivery', 'isDelivery', 'char', 'java.lang.String', '是否已发货（Y 是，N 否）', '\'N\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1285', '728013741413752916', '728013741413752925', 'activation_code', 'activationCode', 'char', 'java.lang.String', '使用的激活码(is_first_buy=N时,此项为空)', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1286', '728013741413752916', '728013741413752926', 'name', 'name', 'char', 'java.lang.String', '收货人姓名', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1287', '728013741413752916', '728013741413752927', 'phone', 'phone', 'char', 'java.lang.String', '收货人电话', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1288', '728013741413752916', '728013741413752928', 'address', 'address', 'char', 'java.lang.String', '收货人地址', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1289', '728013741413752916', '728013741413752929', 'price', 'price', 'decimal', 'java.math.BigDecimal', '价格', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1290', '728013741413752916', '728013741413752930', 'state', 'state', 'varchar', 'java.lang.String', '释放状态(ING 释放中，OVER 释放完毕)', '\'ING\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1291', '728013741413752916', '728013741413752931', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '商品添加时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1292', '728013741413752916', '728013741413752932', 'update_time', 'updateTime', 'datetime', 'java.sql.Date', '修改时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1293', '728013741413752933', '728013741413752934', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1294', '728013741413752933', '728013741413752935', 'order_code', 'orderCode', 'char', 'java.lang.String', '订单号', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1295', '728013741413752933', '728013741413752936', 'login_account', 'loginAccount', 'varchar', 'java.lang.String', '登陆账户（长度4-16）', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1296', '728013741413752933', '728013741413752937', 'name', 'name', 'varchar', 'java.lang.String', '姓名', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1297', '728013741413752933', '728013741413752938', 'phone', 'phone', 'varchar', 'java.lang.String', '电话（仅限手机号）', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1298', '728013741413752933', '728013741413752939', 'user_code', 'userCode', 'char', 'java.lang.String', '用户编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1299', '728013741413752933', '728013741413752940', 'total_price', 'totalPrice', 'decimal', 'java.math.BigDecimal', '订单总额', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1300', '728013741413752933', '728013741413752941', 'brokerage', 'brokerage', 'decimal', 'java.math.BigDecimal', '订单手续费', '0.00', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1301', '728013741413752933', '728013741413752942', 'state', 'state', 'varchar', 'java.lang.String', '状态(CR 创建订单, DONE 处理完成, CANCLE 取消订单)', '\'CR\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1302', '728013741413752933', '728013741413752943', 'descs', 'descs', 'varchar', 'java.lang.String', '订单说明', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1303', '728013741413752933', '728013741413752944', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '申请时间（CR 时的时间）', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1304', '728013741413752933', '728013741413752945', 'cancle_time', 'cancleTime', 'datetime', 'java.sql.Date', '取消时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1305', '728013741413752933', '728013741413752946', 'done_time', 'doneTime', 'datetime', 'java.sql.Date', '完成时间（DONE‘FAIL 共用此时间）', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1306', '728013741413752947', '728013741413752948', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1307', '728013741413752947', '728013741413752949', 'user_code', 'userCode', 'char', 'java.lang.String', '用户编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1308', '728013741413752947', '728013741413752950', 'login_account', 'loginAccount', 'varchar', 'java.lang.String', '登陆账户（长度4-16）', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1309', '728013741413752947', '728013741413752951', 'pwd', 'pwd', 'char', 'java.lang.String', '登陆密码（长度6-20，大小写字母+数字，需要MD5加密32位）', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1310', '728013741413752947', '728013741413752952', 'name', 'name', 'varchar', 'java.lang.String', '姓名', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1311', '728013741413752947', '728013741413752953', 'phone', 'phone', 'varchar', 'java.lang.String', '电话（仅限手机号）', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1312', '728013741413752947', '728013741413752954', 'idcard', 'idcard', 'varchar', 'java.lang.String', '身份证', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1313', '728013741413752947', '728013741413752955', 'recommend_code', 'recommendCode', 'char', 'java.lang.String', '推荐人编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1314', '728013741413752947', '728013741413752956', 'recommend_login_account', 'recommendLoginAccount', 'varchar', 'java.lang.String', '推荐人账户', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1315', '728013741413752947', '728013741413752957', 'role', 'role', 'varchar', 'java.lang.String', '角色(CREATE:新注册, ACTIVATE:激活,OUT:出局)', '\'CREATE\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1316', '728013741413752947', '728013741413752958', 'state', 'state', 'varchar', 'java.lang.String', '用户状态（NORMAL 正常，ABNORMAL 不正常）', '\'NORMAL\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1317', '728013741413752947', '728013741413752959', 'business_state', 'businessState', 'varchar', 'java.lang.String', '商务中心状态(NO 未开通, APPLY 申请中, OPEN 已开通)', '\'NO\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1318', '728013741413752947', '728013741413752960', 'is_need_cycle', 'isNeedCycle', 'char', 'java.lang.String', '是否需复投(Y 需要，N 不需要)', '\'N\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1319', '728013741413752947', '728013741413752961', 'cycle_start_time', 'cycleStartTime', 'datetime', 'java.sql.Date', '复投开始时间:用户什么时候进入复投期的', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1320', '728013741413752947', '728013741413752962', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '创建时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1321', '728013741413752947', '728013741413752963', 'activate_time', 'activateTime', 'datetime', 'java.sql.Date', '激活时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1322', '728013741413752947', '728013741413752964', 'update_time', 'updateTime', 'datetime', 'java.sql.Date', '修改时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1323', '728013758593622016', '728013758593622017', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1324', '728013758593622016', '728013758593622018', 'user_code', 'userCode', 'char', 'java.lang.String', '用户编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1325', '728013758593622016', '728013758593622019', 'balance', 'balance', 'decimal', 'java.math.BigDecimal', '账户余额', '0.00', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1326', '728013758593622016', '728013758593622020', 'vn', 'vn', 'int', 'java.lang.Integer', '版本号', '1', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1327', '728013758593622016', '728013758593622021', 'state', 'state', 'varchar', 'java.lang.String', '账户状态(NORMAL 正常，LOCK 冻结)', '\'NORMAL\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1328', '728013758593622016', '728013758593622022', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '创建时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1329', '728013758593622016', '728013758593622023', 'update_time', 'updateTime', 'datetime', 'java.sql.Date', '更新时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1330', '728013758593622024', '728013758593622025', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1331', '728013758593622024', '728013758593622026', 'sn', 'sn', 'char', 'java.lang.String', '流水码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1332', '728013758593622024', '728013758593622027', 'tc', 'tc', 'char', 'java.lang.String', '交易码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1333', '728013758593622024', '728013758593622028', 'user_code', 'userCode', 'char', 'java.lang.String', '用户编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1334', '728013758593622024', '728013758593622029', 'income', 'income', 'decimal', 'java.math.BigDecimal', '收入', '0.00', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1335', '728013758593622024', '728013758593622030', 'expenditure', 'expenditure', 'decimal', 'java.math.BigDecimal', '支出', '0.00', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1336', '728013758593622024', '728013758593622031', 'subject', 'subject', 'varchar', 'java.lang.String', '会计科目', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1337', '728013758593622024', '728013758593622032', 'subject_name', 'subjectName', 'varchar', 'java.lang.String', '会计科目名称', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1338', '728013758593622024', '728013758593622033', 'direction', 'direction', 'char', 'java.lang.String', '记录方向（SINGLE 单方记录；DOUBLE 双方记录）', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1339', '728013758593622024', '728013758593622034', 'pay_way', 'payWay', 'varchar', 'java.lang.String', '交易方式（REMIT 线下支付，TRANSFER 账户转账，ACCOUNT 账户支付 等枚举）', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1340', '728013758593622024', '728013758593622035', 'remark', 'remark', 'varchar', 'java.lang.String', '备注', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1341', '728013758593622024', '728013758593622036', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '创建时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1342', '728013758593622037', '728013758593622038', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1343', '728013758593622037', '728013758593622039', 'apply_code', 'applyCode', 'char', 'java.lang.String', '申请编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1344', '728013758593622037', '728013758593622040', 'tc', 'tc', 'char', 'java.lang.String', '交易码(审核发放激活码时，回填交易码)', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1345', '728013758593622037', '728013758593622041', 'user_code', 'userCode', 'char', 'java.lang.String', '用户编码(平台生成时，此为空)', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1346', '728013758593622037', '728013758593622042', 'price', 'price', 'decimal', 'java.math.BigDecimal', '激活码单价', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1347', '728013758593622037', '728013758593622043', 'num', 'num', 'smallint', 'java.lang.Short', '申请数量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1348', '728013758593622037', '728013758593622044', 'total_price', 'totalPrice', 'decimal', 'java.math.BigDecimal', '申请总价（折扣之后的总价）', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1349', '728013758593622037', '728013758593622045', 'rate_price', 'ratePrice', 'decimal', 'java.math.BigDecimal', '折后总价', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1350', '728013758593622037', '728013758593622046', 'rate', 'rate', 'smallint', 'java.lang.Short', '折扣(1700代表17%)', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1351', '728013758593622037', '728013758593622047', 'state', 'state', 'varchar', 'java.lang.String', '申请状态(APPLY 申请中, PASS 已发放)', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1352', '728013758593622037', '728013758593622048', 'admin_code', 'adminCode', 'char', 'java.lang.String', '审核管理员编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1353', '728013758593622037', '728013758593622049', 'admin_name', 'adminName', 'varchar', 'java.lang.String', '审核管理员名称', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1354', '728013758593622037', '728013758593622050', 'apply_time', 'applyTime', 'datetime', 'java.sql.Date', '申请时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1355', '728013758593622037', '728013758593622051', 'audit_time', 'auditTime', 'datetime', 'java.sql.Date', '审核时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1356', '728013758593622052', '728013758593622053', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1357', '728013758593622052', '728013758593622054', 'tc', 'tc', 'char', 'java.lang.String', '交易码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1358', '728013758593622052', '728013758593622055', 'apply_code', 'applyCode', 'char', 'java.lang.String', '申请编码(平台生成时，此为空)', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1359', '728013758593622052', '728013758593622056', 'user_code', 'userCode', 'char', 'java.lang.String', '用户编码(平台生成时，此为空)', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1360', '728013758593622052', '728013758593622057', 'activation_code', 'activationCode', 'char', 'java.lang.String', '激活码，此码唯一(仅供用户注册使用)', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1361', '728013758593622052', '728013758593622058', 'is_activate', 'isActivate', 'char', 'java.lang.String', '是否被激活(Y 是，N 否)', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1362', '728013758593622052', '728013758593622059', 'activate_user_code', 'activateUserCode', 'char', 'java.lang.String', '激活用户编码(平台生成时，此为空)', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1363', '728013758593622052', '728013758593622060', 'login_account', 'loginAccount', 'varchar', 'java.lang.String', '激活用户账户(平台生成时，此为空)', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1364', '728013758593622052', '728013758593622061', 'price', 'price', 'decimal', 'java.math.BigDecimal', '单价', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1365', '728013758593622052', '728013758593622062', 'admin_code', 'adminCode', 'char', 'java.lang.String', '发放管理员编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1366', '728013758593622052', '728013758593622063', 'admin_name', 'adminName', 'varchar', 'java.lang.String', '发放管理员名称', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1367', '728013758593622052', '728013758593622064', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '发放时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1368', '728013758593622052', '728013758593622065', 'activate_time', 'activateTime', 'datetime', 'java.sql.Date', '激活时间(平台生成时，此为空)', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1369', '728013758593622066', '728013758593622067', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1370', '728013758593622066', '728013758593622068', 'tc', 'tc', 'char', 'java.lang.String', '交易码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1371', '728013758593622066', '728013758593622069', 'user_code', 'userCode', 'char', 'java.lang.String', '用户编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1372', '728013758593622066', '728013758593622070', 'draw_money', 'drawMoney', 'decimal', 'java.math.BigDecimal', '提现金额', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1373', '728013758593622066', '728013758593622071', 'brokerage', 'brokerage', 'decimal', 'java.math.BigDecimal', '提现手续费', '0.00', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1374', '728013758593622066', '728013758593622072', 'really_draw', 'reallyDraw', 'decimal', 'java.math.BigDecimal', '实际到账', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1375', '728013758593622066', '728013758593622073', 'acct', 'acct', 'varchar', 'java.lang.String', '用户收款银行卡号', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1376', '728013758593622066', '728013758593622074', 'bank', 'bank', 'varchar', 'java.lang.String', '收款账号开户行', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1377', '728013758593622066', '728013758593622075', 'bacct_name', 'bacctName', 'varchar', 'java.lang.String', '收款姓名', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1378', '728013758593622066', '728013758593622076', 'pay_way', 'payWay', 'varchar', 'java.lang.String', '交易方式（REMIT 银行转账，CASH 现金 枚举）', '\'REMIT\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1379', '728013758593622066', '728013758593622077', 'pay_bank', 'payBank', 'varchar', 'java.lang.String', '平台汇款账号开户行', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1380', '728013758593622066', '728013758593622078', 'pay_acct', 'payAcct', 'varchar', 'java.lang.String', '平台汇款账号', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1381', '728013758593622066', '728013758593622079', 'pay_bacct_name', 'payBacctName', 'varchar', 'java.lang.String', '平台汇款人（如果是银行转账REMIT时，此项必填）', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1382', '728013758593622066', '728013758593622080', 'voucher_url', 'voucherUrl', 'varchar', 'java.lang.String', '平台汇款凭证(记录上传的转账凭证图片，回填)', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1383', '728013758593622066', '728013758593622081', 'remark', 'remark', 'varchar', 'java.lang.String', '平台汇款备注', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1384', '728013758593622066', '728013758593622082', 'state', 'state', 'varchar', 'java.lang.String', '状态(CR 提交申请, DEAL 提现处理中, DONE 提现到账, FAIL 提现失败)', '\'CR\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1385', '728013758593622066', '728013758593622083', 'fail_reason', 'failReason', 'varchar', 'java.lang.String', '提现失败原因', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1386', '728013758593622066', '728013758593622084', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '申请时间（CR 时的时间）', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1387', '728013758593622066', '728013758593622085', 'deal_time', 'dealTime', 'datetime', 'java.sql.Date', '处理时间（DEAL 时的时间）', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1388', '728013758593622066', '728013758593622086', 'done_time', 'doneTime', 'datetime', 'java.sql.Date', '完成时间（DONE‘FAIL 共用此时间）', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1389', '728013758593622087', '728013758593622088', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1390', '728013758593622087', '728013758593622089', 'user_code', 'userCode', 'char', 'java.lang.String', '用户编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1391', '728013758593622087', '728013758593622090', 'balance', 'balance', 'decimal', 'java.math.BigDecimal', '账户余额', '0.00', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1392', '728013758593622087', '728013758593622091', 'vn', 'vn', 'int', 'java.lang.Integer', '版本号', '1', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1393', '728013758593622087', '728013758593622092', 'state', 'state', 'varchar', 'java.lang.String', '账户状态(NORMAL 正常，LOCK 冻结)', '\'NORMAL\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1394', '728013758593622087', '728013758593622093', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '创建时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1395', '728013758593622087', '728013758593622094', 'update_time', 'updateTime', 'datetime', 'java.sql.Date', '更新时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1396', '728013758593622095', '728013758593622096', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1397', '728013758593622095', '728013758593622097', 'sn', 'sn', 'char', 'java.lang.String', '流水码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1398', '728013758593622095', '728013758593622098', 'tc', 'tc', 'char', 'java.lang.String', '交易码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1399', '728013758593622095', '728013758593622099', 'user_code', 'userCode', 'char', 'java.lang.String', '用户编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1400', '728013758593622095', '728013758593622100', 'income', 'income', 'decimal', 'java.math.BigDecimal', '收入', '0.00', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1401', '728013758593622095', '728013758593622101', 'expenditure', 'expenditure', 'decimal', 'java.math.BigDecimal', '支出', '0.00', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1402', '728013758593622095', '728013758593622102', 'subject', 'subject', 'varchar', 'java.lang.String', '会计科目', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1403', '728013758593622095', '728013758593622103', 'subject_name', 'subjectName', 'varchar', 'java.lang.String', '会计科目名称', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1404', '728013758593622095', '728013758593622104', 'direction', 'direction', 'char', 'java.lang.String', '记录方向（SINGLE 单方记录；DOUBLE 双方记录）', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1405', '728013758593622095', '728013758593622105', 'pay_way', 'payWay', 'varchar', 'java.lang.String', '交易方式（REMIT 线下支付，TRANSFER 账户转账，ACCOUNT 账户支付 等枚举）', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1406', '728013758593622095', '728013758593622106', 'remark', 'remark', 'varchar', 'java.lang.String', '备注', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1407', '728013758593622095', '728013758593622107', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '创建时间', null, 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1408', '728013758593622108', '728013758593622109', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1409', '728013758593622108', '728013758593622110', 'user_code', 'userCode', 'char', 'java.lang.String', '用户编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1410', '728013758593622108', '728013758593622111', 'parent_code', 'parentCode', 'char', 'java.lang.String', '接点人编码(同一个接点人，下属仅能绑定两人)', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1411', '728013758593622108', '728013758593622112', 'login_account', 'loginAccount', 'varchar', 'java.lang.String', '接点人账户', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1412', '728013758593622108', '728013758593622113', 'activate_time', 'activateTime', 'datetime', 'java.sql.Date', '激活时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('1413', '728013758593622114', '728013758593622115', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1414', '728013758593622114', '728013758593622116', 'user_code', 'userCode', 'char', 'java.lang.String', '用户编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1415', '728013758593622114', '728013758593622117', 'parent_code_old', 'parentCodeOld', 'char', 'java.lang.String', '老推荐人编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1416', '728013758593622114', '728013758593622118', 'parent_code_new', 'parentCodeNew', 'char', 'java.lang.String', '新推荐人编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1417', '728013758593622114', '728013758593622119', 'remark', 'remark', 'varchar', 'java.lang.String', '变动说明', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('1418', '728013758593622114', '728013758593622120', 'create_time', 'createTime', 'datetime', 'java.sql.Date', '创建时间', null, 'N', 'Y', 'N');

-- ----------------------------
-- Table structure for map_relationship
-- ----------------------------
DROP TABLE IF EXISTS `map_relationship`;
CREATE TABLE `map_relationship` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '关系编码',
  `mapClassTableCode` varchar(64) DEFAULT NULL COMMENT '映射编码',
  `isOneToOne` varchar(1) DEFAULT 'N' COMMENT '是否一对一 Y N',
  `isOneToMany` varchar(1) DEFAULT 'N' COMMENT '是否一对多Y N',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模型关系';

-- ----------------------------
-- Records of map_relationship
-- ----------------------------

-- ----------------------------
-- Table structure for module
-- ----------------------------
DROP TABLE IF EXISTS `module`;
CREATE TABLE `module` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '模块编码',
  `name` varchar(64) DEFAULT NULL COMMENT '模块名',
  `description` varchar(256) DEFAULT NULL COMMENT '模块说明',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='第三方模块池';

-- ----------------------------
-- Records of module
-- ----------------------------
INSERT INTO `module` VALUES ('1', '679039706804969422', '工具', '工具');

-- ----------------------------
-- Table structure for module_file
-- ----------------------------
DROP TABLE IF EXISTS `module_file`;
CREATE TABLE `module_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `moudleCode` varchar(64) DEFAULT NULL COMMENT '模块编码',
  `path` varchar(256) DEFAULT NULL COMMENT '文件路径',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模块文件信息';

-- ----------------------------
-- Records of module_file
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
  `buildNumber` int(11) DEFAULT NULL COMMENT '生成次数',
  `isRepository` varchar(1) DEFAULT 'N' COMMENT '是否仓库管理',
  `isParseTable` varchar(1) DEFAULT 'N' COMMENT '是否已经解析表',
  `isParseClass` varchar(1) DEFAULT 'N' COMMENT '是否已经解析类',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `accountCode` varchar(64) DEFAULT NULL COMMENT '账户编码',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8mb4 COMMENT='项目信息';

-- ----------------------------
-- Records of project
-- ----------------------------
INSERT INTO `project` VALUES ('109', '728007402042023936', '环环送', '环环送', 'cycle', 'Mysql', 'java', 'Enable', '仅供学习参考', 'AI', 'AI', 'com.cycle', null, 'DownloadUrl', '1', 'N', 'Y', 'Y', '2018-01-23 11:00:08', '2018-01-23 11:06:18', null);

-- ----------------------------
-- Table structure for project_code_catalog
-- ----------------------------
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

-- ----------------------------
-- Records of project_code_catalog
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
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COMMENT='项目应用技术';

-- ----------------------------
-- Records of project_framwork
-- ----------------------------
INSERT INTO `project_framwork` VALUES ('48', '123456', '728007402042023936');

-- ----------------------------
-- Table structure for project_job
-- ----------------------------
DROP TABLE IF EXISTS `project_job`;
CREATE TABLE `project_job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  `code` varchar(64) NOT NULL COMMENT '任务编码',
  `number` varchar(64) DEFAULT NULL COMMENT '第多少次执行',
  `state` varchar(16) DEFAULT NULL COMMENT '任务状态: 创建[Create] , 执行中[Executing], 完成[C ompleted] ,失败[Error] 警告 [Waring]',
  `createTime` datetime DEFAULT NULL COMMENT '执行任务时间',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='任务';

-- ----------------------------
-- Records of project_job
-- ----------------------------
INSERT INTO `project_job` VALUES ('7', '728007402042023936', '728014548867604480', '0', 'Create', '2018-01-23 11:07:04');

-- ----------------------------
-- Table structure for project_job_logs
-- ----------------------------
DROP TABLE IF EXISTS `project_job_logs`;
CREATE TABLE `project_job_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) DEFAULT NULL COMMENT '任务编码',
  `log` varchar(256) DEFAULT NULL COMMENT '日志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务日志';

-- ----------------------------
-- Records of project_job_logs
-- ----------------------------

-- ----------------------------
-- Table structure for project_map
-- ----------------------------
DROP TABLE IF EXISTS `project_map`;
CREATE TABLE `project_map` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  `mapClassTableCode` varchar(64) DEFAULT NULL COMMENT '字段属性映射编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=196 DEFAULT CHARSET=utf8mb4 COMMENT='项目数据表';

-- ----------------------------
-- Records of project_map
-- ----------------------------
INSERT INTO `project_map` VALUES ('30', '679039706804969472', '688437662185226240');
INSERT INTO `project_map` VALUES ('31', '679039706804969472', '688437662185226250');
INSERT INTO `project_map` VALUES ('32', '679039706804969472', '688437662185226257');
INSERT INTO `project_map` VALUES ('33', '679039706804969472', '688437662185226263');
INSERT INTO `project_map` VALUES ('34', '679039706804969472', '688437662185226274');
INSERT INTO `project_map` VALUES ('35', '679039706804969472', '688437662185226278');
INSERT INTO `project_map` VALUES ('36', '679039706804969472', '688437662185226284');
INSERT INTO `project_map` VALUES ('37', '679039706804969472', '688437662185226289');
INSERT INTO `project_map` VALUES ('38', '679039706804969472', '688437662185226296');
INSERT INTO `project_map` VALUES ('39', '679039706804969472', '688437662185226302');
INSERT INTO `project_map` VALUES ('40', '679039706804969472', '688437662185226320');
INSERT INTO `project_map` VALUES ('41', '679039706804969472', '688437662185226324');
INSERT INTO `project_map` VALUES ('42', '679039706804969472', '688437662185226332');
INSERT INTO `project_map` VALUES ('43', '679039706804969472', '688437662185226336');
INSERT INTO `project_map` VALUES ('44', '679039706804969472', '688437662185226343');
INSERT INTO `project_map` VALUES ('45', '679039706804969472', '688437662185226349');
INSERT INTO `project_map` VALUES ('46', '679039706804969472', '688437662185226353');
INSERT INTO `project_map` VALUES ('47', '679039706804969472', '688437662185226363');
INSERT INTO `project_map` VALUES ('48', '679039706804969472', '688437662185226367');
INSERT INTO `project_map` VALUES ('49', '679039706804969472', '688437662185226371');
INSERT INTO `project_map` VALUES ('50', '679039706804969472', '688437662185226376');
INSERT INTO `project_map` VALUES ('51', '679039706804969472', '688437662185226382');
INSERT INTO `project_map` VALUES ('52', '679039706804969472', '688437662185226387');
INSERT INTO `project_map` VALUES ('53', '679039706804969472', '688437662185226392');
INSERT INTO `project_map` VALUES ('54', '679039706804969472', '688437662185226396');
INSERT INTO `project_map` VALUES ('55', '679039706804969472', '688437662185226403');
INSERT INTO `project_map` VALUES ('56', '679039706804969472', '688437662185226411');
INSERT INTO `project_map` VALUES ('57', '726749045343739904', '726759456344465408');
INSERT INTO `project_map` VALUES ('58', '726749045343739904', '726759456344465418');
INSERT INTO `project_map` VALUES ('59', '726749045343739904', '726759456344465425');
INSERT INTO `project_map` VALUES ('60', '726749045343739904', '726759456344465431');
INSERT INTO `project_map` VALUES ('61', '726749045343739904', '726759456344465442');
INSERT INTO `project_map` VALUES ('62', '726749045343739904', '726759456344465446');
INSERT INTO `project_map` VALUES ('63', '726749045343739904', '726759456344465452');
INSERT INTO `project_map` VALUES ('64', '726749045343739904', '726759456344465457');
INSERT INTO `project_map` VALUES ('65', '726749045343739904', '726759456344465464');
INSERT INTO `project_map` VALUES ('66', '726749045343739904', '726759456344465470');
INSERT INTO `project_map` VALUES ('67', '726749045343739904', '726759456344465488');
INSERT INTO `project_map` VALUES ('68', '726749045343739904', '726759456344465492');
INSERT INTO `project_map` VALUES ('69', '726749045343739904', '726759456344465500');
INSERT INTO `project_map` VALUES ('70', '726749045343739904', '726759456344465504');
INSERT INTO `project_map` VALUES ('71', '726749045343739904', '726759456344465511');
INSERT INTO `project_map` VALUES ('72', '726749045343739904', '726759456344465517');
INSERT INTO `project_map` VALUES ('73', '726749045343739904', '726759456344465521');
INSERT INTO `project_map` VALUES ('74', '726749045343739904', '726759456344465531');
INSERT INTO `project_map` VALUES ('75', '726749045343739904', '726759456344465535');
INSERT INTO `project_map` VALUES ('76', '726749045343739904', '726759456344465539');
INSERT INTO `project_map` VALUES ('77', '726749045343739904', '726759456344465544');
INSERT INTO `project_map` VALUES ('78', '726749045343739904', '726759456344465550');
INSERT INTO `project_map` VALUES ('79', '726749045343739904', '726759456344465555');
INSERT INTO `project_map` VALUES ('80', '726749045343739904', '726759456344465560');
INSERT INTO `project_map` VALUES ('81', '726749045343739904', '726759456344465564');
INSERT INTO `project_map` VALUES ('82', '726749045343739904', '726759456344465571');
INSERT INTO `project_map` VALUES ('83', '726749045343739904', '726759456344465579');
INSERT INTO `project_map` VALUES ('84', '726773595376803840', '726774162312486913');
INSERT INTO `project_map` VALUES ('85', '726773595376803840', '726774179492356105');
INSERT INTO `project_map` VALUES ('86', '726773595376803840', '726774179492356112');
INSERT INTO `project_map` VALUES ('87', '726773595376803840', '726774179492356118');
INSERT INTO `project_map` VALUES ('88', '726773595376803840', '726774179492356129');
INSERT INTO `project_map` VALUES ('89', '726773595376803840', '726774179492356133');
INSERT INTO `project_map` VALUES ('90', '726773595376803840', '726774179492356139');
INSERT INTO `project_map` VALUES ('91', '726773595376803840', '726774179492356144');
INSERT INTO `project_map` VALUES ('92', '726773595376803840', '726774179492356151');
INSERT INTO `project_map` VALUES ('93', '726773595376803840', '726774179492356157');
INSERT INTO `project_map` VALUES ('94', '726773595376803840', '726774179492356175');
INSERT INTO `project_map` VALUES ('95', '726773595376803840', '726774179492356179');
INSERT INTO `project_map` VALUES ('96', '726773595376803840', '726774179492356187');
INSERT INTO `project_map` VALUES ('97', '726773595376803840', '726774179492356191');
INSERT INTO `project_map` VALUES ('98', '726773595376803840', '726774179492356198');
INSERT INTO `project_map` VALUES ('99', '726773595376803840', '726774179492356204');
INSERT INTO `project_map` VALUES ('100', '726773595376803840', '726774179492356208');
INSERT INTO `project_map` VALUES ('101', '726773595376803840', '726774179492356218');
INSERT INTO `project_map` VALUES ('102', '726773595376803840', '726774179492356222');
INSERT INTO `project_map` VALUES ('103', '726773595376803840', '726774179492356226');
INSERT INTO `project_map` VALUES ('104', '726773595376803840', '726774179492356231');
INSERT INTO `project_map` VALUES ('105', '726773595376803840', '726774179492356237');
INSERT INTO `project_map` VALUES ('106', '726773595376803840', '726774179492356242');
INSERT INTO `project_map` VALUES ('107', '726773595376803840', '726774179492356247');
INSERT INTO `project_map` VALUES ('108', '726773595376803840', '726774179492356251');
INSERT INTO `project_map` VALUES ('109', '726773595376803840', '726774179492356258');
INSERT INTO `project_map` VALUES ('110', '726773595376803840', '726774179492356266');
INSERT INTO `project_map` VALUES ('111', '727028630534840320', '727028974132224000');
INSERT INTO `project_map` VALUES ('112', '727028630534840320', '727028974132224010');
INSERT INTO `project_map` VALUES ('113', '727028630534840320', '727028974132224017');
INSERT INTO `project_map` VALUES ('114', '727028630534840320', '727028974132224023');
INSERT INTO `project_map` VALUES ('115', '727028630534840320', '727028974132224034');
INSERT INTO `project_map` VALUES ('116', '727028630534840320', '727028974132224038');
INSERT INTO `project_map` VALUES ('117', '727028630534840320', '727028974132224044');
INSERT INTO `project_map` VALUES ('118', '727028630534840320', '727028974132224049');
INSERT INTO `project_map` VALUES ('119', '727028630534840320', '727028974132224056');
INSERT INTO `project_map` VALUES ('120', '727028630534840320', '727028974132224062');
INSERT INTO `project_map` VALUES ('121', '727028630534840320', '727028974132224080');
INSERT INTO `project_map` VALUES ('122', '727028630534840320', '727028974132224084');
INSERT INTO `project_map` VALUES ('123', '727028630534840320', '727028974132224092');
INSERT INTO `project_map` VALUES ('124', '727028630534840320', '727028974132224096');
INSERT INTO `project_map` VALUES ('125', '727028630534840320', '727028974132224103');
INSERT INTO `project_map` VALUES ('126', '727028630534840320', '727028974132224109');
INSERT INTO `project_map` VALUES ('127', '727028630534840320', '727028974132224113');
INSERT INTO `project_map` VALUES ('128', '727028630534840320', '727028974132224123');
INSERT INTO `project_map` VALUES ('129', '727028630534840320', '727028974132224127');
INSERT INTO `project_map` VALUES ('130', '727028630534840320', '727028974132224131');
INSERT INTO `project_map` VALUES ('131', '727028630534840320', '727028974132224136');
INSERT INTO `project_map` VALUES ('132', '727028630534840320', '727028991312093184');
INSERT INTO `project_map` VALUES ('133', '727028630534840320', '727028991312093189');
INSERT INTO `project_map` VALUES ('134', '727028630534840320', '727028991312093194');
INSERT INTO `project_map` VALUES ('135', '727028630534840320', '727028991312093198');
INSERT INTO `project_map` VALUES ('136', '727028630534840320', '727028991312093205');
INSERT INTO `project_map` VALUES ('137', '727028630534840320', '727028991312093213');
INSERT INTO `project_map` VALUES ('138', '727118223552634880', '727118326631849985');
INSERT INTO `project_map` VALUES ('139', '727118223552634880', '727118326631849997');
INSERT INTO `project_map` VALUES ('140', '727118223552634880', '727118326631850004');
INSERT INTO `project_map` VALUES ('141', '727118223552634880', '727118326631850021');
INSERT INTO `project_map` VALUES ('142', '727118223552634880', '727118326631850038');
INSERT INTO `project_map` VALUES ('143', '727118223552634880', '727118326631850046');
INSERT INTO `project_map` VALUES ('144', '727118223552634880', '727118326631850059');
INSERT INTO `project_map` VALUES ('145', '727118223552634880', '727118326631850069');
INSERT INTO `project_map` VALUES ('146', '727118223552634880', '727118326631850086');
INSERT INTO `project_map` VALUES ('147', '727118223552634880', '727118343811719168');
INSERT INTO `project_map` VALUES ('148', '727118223552634880', '727118343811719186');
INSERT INTO `project_map` VALUES ('149', '727118223552634880', '727118343811719194');
INSERT INTO `project_map` VALUES ('150', '727118223552634880', '727118343811719207');
INSERT INTO `project_map` VALUES ('151', '727118223552634880', '727118343811719222');
INSERT INTO `project_map` VALUES ('152', '727118223552634880', '727118343811719236');
INSERT INTO `project_map` VALUES ('153', '727118223552634880', '727118343811719257');
INSERT INTO `project_map` VALUES ('154', '727118223552634880', '727118343811719265');
INSERT INTO `project_map` VALUES ('155', '727118223552634880', '727118343811719278');
INSERT INTO `project_map` VALUES ('156', '727118223552634880', '727118343811719284');
INSERT INTO `project_map` VALUES ('157', '727887039878496256', '727887950411563009');
INSERT INTO `project_map` VALUES ('158', '727891145867231244', '727891712802914304');
INSERT INTO `project_map` VALUES ('159', '727891145867231244', '727894684920283136');
INSERT INTO `project_map` VALUES ('160', '727891145867231244', '727894891078713349');
INSERT INTO `project_map` VALUES ('161', '727891145867231244', '727894891078713366');
INSERT INTO `project_map` VALUES ('162', '727891145867231244', '727894891078713383');
INSERT INTO `project_map` VALUES ('163', '727891145867231244', '727894891078713391');
INSERT INTO `project_map` VALUES ('164', '727891145867231244', '727894891078713404');
INSERT INTO `project_map` VALUES ('165', '727891145867231244', '727894891078713414');
INSERT INTO `project_map` VALUES ('166', '727891145867231244', '727894891078713431');
INSERT INTO `project_map` VALUES ('167', '727891145867231244', '727894891078713445');
INSERT INTO `project_map` VALUES ('168', '727891145867231244', '727894908258582528');
INSERT INTO `project_map` VALUES ('169', '727891145867231244', '727894908258582536');
INSERT INTO `project_map` VALUES ('170', '727891145867231244', '727894908258582549');
INSERT INTO `project_map` VALUES ('171', '727891145867231244', '727894908258582564');
INSERT INTO `project_map` VALUES ('172', '727891145867231244', '727894908258582578');
INSERT INTO `project_map` VALUES ('173', '727891145867231244', '727894908258582599');
INSERT INTO `project_map` VALUES ('174', '727891145867231244', '727894908258582607');
INSERT INTO `project_map` VALUES ('175', '727891145867231244', '727894908258582620');
INSERT INTO `project_map` VALUES ('176', '727891145867231244', '727894908258582626');
INSERT INTO `project_map` VALUES ('177', '728007402042023936', '728013741413752832');
INSERT INTO `project_map` VALUES ('178', '728007402042023936', '728013741413752844');
INSERT INTO `project_map` VALUES ('179', '728007402042023936', '728013741413752851');
INSERT INTO `project_map` VALUES ('180', '728007402042023936', '728013741413752868');
INSERT INTO `project_map` VALUES ('181', '728007402042023936', '728013741413752885');
INSERT INTO `project_map` VALUES ('182', '728007402042023936', '728013741413752893');
INSERT INTO `project_map` VALUES ('183', '728007402042023936', '728013741413752906');
INSERT INTO `project_map` VALUES ('184', '728007402042023936', '728013741413752916');
INSERT INTO `project_map` VALUES ('185', '728007402042023936', '728013741413752933');
INSERT INTO `project_map` VALUES ('186', '728007402042023936', '728013741413752947');
INSERT INTO `project_map` VALUES ('187', '728007402042023936', '728013758593622016');
INSERT INTO `project_map` VALUES ('188', '728007402042023936', '728013758593622024');
INSERT INTO `project_map` VALUES ('189', '728007402042023936', '728013758593622037');
INSERT INTO `project_map` VALUES ('190', '728007402042023936', '728013758593622052');
INSERT INTO `project_map` VALUES ('191', '728007402042023936', '728013758593622066');
INSERT INTO `project_map` VALUES ('192', '728007402042023936', '728013758593622087');
INSERT INTO `project_map` VALUES ('193', '728007402042023936', '728013758593622095');
INSERT INTO `project_map` VALUES ('194', '728007402042023936', '728013758593622108');
INSERT INTO `project_map` VALUES ('195', '728007402042023936', '728013758593622114');

-- ----------------------------
-- Table structure for project_module
-- ----------------------------
DROP TABLE IF EXISTS `project_module`;
CREATE TABLE `project_module` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  `moduleCode` varchar(64) DEFAULT '' COMMENT '模块编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='项目选择模块';

-- ----------------------------
-- Records of project_module
-- ----------------------------
INSERT INTO `project_module` VALUES ('1', '679039706804969472', '679039706804969422');

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
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COMMENT='版本控制管理';

-- ----------------------------
-- Records of project_repository_account
-- ----------------------------
INSERT INTO `project_repository_account` VALUES ('1', '123456', '679039706804969472', 'hegaoye@qq.com', 'h@gaoy@6258371', 'https://gitee.com/helixin/AiCodeTest.git', 'https://gitee.com/helixin/AiCodeTest.git', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('2', '720601899171119104', '132', '123', '123', '231', '123', 'Enable', 'Mysql');
INSERT INTO `project_repository_account` VALUES ('3', '720936064806617088', '', '12323', '123123', '1233', '123', 'Enable', 'Mysql');
INSERT INTO `project_repository_account` VALUES ('4', '720937336116936704', '项目编码', '帐户名', '密码', '仓库地址', '仓库说明', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('5', '720942919574421504', '23', '1231133', '123', '123', '1321', 'Enable', 'Mysql');
INSERT INTO `project_repository_account` VALUES ('6', '720942953934159872', '23', '1231133', '123', '123', '1321', 'Enable', 'Mysql');
INSERT INTO `project_repository_account` VALUES ('7', '720943125732851712', '23', '1231133', '123', '123', '1321', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('8', '720948021995569152', '123', '13213', '231', '123', '123', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('9', '720958639154724864', '1231321', '213', '23132', '13213', '123132', 'Enable', 'Svn');
INSERT INTO `project_repository_account` VALUES ('10', '720960906897457152', '312', '123', '123', '123', '123', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('11', '720965047245930496', '231', '12313', '123', '123', '1231', 'Enable', 'Svn');
INSERT INTO `project_repository_account` VALUES ('12', '722343302251225088', '23', '12', '13', '123', '31', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('13', '722352648100061184', '131', '司法所反馈', '31213', '31', '123', 'Enable', 'Svn');
INSERT INTO `project_repository_account` VALUES ('14', '722355207900569600', '31', '31561', '15', '2213', '12312', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('15', '722357149225787392', '3', '13131', '3215.2', '313', '21321', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('19', '722374037037195264', '722342735315542016', '3113', '321', '3', '13', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('20', '722374037037195264', '722342735315542016', '3113', '321', '33', '132', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('21', '722374037037195264', '722342735315542016', '333', '321', '3', '13', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('22', '722374037037195264', '722342735315542016', '3113', '321', '3', '13', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('23', '722469093253390336', '722465021624393728', 'qps', '1231', '131', '1313', 'Enable', 'Svn');
INSERT INTO `project_repository_account` VALUES ('24', '722469093253390336', '722465021624393728', 'qps', '1231', '131', '1313', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('25', '722469093253390336', '722465021624393728', 'qps', '1231', '131', '1313', 'Enable', 'Svn');
INSERT INTO `project_repository_account` VALUES ('26', '722374037037195264', '722342735315542016', '3113', '321', '3', '13', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('27', '726750883589742592', '726749045343739904', 'hegaoye@qq.com', 'h@gaoy@6258371', 'https://gitee.com/helixin/AiCodeTest.git', 'aitest', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('28', '726768407056310272', '726680824083210240', 'hegaoye@qq.com', 'h@gaoy@6258371', 'https://gitee.com/helixin/AiCodeTest.git', 'aicode test', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('29', '726768407056310272', '726680824083210240', 'hegaoye@qq.com', 'h@gaoy@6258371', 'https://gitee.com/helixin/AiCodeTest.git', 'aicode test', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('30', '726776086457835520', '726773595376803840', 'hegaoye@qq.com', 'h@gaoy@6258371', 'https://gitee.com/helixin/AiCodeTest.git', '测试代码保存地址', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('31', '727029369269215232', '727028630534840320', 'hegaoye@qq.com', 'h@gaoy@6258371', 'https://gitee.com/helixin/AiCodeTest.git', 'https://gitee.com/helixin/AiCodeTest.git', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('32', '727120078978506752', '727118223552634880', 'zdfsir@163.com', 'Zdf205@#!', 'https://gitee.com/zdfwlxfz_dd/cycle-demo.git', 'https://gitee.com/zdfwlxfz_dd/cycle-demo.git', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('33', '727120078978506752', '727118223552634880', 'zdfsir@163.com', 'Zdf205@#!', 'https://gitee.com/zdfwlxfz_dd/cycle-demo.git', 'https://gitee.com/zdfwlxfz_dd/cycle-demo.git', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('34', '728014428608520192', '728007402042023936', 'zdfsir@163.com', 'Zdf205@#!', 'https://gitee.com/zdfwlxfz_dd/cycle-demo.git', '环环送', 'Enable', 'Git');

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
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COMMENT='项目sql脚本';

-- ----------------------------
-- Records of project_sql
-- ----------------------------
INSERT INTO `project_sql` VALUES ('51', '728007402042023936', '728013724233883648', '/*==============================================================*/\r\n/* DBMS name:      MySQL 5.0                                    */\r\n/* Created on:     2018/1/23 11:04:06                           */\r\n/*==============================================================*/\r\n\r\ndrop database if exists cycle;\r\n\r\ncreate database cycle;\r\n\r\nuse cycle;\r\n\r\ndrop table if exists admin;\r\n\r\ndrop table if exists basic_settings;\r\n\r\ndrop table if exists coupon;\r\n\r\ndrop table if exists coupon_his;\r\n\r\ndrop table if exists finace_plat;\r\n\r\ndrop table if exists finace_plat_rec;\r\n\r\ndrop table if exists goods;\r\n\r\ndrop table if exists goods_buy_record;\r\n\r\ndrop table if exists orders;\r\n\r\ndrop table if exists user;\r\n\r\ndrop table if exists user_account;\r\n\r\ndrop table if exists user_account_rec;\r\n\r\ndrop table if exists user_activation_apply;\r\n\r\ndrop table if exists user_activation_code;\r\n\r\ndrop table if exists user_draw_rec;\r\n\r\ndrop table if exists user_fund;\r\n\r\ndrop table if exists user_fund_rec;\r\n\r\ndrop table if exists user_parent;\r\n\r\ndrop table if exists user_parent_change_rec;\r\n\r\n/*==============================================================*/\r\n/* Table: admin                                                 */\r\n/*==============================================================*/\r\ncreate table admin\r\n(\r\n   id                   bigint not null auto_increment comment \'主键ID\',\r\n   code                 char(32) not null comment \'管理员编码\',\r\n   name                 varchar(64) comment \'管理员名称\',\r\n   login_account        varchar(32) not null comment \'登录账户(长度限制4-16)\',\r\n   pwd                  char(32) not null comment \'密码(MD5加密32位)\',\r\n   phone                char(11) comment \'手机号码\',\r\n   idcard               char(18) comment \'身份证号\',\r\n   role                 varchar(8) default \'NORMAL\' comment \'角色（NORMAL 普通，SUPER 高级）\',\r\n   state                char(16) default \'OPEN\' comment \'状态（OPEN 开启，DELETE 删除）\',\r\n   create_time          datetime not null comment \'创建时间\',\r\n   update_time          datetime comment \'更新时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table admin comment \'管理员表\';\r\n\r\n/*==============================================================*/\r\n/* Table: basic_settings                                        */\r\n/*==============================================================*/\r\ncreate table basic_settings\r\n(\r\n   id                   bigint unsigned not null auto_increment comment \'id\',\r\n   k                    varchar(32) not null comment \'键\',\r\n   v                    text not null comment \'值\',\r\n   remark               varchar(128) comment \'备注\',\r\n   create_time          datetime not null comment \'创建时间\',\r\n   update_time          datetime not null comment \'修改时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table basic_settings comment \'系统基础关键配置表\';\r\n\r\n/*==============================================================*/\r\n/* Table: coupon                                                */\r\n/*==============================================================*/\r\ncreate table coupon\r\n(\r\n   id                   bigint unsigned not null auto_increment comment \'id\',\r\n   tc                   char(32) not null comment \'交易码\',\r\n   record_code          char(32) not null comment \'购买礼包编号\',\r\n   user_code            char(32) not null comment \'用户编码A|B\',\r\n   release_user_code    char(32) comment \'释放人编码（直接触发人:对A说是B的编码，对B说是C的编码）\',\r\n   indirect_user_code   char(32) comment \'间接释放人编码（第二层购买时产生：对A说是C的编码，对B说无编码）\',\r\n   remark               varchar(256) not null comment \'释放说明（您A推广的B成功推广了C，为您释放16张代金券）\',\r\n   coupon_code          char(32) not null comment \'代金券编码\',\r\n   num                  smallint not null comment \'代金券期数\',\r\n   coupon_price         decimal(10,2) not null comment \'代金券面额\',\r\n   fund_rate            smallint comment \'基金抽取比例(1700代表17%)\',\r\n   fund                 decimal(10,2) comment \'抽取基金\',\r\n   price                decimal(10,2) comment \'实际释放额\',\r\n   is_release           char(1) default \'N\' comment \'状态(Y: 已释放,N: 未释放)\',\r\n   create_time          datetime not null comment \'创建时间\',\r\n   release_time         datetime comment \'释放时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table coupon comment \'代金券释放计划（已完成的礼包中所有返还计划转移至历史表）\';\r\n\r\n/*==============================================================*/\r\n/* Table: coupon_his                                            */\r\n/*==============================================================*/\r\ncreate table coupon_his\r\n(\r\n   id                   bigint unsigned not null auto_increment comment \'id\',\r\n   tc                   char(32) not null comment \'交易码\',\r\n   record_code          char(32) not null comment \'购买礼包编号\',\r\n   user_code            char(32) not null comment \'用户编码\',\r\n   release_user_code    char(32) comment \'释放人编码（直接触发人）\',\r\n   indirect_user_code   char(32) comment \'间接释放人编码（第二层购买时产生）\',\r\n   remark               varchar(256) not null comment \'释放说明（您推广的B成功推广了C，为您释放16张代金券）\',\r\n   coupon_code          char(32) not null comment \'代金券编码\',\r\n   num                  smallint not null comment \'代金券期数\',\r\n   coupon_price         decimal(10,2) not null comment \'代金券面额\',\r\n   fund_rate            smallint comment \'基金抽取比例(1700代表17%)\',\r\n   fund                 decimal(10,2) comment \'抽取基金\',\r\n   price                decimal(10,2) comment \'实际释放额\',\r\n   is_release           char(1) default \'Y\' comment \'状态(Y: 已释放,N: 未释放)\',\r\n   create_time          datetime not null comment \'创建时间\',\r\n   release_time         datetime comment \'释放时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table coupon_his comment \'代金券释放计划历史表（已完成的礼包中所有返还计划转移至此）\';\r\n\r\n/*==============================================================*/\r\n/* Table: finace_plat                                           */\r\n/*==============================================================*/\r\ncreate table finace_plat\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   balance              decimal(18,2) not null default 0 comment \'账户余额\',\r\n   income               decimal(18,2) default 0 comment \'总入账\',\r\n   expenditure          decimal(18,2) default 0 comment \'总出账\',\r\n   vn                   bigint not null default 1 comment \'版本号\',\r\n   create_time          datetime not null comment \'创建时间\',\r\n   update_time          datetime not null comment \'更新时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table finace_plat comment \'平台财务账户\';\r\n\r\n/*==============================================================*/\r\n/* Table: finace_plat_rec                                       */\r\n/*==============================================================*/\r\ncreate table finace_plat_rec\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   sn                   char(32) not null comment \'流水码\',\r\n   tc                   char(32) not null comment \'交易码\',\r\n   user_code            char(32) not null comment \'用户编码\',\r\n   income               decimal(18,2) default 0 comment \'收入\',\r\n   expenditure          decimal(18,2) default 0 comment \'支出\',\r\n   subject              varchar(32) not null comment \'会计科目\',\r\n   subject_name         varchar(64) not null comment \'会计科目名称\',\r\n   direction            char(6) not null comment \'记录方向（SINGLE 单方记录；DOUBLE 双方记录）\',\r\n   pay_way              varchar(10) not null comment \'交易方式（REMIT 线下支付，TRANSFER 账户转账，ACCOUNT 账户支付 等枚举）\',\r\n   remark               varchar(32) comment \'备注\',\r\n   create_time          datetime not null comment \'创建时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table finace_plat_rec comment \'平台财务流水\';\r\n\r\n/*==============================================================*/\r\n/* Table: goods                                                 */\r\n/*==============================================================*/\r\ncreate table goods\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   goods_code           char(32) not null comment \'商品编码\',\r\n   goods_name           varchar(128) not null comment \'商品名称\',\r\n   goods_jingle         varchar(150) not null comment \'商品广告词\',\r\n   goods_image          varchar(128) not null comment \'商品主图\',\r\n   price                decimal(10,2) not null comment \'价格\',\r\n   state                varchar(8) not null default \'NORMAL\' comment \'状态 DOWN下架，NORMAL正常\',\r\n   create_time          datetime not null comment \'商品添加时间\',\r\n   update_time          datetime comment \'修改时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table goods comment \'商品\';\r\n\r\n/*==============================================================*/\r\n/* Table: goods_buy_record                                      */\r\n/*==============================================================*/\r\ncreate table goods_buy_record\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   record_code          char(32) not null comment \'购买礼包编号\',\r\n   user_code            char(32) not null comment \'用户编码\',\r\n   goods_code           char(32) not null comment \'商品编码\',\r\n   goods_name           varchar(128) not null comment \'商品名称\',\r\n   goods_image          varchar(128) not null comment \'商品主图\',\r\n   is_first_buy         char(1) default \'N\' comment \'是否首次购买（Y 是，N否）当为Y时，需要用户完善收货信息及平台发货\',\r\n   is_delivery          char(1) default \'N\' comment \'是否已发货（Y 是，N 否）\',\r\n   activation_code      char(10) comment \'使用的激活码(is_first_buy=N时,此项为空)\',\r\n   name                 char(10) comment \'收货人姓名\',\r\n   phone                char(10) comment \'收货人电话\',\r\n   address              char(10) comment \'收货人地址\',\r\n   price                decimal(10,2) not null comment \'价格\',\r\n   state                varchar(4) not null default \'ING\' comment \'释放状态(ING 释放中，OVER 释放完毕)\',\r\n   create_time          datetime not null comment \'商品添加时间\',\r\n   update_time          datetime comment \'修改时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table goods_buy_record comment \'商品购买记录（释放完毕的礼包中对应的所有代金券转移至代金券历史表，复投时改变用户表is_need_cycle）\';\r\n\r\n/*==============================================================*/\r\n/* Table: orders                                                */\r\n/*==============================================================*/\r\ncreate table orders\r\n(\r\n   id                   bigint unsigned not null auto_increment comment \'id\',\r\n   order_code           char(32) not null comment \'订单号\',\r\n   login_account        varchar(32) not null comment \'登陆账户（长度4-16）\',\r\n   name                 varchar(64) comment \'姓名\',\r\n   phone                varchar(16) comment \'电话（仅限手机号）\',\r\n   user_code            char(32) not null comment \'用户编码\',\r\n   total_price          decimal(10,2) not null comment \'订单总额\',\r\n   brokerage            decimal(10,2) not null default 0.00 comment \'订单手续费\',\r\n   state                varchar(8) not null default \'CR\' comment \'状态(CR 创建订单, DONE 处理完成, CANCLE 取消订单)\',\r\n   descs                varchar(128) comment \'订单说明\',\r\n   create_time          datetime not null comment \'申请时间（CR 时的时间）\',\r\n   cancle_time          datetime comment \'取消时间\',\r\n   done_time            datetime comment \'完成时间（DONE‘FAIL 共用此时间）\',\r\n   primary key (id)\r\n);\r\n\r\nalter table orders comment \'用户购物订单表\';\r\n\r\n/*==============================================================*/\r\n/* Table: user                                                  */\r\n/*==============================================================*/\r\ncreate table user\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   user_code            char(32) not null comment \'用户编码\',\r\n   login_account        varchar(32) not null comment \'登陆账户（长度4-16）\',\r\n   pwd                  char(32) not null comment \'登陆密码（长度6-20，大小写字母+数字，需要MD5加密32位）\',\r\n   name                 varchar(64) comment \'姓名\',\r\n   phone                varchar(16) comment \'电话（仅限手机号）\',\r\n   idcard               varchar(18) comment \'身份证\',\r\n   recommend_code       char(32) comment \'推荐人编码\',\r\n   recommend_login_account varchar(32) not null comment \'推荐人账户\',\r\n   role                 varchar(10) not null default \'CREATE\' comment \'角色(CREATE:新注册, ACTIVATE:激活,OUT:出局)\',\r\n   state                varchar(8) default \'NORMAL\' comment \'用户状态（NORMAL 正常，ABNORMAL 不正常）\',\r\n   business_state       varchar(8) default \'NO\' comment \'商务中心状态(NO 未开通, APPLY 申请中, OPEN 已开通)\',\r\n   is_need_cycle        char(1) not null default \'N\' comment \'是否需复投(Y 需要，N 不需要)\',\r\n   cycle_start_time     datetime comment \'复投开始时间:用户什么时候进入复投期的\',\r\n   create_time          datetime not null comment \'创建时间\',\r\n   activate_time        datetime comment \'激活时间\',\r\n   update_time          datetime comment \'修改时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table user comment \'用户\';\r\n\r\n/*==============================================================*/\r\n/* Table: user_account                                          */\r\n/*==============================================================*/\r\ncreate table user_account\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   user_code            char(32) not null comment \'用户编码\',\r\n   balance              decimal(18,2) not null default 0 comment \'账户余额\',\r\n   vn                   int not null default 1 comment \'版本号\',\r\n   state                varchar(8) not null default \'NORMAL\' comment \'账户状态(NORMAL 正常，LOCK 冻结)\',\r\n   create_time          datetime not null comment \'创建时间\',\r\n   update_time          datetime not null comment \'更新时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table user_account comment \'用户账户\';\r\n\r\n/*==============================================================*/\r\n/* Table: user_account_rec                                      */\r\n/*==============================================================*/\r\ncreate table user_account_rec\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   sn                   char(32) not null comment \'流水码\',\r\n   tc                   char(32) not null comment \'交易码\',\r\n   user_code            char(32) not null comment \'用户编码\',\r\n   income               decimal(18,2) default 0 comment \'收入\',\r\n   expenditure          decimal(18,2) default 0 comment \'支出\',\r\n   subject              varchar(32) not null comment \'会计科目\',\r\n   subject_name         varchar(64) not null comment \'会计科目名称\',\r\n   direction            char(6) not null comment \'记录方向（SINGLE 单方记录；DOUBLE 双方记录）\',\r\n   pay_way              varchar(10) not null comment \'交易方式（REMIT 线下支付，TRANSFER 账户转账，ACCOUNT 账户支付 等枚举）\',\r\n   remark               varchar(256) comment \'备注\',\r\n   create_time          datetime not null comment \'创建时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table user_account_rec comment \'用户账户流水\';\r\n\r\n/*==============================================================*/\r\n/* Table: user_activation_apply                                 */\r\n/*==============================================================*/\r\ncreate table user_activation_apply\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   apply_code           char(32) comment \'申请编码\',\r\n   tc                   char(32) not null comment \'交易码(审核发放激活码时，回填交易码)\',\r\n   user_code            char(32) comment \'用户编码(平台生成时，此为空)\',\r\n   price                decimal(10,2) comment \'激活码单价\',\r\n   num                  smallint comment \'申请数量\',\r\n   total_price          decimal(10,2) comment \'申请总价（折扣之后的总价）\',\r\n   rate_price           decimal(10,2) comment \'折后总价\',\r\n   rate                 smallint comment \'折扣(1700代表17%)\',\r\n   state                varchar(8) comment \'申请状态(APPLY 申请中, PASS 已发放)\',\r\n   admin_code           char(32) comment \'审核管理员编码\',\r\n   admin_name           varchar(64) comment \'审核管理员名称\',\r\n   apply_time           datetime comment \'申请时间\',\r\n   audit_time           datetime comment \'审核时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table user_activation_apply comment \'激活码申请表\';\r\n\r\n/*==============================================================*/\r\n/* Table: user_activation_code                                  */\r\n/*==============================================================*/\r\ncreate table user_activation_code\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   tc                   char(32) not null comment \'交易码\',\r\n   apply_code           char(32) comment \'申请编码(平台生成时，此为空)\',\r\n   user_code            char(32) comment \'用户编码(平台生成时，此为空)\',\r\n   activation_code      char(32) comment \'激活码，此码唯一(仅供用户注册使用)\',\r\n   is_activate          char(1) comment \'是否被激活(Y 是，N 否)\',\r\n   activate_user_code   char(32) comment \'激活用户编码(平台生成时，此为空)\',\r\n   login_account        varchar(32) not null comment \'激活用户账户(平台生成时，此为空)\',\r\n   price                decimal(10,2) comment \'单价\',\r\n   admin_code           char(32) comment \'发放管理员编码\',\r\n   admin_name           varchar(64) comment \'发放管理员名称\',\r\n   create_time          datetime comment \'发放时间\',\r\n   activate_time        datetime comment \'激活时间(平台生成时，此为空)\',\r\n   primary key (id)\r\n);\r\n\r\nalter table user_activation_code comment \'激活码(1 平台拿到用户的钱生成激活码，2平台审核商务中心申请并核账生成激活码)\';\r\n\r\n/*==============================================================*/\r\n/* Table: user_draw_rec                                         */\r\n/*==============================================================*/\r\ncreate table user_draw_rec\r\n(\r\n   id                   bigint unsigned not null auto_increment comment \'id\',\r\n   tc                   char(32) not null comment \'交易码\',\r\n   user_code            char(32) not null comment \'用户编码\',\r\n   draw_money           decimal(10,2) not null comment \'提现金额\',\r\n   brokerage            decimal(10,2) not null default 0.00 comment \'提现手续费\',\r\n   really_draw          decimal(10,2) comment \'实际到账\',\r\n   acct                 varchar(32) not null comment \'用户收款银行卡号\',\r\n   bank                 varchar(32) comment \'收款账号开户行\',\r\n   bacct_name           varchar(32) comment \'收款姓名\',\r\n   pay_way              varchar(10) default \'REMIT\' comment \'交易方式（REMIT 银行转账，CASH 现金 枚举）\',\r\n   pay_bank             varchar(64) comment \'平台汇款账号开户行\',\r\n   pay_acct             varchar(32) comment \'平台汇款账号\',\r\n   pay_bacct_name       varchar(32) comment \'平台汇款人（如果是银行转账REMIT时，此项必填）\',\r\n   voucher_url          varchar(128) comment \'平台汇款凭证(记录上传的转账凭证图片，回填)\',\r\n   remark               varchar(128) comment \'平台汇款备注\',\r\n   state                varchar(8) not null default \'CR\' comment \'状态(CR 提交申请, DEAL 提现处理中, DONE 提现到账, FAIL 提现失败)\',\r\n   fail_reason          varchar(128) comment \'提现失败原因\',\r\n   create_time          datetime not null comment \'申请时间（CR 时的时间）\',\r\n   deal_time            datetime comment \'处理时间（DEAL 时的时间）\',\r\n   done_time            datetime comment \'完成时间（DONE‘FAIL 共用此时间）\',\r\n   primary key (id)\r\n);\r\n\r\nalter table user_draw_rec comment \'用户账户提现表\';\r\n\r\n/*==============================================================*/\r\n/* Table: user_fund                                             */\r\n/*==============================================================*/\r\ncreate table user_fund\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   user_code            char(32) not null comment \'用户编码\',\r\n   balance              decimal(18,2) not null default 0 comment \'账户余额\',\r\n   vn                   int not null default 1 comment \'版本号\',\r\n   state                varchar(8) not null default \'NORMAL\' comment \'账户状态(NORMAL 正常，LOCK 冻结)\',\r\n   create_time          datetime not null comment \'创建时间\',\r\n   update_time          datetime not null comment \'更新时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table user_fund comment \'用户基金账户\';\r\n\r\n/*==============================================================*/\r\n/* Table: user_fund_rec                                         */\r\n/*==============================================================*/\r\ncreate table user_fund_rec\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   sn                   char(32) not null comment \'流水码\',\r\n   tc                   char(32) not null comment \'交易码\',\r\n   user_code            char(32) not null comment \'用户编码\',\r\n   income               decimal(18,2) default 0 comment \'收入\',\r\n   expenditure          decimal(18,2) default 0 comment \'支出\',\r\n   subject              varchar(32) not null comment \'会计科目\',\r\n   subject_name         varchar(64) not null comment \'会计科目名称\',\r\n   direction            char(6) not null comment \'记录方向（SINGLE 单方记录；DOUBLE 双方记录）\',\r\n   pay_way              varchar(10) not null comment \'交易方式（REMIT 线下支付，TRANSFER 账户转账，ACCOUNT 账户支付 等枚举）\',\r\n   remark               varchar(256) comment \'备注\',\r\n   create_time          datetime not null comment \'创建时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table user_fund_rec comment \'用户基金账户流水\';\r\n\r\n/*==============================================================*/\r\n/* Table: user_parent                                           */\r\n/*==============================================================*/\r\ncreate table user_parent\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   user_code            char(32) comment \'用户编码\',\r\n   parent_code          char(32) comment \'接点人编码(同一个接点人，下属仅能绑定两人)\',\r\n   login_account        varchar(32) not null comment \'接点人账户\',\r\n   activate_time        datetime comment \'激活时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table user_parent comment \'用户接点关系(用户A的出局,A的两个下属之一向上B填缺,B之外的C换主为B,为每一个关系变动者做好变动记录)\';\r\n\r\n/*==============================================================*/\r\n/* Table: user_parent_change_rec                                */\r\n/*==============================================================*/\r\ncreate table user_parent_change_rec\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   user_code            char(32) not null comment \'用户编码\',\r\n   parent_code_old      char(32) not null comment \'老推荐人编码\',\r\n   parent_code_new      char(32) not null comment \'新推荐人编码\',\r\n   remark               varchar(256) comment \'变动说明\',\r\n   create_time          datetime not null comment \'创建时间\',\r\n   primary key (id)\r\n);\r\n\r\nalter table user_parent_change_rec comment \'用户接点关系变动记录\';\r\n\r\n', 'Enable');

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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='设置';

-- ----------------------------
-- Records of setting
-- ----------------------------
INSERT INTO `setting` VALUES ('1', 'DefaultDatabase', 'ai_code_simple', '默认链接数据库');
INSERT INTO `setting` VALUES ('2', 'Gradle_Directory_Structure', '[\"src/main/java\",\"src/main/resources/framework\"]', 'gradle目录结构');
INSERT INTO `setting` VALUES ('3', 'Workspace', '/workspace/', '工作目录');
INSERT INTO `setting` VALUES ('4', 'Package_entity', 'po', '实体目录命名');
INSERT INTO `setting` VALUES ('5', 'Template_Path', '/templates/', '模板默认路径');

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
) ENGINE=InnoDB AUTO_INCREMENT=1208 DEFAULT CHARSET=utf8 COMMENT='DB WorkerID Assigner for UID Generator';

-- ----------------------------
-- Records of worker_node
-- ----------------------------
INSERT INTO `worker_node` VALUES ('988', '192.168.10.173', '1512094548581-22846', '2', '2017-12-01', '2017-12-01 02:15:48', '2017-12-01 02:15:48');
INSERT INTO `worker_node` VALUES ('989', '192.168.10.173', '1512094698043-95119', '2', '2017-12-01', '2017-12-01 02:18:18', '2017-12-01 02:18:18');
INSERT INTO `worker_node` VALUES ('990', '192.168.10.173', '1512094773022-77517', '2', '2017-12-01', '2017-12-01 02:19:33', '2017-12-01 02:19:33');
INSERT INTO `worker_node` VALUES ('991', '192.168.10.173', '1512095014813-19004', '2', '2017-12-01', '2017-12-01 02:23:35', '2017-12-01 02:23:35');
INSERT INTO `worker_node` VALUES ('992', '192.168.10.173', '1513309884397-46106', '2', '2017-12-15', '2017-12-15 03:51:26', '2017-12-15 03:51:26');
INSERT INTO `worker_node` VALUES ('993', '192.168.10.173', '1513310135170-98647', '2', '2017-12-15', '2017-12-15 03:55:36', '2017-12-15 03:55:36');
INSERT INTO `worker_node` VALUES ('994', '192.168.10.173', '1513591245857-26806', '2', '2017-12-18', '2017-12-18 10:00:47', '2017-12-18 10:00:47');
INSERT INTO `worker_node` VALUES ('995', '192.168.10.173', '1513591420449-50276', '2', '2017-12-18', '2017-12-18 10:03:42', '2017-12-18 10:03:42');
INSERT INTO `worker_node` VALUES ('996', '192.168.10.173', '1513591593186-32726', '2', '2017-12-18', '2017-12-18 10:06:35', '2017-12-18 10:06:35');
INSERT INTO `worker_node` VALUES ('997', '192.168.10.173', '1513591730372-31926', '2', '2017-12-18', '2017-12-18 10:08:52', '2017-12-18 10:08:52');
INSERT INTO `worker_node` VALUES ('998', '192.168.10.173', '1513591951235-90086', '2', '2017-12-18', '2017-12-18 10:12:33', '2017-12-18 10:12:33');
INSERT INTO `worker_node` VALUES ('999', '192.168.10.173', '1513593046735-72326', '2', '2017-12-18', '2017-12-18 10:30:48', '2017-12-18 10:30:48');
INSERT INTO `worker_node` VALUES ('1000', '192.168.10.173', '1513644606826-57140', '2', '2017-12-19', '2017-12-19 00:50:06', '2017-12-19 00:50:06');
INSERT INTO `worker_node` VALUES ('1001', '192.168.10.173', '1513646409662-19234', '2', '2017-12-19', '2017-12-19 01:20:09', '2017-12-19 01:20:09');
INSERT INTO `worker_node` VALUES ('1002', '192.168.10.173', '1513646484702-59483', '2', '2017-12-19', '2017-12-19 01:21:24', '2017-12-19 01:21:24');
INSERT INTO `worker_node` VALUES ('1003', '192.168.10.173', '1513650199975-38149', '2', '2017-12-19', '2017-12-19 02:23:19', '2017-12-19 02:23:19');
INSERT INTO `worker_node` VALUES ('1004', '192.168.10.173', '1513650923580-34323', '2', '2017-12-19', '2017-12-19 02:35:23', '2017-12-19 02:35:23');
INSERT INTO `worker_node` VALUES ('1005', '192.168.10.173', '1513651019824-68158', '2', '2017-12-19', '2017-12-19 02:36:59', '2017-12-19 02:36:59');
INSERT INTO `worker_node` VALUES ('1006', '192.168.10.173', '1513655228882-52087', '2', '2017-12-19', '2017-12-19 03:47:08', '2017-12-19 03:47:08');
INSERT INTO `worker_node` VALUES ('1007', '192.168.10.173', '1513655366790-3095', '2', '2017-12-19', '2017-12-19 03:49:26', '2017-12-19 03:49:26');
INSERT INTO `worker_node` VALUES ('1008', '192.168.10.173', '1513669571410-63939', '2', '2017-12-19', '2017-12-19 07:46:11', '2017-12-19 07:46:11');
INSERT INTO `worker_node` VALUES ('1009', '192.168.10.173', '1513669683775-34770', '2', '2017-12-19', '2017-12-19 07:48:03', '2017-12-19 07:48:03');
INSERT INTO `worker_node` VALUES ('1010', '192.168.10.173', '1513669815746-5350', '2', '2017-12-19', '2017-12-19 07:50:15', '2017-12-19 07:50:15');
INSERT INTO `worker_node` VALUES ('1011', '192.168.10.173', '1513731785662-7746', '2', '2017-12-20', '2017-12-20 01:03:07', '2017-12-20 01:03:07');
INSERT INTO `worker_node` VALUES ('1012', '192.168.10.173', '1513733967394-25874', '2', '2017-12-20', '2017-12-20 01:39:29', '2017-12-20 01:39:29');
INSERT INTO `worker_node` VALUES ('1013', '192.168.10.173', '1513734060592-96921', '2', '2017-12-20', '2017-12-20 01:41:02', '2017-12-20 01:41:02');
INSERT INTO `worker_node` VALUES ('1014', '192.168.10.173', '1513825550487-5721', '2', '2017-12-21', '2017-12-21 03:05:53', '2017-12-21 03:05:53');
INSERT INTO `worker_node` VALUES ('1015', '192.168.10.173', '1513826094126-4165', '2', '2017-12-21', '2017-12-21 03:14:57', '2017-12-21 03:14:57');
INSERT INTO `worker_node` VALUES ('1016', '192.168.10.173', '1513826418197-77985', '2', '2017-12-21', '2017-12-21 03:20:21', '2017-12-21 03:20:21');
INSERT INTO `worker_node` VALUES ('1017', '192.168.10.173', '1513832147007-57375', '2', '2017-12-21', '2017-12-21 04:55:50', '2017-12-21 04:55:50');
INSERT INTO `worker_node` VALUES ('1018', '192.168.10.173', '1513832402679-18083', '2', '2017-12-21', '2017-12-21 05:00:05', '2017-12-21 05:00:05');
INSERT INTO `worker_node` VALUES ('1019', '192.168.10.173', '1513832591862-16190', '2', '2017-12-21', '2017-12-21 05:03:14', '2017-12-21 05:03:14');
INSERT INTO `worker_node` VALUES ('1020', '192.168.10.173', '1513832730204-61403', '2', '2017-12-21', '2017-12-21 05:05:33', '2017-12-21 05:05:33');
INSERT INTO `worker_node` VALUES ('1021', '192.168.10.173', '1513832821991-53447', '2', '2017-12-21', '2017-12-21 05:07:05', '2017-12-21 05:07:05');
INSERT INTO `worker_node` VALUES ('1022', '192.168.10.173', '1513832991026-63732', '2', '2017-12-21', '2017-12-21 05:09:54', '2017-12-21 05:09:54');
INSERT INTO `worker_node` VALUES ('1023', '192.168.10.173', '1513833105764-15198', '2', '2017-12-21', '2017-12-21 05:11:48', '2017-12-21 05:11:48');
INSERT INTO `worker_node` VALUES ('1024', '192.168.10.173', '1513833240030-46303', '2', '2017-12-21', '2017-12-21 05:14:03', '2017-12-21 05:14:03');
INSERT INTO `worker_node` VALUES ('1025', '192.168.10.173', '1513833288839-10379', '2', '2017-12-21', '2017-12-21 05:14:51', '2017-12-21 05:14:51');
INSERT INTO `worker_node` VALUES ('1026', '192.168.10.173', '1513833458226-43764', '2', '2017-12-21', '2017-12-21 05:17:41', '2017-12-21 05:17:41');
INSERT INTO `worker_node` VALUES ('1027', '192.168.10.173', '1513833542350-79287', '2', '2017-12-21', '2017-12-21 05:19:05', '2017-12-21 05:19:05');
INSERT INTO `worker_node` VALUES ('1028', '192.168.10.173', '1513836780031-7727', '2', '2017-12-21', '2017-12-21 06:13:03', '2017-12-21 06:13:03');
INSERT INTO `worker_node` VALUES ('1029', '192.168.10.173', '1513836875502-53585', '2', '2017-12-21', '2017-12-21 06:14:38', '2017-12-21 06:14:38');
INSERT INTO `worker_node` VALUES ('1030', '192.168.10.173', '1513839256424-94618', '2', '2017-12-21', '2017-12-21 06:54:19', '2017-12-21 06:54:19');
INSERT INTO `worker_node` VALUES ('1031', '192.168.10.173', '1513840649674-31001', '2', '2017-12-21', '2017-12-21 07:17:32', '2017-12-21 07:17:32');
INSERT INTO `worker_node` VALUES ('1032', '192.168.10.173', '1513840834306-24923', '2', '2017-12-21', '2017-12-21 07:20:37', '2017-12-21 07:20:37');
INSERT INTO `worker_node` VALUES ('1033', '192.168.10.173', '1513840927362-38115', '2', '2017-12-21', '2017-12-21 07:22:10', '2017-12-21 07:22:10');
INSERT INTO `worker_node` VALUES ('1034', '192.168.10.173', '1513841477678-59721', '2', '2017-12-21', '2017-12-21 07:31:20', '2017-12-21 07:31:20');
INSERT INTO `worker_node` VALUES ('1035', '192.168.10.173', '1513841667285-85193', '2', '2017-12-21', '2017-12-21 07:34:30', '2017-12-21 07:34:30');
INSERT INTO `worker_node` VALUES ('1036', '192.168.10.173', '1513841810811-70679', '2', '2017-12-21', '2017-12-21 07:36:53', '2017-12-21 07:36:53');
INSERT INTO `worker_node` VALUES ('1037', '192.168.10.173', '1513843544732-39400', '2', '2017-12-21', '2017-12-21 08:05:47', '2017-12-21 08:05:47');
INSERT INTO `worker_node` VALUES ('1038', '192.168.10.173', '1513844367858-45484', '2', '2017-12-21', '2017-12-21 08:19:30', '2017-12-21 08:19:30');
INSERT INTO `worker_node` VALUES ('1039', '192.168.10.173', '1513845048709-6603', '2', '2017-12-21', '2017-12-21 08:30:51', '2017-12-21 08:30:51');
INSERT INTO `worker_node` VALUES ('1040', '192.168.10.173', '1513847814730-43331', '2', '2017-12-21', '2017-12-21 09:16:57', '2017-12-21 09:16:57');
INSERT INTO `worker_node` VALUES ('1041', '192.168.10.173', '1513847895778-18838', '2', '2017-12-21', '2017-12-21 09:18:18', '2017-12-21 09:18:18');
INSERT INTO `worker_node` VALUES ('1042', '192.168.10.173', '1513850070373-41415', '2', '2017-12-21', '2017-12-21 09:54:33', '2017-12-21 09:54:33');
INSERT INTO `worker_node` VALUES ('1043', '192.168.10.173', '1513850334761-10597', '2', '2017-12-21', '2017-12-21 09:58:57', '2017-12-21 09:58:57');
INSERT INTO `worker_node` VALUES ('1044', '192.168.10.173', '1513850430278-78439', '2', '2017-12-21', '2017-12-21 10:00:33', '2017-12-21 10:00:33');
INSERT INTO `worker_node` VALUES ('1045', '192.168.10.173', '1513905615560-37641', '2', '2017-12-22', '2017-12-22 01:20:15', '2017-12-22 01:20:15');
INSERT INTO `worker_node` VALUES ('1046', '192.168.10.173', '1513925275754-40072', '2', '2017-12-22', '2017-12-22 06:47:55', '2017-12-22 06:47:55');
INSERT INTO `worker_node` VALUES ('1047', '192.168.10.173', '1513925340351-76514', '2', '2017-12-22', '2017-12-22 06:49:00', '2017-12-22 06:49:00');
INSERT INTO `worker_node` VALUES ('1048', '192.168.10.173', '1513925387330-8110', '2', '2017-12-22', '2017-12-22 06:49:47', '2017-12-22 06:49:47');
INSERT INTO `worker_node` VALUES ('1049', '192.168.10.173', '1513925887636-75071', '2', '2017-12-22', '2017-12-22 06:58:07', '2017-12-22 06:58:07');
INSERT INTO `worker_node` VALUES ('1050', '192.168.56.1', '1513926186366-30573', '2', '2017-12-22', '2017-12-22 07:03:06', '2017-12-22 07:03:06');
INSERT INTO `worker_node` VALUES ('1051', '192.168.56.1', '1513926973787-54628', '2', '2017-12-22', '2017-12-22 07:16:13', '2017-12-22 07:16:13');
INSERT INTO `worker_node` VALUES ('1052', '192.168.10.173', '1513927088189-61333', '2', '2017-12-22', '2017-12-22 07:18:07', '2017-12-22 07:18:07');
INSERT INTO `worker_node` VALUES ('1053', '192.168.10.173', '1513927733133-55938', '2', '2017-12-22', '2017-12-22 07:28:52', '2017-12-22 07:28:52');
INSERT INTO `worker_node` VALUES ('1054', '192.168.56.1', '1513927746301-42445', '2', '2017-12-22', '2017-12-22 07:29:06', '2017-12-22 07:29:06');
INSERT INTO `worker_node` VALUES ('1055', '192.168.56.1', '1513927929018-89828', '2', '2017-12-22', '2017-12-22 07:32:08', '2017-12-22 07:32:08');
INSERT INTO `worker_node` VALUES ('1056', '192.168.56.1', '1513928003096-51358', '2', '2017-12-22', '2017-12-22 07:33:22', '2017-12-22 07:33:22');
INSERT INTO `worker_node` VALUES ('1057', '192.168.10.173', '1513928356361-93972', '2', '2017-12-22', '2017-12-22 07:39:16', '2017-12-22 07:39:16');
INSERT INTO `worker_node` VALUES ('1058', '192.168.56.1', '1513928548333-77530', '2', '2017-12-22', '2017-12-22 07:42:28', '2017-12-22 07:42:28');
INSERT INTO `worker_node` VALUES ('1059', '192.168.10.173', '1513928629326-96343', '2', '2017-12-22', '2017-12-22 07:43:49', '2017-12-22 07:43:49');
INSERT INTO `worker_node` VALUES ('1060', '192.168.56.1', '1513928768689-60185', '2', '2017-12-22', '2017-12-22 07:46:08', '2017-12-22 07:46:08');
INSERT INTO `worker_node` VALUES ('1061', '192.168.56.1', '1513928863590-92036', '2', '2017-12-22', '2017-12-22 07:47:43', '2017-12-22 07:47:43');
INSERT INTO `worker_node` VALUES ('1062', '192.168.56.1', '1513928990491-66878', '2', '2017-12-22', '2017-12-22 07:49:50', '2017-12-22 07:49:50');
INSERT INTO `worker_node` VALUES ('1063', '192.168.56.1', '1513929057528-46228', '2', '2017-12-22', '2017-12-22 07:50:57', '2017-12-22 07:50:57');
INSERT INTO `worker_node` VALUES ('1064', '192.168.56.1', '1513929123951-11381', '2', '2017-12-22', '2017-12-22 07:52:03', '2017-12-22 07:52:03');
INSERT INTO `worker_node` VALUES ('1065', '192.168.56.1', '1513929357605-141', '2', '2017-12-22', '2017-12-22 07:55:57', '2017-12-22 07:55:57');
INSERT INTO `worker_node` VALUES ('1066', '192.168.56.1', '1513929536288-20637', '2', '2017-12-22', '2017-12-22 07:58:56', '2017-12-22 07:58:56');
INSERT INTO `worker_node` VALUES ('1067', '192.168.56.1', '1513929672502-6105', '2', '2017-12-22', '2017-12-22 08:01:12', '2017-12-22 08:01:12');
INSERT INTO `worker_node` VALUES ('1068', '192.168.10.173', '1513929988237-30345', '2', '2017-12-22', '2017-12-22 08:06:27', '2017-12-22 08:06:27');
INSERT INTO `worker_node` VALUES ('1069', '192.168.10.173', '1513930435517-68299', '2', '2017-12-22', '2017-12-22 08:13:55', '2017-12-22 08:13:55');
INSERT INTO `worker_node` VALUES ('1070', '192.168.56.1', '1513930487566-56217', '2', '2017-12-22', '2017-12-22 08:14:47', '2017-12-22 08:14:47');
INSERT INTO `worker_node` VALUES ('1071', '192.168.10.173', '1513930547156-3073', '2', '2017-12-22', '2017-12-22 08:15:46', '2017-12-22 08:15:46');
INSERT INTO `worker_node` VALUES ('1072', '192.168.10.173', '1513930665131-17379', '2', '2017-12-22', '2017-12-22 08:17:44', '2017-12-22 08:17:44');
INSERT INTO `worker_node` VALUES ('1073', '192.168.10.173', '1513930904824-57950', '2', '2017-12-22', '2017-12-22 08:21:44', '2017-12-22 08:21:44');
INSERT INTO `worker_node` VALUES ('1074', '192.168.10.173', '1513930929403-47111', '2', '2017-12-22', '2017-12-22 08:22:09', '2017-12-22 08:22:09');
INSERT INTO `worker_node` VALUES ('1075', '192.168.10.173', '1513932934955-72802', '2', '2017-12-22', '2017-12-22 08:55:34', '2017-12-22 08:55:34');
INSERT INTO `worker_node` VALUES ('1076', '192.168.10.173', '1513934413504-71408', '2', '2017-12-22', '2017-12-22 09:20:13', '2017-12-22 09:20:13');
INSERT INTO `worker_node` VALUES ('1077', '192.168.10.173', '1513934945072-20002', '2', '2017-12-22', '2017-12-22 09:29:04', '2017-12-22 09:29:04');
INSERT INTO `worker_node` VALUES ('1078', '192.168.10.173', '1513935333955-86093', '2', '2017-12-22', '2017-12-22 09:35:33', '2017-12-22 09:35:33');
INSERT INTO `worker_node` VALUES ('1079', '192.168.10.173', '1513935433514-56021', '2', '2017-12-22', '2017-12-22 09:37:13', '2017-12-22 09:37:13');
INSERT INTO `worker_node` VALUES ('1080', '192.168.10.173', '1513935564891-62371', '2', '2017-12-22', '2017-12-22 09:39:24', '2017-12-22 09:39:24');
INSERT INTO `worker_node` VALUES ('1081', '192.168.10.173', '1513935761890-36264', '2', '2017-12-22', '2017-12-22 09:42:41', '2017-12-22 09:42:41');
INSERT INTO `worker_node` VALUES ('1082', '192.168.10.173', '1513989774835-21845', '2', '2017-12-23', '2017-12-23 00:42:55', '2017-12-23 00:42:55');
INSERT INTO `worker_node` VALUES ('1083', '192.168.10.173', '1513996921371-35547', '2', '2017-12-23', '2017-12-23 02:42:01', '2017-12-23 02:42:01');
INSERT INTO `worker_node` VALUES ('1084', '192.168.10.173', '1513998011849-2957', '2', '2017-12-23', '2017-12-23 03:00:12', '2017-12-23 03:00:12');
INSERT INTO `worker_node` VALUES ('1085', '192.168.56.1', '1513998064074-80666', '2', '2017-12-23', '2017-12-23 03:01:03', '2017-12-23 03:01:03');
INSERT INTO `worker_node` VALUES ('1086', '192.168.10.173', '1514008009316-82180', '2', '2017-12-23', '2017-12-23 05:46:49', '2017-12-23 05:46:49');
INSERT INTO `worker_node` VALUES ('1087', '192.168.10.173', '1514009902805-56144', '2', '2017-12-23', '2017-12-23 06:18:22', '2017-12-23 06:18:22');
INSERT INTO `worker_node` VALUES ('1088', '192.168.10.173', '1514014911903-55746', '2', '2017-12-23', '2017-12-23 07:41:51', '2017-12-23 07:41:51');
INSERT INTO `worker_node` VALUES ('1089', '192.168.10.173', '1514093539280-71117', '2', '2017-12-24', '2017-12-24 05:32:20', '2017-12-24 05:32:20');
INSERT INTO `worker_node` VALUES ('1090', '192.168.10.173', '1514095179060-14340', '2', '2017-12-24', '2017-12-24 05:59:40', '2017-12-24 05:59:40');
INSERT INTO `worker_node` VALUES ('1091', '192.168.10.173', '1514095811531-41816', '2', '2017-12-24', '2017-12-24 06:10:12', '2017-12-24 06:10:12');
INSERT INTO `worker_node` VALUES ('1092', '192.168.10.173', '1514095968672-74010', '2', '2017-12-24', '2017-12-24 06:12:49', '2017-12-24 06:12:49');
INSERT INTO `worker_node` VALUES ('1093', '192.168.10.173', '1514096071659-10658', '2', '2017-12-24', '2017-12-24 06:14:32', '2017-12-24 06:14:32');
INSERT INTO `worker_node` VALUES ('1094', '192.168.10.173', '1514096271406-54663', '2', '2017-12-24', '2017-12-24 06:17:52', '2017-12-24 06:17:52');
INSERT INTO `worker_node` VALUES ('1095', '192.168.10.173', '1514097029692-92531', '2', '2017-12-24', '2017-12-24 06:30:31', '2017-12-24 06:30:31');
INSERT INTO `worker_node` VALUES ('1096', '192.168.10.173', '1514097231273-91680', '2', '2017-12-24', '2017-12-24 06:33:52', '2017-12-24 06:33:52');
INSERT INTO `worker_node` VALUES ('1097', '192.168.10.173', '1514097529583-96073', '2', '2017-12-24', '2017-12-24 06:38:50', '2017-12-24 06:38:50');
INSERT INTO `worker_node` VALUES ('1098', '192.168.10.173', '1514097820872-40875', '2', '2017-12-24', '2017-12-24 06:43:42', '2017-12-24 06:43:42');
INSERT INTO `worker_node` VALUES ('1099', '192.168.10.173', '1514098832120-75937', '2', '2017-12-24', '2017-12-24 07:00:33', '2017-12-24 07:00:33');
INSERT INTO `worker_node` VALUES ('1100', '192.168.10.173', '1514099595059-10409', '2', '2017-12-24', '2017-12-24 07:13:16', '2017-12-24 07:13:16');
INSERT INTO `worker_node` VALUES ('1101', '192.168.10.173', '1514100246108-23268', '2', '2017-12-24', '2017-12-24 07:24:07', '2017-12-24 07:24:07');
INSERT INTO `worker_node` VALUES ('1102', '192.168.10.173', '1514103196781-88618', '2', '2017-12-24', '2017-12-24 08:13:17', '2017-12-24 08:13:17');
INSERT INTO `worker_node` VALUES ('1103', '192.168.10.173', '1514103284453-53831', '2', '2017-12-24', '2017-12-24 08:14:45', '2017-12-24 08:14:45');
INSERT INTO `worker_node` VALUES ('1104', '192.168.10.173', '1514103555878-61962', '2', '2017-12-24', '2017-12-24 08:19:17', '2017-12-24 08:19:17');
INSERT INTO `worker_node` VALUES ('1105', '192.168.10.173', '1514104530838-36682', '2', '2017-12-24', '2017-12-24 08:35:32', '2017-12-24 08:35:32');
INSERT INTO `worker_node` VALUES ('1106', '192.168.10.173', '1514108738996-41008', '2', '2017-12-24', '2017-12-24 09:45:40', '2017-12-24 09:45:40');
INSERT INTO `worker_node` VALUES ('1107', '192.168.10.173', '1514109279103-49784', '2', '2017-12-24', '2017-12-24 09:54:40', '2017-12-24 09:54:40');
INSERT INTO `worker_node` VALUES ('1108', '192.168.10.173', '1514109379430-82171', '2', '2017-12-24', '2017-12-24 09:56:20', '2017-12-24 09:56:20');
INSERT INTO `worker_node` VALUES ('1109', '192.168.10.173', '1514109487691-80839', '2', '2017-12-24', '2017-12-24 09:58:08', '2017-12-24 09:58:08');
INSERT INTO `worker_node` VALUES ('1110', '192.168.10.173', '1514109981496-9961', '2', '2017-12-24', '2017-12-24 10:06:22', '2017-12-24 10:06:22');
INSERT INTO `worker_node` VALUES ('1111', '192.168.10.173', '1514110608666-46734', '2', '2017-12-24', '2017-12-24 10:16:49', '2017-12-24 10:16:49');
INSERT INTO `worker_node` VALUES ('1112', '192.168.10.173', '1514112078701-60030', '2', '2017-12-24', '2017-12-24 10:41:19', '2017-12-24 10:41:19');
INSERT INTO `worker_node` VALUES ('1113', '192.168.10.173', '1514163957832-60176', '2', '2017-12-25', '2017-12-25 01:06:01', '2017-12-25 01:06:01');
INSERT INTO `worker_node` VALUES ('1114', '192.168.56.1', '1514259661953-15335', '2', '2017-12-26', '2017-12-26 03:41:00', '2017-12-26 03:41:00');
INSERT INTO `worker_node` VALUES ('1115', '192.168.10.173', '1514369793651-55383', '2', '2017-12-27', '2017-12-27 10:16:35', '2017-12-27 10:16:35');
INSERT INTO `worker_node` VALUES ('1116', '192.168.10.173', '1514369942847-92223', '2', '2017-12-27', '2017-12-27 10:19:04', '2017-12-27 10:19:04');
INSERT INTO `worker_node` VALUES ('1117', '192.168.10.173', '1514370022041-11273', '2', '2017-12-27', '2017-12-27 10:20:23', '2017-12-27 10:20:23');
INSERT INTO `worker_node` VALUES ('1118', '192.168.10.173', '1514370157571-67622', '2', '2017-12-27', '2017-12-27 10:22:38', '2017-12-27 10:22:38');
INSERT INTO `worker_node` VALUES ('1119', '192.168.10.173', '1514370241950-46518', '2', '2017-12-27', '2017-12-27 10:24:03', '2017-12-27 10:24:03');
INSERT INTO `worker_node` VALUES ('1120', '192.168.10.173', '1514370308584-91204', '2', '2017-12-27', '2017-12-27 10:25:09', '2017-12-27 10:25:09');
INSERT INTO `worker_node` VALUES ('1121', '192.168.10.173', '1514370497752-43503', '2', '2017-12-27', '2017-12-27 10:28:19', '2017-12-27 10:28:19');
INSERT INTO `worker_node` VALUES ('1122', '192.168.10.173', '1514370884471-20691', '2', '2017-12-27', '2017-12-27 10:34:45', '2017-12-27 10:34:45');
INSERT INTO `worker_node` VALUES ('1123', '192.168.10.173', '1514371405320-30945', '2', '2017-12-27', '2017-12-27 10:43:26', '2017-12-27 10:43:26');
INSERT INTO `worker_node` VALUES ('1124', '192.168.10.173', '1514371530327-80629', '2', '2017-12-27', '2017-12-27 10:45:31', '2017-12-27 10:45:31');
INSERT INTO `worker_node` VALUES ('1125', '192.168.10.173', '1514371821016-68367', '2', '2017-12-27', '2017-12-27 10:50:22', '2017-12-27 10:50:22');
INSERT INTO `worker_node` VALUES ('1126', '192.168.10.173', '1514372071710-11178', '2', '2017-12-27', '2017-12-27 10:54:33', '2017-12-27 10:54:33');
INSERT INTO `worker_node` VALUES ('1127', '192.168.10.173', '1514372198168-20597', '2', '2017-12-27', '2017-12-27 10:56:39', '2017-12-27 10:56:39');
INSERT INTO `worker_node` VALUES ('1128', '192.168.10.173', '1514372531419-92808', '2', '2017-12-27', '2017-12-27 11:02:12', '2017-12-27 11:02:12');
INSERT INTO `worker_node` VALUES ('1129', '192.168.10.173', '1514373455926-77355', '2', '2017-12-27', '2017-12-27 11:17:37', '2017-12-27 11:17:37');
INSERT INTO `worker_node` VALUES ('1130', '192.168.10.173', '1514425960321-43559', '2', '2017-12-28', '2017-12-28 01:52:43', '2017-12-28 01:52:43');
INSERT INTO `worker_node` VALUES ('1131', '192.168.10.173', '1514426547847-91606', '2', '2017-12-28', '2017-12-28 02:02:30', '2017-12-28 02:02:30');
INSERT INTO `worker_node` VALUES ('1132', '192.168.10.173', '1514426802866-6075', '2', '2017-12-28', '2017-12-28 02:06:45', '2017-12-28 02:06:45');
INSERT INTO `worker_node` VALUES ('1133', '192.168.10.173', '1514427429325-54001', '2', '2017-12-28', '2017-12-28 02:17:12', '2017-12-28 02:17:12');
INSERT INTO `worker_node` VALUES ('1134', '192.168.10.173', '1514440372146-24158', '2', '2017-12-28', '2017-12-28 05:52:52', '2017-12-28 05:52:52');
INSERT INTO `worker_node` VALUES ('1135', '192.168.10.173', '1514443871223-9036', '2', '2017-12-28', '2017-12-28 06:51:11', '2017-12-28 06:51:11');
INSERT INTO `worker_node` VALUES ('1136', '192.168.10.173', '1514444129869-70495', '2', '2017-12-28', '2017-12-28 06:55:29', '2017-12-28 06:55:29');
INSERT INTO `worker_node` VALUES ('1137', '192.168.10.173', '1514444241581-80242', '2', '2017-12-28', '2017-12-28 06:57:21', '2017-12-28 06:57:21');
INSERT INTO `worker_node` VALUES ('1138', '192.168.10.173', '1514444576150-13487', '2', '2017-12-28', '2017-12-28 07:02:55', '2017-12-28 07:02:55');
INSERT INTO `worker_node` VALUES ('1139', '192.168.10.173', '1514444638521-89099', '2', '2017-12-28', '2017-12-28 07:03:58', '2017-12-28 07:03:58');
INSERT INTO `worker_node` VALUES ('1140', '192.168.10.173', '1514444813158-26562', '2', '2017-12-28', '2017-12-28 07:06:52', '2017-12-28 07:06:52');
INSERT INTO `worker_node` VALUES ('1141', '192.168.10.173', '1514447649139-59743', '2', '2017-12-28', '2017-12-28 07:54:08', '2017-12-28 07:54:08');
INSERT INTO `worker_node` VALUES ('1142', '192.168.10.173', '1514449827545-28870', '2', '2017-12-28', '2017-12-28 08:30:27', '2017-12-28 08:30:27');
INSERT INTO `worker_node` VALUES ('1143', '192.168.10.173', '1514450212029-15509', '2', '2017-12-28', '2017-12-28 08:36:51', '2017-12-28 08:36:51');
INSERT INTO `worker_node` VALUES ('1144', '192.168.10.173', '1514450509659-49582', '2', '2017-12-28', '2017-12-28 08:41:49', '2017-12-28 08:41:49');
INSERT INTO `worker_node` VALUES ('1145', '192.168.10.173', '1514450643331-50882', '2', '2017-12-28', '2017-12-28 08:44:03', '2017-12-28 08:44:03');
INSERT INTO `worker_node` VALUES ('1146', '192.168.10.173', '1514450835613-84327', '2', '2017-12-28', '2017-12-28 08:47:15', '2017-12-28 08:47:15');
INSERT INTO `worker_node` VALUES ('1147', '192.168.10.173', '1514450964308-93280', '2', '2017-12-28', '2017-12-28 08:49:24', '2017-12-28 08:49:24');
INSERT INTO `worker_node` VALUES ('1148', '192.168.10.173', '1514451182867-6887', '2', '2017-12-28', '2017-12-28 08:53:02', '2017-12-28 08:53:02');
INSERT INTO `worker_node` VALUES ('1149', '192.168.10.173', '1514451745975-53117', '2', '2017-12-28', '2017-12-28 09:02:25', '2017-12-28 09:02:25');
INSERT INTO `worker_node` VALUES ('1150', '192.168.10.173', '1514452012533-25946', '2', '2017-12-28', '2017-12-28 09:06:52', '2017-12-28 09:06:52');
INSERT INTO `worker_node` VALUES ('1151', '192.168.10.173', '1514452434470-87735', '2', '2017-12-28', '2017-12-28 09:13:54', '2017-12-28 09:13:54');
INSERT INTO `worker_node` VALUES ('1152', '192.168.10.173', '1514452610104-85134', '2', '2017-12-28', '2017-12-28 09:16:49', '2017-12-28 09:16:49');
INSERT INTO `worker_node` VALUES ('1153', '192.168.10.173', '1514940809669-26695', '2', '2018-01-03', '2018-01-03 00:53:31', '2018-01-03 00:53:31');
INSERT INTO `worker_node` VALUES ('1154', '2001:0:9d38:953c:1094:3e19:3f57:f59a', '1515459795575-1218', '2', '2018-01-09', '2018-01-09 01:03:14', '2018-01-09 01:03:14');
INSERT INTO `worker_node` VALUES ('1155', '2001:0:9d38:953c:1094:3e19:3f57:f59a', '1515460357125-88107', '2', '2018-01-09', '2018-01-09 01:12:36', '2018-01-09 01:12:36');
INSERT INTO `worker_node` VALUES ('1156', '2001:0:9d38:953c:1094:3e19:3f57:f59a', '1515470133497-68243', '2', '2018-01-09', '2018-01-09 03:55:32', '2018-01-09 03:55:32');
INSERT INTO `worker_node` VALUES ('1157', '192.168.56.1', '1515545693019-31829', '2', '2018-01-10', '2018-01-10 00:54:50', '2018-01-10 00:54:50');
INSERT INTO `worker_node` VALUES ('1158', '192.168.56.1', '1515549308117-81966', '2', '2018-01-10', '2018-01-10 01:55:05', '2018-01-10 01:55:05');
INSERT INTO `worker_node` VALUES ('1159', '192.168.56.1', '1515660445207-39210', '2', '2018-01-11', '2018-01-11 08:47:23', '2018-01-11 08:47:23');
INSERT INTO `worker_node` VALUES ('1160', '192.168.10.173', '1516157963862-89025', '2', '2018-01-17', '2018-01-17 02:59:25', '2018-01-17 02:59:25');
INSERT INTO `worker_node` VALUES ('1161', '192.168.10.173', '1516328180229-84177', '2', '2018-01-19', '2018-01-19 02:16:20', '2018-01-19 02:16:20');
INSERT INTO `worker_node` VALUES ('1162', '192.168.10.173', '1516408980709-59982', '2', '2018-01-20', '2018-01-20 00:43:02', '2018-01-20 00:43:02');
INSERT INTO `worker_node` VALUES ('1163', '192.168.10.173', '1516599000712-94525', '2', '2018-01-22', '2018-01-22 05:30:00', '2018-01-22 05:30:00');
INSERT INTO `worker_node` VALUES ('1164', '192.168.10.173', '1516668517864-56421', '2', '2018-01-23', '2018-01-23 00:48:38', '2018-01-23 00:48:38');
INSERT INTO `worker_node` VALUES ('1165', '192.168.10.173', '1516674928715-56685', '2', '2018-01-23', '2018-01-23 02:35:29', '2018-01-23 02:35:29');
INSERT INTO `worker_node` VALUES ('1166', '192.168.10.173', '1516675115435-51731', '2', '2018-01-23', '2018-01-23 02:38:36', '2018-01-23 02:38:36');
INSERT INTO `worker_node` VALUES ('1167', '192.168.10.173', '1516675900920-37276', '2', '2018-01-23', '2018-01-23 02:51:41', '2018-01-23 02:51:41');
INSERT INTO `worker_node` VALUES ('1168', '192.168.10.173', '1516677127367-82293', '2', '2018-01-23', '2018-01-23 03:12:08', '2018-01-23 03:12:08');
INSERT INTO `worker_node` VALUES ('1169', '192.168.10.173', '1516677570119-19202', '2', '2018-01-23', '2018-01-23 03:19:31', '2018-01-23 03:19:31');
INSERT INTO `worker_node` VALUES ('1170', '192.168.10.173', '1516677875522-67513', '2', '2018-01-23', '2018-01-23 03:24:36', '2018-01-23 03:24:36');
INSERT INTO `worker_node` VALUES ('1171', '192.168.10.173', '1516678328434-206', '2', '2018-01-23', '2018-01-23 03:32:09', '2018-01-23 03:32:09');
INSERT INTO `worker_node` VALUES ('1172', '192.168.10.173', '1516678627834-72168', '2', '2018-01-23', '2018-01-23 03:37:08', '2018-01-23 03:37:08');
INSERT INTO `worker_node` VALUES ('1173', '192.168.10.173', '1516678801979-53372', '2', '2018-01-23', '2018-01-23 03:40:02', '2018-01-23 03:40:02');
INSERT INTO `worker_node` VALUES ('1174', '192.168.10.173', '1516678947392-44231', '2', '2018-01-23', '2018-01-23 03:42:28', '2018-01-23 03:42:28');
INSERT INTO `worker_node` VALUES ('1175', '192.168.10.173', '1516679152859-18867', '2', '2018-01-23', '2018-01-23 03:45:53', '2018-01-23 03:45:53');
INSERT INTO `worker_node` VALUES ('1176', '192.168.10.173', '1516679420362-26830', '2', '2018-01-23', '2018-01-23 03:50:21', '2018-01-23 03:50:21');
INSERT INTO `worker_node` VALUES ('1177', '192.168.10.173', '1516679577700-85195', '2', '2018-01-23', '2018-01-23 03:52:58', '2018-01-23 03:52:58');
INSERT INTO `worker_node` VALUES ('1178', '192.168.10.173', '1516679889877-9247', '2', '2018-01-23', '2018-01-23 03:58:10', '2018-01-23 03:58:10');
INSERT INTO `worker_node` VALUES ('1179', '192.168.10.173', '1516680158246-6266', '2', '2018-01-23', '2018-01-23 04:02:39', '2018-01-23 04:02:39');
INSERT INTO `worker_node` VALUES ('1180', '192.168.10.173', '1516680303874-13581', '2', '2018-01-23', '2018-01-23 04:05:04', '2018-01-23 04:05:04');
INSERT INTO `worker_node` VALUES ('1181', '192.168.10.173', '1516680575132-47985', '2', '2018-01-23', '2018-01-23 04:09:36', '2018-01-23 04:09:36');
INSERT INTO `worker_node` VALUES ('1182', '192.168.10.173', '1516680694451-22774', '2', '2018-01-23', '2018-01-23 04:11:35', '2018-01-23 04:11:35');
INSERT INTO `worker_node` VALUES ('1183', '192.168.10.173', '1516680799104-93679', '2', '2018-01-23', '2018-01-23 04:13:20', '2018-01-23 04:13:20');
INSERT INTO `worker_node` VALUES ('1184', '192.168.10.173', '1516681027383-61446', '2', '2018-01-23', '2018-01-23 04:17:08', '2018-01-23 04:17:08');
INSERT INTO `worker_node` VALUES ('1185', '192.168.10.173', '1516681317116-4844', '2', '2018-01-23', '2018-01-23 04:21:57', '2018-01-23 04:21:57');
INSERT INTO `worker_node` VALUES ('1186', '192.168.10.173', '1516686020463-27643', '2', '2018-01-23', '2018-01-23 05:40:21', '2018-01-23 05:40:21');
INSERT INTO `worker_node` VALUES ('1187', '192.168.10.173', '1516686506787-32860', '2', '2018-01-23', '2018-01-23 05:48:27', '2018-01-23 05:48:27');
INSERT INTO `worker_node` VALUES ('1188', '192.168.10.173', '1516686959514-15550', '2', '2018-01-23', '2018-01-23 05:56:00', '2018-01-23 05:56:00');
INSERT INTO `worker_node` VALUES ('1189', '192.168.10.173', '1516773996205-15994', '2', '2018-01-24', '2018-01-24 06:06:38', '2018-01-24 06:06:38');
INSERT INTO `worker_node` VALUES ('1190', '192.168.10.173', '1516774454286-10435', '2', '2018-01-24', '2018-01-24 06:14:16', '2018-01-24 06:14:16');
INSERT INTO `worker_node` VALUES ('1191', '192.168.10.173', '1516774598412-69652', '2', '2018-01-24', '2018-01-24 06:16:40', '2018-01-24 06:16:40');
INSERT INTO `worker_node` VALUES ('1192', '192.168.10.173', '1516774754244-77517', '2', '2018-01-24', '2018-01-24 06:19:16', '2018-01-24 06:19:16');
INSERT INTO `worker_node` VALUES ('1193', '192.168.10.173', '1516774882598-75989', '2', '2018-01-24', '2018-01-24 06:21:24', '2018-01-24 06:21:24');
INSERT INTO `worker_node` VALUES ('1194', '192.168.10.173', '1516775038015-59571', '2', '2018-01-24', '2018-01-24 06:23:59', '2018-01-24 06:23:59');
INSERT INTO `worker_node` VALUES ('1195', '192.168.10.173', '1516775339540-59674', '2', '2018-01-24', '2018-01-24 06:29:01', '2018-01-24 06:29:01');
INSERT INTO `worker_node` VALUES ('1196', '192.168.10.173', '1516775465053-57106', '2', '2018-01-24', '2018-01-24 06:31:06', '2018-01-24 06:31:06');
INSERT INTO `worker_node` VALUES ('1197', '192.168.10.173', '1516775711784-24165', '2', '2018-01-24', '2018-01-24 06:35:13', '2018-01-24 06:35:13');
INSERT INTO `worker_node` VALUES ('1198', '192.168.10.173', '1516780653622-8392', '2', '2018-01-24', '2018-01-24 07:57:35', '2018-01-24 07:57:35');
INSERT INTO `worker_node` VALUES ('1199', '192.168.10.173', '1516780815278-15286', '2', '2018-01-24', '2018-01-24 08:00:17', '2018-01-24 08:00:17');
INSERT INTO `worker_node` VALUES ('1200', '192.168.10.173', '1516780904170-59680', '2', '2018-01-24', '2018-01-24 08:01:46', '2018-01-24 08:01:46');
INSERT INTO `worker_node` VALUES ('1201', '192.168.10.173', '1516781358167-89596', '2', '2018-01-24', '2018-01-24 08:09:20', '2018-01-24 08:09:20');
INSERT INTO `worker_node` VALUES ('1202', '192.168.10.173', '1516782435947-20595', '2', '2018-01-24', '2018-01-24 08:27:17', '2018-01-24 08:27:17');
INSERT INTO `worker_node` VALUES ('1203', '192.168.10.173', '1516783270017-3533', '2', '2018-01-24', '2018-01-24 08:41:11', '2018-01-24 08:41:11');
INSERT INTO `worker_node` VALUES ('1204', '192.168.10.173', '1516784119941-14827', '2', '2018-01-24', '2018-01-24 08:55:21', '2018-01-24 08:55:21');
INSERT INTO `worker_node` VALUES ('1205', '192.168.10.173', '1516786210974-34760', '2', '2018-01-24', '2018-01-24 09:30:12', '2018-01-24 09:30:12');
INSERT INTO `worker_node` VALUES ('1206', '192.168.10.173', '1516787831475-25510', '2', '2018-01-24', '2018-01-24 09:57:13', '2018-01-24 09:57:13');
INSERT INTO `worker_node` VALUES ('1207', '192.168.10.173', '1516789312079-96961', '2', '2018-01-24', '2018-01-24 10:21:53', '2018-01-24 10:21:53');
