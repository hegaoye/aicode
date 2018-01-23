/*
 * ${copyright}
 */
package ${basePackage}.${model}.facade;

import java.util.List;

import ${basePackage}.core.base.BaseMybatisSV;
import ${basePackage}.${model}.entity.${className};

/**
 * ${notes}
 *
 * @author ${author}
 */
public interface ${className}SV extends BaseMybatisSV<${className},Long>{

    /**
     * 加载一个对象${className}
     * 通过<#list pkFields as field>${field.field}<#if field_has_next>,</#if></#list>
     * <#list pkFields as field>@param ${field.field} ${field.notes}</#list>
     * @return ${className}
     */
    public ${className} load(<#list pkFields as field>${field.fieldType} ${field.field}<#if field_has_next>,</#if></#list>);

    <#if (pkFields?size>0)>
    <#list pkFields as field>
    /**
     * 加载一个对象${className} 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return ${className}
     */
    public ${className} loadBy${field.field?cap_first}(${field.fieldType} ${field.field});
    </#list>
    </#if>



    /**
     * 查询${className}分页
     *
     * @param ${classNameLower}  ${notes}
     * @param offset 查询开始行
     * @param limit  查询行数
     * @return List<${className}>
     */
    public List<${className}> list${className}(${className} ${classNameLower}, int offset, int limit);
    public int count(${className} ${classNameLower});

    <#list fields as field>
    <#if field.checkDate>
    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}Begin(${field.fieldType} ${field.field});
    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}Begin(${field.fieldType} ${field.field},int offset,int limit) ;

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}End(${field.fieldType} ${field.field});

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}End(${field.fieldType} ${field.field},int offset,int limit);

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field}Begin ${field.notes}Begin
     * @param ${field.field}End ${field.notes}End
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}(${field.fieldType} ${field.field}Begin,${field.fieldType} ${field.field}End) ;
    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field}Begin ${field.notes}Begin
     * @param ${field.field}End ${field.notes}End
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}(${field.fieldType} ${field.field}Begin,${field.fieldType} ${field.field}End,int offset,int limit);
    </#if>
    </#list>
}
