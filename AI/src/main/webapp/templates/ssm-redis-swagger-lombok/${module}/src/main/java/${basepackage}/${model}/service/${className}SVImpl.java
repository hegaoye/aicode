/*
 * ${copyright}
 */
package ${basePackage}.${model}.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.alibaba.fastjson.JSON;
import ${basePackage}.core.base.BaseMybatisDAO;
import ${basePackage}.core.base.BaseMybatisSVImpl;
import ${basePackage}.core.base.Page;
import ${basePackage}.${model}.facade.${className}SV;
import ${basePackage}.${model}.dao.${className}DAO;
import ${basePackage}.${model}.entity.${className};
import ${basePackage}.core.base.BeanRet;
import ${basePackage}.core.tools.StringTools;

/**
 * ${notes}
 * @author ${author}
 */
@Component
@Service
public class ${className}SVImpl extends BaseMybatisSVImpl<${className},Long> implements ${className}SV{


	@Resource
	private ${className}DAO ${classNameLower}DAO;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO(){
		return ${classNameLower}DAO;
	}

	/**
	 * 添加对象${className}
	 * @param ${className} ${classNameLower}
	 * @return ${className}
	 */
	@Override
	public BeanRet insert(${className} ${classNameLower}){
<#list fields as field>
		if(StringTools.isEmpty(${classNameLower}.get${field.field?cap_first}())){
		return BeanRet.create(false,"${field.notes}不能为空");
		}
		//判断值是否重复
		if(${classNameLower}SV.isExist("${field.field}",${classNameLower}.get${field.field?cap_first}())){
		return BeanRet.create(false,"${field.notes}重复");
		}
</#list>

		${classNameLower}=${classNameLower}DAO.insert(${classNameLower});
		return BeanRet.create(true, "创建${className}成功",${classNameLower});

	}

	/**
	 * 修改对象${className}
	 * @param ${className} ${classNameLower}
	 * @return ${className}
	 */
	@Override
	public BeanRet modify(${className} ${classNameLower}){
		//唯一标识字段
		Map map=new HashMap();
<#list pkFields as pkField>
<#if pkField.field!='id'>
		map.put("${pkField.field}",${classNameLower}.get${pkField.field?cap_first}());
</#if>
</#list>

<#list fields as field>
		if(StringTools.isEmpty(${classNameLower}.get${field.field?cap_first}())){
		return BeanRet.create(false,"${field.notes}不能为空");
		}
		//判断值是否重复
		if(${classNameLower}SV.isExistByConditions("${field.field}",${classNameLower}.get${field.field?cap_first}(),map)){
		return BeanRet.create(false,"${field.notes}重复");
		}
</#list>

		${classNameLower}=${classNameLower}DAO.update(${classNameLower});
		return BeanRet.create(true, "修改${className}成功",${classNameLower});
	}


<#if (pkFields?size>0)>

	<#list pkFields as pkField>
	<#if pkField.field!='id'>
	/**
	 * 加载对象${className} 通过${pkField.field}
	 * @param ${pkField.field} ${pkField.notes}
	 * @return ${className}
	 */
     @Override
     public ${className} loadBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field}) {
		return ${classNameLower}DAO.loadBy${pkField.field?cap_first}(${pkField.field});
	 }
	</#if>
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


}
