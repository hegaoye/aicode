/*
 *${copyright}
 */
package ${basepackage}.${model}.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import ${basepackage}.common.BaseEntity;
import lombok.Data;

/**
 * ${notes}
 * @author ${author}
 */
@Data
public class ${className} extends BaseEntity implements java.io.Serializable{

	<#list fields as field>
	private ${field.fieldType} ${field.field};//数据库字段:${field.columnInfo.name}  属性显示:${field.columnInfo.notes}
	</#list>
}
