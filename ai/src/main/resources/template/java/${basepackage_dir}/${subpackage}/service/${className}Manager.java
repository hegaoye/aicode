<#include "java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first> 
package ${basepackage}.${subpackage}.service;

<#include "java_imports.include">

import org.springframework.stereotype.Component;

@Component
public class ${className}Manager extends BaseManager{

	private ${className}Dao ${classNameLower}Dao;
	/**通过spring注入${className}Dao*/
	public void set${className}Dao(${className}Dao dao) {
		this.${classNameLower}Dao = dao;
	}
	public BaseDao getEntityDao() {
		return this.${classNameLower}Dao;
	}
	/**通过PageInfo查询*/
	public Page findByPageInfo(PageInfo info) {
		return ${classNameLower}Dao.findByPageInfo(info);
	}
	
<#list table.columns as column>
	<#if column.unique && !column.pk>
	public ${className} getBy${column.columnName}(${column.javaType} v) {
		return ${classNameLower}Dao.getBy${column.columnName}(v);
	}	
	</#if>
</#list>
}
