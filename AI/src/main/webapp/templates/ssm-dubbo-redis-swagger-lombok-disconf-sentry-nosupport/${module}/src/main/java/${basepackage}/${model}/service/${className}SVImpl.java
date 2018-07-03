/*
 * ${copyright}
 */
package ${basePackage}.${model}.service;

import org.springframework.stereotype.Component;
import com.baidu.fsg.uid.UidGenerator;
import com.alibaba.dubbo.config.annotation.Service;
import javax.annotation.Resource;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.alibaba.fastjson.JSON;
import ${basePackage}.core.base.BaseMybatisDAO;
import ${basePackage}.core.base.BaseMybatisSVImpl;
import ${basePackage}.core.entity.Page;
import ${basePackage}.${model}.service.${className}SV;
import ${basePackage}.${model}.dao.${className}DAO;
import ${basePackage}.${model}.entity.${className};

/**
 * ${notes}
 * @author ${author}
 */
@Component
@Service
public class ${className}SVImpl extends BaseMybatisSVImpl<${className},Long> implements ${className}SV{

	@Resource
	private ${className}DAO ${classNameLower}DAO;

	@Resource
	private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO(){
		return ${classNameLower}DAO;
	}


<#if (pkFields?size>0)>
	/**
	 * 加载一个对象${className}
	  <#list pkFields as field>* @param ${field.field} ${field.notes}</#list>
	 * @return ${className}
	 */
	@Override
	public ${className} load(<#list pkFields as field>${field.fieldType} ${field.field}<#if field_has_next>,</#if></#list>) {
		return ${classNameLower}DAO.load(<#list pkFields as field>${field.field}<#if field_has_next>,</#if></#list>);
	}
	<#list pkFields as pkField>
	/**
	 * 加载一个对象${className} 通过${pkField.field}
	 * @param ${pkField.field} ${pkField.notes}
	 * @return ${className}
	 */
     @Override
     public ${className} loadBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field}) {
		return ${classNameLower}DAO.loadBy${pkField.field?cap_first}(${pkField.field});
	 }

	</#list>

     /**
      * 删除对象${className}
	   <#list pkFields as pkField>* @param ${pkField.field} ${pkField.notes}</#list>
      * @return ${className}
      */
	 @Override
     public void delete(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>) {
		${classNameLower}DAO.delete(<#list pkFields as pkField>${pkField.field}<#if pkField_has_next>,</#if></#list>);
	 }

</#if>


	/**
	 * 查询${className}分页
	 *
	 * @param ${classNameLower}  ${notes}
	 * @param offset 查询开始行
	 * @param limit  查询行数
	 * @return List<${className}>
	 */
	@Override
	public List<${className}> list(${className} ${classNameLower}, int offset, int limit) {
		if (offset < 0) {
		  offset = 0;
		}

		if (limit < 0) {
		  limit = Page.limit;
		}

		Map<String, Object> map = null;
		if (${classNameLower} != null) {
		  map = JSON.parseObject(JSON.toJSONString(${classNameLower}));
		} else {
		  map = new HashMap<>();
		}
		return ${classNameLower}DAO.query(map, offset, limit);
	}

	@Override
	public int count(${className} ${classNameLower}) {
		Map<String, Object> map = null;
		if (${classNameLower} != null) {
		  map = JSON.parseObject(JSON.toJSONString(${classNameLower}));
		} else {
		  map = new HashMap<>();
		}

		return ${classNameLower}DAO.count(map);
	}


    /**
     * 查询${className}分页
	 *
     <#list pkFields as pkField>* @param ${pkField.field}  ${pkField.notes}</#list>
     * @param offset 查询开始行
     * @param limit  查询行数
     * @return List<${className}>
     */
    @Override
    public List<${className}> list(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field},</#list>int offset, int limit) {
		if (offset < 0) {
		  offset = 0;
		}

		if (limit < 0) {
		  limit = Page.limit;
		}

		Map<String, Object> map = new HashMap();
        <#list pkFields as pkField>
		if(${pkField.field}!=null){
 		   map.put("${pkField.field}",${pkField.field});
		}
        </#list>
		return ${classNameLower}DAO.query(map, offset, limit);
	}

    @Override
    public int count(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>) {
		Map<String, Object> map = new HashMap();
      <#list pkFields as pkField>
		if(${pkField.field}!=null){
		map.put("${pkField.field}",${pkField.field});
		}
      </#list>
		return ${classNameLower}DAO.count(map);
	}

<#list fields as field>
<#if field.checkDate>
	/**
	 * 根据时间查询${className}列表 通过${field.field}
	 * @param ${field.field} ${field.notes}
	 * @return List<${className}>
	 */
	@Override
	public List<${className}> listBy${field.field?cap_first}Begin(${field.fieldType} ${field.field}) {
		return ${classNameLower}DAO.listBy${field.field?cap_first}Begin(${field.field});
	}

	/**
	 * 根据时间查询${className}列表 通过${field.field}
	 * @param ${field.field} ${field.notes}
	 * @return List<${className}>
	 */
	@Override
	public List<${className}> listBy${field.field?cap_first}Begin(${field.fieldType} ${field.field},int offset,int limit) {
		return ${classNameLower}DAO.listBy${field.field?cap_first}Begin(${field.field}, offset, limit);
	}

	/**
	 * 根据时间查询${className}列表 通过${field.field}
	 * @param ${field.field} ${field.notes}
	 * @return List<${className}>
	 */
    @Override
	public List<${className}> listBy${field.field?cap_first}End(${field.fieldType} ${field.field}) {
		return ${classNameLower}DAO.listBy${field.field?cap_first}End(${field.field});
	}

	/**
	 * 根据时间查询${className}列表 通过${field.field}
	 * @param ${field.field} ${field.notes}
	 * @return List<${className}>
	 */
	@Override
	public List<${className}> listBy${field.field?cap_first}End(${field.fieldType} ${field.field},int offset,int limit) {
		if (offset < 0) {
		 offset = 0;
		}
		if (limit < 0) {
		 limit = Page.limit;
		}
		return ${classNameLower}DAO.listBy${field.field?cap_first}End(${field.field},offset,limit);
	}

	/**
	 * 根据时间查询${className}列表 通过${field.field}
	 * @param ${field.field}Begin ${field.notes}Begin
	 * @param ${field.field}End ${field.notes}End
	 * @return List<${className}>
	 */
	@Override
	public List<${className}> listBy${field.field?cap_first}(${field.fieldType} ${field.field}Begin,${field.fieldType} ${field.field}End) {
		return ${classNameLower}DAO.listBy${field.field?cap_first}(${field.field}Begin,${field.field}End);
	}

	/**
	 * 根据时间查询${className}列表 通过${field.field}
	 * @param ${field.field}Begin ${field.notes}Begin
	 * @param ${field.field}End ${field.notes}End
	 * @return List<${className}>
	 */
	@Override
	public List<${className}> listBy${field.field?cap_first}(${field.fieldType} ${field.field}Begin,${field.fieldType} ${field.field}End,int offset,int limit) {
		if (offset < 0) {
		 offset = 0;
		}
		if (limit < 0) {
		 limit = Page.limit;
		}
		return ${classNameLower}DAO.listBy${field.field?cap_first}(${field.field}Begin,${field.field}End, offset, limit);
	}
</#if>
</#list>



}
