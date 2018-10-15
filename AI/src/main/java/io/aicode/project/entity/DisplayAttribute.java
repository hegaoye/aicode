/*
 *  * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */
package io.aicode.project.entity;

import io.aicode.core.base.BaseEntity;
import lombok.Data;

/**
 * 显示属性
 * @author berton
 */
@Data
public class DisplayAttribute extends BaseEntity implements java.io.Serializable {

	/**
	 * 数据库字段:mapFieldColumnCode  属性显示:字段编码
	 */
	private String mapFieldColumnCode;
	/**
	 * 数据库字段:isRequired  属性显示:是否必填 Y,N
	 */
	private String isRequired;
	/**
	 * 数据库字段:isAllowUpdate  属性显示:是否允许修改 Y,N
	 */
	private String isAllowUpdate;
	/**
	 * 数据库字段:isListPageDisplay  属性显示:是否分页列表显示 Y,N
	 */
	private String isListPageDisplay;
	/**
	 * 数据库字段:isDetailPageDisplay  属性显示:是否详情页显示 Y,N
	 */
	private String isDetailPageDisplay;
	/**
	 * 数据库字段:isQueryRequired  属性显示:是否是查询条件 Y,N
	 */
	private String isQueryRequired;
	/**
	 * 数据库字段:displayType  属性显示:显示格式 自动完成 Autocomplete,级联选择 Cascader,日期选择框 DatePicker,时间选择 TimePicker,输入框 Input,数字输入框 InputNumber,提及 Mention,邮箱 Email，电话Phone，手机Mobile，备注说明 Summary，选择器 Select，单选 Radio，多选框 Checkbox,评分 Rate,加载展位图 Skeleton,滑动输入条 Silder，开关 Switch,穿梭框 Transfer,选择树 TreeSelect ,上传 Upload,头像 Avatar
	 */
	private String displayType;
}
