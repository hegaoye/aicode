/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
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

import java.util.HashSet;
import java.util.Set;

public class LoggingEvent extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    //alias
    public static final String TABLE_ALIAS = "LoggingEvent";
    public static final String ALIAS_TIMESTMP = "timestmp";
    public static final String ALIAS_FORMATTED_MESSAGE = "formattedMessage";
    public static final String ALIAS_LOGGER_NAME = "loggerName";
    public static final String ALIAS_LEVEL_STRING = "levelString";
    public static final String ALIAS_THREAD_NAME = "threadName";
    public static final String ALIAS_REFERENCE_FLAG = "referenceFlag";
    public static final String ALIAS_ARG0 = "arg0";
    public static final String ALIAS_ARG1 = "arg1";
    public static final String ALIAS_ARG2 = "arg2";
    public static final String ALIAS_ARG3 = "arg3";
    public static final String ALIAS_CALLER_FILENAME = "callerFilename";
    public static final String ALIAS_CALLER_CLASS = "callerClass";
    public static final String ALIAS_CALLER_METHOD = "callerMethod";
    public static final String ALIAS_CALLER_LINE = "callerLine";
    public static final String ALIAS_EVENT_ID = "eventId";

    //date formats

    //可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    private Long timestmp;//数据库字段:timestmp  属性显示:timestmp
    private String formattedMessage;//数据库字段:formatted_message  属性显示:formattedMessage
    private String loggerName;//数据库字段:logger_name  属性显示:loggerName
    private String levelString;//数据库字段:level_string  属性显示:levelString
    private String threadName;//数据库字段:thread_name  属性显示:threadName
    private Integer referenceFlag;//数据库字段:reference_flag  属性显示:referenceFlag
    private String arg0;//数据库字段:arg0  属性显示:arg0
    private String arg1;//数据库字段:arg1  属性显示:arg1
    private String arg2;//数据库字段:arg2  属性显示:arg2
    private String arg3;//数据库字段:arg3  属性显示:arg3
    private String callerFilename;//数据库字段:caller_filename  属性显示:callerFilename
    private String callerClass;//数据库字段:caller_class  属性显示:callerClass
    private String callerMethod;//数据库字段:caller_method  属性显示:callerMethod
    private String callerLine;//数据库字段:caller_line  属性显示:callerLine
    private Long eventId;//数据库字段:event_id  属性显示:eventId

    public LoggingEvent() {
    }

    public LoggingEvent(Long eventId) {
        this.eventId = eventId;
    }

    public Long getTimestmp() {
        return timestmp;
    }

    public void setTimestmp(Long timestmp) {
        this.timestmp = timestmp;
    }

    public String getFormattedMessage() {
        return formattedMessage;
    }

    public void setFormattedMessage(String formattedMessage) {
        this.formattedMessage = formattedMessage;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getLevelString() {
        return levelString;
    }

    public void setLevelString(String levelString) {
        this.levelString = levelString;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public Integer getReferenceFlag() {
        return referenceFlag;
    }

    public void setReferenceFlag(Integer referenceFlag) {
        this.referenceFlag = referenceFlag;
    }

    public String getArg0() {
        return arg0;
    }

    public void setArg0(String arg0) {
        this.arg0 = arg0;
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public String getArg3() {
        return arg3;
    }

    public void setArg3(String arg3) {
        this.arg3 = arg3;
    }

    public String getCallerFilename() {
        return callerFilename;
    }

    public void setCallerFilename(String callerFilename) {
        this.callerFilename = callerFilename;
    }

    public String getCallerClass() {
        return callerClass;
    }

    public void setCallerClass(String callerClass) {
        this.callerClass = callerClass;
    }

    public String getCallerMethod() {
        return callerMethod;
    }

    public void setCallerMethod(String callerMethod) {
        this.callerMethod = callerMethod;
    }

    public String getCallerLine() {
        return callerLine;
    }

    public void setCallerLine(String callerLine) {
        this.callerLine = callerLine;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    private Set loggingEventPropertys = new HashSet(0);

    public void setLoggingEventPropertys(Set<LoggingEventProperty> loggingEventProperty) {
        this.loggingEventPropertys = loggingEventProperty;
    }

    public Set<LoggingEventProperty> getLoggingEventPropertys() {
        return loggingEventPropertys;
    }

    private Set loggingEventExceptions = new HashSet(0);

    public void setLoggingEventExceptions(Set<LoggingEventException> loggingEventException) {
        this.loggingEventExceptions = loggingEventException;
    }

    public Set<LoggingEventException> getLoggingEventExceptions() {
        return loggingEventExceptions;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("Timestmp", getTimestmp())
                .append("FormattedMessage", getFormattedMessage())
                .append("LoggerName", getLoggerName())
                .append("LevelString", getLevelString())
                .append("ThreadName", getThreadName())
                .append("ReferenceFlag", getReferenceFlag())
                .append("Arg0", getArg0())
                .append("Arg1", getArg1())
                .append("Arg2", getArg2())
                .append("Arg3", getArg3())
                .append("CallerFilename", getCallerFilename())
                .append("CallerClass", getCallerClass())
                .append("CallerMethod", getCallerMethod())
                .append("CallerLine", getCallerLine())
                .append("EventId", getEventId())
                .toString();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getEventId())
                .toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof LoggingEvent == false) return false;
        if (this == obj) return true;
        LoggingEvent other = (LoggingEvent) obj;
        return new EqualsBuilder()
                .append(getEventId(), other.getEventId())
                .isEquals();
    }
}

