/*
 * ${copyright}
 */
package ${basePackage}.${model}.service;

import java.util.List;

import ${basePackage}.core.base.BaseMybatisSV;
import ${basePackage}.${model}.entity.${className};

/**
 * ${notes}
 *
 * @author ${author}
 */
public interface ${className}SV extends BaseMybatisSV<${className},Long>{


<#if (pkFields?size>0)>

    /**
     * 加载一个对象${className}
     * <#list pkFields as pkField>@param ${pkField.field} ${pkField.notes}</#list>
     * @return ${className}
     */
     ${className} load(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>);

    <#list pkFields as pkField>
    /**
     * 加载一个对象${className} 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return ${className}
     */
     ${className} loadBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field});

   </#list>

    /**
     * 删除对象${className}
     * <#list pkFields as pkField>@param ${pkField.field} ${pkField.notes}</#list>
     * @return ${className}
     */
     void delete(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>);
</#if>



    /**
     * 查询${className}分页
     *
     * @param ${classNameLower}  ${notes}
     * @param offset 查询开始行
     * @param limit  查询行数
     * @return List<${className}>
     */
     List<${className}> list(${className} ${classNameLower}, int offset, int limit);
     int count(${className} ${classNameLower});

     /**
     * 查询${className}分页
     *
     <#list pkFields as pkField>* @param ${pkField.field}  ${pkField.notes}</#list>
     * @param offset 查询开始行
     * @param limit  查询行数
     * @return List<${className}>
     */
     List<${className}> list(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field},</#list>int offset, int limit);
     int count(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>);

    <#list fields as field>
    <#if field.checkDate>
    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return List<${className}>
     */
     List<${className}> listBy${field.field?cap_first}Begin(${field.fieldType} ${field.field});
    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return List<${className}>
     */
     List<${className}> listBy${field.field?cap_first}Begin(${field.fieldType} ${field.field},int offset,int limit) ;

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return List<${className}>
     */
     List<${className}> listBy${field.field?cap_first}End(${field.fieldType} ${field.field});

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return List<${className}>
     */
     List<${className}> listBy${field.field?cap_first}End(${field.fieldType} ${field.field},int offset,int limit);

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field}Begin ${field.notes}Begin
     * @param ${field.field}End ${field.notes}End
     * @return List<${className}>
     */
     List<${className}> listBy${field.field?cap_first}(${field.fieldType} ${field.field}Begin,${field.fieldType} ${field.field}End) ;
    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field}Begin ${field.notes}Begin
     * @param ${field.field}End ${field.notes}End
     * @return List<${className}>
     */
     List<${className}> listBy${field.field?cap_first}(${field.fieldType} ${field.field}Begin,${field.fieldType} ${field.field}End,int offset,int limit);
    </#if>
    </#list>
}
