/*
* ${copyright}
*/
package ${basePackage}.${model}.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
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
    @GetMapping(value = "/${className?uncap_first}/load")
    ${className} load(<#list pkFields as pkField>@RequestParam("${pkField.field}") ${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>);

    <#list pkFields as pkField>

    /**
     * 根据条件${pkField.field}查询${className}一个详情信息
     *
     * @param ${pkField.field} ${pkField.notes}
     * @return BeanRet
     */
    @GetMapping(value = "/${className?uncap_first}/load/${pkField.field}/{${pkField.field}}")
    ${className} loadBy${pkField.field?cap_first}(@RequestParam("${pkField.field}") ${pkField.fieldType} ${pkField.field});

    </#list>
    /**
     * 删除${className}
     *
     * @return BeanRet
     */
    @DeleteMapping("/${className?uncap_first}/delete")
    void delete(<#list pkFields as pkField>@RequestParam("${pkField.field}") ${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>);
</#if>


    /**
     * 查询${className}信息集合
     *
     * @return 分页对象
     */
    @PostMapping(value = "/${className?uncap_first}/list")
    List<${className}> list(@RequestBody ${className} ${className?uncap_first},@RequestParam("curPage") int curPage,@RequestParam("pageSize") int pageSize);


    /**
     * 查询${className}信息集合
     *
     * @return 分页对象
     */
    @GetMapping(value = "/${className?uncap_first}/list/by")
    List<${className}> listByPk(<#list pkFields as pkField>@RequestParam("${pkField.field}") ${pkField.fieldType} ${pkField.field},</#list>@RequestParam("curPage") int curPage,@RequestParam("pageSize") int pageSize);




    /**
    * 统计${className}信息数量根据主键
    *
    * @return 总条数
    */
    @GetMapping(value = "/${className?uncap_first}/count/by")
    Integer count(<#list pkFields as pkField>@RequestParam("${pkField.field}") ${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>);

    /**
    * 统计${className}信息数量
    *
    * @return 总条数
    */
    @PostMapping(value = "/${className?uncap_first}/count")
    Integer count(@RequestBody ${className} ${className?uncap_first});



    /**
     * 创建${className}
     *
     * @return BeanRet
     */
    @PostMapping("/${className?uncap_first}/build")
    ${className} build(@RequestBody ${className} ${className?uncap_first});


    /**
     * 修改${className}
     *
     * @return BeanRet
     */
    @PutMapping("/${className?uncap_first}/modify")
    ${className} modify(@RequestBody ${className} ${className?uncap_first});



}
