/*
Navicat MariaDB Data Transfer

Source Server         : 192.168.1.220
Source Server Version : 100208
Source Host           : 192.168.1.220:3306
Source Database       : ai_code

Target Server Type    : MariaDB
Target Server Version : 100208
File Encoding         : 65001

Date: 2018-10-24 20:57:51
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COMMENT='账户';

-- ----------------------------
-- Records of account
-- ----------------------------
INSERT INTO `account` VALUES ('1', '21218cca77804d2ba1', 'lixin', '21218cca77804d2ba1922c33e0151105');
INSERT INTO `account` VALUES ('2', '752166558724046848', 'likun', '21218cca77804d2ba1922c33e0151105');
INSERT INTO `account` VALUES ('3', '752216809841434624', '111111', '96e79218965eb72c92a549dd5a330112');
INSERT INTO `account` VALUES ('4', '752217239338164224', '666666', 'f379eaf3c831b04de153469d1bec345e');
INSERT INTO `account` VALUES ('5', '752225897992232960', '888888', '21218cca77804d2ba1922c33e0151105');
INSERT INTO `account` VALUES ('6', '752245998439186432', '999999', '52c69e3a57331081823331c4e69d3f2e');
INSERT INTO `account` VALUES ('7', '946523569854291968', 'ada', '8c8d357b5e872bbacd45197626bd5759');
INSERT INTO `account` VALUES ('8', '949393243663187968', 'lihaizhong', '21218cca77804d2ba1922c33e0151105');
INSERT INTO `account` VALUES ('9', '949395167808536576', 'wan', 'e10adc3949ba59abbe56e057f20f883e');
INSERT INTO `account` VALUES ('11', '959610713162645504', 'swx', 'e10adc3949ba59abbe56e057f20f883e');
INSERT INTO `account` VALUES ('12', '1019673528695439360', 'moyansen@yeah.net', 'fd78a23eed62c7622265afe9f069483b');
INSERT INTO `account` VALUES ('13', '1053385276757663744', 'admin', 'e10adc3949ba59abbe56e057f20f883e');
INSERT INTO `account` VALUES ('14', '1102552894132822016', 'jane', 'e10adc3949ba59abbe56e057f20f883e');

-- ----------------------------
-- Table structure for display_attribute
-- ----------------------------
DROP TABLE IF EXISTS `display_attribute`;
CREATE TABLE `display_attribute` (
  `id` bigint(20) NOT NULL,
  `mapFieldColumnCode` varchar(64) NOT NULL COMMENT '字段编码',
  `isRequired` varchar(2) DEFAULT NULL COMMENT '是否必填 Y,N',
  `isInsert` varchar(2) DEFAULT NULL COMMENT '是否插入',
  `isDeleteCondition` varchar(2) DEFAULT NULL COMMENT '是否是删除条件',
  `isAllowUpdate` varchar(2) DEFAULT NULL COMMENT '是否允许修改 Y,N',
  `isListPageDisplay` varchar(2) DEFAULT NULL COMMENT '是否分页列表显示 Y,N',
  `isDetailPageDisplay` varchar(2) DEFAULT NULL COMMENT '是否详情页显示 Y,N',
  `isQueryRequired` varchar(2) DEFAULT NULL COMMENT '是否是查询条件 Y,N',
  `isLineNew` varchar(2) DEFAULT NULL COMMENT '是否换行',
  `matchType` varchar(16) DEFAULT NULL COMMENT '匹配方式 =,!=,>=,<=,>,<,like,左like，右like,between,in',
  `displayType` varchar(32) DEFAULT NULL COMMENT '显示格式 自动完成 Autocomplete,级联选择 Cascader,日期选择框 DatePicker,时间选择 TimePicker,输入框 Input,数字输入框 InputNumber,提及 Mention,邮箱 Email，电话Phone，手机Mobile，备注说明 Summary，选择器 Select，单选 Radio，多选框 Checkbox,评分 Rate,加载展位图 Skeleton,滑动输入条 Silder，开关 Switch,穿梭框 Transfer,选择树 TreeSelect ,上传 Upload,头像 Avatar',
  `displayName` varchar(64) DEFAULT NULL COMMENT '显示列名称',
  `displayNo` int(11) DEFAULT NULL COMMENT '显示顺序',
  `fieldValidationMode` varchar(32) DEFAULT NULL COMMENT '字段验证方式',
  `validateText` varchar(64) DEFAULT NULL COMMENT '验证提示语',
  `displayCss` varchar(32) DEFAULT NULL COMMENT '显示css样式',
  PRIMARY KEY (`id`,`mapFieldColumnCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='显示属性';

-- ----------------------------
-- Records of display_attribute
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
  `gitHome` varchar(256) DEFAULT NULL,
  `account` varchar(32) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `isPublic` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COMMENT='框架技术池';

-- ----------------------------
-- Records of frameworks
-- ----------------------------
INSERT INTO `frameworks` VALUES ('5', '123456', 'ssm-redis-swagger-lombok', 'ssm+redis+swagger+lombok', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '', 'Y');
INSERT INTO `frameworks` VALUES ('6', '456789', 'ssm-dubbo-redis-swagger-lombok-disconf-sentry', '此项目整合了ssm+dubbo+redis+swagger+lombok几大框架便于实现分布式，采用gradle管理构建项目，lombok使用请安装ide的插件并进行设置即可完成javabean的简化', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', null, 'N');
INSERT INTO `frameworks` VALUES ('7', '74108520', 'ssh+redis+swagger+lombok', 'ssh+redis+swagger+lombok', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', null, null);
INSERT INTO `frameworks` VALUES ('8', '888888', 'spring-cloud-redis-lombok-sentry', '此框架整合了spring-cloud、redis、lombok，swagger等最新版本，可以方便的实现单项目开发，分布式开发的需求；需要注意的是需要对spring boot有所了解将有助于使用此套框架，框架自己包含了工具包，配置好了分层，需要修改数据库连接地址以及一些自己内网的信息接口生成后直接运行使用，免测试。', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', null, 'Y');
INSERT INTO `frameworks` VALUES ('9', '999999', 'ssm-redis-swagger-lombok-pitop', 'ssm+redis+swagger+lombok', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '', 'Y');
INSERT INTO `frameworks` VALUES ('10', '10101010', 'springboot-redis-swagger-lombok', 'springboot+redis+swagger+lombok', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', null, 'Y');
INSERT INTO `frameworks` VALUES ('25', '1081629222214967296', 'angular-template', 'angular-template', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '', 'Y');
INSERT INTO `frameworks` VALUES ('26', '1081629222214967297', 'springboot-redis-swagger-lombok-frontend', 'springboot-redis-swagger-lombok-frontend', 'https://gitee.com/helixin/aicode_template.git', 'hegaoye@qq.com', '', 'Y');

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
) ENGINE=InnoDB AUTO_INCREMENT=161874 DEFAULT CHARSET=utf8mb4 COMMENT='框架配置文件模板';

-- ----------------------------
-- Records of frameworks_template
-- ----------------------------

-- ----------------------------
-- Table structure for map_class_table
-- ----------------------------
DROP TABLE IF EXISTS `map_class_table`;
CREATE TABLE `map_class_table` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '映射编码',
  `tableName` varchar(64) DEFAULT NULL COMMENT '表名',
  `className` varchar(64) DEFAULT NULL COMMENT '类名',
  `notes` varchar(512) DEFAULT '' COMMENT '注释',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=1191 DEFAULT CHARSET=utf8mb4 COMMENT='类表映射信息';

-- ----------------------------
-- Records of map_class_table
-- ----------------------------
INSERT INTO `map_class_table` VALUES ('1140', '1109538417921712129', 'count', 'Count', '统计数据');
INSERT INTO `map_class_table` VALUES ('1141', '1109538417921712135', 'jlb_ore_log', 'JlbOreLog', '金兰宝矿石转账记录');
INSERT INTO `map_class_table` VALUES ('1142', '1109538435101581312', 'setting', 'Setting', '设置');
INSERT INTO `map_class_table` VALUES ('1143', '1109538435101581318', 'user_deposit', 'UserDeposit', '用户的押金');
INSERT INTO `map_class_table` VALUES ('1149', '1122747761639841792', 'repair_feedback', 'RepairFeedback', '维修反馈');
INSERT INTO `map_class_table` VALUES ('1150', '1122747761639841802', 'supplier', 'Supplier', '供应商');
INSERT INTO `map_class_table` VALUES ('1151', '1122747761639841826', 'test_record', 'TestRecord', '检测记录');
INSERT INTO `map_class_table` VALUES ('1152', '1122747761639841839', 'test_unit', 'TestUnit', '测试件，测试硬件单元');
INSERT INTO `map_class_table` VALUES ('1153', '1122790092837519361', 'basic_area', 'BasicArea', '行政区域');
INSERT INTO `map_class_table` VALUES ('1154', '1122790092837519370', 'basic_area_idcard', 'BasicAreaIdcard', '基础区域管理-行政区域代码');
INSERT INTO `map_class_table` VALUES ('1155', '1123098626108203008', 'box', 'Box', '箱库存');
INSERT INTO `map_class_table` VALUES ('1156', '1123098626108203018', 'order', 'Order', '订单');
INSERT INTO `map_class_table` VALUES ('1157', '1123098626108203034', 'repair_staff', 'RepairStaff', '维修账户');
INSERT INTO `map_class_table` VALUES ('1168', '1124295015018373121', 'box', 'Box', '箱库存');
INSERT INTO `map_class_table` VALUES ('1169', '1124295015018373130', 'box_item', 'BoxItem', '装箱产品');
INSERT INTO `map_class_table` VALUES ('1170', '1124295015018373135', 'customer', 'Customer', '客户信息');
INSERT INTO `map_class_table` VALUES ('1171', '1124295015018373147', 'order', 'Order', '订单');
INSERT INTO `map_class_table` VALUES ('1172', '1124295032198242304', 'order_detail', 'OrderDetail', '订单详情');
INSERT INTO `map_class_table` VALUES ('1175', '1127246516544208896', 'item', 'Item', '产品');
INSERT INTO `map_class_table` VALUES ('1176', '1127246533724078080', 'item_category', 'ItemCategory', '分类');
INSERT INTO `map_class_table` VALUES ('1177', '1127246533724078084', 'item_order', 'ItemOrder', '订单');
INSERT INTO `map_class_table` VALUES ('1178', '1127246533724078103', 'item_pay_method_price', 'ItemPayMethodPrice', '支付方式价格');
INSERT INTO `map_class_table` VALUES ('1179', '1127246533724078109', 'pay_mothed', 'PayMothed', '支付方式分类');
INSERT INTO `map_class_table` VALUES ('1180', '1127246533724078113', 'user_address', 'UserAddress', '用户收货地址');
INSERT INTO `map_class_table` VALUES ('1181', '1130295462288039937', 'zx_kuang_fen', 'ZxKuangFen', '');
INSERT INTO `map_class_table` VALUES ('1183', '1133516533141241857', 'order_trace', 'OrderTrace', '订单追踪');
INSERT INTO `map_class_table` VALUES ('1184', '1133635057058742272', 'box', 'Box', '箱库存');
INSERT INTO `map_class_table` VALUES ('1185', '1133635057058742281', 'box_item', 'BoxItem', '装箱产品');
INSERT INTO `map_class_table` VALUES ('1186', '1133635057058742286', 'customer', 'Customer', '客户信息');
INSERT INTO `map_class_table` VALUES ('1187', '1133635057058742298', 'customer_address', 'CustomerAddress', '客户地址');
INSERT INTO `map_class_table` VALUES ('1188', '1133635057058742302', 'order', 'Order', '订单');
INSERT INTO `map_class_table` VALUES ('1189', '1133635057058742322', 'order_detail', 'OrderDetail', '订单详情');
INSERT INTO `map_class_table` VALUES ('1190', '1133635057058742330', 'order_trace', 'OrderTrace', '订单追踪');

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
  `notes` varchar(512) DEFAULT '' COMMENT '注释',
  `defaultValue` varchar(32) DEFAULT NULL COMMENT '字段默认值',
  `isPrimaryKey` varchar(1) DEFAULT 'N' COMMENT '是否是主键',
  `isDate` varchar(1) DEFAULT 'N' COMMENT '是否是时间类型',
  `isState` varchar(1) DEFAULT 'N' COMMENT '是否是状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10458 DEFAULT CHARSET=utf8mb4 COMMENT='字段属性映射信息';

-- ----------------------------
-- Records of map_field_column
-- ----------------------------
INSERT INTO `map_field_column` VALUES ('9939', '1109538417921712129', '1109538417921712130', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9940', '1109538417921712129', '1109538417921712131', 'totalJlb', 'totalJlb', 'varchar', 'java.lang.String', '金兰宝产生总数量', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9941', '1109538417921712129', '1109538417921712132', 'costJlb', 'costJlb', 'varchar', 'java.lang.String', '金兰宝消耗的总数量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9942', '1109538417921712129', '1109538417921712133', 'totalOre', 'totalOre', 'varchar', 'java.lang.String', '矿石产生的总数量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9943', '1109538417921712129', '1109538417921712134', 'costOre', 'costOre', 'varchar', 'java.lang.String', '矿石消耗的总数量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9944', '1109538417921712135', '1109538417921712136', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9945', '1109538417921712135', '1109538417921712137', 'outUserCode', 'outUserCode', 'varchar', 'java.lang.String', '转出账户编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9946', '1109538417921712135', '1109538417921712138', 'inUserCode', 'inUserCode', 'varchar', 'java.lang.String', '转入账户编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9947', '1109538417921712135', '1109538417921712139', 'num', 'num', 'int', 'java.lang.Integer', '数量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9948', '1109538417921712135', '1109538417921712140', 'type', 'type', 'varchar', 'java.lang.String', '类型 金兰宝  JLB ,矿石 ORE', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9949', '1109538417921712135', '1109538417921712141', 'event', 'event', 'char', 'java.lang.String', '事件：转账 Transfer，复投 Re_Cast，押金 Deposit，押金返还 Deposit_Return', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9950', '1109538417921712135', '1109538417921712142', 'beforeOutNum', 'beforeOutNum', 'int', 'java.lang.Integer', '转出前转出用户数量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9951', '1109538417921712135', '1109538417921712143', 'afterOutNum', 'afterOutNum', 'int', 'java.lang.Integer', '转出后转出用户数量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9952', '1109538417921712135', '1109538417921712144', 'beforeInNum', 'beforeInNum', 'int', 'java.lang.Integer', '转入前转入用户数量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9953', '1109538417921712135', '1109538417921712145', 'afterInNum', 'afterInNum', 'int', 'java.lang.Integer', '转入后转入用户数量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9954', '1109538417921712135', '1109538417921712146', 'createTime', 'createTime', 'datetime', 'java.util.Date', '转账时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('9955', '1109538435101581312', '1109538435101581313', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9956', '1109538435101581312', '1109538435101581314', 'k', 'k', 'varchar', 'java.lang.String', '键', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9957', '1109538435101581312', '1109538435101581315', 'v', 'v', 'varchar', 'java.lang.String', '值', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9958', '1109538435101581312', '1109538435101581316', 'summary', 'summary', 'varchar', 'java.lang.String', '备注 128个汉字', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9959', '1109538435101581312', '1109538435101581317', 'ver', 'ver', 'int', 'java.lang.Integer', '版本', '1', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9960', '1109538435101581318', '1109538435101581319', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9961', '1109538435101581318', '1109538435101581320', 'user_code', 'userCode', 'varchar', 'java.lang.String', '用户编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9962', '1109538435101581318', '1109538435101581321', 'deposit', 'deposit', 'int', 'java.lang.Integer', '用户押金', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9963', '1109538435101581318', '1109538435101581322', 'star_level', 'starLevel', 'int', 'java.lang.Integer', '星级', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9964', '1109538435101581318', '1109538435101581323', 'state', 'state', 'varchar', 'java.lang.String', '状态：可返还 Returnable，冻结 Frozen，已返还 Returned', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('9965', '1109538435101581318', '1109538435101581324', 'returnTime', 'returnTime', 'datetime', 'java.util.Date', '返还时间 一年以后的时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10002', '1122747761639841792', '1122747761639841793', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10003', '1122747761639841792', '1122747761639841794', 'factoryCode', 'factoryCode', 'varchar', 'java.lang.String', '工厂编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10004', '1122747761639841792', '1122747761639841795', 'code', 'code', 'varchar', 'java.lang.String', '维修反馈编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10005', '1122747761639841792', '1122747761639841796', 'testUnitCode', 'testUnitCode', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10006', '1122747761639841792', '1122747761639841797', 'staffCode', 'staffCode', 'varchar', 'java.lang.String', '员工编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10007', '1122747761639841792', '1122747761639841798', 'errorCode', 'errorCode', 'varchar', 'java.lang.String', '错误类型编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10008', '1122747761639841792', '1122747761639841799', 'state', 'state', 'varchar', 'java.lang.String', '状态：修复repaired，损坏broken', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10009', '1122747761639841792', '1122747761639841800', 'feedback', 'feedback', 'varchar', 'java.lang.String', '反馈信息', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10010', '1122747761639841792', '1122747761639841801', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10011', '1122747761639841802', '1122747761639841803', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10012', '1122747761639841802', '1122747761639841804', 'code', 'code', 'varchar', 'java.lang.String', '编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10013', '1122747761639841802', '1122747761639841805', 'name', 'name', 'varchar', 'java.lang.String', '供应商名称 最长32个汉字 64个英文', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10014', '1122747761639841802', '1122747761639841806', 'englishName', 'englishName', 'varchar', 'java.lang.String', '英文名', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10015', '1122747761639841802', '1122747761639841807', 'contacts', 'contacts', 'varchar', 'java.lang.String', '联系人', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10016', '1122747761639841802', '1122747761639841808', 'bank', 'bank', 'varchar', 'java.lang.String', '最长32个汉字 64个英文', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10017', '1122747761639841802', '1122747761639841809', 'bank_account', 'bankAccount', 'varchar', 'java.lang.String', '开户行账户', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10018', '1122747761639841802', '1122747761639841810', 'payment_term', 'paymentTerm', 'varchar', 'java.lang.String', '支付方式 ', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10019', '1122747761639841802', '1122747761639841811', 'credit_rating', 'creditRating', 'int', 'java.lang.Integer', '信用等级', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10020', '1122747761639841802', '1122747761639841812', 'supplierCategoryCode', 'supplierCategoryCode', 'varchar', 'java.lang.String', '供应商类型编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10021', '1122747761639841802', '1122747761639841813', 'website', 'website', 'varchar', 'java.lang.String', '公司官网', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10022', '1122747761639841802', '1122747761639841814', 'address', 'address', 'varchar', 'java.lang.String', '地址 64个汉字，128个英文字符', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10023', '1122747761639841802', '1122747761639841815', 'english_address', 'englishAddress', 'varchar', 'java.lang.String', '英文地址', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10024', '1122747761639841802', '1122747761639841816', 'email', 'email', 'varchar', 'java.lang.String', '电子邮箱', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10025', '1122747761639841802', '1122747761639841817', 'fax', 'fax', 'varchar', 'java.lang.String', '传真号', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10026', '1122747761639841802', '1122747761639841818', 'mobile', 'mobile', 'varchar', 'java.lang.String', '移动电话', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10027', '1122747761639841802', '1122747761639841819', 'telephone', 'telephone', 'varchar', 'java.lang.String', '固定电话', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10028', '1122747761639841802', '1122747761639841820', 'zip_code', 'zipCode', 'varchar', 'java.lang.String', '邮编', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10029', '1122747761639841802', '1122747761639841821', 'company_nature', 'companyNature', 'varchar', 'java.lang.String', '公司性质 国营 state_run ,民营privately operated，合资joint venture，外资 foreign capital', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10030', '1122747761639841802', '1122747761639841822', 'state', 'state', 'varchar', 'java.lang.String', '状态：启用 enable,禁用 disable \r\n            ', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10031', '1122747761639841802', '1122747761639841823', 'summary', 'summary', 'varchar', 'java.lang.String', '备注 128 个汉字说明', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10032', '1122747761639841802', '1122747761639841824', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10033', '1122747761639841802', '1122747761639841825', 'updateTime', 'updateTime', 'datetime', 'java.util.Date', '更新时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10034', '1122747761639841826', '1122747761639841827', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10035', '1122747761639841826', '1122747761639841828', 'factoryCode', 'factoryCode', 'varchar', 'java.lang.String', '工厂编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10036', '1122747761639841826', '1122747761639841829', 'code', 'code', 'varchar', 'java.lang.String', '记录编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10037', '1122747761639841826', '1122747761639841830', 'staffCode', 'staffCode', 'varchar', 'java.lang.String', '员工编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10038', '1122747761639841826', '1122747761639841831', 'jobNumber', 'jobNumber', 'varchar', 'java.lang.String', '工号', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10039', '1122747761639841826', '1122747761639841832', 'testUnitCode', 'testUnitCode', 'varchar', 'java.lang.String', '测试件编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10040', '1122747761639841826', '1122747761639841833', 'testOptionCode', 'testOptionCode', 'varchar', 'java.lang.String', '测试项编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10041', '1122747761639841826', '1122747761639841834', 'staffName', 'staffName', 'varchar', 'java.lang.String', '员工姓名', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10042', '1122747761639841826', '1122747761639841835', 'factorySN', 'factorySn', 'varchar', 'java.lang.String', '工厂sn码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10043', '1122747761639841826', '1122747761639841836', 'testOption', 'testOption', 'varchar', 'java.lang.String', '测试项', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10044', '1122747761639841826', '1122747761639841837', 'state', 'state', 'varchar', 'java.lang.String', '状态 成功 TestSuccess，失败 TestFailed', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10045', '1122747761639841826', '1122747761639841838', 'testTime', 'testTime', 'datetime', 'java.util.Date', '测试时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10046', '1122747761639841839', '1122747761639841840', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10047', '1122747761639841839', '1122747761639841841', 'factoryCode', 'factoryCode', 'varchar', 'java.lang.String', '工厂编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10048', '1122747761639841839', '1122747761639841842', 'sampleCode', 'sampleCode', 'varchar', 'java.lang.String', '样品编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10049', '1122747761639841839', '1122747761639841843', 'factorySN', 'factorySn', 'varchar', 'java.lang.String', '工厂编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10050', '1122747761639841839', '1122747761639841844', 'sn', 'sn', 'varchar', 'java.lang.String', '产品sn', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10051', '1122747761639841839', '1122747761639841845', 'code', 'code', 'varchar', 'java.lang.String', '编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10052', '1122747761639841839', '1122747761639841846', 'state', 'state', 'varchar', 'java.lang.String', '状态 创建 Create，测试通过TestSuccess，测试失败TestFailed，检修 Repair', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10053', '1122747761639841839', '1122747761639841847', 'syncState', 'syncState', 'varchar', 'java.lang.String', '同步状态：禁止同步 Ban, 准备同步 Ready,同步中 Sync,同步错误 Error,已经同步 Synced ', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10054', '1122747761639841839', '1122747761639841848', 'jobnumber', 'jobnumber', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10055', '1122747761639841839', '1122747761639841849', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建日期', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10056', '1122747761639841839', '1122747761639841850', 'staffCode', 'staffCode', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10057', '1122747761639841839', '1122747761639841851', 'updateTime', 'updateTime', 'datetime', 'java.util.Date', '更新时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10058', '1122790092837519361', '1122790092837519362', 'area_code', 'areaCode', 'char', 'java.lang.String', '行政区划编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10059', '1122790092837519361', '1122790092837519363', 'area_name', 'areaName', 'varchar', 'java.lang.String', '名称', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10060', '1122790092837519361', '1122790092837519364', 'full_name', 'fullName', 'varchar', 'java.lang.String', '行政区划全称', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10061', '1122790092837519361', '1122790092837519365', 'province', 'province', 'char', 'java.lang.String', '省', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10062', '1122790092837519361', '1122790092837519366', 'city', 'city', 'char', 'java.lang.String', '市', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10063', '1122790092837519361', '1122790092837519367', 'county', 'county', 'char', 'java.lang.String', '县区', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10064', '1122790092837519361', '1122790092837519368', 'town', 'town', 'char', 'java.lang.String', '镇办事处', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10065', '1122790092837519361', '1122790092837519369', 'level', 'level', 'tinyint', 'java.lang.Byte', '等级 省1 市2 县3 乡4 村5', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10066', '1122790092837519370', '1122790092837519371', 'area_code', 'areaCode', 'char', 'java.lang.String', '行政区划编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10067', '1122790092837519370', '1122790092837519372', 'area_name', 'areaName', 'varchar', 'java.lang.String', '行政区域名称', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10068', '1122790092837519370', '1122790092837519373', 'full_name', 'fullName', 'varchar', 'java.lang.String', '区域全名称', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10069', '1122790092837519370', '1122790092837519374', 'province', 'province', 'char', 'java.lang.String', '省级代码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10070', '1122790092837519370', '1122790092837519375', 'city', 'city', 'char', 'java.lang.String', '市级代码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10071', '1122790092837519370', '1122790092837519376', 'level', 'level', 'tinyint', 'java.lang.Byte', '地行政区域级别 (0 中国, 1 省、2 市、3 县区、4镇/办事处 5村/居委会 )', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10072', '1122790092837519370', '1122790092837519377', 'is_new', 'isNew', 'char', 'java.lang.String', '是否是新规划的行政区域代码 (N否， Y是)', '\'N\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10073', '1122790092837519370', '1122790092837519378', 'new_area_code', 'newAreaCode', 'char', 'java.lang.String', '新的行政区域代码，如果is_new=0时，对应area_code', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10074', '1123098626108203008', '1123098626108203009', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10075', '1123098626108203008', '1123098626108203010', 'code', 'code', 'varchar', 'java.lang.String', '编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10076', '1123098626108203008', '1123098626108203011', 'factoryCode', 'factoryCode', 'varchar', 'java.lang.String', '工厂编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10077', '1123098626108203008', '1123098626108203012', 'sn', 'sn', 'varchar', 'java.lang.String', '箱SN', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10078', '1123098626108203008', '1123098626108203013', 'jobNumber', 'jobNumber', 'varchar', 'java.lang.String', '员工号', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10079', '1123098626108203008', '1123098626108203014', 'syncState', 'syncState', 'varchar', 'java.lang.String', '同步状态： \r\n            准备同步 Ready,同步中 Sync,同步错误 Error,已经同步 Synced', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10080', '1123098626108203008', '1123098626108203015', 'state', 'state', 'varchar', 'java.lang.String', '状态：入库 Storage，已出库 Issue', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10081', '1123098626108203008', '1123098626108203016', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10082', '1123098626108203008', '1123098626108203017', 'updateTime', 'updateTime', 'datetime', 'java.util.Date', '更新时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10083', '1123098626108203018', '1123098626108203019', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10084', '1123098626108203018', '1123098626108203020', 'code', 'code', 'varchar', 'java.lang.String', '编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10085', '1123098626108203018', '1123098626108203021', 'factoryCode', 'factoryCode', 'varchar', 'java.lang.String', '工厂编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10086', '1123098626108203018', '1123098626108203022', 'order_no', 'orderNo', 'varchar', 'java.lang.String', '订单编号', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10087', '1123098626108203018', '1123098626108203023', 'customerCode', 'customerCode', 'varchar', 'java.lang.String', '客户编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10088', '1123098626108203018', '1123098626108203024', 'item_sku', 'itemSku', 'varchar', 'java.lang.String', '产品sku', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10089', '1123098626108203018', '1123098626108203025', 'item_summary', 'itemSummary', 'varchar', 'java.lang.String', '产品说明', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10090', '1123098626108203018', '1123098626108203026', 'item_total_num', 'itemTotalNum', 'int', 'java.lang.Integer', '产品总量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10091', '1123098626108203018', '1123098626108203027', 'item_box_num', 'itemBoxNum', 'int', 'java.lang.Integer', '产品箱数', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10092', '1123098626108203018', '1123098626108203028', 'expect_ship_date', 'expectShipDate', 'datetime', 'java.util.Date', '期望出单日期', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10093', '1123098626108203018', '1123098626108203029', 'order_date', 'orderDate', 'varchar', 'java.lang.String', '下单日期', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10094', '1123098626108203018', '1123098626108203030', 'ship_time', 'shipTime', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10095', '1123098626108203018', '1123098626108203031', 'state', 'state', 'varchar', 'java.lang.String', '状态：已下单 Order，待发货 Pending_Delivery，已发货Shipped,取消 Cancel,延期 Delay', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10096', '1123098626108203018', '1123098626108203032', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10097', '1123098626108203018', '1123098626108203033', 'updateTime', 'updateTime', 'datetime', 'java.util.Date', '更新时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10098', '1123098626108203034', '1123098626108203035', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10099', '1123098626108203034', '1123098626108203036', 'code', 'code', 'varchar', 'java.lang.String', '编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10100', '1123098626108203034', '1123098626108203037', 'email', 'email', 'varchar', 'java.lang.String', '邮箱', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10101', '1123098626108203034', '1123098626108203038', 'password', 'password', 'varchar', 'java.lang.String', '密码 md5 加密', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10102', '1123098626108203034', '1123098626108203039', 'jobnumber', 'jobnumber', 'varchar', 'java.lang.String', '工号 0000四位格式', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10103', '1123098626108203034', '1123098626108203040', 'last_name', 'lastName', 'varchar', 'java.lang.String', '姓', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10104', '1123098626108203034', '1123098626108203041', 'first_name', 'firstName', 'varchar', 'java.lang.String', '名', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10105', '1123098626108203034', '1123098626108203042', 'gender', 'gender', 'varchar', 'java.lang.String', '性别：男 Male，女 Female，其他 Other', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10106', '1123098626108203034', '1123098626108203043', 'avatar', 'avatar', 'varchar', 'java.lang.String', '头像', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10107', '1123098626108203034', '1123098626108203044', 'country_code', 'countryCode', 'varchar', 'java.lang.String', '国家代码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10108', '1123098626108203034', '1123098626108203045', 'language', 'language', 'varchar', 'java.lang.String', '语言', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10109', '1123098626108203034', '1123098626108203046', 'timezone', 'timezone', 'varchar', 'java.lang.String', '所在时区', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10110', '1123098626108203034', '1123098626108203047', 'state', 'state', 'varchar', 'java.lang.String', '状态：激活[Activate]，冻结[Frozen]，删除[Delete]', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10111', '1123098626108203034', '1123098626108203048', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10112', '1123098626108203034', '1123098626108203049', 'updateTime', 'updateTime', 'datetime', 'java.util.Date', '更新时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10212', '1124295015018373121', '1124295015018373122', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10213', '1124295015018373121', '1124295015018373123', 'code', 'code', 'varchar', 'java.lang.String', '编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10214', '1124295015018373121', '1124295015018373124', 'sn', 'sn', 'varchar', 'java.lang.String', '箱SN', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10215', '1124295015018373121', '1124295015018373125', 'jobNumber', 'jobNumber', 'varchar', 'java.lang.String', '员工号', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10216', '1124295015018373121', '1124295015018373126', 'syncState', 'syncState', 'varchar', 'java.lang.String', '同步状态： \r\n            准备同步 Ready,同步中 Sync,同步错误 Error,已经同步 Synced', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10217', '1124295015018373121', '1124295015018373127', 'state', 'state', 'varchar', 'java.lang.String', '状态：入库 Storage，已出库 Issue', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10218', '1124295015018373121', '1124295015018373128', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10219', '1124295015018373121', '1124295015018373129', 'updateTime', 'updateTime', 'datetime', 'java.util.Date', '更新时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10220', '1124295015018373130', '1124295015018373131', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10221', '1124295015018373130', '1124295015018373132', 'testUnitCode', 'testUnitCode', 'varchar', 'java.lang.String', '测试件编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10222', '1124295015018373130', '1124295015018373133', 'box_sn', 'boxSn', 'varchar', 'java.lang.String', '箱体SN码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10223', '1124295015018373130', '1124295015018373134', 'item_sn', 'itemSn', 'varchar', 'java.lang.String', '产品SN码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10224', '1124295015018373135', '1124295015018373136', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10225', '1124295015018373135', '1124295015018373137', 'code', 'code', 'varchar', 'java.lang.String', '编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10226', '1124295015018373135', '1124295015018373138', 'customer_abbreviation', 'customerAbbreviation', 'varchar', 'java.lang.String', '客户简称', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10227', '1124295015018373135', '1124295015018373139', 'name', 'name', 'varchar', 'java.lang.String', '客户名称', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10228', '1124295015018373135', '1124295015018373140', 'linkman', 'linkman', 'varchar', 'java.lang.String', '联系人', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10229', '1124295015018373135', '1124295015018373141', 'phone', 'phone', 'varchar', 'java.lang.String', '电话', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10230', '1124295015018373135', '1124295015018373142', 'email', 'email', 'varchar', 'java.lang.String', '邮箱', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10231', '1124295015018373135', '1124295015018373143', 'language', 'language', 'varchar', 'java.lang.String', '语言', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10232', '1124295015018373135', '1124295015018373144', 'countryCode', 'countryCode', 'varchar', 'java.lang.String', '国家编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10233', '1124295015018373135', '1124295015018373145', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10234', '1124295015018373135', '1124295015018373146', 'updateTime', 'updateTime', 'datetime', 'java.util.Date', '更新时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10235', '1124295015018373147', '1124295015018373148', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10236', '1124295015018373147', '1124295015018373149', 'code', 'code', 'varchar', 'java.lang.String', '编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10237', '1124295015018373147', '1124295015018373150', 'order_no', 'orderNo', 'varchar', 'java.lang.String', '订单编号', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10238', '1124295015018373147', '1124295015018373151', 'customerCode', 'customerCode', 'varchar', 'java.lang.String', '客户编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10239', '1124295015018373147', '1124295015018373152', 'item_sku', 'itemSku', 'varchar', 'java.lang.String', '产品sku', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10240', '1124295015018373147', '1124295015018373153', 'item_summary', 'itemSummary', 'varchar', 'java.lang.String', '产品说明', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10241', '1124295015018373147', '1124295015018373154', 'item_total_num', 'itemTotalNum', 'int', 'java.lang.Integer', '产品总量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10242', '1124295015018373147', '1124295015018373155', 'item_box_num', 'itemBoxNum', 'int', 'java.lang.Integer', '产品箱数', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10243', '1124295015018373147', '1124295015018373156', 'expect_ship_date', 'expectShipDate', 'datetime', 'java.util.Date', '期望出单日期', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10244', '1124295015018373147', '1124295015018373157', 'order_date', 'orderDate', 'varchar', 'java.lang.String', '下单日期', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10245', '1124295015018373147', '1124295015018373158', 'ship_time', 'shipTime', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10246', '1124295015018373147', '1124295015018373159', 'state', 'state', 'varchar', 'java.lang.String', '状态：已下单 Order，待发货 Pending_Delivery，已发货Shipped,取消 Cancel,延期 Delay', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10247', '1124295015018373147', '1124295015018373160', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10248', '1124295015018373147', '1124295015018373161', 'updateTime', 'updateTime', 'datetime', 'java.util.Date', '更新时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10249', '1124295032198242304', '1124295032198242305', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10250', '1124295032198242304', '1124295032198242306', 'orderCode', 'orderCode', 'varchar', 'java.lang.String', '订单编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10251', '1124295032198242304', '1124295032198242307', 'box_sn', 'boxSn', 'varchar', 'java.lang.String', '箱体SN码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10252', '1124295032198242304', '1124295032198242308', 'item_sn', 'itemSn', 'varchar', 'java.lang.String', '产品SN', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10253', '1124295032198242304', '1124295032198242309', 'box_num', 'boxNum', 'varchar', 'java.lang.String', '箱号', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10254', '1124295032198242304', '1124295032198242310', 'item_num', 'itemNum', 'varchar', 'java.lang.String', '装箱数量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10255', '1124295032198242304', '1124295032198242311', 'linkman', 'linkman', 'varchar', 'java.lang.String', '联系人', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10256', '1124295032198242304', '1124295032198242312', 'phone', 'phone', 'varchar', 'java.lang.String', '电话', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10257', '1124295032198242304', '1124295032198242313', 'email', 'email', 'varchar', 'java.lang.String', '邮箱', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10258', '1124295032198242304', '1124295032198242314', 'address', 'address', 'varchar', 'java.lang.String', '地址', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10259', '1124295032198242304', '1124295032198242315', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10324', '1127246516544208896', '1127246516544208897', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10325', '1127246516544208896', '1127246516544208898', 'code', 'code', 'varchar', 'java.lang.String', '编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10326', '1127246516544208896', '1127246516544208899', 'category_code', 'categoryCode', 'varchar', 'java.lang.String', '分类编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10327', '1127246516544208896', '1127246516544208900', 'name', 'name', 'varchar', 'java.lang.String', '产品名', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10328', '1127246516544208896', '1127246516544208901', 'image_url', 'imageUrl', 'varchar', 'java.lang.String', '图片地址', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10329', '1127246516544208896', '1127246516544208902', 'summary', 'summary', 'varchar', 'java.lang.String', '摘要描述', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10330', '1127246516544208896', '1127246516544208903', 'jlb', 'jlb', 'varchar', 'java.lang.String', '金兰宝价格', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10331', '1127246516544208896', '1127246516544208904', 'cost', 'cost', 'varchar', 'java.lang.String', '成本价', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10332', '1127246516544208896', '1127246516544208905', 'member_price', 'memberPrice', 'varchar', 'java.lang.String', '会员价', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10333', '1127246516544208896', '1127246516544208906', 'discount', 'discount', 'varchar', 'java.lang.String', '折扣', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10334', '1127246516544208896', '1127246516544208907', 'store_num', 'storeNum', 'int', 'java.lang.Integer', '库存量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10335', '1127246516544208896', '1127246516544208908', 'is_discount', 'isDiscount', 'varchar', 'java.lang.String', '是否促销 Y,N', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10336', '1127246516544208896', '1127246516544208909', 'limit_num', 'limitNum', 'int', 'java.lang.Integer', '限购数量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10337', '1127246516544208896', '1127246516544208910', 'state', 'state', 'varchar', 'java.lang.String', '状态:待审核 audited，上架 up_shelves，下架down_shelves，售罄 sold_out', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10338', '1127246516544208896', '1127246516544208911', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10339', '1127246516544208896', '1127246516544208912', 'updateTime', 'updateTime', 'datetime', 'java.util.Date', '更新时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10340', '1127246533724078080', '1127246533724078081', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10341', '1127246533724078080', '1127246533724078082', 'code', 'code', 'varchar', 'java.lang.String', '编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10342', '1127246533724078080', '1127246533724078083', 'name', 'name', 'varchar', 'java.lang.String', '分类', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10343', '1127246533724078084', '1127246533724078085', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10344', '1127246533724078084', '1127246533724078086', 'code', 'code', 'varchar', 'java.lang.String', '编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10345', '1127246533724078084', '1127246533724078087', 'user_code', 'userCode', 'varchar', 'java.lang.String', '用户编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10346', '1127246533724078084', '1127246533724078088', 'item_code', 'itemCode', 'varchar', 'java.lang.String', '产品编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10347', '1127246533724078084', '1127246533724078089', 'item_name', 'itemName', 'varchar', 'java.lang.String', '产品名称', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10348', '1127246533724078084', '1127246533724078090', 'item_num', 'itemNum', 'int', 'java.lang.Integer', '产品数量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10349', '1127246533724078084', '1127246533724078091', 'state', 'state', 'varchar', 'java.lang.String', '状态：\r\n            状态:待支付 Pay，待发货 Deliver，已发货 Shipped，已签收 Signed', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10350', '1127246533724078084', '1127246533724078092', 'user_name', 'userName', 'varchar', 'java.lang.String', '联系人', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10351', '1127246533724078084', '1127246533724078093', 'address', 'address', 'varchar', 'java.lang.String', '地址', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10352', '1127246533724078084', '1127246533724078094', 'cash', 'cash', 'varchar', 'java.lang.String', '现金价格', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10353', '1127246533724078084', '1127246533724078095', 'jlb', 'jlb', 'varchar', 'java.lang.String', '金兰宝价格', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10354', '1127246533724078084', '1127246533724078096', 'ore', 'ore', 'varchar', 'java.lang.String', '矿石价格', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10355', '1127246533724078084', '1127246533724078097', 'active', 'active', 'varchar', 'java.lang.String', '激活币价格', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10356', '1127246533724078084', '1127246533724078098', 'pay_method', 'payMethod', 'varchar', 'java.lang.String', '支付方式', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10357', '1127246533724078084', '1127246533724078099', 'phone', 'phone', 'varchar', 'java.lang.String', '电话', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10358', '1127246533724078084', '1127246533724078100', 'remark', 'remark', 'varchar', 'java.lang.String', '留言', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10359', '1127246533724078084', '1127246533724078101', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10360', '1127246533724078084', '1127246533724078102', 'updateTime', 'updateTime', 'datetime', 'java.util.Date', '更新时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10361', '1127246533724078103', '1127246533724078104', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10362', '1127246533724078103', '1127246533724078105', 'item_code', 'itemCode', 'varchar', 'java.lang.String', '产品编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10363', '1127246533724078103', '1127246533724078106', 'pay_method_code', 'payMethodCode', 'varchar', 'java.lang.String', '支付方式编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10364', '1127246533724078103', '1127246533724078107', 'pay_method_name', 'payMethodName', 'varchar', 'java.lang.String', '支付方式', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10365', '1127246533724078103', '1127246533724078108', 'price', 'price', 'varchar', 'java.lang.String', '价格', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10366', '1127246533724078109', '1127246533724078110', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10367', '1127246533724078109', '1127246533724078111', 'code', 'code', 'varchar', 'java.lang.String', '编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10368', '1127246533724078109', '1127246533724078112', 'name', 'name', 'varchar', 'java.lang.String', '支付方式', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10369', '1127246533724078113', '1127246533724078114', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10370', '1127246533724078113', '1127246533724078115', 'user_code', 'userCode', 'varchar', 'java.lang.String', '用户编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10371', '1127246533724078113', '1127246533724078116', 'address', 'address', 'varchar', 'java.lang.String', '地址', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10372', '1127246533724078113', '1127246533724078117', 'name', 'name', 'varchar', 'java.lang.String', '收货人姓名', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10373', '1127246533724078113', '1127246533724078118', 'phone', 'phone', 'varchar', 'java.lang.String', '联系方式', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10374', '1130295462288039937', '1130295462288039938', 'id', 'id', 'int', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10375', '1130295462288039937', '1130295462288039939', 'user_id', 'userId', 'varchar', 'java.lang.String', '', '\'\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10376', '1130295462288039937', '1130295462288039940', 'account', 'account', 'varchar', 'java.lang.String', '', '\'\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10377', '1130295462288039937', '1130295462288039941', 'kuang_f3', 'kuangF3', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10378', '1130295462288039937', '1130295462288039942', 'kuang_f4', 'kuangF4', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10379', '1130295462288039937', '1130295462288039943', 'kuang_f5', 'kuangF5', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10380', '1130295462288039937', '1130295462288039944', 'kuang_f6', 'kuangF6', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10381', '1130295462288039937', '1130295462288039945', 'dig_out', 'digOut', 'varchar', 'java.lang.String', '', '\'\'', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10382', '1130295462288039937', '1130295462288039946', 'kuang_f8', 'kuangF8', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10383', '1130295462288039937', '1130295462288039947', 'kuang_f9', 'kuangF9', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10384', '1130295462288039937', '1130295462288039948', 'kuang_f10', 'kuangF10', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10385', '1130295462288039937', '1130295462288039949', 'kuang_f11', 'kuangF11', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10386', '1130295462288039937', '1130295462288039950', 'kuang_f12', 'kuangF12', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10387', '1130295462288039937', '1130295462288039951', 'kuang_f13', 'kuangF13', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10388', '1130295462288039937', '1130295462288039952', 'kuang_f14', 'kuangF14', 'varchar', 'java.lang.String', '', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10392', '1133516533141241857', '1133516533141241858', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10393', '1133516533141241857', '1133516533141241859', 'item_order_code', 'itemOrderCode', 'varchar', 'java.lang.String', '订单编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10394', '1133516533141241857', '1133516533141241860', 'order_no', 'orderNo', 'varchar', 'java.lang.String', '订单号', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10395', '1133516533141241857', '1133516533141241861', 'state', 'state', 'varchar', 'java.lang.String', '状态', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10396', '1133516533141241857', '1133516533141241862', 'createTime', 'createTime', 'datetime', 'java.util.Date', '时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10397', '1133516533141241857', '1133516533141241863', 'customerCode', 'customerCode', 'varchar', 'java.lang.String', '客户编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10398', '1133516533141241857', '1133516533141241864', 'customerName', 'customerName', 'varchar', 'java.lang.String', '客户姓名', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10399', '1133635057058742272', '1133635057058742273', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10400', '1133635057058742272', '1133635057058742274', 'code', 'code', 'varchar', 'java.lang.String', '编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10401', '1133635057058742272', '1133635057058742275', 'sn', 'sn', 'varchar', 'java.lang.String', '箱SN', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10402', '1133635057058742272', '1133635057058742276', 'jobNumber', 'jobNumber', 'varchar', 'java.lang.String', '员工号', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10403', '1133635057058742272', '1133635057058742277', 'syncState', 'syncState', 'varchar', 'java.lang.String', '同步状态：准备同步 Ready,同步中 Sync,同步错误 Error,已经同步 Synced', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10404', '1133635057058742272', '1133635057058742278', 'state', 'state', 'varchar', 'java.lang.String', '状态：入库 Storage，已出库 Issue', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10405', '1133635057058742272', '1133635057058742279', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10406', '1133635057058742272', '1133635057058742280', 'updateTime', 'updateTime', 'datetime', 'java.util.Date', '更新时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10407', '1133635057058742281', '1133635057058742282', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10408', '1133635057058742281', '1133635057058742283', 'testUnitCode', 'testUnitCode', 'varchar', 'java.lang.String', '测试件编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10409', '1133635057058742281', '1133635057058742284', 'box_sn', 'boxSn', 'varchar', 'java.lang.String', '箱体SN码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10410', '1133635057058742281', '1133635057058742285', 'item_sn', 'itemSn', 'varchar', 'java.lang.String', '产品SN码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10411', '1133635057058742286', '1133635057058742287', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10412', '1133635057058742286', '1133635057058742288', 'code', 'code', 'varchar', 'java.lang.String', '编码', null, 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10413', '1133635057058742286', '1133635057058742289', 'customer_abbreviation', 'customerAbbreviation', 'varchar', 'java.lang.String', '客户简称', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10414', '1133635057058742286', '1133635057058742290', 'name', 'name', 'varchar', 'java.lang.String', '客户名称', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10415', '1133635057058742286', '1133635057058742291', 'linkman', 'linkman', 'varchar', 'java.lang.String', '联系人', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10416', '1133635057058742286', '1133635057058742292', 'phone', 'phone', 'varchar', 'java.lang.String', '电话', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10417', '1133635057058742286', '1133635057058742293', 'email', 'email', 'varchar', 'java.lang.String', '邮箱', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10418', '1133635057058742286', '1133635057058742294', 'language', 'language', 'varchar', 'java.lang.String', '语言', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10419', '1133635057058742286', '1133635057058742295', 'countryCode', 'countryCode', 'varchar', 'java.lang.String', '国家编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10420', '1133635057058742286', '1133635057058742296', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10421', '1133635057058742286', '1133635057058742297', 'updateTime', 'updateTime', 'datetime', 'java.util.Date', '更新时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10422', '1133635057058742298', '1133635057058742299', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10423', '1133635057058742298', '1133635057058742300', 'customer_code', 'customerCode', 'varchar', 'java.lang.String', '客户编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10424', '1133635057058742298', '1133635057058742301', 'address', 'address', 'varchar', 'java.lang.String', '地址', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10425', '1133635057058742302', '1133635057058742303', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10426', '1133635057058742302', '1133635057058742304', 'code', 'code', 'varchar', 'java.lang.String', '编码', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10427', '1133635057058742302', '1133635057058742305', 'order_no', 'orderNo', 'varchar', 'java.lang.String', '订单编号', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10428', '1133635057058742302', '1133635057058742306', 'customerCode', 'customerCode', 'varchar', 'java.lang.String', '客户编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10429', '1133635057058742302', '1133635057058742307', 'item_sku', 'itemSku', 'varchar', 'java.lang.String', '产品sku', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10430', '1133635057058742302', '1133635057058742308', 'item_summary', 'itemSummary', 'varchar', 'java.lang.String', '产品说明', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10431', '1133635057058742302', '1133635057058742309', 'item_total_num', 'itemTotalNum', 'int', 'java.lang.Integer', '产品总量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10432', '1133635057058742302', '1133635057058742310', 'item_box_num', 'itemBoxNum', 'int', 'java.lang.Integer', '产品箱数', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10433', '1133635057058742302', '1133635057058742311', 'bind_item_box_num', 'bindItemBoxNum', 'int', 'java.lang.Integer', '绑定的产品箱数，默认为0', '0', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10434', '1133635057058742302', '1133635057058742312', 'expect_ship_date', 'expectShipDate', 'datetime', 'java.util.Date', '期望出单日期', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10435', '1133635057058742302', '1133635057058742313', 'order_date', 'orderDate', 'datetime', 'java.util.Date', '下单日期', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10436', '1133635057058742302', '1133635057058742314', 'ship_time', 'shipTime', 'datetime', 'java.util.Date', '', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10437', '1133635057058742302', '1133635057058742315', 'state', 'state', 'varchar', 'java.lang.String', '状态：已下单 Order，待发货 Pending_Delivery，已发货Shipped,取消 Cancel,延期 Delay', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10438', '1133635057058742302', '1133635057058742316', 'address', 'address', 'varchar', 'java.lang.String', '地址', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10439', '1133635057058742302', '1133635057058742317', 'linkman', 'linkman', 'varchar', 'java.lang.String', '联系人', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10440', '1133635057058742302', '1133635057058742318', 'phone', 'phone', 'varchar', 'java.lang.String', '电话', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10441', '1133635057058742302', '1133635057058742319', 'email', 'email', 'varchar', 'java.lang.String', '邮箱', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10442', '1133635057058742302', '1133635057058742320', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10443', '1133635057058742302', '1133635057058742321', 'updateTime', 'updateTime', 'datetime', 'java.util.Date', '更新时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10444', '1133635057058742322', '1133635057058742323', 'id', 'id', 'bigint', 'java.lang.Long', '', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10445', '1133635057058742322', '1133635057058742324', 'orderCode', 'orderCode', 'varchar', 'java.lang.String', '订单编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10446', '1133635057058742322', '1133635057058742325', 'box_sn', 'boxSn', 'varchar', 'java.lang.String', '箱体SN码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10447', '1133635057058742322', '1133635057058742326', 'item_sn', 'itemSn', 'varchar', 'java.lang.String', '产品SN', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10448', '1133635057058742322', '1133635057058742327', 'box_num', 'boxNum', 'varchar', 'java.lang.String', '箱号', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10449', '1133635057058742322', '1133635057058742328', 'item_num', 'itemNum', 'varchar', 'java.lang.String', '装箱数量', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10450', '1133635057058742322', '1133635057058742329', 'createTime', 'createTime', 'datetime', 'java.util.Date', '创建时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10451', '1133635057058742330', '1133635057058742331', 'id', 'id', 'bigint', 'java.lang.Long', 'id', null, 'Y', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10452', '1133635057058742330', '1133635057058742332', 'item_order_code', 'itemOrderCode', 'varchar', 'java.lang.String', '订单编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10453', '1133635057058742330', '1133635057058742333', 'order_no', 'orderNo', 'varchar', 'java.lang.String', '订单号', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10454', '1133635057058742330', '1133635057058742334', 'state', 'state', 'varchar', 'java.lang.String', '状态', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10455', '1133635057058742330', '1133635057058742335', 'createTime', 'createTime', 'datetime', 'java.util.Date', '时间', 'NULL', 'N', 'Y', 'N');
INSERT INTO `map_field_column` VALUES ('10456', '1133635057058742330', '1133635057058742336', 'customerCode', 'customerCode', 'varchar', 'java.lang.String', '客户编码', 'NULL', 'N', 'N', 'N');
INSERT INTO `map_field_column` VALUES ('10457', '1133635057058742330', '1133635057058742337', 'customerName', 'customerName', 'varchar', 'java.lang.String', '客户姓名', 'NULL', 'N', 'N', 'N');

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
  `isIncrement` varchar(16) DEFAULT NULL COMMENT '是否增量生成',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=195 DEFAULT CHARSET=utf8mb4 COMMENT='项目信息';

-- ----------------------------
-- Records of project
-- ----------------------------
INSERT INTO `project` VALUES ('129', '747543009249812480', '庞帝zoom临时账户业务', '龐帝業務系统,临时增加zoom的临时账户业务解决zoom资源问题', 'zoom_account', 'Mysql', 'java', 'Enable', 'Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.\n                      http://www.rzhkj.com/\n      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.\n      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.\n      本代码仅用于龐帝業務系统.', 'lixin', 'hegaoye@qq.com', 'com.ponddy', null, '/repository/zoom_account.zip', '1', 'N', 'Y', 'Y', '2018-02-05 14:52:10', '2018-02-05 21:03:29', '21218cca77804d2ba1', null);
INSERT INTO `project` VALUES ('130', '752172640397746176', '测试', '测试', 'demo', 'Mysql', 'js', 'Enable', '测试', '测试', '15824826097', '测试', null, 'DownloadUrl', null, 'N', 'N', 'N', '2018-02-08 17:43:30', '2018-02-08 17:43:30', '21218cca77804d2ba1922c33e0151105', null);
INSERT INTO `project` VALUES ('131', '856024537298477056', 'pps', 'pps是玺得科技公司针对pi-top产品生产质量检测而研发的系统，仅限于本公司使用与研究.', 'pps', 'Mysql', 'java', 'Enable', 'pps是玺得科技公司针对pi-top产品生产质量检测而研发的系统，仅限于本公司使用与研究.', 'leo', 'leo@pi-top.com', 'com.pitop', null, '/repository/pps.zip', '1', 'N', 'Y', 'Y', '2018-04-19 16:53:06', '2018-04-20 19:15:48', '21218cca77804d2ba1', null);
INSERT INTO `project` VALUES ('135', '946529806146805760', 'springCloudDemo', 'Demo', 'springCloudDemo', 'Mysql', 'java', 'Delete', 'springCloudDemo', 'ada', '13129590182', 'com.cn', null, 'DownloadUrl', null, 'N', 'Y', 'Y', '2018-06-19 16:14:46', '2018-06-19 16:20:16', '946523569854291968', null);
INSERT INTO `project` VALUES ('136', '946641355037425664', 'pps-a', 'pps_a', 'pps_a', 'Mysql', 'java', 'Enable', 'pps-a', 'ada', '13129590182', 'com.hk', null, '/repository/pps_a.zip', '1', 'N', 'Y', 'Y', '2018-06-19 18:02:59', '2018-06-19 18:03:29', '946523569854291968', null);
INSERT INTO `project` VALUES ('137', '946651920656973824', 'pps-aa', 'pps_aa', 'pps_aa', 'Mysql', 'java', 'Enable', 'pps-aa', 'ada', '13129590182', 'com.hk', null, '/repository/pps_aa.zip', '1', 'N', 'Y', 'Y', '2018-06-19 18:13:14', '2018-06-19 18:13:40', '946523569854291968', null);
INSERT INTO `project` VALUES ('138', '949394669592330240', 'pps-cl', '33333', 'ppscloud', 'Mysql', 'js', 'Enable', '2089298', 'lhz', '28918291829128', '22', null, '/repository/ppscloud.zip', '2', 'N', 'Y', 'Y', '2018-06-21 14:34:03', '2018-06-21 14:35:39', '949393243663187968', null);
INSERT INTO `project` VALUES ('139', '949396696816893952', 'test', 'a', 'test', 'Mysql', 'js', 'Enable', 'test', 'wayne', 'f', 'a', null, '/repository/test.zip', '1', 'N', 'Y', 'Y', '2018-06-21 14:36:01', '2018-06-21 14:40:10', '949395167808536576', null);
INSERT INTO `project` VALUES ('141', '958095826657607680', 'pps_cloud_demo', 'ppscloudDemo', 'ppscloudDemo', 'Mysql', 'java', 'Enable', 'ppscloudDemo', 'ada', '12345678901', 'com.pitop', null, '/repository/ppscloudDemo.zip', '1', 'N', 'Y', 'Y', '2018-06-27 11:15:17', '2018-06-27 11:15:53', '946523569854291968', null);
INSERT INTO `project` VALUES ('142', '958147125747023872', 'pps_c', 'pps_c', 'pps_c', 'Mysql', 'java', 'Enable', 'pps_c', 'ada', '12345678901', 'com.pitop', null, '/repository/pps_c.zip', '1', 'N', 'Y', 'Y', '2018-06-27 12:05:03', '2018-06-27 12:05:24', '946523569854291968', null);
INSERT INTO `project` VALUES ('143', '958259860048617472', 'pps_cc', 'pps_cc', 'pps_cc', 'Mysql', 'java', 'Enable', 'pps_cc', 'pps_cc', '12345678901', 'com.pitop', null, '/repository/pps_cc.zip', '1', 'N', 'Y', 'Y', '2018-06-27 13:54:25', '2018-06-27 14:06:31', '946523569854291968', null);
INSERT INTO `project` VALUES ('145', '958616909269958656', 'springCloudTest', 'springCloudTest', 'springCloudTest', 'Mysql', 'java', 'Enable', 'springCloudTest', 'ada', '12345678910', 'com.pitop', null, '/repository/springCloudTest.zip', '1', 'N', 'Y', 'Y', '2018-06-27 19:40:48', '2018-06-27 19:41:23', '946523569854291968', null);
INSERT INTO `project` VALUES ('147', '959878942460223488', 'ppscloudd', 'ppscloud', 'ppscloudd', 'Mysql', 'java', 'Enable', 'ppscloudd', 'ppscloudd', '12345678901', 'com.pitop', null, '/repository/ppscloudd.zip', '1', 'N', 'Y', 'Y', '2018-06-28 16:05:08', '2018-06-28 16:05:56', '946523569854291968', null);
INSERT INTO `project` VALUES ('148', '959895074357395456', 'ppscc', 'ppscc', 'ppscc', 'Mysql', 'java', 'Enable', 'ppscc', 'ada', '12345698701', 'com.pitop', null, '/repository/ppscc.zip', '7', 'N', 'Y', 'Y', '2018-06-28 16:20:47', '2018-06-28 16:21:21', '946523569854291968', null);
INSERT INTO `project` VALUES ('151', '961285355271159808', 'aippscloud', 'com.pitop', 'aippscloud', 'Mysql', 'java', 'Enable', 'aippscloud', 'aippscloud', 'aippscloud', 'com.pitop', null, '/repository/aippscloud.zip', '1', 'N', 'Y', 'Y', '2018-06-29 14:49:32', '2018-06-29 14:56:09', '959610713162645504', null);
INSERT INTO `project` VALUES ('152', '965480833944739840', 'pps-cloud', '补充pps工厂端的备份业务系统，将工厂端的数据通过网络同步方式保存在云端.', 'pps_cloud', 'Mysql', 'java', 'Enable', 'Copyright (c) 2018. 玺得（深圳）科技有限公司.保留所有权利. http://www.pi-top.com/ 玺得（深圳）科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系. 代码只针对特定需求定制编码，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担. 本代码仅用于pps-cloud系统.', 'pi-top', 'www.pi-top.com', 'com.pitop', null, '/repository/pps_cloud.zip', '12', 'N', 'Y', 'Y', '2018-07-02 10:39:41', '2018-07-02 10:43:58', '21218cca77804d2ba1', null);
INSERT INTO `project` VALUES ('153', '967412452536827904', 'ppsv2', '对pps系统的升级，增加序列号业务模块', 'ppsv2', 'Mysql', 'java', 'Enable', 'Copyright (c) 2018. 玺得（深圳）科技有限公司.保留所有权利. \nhttp://www.pi-top.com/ 玺得（深圳）科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系. \n代码只针对特定需求定制编码，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担. \n本代码仅用于pps-cloud系统.', 'www.pi-top.com', 'www.pi-top.com', 'com.pitop', null, '/repository/ppsv2.zip', '3', 'N', 'Y', 'Y', '2018-07-03 17:53:36', '2018-07-03 17:54:58', '21218cca77804d2ba1', null);
INSERT INTO `project` VALUES ('155', '977425344153894912', 'mybox', '对象存储系统', 'mybox', 'Mysql', 'java', 'Enable', '对象存储', 'lixin', 'hegaoye@qq.com', 'com.rzhkj', null, '/repository/mybox.zip', '1', 'N', 'Y', 'Y', '2018-07-10 11:47:23', '2018-07-10 11:47:51', '21218cca77804d2ba1', null);
INSERT INTO `project` VALUES ('157', '1010331133013041152', 'open-sso', '单点登录', 'opensso', 'Mysql', 'java', 'Enable', 'sso', 'helixin', 'hegaoye@qq.com', 'com.opensso', null, '/repository/opensso.zip', '1', 'N', 'Y', 'Y', '2018-08-01 15:50:12', '2018-08-01 15:50:35', '21218cca77804d2ba1', null);
INSERT INTO `project` VALUES ('158', '1019686052820074496', 'test', 'test', 'test_01', 'MongoDB', 'js', 'Enable', 'test function', 'sen', 'test', 'com.test', null, 'DownloadUrl', null, 'N', 'N', 'N', '2018-08-07 23:05:40', '2018-08-07 23:05:40', '1019673528695439360', null);
INSERT INTO `project` VALUES ('162', '1053528539686813696', 'test111', 'test111', 'pps_cloud11', 'Mysql', 'java', 'Enable', 'fdas111', 'test111', 'test111', 'com.test', null, 'DownloadUrl', null, 'N', 'N', 'N', '2018-08-30 18:17:12', '2018-08-30 18:17:12', '959610713162645504', null);
INSERT INTO `project` VALUES ('165', '1058961931014316032', 'berton-cloud', 'berton', 'berton', 'Mysql', 'java', 'Enable', 'Copyright (c) 2018. 玺得（深圳）科技有限公司.保留所有权利. http://www.pi-top.com/ 玺得（深圳）科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系. 代码只针对特定需求定制编码，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担. 本代码仅用于pps-cloud系统.', 'berton', 'berton', 'com.pitop', null, '/repository/berton.zip', '6', 'N', 'Y', 'Y', '2018-09-03 10:08:17', '2018-10-23 15:35:47', '1053385276757663744', null);
INSERT INTO `project` VALUES ('166', '1059236585582960640', 'pps-cloud-demo', 'berton', 'pps_cloud_demo', 'Mysql', 'java', 'Enable', ' Copyright (c) 2018. 玺得（深圳）科技有限公司.保留所有权利. http://www.pi-top.com/ 玺得（深圳）科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系. 代码只针对特定需求定制编码，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担. 本代码仅用于pps-cloud系统.', 'berton', 'berton', 'com.pitop', null, '/repository/pps_cloud_demo.zip', '1', 'N', 'Y', 'Y', '2018-09-03 14:34:44', '2018-10-23 17:30:46', '1053385276757663744', null);
INSERT INTO `project` VALUES ('170', '1069945279081734144', 'springboot_shiro', 'berton', 'springboot_shiro', 'Mysql', 'java', 'Enable', 'berton', 'berton', 'berton', 'com.pitop', null, '/repository/springboot_shiro.zip', '1', 'N', 'Y', 'Y', '2018-09-10 19:43:32', '2018-09-18 15:28:52', '1053385276757663744', null);
INSERT INTO `project` VALUES ('171', '1073852067233521664', 'support1', 'support1', 'support1', 'Mysql', 'java', 'Enable', 'support1', 'swx', 'swx', 'com.pitop', null, '/repository/support1.zip', '1', 'N', 'Y', 'Y', '2018-09-13 10:53:37', '2018-09-13 10:53:45', '959610713162645504', null);
INSERT INTO `project` VALUES ('172', '1073888213678284800', 'support3', 'support3', 'support3', 'Mysql', 'java', 'Enable', 'support3', 'support3', 'support3', 'com.pitop', null, '/repository/support3.zip', '1', 'N', 'Y', 'Y', '2018-09-13 11:28:41', '2018-09-13 11:28:50', '959610713162645504', null);
INSERT INTO `project` VALUES ('173', '1081461873108918272', 'test1', 'test1', 'test1', 'Mysql', 'java', 'Enable', 'test1', 'test1', 'test1', 'com.pitop', null, '/repository/test1.zip', '1', 'N', 'Y', 'Y', '2018-09-18 13:56:06', '2018-09-18 13:57:08', '959610713162645504', null);
INSERT INTO `project` VALUES ('174', '1081625150585970688', 'angular', 'angular', 'angular', 'Mysql', 'js', 'Enable', 'jane', 'jane', 'jane', 'com.pitop', null, '/repository/angular.zip', '18', 'N', 'Y', 'Y', '2018-09-18 16:34:30', '2018-09-18 19:58:54', '1053385276757663744', null);
INSERT INTO `project` VALUES ('175', '1083025464543363072', '前端模板', '前端模板', 'angular_template', 'Mysql', 'js', 'Enable', '前端模板', 'jane', '15737198859', 'angular_template', null, '/repository/angular_template.zip', '1', 'N', 'Y', 'Y', '2018-09-19 15:12:59', '2018-09-19 15:13:41', '1053385276757663744', null);
INSERT INTO `project` VALUES ('176', '1083069479368212480', 'angular模板', 'angular模板', 'angular_zorro_template', 'Mysql', 'js', 'Enable', 'angular模板', 'jane', '15737198859', 'angular_template', null, '/repository/angular_zorro_template.zip', '18', 'N', 'Y', 'Y', '2018-09-19 15:55:41', '2018-09-20 11:49:56', '1053385276757663744', null);
INSERT INTO `project` VALUES ('179', '1086234131531120640', 'repair-angular', 'jane', 'repair_angular', 'Mysql', 'js', 'Enable', 'pi-top', 'jane', 'jane', 'com.pitop', null, '/repository/repair_angular.zip', '1', 'N', 'Y', 'Y', '2018-09-21 19:05:48', '2018-09-21 19:06:48', '1053385276757663744', null);
INSERT INTO `project` VALUES ('180', '1102286451541630976', 'aicode', 'ai-code', 'aicode', 'Mysql', 'java', 'Enable', ' * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.\n *                       http://www.rzhkj.com/\n *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.\n *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.\n *      本代码仅用于AI-Code.', 'berton', 'berton', 'com.rzhkj', null, '/repository/aicode.zip', '1', 'N', 'Y', 'Y', '2018-10-02 14:38:36', '2018-10-02 14:42:44', '1053385276757663744', null);
INSERT INTO `project` VALUES ('181', '1102343591786553344', 'aa', 'jlb', 'aa', 'Mysql', 'java', 'Enable', 'jlb', 'jlb', 'jlb', 'com.jlb', null, '/repository/aa.zip', '1', 'N', 'Y', 'Y', '2018-10-02 15:34:02', '2018-10-16 10:09:46', '1053385276757663744', null);
INSERT INTO `project` VALUES ('182', '1102553839025627136', '1', '1', 'a', 'Mysql', 'java', 'Enable', '1', 'jane', '13808854478', '1', null, 'DownloadUrl', null, 'N', 'Y', 'Y', '2018-10-02 18:58:00', '2018-10-02 19:02:52', '1102552894132822016', null);
INSERT INTO `project` VALUES ('187', '1105399529737224192', 'jlb', 'jlb', 'jlbtemp', 'Mysql', 'java', 'Enable', 'jlb', 'jlb', 'jlb', 'com.jlb', null, '/repository/jlbtemp.zip', '1', 'N', 'Y', 'Y', '2018-10-04 16:58:41', '2018-10-19 10:13:04', '1053385276757663744', null);
INSERT INTO `project` VALUES ('188', '1106948707261038592', 'jlb-frontend', 'jlb', 'jlb_frontend', 'Mysql', 'js', 'Enable', 'jlb', 'jlb', 'jlb', 'com.jlb', null, '/repository/jlb_frontend.zip', '1', 'N', 'Y', 'Y', '2018-10-05 18:01:35', '2018-10-07 11:53:57', '1053385276757663744', null);
INSERT INTO `project` VALUES ('189', '1116944075411857408', '测试日志', 'ceshilog', 'ceshilog', 'Mysql', 'java', 'Enable', 'ceshilog', 'ceshilog', 'ceshilog', 'ceshilog', null, '/repository/ceshilog.zip', '6', 'N', 'Y', 'Y', '2018-10-12 11:38:22', '2018-10-16 15:09:04', '21218cca77804d2ba1', null);
INSERT INTO `project` VALUES ('191', '1122747143164551168', 'test2', 'test2', 'test2', 'Mysql', 'java', 'Enable', 'test2', 'test2', 'test2', 'test2', null, 'DownloadUrl', null, 'N', 'Y', 'Y', '2018-10-16 09:28:05', '2018-10-16 09:28:41', null, null);
INSERT INTO `project` VALUES ('193', '1124294654241120256', 'pps-order', 'pps-order', 'pps_order', 'Mysql', 'js', 'Enable', 'Copyright (c) 2018. 玺得（深圳）科技有限公司.保留所有权利. http://www.pi-top.com/ 玺得（深圳）科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系. 代码只针对特定需求定制编码，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担. 本代码仅用于pps-cloud系统.', 'Jane', 'Jane', 'com.pitop', null, '/repository/pps_order.zip', '1', 'N', 'Y', 'Y', '2018-10-17 10:29:22', '2018-10-17 10:29:44', '1102552894132822016', null);
INSERT INTO `project` VALUES ('194', '1125753757710794752', 'jinlanbao', 'jinlanbao', 'jinlanbao', 'Mysql', 'java', 'Enable', 'jinlanbao', 'jinlanbao', 'jinlanbao', 'com.jlb', null, '/repository/jinlanbao.zip', '1', 'N', 'Y', 'Y', '2018-10-18 10:04:53', '2018-10-21 11:30:55', '1053385276757663744', null);

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
) ENGINE=InnoDB AUTO_INCREMENT=172 DEFAULT CHARSET=utf8mb4 COMMENT='项目应用技术';

-- ----------------------------
-- Records of project_framwork
-- ----------------------------
INSERT INTO `project_framwork` VALUES ('152', '1081629222214967296', '1106948707261038592');
INSERT INTO `project_framwork` VALUES ('158', '1081629222214967297', '1102343591786553344');
INSERT INTO `project_framwork` VALUES ('159', '10101010', '1116944075411857408');
INSERT INTO `project_framwork` VALUES ('163', '1081629222214967296', '1124294654241120256');
INSERT INTO `project_framwork` VALUES ('167', '1081629222214967297', '1105399529737224192');
INSERT INTO `project_framwork` VALUES ('168', '1081629222214967297', '1125753757710794752');
INSERT INTO `project_framwork` VALUES ('170', '999999', '1058961931014316032');
INSERT INTO `project_framwork` VALUES ('171', '888888', '1059236585582960640');

-- ----------------------------
-- Table structure for project_job
-- ----------------------------
DROP TABLE IF EXISTS `project_job`;
CREATE TABLE `project_job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  `code` varchar(64) NOT NULL COMMENT '任务编码',
  `number` varchar(64) DEFAULT NULL COMMENT '第多少次执行',
  `state` varchar(16) DEFAULT '' COMMENT '任务状态: {创建:Create} , {执行中:Executing}, {完成:Completed} ,{失败:Error}, {警告:Waring}',
  `createTime` datetime DEFAULT NULL COMMENT '执行任务时间',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=787 DEFAULT CHARSET=utf8mb4 COMMENT='任务';

-- ----------------------------
-- Records of project_job
-- ----------------------------
INSERT INTO `project_job` VALUES ('716', '1105399529737224192', '1105400388730683392', '1', 'Completed', '2018-10-04 16:59:31');
INSERT INTO `project_job` VALUES ('717', '1106948707261038592', '1106949033678553088', '1', 'Completed', '2018-10-05 18:01:54');
INSERT INTO `project_job` VALUES ('718', '1102343591786553344', '1106956283583348736', '1', 'Completed', '2018-10-05 18:08:56');
INSERT INTO `project_job` VALUES ('719', '1106948707261038592', '1106966041749045248', '1', 'Completed', '2018-10-05 18:18:24');
INSERT INTO `project_job` VALUES ('720', '1106948707261038592', '1109538641260011520', '1', 'Completed', '2018-10-07 11:54:09');
INSERT INTO `project_job` VALUES ('721', '1102343591786553344', '1111211651280896000', '1', 'Completed', '2018-10-08 14:57:11');
INSERT INTO `project_job` VALUES ('722', '1116944075411857408', '1116945999557206016', '1', 'Completed', '2018-10-12 11:40:14');
INSERT INTO `project_job` VALUES ('723', '1116944075411857408', '1116947339587002368', '1', 'Completed', '2018-10-12 11:41:32');
INSERT INTO `project_job` VALUES ('724', '1116944075411857408', '1116970274712371200', '1', 'Completed', '2018-10-12 12:03:47');
INSERT INTO `project_job` VALUES ('725', '1116944075411857408', '1116972679894056960', '1', 'Completed', '2018-10-12 12:06:07');
INSERT INTO `project_job` VALUES ('726', '1116944075411857408', '1117254000251953152', '1', 'Completed', '2018-10-12 16:39:02');
INSERT INTO `project_job` VALUES ('727', '1116944075411857408', '1121725988920049664', '1', 'Completed', '2018-10-15 16:57:26');
INSERT INTO `project_job` VALUES ('728', '1116944075411857408', '1121727294590107648', '1', 'Completed', '2018-10-15 16:58:42');
INSERT INTO `project_job` VALUES ('729', '1116944075411857408', '1121728411281604608', '1', 'Completed', '2018-10-15 16:59:47');
INSERT INTO `project_job` VALUES ('730', '1116944075411857408', '1121730936722374656', '1', 'Completed', '2018-10-15 17:02:14');
INSERT INTO `project_job` VALUES ('731', '1116944075411857408', '1121732740608638976', '1', 'Completed', '2018-10-15 17:03:59');
INSERT INTO `project_job` VALUES ('732', '1116944075411857408', '1121733685501444096', '1', 'Completed', '2018-10-15 17:04:54');
INSERT INTO `project_job` VALUES ('733', '1116944075411857408', '1121736606079205376', '1', 'Completed', '2018-10-15 17:07:44');
INSERT INTO `project_job` VALUES ('734', '1116944075411857408', '1121751844623179776', '1', 'Completed', '2018-10-15 17:22:31');
INSERT INTO `project_job` VALUES ('735', '1116944075411857408', '1121757462440402944', '1', 'Completed', '2018-10-15 17:27:58');
INSERT INTO `project_job` VALUES ('736', '1116944075411857408', '1121758012196216832', '1', 'Completed', '2018-10-15 17:28:30');
INSERT INTO `project_job` VALUES ('737', '1116944075411857408', '1121758046555955200', '1', 'Completed', '2018-10-15 17:28:32');
INSERT INTO `project_job` VALUES ('738', '1116944075411857408', '1121758046555955201', '1', 'Completed', '2018-10-15 17:28:32');
INSERT INTO `project_job` VALUES ('739', '1116944075411857408', '1121758063735824384', '1', 'Completed', '2018-10-15 17:28:33');
INSERT INTO `project_job` VALUES ('740', '1116944075411857408', '1121758115275431936', '1', 'Completed', '2018-10-15 17:28:36');
INSERT INTO `project_job` VALUES ('741', '1116944075411857408', '1121763286416064512', '1', 'Completed', '2018-10-15 17:33:37');
INSERT INTO `project_job` VALUES ('742', '1116944075411857408', '1121764059510177792', '1', 'Executing', '2018-10-15 17:34:22');
INSERT INTO `project_job` VALUES ('743', '1116944075411857408', '1121764162589392896', '1', 'Executing', '2018-10-15 17:34:28');
INSERT INTO `project_job` VALUES ('744', '1116944075411857408', '1121764248488738853', '1', 'Executing', '2018-10-15 17:34:33');
INSERT INTO `project_job` VALUES ('745', '1116944075411857408', '1121764763884814336', '1', 'Completed', '2018-10-15 17:35:03');
INSERT INTO `project_job` VALUES ('746', '1116944075411857408', '1121765210561413120', '1', 'Completed', '2018-10-15 17:35:29');
INSERT INTO `project_job` VALUES ('747', '1116944075411857408', '1121765588518535168', '1', 'Completed', '2018-10-15 17:35:51');
INSERT INTO `project_job` VALUES ('748', '1116944075411857408', '1121766000835395584', '1', 'Completed', '2018-10-15 17:36:15');
INSERT INTO `project_job` VALUES ('749', '1116944075411857408', '1121766327252910080', '1', 'Completed', '2018-10-15 17:36:34');
INSERT INTO `project_job` VALUES ('750', '1116944075411857408', '1121769642967662592', '1', 'Completed', '2018-10-15 17:39:47');
INSERT INTO `project_job` VALUES ('751', '1116944075411857408', '1121772632264900608', '1', 'Completed', '2018-10-15 17:42:41');
INSERT INTO `project_job` VALUES ('752', '1116944075411857408', '1121774917187502080', '1', 'Completed', '2018-10-15 17:44:54');
INSERT INTO `project_job` VALUES ('753', '1116944075411857408', '1121775415403708416', '1', 'Completed', '2018-10-15 17:45:23');
INSERT INTO `project_job` VALUES ('754', '1116944075411857408', '1121775965159522304', '1', 'Completed', '2018-10-15 17:45:55');
INSERT INTO `project_job` VALUES ('755', '1116944075411857408', '1121776841332850688', '1', 'Completed', '2018-10-15 17:46:46');
INSERT INTO `project_job` VALUES ('756', '1116944075411857408', '1121777202110103552', '1', 'Completed', '2018-10-15 17:47:07');
INSERT INTO `project_job` VALUES ('757', '1116944075411857408', '1121780878602108928', '1', 'Completed', '2018-10-15 17:50:41');
INSERT INTO `project_job` VALUES ('758', '1116944075411857408', '1121783094805233664', '1', 'Completed', '2018-10-15 17:52:50');
INSERT INTO `project_job` VALUES ('759', '1116944075411857408', '1123101821563887616', '1', 'Completed', '2018-10-16 15:12:10');
INSERT INTO `project_job` VALUES ('760', '1116944075411857408', '1123108899670007808', '1', 'Executing', '2018-10-16 15:19:02');
INSERT INTO `project_job` VALUES ('761', '1116944075411857408', '1123109844562821120', '1', 'Completed', '2018-10-16 15:19:57');
INSERT INTO `project_job` VALUES ('762', '1116944075411857408', '1123110067901120512', '1', 'Executing', '2018-10-16 15:20:10');
INSERT INTO `project_job` VALUES ('763', '1116944075411857408', '1123117369345531904', '1', 'Completed', '2018-10-16 15:27:15');
INSERT INTO `project_job` VALUES ('764', '1116944075411857408', '1123118486037028864', '1', 'Completed', '2018-10-16 15:28:20');
INSERT INTO `project_job` VALUES ('765', '1116944075411857408', '1123120083764862976', '1', 'Completed', '2018-10-16 15:29:53');
INSERT INTO `project_job` VALUES ('766', '1116944075411857408', '1123121251995967488', '1', 'Completed', '2018-10-16 15:31:01');
INSERT INTO `project_job` VALUES ('767', '1116944075411857408', '1123123347940007936', '1', 'Completed', '2018-10-16 15:33:03');
INSERT INTO `project_job` VALUES ('768', '1116944075411857408', '1123123897695821824', '1', 'Completed', '2018-10-16 15:33:35');
INSERT INTO `project_job` VALUES ('769', '1116944075411857408', '1123124825408757760', '1', 'Completed', '2018-10-16 15:34:29');
INSERT INTO `project_job` VALUES ('770', '1116944075411857408', '1123126594935283712', '1', 'Completed', '2018-10-16 15:36:12');
INSERT INTO `project_job` VALUES ('771', '1116944075411857408', '1123128639339716608', '1', 'Completed', '2018-10-16 15:38:11');
INSERT INTO `project_job` VALUES ('772', '1116944075411857408', '1123131096061009920', '1', 'Completed', '2018-10-16 15:40:34');
INSERT INTO `project_job` VALUES ('773', '1116944075411857408', '1123134463315369984', '1', 'Completed', '2018-10-16 15:43:50');
INSERT INTO `project_job` VALUES ('774', '1058961931014316032', '1123171692091899904', '1', 'Completed', '2018-10-16 16:19:57');
INSERT INTO `project_job` VALUES ('775', '1059236585582960640', '1123205673873145856', '1', 'Completed', '2018-10-16 16:52:55');
INSERT INTO `project_job` VALUES ('776', '1059236585582960640', '1123208182134046720', '1', 'Completed', '2018-10-16 16:55:21');
INSERT INTO `project_job` VALUES ('777', '1124294654241120256', '1124297007883198464', '1', 'Completed', '2018-10-17 10:31:39');
INSERT INTO `project_job` VALUES ('778', '1125753757710794752', '1125755200819806208', '1', 'Completed', '2018-10-18 10:06:17');
INSERT INTO `project_job` VALUES ('779', '1125753757710794752', '1125782911948800000', '1', 'Completed', '2018-10-18 10:33:10');
INSERT INTO `project_job` VALUES ('780', '1125753757710794752', '1125800641573806080', '1', 'Completed', '2018-10-18 10:50:22');
INSERT INTO `project_job` VALUES ('781', '1125753757710794752', '1126296761836101632', '1', 'Completed', '2018-10-18 18:51:40');
INSERT INTO `project_job` VALUES ('782', '1105399529737224192', '1127247530156490752', '1', 'Completed', '2018-10-19 10:14:02');
INSERT INTO `project_job` VALUES ('783', '1125753757710794752', '1130296166662676480', '1', 'Completed', '2018-10-21 11:31:36');
INSERT INTO `project_job` VALUES ('784', '1058961931014316032', '1132169700116684800', '1', 'Completed', '2018-10-22 17:49:10');
INSERT INTO `project_job` VALUES ('785', '1058961931014316032', '1133519453719003136', '1', 'Completed', '2018-10-23 15:38:36');
INSERT INTO `project_job` VALUES ('786', '1059236585582960640', '1133635520915210240', '1', 'Completed', '2018-10-23 17:31:12');

-- ----------------------------
-- Table structure for project_job_logs
-- ----------------------------
DROP TABLE IF EXISTS `project_job_logs`;
CREATE TABLE `project_job_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) DEFAULT NULL COMMENT '任务编码',
  `log` varchar(1024) DEFAULT '' COMMENT '日志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61420 DEFAULT CHARSET=utf8mb4 COMMENT='任务日志';

-- ----------------------------
-- Records of project_job_logs
-- ----------------------------
INSERT INTO `project_job_logs` VALUES ('59774', '1105400388730683392', 'Start By AI-Code @Copyright <a href=\'http://www.rzhkj.com\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('59775', '1105400388730683392', '> ✔ 2018-10-04 16:59:32:066&nbsp;&nbsp; 已初始化项目 【 jlb ( jlb )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('59776', '1105400388730683392', '> ✔ 2018-10-04 16:59:41:964&nbsp;&nbsp;已获取项目 【springboot-redis-swagger-lombok-frontend】 的模板');
INSERT INTO `project_job_logs` VALUES ('59777', '1105400388730683392', '> ✔ 2018-10-04 16:59:42:021&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('59778', '1105400388730683392', '> ✔ 2018-10-04 16:59:42:284&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59779', '1105400388730683392', '> ✔ 2018-10-04 16:59:42:470&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59780', '1105400388730683392', '> ✔ 2018-10-04 16:59:42:722&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59781', '1105400388730683392', '> ✔ 2018-10-04 16:59:42:779&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/Application.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59782', '1105400388730683392', '> ✔ 2018-10-04 16:59:42:831&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/base/Uid.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59783', '1105400388730683392', '> ✔ 2018-10-04 16:59:42:885&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/base/WebMvcConfigurer.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59784', '1105400388730683392', '> ✔ 2018-10-04 16:59:42:942&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/application.yml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59785', '1105400388730683392', '> ✔ 2018-10-04 16:59:42:999&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/bak_bootstrap.yml 的源码');
INSERT INTO `project_job_logs` VALUES ('59786', '1105400388730683392', '> ✔ 2018-10-04 16:59:43:241&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/mapper/${model}/${className}.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59787', '1105400388730683392', '> ✔ 2018-10-04 16:59:43:295&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/mapper/mybatis-config.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59788', '1105400388730683392', '> ✔ 2018-10-04 16:59:43:345&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/sentry.properties.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59789', '1105400388730683392', '> ✔ 2018-10-04 16:59:43:398&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/spring-uid.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59790', '1105400388730683392', '> ✔ 2018-10-04 16:59:43:447&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('59791', '1105400388730683392', '> ✔ 2018-10-04 16:59:43:508&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseDAO.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59792', '1105400388730683392', '> ✔ 2018-10-04 16:59:43:564&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseEntity.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59793', '1105400388730683392', '> ✔ 2018-10-04 16:59:43:625&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseEntityVO.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59794', '1105400388730683392', '> ✔ 2018-10-04 16:59:43:681&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseHibernateDAO.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59795', '1105400388730683392', '> ✔ 2018-10-04 16:59:43:736&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseHibernateSV.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59796', '1105400388730683392', '> ✔ 2018-10-04 16:59:43:790&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseSV.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59797', '1105400388730683392', '> ✔ 2018-10-04 16:59:43:844&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/entity/BeanRet.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59798', '1105400388730683392', '> ✔ 2018-10-04 16:59:43:905&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/entity/Page.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59799', '1105400388730683392', '> ✔ 2018-10-04 16:59:43:955&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-sv/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('59800', '1105400388730683392', '> ✔ 2018-10-04 16:59:44:024&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-sv/src/main/java/${basepackage}/core/base/BaseSVImpl.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59801', '1105400388730683392', '> ✔ 2018-10-04 16:59:44:082&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-sv/src/main/java/${basepackage}/core/cache/MybatisRedisCache.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59802', '1105400388730683392', '> ✔ 2018-10-04 16:59:44:138&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-sv/src/main/java/${basepackage}/core/cache/RedisCacheTransfer.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59803', '1105400388730683392', '> ✔ 2018-10-04 16:59:44:188&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('59804', '1105400388730683392', '> ✔ 2018-10-04 16:59:44:249&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/src/main/java/${basepackage}/core/ExceptionHandle.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59805', '1105400388730683392', '> ✔ 2018-10-04 16:59:44:305&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/src/main/java/${basepackage}/core/interceptor/ContextConfiguration.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59806', '1105400388730683392', '> ✔ 2018-10-04 16:59:44:361&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59807', '1105400388730683392', '> ✔ 2018-10-04 16:59:44:466&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/src/main/java/${basepackage}/core/interceptor/LoginInterceptor.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59808', '1105400388730683392', '> ✔ 2018-10-04 16:59:44:520&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/src/main/java/${basepackage}/core/package-info.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59809', '1105400388730683392', '> ✔ 2018-10-04 16:59:44:574&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('59810', '1105400388730683392', '> ✔ 2018-10-04 16:59:44:624&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('59811', '1105400388730683392', '> ✔ 2018-10-04 16:59:44:687&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/config/SwaggerConfig.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59812', '1105400388730683392', '> ✔ 2018-10-04 16:59:44:747&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/enums/SexEnum.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59813', '1105400388730683392', '> ✔ 2018-10-04 16:59:44:796&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/enums/YNEnum.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59814', '1105400388730683392', '> ✔ 2018-10-04 16:59:44:955&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/exceptions/${className}Exception.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59815', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:011&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/exceptions/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59816', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:077&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/ConfigUtil.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59817', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:138&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/DateTools.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59818', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:197&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/Executors.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59819', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:254&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/FileUtil.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59820', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:309&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/HandleFuncs.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59821', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:364&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/memcached/Constants.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59822', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:421&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/memcached/IScanFileToCached.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59823', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:477&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/memcached/MemcachedKey.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59824', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:534&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/memcached/MemCachedUtil.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59825', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:594&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/memcached/ScanFileToCached.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59826', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:649&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/OkhttpLogInterceptor.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59827', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:708&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/redis/RedisKey.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59828', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:766&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59829', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:828&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/BASE64Decoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59830', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:896&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/BASE64Encoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59831', '1105400388730683392', '> ✔ 2018-10-04 16:59:45:964&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/BouncyCastleProvider.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59832', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:032&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/CharacterDecoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59833', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:090&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/coder/DESCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59834', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:146&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/coder/HmacCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59835', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:202&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/coder/MDCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59836', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:259&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/coder/RSACoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59837', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:315&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/coder/SHACoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59838', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:373&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/Hex.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59839', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:432&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/http/HTTPSCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59840', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:492&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/http/HTTPSPKCSCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59841', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:561&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/Md5.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59842', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:618&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/Rzh_3DES.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59843', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:677&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/Rzh_SHA1.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59844', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:735&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/SecurityCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59845', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:795&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/SecurityUtil.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59846', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:870&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/SortTools.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59847', '1105400388730683392', '> ✔ 2018-10-04 16:59:46:931&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/StringTools.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59848', '1105400388730683392', '> ✔ 2018-10-04 16:59:47:010&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/UuidTools.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59849', '1105400388730683392', '> ✔ 2018-10-04 16:59:47:065&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/ZipTools.java 的源码');
INSERT INTO `project_job_logs` VALUES ('59850', '1105400388730683392', '> ✔ 2018-10-04 16:59:47:124&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/resources/config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('59851', '1105400388730683392', '> ✔ 2018-10-04 16:59:47:174&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/resources/upload_config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('59852', '1105400388730683392', '> ✔ 2018-10-04 16:59:47:224&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/facade/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('59853', '1105400388730683392', '> ✔ 2018-10-04 16:59:47:477&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/facade/src/main/java/${basepackage}/${model}/entity/${className}.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59854', '1105400388730683392', '> ✔ 2018-10-04 16:59:47:653&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/facade/src/main/java/${basepackage}/${model}/entity/${className}State.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59855', '1105400388730683392', '> ✔ 2018-10-04 16:59:47:837&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/facade/src/main/java/${basepackage}/${model}/service/${className}SV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59856', '1105400388730683392', '> ✔ 2018-10-04 16:59:49:566&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/libs/uid-generator-1.0.0-SNAPSHOT.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('59857', '1105400388730683392', '> ✔ 2018-10-04 16:59:49:618&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/libs/worker_node.sql 的源码');
INSERT INTO `project_job_logs` VALUES ('59858', '1105400388730683392', '> ✔ 2018-10-04 16:59:49:667&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/settings.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('59859', '1105400388730683392', '> ✔ 2018-10-04 16:59:49:819&nbsp;&nbsp; 【已经生成】 jlbSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('59860', '1105400388730683392', '> ✔ 2018-10-04 16:59:49:824&nbsp;&nbsp;获取代码仓库信息: 2564054974@qq.com');
INSERT INTO `project_job_logs` VALUES ('59861', '1105400388730683392', '> ✔ 2018-10-04 16:59:56:812&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'https://gitee.com/helixin/jlb.git\' target=\'_blank\'>https://gitee.com/helixin/jlb.git </a>仓库');
INSERT INTO `project_job_logs` VALUES ('59862', '1105400388730683392', '> ✔ 2018-10-04 16:59:57:250&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/jlb.zip\' target=\'_blank\'>jlb.zip</a>');
INSERT INTO `project_job_logs` VALUES ('59863', '1105400388730683392', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('59864', '1105400388730683392', 'End');
INSERT INTO `project_job_logs` VALUES ('59865', '1106949033678553088', 'Start By AI-Code @Copyright <a href=\'http://www.rzhkj.com\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('59866', '1106949033678553088', '> ✔ 2018-10-05 18:01:55:127&nbsp;&nbsp; 已初始化项目 【 jlb-frontend ( jlb_frontend )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('59867', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:416&nbsp;&nbsp;已获取项目 【angular-template】 的模板');
INSERT INTO `project_job_logs` VALUES ('59868', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:543&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/.editorconfig 的源码');
INSERT INTO `project_job_logs` VALUES ('59869', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:556&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/.gitignore 的源码');
INSERT INTO `project_job_logs` VALUES ('59870', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:575&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/angular.json 的源码');
INSERT INTO `project_job_logs` VALUES ('59871', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:590&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/e2e/protractor.conf.js 的源码');
INSERT INTO `project_job_logs` VALUES ('59872', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:602&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/e2e/src/app.e2e-spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59873', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:612&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/e2e/src/app.po.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59874', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:620&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/e2e/tsconfig.e2e.json 的源码');
INSERT INTO `project_job_logs` VALUES ('59875', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:696&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/package-lock.json 的源码');
INSERT INTO `project_job_logs` VALUES ('59876', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:708&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/package.json 的源码');
INSERT INTO `project_job_logs` VALUES ('59877', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:772&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/proxy.conf.js 的源码');
INSERT INTO `project_job_logs` VALUES ('59878', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:785&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/app.component.css 的源码');
INSERT INTO `project_job_logs` VALUES ('59879', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:794&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/app.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('59880', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:808&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/app.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59881', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:819&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/app.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59882', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:830&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/app.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59883', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:847&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/main/main.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('59884', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:856&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/main/main.component.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('59885', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:867&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/main/main.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59886', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:879&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/main/main.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59887', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:891&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/page/page.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('59888', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:901&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/page/page.component.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('59889', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:914&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/page/page.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59890', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:923&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/page/page.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59891', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:936&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/module-import-guard.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59892', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:947&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/date-en.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59893', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:959&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/date-en.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59894', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:968&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/enum-name.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59895', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:979&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/enum-name.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59896', '1106949033678553088', '> ✔ 2018-10-05 18:02:13:990&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/img-preview.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59897', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:003&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/img-preview.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59898', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:015&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/img-size.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59899', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:030&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/img-size.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59900', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:042&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/level-2-area-name.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59901', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:056&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/level-2-area-name.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59902', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:069&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/splice-str.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59903', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:081&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/splice-str.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59904', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:092&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/public.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59905', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:104&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/public.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59906', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:115&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/service/ajax.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59907', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:126&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/service/main.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59908', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:140&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/service/pattern.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59909', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:152&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/setting/enums.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59910', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:170&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/setting/menus.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59911', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:179&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/setting/model.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59912', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:198&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/setting/setting.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59913', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:215&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/setting/setting_url.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59914', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:226&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/area_level_1.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59915', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:248&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/area_level_2.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59916', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:330&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/area_level_3.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59917', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:339&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/china_area.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59918', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:349&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/page.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59919', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:360&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/util.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59920', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:373&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-detail/${dashedCaseName}-detail.component.css 的源码');
INSERT INTO `project_job_logs` VALUES ('59921', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:401&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-detail/${dashedCaseName}-detail.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('59922', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:421&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-detail/${dashedCaseName}-detail.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59923', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:446&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-detail/${dashedCaseName}-detail.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59924', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:461&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-edit/${dashedCaseName}-edit.component.css 的源码');
INSERT INTO `project_job_logs` VALUES ('59925', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:484&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-edit/${dashedCaseName}-edit.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('59926', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:505&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-edit/${dashedCaseName}-edit.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59927', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:530&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-edit/${dashedCaseName}-edit.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59928', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:554&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-list/${dashedCaseName}-list.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('59929', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:566&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-list/${dashedCaseName}-list.component.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('59930', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:584&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-list/${dashedCaseName}-list.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59931', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:604&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-list/${dashedCaseName}-list.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59932', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:624&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${model}.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59933', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:641&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${model}.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59934', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:657&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${model}.service.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59935', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:676&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${model}.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59936', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:687&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home/home.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('59937', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:694&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home/home.component.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('59938', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:705&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home/home.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59939', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:714&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home/home.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59940', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:723&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59941', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:730&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59942', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:739&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home.service.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59943', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:748&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59944', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:759&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/login/login.component.css 的源码');
INSERT INTO `project_job_logs` VALUES ('59945', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:769&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/login/login.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('59946', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:776&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/login/login.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59947', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:785&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/login/login.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59948', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:795&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/pages.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59949', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:804&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/pages.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59950', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:814&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/pages.service.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59951', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:825&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/pages.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59952', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:834&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/routes.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59953', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:843&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/routes.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59954', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:855&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/routes.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59955', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:865&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/compontents/back-btn/back-btn.component.css 的源码');
INSERT INTO `project_job_logs` VALUES ('59956', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:874&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/compontents/back-btn/back-btn.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('59957', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:882&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/compontents/back-btn/back-btn.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59958', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:890&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/compontents/back-btn/back-btn.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59959', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:899&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/shared.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59960', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:908&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/shared.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59961', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:916&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/.gitkeep 的源码');
INSERT INTO `project_job_logs` VALUES ('59962', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:924&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/css/base.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('59963', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:933&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/css/commons.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('59964', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:947&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/css/fonts.css 的源码');
INSERT INTO `project_job_logs` VALUES ('59965', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:955&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/css/theme.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('59966', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:964&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/css/util.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('59967', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:974&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/i18n/en_US.json 的源码');
INSERT INTO `project_job_logs` VALUES ('59968', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:986&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/i18n/zh_CN.json 的源码');
INSERT INTO `project_job_logs` VALUES ('59969', '1106949033678553088', '> ✔ 2018-10-05 18:02:14:994&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/browserslist 的源码');
INSERT INTO `project_job_logs` VALUES ('59970', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:003&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/environments/environment.prod.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59971', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:012&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/environments/environment.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59972', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:030&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/favicon.ico 的源码');
INSERT INTO `project_job_logs` VALUES ('59973', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:040&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/index.html 的源码');
INSERT INTO `project_job_logs` VALUES ('59974', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:051&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/karma.conf.js 的源码');
INSERT INTO `project_job_logs` VALUES ('59975', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:059&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/main.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59976', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:068&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/polyfills.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59977', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:075&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/styles.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('59978', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:090&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/test.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('59979', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:098&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/theme.less 的源码');
INSERT INTO `project_job_logs` VALUES ('59980', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:108&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/tsconfig.app.json 的源码');
INSERT INTO `project_job_logs` VALUES ('59981', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:118&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/tsconfig.spec.json 的源码');
INSERT INTO `project_job_logs` VALUES ('59982', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:129&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/tslint.json 的源码');
INSERT INTO `project_job_logs` VALUES ('59983', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:136&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/tsconfig.json 的源码');
INSERT INTO `project_job_logs` VALUES ('59984', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:145&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/tslint.json 的源码');
INSERT INTO `project_job_logs` VALUES ('59985', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:323&nbsp;&nbsp; 【已经生成】 jlb_frontendSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('59986', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:328&nbsp;&nbsp;获取代码仓库信息: aa');
INSERT INTO `project_job_logs` VALUES ('59987', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:335&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>aa </a>仓库');
INSERT INTO `project_job_logs` VALUES ('59988', '1106949033678553088', '> ✔ 2018-10-05 18:02:15:483&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/jlb_frontend.zip\' target=\'_blank\'>jlb_frontend.zip</a>');
INSERT INTO `project_job_logs` VALUES ('59989', '1106949033678553088', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('59990', '1106949033678553088', 'End');
INSERT INTO `project_job_logs` VALUES ('59991', '1106956283583348736', 'Start By AI-Code @Copyright <a href=\'http://www.rzhkj.com\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('59992', '1106956283583348736', '> ✔ 2018-10-05 18:08:56:434&nbsp;&nbsp; 已初始化项目 【 aa ( aa )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('59993', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:217&nbsp;&nbsp;已获取项目 【springboot-redis-swagger-lombok-frontend】 的模板');
INSERT INTO `project_job_logs` VALUES ('59994', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:234&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('59995', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:275&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59996', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:302&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59997', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:336&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59998', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:348&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/Application.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('59999', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:365&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/base/Uid.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60000', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:379&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/base/WebMvcConfigurer.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60001', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:394&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/application.yml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60002', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:407&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/bak_bootstrap.yml 的源码');
INSERT INTO `project_job_logs` VALUES ('60003', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:448&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/mapper/${model}/${className}.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60004', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:460&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/mapper/mybatis-config.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60005', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:468&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/sentry.properties.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60006', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:477&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/spring-uid.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60007', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:486&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60008', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:503&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseDAO.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60009', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:519&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseEntity.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60010', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:533&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseEntityVO.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60011', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:548&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseHibernateDAO.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60012', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:563&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseHibernateSV.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60013', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:575&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseSV.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60014', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:593&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/entity/BeanRet.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60015', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:610&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/entity/Page.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60016', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:621&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-sv/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60017', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:641&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-sv/src/main/java/${basepackage}/core/base/BaseSVImpl.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60018', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:657&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-sv/src/main/java/${basepackage}/core/cache/MybatisRedisCache.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60019', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:670&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-sv/src/main/java/${basepackage}/core/cache/RedisCacheTransfer.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60020', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:678&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60021', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:695&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/src/main/java/${basepackage}/core/ExceptionHandle.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60022', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:709&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/src/main/java/${basepackage}/core/interceptor/ContextConfiguration.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60023', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:723&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60024', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:739&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/src/main/java/${basepackage}/core/interceptor/LoginInterceptor.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60025', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:751&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/src/main/java/${basepackage}/core/package-info.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60026', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:762&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60027', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:772&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60028', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:791&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/config/SwaggerConfig.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60029', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:805&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/enums/SexEnum.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60030', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:818&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/enums/YNEnum.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60031', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:843&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/exceptions/${className}Exception.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60032', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:861&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/exceptions/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60033', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:876&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/ConfigUtil.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60034', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:900&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/DateTools.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60035', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:911&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/Executors.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60036', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:925&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/FileUtil.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60037', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:940&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/HandleFuncs.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60038', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:951&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/memcached/Constants.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60039', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:965&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/memcached/IScanFileToCached.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60040', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:975&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/memcached/MemcachedKey.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60041', '1106956283583348736', '> ✔ 2018-10-05 18:09:06:991&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/memcached/MemCachedUtil.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60042', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:003&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/memcached/ScanFileToCached.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60043', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:019&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/OkhttpLogInterceptor.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60044', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:034&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/redis/RedisKey.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60045', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:047&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60046', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:060&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/BASE64Decoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60047', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:074&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/BASE64Encoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60048', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:095&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/BouncyCastleProvider.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60049', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:112&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/CharacterDecoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60050', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:125&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/coder/DESCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60051', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:140&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/coder/HmacCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60052', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:156&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/coder/MDCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60053', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:172&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/coder/RSACoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60054', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:185&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/coder/SHACoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60055', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:202&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/Hex.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60056', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:217&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/http/HTTPSCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60057', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:232&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/http/HTTPSPKCSCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60058', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:246&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/Md5.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60059', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:259&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/Rzh_3DES.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60060', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:270&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/Rzh_SHA1.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60061', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:285&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/SecurityCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60062', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:297&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/SecurityUtil.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60063', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:312&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/SortTools.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60064', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:326&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/StringTools.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60065', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:339&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/UuidTools.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60066', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:352&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/ZipTools.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60067', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:364&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/resources/config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60068', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:374&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/resources/upload_config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60069', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:385&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/facade/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60070', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:417&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/facade/src/main/java/${basepackage}/${model}/entity/${className}.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60071', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:440&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/facade/src/main/java/${basepackage}/${model}/entity/${className}State.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60072', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:464&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/facade/src/main/java/${basepackage}/${model}/service/${className}SV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60073', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:658&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/libs/uid-generator-1.0.0-SNAPSHOT.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60074', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:668&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/libs/worker_node.sql 的源码');
INSERT INTO `project_job_logs` VALUES ('60075', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:678&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/settings.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60076', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:835&nbsp;&nbsp; 【已经生成】 aaSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('60077', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:841&nbsp;&nbsp;获取代码仓库信息: aa');
INSERT INTO `project_job_logs` VALUES ('60078', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:846&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>aa </a>仓库');
INSERT INTO `project_job_logs` VALUES ('60079', '1106956283583348736', '> ✔ 2018-10-05 18:09:07:980&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/aa.zip\' target=\'_blank\'>aa.zip</a>');
INSERT INTO `project_job_logs` VALUES ('60080', '1106956283583348736', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('60081', '1106956283583348736', 'End');
INSERT INTO `project_job_logs` VALUES ('60082', '1106966041749045248', 'Start By AI-Code @Copyright <a href=\'http://www.rzhkj.com\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('60083', '1106966041749045248', '> ✔ 2018-10-05 18:18:24:874&nbsp;&nbsp; 已初始化项目 【 jlb-frontend ( jlb_frontend )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('60084', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:152&nbsp;&nbsp;已获取项目 【angular-template】 的模板');
INSERT INTO `project_job_logs` VALUES ('60085', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:169&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/.editorconfig 的源码');
INSERT INTO `project_job_logs` VALUES ('60086', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:184&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/.gitignore 的源码');
INSERT INTO `project_job_logs` VALUES ('60087', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:201&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/angular.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60088', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:224&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/e2e/protractor.conf.js 的源码');
INSERT INTO `project_job_logs` VALUES ('60089', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:241&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/e2e/src/app.e2e-spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60090', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:253&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/e2e/src/app.po.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60091', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:263&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/e2e/tsconfig.e2e.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60092', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:326&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/package-lock.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60093', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:336&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/package.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60094', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:350&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/proxy.conf.js 的源码');
INSERT INTO `project_job_logs` VALUES ('60095', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:359&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/app.component.css 的源码');
INSERT INTO `project_job_logs` VALUES ('60096', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:367&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/app.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60097', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:378&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/app.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60098', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:389&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/app.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60099', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:399&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/app.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60100', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:414&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/main/main.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60101', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:423&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/main/main.component.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60102', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:431&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/main/main.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60103', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:441&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/main/main.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60104', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:450&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/page/page.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60105', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:460&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/page/page.component.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60106', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:469&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/page/page.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60107', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:478&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/page/page.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60108', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:491&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/module-import-guard.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60109', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:502&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/date-en.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60110', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:511&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/date-en.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60111', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:519&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/enum-name.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60112', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:527&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/enum-name.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60113', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:536&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/img-preview.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60114', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:544&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/img-preview.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60115', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:553&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/img-size.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60116', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:563&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/img-size.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60117', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:574&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/level-2-area-name.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60118', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:585&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/level-2-area-name.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60119', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:594&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/splice-str.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60120', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:602&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/splice-str.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60121', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:611&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/public.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60122', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:620&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/public.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60123', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:633&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/service/ajax.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60124', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:644&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/service/main.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60125', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:655&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/service/pattern.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60126', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:667&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/setting/enums.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60127', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:678&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/setting/menus.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60128', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:690&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/setting/model.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60129', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:714&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/setting/setting.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60130', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:731&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/setting/setting_url.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60131', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:746&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/area_level_1.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60132', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:768&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/area_level_2.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60133', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:849&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/area_level_3.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60134', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:875&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/china_area.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60135', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:883&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/page.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60136', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:894&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/util.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60137', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:913&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-detail/${dashedCaseName}-detail.component.css 的源码');
INSERT INTO `project_job_logs` VALUES ('60138', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:938&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-detail/${dashedCaseName}-detail.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60139', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:958&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-detail/${dashedCaseName}-detail.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60140', '1106966041749045248', '> ✔ 2018-10-05 18:18:35:984&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-detail/${dashedCaseName}-detail.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60141', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:001&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-edit/${dashedCaseName}-edit.component.css 的源码');
INSERT INTO `project_job_logs` VALUES ('60142', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:033&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-edit/${dashedCaseName}-edit.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60143', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:059&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-edit/${dashedCaseName}-edit.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60144', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:096&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-edit/${dashedCaseName}-edit.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60145', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:124&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-list/${dashedCaseName}-list.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60146', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:138&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-list/${dashedCaseName}-list.component.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60147', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:161&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-list/${dashedCaseName}-list.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60148', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:184&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-list/${dashedCaseName}-list.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60149', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:211&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${model}.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60150', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:246&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${model}.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60151', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:280&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${model}.service.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60152', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:317&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${model}.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60153', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:328&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home/home.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60154', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:339&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home/home.component.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60155', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:350&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home/home.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60156', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:363&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home/home.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60157', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:371&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60158', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:387&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60159', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:399&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home.service.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60160', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:412&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60161', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:425&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/login/login.component.css 的源码');
INSERT INTO `project_job_logs` VALUES ('60162', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:440&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/login/login.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60163', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:452&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/login/login.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60164', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:464&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/login/login.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60165', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:474&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/pages.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60166', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:486&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/pages.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60167', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:499&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/pages.service.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60168', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:510&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/pages.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60169', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:518&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/routes.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60170', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:527&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/routes.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60171', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:538&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/routes.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60172', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:549&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/compontents/back-btn/back-btn.component.css 的源码');
INSERT INTO `project_job_logs` VALUES ('60173', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:559&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/compontents/back-btn/back-btn.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60174', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:567&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/compontents/back-btn/back-btn.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60175', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:575&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/compontents/back-btn/back-btn.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60176', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:585&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/shared.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60177', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:593&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/shared.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60178', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:600&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/.gitkeep 的源码');
INSERT INTO `project_job_logs` VALUES ('60179', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:612&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/css/base.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60180', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:623&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/css/commons.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60181', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:645&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/css/fonts.css 的源码');
INSERT INTO `project_job_logs` VALUES ('60182', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:662&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/css/theme.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60183', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:676&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/css/util.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60184', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:687&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/i18n/en_US.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60185', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:704&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/i18n/zh_CN.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60186', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:714&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/browserslist 的源码');
INSERT INTO `project_job_logs` VALUES ('60187', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:725&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/environments/environment.prod.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60188', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:734&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/environments/environment.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60189', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:749&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/favicon.ico 的源码');
INSERT INTO `project_job_logs` VALUES ('60190', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:758&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/index.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60191', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:774&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/karma.conf.js 的源码');
INSERT INTO `project_job_logs` VALUES ('60192', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:787&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/main.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60193', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:798&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/polyfills.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60194', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:806&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/styles.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60195', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:824&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/test.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60196', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:838&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/theme.less 的源码');
INSERT INTO `project_job_logs` VALUES ('60197', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:851&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/tsconfig.app.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60198', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:870&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/tsconfig.spec.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60199', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:886&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/tslint.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60200', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:896&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/tsconfig.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60201', '1106966041749045248', '> ✔ 2018-10-05 18:18:36:906&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/tslint.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60202', '1106966041749045248', '> ✔ 2018-10-05 18:18:37:087&nbsp;&nbsp; 【已经生成】 jlb_frontendSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('60203', '1106966041749045248', '> ✔ 2018-10-05 18:18:37:092&nbsp;&nbsp;获取代码仓库信息: aa');
INSERT INTO `project_job_logs` VALUES ('60204', '1106966041749045248', '> ✔ 2018-10-05 18:18:37:101&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>aa </a>仓库');
INSERT INTO `project_job_logs` VALUES ('60205', '1106966041749045248', '> ✔ 2018-10-05 18:18:37:235&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/jlb_frontend.zip\' target=\'_blank\'>jlb_frontend.zip</a>');
INSERT INTO `project_job_logs` VALUES ('60206', '1106966041749045248', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('60207', '1106966041749045248', 'End');
INSERT INTO `project_job_logs` VALUES ('60208', '1109538641260011520', 'Start By AI-Code @Copyright <a href=\'http://www.rzhkj.com\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('60209', '1109538641260011520', '> ✔ 2018-10-07 11:54:09:489&nbsp;&nbsp; 已初始化项目 【 jlb-frontend ( jlb_frontend )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('60210', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:603&nbsp;&nbsp;已获取项目 【angular-template】 的模板');
INSERT INTO `project_job_logs` VALUES ('60211', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:624&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/.editorconfig 的源码');
INSERT INTO `project_job_logs` VALUES ('60212', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:638&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/.gitignore 的源码');
INSERT INTO `project_job_logs` VALUES ('60213', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:647&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/angular.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60214', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:656&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/e2e/protractor.conf.js 的源码');
INSERT INTO `project_job_logs` VALUES ('60215', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:667&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/e2e/src/app.e2e-spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60216', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:677&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/e2e/src/app.po.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60217', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:685&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/e2e/tsconfig.e2e.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60218', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:696&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/package-lock.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60219', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:707&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/package.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60220', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:717&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/proxy.conf.js 的源码');
INSERT INTO `project_job_logs` VALUES ('60221', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:728&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/app.component.css 的源码');
INSERT INTO `project_job_logs` VALUES ('60222', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:738&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/app.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60223', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:746&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/app.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60224', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:756&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/app.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60225', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:768&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/app.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60226', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:778&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/main/main.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60227', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:787&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/main/main.component.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60228', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:798&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/main/main.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60229', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:808&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/main/main.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60230', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:816&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/page/page.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60231', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:827&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/page/page.component.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60232', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:836&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/page/page.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60233', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:844&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/layout/page/page.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60234', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:853&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/module-import-guard.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60235', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:864&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/date-en.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60236', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:874&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/date-en.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60237', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:882&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/enum-name.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60238', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:890&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/enum-name.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60239', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:902&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/img-preview.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60240', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:910&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/img-preview.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60241', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:921&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/img-size.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60242', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:931&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/img-size.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60243', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:941&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/level-2-area-name.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60244', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:956&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/level-2-area-name.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60245', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:965&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/splice-str.pipe.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60246', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:978&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/pipes/splice-str.pipe.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60247', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:989&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/public.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60248', '1109538641260011520', '> ✔ 2018-10-07 11:54:26:999&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/public.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60249', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:007&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/service/ajax.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60250', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:016&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/service/main.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60251', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:028&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/service/pattern.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60252', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:036&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/setting/enums.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60253', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:046&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/setting/menus.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60254', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:058&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/setting/model.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60255', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:068&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/setting/setting.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60256', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:076&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/setting/setting_url.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60257', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:085&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/area_level_1.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60258', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:096&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/area_level_2.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60259', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:111&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/area_level_3.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60260', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:128&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/china_area.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60261', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:140&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/page.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60262', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:148&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/public/util/util.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60263', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:263&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-detail/${dashedCaseName}-detail.component.css 的源码');
INSERT INTO `project_job_logs` VALUES ('60264', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:358&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-detail/${dashedCaseName}-detail.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60265', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:399&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-detail/${dashedCaseName}-detail.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60266', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:422&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-detail/${dashedCaseName}-detail.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60267', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:435&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-edit/${dashedCaseName}-edit.component.css 的源码');
INSERT INTO `project_job_logs` VALUES ('60268', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:450&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-edit/${dashedCaseName}-edit.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60269', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:467&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-edit/${dashedCaseName}-edit.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60270', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:489&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-edit/${dashedCaseName}-edit.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60271', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:504&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-list/${dashedCaseName}-list.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60272', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:516&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-list/${dashedCaseName}-list.component.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60273', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:528&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-list/${dashedCaseName}-list.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60274', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:541&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${dashedCaseName}-list/${dashedCaseName}-list.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60275', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:556&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${model}.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60276', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:577&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${model}.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60277', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:590&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${model}.service.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60278', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:609&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/${model}/${model}.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60279', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:617&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home/home.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60280', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:629&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home/home.component.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60281', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:638&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home/home.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60282', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:646&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home/home.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60283', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:657&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60284', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:666&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60285', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:674&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home.service.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60286', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:684&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/home/home.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60287', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:695&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/login/login.component.css 的源码');
INSERT INTO `project_job_logs` VALUES ('60288', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:703&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/login/login.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60289', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:713&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/login/login.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60290', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:724&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/login/login.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60291', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:734&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/pages.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60292', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:742&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/pages.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60293', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:752&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/pages.service.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60294', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:767&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/pages/pages.service.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60295', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:780&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/routes.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60296', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:789&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/routes.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60297', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:801&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/routes/routes.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60298', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:812&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/compontents/back-btn/back-btn.component.css 的源码');
INSERT INTO `project_job_logs` VALUES ('60299', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:821&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/compontents/back-btn/back-btn.component.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60300', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:832&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/compontents/back-btn/back-btn.component.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60301', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:840&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/compontents/back-btn/back-btn.component.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60302', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:850&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/shared.module.spec.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60303', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:862&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/app/shared/shared.module.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60304', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:873&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/.gitkeep 的源码');
INSERT INTO `project_job_logs` VALUES ('60305', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:883&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/css/base.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60306', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:892&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/css/commons.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60307', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:903&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/css/fonts.css 的源码');
INSERT INTO `project_job_logs` VALUES ('60308', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:914&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/css/theme.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60309', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:923&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/css/util.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60310', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:932&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/i18n/en_US.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60311', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:941&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/assets/i18n/zh_CN.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60312', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:952&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/browserslist 的源码');
INSERT INTO `project_job_logs` VALUES ('60313', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:962&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/environments/environment.prod.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60314', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:970&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/environments/environment.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60315', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:981&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/favicon.ico 的源码');
INSERT INTO `project_job_logs` VALUES ('60316', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:991&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/index.html 的源码');
INSERT INTO `project_job_logs` VALUES ('60317', '1109538641260011520', '> ✔ 2018-10-07 11:54:27:999&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/karma.conf.js 的源码');
INSERT INTO `project_job_logs` VALUES ('60318', '1109538641260011520', '> ✔ 2018-10-07 11:54:28:007&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/main.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60319', '1109538641260011520', '> ✔ 2018-10-07 11:54:28:019&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/polyfills.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60320', '1109538641260011520', '> ✔ 2018-10-07 11:54:28:028&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/styles.scss 的源码');
INSERT INTO `project_job_logs` VALUES ('60321', '1109538641260011520', '> ✔ 2018-10-07 11:54:28:037&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/test.ts 的源码');
INSERT INTO `project_job_logs` VALUES ('60322', '1109538641260011520', '> ✔ 2018-10-07 11:54:28:046&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/theme.less 的源码');
INSERT INTO `project_job_logs` VALUES ('60323', '1109538641260011520', '> ✔ 2018-10-07 11:54:28:065&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/tsconfig.app.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60324', '1109538641260011520', '> ✔ 2018-10-07 11:54:28:073&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/tsconfig.spec.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60325', '1109538641260011520', '> ✔ 2018-10-07 11:54:28:085&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/src/tslint.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60326', '1109538641260011520', '> ✔ 2018-10-07 11:54:28:095&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/tsconfig.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60327', '1109538641260011520', '> ✔ 2018-10-07 11:54:28:105&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/angular-template/tslint.json 的源码');
INSERT INTO `project_job_logs` VALUES ('60328', '1109538641260011520', '> ✔ 2018-10-07 11:54:28:315&nbsp;&nbsp; 【已经生成】 jlb_frontendSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('60329', '1109538641260011520', '> ✔ 2018-10-07 11:54:28:320&nbsp;&nbsp;获取代码仓库信息: aa');
INSERT INTO `project_job_logs` VALUES ('60330', '1109538641260011520', '> ✔ 2018-10-07 11:54:28:327&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>aa </a>仓库');
INSERT INTO `project_job_logs` VALUES ('60331', '1109538641260011520', '> ✔ 2018-10-07 11:54:30:303&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/jlb_frontend.zip\' target=\'_blank\'>jlb_frontend.zip</a>');
INSERT INTO `project_job_logs` VALUES ('60332', '1109538641260011520', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('60333', '1109538641260011520', 'End');
INSERT INTO `project_job_logs` VALUES ('60334', '1111211651280896000', 'Start By AI-Code @Copyright <a href=\'http://www.rzhkj.com\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('60335', '1111211651280896000', '> ✔ 2018-10-08 14:57:12:038&nbsp;&nbsp; 已初始化项目 【 aa ( aa )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('60336', '1111211651280896000', '> ✔ 2018-10-08 14:57:33:615&nbsp;&nbsp;已获取项目 【springboot-redis-swagger-lombok-frontend】 的模板');
INSERT INTO `project_job_logs` VALUES ('60337', '1111211651280896000', '> ✔ 2018-10-08 14:57:33:762&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60338', '1111211651280896000', '> ✔ 2018-10-08 14:57:33:893&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60339', '1111211651280896000', '> ✔ 2018-10-08 14:57:33:924&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60340', '1111211651280896000', '> ✔ 2018-10-08 14:57:33:983&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60341', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:006&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/Application.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60342', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:021&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/base/Uid.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60343', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:035&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/java/${basepackage}/base/WebMvcConfigurer.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60344', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:052&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/application.yml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60345', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:065&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/bak_bootstrap.yml 的源码');
INSERT INTO `project_job_logs` VALUES ('60346', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:106&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/mapper/${model}/${className}.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60347', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:124&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/mapper/mybatis-config.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60348', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:133&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/sentry.properties.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60349', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:147&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/${module}/src/main/resources/spring-uid.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60350', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:157&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60351', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:189&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseDAO.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60352', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:202&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseEntity.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60353', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:224&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseEntityVO.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60354', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:243&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseHibernateDAO.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60355', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:258&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseHibernateSV.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60356', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:271&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/base/BaseSV.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60357', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:292&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/entity/BeanRet.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60358', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:308&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-facade/src/main/java/${basepackage}/core/entity/Page.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60359', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:321&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-sv/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60360', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:360&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-sv/src/main/java/${basepackage}/core/base/BaseSVImpl.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60361', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:377&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-sv/src/main/java/${basepackage}/core/cache/MybatisRedisCache.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60362', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:390&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-sv/src/main/java/${basepackage}/core/cache/RedisCacheTransfer.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60363', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:404&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60364', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:434&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/src/main/java/${basepackage}/core/ExceptionHandle.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60365', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:449&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/src/main/java/${basepackage}/core/interceptor/ContextConfiguration.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60366', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:464&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60367', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:482&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/src/main/java/${basepackage}/core/interceptor/LoginInterceptor.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60368', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:502&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/base-web/src/main/java/${basepackage}/core/package-info.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60369', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:518&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60370', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:526&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60371', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:547&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/config/SwaggerConfig.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60372', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:562&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/enums/SexEnum.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60373', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:573&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/enums/YNEnum.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60374', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:595&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/exceptions/${className}Exception.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60375', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:611&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/exceptions/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60376', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:632&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/ConfigUtil.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60377', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:667&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/DateTools.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60378', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:685&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/Executors.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60379', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:701&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/FileUtil.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60380', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:719&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/HandleFuncs.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60381', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:731&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/memcached/Constants.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60382', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:742&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/memcached/IScanFileToCached.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60383', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:754&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/memcached/MemcachedKey.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60384', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:768&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/memcached/MemCachedUtil.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60385', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:784&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/memcached/ScanFileToCached.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60386', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:797&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/OkhttpLogInterceptor.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60387', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:809&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/redis/RedisKey.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60388', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:836&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60389', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:854&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/BASE64Decoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60390', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:873&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/BASE64Encoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60391', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:899&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/BouncyCastleProvider.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60392', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:916&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/CharacterDecoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60393', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:933&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/coder/DESCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60394', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:948&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/coder/HmacCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60395', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:962&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/coder/MDCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60396', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:978&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/coder/RSACoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60397', '1111211651280896000', '> ✔ 2018-10-08 14:57:34:993&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/coder/SHACoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60398', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:011&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/Hex.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60399', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:030&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/http/HTTPSCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60400', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:047&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/http/HTTPSPKCSCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60401', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:063&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/Md5.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60402', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:082&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/Rzh_3DES.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60403', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:095&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/Rzh_SHA1.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60404', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:108&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/SecurityCoder.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60405', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:125&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/security/SecurityUtil.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60406', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:139&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/SortTools.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60407', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:156&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/StringTools.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60408', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:169&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/UuidTools.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60409', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:182&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/java/${basepackage}/core/tools/ZipTools.java 的源码');
INSERT INTO `project_job_logs` VALUES ('60410', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:191&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/resources/config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60411', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:211&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/core/src/main/resources/upload_config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60412', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:223&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/facade/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60413', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:253&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/facade/src/main/java/${basepackage}/${model}/entity/${className}.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60414', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:272&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/facade/src/main/java/${basepackage}/${model}/entity/${className}State.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60415', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:298&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/facade/src/main/java/${basepackage}/${model}/service/${className}SV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60416', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:450&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/libs/uid-generator-1.0.0-SNAPSHOT.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60417', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:458&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/libs/worker_node.sql 的源码');
INSERT INTO `project_job_logs` VALUES ('60418', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:468&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/springboot-redis-swagger-lombok-frontend/settings.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60419', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:657&nbsp;&nbsp; 【已经生成】 aaSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('60420', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:663&nbsp;&nbsp;获取代码仓库信息: aa');
INSERT INTO `project_job_logs` VALUES ('60421', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:670&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>aa </a>仓库');
INSERT INTO `project_job_logs` VALUES ('60422', '1111211651280896000', '> ✔ 2018-10-08 14:57:35:836&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/aa.zip\' target=\'_blank\'>aa.zip</a>');
INSERT INTO `project_job_logs` VALUES ('60423', '1111211651280896000', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('60424', '1111211651280896000', 'End');
INSERT INTO `project_job_logs` VALUES ('60425', '1116945999557206016', 'Start By AI-Code @Copyright <a href=\'http://www.rzhkj.com\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('60426', '1116945999557206016', '> ✔ 2018-10-12 11:40:37:840&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('60427', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:014&nbsp;&nbsp;已获取项目 【ssm-redis-swagger-lombok-pitop】 的模板');
INSERT INTO `project_job_logs` VALUES ('60428', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:275&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60429', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:424&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60430', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:440&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60431', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:455&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60432', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:470&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}StateEnum.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60433', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:493&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/facade/${className}SV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60434', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:513&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60435', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:536&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseCtrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60436', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:545&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseEntity.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60437', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:564&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60438', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:584&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60439', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:607&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAOImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60440', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:623&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60441', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:643&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60442', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:660&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BeanRet.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60443', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:679&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/ExceptionHandler.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60444', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:700&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/Page.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60445', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:721&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/execptions/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60446', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:740&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60447', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:763&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/MybatisRedisCache.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60448', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:775&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisCacheTransfer.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60449', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:793&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60450', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:817&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/StringTools.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60451', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:830&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/SwaggerConfig.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60452', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:851&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/disconf.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60453', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:869&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-cache-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60454', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:886&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-data-redis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60455', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:904&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-datasource.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60456', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:923&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-import.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60457', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:943&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-mybatis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60458', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:972&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60459', '1116945999557206016', '> ✔ 2018-10-12 11:41:04:989&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/jdbc.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60460', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:001&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/log4j.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60461', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:032&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/${model}/${className}.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60462', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:056&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/mybatis-config.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60463', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:067&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/redis-manager-config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60464', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:087&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/sentry.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60465', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:097&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/spring-servlet.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60466', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:112&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/upload_config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60467', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:139&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/webapp/WEB-INF/web.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60468', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:160&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60469', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:195&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/pinyin4j-2.5.0.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60470', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:207&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/uid-generator-1.0.0-SNAPSHOT.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60471', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:216&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/worker_node.sql 的源码');
INSERT INTO `project_job_logs` VALUES ('60472', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:233&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/settings.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60473', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:403&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('60474', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:413&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('60475', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:417&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('60476', '1116945999557206016', '> ✔ 2018-10-12 11:41:05:614&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('60477', '1116945999557206016', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('60478', '1116945999557206016', 'End');
INSERT INTO `project_job_logs` VALUES ('60479', '1116947339587002368', 'Start By AI-Code @Copyright <a href=\'http://www.rzhkj.com\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('60480', '1116947339587002368', '> ✔ 2018-10-12 11:41:33:760&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('60481', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:660&nbsp;&nbsp;已获取项目 【ssm-redis-swagger-lombok-pitop】 的模板');
INSERT INTO `project_job_logs` VALUES ('60482', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:686&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60483', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:699&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60484', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:716&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60485', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:734&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60486', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:752&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}StateEnum.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60487', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:771&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/facade/${className}SV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60488', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:788&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60489', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:805&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseCtrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60490', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:822&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseEntity.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60491', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:840&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60492', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:857&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60493', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:875&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAOImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60494', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:893&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60495', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:910&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60496', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:928&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BeanRet.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60497', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:946&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/ExceptionHandler.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60498', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:964&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/Page.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60499', '1116947339587002368', '> ✔ 2018-10-12 11:41:43:982&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/execptions/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60500', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:000&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60501', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:018&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/MybatisRedisCache.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60502', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:038&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisCacheTransfer.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60503', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:057&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60504', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:089&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/StringTools.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60505', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:102&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/SwaggerConfig.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60506', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:121&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/disconf.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60507', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:138&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-cache-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60508', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:157&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-data-redis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60509', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:175&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-datasource.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60510', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:192&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-import.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60511', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:210&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-mybatis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60512', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:227&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60513', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:244&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/jdbc.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60514', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:263&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/log4j.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60515', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:283&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/${model}/${className}.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60516', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:302&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/mybatis-config.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60517', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:319&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/redis-manager-config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60518', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:337&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/sentry.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60519', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:355&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/spring-servlet.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60520', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:373&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/upload_config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60521', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:391&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/webapp/WEB-INF/web.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60522', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:408&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60523', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:431&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/pinyin4j-2.5.0.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60524', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:446&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/uid-generator-1.0.0-SNAPSHOT.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60525', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:465&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/worker_node.sql 的源码');
INSERT INTO `project_job_logs` VALUES ('60526', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:470&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/settings.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60527', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:655&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('60528', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:659&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('60529', '1116947339587002368', '> ✔ 2018-10-12 11:41:44:678&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('60530', '1116947339587002368', '> ✔ 2018-10-12 11:41:45:299&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('60531', '1116947339587002368', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('60532', '1116947339587002368', 'End');
INSERT INTO `project_job_logs` VALUES ('60533', '1116970274712371200', 'Start By AI-Code @Copyright <a href=\'http://www.rzhkj.com\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('60534', '1116970274712371200', '> ✔ 2018-10-12 12:03:51:553&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('60535', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:309&nbsp;&nbsp;已获取项目 【ssm-redis-swagger-lombok-pitop】 的模板');
INSERT INTO `project_job_logs` VALUES ('60536', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:333&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60537', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:338&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60538', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:354&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60539', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:374&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60540', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:394&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}StateEnum.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60541', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:412&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/facade/${className}SV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60542', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:430&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60543', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:447&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseCtrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60544', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:464&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseEntity.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60545', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:482&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60546', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:499&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60547', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:517&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAOImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60548', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:534&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60549', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:553&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60550', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:570&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BeanRet.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60551', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:589&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/ExceptionHandler.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60552', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:606&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/Page.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60553', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:625&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/execptions/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60554', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:642&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60555', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:660&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/MybatisRedisCache.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60556', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:677&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisCacheTransfer.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60557', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:695&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60558', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:712&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/StringTools.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60559', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:730&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/SwaggerConfig.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60560', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:747&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/disconf.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60561', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:763&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-cache-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60562', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:781&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-data-redis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60563', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:798&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-datasource.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60564', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:817&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-import.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60565', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:834&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-mybatis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60566', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:851&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60567', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:869&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/jdbc.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60568', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:887&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/log4j.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60569', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:904&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/${model}/${className}.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60570', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:922&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/mybatis-config.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60571', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:940&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/redis-manager-config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60572', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:958&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/sentry.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60573', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:976&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/spring-servlet.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60574', '1116970274712371200', '> ✔ 2018-10-12 12:04:02:994&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/upload_config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60575', '1116970274712371200', '> ✔ 2018-10-12 12:04:03:013&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/webapp/WEB-INF/web.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60576', '1116970274712371200', '> ✔ 2018-10-12 12:04:03:032&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60577', '1116970274712371200', '> ✔ 2018-10-12 12:04:03:068&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/pinyin4j-2.5.0.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60578', '1116970274712371200', '> ✔ 2018-10-12 12:04:03:077&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/uid-generator-1.0.0-SNAPSHOT.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60579', '1116970274712371200', '> ✔ 2018-10-12 12:04:03:087&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/worker_node.sql 的源码');
INSERT INTO `project_job_logs` VALUES ('60580', '1116970274712371200', '> ✔ 2018-10-12 12:04:03:105&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/settings.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60581', '1116970274712371200', '> ✔ 2018-10-12 12:04:03:255&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('60582', '1116970274712371200', '> ✔ 2018-10-12 12:04:03:265&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('60583', '1116970274712371200', '> ✔ 2018-10-12 12:04:03:270&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('60584', '1116970274712371200', '> ✔ 2018-10-12 12:04:03:850&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('60585', '1116970274712371200', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('60586', '1116970274712371200', 'End');
INSERT INTO `project_job_logs` VALUES ('60587', '1116972679894056960', 'Start By AI-Code @Copyright <a href=\'http://www.rzhkj.com\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('60588', '1116972679894056960', '> ✔ 2018-10-12 12:06:20:819&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('60589', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:555&nbsp;&nbsp;已获取项目 【ssm-redis-swagger-lombok-pitop】 的模板');
INSERT INTO `project_job_logs` VALUES ('60590', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:573&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60591', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:591&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60592', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:609&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60593', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:627&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60594', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:644&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}StateEnum.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60595', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:663&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/facade/${className}SV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60596', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:680&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60597', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:698&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseCtrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60598', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:716&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseEntity.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60599', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:734&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60600', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:751&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60601', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:769&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAOImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60602', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:788&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60603', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:821&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60604', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:838&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BeanRet.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60605', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:854&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/ExceptionHandler.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60606', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:872&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/Page.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60607', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:891&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/execptions/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60608', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:909&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60609', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:927&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/MybatisRedisCache.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60610', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:945&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisCacheTransfer.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60611', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:963&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60612', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:980&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/StringTools.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60613', '1116972679894056960', '> ✔ 2018-10-12 12:06:30:998&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/SwaggerConfig.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60614', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:015&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/disconf.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60615', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:035&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-cache-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60616', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:053&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-data-redis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60617', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:071&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-datasource.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60618', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:089&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-import.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60619', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:107&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-mybatis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60620', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:125&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60621', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:143&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/jdbc.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60622', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:161&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/log4j.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60623', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:178&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/${model}/${className}.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60624', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:197&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/mybatis-config.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60625', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:213&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/redis-manager-config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60626', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:231&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/sentry.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60627', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:248&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/spring-servlet.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60628', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:267&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/upload_config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60629', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:285&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/webapp/WEB-INF/web.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60630', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:302&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60631', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:326&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/pinyin4j-2.5.0.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60632', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:342&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/uid-generator-1.0.0-SNAPSHOT.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60633', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:357&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/worker_node.sql 的源码');
INSERT INTO `project_job_logs` VALUES ('60634', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:375&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/settings.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60635', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:516&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('60636', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:521&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('60637', '1116972679894056960', '> ✔ 2018-10-12 12:06:31:539&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('60638', '1116972679894056960', '> ✔ 2018-10-12 12:06:32:069&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('60639', '1116972679894056960', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('60640', '1116972679894056960', 'End');
INSERT INTO `project_job_logs` VALUES ('60641', '1117254000251953152', 'Start By AI-Code @Copyright <a href=\'http://www.rzhkj.com\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('60642', '1117254000251953152', '> ✔ 2018-10-12 16:39:05:921&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('60643', '1117254000251953152', '> ✔ 2018-10-12 16:39:35:771&nbsp;&nbsp;已获取项目 【ssm-redis-swagger-lombok-pitop】 的模板');
INSERT INTO `project_job_logs` VALUES ('60644', '1117254000251953152', '> ✔ 2018-10-12 16:39:35:800&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60645', '1117254000251953152', '> ✔ 2018-10-12 16:39:35:808&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60646', '1117254000251953152', '> ✔ 2018-10-12 16:39:35:828&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60647', '1117254000251953152', '> ✔ 2018-10-12 16:39:35:848&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60648', '1117254000251953152', '> ✔ 2018-10-12 16:39:35:866&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}StateEnum.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60649', '1117254000251953152', '> ✔ 2018-10-12 16:39:35:886&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/facade/${className}SV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60650', '1117254000251953152', '> ✔ 2018-10-12 16:39:35:906&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60651', '1117254000251953152', '> ✔ 2018-10-12 16:39:35:928&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseCtrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60652', '1117254000251953152', '> ✔ 2018-10-12 16:39:35:946&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseEntity.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60653', '1117254000251953152', '> ✔ 2018-10-12 16:39:35:964&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60654', '1117254000251953152', '> ✔ 2018-10-12 16:39:35:982&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60655', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:001&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAOImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60656', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:020&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60657', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:057&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60658', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:073&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BeanRet.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60659', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:094&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/ExceptionHandler.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60660', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:114&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/Page.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60661', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:132&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/execptions/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60662', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:150&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60663', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:168&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/MybatisRedisCache.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60664', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:185&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisCacheTransfer.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60665', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:202&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60666', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:220&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/StringTools.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60667', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:238&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/SwaggerConfig.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60668', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:257&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/disconf.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60669', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:272&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-cache-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60670', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:290&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-data-redis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60671', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:309&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-datasource.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60672', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:327&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-import.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60673', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:344&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-mybatis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60674', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:362&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60675', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:380&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/jdbc.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60676', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:397&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/log4j.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60677', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:416&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/${model}/${className}.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60678', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:434&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/mybatis-config.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60679', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:453&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/redis-manager-config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60680', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:471&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/sentry.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60681', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:488&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/spring-servlet.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60682', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:507&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/upload_config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60683', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:525&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/webapp/WEB-INF/web.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60684', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:543&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60685', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:582&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/pinyin4j-2.5.0.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60686', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:591&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/uid-generator-1.0.0-SNAPSHOT.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60687', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:596&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/worker_node.sql 的源码');
INSERT INTO `project_job_logs` VALUES ('60688', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:611&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/settings.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60689', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:840&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('60690', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:845&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('60691', '1117254000251953152', '> ✔ 2018-10-12 16:39:36:863&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('60692', '1117254000251953152', '> ✔ 2018-10-12 16:39:37:630&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('60693', '1117254000251953152', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('60694', '1117254000251953152', 'End');
INSERT INTO `project_job_logs` VALUES ('60695', '1121725988920049664', 'Start By AI-Code @Copyright <a href=\'http://www.aicode.io\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('60696', '1121725988920049664', '> ✔ 2018-10-15 16:57:26:749&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('60697', '1121725988920049664', '> ✔ 2018-10-15 16:57:50:397&nbsp;&nbsp;已获取项目 【ssm-redis-swagger-lombok-pitop】 的模板');
INSERT INTO `project_job_logs` VALUES ('60698', '1121725988920049664', '> ✔ 2018-10-15 16:57:50:673&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60699', '1121725988920049664', '> ✔ 2018-10-15 16:57:50:848&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60700', '1121725988920049664', '> ✔ 2018-10-15 16:57:50:865&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60701', '1121725988920049664', '> ✔ 2018-10-15 16:57:50:885&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60702', '1121725988920049664', '> ✔ 2018-10-15 16:57:50:901&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}StateEnum.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60703', '1121725988920049664', '> ✔ 2018-10-15 16:57:50:928&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/facade/${className}SV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60704', '1121725988920049664', '> ✔ 2018-10-15 16:57:50:950&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60705', '1121725988920049664', '> ✔ 2018-10-15 16:57:50:975&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseCtrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60706', '1121725988920049664', '> ✔ 2018-10-15 16:57:50:986&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseEntity.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60707', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:006&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60708', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:029&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60709', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:050&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAOImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60710', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:067&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60711', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:089&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60712', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:108&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BeanRet.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60713', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:128&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/ExceptionHandler.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60714', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:178&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/Page.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60715', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:201&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/execptions/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60716', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:222&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60717', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:245&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/MybatisRedisCache.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60718', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:257&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisCacheTransfer.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60719', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:279&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60720', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:304&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/StringTools.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60721', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:319&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/SwaggerConfig.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60722', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:343&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/disconf.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60723', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:363&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-cache-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60724', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:387&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-data-redis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60725', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:409&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-datasource.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60726', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:430&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-import.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60727', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:451&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-mybatis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60728', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:471&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60729', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:489&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/jdbc.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60730', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:498&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/log4j.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60731', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:537&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/${model}/${className}.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60732', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:561&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/mybatis-config.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60733', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:574&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/redis-manager-config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60734', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:594&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/sentry.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60735', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:603&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/spring-servlet.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60736', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:619&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/upload_config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60737', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:651&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/webapp/WEB-INF/web.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60738', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:666&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60739', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:702&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/pinyin4j-2.5.0.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60740', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:711&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/uid-generator-1.0.0-SNAPSHOT.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60741', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:719&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/worker_node.sql 的源码');
INSERT INTO `project_job_logs` VALUES ('60742', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:738&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/settings.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60743', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:941&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('60744', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:953&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('60745', '1121725988920049664', '> ✔ 2018-10-15 16:57:51:956&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('60746', '1121725988920049664', '> ✔ 2018-10-15 16:57:52:179&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('60747', '1121725988920049664', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('60748', '1121725988920049664', 'End');
INSERT INTO `project_job_logs` VALUES ('60749', '1121727294590107648', 'Start By AI-Code @Copyright <a href=\'http://www.aicode.io\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('60750', '1121727294590107648', '> ✔ 2018-10-15 16:58:42:556&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('60751', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:365&nbsp;&nbsp;已获取项目 【ssm-redis-swagger-lombok-pitop】 的模板');
INSERT INTO `project_job_logs` VALUES ('60752', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:378&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60753', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:396&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60754', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:415&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60755', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:439&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60756', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:457&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}StateEnum.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60757', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:475&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/facade/${className}SV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60758', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:493&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60759', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:512&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseCtrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60760', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:530&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseEntity.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60761', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:549&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60762', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:567&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60763', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:586&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAOImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60764', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:605&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60765', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:624&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60766', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:643&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BeanRet.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60767', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:660&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/ExceptionHandler.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60768', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:679&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/Page.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60769', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:697&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/execptions/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60770', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:716&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60771', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:734&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/MybatisRedisCache.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60772', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:754&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisCacheTransfer.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60773', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:771&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60774', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:791&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/StringTools.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60775', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:809&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/SwaggerConfig.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60776', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:828&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/disconf.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60777', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:846&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-cache-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60778', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:865&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-data-redis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60779', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:883&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-datasource.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60780', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:902&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-import.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60781', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:920&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-mybatis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60782', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:939&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60783', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:958&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/jdbc.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60784', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:976&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/log4j.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60785', '1121727294590107648', '> ✔ 2018-10-15 16:58:53:994&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/${model}/${className}.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60786', '1121727294590107648', '> ✔ 2018-10-15 16:58:54:013&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/mybatis-config.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60787', '1121727294590107648', '> ✔ 2018-10-15 16:58:54:032&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/redis-manager-config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60788', '1121727294590107648', '> ✔ 2018-10-15 16:58:54:051&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/sentry.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60789', '1121727294590107648', '> ✔ 2018-10-15 16:58:54:068&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/spring-servlet.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60790', '1121727294590107648', '> ✔ 2018-10-15 16:58:54:087&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/upload_config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60791', '1121727294590107648', '> ✔ 2018-10-15 16:58:54:106&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/webapp/WEB-INF/web.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60792', '1121727294590107648', '> ✔ 2018-10-15 16:58:54:125&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60793', '1121727294590107648', '> ✔ 2018-10-15 16:58:54:150&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/pinyin4j-2.5.0.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60794', '1121727294590107648', '> ✔ 2018-10-15 16:58:54:165&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/uid-generator-1.0.0-SNAPSHOT.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60795', '1121727294590107648', '> ✔ 2018-10-15 16:58:54:180&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/worker_node.sql 的源码');
INSERT INTO `project_job_logs` VALUES ('60796', '1121727294590107648', '> ✔ 2018-10-15 16:58:54:200&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/settings.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60797', '1121727294590107648', '> ✔ 2018-10-15 16:58:54:373&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('60798', '1121727294590107648', '> ✔ 2018-10-15 16:58:54:377&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('60799', '1121727294590107648', '> ✔ 2018-10-15 16:58:54:398&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('60800', '1121727294590107648', '> ✔ 2018-10-15 16:58:55:089&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('60801', '1121727294590107648', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('60802', '1121727294590107648', 'End');
INSERT INTO `project_job_logs` VALUES ('60803', '1121728411281604608', 'Start By AI-Code @Copyright <a href=\'http://www.aicode.io\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('60804', '1121728411281604608', '> ✔ 2018-10-15 16:59:47:980&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('60805', '1121728411281604608', '> ✔ 2018-10-15 16:59:58:973&nbsp;&nbsp;已获取项目 【ssm-redis-swagger-lombok-pitop】 的模板');
INSERT INTO `project_job_logs` VALUES ('60806', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:001&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60807', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:014&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60808', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:034&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60809', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:053&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60810', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:072&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}StateEnum.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60811', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:091&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/facade/${className}SV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60812', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:108&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60813', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:127&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseCtrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60814', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:144&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseEntity.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60815', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:163&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60816', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:181&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60817', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:199&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAOImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60818', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:218&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60819', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:237&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60820', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:256&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BeanRet.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60821', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:274&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/ExceptionHandler.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60822', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:293&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/Page.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60823', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:311&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/execptions/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60824', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:330&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60825', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:348&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/MybatisRedisCache.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60826', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:367&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisCacheTransfer.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60827', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:385&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60828', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:404&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/StringTools.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60829', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:423&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/SwaggerConfig.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60830', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:441&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/disconf.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60831', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:459&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-cache-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60832', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:478&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-data-redis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60833', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:497&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-datasource.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60834', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:516&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-import.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60835', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:534&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-mybatis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60836', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:553&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60837', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:571&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/jdbc.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60838', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:589&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/log4j.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60839', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:608&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/${model}/${className}.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60840', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:632&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/mybatis-config.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60841', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:650&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/redis-manager-config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60842', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:669&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/sentry.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60843', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:688&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/spring-servlet.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60844', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:692&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/upload_config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60845', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:711&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/webapp/WEB-INF/web.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60846', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:729&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60847', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:759&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/pinyin4j-2.5.0.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60848', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:775&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/uid-generator-1.0.0-SNAPSHOT.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60849', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:792&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/worker_node.sql 的源码');
INSERT INTO `project_job_logs` VALUES ('60850', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:810&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/settings.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60851', '1121728411281604608', '> ✔ 2018-10-15 16:59:59:998&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('60852', '1121728411281604608', '> ✔ 2018-10-15 17:00:00:002&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('60853', '1121728411281604608', '> ✔ 2018-10-15 17:00:00:025&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('60854', '1121728411281604608', '> ✔ 2018-10-15 17:00:00:735&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('60855', '1121728411281604608', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('60856', '1121728411281604608', 'End');
INSERT INTO `project_job_logs` VALUES ('60857', '1121730936722374656', 'Start By AI-Code @Copyright <a href=\'http://www.aicode.io\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('60858', '1121730936722374656', '> ✔ 2018-10-15 17:02:14:242&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('60859', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:052&nbsp;&nbsp;已获取项目 【ssm-redis-swagger-lombok-pitop】 的模板');
INSERT INTO `project_job_logs` VALUES ('60860', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:073&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60861', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:090&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60862', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:110&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60863', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:129&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60864', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:148&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}StateEnum.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60865', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:167&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/facade/${className}SV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60866', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:186&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60867', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:205&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseCtrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60868', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:221&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseEntity.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60869', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:240&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60870', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:259&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60871', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:280&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAOImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60872', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:298&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60873', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:316&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60874', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:335&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BeanRet.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60875', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:353&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/ExceptionHandler.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60876', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:374&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/Page.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60877', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:402&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/execptions/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60878', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:418&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60879', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:438&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/MybatisRedisCache.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60880', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:457&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisCacheTransfer.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60881', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:476&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60882', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:494&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/StringTools.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60883', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:513&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/SwaggerConfig.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60884', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:531&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/disconf.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60885', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:547&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-cache-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60886', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:567&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-data-redis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60887', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:585&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-datasource.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60888', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:603&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-import.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60889', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:623&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-mybatis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60890', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:641&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60891', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:660&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/jdbc.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60892', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:678&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/log4j.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60893', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:698&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/${model}/${className}.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60894', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:716&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/mybatis-config.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60895', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:735&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/redis-manager-config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60896', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:753&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/sentry.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60897', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:771&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/spring-servlet.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60898', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:790&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/upload_config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60899', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:809&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/webapp/WEB-INF/web.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60900', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:827&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60901', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:852&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/pinyin4j-2.5.0.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60902', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:870&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/uid-generator-1.0.0-SNAPSHOT.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60903', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:886&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/worker_node.sql 的源码');
INSERT INTO `project_job_logs` VALUES ('60904', '1121730936722374656', '> ✔ 2018-10-15 17:02:24:905&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/settings.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60905', '1121730936722374656', '> ✔ 2018-10-15 17:02:25:081&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('60906', '1121730936722374656', '> ✔ 2018-10-15 17:02:25:094&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('60907', '1121730936722374656', '> ✔ 2018-10-15 17:02:25:097&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('60908', '1121730936722374656', '> ✔ 2018-10-15 17:02:25:741&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('60909', '1121730936722374656', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('60910', '1121730936722374656', 'End');
INSERT INTO `project_job_logs` VALUES ('60911', '1121732740608638976', 'Start By AI-Code @Copyright <a href=\'http://www.aicode.io\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('60912', '1121732740608638976', '> ✔ 2018-10-15 17:03:59:912&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('60913', '1121732740608638976', '> ✔ 2018-10-15 17:04:08:889&nbsp;&nbsp;已获取项目 【ssm-redis-swagger-lombok-pitop】 的模板');
INSERT INTO `project_job_logs` VALUES ('60914', '1121732740608638976', '> ✔ 2018-10-15 17:04:08:908&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60915', '1121732740608638976', '> ✔ 2018-10-15 17:04:08:925&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60916', '1121732740608638976', '> ✔ 2018-10-15 17:04:08:944&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60917', '1121732740608638976', '> ✔ 2018-10-15 17:04:08:964&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60918', '1121732740608638976', '> ✔ 2018-10-15 17:04:08:983&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}StateEnum.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60919', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:001&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/facade/${className}SV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60920', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:020&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60921', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:040&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseCtrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60922', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:058&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseEntity.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60923', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:077&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60924', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:095&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60925', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:114&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAOImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60926', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:132&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60927', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:151&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60928', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:170&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BeanRet.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60929', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:189&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/ExceptionHandler.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60930', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:207&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/Page.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60931', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:226&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/execptions/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60932', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:244&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60933', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:263&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/MybatisRedisCache.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60934', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:283&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisCacheTransfer.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60935', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:300&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60936', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:319&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/StringTools.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60937', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:337&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/SwaggerConfig.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60938', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:356&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/disconf.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60939', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:372&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-cache-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60940', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:392&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-data-redis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60941', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:410&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-datasource.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60942', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:429&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-import.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60943', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:448&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-mybatis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60944', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:466&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60945', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:485&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/jdbc.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60946', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:503&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/log4j.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60947', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:522&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/${model}/${className}.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60948', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:541&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/mybatis-config.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60949', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:560&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/redis-manager-config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60950', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:578&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/sentry.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60951', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:596&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/spring-servlet.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60952', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:615&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/upload_config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60953', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:634&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/webapp/WEB-INF/web.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60954', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:652&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60955', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:675&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/pinyin4j-2.5.0.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60956', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:693&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/uid-generator-1.0.0-SNAPSHOT.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('60957', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:709&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/worker_node.sql 的源码');
INSERT INTO `project_job_logs` VALUES ('60958', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:728&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/settings.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60959', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:879&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('60960', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:883&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('60961', '1121732740608638976', '> ✔ 2018-10-15 17:04:09:903&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('60962', '1121732740608638976', '> ✔ 2018-10-15 17:04:10:463&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('60963', '1121732740608638976', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('60964', '1121732740608638976', 'End');
INSERT INTO `project_job_logs` VALUES ('60965', '1121733685501444096', 'Start By AI-Code @Copyright <a href=\'http://www.aicode.io\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('60966', '1121733685501444096', '> ✔ 2018-10-15 17:04:54:151&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('60967', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:595&nbsp;&nbsp;已获取项目 【ssm-redis-swagger-lombok-pitop】 的模板');
INSERT INTO `project_job_logs` VALUES ('60968', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:623&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('60969', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:637&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60970', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:656&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60971', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:675&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60972', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:693&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}StateEnum.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60973', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:712&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/facade/${className}SV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60974', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:730&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60975', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:748&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseCtrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60976', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:766&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseEntity.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60977', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:785&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60978', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:804&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60979', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:822&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAOImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60980', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:841&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60981', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:859&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60982', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:878&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BeanRet.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60983', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:895&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/ExceptionHandler.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60984', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:914&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/Page.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60985', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:932&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/execptions/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60986', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:951&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60987', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:970&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/MybatisRedisCache.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60988', '1121733685501444096', '> ✔ 2018-10-15 17:05:03:988&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisCacheTransfer.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60989', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:007&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60990', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:026&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/StringTools.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60991', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:045&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/SwaggerConfig.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('60992', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:063&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/disconf.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('60993', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:081&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-cache-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60994', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:100&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-data-redis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60995', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:119&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-datasource.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60996', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:138&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-import.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60997', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:156&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-mybatis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60998', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:175&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('60999', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:193&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/jdbc.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('61000', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:212&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/log4j.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('61001', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:230&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/${model}/${className}.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61002', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:253&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/mybatis-config.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61003', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:271&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/redis-manager-config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('61004', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:289&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/sentry.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('61005', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:308&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/spring-servlet.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('61006', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:312&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/upload_config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('61007', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:331&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/webapp/WEB-INF/web.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('61008', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:349&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('61009', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:377&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/pinyin4j-2.5.0.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('61010', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:394&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/uid-generator-1.0.0-SNAPSHOT.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('61011', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:410&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/worker_node.sql 的源码');
INSERT INTO `project_job_logs` VALUES ('61012', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:429&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/settings.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('61013', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:595&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61014', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:599&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61015', '1121733685501444096', '> ✔ 2018-10-15 17:05:04:618&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61016', '1121733685501444096', '> ✔ 2018-10-15 17:05:05:172&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61017', '1121733685501444096', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61018', '1121733685501444096', 'End');
INSERT INTO `project_job_logs` VALUES ('61019', '1121736606079205376', 'Start By AI-Code @Copyright <a href=\'http://www.aicode.io\' target=\'_blank\'>仁中和</a>');
INSERT INTO `project_job_logs` VALUES ('61020', '1121736606079205376', '> ✔ 2018-10-15 17:07:44:565&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61021', '1121736606079205376', '> ✔ 2018-10-15 17:07:54:728&nbsp;&nbsp;已获取项目 【ssm-redis-swagger-lombok-pitop】 的模板');
INSERT INTO `project_job_logs` VALUES ('61022', '1121736606079205376', '> ✔ 2018-10-15 17:07:54:756&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('61023', '1121736606079205376', '> ✔ 2018-10-15 17:07:54:769&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/ctrl/${className}Ctrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61024', '1121736606079205376', '> ✔ 2018-10-15 17:07:54:788&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/dao/${className}DAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61025', '1121736606079205376', '> ✔ 2018-10-15 17:07:54:806&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61026', '1121736606079205376', '> ✔ 2018-10-15 17:07:54:826&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/entity/${className}StateEnum.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61027', '1121736606079205376', '> ✔ 2018-10-15 17:07:54:845&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/facade/${className}SV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61028', '1121736606079205376', '> ✔ 2018-10-15 17:07:54:864&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/${model}/service/${className}SVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61029', '1121736606079205376', '> ✔ 2018-10-15 17:07:54:882&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseCtrl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61030', '1121736606079205376', '> ✔ 2018-10-15 17:07:54:901&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseEntity.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61031', '1121736606079205376', '> ✔ 2018-10-15 17:07:54:920&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61032', '1121736606079205376', '> ✔ 2018-10-15 17:07:54:938&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAO.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61033', '1121736606079205376', '> ✔ 2018-10-15 17:07:54:957&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisDAOImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61034', '1121736606079205376', '> ✔ 2018-10-15 17:07:54:975&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSV.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61035', '1121736606079205376', '> ✔ 2018-10-15 17:07:54:994&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BaseMybatisSVImpl.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61036', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:014&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/BeanRet.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61037', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:034&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/ExceptionHandler.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61038', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:053&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/base/Page.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61039', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:071&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/execptions/BaseException.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61040', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:090&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/interceptor/ContextInterceptor.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61041', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:108&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/MybatisRedisCache.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61042', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:126&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisCacheTransfer.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61043', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:145&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/redis/RedisUtils.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61044', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:162&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/StringTools.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61045', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:181&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/java/${basepackage}/core/tools/SwaggerConfig.java.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61046', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:199&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/disconf.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('61047', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:218&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-cache-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('61048', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:237&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-data-redis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('61049', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:256&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-datasource.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('61050', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:274&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-import.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('61051', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:292&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-mybatis.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('61052', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:311&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/frameworks/spring-uid.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('61053', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:330&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/jdbc.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('61054', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:348&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/log4j.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('61055', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:368&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/${model}/${className}.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61056', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:392&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/mybatis/mybatis-config.xml.ftl 的源码');
INSERT INTO `project_job_logs` VALUES ('61057', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:410&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/redis-manager-config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('61058', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:429&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/sentry.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('61059', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:447&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/spring-servlet.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('61060', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:451&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/resources/upload_config.properties 的源码');
INSERT INTO `project_job_logs` VALUES ('61061', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:470&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/${module}/src/main/webapp/WEB-INF/web.xml 的源码');
INSERT INTO `project_job_logs` VALUES ('61062', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:488&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/build.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('61063', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:516&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/pinyin4j-2.5.0.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('61064', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:535&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/uid-generator-1.0.0-SNAPSHOT.jar 的源码');
INSERT INTO `project_job_logs` VALUES ('61065', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:551&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/libs/worker_node.sql 的源码');
INSERT INTO `project_job_logs` VALUES ('61066', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:570&nbsp;&nbsp; 【已经生成】 模板 /aicode_template/ssm-redis-swagger-lombok-pitop/settings.gradle 的源码');
INSERT INTO `project_job_logs` VALUES ('61067', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:734&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61068', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:745&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61069', '1121736606079205376', '> ✔ 2018-10-15 17:07:55:749&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61070', '1121736606079205376', '> ✔ 2018-10-15 17:07:56:303&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61071', '1121736606079205376', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61072', '1121736606079205376', 'End');
INSERT INTO `project_job_logs` VALUES ('61073', '1121751844623179776', '> ✔ 2018-10-15 17:22:31:551&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61074', '1121751844623179776', '> ✔ 2018-10-15 17:22:58:924&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61075', '1121751844623179776', '> ✔ 2018-10-15 17:22:58:936&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61076', '1121751844623179776', '> ✔ 2018-10-15 17:22:58:939&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61077', '1121751844623179776', '> ✔ 2018-10-15 17:22:59:547&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61078', '1121751844623179776', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61079', '1121751844623179776', 'End');
INSERT INTO `project_job_logs` VALUES ('61080', '1121757462440402944', '> ✔ 2018-10-15 17:27:58:534&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61081', '1121757462440402944', '> ✔ 2018-10-15 17:28:09:099&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61082', '1121757462440402944', '> ✔ 2018-10-15 17:28:09:105&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61083', '1121757462440402944', '> ✔ 2018-10-15 17:28:09:111&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61084', '1121757462440402944', '> ✔ 2018-10-15 17:28:09:694&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61085', '1121757462440402944', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61086', '1121757462440402944', 'End');
INSERT INTO `project_job_logs` VALUES ('61087', '1121758012196216832', '> ✔ 2018-10-15 17:28:30:987&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61088', '1121758046555955200', '> ✔ 2018-10-15 17:28:32:094&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61089', '1121758046555955200', '> ✔ 2018-10-15 17:28:32:158&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61090', '1121758046555955200', '> ✔ 2018-10-15 17:28:32:172&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61091', '1121758046555955200', '> ✔ 2018-10-15 17:28:32:190&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61092', '1121758046555955201', '> ✔ 2018-10-15 17:28:32:777&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61093', '1121758046555955200', '> ✔ 2018-10-15 17:28:32:835&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61094', '1121758046555955200', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61095', '1121758046555955200', 'End');
INSERT INTO `project_job_logs` VALUES ('61096', '1121758046555955201', '> ✔ 2018-10-15 17:28:32:904&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61097', '1121758046555955201', '> ✔ 2018-10-15 17:28:32:953&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61098', '1121758046555955201', '> ✔ 2018-10-15 17:28:32:957&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61099', '1121758063735824384', '> ✔ 2018-10-15 17:28:33:156&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61100', '1121758063735824384', '> ✔ 2018-10-15 17:28:33:227&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61101', '1121758063735824384', '> ✔ 2018-10-15 17:28:33:249&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61102', '1121758063735824384', '> ✔ 2018-10-15 17:28:33:259&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61103', '1121758046555955201', '> ✔ 2018-10-15 17:28:33:354&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61104', '1121758046555955201', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61105', '1121758046555955201', 'End');
INSERT INTO `project_job_logs` VALUES ('61106', '1121758063735824384', '> ✔ 2018-10-15 17:28:33:475&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61107', '1121758063735824384', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61108', '1121758063735824384', 'End');
INSERT INTO `project_job_logs` VALUES ('61109', '1121758115275431936', '> ✔ 2018-10-15 17:28:36:110&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61110', '1121758115275431936', '> ✔ 2018-10-15 17:28:40:831&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61111', '1121758115275431936', '> ✔ 2018-10-15 17:28:40:871&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61112', '1121758115275431936', '> ✔ 2018-10-15 17:28:40:891&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61113', '1121758012196216832', '> ✔ 2018-10-15 17:28:41:758&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61114', '1121758012196216832', '> ✔ 2018-10-15 17:28:41:776&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61115', '1121758012196216832', '> ✔ 2018-10-15 17:28:41:780&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61116', '1121758012196216832', '> ✔ 2018-10-15 17:28:41:885&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61117', '1121758012196216832', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61118', '1121758012196216832', 'End');
INSERT INTO `project_job_logs` VALUES ('61119', '1121758115275431936', '> ✔ 2018-10-15 17:28:42:043&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61120', '1121758115275431936', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61121', '1121758115275431936', 'End');
INSERT INTO `project_job_logs` VALUES ('61122', '1121763286416064512', '> ✔ 2018-10-15 17:33:37:608&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61123', '1121763286416064512', '> ✔ 2018-10-15 17:33:49:313&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61124', '1121763286416064512', '> ✔ 2018-10-15 17:33:49:329&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61125', '1121763286416064512', '> ✔ 2018-10-15 17:33:49:370&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61126', '1121763286416064512', '> ✔ 2018-10-15 17:33:51:751&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61127', '1121763286416064512', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61128', '1121763286416064512', 'End');
INSERT INTO `project_job_logs` VALUES ('61129', '1121764059510177792', '> ✔ 2018-10-15 17:34:23:067&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61130', '1121764162589392896', '> ✔ 2018-10-15 17:34:28:452&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61131', '1121764248488738853', '> ✔ 2018-10-15 17:34:34:074&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61132', '1121764248488738853', '> ✔ 2018-10-15 17:34:36:976&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61133', '1121764248488738853', '> ✔ 2018-10-15 17:34:36:976&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61134', '1121764248488738853', '> ✔ 2018-10-15 17:34:36:976&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61135', '1121764763884814336', '> ✔ 2018-10-15 17:35:03:687&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61136', '1121764763884814336', '> ✔ 2018-10-15 17:35:15:098&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61137', '1121764763884814336', '> ✔ 2018-10-15 17:35:15:114&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61138', '1121764763884814336', '> ✔ 2018-10-15 17:35:15:136&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61139', '1121764763884814336', '> ✔ 2018-10-15 17:35:17:559&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61140', '1121764763884814336', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61141', '1121764763884814336', 'End');
INSERT INTO `project_job_logs` VALUES ('61142', '1121765210561413120', '> ✔ 2018-10-15 17:35:29:251&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61143', '1121765210561413120', '> ✔ 2018-10-15 17:35:40:685&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61144', '1121765210561413120', '> ✔ 2018-10-15 17:35:40:701&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61145', '1121765210561413120', '> ✔ 2018-10-15 17:35:40:717&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61146', '1121765210561413120', '> ✔ 2018-10-15 17:35:43:084&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61147', '1121765210561413120', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61148', '1121765210561413120', 'End');
INSERT INTO `project_job_logs` VALUES ('61149', '1121765588518535168', '> ✔ 2018-10-15 17:35:51:155&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61150', '1121765588518535168', '> ✔ 2018-10-15 17:36:02:718&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61151', '1121765588518535168', '> ✔ 2018-10-15 17:36:02:718&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61152', '1121765588518535168', '> ✔ 2018-10-15 17:36:02:718&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61153', '1121765588518535168', '> ✔ 2018-10-15 17:36:04:980&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61154', '1121765588518535168', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61155', '1121765588518535168', 'End');
INSERT INTO `project_job_logs` VALUES ('61156', '1121766000835395584', '> ✔ 2018-10-15 17:36:15:305&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61157', '1121766000835395584', '> ✔ 2018-10-15 17:36:26:573&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61158', '1121766000835395584', '> ✔ 2018-10-15 17:36:26:589&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61159', '1121766000835395584', '> ✔ 2018-10-15 17:36:26:605&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61160', '1121766000835395584', '> ✔ 2018-10-15 17:36:28:844&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61161', '1121766000835395584', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61162', '1121766000835395584', 'End');
INSERT INTO `project_job_logs` VALUES ('61163', '1121766327252910080', '> ✔ 2018-10-15 17:36:34:310&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61164', '1121766327252910080', '> ✔ 2018-10-15 17:36:45:559&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61165', '1121766327252910080', '> ✔ 2018-10-15 17:36:45:559&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61166', '1121766327252910080', '> ✔ 2018-10-15 17:36:45:559&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61167', '1121766327252910080', '> ✔ 2018-10-15 17:36:47:809&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61168', '1121766327252910080', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61169', '1121766327252910080', 'End');
INSERT INTO `project_job_logs` VALUES ('61170', '1121769642967662592', '> ✔ 2018-10-15 17:39:48:000&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61171', '1121769642967662592', '> ✔ 2018-10-15 17:39:59:573&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61172', '1121769642967662592', '> ✔ 2018-10-15 17:39:59:589&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61173', '1121769642967662592', '> ✔ 2018-10-15 17:39:59:610&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61174', '1121769642967662592', '> ✔ 2018-10-15 17:40:01:943&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61175', '1121769642967662592', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61176', '1121769642967662592', 'End');
INSERT INTO `project_job_logs` VALUES ('61177', '1121772632264900608', '> ✔ 2018-10-15 17:42:41:740&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61178', '1121772632264900608', '> ✔ 2018-10-15 17:42:52:512&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61179', '1121772632264900608', '> ✔ 2018-10-15 17:42:52:527&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61180', '1121772632264900608', '> ✔ 2018-10-15 17:42:52:543&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61181', '1121772632264900608', '> ✔ 2018-10-15 17:42:54:844&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61182', '1121772632264900608', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61183', '1121772632264900608', 'End');
INSERT INTO `project_job_logs` VALUES ('61184', '1121774917187502080', '> ✔ 2018-10-15 17:44:54:641&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61185', '1121774917187502080', '> ✔ 2018-10-15 17:45:06:154&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61186', '1121774917187502080', '> ✔ 2018-10-15 17:45:06:170&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61187', '1121774917187502080', '> ✔ 2018-10-15 17:45:06:192&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61188', '1121774917187502080', '> ✔ 2018-10-15 17:45:08:486&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61189', '1121774917187502080', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61190', '1121774917187502080', 'End');
INSERT INTO `project_job_logs` VALUES ('61191', '1121775415403708416', '> ✔ 2018-10-15 17:45:23:774&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61192', '1121775415403708416', '> ✔ 2018-10-15 17:45:35:208&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61193', '1121775415403708416', '> ✔ 2018-10-15 17:45:35:223&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61194', '1121775415403708416', '> ✔ 2018-10-15 17:45:35:239&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61195', '1121775415403708416', '> ✔ 2018-10-15 17:45:37:569&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61196', '1121775415403708416', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61197', '1121775415403708416', 'End');
INSERT INTO `project_job_logs` VALUES ('61198', '1121775965159522304', '> ✔ 2018-10-15 17:45:55:473&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61199', '1121775965159522304', '> ✔ 2018-10-15 17:46:09:601&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61200', '1121775965159522304', '> ✔ 2018-10-15 17:46:09:617&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61201', '1121775965159522304', '> ✔ 2018-10-15 17:46:09:635&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61202', '1121775965159522304', '> ✔ 2018-10-15 17:46:11:945&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61203', '1121775965159522304', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61204', '1121775965159522304', 'End');
INSERT INTO `project_job_logs` VALUES ('61205', '1121776841332850688', '> ✔ 2018-10-15 17:46:46:823&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61206', '1121776841332850688', '> ✔ 2018-10-15 17:46:57:918&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61207', '1121776841332850688', '> ✔ 2018-10-15 17:46:57:934&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61208', '1121776841332850688', '> ✔ 2018-10-15 17:46:57:949&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61209', '1121776841332850688', '> ✔ 2018-10-15 17:47:00:289&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61210', '1121776841332850688', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61211', '1121776841332850688', 'End');
INSERT INTO `project_job_logs` VALUES ('61212', '1121777202110103552', '> ✔ 2018-10-15 17:47:07:989&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61213', '1121777202110103552', '> ✔ 2018-10-15 17:47:19:305&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61214', '1121777202110103552', '> ✔ 2018-10-15 17:47:19:320&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61215', '1121777202110103552', '> ✔ 2018-10-15 17:47:19:336&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61216', '1121777202110103552', '> ✔ 2018-10-15 17:47:21:666&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61217', '1121777202110103552', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61218', '1121777202110103552', 'End');
INSERT INTO `project_job_logs` VALUES ('61219', '1121780878602108928', '> ✔ 2018-10-15 17:50:41:774&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61220', '1121780878602108928', '> ✔ 2018-10-15 17:50:53:398&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61221', '1121780878602108928', '> ✔ 2018-10-15 17:50:53:414&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61222', '1121780878602108928', '> ✔ 2018-10-15 17:50:53:429&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61223', '1121780878602108928', '> ✔ 2018-10-15 17:50:55:864&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61224', '1121780878602108928', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61225', '1121780878602108928', 'End');
INSERT INTO `project_job_logs` VALUES ('61226', '1121783094805233664', '> ✔ 2018-10-15 17:52:50:131&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61227', '1121783094805233664', '> ✔ 2018-10-15 17:53:01:625&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61228', '1121783094805233664', '> ✔ 2018-10-15 17:53:01:641&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61229', '1121783094805233664', '> ✔ 2018-10-15 17:53:01:657&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61230', '1121783094805233664', '> ✔ 2018-10-15 17:53:03:955&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61231', '1121783094805233664', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61232', '1121783094805233664', 'End');
INSERT INTO `project_job_logs` VALUES ('61233', '1123101821563887616', '> ✔ 2018-10-16 15:12:11:177&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61234', '1123101821563887616', '> ✔ 2018-10-16 15:13:00:654&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61235', '1123101821563887616', '> ✔ 2018-10-16 15:13:00:674&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61236', '1123101821563887616', '> ✔ 2018-10-16 15:13:00:680&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61237', '1123101821563887616', '> ✔ 2018-10-16 15:13:03:883&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61238', '1123101821563887616', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61239', '1123101821563887616', 'End');
INSERT INTO `project_job_logs` VALUES ('61240', '1123108899670007808', '> ✔ 2018-10-16 15:19:03:225&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61241', '1123109844562821120', '> ✔ 2018-10-16 15:19:57:646&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61242', '1123109844562821120', '> ✔ 2018-10-16 15:19:57:740&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61243', '1123109844562821120', '> ✔ 2018-10-16 15:19:57:748&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61244', '1123109844562821120', '> ✔ 2018-10-16 15:19:57:755&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61245', '1123109844562821120', '> ✔ 2018-10-16 15:20:02:743&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61246', '1123109844562821120', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61247', '1123109844562821120', 'End');
INSERT INTO `project_job_logs` VALUES ('61248', '1123110067901120512', '> ✔ 2018-10-16 15:20:10:183&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61249', '1123110067901120512', '> ✔ 2018-10-16 15:20:27:224&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61250', '1123110067901120512', '> ✔ 2018-10-16 15:20:27:236&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61251', '1123110067901120512', '> ✔ 2018-10-16 15:20:27:241&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61252', '1123117369345531904', '> ✔ 2018-10-16 15:27:16:049&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61253', '1123117369345531904', '> ✔ 2018-10-16 15:27:32:633&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61254', '1123117369345531904', '> ✔ 2018-10-16 15:27:32:646&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61255', '1123117369345531904', '> ✔ 2018-10-16 15:27:32:650&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61256', '1123117369345531904', '> ✔ 2018-10-16 15:27:34:023&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61257', '1123117369345531904', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61258', '1123117369345531904', 'End');
INSERT INTO `project_job_logs` VALUES ('61259', '1123118486037028864', '> ✔ 2018-10-16 15:28:20:788&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61260', '1123118486037028864', '> ✔ 2018-10-16 15:28:36:793&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61261', '1123118486037028864', '> ✔ 2018-10-16 15:28:36:805&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61262', '1123118486037028864', '> ✔ 2018-10-16 15:28:36:809&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61263', '1123118486037028864', '> ✔ 2018-10-16 15:28:41:763&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61264', '1123118486037028864', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61265', '1123118486037028864', 'End');
INSERT INTO `project_job_logs` VALUES ('61266', '1123120083764862976', '> ✔ 2018-10-16 15:29:53:644&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61267', '1123120083764862976', '> ✔ 2018-10-16 15:30:09:787&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61268', '1123120083764862976', '> ✔ 2018-10-16 15:30:09:799&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61269', '1123120083764862976', '> ✔ 2018-10-16 15:30:09:808&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61270', '1123120083764862976', '> ✔ 2018-10-16 15:30:15:651&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61271', '1123120083764862976', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61272', '1123120083764862976', 'End');
INSERT INTO `project_job_logs` VALUES ('61273', '1123121251995967488', '> ✔ 2018-10-16 15:31:01:793&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61274', '1123121251995967488', '> ✔ 2018-10-16 15:31:17:923&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61275', '1123121251995967488', '> ✔ 2018-10-16 15:31:17:935&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61276', '1123121251995967488', '> ✔ 2018-10-16 15:31:17:939&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61277', '1123121251995967488', '> ✔ 2018-10-16 15:31:23:034&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61278', '1123121251995967488', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61279', '1123121251995967488', 'End');
INSERT INTO `project_job_logs` VALUES ('61280', '1123123347940007936', '> ✔ 2018-10-16 15:33:04:061&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61281', '1123123347940007936', '> ✔ 2018-10-16 15:33:20:186&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61282', '1123123347940007936', '> ✔ 2018-10-16 15:33:20:198&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61283', '1123123347940007936', '> ✔ 2018-10-16 15:33:20:202&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61284', '1123123347940007936', '> ✔ 2018-10-16 15:33:25:283&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61285', '1123123347940007936', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61286', '1123123347940007936', 'End');
INSERT INTO `project_job_logs` VALUES ('61287', '1123123897695821824', '> ✔ 2018-10-16 15:33:36:064&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61288', '1123123897695821824', '> ✔ 2018-10-16 15:33:52:465&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61289', '1123123897695821824', '> ✔ 2018-10-16 15:33:52:471&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61290', '1123123897695821824', '> ✔ 2018-10-16 15:33:52:485&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61291', '1123123897695821824', '> ✔ 2018-10-16 15:33:57:533&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61292', '1123123897695821824', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61293', '1123123897695821824', 'End');
INSERT INTO `project_job_logs` VALUES ('61294', '1123124825408757760', '> ✔ 2018-10-16 15:34:29:571&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61295', '1123124825408757760', '> ✔ 2018-10-16 15:34:45:484&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61296', '1123124825408757760', '> ✔ 2018-10-16 15:34:45:495&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61297', '1123124825408757760', '> ✔ 2018-10-16 15:34:45:500&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61298', '1123124825408757760', '> ✔ 2018-10-16 15:34:50:507&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61299', '1123124825408757760', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61300', '1123124825408757760', 'End');
INSERT INTO `project_job_logs` VALUES ('61301', '1123126594935283712', '> ✔ 2018-10-16 15:36:12:317&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61302', '1123126594935283712', '> ✔ 2018-10-16 15:36:28:267&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61303', '1123126594935283712', '> ✔ 2018-10-16 15:36:28:273&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61304', '1123126594935283712', '> ✔ 2018-10-16 15:36:28:294&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>adf </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61305', '1123126594935283712', '> ✔ 2018-10-16 15:36:33:221&nbsp;&nbsp;代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>ceshilog.zip</a>');
INSERT INTO `project_job_logs` VALUES ('61306', '1123126594935283712', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61307', '1123126594935283712', 'End');
INSERT INTO `project_job_logs` VALUES ('61308', '1123128639339716608', '> ✔ 2018-10-16 15:38:11:768&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61309', '1123128639339716608', '> ✔ 2018-10-16 15:38:27:794&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61310', '1123128639339716608', '> ✔ 2018-10-16 15:38:27:826&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61311', '1123128639339716608', '> ✔ 2018-10-16 15:38:27:858&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>[adf] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61312', '1123128639339716608', '> ✔ 2018-10-16 15:38:33:196&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>[点击下载ceshilog.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61313', '1123128639339716608', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61314', '1123128639339716608', 'End');
INSERT INTO `project_job_logs` VALUES ('61315', '1123131096061009920', '> ✔ 2018-10-16 15:40:34:327&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61316', '1123131096061009920', '> ✔ 2018-10-16 15:40:50:105&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61317', '1123131096061009920', '> ✔ 2018-10-16 15:40:50:117&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61318', '1123131096061009920', '> ✔ 2018-10-16 15:40:50:121&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>[adf] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61319', '1123131096061009920', '> ✔ 2018-10-16 15:40:55:035&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>[点击下载ceshilog.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61320', '1123131096061009920', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61321', '1123131096061009920', 'End');
INSERT INTO `project_job_logs` VALUES ('61322', '1123134463315369984', '> ✔ 2018-10-16 15:43:50:704&nbsp;&nbsp; 已初始化项目 【 测试日志 ( ceshilog )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61323', '1123134463315369984', '> ✔ 2018-10-16 15:44:06:259&nbsp;&nbsp; 【已经生成】 ceshilogSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61324', '1123134463315369984', '> ✔ 2018-10-16 15:44:06:271&nbsp;&nbsp;获取代码仓库信息: srd');
INSERT INTO `project_job_logs` VALUES ('61325', '1123134463315369984', '> ✔ 2018-10-16 15:44:06:275&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'adf\' target=\'_blank\'>[adf] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61326', '1123134463315369984', '> ✔ 2018-10-16 15:44:11:335&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/ceshilog.zip\' target=\'_blank\'>[点击下载ceshilog.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61327', '1123134463315369984', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61328', '1123134463315369984', 'End');
INSERT INTO `project_job_logs` VALUES ('61329', '1123171692091899904', '> ✔ 2018-10-16 16:19:57:704&nbsp;&nbsp; 已初始化项目 【 berton-cloud ( berton )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61330', '1123171692091899904', '> ✔ 2018-10-16 16:20:16:491&nbsp;&nbsp; 【已经生成】 bertonSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61331', '1123171692091899904', '> ✔ 2018-10-16 16:20:16:510&nbsp;&nbsp;获取代码仓库信息: lixin');
INSERT INTO `project_job_logs` VALUES ('61332', '1123171692091899904', '> ✔ 2018-10-16 16:20:16:537&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>[aa] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61333', '1123171692091899904', '> ✔ 2018-10-16 16:20:16:708&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/berton.zip\' target=\'_blank\'>[点击下载berton.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61334', '1123171692091899904', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61335', '1123171692091899904', 'End');
INSERT INTO `project_job_logs` VALUES ('61336', '1123205673873145856', '> ✔ 2018-10-16 16:52:55:603&nbsp;&nbsp; 已初始化项目 【 pps-cloud-demo ( pps_cloud_demo )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61337', '1123205673873145856', '> ✔ 2018-10-16 16:53:13:012&nbsp;&nbsp; 【已经生成】 pps_cloud_demoSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61338', '1123205673873145856', '> ✔ 2018-10-16 16:53:13:024&nbsp;&nbsp;获取代码仓库信息: lixin');
INSERT INTO `project_job_logs` VALUES ('61339', '1123205673873145856', '> ✔ 2018-10-16 16:53:13:033&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>[aa] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61340', '1123205673873145856', '> ✔ 2018-10-16 16:53:13:208&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/pps_cloud_demo.zip\' target=\'_blank\'>[点击下载pps_cloud_demo.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61341', '1123205673873145856', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61342', '1123205673873145856', 'End');
INSERT INTO `project_job_logs` VALUES ('61343', '1123208182134046720', '> ✔ 2018-10-16 16:55:22:149&nbsp;&nbsp; 已初始化项目 【 pps-cloud-demo ( pps_cloud_demo )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61344', '1123208182134046720', '> ✔ 2018-10-16 16:55:39:736&nbsp;&nbsp; 【已经生成】 pps_cloud_demoSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61345', '1123208182134046720', '> ✔ 2018-10-16 16:55:39:745&nbsp;&nbsp;获取代码仓库信息: lixin');
INSERT INTO `project_job_logs` VALUES ('61346', '1123208182134046720', '> ✔ 2018-10-16 16:55:39:755&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>[aa] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61347', '1123208182134046720', '> ✔ 2018-10-16 16:55:39:994&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/pps_cloud_demo.zip\' target=\'_blank\'>[点击下载pps_cloud_demo.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61348', '1123208182134046720', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61349', '1123208182134046720', 'End');
INSERT INTO `project_job_logs` VALUES ('61350', '1124297007883198464', '> ✔ 2018-10-17 10:31:40:172&nbsp;&nbsp; 已初始化项目 【 pps-order ( pps_order )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61351', '1124297007883198464', '> ✔ 2018-10-17 10:32:08:437&nbsp;&nbsp; 【已经生成】 pps_orderSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61352', '1124297007883198464', '> ✔ 2018-10-17 10:32:08:443&nbsp;&nbsp;获取代码仓库信息: 11');
INSERT INTO `project_job_logs` VALUES ('61353', '1124297007883198464', '> ✔ 2018-10-17 10:32:08:449&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'11\' target=\'_blank\'>[11] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61354', '1124297007883198464', '> ✔ 2018-10-17 10:32:08:637&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/pps_order.zip\' target=\'_blank\'>[点击下载pps_order.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61355', '1124297007883198464', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61356', '1124297007883198464', 'End');
INSERT INTO `project_job_logs` VALUES ('61357', '1125755200819806208', '> ✔ 2018-10-18 10:06:17:873&nbsp;&nbsp; 已初始化项目 【 jinlanbao ( jinlanbao )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61358', '1125755200819806208', '> ✔ 2018-10-18 10:06:34:689&nbsp;&nbsp; 【已经生成】 jinlanbaoSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61359', '1125755200819806208', '> ✔ 2018-10-18 10:06:34:695&nbsp;&nbsp;获取代码仓库信息: lixin');
INSERT INTO `project_job_logs` VALUES ('61360', '1125755200819806208', '> ✔ 2018-10-18 10:06:34:702&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>[aa] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61361', '1125755200819806208', '> ✔ 2018-10-18 10:06:34:801&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/jinlanbao.zip\' target=\'_blank\'>[点击下载jinlanbao.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61362', '1125755200819806208', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61363', '1125755200819806208', 'End');
INSERT INTO `project_job_logs` VALUES ('61364', '1125782911948800000', '> ✔ 2018-10-18 10:33:10:261&nbsp;&nbsp; 已初始化项目 【 jinlanbao ( jinlanbao )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61365', '1125782911948800000', '> ✔ 2018-10-18 10:33:24:257&nbsp;&nbsp; 【已经生成】 jinlanbaoSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61366', '1125782911948800000', '> ✔ 2018-10-18 10:33:24:263&nbsp;&nbsp;获取代码仓库信息: lixin');
INSERT INTO `project_job_logs` VALUES ('61367', '1125782911948800000', '> ✔ 2018-10-18 10:33:24:270&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>[aa] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61368', '1125782911948800000', '> ✔ 2018-10-18 10:33:25:163&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/jinlanbao.zip\' target=\'_blank\'>[点击下载jinlanbao.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61369', '1125782911948800000', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61370', '1125782911948800000', 'End');
INSERT INTO `project_job_logs` VALUES ('61371', '1125800641573806080', '> ✔ 2018-10-18 10:50:23:258&nbsp;&nbsp; 已初始化项目 【 jinlanbao ( jinlanbao )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61372', '1125800641573806080', '> ✔ 2018-10-18 10:50:38:209&nbsp;&nbsp; 【已经生成】 jinlanbaoSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61373', '1125800641573806080', '> ✔ 2018-10-18 10:50:38:227&nbsp;&nbsp;获取代码仓库信息: lixin');
INSERT INTO `project_job_logs` VALUES ('61374', '1125800641573806080', '> ✔ 2018-10-18 10:50:38:237&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>[aa] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61375', '1125800641573806080', '> ✔ 2018-10-18 10:50:38:397&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/jinlanbao.zip\' target=\'_blank\'>[点击下载jinlanbao.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61376', '1125800641573806080', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61377', '1125800641573806080', 'End');
INSERT INTO `project_job_logs` VALUES ('61378', '1126296761836101632', '> ✔ 2018-10-18 18:51:40:217&nbsp;&nbsp; 已初始化项目 【 jinlanbao ( jinlanbao )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61379', '1126296761836101632', '> ✔ 2018-10-18 18:52:02:469&nbsp;&nbsp; 【已经生成】 jinlanbaoSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61380', '1126296761836101632', '> ✔ 2018-10-18 18:52:02:476&nbsp;&nbsp;获取代码仓库信息: lixin');
INSERT INTO `project_job_logs` VALUES ('61381', '1126296761836101632', '> ✔ 2018-10-18 18:52:02:483&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>[aa] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61382', '1126296761836101632', '> ✔ 2018-10-18 18:52:04:961&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/jinlanbao.zip\' target=\'_blank\'>[点击下载jinlanbao.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61383', '1126296761836101632', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61384', '1126296761836101632', 'End');
INSERT INTO `project_job_logs` VALUES ('61385', '1127247530156490752', '> ✔ 2018-10-19 10:14:02:506&nbsp;&nbsp; 已初始化项目 【 jlb ( jlbtemp )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61386', '1127247530156490752', '> ✔ 2018-10-19 10:14:25:039&nbsp;&nbsp; 【已经生成】 jlbtempSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61387', '1127247530156490752', '> ✔ 2018-10-19 10:14:25:044&nbsp;&nbsp;获取代码仓库信息: 2564054974@qq.com');
INSERT INTO `project_job_logs` VALUES ('61388', '1127247530156490752', '> ✔ 2018-10-19 10:14:25:053&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>[aa] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61389', '1127247530156490752', '> ✔ 2018-10-19 10:14:25:237&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/jlbtemp.zip\' target=\'_blank\'>[点击下载jlbtemp.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61390', '1127247530156490752', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61391', '1127247530156490752', 'End');
INSERT INTO `project_job_logs` VALUES ('61392', '1130296166662676480', '> ✔ 2018-10-21 11:31:36:587&nbsp;&nbsp; 已初始化项目 【 jinlanbao ( jinlanbao )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61393', '1130296166662676480', '> ✔ 2018-10-21 11:32:00:003&nbsp;&nbsp; 【已经生成】 jinlanbaoSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61394', '1130296166662676480', '> ✔ 2018-10-21 11:32:00:011&nbsp;&nbsp;获取代码仓库信息: lixin');
INSERT INTO `project_job_logs` VALUES ('61395', '1130296166662676480', '> ✔ 2018-10-21 11:32:00:022&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>[aa] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61396', '1130296166662676480', '> ✔ 2018-10-21 11:32:00:487&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/jinlanbao.zip\' target=\'_blank\'>[点击下载jinlanbao.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61397', '1130296166662676480', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61398', '1130296166662676480', 'End');
INSERT INTO `project_job_logs` VALUES ('61399', '1132169700116684800', '> ✔ 2018-10-22 17:49:11:424&nbsp;&nbsp; 已初始化项目 【 berton-cloud ( berton )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61400', '1132169700116684800', '> ✔ 2018-10-22 17:49:37:813&nbsp;&nbsp; 【已经生成】 bertonSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61401', '1132169700116684800', '> ✔ 2018-10-22 17:49:37:824&nbsp;&nbsp;获取代码仓库信息: lixin');
INSERT INTO `project_job_logs` VALUES ('61402', '1132169700116684800', '> ✔ 2018-10-22 17:49:37:838&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>[aa] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61403', '1132169700116684800', '> ✔ 2018-10-22 17:49:37:958&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/berton.zip\' target=\'_blank\'>[点击下载berton.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61404', '1132169700116684800', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61405', '1132169700116684800', 'End');
INSERT INTO `project_job_logs` VALUES ('61406', '1133519453719003136', '> ✔ 2018-10-23 15:38:36:885&nbsp;&nbsp; 已初始化项目 【 berton-cloud ( berton )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61407', '1133519453719003136', '> ✔ 2018-10-23 15:39:10:600&nbsp;&nbsp; 【已经生成】 bertonSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61408', '1133519453719003136', '> ✔ 2018-10-23 15:39:10:612&nbsp;&nbsp;获取代码仓库信息: lixin');
INSERT INTO `project_job_logs` VALUES ('61409', '1133519453719003136', '> ✔ 2018-10-23 15:39:10:621&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>[aa] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61410', '1133519453719003136', '> ✔ 2018-10-23 15:39:10:746&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/berton.zip\' target=\'_blank\'>[点击下载berton.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61411', '1133519453719003136', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61412', '1133519453719003136', 'End');
INSERT INTO `project_job_logs` VALUES ('61413', '1133635520915210240', '> ✔ 2018-10-23 17:31:12:296&nbsp;&nbsp; 已初始化项目 【 pps-cloud-demo ( pps_cloud_demo )】 工作空间');
INSERT INTO `project_job_logs` VALUES ('61414', '1133635520915210240', '> ✔ 2018-10-23 17:31:50:256&nbsp;&nbsp; 【已经生成】 pps_cloud_demoSql 脚本文件并追加系统配置');
INSERT INTO `project_job_logs` VALUES ('61415', '1133635520915210240', '> ✔ 2018-10-23 17:31:50:263&nbsp;&nbsp;获取代码仓库信息: lixin');
INSERT INTO `project_job_logs` VALUES ('61416', '1133635520915210240', '> ✔ 2018-10-23 17:31:50:273&nbsp;&nbsp;代码已经提交到 ⇛⇛⇛ <a style=\'text-decoration:underline;\' href=\'aa\' target=\'_blank\'>[aa] </a>仓库');
INSERT INTO `project_job_logs` VALUES ('61417', '1133635520915210240', '> ✔ 2018-10-23 17:31:50:513&nbsp;&nbsp;代码已打包ZIP, ⇛⇛⇛  <a style=\'text-decoration:underline;\' href=\'/repository/pps_cloud_demo.zip\' target=\'_blank\'>[点击下载pps_cloud_demo.zip]</a>');
INSERT INTO `project_job_logs` VALUES ('61418', '1133635520915210240', 'Finished: SUCCESS');
INSERT INTO `project_job_logs` VALUES ('61419', '1133635520915210240', 'End');

-- ----------------------------
-- Table structure for project_map
-- ----------------------------
DROP TABLE IF EXISTS `project_map`;
CREATE TABLE `project_map` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `projectCode` varchar(64) DEFAULT NULL COMMENT '项目编码',
  `mapClassTableCode` varchar(64) DEFAULT NULL COMMENT '字段属性映射编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1192 DEFAULT CHARSET=utf8mb4 COMMENT='项目数据表';

-- ----------------------------
-- Records of project_map
-- ----------------------------
INSERT INTO `project_map` VALUES ('1141', '1106948707261038592', '1109538417921712129');
INSERT INTO `project_map` VALUES ('1142', '1106948707261038592', '1109538417921712135');
INSERT INTO `project_map` VALUES ('1143', '1106948707261038592', '1109538435101581312');
INSERT INTO `project_map` VALUES ('1144', '1106948707261038592', '1109538435101581318');
INSERT INTO `project_map` VALUES ('1150', '1122747143164551168', '1122747761639841792');
INSERT INTO `project_map` VALUES ('1151', '1122747143164551168', '1122747761639841802');
INSERT INTO `project_map` VALUES ('1152', '1122747143164551168', '1122747761639841826');
INSERT INTO `project_map` VALUES ('1153', '1122747143164551168', '1122747761639841839');
INSERT INTO `project_map` VALUES ('1154', '1102343591786553344', '1122790092837519361');
INSERT INTO `project_map` VALUES ('1155', '1102343591786553344', '1122790092837519370');
INSERT INTO `project_map` VALUES ('1156', '1116944075411857408', '1123098626108203008');
INSERT INTO `project_map` VALUES ('1157', '1116944075411857408', '1123098626108203018');
INSERT INTO `project_map` VALUES ('1158', '1116944075411857408', '1123098626108203034');
INSERT INTO `project_map` VALUES ('1169', '1124294654241120256', '1124295015018373121');
INSERT INTO `project_map` VALUES ('1170', '1124294654241120256', '1124295015018373130');
INSERT INTO `project_map` VALUES ('1171', '1124294654241120256', '1124295015018373135');
INSERT INTO `project_map` VALUES ('1172', '1124294654241120256', '1124295015018373147');
INSERT INTO `project_map` VALUES ('1173', '1124294654241120256', '1124295032198242304');
INSERT INTO `project_map` VALUES ('1176', '1105399529737224192', '1127246516544208896');
INSERT INTO `project_map` VALUES ('1177', '1105399529737224192', '1127246533724078080');
INSERT INTO `project_map` VALUES ('1178', '1105399529737224192', '1127246533724078084');
INSERT INTO `project_map` VALUES ('1179', '1105399529737224192', '1127246533724078103');
INSERT INTO `project_map` VALUES ('1180', '1105399529737224192', '1127246533724078109');
INSERT INTO `project_map` VALUES ('1181', '1105399529737224192', '1127246533724078113');
INSERT INTO `project_map` VALUES ('1182', '1125753757710794752', '1130295462288039937');
INSERT INTO `project_map` VALUES ('1184', '1058961931014316032', '1133516533141241857');
INSERT INTO `project_map` VALUES ('1185', '1059236585582960640', '1133635057058742272');
INSERT INTO `project_map` VALUES ('1186', '1059236585582960640', '1133635057058742281');
INSERT INTO `project_map` VALUES ('1187', '1059236585582960640', '1133635057058742286');
INSERT INTO `project_map` VALUES ('1188', '1059236585582960640', '1133635057058742298');
INSERT INTO `project_map` VALUES ('1189', '1059236585582960640', '1133635057058742302');
INSERT INTO `project_map` VALUES ('1190', '1059236585582960640', '1133635057058742322');
INSERT INTO `project_map` VALUES ('1191', '1059236585582960640', '1133635057058742330');

-- ----------------------------
-- Table structure for project_model
-- ----------------------------
DROP TABLE IF EXISTS `project_model`;
CREATE TABLE `project_model` (
  `id` bigint(20) NOT NULL,
  `code` varchar(64) NOT NULL COMMENT '模块编码',
  `pre_code` varchar(64) DEFAULT NULL COMMENT '上级模块编码',
  `name` varchar(64) DEFAULT NULL COMMENT '模块显示名称',
  `route` varchar(64) DEFAULT NULL COMMENT '模块路由',
  `css` varchar(32) DEFAULT NULL COMMENT '模块css样式',
  `is_menu` varchar(2) DEFAULT NULL COMMENT '是否是菜单 Y,N',
  `ico` varchar(64) DEFAULT NULL COMMENT '模块图标',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模块';

-- ----------------------------
-- Records of project_model
-- ----------------------------

-- ----------------------------
-- Table structure for project_model_class
-- ----------------------------
DROP TABLE IF EXISTS `project_model_class`;
CREATE TABLE `project_model_class` (
  `id` bigint(20) NOT NULL,
  `mapClassTableCode` varchar(64) NOT NULL COMMENT '类编码',
  `projectModelCode` varchar(64) NOT NULL COMMENT '模块编码',
  PRIMARY KEY (`id`,`mapClassTableCode`,`projectModelCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模块下的类';

-- ----------------------------
-- Records of project_model_class
-- ----------------------------

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
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COMMENT='版本控制管理';

-- ----------------------------
-- Records of project_repository_account
-- ----------------------------
INSERT INTO `project_repository_account` VALUES ('104', '1105400302831337472', '1105399529737224192', '2564054974@qq.com', 'abc123_', 'aa', 'jlb', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('105', '1106948999318814720', '1106948707261038592', 'aa', 'aa', 'aa', 'aa', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('106', '1106956249223610368', '1102343591786553344', 'aa', 'aa', 'aa', 'aa', 'Enable', 'GIT');
INSERT INTO `project_repository_account` VALUES ('107', '1116945089024139264', '1116944075411857408', 'srd', 'fg', 'adf', 'adf', 'Enable', 'GIT');
INSERT INTO `project_repository_account` VALUES ('108', '1123110514577686528', '1058961931014316032', 'lixin', '888888', 'aa', 'aa', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('109', '1123205570793930752', '1059236585582960640', 'lixin', '888888', 'aa', 'aa', 'Enable', 'Git');
INSERT INTO `project_repository_account` VALUES ('110', '1124296595566338048', '1124294654241120256', '11', '11', '11', '11', 'Enable', 'GIT');
INSERT INTO `project_repository_account` VALUES ('111', '1125754874402291712', '1125753757710794752', 'lixin', '888888', 'aa', 'aa', 'Enable', 'Git');

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
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8mb4 COMMENT='项目sql脚本';

-- ----------------------------
-- Records of project_sql
-- ----------------------------
INSERT INTO `project_sql` VALUES ('136', '1105399529737224192', '1127246499364339712', 'DROP DATABASE if EXISTS jlbtemp;\nCREATE DATABASE jlbtemp;\nUSE jlbtemp;\ndrop table if exists user_address;\r\n\r\n/*==============================================================*/\r\n/* Table: user_address                                          */\r\n/*==============================================================*/\r\ncreate table user_address\r\n(\r\n   id                   bigint not null auto_increment,\r\n   user_code            varchar(64) comment \'用户编码\',\r\n   address              varchar(128) comment \'地址\',\r\n   name                 varchar(32) comment \'收货人姓名\',\r\n   phone                varchar(32) comment \'联系方式\',\r\n   primary key (id)\r\n);\r\n\r\nalter table user_address comment \'用户收货地址\';\r\n\r\n\r\ndrop table if exists item_order;\r\n\r\n/*==============================================================*/\r\n/* Table: item_order                                            */\r\n/*==============================================================*/\r\ncreate table item_order\r\n(\r\n   id                   bigint not null auto_increment,\r\n   code                 varchar(64) not null comment \'编码\',\r\n   user_code            varchar(64) not null comment \'用户编码\',\r\n   item_code            varchar(64) not null comment \'产品编码\',\r\n   item_name            varchar(64) comment \'产品名称\',\r\n   item_num             int comment \'产品数量\',\r\n   state                varchar(16) comment \'状态：\r\n            状态:待支付 Pay，待发货 Deliver，已发货 Shipped，已签收 Signed\',\r\n   user_name            varchar(32) comment \'联系人\',\r\n   address              varchar(128) comment \'地址\',\r\n   cash                 varchar(16) comment \'现金价格\',\r\n   jlb                  varchar(16) comment \'金兰宝价格\',\r\n   ore                  varchar(16) comment \'矿石价格\',\r\n   active               varchar(16) comment \'激活币价格\',\r\n   pay_method           varchar(32) comment \'支付方式\',\r\n   phone                varchar(30) comment \'电话\',\r\n   remark               varchar(128) comment \'留言\',\r\n   createTime           datetime comment \'创建时间\',\r\n   updateTime           datetime comment \'更新时间\',\r\n   primary key (id, code, user_code, item_code)\r\n);\r\n\r\nalter table item_order comment \'订单\';\r\n\r\n\r\ndrop table if exists item_category;\r\n\r\n/*==============================================================*/\r\n/* Table: item_category                                         */\r\n/*==============================================================*/\r\ncreate table item_category\r\n(\r\n   id                   bigint not null auto_increment,\r\n   code                 varchar(64) not null comment \'编码\',\r\n   name                 varchar(32) comment \'分类\',\r\n   primary key (id, code)\r\n);\r\n\r\nalter table item_category comment \'分类\';\r\n\r\n\r\ndrop table if exists item;\r\n\r\n/*==============================================================*/\r\n/* Table: item                                                  */\r\n/*==============================================================*/\r\ncreate table item\r\n(\r\n   id                   bigint not null auto_increment,\r\n   code                 varchar(64) not null comment \'编码\',\r\n   category_code        varchar(64) not null comment \'分类编码\',\r\n   name                 varchar(64) comment \'产品名\',\r\n   image_url            varchar(64) comment \'图片地址\',\r\n   summary              varchar(128) comment \'摘要描述\',\r\n   jlb                  varchar(16) comment \'金兰宝价格\',\r\n   cost                 varchar(16) comment \'成本价\',\r\n   member_price         varchar(16) comment \'会员价\',\r\n   discount             varchar(16) comment \'折扣\',\r\n   store_num            int comment \'库存量\',\r\n   is_discount          varchar(2) comment \'是否促销 Y,N\',\r\n   limit_num            int comment \'限购数量\',\r\n   state                varchar(16) comment \'状态:待审核 audited，上架 up_shelves，下架down_shelves，售罄 sold_out\',\r\n   createTime           datetime comment \'创建时间\',\r\n   updateTime           datetime comment \'更新时间\',\r\n   primary key (id, code, category_code)\r\n);\r\n\r\nalter table item comment \'产品\';\r\n\r\n\r\ndrop table if exists pay_mothed;\r\n\r\n/*==============================================================*/\r\n/* Table: pay_mothed                                            */\r\n/*==============================================================*/\r\ncreate table pay_mothed\r\n(\r\n   id                   bigint not null auto_increment,\r\n   code                 varchar(64) not null comment \'编码\',\r\n   name                 varchar(64) comment \'支付方式\',\r\n   primary key (id, code)\r\n);\r\n\r\nalter table pay_mothed comment \'支付方式分类\';\r\n\r\n\r\ndrop table if exists item_pay_method_price;\r\n\r\n/*==============================================================*/\r\n/* Table: item_pay_method_price                                 */\r\n/*==============================================================*/\r\ncreate table item_pay_method_price\r\n(\r\n   id                   bigint not null auto_increment,\r\n   item_code            varchar(64) not null comment \'产品编码\',\r\n   pay_method_code      varchar(64) comment \'支付方式编码\',\r\n   pay_method_name      varchar(64) comment \'支付方式\',\r\n   price                varchar(16) comment \'价格\',\r\n   primary key (id, item_code)\r\n);\r\n\r\nalter table item_pay_method_price comment \'支付方式价格\';\r\n\r\n', 'Enable');
INSERT INTO `project_sql` VALUES ('137', '1106948707261038592', '1109538417921712128', 'DROP DATABASE if EXISTS jlb_frontend;\nCREATE DATABASE jlb_frontend;\nUSE jlb_frontend;\n\r\nCREATE TABLE `count` (\r\n  `id` bigint(20) NOT NULL AUTO_INCREMENT,\r\n  `totalJlb` varchar(32) NOT NULL COMMENT \'金兰宝产生总数量\',\r\n  `costJlb` varchar(32) DEFAULT NULL COMMENT \'金兰宝消耗的总数量\',\r\n  `totalOre` varchar(32) DEFAULT NULL COMMENT \'矿石产生的总数量\',\r\n  `costOre` varchar(32) DEFAULT NULL COMMENT \'矿石消耗的总数量\',\r\n  PRIMARY KEY (`id`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT=\'统计数据\';\r\n\r\n-- ----------------------------\r\n-- Table structure for jlb_ore_log\r\n-- ----------------------------\r\nCREATE TABLE `jlb_ore_log` (\r\n  `id` bigint(20) NOT NULL AUTO_INCREMENT,\r\n  `outUserCode` varchar(64) NOT NULL COMMENT \'转出账户编码\',\r\n  `inUserCode` varchar(64) DEFAULT NULL COMMENT \'转入账户编码\',\r\n  `num` int(11) DEFAULT NULL COMMENT \'数量\',\r\n  `type` varchar(16) DEFAULT NULL COMMENT \'类型 金兰宝  JLB ,矿石 ORE\',\r\n  `event` char(10) DEFAULT NULL COMMENT \'事件：转账 Transfer，复投 Re_Cast，押金 Deposit，押金返还 Deposit_Return\',\r\n  `beforeOutNum` int(11) DEFAULT NULL COMMENT \'转出前转出用户数量\',\r\n  `afterOutNum` int(11) DEFAULT NULL COMMENT \'转出后转出用户数量\',\r\n  `beforeInNum` int(11) DEFAULT NULL COMMENT \'转入前转入用户数量\',\r\n  `afterInNum` int(11) DEFAULT NULL COMMENT \'转入后转入用户数量\',\r\n  `createTime` datetime DEFAULT NULL COMMENT \'转账时间\',\r\n  PRIMARY KEY (`id`,`outUserCode`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT=\'金兰宝矿石转账记录\';\r\n\r\n-- ----------------------------\r\n-- Table structure for user_deposit\r\n-- ----------------------------\r\nCREATE TABLE `user_deposit` (\r\n  `id` bigint(20) NOT NULL,\r\n  `user_code` varchar(64) NOT NULL COMMENT \'用户编码\',\r\n  `deposit` int(11) DEFAULT NULL COMMENT \'用户押金\',\r\n  `star_level` int(11) DEFAULT NULL COMMENT \'星级\',\r\n  `state` varchar(16) DEFAULT NULL COMMENT \'状态：可返还 Returnable，冻结 Frozen，已返还 Returned\',\r\n  `returnTime` datetime DEFAULT NULL COMMENT \'返还时间 一年以后的时间\',\r\n  PRIMARY KEY (`id`,`user_code`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT=\'用户的押金\';\r\n\r\nCREATE TABLE `setting` (\r\n  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT \'id\',\r\n  `k` varchar(64) NOT NULL COMMENT \'键\',\r\n  `v` varchar(128) DEFAULT NULL COMMENT \'值\',\r\n  `summary` varchar(256) DEFAULT NULL COMMENT \'备注 128个汉字\',\r\n  `ver` int(11) DEFAULT 1 COMMENT \'版本\',\r\n  PRIMARY KEY (`id`,`k`)\r\n) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COMMENT=\'设置\';\r\n', 'Enable');
INSERT INTO `project_sql` VALUES ('138', '1102343591786553344', '1122790092837519360', 'DROP DATABASE if EXISTS aa;\nCREATE DATABASE aa;\nUSE aa;\n\r\n\r\nCREATE TABLE `basic_area` (\r\n  `area_code` char(12) NOT NULL COMMENT \'行政区划编码\',\r\n  `area_name` varchar(32) NOT NULL COMMENT \'名称\',\r\n  `full_name` varchar(128) DEFAULT NULL COMMENT \'行政区划全称\',\r\n  `province` char(12) DEFAULT NULL COMMENT \'省\',\r\n  `city` char(12) DEFAULT NULL COMMENT \'市\',\r\n  `county` char(12) DEFAULT NULL COMMENT \'县区\',\r\n  `town` char(12) DEFAULT NULL COMMENT \'镇办事处\',\r\n  `level` tinyint(4) DEFAULT NULL COMMENT \'等级 省1 市2 县3 乡4 村5\',\r\n  PRIMARY KEY (`area_code`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=\'行政区域\';\r\n\r\n-- ----------------------------\r\n-- Table structure for basic_area_idcard\r\n-- ----------------------------\r\nCREATE TABLE `basic_area_idcard` (\r\n  `area_code` char(6) NOT NULL COMMENT \'行政区划编码\',\r\n  `area_name` varchar(32) NOT NULL COMMENT \'行政区域名称\',\r\n  `full_name` varchar(128) DEFAULT NULL COMMENT \'区域全名称\',\r\n  `province` char(2) DEFAULT NULL COMMENT \'省级代码\',\r\n  `city` char(4) DEFAULT NULL COMMENT \'市级代码\',\r\n  `level` tinyint(4) DEFAULT NULL COMMENT \'地行政区域级别 (0 中国, 1 省、2 市、3 县区、4镇/办事处 5村/居委会 )\',\r\n  `is_new` char(1) DEFAULT \'N\' COMMENT \'是否是新规划的行政区域代码 (N否， Y是)\',\r\n  `new_area_code` char(6) DEFAULT NULL COMMENT \'新的行政区域代码，如果is_new=0时，对应area_code\',\r\n  PRIMARY KEY (`area_code`),\r\n  UNIQUE KEY `area_code_index` (`area_code`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=\'基础区域管理-行政区域代码\';\r\n', 'Enable');
INSERT INTO `project_sql` VALUES ('139', '1116944075411857408', '1123098608928333824', 'DROP DATABASE if EXISTS ceshilog;\nCREATE DATABASE ceshilog;\nUSE ceshilog;\ndrop table if exists `order`;\r\n\r\n/*==============================================================*/\r\n/* Table: \"order\"                                               */\r\n/*==============================================================*/\r\ncreate table `order`\r\n(\r\n   id                   bigint not null auto_increment,\r\n   code                 varchar(64) not null comment \'编码\',\r\n   factoryCode          varchar(64) comment \'工厂编码\',\r\n   order_no             varchar(64) comment \'订单编号\',\r\n   customerCode         varchar(64) comment \'客户编码\',\r\n   item_sku             varchar(64) comment \'产品sku\',\r\n   item_summary         varchar(128) comment \'产品说明\',\r\n   item_total_num       int comment \'产品总量\',\r\n   item_box_num         int comment \'产品箱数\',\r\n   expect_ship_date     datetime comment \'期望出单日期\',\r\n   order_date           varchar(16) comment \'下单日期\',\r\n   ship_time            varchar(16),\r\n   state                varchar(20) comment \'状态：已下单 Order，待发货 Pending_Delivery，已发货Shipped,取消 Cancel,延期 Delay\',\r\n   createTime           datetime comment \'创建时间\',\r\n   updateTime           datetime comment \'更新时间\',\r\n   primary key (id, code)\r\n);\r\n\r\nalter table `order` comment \'订单\';\r\n\r\n\r\ndrop table if exists box;\r\n\r\n/*==============================================================*/\r\n/* Table: box                                                   */\r\n/*==============================================================*/\r\ncreate table box\r\n(\r\n   id                   bigint not null auto_increment,\r\n   code                 varchar(64) not null comment \'编码\',\r\n   factoryCode          varchar(64) comment \'工厂编码\',\r\n   sn                   varchar(64) not null comment \'箱SN\',\r\n   jobNumber            varchar(32) comment \'员工号\',\r\n   syncState            varchar(16) comment \'同步状态： \r\n            准备同步 Ready,同步中 Sync,同步错误 Error,已经同步 Synced\',\r\n   state                varchar(16) comment \'状态：入库 Storage，已出库 Issue\',\r\n   createTime           datetime comment \'创建时间\',\r\n   updateTime           datetime comment \'更新时间\',\r\n   primary key (id, code, sn)\r\n);\r\n\r\nalter table box comment \'箱库存\';\r\n\r\n\r\ndrop table if exists repair_staff;\r\n\r\n/*==============================================================*/\r\n/* Table: repair_staff                                          */\r\n/*==============================================================*/\r\ncreate table repair_staff\r\n(\r\n   id                   bigint not null auto_increment,\r\n   code                 varchar(64) not null comment \'编码\',\r\n   email                varchar(64) comment \'邮箱\',\r\n   password             varchar(32) comment \'密码 md5 加密\',\r\n   jobnumber            varchar(16) comment \'工号 0000四位格式\',\r\n   last_name            varchar(32) comment \'姓\',\r\n   first_name           varchar(32) comment \'名\',\r\n   gender               varchar(10) comment \'性别：男 Male，女 Female，其他 Other\',\r\n   avatar               varchar(64) comment \'头像\',\r\n   country_code         varchar(10) comment \'国家代码\',\r\n   language             varchar(16) comment \'语言\',\r\n   timezone             varchar(16) comment \'所在时区\',\r\n   state                varchar(16) comment \'状态：激活[Activate]，冻结[Frozen]，删除[Delete]\',\r\n   createTime           datetime comment \'创建时间\',\r\n   updateTime           datetime comment \'更新时间\',\r\n   primary key (id, code)\r\n);\r\n\r\nalter table repair_staff comment \'维修账户\';\r\n', 'Enable');
INSERT INTO `project_sql` VALUES ('145', '1122747143164551168', '1122747744459972608', 'DROP DATABASE if EXISTS test2;\nCREATE DATABASE test2;\nUSE test2;\ndrop table if exists supplier;\r\n\r\n/*==============================================================*/\r\n/* Table: supplier                                              */\r\n/*==============================================================*/\r\ncreate table supplier\r\n(\r\n   id                   bigint not null auto_increment,\r\n   code                 varchar(64) not null comment \'编码\',\r\n   name                 varchar(64) comment \'供应商名称 最长32个汉字 64个英文\',\r\n   englishName          varchar(64) comment \'英文名\',\r\n   contacts             varchar(16) comment \'联系人\',\r\n   bank                 varchar(64) comment \'最长32个汉字 64个英文\',\r\n   bank_account         varchar(32) comment \'开户行账户\',\r\n   payment_term         varchar(16) comment \'支付方式 \',\r\n   credit_rating        int comment \'信用等级\',\r\n   supplierCategoryCode varchar(64) comment \'供应商类型编码\',\r\n   website              varchar(128) comment \'公司官网\',\r\n   address              varchar(128) comment \'地址 64个汉字，128个英文字符\',\r\n   english_address      varchar(256) comment \'英文地址\',\r\n   email                varchar(32) comment \'电子邮箱\',\r\n   fax                  varchar(16) comment \'传真号\',\r\n   mobile               varchar(16) comment \'移动电话\',\r\n   telephone            varchar(16) comment \'固定电话\',\r\n   zip_code             varchar(10) comment \'邮编\',\r\n   company_nature       varchar(32) comment \'公司性质 国营 state_run ,民营privately operated，合资joint venture，外资 foreign capital\',\r\n   state                varchar(16) comment \'状态：启用 enable,禁用 disable \r\n            \',\r\n   summary              varchar(256) comment \'备注 128 个汉字说明\',\r\n   createTime           datetime comment \'创建时间\',\r\n   updateTime           datetime comment \'更新时间\',\r\n   primary key (id, code)\r\n);\r\n\r\nalter table supplier comment \'供应商\';\r\n\r\n\r\ndrop table if exists test_unit;\r\n\r\n/*==============================================================*/\r\n/* Table: test_unit                                             */\r\n/*==============================================================*/\r\ncreate table test_unit\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   factoryCode          varchar(64) not null comment \'工厂编码\',\r\n   sampleCode           varchar(64) not null comment \'样品编码\',\r\n   factorySN            varchar(64) not null comment \'工厂编码\',\r\n   sn                   varchar(64) not null comment \'产品sn\',\r\n   code                 varchar(64) not null comment \'编码\',\r\n   state                varchar(16) comment \'状态 创建 Create，测试通过TestSuccess，测试失败TestFailed，检修 Repair\',\r\n   syncState            varchar(16) comment \'同步状态：禁止同步 Ban, 准备同步 Ready,同步中 Sync,同步错误 Error,已经同步 Synced \',\r\n   jobnumber            varchar(32),\r\n   createTime           datetime comment \'创建日期\',\r\n   staffCode            varchar(64),\r\n   updateTime           datetime comment \'更新时间\',\r\n   primary key (id, factoryCode, sampleCode, factorySN, code)\r\n);\r\n\r\nalter table test_unit comment \'测试件，测试硬件单元\';\r\n\r\n\r\ndrop table if exists repair_feedback;\r\n\r\n/*==============================================================*/\r\n/* Table: repair_feedback                                       */\r\n/*==============================================================*/\r\ncreate table repair_feedback\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   factoryCode          varchar(64) not null comment \'工厂编码\',\r\n   code                 varchar(64) not null comment \'维修反馈编码\',\r\n   testUnitCode         varchar(64),\r\n   staffCode            varchar(64) comment \'员工编码\',\r\n   errorCode            varchar(64) comment \'错误类型编码\',\r\n   state                varchar(16) comment \'状态：修复repaired，损坏broken\',\r\n   feedback             varchar(256) comment \'反馈信息\',\r\n   createTime           datetime comment \'创建时间\',\r\n   primary key (id, code)\r\n);\r\n\r\nalter table repair_feedback comment \'维修反馈\';\r\n\r\ndrop table if exists test_record;\r\n\r\n/*==============================================================*/\r\n/* Table: test_record                                           */\r\n/*==============================================================*/\r\ncreate table test_record\r\n(\r\n   id                   bigint not null auto_increment comment \'id\',\r\n   factoryCode          varchar(64) not null comment \'工厂编码\',\r\n   code                 varchar(64) not null comment \'记录编码\',\r\n   staffCode            varchar(64) not null comment \'员工编码\',\r\n   jobNumber            varchar(32) comment \'工号\',\r\n   testUnitCode         varchar(64) comment \'测试件编码\',\r\n   testOptionCode       varchar(64) comment \'测试项编码\',\r\n   staffName            varchar(64) comment \'员工姓名\',\r\n   factorySN            varchar(64) comment \'工厂sn码\',\r\n   testOption           varchar(64) comment \'测试项\',\r\n   state                varchar(64) comment \'状态 成功 TestSuccess，失败 TestFailed\',\r\n   testTime             datetime comment \'测试时间\',\r\n   primary key (id, code)\r\n);\r\n\r\nalter table test_record comment \'检测记录\';\r\n\r\n', 'Enable');
INSERT INTO `project_sql` VALUES ('146', '1058961931014316032', '1133516533141241856', 'DROP DATABASE if EXISTS berton;\nCREATE DATABASE berton;\nUSE berton;\nCREATE TABLE `order_trace` (\r\n  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT \'id\',\r\n  `item_order_code` varchar(64) DEFAULT NULL COMMENT \'订单编码\',\r\n  `order_no` varchar(64) DEFAULT NULL COMMENT \'订单号\',\r\n  `state` varchar(16) DEFAULT NULL COMMENT \'状态\',\r\n  `createTime` datetime DEFAULT NULL COMMENT \'时间\',\r\n  `customerCode` varchar(64) DEFAULT NULL COMMENT \'客户编码\',\r\n  `customerName` varchar(64) DEFAULT NULL COMMENT \'客户姓名\',\r\n  PRIMARY KEY (`id`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=\'订单追踪\';\r\n\r\n', 'Enable');
INSERT INTO `project_sql` VALUES ('147', '1059236585582960640', '1133635039878873088', 'DROP DATABASE if EXISTS pps_cloud_demo;\nCREATE DATABASE pps_cloud_demo;\nUSE pps_cloud_demo;\n/*\r\nNavicat MySQL Data Transfer\r\n\r\nSource Server         : 220\r\nSource Server Version : 50505\r\nSource Host           : 192.168.1.220:3306\r\nSource Database       : pps-factory\r\n\r\nTarget Server Type    : MYSQL\r\nTarget Server Version : 50505\r\nFile Encoding         : 65001\r\n\r\nDate: 2018-10-23 17:26:15\r\n*/\r\n\r\nSET FOREIGN_KEY_CHECKS=0;\r\n\r\n-- ----------------------------\r\n-- Table structure for box\r\n-- ----------------------------\r\nDROP TABLE IF EXISTS `box`;\r\nCREATE TABLE `box` (\r\n  `id` bigint(20) NOT NULL AUTO_INCREMENT,\r\n  `code` varchar(64) NOT NULL COMMENT \'编码\',\r\n  `sn` varchar(64) NOT NULL COMMENT \'箱SN\',\r\n  `jobNumber` varchar(32) DEFAULT NULL COMMENT \'员工号\',\r\n  `syncState` varchar(16) DEFAULT NULL COMMENT \'同步状态：准备同步 Ready,同步中 Sync,同步错误 Error,已经同步 Synced\',\r\n  `state` varchar(16) DEFAULT NULL COMMENT \'状态：入库 Storage，已出库 Issue\',\r\n  `createTime` datetime DEFAULT NULL COMMENT \'创建时间\',\r\n  `updateTime` datetime DEFAULT NULL COMMENT \'更新时间\',\r\n  PRIMARY KEY (`id`,`code`,`sn`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=\'箱库存\';\r\n\r\n-- ----------------------------\r\n-- Table structure for box_item\r\n-- ----------------------------\r\nDROP TABLE IF EXISTS `box_item`;\r\nCREATE TABLE `box_item` (\r\n  `id` bigint(20) NOT NULL AUTO_INCREMENT,\r\n  `testUnitCode` varchar(64) DEFAULT NULL COMMENT \'测试件编码\',\r\n  `box_sn` varchar(64) DEFAULT NULL COMMENT \'箱体SN码\',\r\n  `item_sn` varchar(64) DEFAULT NULL COMMENT \'产品SN码\',\r\n  PRIMARY KEY (`id`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=\'装箱产品\';\r\n\r\n-- ----------------------------\r\n-- Table structure for customer\r\n-- ----------------------------\r\nDROP TABLE IF EXISTS `customer`;\r\nCREATE TABLE `customer` (\r\n  `id` bigint(20) NOT NULL AUTO_INCREMENT,\r\n  `code` varchar(64) NOT NULL COMMENT \'编码\',\r\n  `customer_abbreviation` varchar(64) DEFAULT NULL COMMENT \'客户简称\',\r\n  `name` varchar(32) DEFAULT NULL COMMENT \'客户名称\',\r\n  `linkman` varchar(64) DEFAULT NULL COMMENT \'联系人\',\r\n  `phone` varchar(32) DEFAULT NULL COMMENT \'电话\',\r\n  `email` varchar(32) DEFAULT NULL COMMENT \'邮箱\',\r\n  `language` varchar(32) DEFAULT NULL COMMENT \'语言\',\r\n  `countryCode` varchar(16) DEFAULT NULL COMMENT \'国家编码\',\r\n  `createTime` datetime DEFAULT NULL COMMENT \'创建时间\',\r\n  `updateTime` datetime DEFAULT NULL COMMENT \'更新时间\',\r\n  PRIMARY KEY (`id`)\r\n) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT=\'客户信息\';\r\n\r\n-- ----------------------------\r\n-- Table structure for customer_address\r\n-- ----------------------------\r\nDROP TABLE IF EXISTS `customer_address`;\r\nCREATE TABLE `customer_address` (\r\n  `id` bigint(20) NOT NULL,\r\n  `customer_code` varchar(64) NOT NULL COMMENT \'客户编码\',\r\n  `address` varchar(128) DEFAULT NULL COMMENT \'地址\',\r\n  PRIMARY KEY (`id`,`customer_code`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=\'客户地址\';\r\n\r\n-- ----------------------------\r\n-- Table structure for order\r\n-- ----------------------------\r\nDROP TABLE IF EXISTS `order`;\r\nCREATE TABLE `order` (\r\n  `id` bigint(20) NOT NULL AUTO_INCREMENT,\r\n  `code` varchar(64) NOT NULL COMMENT \'编码\',\r\n  `order_no` varchar(64) DEFAULT NULL COMMENT \'订单编号\',\r\n  `customerCode` varchar(64) DEFAULT NULL COMMENT \'客户编码\',\r\n  `item_sku` varchar(64) DEFAULT NULL COMMENT \'产品sku\',\r\n  `item_summary` varchar(128) DEFAULT NULL COMMENT \'产品说明\',\r\n  `item_total_num` int(11) DEFAULT NULL COMMENT \'产品总量\',\r\n  `item_box_num` int(11) DEFAULT NULL COMMENT \'产品箱数\',\r\n  `bind_item_box_num` int(11) DEFAULT 0 COMMENT \'绑定的产品箱数，默认为0\',\r\n  `expect_ship_date` datetime DEFAULT NULL COMMENT \'期望出单日期\',\r\n  `order_date` datetime DEFAULT NULL COMMENT \'下单日期\',\r\n  `ship_time` datetime DEFAULT NULL,\r\n  `state` varchar(20) DEFAULT NULL COMMENT \'状态：已下单 Order，待发货 Pending_Delivery，已发货Shipped,取消 Cancel,延期 Delay\',\r\n  `address` varchar(128) DEFAULT NULL COMMENT \'地址\',\r\n  `linkman` varchar(64) DEFAULT NULL COMMENT \'联系人\',\r\n  `phone` varchar(32) DEFAULT NULL COMMENT \'电话\',\r\n  `email` varchar(32) DEFAULT NULL COMMENT \'邮箱\',\r\n  `createTime` datetime DEFAULT NULL COMMENT \'创建时间\',\r\n  `updateTime` datetime DEFAULT NULL COMMENT \'更新时间\',\r\n  PRIMARY KEY (`id`,`code`)\r\n) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT=\'订单\';\r\n\r\n-- ----------------------------\r\n-- Table structure for order_detail\r\n-- ----------------------------\r\nDROP TABLE IF EXISTS `order_detail`;\r\nCREATE TABLE `order_detail` (\r\n  `id` bigint(20) NOT NULL AUTO_INCREMENT,\r\n  `orderCode` varchar(64) DEFAULT NULL COMMENT \'订单编码\',\r\n  `box_sn` varchar(64) DEFAULT NULL COMMENT \'箱体SN码\',\r\n  `item_sn` varchar(64) DEFAULT NULL COMMENT \'产品SN\',\r\n  `box_num` varchar(16) DEFAULT NULL COMMENT \'箱号\',\r\n  `item_num` varchar(64) DEFAULT NULL COMMENT \'装箱数量\',\r\n  `createTime` datetime DEFAULT NULL COMMENT \'创建时间\',\r\n  PRIMARY KEY (`id`)\r\n) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT=\'订单详情\';\r\n\r\n-- ----------------------------\r\n-- Table structure for order_trace\r\n-- ----------------------------\r\nDROP TABLE IF EXISTS `order_trace`;\r\nCREATE TABLE `order_trace` (\r\n  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT \'id\',\r\n  `item_order_code` varchar(64) DEFAULT NULL COMMENT \'订单编码\',\r\n  `order_no` varchar(64) DEFAULT NULL COMMENT \'订单号\',\r\n  `state` varchar(16) DEFAULT NULL COMMENT \'状态\',\r\n  `createTime` datetime DEFAULT NULL COMMENT \'时间\',\r\n  `customerCode` varchar(64) DEFAULT NULL COMMENT \'客户编码\',\r\n  `customerName` varchar(64) DEFAULT NULL COMMENT \'客户姓名\',\r\n  PRIMARY KEY (`id`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=\'订单追踪\';\r\n', 'Enable');
INSERT INTO `project_sql` VALUES ('150', '1124294654241120256', '1124295015018373120', 'DROP DATABASE if EXISTS pps_order;\nCREATE DATABASE pps_order;\nUSE pps_order;\n-- ----------------------------\r\n-- Table structure for box\r\n-- ----------------------------\r\nDROP TABLE IF EXISTS `box`;\r\nCREATE TABLE `box` (\r\n  `id` bigint(20) NOT NULL AUTO_INCREMENT,\r\n  `code` varchar(64) NOT NULL COMMENT \'编码\',\r\n  `sn` varchar(64) NOT NULL COMMENT \'箱SN\',\r\n  `jobNumber` varchar(32) DEFAULT NULL COMMENT \'员工号\',\r\n  `syncState` varchar(16) DEFAULT NULL COMMENT \'同步状态： \\r\\n            准备同步 Ready,同步中 Sync,同步错误 Error,已经同步 Synced\',\r\n  `state` varchar(16) DEFAULT NULL COMMENT \'状态：入库 Storage，已出库 Issue\',\r\n  `createTime` datetime DEFAULT NULL COMMENT \'创建时间\',\r\n  `updateTime` datetime DEFAULT NULL COMMENT \'更新时间\',\r\n  PRIMARY KEY (`id`,`code`,`sn`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=\'箱库存\';\r\n\r\n-- ----------------------------\r\n-- Table structure for box_item\r\n-- ----------------------------\r\nDROP TABLE IF EXISTS `box_item`;\r\nCREATE TABLE `box_item` (\r\n  `id` bigint(20) NOT NULL AUTO_INCREMENT,\r\n  `testUnitCode` varchar(64) DEFAULT NULL COMMENT \'测试件编码\',\r\n  `box_sn` varchar(64) DEFAULT NULL COMMENT \'箱体SN码\',\r\n  `item_sn` varchar(64) DEFAULT NULL COMMENT \'产品SN码\',\r\n  PRIMARY KEY (`id`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=\'装箱产品\';\r\n\r\n-- ----------------------------\r\n-- Table structure for customer\r\n-- ----------------------------\r\nDROP TABLE IF EXISTS `customer`;\r\nCREATE TABLE `customer` (\r\n  `id` bigint(20) NOT NULL,\r\n  `code` varchar(64) DEFAULT NULL COMMENT \'编码\',\r\n  `customer_abbreviation` varchar(64) DEFAULT NULL COMMENT \'客户简称\',\r\n  `name` varchar(32) DEFAULT NULL COMMENT \'客户名称\',\r\n  `linkman` varchar(64) DEFAULT NULL COMMENT \'联系人\',\r\n  `phone` varchar(32) DEFAULT NULL COMMENT \'电话\',\r\n  `email` varchar(32) DEFAULT NULL COMMENT \'邮箱\',\r\n  `language` varchar(32) DEFAULT NULL COMMENT \'语言\',\r\n  `countryCode` varchar(16) DEFAULT NULL COMMENT \'国家编码\',\r\n  `createTime` datetime DEFAULT NULL COMMENT \'创建时间\',\r\n  `updateTime` datetime DEFAULT NULL COMMENT \'更新时间\',\r\n  PRIMARY KEY (`id`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=\'客户信息\';\r\n\r\n-- ----------------------------\r\n-- Table structure for order\r\n-- ----------------------------\r\nDROP TABLE IF EXISTS `order`;\r\nCREATE TABLE `order` (\r\n  `id` bigint(20) NOT NULL AUTO_INCREMENT,\r\n  `code` varchar(64) NOT NULL COMMENT \'编码\',\r\n  `order_no` varchar(64) DEFAULT NULL COMMENT \'订单编号\',\r\n  `customerCode` varchar(64) DEFAULT NULL COMMENT \'客户编码\',\r\n  `item_sku` varchar(64) DEFAULT NULL COMMENT \'产品sku\',\r\n  `item_summary` varchar(128) DEFAULT NULL COMMENT \'产品说明\',\r\n  `item_total_num` int(11) DEFAULT NULL COMMENT \'产品总量\',\r\n  `item_box_num` int(11) DEFAULT NULL COMMENT \'产品箱数\',\r\n  `expect_ship_date` datetime DEFAULT NULL COMMENT \'期望出单日期\',\r\n  `order_date` varchar(16) DEFAULT NULL COMMENT \'下单日期\',\r\n  `ship_time` varchar(16) DEFAULT NULL,\r\n  `state` varchar(20) DEFAULT NULL COMMENT \'状态：已下单 Order，待发货 Pending_Delivery，已发货Shipped,取消 Cancel,延期 Delay\',\r\n  `createTime` datetime DEFAULT NULL COMMENT \'创建时间\',\r\n  `updateTime` datetime DEFAULT NULL COMMENT \'更新时间\',\r\n  PRIMARY KEY (`id`,`code`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=\'订单\';\r\n\r\n-- ----------------------------\r\n-- Table structure for order_detail\r\n-- ----------------------------\r\nDROP TABLE IF EXISTS `order_detail`;\r\nCREATE TABLE `order_detail` (\r\n  `id` bigint(20) NOT NULL AUTO_INCREMENT,\r\n  `orderCode` varchar(64) DEFAULT NULL COMMENT \'订单编码\',\r\n  `box_sn` varchar(64) DEFAULT NULL COMMENT \'箱体SN码\',\r\n  `item_sn` varchar(64) DEFAULT NULL COMMENT \'产品SN\',\r\n  `box_num` varchar(16) DEFAULT NULL COMMENT \'箱号\',\r\n  `item_num` varchar(64) DEFAULT NULL COMMENT \'装箱数量\',\r\n  `linkman` varchar(64) DEFAULT NULL COMMENT \'联系人\',\r\n  `phone` varchar(32) DEFAULT NULL COMMENT \'电话\',\r\n  `email` varchar(32) DEFAULT NULL COMMENT \'邮箱\',\r\n  `address` varchar(128) DEFAULT NULL COMMENT \'地址\',\r\n  `createTime` datetime DEFAULT NULL COMMENT \'创建时间\',\r\n  PRIMARY KEY (`id`)\r\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=\'订单详情\';\r\n', 'Enable');
INSERT INTO `project_sql` VALUES ('151', '1125753757710794752', '1130295462288039936', 'DROP DATABASE if EXISTS jinlanbao;\nCREATE DATABASE jinlanbao;\nUSE jinlanbao;\nCREATE TABLE `zx_kuang_fen` (\r\n  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,\r\n  `user_id` varchar(255) DEFAULT \'\',\r\n  `account` varchar(255) DEFAULT \'\',\r\n  `kuang_f3` varchar(255) DEFAULT NULL,\r\n  `kuang_f4` varchar(255) DEFAULT NULL,\r\n  `kuang_f5` varchar(255) DEFAULT NULL,\r\n  `kuang_f6` varchar(255) DEFAULT NULL,\r\n  `dig_out` varchar(255) DEFAULT \'\',\r\n  `kuang_f8` varchar(255) DEFAULT NULL,\r\n  `kuang_f9` varchar(255) DEFAULT NULL,\r\n  `kuang_f10` varchar(255) DEFAULT NULL,\r\n  `kuang_f11` varchar(255) DEFAULT NULL,\r\n  `kuang_f12` varchar(255) DEFAULT NULL,\r\n  `kuang_f13` varchar(255) DEFAULT NULL,\r\n  `kuang_f14` varchar(255) DEFAULT NULL,\r\n  PRIMARY KEY (`id`)\r\n) ENGINE=InnoDB AUTO_INCREMENT=3254 DEFAULT CHARSET=utf8;\r\n\r\n', 'Enable');

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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='设置';

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
INSERT INTO `setting` VALUES ('8', 'SandBox_Path', '/sandbox/', '沙箱环境目录');

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
) ENGINE=InnoDB AUTO_INCREMENT=1877 DEFAULT CHARSET=utf8 COMMENT='DB WorkerID Assigner for UID Generator';

-- ----------------------------
-- Records of worker_node
-- ----------------------------
INSERT INTO `worker_node` VALUES ('1245', '192.168.10.173', '1517290622873-94263', '2', '2018-01-30', '2018-01-30 13:37:02', '2018-01-30 13:37:02');
INSERT INTO `worker_node` VALUES ('1246', '192.168.10.173', '1517291310984-9900', '2', '2018-01-30', '2018-01-30 13:48:30', '2018-01-30 13:48:30');
INSERT INTO `worker_node` VALUES ('1247', '192.168.10.173', '1517291785523-20187', '2', '2018-01-30', '2018-01-30 13:56:25', '2018-01-30 13:56:25');
INSERT INTO `worker_node` VALUES ('1248', '192.168.10.173', '1517292084737-30916', '2', '2018-01-30', '2018-01-30 14:01:24', '2018-01-30 14:01:24');
INSERT INTO `worker_node` VALUES ('1249', '192.168.10.173', '1517305491084-31902', '2', '2018-01-30', '2018-01-30 17:44:50', '2018-01-30 17:44:50');
INSERT INTO `worker_node` VALUES ('1250', '192.168.10.173', '1517307126069-4014', '2', '2018-01-30', '2018-01-30 18:12:05', '2018-01-30 18:12:05');
INSERT INTO `worker_node` VALUES ('1251', '192.168.10.173', '1517307713010-86458', '2', '2018-01-30', '2018-01-30 18:21:52', '2018-01-30 18:21:52');
INSERT INTO `worker_node` VALUES ('1252', '192.168.10.173', '1517363477335-75727', '2', '2018-01-31', '2018-01-31 09:51:18', '2018-01-31 09:51:18');
INSERT INTO `worker_node` VALUES ('1253', '192.168.10.173', '1517364673565-12415', '2', '2018-01-31', '2018-01-31 10:11:14', '2018-01-31 10:11:14');
INSERT INTO `worker_node` VALUES ('1254', '192.168.10.173', '1517364905069-59307', '2', '2018-01-31', '2018-01-31 10:15:06', '2018-01-31 10:15:06');
INSERT INTO `worker_node` VALUES ('1255', '192.168.10.173', '1517367066629-43021', '2', '2018-01-31', '2018-01-31 10:51:07', '2018-01-31 10:51:07');
INSERT INTO `worker_node` VALUES ('1256', '192.168.10.173', '1517367483902-88603', '2', '2018-01-31', '2018-01-31 10:58:05', '2018-01-31 10:58:05');
INSERT INTO `worker_node` VALUES ('1257', '192.168.10.173', '1517367652975-63995', '2', '2018-01-31', '2018-01-31 11:00:54', '2018-01-31 11:00:54');
INSERT INTO `worker_node` VALUES ('1258', '192.168.10.173', '1517371453645-78618', '2', '2018-01-31', '2018-01-31 12:04:14', '2018-01-31 12:04:14');
INSERT INTO `worker_node` VALUES ('1259', '192.168.10.173', '1517372959539-31327', '2', '2018-01-31', '2018-01-31 12:29:20', '2018-01-31 12:29:20');
INSERT INTO `worker_node` VALUES ('1260', '192.168.10.173', '1517373222268-88699', '2', '2018-01-31', '2018-01-31 12:33:43', '2018-01-31 12:33:43');
INSERT INTO `worker_node` VALUES ('1261', '192.168.10.173', '1517373466369-48500', '2', '2018-01-31', '2018-01-31 12:37:47', '2018-01-31 12:37:47');
INSERT INTO `worker_node` VALUES ('1262', '192.168.10.173', '1517373522975-85297', '2', '2018-01-31', '2018-01-31 12:38:44', '2018-01-31 12:38:44');
INSERT INTO `worker_node` VALUES ('1263', '192.168.10.173', '1517377224912-29286', '2', '2018-01-31', '2018-01-31 13:40:25', '2018-01-31 13:40:25');
INSERT INTO `worker_node` VALUES ('1264', '192.168.10.173', '1517378329376-91702', '2', '2018-01-31', '2018-01-31 13:58:50', '2018-01-31 13:58:50');
INSERT INTO `worker_node` VALUES ('1265', '192.168.10.173', '1517378599680-79967', '2', '2018-01-31', '2018-01-31 14:03:20', '2018-01-31 14:03:20');
INSERT INTO `worker_node` VALUES ('1266', '192.168.10.173', '1517379260366-99503', '2', '2018-01-31', '2018-01-31 14:14:21', '2018-01-31 14:14:21');
INSERT INTO `worker_node` VALUES ('1267', '192.168.10.173', '1517383546717-22433', '2', '2018-01-31', '2018-01-31 15:25:47', '2018-01-31 15:25:47');
INSERT INTO `worker_node` VALUES ('1268', '192.168.10.173', '1517384278205-66746', '2', '2018-01-31', '2018-01-31 15:37:59', '2018-01-31 15:37:59');
INSERT INTO `worker_node` VALUES ('1269', '192.168.10.173', '1517384730513-48449', '2', '2018-01-31', '2018-01-31 15:45:31', '2018-01-31 15:45:31');
INSERT INTO `worker_node` VALUES ('1270', '192.168.10.173', '1517384928050-7111', '2', '2018-01-31', '2018-01-31 15:48:48', '2018-01-31 15:48:48');
INSERT INTO `worker_node` VALUES ('1271', '192.168.10.173', '1517385052879-12707', '2', '2018-01-31', '2018-01-31 15:50:53', '2018-01-31 15:50:53');
INSERT INTO `worker_node` VALUES ('1272', '192.168.10.173', '1517389511285-56175', '2', '2018-01-31', '2018-01-31 17:05:12', '2018-01-31 17:05:12');
INSERT INTO `worker_node` VALUES ('1273', '192.168.10.173', '1517389645536-54093', '2', '2018-01-31', '2018-01-31 17:07:26', '2018-01-31 17:07:26');
INSERT INTO `worker_node` VALUES ('1274', '192.168.10.173', '1517389896529-8015', '2', '2018-01-31', '2018-01-31 17:11:37', '2018-01-31 17:11:37');
INSERT INTO `worker_node` VALUES ('1275', '192.168.10.173', '1517390050750-58445', '2', '2018-01-31', '2018-01-31 17:14:11', '2018-01-31 17:14:11');
INSERT INTO `worker_node` VALUES ('1276', '192.168.10.178', '1517393532112-89152', '2', '2018-01-31', '2018-01-31 18:12:11', '2018-01-31 18:12:11');
INSERT INTO `worker_node` VALUES ('1277', '192.168.10.178', '1517393967383-59838', '2', '2018-01-31', '2018-01-31 18:19:27', '2018-01-31 18:19:27');
INSERT INTO `worker_node` VALUES ('1278', '192.168.10.178', '1517395001745-80139', '2', '2018-01-31', '2018-01-31 18:36:41', '2018-01-31 18:36:41');
INSERT INTO `worker_node` VALUES ('1279', '192.168.10.178', '1517395161889-4106', '2', '2018-01-31', '2018-01-31 18:39:21', '2018-01-31 18:39:21');
INSERT INTO `worker_node` VALUES ('1280', '192.168.10.178', '1517448854169-13174', '2', '2018-02-01', '2018-02-01 09:34:15', '2018-02-01 09:34:15');
INSERT INTO `worker_node` VALUES ('1281', '192.168.10.173', '1517449295929-68473', '2', '2018-02-01', '2018-02-01 09:41:38', '2018-02-01 09:41:38');
INSERT INTO `worker_node` VALUES ('1282', '192.168.10.178', '1517452646820-95215', '2', '2018-02-01', '2018-02-01 10:37:28', '2018-02-01 10:37:28');
INSERT INTO `worker_node` VALUES ('1283', '192.168.10.178', '1517452931145-95789', '2', '2018-02-01', '2018-02-01 10:42:12', '2018-02-01 10:42:12');
INSERT INTO `worker_node` VALUES ('1284', '192.168.10.178', '1517453073699-52315', '2', '2018-02-01', '2018-02-01 10:44:34', '2018-02-01 10:44:34');
INSERT INTO `worker_node` VALUES ('1285', '192.168.10.178', '1517453263409-19975', '2', '2018-02-01', '2018-02-01 10:47:44', '2018-02-01 10:47:44');
INSERT INTO `worker_node` VALUES ('1286', '192.168.10.178', '1517453400573-770', '2', '2018-02-01', '2018-02-01 10:50:01', '2018-02-01 10:50:01');
INSERT INTO `worker_node` VALUES ('1287', '192.168.10.178', '1517453753283-98244', '2', '2018-02-01', '2018-02-01 10:55:54', '2018-02-01 10:55:54');
INSERT INTO `worker_node` VALUES ('1288', '192.168.10.178', '1517454289038-26398', '2', '2018-02-01', '2018-02-01 11:04:50', '2018-02-01 11:04:50');
INSERT INTO `worker_node` VALUES ('1289', '192.168.10.178', '1517462860188-11344', '2', '2018-02-01', '2018-02-01 13:27:41', '2018-02-01 13:27:41');
INSERT INTO `worker_node` VALUES ('1290', '192.168.10.178', '1517463262606-25048', '2', '2018-02-01', '2018-02-01 13:34:24', '2018-02-01 13:34:24');
INSERT INTO `worker_node` VALUES ('1291', '192.168.10.178', '1517463953637-49711', '2', '2018-02-01', '2018-02-01 13:45:55', '2018-02-01 13:45:55');
INSERT INTO `worker_node` VALUES ('1292', '192.168.10.178', '1517464485937-47800', '2', '2018-02-01', '2018-02-01 13:54:47', '2018-02-01 13:54:47');
INSERT INTO `worker_node` VALUES ('1293', '192.168.10.178', '1517464713948-8232', '2', '2018-02-01', '2018-02-01 13:58:35', '2018-02-01 13:58:35');
INSERT INTO `worker_node` VALUES ('1294', '192.168.10.178', '1517464896750-87898', '2', '2018-02-01', '2018-02-01 14:01:38', '2018-02-01 14:01:38');
INSERT INTO `worker_node` VALUES ('1295', '192.168.10.178', '1517465273833-45738', '2', '2018-02-01', '2018-02-01 14:07:55', '2018-02-01 14:07:55');
INSERT INTO `worker_node` VALUES ('1296', '192.168.10.173', '1517468144644-76000', '2', '2018-02-01', '2018-02-01 14:55:46', '2018-02-01 14:55:46');
INSERT INTO `worker_node` VALUES ('1297', '192.168.10.173', '1517468948930-78468', '2', '2018-02-01', '2018-02-01 15:09:11', '2018-02-01 15:09:11');
INSERT INTO `worker_node` VALUES ('1298', '192.168.10.173', '1517469540856-2788', '2', '2018-02-01', '2018-02-01 15:19:03', '2018-02-01 15:19:03');
INSERT INTO `worker_node` VALUES ('1299', '192.168.10.173', '1517470080245-94560', '2', '2018-02-01', '2018-02-01 15:28:02', '2018-02-01 15:28:02');
INSERT INTO `worker_node` VALUES ('1300', '192.168.10.173', '1517470273275-96432', '2', '2018-02-01', '2018-02-01 15:31:15', '2018-02-01 15:31:15');
INSERT INTO `worker_node` VALUES ('1301', '192.168.10.173', '1517470542529-90545', '2', '2018-02-01', '2018-02-01 15:35:44', '2018-02-01 15:35:44');
INSERT INTO `worker_node` VALUES ('1302', '192.168.10.173', '1517470957586-12403', '2', '2018-02-01', '2018-02-01 15:42:39', '2018-02-01 15:42:39');
INSERT INTO `worker_node` VALUES ('1303', '192.168.10.173', '1517472973342-34006', '2', '2018-02-01', '2018-02-01 16:16:15', '2018-02-01 16:16:15');
INSERT INTO `worker_node` VALUES ('1304', '192.168.10.178', '1517473071722-75639', '2', '2018-02-01', '2018-02-01 16:17:52', '2018-02-01 16:17:52');
INSERT INTO `worker_node` VALUES ('1305', '192.168.10.178', '1517474121055-71145', '2', '2018-02-01', '2018-02-01 16:35:22', '2018-02-01 16:35:22');
INSERT INTO `worker_node` VALUES ('1306', '192.168.10.173', '1517474172414-93881', '2', '2018-02-01', '2018-02-01 16:36:14', '2018-02-01 16:36:14');
INSERT INTO `worker_node` VALUES ('1307', '192.168.10.178', '1517474449013-18582', '2', '2018-02-01', '2018-02-01 16:40:50', '2018-02-01 16:40:50');
INSERT INTO `worker_node` VALUES ('1308', '192.168.10.178', '1517475153974-12264', '2', '2018-02-01', '2018-02-01 16:52:35', '2018-02-01 16:52:35');
INSERT INTO `worker_node` VALUES ('1309', '192.168.10.178', '1517477321568-95088', '2', '2018-02-01', '2018-02-01 17:28:42', '2018-02-01 17:28:42');
INSERT INTO `worker_node` VALUES ('1310', '192.168.10.178', '1517477613640-17741', '2', '2018-02-01', '2018-02-01 17:33:34', '2018-02-01 17:33:34');
INSERT INTO `worker_node` VALUES ('1311', '192.168.56.1', '1517537174676-69014', '2', '2018-02-02', '2018-02-02 10:06:09', '2018-02-02 10:06:09');
INSERT INTO `worker_node` VALUES ('1312', '192.168.56.1', '1517537449866-10448', '2', '2018-02-02', '2018-02-02 10:10:45', '2018-02-02 10:10:45');
INSERT INTO `worker_node` VALUES ('1313', '192.168.10.173', '1517538439022-26908', '2', '2018-02-02', '2018-02-02 10:27:18', '2018-02-02 10:27:18');
INSERT INTO `worker_node` VALUES ('1314', '192.168.10.173', '1517542467103-82861', '2', '2018-02-02', '2018-02-02 11:34:26', '2018-02-02 11:34:26');
INSERT INTO `worker_node` VALUES ('1315', '192.168.10.173', '1517543131222-99000', '2', '2018-02-02', '2018-02-02 11:45:30', '2018-02-02 11:45:30');
INSERT INTO `worker_node` VALUES ('1316', '192.168.10.173', '1517543525894-87742', '2', '2018-02-02', '2018-02-02 11:52:05', '2018-02-02 11:52:05');
INSERT INTO `worker_node` VALUES ('1317', '192.168.10.173', '1517543775050-34079', '2', '2018-02-02', '2018-02-02 11:56:14', '2018-02-02 11:56:14');
INSERT INTO `worker_node` VALUES ('1318', '192.168.10.173', '1517544013707-40195', '2', '2018-02-02', '2018-02-02 12:00:13', '2018-02-02 12:00:13');
INSERT INTO `worker_node` VALUES ('1319', '192.168.10.173', '1517545870386-20175', '2', '2018-02-02', '2018-02-02 12:31:10', '2018-02-02 12:31:10');
INSERT INTO `worker_node` VALUES ('1320', '192.168.10.173', '1517545971943-1148', '2', '2018-02-02', '2018-02-02 12:32:51', '2018-02-02 12:32:51');
INSERT INTO `worker_node` VALUES ('1321', '192.168.10.173', '1517546235657-83077', '2', '2018-02-02', '2018-02-02 12:37:15', '2018-02-02 12:37:15');
INSERT INTO `worker_node` VALUES ('1322', '192.168.10.173', '1517546534525-90595', '2', '2018-02-02', '2018-02-02 12:42:14', '2018-02-02 12:42:14');
INSERT INTO `worker_node` VALUES ('1323', '192.168.10.173', '1517547282397-92989', '2', '2018-02-02', '2018-02-02 12:54:41', '2018-02-02 12:54:41');
INSERT INTO `worker_node` VALUES ('1324', '192.168.10.173', '1517549466086-53867', '2', '2018-02-02', '2018-02-02 13:31:05', '2018-02-02 13:31:05');
INSERT INTO `worker_node` VALUES ('1325', '192.168.10.173', '1517550681953-14759', '2', '2018-02-02', '2018-02-02 13:51:21', '2018-02-02 13:51:21');
INSERT INTO `worker_node` VALUES ('1326', '192.168.10.173', '1517552453621-84768', '2', '2018-02-02', '2018-02-02 14:20:53', '2018-02-02 14:20:53');
INSERT INTO `worker_node` VALUES ('1327', '192.168.10.173', '1517552951344-29441', '2', '2018-02-02', '2018-02-02 14:29:10', '2018-02-02 14:29:10');
INSERT INTO `worker_node` VALUES ('1328', '192.168.10.173', '1517553269783-15283', '2', '2018-02-02', '2018-02-02 14:34:29', '2018-02-02 14:34:29');
INSERT INTO `worker_node` VALUES ('1329', '192.168.10.173', '1517553488306-94456', '2', '2018-02-02', '2018-02-02 14:38:07', '2018-02-02 14:38:07');
INSERT INTO `worker_node` VALUES ('1330', '192.168.10.173', '1517553775910-83869', '2', '2018-02-02', '2018-02-02 14:42:55', '2018-02-02 14:42:55');
INSERT INTO `worker_node` VALUES ('1331', '192.168.10.173', '1517553898158-74179', '2', '2018-02-02', '2018-02-02 14:44:57', '2018-02-02 14:44:57');
INSERT INTO `worker_node` VALUES ('1332', '192.168.10.173', '1517554097384-64238', '2', '2018-02-02', '2018-02-02 14:48:16', '2018-02-02 14:48:16');
INSERT INTO `worker_node` VALUES ('1333', '192.168.10.173', '1517554395723-32288', '2', '2018-02-02', '2018-02-02 14:53:15', '2018-02-02 14:53:15');
INSERT INTO `worker_node` VALUES ('1334', '192.168.10.173', '1517554557988-95694', '2', '2018-02-02', '2018-02-02 14:55:57', '2018-02-02 14:55:57');
INSERT INTO `worker_node` VALUES ('1335', '192.168.10.173', '1517554995048-32193', '2', '2018-02-02', '2018-02-02 15:03:14', '2018-02-02 15:03:14');
INSERT INTO `worker_node` VALUES ('1336', '192.168.10.173', '1517555123795-43783', '2', '2018-02-02', '2018-02-02 15:05:23', '2018-02-02 15:05:23');
INSERT INTO `worker_node` VALUES ('1337', '192.168.10.173', '1517560964856-21055', '2', '2018-02-02', '2018-02-02 16:42:44', '2018-02-02 16:42:44');
INSERT INTO `worker_node` VALUES ('1338', '192.168.10.173', '1517561360869-19111', '2', '2018-02-02', '2018-02-02 16:49:20', '2018-02-02 16:49:20');
INSERT INTO `worker_node` VALUES ('1339', '192.168.10.173', '1517570249610-10907', '2', '2018-02-02', '2018-02-02 19:17:28', '2018-02-02 19:17:28');
INSERT INTO `worker_node` VALUES ('1340', '192.168.10.173', '1517622167028-91772', '2', '2018-02-03', '2018-02-03 09:42:47', '2018-02-03 09:42:47');
INSERT INTO `worker_node` VALUES ('1341', '192.168.10.178', '1517629607970-51531', '2', '2018-02-03', '2018-02-03 11:46:50', '2018-02-03 11:46:50');
INSERT INTO `worker_node` VALUES ('1342', '192.168.10.173', '1517630129508-67295', '2', '2018-02-03', '2018-02-03 11:55:29', '2018-02-03 11:55:29');
INSERT INTO `worker_node` VALUES ('1343', '192.168.10.173', '1517630318013-90850', '2', '2018-02-03', '2018-02-03 11:58:38', '2018-02-03 11:58:38');
INSERT INTO `worker_node` VALUES ('1344', '192.168.10.173', '1517630490770-51911', '2', '2018-02-03', '2018-02-03 12:01:31', '2018-02-03 12:01:31');
INSERT INTO `worker_node` VALUES ('1345', '192.168.10.173', '1517632703679-13436', '2', '2018-02-03', '2018-02-03 12:38:24', '2018-02-03 12:38:24');
INSERT INTO `worker_node` VALUES ('1346', '192.168.10.173', '1517632870447-54277', '2', '2018-02-03', '2018-02-03 12:41:10', '2018-02-03 12:41:10');
INSERT INTO `worker_node` VALUES ('1347', '192.168.10.173', '1517636241851-65425', '2', '2018-02-03', '2018-02-03 13:37:21', '2018-02-03 13:37:21');
INSERT INTO `worker_node` VALUES ('1348', '192.168.10.173', '1517636653791-89306', '2', '2018-02-03', '2018-02-03 13:44:13', '2018-02-03 13:44:13');
INSERT INTO `worker_node` VALUES ('1349', '192.168.10.178', '1517646249487-65970', '2', '2018-02-03', '2018-02-03 16:24:11', '2018-02-03 16:24:11');
INSERT INTO `worker_node` VALUES ('1350', '192.168.10.173', '1517660961155-90144', '2', '2018-02-03', '2018-02-04 04:29:20', '2018-02-04 04:29:20');
INSERT INTO `worker_node` VALUES ('1351', '192.168.10.173', '1517710531626-81521', '2', '2018-02-04', '2018-02-04 18:15:32', '2018-02-04 18:15:32');
INSERT INTO `worker_node` VALUES ('1352', '192.168.10.173', '1517794255595-76115', '2', '2018-02-05', '2018-02-05 17:30:54', '2018-02-05 17:30:54');
INSERT INTO `worker_node` VALUES ('1353', '192.168.10.173', '1517794811824-38538', '2', '2018-02-05', '2018-02-05 17:40:11', '2018-02-05 17:40:11');
INSERT INTO `worker_node` VALUES ('1354', '192.168.10.173', '1517795143567-82446', '2', '2018-02-05', '2018-02-05 17:45:42', '2018-02-05 17:45:42');
INSERT INTO `worker_node` VALUES ('1355', '192.168.10.173', '1517796898560-68023', '2', '2018-02-05', '2018-02-05 18:14:57', '2018-02-05 18:14:57');
INSERT INTO `worker_node` VALUES ('1356', '192.168.10.173', '1517821240971-39383', '2', '2018-02-05', '2018-02-06 01:00:39', '2018-02-06 01:00:39');
INSERT INTO `worker_node` VALUES ('1357', '192.168.10.173', '1517821974812-26429', '2', '2018-02-05', '2018-02-06 01:12:53', '2018-02-06 01:12:53');
INSERT INTO `worker_node` VALUES ('1358', '172.17.0.5', '1517823040156-18252', '2', '2018-02-05', '2018-02-06 01:30:40', '2018-02-06 01:30:40');
INSERT INTO `worker_node` VALUES ('1359', '172.17.0.13', '1517823174753-63012', '2', '2018-02-05', '2018-02-06 01:32:54', '2018-02-06 01:32:54');
INSERT INTO `worker_node` VALUES ('1360', '172.17.0.7', '1517846697696-63619', '2', '2018-02-06', '2018-02-06 08:04:57', '2018-02-06 08:04:57');
INSERT INTO `worker_node` VALUES ('1361', '172.17.0.11', '1517846698388-35706', '2', '2018-02-06', '2018-02-06 08:04:58', '2018-02-06 08:04:58');
INSERT INTO `worker_node` VALUES ('1362', '192.168.10.173', '1517880571489-47826', '2', '2018-02-06', '2018-02-06 17:29:34', '2018-02-06 17:29:34');
INSERT INTO `worker_node` VALUES ('1363', '192.168.10.173', '1517880821075-36714', '2', '2018-02-06', '2018-02-06 17:33:44', '2018-02-06 17:33:44');
INSERT INTO `worker_node` VALUES ('1364', '172.17.0.11', '1517963772325-32398', '2', '2018-02-07', '2018-02-07 16:36:12', '2018-02-07 16:36:12');
INSERT INTO `worker_node` VALUES ('1365', '172.17.0.10', '1517963773284-64534', '2', '2018-02-07', '2018-02-07 16:36:13', '2018-02-07 16:36:13');
INSERT INTO `worker_node` VALUES ('1366', '172.17.0.5', '1518071178281-51209', '2', '2018-02-08', '2018-02-08 22:26:18', '2018-02-08 22:26:18');
INSERT INTO `worker_node` VALUES ('1367', '172.17.0.3', '1518071178503-67785', '2', '2018-02-08', '2018-02-08 22:26:18', '2018-02-08 22:26:18');
INSERT INTO `worker_node` VALUES ('1368', '192.168.10.173', '1518075328032-34129', '2', '2018-02-08', '2018-02-08 23:35:30', '2018-02-08 23:35:30');
INSERT INTO `worker_node` VALUES ('1369', '192.168.10.173', '1518075492870-67305', '2', '2018-02-08', '2018-02-08 23:38:15', '2018-02-08 23:38:15');
INSERT INTO `worker_node` VALUES ('1370', '192.168.10.173', '1518075727507-83015', '2', '2018-02-08', '2018-02-08 23:42:10', '2018-02-08 23:42:10');
INSERT INTO `worker_node` VALUES ('1371', '192.168.10.173', '1518076329290-42636', '2', '2018-02-08', '2018-02-08 23:52:12', '2018-02-08 23:52:12');
INSERT INTO `worker_node` VALUES ('1372', '192.168.10.173', '1518076488767-16908', '2', '2018-02-08', '2018-02-08 23:54:51', '2018-02-08 23:54:51');
INSERT INTO `worker_node` VALUES ('1373', '192.168.10.173', '1518077098632-46159', '2', '2018-02-08', '2018-02-09 00:05:01', '2018-02-09 00:05:01');
INSERT INTO `worker_node` VALUES ('1374', '192.168.10.173', '1518077331013-22893', '2', '2018-02-08', '2018-02-09 00:08:53', '2018-02-09 00:08:53');
INSERT INTO `worker_node` VALUES ('1375', '192.168.10.173', '1518078299846-47684', '2', '2018-02-08', '2018-02-09 00:25:02', '2018-02-09 00:25:02');
INSERT INTO `worker_node` VALUES ('1376', '192.168.10.173', '1518078393666-11028', '2', '2018-02-08', '2018-02-09 00:26:36', '2018-02-09 00:26:36');
INSERT INTO `worker_node` VALUES ('1377', '192.168.10.173', '1518079375847-97727', '2', '2018-02-08', '2018-02-09 00:42:55', '2018-02-09 00:42:55');
INSERT INTO `worker_node` VALUES ('1378', '192.168.10.173', '1518079980284-99591', '2', '2018-02-08', '2018-02-09 00:53:00', '2018-02-09 00:53:00');
INSERT INTO `worker_node` VALUES ('1379', '192.168.10.173', '1518080094631-48938', '2', '2018-02-08', '2018-02-09 00:54:54', '2018-02-09 00:54:54');
INSERT INTO `worker_node` VALUES ('1380', '192.168.10.173', '1518081803741-34233', '2', '2018-02-08', '2018-02-09 01:23:23', '2018-02-09 01:23:23');
INSERT INTO `worker_node` VALUES ('1381', '192.168.10.173', '1518081914060-20143', '2', '2018-02-08', '2018-02-09 01:25:14', '2018-02-09 01:25:14');
INSERT INTO `worker_node` VALUES ('1382', '192.168.10.173', '1518082498992-90287', '2', '2018-02-08', '2018-02-09 01:34:58', '2018-02-09 01:34:58');
INSERT INTO `worker_node` VALUES ('1383', '192.168.10.173', '1518082777405-8146', '2', '2018-02-08', '2018-02-09 01:39:37', '2018-02-09 01:39:37');
INSERT INTO `worker_node` VALUES ('1384', '192.168.10.173', '1518083332461-3131', '2', '2018-02-08', '2018-02-09 01:48:52', '2018-02-09 01:48:52');
INSERT INTO `worker_node` VALUES ('1385', '192.168.10.173', '1518084034395-67247', '2', '2018-02-08', '2018-02-09 02:00:34', '2018-02-09 02:00:34');
INSERT INTO `worker_node` VALUES ('1386', '192.168.10.173', '1518087021316-85084', '2', '2018-02-08', '2018-02-09 02:50:21', '2018-02-09 02:50:21');
INSERT INTO `worker_node` VALUES ('1387', '172.17.0.9', '1519610303440-64894', '2', '2018-02-26', '2018-02-26 17:58:23', '2018-02-26 17:58:23');
INSERT INTO `worker_node` VALUES ('1388', '172.17.0.6', '1519610304289-86499', '2', '2018-02-26', '2018-02-26 17:58:24', '2018-02-26 17:58:24');
INSERT INTO `worker_node` VALUES ('1389', '172.17.0.2', '1519694444349-97753', '2', '2018-02-27', '2018-02-27 17:20:44', '2018-02-27 17:20:44');
INSERT INTO `worker_node` VALUES ('1390', '172.17.0.3', '1519694444355-78718', '2', '2018-02-27', '2018-02-27 17:20:44', '2018-02-27 17:20:44');
INSERT INTO `worker_node` VALUES ('1391', '172.17.0.6', '1519778557340-63041', '2', '2018-02-28', '2018-02-28 16:42:37', '2018-02-28 16:42:37');
INSERT INTO `worker_node` VALUES ('1392', '172.17.0.10', '1519778557614-13241', '2', '2018-02-28', '2018-02-28 16:42:37', '2018-02-28 16:42:37');
INSERT INTO `worker_node` VALUES ('1393', '172.17.0.3', '1520041167870-68591', '2', '2018-03-03', '2018-03-03 17:39:28', '2018-03-03 17:39:28');
INSERT INTO `worker_node` VALUES ('1394', '172.17.0.5', '1520041173280-19733', '2', '2018-03-03', '2018-03-03 17:39:33', '2018-03-03 17:39:33');
INSERT INTO `worker_node` VALUES ('1395', '172.17.0.11', '1520216336922-20179', '2', '2018-03-05', '2018-03-05 18:18:57', '2018-03-05 18:18:57');
INSERT INTO `worker_node` VALUES ('1396', '172.17.0.10', '1520216337394-80653', '2', '2018-03-05', '2018-03-05 18:18:57', '2018-03-05 18:18:57');
INSERT INTO `worker_node` VALUES ('1397', '172.17.0.7', '1520496831514-38829', '2', '2018-03-08', '2018-03-09 00:13:52', '2018-03-09 00:13:52');
INSERT INTO `worker_node` VALUES ('1398', '172.17.0.8', '1520496832040-83215', '2', '2018-03-08', '2018-03-09 00:13:52', '2018-03-09 00:13:52');
INSERT INTO `worker_node` VALUES ('1399', '172.17.0.8', '1520645472580-34946', '2', '2018-03-10', '2018-03-10 17:31:12', '2018-03-10 17:31:12');
INSERT INTO `worker_node` VALUES ('1400', '172.17.0.11', '1520645473183-23538', '2', '2018-03-10', '2018-03-10 17:31:13', '2018-03-10 17:31:13');
INSERT INTO `worker_node` VALUES ('1401', '172.17.0.10', '1522292638305-90155', '2', '2018-03-29', '2018-03-29 19:03:58', '2018-03-29 19:03:58');
INSERT INTO `worker_node` VALUES ('1402', '172.17.0.6', '1522292640080-2472', '2', '2018-03-29', '2018-03-29 19:04:00', '2018-03-29 19:04:00');
INSERT INTO `worker_node` VALUES ('1403', '172.17.0.10', '1522457537198-56030', '2', '2018-03-31', '2018-03-31 16:52:17', '2018-03-31 16:52:17');
INSERT INTO `worker_node` VALUES ('1404', '172.17.0.4', '1522457539206-82582', '2', '2018-03-31', '2018-03-31 16:52:19', '2018-03-31 16:52:19');
INSERT INTO `worker_node` VALUES ('1405', '172.17.0.10', '1523151744936-41563', '2', '2018-04-08', '2018-04-08 17:42:25', '2018-04-08 17:42:25');
INSERT INTO `worker_node` VALUES ('1406', '172.17.0.5', '1523151745092-8928', '2', '2018-04-08', '2018-04-08 17:42:25', '2018-04-08 17:42:25');
INSERT INTO `worker_node` VALUES ('1407', '172.17.0.6', '1523234578403-32118', '2', '2018-04-09', '2018-04-09 16:42:58', '2018-04-09 16:42:58');
INSERT INTO `worker_node` VALUES ('1408', '172.17.0.10', '1523234578538-14124', '2', '2018-04-09', '2018-04-09 16:42:58', '2018-04-09 16:42:58');
INSERT INTO `worker_node` VALUES ('1409', '172.17.0.9', '1523407569297-57191', '2', '2018-04-11', '2018-04-11 16:46:09', '2018-04-11 16:46:09');
INSERT INTO `worker_node` VALUES ('1410', '172.17.0.5', '1523407570055-87350', '2', '2018-04-11', '2018-04-11 16:46:10', '2018-04-11 16:46:10');
INSERT INTO `worker_node` VALUES ('1411', '172.17.0.11', '1523493540153-11267', '2', '2018-04-12', '2018-04-12 16:39:00', '2018-04-12 16:39:00');
INSERT INTO `worker_node` VALUES ('1412', '172.17.0.10', '1523493540881-15668', '2', '2018-04-12', '2018-04-12 16:39:01', '2018-04-12 16:39:01');
INSERT INTO `worker_node` VALUES ('1413', '172.17.0.8', '1523579827220-75474', '2', '2018-04-13', '2018-04-13 16:37:07', '2018-04-13 16:37:07');
INSERT INTO `worker_node` VALUES ('1414', '172.17.0.4', '1523579830074-58866', '2', '2018-04-13', '2018-04-13 16:37:10', '2018-04-13 16:37:10');
INSERT INTO `worker_node` VALUES ('1415', '172.17.0.10', '1523666554637-15733', '2', '2018-04-14', '2018-04-14 16:42:34', '2018-04-14 16:42:34');
INSERT INTO `worker_node` VALUES ('1416', '172.17.0.7', '1523666554883-4451', '2', '2018-04-14', '2018-04-14 16:42:35', '2018-04-14 16:42:35');
INSERT INTO `worker_node` VALUES ('1417', '172.17.0.10', '1523844465972-63676', '2', '2018-04-16', '2018-04-16 18:07:46', '2018-04-16 18:07:46');
INSERT INTO `worker_node` VALUES ('1418', '172.17.0.11', '1523844467191-21720', '2', '2018-04-16', '2018-04-16 18:07:47', '2018-04-16 18:07:47');
INSERT INTO `worker_node` VALUES ('1419', '172.17.0.10', '1524010827051-89480', '2', '2018-04-18', '2018-04-18 16:20:27', '2018-04-18 16:20:27');
INSERT INTO `worker_node` VALUES ('1420', '172.17.0.7', '1524010830766-72521', '2', '2018-04-18', '2018-04-18 16:20:31', '2018-04-18 16:20:31');
INSERT INTO `worker_node` VALUES ('1421', '2001:0:9d38:90d7:3496:305e:3f57:fe72', '1524127769916-41743', '2', '2018-04-19', '2018-04-20 00:49:29', '2018-04-20 00:49:29');
INSERT INTO `worker_node` VALUES ('1422', '2001:0:9d38:90d7:3496:305e:3f57:fe72', '1524128256131-27280', '2', '2018-04-19', '2018-04-20 00:57:36', '2018-04-20 00:57:36');
INSERT INTO `worker_node` VALUES ('1423', '2001:0:9d38:90d7:3496:305e:3f57:fe72', '1524129308783-89523', '2', '2018-04-19', '2018-04-20 01:15:08', '2018-04-20 01:15:08');
INSERT INTO `worker_node` VALUES ('1424', '2001:0:9d38:90d7:14db:123f:3f57:fea7', '1524219756185-97724', '2', '2018-04-20', '2018-04-20 18:22:35', '2018-04-20 18:22:35');
INSERT INTO `worker_node` VALUES ('1425', '2001:0:9d38:90d7:2499:20b0:3f57:fea0', '1524535628430-69085', '2', '2018-04-24', '2018-04-24 10:07:06', '2018-04-24 10:07:06');
INSERT INTO `worker_node` VALUES ('1426', '192.168.1.95', '1526272108277-33844', '2', '2018-05-14', '2018-05-14 12:28:14', '2018-05-14 12:28:14');
INSERT INTO `worker_node` VALUES ('1427', '192.168.1.95', '1526287647016-67238', '2', '2018-05-14', '2018-05-14 16:47:12', '2018-05-14 16:47:12');
INSERT INTO `worker_node` VALUES ('1428', '192.168.1.95', '1526350528918-18774', '2', '2018-05-15', '2018-05-15 10:15:20', '2018-05-15 10:15:20');
INSERT INTO `worker_node` VALUES ('1429', '192.168.1.95', '1528101083017-93042', '2', '2018-06-04', '2018-06-04 16:31:22', '2018-06-04 16:31:22');
INSERT INTO `worker_node` VALUES ('1430', '192.168.1.95', '1528102953069-85475', '2', '2018-06-04', '2018-06-04 17:02:32', '2018-06-04 17:02:32');
INSERT INTO `worker_node` VALUES ('1431', '192.168.1.95', '1529393697087-82605', '2', '2018-06-19', '2018-06-19 15:34:53', '2018-06-19 15:34:53');
INSERT INTO `worker_node` VALUES ('1432', '192.168.1.95', '1529393819673-9192', '2', '2018-06-19', '2018-06-19 15:36:55', '2018-06-19 15:36:55');
INSERT INTO `worker_node` VALUES ('1433', '172.17.0.4', '1529395599148-66457', '2', '2018-06-19', '2018-06-19 16:06:39', '2018-06-19 16:06:39');
INSERT INTO `worker_node` VALUES ('1434', '172.17.0.4', '1529399887679-13232', '2', '2018-06-19', '2018-06-19 17:18:07', '2018-06-19 17:18:07');
INSERT INTO `worker_node` VALUES ('1435', '172.17.0.4', '1529567767185-74516', '2', '2018-06-21', '2018-06-21 15:56:07', '2018-06-21 15:56:07');
INSERT INTO `worker_node` VALUES ('1436', '192.168.1.95', '1529567872530-97433', '2', '2018-06-21', '2018-06-21 15:57:51', '2018-06-21 15:57:51');
INSERT INTO `worker_node` VALUES ('1437', '192.168.1.95', '1529573267320-55073', '2', '2018-06-21', '2018-06-21 17:27:46', '2018-06-21 17:27:46');
INSERT INTO `worker_node` VALUES ('1438', '192.168.1.95', '1529573680639-23384', '2', '2018-06-21', '2018-06-21 17:34:39', '2018-06-21 17:34:39');
INSERT INTO `worker_node` VALUES ('1439', '192.168.1.95', '1529580445715-64385', '2', '2018-06-21', '2018-06-21 19:27:24', '2018-06-21 19:27:24');
INSERT INTO `worker_node` VALUES ('1440', '192.168.1.95', '1529581112511-4283', '2', '2018-06-21', '2018-06-21 19:38:31', '2018-06-21 19:38:31');
INSERT INTO `worker_node` VALUES ('1441', '172.17.0.4', '1529582712165-42322', '2', '2018-06-21', '2018-06-21 20:05:12', '2018-06-21 20:05:12');
INSERT INTO `worker_node` VALUES ('1442', '192.168.1.95', '1529907806120-27883', '2', '2018-06-25', '2018-06-25 14:23:25', '2018-06-25 14:23:25');
INSERT INTO `worker_node` VALUES ('1443', '192.168.1.95', '1529908159317-88260', '2', '2018-06-25', '2018-06-25 14:29:18', '2018-06-25 14:29:18');
INSERT INTO `worker_node` VALUES ('1444', '192.168.1.95', '1529908418611-41334', '2', '2018-06-25', '2018-06-25 14:33:37', '2018-06-25 14:33:37');
INSERT INTO `worker_node` VALUES ('1445', '192.168.1.95', '1529930385336-19437', '2', '2018-06-25', '2018-06-25 20:39:43', '2018-06-25 20:39:43');
INSERT INTO `worker_node` VALUES ('1446', '192.168.1.95', '1529931455055-20067', '2', '2018-06-25', '2018-06-25 20:57:33', '2018-06-25 20:57:33');
INSERT INTO `worker_node` VALUES ('1447', '192.168.1.95', '1529932962670-32469', '2', '2018-06-25', '2018-06-25 21:22:40', '2018-06-25 21:22:40');
INSERT INTO `worker_node` VALUES ('1448', '192.168.1.95', '1529984978117-56850', '2', '2018-06-26', '2018-06-26 11:49:37', '2018-06-26 11:49:37');
INSERT INTO `worker_node` VALUES ('1449', '192.168.1.95', '1529986974360-13162', '2', '2018-06-26', '2018-06-26 12:22:53', '2018-06-26 12:22:53');
INSERT INTO `worker_node` VALUES ('1450', '192.168.1.95', '1529993055205-3693', '2', '2018-06-26', '2018-06-26 14:04:13', '2018-06-26 14:04:13');
INSERT INTO `worker_node` VALUES ('1451', '192.168.1.95', '1529993515700-64189', '2', '2018-06-26', '2018-06-26 14:11:54', '2018-06-26 14:11:54');
INSERT INTO `worker_node` VALUES ('1452', '192.168.1.95', '1530007527422-24328', '2', '2018-06-26', '2018-06-26 18:05:25', '2018-06-26 18:05:25');
INSERT INTO `worker_node` VALUES ('1453', '192.168.1.95', '1530011021960-55872', '2', '2018-06-26', '2018-06-26 19:03:39', '2018-06-26 19:03:39');
INSERT INTO `worker_node` VALUES ('1454', '192.168.1.95', '1530011581315-26195', '2', '2018-06-26', '2018-06-26 19:12:58', '2018-06-26 19:12:58');
INSERT INTO `worker_node` VALUES ('1455', '192.168.1.95', '1530067096228-10694', '2', '2018-06-27', '2018-06-27 10:38:13', '2018-06-27 10:38:13');
INSERT INTO `worker_node` VALUES ('1456', '172.17.0.4', '1530068830121-32351', '2', '2018-06-27', '2018-06-27 11:07:10', '2018-06-27 11:07:10');
INSERT INTO `worker_node` VALUES ('1457', '192.168.1.95', '1530071227683-73325', '2', '2018-06-27', '2018-06-27 11:47:04', '2018-06-27 11:47:04');
INSERT INTO `worker_node` VALUES ('1458', '192.168.1.95', '1530071461400-1941', '2', '2018-06-27', '2018-06-27 11:50:58', '2018-06-27 11:50:58');
INSERT INTO `worker_node` VALUES ('1459', '192.168.1.95', '1530071701957-91132', '2', '2018-06-27', '2018-06-27 11:54:58', '2018-06-27 11:54:58');
INSERT INTO `worker_node` VALUES ('1460', '172.17.0.4', '1530071978920-12179', '2', '2018-06-27', '2018-06-27 11:59:38', '2018-06-27 11:59:38');
INSERT INTO `worker_node` VALUES ('1461', '172.17.0.4', '1530073545827-35443', '2', '2018-06-27', '2018-06-27 12:25:45', '2018-06-27 12:25:45');
INSERT INTO `worker_node` VALUES ('1462', '172.17.0.4', '1530079850804-60855', '2', '2018-06-27', '2018-06-27 14:10:50', '2018-06-27 14:10:50');
INSERT INTO `worker_node` VALUES ('1463', '192.168.1.95', '1530086485229-54060', '2', '2018-06-27', '2018-06-27 16:01:21', '2018-06-27 16:01:21');
INSERT INTO `worker_node` VALUES ('1464', '172.17.0.4', '1530086734693-2909', '2', '2018-06-27', '2018-06-27 16:05:34', '2018-06-27 16:05:34');
INSERT INTO `worker_node` VALUES ('1465', '192.168.1.95', '1530087014645-21838', '2', '2018-06-27', '2018-06-27 16:10:10', '2018-06-27 16:10:10');
INSERT INTO `worker_node` VALUES ('1466', '172.17.0.4', '1530087416131-87038', '2', '2018-06-27', '2018-06-27 16:16:56', '2018-06-27 16:16:56');
INSERT INTO `worker_node` VALUES ('1467', '192.168.1.95', '1530091374725-64082', '2', '2018-06-27', '2018-06-27 17:22:50', '2018-06-27 17:22:50');
INSERT INTO `worker_node` VALUES ('1468', '192.168.1.95', '1530091673661-89928', '2', '2018-06-27', '2018-06-27 17:27:49', '2018-06-27 17:27:49');
INSERT INTO `worker_node` VALUES ('1469', '172.17.0.4', '1530092834343-21027', '2', '2018-06-27', '2018-06-27 17:47:14', '2018-06-27 17:47:14');
INSERT INTO `worker_node` VALUES ('1470', '192.168.1.95', '1530093016223-43525', '2', '2018-06-27', '2018-06-27 17:50:12', '2018-06-27 17:50:12');
INSERT INTO `worker_node` VALUES ('1471', '172.17.0.4', '1530097980702-6322', '2', '2018-06-27', '2018-06-27 19:13:00', '2018-06-27 19:13:00');
INSERT INTO `worker_node` VALUES ('1472', '172.17.0.4', '1530098072695-56037', '2', '2018-06-27', '2018-06-27 19:14:32', '2018-06-27 19:14:32');
INSERT INTO `worker_node` VALUES ('1473', '172.17.0.4', '1530171964567-54850', '2', '2018-06-28', '2018-06-28 15:46:04', '2018-06-28 15:46:04');
INSERT INTO `worker_node` VALUES ('1474', '172.17.0.4', '1530173875135-10666', '2', '2018-06-28', '2018-06-28 16:17:55', '2018-06-28 16:17:55');
INSERT INTO `worker_node` VALUES ('1475', '192.168.1.95', '1530179745044-19986', '2', '2018-06-28', '2018-06-28 17:55:41', '2018-06-28 17:55:41');
INSERT INTO `worker_node` VALUES ('1476', '192.168.1.95', '1530179986084-59309', '2', '2018-06-28', '2018-06-28 17:59:42', '2018-06-28 17:59:42');
INSERT INTO `worker_node` VALUES ('1477', '192.168.1.95', '1530180577611-34543', '2', '2018-06-28', '2018-06-28 18:09:33', '2018-06-28 18:09:33');
INSERT INTO `worker_node` VALUES ('1478', '192.168.1.95', '1530181282756-23968', '2', '2018-06-28', '2018-06-28 18:21:18', '2018-06-28 18:21:18');
INSERT INTO `worker_node` VALUES ('1479', '172.17.0.4', '1530183214061-77341', '2', '2018-06-28', '2018-06-28 18:53:34', '2018-06-28 18:53:34');
INSERT INTO `worker_node` VALUES ('1480', '172.17.0.4', '1530242925201-31947', '2', '2018-06-29', '2018-06-29 11:28:45', '2018-06-29 11:28:45');
INSERT INTO `worker_node` VALUES ('1481', '172.17.0.4', '1530260502420-34937', '2', '2018-06-29', '2018-06-29 16:21:42', '2018-06-29 16:21:42');
INSERT INTO `worker_node` VALUES ('1482', '192.168.1.95', '1530262491350-14273', '2', '2018-06-29', '2018-06-29 16:54:48', '2018-06-29 16:54:48');
INSERT INTO `worker_node` VALUES ('1483', '172.17.0.4', '1530263104912-21658', '2', '2018-06-29', '2018-06-29 17:05:04', '2018-06-29 17:05:04');
INSERT INTO `worker_node` VALUES ('1484', '192.168.1.95', '1530269585257-74856', '2', '2018-06-29', '2018-06-29 18:53:02', '2018-06-29 18:53:02');
INSERT INTO `worker_node` VALUES ('1485', '192.168.1.95', '1530269637052-36934', '2', '2018-06-29', '2018-06-29 18:53:54', '2018-06-29 18:53:54');
INSERT INTO `worker_node` VALUES ('1486', '192.168.1.95', '1530269719995-28338', '2', '2018-06-29', '2018-06-29 18:55:16', '2018-06-29 18:55:16');
INSERT INTO `worker_node` VALUES ('1487', '192.168.1.95', '1530348925013-99709', '2', '2018-06-30', '2018-06-30 16:55:25', '2018-06-30 16:55:25');
INSERT INTO `worker_node` VALUES ('1488', '192.168.1.95', '1530349253730-52299', '2', '2018-06-30', '2018-06-30 17:00:53', '2018-06-30 17:00:53');
INSERT INTO `worker_node` VALUES ('1489', '192.168.1.95', '1530349728676-91026', '2', '2018-06-30', '2018-06-30 17:08:48', '2018-06-30 17:08:48');
INSERT INTO `worker_node` VALUES ('1490', '192.168.1.95', '1530350104246-98233', '2', '2018-06-30', '2018-06-30 17:15:04', '2018-06-30 17:15:04');
INSERT INTO `worker_node` VALUES ('1491', '192.168.1.95', '1530350399065-3972', '2', '2018-06-30', '2018-06-30 17:19:59', '2018-06-30 17:19:59');
INSERT INTO `worker_node` VALUES ('1492', '192.168.1.95', '1530350824776-69400', '2', '2018-06-30', '2018-06-30 17:27:04', '2018-06-30 17:27:04');
INSERT INTO `worker_node` VALUES ('1493', '192.168.1.95', '1530351049422-16950', '2', '2018-06-30', '2018-06-30 17:30:49', '2018-06-30 17:30:49');
INSERT INTO `worker_node` VALUES ('1494', '192.168.1.95', '1530352259840-26564', '2', '2018-06-30', '2018-06-30 17:50:59', '2018-06-30 17:50:59');
INSERT INTO `worker_node` VALUES ('1495', '192.168.1.95', '1530352574860-29872', '2', '2018-06-30', '2018-06-30 17:56:14', '2018-06-30 17:56:14');
INSERT INTO `worker_node` VALUES ('1496', '192.168.1.95', '1530353373249-57869', '2', '2018-06-30', '2018-06-30 18:09:33', '2018-06-30 18:09:33');
INSERT INTO `worker_node` VALUES ('1497', '192.168.1.95', '1530353471584-40087', '2', '2018-06-30', '2018-06-30 18:11:11', '2018-06-30 18:11:11');
INSERT INTO `worker_node` VALUES ('1498', '192.168.1.95', '1530353692326-91042', '2', '2018-06-30', '2018-06-30 18:14:52', '2018-06-30 18:14:52');
INSERT INTO `worker_node` VALUES ('1499', '192.168.1.95', '1530353858648-94450', '2', '2018-06-30', '2018-06-30 18:17:38', '2018-06-30 18:17:38');
INSERT INTO `worker_node` VALUES ('1500', '192.168.1.95', '1530363199345-6145', '2', '2018-06-30', '2018-06-30 20:53:18', '2018-06-30 20:53:18');
INSERT INTO `worker_node` VALUES ('1501', '192.168.1.95', '1530364537540-42482', '2', '2018-06-30', '2018-06-30 21:15:36', '2018-06-30 21:15:36');
INSERT INTO `worker_node` VALUES ('1502', '192.168.1.95', '1530364881610-92883', '2', '2018-06-30', '2018-06-30 21:21:20', '2018-06-30 21:21:20');
INSERT INTO `worker_node` VALUES ('1503', '192.168.1.95', '1530499568004-78990', '2', '2018-07-02', '2018-07-02 10:46:08', '2018-07-02 10:46:08');
INSERT INTO `worker_node` VALUES ('1504', '192.168.1.95', '1530583598331-43921', '2', '2018-07-03', '2018-07-03 10:06:37', '2018-07-03 10:06:37');
INSERT INTO `worker_node` VALUES ('1505', '192.168.1.95', '1530584798407-45698', '2', '2018-07-03', '2018-07-03 10:26:37', '2018-07-03 10:26:37');
INSERT INTO `worker_node` VALUES ('1506', '192.168.1.95', '1530584983201-74213', '2', '2018-07-03', '2018-07-03 10:29:42', '2018-07-03 10:29:42');
INSERT INTO `worker_node` VALUES ('1507', '192.168.1.95', '1530585525873-50611', '2', '2018-07-03', '2018-07-03 10:38:45', '2018-07-03 10:38:45');
INSERT INTO `worker_node` VALUES ('1508', '192.168.1.95', '1530585735610-8696', '2', '2018-07-03', '2018-07-03 10:42:14', '2018-07-03 10:42:14');
INSERT INTO `worker_node` VALUES ('1509', '192.168.1.95', '1530586120557-66611', '2', '2018-07-03', '2018-07-03 10:48:39', '2018-07-03 10:48:39');
INSERT INTO `worker_node` VALUES ('1510', '192.168.1.95', '1530586446026-1595', '2', '2018-07-03', '2018-07-03 10:54:05', '2018-07-03 10:54:05');
INSERT INTO `worker_node` VALUES ('1511', '192.168.1.95', '1530587094507-37067', '2', '2018-07-03', '2018-07-03 11:04:53', '2018-07-03 11:04:53');
INSERT INTO `worker_node` VALUES ('1512', '192.168.1.95', '1530587728260-30561', '2', '2018-07-03', '2018-07-03 11:15:27', '2018-07-03 11:15:27');
INSERT INTO `worker_node` VALUES ('1513', '192.168.1.95', '1530588077871-46563', '2', '2018-07-03', '2018-07-03 11:21:17', '2018-07-03 11:21:17');
INSERT INTO `worker_node` VALUES ('1514', '192.168.1.95', '1530588719662-335', '2', '2018-07-03', '2018-07-03 11:31:58', '2018-07-03 11:31:58');
INSERT INTO `worker_node` VALUES ('1515', '192.168.1.95', '1530588961039-70435', '2', '2018-07-03', '2018-07-03 11:36:00', '2018-07-03 11:36:00');
INSERT INTO `worker_node` VALUES ('1516', '192.168.1.95', '1530589015539-43056', '2', '2018-07-03', '2018-07-03 11:36:54', '2018-07-03 11:36:54');
INSERT INTO `worker_node` VALUES ('1517', '192.168.1.95', '1530589441695-41533', '2', '2018-07-03', '2018-07-03 11:44:00', '2018-07-03 11:44:00');
INSERT INTO `worker_node` VALUES ('1518', '192.168.1.95', '1530589928515-66601', '2', '2018-07-03', '2018-07-03 11:52:07', '2018-07-03 11:52:07');
INSERT INTO `worker_node` VALUES ('1519', '192.168.1.95', '1530590350003-13790', '2', '2018-07-03', '2018-07-03 11:59:09', '2018-07-03 11:59:09');
INSERT INTO `worker_node` VALUES ('1520', '192.168.1.95', '1530590658796-72955', '2', '2018-07-03', '2018-07-03 12:04:17', '2018-07-03 12:04:17');
INSERT INTO `worker_node` VALUES ('1521', '192.168.1.95', '1530590941560-13467', '2', '2018-07-03', '2018-07-03 12:09:00', '2018-07-03 12:09:00');
INSERT INTO `worker_node` VALUES ('1522', '192.168.1.95', '1530591083203-48162', '2', '2018-07-03', '2018-07-03 12:11:22', '2018-07-03 12:11:22');
INSERT INTO `worker_node` VALUES ('1523', '192.168.1.95', '1530596375234-50933', '2', '2018-07-03', '2018-07-03 13:39:33', '2018-07-03 13:39:33');
INSERT INTO `worker_node` VALUES ('1524', '192.168.1.95', '1530596498185-59584', '2', '2018-07-03', '2018-07-03 13:41:36', '2018-07-03 13:41:36');
INSERT INTO `worker_node` VALUES ('1525', '192.168.1.95', '1530596914961-48941', '2', '2018-07-03', '2018-07-03 13:48:33', '2018-07-03 13:48:33');
INSERT INTO `worker_node` VALUES ('1526', '192.168.1.95', '1530597247690-2827', '2', '2018-07-03', '2018-07-03 13:54:06', '2018-07-03 13:54:06');
INSERT INTO `worker_node` VALUES ('1527', '192.168.1.95', '1530597472729-47156', '2', '2018-07-03', '2018-07-03 13:57:51', '2018-07-03 13:57:51');
INSERT INTO `worker_node` VALUES ('1528', '192.168.1.95', '1530597572804-81595', '2', '2018-07-03', '2018-07-03 13:59:31', '2018-07-03 13:59:31');
INSERT INTO `worker_node` VALUES ('1529', '192.168.1.95', '1530597668525-31341', '2', '2018-07-03', '2018-07-03 14:01:07', '2018-07-03 14:01:07');
INSERT INTO `worker_node` VALUES ('1530', '192.168.1.95', '1530610727147-16085', '2', '2018-07-03', '2018-07-03 17:38:45', '2018-07-03 17:38:45');
INSERT INTO `worker_node` VALUES ('1531', '192.168.1.95', '1530613124692-4549', '2', '2018-07-03', '2018-07-03 18:18:42', '2018-07-03 18:18:42');
INSERT INTO `worker_node` VALUES ('1532', '192.168.1.95', '1530613664089-36475', '2', '2018-07-03', '2018-07-03 18:27:41', '2018-07-03 18:27:41');
INSERT INTO `worker_node` VALUES ('1533', '192.168.1.95', '1530614007119-61225', '2', '2018-07-03', '2018-07-03 18:33:24', '2018-07-03 18:33:24');
INSERT INTO `worker_node` VALUES ('1534', '192.168.1.95', '1530614450712-29951', '2', '2018-07-03', '2018-07-03 18:40:48', '2018-07-03 18:40:48');
INSERT INTO `worker_node` VALUES ('1535', '192.168.1.95', '1530693491518-19834', '2', '2018-07-04', '2018-07-04 16:38:07', '2018-07-04 16:38:07');
INSERT INTO `worker_node` VALUES ('1536', '192.168.1.95', '1530696027678-97567', '2', '2018-07-04', '2018-07-04 17:20:23', '2018-07-04 17:20:23');
INSERT INTO `worker_node` VALUES ('1537', '192.168.1.95', '1530696358433-80843', '2', '2018-07-04', '2018-07-04 17:25:54', '2018-07-04 17:25:54');
INSERT INTO `worker_node` VALUES ('1538', '192.168.1.95', '1530696480197-27270', '2', '2018-07-04', '2018-07-04 17:27:55', '2018-07-04 17:27:55');
INSERT INTO `worker_node` VALUES ('1539', '192.168.1.95', '1530697116621-16968', '2', '2018-07-04', '2018-07-04 17:38:32', '2018-07-04 17:38:32');
INSERT INTO `worker_node` VALUES ('1540', '192.168.1.95', '1530697284464-63900', '2', '2018-07-04', '2018-07-04 17:41:19', '2018-07-04 17:41:19');
INSERT INTO `worker_node` VALUES ('1541', '192.168.1.95', '1530697708840-60531', '2', '2018-07-04', '2018-07-04 17:48:24', '2018-07-04 17:48:24');
INSERT INTO `worker_node` VALUES ('1542', '192.168.1.95', '1530698783326-12717', '2', '2018-07-04', '2018-07-04 18:06:18', '2018-07-04 18:06:18');
INSERT INTO `worker_node` VALUES ('1543', '192.168.1.95', '1530699324857-25842', '2', '2018-07-04', '2018-07-04 18:15:20', '2018-07-04 18:15:20');
INSERT INTO `worker_node` VALUES ('1544', '192.168.1.95', '1530700054648-462', '2', '2018-07-04', '2018-07-04 18:27:30', '2018-07-04 18:27:30');
INSERT INTO `worker_node` VALUES ('1545', '192.168.1.95', '1530870115486-54866', '2', '2018-07-06', '2018-07-06 17:41:53', '2018-07-06 17:41:53');
INSERT INTO `worker_node` VALUES ('1546', '172.17.0.4', '1531107523392-82480', '2', '2018-07-09', '2018-07-09 11:38:43', '2018-07-09 11:38:43');
INSERT INTO `worker_node` VALUES ('1547', '192.168.1.95', '1531193164585-24186', '2', '2018-07-10', '2018-07-10 11:26:04', '2018-07-10 11:26:04');
INSERT INTO `worker_node` VALUES ('1548', '192.168.1.95', '1531193665778-55979', '2', '2018-07-10', '2018-07-10 11:34:25', '2018-07-10 11:34:25');
INSERT INTO `worker_node` VALUES ('1549', '192.168.1.95', '1531193800477-25622', '2', '2018-07-10', '2018-07-10 11:36:39', '2018-07-10 11:36:39');
INSERT INTO `worker_node` VALUES ('1550', '192.168.1.95', '1531194236429-67370', '2', '2018-07-10', '2018-07-10 11:43:55', '2018-07-10 11:43:55');
INSERT INTO `worker_node` VALUES ('1551', '192.168.1.95', '1531295288409-13728', '2', '2018-07-11', '2018-07-11 15:48:03', '2018-07-11 15:48:03');
INSERT INTO `worker_node` VALUES ('1552', '192.168.1.95', '1531296001409-85575', '2', '2018-07-11', '2018-07-11 15:59:55', '2018-07-11 15:59:55');
INSERT INTO `worker_node` VALUES ('1553', '192.168.1.95', '1531296234138-86824', '2', '2018-07-11', '2018-07-11 16:03:48', '2018-07-11 16:03:48');
INSERT INTO `worker_node` VALUES ('1554', '192.168.1.95', '1531296559622-94041', '2', '2018-07-11', '2018-07-11 16:09:14', '2018-07-11 16:09:14');
INSERT INTO `worker_node` VALUES ('1555', '192.168.1.95', '1531297486156-88041', '2', '2018-07-11', '2018-07-11 16:24:40', '2018-07-11 16:24:40');
INSERT INTO `worker_node` VALUES ('1556', '172.17.0.4', '1531791647985-81264', '2', '2018-07-17', '2018-07-17 09:40:48', '2018-07-17 09:40:48');
INSERT INTO `worker_node` VALUES ('1557', '172.17.0.4', '1531979055596-13319', '2', '2018-07-19', '2018-07-19 13:44:15', '2018-07-19 13:44:15');
INSERT INTO `worker_node` VALUES ('1558', '172.17.0.4', '1533110783424-3629', '2', '2018-08-01', '2018-08-01 16:06:23', '2018-08-01 16:06:23');
INSERT INTO `worker_node` VALUES ('1559', '192.168.1.95', '1533174044074-93160', '2', '2018-08-02', '2018-08-02 09:40:42', '2018-08-02 09:40:42');
INSERT INTO `worker_node` VALUES ('1560', '192.168.1.95', '1533174332574-85283', '2', '2018-08-02', '2018-08-02 09:45:30', '2018-08-02 09:45:30');
INSERT INTO `worker_node` VALUES ('1561', '172.17.0.4', '1534856245457-82614', '2', '2018-08-21', '2018-08-21 20:57:25', '2018-08-21 20:57:25');
INSERT INTO `worker_node` VALUES ('1562', '172.17.0.4', '1534856277081-45932', '2', '2018-08-21', '2018-08-21 20:57:57', '2018-08-21 20:57:57');
INSERT INTO `worker_node` VALUES ('1563', '172.17.0.3', '1534856662352-934', '2', '2018-08-21', '2018-08-21 21:04:22', '2018-08-21 21:04:22');
INSERT INTO `worker_node` VALUES ('1564', '192.168.1.182', '1535613115256-3485', '2', '2018-08-30', '2018-08-30 15:11:53', '2018-08-30 15:11:53');
INSERT INTO `worker_node` VALUES ('1565', '192.168.1.182', '1535614118438-10086', '2', '2018-08-30', '2018-08-30 15:28:36', '2018-08-30 15:28:36');
INSERT INTO `worker_node` VALUES ('1566', '192.168.1.182', '1535614464627-89332', '2', '2018-08-30', '2018-08-30 15:34:23', '2018-08-30 15:34:23');
INSERT INTO `worker_node` VALUES ('1567', '192.168.1.182', '1535614533087-42157', '2', '2018-08-30', '2018-08-30 15:35:31', '2018-08-30 15:35:31');
INSERT INTO `worker_node` VALUES ('1568', '192.168.1.182', '1535614823374-90386', '2', '2018-08-30', '2018-08-30 15:40:21', '2018-08-30 15:40:21');
INSERT INTO `worker_node` VALUES ('1569', '192.168.1.182', '1535615103276-8527', '2', '2018-08-30', '2018-08-30 15:45:01', '2018-08-30 15:45:01');
INSERT INTO `worker_node` VALUES ('1570', '192.168.1.182', '1535615242266-86262', '2', '2018-08-30', '2018-08-30 15:47:20', '2018-08-30 15:47:20');
INSERT INTO `worker_node` VALUES ('1571', '192.168.1.182', '1535615807409-23397', '2', '2018-08-30', '2018-08-30 15:56:45', '2018-08-30 15:56:45');
INSERT INTO `worker_node` VALUES ('1572', '192.168.1.182', '1535616093486-11281', '2', '2018-08-30', '2018-08-30 16:01:31', '2018-08-30 16:01:31');
INSERT INTO `worker_node` VALUES ('1573', '192.168.1.182', '1535616266888-9557', '2', '2018-08-30', '2018-08-30 16:04:25', '2018-08-30 16:04:25');
INSERT INTO `worker_node` VALUES ('1574', '172.17.0.5', '1535620270398-58484', '2', '2018-08-30', '2018-08-30 17:11:10', '2018-08-30 17:11:10');
INSERT INTO `worker_node` VALUES ('1575', '192.168.1.182', '1535625337100-4506', '2', '2018-08-30', '2018-08-30 18:35:35', '2018-08-30 18:35:35');
INSERT INTO `worker_node` VALUES ('1576', '192.168.1.182', '1535689452781-55792', '2', '2018-08-31', '2018-08-31 12:24:10', '2018-08-31 12:24:10');
INSERT INTO `worker_node` VALUES ('1577', '172.17.0.11', '1535702389896-31202', '2', '2018-08-31', '2018-08-31 15:59:49', '2018-08-31 15:59:49');
INSERT INTO `worker_node` VALUES ('1578', '192.168.1.182', '1535705543161-44603', '2', '2018-08-31', '2018-08-31 16:52:21', '2018-08-31 16:52:21');
INSERT INTO `worker_node` VALUES ('1579', '192.168.1.182', '1535956830777-136', '2', '2018-09-03', '2018-09-03 14:40:30', '2018-09-03 14:40:30');
INSERT INTO `worker_node` VALUES ('1580', '192.168.1.182', '1535960427289-36610', '2', '2018-09-03', '2018-09-03 15:40:27', '2018-09-03 15:40:27');
INSERT INTO `worker_node` VALUES ('1581', '192.168.1.182', '1535961745546-24960', '2', '2018-09-03', '2018-09-03 16:02:25', '2018-09-03 16:02:25');
INSERT INTO `worker_node` VALUES ('1582', '172.17.0.11', '1536144789029-92245', '2', '2018-09-05', '2018-09-05 18:53:09', '2018-09-05 18:53:09');
INSERT INTO `worker_node` VALUES ('1583', '172.17.0.10', '1536213417845-71218', '2', '2018-09-06', '2018-09-06 13:56:58', '2018-09-06 13:56:58');
INSERT INTO `worker_node` VALUES ('1584', '192.168.1.182', '1536293493646-83442', '2', '2018-09-07', '2018-09-07 12:11:32', '2018-09-07 12:11:32');
INSERT INTO `worker_node` VALUES ('1585', '2001:0:9d38:90d7:140b:1152:d5fd:336e', '1536551002717-66162', '2', '2018-09-10', '2018-09-10 11:43:18', '2018-09-10 11:43:18');
INSERT INTO `worker_node` VALUES ('1586', '2001:0:9d38:90d7:140b:1152:d5fd:336e', '1536629484770-63471', '2', '2018-09-11', '2018-09-11 09:31:24', '2018-09-11 09:31:24');
INSERT INTO `worker_node` VALUES ('1587', '2001:0:9d38:90d7:140b:1152:d5fd:336e', '1536656403028-36078', '2', '2018-09-11', '2018-09-11 17:00:03', '2018-09-11 17:00:03');
INSERT INTO `worker_node` VALUES ('1588', '2001:0:9d38:90d7:140b:1152:d5fd:336e', '1536656534228-6840', '2', '2018-09-11', '2018-09-11 17:02:14', '2018-09-11 17:02:14');
INSERT INTO `worker_node` VALUES ('1589', '2001:0:9d38:90d7:140b:1152:d5fd:336e', '1536658512503-44584', '2', '2018-09-11', '2018-09-11 17:35:12', '2018-09-11 17:35:12');
INSERT INTO `worker_node` VALUES ('1590', '192.168.1.182', '1536715709436-12925', '2', '2018-09-12', '2018-09-12 09:28:28', '2018-09-12 09:28:28');
INSERT INTO `worker_node` VALUES ('1591', '2001:0:9d38:90d7:140b:1152:d5fd:336e', '1536751846239-21386', '2', '2018-09-12', '2018-09-12 19:30:45', '2018-09-12 19:30:45');
INSERT INTO `worker_node` VALUES ('1592', '2001:0:9d38:90d7:140b:1152:d5fd:336e', '1536752478579-3825', '2', '2018-09-12', '2018-09-12 19:41:18', '2018-09-12 19:41:18');
INSERT INTO `worker_node` VALUES ('1593', '2001:0:9d38:90d7:140b:1152:d5fd:336e', '1536752621099-44373', '2', '2018-09-12', '2018-09-12 19:43:40', '2018-09-12 19:43:40');
INSERT INTO `worker_node` VALUES ('1594', '2001:0:9d38:90d7:140b:1152:d5fd:336e', '1536752966704-74048', '2', '2018-09-12', '2018-09-12 19:49:26', '2018-09-12 19:49:26');
INSERT INTO `worker_node` VALUES ('1595', '2001:0:9d38:90d7:140b:1152:d5fd:336e', '1536802120946-40718', '2', '2018-09-13', '2018-09-13 09:28:39', '2018-09-13 09:28:39');
INSERT INTO `worker_node` VALUES ('1596', '2001:0:9d38:90d7:140b:1152:d5fd:336e', '1536832701417-56924', '2', '2018-09-13', '2018-09-13 17:58:20', '2018-09-13 17:58:20');
INSERT INTO `worker_node` VALUES ('1597', '2001:0:9d38:90d7:140b:1152:d5fd:336e', '1536832917277-45722', '2', '2018-09-13', '2018-09-13 18:01:56', '2018-09-13 18:01:56');
INSERT INTO `worker_node` VALUES ('1598', '2001:0:9d38:90d7:140b:1152:d5fd:336e', '1536889124068-24318', '2', '2018-09-14', '2018-09-14 09:38:41', '2018-09-14 09:38:41');
INSERT INTO `worker_node` VALUES ('1599', '192.168.1.182', '1537066030384-3201', '2', '2018-09-16', '2018-09-16 10:47:06', '2018-09-16 10:47:06');
INSERT INTO `worker_node` VALUES ('1600', '192.168.1.95', '1537066815365-10822', '2', '2018-09-16', '2018-09-16 11:00:12', '2018-09-16 11:00:12');
INSERT INTO `worker_node` VALUES ('1601', '192.168.1.95', '1537067339773-97870', '2', '2018-09-16', '2018-09-16 11:08:57', '2018-09-16 11:08:57');
INSERT INTO `worker_node` VALUES ('1602', '192.168.1.95', '1537067705195-50974', '2', '2018-09-16', '2018-09-16 11:15:02', '2018-09-16 11:15:02');
INSERT INTO `worker_node` VALUES ('1603', '172.17.0.10', '1537068036915-33358', '2', '2018-09-16', '2018-09-16 11:20:36', '2018-09-16 11:20:36');
INSERT INTO `worker_node` VALUES ('1604', '172.17.0.10', '1537068218939-19507', '2', '2018-09-16', '2018-09-16 11:23:38', '2018-09-16 11:23:38');
INSERT INTO `worker_node` VALUES ('1605', '192.168.1.182', '1537076815477-36621', '2', '2018-09-16', '2018-09-16 13:46:55', '2018-09-16 13:46:55');
INSERT INTO `worker_node` VALUES ('1606', '192.168.1.182', '1537076862035-18034', '2', '2018-09-16', '2018-09-16 13:47:42', '2018-09-16 13:47:42');
INSERT INTO `worker_node` VALUES ('1607', '192.168.1.182', '1537076904646-20051', '2', '2018-09-16', '2018-09-16 13:48:24', '2018-09-16 13:48:24');
INSERT INTO `worker_node` VALUES ('1608', '192.168.1.182', '1537077186079-24838', '2', '2018-09-16', '2018-09-16 13:53:06', '2018-09-16 13:53:06');
INSERT INTO `worker_node` VALUES ('1609', '172.17.0.9', '1537082014712-41057', '2', '2018-09-16', '2018-09-16 15:13:34', '2018-09-16 15:13:34');
INSERT INTO `worker_node` VALUES ('1610', '192.168.1.182', '1537085014722-66099', '2', '2018-09-16', '2018-09-16 16:03:34', '2018-09-16 16:03:34');
INSERT INTO `worker_node` VALUES ('1611', '192.168.1.182', '1537086118647-96643', '2', '2018-09-16', '2018-09-16 16:21:58', '2018-09-16 16:21:58');
INSERT INTO `worker_node` VALUES ('1612', '192.168.1.182', '1537088822802-73361', '2', '2018-09-16', '2018-09-16 17:07:02', '2018-09-16 17:07:02');
INSERT INTO `worker_node` VALUES ('1613', '192.168.1.182', '1537088879416-15262', '2', '2018-09-16', '2018-09-16 17:07:59', '2018-09-16 17:07:59');
INSERT INTO `worker_node` VALUES ('1614', '192.168.1.182', '1537089799194-57962', '2', '2018-09-16', '2018-09-16 17:23:19', '2018-09-16 17:23:19');
INSERT INTO `worker_node` VALUES ('1615', '192.168.1.182', '1537090699415-84971', '2', '2018-09-16', '2018-09-16 17:38:19', '2018-09-16 17:38:19');
INSERT INTO `worker_node` VALUES ('1616', '192.168.1.182', '1537090826616-58989', '2', '2018-09-16', '2018-09-16 17:40:26', '2018-09-16 17:40:26');
INSERT INTO `worker_node` VALUES ('1617', '192.168.1.182', '1537091652924-99986', '2', '2018-09-16', '2018-09-16 17:54:13', '2018-09-16 17:54:13');
INSERT INTO `worker_node` VALUES ('1618', '192.168.1.182', '1537092494147-3385', '2', '2018-09-16', '2018-09-16 18:08:14', '2018-09-16 18:08:14');
INSERT INTO `worker_node` VALUES ('1619', '192.168.1.182', '1537093068714-105', '2', '2018-09-16', '2018-09-16 18:17:48', '2018-09-16 18:17:48');
INSERT INTO `worker_node` VALUES ('1620', '192.168.1.182', '1537095290046-42602', '2', '2018-09-16', '2018-09-16 18:54:50', '2018-09-16 18:54:50');
INSERT INTO `worker_node` VALUES ('1621', '192.168.1.182', '1537095532339-22879', '2', '2018-09-16', '2018-09-16 18:58:52', '2018-09-16 18:58:52');
INSERT INTO `worker_node` VALUES ('1622', '192.168.1.182', '1537098031315-18655', '2', '2018-09-16', '2018-09-16 19:40:31', '2018-09-16 19:40:31');
INSERT INTO `worker_node` VALUES ('1623', '192.168.1.182', '1537098159626-66876', '2', '2018-09-16', '2018-09-16 19:42:39', '2018-09-16 19:42:39');
INSERT INTO `worker_node` VALUES ('1624', '192.168.1.182', '1537098201236-90805', '2', '2018-09-16', '2018-09-16 19:43:21', '2018-09-16 19:43:21');
INSERT INTO `worker_node` VALUES ('1625', '192.168.1.182', '1537098276810-90039', '2', '2018-09-16', '2018-09-16 19:44:37', '2018-09-16 19:44:37');
INSERT INTO `worker_node` VALUES ('1626', '192.168.1.182', '1537099035954-81881', '2', '2018-09-16', '2018-09-16 19:57:16', '2018-09-16 19:57:16');
INSERT INTO `worker_node` VALUES ('1627', '192.168.1.182', '1537100375558-58979', '2', '2018-09-16', '2018-09-16 20:19:35', '2018-09-16 20:19:35');
INSERT INTO `worker_node` VALUES ('1628', '192.168.1.182', '1537101447217-84800', '2', '2018-09-16', '2018-09-16 20:37:27', '2018-09-16 20:37:27');
INSERT INTO `worker_node` VALUES ('1629', '192.168.1.182', '1537101651488-80225', '2', '2018-09-16', '2018-09-16 20:40:51', '2018-09-16 20:40:51');
INSERT INTO `worker_node` VALUES ('1630', '192.168.1.182', '1537106858064-78136', '2', '2018-09-16', '2018-09-16 22:07:38', '2018-09-16 22:07:38');
INSERT INTO `worker_node` VALUES ('1631', '192.168.1.182', '1537108107691-26974', '2', '2018-09-16', '2018-09-16 22:28:28', '2018-09-16 22:28:28');
INSERT INTO `worker_node` VALUES ('1632', '192.168.1.182', '1537108468453-72411', '2', '2018-09-16', '2018-09-16 22:34:28', '2018-09-16 22:34:28');
INSERT INTO `worker_node` VALUES ('1633', '192.168.1.182', '1537108579240-47341', '2', '2018-09-16', '2018-09-16 22:36:19', '2018-09-16 22:36:19');
INSERT INTO `worker_node` VALUES ('1634', '192.168.1.182', '1537108697476-26813', '2', '2018-09-16', '2018-09-16 22:38:17', '2018-09-16 22:38:17');
INSERT INTO `worker_node` VALUES ('1635', '192.168.1.182', '1537108762217-77950', '2', '2018-09-16', '2018-09-16 22:39:22', '2018-09-16 22:39:22');
INSERT INTO `worker_node` VALUES ('1636', '192.168.1.182', '1537108922457-2561', '2', '2018-09-16', '2018-09-16 22:42:02', '2018-09-16 22:42:02');
INSERT INTO `worker_node` VALUES ('1637', '192.168.1.182', '1537109013008-89028', '2', '2018-09-16', '2018-09-16 22:43:33', '2018-09-16 22:43:33');
INSERT INTO `worker_node` VALUES ('1638', '192.168.1.182', '1537109068911-90083', '2', '2018-09-16', '2018-09-16 22:44:29', '2018-09-16 22:44:29');
INSERT INTO `worker_node` VALUES ('1639', '192.168.1.182', '1537109169836-63715', '2', '2018-09-16', '2018-09-16 22:46:10', '2018-09-16 22:46:10');
INSERT INTO `worker_node` VALUES ('1640', '192.168.1.182', '1537109214365-37973', '2', '2018-09-16', '2018-09-16 22:46:54', '2018-09-16 22:46:54');
INSERT INTO `worker_node` VALUES ('1641', '192.168.1.182', '1537109297288-62261', '2', '2018-09-16', '2018-09-16 22:48:17', '2018-09-16 22:48:17');
INSERT INTO `worker_node` VALUES ('1642', '192.168.1.182', '1537109499357-15095', '2', '2018-09-16', '2018-09-16 22:51:39', '2018-09-16 22:51:39');
INSERT INTO `worker_node` VALUES ('1643', '192.168.1.182', '1537109646512-36985', '2', '2018-09-16', '2018-09-16 22:54:06', '2018-09-16 22:54:06');
INSERT INTO `worker_node` VALUES ('1644', '192.168.1.182', '1537109754659-80959', '2', '2018-09-16', '2018-09-16 22:55:55', '2018-09-16 22:55:55');
INSERT INTO `worker_node` VALUES ('1645', '192.168.1.182', '1537109913401-32012', '2', '2018-09-16', '2018-09-16 22:58:33', '2018-09-16 22:58:33');
INSERT INTO `worker_node` VALUES ('1646', '192.168.1.182', '1537110151053-61929', '2', '2018-09-16', '2018-09-16 23:02:31', '2018-09-16 23:02:31');
INSERT INTO `worker_node` VALUES ('1647', '192.168.1.182', '1537110633669-81333', '2', '2018-09-16', '2018-09-16 23:10:34', '2018-09-16 23:10:34');
INSERT INTO `worker_node` VALUES ('1648', '192.168.1.182', '1537145353568-77464', '2', '2018-09-17', '2018-09-17 08:49:12', '2018-09-17 08:49:12');
INSERT INTO `worker_node` VALUES ('1649', '192.168.1.182', '1537259615090-1639', '2', '2018-09-18', '2018-09-18 16:33:33', '2018-09-18 16:33:33');
INSERT INTO `worker_node` VALUES ('1650', '192.168.1.182', '1537260923544-36095', '2', '2018-09-18', '2018-09-18 16:55:21', '2018-09-18 16:55:21');
INSERT INTO `worker_node` VALUES ('1651', '192.168.1.182', '1537264112889-72494', '2', '2018-09-18', '2018-09-18 17:48:31', '2018-09-18 17:48:31');
INSERT INTO `worker_node` VALUES ('1652', '192.168.1.182', '1537274640002-90432', '2', '2018-09-18', '2018-09-18 20:43:58', '2018-09-18 20:43:58');
INSERT INTO `worker_node` VALUES ('1653', '192.168.1.182', '1537322795739-66774', '2', '2018-09-19', '2018-09-19 10:06:33', '2018-09-19 10:06:33');
INSERT INTO `worker_node` VALUES ('1654', '192.168.1.182', '1537333356870-40844', '2', '2018-09-19', '2018-09-19 13:02:34', '2018-09-19 13:02:34');
INSERT INTO `worker_node` VALUES ('1655', '192.168.1.182', '1537335548774-2469', '2', '2018-09-19', '2018-09-19 13:39:06', '2018-09-19 13:39:06');
INSERT INTO `worker_node` VALUES ('1656', '192.168.1.182', '1537336286376-1433', '2', '2018-09-19', '2018-09-19 13:51:24', '2018-09-19 13:51:24');
INSERT INTO `worker_node` VALUES ('1657', '192.168.1.182', '1537336805842-55116', '2', '2018-09-19', '2018-09-19 14:00:03', '2018-09-19 14:00:03');
INSERT INTO `worker_node` VALUES ('1658', '192.168.1.182', '1537337814275-1720', '2', '2018-09-19', '2018-09-19 14:16:52', '2018-09-19 14:16:52');
INSERT INTO `worker_node` VALUES ('1659', '192.168.1.182', '1537358790471-76556', '2', '2018-09-19', '2018-09-19 20:06:30', '2018-09-19 20:06:30');
INSERT INTO `worker_node` VALUES ('1660', '192.168.1.182', '1537358972579-91231', '2', '2018-09-19', '2018-09-19 20:09:32', '2018-09-19 20:09:32');
INSERT INTO `worker_node` VALUES ('1661', '192.168.1.182', '1537359938918-51852', '2', '2018-09-19', '2018-09-19 20:25:39', '2018-09-19 20:25:39');
INSERT INTO `worker_node` VALUES ('1662', '192.168.1.182', '1537406857370-80953', '2', '2018-09-20', '2018-09-20 09:27:36', '2018-09-20 09:27:36');
INSERT INTO `worker_node` VALUES ('1663', '192.168.1.182', '1537409386468-59833', '2', '2018-09-20', '2018-09-20 10:09:45', '2018-09-20 10:09:45');
INSERT INTO `worker_node` VALUES ('1664', '192.168.1.182', '1537417146922-11565', '2', '2018-09-20', '2018-09-20 12:19:05', '2018-09-20 12:19:05');
INSERT INTO `worker_node` VALUES ('1665', '192.168.1.182', '1537429357122-22856', '2', '2018-09-20', '2018-09-20 15:42:36', '2018-09-20 15:42:36');
INSERT INTO `worker_node` VALUES ('1666', '192.168.1.182', '1537431030396-96024', '2', '2018-09-20', '2018-09-20 16:10:29', '2018-09-20 16:10:29');
INSERT INTO `worker_node` VALUES ('1667', '192.168.1.182', '1537431352686-77531', '2', '2018-09-20', '2018-09-20 16:15:51', '2018-09-20 16:15:51');
INSERT INTO `worker_node` VALUES ('1668', '192.168.1.182', '1537431654139-18891', '2', '2018-09-20', '2018-09-20 16:20:53', '2018-09-20 16:20:53');
INSERT INTO `worker_node` VALUES ('1669', '192.168.1.182', '1537432146034-40624', '2', '2018-09-20', '2018-09-20 16:29:05', '2018-09-20 16:29:05');
INSERT INTO `worker_node` VALUES ('1670', '192.168.1.182', '1537432595072-85372', '2', '2018-09-20', '2018-09-20 16:36:34', '2018-09-20 16:36:34');
INSERT INTO `worker_node` VALUES ('1671', '192.168.1.182', '1537433690427-24891', '2', '2018-09-20', '2018-09-20 16:54:49', '2018-09-20 16:54:49');
INSERT INTO `worker_node` VALUES ('1672', '192.168.1.182', '1537493454741-98299', '2', '2018-09-21', '2018-09-21 09:30:53', '2018-09-21 09:30:53');
INSERT INTO `worker_node` VALUES ('1673', '192.168.1.182', '1537503307815-8359', '2', '2018-09-21', '2018-09-21 12:15:06', '2018-09-21 12:15:06');
INSERT INTO `worker_node` VALUES ('1674', '192.168.1.182', '1537503472045-39372', '2', '2018-09-21', '2018-09-21 12:17:50', '2018-09-21 12:17:50');
INSERT INTO `worker_node` VALUES ('1675', '192.168.1.182', '1537528722826-30530', '2', '2018-09-21', '2018-09-21 19:18:42', '2018-09-21 19:18:42');
INSERT INTO `worker_node` VALUES ('1676', '192.168.1.182', '1537529332229-83865', '2', '2018-09-21', '2018-09-21 19:28:51', '2018-09-21 19:28:51');
INSERT INTO `worker_node` VALUES ('1677', '192.168.1.182', '1537531137562-31027', '2', '2018-09-21', '2018-09-21 19:58:56', '2018-09-21 19:58:56');
INSERT INTO `worker_node` VALUES ('1678', '192.168.1.182', '1537838831466-79001', '2', '2018-09-25', '2018-09-25 09:27:06', '2018-09-25 09:27:06');
INSERT INTO `worker_node` VALUES ('1679', '192.168.1.182', '1538028887063-64232', '2', '2018-09-27', '2018-09-27 14:14:45', '2018-09-27 14:14:45');
INSERT INTO `worker_node` VALUES ('1680', '192.168.1.182', '1538105346807-23807', '2', '2018-09-28', '2018-09-28 11:29:05', '2018-09-28 11:29:05');
INSERT INTO `worker_node` VALUES ('1681', '192.168.1.182', '1538200475511-5651', '2', '2018-09-29', '2018-09-29 13:54:33', '2018-09-29 13:54:33');
INSERT INTO `worker_node` VALUES ('1682', '192.168.1.182', '1538271203963-84578', '2', '2018-09-30', '2018-09-30 09:33:21', '2018-09-30 09:33:21');
INSERT INTO `worker_node` VALUES ('1683', '192.168.1.95', '1538280277634-78541', '2', '2018-09-30', '2018-09-30 12:04:37', '2018-09-30 12:04:37');
INSERT INTO `worker_node` VALUES ('1684', '192.168.1.95', '1538280625756-77005', '2', '2018-09-30', '2018-09-30 12:10:25', '2018-09-30 12:10:25');
INSERT INTO `worker_node` VALUES ('1685', '192.168.1.95', '1538280729249-45991', '2', '2018-09-30', '2018-09-30 12:12:08', '2018-09-30 12:12:08');
INSERT INTO `worker_node` VALUES ('1686', '192.168.1.95', '1538280801837-14024', '2', '2018-09-30', '2018-09-30 12:13:21', '2018-09-30 12:13:21');
INSERT INTO `worker_node` VALUES ('1687', '192.168.1.95', '1538280957951-78884', '2', '2018-09-30', '2018-09-30 12:15:57', '2018-09-30 12:15:57');
INSERT INTO `worker_node` VALUES ('1688', '192.168.1.95', '1538281015000-58525', '2', '2018-09-30', '2018-09-30 12:16:54', '2018-09-30 12:16:54');
INSERT INTO `worker_node` VALUES ('1689', '192.168.1.95', '1538281080639-19390', '2', '2018-09-30', '2018-09-30 12:18:00', '2018-09-30 12:18:00');
INSERT INTO `worker_node` VALUES ('1690', '192.168.1.95', '1538281389078-27767', '2', '2018-09-30', '2018-09-30 12:23:08', '2018-09-30 12:23:08');
INSERT INTO `worker_node` VALUES ('1691', '192.168.1.95', '1538285847688-77491', '2', '2018-09-30', '2018-09-30 13:37:27', '2018-09-30 13:37:27');
INSERT INTO `worker_node` VALUES ('1692', '192.168.1.95', '1538285973775-24486', '2', '2018-09-30', '2018-09-30 13:39:33', '2018-09-30 13:39:33');
INSERT INTO `worker_node` VALUES ('1693', '192.168.1.95', '1538286588397-87194', '2', '2018-09-30', '2018-09-30 13:49:47', '2018-09-30 13:49:47');
INSERT INTO `worker_node` VALUES ('1694', '192.168.1.95', '1538286709653-55843', '2', '2018-09-30', '2018-09-30 13:51:49', '2018-09-30 13:51:49');
INSERT INTO `worker_node` VALUES ('1695', '192.168.1.95', '1538287013406-90384', '2', '2018-09-30', '2018-09-30 13:56:52', '2018-09-30 13:56:52');
INSERT INTO `worker_node` VALUES ('1696', '192.168.1.95', '1538292407473-96989', '2', '2018-09-30', '2018-09-30 15:26:46', '2018-09-30 15:26:46');
INSERT INTO `worker_node` VALUES ('1697', '192.168.1.95', '1538304722364-35577', '2', '2018-09-30', '2018-09-30 18:52:00', '2018-09-30 18:52:00');
INSERT INTO `worker_node` VALUES ('1698', '192.168.1.95', '1538304838838-39997', '2', '2018-09-30', '2018-09-30 18:53:57', '2018-09-30 18:53:57');
INSERT INTO `worker_node` VALUES ('1699', '192.168.1.95', '1538305227249-42097', '2', '2018-09-30', '2018-09-30 19:00:25', '2018-09-30 19:00:25');
INSERT INTO `worker_node` VALUES ('1700', '192.168.1.95', '1538305434056-64018', '2', '2018-09-30', '2018-09-30 19:03:52', '2018-09-30 19:03:52');
INSERT INTO `worker_node` VALUES ('1701', '192.168.1.95', '1538305715712-47178', '2', '2018-09-30', '2018-09-30 19:08:34', '2018-09-30 19:08:34');
INSERT INTO `worker_node` VALUES ('1702', '192.168.1.95', '1538306004421-16453', '2', '2018-09-30', '2018-09-30 19:13:22', '2018-09-30 19:13:22');
INSERT INTO `worker_node` VALUES ('1703', '192.168.1.95', '1538306240815-70219', '2', '2018-09-30', '2018-09-30 19:17:19', '2018-09-30 19:17:19');
INSERT INTO `worker_node` VALUES ('1704', '192.168.1.95', '1538306311365-11969', '2', '2018-09-30', '2018-09-30 19:18:29', '2018-09-30 19:18:29');
INSERT INTO `worker_node` VALUES ('1705', '192.168.1.95', '1538306465350-42074', '2', '2018-09-30', '2018-09-30 19:21:03', '2018-09-30 19:21:03');
INSERT INTO `worker_node` VALUES ('1706', '192.168.1.95', '1538306606411-41456', '2', '2018-09-30', '2018-09-30 19:23:24', '2018-09-30 19:23:24');
INSERT INTO `worker_node` VALUES ('1707', '192.168.1.95', '1538306743890-45678', '2', '2018-09-30', '2018-09-30 19:25:42', '2018-09-30 19:25:42');
INSERT INTO `worker_node` VALUES ('1708', '192.168.1.95', '1538306895549-42980', '2', '2018-09-30', '2018-09-30 19:28:14', '2018-09-30 19:28:14');
INSERT INTO `worker_node` VALUES ('1709', '192.168.1.95', '1538307174319-82148', '2', '2018-09-30', '2018-09-30 19:32:52', '2018-09-30 19:32:52');
INSERT INTO `worker_node` VALUES ('1710', '192.168.1.95', '1538307439555-52799', '2', '2018-09-30', '2018-09-30 19:37:18', '2018-09-30 19:37:18');
INSERT INTO `worker_node` VALUES ('1711', '192.168.1.95', '1538307537188-65189', '2', '2018-09-30', '2018-09-30 19:38:55', '2018-09-30 19:38:55');
INSERT INTO `worker_node` VALUES ('1712', '192.168.1.95', '1538307620380-82613', '2', '2018-09-30', '2018-09-30 19:40:18', '2018-09-30 19:40:18');
INSERT INTO `worker_node` VALUES ('1713', '192.168.1.95', '1538307825842-82854', '2', '2018-09-30', '2018-09-30 19:43:44', '2018-09-30 19:43:44');
INSERT INTO `worker_node` VALUES ('1714', '192.168.1.95', '1538307991988-64461', '2', '2018-09-30', '2018-09-30 19:46:30', '2018-09-30 19:46:30');
INSERT INTO `worker_node` VALUES ('1715', '192.168.1.95', '1538308092792-64337', '2', '2018-09-30', '2018-09-30 19:48:11', '2018-09-30 19:48:11');
INSERT INTO `worker_node` VALUES ('1716', '192.168.1.95', '1538308142293-67028', '2', '2018-09-30', '2018-09-30 19:49:00', '2018-09-30 19:49:00');
INSERT INTO `worker_node` VALUES ('1717', '192.168.1.95', '1538308254264-51639', '2', '2018-09-30', '2018-09-30 19:50:52', '2018-09-30 19:50:52');
INSERT INTO `worker_node` VALUES ('1718', '192.168.1.95', '1538308383134-56027', '2', '2018-09-30', '2018-09-30 19:53:01', '2018-09-30 19:53:01');
INSERT INTO `worker_node` VALUES ('1719', '192.168.1.95', '1538308493330-36441', '2', '2018-09-30', '2018-09-30 19:54:51', '2018-09-30 19:54:51');
INSERT INTO `worker_node` VALUES ('1720', '192.168.1.95', '1538308619493-39935', '2', '2018-09-30', '2018-09-30 19:56:57', '2018-09-30 19:56:57');
INSERT INTO `worker_node` VALUES ('1721', '192.168.1.95', '1538308759948-49080', '2', '2018-09-30', '2018-09-30 19:59:18', '2018-09-30 19:59:18');
INSERT INTO `worker_node` VALUES ('1722', '192.168.1.95', '1538308839613-33395', '2', '2018-09-30', '2018-09-30 20:00:38', '2018-09-30 20:00:38');
INSERT INTO `worker_node` VALUES ('1723', '192.168.1.95', '1538308897604-49021', '2', '2018-09-30', '2018-09-30 20:01:36', '2018-09-30 20:01:36');
INSERT INTO `worker_node` VALUES ('1724', '192.168.1.95', '1538309019302-85248', '2', '2018-09-30', '2018-09-30 20:03:37', '2018-09-30 20:03:37');
INSERT INTO `worker_node` VALUES ('1725', '192.168.1.95', '1538309167870-30519', '2', '2018-09-30', '2018-09-30 20:06:06', '2018-09-30 20:06:06');
INSERT INTO `worker_node` VALUES ('1726', '192.168.1.95', '1538375744365-28016', '2', '2018-10-01', '2018-10-01 14:35:42', '2018-10-01 14:35:42');
INSERT INTO `worker_node` VALUES ('1727', '192.168.1.95', '1538376136078-26848', '2', '2018-10-01', '2018-10-01 14:42:13', '2018-10-01 14:42:13');
INSERT INTO `worker_node` VALUES ('1728', '192.168.1.95', '1538376623840-16158', '2', '2018-10-01', '2018-10-01 14:50:21', '2018-10-01 14:50:21');
INSERT INTO `worker_node` VALUES ('1729', '192.168.1.95', '1538376939208-88424', '2', '2018-10-01', '2018-10-01 14:55:36', '2018-10-01 14:55:36');
INSERT INTO `worker_node` VALUES ('1730', '192.168.1.95', '1538377334364-39850', '2', '2018-10-01', '2018-10-01 15:02:12', '2018-10-01 15:02:12');
INSERT INTO `worker_node` VALUES ('1731', '192.168.1.95', '1538377457081-63249', '2', '2018-10-01', '2018-10-01 15:04:14', '2018-10-01 15:04:14');
INSERT INTO `worker_node` VALUES ('1732', '192.168.1.95', '1538377669063-11328', '2', '2018-10-01', '2018-10-01 15:07:46', '2018-10-01 15:07:46');
INSERT INTO `worker_node` VALUES ('1733', '192.168.1.95', '1538377751927-92791', '2', '2018-10-01', '2018-10-01 15:09:09', '2018-10-01 15:09:09');
INSERT INTO `worker_node` VALUES ('1734', '192.168.1.95', '1538378560545-27111', '2', '2018-10-01', '2018-10-01 15:22:38', '2018-10-01 15:22:38');
INSERT INTO `worker_node` VALUES ('1735', '192.168.1.95', '1538378631976-86377', '2', '2018-10-01', '2018-10-01 15:23:49', '2018-10-01 15:23:49');
INSERT INTO `worker_node` VALUES ('1736', '192.168.1.95', '1538378735661-79456', '2', '2018-10-01', '2018-10-01 15:25:33', '2018-10-01 15:25:33');
INSERT INTO `worker_node` VALUES ('1737', '192.168.1.95', '1538379008521-80651', '2', '2018-10-01', '2018-10-01 15:30:06', '2018-10-01 15:30:06');
INSERT INTO `worker_node` VALUES ('1738', '192.168.1.95', '1538379146755-46983', '2', '2018-10-01', '2018-10-01 15:32:24', '2018-10-01 15:32:24');
INSERT INTO `worker_node` VALUES ('1739', '192.168.1.95', '1538379458421-19104', '2', '2018-10-01', '2018-10-01 15:37:35', '2018-10-01 15:37:35');
INSERT INTO `worker_node` VALUES ('1740', '192.168.1.95', '1538379841310-14313', '2', '2018-10-01', '2018-10-01 15:43:58', '2018-10-01 15:43:58');
INSERT INTO `worker_node` VALUES ('1741', '192.168.1.95', '1538386057167-58766', '2', '2018-10-01', '2018-10-01 17:27:34', '2018-10-01 17:27:34');
INSERT INTO `worker_node` VALUES ('1742', '192.168.1.95', '1538386147843-54965', '2', '2018-10-01', '2018-10-01 17:29:05', '2018-10-01 17:29:05');
INSERT INTO `worker_node` VALUES ('1743', '192.168.1.95', '1538386329865-14691', '2', '2018-10-01', '2018-10-01 17:32:07', '2018-10-01 17:32:07');
INSERT INTO `worker_node` VALUES ('1744', '192.168.1.95', '1538386464731-89878', '2', '2018-10-01', '2018-10-01 17:34:21', '2018-10-01 17:34:21');
INSERT INTO `worker_node` VALUES ('1745', '192.168.1.95', '1538386803607-3113', '2', '2018-10-01', '2018-10-01 17:40:00', '2018-10-01 17:40:00');
INSERT INTO `worker_node` VALUES ('1746', '192.168.1.95', '1538387919807-8416', '2', '2018-10-01', '2018-10-01 17:58:36', '2018-10-01 17:58:36');
INSERT INTO `worker_node` VALUES ('1747', '192.168.1.95', '1538388005443-68413', '2', '2018-10-01', '2018-10-01 18:00:02', '2018-10-01 18:00:02');
INSERT INTO `worker_node` VALUES ('1748', '192.168.1.95', '1538388182451-59172', '2', '2018-10-01', '2018-10-01 18:02:59', '2018-10-01 18:02:59');
INSERT INTO `worker_node` VALUES ('1749', '192.168.1.95', '1538389552814-25062', '2', '2018-10-01', '2018-10-01 18:25:49', '2018-10-01 18:25:49');
INSERT INTO `worker_node` VALUES ('1750', '192.168.1.95', '1538389646818-78189', '2', '2018-10-01', '2018-10-01 18:27:23', '2018-10-01 18:27:23');
INSERT INTO `worker_node` VALUES ('1751', '192.168.1.95', '1538390306631-21877', '2', '2018-10-01', '2018-10-01 18:38:23', '2018-10-01 18:38:23');
INSERT INTO `worker_node` VALUES ('1752', '192.168.1.95', '1538390524556-67512', '2', '2018-10-01', '2018-10-01 18:42:01', '2018-10-01 18:42:01');
INSERT INTO `worker_node` VALUES ('1753', '192.168.1.95', '1538390765069-90281', '2', '2018-10-01', '2018-10-01 18:46:02', '2018-10-01 18:46:02');
INSERT INTO `worker_node` VALUES ('1754', '192.168.1.95', '1538390885893-48125', '2', '2018-10-01', '2018-10-01 18:48:02', '2018-10-01 18:48:02');
INSERT INTO `worker_node` VALUES ('1755', '192.168.1.95', '1538391025585-32078', '2', '2018-10-01', '2018-10-01 18:50:22', '2018-10-01 18:50:22');
INSERT INTO `worker_node` VALUES ('1756', '192.168.1.95', '1538391361967-9281', '2', '2018-10-01', '2018-10-01 18:55:58', '2018-10-01 18:55:58');
INSERT INTO `worker_node` VALUES ('1757', '192.168.1.95', '1538392207653-56771', '2', '2018-10-01', '2018-10-01 19:10:04', '2018-10-01 19:10:04');
INSERT INTO `worker_node` VALUES ('1758', '192.168.1.95', '1538392328778-5604', '2', '2018-10-01', '2018-10-01 19:12:05', '2018-10-01 19:12:05');
INSERT INTO `worker_node` VALUES ('1759', '192.168.1.95', '1538392532659-90414', '2', '2018-10-01', '2018-10-01 19:15:29', '2018-10-01 19:15:29');
INSERT INTO `worker_node` VALUES ('1760', '192.168.1.95', '1538392687747-66039', '2', '2018-10-01', '2018-10-01 19:18:04', '2018-10-01 19:18:04');
INSERT INTO `worker_node` VALUES ('1761', '192.168.1.95', '1538392826071-50493', '2', '2018-10-01', '2018-10-01 19:20:22', '2018-10-01 19:20:22');
INSERT INTO `worker_node` VALUES ('1762', '192.168.1.95', '1538393889884-85706', '2', '2018-10-01', '2018-10-01 19:38:06', '2018-10-01 19:38:06');
INSERT INTO `worker_node` VALUES ('1763', '192.168.1.95', '1538394119545-32446', '2', '2018-10-01', '2018-10-01 19:41:56', '2018-10-01 19:41:56');
INSERT INTO `worker_node` VALUES ('1764', '192.168.1.95', '1538394317613-38592', '2', '2018-10-01', '2018-10-01 19:45:14', '2018-10-01 19:45:14');
INSERT INTO `worker_node` VALUES ('1765', '192.168.1.95', '1538394442868-65026', '2', '2018-10-01', '2018-10-01 19:47:19', '2018-10-01 19:47:19');
INSERT INTO `worker_node` VALUES ('1766', '192.168.1.95', '1538395168430-38942', '2', '2018-10-01', '2018-10-01 19:59:25', '2018-10-01 19:59:25');
INSERT INTO `worker_node` VALUES ('1767', '192.168.1.95', '1538395245249-86855', '2', '2018-10-01', '2018-10-01 20:00:42', '2018-10-01 20:00:42');
INSERT INTO `worker_node` VALUES ('1768', '192.168.1.95', '1538395473804-66321', '2', '2018-10-01', '2018-10-01 20:04:30', '2018-10-01 20:04:30');
INSERT INTO `worker_node` VALUES ('1769', '192.168.1.95', '1538396140971-11477', '2', '2018-10-01', '2018-10-01 20:15:37', '2018-10-01 20:15:37');
INSERT INTO `worker_node` VALUES ('1770', '192.168.1.95', '1538396277635-96358', '2', '2018-10-01', '2018-10-01 20:17:54', '2018-10-01 20:17:54');
INSERT INTO `worker_node` VALUES ('1771', '192.168.1.182', '1538461058941-81984', '2', '2018-10-02', '2018-10-02 14:17:35', '2018-10-02 14:17:35');
INSERT INTO `worker_node` VALUES ('1772', '192.168.1.182', '1538463533253-28137', '2', '2018-10-02', '2018-10-02 14:58:53', '2018-10-02 14:58:53');
INSERT INTO `worker_node` VALUES ('1773', '192.168.1.182', '1538463913369-77982', '2', '2018-10-02', '2018-10-02 15:05:13', '2018-10-02 15:05:13');
INSERT INTO `worker_node` VALUES ('1774', '192.168.1.182', '1538533646249-73202', '2', '2018-10-03', '2018-10-03 10:27:26', '2018-10-03 10:27:26');
INSERT INTO `worker_node` VALUES ('1775', '192.168.1.182', '1538535041737-91985', '2', '2018-10-03', '2018-10-03 10:50:42', '2018-10-03 10:50:42');
INSERT INTO `worker_node` VALUES ('1776', '192.168.1.182', '1538535566656-17876', '2', '2018-10-03', '2018-10-03 10:59:26', '2018-10-03 10:59:26');
INSERT INTO `worker_node` VALUES ('1777', '192.168.1.182', '1538538662787-41236', '2', '2018-10-03', '2018-10-03 11:51:03', '2018-10-03 11:51:03');
INSERT INTO `worker_node` VALUES ('1778', '192.168.1.182', '1538538747336-10190', '2', '2018-10-03', '2018-10-03 11:52:27', '2018-10-03 11:52:27');
INSERT INTO `worker_node` VALUES ('1779', '192.168.1.182', '1538540086052-91049', '2', '2018-10-03', '2018-10-03 12:14:46', '2018-10-03 12:14:46');
INSERT INTO `worker_node` VALUES ('1780', '192.168.1.182', '1538541755253-25838', '2', '2018-10-03', '2018-10-03 12:42:35', '2018-10-03 12:42:35');
INSERT INTO `worker_node` VALUES ('1781', '192.168.1.182', '1538547436310-67109', '2', '2018-10-03', '2018-10-03 14:17:16', '2018-10-03 14:17:16');
INSERT INTO `worker_node` VALUES ('1782', '192.168.1.182', '1538553637203-18596', '2', '2018-10-03', '2018-10-03 16:00:37', '2018-10-03 16:00:37');
INSERT INTO `worker_node` VALUES ('1783', '192.168.1.182', '1538616884619-57937', '2', '2018-10-04', '2018-10-04 09:34:44', '2018-10-04 09:34:44');
INSERT INTO `worker_node` VALUES ('1784', '192.168.1.182', '1538640200971-85311', '2', '2018-10-04', '2018-10-04 16:03:20', '2018-10-04 16:03:20');
INSERT INTO `worker_node` VALUES ('1785', '192.168.1.182', '1538712748800-9234', '2', '2018-10-05', '2018-10-05 12:12:28', '2018-10-05 12:12:28');
INSERT INTO `worker_node` VALUES ('1786', '192.168.1.182', '1538732970552-37310', '2', '2018-10-05', '2018-10-05 17:49:30', '2018-10-05 17:49:30');
INSERT INTO `worker_node` VALUES ('1787', '192.168.1.182', '1538884253365-1198', '2', '2018-10-07', '2018-10-07 11:50:51', '2018-10-07 11:50:51');
INSERT INTO `worker_node` VALUES ('1788', '192.168.1.182', '1538978598053-22347', '2', '2018-10-08', '2018-10-08 14:03:15', '2018-10-08 14:03:15');
INSERT INTO `worker_node` VALUES ('1789', '192.168.1.182', '1538988565275-58745', '2', '2018-10-08', '2018-10-08 16:49:22', '2018-10-08 16:49:22');
INSERT INTO `worker_node` VALUES ('1790', '192.168.1.95', '1539075337745-18305', '2', '2018-10-09', '2018-10-09 16:55:34', '2018-10-09 16:55:34');
INSERT INTO `worker_node` VALUES ('1791', '192.168.1.95', '1539076475194-60143', '2', '2018-10-09', '2018-10-09 17:14:32', '2018-10-09 17:14:32');
INSERT INTO `worker_node` VALUES ('1792', '192.168.1.95', '1539076551997-64135', '2', '2018-10-09', '2018-10-09 17:15:48', '2018-10-09 17:15:48');
INSERT INTO `worker_node` VALUES ('1793', '192.168.1.95', '1539076794295-25081', '2', '2018-10-09', '2018-10-09 17:19:51', '2018-10-09 17:19:51');
INSERT INTO `worker_node` VALUES ('1794', '192.168.1.95', '1539084537963-99630', '2', '2018-10-09', '2018-10-09 19:28:54', '2018-10-09 19:28:54');
INSERT INTO `worker_node` VALUES ('1795', '192.168.1.95', '1539085109958-40307', '2', '2018-10-09', '2018-10-09 19:38:26', '2018-10-09 19:38:26');
INSERT INTO `worker_node` VALUES ('1796', '192.168.1.95', '1539085586298-40581', '2', '2018-10-09', '2018-10-09 19:46:22', '2018-10-09 19:46:22');
INSERT INTO `worker_node` VALUES ('1797', '192.168.1.95', '1539085818970-65614', '2', '2018-10-09', '2018-10-09 19:50:15', '2018-10-09 19:50:15');
INSERT INTO `worker_node` VALUES ('1798', '192.168.1.95', '1539086226990-12301', '2', '2018-10-09', '2018-10-09 19:57:03', '2018-10-09 19:57:03');
INSERT INTO `worker_node` VALUES ('1799', '192.168.1.95', '1539086284604-90647', '2', '2018-10-09', '2018-10-09 19:58:01', '2018-10-09 19:58:01');
INSERT INTO `worker_node` VALUES ('1800', '192.168.1.95', '1539086360427-80570', '2', '2018-10-09', '2018-10-09 19:59:16', '2018-10-09 19:59:16');
INSERT INTO `worker_node` VALUES ('1801', '192.168.1.95', '1539086453399-71718', '2', '2018-10-09', '2018-10-09 20:00:49', '2018-10-09 20:00:49');
INSERT INTO `worker_node` VALUES ('1802', '192.168.1.95', '1539090327319-29631', '2', '2018-10-09', '2018-10-09 21:05:23', '2018-10-09 21:05:23');
INSERT INTO `worker_node` VALUES ('1803', '192.168.1.95', '1539091053483-36937', '2', '2018-10-09', '2018-10-09 21:17:29', '2018-10-09 21:17:29');
INSERT INTO `worker_node` VALUES ('1804', '192.168.1.95', '1539091169654-76257', '2', '2018-10-09', '2018-10-09 21:19:25', '2018-10-09 21:19:25');
INSERT INTO `worker_node` VALUES ('1805', '192.168.1.95', '1539091299205-67846', '2', '2018-10-09', '2018-10-09 21:21:35', '2018-10-09 21:21:35');
INSERT INTO `worker_node` VALUES ('1806', '192.168.1.95', '1539091389132-21396', '2', '2018-10-09', '2018-10-09 21:23:05', '2018-10-09 21:23:05');
INSERT INTO `worker_node` VALUES ('1807', '192.168.1.95', '1539091449240-1610', '2', '2018-10-09', '2018-10-09 21:24:05', '2018-10-09 21:24:05');
INSERT INTO `worker_node` VALUES ('1808', '192.168.1.95', '1539091515698-20869', '2', '2018-10-09', '2018-10-09 21:25:11', '2018-10-09 21:25:11');
INSERT INTO `worker_node` VALUES ('1809', '192.168.1.95', '1539091624640-47189', '2', '2018-10-09', '2018-10-09 21:27:00', '2018-10-09 21:27:00');
INSERT INTO `worker_node` VALUES ('1810', '192.168.1.95', '1539092053026-61875', '2', '2018-10-09', '2018-10-09 21:34:09', '2018-10-09 21:34:09');
INSERT INTO `worker_node` VALUES ('1811', '192.168.1.95', '1539092322047-82597', '2', '2018-10-09', '2018-10-09 21:38:38', '2018-10-09 21:38:38');
INSERT INTO `worker_node` VALUES ('1812', '192.168.1.95', '1539092536851-69456', '2', '2018-10-09', '2018-10-09 21:42:13', '2018-10-09 21:42:13');
INSERT INTO `worker_node` VALUES ('1813', '192.168.1.95', '1539092669255-50894', '2', '2018-10-09', '2018-10-09 21:44:25', '2018-10-09 21:44:25');
INSERT INTO `worker_node` VALUES ('1814', '192.168.1.95', '1539092849696-24073', '2', '2018-10-09', '2018-10-09 21:47:25', '2018-10-09 21:47:25');
INSERT INTO `worker_node` VALUES ('1815', '192.168.1.95', '1539092920691-59779', '2', '2018-10-09', '2018-10-09 21:48:36', '2018-10-09 21:48:36');
INSERT INTO `worker_node` VALUES ('1816', '192.168.1.95', '1539093097864-44494', '2', '2018-10-09', '2018-10-09 21:51:34', '2018-10-09 21:51:34');
INSERT INTO `worker_node` VALUES ('1817', '192.168.1.95', '1539094233088-15908', '2', '2018-10-09', '2018-10-09 22:10:29', '2018-10-09 22:10:29');
INSERT INTO `worker_node` VALUES ('1818', '192.168.1.95', '1539250311486-33253', '2', '2018-10-11', '2018-10-11 17:31:46', '2018-10-11 17:31:46');
INSERT INTO `worker_node` VALUES ('1819', '192.168.1.95', '1539250477860-44259', '2', '2018-10-11', '2018-10-11 17:34:33', '2018-10-11 17:34:33');
INSERT INTO `worker_node` VALUES ('1820', '192.168.1.95', '1539251188120-92126', '2', '2018-10-11', '2018-10-11 17:46:23', '2018-10-11 17:46:23');
INSERT INTO `worker_node` VALUES ('1821', '192.168.1.95', '1539251477292-75154', '2', '2018-10-11', '2018-10-11 17:51:12', '2018-10-11 17:51:12');
INSERT INTO `worker_node` VALUES ('1822', '192.168.1.95', '1539251657369-75185', '2', '2018-10-11', '2018-10-11 17:54:12', '2018-10-11 17:54:12');
INSERT INTO `worker_node` VALUES ('1823', '192.168.1.95', '1539251782789-89864', '2', '2018-10-11', '2018-10-11 17:56:18', '2018-10-11 17:56:18');
INSERT INTO `worker_node` VALUES ('1824', '192.168.1.95', '1539251894993-29076', '2', '2018-10-11', '2018-10-11 17:58:10', '2018-10-11 17:58:10');
INSERT INTO `worker_node` VALUES ('1825', '192.168.1.95', '1539252454364-80417', '2', '2018-10-11', '2018-10-11 18:07:29', '2018-10-11 18:07:29');
INSERT INTO `worker_node` VALUES ('1826', '192.168.1.95', '1539252864505-77846', '2', '2018-10-11', '2018-10-11 18:14:19', '2018-10-11 18:14:19');
INSERT INTO `worker_node` VALUES ('1827', '192.168.1.95', '1539253020482-14147', '2', '2018-10-11', '2018-10-11 18:16:55', '2018-10-11 18:16:55');
INSERT INTO `worker_node` VALUES ('1828', '192.168.1.95', '1539259269199-88644', '2', '2018-10-11', '2018-10-11 20:01:04', '2018-10-11 20:01:04');
INSERT INTO `worker_node` VALUES ('1829', '192.168.1.95', '1539259583436-83015', '2', '2018-10-11', '2018-10-11 20:06:18', '2018-10-11 20:06:18');
INSERT INTO `worker_node` VALUES ('1830', '192.168.1.95', '1539259644637-49272', '2', '2018-10-11', '2018-10-11 20:07:19', '2018-10-11 20:07:19');
INSERT INTO `worker_node` VALUES ('1831', '192.168.1.95', '1539259782734-92634', '2', '2018-10-11', '2018-10-11 20:09:37', '2018-10-11 20:09:37');
INSERT INTO `worker_node` VALUES ('1832', '192.168.1.95', '1539260034625-19218', '2', '2018-10-11', '2018-10-11 20:13:49', '2018-10-11 20:13:49');
INSERT INTO `worker_node` VALUES ('1833', '192.168.1.95', '1539260118597-62688', '2', '2018-10-11', '2018-10-11 20:15:13', '2018-10-11 20:15:13');
INSERT INTO `worker_node` VALUES ('1834', '192.168.1.95', '1539260188762-43976', '2', '2018-10-11', '2018-10-11 20:16:23', '2018-10-11 20:16:23');
INSERT INTO `worker_node` VALUES ('1835', '192.168.1.95', '1539260291133-75148', '2', '2018-10-11', '2018-10-11 20:18:06', '2018-10-11 20:18:06');
INSERT INTO `worker_node` VALUES ('1836', '192.168.1.95', '1539260736546-88757', '2', '2018-10-11', '2018-10-11 20:25:31', '2018-10-11 20:25:31');
INSERT INTO `worker_node` VALUES ('1837', '192.168.1.95', '1539260887404-37363', '2', '2018-10-11', '2018-10-11 20:28:02', '2018-10-11 20:28:02');
INSERT INTO `worker_node` VALUES ('1838', '192.168.1.95', '1539261015459-33569', '2', '2018-10-11', '2018-10-11 20:30:10', '2018-10-11 20:30:10');
INSERT INTO `worker_node` VALUES ('1839', '192.168.1.95', '1539261860045-5368', '2', '2018-10-11', '2018-10-11 20:44:14', '2018-10-11 20:44:14');
INSERT INTO `worker_node` VALUES ('1840', '192.168.1.95', '1539261946831-13860', '2', '2018-10-11', '2018-10-11 20:45:41', '2018-10-11 20:45:41');
INSERT INTO `worker_node` VALUES ('1841', '192.168.1.95', '1539311834760-90643', '2', '2018-10-12', '2018-10-12 10:37:12', '2018-10-12 10:37:12');
INSERT INTO `worker_node` VALUES ('1842', '192.168.1.95', '1539314360777-46684', '2', '2018-10-12', '2018-10-12 11:19:18', '2018-10-12 11:19:18');
INSERT INTO `worker_node` VALUES ('1843', '192.168.1.95', '1539314717103-97902', '2', '2018-10-12', '2018-10-12 11:25:14', '2018-10-12 11:25:14');
INSERT INTO `worker_node` VALUES ('1844', '192.168.1.95', '1539314901311-57378', '2', '2018-10-12', '2018-10-12 11:28:18', '2018-10-12 11:28:18');
INSERT INTO `worker_node` VALUES ('1845', '192.168.1.95', '1539315037654-4541', '2', '2018-10-12', '2018-10-12 11:30:35', '2018-10-12 11:30:35');
INSERT INTO `worker_node` VALUES ('1846', '192.168.1.95', '1539317002740-94167', '2', '2018-10-12', '2018-10-12 12:03:20', '2018-10-12 12:03:20');
INSERT INTO `worker_node` VALUES ('1847', '192.168.1.95', '1539333495286-24043', '2', '2018-10-12', '2018-10-12 16:38:11', '2018-10-12 16:38:11');
INSERT INTO `worker_node` VALUES ('1848', '192.168.1.95', '1539588423338-87177', '2', '2018-10-15', '2018-10-15 15:27:02', '2018-10-15 15:27:02');
INSERT INTO `worker_node` VALUES ('1849', '192.168.1.95', '1539590449557-94997', '2', '2018-10-15', '2018-10-15 16:00:48', '2018-10-15 16:00:48');
INSERT INTO `worker_node` VALUES ('1850', '192.168.1.95', '1539593178158-95507', '2', '2018-10-15', '2018-10-15 16:46:16', '2018-10-15 16:46:16');
INSERT INTO `worker_node` VALUES ('1851', '192.168.1.95', '1539595195160-28387', '2', '2018-10-15', '2018-10-15 17:19:53', '2018-10-15 17:19:53');
INSERT INTO `worker_node` VALUES ('1852', '192.168.1.95', '1539595822790-14743', '2', '2018-10-15', '2018-10-15 17:30:21', '2018-10-15 17:30:21');
INSERT INTO `worker_node` VALUES ('1853', '192.168.1.95', '1539609741637-82555', '2', '2018-10-15', '2018-10-15 21:22:19', '2018-10-15 21:22:19');
INSERT INTO `worker_node` VALUES ('1854', '192.168.1.95', '1539610123499-87822', '2', '2018-10-15', '2018-10-15 21:28:41', '2018-10-15 21:28:41');
INSERT INTO `worker_node` VALUES ('1855', '192.168.1.95', '1539610440164-20923', '2', '2018-10-15', '2018-10-15 21:33:58', '2018-10-15 21:33:58');
INSERT INTO `worker_node` VALUES ('1856', '192.168.1.95', '1539610606133-35268', '2', '2018-10-15', '2018-10-15 21:36:44', '2018-10-15 21:36:44');
INSERT INTO `worker_node` VALUES ('1857', '192.168.1.95', '1539610688588-36862', '2', '2018-10-15', '2018-10-15 21:38:06', '2018-10-15 21:38:06');
INSERT INTO `worker_node` VALUES ('1858', '192.168.1.95', '1539610772422-22910', '2', '2018-10-15', '2018-10-15 21:39:30', '2018-10-15 21:39:30');
INSERT INTO `worker_node` VALUES ('1859', '192.168.1.95', '1539653058506-11803', '2', '2018-10-16', '2018-10-16 09:24:17', '2018-10-16 09:24:17');
INSERT INTO `worker_node` VALUES ('1860', '192.168.1.182', '1539653827244-25670', '2', '2018-10-16', '2018-10-16 09:37:03', '2018-10-16 09:37:03');
INSERT INTO `worker_node` VALUES ('1861', '192.168.1.95', '1539673625418-34350', '2', '2018-10-16', '2018-10-16 15:07:03', '2018-10-16 15:07:03');
INSERT INTO `worker_node` VALUES ('1862', '192.168.1.182', '1539673738748-70405', '2', '2018-10-16', '2018-10-16 15:08:55', '2018-10-16 15:08:55');
INSERT INTO `worker_node` VALUES ('1863', '192.168.1.95', '1539673910230-30978', '2', '2018-10-16', '2018-10-16 15:11:48', '2018-10-16 15:11:48');
INSERT INTO `worker_node` VALUES ('1864', '192.168.1.95', '1539674236639-41528', '2', '2018-10-16', '2018-10-16 15:17:14', '2018-10-16 15:17:14');
INSERT INTO `worker_node` VALUES ('1865', '192.168.1.95', '1539674331928-94096', '2', '2018-10-16', '2018-10-16 15:18:50', '2018-10-16 15:18:50');
INSERT INTO `worker_node` VALUES ('1866', '192.168.1.95', '1539674374223-47893', '2', '2018-10-16', '2018-10-16 15:19:32', '2018-10-16 15:19:32');
INSERT INTO `worker_node` VALUES ('1867', '192.168.1.95', '1539674812707-6571', '2', '2018-10-16', '2018-10-16 15:26:50', '2018-10-16 15:26:50');
INSERT INTO `worker_node` VALUES ('1868', '192.168.1.182', '1539676034802-66926', '2', '2018-10-16', '2018-10-16 15:47:11', '2018-10-16 15:47:11');
INSERT INTO `worker_node` VALUES ('1869', '192.168.1.182', '1539742510770-45297', '2', '2018-10-17', '2018-10-17 10:15:10', '2018-10-17 10:15:10');
INSERT INTO `worker_node` VALUES ('1870', '192.168.1.182', '1539827912698-3759', '2', '2018-10-18', '2018-10-18 09:58:31', '2018-10-18 09:58:31');
INSERT INTO `worker_node` VALUES ('1871', '192.168.1.182', '1539830870613-27501', '2', '2018-10-18', '2018-10-18 10:47:49', '2018-10-18 10:47:49');
INSERT INTO `worker_node` VALUES ('1872', '192.168.1.182', '1539915036642-40149', '2', '2018-10-19', '2018-10-19 10:10:35', '2018-10-19 10:10:35');
INSERT INTO `worker_node` VALUES ('1873', '192.168.1.182', '1540092545124-58726', '2', '2018-10-21', '2018-10-21 11:29:05', '2018-10-21 11:29:05');
INSERT INTO `worker_node` VALUES ('1874', '172.17.0.11', '1540197603563-17692', '2', '2018-10-22', '2018-10-22 16:40:03', '2018-10-22 16:40:03');
INSERT INTO `worker_node` VALUES ('1875', '192.168.1.182', '1540200019953-39213', '2', '2018-10-22', '2018-10-22 17:20:20', '2018-10-22 17:20:20');
INSERT INTO `worker_node` VALUES ('1876', '192.168.1.182', '1540277533117-36769', '2', '2018-10-23', '2018-10-23 14:52:14', '2018-10-23 14:52:14');
