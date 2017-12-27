<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first> 
package ${basepackage}.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ${basepackage}.entity.${className};

public interface ${className}SV extends BaseMybatisSV<${className},${table.idColumn.javaType}>{

}
