/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.display.ctrl;

import com.aicode.display.entity.DisplayAttribute;
import com.aicode.display.service.DisplayAttributeService;
import com.aicode.display.vo.DisplayAttributePageVO;
import com.aicode.display.vo.DisplayAttributeSaveVO;
import com.aicode.display.vo.DisplayAttributeVO;
import com.aicode.core.entity.Page;
import com.aicode.core.entity.PageVO;
import com.aicode.core.entity.R;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.*;
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


    /**
     * 创建 显示属性
     *
     * @return R
     */
    @ApiOperation(value = "创建DisplayAttribute", notes = "创建DisplayAttribute")
    @PostMapping("/build")
    public DisplayAttributeSaveVO build(@ApiParam(name = "创建DisplayAttribute", value = "传入json格式", required = true)
                                   @RequestBody DisplayAttributeSaveVO displayAttributeSaveVO) {
        if (null == displayAttributeSaveVO) {
            return null;
        }
        DisplayAttribute newDisplayAttribute = new DisplayAttribute();
        BeanUtils.copyProperties(displayAttributeSaveVO, newDisplayAttribute);

        displayAttributeService.save(newDisplayAttribute);

        displayAttributeSaveVO = new DisplayAttributeSaveVO();
        BeanUtils.copyProperties(newDisplayAttribute, displayAttributeSaveVO);
        log.debug(JSON.toJSONString(displayAttributeSaveVO));
        return displayAttributeSaveVO;
    }


    /**
     * 根据条件mapFieldColumnCode查询显示属性一个详情信息
     *
     * @param mapFieldColumnCode 字段编码
     * @return DisplayAttributeVO
     */
    @ApiOperation(value = "创建DisplayAttribute", notes = "创建DisplayAttribute")
    @GetMapping("/load/mapFieldColumnCode/{mapFieldColumnCode}")
    public DisplayAttributeVO loadByMapFieldColumnCode(@PathVariable java.lang.String mapFieldColumnCode) {
        if (mapFieldColumnCode == null) {
            return null;
        }
        DisplayAttribute displayAttribute = displayAttributeService.getOne(new LambdaQueryWrapper<DisplayAttribute>()
                .eq(DisplayAttribute::getMapFieldColumnCode, mapFieldColumnCode));
        DisplayAttributeVO displayAttributeVO = new DisplayAttributeVO();
        BeanUtils.copyProperties(displayAttribute, displayAttributeVO);
        log.debug(JSON.toJSONString(displayAttributeVO));
        return displayAttributeVO;
    }

    /**
     * 查询显示属性信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询DisplayAttribute信息集合", notes = "查询DisplayAttribute信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<DisplayAttributeVO> list(@ApiIgnore DisplayAttributePageVO displayAttributeVO, Integer curPage, Integer pageSize) {
        Page<DisplayAttribute> page = new Page<>(pageSize, curPage);
        QueryWrapper<DisplayAttribute> queryWrapper = new QueryWrapper<>();
        if (displayAttributeVO.getIsRequired() != null) {
            queryWrapper.lambda().eq(DisplayAttribute::getIsRequired, displayAttributeVO.getIsRequired());
        }
        if (displayAttributeVO.getIsAllowUpdate() != null) {
            queryWrapper.lambda().eq(DisplayAttribute::getIsAllowUpdate, displayAttributeVO.getIsAllowUpdate());
        }
        if (displayAttributeVO.getIsListPageDisplay() != null) {
            queryWrapper.lambda().eq(DisplayAttribute::getIsListPageDisplay, displayAttributeVO.getIsListPageDisplay());
        }
        if (displayAttributeVO.getIsDetailPageDisplay() != null) {
            queryWrapper.lambda().eq(DisplayAttribute::getIsDetailPageDisplay, displayAttributeVO.getIsDetailPageDisplay());
        }
        if (displayAttributeVO.getIsQueryRequired() != null) {
            queryWrapper.lambda().eq(DisplayAttribute::getIsQueryRequired, displayAttributeVO.getIsQueryRequired());
        }
        if (displayAttributeVO.getMatchType() != null) {
            queryWrapper.lambda().eq(DisplayAttribute::getMatchType, displayAttributeVO.getMatchType());
        }
        if (displayAttributeVO.getDisplayType() != null) {
            queryWrapper.lambda().eq(DisplayAttribute::getDisplayType, displayAttributeVO.getDisplayType());
        }
        if (displayAttributeVO.getFieldValidationMode() != null) {
            queryWrapper.lambda().eq(DisplayAttribute::getFieldValidationMode, displayAttributeVO.getFieldValidationMode());
        }
        if (displayAttributeVO.getDisplayCss() != null) {
            queryWrapper.lambda().eq(DisplayAttribute::getDisplayCss, displayAttributeVO.getDisplayCss());
        }
        int total = displayAttributeService.count(queryWrapper);
        PageVO<DisplayAttributeVO> displayAttributeVOPageVO = new PageVO<>();
        if (total > 0) {
            List<DisplayAttribute> displayAttributeList = displayAttributeService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            displayAttributeVOPageVO.setTotalRow(total);
            displayAttributeVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(displayAttributeList),DisplayAttributeVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return displayAttributeVOPageVO;
    }


    /**
     * 修改 显示属性
     *
     * @return R
     */
    @ApiOperation(value = "修改DisplayAttribute", notes = "修改DisplayAttribute")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改DisplayAttribute", value = "传入json格式", required = true)
                          @RequestBody DisplayAttributeVO displayAttributeVO) {
        DisplayAttribute newDisplayAttribute = new DisplayAttribute();
        BeanUtils.copyProperties(displayAttributeVO, newDisplayAttribute);
        boolean isUpdated = displayAttributeService.update(newDisplayAttribute, new LambdaQueryWrapper<DisplayAttribute>()
                .eq(DisplayAttribute::getId, displayAttributeVO.getId()));
        return isUpdated;
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
                .eq(DisplayAttribute::getId, displayAttributeVO.getId()));
        return R.success("删除成功");
    }

}
