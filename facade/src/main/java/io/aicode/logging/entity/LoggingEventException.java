/*
 *
 *                        http://www.aicode.io
 *       郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *       代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *       本代码仅用于AI-Code.目.
 */


package io.aicode.logging.entity;

import io.aicode.core.base.BaseEntity;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class LoggingEventException extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    //alias
    public static final String TABLE_ALIAS = "LoggingEventException";
    public static final String ALIAS_EVENT_ID = "eventId";
    public static final String ALIAS_I = "i";
    public static final String ALIAS_TRACE_LINE = "traceLine";

    //date formats

    //可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    private Long eventId;//数据库字段:event_id  属性显示:eventId
    private Integer i;//数据库字段:i  属性显示:i
    private String traceLine;//数据库字段:trace_line  属性显示:traceLine

    public LoggingEventException() {
    }

    public LoggingEventException(
            Long eventId,
            Integer i
    ) {
        this.eventId = eventId;
        this.i = i;
    }

    public void setEventId(Long value) {
        this.eventId = value;
    }

    public Long getEventId() {
        return this.eventId;
    }

    public void setI(Integer value) {
        this.i = value;
    }

    public Integer getI() {
        return this.i;
    }

    public void setTraceLine(String value) {
        this.traceLine = value;
    }

    public String getTraceLine() {
        return this.traceLine;
    }

    private LoggingEvent loggingEvent;

    public void setLoggingEvent(LoggingEvent loggingEvent) {
        this.loggingEvent = loggingEvent;
    }

    public LoggingEvent getLoggingEvent() {
        return loggingEvent;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("EventId", getEventId())
                .append("I", getI())
                .append("TraceLine", getTraceLine())
                .toString();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getEventId())
                .append(getI())
                .toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof LoggingEventException == false) return false;
        if (this == obj) return true;
        LoggingEventException other = (LoggingEventException) obj;
        return new EqualsBuilder()
                .append(getEventId(), other.getEventId())
                .append(getI(), other.getI())
                .isEquals();
    }
}

