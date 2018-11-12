/*
 *
 *                        http://www.aicode.io
 *
 *
 *       本代码仅用于AI-Code.目.
 */


package io.aicode.logging.entity;

import io.aicode.core.base.BaseEntity;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class LogType extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    //alias
    public static final String TABLE_ALIAS = "LogType";
    public static final String ALIAS_ID = "id";
    public static final String ALIAS_NAME = "日志类型名称";
    public static final String ALIAS_CODE = "日志类型代号";
    public static final String ALIAS_DETAIL = "日志类型详情";
    public static final String ALIAS_ENABLE = "日志类型是否启用";

    //date formats

    //可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    private String name;//数据库字段:name  属性显示:日志类型名称
    private String code;//数据库字段:code  属性显示:日志类型代号
    private String detail;//数据库字段:detail  属性显示:日志类型详情
    private Integer enable;//数据库字段:enable  属性显示:日志类型是否启用

    public LogType() {
    }

    public LogType(
            Long id
    ) {
        this.id = id;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getName() {
        return this.name;
    }

    public void setCode(String value) {
        this.code = value;
    }

    public String getCode() {
        return this.code;
    }

    public void setDetail(String value) {
        this.detail = value;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setEnable(Integer value) {
        this.enable = value;
    }

    public Integer getEnable() {
        return this.enable;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("Id", getId())
                .append("Name", getName())
                .append("Code", getCode())
                .append("Detail", getDetail())
                .append("Enable", getEnable())
                .toString();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getId())
                .toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof LogType == false) return false;
        if (this == obj) return true;
        LogType other = (LogType) obj;
        return new EqualsBuilder()
                .append(getId(), other.getId())
                .isEquals();
    }
}

