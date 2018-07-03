/*
* ${copyright}
*/
package ${basePackage}.${model}.ctrl;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import javax.annotation.Resource;
import java.util.List;
import java.util.HashMap;

import ${basePackage}.core.entity.BeanRet;
import ${basePackage}.core.entity.Page;
import ${basePackage}.core.tools.redis.RedisUtils;
import ${basePackage}.${model}.entity.${className};
import ${basePackage}.${model}.service.${className}SV;

/**
* ${notes} 控制器
*
* @author ${author}
*/
@RestController
@RequestMapping("/${className?uncap_first}")
@Slf4j
@Api(value = "${notes}控制器", description = "${notes}控制器")
public class ${className}Ctrl {

    @Resource
    protected RedisUtils redisUtils;

    @Resource
    private ${className}SV ${className?uncap_first}SV;

<#if (pkFields?size>0)>
    /**
    * 查询${className}一个详情信息
    <#list pkFields as pkField>
    * @param ${pkField.field} ${pkField.notes}
    </#list>
    * @return BeanRet
    */
    @ApiOperation(value = "查询${className}一个详情信息", notes = "查询${className}一个详情信息")
    @ApiImplicitParams({
        <#list pkFields as pkField>
        @ApiImplicitParam(name = "${pkField.field}", value = "${pkField.notes}",dataType = "${pkField.fieldType}", paramType = "query")<#if pkField_has_next>,</#if>
        </#list>
    })
    @GetMapping(value = "/load")
    @ResponseBody
    public ${className} load(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>) {
        <#list pkFields as pkField>
        if(${pkField.field}==null){
        return null;
        }
        </#list>
        ${className} ${classNameLower} = ${className?uncap_first}SV.load(<#list pkFields as pkField>${pkField.field}<#if pkField_has_next>,</#if></#list>);
        log.info(JSON.toJSONString(${classNameLower}));
        return ${classNameLower};
    }


    <#list pkFields as pkField>
    /**
    * 根据条件${pkField.field}查询${className}一个详情信息
    *
    * @param ${pkField.field} ${pkField.notes}
    * @return BeanRet
    */
    @ApiOperation(value = "查询${className}一个详情信息", notes = "查询${className}一个详情信息")
    @ApiImplicitParams({
       @ApiImplicitParam(name = "${pkField.field}", value = "${pkField.notes}",dataType = "${pkField.fieldType}", paramType = "path")
    })
    @GetMapping(value = "/load/${pkField.field}/{${pkField.field}}")
    @ResponseBody
    public ${className} loadBy${pkField.field?cap_first}(@PathVariable ${pkField.fieldType} ${pkField.field}) {
        if(${pkField.field}==null){
           return null;
        }
        ${className} ${classNameLower} = ${className?uncap_first}SV.loadBy${pkField.field?cap_first}(${pkField.field});
        log.info(JSON.toJSONString(${classNameLower}));
        return ${classNameLower};
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
    @PostMapping(value = "/list")
    @ResponseBody
    public List<${className}> list(@RequestBody @ApiIgnore ${className} ${classNameLower},Integer curPage,Integer pageSize) {
        Page<${className}> page=new Page<${className}>(pageSize,curPage);
        List<${className}> ${classNameLower}s = ${className?uncap_first}SV.list(${classNameLower},page.genRowStart(),page.getPageSize());
        int total = ${className?uncap_first}SV.count(${classNameLower});
        page.setTotalRow(total);
        log.info(JSON.toJSONString(page));
        return ${classNameLower}s;
    }


    /**
    * 查询${className}信息集合
    *
    * @return 分页对象
    */
    @ApiOperation(value = "查询${className}信息集合", notes = "查询${className}信息集合")
    @ApiImplicitParams({
    @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
    @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    <#list pkFields as pkField>
    @ApiImplicitParam(name = "${pkField.field}", value = "${pkField.notes}", paramType = "query")<#if pkField_has_next>,</#if>
    </#list>
    })
    @GetMapping(value = "/list/by")
    @ResponseBody
    public List<${className}> listByPk(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field},</#list>Integer curPage,Integer pageSize) {
        Page<${className}> page=new Page<${className}>(pageSize,curPage);
        List<${className}> ${classNameLower}s = ${className?uncap_first}SV.list(<#list pkFields as pkField>${pkField.field},</#list> page.genRowStart(),page.getPageSize());
        int total = ${className?uncap_first}SV.count(<#list pkFields as pkField>${pkField.field}<#if pkField_has_next>,</#if></#list>);
        page.setTotalRow(total);
        log.info(JSON.toJSONString(page));
        return ${classNameLower}s;
    }

    /**
    * 统计${className}信息数量根据主键
    *
    * @return 总条数
    */
    @ApiOperation(value = " 统计${className}信息数量根据主键", notes = " 统计${className}信息数量根据主键")
    @ApiImplicitParams({
    <#list pkFields as pkField>
    @ApiImplicitParam(name = "${pkField.field}", value = "${pkField.notes}", paramType = "query")<#if pkField_has_next>,</#if>
    </#list>
    })
    @GetMapping(value = "/count/by")
    @ResponseBody
    public Integer count(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>) {
        return ${className?uncap_first}SV.count(<#list pkFields as pkField>${pkField.field}<#if pkField_has_next>,</#if></#list>);
    }

    /**
    * 统计${className}信息数量
    *
    * @return 总条数
    */
    @ApiOperation(value = "统计${className}信息数量", notes = "统计${className}信息数量")
    @ApiImplicitParams({
    <#list fields as field>
    @ApiImplicitParam(name = "${field.field}", value = "${field.notes}", paramType = "query")<#if field_has_next>,</#if>
    </#list>
    })
    @PostMapping(value = "/count")
    @ResponseBody
    public Integer count(@RequestBody ${className} ${classNameLower}) {
        if(${classNameLower}==null){
            return ${className?uncap_first}SV.count(new HashMap());
        }else{
            return ${className?uncap_first}SV.count(${classNameLower});
        }
    }



    /**
    * 创建${className}
    *
    * @return BeanRet
    */
    @ApiOperation(value = "创建${className}", notes = "创建${className}")
    @ApiImplicitParams({
    <#list notPkFields as field>
        <#if !field.checkDate>
        @ApiImplicitParam(name = "${field.field}", value = "${field.notes}", paramType = "query")<#if field_has_next>,</#if>
        </#if>
    </#list>
    })
    @PostMapping("/build")
    @ResponseBody
    public ${className} build(@RequestBody @ApiIgnore ${className} ${classNameLower}) {
        ${className?uncap_first}SV.save(${classNameLower});
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
        ${className?uncap_first}SV.modify(${classNameLower});
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
        ${className?uncap_first}SV.delete(<#list pkFields as pkField>${pkField.field}<#if pkField_has_next>,</#if></#list>);
        return BeanRet.create(true, "删除${className}成功");
    }

}
