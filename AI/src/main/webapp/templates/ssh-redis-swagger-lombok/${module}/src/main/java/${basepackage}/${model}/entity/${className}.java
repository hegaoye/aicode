/*
 * ${copyright}
 */
package ${basePackage}.${model}.entity;

import ${basePackage}.core.base.BaseEntity;
import lombok.Data;
import javax.persistence.*;
/**
 * ${notes}
 * @author ${author}
 */
@Data
@Entity
@Table(name = "${tableName}")
public class ${className} extends BaseEntity implements java.io.Serializable {

	<#list fields as field>
		<#if field.field != 'id'>
	private ${field.fieldType} ${field.field};//数据库字段:${field.column}  属性显示:${field.notes}
		</#if>
	</#list>
}
