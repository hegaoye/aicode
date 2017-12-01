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
import com.rzhkj.core.tools.DateTools;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.HashSet;
import java.util.Set;


/**
 * 触发器的基本信息
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public class QrtzTriggers extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    //alias
    public static final String TABLE_ALIAS = "QrtzTriggers";
    public static final String ALIAS_SCHED_NAME = "schedName";
    public static final String ALIAS_TRIGGER_NAME = "triggerName";
    public static final String ALIAS_TRIGGER_GROUP = "triggerGroup";
    public static final String ALIAS_JOB_NAME = "jobName";
    public static final String ALIAS_JOB_GROUP = "jobGroup";
    public static final String ALIAS_DESCRIPTION = "description";
    public static final String ALIAS_NEXT_FIRE_TIME = "nextFireTime";
    public static final String ALIAS_PREV_FIRE_TIME = "prevFireTime";
    public static final String ALIAS_PRIORITY = "priority";
    public static final String ALIAS_TRIGGER_STATE = "triggerState";
    public static final String ALIAS_TRIGGER_TYPE = "triggerType";
    public static final String ALIAS_START_TIME = "startTime";
    public static final String ALIAS_END_TIME = "endTime";
    public static final String ALIAS_CALENDAR_NAME = "calendarName";
    public static final String ALIAS_MISFIRE_INSTR = "misfireInstr";
    public static final String ALIAS_JOB_DATA = "jobData";

    //date formats

    //可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    private String schedName;//数据库字段:SCHED_NAME  属性显示:schedName
    private String triggerName;//数据库字段:TRIGGER_NAME  属性显示:triggerName
    private String triggerGroup;//数据库字段:TRIGGER_GROUP  属性显示:triggerGroup
    private String jobName;//数据库字段:JOB_NAME  属性显示:jobName
    private String jobGroup;//数据库字段:JOB_GROUP  属性显示:jobGroup
    private String description;//数据库字段:DESCRIPTION  属性显示:description
    private Long nextFireTime;//数据库字段:NEXT_FIRE_TIME  属性显示:nextFireTime
    private Long prevFireTime;//数据库字段:PREV_FIRE_TIME  属性显示:prevFireTime
    private Integer priority;//数据库字段:PRIORITY  属性显示:priority
    private String triggerState;//数据库字段:TRIGGER_STATE  属性显示:triggerState
    private String triggerType;//数据库字段:TRIGGER_TYPE  属性显示:triggerType
    private Long startTime;//数据库字段:START_TIME  属性显示:startTime
    private Long endTime;//数据库字段:END_TIME  属性显示:endTime
    private String calendarName;//数据库字段:CALENDAR_NAME  属性显示:calendarName
    private Integer misfireInstr;//数据库字段:MISFIRE_INSTR  属性显示:misfireInstr
    private byte[] jobData;//数据库字段:JOB_DATA  属性显示:jobData

    public QrtzTriggers() {
    }

    public QrtzTriggers(String schedName, String triggerName, String triggerGroup) {
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

    public void setDescription(String value) {
        this.description = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setNextFireTime(Long value) {
        this.nextFireTime = value;
    }

    public String getNextFireTime() {
        return DateTools.times(this.nextFireTime);
    }

    public void setPrevFireTime(Long value) {
        this.prevFireTime = value;
    }

    public String getPrevFireTime() {
        return DateTools.times(this.prevFireTime);
    }

    public void setPriority(Integer value) {
        this.priority = value;
    }

    public String getPriority() {
        return this.priority + "级";
    }

    public void setTriggerState(String value) {
        this.triggerState = value;
    }

    public String getTriggerState() {
        String stateStr = this.triggerState;
        if (this.triggerState.equalsIgnoreCase("WAITING")) {
            stateStr = "等待";
        } else if (this.triggerState.equalsIgnoreCase("EXECUTING")) {
            stateStr = "执行中";
        } else if (this.triggerState.equalsIgnoreCase("ACQUIRED")) {
            stateStr = "正常执行";
        } else if (this.triggerState.equalsIgnoreCase("PAUSED")) {
            stateStr = "暂停";
        } else if (this.triggerState.equalsIgnoreCase("BLOCKED")) {
            stateStr = "阻塞";
        } else if (this.triggerState.equalsIgnoreCase("ERROR")) {
            stateStr = "错误";
        } else if (this.triggerState.equalsIgnoreCase("COMPLETE")) {
            stateStr = "执行完成";
        }
        return stateStr;
    }

    public void setTriggerType(String value) {
        this.triggerType = value;
    }

    public String getTriggerType() {
        return this.triggerType;
    }

    public void setStartTime(Long value) {
        this.startTime = value;
    }

    public String getStartTime() {
        return DateTools.times(this.startTime);
    }

    public void setEndTime(Long value) {
        this.endTime = value;
    }

    public String getEndTime() {
        return DateTools.times(this.endTime);
    }

    public void setCalendarName(String value) {
        this.calendarName = value;
    }

    public String getCalendarName() {
        return this.calendarName == null ? "无" : this.calendarName;
    }

    public void setMisfireInstr(Integer value) {
        this.misfireInstr = value;
    }

    public Integer getMisfireInstr() {
        return this.misfireInstr;
    }

    public void setJobData(byte[] value) {
        this.jobData = value;
    }

    public byte[] getJobData() {
        return this.jobData;
    }

    private Set qrtzCronTriggerss = new HashSet(0);

    public void setQrtzCronTriggerss(Set<QrtzCronTriggers> qrtzCronTriggers) {
        this.qrtzCronTriggerss = qrtzCronTriggers;
    }

    public Set<QrtzCronTriggers> getQrtzCronTriggerss() {
        return qrtzCronTriggerss;
    }

    private Set qrtzSimpleTriggerss = new HashSet(0);

    public void setQrtzSimpleTriggerss(Set<QrtzSimpleTriggers> qrtzSimpleTriggers) {
        this.qrtzSimpleTriggerss = qrtzSimpleTriggers;
    }

    public Set<QrtzSimpleTriggers> getQrtzSimpleTriggerss() {
        return qrtzSimpleTriggerss;
    }

    private Set qrtzBlobTriggerss = new HashSet(0);

    public void setQrtzBlobTriggerss(Set<QrtzBlobTriggers> qrtzBlobTriggers) {
        this.qrtzBlobTriggerss = qrtzBlobTriggers;
    }

    public Set<QrtzBlobTriggers> getQrtzBlobTriggerss() {
        return qrtzBlobTriggerss;
    }

    private Set qrtzSimpropTriggerss = new HashSet(0);

    public void setQrtzSimpropTriggerss(Set<QrtzSimpropTriggers> qrtzSimpropTriggers) {
        this.qrtzSimpropTriggerss = qrtzSimpropTriggers;
    }

    public Set<QrtzSimpropTriggers> getQrtzSimpropTriggerss() {
        return qrtzSimpropTriggerss;
    }

    private QrtzJobDetails qrtzJobDetails;

    public void setQrtzJobDetails(QrtzJobDetails qrtzJobDetails) {
        this.qrtzJobDetails = qrtzJobDetails;
    }

    public QrtzJobDetails getQrtzJobDetails() {
        return qrtzJobDetails;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("SchedName", getSchedName())
                .append("TriggerName", getTriggerName())
                .append("TriggerGroup", getTriggerGroup())
                .append("JobName", getJobName())
                .append("JobGroup", getJobGroup())
                .append("Description", getDescription())
                .append("NextFireTime", getNextFireTime())
                .append("PrevFireTime", getPrevFireTime())
                .append("Priority", getPriority())
                .append("TriggerState", getTriggerState())
                .append("TriggerType", getTriggerType())
                .append("StartTime", getStartTime())
                .append("EndTime", getEndTime())
                .append("CalendarName", getCalendarName())
                .append("MisfireInstr", getMisfireInstr())
                .append("JobData", getJobData())
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
        if (obj instanceof QrtzTriggers == false) return false;
        if (this == obj) return true;
        QrtzTriggers other = (QrtzTriggers) obj;
        return new EqualsBuilder()
                .append(getSchedName(), other.getSchedName())
                .append(getTriggerName(), other.getTriggerName())
                .append(getTriggerGroup(), other.getTriggerGroup())
                .isEquals();
    }


}

