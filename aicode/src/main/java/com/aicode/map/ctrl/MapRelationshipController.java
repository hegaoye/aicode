/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.ctrl;

import com.aicode.core.entity.R;
import com.aicode.core.enums.YNEnum;
import com.aicode.core.exceptions.BaseException;
import com.aicode.map.entity.MapClassTable;
import com.aicode.map.entity.MapFieldColumn;
import com.aicode.map.entity.MapRelationship;
import com.aicode.map.service.MapClassTableService;
import com.aicode.map.service.MapFieldColumnService;
import com.aicode.map.service.MapRelationshipService;
import com.aicode.map.vo.MapRelationshipVO;
import com.aicode.project.entity.ProjectMap;
import com.aicode.project.service.ProjectMapService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 模型关系
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/project/relationship")
@Slf4j
@Api(value = "模型关系控制器", tags = "模型关系控制器")
public class MapRelationshipController {
    @Autowired
    private MapRelationshipService mapRelationshipService;

    @Autowired
    private ProjectMapService projectMapService;

    @Autowired
    private MapClassTableService mapClassTableService;

    @Autowired
    private MapFieldColumnService mapFieldColumnService;

    @Autowired
    private UidGenerator uidGenerator;

    /**
     * 创建 模型关系
     *
     * @return R
     */
    @ApiOperation(value = "创建MapRelationship", notes = "创建MapRelationship")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapClassTableCode", value = "关联编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "associateCode", value = "被关联编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "oneToOne", value = "一对一", required = true, paramType = "query"),
            @ApiImplicitParam(name = "oneToMany", value = "一对多", required = true, paramType = "query"),
            @ApiImplicitParam(name = "mainField", value = "主表关联属性", required = true, paramType = "query"),
            @ApiImplicitParam(name = "joinField", value = "从表关联属性", required = true, paramType = "query"),
    })
    @PostMapping("/build")
    public R build(String mapClassTableCode, String associateCode, YNEnum oneToOne,
                   YNEnum oneToMany, String mainField, String joinField) {
        Assert.hasText(mapClassTableCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(associateCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(mainField, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(joinField, BaseException.BaseExceptionEnum.Empty_Param.toString());

        MapRelationship mapRelationship = this.load(mapClassTableCode, associateCode);
        mapRelationship.setIsOneToOne(oneToOne.name());
        mapRelationship.setIsOneToMany(oneToMany.name());
        mapRelationship.setMainField(mainField);
        mapRelationship.setJoinField(joinField);

        mapRelationshipService.saveOrUpdate(mapRelationship);

        //反向建立关联关系
        MapRelationship mapRelationshipFlag = this.load(associateCode, mapClassTableCode);
        mapRelationshipFlag.setMainField(joinField);
        mapRelationshipFlag.setJoinField(mainField);
        mapRelationshipFlag.setIsOneToOne(YNEnum.Y.name());
        mapRelationshipFlag.setIsOneToMany(YNEnum.N.name());
        mapRelationshipService.saveOrUpdate(mapRelationshipFlag);

        return R.success();
    }

    /**
     * 查询关系是否存在，不存在的情况下，创建一个实体返回
     *
     * @param mapClassTableCode 主表code
     * @param associateCode     附表code
     * @return MapRelationship
     */
    private MapRelationship load(String mapClassTableCode, String associateCode) {
        MapRelationship mapRelationship;

        mapRelationship = mapRelationshipService.getOne(new LambdaQueryWrapper<MapRelationship>()
                .eq(MapRelationship::getMapClassTableCode, mapClassTableCode)
                .eq(MapRelationship::getAssociateCode, associateCode));
        if (mapRelationship != null) {
            mapRelationshipService.remove(new LambdaQueryWrapper<MapRelationship>()
                    .eq(MapRelationship::getCode, mapRelationship.getCode()));
        }

        mapRelationship = new MapRelationship();
        mapRelationship.setCode(String.valueOf(uidGenerator.getUID()));
        mapRelationship.setMapClassTableCode(mapClassTableCode);
        mapRelationship.setAssociateCode(associateCode);
        return mapRelationship;
    }


    /**
     * 根据条件code查询模型关系一个详情信息
     *
     * @param code 关系编码
     * @return MapRelationshipVO
     */
    @ApiOperation(value = "创建MapRelationship", notes = "创建MapRelationship")
    @GetMapping("/load/code/{code}")
    public MapRelationshipVO loadByCode(@PathVariable java.lang.String code) {
        if (code == null) {
            return null;
        }
        MapRelationship mapRelationship = mapRelationshipService.getOne(new LambdaQueryWrapper<MapRelationship>()
                .eq(MapRelationship::getCode, code));
        MapRelationshipVO mapRelationshipVO = new MapRelationshipVO();
        BeanUtils.copyProperties(mapRelationship, mapRelationshipVO);
        log.debug(JSON.toJSONString(mapRelationshipVO));
        return mapRelationshipVO;
    }

    /**
     * 查询模型关系信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询MapRelationship信息集合", notes = "查询MapRelationship信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "classTableCode", value = "类表映射编码", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public R list(String classTableCode) {
        Assert.hasText(classTableCode, "查询参数为空！");
        List<MapRelationship> relationships = mapRelationshipService.list(new LambdaQueryWrapper<MapRelationship>()
                .eq(MapRelationship::getMapClassTableCode, classTableCode));
        return R.success(relationships);
    }

    /**
     * 查询类表映射关系列表
     *
     * @param projectCode 项目名
     * @return BeanRet
     */
    @ApiOperation(value = "查询类表映射关系列表", notes = "查询类表映射关系列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目名", required = true, paramType = "query")
    })
    @GetMapping(value = "/listMapClassTable")

    public R listMapClassTable(String projectCode) {
        Assert.hasText(projectCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        List<ProjectMap> projectMapList = projectMapService.list(new LambdaQueryWrapper<ProjectMap>()
                .eq(ProjectMap::getProjectCode, projectCode));
        if (CollectionUtils.isNotEmpty(projectMapList)) {
            List<MapClassTable> mapRelationships = mapClassTableService.list(new LambdaQueryWrapper<MapClassTable>()
                    .in(MapClassTable::getCode, projectMapList.stream()
                            .map(projectMap -> projectMap.getMapClassTableCode()).collect(Collectors.toList())));
            return R.success(mapRelationships);

        }

        return R.failed("");
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
    public R listByProjectCode(String classTableCode) {
        Assert.hasText(classTableCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        List<MapRelationship> relationships = mapRelationshipService.list(new LambdaQueryWrapper<MapRelationship>()
                .eq(MapRelationship::getMapClassTableCode, classTableCode));
        return R.success(relationships);
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
    public R listMapFieldColumn(String mapClassTableCode, String associateCode) {
        if (StringUtils.isEmpty(mapClassTableCode)) {
            return R.failed("分页对象不能为空");
        }
        JSONObject jsonObject = new JSONObject();
        List<MapFieldColumn> fields = mapFieldColumnService.list(new LambdaQueryWrapper<MapFieldColumn>()
                .eq(MapFieldColumn::getMapClassTableCode, mapClassTableCode));
        jsonObject.put("mainFields", fields);

        List<MapFieldColumn> associateFields = mapFieldColumnService.list(new LambdaQueryWrapper<MapFieldColumn>()
                .eq(MapFieldColumn::getMapClassTableCode, associateCode));

        jsonObject.put("associateFields", associateFields);
        //查询关联关系
        MapRelationship mapRelationship = mapRelationshipService.getOne(new LambdaQueryWrapper<MapRelationship>()
                .eq(MapRelationship::getMapClassTableCode, mapClassTableCode)
                .eq(MapRelationship::getAssociateCode, associateCode));
        jsonObject.put("mapRelationship", mapRelationship);
        return R.success(jsonObject);
    }


    /**
     * 修改 模型关系
     *
     * @return R
     */
    @ApiOperation(value = "修改MapRelationship", notes = "修改MapRelationship")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改MapRelationship", value = "传入json格式", required = true)
                          @RequestBody MapRelationshipVO mapRelationshipVO) {
        MapRelationship newMapRelationship = new MapRelationship();
        BeanUtils.copyProperties(mapRelationshipVO, newMapRelationship);
        boolean isUpdated = mapRelationshipService.update(newMapRelationship, new LambdaQueryWrapper<MapRelationship>()
                .eq(MapRelationship::getId, mapRelationshipVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 模型关系
     *
     * @return R
     */
    @ApiOperation(value = "删除MapRelationship", notes = "删除MapRelationship")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "codes", value = "编码，多个编码使用逗号隔开", required = true, paramType = "query"),
    })
    @DeleteMapping("/delete")
    public R delete(String codes) {
        Assert.hasText(codes, BaseException.BaseExceptionEnum.Empty_Param.toString());
        String[] array = codes.split(",");
        for (String code : array) {
            mapRelationshipService.remove(new LambdaQueryWrapper<MapRelationship>()
                    .eq(MapRelationship::getCode, code));
        }
        return R.success("删除成功");
    }

}
