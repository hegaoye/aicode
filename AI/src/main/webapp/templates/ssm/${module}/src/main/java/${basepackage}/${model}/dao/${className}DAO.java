/*
 *${copyright}
 */

package ${basePackage}.dao;

import com.aixin.core.base.BaseMybatisDAOImpl;
import com.aixin.elder.entity.${className};
import org.springframework.stereotype.Repository;

/**
 * ${notes}
 * @author ${author}
 */
@Repository
public class ${className}DAO extends BaseMybatisDAOImpl<${className},Long>{

<#if (pkFields?size>0)>
<#list pkFields as pkField>
    /**
     * 加载一个对象${className} 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return ${className}
     */
    public ${className} loadBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field}) {
            return getSqlSessionTemplate().selectOne(this.sqlmapNamespace+".load${pkField.field?cap_first}",${pkField.field});
    }

    /**
     * 删除对象${className} 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return ${className}
     */
    public void deleteBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field}) {
             getSqlSessionTemplate().delete(this.sqlmapNamespace+".delete",${pkField.field});
    }

    /**
     * 更新对象${className} 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return ${className}
     */
    public void updateBy${pkField.field?cap_first}(${className} ${className?uncap_first}) {
             getSqlSessionTemplate().update(this.sqlmapNamespace+".update",${className?uncap_first});
    }

    /**
     * 查询${className}列表 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return List<${className}>
     */
    public List<${className}> listBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field}) {
            return getSqlSession().selectList(this.sqlmapNamespace + ".list", ${pkField.field})
    }

    /**
     * 查询${className}列表 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return List<${className}>
     */
    public List<${className}> listBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field},int offset,int limit) {
            return getSqlSession().selectList(this.sqlmapNamespace + ".list", ${pkField.field}, new RowBounds(offset, limit))
    }
</#list>
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
            return getSqlSession().selectList(this.sqlmapNamespace + ".list", map)
    }

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}Begin(${field.fieldType} ${field.field},int offset,int limit) {
                   Map<String,Object> map=new HashMap<>();
                   map.put("${field.field}Begin",${field.field});
            return getSqlSession().selectList(this.sqlmapNamespace + ".list", ${field.field}, new RowBounds(offset, limit))
    }

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}End(${field.fieldType} ${field.field}) {
                   Map<String,Object> map=new HashMap<>();
                   map.put("${field.field}End",${field.field});
            return getSqlSession().selectList(this.sqlmapNamespace + ".list", map)
    }

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}End(${field.fieldType} ${field.field},int offset,int limit) {
                   Map<String,Object> map=new HashMap<>();
                   map.put("${field.field}End",${field.field});
            return getSqlSession().selectList(this.sqlmapNamespace + ".list", map, new RowBounds(offset, limit))
    }

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}Begin
     * @param ${field.field} ${field.notes}End
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}(${field.fieldType} ${field.field}Begin,${field.fieldType} ${field.field}End) {
                   Map<String,Object> map=new HashMap<>();
                   map.put("${field.field}Begin",${field.field}Begin);
                   map.put("${field.field}End",${field.field}End);
            return getSqlSession().selectList(this.sqlmapNamespace + ".list", map)
    }

    /**
     * 根据时间查询${className}列表 通过${field.field}
     * @param ${field.field} ${field.notes}Begin
     * @param ${field.field} ${field.notes}End
     * @return List<${className}>
     */
    public List<${className}> listBy${field.field?cap_first}(${field.fieldType} ${field.field}Begin,${field.fieldType} ${field.field}End,int offset,int limit) {
                   Map<String,Object> map=new HashMap<>();
                   map.put("${field.field}Begin",${field.field}Begin);
                   map.put("${field.field}End",${field.field}End);
            return getSqlSession().selectList(this.sqlmapNamespace + ".list", map, new RowBounds(offset, limit))
    }
  </#if>
</#list>


}
