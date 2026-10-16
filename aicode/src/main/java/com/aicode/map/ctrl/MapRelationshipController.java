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
import com.aicode.map.vo.MapClassTableVO;
import com.aicode.map.vo.MapFieldColumnReadVO;
import com.aicode.map.vo.MapRelationshipReadVO;
import com.aicode.map.vo.MapRelationshipVO;
import com.aicode.map.vo.RelationshipFieldsVO;
import com.aicode.project.entity.ProjectMap;
import com.aicode.project.service.ProjectMapService;
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
import cn.hutool.core.util.IdUtil;

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
    public R<Void> build(String mapClassTableCode, String associateCode, YNEnum oneToOne,
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
        //A->B oneToMany 时，反向 B->A 应是 oneToOne（A 持有 B 的集合，B 仅持有一个 A）
        //A->B oneToOne 时，反向 B->A 也应是 oneToOne
        YNEnum reverseOneToOne = oneToOne;
        YNEnum reverseOneToMany = YNEnum.N;
        if (oneToMany == YNEnum.Y && oneToOne != YNEnum.Y) {
            reverseOneToOne = YNEnum.Y;
            reverseOneToMany = YNEnum.N;
        }
        MapRelationship mapRelationshipFlag = this.load(associateCode, mapClassTableCode);
        mapRelationshipFlag.setMainField(joinField);
        mapRelationshipFlag.setJoinField(mainField);
        mapRelationshipFlag.setIsOneToOne(reverseOneToOne.name());
        mapRelationshipFlag.setIsOneToMany(reverseOneToMany.name());
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
        // code 是业务编码，不是主键派生值（主键由数据库自增赋值）
        mapRelationship.setCode(IdUtil.getSnowflakeNextIdStr());
        mapRelationship.setMapClassTableCode(mapClassTableCode);
        mapRelationship.setAssociateCode(associateCode);
        return mapRelationship;
    }


    @Operation(summary = "查询MapRelationship信息集合", description = "查询MapRelationship信息集合")
    @Parameters({
            @Parameter(name = "classTableCode", description = "类表映射编码", required = true),
    })
    @GetMapping(value = "/list")
    public R<java.util.List<MapRelationshipReadVO>> list(String classTableCode) {
        Assert.hasText(classTableCode, "查询参数为空！");
        List<MapRelationship> relationships = mapRelationshipService.list(new LambdaQueryWrapper<MapRelationship>()
                .eq(MapRelationship::getMapClassTableCode, classTableCode));
        List<MapRelationshipReadVO> voList = new java.util.ArrayList<>(relationships.size());
        for (MapRelationship entity : relationships) {
            voList.add(toRelationshipReadVo(entity));
        }
        return R.success(voList);
    }

    /**
     * 把持久化实体 {@link MapRelationship} 转换为关联关系读投影
     * {@link MapRelationshipReadVO}（7 字段）。
     *
     * <p>刻意排除：自增主键 {@code id}（对外标识是业务编码 {@code code}）、
     * 嵌套 {@code associateClass}（整个 {@code MapClassTable} 实体，列表场景必为 null）、
     * 派生 boolean {@code oneToOne} / {@code oneToMany}（与 {@code isOneToOne} /
     * {@code isOneToMany} 语义同源）。
     *
     * <p><b>与 {@link MapRelationshipVO} 的区别</b>：后者是
     * {@code PUT /modify} 的 {@code @RequestBody} 入参载体，其 {@code id} 是该端点
     * 查询必需的，不可裁。</p>
     *
     * <p>逐字段显式赋值，与 {@code ProjectController#toSummary} 保持一致：
     * 显式胜过隐式，字段重命名会在编译期暴露。</p>
     */
    private static MapRelationshipReadVO toRelationshipReadVo(MapRelationship entity) {
        MapRelationshipReadVO vo = new MapRelationshipReadVO();
        vo.setCode(entity.getCode());
        vo.setMapClassTableCode(entity.getMapClassTableCode());
        vo.setAssociateCode(entity.getAssociateCode());
        vo.setIsOneToOne(entity.getIsOneToOne());
        vo.setIsOneToMany(entity.getIsOneToMany());
        vo.setMainField(entity.getMainField());
        vo.setJoinField(entity.getJoinField());
        return vo;
    }


    @Operation(summary = "查询类表映射关系列表", description = "查询类表映射关系列表")
    @Parameters({
            @Parameter(name = "projectCode", description = "项目名", required = true)
    })
    @GetMapping(value = "/listMapClassTable")
    public R<java.util.List<MapClassTableVO>> listMapClassTable(String projectCode) {
        Assert.hasText(projectCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        List<ProjectMap> projectMapList = projectMapService.list(new LambdaQueryWrapper<ProjectMap>()
                .eq(ProjectMap::getProjectCode, projectCode));
        log.info("projectCode: {} , projectMapList: {}", projectCode, projectMapList);

        if (CollectionUtils.isNotEmpty(projectMapList)) {
            List<MapClassTable> mapRelationships = mapClassTableService.list(new LambdaQueryWrapper<MapClassTable>()
                    .in(MapClassTable::getCode, projectMapList.stream()
                            .map(projectMap -> projectMap.getMapClassTableCode()).collect(Collectors.toList())));
            // 出参返 VO 而非 9 字段实体 —— 见 api-data-object 的
            // 「Controller SHALL 返回 VO 而非持久化实体」。实体的两个关联 list
            // （mapFieldColumnList / mapRelationshipList）在此场景必为 null。
            List<MapClassTableVO> voList = new java.util.ArrayList<>(mapRelationships.size());
            for (MapClassTable entity : mapRelationships) {
                voList.add(toClassTableVo(entity));
            }
            return R.success(voList);

        }

        return R.failed(BaseException.BaseExceptionEnum.Result_Not_Exist);
    }

    /**
     * 把持久化实体 {@link MapClassTable} 转换为类表映射出参
     * {@link MapClassTableVO}（6 字段）。
     *
     * <p>刻意排除：自增主键 {@code id}、嵌套 {@code mapFieldColumnList} /
     * {@code mapRelationshipList}（列表场景必为 null）。</p>
     */
    private static MapClassTableVO toClassTableVo(MapClassTable entity) {
        MapClassTableVO vo = new MapClassTableVO();
        vo.setCode(entity.getCode());
        vo.setTableName(entity.getTableName());
        vo.setClassName(entity.getClassName());
        vo.setClassModel(entity.getClassModel());
        vo.setDashedCaseName(entity.getDashedCaseName());
        vo.setNotes(entity.getNotes());
        return vo;
    }


    @Operation(summary = "查询字段信息--设置表的关联关系时使用", description = "查询字段信息--设置表的关联关系时使用")
    @Parameters({
            @Parameter(name = "mapClassTableCode", description = "映射编码"),
            @Parameter(name = "associateCode", description = "被关联编码"),
    })
    @GetMapping(value = "/listMapFieldColumn")
    public R<RelationshipFieldsVO> listMapFieldColumn(String mapClassTableCode, String associateCode) {
        if (StringUtils.isEmpty(mapClassTableCode)) {
            return R.failed(BaseException.BaseExceptionEnum.Empty_Param);
        }
        List<MapFieldColumn> fields = mapFieldColumnService.list(new LambdaQueryWrapper<MapFieldColumn>()
                .eq(MapFieldColumn::getMapClassTableCode, mapClassTableCode));
        List<MapFieldColumn> associateFields = mapFieldColumnService.list(new LambdaQueryWrapper<MapFieldColumn>()
                .eq(MapFieldColumn::getMapClassTableCode, associateCode));

        //查询关联关系
        MapRelationship mapRelationship = mapRelationshipService.getOne(new LambdaQueryWrapper<MapRelationship>()
                .eq(MapRelationship::getMapClassTableCode, mapClassTableCode)
                .eq(MapRelationship::getAssociateCode, associateCode));

        // 具名 VO 而非 JSONObject：原先的 R<Object> 让契约里 data 是无约束的 {}，
        // 前端拿不到内部字段名 —— 见 api-contract 的
        // 「R<Object> 出参 SHALL 被识别为契约缺陷而非合法写法」。
        // 三个字段名与原 jsonObject.put 的 key 一一对应，SHALL NOT 改名。
        RelationshipFieldsVO vo = new RelationshipFieldsVO();
        vo.setMainFields(toFieldReadVoList(fields));
        vo.setAssociateFields(toFieldReadVoList(associateFields));
        vo.setMapRelationship(null == mapRelationship ? null : toRelationshipReadVo(mapRelationship));
        return R.success(vo);
    }

    /**
     * 把持久化实体 {@link MapFieldColumn} 转换为字段读投影
     * {@link MapFieldColumnReadVO}（8 字段）的列表。
     *
     * <p>刻意排除：自增主键 {@code id}、嵌套 {@code displayAttribute}
     * （由 {@code /displayAttribute/list} 单独给出，且该实体本身是"前端表单的 UI 契约"）、
     * 嵌套 {@code mapRelationship}、{@code notes} / {@code defaultValue} /
     * {@code sqlType} / {@code upper}、4 个派生 boolean
     * （{@code checkDate} / {@code checkState} / {@code checkPk} 等）。</p>
     *
     * <p><b>与 {@link com.aicode.map.vo.MapFieldColumnVO} 的区别</b>：后者（13 字段）
     * 是 {@code /displayAttribute/list} 的出参，被 {@code DisplayAttributesPage} 消费，
     * 不可裁。</p>
     */
    private static List<MapFieldColumnReadVO> toFieldReadVoList(List<MapFieldColumn> entities) {
        List<MapFieldColumnReadVO> voList = new java.util.ArrayList<>(entities.size());
        for (MapFieldColumn entity : entities) {
            MapFieldColumnReadVO vo = new MapFieldColumnReadVO();
            vo.setCode(entity.getCode());
            vo.setMapClassTableCode(entity.getMapClassTableCode());
            vo.setColumn(entity.getColumn());
            vo.setField(entity.getField());
            vo.setFieldType(entity.getFieldType());
            vo.setIsPrimaryKey(entity.getIsPrimaryKey());
            vo.setIsDate(entity.getIsDate());
            vo.setIsState(entity.getIsState());
            voList.add(vo);
        }
        return voList;
    }


    @Operation(summary = "修改MapRelationship", description = "修改MapRelationship")
    @PutMapping("/modify")
    public R<Boolean> modify(@RequestBody MapRelationshipVO mapRelationshipVO) {
        MapRelationship newMapRelationship = new MapRelationship();
        BeanUtils.copyProperties(mapRelationshipVO, newMapRelationship);
        boolean isUpdated = mapRelationshipService.update(newMapRelationship, new LambdaQueryWrapper<MapRelationship>()
                .eq(MapRelationship::getId, mapRelationshipVO.getId()));
        // R<Boolean> 而非裸 boolean：裸出参绕过 R 的 code/info/success 三元组，
        // 前端的 unwrapSuccess 无从工作，该端点只能被特殊处理。
        // 见 api-contract 的「端点 SHALL NOT 裸返 boolean 等非 R 包络类型」。
        return R.success(isUpdated);
    }


    @Operation(summary = "删除MapRelationship", description = "删除MapRelationship")
    @Parameters({
            @Parameter(name = "codes", description = "编码，多个编码使用逗号隔开", required = true),
    })
    @DeleteMapping("/delete")
    public R<String> delete(String codes) {
        Assert.hasText(codes, BaseException.BaseExceptionEnum.Empty_Param.toString());
        String[] array = codes.split(",");
        for (String code : array) {
            mapRelationshipService.remove(new LambdaQueryWrapper<MapRelationship>()
                    .eq(MapRelationship::getCode, code));
        }
        return R.success("删除成功");
    }

}
