/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于龐帝業務系统.
 */

/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.jobgui.entity;

import com.rzhkj.core.base.BaseEntity;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */


public class QrtzSimpropTriggers extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    //alias
    public static final String TABLE_ALIAS = "QrtzSimpropTriggers";
    public static final String ALIAS_SCHED_NAME = "schedName";
    public static final String ALIAS_TRIGGER_NAME = "triggerName";
    public static final String ALIAS_TRIGGER_GROUP = "triggerGroup";
    public static final String ALIAS_STR_PROP1 = "strProp1";
    public static final String ALIAS_STR_PROP2 = "strProp2";
    public static final String ALIAS_STR_PROP3 = "strProp3";
    public static final String ALIAS_INT_PROP1 = "intProp1";
    public static final String ALIAS_INT_PROP2 = "intProp2";
    public static final String ALIAS_LONG_PROP1 = "longProp1";
    public static final String ALIAS_LONG_PROP2 = "longProp2";
    public static final String ALIAS_DEC_PROP1 = "decProp1";
    public static final String ALIAS_DEC_PROP2 = "decProp2";
    public static final String ALIAS_BOOL_PROP1 = "boolProp1";
    public static final String ALIAS_BOOL_PROP2 = "boolProp2";

    //date formats

    //可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    private String schedName;//数据库字段:SCHED_NAME  属性显示:schedName
    private String triggerName;//数据库字段:TRIGGER_NAME  属性显示:triggerName
    private String triggerGroup;//数据库字段:TRIGGER_GROUP  属性显示:triggerGroup
    private String strProp1;//数据库字段:STR_PROP_1  属性显示:strProp1
    private String strProp2;//数据库字段:STR_PROP_2  属性显示:strProp2
    private String strProp3;//数据库字段:STR_PROP_3  属性显示:strProp3
    private Integer intProp1;//数据库字段:INT_PROP_1  属性显示:intProp1
    private Integer intProp2;//数据库字段:INT_PROP_2  属性显示:intProp2
    private Long longProp1;//数据库字段:LONG_PROP_1  属性显示:longProp1
    private Long longProp2;//数据库字段:LONG_PROP_2  属性显示:longProp2
    private Long decProp1;//数据库字段:DEC_PROP_1  属性显示:decProp1
    private Long decProp2;//数据库字段:DEC_PROP_2  属性显示:decProp2
    private String boolProp1;//数据库字段:BOOL_PROP_1  属性显示:boolProp1
    private String boolProp2;//数据库字段:BOOL_PROP_2  属性显示:boolProp2

    public QrtzSimpropTriggers() {
    }

    public QrtzSimpropTriggers(
            String schedName,
            String triggerName,
            String triggerGroup
    ) {
        this.schedName = schedName;
        this.triggerName = triggerName;
        this.triggerGroup = triggerGroup;
    }

    public void setSchedName(String value) {
        this.schedName = value;
    }

    public String getSchedName() {
        return this.schedName;
    }

    public void setTriggerName(String value) {
        this.triggerName = value;
    }

    public String getTriggerName() {
        return this.triggerName;
    }

    public void setTriggerGroup(String value) {
        this.triggerGroup = value;
    }

    public String getTriggerGroup() {
        return this.triggerGroup;
    }

    public void setStrProp1(String value) {
        this.strProp1 = value;
    }

    public String getStrProp1() {
        return this.strProp1;
    }

    public void setStrProp2(String value) {
        this.strProp2 = value;
    }

    public String getStrProp2() {
        return this.strProp2;
    }

    public void setStrProp3(String value) {
        this.strProp3 = value;
    }

    public String getStrProp3() {
        return this.strProp3;
    }

    public void setIntProp1(Integer value) {
        this.intProp1 = value;
    }

    public Integer getIntProp1() {
        return this.intProp1;
    }

    public void setIntProp2(Integer value) {
        this.intProp2 = value;
    }

    public Integer getIntProp2() {
        return this.intProp2;
    }

    public void setLongProp1(Long value) {
        this.longProp1 = value;
    }

    public Long getLongProp1() {
        return this.longProp1;
    }

    public void setLongProp2(Long value) {
        this.longProp2 = value;
    }

    public Long getLongProp2() {
        return this.longProp2;
    }

    public void setDecProp1(Long value) {
        this.decProp1 = value;
    }

    public Long getDecProp1() {
        return this.decProp1;
    }

    public void setDecProp2(Long value) {
        this.decProp2 = value;
    }

    public Long getDecProp2() {
        return this.decProp2;
    }

    public void setBoolProp1(String value) {
        this.boolProp1 = value;
    }

    public String getBoolProp1() {
        return this.boolProp1;
    }

    public void setBoolProp2(String value) {
        this.boolProp2 = value;
    }

    public String getBoolProp2() {
        return this.boolProp2;
    }

    private QrtzTriggers qrtzTriggers;

    public void setQrtzTriggers(QrtzTriggers qrtzTriggers) {
        this.qrtzTriggers = qrtzTriggers;
    }

    public QrtzTriggers getQrtzTriggers() {
        return qrtzTriggers;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("SchedName", getSchedName())
                .append("TriggerName", getTriggerName())
                .append("TriggerGroup", getTriggerGroup())
                .append("StrProp1", getStrProp1())
                .append("StrProp2", getStrProp2())
                .append("StrProp3", getStrProp3())
                .append("IntProp1", getIntProp1())
                .append("IntProp2", getIntProp2())
                .append("LongProp1", getLongProp1())
                .append("LongProp2", getLongProp2())
                .append("DecProp1", getDecProp1())
                .append("DecProp2", getDecProp2())
                .append("BoolProp1", getBoolProp1())
                .append("BoolProp2", getBoolProp2())
                .toString();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getSchedName())
                .append(getTriggerName())
                .append(getTriggerGroup())
                .toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof QrtzSimpropTriggers == false) return false;
        if (this == obj) return true;
        QrtzSimpropTriggers other = (QrtzSimpropTriggers) obj;
        return new EqualsBuilder()
                .append(getSchedName(), other.getSchedName())
                .append(getTriggerName(), other.getTriggerName())
                .append(getTriggerGroup(), other.getTriggerGroup())
                .isEquals();
    }
}

