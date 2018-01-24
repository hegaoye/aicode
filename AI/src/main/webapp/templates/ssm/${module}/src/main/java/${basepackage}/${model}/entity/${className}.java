/*
 * ${copyright}
 */
package ${basePackage}.${model}.entity;
import lombok.Data;

/**
 * ${notes}
 * @author ${author}
 */
@Data
public class ${className} implements java.io.Serializable {

	<#list fields as field>
	private ${field.fieldType} ${field.field};//数据库字段:${field.column}  属性显示:${field.notes}
	</#list>
}
