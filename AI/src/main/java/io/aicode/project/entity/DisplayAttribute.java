/*
 *  *
 *                       http://www.aicode.io
 *
 *
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
