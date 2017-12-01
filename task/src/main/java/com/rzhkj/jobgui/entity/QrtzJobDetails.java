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

import java.util.HashSet;
import java.util.Set;


/**
 * 作业信息表
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public class QrtzJobDetails extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    //alias
    public static final String TABLE_ALIAS = "QrtzJobDetails";
    public static final String ALIAS_SCHED_NAME = "schedName";
    public static final String ALIAS_JOB_NAME = "jobName";
    public static final String ALIAS_JOB_GROUP = "jobGroup";
    public static final String ALIAS_DESCRIPTION = "description";
    public static final String ALIAS_JOB_CLASS_NAME = "jobClassName";
    public static final String ALIAS_IS_DURABLE = "isDurable";
    public static final String ALIAS_IS_NONCONCURRENT = "isNonconcurrent";
    public static final String ALIAS_IS_UPDATE_DATA = "isUpdateData";
    public static final String ALIAS_REQUESTS_RECOVERY = "requestsRecovery";
    public static final String ALIAS_JOB_DATA = "jobData";

    //date formats

    //可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    private String schedName;//trigger的名字,该名字用户自己可以随意定制,无强行要求 数据库字段:SCHED_NAME  属性显示:schedName
    private String jobName;//集群中job的名字,该名字用户自己可以随意定制,无强行要求 数据库字段:JOB_NAME  属性显示:jobName
    private String jobGroup;//集群中job的所属组的名字,该名字用户自己随意定制,无强行要求  数据库字段:JOB_GROUP  属性显示:jobGroup
    private String description;//数据库字段:DESCRIPTION  属性显示:description
    private String jobClassName;//集群中个note job实现类的完全包名,quartz就是根据这个路径到classpath找到该job类 数据库字段:JOB_CLASS_NAME  属性显示:jobClassName
    private String isDurable;//是否持久化 数据库字段:IS_DURABLE  属性显示:isDurable
    private String isNonconcurrent;//是否连续 数据库字段:IS_NONCONCURRENT  属性显示:isNonconcurrent
    private String isUpdateData;//数据库字段:IS_UPDATE_DATA  属性显示:isUpdateData
    private String requestsRecovery;//数据库字段:REQUESTS_RECOVERY  属性显示:requestsRecovery
    private byte[] jobData;//存放持久化job对象 数据库字段:JOB_DATA  属性显示:jobData

    public QrtzJobDetails() {
    }

    public QrtzJobDetails(String schedName, String jobName, String jobGroup) {
        this.schedName = schedName;
        this.jobName = jobName;
        this.jobGroup = jobGroup;
    }

    public void setSchedName(String value) {
        this.schedName = value;
    }

    public String getSchedName() {
        return this.schedName;
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

    public void setJobClassName(String value) {
        this.jobClassName = value;
    }

    public String getJobClassName() {
        return this.jobClassName;
    }

    public void setIsDurable(String value) {
        this.isDurable = value;
    }

    public String getIsDurable() {
        return this.isDurable;
    }

    public void setIsNonconcurrent(String value) {
        this.isNonconcurrent = value;
    }

    public String getIsNonconcurrent() {
        return this.isNonconcurrent;
    }

    public void setIsUpdateData(String value) {
        this.isUpdateData = value;
    }

    public String getIsUpdateData() {
        return this.isUpdateData;
    }

    public void setRequestsRecovery(String value) {
        this.requestsRecovery = value;
    }

    public String getRequestsRecovery() {
        return this.requestsRecovery;
    }

    public void setJobData(byte[] value) {
        this.jobData = value;
    }

    public byte[] getJobData() {
        return this.jobData;
    }

    private Set qrtzTriggerss = new HashSet(0);

    public void setQrtzTriggerss(Set<QrtzTriggers> qrtzTriggers) {
        this.qrtzTriggerss = qrtzTriggers;
    }

    public Set<QrtzTriggers> getQrtzTriggerss() {
        return qrtzTriggerss;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("SchedName", getSchedName())
                .append("JobName", getJobName())
                .append("JobGroup", getJobGroup())
                .append("Description", getDescription())
                .append("JobClassName", getJobClassName())
                .append("IsDurable", getIsDurable())
                .append("IsNonconcurrent", getIsNonconcurrent())
                .append("IsUpdateData", getIsUpdateData())
                .append("RequestsRecovery", getRequestsRecovery())
                .append("JobData", getJobData())
                .toString();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getSchedName())
                .append(getJobName())
                .append(getJobGroup())
                .toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof QrtzJobDetails == false) return false;
        if (this == obj) return true;
        QrtzJobDetails other = (QrtzJobDetails) obj;
        return new EqualsBuilder()
                .append(getSchedName(), other.getSchedName())
                .append(getJobName(), other.getJobName())
                .append(getJobGroup(), other.getJobGroup())
                .isEquals();
    }
}

