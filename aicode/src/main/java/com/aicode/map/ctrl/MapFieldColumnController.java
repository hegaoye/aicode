/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.ctrl;

import com.aicode.map.entity.MapFieldColumn;
import com.aicode.map.service.MapFieldColumnService;
import com.aicode.map.vo.MapFieldColumnPageVO;
import com.aicode.map.vo.MapFieldColumnSaveVO;
import com.aicode.map.vo.MapFieldColumnVO;
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
 * 字段属性映射信息
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/mapFieldColumn")
@Slf4j
@Api(value = "字段属性映射信息控制器", tags = "字段属性映射信息控制器")
public class MapFieldColumnController {
    @Autowired
    private MapFieldColumnService mapFieldColumnService;


    /**
     * 创建 字段属性映射信息
     *
     * @return R
     */
    @ApiOperation(value = "创建MapFieldColumn", notes = "创建MapFieldColumn")
    @PostMapping("/build")
    public MapFieldColumnSaveVO build(@ApiParam(name = "创建MapFieldColumn", value = "传入json格式", required = true)
                                   @RequestBody MapFieldColumnSaveVO mapFieldColumnSaveVO) {
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



    /**
     * 查询字段属性映射信息信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询MapFieldColumn信息集合", notes = "查询MapFieldColumn信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<MapFieldColumnVO> list(@ApiIgnore MapFieldColumnPageVO mapFieldColumnVO, Integer curPage, Integer pageSize) {
        Page<MapFieldColumn> page = new Page<>(pageSize, curPage);
        QueryWrapper<MapFieldColumn> queryWrapper = new QueryWrapper<>();
        int total = mapFieldColumnService.count(queryWrapper);
        PageVO<MapFieldColumnVO> mapFieldColumnVOPageVO = new PageVO<>();
        if (total > 0) {
            List<MapFieldColumn> mapFieldColumnList = mapFieldColumnService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            mapFieldColumnVOPageVO.setTotalRow(total);
            mapFieldColumnVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(mapFieldColumnList),MapFieldColumnVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return mapFieldColumnVOPageVO;
    }


    /**
     * 修改 字段属性映射信息
     *
     * @return R
     */
    @ApiOperation(value = "修改MapFieldColumn", notes = "修改MapFieldColumn")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改MapFieldColumn", value = "传入json格式", required = true)
                          @RequestBody MapFieldColumnVO mapFieldColumnVO) {
        MapFieldColumn newMapFieldColumn = new MapFieldColumn();
        BeanUtils.copyProperties(mapFieldColumnVO, newMapFieldColumn);
        boolean isUpdated = mapFieldColumnService.update(newMapFieldColumn, new LambdaQueryWrapper<MapFieldColumn>()
                .eq(MapFieldColumn::getId, mapFieldColumnVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 字段属性映射信息
     *
     * @return R
     */
    @ApiOperation(value = "删除MapFieldColumn", notes = "删除MapFieldColumn")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore MapFieldColumnVO mapFieldColumnVO) {
        MapFieldColumn newMapFieldColumn = new MapFieldColumn();
        BeanUtils.copyProperties(mapFieldColumnVO, newMapFieldColumn);
        mapFieldColumnService.remove(new LambdaQueryWrapper<MapFieldColumn>()
                .eq(MapFieldColumn::getId, mapFieldColumnVO.getId()));
        return R.success("删除成功");
    }

}
