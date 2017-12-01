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
 * 调度器状态
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public class QrtzSchedulerState extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    //alias
    public static final String TABLE_ALIAS = "QrtzSchedulerState";
    public static final String ALIAS_SCHED_NAME = "schedName";
    public static final String ALIAS_INSTANCE_NAME = "instanceName";
    public static final String ALIAS_LAST_CHECKIN_TIME = "lastCheckinTime";
    public static final String ALIAS_CHECKIN_INTERVAL = "checkinInterval";

    //date formats

    //可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    private String schedName;//数据库字段:SCHED_NAME  属性显示:schedName
    private String instanceName;//数据库字段:INSTANCE_NAME  属性显示:instanceName
    private Long lastCheckinTime;//数据库字段:LAST_CHECKIN_TIME  属性显示:lastCheckinTime
    private Long checkinInterval;//数据库字段:CHECKIN_INTERVAL  属性显示:checkinInterval

    public QrtzSchedulerState() {
    }

    public QrtzSchedulerState(
            String schedName,
            String instanceName
    ) {
        this.schedName = schedName;
        this.instanceName = instanceName;
    }

    public void setSchedName(String value) {
        this.schedName = value;
    }

    public String getSchedName() {
        return this.schedName;
    }

    public void setInstanceName(String value) {
        this.instanceName = value;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public void setLastCheckinTime(Long value) {
        this.lastCheckinTime = value;
    }

    public Long getLastCheckinTime() {
        return this.lastCheckinTime;
    }

    public void setCheckinInterval(Long value) {
        this.checkinInterval = value;
    }

    public Long getCheckinInterval() {
        return this.checkinInterval;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("SchedName", getSchedName())
                .append("InstanceName", getInstanceName())
                .append("LastCheckinTime", getLastCheckinTime())
                .append("CheckinInterval", getCheckinInterval())
                .toString();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getSchedName())
                .append(getInstanceName())
                .toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof QrtzSchedulerState == false) return false;
        if (this == obj) return true;
        QrtzSchedulerState other = (QrtzSchedulerState) obj;
        return new EqualsBuilder()
                .append(getSchedName(), other.getSchedName())
                .append(getInstanceName(), other.getInstanceName())
                .isEquals();
    }
}

