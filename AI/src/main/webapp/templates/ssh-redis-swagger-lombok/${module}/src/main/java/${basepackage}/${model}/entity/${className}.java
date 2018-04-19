/*
 * ${copyright}
 */
package ${basePackage}.${model}.entity;

import ${basePackage}.core.base.BaseEntity;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
/**
 * ${notes}
 * @author ${author}
 */
@Data
@Entity
@Table(name = "${tableName}")
public class ${className} extends BaseEntity implements java.io.Serializable {

	@Id
	@GeneratedValue
	public Long id;
	<#list fields as field>
		<#if field.field != 'id'>
	@Column(name = "${field.column}")
	private ${field.fieldType} ${field.field};//数据库字段:${field.column}  属性显示:${field.notes}
		</#if>
	</#list>
}
