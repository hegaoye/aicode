/*
 *
 *                        http://www.aicode.io
 *
 *
 *       本代码仅用于AI-Code.目.
 */


package io.aicode.logging.vo;


import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

public class LoggingEventExceptionVO implements Serializable {
    private static final long serialVersionUID = 3148176768559230877L;


    /**
     * eventId
     */
    private Long eventId;
    /**
     * i
     */
    private Integer i;
    /**
     * traceLine
     */
    private String traceLine;

    public Long getEventId() {
        return this.eventId;
    }

    public void setEventId(Long value) {
        this.eventId = value;
    }

    public Integer getI() {
        return this.i;
    }

    public void setI(Integer value) {
        this.i = value;
    }

    public String getTraceLine() {
        return this.traceLine;
    }

    public void setTraceLine(String value) {
        this.traceLine = value;
    }


    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}

