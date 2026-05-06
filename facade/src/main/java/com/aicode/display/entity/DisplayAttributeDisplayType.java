/*
* aicode
 */
package com.aicode.display.entity;

/**
 * Autocomplete 自动完成, Cascader 级联选择 , DatePicker 日期选择框 , TimePicker 时间选择框 ,Input 单行文本框 , Textarea 多行文本框 , InputNumber 数字输入框 , Mobile 手机 , Phone 固话 , MobileOrPhone 手机或固话 ,Password 密码 , Email 邮箱 , Website 网址 , IdCard 身份证 , Mention 提及(@) ,Select 单项选择器 , MultiSelect 多项选择器 , Radio 单选按钮 , Checkbox 复选框 , Rate 评分 , Silder 滑动条 ,Switch 开关按钮 , TreeSelect 选择树 , Upload 上传  的实体类的状态
 *
 * @author aicode
 */
public enum DisplayAttributeDisplayType implements java.io.Serializable {
    TimePicker("时间选择框"),
    MultiSelect("多项选择器"),
    InputNumber("数字输入框"),
    Email("邮箱"),
    Rate("评分"),
    Silder("滑动条"),
    Website("网址"),
    Upload("上传"),
    Radio("单选按钮"),
    Mobile("手机"),
    Autocomplete("自动完成"),
    Input("单行文本框"),
    Textarea("多行文本框"),
    Switch("开关按钮"),
    Phone("固话"),
    Checkbox("复选框"),
    Cascader("级联选择"),
    MobileOrPhone("手机或固话"),
    Mention("提及"),
    Select("单项选择器"),
    TreeSelect("选择树"),
    IdCard("身份证"),
    DatePicker("日期选择框"),
    Password("密码"),
    ;

    public String val;

    DisplayAttributeDisplayType(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static DisplayAttributeDisplayType getEnum(String stateName) {
        for (DisplayAttributeDisplayType enumName : DisplayAttributeDisplayType.values()) {
            if (enumName.name().equalsIgnoreCase(stateName)) {
                return enumName;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
