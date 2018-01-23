/*
 * ${copyright}
 */
package ${basePackage}.${model}.service;

import ${basePackage}.core.base.BaseMybatisDAO;
import ${basePackage}.core.base.BaseMybatisSVImpl;
import ${basePackage}.core.entity.Page;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baidu.fsg.uid.UidGenerator;
import java.util.Map;
import java.util.List;
import ${basePackage}.${model}.facade.${className}SV;
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

	/**
	 * 加载一个对象${className}
	 * 通过<#list pkFields as field>${field.field}<#if field_has_next>,</#if></#list>
	 * <#list pkFields as field>@param ${field.field} ${field.notes}</#list>
	 * @return ${className}
	 */
	public ${className} load(<#list pkFields as field>${field.fieldType} ${field.field}<#if field_has_next>,</#if></#list>) {
		return ${classNameLower}DAO.load(<#list pkFields as field>${field.fieldType} ${field.field}<#if field_has_next>,</#if></#list>);
	}

<#if (pkFields?size>0)>
	<#list pkFields as field>
	/**
	 * 加载一个对象${className} 通过${field.field}
	 * @param ${field.field} ${field.notes}
	 * @return ${className}
	 */
     public ${className} loadBy${field.field?cap_first}(${field.fieldType} ${field.field}) {
		return ${classNameLower}DAO.loadBy${field.field?cap_first}(${field.field});
	 }
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
	@Override
	public List<${className}> list${className}(${className} ${classNameLower}, int offset, int limit) {
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
		  map = Maps.newHashMap();
		}
		map.put("sortColumns", " createTime desc");
		return ${classNameLower}DAO.query(map, offset, limit);
	}

	@Override
	public int count(${className} ${classNameLower}) {
		Map<String, Object> map = null;
		if (${classNameLower} != null) {
		  map = JSON.parseObject(JSON.toJSONString(${classNameLower}));
		} else {
		  map = Maps.newHashMap();
		}

		return ${classNameLower}DAO.count(map);
	}

<#list fields as field>
<#if field.checkDate>
	/**
	 * 根据时间查询${className}列表 通过${field.field}
	 * @param ${field.field} ${field.notes}
	 * @return List<${className}>
	 */
	public List<${className}> listBy${field.field?cap_first}Begin(${field.fieldType} ${field.field}) {
		return ${classNameLower}DAO.listBy${field.field?cap_first}Begin(${field.field});
	}

	/**
	 * 根据时间查询${className}列表 通过${field.field}
	 * @param ${field.field} ${field.notes}
	 * @return List<${className}>
	 */
	public List<${className}> listBy${field.field?cap_first}Begin(${field.fieldType} ${field.field},int offset,int limit) {
		return ${classNameLower}DAO.listBy${field.field?cap_first}Begin(${field.field}, offset, limit);
	}

	/**
	 * 根据时间查询${className}列表 通过${field.field}
	 * @param ${field.field} ${field.notes}
	 * @return List<${className}>
	 */
	public List<${className}> listBy${field.field?cap_first}End(${field.fieldType} ${field.field}) {
		return ${classNameLower}DAO.listBy${field.field?cap_first}End(${field.field});
	}

	/**
	 * 根据时间查询${className}列表 通过${field.field}
	 * @param ${field.field} ${field.notes}
	 * @return List<${className}>
	 */
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
	public List<${className}> listBy${field.field?cap_first}(${field.fieldType} ${field.field}Begin,${field.fieldType} ${field.field}End) {
		return ${classNameLower}DAO.listBy${field.field?cap_first}(${field.field}Begin,${field.field}End);
	}

	/**
	 * 根据时间查询${className}列表 通过${field.field}
	 * @param ${field.field}Begin ${field.notes}Begin
	 * @param ${field.field}End ${field.notes}End
	 * @return List<${className}>
	 */
	public List<${className}> listBy${field.field?cap_first}(${field.fieldType} ${field.field}Begin,${field.fieldType} ${field.field}End,int offset,int limit) {
		if (offset < 0) {
		 offset = 0;
		}
		if (limit < 0) {
		 limit = Page.limit;
		}
		return ${classNameLower}DAO.listBy${field.field?cap_first}(${field.field}Begin,${field.field}End,int offset,int limit);
	}
</#if>
</#list>

}
