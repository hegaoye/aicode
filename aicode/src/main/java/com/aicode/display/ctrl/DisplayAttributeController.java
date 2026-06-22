/*
 * aicode
 */
package com.aicode.display.ctrl;

import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.display.entity.DisplayAttribute;
import com.aicode.display.service.DisplayAttributeService;
import com.aicode.map.entity.MapFieldColumn;
import com.aicode.map.service.MapFieldColumnService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public R build(@RequestBody List<DisplayAttribute> displayAttributes) {
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
    public R list(String mapClassTableCode) {

        List<MapFieldColumn> mapFieldColumns = mapFieldColumnService.list(new LambdaQueryWrapper<MapFieldColumn>()
                .eq(MapFieldColumn::getMapClassTableCode, mapClassTableCode));
        if (!mapFieldColumns.isEmpty()) {
            for (MapFieldColumn mapFieldColumn : mapFieldColumns) {
                String fieldCode = mapFieldColumn.getCode();
                DisplayAttribute displayAttribute = displayAttributeService.getOne(new LambdaQueryWrapper<DisplayAttribute>()
                        .eq(DisplayAttribute::getMapFieldColumnCode, fieldCode));
                mapFieldColumn.setDisplayAttribute(displayAttribute);
            }
        }
        return R.success(mapFieldColumns);
    }

}
