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
 * 存放日历信息
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public class QrtzCalendars extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    //alias
    public static final String TABLE_ALIAS = "QrtzCalendars";
    public static final String ALIAS_SCHED_NAME = "schedName";
    public static final String ALIAS_CALENDAR_NAME = "calendarName";
    public static final String ALIAS_CALENDAR = "calendar";

    //date formats

    //可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    private String schedName;//数据库字段:SCHED_NAME  属性显示:schedName
    private String calendarName;//数据库字段:CALENDAR_NAME  属性显示:calendarName
    private byte[] calendar;//数据库字段:CALENDAR  属性显示:calendar

    public QrtzCalendars() {
    }

    public QrtzCalendars(
            String schedName,
            String calendarName
    ) {
        this.schedName = schedName;
        this.calendarName = calendarName;
    }

    public void setSchedName(String value) {
        this.schedName = value;
    }

    public String getSchedName() {
        return this.schedName;
    }

    public void setCalendarName(String value) {
        this.calendarName = value;
    }

    public String getCalendarName() {
        return this.calendarName;
    }

    public void setCalendar(byte[] value) {
        this.calendar = value;
    }

    public byte[] getCalendar() {
        return this.calendar;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("SchedName", getSchedName())
                .append("CalendarName", getCalendarName())
                .append("Calendar", getCalendar())
                .toString();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getSchedName())
                .append(getCalendarName())
                .toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof QrtzCalendars == false) return false;
        if (this == obj) return true;
        QrtzCalendars other = (QrtzCalendars) obj;
        return new EqualsBuilder()
                .append(getSchedName(), other.getSchedName())
                .append(getCalendarName(), other.getCalendarName())
                .isEquals();
    }
}

