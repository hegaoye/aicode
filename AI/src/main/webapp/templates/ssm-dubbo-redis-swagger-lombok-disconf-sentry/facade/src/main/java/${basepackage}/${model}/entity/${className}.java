/*
 * ${copyright}
 */
package ${basePackage}.${model}.entity;

import ${basePackage}.core.base.BaseEntity;
import lombok.Data;

/**
 * ${notes}
 * @author ${author}
 */
@Data
public class ${className} extends BaseEntity implements java.io.Serializable {

	<#list fields as field>
	private ${field.fieldType} ${field.field};//数据库字段:${field.column}  属性显示:${field.notes}

	<#if field.checkDate>
	private ${field.fieldType} ${field.field}Begin;//数据库字段:${field.column}  属性显示:${field.notes}
	private ${field.fieldType} ${field.field}End;//数据库字段:${field.column}  属性显示:${field.notes}
	</#if>
	</#list>
}
