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

public class LogTemplateVO implements Serializable {
    private static final long serialVersionUID = 3148176768559230877L;


    /**
     * id
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 角色id
     */
    private Long roleId;
    /**
     * 日志详情
     */
    private String detail;
    /**
     * ip地址
     */
    private String ip;
    /**
     * 创建时间
     */
    private java.util.Date createTimeBegin;
    private java.util.Date createTimeEnd;
    /**
     * 门户类型 例如：ilinm,tianyzhen 等区分平台
     */
    private String portalType;
    /**
     * 客户端类型 如：web/client/app
     */
    private String clientType;

    public Long getId() {
        return this.id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long value) {
        this.userId = value;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Long value) {
        this.roleId = value;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail(String value) {
        this.detail = value;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String value) {
        this.ip = value;
    }

    public java.util.Date getCreateTimeBegin() {
        return this.createTimeBegin;
    }

    public void setCreateTimeBegin(java.util.Date value) {
        this.createTimeBegin = value;
    }

    public java.util.Date getCreateTimeEnd() {
        return this.createTimeEnd;
    }

    public void setCreateTimeEnd(java.util.Date value) {
        this.createTimeEnd = value;
    }

    public String getPortalType() {
        return this.portalType;
    }

    public void setPortalType(String value) {
        this.portalType = value;
    }

    public String getClientType() {
        return this.clientType;
    }

    public void setClientType(String value) {
        this.clientType = value;
    }


    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}

