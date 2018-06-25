package ${basePackage}.user.ctrl;


import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import ${basePackage}.core.entity.BeanRet;
import ${basePackage}.core.entity.Page;
import ${basePackage}.core.tools.redis.RedisUtils;
import ${basePackage}.${model}.entity.${className};
import ${basePackage}.${model}.service.${className}FeignSV;
/**
 * ${notes} 控制器
 *
 * @author ${author}
 */
@RestController
@RequestMapping("/${classNameLower}")
@Slf4j
@Api(value = "${notes}控制器", description = "${notes}控制器")
public class ${className}Ctrl {

    @Resource
    protected RedisUtils redisUtils;
    @Autowired
    private ${className}FeignSV ${classNameLower}FeignSV;


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
            ${className} ${classNameLower} = ${classNameLower}SV.load(<#list pkFields as pkField>${pkField.field}<#if pkField_has_next>,</#if></#list>);
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
    @GetMapping(value = "/load/{${pkField.field}}")
    @ResponseBody
    public ${className} loadBy${pkField.field?cap_first}(@PathVariable ${pkField.fieldType} ${pkField.field}) {
        if(${pkField.field}==null){
          return null;
        }
        ${className} ${classNameLower} = ${classNameLower}SV.loadBy${pkField.field?cap_first}(${pkField.field});
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
    @GetMapping(value = "/list")
    @ResponseBody
    public List<${className}> list(@ApiIgnore ${className} ${classNameLower},@ApiIgnore Page<${className}> page) {
        if(page==null){
          return BeanRet.create();
        }
        List<${className}> ${classNameLower}s = ${classNameLower}SV.list(${classNameLower},page.genRowStart(),page.getPageSize());
        int total = ${classNameLower}SV.count(${classNameLower});
        page.setTotalRow(total);
        page.setVoList();
        logger.info(JSON.toJSONString(page));
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
    public List<${className}> listByPk(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field},</#list>@ApiIgnore Page<${className}> page) {
        if(page==null){
            return BeanRet.create();
        }
        List<${className}> ${classNameLower}s = ${classNameLower}SV.list(<#list pkFields as pkField>${pkField.field},</#list> page.genRowStart(),page.getPageSize());
        int total = ${classNameLower}SV.count(<#list pkFields as pkField>${pkField.field}<#if pkField_has_next>,</#if></#list>);
        page.setTotalRow(total);
        logger.info(JSON.toJSONString(page));
        return ${classNameLower}s;
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
        ${classNameLower}SV.update(${classNameLower});
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
