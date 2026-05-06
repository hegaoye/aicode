/*
 * aicode
 */
package com.aicode.map.ctrl;

import com.aicode.core.PageVO;
import com.aicode.core.R;
import com.aicode.map.entity.MapFieldColumn;
import com.aicode.map.service.MapFieldColumnService;
import com.aicode.map.vo.MapFieldColumnSaveVO;
import com.aicode.map.vo.MapFieldColumnVO;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
 * 字段属性映射信息
 *
 * @author aicode
 */
@RestController
@RequestMapping("/mapFieldColumn")
@Slf4j
@Tag(name = "字段属性映射信息控制器", description = "字段属性映射信息控制器")
public class MapFieldColumnController {
    @Autowired
    private MapFieldColumnService mapFieldColumnService;


    @Operation(summary = "创建MapFieldColumn", description = "创建MapFieldColumn")
    @PostMapping("/build")
    public MapFieldColumnSaveVO build(@RequestBody MapFieldColumnSaveVO mapFieldColumnSaveVO) {
        if (null == mapFieldColumnSaveVO) {
            return null;
        }
        MapFieldColumn newMapFieldColumn = new MapFieldColumn();
        BeanUtils.copyProperties(mapFieldColumnSaveVO, newMapFieldColumn);

        mapFieldColumnService.save(newMapFieldColumn);

        mapFieldColumnSaveVO = new MapFieldColumnSaveVO();
        BeanUtils.copyProperties(newMapFieldColumn, mapFieldColumnSaveVO);
        log.debug(JSON.toJSONString(mapFieldColumnSaveVO));
        return mapFieldColumnSaveVO;
    }


    @Operation(summary = "查询MapFieldColumn信息集合", description = "查询MapFieldColumn信息集合")
    @Parameters({
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true),
    })
    @GetMapping(value = "/list")
    public PageVO<MapFieldColumnVO> list(Integer curPage, Integer pageSize) {
        IPage<MapFieldColumn> page = new Page<>(curPage, pageSize);

        QueryWrapper<MapFieldColumn> queryWrapper = new QueryWrapper<>();
        long total = mapFieldColumnService.count(queryWrapper);
        PageVO<MapFieldColumnVO> mapFieldColumnVOPageVO = new PageVO<>();
        if (total > 0) {
            queryWrapper.lambda().orderByDesc(MapFieldColumn::getId);

            IPage<MapFieldColumn> mapFieldColumnPage = mapFieldColumnService.page(page, queryWrapper);
            List<MapFieldColumnVO> mapFieldColumnPageVOList = JSON.parseArray(JSON.toJSONString(mapFieldColumnPage.getRecords()), MapFieldColumnVO.class);

            mapFieldColumnVOPageVO.setTotalRow(total);
            mapFieldColumnVOPageVO.setRecords(mapFieldColumnPageVOList);
            log.debug(JSON.toJSONString(page));
        }

        return mapFieldColumnVOPageVO;
    }


    @Operation(summary = "修改MapFieldColumn", description = "修改MapFieldColumn")
    @PutMapping("/modify")
    public boolean modify(@RequestBody MapFieldColumnVO mapFieldColumnVO) {
        MapFieldColumn newMapFieldColumn = new MapFieldColumn();
        BeanUtils.copyProperties(mapFieldColumnVO, newMapFieldColumn);
        boolean isUpdated = mapFieldColumnService.update(newMapFieldColumn, new LambdaQueryWrapper<MapFieldColumn>()
                .eq(MapFieldColumn::getId, mapFieldColumnVO.getId()));
        return isUpdated;
    }


    @Operation(summary = "删除MapFieldColumn", description = "删除MapFieldColumn")
    @Parameters({
            @Parameter(name = "id", description = "id")
    })
    @DeleteMapping("/delete")
    public R delete(@Parameter(hidden = true) MapFieldColumnVO mapFieldColumnVO) {
        MapFieldColumn newMapFieldColumn = new MapFieldColumn();
        BeanUtils.copyProperties(mapFieldColumnVO, newMapFieldColumn);
        mapFieldColumnService.remove(new LambdaQueryWrapper<MapFieldColumn>()
                .eq(MapFieldColumn::getId, mapFieldColumnVO.getId()));
        return R.success("删除成功");
    }

}
