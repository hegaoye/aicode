/*
* aicode
 */
package com.aicode.display.entity;

/**
 * Email 邮件, Address 地址, Telephone 电话 , Password 密码, Date  日期 , Number 数值 , Integer 整数 , Positive_Integer 正整数 , text 文本 ,IdCard 身份证 , Website 网址 的实体类的状态
 *
 * @author aicode
 */
public enum DisplayAttributeFieldValidationMode implements java.io.Serializable {
    Integer("整数"),
    Email("邮件"),
    Positive_Integer("正整数"),
    Address("地址"),
    Telephone("电话"),
    Number("数值"),
    IdCard("身份证"),
    Website("网址"),
    Text("文本"),
    Password("密码"),
    ;

    public String val;

    DisplayAttributeFieldValidationMode(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static DisplayAttributeFieldValidationMode getEnum(String stateName) {
        for (DisplayAttributeFieldValidationMode enumName : DisplayAttributeFieldValidationMode.values()) {
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
