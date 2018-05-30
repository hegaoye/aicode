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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;
import java.util.List;
import ${basePackage}.core.tools.redis.RedisUtils;
import ${basePackage}.core.tools.StringTools;
import ${basePackage}.core.base.BeanRet;
import ${basePackage}.core.base.Page;
import ${basePackage}.core.base.BaseCtrl;
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
 private final static Logger logger = LoggerFactory.getLogger(${className}Ctrl.class);

    @Resource
    protected RedisTemplate<String, Object> redisTemplate;
    @Resource
    protected RedisUtils redisUtils;

    @Resource
    private ${className}SV ${classNameLower}SV;
<#if (pkFields?size>0)>


<#list pkFields as pkField>

    /**
     * 查询${className}详情信息
     *
     * @param ${pkField.field} ${pkField.notes}
     * @return BeanRet
     */
    @ApiOperation(value = "查询${className}详情信息", notes = "查询${className}详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "${pkField.field}", value = "${pkField.notes}", paramType = "query")
    })
    @GetMapping(value = "/loadBy${pkField.field?cap_first}")
    @ResponseBody
    public BeanRet loadBy${pkField.field?cap_first}(@PathVariable ${pkField.fieldType} ${pkField.field}) {
        if(StringTools.isEmpty(${pkField.field})){
          return BeanRet.create();
        }
<#if pkField.field!='id'>
        ${className} ${classNameLower} = ${classNameLower}SV.loadBy${pkField.field?cap_first}(${pkField.field});
</#if>
<#if pkField.field =='id'>
        ${className} ${classNameLower} = ${classNameLower}SV.load(${pkField.field});
</#if>
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
          return BeanRet.create(false,"分页对象不能为空");
        }
        page.setParams(JSON.parseObject(JSON.toJSONString(${classNameLower})));
        page = ${classNameLower}SV.getList(page);
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
            @ApiImplicitParam(name = "${field.field}", value = "${field.notes}", paramType = "query", required = true)<#if field_has_next>,</#if>
            </#list>
    })
    @PostMapping("/build")
    @ResponseBody
    public BeanRet build(@ApiIgnore ${className} ${classNameLower}) {
        return ${classNameLower}SV.insert(${classNameLower});
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
        return ${classNameLower}SV.modify(${classNameLower});
    }

    /**
     * 删除${className}
     *
     * @return BeanRet
     */
    @ApiOperation(value = "删除${className}", notes = "删除${className}")
    @ApiImplicitParams({
            <#list pkFields as pkField>
            @ApiImplicitParam(name = "${pkField.field}", value = "${pkField.notes}", paramType = "query", required = true)<#if pkField_has_next>,</#if>
            </#list>
    })
    @DeleteMapping("/delete")
    @ResponseBody
    public BeanRet delete(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>) {
        <#list pkFields as field>
                if(StringTools.isEmpty(${classNameLower}.get${field.field?cap_first}())){
                return BeanRet.create(false,"${field.notes}不能为空");
                }
        </#list>
        ${classNameLower}SV.delete(<#list pkFields as pkField>${pkField.field}<#if pkField_has_next>,</#if></#list>);
        return BeanRet.create(true, "删除${className}成功");
    }

}
