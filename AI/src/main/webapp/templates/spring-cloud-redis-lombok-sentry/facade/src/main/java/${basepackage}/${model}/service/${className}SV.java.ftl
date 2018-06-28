/*
 * ${copyright}
 */
package ${basePackage}.${model}.service;

import java.util.List;
import java.util.Map;
import ${basePackage}.core.base.BaseSV;
import ${basePackage}.${model}.entity.${className}State;
import ${basePackage}.${model}.entity.${className};

/**
 * ${notes}
 *
 * @author ${author}
 */
public interface ${className}SV extends BaseSV {

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


    <#list pkFields as pkField>
    /**
    * 根据主键${pkField.field},oldStates 共同更新 ${className} 的状态到newState状态
    *
    * @param ${pkField.field} ${pkField.notes}
    * @param newState 新状态
    * @param oldStates 旧状态集合
    */
    void updateStateBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field},${className}State newState,${className}State... oldStates);
    </#list>

    <#list pkFields as pkField>
    /**
    * 根据主键${pkField.field} 更新 ${className} 的状态到另一个状态
    *
    * @param ${pkField.field} ${pkField.notes}
    * @param state 状态
    */
    void updateBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field},${className}State state);
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



}
