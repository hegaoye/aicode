/*
 *
 *                        http://www.aicode.io
 *       郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *       代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
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

