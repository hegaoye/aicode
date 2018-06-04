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
	<#if field.field!='id'>
	//数据库字段:${field.column}  属性显示:${field.notes}
	private ${field.fieldType} ${field.field};
	</#if>
	<#if field.checkDate>
	//数据库字段:${field.column}  属性显示:${field.notes}
	private ${field.fieldType} ${field.field}Begin;
	//数据库字段:${field.column}  属性显示:${field.notes}
	private ${field.fieldType} ${field.field}End;
	</#if>
	</#list>
}
