package io.aicode.project.ctrl;

import com.baidu.fsg.uid.UidGenerator;
import io.aicode.core.base.BaseCtrl;
import io.aicode.core.entity.BeanRet;
import io.aicode.core.enums.YNEnum;
import io.aicode.core.exceptions.BaseException;
import io.aicode.project.entity.MapClassTable;
import io.aicode.project.entity.MapRelationship;
import io.aicode.project.service.MapRelationshipSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 模型关系控制器
 *
 * @author berton
 */
@Controller
@RequestMapping("/project/relationship")
@Api(value = "模型关系控制器", description = "模型关系控制器")
public class MapRelationshipCtrl extends BaseCtrl {

    @Resource
    private MapRelationshipSV mapRelationshipSV;

    @Resource
    private UidGenerator uidGenerator;

    /**
     * 创建或修改模式关系
     */
    @ApiOperation(value = "创建或修改模式关系", notes = "创建或修改模式关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapClassTableCode", value = "关联编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "oneToOne", value = "一对一", required = true, paramType = "query"),
            @ApiImplicitParam(name = "oneToMany", value = "一对多", required = true, paramType = "query"),
            @ApiImplicitParam(name = "associateCode", value = "被关联编码:多个使用逗号隔开", required = true, paramType = "query"),
    })
    @PostMapping("/build")
    @ResponseBody
    public BeanRet build(String mapClassTableCode, String associateCode, YNEnum oneToOne, YNEnum oneToMany) {
        Assert.hasText(mapClassTableCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(associateCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Map<String, Object> params;
        MapRelationship mapRelationship;
        String[] arrayCode = associateCode.split(",");
        for (String code : arrayCode) {
            params = new HashMap<>();
            params.put("mapClassTableCode", mapClassTableCode);
            params.put("associateCode", code);
            mapRelationship = mapRelationshipSV.load(params);
            if (mapRelationship == null) {
                mapRelationship = new MapRelationship();
                mapRelationship.setCode(String.valueOf(uidGenerator.getUID()));
                mapRelationship.setMapClassTableCode(mapClassTableCode);
                mapRelationship.setAssociateCode(code);
            } else {
                //一对一，一对多是互斥行为，不能同时出现两种模型关系
                if(YNEnum.Y == YNEnum.getYN(mapRelationship.getIsOneToOne()) && YNEnum.Y == oneToMany) {
                    return BeanRet.create(false, "不能同时设置两种模型关系");
                }
                if(YNEnum.Y == YNEnum.getYN(mapRelationship.getIsOneToMany()) && YNEnum.Y == oneToOne) {
                    return BeanRet.create(false, "不能同时设置两种模型关系");
                }
            }
            mapRelationship.setIsOneToOne(oneToOne.name());
            mapRelationship.setIsOneToMany(oneToMany.name());
            mapRelationshipSV.saveOrUpdate(mapRelationship);
        }
        return BeanRet.create(true, "成功");
    }

    /**
     * 查询模型关系列表
     *
     * @param classTableCode 表名
     * @return BeanRet
     */
    @ApiOperation(value = "查询模型关系列表", notes = "查询模型关系列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "classTableCode", value = "类表映射编码", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(String classTableCode) {
        Assert.hasText(classTableCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        List<MapRelationship> relationships = mapRelationshipSV.listByProjectCode(classTableCode);
        return BeanRet.create(true, "查询成功", relationships);
    }

    /**
     * 查询类表映射关系列表
     *
     * @param projectCode 项目名
     * @return BeanRet
     */
    @ApiOperation(value = "查询类表映射关系列表", notes = "查询类表映射关系列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目名", required = true, paramType = "query"),
    })
    @GetMapping(value = "/listMapClassTable")
    @ResponseBody
    public BeanRet listMapClassTable(String projectCode) {
        Assert.hasText(projectCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        List<MapClassTable> mapRelationships = mapRelationshipSV.listMapClassTable(projectCode);
        return BeanRet.create(true, "查询成功", mapRelationships);
    }

    /**
     * 查询模型关系列表
     *
     * @param classTableCode 类表映射编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询模型关系列表", notes = "查询模型关系列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "classTableCode", value = "类表映射编码", required = true, paramType = "query"),
    })
    @GetMapping(value = "/listByClassTableCode")
    @ResponseBody
    public BeanRet listByProjectCode(String classTableCode) {
        Assert.hasText(classTableCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        List<MapRelationship> relationships = mapRelationshipSV.listByProjectCode(classTableCode);
        return BeanRet.create(true, "查询成功", relationships);
    }


    /**
     * 删除模型关系
     *
     * @param codes 表名
     * @return BeanRet
     */
    @ApiOperation(value = "删除关系模型", notes = "删除关系模型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "codes", value = "编码，多个编码使用逗号隔开", required = true, paramType = "query"),
    })
    @DeleteMapping(value = "/delete")
    @ResponseBody
    public BeanRet delete(String codes) {
        Assert.hasText(codes, BaseException.BaseExceptionEnum.Empty_Param.toString());
        String[] array = codes.split(",");
        for (String code : array) {
            mapRelationshipSV.deleteByCode(code);
        }
        return BeanRet.create(true, "删除成功");
    }


}
