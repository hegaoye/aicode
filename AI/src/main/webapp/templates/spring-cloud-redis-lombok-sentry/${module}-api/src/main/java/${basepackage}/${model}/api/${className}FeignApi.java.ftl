/*
* ${copyright}
*/
package ${basePackage}.${model}.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

import ${basePackage}.${model}.entity.${className};

/**
 * ${notes} api请求熔断器
 * @author ${author}
 */
@FeignClient(value = "${projectName}-provider", fallback = ${className}FeignApiImpl.class)
public interface ${className}FeignApi {
<#if (pkFields?size>0)>
    /**
     * 查询${className}一个详情信息
     <#list pkFields as pkField>
     * @param ${pkField.field} ${pkField.notes}
    </#list>
     * @return BeanRet
     */
    @GetMapping(value = "/load")
    ${className} load(<#list pkFields as pkField>@RequestParam("${pkField.field}") ${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>);

    <#list pkFields as pkField>

    /**
     * 根据条件${pkField.field}查询${className}一个详情信息
     *
     * @param ${pkField.field} ${pkField.notes}
     * @return BeanRet
     */
    @GetMapping(value = "/load/${pkField.field}")
    ${className} loadBy${pkField.field?cap_first}(@RequestParam("${pkField.field}") ${pkField.fieldType} ${pkField.field});

    </#list>
    /**
     * 删除${className}
     *
     * @return BeanRet
     */
    @DeleteMapping("/delete")
    void delete(<#list pkFields as pkField>@RequestParam("${pkField.field}") ${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>);
</#if>


    /**
     * 查询${className}信息集合
     *
     * @return 分页对象
     */
    @PostMapping(value = "/list")
    List<${className}> list(@RequestBody ${className} ${classNameLower},int curPage,int pageSize);


    /**
     * 查询${className}信息集合
     *
     * @return 分页对象
     */
    @GetMapping(value = "/list/by")
    List<${className}> listByPk(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field},</#list>int curPage,int pageSize);

    /**
     * 创建${className}
     *
     * @return BeanRet
     */
    @PostMapping("/build")
    ${className} build(@RequestBody ${className} ${classNameLower});


    /**
     * 修改${className}
     *
     * @return BeanRet
     */
    @PutMapping("/modify")
    ${className} modify(@RequestBody ${className} ${classNameLower});



}
