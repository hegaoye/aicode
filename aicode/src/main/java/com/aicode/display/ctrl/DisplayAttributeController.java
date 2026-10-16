/*
 * aicode
 */
package com.aicode.display.ctrl;

import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.display.entity.DisplayAttribute;
import com.aicode.display.service.DisplayAttributeService;
import com.aicode.display.vo.DisplayAttributeVO;
import com.aicode.map.entity.MapFieldColumn;
import com.aicode.map.service.MapFieldColumnService;
import com.aicode.map.vo.MapFieldColumnVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示属性
 *
 * @author aicode
 */
@RestController
@RequestMapping("/displayAttribute")
@Slf4j
@Tag(name = "显示属性控制器", description = "显示属性控制器")
public class DisplayAttributeController {
    @Autowired
    private DisplayAttributeService displayAttributeService;
    @Autowired
    private MapFieldColumnService mapFieldColumnService;


    @Operation(summary = "创建DisplayAttribute", description = "创建DisplayAttribute")
    @PostMapping("/save")
    public R<Void> build(@RequestBody List<DisplayAttribute> displayAttributes) {
        if (displayAttributes == null || displayAttributes.size() <= 0) {
            return R.failed(BaseException.BaseExceptionEnum.Empty_Param);
        }
        DisplayAttribute displayAttributeFlag;
        for (DisplayAttribute displayAttribute : displayAttributes) {
            MapFieldColumn mapFieldColumn = mapFieldColumnService.getOne(new LambdaQueryWrapper<MapFieldColumn>()
                    .eq(MapFieldColumn::getCode, displayAttribute.getMapFieldColumnCode()));
            if (mapFieldColumn == null) {
                continue;
            }
            displayAttributeFlag = displayAttributeService.getOne(new LambdaQueryWrapper<DisplayAttribute>()
                    .eq(DisplayAttribute::getMapFieldColumnCode, displayAttribute.getMapFieldColumnCode()));
            if (displayAttributeFlag == null) {
                displayAttribute.setMapClassTableCode(mapFieldColumn.getMapClassTableCode());
                displayAttributeService.save(displayAttribute);
            } else {
                displayAttributeService.updateById(displayAttribute);
            }
        }
        return R.success();
    }


    @Operation(summary = "查询DisplayAttribute信息集合", description = "查询DisplayAttribute信息集合")
    @Parameters({
            @Parameter(name = "mapClassTableCode", description = "映射编码"),
    })
    @GetMapping(value = "/list")
    public R<java.util.List<com.aicode.map.vo.MapFieldColumnVO>> list(String mapClassTableCode) {

        List<MapFieldColumn> mapFieldColumns = mapFieldColumnService.list(new LambdaQueryWrapper<MapFieldColumn>()
                .eq(MapFieldColumn::getMapClassTableCode, mapClassTableCode));

        // 手工装配而非 BeanUtils.copyProperties：本 VO 的全部价值就在于「它是实体的字段子集」。
        // 自动拷贝会让后续新增的实体字段悄悄进入响应，使该约束失效 —— 而本端点的响应即将被
        // 导出为 contracts/openapi/openapi.yaml，错误字段一旦固化进契约就需要同时改契约、
        // 前端类型与 mock 三处。详见 openspec/specs/api-data-object/spec.md。
        List<MapFieldColumnVO> voList = new ArrayList<>(mapFieldColumns.size());
        for (MapFieldColumn mapFieldColumn : mapFieldColumns) {
            String fieldCode = mapFieldColumn.getCode();
            DisplayAttribute displayAttribute = displayAttributeService.getOne(new LambdaQueryWrapper<DisplayAttribute>()
                    .eq(DisplayAttribute::getMapFieldColumnCode, fieldCode));
            voList.add(toVo(mapFieldColumn, displayAttribute));
        }
        return R.success(voList);
    }

    /** 实体 → 出参 VO 的逐字段装配。 */
    private static MapFieldColumnVO toVo(MapFieldColumn entity, DisplayAttribute displayAttribute) {
        MapFieldColumnVO vo = new MapFieldColumnVO();
        vo.setId(entity.getId());
        vo.setMapClassTableCode(entity.getMapClassTableCode());
        vo.setCode(entity.getCode());
        vo.setColumn(entity.getColumn());
        vo.setField(entity.getField());
        vo.setSqlType(entity.getSqlType());
        vo.setFieldType(entity.getFieldType());
        vo.setNotes(entity.getNotes());
        vo.setDefaultValue(entity.getDefaultValue());
        vo.setIsPrimaryKey(entity.getIsPrimaryKey());
        vo.setIsDate(entity.getIsDate());
        vo.setIsState(entity.getIsState());
        vo.setDisplayAttribute(toVo(displayAttribute));
        return vo;
    }

    /** 展示属性实体 → VO。字段集合与实体一致，因为该实体本身就是「前端表单的 UI 契约」。 */
    private static DisplayAttributeVO toVo(DisplayAttribute entity) {
        if (entity == null) {
            return null;
        }
        DisplayAttributeVO vo = new DisplayAttributeVO();
        vo.setId(entity.getId());
        vo.setMapClassTableCode(entity.getMapClassTableCode());
        vo.setMapFieldColumnCode(entity.getMapFieldColumnCode());
        vo.setIsRequired(entity.getIsRequired());
        vo.setIsInsert(entity.getIsInsert());
        vo.setIsDeleteCondition(entity.getIsDeleteCondition());
        vo.setIsAllowUpdate(entity.getIsAllowUpdate());
        vo.setIsListPageDisplay(entity.getIsListPageDisplay());
        vo.setIsDetailPageDisplay(entity.getIsDetailPageDisplay());
        vo.setIsQueryRequired(entity.getIsQueryRequired());
        vo.setIsLineNew(entity.getIsLineNew());
        vo.setMatchType(entity.getMatchType());
        vo.setDisplayType(entity.getDisplayType());
        vo.setDisplayName(entity.getDisplayName());
        vo.setDisplayNo(entity.getDisplayNo());
        vo.setFieldValidationMode(entity.getFieldValidationMode());
        vo.setValidateText(entity.getValidateText());
        vo.setDisplayCss(entity.getDisplayCss());
        return vo;
    }

}
