/*
 * aicode
 */
package com.aicode.project.ctrl;

import com.aicode.core.R;
import com.aicode.project.entity.ProjectJobLogs;
import com.aicode.project.service.ProjectJobLogsService;
import com.aicode.project.vo.ProjectJobLogsPageVO;
import com.aicode.project.vo.ProjectJobLogsSaveVO;
import com.aicode.project.vo.ProjectJobLogsVO;
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

/**
 * 任务日志
 *
 * @author aicode
 */
@RestController
@RequestMapping("/projectJobLogs")
@Slf4j
@Tag(name = "任务日志控制器", description = "任务日志控制器")
public class ProjectJobLogsController {
    @Autowired
    private ProjectJobLogsService projectJobLogsService;

    /**
     * 创建 任务日志
     *
     * @return R
     */
    @Operation(summary = "创建ProjectJobLogs", description = "创建ProjectJobLogs")
    @PostMapping("/build")
    public ProjectJobLogsSaveVO build(@RequestBody ProjectJobLogsSaveVO projectJobLogsSaveVO) {
        if (null == projectJobLogsSaveVO) {
            return null;
        }
        ProjectJobLogs newProjectJobLogs = new ProjectJobLogs();
        BeanUtils.copyProperties(projectJobLogsSaveVO, newProjectJobLogs);

        projectJobLogsService.save(newProjectJobLogs);

        projectJobLogsSaveVO = new ProjectJobLogsSaveVO();
        BeanUtils.copyProperties(newProjectJobLogs, projectJobLogsSaveVO);
        log.debug(JSON.toJSONString(projectJobLogsSaveVO));
        return projectJobLogsSaveVO;
    }


    /**
     * 查询任务日志信息集合
     *
     * @return 分页对象
     */
    @Operation(summary = "查询ProjectJobLogs信息集合", description = "查询ProjectJobLogs信息集合")
    @Parameters({
            @Parameter(name = "code", description = "任务编码"),
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true)
    })
    @GetMapping(value = "/list")
    public R list(@Parameter(hidden = true) ProjectJobLogsPageVO projectJobLogsVO, Integer curPage, Integer pageSize) {
        IPage<ProjectJobLogs> page = new Page<>(curPage, pageSize);

        QueryWrapper<ProjectJobLogs> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProjectJobLogs::getCode, projectJobLogsVO.getCode());

        com.aicode.core.Page pageVO = new com.aicode.core.Page();
        long total = projectJobLogsService.count(queryWrapper);
        if (total > 0) {

            queryWrapper.lambda().orderByDesc(ProjectJobLogs::getId);

            IPage<ProjectJobLogs> projectJobLogsPage = projectJobLogsService.page(page, queryWrapper);

            pageVO.setTotalRow(total);
            pageVO.setVoList(projectJobLogsPage.getRecords());
            log.debug(JSON.toJSONString(page));
        }

        return R.success(pageVO);
    }


    /**
     * 修改 任务日志
     *
     * @return R
     */
    @Operation(summary = "修改ProjectJobLogs", description = "修改ProjectJobLogs")
    @PutMapping("/modify")
    public boolean modify(@RequestBody ProjectJobLogsVO projectJobLogsVO) {
        ProjectJobLogs newProjectJobLogs = new ProjectJobLogs();
        BeanUtils.copyProperties(projectJobLogsVO, newProjectJobLogs);
        boolean isUpdated = projectJobLogsService.update(newProjectJobLogs, new LambdaQueryWrapper<ProjectJobLogs>()
                .eq(ProjectJobLogs::getId, projectJobLogsVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 任务日志
     *
     * @return R
     */
    @Operation(summary = "删除ProjectJobLogs", description = "删除ProjectJobLogs")
    @Parameters({
            @Parameter(name = "id", description = "id")
    })
    @DeleteMapping("/delete")
    public R delete(@Parameter(hidden = true) ProjectJobLogsVO projectJobLogsVO) {
        ProjectJobLogs newProjectJobLogs = new ProjectJobLogs();
        BeanUtils.copyProperties(projectJobLogsVO, newProjectJobLogs);
        projectJobLogsService.remove(new LambdaQueryWrapper<ProjectJobLogs>()
                .eq(ProjectJobLogs::getId, projectJobLogsVO.getId()));
        return R.success("删除成功");
    }

}
