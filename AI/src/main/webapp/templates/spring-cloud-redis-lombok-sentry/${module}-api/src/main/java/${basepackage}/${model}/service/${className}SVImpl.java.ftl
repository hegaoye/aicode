/*
* ${copyright}
*/
package ${basePackage}.${model}.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

import ${basePackage}.${model}.api.${className}FeignAPI;
import ${basePackage}.${model}.entity.${className};
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.core.exceptions.${className}Exception;
import ${basePackage}.${model}.entity.${className};

/**
 * ${notes}
 * @author ${author}
 */
@Service("${classNameLower}SVImpl")
@Slf4j
public class ${className}SVImpl {

    @Autowired
    private ${className}FeignAPI ${classNameLower}FeignAPI;

<#if (pkFields?size>0)>
    /**
     * 加载一个对象${className}
     <#list pkFields as field>* @param ${field.field} ${field.notes}</#list>
     * @return ${className}
     */
    public ${className} load(<#list pkFields as field>${field.fieldType} ${field.field}<#if field_has_next>,</#if></#list>) {
        if(<#list pkFields as field>${field.field}==null<#if field_has_next>&&</#if></#list>){
            throw new ${className}Exception(BaseException.BaseExceptionEnum.Ilegal_Param);
        }
        return ${classNameLower}FeignAPI.load(<#list pkFields as field>${field.field}<#if field_has_next>,</#if></#list>);
    }
    <#list pkFields as pkField>
    /**
     * 加载一个对象${className} 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return ${className}
     */
    public ${className} loadBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field}) {
        if(${pkField.field}==null){
            throw new ${className}Exception(BaseException.BaseExceptionEnum.Ilegal_Param);
        }
        return ${classNameLower}FeignAPI.loadBy${pkField.field?cap_first}(${pkField.field});
    }

    </#list>

    /**
     * 删除对象${className}
     <#list pkFields as pkField>* @param ${pkField.field} ${pkField.notes}</#list>
     * @return ${className}
     */
    
    public void delete(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>) {
        if(<#list pkFields as field>${field.field}==null<#if field_has_next>&&</#if></#list>){
            throw new ${className}Exception(BaseException.BaseExceptionEnum.Ilegal_Param);
        }
        ${classNameLower}FeignAPI.delete(<#list pkFields as pkField>${pkField.field}<#if pkField_has_next>,</#if></#list>);
    }

</#if>


    /**
     * 查询${className}分页
     *
     * @param ${classNameLower}  对象
     * @param curPage 当前页
     * @param pageSize 分页条数
     * @return List<${className}>
     */
    public List<${className}> list(${className} ${classNameLower}, int curPage,int pageSize) {
        return ${classNameLower}FeignAPI.list(${classNameLower}, curPage,pageSize);
    }

    /**
     * 查询${className}分页
     *
     <#list pkFields as pkField>* @param ${pkField.field}  ${pkField.notes}</#list>
     * @param curPage 当前页
     * @param pageSize 分页条数
     * @return List<${className}>
     */
    public List<${className}> list(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field},</#list>int offset, int limit) {
        if (curPage < 0) {
           curPage = 1;
        }

        if (pageSize < 0) {
            pageSize = Page.limit;
        }

        return ${classNameLower}FeignAPI.list(<#list pkFields as pkField>${pkField.field},</#list>curPage,pageSize);
    }


    /**
     * 保存
     *
     * @param ${classNameLower} 实体
     * @throws BaseException
     */
    public void save(${className} ${classNameLower}) {
        if(${classNameLower}==null){ throw new ${className}Exception(BaseException.BaseExceptionEnum.Ilegal_Param);}
        ${classNameLower}FeignAPI.build(${classNameLower});
    }

    /**
     * 更新
     *
     * @param ${classNameLower} 实体
     * @throws BaseException
     */
    public void modify(${className} ${classNameLower}) {
        if(${classNameLower}==null){ throw new ${className}Exception(BaseException.BaseExceptionEnum.Ilegal_Param);}
        ${classNameLower}FeignAPI.modify(${classNameLower});
    }

}
