/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.project.entity.ProjectMap;
import com.aicode.project.service.ProjectMapService;
import com.aicode.project.vo.ProjectMapPageVO;
import com.aicode.project.vo.ProjectMapSaveVO;
import com.aicode.project.vo.ProjectMapVO;
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
 * 项目数据表
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/projectMap")
@Slf4j
@Api(value = "项目数据表控制器", tags = "项目数据表控制器")
public class ProjectMapController {
    @Autowired
    private ProjectMapService projectMapService;


    /**
     * 创建 项目数据表
     *
     * @return R
     */
    @ApiOperation(value = "创建ProjectMap", notes = "创建ProjectMap")
    @PostMapping("/build")
    public ProjectMapSaveVO build(@ApiParam(name = "创建ProjectMap", value = "传入json格式", required = true)
                                   @RequestBody ProjectMapSaveVO projectMapSaveVO) {
        if (null == projectMapSaveVO) {
            return null;
        }
        ProjectMap newProjectMap = new ProjectMap();
        BeanUtils.copyProperties(projectMapSaveVO, newProjectMap);

        projectMapService.save(newProjectMap);

        projectMapSaveVO = new ProjectMapSaveVO();
        BeanUtils.copyProperties(newProjectMap, projectMapSaveVO);
        log.debug(JSON.toJSONString(projectMapSaveVO));
        return projectMapSaveVO;
    }



    /**
     * 查询项目数据表信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询ProjectMap信息集合", notes = "查询ProjectMap信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<ProjectMapVO> list(@ApiIgnore ProjectMapPageVO projectMapVO, Integer curPage, Integer pageSize) {
        Page<ProjectMap> page = new Page<>(pageSize, curPage);
        QueryWrapper<ProjectMap> queryWrapper = new QueryWrapper<>();
        int total = projectMapService.count(queryWrapper);
        PageVO<ProjectMapVO> projectMapVOPageVO = new PageVO<>();
        if (total > 0) {
            List<ProjectMap> projectMapList = projectMapService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            projectMapVOPageVO.setTotalRow(total);
            projectMapVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(projectMapList),ProjectMapVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return projectMapVOPageVO;
    }


    /**
     * 修改 项目数据表
     *
     * @return R
     */
    @ApiOperation(value = "修改ProjectMap", notes = "修改ProjectMap")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改ProjectMap", value = "传入json格式", required = true)
                          @RequestBody ProjectMapVO projectMapVO) {
        ProjectMap newProjectMap = new ProjectMap();
        BeanUtils.copyProperties(projectMapVO, newProjectMap);
        boolean isUpdated = projectMapService.update(newProjectMap, new LambdaQueryWrapper<ProjectMap>()
                .eq(ProjectMap::getId, projectMapVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 项目数据表
     *
     * @return R
     */
    @ApiOperation(value = "删除ProjectMap", notes = "删除ProjectMap")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ProjectMapVO projectMapVO) {
        ProjectMap newProjectMap = new ProjectMap();
        BeanUtils.copyProperties(projectMapVO, newProjectMap);
        projectMapService.remove(new LambdaQueryWrapper<ProjectMap>()
                .eq(ProjectMap::getId, projectMapVO.getId()));
        return R.success("删除成功");
    }

}
