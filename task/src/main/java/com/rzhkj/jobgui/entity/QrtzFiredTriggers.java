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
 * 存放已触发的触发器
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public class QrtzFiredTriggers extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    //alias
    public static final String TABLE_ALIAS = "QrtzFiredTriggers";
    public static final String ALIAS_SCHED_NAME = "schedName";
    public static final String ALIAS_ENTRY_ID = "entryId";
    public static final String ALIAS_TRIGGER_NAME = "triggerName";
    public static final String ALIAS_TRIGGER_GROUP = "triggerGroup";
    public static final String ALIAS_INSTANCE_NAME = "instanceName";
    public static final String ALIAS_FIRED_TIME = "firedTime";
    public static final String ALIAS_SCHED_TIME = "schedTime";
    public static final String ALIAS_PRIORITY = "priority";
    public static final String ALIAS_STATE = "state";
    public static final String ALIAS_JOB_NAME = "jobName";
    public static final String ALIAS_JOB_GROUP = "jobGroup";
    public static final String ALIAS_IS_NONCONCURRENT = "isNonconcurrent";
    public static final String ALIAS_REQUESTS_RECOVERY = "requestsRecovery";

    //date formats

    //可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    private String schedName;//数据库字段:SCHED_NAME  属性显示:schedName
    private String entryId;//数据库字段:ENTRY_ID  属性显示:entryId
    private String triggerName;//数据库字段:TRIGGER_NAME  属性显示:triggerName
    private String triggerGroup;//数据库字段:TRIGGER_GROUP  属性显示:triggerGroup
    private String instanceName;//数据库字段:INSTANCE_NAME  属性显示:instanceName
    private Long firedTime;//数据库字段:FIRED_TIME  属性显示:firedTime
    private Long schedTime;//数据库字段:SCHED_TIME  属性显示:schedTime
    private Integer priority;//数据库字段:PRIORITY  属性显示:priority
    private String state;//数据库字段:STATE  属性显示:state
    private String jobName;//数据库字段:JOB_NAME  属性显示:jobName
    private String jobGroup;//数据库字段:JOB_GROUP  属性显示:jobGroup
    private String isNonconcurrent;//数据库字段:IS_NONCONCURRENT  属性显示:isNonconcurrent
    private String requestsRecovery;//数据库字段:REQUESTS_RECOVERY  属性显示:requestsRecovery

    public QrtzFiredTriggers() {
    }

    public QrtzFiredTriggers(
            String schedName,
            String entryId
    ) {
        this.schedName = schedName;
        this.entryId = entryId;
    }

    public void setSchedName(String value) {
        this.schedName = value;
    }

    public String getSchedName() {
        return this.schedName;
    }

    public void setEntryId(String value) {
        this.entryId = value;
    }

    public String getEntryId() {
        return this.entryId;
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

    public void setInstanceName(String value) {
        this.instanceName = value;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public void setFiredTime(Long value) {
        this.firedTime = value;
    }

    public Long getFiredTime() {
        return this.firedTime;
    }

    public void setSchedTime(Long value) {
        this.schedTime = value;
    }

    public Long getSchedTime() {
        return this.schedTime;
    }

    public void setPriority(Integer value) {
        this.priority = value;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public void setState(String value) {
        this.state = value;
    }

    public String getState() {
        return this.state;
    }

    public void setJobName(String value) {
        this.jobName = value;
    }

    public String getJobName() {
        return this.jobName;
    }

    public void setJobGroup(String value) {
        this.jobGroup = value;
    }

    public String getJobGroup() {
        return this.jobGroup;
    }

    public void setIsNonconcurrent(String value) {
        this.isNonconcurrent = value;
    }

    public String getIsNonconcurrent() {
        return this.isNonconcurrent;
    }

    public void setRequestsRecovery(String value) {
        this.requestsRecovery = value;
    }

    public String getRequestsRecovery() {
        return this.requestsRecovery;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("SchedName", getSchedName())
                .append("EntryId", getEntryId())
                .append("TriggerName", getTriggerName())
                .append("TriggerGroup", getTriggerGroup())
                .append("InstanceName", getInstanceName())
                .append("FiredTime", getFiredTime())
                .append("SchedTime", getSchedTime())
                .append("Priority", getPriority())
                .append("State", getState())
                .append("JobName", getJobName())
                .append("JobGroup", getJobGroup())
                .append("IsNonconcurrent", getIsNonconcurrent())
                .append("RequestsRecovery", getRequestsRecovery())
                .toString();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getSchedName())
                .append(getEntryId())
                .toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof QrtzFiredTriggers == false) return false;
        if (this == obj) return true;
        QrtzFiredTriggers other = (QrtzFiredTriggers) obj;
        return new EqualsBuilder()
                .append(getSchedName(), other.getSchedName())
                .append(getEntryId(), other.getEntryId())
                .isEquals();
    }
}

