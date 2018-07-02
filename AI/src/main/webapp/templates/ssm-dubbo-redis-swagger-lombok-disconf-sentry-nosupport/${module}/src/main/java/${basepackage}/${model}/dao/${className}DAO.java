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

    /**
     * 加载一个对象${className}
     * <#list pkFields as pkField>@param ${pkField.field} ${pkField.notes}</#list>
     * @return ${className}
     */
    public ${className} load(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>) {
        Map<String,Object> map= new HashMap<String,Object>();
        <#list pkFields as pkField>
        map.put("${pkField.field}",${pkField.field});
        </#list>
        return getSqlSession().selectOne(this.sqlmapNamespace+".load",map);
    }




<#list pkFields as pkField>
    /**
     * 加载一个对象${className} 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return ${className}
     */
    public ${className} loadBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field}) {
            return getSqlSession().selectOne(this.sqlmapNamespace+".loadBy${pkField.field?cap_first}",${pkField.field});
    }

    /**
     * 查询${className}列表 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return List<${className}>
     */
    public List<${className}> listBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field}) {
            return getSqlSession().selectList(this.sqlmapNamespace + ".query", ${pkField.field});
    }

    /**
     * 查询${className}列表 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return List<${className}>
     */
    public List<${className}> listBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field},int offset,int limit) {
         return getSqlSession().selectList(this.sqlmapNamespace + ".query", ${pkField.field}, new RowBounds(offset, limit));
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


<#list fields as field>
  <#if field.checkDate>
    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}Begin(${field.fieldType} ${field.field}) {
                   Map<String,Object> map=new HashMap<>();
                   map.put("${field.field}Begin",${field.field});
            return getSqlSession().selectList(this.sqlmapNamespace + ".query", map);
    }

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}Begin(${field.fieldType} ${field.field},int offset,int limit) {
                   Map<String,Object> map=new HashMap<>();
                   map.put("${field.field}Begin",${field.field});
            return getSqlSession().selectList(this.sqlmapNamespace + ".query", ${field.field}, new RowBounds(offset, limit));
    }

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}End(${field.fieldType} ${field.field}) {
                   Map<String,Object> map=new HashMap<>();
                   map.put("${field.field}End",${field.field});
            return getSqlSession().selectList(this.sqlmapNamespace + ".query", map);
    }

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}End(${field.fieldType} ${field.field},int offset,int limit) {
                   Map<String,Object> map=new HashMap<>();
                   map.put("${field.field}End",${field.field});
            return getSqlSession().selectList(this.sqlmapNamespace + ".query", map, new RowBounds(offset, limit));
    }

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field}Begin ${field.notes}Begin
     * @param ${field.field}End ${field.notes}End
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}(${field.fieldType} ${field.field}Begin,${field.fieldType} ${field.field}End) {
                   Map<String,Object> map=new HashMap<>();
                   map.put("${field.field}Begin",${field.field}Begin);
                   map.put("${field.field}End",${field.field}End);
            return getSqlSession().selectList(this.sqlmapNamespace + ".query", map);
    }

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field}Begin ${field.notes}Begin
     * @param ${field.field}End ${field.notes}End
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}(${field.fieldType} ${field.field}Begin,${field.fieldType} ${field.field}End,int offset,int limit) {
                   Map<String,Object> map=new HashMap<>();
                   map.put("${field.field}Begin",${field.field}Begin);
                   map.put("${field.field}End",${field.field}End);
            return getSqlSession().selectList(this.sqlmapNamespace + ".query", map, new RowBounds(offset, limit));
    }
  </#if>
</#list>


}
