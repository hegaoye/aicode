/*
 * ${copyright}
 */
package ${basePackage}.${model}.facade;

import java.util.List;

import ${basePackage}.core.base.BaseMybatisSV;
import ${basePackage}.${model}.entity.${className};
import ${basePackage}.core.base.BeanRet;

/**
 * ${notes}
 *
 * @author ${author}
 */
public interface ${className}SV extends BaseMybatisSV<${className},Long>{

    /**
     * 添加对象${className}
     * @param ${className} ${classNameLower}
     * @return BeanRet
     */
     BeanRet insert(${className} ${classNameLower});
    /**
     * 修改对象${className}
     * @param ${className} ${classNameLower}
     * @return BeanRet
     */
     BeanRet modify(${className} ${classNameLower});


<#if (pkFields?size>0)>


    <#list pkFields as pkField>
    <#if pkField.field!='id'>
    /**
     * 加载对象${className} 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return ${className}
     */
     ${className} loadBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field});

     </#if>
   </#list>

    /**
     * 删除对象${className}
     * <#list pkFields as pkField>@param ${pkField.field} ${pkField.notes}</#list>
     * @return ${className}
     */
     void delete(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>);
</#if>



}
