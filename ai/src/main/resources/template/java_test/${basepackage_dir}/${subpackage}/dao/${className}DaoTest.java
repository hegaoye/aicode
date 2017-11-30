<#include "java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>   
package ${basepackage}.${subpackage}.dao;

import java.util.List;

import javacommon.page.Page;
import javacommon.page.impl.PageInfoImpl;

<#include "java_imports.include">

public class ${className}DaoTest extends BaseDaoTestCase{
	
	private ${className}Dao dao;
	/**通过spring注入${className}Dao*/
	public void set${className}Dao(${className}Dao dao) {
		this.dao = dao;
	}
	
	@Override
	protected void onTearDownInTransaction() throws Exception {
		super.onTearDownInTransaction();
		dao.flush();
	}
	
	public void testFindByPageInfo() {
		int pageNumber = 1;
		int pageSize = 10;
		
		PageInfoImpl pageInfo = new PageInfoImpl();
		pageInfo.setPageNumber(pageNumber);
		pageInfo.setPageSize(pageSize);
		
		pageInfo.setSortingColumn(null);
		pageInfo.setSortingDirection("desc");
		
		<#list table.columns as column>
	  		<#if column.isNotIdOrVersionField>
		pageInfo.getFilters().put("${column.columnNameLower}", "1");
			</#if>
		</#list>
		
		Page page = dao.findByPageInfo(pageInfo);
		
		assertEquals(pageNumber,page.getLastPageNumber());
		assertEquals(pageSize,page.getPageSize());
		List resultList = (List)page.getThisPageElements();
		assertNotNull(resultList);
	}
	
}
