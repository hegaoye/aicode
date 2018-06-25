/*
 * ${copyright}
 */
package ${basePackage}.${model}.dao;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

import ${basePackage}.${model}.entity.${className};

/**
 * ${notes}
 *
 * @author ${author}
 */
@Mapper
@Repository
public   interface ${className}DAO {

    /**
     * 保存 ${className}
     *
     * @param ${classNameLower} 对象
     */
    void insert(${className} ${classNameLower});

    /**
     * 批量保存 ${className}
     *
     * @param ${classNameLower}s ${className}集合
     */
    void batchInsert(List<${className}> ${classNameLower}s);


    /**
     * 更新 ${className}
     *
     * @param ${classNameLower} 对象
     */
    void update(${className} ${classNameLower});



<#if (pkFields?size>0)>
    /**
     * 加载一个对象${className}
     <#list pkFields as pkField>
     *@param ${pkField.field} ${pkField.notes}
     </#list>
     * @return ${className}
     */
    ${className} load(<#list pkFields as pkField>@Param("${pkField.field}")${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>);


    <#list pkFields as pkField>
    <#if (pkFields?size>1)>
    /**
     * 加载一个对象${className} 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return ${className}
     */
     ${className} load(@Param("${pkField.field}") ${pkField.fieldType} ${pkField.field});
    </#if>

    /**
    * 根据主键${pkField.field},oldStates 共同更新 ${className} 的状态到newState状态
    *
    * @param ${classNameLower} 对象
    */
    void updateStateBy${field.field?cap_first}(@Param("${pkField.field}") ${pkField.fieldType} ${pkField.field},@Param("newState") ${className}State newState,@Param("oldState") ${className}State... oldStates);

    /**
    * 根据主键${pkField.field} 更新 ${className} 的状态到另一个状态
    *
    * @param ${classNameLower} 对象
    */
    void update(${className} ${classNameLower},@Param("states") ${className}State... states);

    /**
    * 根据主键${pkField.field} 更新 ${className} 的状态到另一个状态
    *
    * @param ${classNameLower} 对象
    */
    void update(@Param("${pkField.field}") ${pkField.fieldType} ${pkField.field},@Param("state") ${className}State state);

    </#list>

    /**
     * 删除对象${className}
      <#list pkFields as pkField>
     *@param ${pkField.field} ${pkField.notes}
      </#list>
     * @return ${className}
     */
     void delete(<#list pkFields as pkField>@Param("${pkField.field}") ${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>);

</#if>


    /**
    * 查询${className}列表 根据${classNameLower}的属性
    * @param ${classNameLower} 对象
    * @param rowBounds 分页参数
    * @return List<${className}>
    */
    List<${className}> list(${className} ${classNameLower},RowBounds rowBounds);

   /**
    * 查询${className}列表
    * @param ${classNameLower} 对象
    * @param rowBounds 分页参数
    * @return List<${className}>
    */
    List<${className}> list(RowBounds rowBounds);

   /**
    * 查询${className}列表 根据${classNameLower}的属性
    * @param ${classNameLower} 对象
    * @return List<${className}>
    */
    List<${className}> list(${className} ${classNameLower});

    /**
    * 查询${className}列表
    * @return List<${className}>
    */
    List<${className}> list();

    int count();
    int count(@Param(${className} ${classNameLower});


    /**
    * 查询${className}分页
    *
    <#list pkFields as pkField>
    * @param ${pkField.field}  ${pkField.notes}
    </#list>
    * @return List<${className}>
    */
    List<${className}> list(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field},</#list>RowBounds rowBounds);
    int count(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>);

   /**
    * 查询${className}分页 根据状态
    *
    <#list pkFields as pkField>
    * @param ${pkField.field}  ${pkField.notes}
    </#list>
    * @return List<${className}>
    */
    List<${className}> list(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field},</#list>${className}State state,RowBounds rowBounds);
    int count(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}</#list>${className}State state);
}
