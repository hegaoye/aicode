/*
 * aicode
 */
package com.aicode.map.ctrl;

import com.aicode.core.PageVO;
import com.aicode.core.R;
import com.aicode.map.entity.MapClassTable;
import com.aicode.map.service.MapClassTableService;
import com.aicode.map.vo.MapClassTableSaveVO;
import com.aicode.map.vo.MapClassTableVO;
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
 * 类表映射信息
 *
 * @author aicode
 */
@Slf4j
@RestController
@RequestMapping("/mapClassTable")
@Tag(name = "类表映射信息控制器", description = "类表映射信息控制器")
public class MapClassTableController {
    @Autowired
    private MapClassTableService mapClassTableService;

    @Operation(summary = "创建MapClassTable", description = "创建MapClassTable")
    @PostMapping("/build")
    public MapClassTableSaveVO build(@RequestBody MapClassTableSaveVO mapClassTableSaveVO) {
        if (null == mapClassTableSaveVO) {
            return null;
        }
        MapClassTable newMapClassTable = new MapClassTable();
        BeanUtils.copyProperties(mapClassTableSaveVO, newMapClassTable);

        mapClassTableService.save(newMapClassTable);

        mapClassTableSaveVO = new MapClassTableSaveVO();
        BeanUtils.copyProperties(newMapClassTable, mapClassTableSaveVO);
        log.debug(JSON.toJSONString(mapClassTableSaveVO));
        return mapClassTableSaveVO;
    }


    @Operation(summary = "创建MapClassTable", description = "创建MapClassTable")
    @GetMapping("/load/code/{code}")
    public MapClassTableVO loadByCode(@PathVariable String code) {
        if (code == null) {
            return null;
        }
        MapClassTable mapClassTable = mapClassTableService.getOne(new LambdaQueryWrapper<MapClassTable>()
                .eq(MapClassTable::getCode, code));
        MapClassTableVO mapClassTableVO = new MapClassTableVO();
        BeanUtils.copyProperties(mapClassTable, mapClassTableVO);
        log.debug(JSON.toJSONString(mapClassTableVO));
        return mapClassTableVO;
    }


    @Operation(summary = "查询MapClassTable信息集合", description = "查询MapClassTable信息集合")
    @Parameters({
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true),
    })
    @GetMapping(value = "/list")
    public PageVO<MapClassTableVO> list(Integer curPage, Integer pageSize) {
        IPage<MapClassTable> page = new Page<>(curPage, pageSize);

        QueryWrapper<MapClassTable> queryWrapper = new QueryWrapper<>();
        long total = mapClassTableService.count(queryWrapper);
        PageVO<MapClassTableVO> mapClassTableVOPageVO = new PageVO<>();
        if (total > 0) {
            queryWrapper.lambda().orderByDesc(MapClassTable::getId);
            IPage<MapClassTable> mapClassTablePage = mapClassTableService.page(page, queryWrapper);
            List<MapClassTableVO> mapClassTablePageVOList = JSON.parseArray(JSON.toJSONString(mapClassTablePage.getRecords()), MapClassTableVO.class);

            mapClassTableVOPageVO.setTotalRow(total);
            mapClassTableVOPageVO.setRecords(mapClassTablePageVOList);
            log.debug(JSON.toJSONString(page));
        }

        return mapClassTableVOPageVO;
    }


    @Operation(summary = "修改MapClassTable", description = "修改MapClassTable")
    @PutMapping("/modify")
    public boolean modify(@RequestBody MapClassTableVO mapClassTableVO) {
        MapClassTable newMapClassTable = new MapClassTable();
        BeanUtils.copyProperties(mapClassTableVO, newMapClassTable);
        boolean isUpdated = mapClassTableService.update(newMapClassTable, new LambdaQueryWrapper<MapClassTable>()
                .eq(MapClassTable::getId, mapClassTableVO.getId()));
        return isUpdated;
    }


    @Operation(summary = "删除MapClassTable", description = "删除MapClassTable")
    @Parameters({
            @Parameter(name = "id", description = "id"),
            @Parameter(name = "code", description = "映射编码")
    })
    @DeleteMapping("/delete")
    public R delete(@Parameter(hidden = true) MapClassTableVO mapClassTableVO) {
        MapClassTable newMapClassTable = new MapClassTable();
        BeanUtils.copyProperties(mapClassTableVO, newMapClassTable);
        mapClassTableService.remove(new LambdaQueryWrapper<MapClassTable>()
                .eq(MapClassTable::getId, mapClassTableVO.getId()));
        return R.success("删除成功");
    }

}
