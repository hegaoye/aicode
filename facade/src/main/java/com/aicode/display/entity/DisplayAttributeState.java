/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.display.entity;

/**
 * 显示属性 的实体类的状态
 *
 * @author hegaoye
 */
public enum DisplayAttributeState implements java.io.Serializable {
    Y("是否必填"),
    Like("右"),
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
    Integer("整数"),
    Positive_Integer("正整数"),
    Address("地址"),
    Telephone("电话"),
    Number("数值"),
    Text("文本"),
    Css("显示"),
    ;

    public String val;

    DisplayAttributeState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static DisplayAttributeState getEnum(String stateName) {
        for (DisplayAttributeState displayAttributeState : DisplayAttributeState.values()) {
            if (displayAttributeState.name().equalsIgnoreCase(stateName)) {
                return displayAttributeState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
