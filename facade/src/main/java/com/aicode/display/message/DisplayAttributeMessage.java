/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.display.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * display 消息体针对 测试业务
 */
@Data
public class DisplayAttributeMessage implements java.io.Serializable {

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private java.lang.Long id;
    /**
     * 类模型编码
     */
    @ApiModelProperty(value = "类模型编码")
    private java.lang.String mapClassTableCode;
    /**
     * 字段编码
     */
    @ApiModelProperty(value = "字段编码")
    private java.lang.String mapFieldColumnCode;
    /**
     * 是否必填 Y,N
     */
    @ApiModelProperty(value = "是否必填 Y,N")
    private java.lang.String isRequired;
    /**
     * 是否插入
     */
    @ApiModelProperty(value = "是否插入")
    private java.lang.String isInsert;
    /**
     * 是否是删除条件
     */
    @ApiModelProperty(value = "是否是删除条件")
    private java.lang.String isDeleteCondition;
    /**
     * 是否允许修改 Y,N
     */
    @ApiModelProperty(value = "是否允许修改 Y,N")
    private java.lang.String isAllowUpdate;
    /**
     * 是否分页列表显示 Y,N
     */
    @ApiModelProperty(value = "是否分页列表显示 Y,N")
    private java.lang.String isListPageDisplay;
    /**
     * 是否详情页显示 Y,N
     */
    @ApiModelProperty(value = "是否详情页显示 Y,N")
    private java.lang.String isDetailPageDisplay;
    /**
     * 是否是查询条件 Y,N
     */
    @ApiModelProperty(value = "是否是查询条件 Y,N")
    private java.lang.String isQueryRequired;
    /**
     * 是否换行
     */
    @ApiModelProperty(value = "是否换行")
    private java.lang.String isLineNew;
    /**
     * 匹配方式 =,!=,>=,<=,>,<,like,左like，右like,between,in
     */
    @ApiModelProperty(value = "匹配方式 =,!=,>=,<=,>,<,like,左like，右like,between,in")
    private java.lang.String matchType;
    /**
     * Autocomplete 自动完成, Cascader 级联选择 , DatePicker 日期选择框 , TimePicker 时间选择框 ,Input 单行文本框 , Textarea 多行文本框 , InputNumber 数字输入框 , Mobile 手机 , Phone 固话 , MobileOrPhone 手机或固话 ,Password 密码 , Email 邮箱 , Website 网址 , IdCard 身份证 , Mention 提及(@) ,Select 单项选择器 , MultiSelect 多项选择器 , Radio 单选按钮 , Checkbox 复选框 , Rate 评分 , Silder 滑动条 ,Switch 开关按钮 , TreeSelect 选择树 , Upload 上传 
     */
    @ApiModelProperty(value = "Autocomplete 自动完成, Cascader 级联选择 , DatePicker 日期选择框 , TimePicker 时间选择框 ,Input 单行文本框 , Textarea 多行文本框 , InputNumber 数字输入框 , Mobile 手机 , Phone 固话 , MobileOrPhone 手机或固话 ,Password 密码 , Email 邮箱 , Website 网址 , IdCard 身份证 , Mention 提及(@) ,Select 单项选择器 , MultiSelect 多项选择器 , Radio 单选按钮 , Checkbox 复选框 , Rate 评分 , Silder 滑动条 ,Switch 开关按钮 , TreeSelect 选择树 , Upload 上传 ")
    private java.lang.String displayType;
    /**
     * 显示列名称
     */
    @ApiModelProperty(value = "显示列名称")
    private java.lang.String displayName;
    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序")
    private java.lang.Integer displayNo;
    /**
     * Email 邮件, Address 地址, Telephone 电话 , Password 密码, Date  日期 , Number 数值 , Integer 整数 , Positive_Integer 正整数 , text 文本 ,IdCard 身份证 , Website 网址
     */
    @ApiModelProperty(value = "Email 邮件, Address 地址, Telephone 电话 , Password 密码, Date  日期 , Number 数值 , Integer 整数 , Positive_Integer 正整数 , text 文本 ,IdCard 身份证 , Website 网址")
    private java.lang.String fieldValidationMode;
    /**
     * 验证提示语
     */
    @ApiModelProperty(value = "验证提示语")
    private java.lang.String validateText;
    /**
     * 显示css样式
     */
    @ApiModelProperty(value = "显示css样式")
    private java.lang.String displayCss;

}
