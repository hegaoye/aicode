/*
 * aicode
 */
package com.aicode.map.ctrl;

import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.core.enums.YNEnum;
import com.aicode.map.entity.MapClassTable;
import com.aicode.map.entity.MapFieldColumn;
import com.aicode.map.entity.MapRelationship;
import com.aicode.map.service.MapClassTableService;
import com.aicode.map.service.MapFieldColumnService;
import com.aicode.map.service.MapRelationshipService;
import com.aicode.map.vo.MapRelationshipVO;
import com.aicode.project.entity.ProjectMap;
import com.aicode.project.service.ProjectMapService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
 * @author aicode
 */
@Slf4j
@RestController
@RequestMapping("/project/relationship")
@Tag(name = "模型关系控制器", description = "模型关系控制器")
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

    
    @Operation(summary = "创建MapRelationship", description = "创建MapRelationship")
    @Parameters({
            @Parameter(name = "mapClassTableCode", description = "关联编码", required = true),
            @Parameter(name = "associateCode", description = "被关联编码", required = true),
            @Parameter(name = "oneToOne", description = "一对一", required = true),
            @Parameter(name = "oneToMany", description = "一对多", required = true),
            @Parameter(name = "mainField", description = "主表关联属性", required = true),
            @Parameter(name = "joinField", description = "从表关联属性", required = true),
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


    
    @Operation(summary = "创建MapRelationship", description = "创建MapRelationship")
    @GetMapping("/load/code/{code}")
    public MapRelationshipVO loadByCode(@PathVariable String code) {
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


    
    @Operation(summary = "查询MapRelationship信息集合", description = "查询MapRelationship信息集合")
    @Parameters({
            @Parameter(name = "classTableCode", description = "类表映射编码", required = true),
    })
    @GetMapping(value = "/list")
    public R list(String classTableCode) {
        Assert.hasText(classTableCode, "查询参数为空！");
        List<MapRelationship> relationships = mapRelationshipService.list(new LambdaQueryWrapper<MapRelationship>()
                .eq(MapRelationship::getMapClassTableCode, classTableCode));
        return R.success(relationships);
    }


    
    @Operation(summary = "查询类表映射关系列表", description = "查询类表映射关系列表")
    @Parameters({
            @Parameter(name = "projectCode", description = "项目名", required = true)
    })
    @GetMapping(value = "/listMapClassTable")
    public R listMapClassTable(String projectCode) {
        Assert.hasText(projectCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        List<ProjectMap> projectMapList = projectMapService.list(new LambdaQueryWrapper<ProjectMap>()
                .eq(ProjectMap::getProjectCode, projectCode));
        log.info("projectCode: {} , projectMapList: {}", projectCode, projectMapList);

        if (CollectionUtils.isNotEmpty(projectMapList)) {
            List<MapClassTable> mapRelationships = mapClassTableService.list(new LambdaQueryWrapper<MapClassTable>()
                    .in(MapClassTable::getCode, projectMapList.stream()
                            .map(projectMap -> projectMap.getMapClassTableCode()).collect(Collectors.toList())));
            return R.success(mapRelationships);

        }

        return R.failed(BaseException.BaseExceptionEnum.Result_Not_Exist);
    }


    
    @Operation(summary = "查询模型关系列表", description = "查询模型关系列表")
    @Parameters({
            @Parameter(name = "classTableCode", description = "类表映射编码", required = true),
    })
    @GetMapping(value = "/listByClassTableCode")
    public R listByProjectCode(String classTableCode) {
        Assert.hasText(classTableCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        List<MapRelationship> relationships = mapRelationshipService.list(new LambdaQueryWrapper<MapRelationship>()
                .eq(MapRelationship::getMapClassTableCode, classTableCode));
        return R.success(relationships);
    }


    
    @Operation(summary = "查询字段信息--设置表的关联关系时使用", description = "查询字段信息--设置表的关联关系时使用")
    @Parameters({
            @Parameter(name = "mapClassTableCode", description = "映射编码"),
            @Parameter(name = "associateCode", description = "被关联编码"),
    })
    @GetMapping(value = "/listMapFieldColumn")
    @ResponseBody
    public R listMapFieldColumn(String mapClassTableCode, String associateCode) {
        if (StringUtils.isEmpty(mapClassTableCode)) {
            return R.failed(BaseException.BaseExceptionEnum.Empty_Param);
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


    
    @Operation(summary = "修改MapRelationship", description = "修改MapRelationship")
    @PutMapping("/modify")
    public boolean modify(@RequestBody MapRelationshipVO mapRelationshipVO) {
        MapRelationship newMapRelationship = new MapRelationship();
        BeanUtils.copyProperties(mapRelationshipVO, newMapRelationship);
        boolean isUpdated = mapRelationshipService.update(newMapRelationship, new LambdaQueryWrapper<MapRelationship>()
                .eq(MapRelationship::getId, mapRelationshipVO.getId()));
        return isUpdated;
    }


    
    @Operation(summary = "删除MapRelationship", description = "删除MapRelationship")
    @Parameters({
            @Parameter(name = "codes", description = "编码，多个编码使用逗号隔开", required = true),
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
