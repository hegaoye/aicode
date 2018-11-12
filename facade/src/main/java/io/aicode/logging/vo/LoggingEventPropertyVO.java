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

public class LoggingEventPropertyVO implements Serializable {
    private static final long serialVersionUID = 3148176768559230877L;


    /**
     * eventId
     */
    private Long eventId;
    /**
     * mappedKey
     */
    private String mappedKey;
    /**
     * mappedValue
     */
    private String mappedValue;

    public Long getEventId() {
        return this.eventId;
    }

    public void setEventId(Long value) {
        this.eventId = value;
    }

    public String getMappedKey() {
        return this.mappedKey;
    }

    public void setMappedKey(String value) {
        this.mappedKey = value;
    }

    public String getMappedValue() {
        return this.mappedValue;
    }

    public void setMappedValue(String value) {
        this.mappedValue = value;
    }


    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}

