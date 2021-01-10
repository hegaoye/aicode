/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.ctrl;

import com.aicode.map.entity.MapClassTable;
import com.aicode.map.service.MapClassTableService;
import com.aicode.map.vo.MapClassTablePageVO;
import com.aicode.map.vo.MapClassTableSaveVO;
import com.aicode.map.vo.MapClassTableVO;
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
 * 类表映射信息
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/mapClassTable")
@Slf4j
@Api(value = "类表映射信息控制器", tags = "类表映射信息控制器")
public class MapClassTableController {
    @Autowired
    private MapClassTableService mapClassTableService;


    /**
     * 创建 类表映射信息
     *
     * @return R
     */
    @ApiOperation(value = "创建MapClassTable", notes = "创建MapClassTable")
    @PostMapping("/build")
    public MapClassTableSaveVO build(@ApiParam(name = "创建MapClassTable", value = "传入json格式", required = true)
                                   @RequestBody MapClassTableSaveVO mapClassTableSaveVO) {
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


    /**
     * 根据条件code查询类表映射信息一个详情信息
     *
     * @param code 映射编码
     * @return MapClassTableVO
     */
    @ApiOperation(value = "创建MapClassTable", notes = "创建MapClassTable")
    @GetMapping("/load/code/{code}")
    public MapClassTableVO loadByCode(@PathVariable java.lang.String code) {
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

    /**
     * 查询类表映射信息信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询MapClassTable信息集合", notes = "查询MapClassTable信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<MapClassTableVO> list(@ApiIgnore MapClassTablePageVO mapClassTableVO, Integer curPage, Integer pageSize) {
        Page<MapClassTable> page = new Page<>(pageSize, curPage);
        QueryWrapper<MapClassTable> queryWrapper = new QueryWrapper<>();
        int total = mapClassTableService.count(queryWrapper);
        PageVO<MapClassTableVO> mapClassTableVOPageVO = new PageVO<>();
        if (total > 0) {
            List<MapClassTable> mapClassTableList = mapClassTableService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            mapClassTableVOPageVO.setTotalRow(total);
            mapClassTableVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(mapClassTableList),MapClassTableVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return mapClassTableVOPageVO;
    }


    /**
     * 修改 类表映射信息
     *
     * @return R
     */
    @ApiOperation(value = "修改MapClassTable", notes = "修改MapClassTable")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改MapClassTable", value = "传入json格式", required = true)
                          @RequestBody MapClassTableVO mapClassTableVO) {
        MapClassTable newMapClassTable = new MapClassTable();
        BeanUtils.copyProperties(mapClassTableVO, newMapClassTable);
        boolean isUpdated = mapClassTableService.update(newMapClassTable, new LambdaQueryWrapper<MapClassTable>()
                .eq(MapClassTable::getId, mapClassTableVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 类表映射信息
     *
     * @return R
     */
    @ApiOperation(value = "删除MapClassTable", notes = "删除MapClassTable")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "映射编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore MapClassTableVO mapClassTableVO) {
        MapClassTable newMapClassTable = new MapClassTable();
        BeanUtils.copyProperties(mapClassTableVO, newMapClassTable);
        mapClassTableService.remove(new LambdaQueryWrapper<MapClassTable>()
                .eq(MapClassTable::getId, mapClassTableVO.getId()));
        return R.success("删除成功");
    }

}
