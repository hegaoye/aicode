/*
 * ${copyright}
 */
package ${basePackage}.${model}.ctrl;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;

import ${basePackage}.core.base.BaseCtrl;
import ${basePackage}.core.entity.BeanRet;
import ${basePackage}.core.entity.Page;
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.${model}.facade.${className}SV;
import ${basePackage}.${model}.entity.${className};



/**
 * ${notes}
 *
 * @author ${author}
 */
@Controller
@RequestMapping("/${classNameLower}")
@Api(value = "${notes}控制器", description = "${notes}控制器")
public class ${className}Ctrl extends BaseCtrl {

    @Resource
    private ${className}SV ${classNameLower}SV;
<#if (pkFields?size>0)>
    /**
     * 查询${className}一个详情信息
     * <#list pkFields as field>@param ${field.field} ${field.notes}</#list>
     * @return BeanRet
     */
    @ApiOperation(value = "查询${className}一个详情信息", notes = "查询${className}一个详情信息")
    @ApiImplicitParams({
            <#list pkFields as field>
            @ApiImplicitParam(name = "${field.field}", value = "${field.notes}", paramType = "query")<#if field_has_next>,</#if>
            </#list>
    })
    @GetMapping(value = "/load")
    @ResponseBody
    public BeanRet load(<#list pkFields as field>${field.fieldType} ${field.field}<#if field_has_next>,</#if></#list>) {
        <#list pkFields as field>
        Assert.hasText(${field.field}, BaseException.BaseExceptionEnum.Empty_Param.toString());
        </#list>
        ${className} ${classNameLower} = ${classNameLower}SV.load(<#list pkFields as field>${field.field}<#if field_has_next>,</#if></#list>);
        logger.info(JSON.toJSONString(${classNameLower}));
        return BeanRet.create(true, "查询详情信息", ${classNameLower});
    }


<#list pkFields as pkField>
    /**
     * 查询${className}一个详情信息
     *
     * @param ${pkField.field} ${pkField.notes}
     * @return BeanRet
     */
    @ApiOperation(value = "查询${className}一个详情信息", notes = "查询${className}一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "${pkField.field}", value = "${pkField.notes}", paramType = "query")
    })
    @GetMapping(value = "/load/${pkField.field}")
    @ResponseBody
    public BeanRet load(${pkField.fieldType} ${pkField.field}) {
        Assert.hasText(${pkField.field}, BaseException.BaseExceptionEnum.Empty_Param.toString());
        ${className} ${classNameLower} = ${classNameLower}SV.loadBy${pkField.field?cap_first}(${pkField.field});
        logger.info(JSON.toJSONString(${classNameLower}));
        return BeanRet.create(true, "查询详情信息", ${classNameLower});
    }
</#list>
</#if>


    /**
     * 查询${className}信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询${className}信息集合", notes = "查询${className}信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
            <#list fields as field>
            @ApiImplicitParam(name = "${field.field}", value = "${field.notes}", paramType = "query")<#if field_has_next>,</#if>
            </#list>
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(@ApiIgnore ${className} ${classNameLower},@ApiIgnore Page<${className}> page) {
        Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());
        List<${className}> ${classNameLower}s = ${classNameLower}SV.list(${classNameLower},page.genRowStart(),page.getPageSize());
        int total = ${classNameLower}SV.count(new HashedMap());
        page.setTotalRow(total);
        page.setVoList(${classNameLower}s);
        logger.info(JSON.toJSONString(page));
        return BeanRet.create(true, "", page);
    }

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
    public BeanRet build(@ApiIgnore ${className} ${classNameLower}) {
        <#list fields as field>
          Assert.hasText(${field.field}, BaseException.BaseExceptionEnum.Empty_Param.toString());
        </#list>

        ${classNameLower}SV.saveOrUpdate(${classNameLower});
        return BeanRet.create(true, "创建${className}成功", ${classNameLower});
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
    public BeanRet modify(@ApiIgnore ${className} ${classNameLower}) {
        <#list fields as field>
         Assert.hasText(${field.field}, BaseException.BaseExceptionEnum.Empty_Param.toString());
        </#list>

        ${classNameLower}SV.saveOrUpdate(${classNameLower});
        return BeanRet.create(true, "修改${className}成功", ${classNameLower});
    }

    /**
     * 删除${className}
     *
     * @return BeanRet
     */
    @ApiOperation(value = "删除${className}", notes = "删除${className}")
    @ApiImplicitParams({
            <#list pkFields as field>
            @ApiImplicitParam(name = "${field.field}", value = "${field.notes}", paramType = "query")<#if field_has_next>,</#if>
            </#list>
    })
    @DeleteMapping("/delete")
    @ResponseBody
    public BeanRet delete(<#list pkFields as field>${field.fieldType} ${field.field}<#if field_has_next>,</#if></#list>) {
        <#list pkFields as field>
         Assert.hasText(${field.field}, BaseException.BaseExceptionEnum.Empty_Param.toString());
        </#list>

        ${classNameLower}SV.delete(<#list pkFields as field>${field.field}<#if field_has_next>,</#if></#list>);
        return BeanRet.create(true, "删除${className}成功");
    }

}