/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *       郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *       代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *       本代码仅用于AI-Code.目.
 */


package com.rzhkj.logging.vo;


import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

public class LoggingEventVO implements Serializable {
    private static final long serialVersionUID = 3148176768559230877L;


    /**
     * timestmp
     */
    private Long timestmp;
    /**
     * formattedMessage
     */
    private String formattedMessage;
    /**
     * loggerName
     */
    private String loggerName;
    /**
     * levelString
     */
    private String levelString;
    /**
     * threadName
     */
    private String threadName;
    /**
     * referenceFlag
     */
    private Integer referenceFlag;
    /**
     * arg0
     */
    private String arg0;
    /**
     * arg1
     */
    private String arg1;
    /**
     * arg2
     */
    private String arg2;
    /**
     * arg3
     */
    private String arg3;
    /**
     * callerFilename
     */
    private String callerFilename;
    /**
     * callerClass
     */
    private String callerClass;
    /**
     * callerMethod
     */
    private String callerMethod;
    /**
     * callerLine
     */
    private String callerLine;
    /**
     * eventId
     */
    private Long eventId;

    public Long getTimestmp() {
        return this.timestmp;
    }

    public void setTimestmp(Long value) {
        this.timestmp = value;
    }

    public String getFormattedMessage() {
        return this.formattedMessage;
    }

    public void setFormattedMessage(String value) {
        this.formattedMessage = value;
    }

    public String getLoggerName() {
        return this.loggerName;
    }

    public void setLoggerName(String value) {
        this.loggerName = value;
    }

    public String getLevelString() {
        return this.levelString;
    }

    public void setLevelString(String value) {
        this.levelString = value;
    }

    public String getThreadName() {
        return this.threadName;
    }

    public void setThreadName(String value) {
        this.threadName = value;
    }

    public Integer getReferenceFlag() {
        return this.referenceFlag;
    }

    public void setReferenceFlag(Integer value) {
        this.referenceFlag = value;
    }

    public String getArg0() {
        return this.arg0;
    }

    public void setArg0(String value) {
        this.arg0 = value;
    }

    public String getArg1() {
        return this.arg1;
    }

    public void setArg1(String value) {
        this.arg1 = value;
    }

    public String getArg2() {
        return this.arg2;
    }

    public void setArg2(String value) {
        this.arg2 = value;
    }

    public String getArg3() {
        return this.arg3;
    }

    public void setArg3(String value) {
        this.arg3 = value;
    }

    public String getCallerFilename() {
        return this.callerFilename;
    }

    public void setCallerFilename(String value) {
        this.callerFilename = value;
    }

    public String getCallerClass() {
        return this.callerClass;
    }

    public void setCallerClass(String value) {
        this.callerClass = value;
    }

    public String getCallerMethod() {
        return this.callerMethod;
    }

    public void setCallerMethod(String value) {
        this.callerMethod = value;
    }

    public String getCallerLine() {
        return this.callerLine;
    }

    public void setCallerLine(String value) {
        this.callerLine = value;
    }

    public Long getEventId() {
        return this.eventId;
    }

    public void setEventId(Long value) {
        this.eventId = value;
    }


    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}

