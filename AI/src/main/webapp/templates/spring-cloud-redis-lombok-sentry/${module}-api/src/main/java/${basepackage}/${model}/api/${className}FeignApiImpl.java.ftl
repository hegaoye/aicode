/*
* ${copyright}
*/
package ${basePackage}.${model}.api;

import ${basePackage}.${model}.entity.${className};
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * ${notes} api请求熔断器 熔断实现
 * @author ${author}
 */
@Component
public class ${className}FeignApiImpl implements ${className}FeignApi {
    <#if (pkFields?size>0)>
    /**
     * 查询${className}一个详情信息
     <#list pkFields as pkField>
     * @param ${pkField.field} ${pkField.notes}
    </#list>
     * @return BeanRet
     */
    @Override
    public  ${className} load(<#list pkFields as pkField>@RequestParam("${pkField.field}") ${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>){
        return new ${className}();
    }

    <#list pkFields as pkField>

    /**
     * 根据条件${pkField.field}查询${className}一个详情信息
     *
     * @param ${pkField.field} ${pkField.notes}
     * @return BeanRet
     */
    @Override
    public   ${className} loadBy${pkField.field?cap_first}(@RequestParam("${pkField.field}") ${pkField.fieldType} ${pkField.field}){
        return new ${className}();
    }
    </#list>
    </#if>


    /**
     * 查询${className}信息集合
     *
     * @return 分页对象
     */
    @Override
    public    List<${className}> list(@RequestBody ${className} ${classNameLower},int curPage,int pageSize){
        return new ArrayList();
    }


    /**
     * 查询${className}信息集合
     *
     * @return 分页对象
     */
    @Override
    public   List<${className}> listByPk(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field},</#list>int curPage,int pageSize){
        return new ArrayList();
    }


    @Override
    public Integer count(<#list pkFields as pkField>@RequestParam("${pkField.field}") ${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>){
         return 0;
    }

    @Override
    public Integer count(@RequestBody ${className} ${classNameLower}) {
        return 0;
    }
    /**
     * 创建${className}
     *
     * @return BeanRet
     */
    @Override
    public   ${className} build(@RequestBody ${className} ${classNameLower}){
        return new ${className}();
    }


    /**
     * 修改${className}
     *
     * @return BeanRet
     */
    @Override
    public   ${className} modify(@RequestBody ${className} ${classNameLower}){
        return new ${className}();
    }

    /**
     * 删除${className}
     *
     * @return BeanRet
     */
    @Override
    public  void delete(<#list pkFields as pkField>@RequestParam("${pkField.field}") ${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>){
        return;
    }
}
