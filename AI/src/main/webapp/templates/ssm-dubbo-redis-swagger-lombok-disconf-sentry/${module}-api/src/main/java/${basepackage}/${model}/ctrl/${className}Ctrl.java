/*
 * ${copyright}
 */
package ${basePackage}.${model}.ctrl;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import springfox.documentation.annotations.ApiIgnore;
import com.alibaba.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;
import java.util.List;
import ${basePackage}.core.tools.redis.RedisUtils;
import ${basePackage}.core.base.BeanRet;
import ${basePackage}.core.base.Page;
import ${basePackage}.${model}.service.${className}SV;
import ${basePackage}.${model}.entity.${className};



/**
 * ${notes}
 *
 * @author ${author}
 */
@Controller
@RequestMapping("/${classNameLower}")
@Api(value = "${notes}控制器", description = "${notes}控制器")
public class ${className}Ctrl  {
 private final static Logger logger = LoggerFactory.getLogger(${className}Ctrl.class);

    @Resource
    protected RedisTemplate<String, Object> redisTemplate;
    @Resource
    protected RedisUtils redisUtils;

    @Reference
    private ${className}SV ${classNameLower}SV;
<#if (pkFields?size>0)>
    /**
     * 查询${className}一个详情信息
     * <#list pkFields as pkField>@param ${pkField.field} ${pkField.notes}</#list>
     * @return BeanRet
     */
    @ApiOperation(value = "查询${className}一个详情信息", notes = "查询${className}一个详情信息")
    @ApiImplicitParams({
            <#list pkFields as pkField>
            @ApiImplicitParam(name = "${pkField.field}", value = "${pkField.notes}", paramType = "query")<#if pkField_has_next>,</#if>
            </#list>
    })
    @GetMapping(value = "/load")
    @ResponseBody
    public BeanRet load(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>) {
        <#list pkFields as pkField>
        if(${pkField.field}==null){
          return BeanRet.create();
        }
        </#list>
        ${className} ${classNameLower} = ${classNameLower}SV.load(<#list pkFields as pkField>${pkField.field}<#if pkField_has_next>,</#if></#list>);
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
            @ApiImplicitParam(name = "${pkField.field}", value = "${pkField.notes}", paramType = "path")
    })
    @GetMapping(value = "/load/{${pkField.field}}")
    @ResponseBody
    public BeanRet loadBy${pkField.field?cap_first}(@PathVariable ${pkField.fieldType} ${pkField.field}) {
        if(${pkField.field}==null){
          return BeanRet.create();
        }
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
        if(page==null){
          return BeanRet.create();
        }
        List<${className}> ${classNameLower}s = ${classNameLower}SV.list(${classNameLower},page.genRowStart(),page.getPageSize());
        int total = ${classNameLower}SV.count(${classNameLower});
        page.setTotalRow(total);
        page.setVoList(${classNameLower}s);
        logger.info(JSON.toJSONString(page));
        return BeanRet.create(true, "", page);
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
    public BeanRet listByPk(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field},</#list>@ApiIgnore Page<${className}> page) {
        if(page==null){
          return BeanRet.create();
        }
        List<${className}> ${classNameLower}s = ${classNameLower}SV.list(<#list pkFields as pkField>${pkField.field},</#list> page.genRowStart(),page.getPageSize());
        int total = ${classNameLower}SV.count(<#list pkFields as pkField>${pkField.field}<#if pkField_has_next>,</#if></#list>);
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
        if(${classNameLower}.get${field.field?cap_first}()==null){
          return BeanRet.create();
        }
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
        if(${classNameLower}.get${field.field?cap_first}()==null){
        return BeanRet.create();
        }
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
