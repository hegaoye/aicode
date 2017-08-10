<#include "java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first> 
package ${basepackage}.${subpackage}.service;

import javacommon.mock.MockOpenSessionInViewFilter;

<#include "java_imports.include">

public class ${className}ManagerTest extends BaseManagerTestCase{

	private ${className}Manager manager;
	/**通过spring注入${className}Dao*/
	public void set${className}Manager(${className}Manager manager) {
		this.manager = manager;
	}
	@Override
	public void onTearDownInTransaction() throws Exception {
		super.onTearDownInTransaction();
		this.manager.getEntityDao().flush();
	}
	
	public void testSave() {
		${className} obj = new ${className}();
		
		<#list table.columns as column>
	  		<#if column.isNotIdOrVersionField>
	  	obj.set${column.columnName}(new ${column.javaType}("1"));
			</#if>
		</#list>
		manager.save(obj);		
	}
}
