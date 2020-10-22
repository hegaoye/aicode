/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.project.entity.ProjectJob;
import com.aicode.project.service.ProjectJobService;
import com.aicode.project.vo.ProjectJobPageVO;
import com.aicode.project.vo.ProjectJobSaveVO;
import com.aicode.project.vo.ProjectJobVO;
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
 * 任务
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/projectJob")
@Slf4j
@Api(value = "任务控制器", tags = "任务控制器")
public class ProjectJobController {
    @Autowired
    private ProjectJobService projectJobService;


    /**
     * 创建 任务
     *
     * @return R
     */
    @ApiOperation(value = "创建ProjectJob", notes = "创建ProjectJob")
    @PostMapping("/build")
    public ProjectJobSaveVO build(@ApiParam(name = "创建ProjectJob", value = "传入json格式", required = true)
                                   @RequestBody ProjectJobSaveVO projectJobSaveVO) {
        if (null == projectJobSaveVO) {
            return null;
        }
        ProjectJob newProjectJob = new ProjectJob();
        BeanUtils.copyProperties(projectJobSaveVO, newProjectJob);

        projectJobService.save(newProjectJob);

        projectJobSaveVO = new ProjectJobSaveVO();
        BeanUtils.copyProperties(newProjectJob, projectJobSaveVO);
        log.debug(JSON.toJSONString(projectJobSaveVO));
        return projectJobSaveVO;
    }


    /**
     * 根据条件code查询任务一个详情信息
     *
     * @param code 任务编码
     * @return ProjectJobVO
     */
    @ApiOperation(value = "创建ProjectJob", notes = "创建ProjectJob")
    @GetMapping("/load/code/{code}")
    public ProjectJobVO loadByCode(@PathVariable java.lang.String code) {
        if (code == null) {
            return null;
        }
        ProjectJob projectJob = projectJobService.getOne(new LambdaQueryWrapper<ProjectJob>()
                .eq(ProjectJob::getCode, code));
        ProjectJobVO projectJobVO = new ProjectJobVO();
        BeanUtils.copyProperties(projectJob, projectJobVO);
        log.debug(JSON.toJSONString(projectJobVO));
        return projectJobVO;
    }

    /**
     * 查询任务信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询ProjectJob信息集合", notes = "查询ProjectJob信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
            @ApiImplicitParam(name = "createTimeBegin", value = "执行任务时间", paramType = "query"),
            @ApiImplicitParam(name = "createTimeEnd", value = "执行任务时间", paramType = "query")
    })
    @GetMapping(value = "/list")
    public PageVO<ProjectJobVO> list(@ApiIgnore ProjectJobPageVO projectJobVO, Integer curPage, Integer pageSize) {
        Page<ProjectJob> page = new Page<>(pageSize, curPage);
        QueryWrapper<ProjectJob> queryWrapper = new QueryWrapper<>();
        if (projectJobVO.getCreateTimeBegin() != null) {
            queryWrapper.lambda().gt(ProjectJob::getCreateTime, projectJobVO.getCreateTimeBegin());
        }
        if (projectJobVO.getCreateTimeEnd() != null) {
            queryWrapper.lambda().lt(ProjectJob::getCreateTime, projectJobVO.getCreateTimeEnd());
        }
        int total = projectJobService.count(queryWrapper);
        PageVO<ProjectJobVO> projectJobVOPageVO = new PageVO<>();
        if (total > 0) {
            List<ProjectJob> projectJobList = projectJobService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            projectJobVOPageVO.setTotalRow(total);
            projectJobVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(projectJobList),ProjectJobVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return projectJobVOPageVO;
    }


    /**
     * 修改 任务
     *
     * @return R
     */
    @ApiOperation(value = "修改ProjectJob", notes = "修改ProjectJob")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改ProjectJob", value = "传入json格式", required = true)
                          @RequestBody ProjectJobVO projectJobVO) {
        ProjectJob newProjectJob = new ProjectJob();
        BeanUtils.copyProperties(projectJobVO, newProjectJob);
        boolean isUpdated = projectJobService.update(newProjectJob, new LambdaQueryWrapper<ProjectJob>()
                .eq(ProjectJob::getId, projectJobVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 任务
     *
     * @return R
     */
    @ApiOperation(value = "删除ProjectJob", notes = "删除ProjectJob")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "任务编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ProjectJobVO projectJobVO) {
        ProjectJob newProjectJob = new ProjectJob();
        BeanUtils.copyProperties(projectJobVO, newProjectJob);
        projectJobService.remove(new LambdaQueryWrapper<ProjectJob>()
                .eq(ProjectJob::getId, projectJobVO.getId()));
        return R.success("删除成功");
    }

}
