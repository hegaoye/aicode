/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *       郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *       代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *       本代码仅用于龐帝業務系统.目.
 */


package com.rzhkj.logging.vo;


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

