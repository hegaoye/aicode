/*
 * aicode
 */
package com.aicode.project.ctrl;

import com.aicode.core.PageVO;
import com.aicode.core.R;
import com.aicode.project.entity.ProjectMap;
import com.aicode.project.service.ProjectMapService;
import com.aicode.project.vo.ProjectMapSaveVO;
import com.aicode.project.vo.ProjectMapVO;
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
 * 项目数据表
 *
 * @author aicode
 */
@RestController
@RequestMapping("/projectMap")
@Slf4j
@Tag(name = "项目数据表控制器", description = "项目数据表控制器")
public class ProjectMapController {
    @Autowired
    private ProjectMapService projectMapService;

    /**
     * 创建 项目数据表
     *
     * @return R
     */
    @Operation(summary = "创建ProjectMap", description = "创建ProjectMap")
    @PostMapping("/build")
    public ProjectMapSaveVO build(@RequestBody ProjectMapSaveVO projectMapSaveVO) {
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
    @Operation(summary = "查询ProjectMap信息集合", description = "查询ProjectMap信息集合")
    @Parameters({
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true),
    })
    @GetMapping(value = "/list")
    public PageVO<ProjectMapVO> list(Integer curPage, Integer pageSize) {
        IPage<ProjectMap> page = new Page<>(curPage, pageSize);
        QueryWrapper<ProjectMap> queryWrapper = new QueryWrapper<>();
        long total = projectMapService.count(queryWrapper);
        PageVO<ProjectMapVO> projectMapVOPageVO = new PageVO<>();
        if (total > 0) {
            queryWrapper.lambda().orderByDesc(ProjectMap::getId);

            IPage<ProjectMap> projectMapPage = projectMapService.page(page, queryWrapper);
            List<ProjectMapVO> projectMapPageVOList = JSON.parseArray(JSON.toJSONString(projectMapPage.getRecords()), ProjectMapVO.class);

            projectMapVOPageVO.setTotalRow(total);
            projectMapVOPageVO.setRecords(projectMapPageVOList);
            log.debug(JSON.toJSONString(page));
        }
        return projectMapVOPageVO;
    }


    /**
     * 修改 项目数据表
     *
     * @return R
     */
    @Operation(summary = "修改ProjectMap", description = "修改ProjectMap")
    @PutMapping("/modify")
    public boolean modify(@RequestBody ProjectMapVO projectMapVO) {
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
    @Operation(summary = "删除ProjectMap", description = "删除ProjectMap")
    @Parameters({
            @Parameter(name = "id", description = "id")
    })
    @DeleteMapping("/delete")
    public R delete(@Parameter(hidden = true) ProjectMapVO projectMapVO) {
        ProjectMap newProjectMap = new ProjectMap();
        BeanUtils.copyProperties(projectMapVO, newProjectMap);
        projectMapService.remove(new LambdaQueryWrapper<ProjectMap>()
                .eq(ProjectMap::getId, projectMapVO.getId()));
        return R.success("删除成功");
    }


}
