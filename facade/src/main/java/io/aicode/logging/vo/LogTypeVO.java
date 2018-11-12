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

public class LogTypeVO implements Serializable {
    private static final long serialVersionUID = 3148176768559230877L;


    /**
     * id
     */
    private Long id;
    /**
     * 日志类型名称
     */
    private String name;
    /**
     * 日志类型代号
     */
    private String code;
    /**
     * 日志类型详情
     */
    private String detail;
    /**
     * 日志类型是否启用
     */
    private Integer enable;

    public Long getId() {
        return this.id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String value) {
        this.code = value;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail(String value) {
        this.detail = value;
    }

    public Integer getEnable() {
        return this.enable;
    }

    public void setEnable(Integer value) {
        this.enable = value;
    }


    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}

