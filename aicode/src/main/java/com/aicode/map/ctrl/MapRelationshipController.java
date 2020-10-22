/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.ctrl;

import com.aicode.map.entity.MapRelationship;
import com.aicode.map.service.MapRelationshipService;
import com.aicode.map.vo.MapRelationshipPageVO;
import com.aicode.map.vo.MapRelationshipSaveVO;
import com.aicode.map.vo.MapRelationshipVO;
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
 * 模型关系
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/mapRelationship")
@Slf4j
@Api(value = "模型关系控制器", tags = "模型关系控制器")
public class MapRelationshipController {
    @Autowired
    private MapRelationshipService mapRelationshipService;


    /**
     * 创建 模型关系
     *
     * @return R
     */
    @ApiOperation(value = "创建MapRelationship", notes = "创建MapRelationship")
    @PostMapping("/build")
    public MapRelationshipSaveVO build(@ApiParam(name = "创建MapRelationship", value = "传入json格式", required = true)
                                   @RequestBody MapRelationshipSaveVO mapRelationshipSaveVO) {
        if (null == mapRelationshipSaveVO) {
            return null;
        }
        MapRelationship newMapRelationship = new MapRelationship();
        BeanUtils.copyProperties(mapRelationshipSaveVO, newMapRelationship);

        mapRelationshipService.save(newMapRelationship);

        mapRelationshipSaveVO = new MapRelationshipSaveVO();
        BeanUtils.copyProperties(newMapRelationship, mapRelationshipSaveVO);
        log.debug(JSON.toJSONString(mapRelationshipSaveVO));
        return mapRelationshipSaveVO;
    }


    /**
     * 根据条件code查询模型关系一个详情信息
     *
     * @param code 关系编码
     * @return MapRelationshipVO
     */
    @ApiOperation(value = "创建MapRelationship", notes = "创建MapRelationship")
    @GetMapping("/load/code/{code}")
    public MapRelationshipVO loadByCode(@PathVariable java.lang.String code) {
        if (code == null) {
            return null;
        }
        MapRelationship mapRelationship = mapRelationshipService.getOne(new LambdaQueryWrapper<MapRelationship>()
                .eq(MapRelationship::getCode, code));
        MapRelationshipVO mapRelationshipVO = new MapRelationshipVO();
        BeanUtils.copyProperties(mapRelationship, mapRelationshipVO);
        log.debug(JSON.toJSONString(mapRelationshipVO));
        return mapRelationshipVO;
    }

    /**
     * 查询模型关系信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询MapRelationship信息集合", notes = "查询MapRelationship信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<MapRelationshipVO> list(@ApiIgnore MapRelationshipPageVO mapRelationshipVO, Integer curPage, Integer pageSize) {
        Page<MapRelationship> page = new Page<>(pageSize, curPage);
        QueryWrapper<MapRelationship> queryWrapper = new QueryWrapper<>();
        if (mapRelationshipVO.getIsOneToOne() != null) {
            queryWrapper.lambda().eq(MapRelationship::getIsOneToOne, mapRelationshipVO.getIsOneToOne());
        }
        if (mapRelationshipVO.getIsOneToMany() != null) {
            queryWrapper.lambda().eq(MapRelationship::getIsOneToMany, mapRelationshipVO.getIsOneToMany());
        }
        int total = mapRelationshipService.count(queryWrapper);
        PageVO<MapRelationshipVO> mapRelationshipVOPageVO = new PageVO<>();
        if (total > 0) {
            List<MapRelationship> mapRelationshipList = mapRelationshipService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            mapRelationshipVOPageVO.setTotalRow(total);
            mapRelationshipVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(mapRelationshipList),MapRelationshipVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return mapRelationshipVOPageVO;
    }


    /**
     * 修改 模型关系
     *
     * @return R
     */
    @ApiOperation(value = "修改MapRelationship", notes = "修改MapRelationship")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改MapRelationship", value = "传入json格式", required = true)
                          @RequestBody MapRelationshipVO mapRelationshipVO) {
        MapRelationship newMapRelationship = new MapRelationship();
        BeanUtils.copyProperties(mapRelationshipVO, newMapRelationship);
        boolean isUpdated = mapRelationshipService.update(newMapRelationship, new LambdaQueryWrapper<MapRelationship>()
                .eq(MapRelationship::getId, mapRelationshipVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 模型关系
     *
     * @return R
     */
    @ApiOperation(value = "删除MapRelationship", notes = "删除MapRelationship")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "关系编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore MapRelationshipVO mapRelationshipVO) {
        MapRelationship newMapRelationship = new MapRelationship();
        BeanUtils.copyProperties(mapRelationshipVO, newMapRelationship);
        mapRelationshipService.remove(new LambdaQueryWrapper<MapRelationship>()
                .eq(MapRelationship::getId, mapRelationshipVO.getId()));
        return R.success("删除成功");
    }

}
