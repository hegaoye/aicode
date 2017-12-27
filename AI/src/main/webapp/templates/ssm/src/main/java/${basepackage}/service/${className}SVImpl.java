<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first> 
package ${basepackage}.service;

import com.aixin.core.base.BaseMybatisDAO;
import com.aixin.core.base.BaseMybatisSVImpl;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import com.baidu.fsg.uid.UidGenerator;

import ${basepackage}.dao.${className}DAO;
import ${basepackage}.entity.${className};


@Component
@Service
public class ${className}SVImpl extends BaseMybatisSVImpl<${className},${table.idColumn.javaType}> implements ${className}SV{

	@Resource
	private ${className}DAO ${classNameLower}DAO;

	@Resource
	private UidGenerator uidGenerator;

	@Override
	protected BaseMybatisDAO getBaseMybatisDAO() {
		return ${classNameLower}DAO;
	}

<#list table.columns as column>
	<#if column.unique && !column.pk>
	@Transactional(readOnly=true)
	public ${className} load${column.columnName}(${column.javaType} v) {
		return ${classNameLower}DAO.load${column.columnName}(v);
	}	
	
	</#if>
</#list>
}
