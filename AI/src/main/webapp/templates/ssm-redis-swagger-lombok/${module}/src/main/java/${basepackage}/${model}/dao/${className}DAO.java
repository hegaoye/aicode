/*
 * ${copyright}
 */

package ${basePackage}.${model}.dao;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ${basePackage}.core.base.BaseMybatisDAOImpl;
import ${basePackage}.${model}.entity.${className};

/**
 * ${notes}
 * @author ${author}
 */
@Repository
public class ${className}DAO extends BaseMybatisDAOImpl<${className},Long>{

<#if (pkFields?size>0)>

<#list pkFields as pkField>
<#if pkField.field!='id'>
    /**
     * 加载一个对象${className} 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return ${className}
     */
    public ${className} loadBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field}) {
            return getSqlSession().selectOne(this.sqlmapNamespace+".loadBy${pkField.field?cap_first}",${pkField.field});
    }

    /**
     * 更新对象${className} 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return ${className}
     */
    public void updateBy${pkField.field?cap_first}(${className} ${classNameLower},${pkField.fieldType} ${pkField.field}) {
        if(${pkField.field}!=null){
           ${classNameLower}.set${pkField.field?cap_first}(${pkField.field});
        }
        getSqlSession().update(this.sqlmapNamespace+".update",${classNameLower});
    }
</#if>
</#list>


    /**
     * 删除对象${className}
     * <#list pkFields as pkField>@param ${pkField.field} ${pkField.notes}</#list>
     * @return ${className}
     */
     public void delete(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>) {
         Map<String,Object> map= new HashMap<String,Object>();
         <#list pkFields as pkField>
         if(${pkField.field}!=null){
            map.put("${pkField.field}",${pkField.field});
         }
         </#list>
         getSqlSession().delete(this.sqlmapNamespace+".delete",map);
    }

</#if>

}
