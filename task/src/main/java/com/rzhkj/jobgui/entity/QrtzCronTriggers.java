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
 * cron类型的触发器
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public class QrtzCronTriggers extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    //alias
    public static final String TABLE_ALIAS = "QrtzCronTriggers";
    public static final String ALIAS_SCHED_NAME = "schedName";
    public static final String ALIAS_TRIGGER_NAME = "triggerName";
    public static final String ALIAS_TRIGGER_GROUP = "triggerGroup";
    public static final String ALIAS_CRON_EXPRESSION = "cronExpression";
    public static final String ALIAS_TIME_ZONE_ID = "timeZoneId";

    //date formats

    //可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    private String schedName;//数据库字段:SCHED_NAME  属性显示:schedName
    private String triggerName;//数据库字段:TRIGGER_NAME  属性显示:triggerName
    private String triggerGroup;//数据库字段:TRIGGER_GROUP  属性显示:triggerGroup
    private String cronExpression;//数据库字段:CRON_EXPRESSION  属性显示:cronExpression
    private String timeZoneId;//数据库字段:TIME_ZONE_ID  属性显示:timeZoneId

    public QrtzCronTriggers() {
    }

    public QrtzCronTriggers(
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

    public void setCronExpression(String value) {
        this.cronExpression = value;
    }

    public String getCronExpression() {
        return this.cronExpression;
    }

    public void setTimeZoneId(String value) {
        this.timeZoneId = value;
    }

    public String getTimeZoneId() {
        return this.timeZoneId;
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
                .append("CronExpression", getCronExpression())
                .append("TimeZoneId", getTimeZoneId())
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
        if (obj instanceof QrtzCronTriggers == false) return false;
        if (this == obj) return true;
        QrtzCronTriggers other = (QrtzCronTriggers) obj;
        return new EqualsBuilder()
                .append(getSchedName(), other.getSchedName())
                .append(getTriggerName(), other.getTriggerName())
                .append(getTriggerGroup(), other.getTriggerGroup())
                .isEquals();
    }
}

