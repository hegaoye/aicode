/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.core.entity.Page;
import com.aicode.core.entity.R;
import com.aicode.project.entity.ProjectJobLogs;
import com.aicode.project.service.ProjectJobLogsService;
import com.aicode.project.vo.ProjectJobLogsPageVO;
import com.aicode.project.vo.ProjectJobLogsSaveVO;
import com.aicode.project.vo.ProjectJobLogsVO;
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
 * 任务日志
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/projectJobLogs")
@Slf4j
@Api(value = "任务日志控制器", tags = "任务日志控制器")
public class ProjectJobLogsController {
    @Autowired
    private ProjectJobLogsService projectJobLogsService;


    /**
     * 创建 任务日志
     *
     * @return R
     */
    @ApiOperation(value = "创建ProjectJobLogs", notes = "创建ProjectJobLogs")
    @PostMapping("/build")
    public ProjectJobLogsSaveVO build(@ApiParam(name = "创建ProjectJobLogs", value = "传入json格式", required = true)
                                      @RequestBody ProjectJobLogsSaveVO projectJobLogsSaveVO) {
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
    @ApiOperation(value = "查询ProjectJobLogs信息集合", notes = "查询ProjectJobLogs信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "任务编码", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @GetMapping(value = "/list")
    public R list(@ApiIgnore ProjectJobLogsPageVO projectJobLogsVO, Integer curPage, Integer pageSize) {
        Page<ProjectJobLogs> page = new Page<>(pageSize, curPage);
        QueryWrapper<ProjectJobLogs> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProjectJobLogs::getCode, projectJobLogsVO.getCode());

        int total = projectJobLogsService.count(queryWrapper);
        if (total > 0) {
            List<ProjectJobLogs> projectJobLogsList = projectJobLogsService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            page.setTotalRow(total);
            page.setRecords(projectJobLogsList);
            log.debug(JSON.toJSONString(page));
        }

        return R.success(page);
    }


    /**
     * 修改 任务日志
     *
     * @return R
     */
    @ApiOperation(value = "修改ProjectJobLogs", notes = "修改ProjectJobLogs")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改ProjectJobLogs", value = "传入json格式", required = true)
                          @RequestBody ProjectJobLogsVO projectJobLogsVO) {
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
    @ApiOperation(value = "删除ProjectJobLogs", notes = "删除ProjectJobLogs")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ProjectJobLogsVO projectJobLogsVO) {
        ProjectJobLogs newProjectJobLogs = new ProjectJobLogs();
        BeanUtils.copyProperties(projectJobLogsVO, newProjectJobLogs);
        projectJobLogsService.remove(new LambdaQueryWrapper<ProjectJobLogs>()
                .eq(ProjectJobLogs::getId, projectJobLogsVO.getId()));
        return R.success("删除成功");
    }

}
