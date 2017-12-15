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

