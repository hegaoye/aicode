package io.aicode.project.ctrl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.fsg.uid.UidGenerator;
import io.aicode.base.BaseCtrl;
import io.aicode.base.core.BeanRet;
import io.aicode.base.enums.YNEnum;
import io.aicode.base.exceptions.BaseException;
import io.aicode.base.tools.StringTools;
import io.aicode.project.entity.MapClassTable;
import io.aicode.project.entity.MapFieldColumn;
import io.aicode.project.entity.MapRelationship;
import io.aicode.project.service.MapFieldColumnSV;
import io.aicode.project.service.MapRelationshipSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
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
    private MapFieldColumnSV mapFieldColumnSV;

    @Resource
    private UidGenerator uidGenerator;

    /**
     * 创建或修改模型关系
     * 1.参数合法性验证
     * 2.查看主表和附表是否已经存在关系，存在关系不能再添加
     * 3.反向建立关联关系：一对一的反向建立一对一关系，一对多的反向建立一对一关系
     */
    @ApiOperation(value = "创建或修改模型关系", notes = "创建或修改模型关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapClassTableCode", value = "关联编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "associateCode", value = "被关联编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "oneToOne", value = "一对一", required = true, paramType = "query"),
            @ApiImplicitParam(name = "oneToMany", value = "一对多", required = true, paramType = "query"),
            @ApiImplicitParam(name = "mainField", value = "主表关联属性", required = true, paramType = "query"),
            @ApiImplicitParam(name = "joinField", value = "从表关联属性", required = true, paramType = "query"),
    })
    @PostMapping("/build")
    @ResponseBody
    public BeanRet build(String mapClassTableCode, String associateCode, YNEnum oneToOne, YNEnum oneToMany, String mainField, String joinField) {
        Assert.hasText(mapClassTableCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(associateCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(mainField, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(joinField, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Map<String, Object> params;
        MapRelationship mapRelationship;
        //删除之前数据
        params = new HashMap<>();
        params.put("mapClassTableCode", mapClassTableCode);
        params.put("associateCode", associateCode);
        mapRelationship = this.load(mapClassTableCode, associateCode);
        mapRelationship.setIsOneToOne(oneToOne.name());
        mapRelationship.setIsOneToMany(oneToMany.name());
        mapRelationship.setMainField(mainField);
        mapRelationship.setJoinField(joinField);
        mapRelationshipSV.saveOrUpdate(mapRelationship);
        //反向建立关联关系
        MapRelationship mapRelationshipFlag = this.load(associateCode, mapClassTableCode);
        mapRelationshipFlag.setMainField(joinField);
        mapRelationshipFlag.setJoinField(mainField);
        mapRelationshipFlag.setIsOneToOne(YNEnum.Y.name());
        mapRelationshipFlag.setIsOneToMany(YNEnum.N.name());
        mapRelationshipSV.saveOrUpdate(mapRelationshipFlag);
        return BeanRet.create(true, "成功");
    }

    /**
     * 查询关系是否存在，不存在的情况下，创建一个实体返回
     *
     * @param mapClassTableCode 主表code
     * @param associateCode     附表code
     * @return MapRelationship
     */
    private MapRelationship load(String mapClassTableCode, String associateCode) {
        Map<String, Object> params;
        MapRelationship mapRelationship;
        params = new HashMap<>();
        params.put("mapClassTableCode", mapClassTableCode);
        params.put("associateCode", associateCode);
        mapRelationship = mapRelationshipSV.load(params);
        if (mapRelationship != null) {
            mapRelationshipSV.deleteByCode(mapRelationship.getCode());
        }
        mapRelationship = new MapRelationship();
        mapRelationship.setCode(String.valueOf(uidGenerator.getUID()));
        mapRelationship.setMapClassTableCode(mapClassTableCode);
        mapRelationship.setAssociateCode(associateCode);
        return mapRelationship;
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

    /**
     * 查询字段信息
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询字段信息--设置表的关联关系时使用", notes = "查询字段信息--设置表的关联关系时使用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapClassTableCode", value = "映射编码", paramType = "query"),
            @ApiImplicitParam(name = "associateCode", value = "被关联编码", paramType = "query"),
    })
    @GetMapping(value = "/listMapFieldColumn")
    @ResponseBody
    public BeanRet listMapFieldColumn(String mapClassTableCode, String associateCode) {
        if (StringTools.isEmpty(mapClassTableCode)) {
            return BeanRet.create(false, "分页对象不能为空");
        }
        Map<String, Object> params = new HashedMap();
        JSONObject jsonObject = new JSONObject();
        params.put("mapClassTableCode", mapClassTableCode);
        List<MapFieldColumn> fields = mapFieldColumnSV.queryList(params);
        jsonObject.put("mainFields", fields);

        params.put("mapClassTableCode", associateCode);
        List<MapFieldColumn> associateFields = mapFieldColumnSV.queryList(params);
        jsonObject.put("associateFields", associateFields);
        //查询关联关系
        params.put("mapClassTableCode", mapClassTableCode);
        params.put("associateCode", associateCode);
        MapRelationship mapRelationship = mapRelationshipSV.load(params);
        jsonObject.put("mapRelationship", mapRelationship);
        return BeanRet.create(true, "查询成功", jsonObject);
    }

}
