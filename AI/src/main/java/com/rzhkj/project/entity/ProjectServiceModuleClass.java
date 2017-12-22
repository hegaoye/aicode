/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import com.rzhkj.mds.common.BaseEntity;
import javax.annotation.Resource;
import com.ponddy.tutor.entity.*;
import com.ponddy.tutor.dao.*;
import com.ponddy.tutor.service.*;

/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

/**
 * 
 */
public class ProjectServiceModuleClass extends BaseEntity implements java.io.Serializable{
	private static final long serialVersionUID = 5454155825314635342L;

	//alias
	public static final String TABLE_ALIAS = "ProjectServiceModuleClass";
	public static final String ALIAS_ID = "id";
	public static final String ALIAS_CLASS_INFO_CODE = "类编码";
	public static final String ALIAS_SERVICE_MODULE_CODE = "业务编码";

	//date formats

	//可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
	private Long id;//数据库字段:id  属性显示:id
	private String classInfoCode;//数据库字段:classInfoCode  属性显示:类编码
	private String serviceModuleCode;//数据库字段:serviceModuleCode  属性显示:业务编码

	public ProjectServiceModuleClass(){
	}

	public ProjectServiceModuleClass(
		Long id,
		String classInfoCode
	){
		this.id = id;
		this.classInfoCode = classInfoCode;
	}

	public void setId(Long value) {
		this.id = value;
	}

	public Long getId() {
		return this.id;
	}
	public void setClassInfoCode(String value) {
		this.classInfoCode = value;
	}

	public String getClassInfoCode() {
		return this.classInfoCode;
	}
	public void setServiceModuleCode(String value) {
		this.serviceModuleCode = value;
	}

	public String getServiceModuleCode() {
		return this.serviceModuleCode;
	}

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("ClassInfoCode",getClassInfoCode())
			.append("ServiceModuleCode",getServiceModuleCode())
			.toString();
	}

	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.append(getClassInfoCode())
			.toHashCode();
	}

	public boolean equals(Object obj) {
		if(obj instanceof ProjectServiceModuleClass == false) return false;
		if(this == obj) return true;
		ProjectServiceModuleClass other = (ProjectServiceModuleClass)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.append(getClassInfoCode(),other.getClassInfoCode())
			.isEquals();
	}
}

