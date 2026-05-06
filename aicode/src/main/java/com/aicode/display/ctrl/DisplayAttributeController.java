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
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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


    @Operation(summary = "创建DisplayAttribute", description = "创建DisplayAttribute")
    @GetMapping("/load/mapFieldColumnCode/{mapFieldColumnCode}")
    public R loadByMapFieldColumnCode(@PathVariable String mapFieldColumnCode) {
        if (mapFieldColumnCode == null) {
            return null;
        }
        DisplayAttribute displayAttribute = displayAttributeService.getOne(new LambdaQueryWrapper<DisplayAttribute>()
                .eq(DisplayAttribute::getMapFieldColumnCode, mapFieldColumnCode));
        DisplayAttributeVO displayAttributeVO = new DisplayAttributeVO();
        BeanUtils.copyProperties(displayAttribute, displayAttributeVO);
        log.debug(JSON.toJSONString(displayAttributeVO));
        return R.success(displayAttributeVO);
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


    @Operation(summary = "修改DisplayAttribute", description = "修改DisplayAttribute")
    @Parameters({
            @Parameter(name = "mapFieldColumnCode", description = "字段编码"),
            @Parameter(name = "isRequired", description = "是否必填 Y,N"),
            @Parameter(name = "isInsert", description = "是否插入"),
            @Parameter(name = "isDeleteCondition", description = "是否是删除条件"),
            @Parameter(name = "isAllowUpdate", description = "是否允许修改 Y,N"),
            @Parameter(name = "isListPageDisplay", description = "是否分页列表显示 Y,N"),
            @Parameter(name = "isDetailPageDisplay", description = "是否详情页显示 Y,N"),
            @Parameter(name = "isQueryRequired", description = "是否是查询条件 Y,N"),
            @Parameter(name = "isLineNew", description = "是否换行"),
            @Parameter(name = "matchType", description = "匹配方式 =,!=,>=,<=,>,<,like,左like，右like,between,in"),
            @Parameter(name = "displayType", description = "显示格式 自动完成 Autocomplete,级联选择 Cascader,日期选择框 DatePicker,时间选择 TimePicker,输入框 Input,数字输入框 InputNumber,提及 Mention,邮箱 Email，电话Phone，手机Mobile，备注说明 Summary，选择器 Select，单选 Radio，多选框 Checkbox,评分 Rate,加载展位图 Skeleton,滑动输入条 Silder，开关 Switch,穿梭框 Transfer,选择树 TreeSelect ,上传 Upload,头像 Avatar"),
            @Parameter(name = "displayName", description = "显示列名称"),
            @Parameter(name = "displayNo", description = "显示顺序"),
            @Parameter(name = "fieldValidationMode", description = "字段验证方式"),
            @Parameter(name = "validateText", description = "验证提示语"),
            @Parameter(name = "displayCss", description = "显示css样式")
    })
    @PutMapping("/modify")
    public R modify(@Parameter(hidden = true) DisplayAttribute displayAttribute) {
        displayAttributeService.update(displayAttribute, new LambdaQueryWrapper<DisplayAttribute>()
                .eq(DisplayAttribute::getMapFieldColumnCode, displayAttribute.getMapFieldColumnCode()));
        return R.success();
    }

    @Operation(summary = "删除DisplayAttribute", description = "删除DisplayAttribute")
    @Parameters({
            @Parameter(name = "id", description = ""),
            @Parameter(name = "mapFieldColumnCode", description = "字段编码")
    })
    @DeleteMapping("/delete")
    public R delete(@Parameter(hidden = true) DisplayAttributeVO displayAttributeVO) {
        DisplayAttribute newDisplayAttribute = new DisplayAttribute();
        BeanUtils.copyProperties(displayAttributeVO, newDisplayAttribute);
        displayAttributeService.remove(new LambdaQueryWrapper<DisplayAttribute>()
                .eq(DisplayAttribute::getId, displayAttributeVO.getId())
                .eq(DisplayAttribute::getMapFieldColumnCode, displayAttributeVO.getMapFieldColumnCode()));
        return R.success("删除成功");
    }

}
