/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.display.ctrl;

import com.aicode.core.entity.R;
import com.aicode.display.entity.DisplayAttribute;
import com.aicode.display.service.DisplayAttributeService;
import com.aicode.display.vo.DisplayAttributeVO;
import com.aicode.map.entity.MapFieldColumn;
import com.aicode.map.service.MapFieldColumnService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 显示属性
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/displayAttribute")
@Slf4j
@Api(value = "显示属性控制器", tags = "显示属性控制器")
public class DisplayAttributeController {
    @Autowired
    private DisplayAttributeService displayAttributeService;

    @Autowired
    private MapFieldColumnService mapFieldColumnService;

    /**
     * 创建 显示属性
     *
     * @return R
     */
    @ApiOperation(value = "创建DisplayAttribute", notes = "创建DisplayAttribute")
    @PostMapping("/save")
    public R build(@RequestBody List<DisplayAttribute> displayAttributes) {
        if (displayAttributes == null || displayAttributes.size() <= 0) {
            return R.failed("参数不能为空");
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


    /**
     * 根据条件mapFieldColumnCode查询显示属性一个详情信息
     *
     * @param mapFieldColumnCode 字段编码
     * @return DisplayAttributeVO
     */
    @ApiOperation(value = "创建DisplayAttribute", notes = "创建DisplayAttribute")
    @GetMapping("/load/mapFieldColumnCode/{mapFieldColumnCode}")
    public R loadByMapFieldColumnCode(@PathVariable java.lang.String mapFieldColumnCode) {
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

    /**
     * 查询显示属性信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询DisplayAttribute信息集合", notes = "查询DisplayAttribute信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapClassTableCode", value = "映射编码", paramType = "query"),
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


    /**
     * 修改 显示属性
     *
     * @return R
     */
    @ApiOperation(value = "修改DisplayAttribute", notes = "修改DisplayAttribute")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapFieldColumnCode", value = "字段编码", paramType = "query"),
            @ApiImplicitParam(name = "isRequired", value = "是否必填 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isInsert", value = "是否插入", paramType = "query"),
            @ApiImplicitParam(name = "isDeleteCondition", value = "是否是删除条件", paramType = "query"),
            @ApiImplicitParam(name = "isAllowUpdate", value = "是否允许修改 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isListPageDisplay", value = "是否分页列表显示 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isDetailPageDisplay", value = "是否详情页显示 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isQueryRequired", value = "是否是查询条件 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "isLineNew", value = "是否换行", paramType = "query"),
            @ApiImplicitParam(name = "matchType", value = "匹配方式 =,!=,>=,<=,>,<,like,左like，右like,between,in", paramType = "query"),
            @ApiImplicitParam(name = "displayType", value = "显示格式 自动完成 Autocomplete,级联选择 Cascader,日期选择框 DatePicker,时间选择 TimePicker,输入框 Input,数字输入框 InputNumber,提及 Mention,邮箱 Email，电话Phone，手机Mobile，备注说明 Summary，选择器 Select，单选 Radio，多选框 Checkbox,评分 Rate,加载展位图 Skeleton,滑动输入条 Silder，开关 Switch,穿梭框 Transfer,选择树 TreeSelect ,上传 Upload,头像 Avatar", paramType = "query"),
            @ApiImplicitParam(name = "displayName", value = "显示列名称", paramType = "query"),
            @ApiImplicitParam(name = "displayNo", value = "显示顺序", paramType = "query"),
            @ApiImplicitParam(name = "fieldValidationMode", value = "字段验证方式", paramType = "query"),
            @ApiImplicitParam(name = "validateText", value = "验证提示语", paramType = "query"),
            @ApiImplicitParam(name = "displayCss", value = "显示css样式", paramType = "query")
    })
    @PutMapping("/modify")
    public R modify(@ApiIgnore DisplayAttribute displayAttribute) {
        displayAttributeService.update(displayAttribute, new LambdaQueryWrapper<DisplayAttribute>()
                .eq(DisplayAttribute::getMapFieldColumnCode, displayAttribute.getMapFieldColumnCode()));
        return R.success();
    }


    /**
     * 删除 显示属性
     *
     * @return R
     */
    @ApiOperation(value = "删除DisplayAttribute", notes = "删除DisplayAttribute")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "mapFieldColumnCode", value = "字段编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore DisplayAttributeVO displayAttributeVO) {
        DisplayAttribute newDisplayAttribute = new DisplayAttribute();
        BeanUtils.copyProperties(displayAttributeVO, newDisplayAttribute);
        displayAttributeService.remove(new LambdaQueryWrapper<DisplayAttribute>()
                .eq(DisplayAttribute::getId, displayAttributeVO.getId())
                .eq(DisplayAttribute::getMapFieldColumnCode, displayAttributeVO.getMapFieldColumnCode()));
        return R.success("删除成功");
    }

}
