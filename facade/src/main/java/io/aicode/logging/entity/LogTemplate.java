/*
 *
 *                        http://www.aicode.io
 *
 *
 *       本代码仅用于AI-Code.目.
 */


package io.aicode.logging.entity;

import com.baidu.fsg.uid.utils.DateUtils;
import io.aicode.core.base.BaseEntity;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class LogTemplate extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    //alias
    public static final String TABLE_ALIAS = "LogTemplate";
    public static final String ALIAS_ID = "id";
    public static final String ALIAS_USER_ID = "用户id";
    public static final String ALIAS_ROLE_ID = "角色id";
    public static final String ALIAS_DETAIL = "日志详情";
    public static final String ALIAS_IP = "ip地址";
    public static final String ALIAS_CREATE_TIME = "创建时间";
    public static final String ALIAS_PORTAL_TYPE = "门户类型 例如：ilinm,tianyzhen 等区分平台";
    public static final String ALIAS_CLIENT_TYPE = "客户端类型 如：web/client/app";

    //date formats
    public static final String FORMAT_CREATE_TIME = DATE_FORMAT;

    //可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    private Long userId;//数据库字段:user_id  属性显示:用户id
    private Long roleId;//数据库字段:role_id  属性显示:角色id
    private String detail;//数据库字段:detail  属性显示:日志详情
    private String ip;//数据库字段:ip  属性显示:ip地址
    private java.util.Date createTime;//数据库字段:create_time  属性显示:创建时间
    private String portalType;//数据库字段:portal_type  属性显示:门户类型 例如：ilinm,tianyzhen 等区分平台
    private String clientType;//数据库字段:client_type  属性显示:客户端类型 如：web/client/app

    public LogTemplate() {
    }

    public LogTemplate(
            Long id
    ) {
        this.id = id;
    }

    public void setUserId(Long value) {
        this.userId = value;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setRoleId(Long value) {
        this.roleId = value;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public void setDetail(String value) {
        this.detail = value;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setIp(String value) {
        this.ip = value;
    }

    public String getIp() {
        return this.ip;
    }

    public String getCreateTimeString() {
        return DateUtils.formatDate(getCreateTime(), DATE_TIME_FORMAT);
    }

    public void setCreateTimeString(String value) {
        setCreateTime(DateUtils.parseDate(value, DATE_FORMAT));
    }

    public void setCreateTime(java.util.Date value) {
        this.createTime = value;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setPortalType(String value) {
        this.portalType = value;
    }

    public String getPortalType() {
        return this.portalType;
    }

    public void setClientType(String value) {
        this.clientType = value;
    }

    public String getClientType() {
        return this.clientType;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("Id", getId())
                .append("UserId", getUserId())
                .append("RoleId", getRoleId())
                .append("Detail", getDetail())
                .append("Ip", getIp())
                .append("CreateTime", getCreateTime())
                .append("PortalType", getPortalType())
                .append("ClientType", getClientType())
                .toString();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getId())
                .toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof LogTemplate == false) return false;
        if (this == obj) return true;
        LogTemplate other = (LogTemplate) obj;
        return new EqualsBuilder()
                .append(getId(), other.getId())
                .isEquals();
    }
}

