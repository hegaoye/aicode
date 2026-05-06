/**
 * aicode
 */
package com.aicode.display.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保存 显示属性 VO
 *
 * @author aicode
 */
@Data
public class DisplayAttributeSaveDTO implements java.io.Serializable {
    @Schema(description = "数据库字段:id ")
    private Long id;
    @Schema(description = "数据库字段:mapClassTableCode 类模型编码")
    private String mapClassTableCode;
    @Schema(description = "数据库字段:mapFieldColumnCode 字段编码")
    private String mapFieldColumnCode;
    @Schema(description = "数据库字段:isRequired 是否必填 Y,N")
    private String isRequired;
    @Schema(description = "数据库字段:isInsert 是否插入")
    private String isInsert;
    @Schema(description = "数据库字段:isDeleteCondition 是否是删除条件")
    private String isDeleteCondition;
    @Schema(description = "数据库字段:isAllowUpdate 是否允许修改 Y,N")
    private String isAllowUpdate;
    @Schema(description = "数据库字段:isListPageDisplay 是否分页列表显示 Y,N")
    private String isListPageDisplay;
    @Schema(description = "数据库字段:isDetailPageDisplay 是否详情页显示 Y,N")
    private String isDetailPageDisplay;
    @Schema(description = "数据库字段:isQueryRequired 是否是查询条件 Y,N")
    private String isQueryRequired;
    @Schema(description = "数据库字段:isLineNew 是否换行")
    private String isLineNew;
    @Schema(description = "数据库字段:matchType 匹配方式 =,!=,>=,<=,>,<,like,左like，右like,between,in")
    private String matchType;
    @Schema(description = "数据库字段:displayType Autocomplete 自动完成, Cascader 级联选择 , DatePicker 日期选择框 , TimePicker 时间选择框 ,Input 单行文本框 , Textarea 多行文本框 , InputNumber 数字输入框 , Mobile 手机 , Phone 固话 , MobileOrPhone 手机或固话 ,Password 密码 , Email 邮箱 , Website 网址 , IdCard 身份证 , Mention 提及(@) ,Select 单项选择器 , MultiSelect 多项选择器 , Radio 单选按钮 , Checkbox 复选框 , Rate 评分 , Silder 滑动条 ,Switch 开关按钮 , TreeSelect 选择树 , Upload 上传 ")
    private String displayType;
    @Schema(description = "数据库字段:displayName 显示列名称")
    private String displayName;
    @Schema(description = "数据库字段:displayNo 显示顺序")
    private Integer displayNo;
    @Schema(description = "数据库字段:fieldValidationMode Email 邮件, Address 地址, Telephone 电话 , Password 密码, Date  日期 , Number 数值 , Integer 整数 , Positive_Integer 正整数 , text 文本 ,IdCard 身份证 , Website 网址")
    private String fieldValidationMode;
    @Schema(description = "数据库字段:validateText 验证提示语")
    private String validateText;
    @Schema(description = "数据库字段:displayCss 显示css样式")
    private String displayCss;
}
