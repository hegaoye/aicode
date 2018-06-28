/*
 * ${copyright}
 */
package ${basePackage}.${model}.dao;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import ${basePackage}.core.base.BaseDAO;
import ${basePackage}.${model}.entity.${className}State;
import ${basePackage}.${model}.entity.${className};

/**
 * ${notes}
 *
 * @author ${author}
 */
@Mapper
@Repository
public  interface ${className}DAO extends BaseDAO<${className}, Long> {

<#if (pkFields?size>0)>
    <#list pkFields as pkField>
    <#if (pkFields?size>1)>
    /**
     * 加载一个对象${className} 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return ${className}
     */
     ${className} loadBy${pkField.field?cap_first}(@Param("${pkField.field}") ${pkField.fieldType} ${pkField.field});
    </#if>
    </#list>

    <#list pkFields as pkField>
    /**
    * 根据主键${pkField.field},oldStates 共同更新 ${className} 的状态到newState状态
    *
    * @param ${pkField.field} ${pkField.notes}
    * @param newState 新状态
    * @param oldStates 旧状态集合
    */
    void updateStateBy${pkField.field?cap_first}(@Param("${pkField.field}") ${pkField.fieldType} ${pkField.field},@Param("newState") ${className}State newState,@Param("oldState") ${className}State... oldStates);
    </#list>

    <#list pkFields as pkField>
    /**
    * 根据主键${pkField.field} 更新 ${className} 的状态到另一个状态
    *
    * @param ${pkField.field} ${pkField.notes}
    * @param state 状态
    */
    void updateBy${pkField.field?cap_first}(@Param("${pkField.field}") ${pkField.fieldType} ${pkField.field},@Param("state") ${className}State state);
    </#list>

    /**
     * 删除对象${className}
     *@param params 实体的属性
     */
     void delete(Map<String, Object> params);

</#if>

   /**
    * 查询${className}列表
    * @param ${classNameLower} 对象
    * @param rowBounds 分页参数
    * @return List<${className}>
    */
    List<${className}> list(RowBounds rowBounds);


   /**
    * 查询${className}分页 根据状态
    *
    <#list pkFields as pkField>
    * @param ${pkField.field}  ${pkField.notes}
    </#list>
    * @return List<${className}>
    */
    List<${className}> listByPk(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field},</#list>${className}State state,RowBounds rowBounds);
    int countByPk(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field},</#list>${className}State state);
}
