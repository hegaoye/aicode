/*
 *
 *                        http://www.aicode.io
 *
 *
 *       本代码仅用于AI-Code.目.
 */


package io.aicode.logging.entity;

import io.aicode.base.BaseEntity;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class LoggingEventProperty extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    //alias
    public static final String TABLE_ALIAS = "LoggingEventProperty";
    public static final String ALIAS_EVENT_ID = "eventId";
    public static final String ALIAS_MAPPED_KEY = "mappedKey";
    public static final String ALIAS_MAPPED_VALUE = "mappedValue";

    //date formats

    //可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    private Long eventId;//数据库字段:event_id  属性显示:eventId
    private String mappedKey;//数据库字段:mapped_key  属性显示:mappedKey
    private String mappedValue;//数据库字段:mapped_value  属性显示:mappedValue

    public LoggingEventProperty() {
    }

    public LoggingEventProperty(
            Long eventId,
            String mappedKey
    ) {
        this.eventId = eventId;
        this.mappedKey = mappedKey;
    }

    public void setEventId(Long value) {
        this.eventId = value;
    }

    public Long getEventId() {
        return this.eventId;
    }

    public void setMappedKey(String value) {
        this.mappedKey = value;
    }

    public String getMappedKey() {
        return this.mappedKey;
    }

    public void setMappedValue(String value) {
        this.mappedValue = value;
    }

    public String getMappedValue() {
        return this.mappedValue;
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
                .append("MappedKey", getMappedKey())
                .append("MappedValue", getMappedValue())
                .toString();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getEventId())
                .append(getMappedKey())
                .toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof LoggingEventProperty == false) return false;
        if (this == obj) return true;
        LoggingEventProperty other = (LoggingEventProperty) obj;
        return new EqualsBuilder()
                .append(getEventId(), other.getEventId())
                .append(getMappedKey(), other.getMappedKey())
                .isEquals();
    }
}

