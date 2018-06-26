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

@FeignClient(value = "${projectName}-provider", fallback = ${className}FeignAPIImpl.class)
public interface ${className}FeignAPI {
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
    </#if>


    /**
     * 查询${className}信息集合
     *
     * @return 分页对象
     */
    @GetMapping(value = "/list")
    List<${className}> list(@RequestBody ${className} ${classNameLower},@ApiIgnore Page<${className}> page);


    /**
     * 查询${className}信息集合
     *
     * @return 分页对象
     */
    @GetMapping(value = "/list/by")
    @ResponseBody
    List<${className}> listByPk(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field},</#list>@ApiIgnore Page<${className}> page);

    /**
     * 创建${className}
     *
     * @return BeanRet
     */
    @ApiOperation(value = "创建${className}", notes = "创建${className}")
    @ApiImplicitParams({
            <#list fields as field>
            @ApiImplicitParam(name = "${field.field}", value = "${field.notes}", paramType = "query")<#if field_has_next>,</#if>
            </#list>
            })
    @PostMapping("/build")
    @ResponseBody
    public ${className} build(@ApiIgnore ${className} ${classNameLower}) {
            ${classNameLower}SV.save(${classNameLower});
            return ${classNameLower};
            }


    /**
     * 修改${className}
     *
     * @return BeanRet
     */
    @ApiOperation(value = "修改${className}", notes = "修改${className}")
    @ApiImplicitParams({
            <#list fields as field>
            @ApiImplicitParam(name = "${field.field}", value = "${field.notes}", paramType = "query")<#if field_has_next>,</#if>
            </#list>
            })
    @PutMapping("/modify")
    @ResponseBody
    public ${className} modify(@ApiIgnore ${className} ${classNameLower}) {
            ${classNameLower}SV.modify(${classNameLower});
            return ${classNameLower};
            }

    /**
     * 删除${className}
     *
     * @return BeanRet
     */
    @ApiOperation(value = "删除${className}", notes = "删除${className}")
    @ApiImplicitParams({
            <#list pkFields as pkField>
            @ApiImplicitParam(name = "${pkField.field}", value = "${pkField.notes}", paramType = "query")<#if pkField_has_next>,</#if>
            </#list>
            })
    @DeleteMapping("/delete")
    @ResponseBody
    public BeanRet delete(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>) {
            ${classNameLower}SV.delete(<#list pkFields as pkField>${pkField.field}<#if pkField_has_next>,</#if></#list>);
            return BeanRet.create(true, "删除${className}成功");
            }

}
