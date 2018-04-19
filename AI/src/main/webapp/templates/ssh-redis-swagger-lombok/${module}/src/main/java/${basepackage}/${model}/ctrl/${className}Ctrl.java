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
import ${basePackage}.core.base.BeanRet;
import ${basePackage}.core.base.Page;
import ${basePackage}.${model}.facade.${className}SV;
import ${basePackage}.${model}.entity.${className};
import ${basePackage}.core.tools.StringTools;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import org.apache.commons.lang.StringUtils;

/**
 * ${notes}
 *
 * @author ${author}
 */
@Controller
@RequestMapping("/${classNameLower}")
@Api(value = "${notes}控制器", description = "${notes}控制器")
public class ${className}Ctrl {
 private final static Logger logger = LoggerFactory.getLogger(${className}Ctrl.class);

    @Resource
    protected RedisTemplate<String, Object> redisTemplate;
    @Resource
    protected RedisUtils redisUtils;

    @Resource
    private ${className}SV ${classNameLower}SV;
<#if (pkFields?size>0)>
    /**
     * 查询${className}一个详情信息
     * @return BeanRet
     */
    @ApiOperation(value = "查询${className}详情信息", notes = "查询${className}详情信息")
    @ApiImplicitParams({
            <#list pkFields as pkField>
            <#if !pkField.checkDate>
                @ApiImplicitParam(name = "${pkField.field}", value = "${pkField.notes}", paramType = "query")<#if pkField_has_next>,</#if>
            </#if>
            </#list>
    })
    @GetMapping(value = "/load")
    @ResponseBody
    public BeanRet load(${className} ${classNameLower}) {
        ${classNameLower} = ${classNameLower}SV.query(${className}.class,${classNameLower});
        logger.info(JSON.toJSONString(${classNameLower}));
        return BeanRet.create(true, "查询详情信息", ${classNameLower});
    }
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
            <#if field.field!='id' && !field.checkDate>
                @ApiImplicitParam(name = "${field.field}", value = "${field.notes}", paramType = "query")<#if field_has_next>,</#if>
            </#if>
            </#list>
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(@ApiIgnore ${className} ${classNameLower},@ApiIgnore Page<${className}> page) {
        if(page==null){
          return BeanRet.create();
        }
        Map<String, Object> paras = new HashMap<>();
        //封装查询参数
<#list fields as field >
        <#if field.field!='id'  && !field.checkDate>
        if(StringTools.isNotEmpty(${classNameLower}.get${field.field?cap_first}())){
            paras.put("${field.field}", ${classNameLower}.get${field.field?cap_first}());
        }
        </#if>
</#list>
        page.setParams(paras);
        page = ${classNameLower}SV.queryPage(${className}.class,page);
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
            <#if field.field!='id'  && !field.checkDate>
            @ApiImplicitParam(name = "${field.field}", value = "${field.notes}", paramType = "query")<#if field_has_next>,</#if>
            </#if>
            </#list>
    })
    @PostMapping("/build")
    @ResponseBody
    public BeanRet build(@ApiIgnore ${className} ${classNameLower}) {
        <#list fields as field>
        <#if field.field!='id'  && !field.checkDate>
        if(StringTools.isEmpty(${classNameLower}.get${field.field?cap_first}())){
          return BeanRet.create();
        }
        </#if>
        <#if field.checkDate>
        ${classNameLower}.set${field.field?cap_first}(new Date());
        </#if>
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
            <#if !field.checkDate>
            @ApiImplicitParam(name = "${field.field}", value = "${field.notes}", paramType = "query")<#if field_has_next>,</#if>
            </#if>
            </#list>
    })
    @PutMapping("/modify")
    @ResponseBody
    public BeanRet modify(@ApiIgnore ${className} ${classNameLower}) {
        <#list fields as field>
        <#if !field.checkDate>
        if(StringTools.isEmpty(${classNameLower}.get${field.field?cap_first}())){
        return BeanRet.create();
        }
        </#if>
        <#if field.checkDate>
                ${classNameLower}.set${field.field?cap_first}(new Date());
        </#if>
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
            <#if !pkField.checkDate>
            @ApiImplicitParam(name = "${pkField.field}", value = "${pkField.notes}", paramType = "query")<#if pkField_has_next>,</#if>
            </#if>
            </#list>
    })
    @DeleteMapping("/delete")
    @ResponseBody
    public BeanRet delete(${className} ${classNameLower}) {
        ${classNameLower} = ${classNameLower}SV.query(${className}.class,${classNameLower});
        ${classNameLower}SV.delete(${classNameLower});
        return BeanRet.create(true, "删除${className}成功");
    }

}
