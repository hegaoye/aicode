/*
 * Powered By [lixin]
 *
 */

package io.aicode.display.entity;

/**
 * 字段验证方式 email 邮件, Address 地址, Telephone 电话 , Password 密码,
 * Date  日期 , Number 数值 , Integer 整数 , Positive_Integer 正整数 , text 文本 ,
 * IdCard 身份证 , Website 网址
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public enum DisplayFieldValidationEnum implements java.io.Serializable {
    Email("邮件"), Address("地址"), Telephone("电话"), Password("密码"),
    Date("日期"), Number("数值"), Integer("整数"), Positive_Integer("正整数"), text("文本"),
    IdCard("身份证"), Website("网址"),
    ;
    public String val;

    DisplayFieldValidationEnum(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static DisplayFieldValidationEnum getEnum(String stateName) {
        for (DisplayFieldValidationEnum projectStateEnum : DisplayFieldValidationEnum.values()) {
            if (projectStateEnum.name().equalsIgnoreCase(stateName)) {
                return projectStateEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }
}

