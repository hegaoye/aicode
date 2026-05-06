/*
 * aicode
 */
package com.aicode.project.ctrl;

import com.aicode.config.websocket.WSClientManager;
import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.project.entity.ProjectJob;
import com.aicode.project.service.ProjectJobService;
import com.aicode.project.vo.ProjectJobPageVO;
import com.aicode.project.vo.ProjectJobVO;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * 任务
 *
 * @author aicode
 */
@RestController
@RequestMapping("/project/job")
@Slf4j
@Tag(name = "任务控制器", description = "任务控制器")
public class ProjectJobController {
    @Autowired
    private ProjectJobService projectJobService;

    /**
     * 查询任务详情信息
     *
     * @param code 任务编码
     * @return BeanRet
     */
    @Operation(summary = "查询任务详情信息", description = "查询任务详情信息")
    @Parameters({
            @Parameter(name = "code", description = "任务编码")
    })
    @GetMapping(value = "/load")

    public R load(String code) {
        Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
        ProjectJob projectJob = projectJobService.getOne(new LambdaQueryWrapper<ProjectJob>()
                .eq(ProjectJob::getCode, code));
        log.info(JSON.toJSONString(projectJob));
        return R.success(projectJob);

    }

    /**
     * 创建 任务
     *
     * @return R
     */
    @Operation(summary = "创建ProjectJob", description = "创建ProjectJob")
    @Parameters({
            @Parameter(name = "projectCode", description = "项目编码", required = true),
            @Parameter(name = "name", description = "任务名", required = true),
            @Parameter(name = "description", description = "任务描述", required = true)
    })
    @PostMapping("/build")
    public R build(@Parameter(hidden = true) ProjectJob projectJob) {

        ProjectJob projectJobLoad = projectJobService.getOne(new LambdaQueryWrapper<ProjectJob>()
                .eq(ProjectJob::getProjectCode, projectJob.getProjectCode()));
        if (null != projectJobLoad) {
            return R.success(projectJobLoad);
        }
        projectJobService.save(projectJob);

        log.debug(JSON.toJSONString(projectJob));
        return R.success(projectJob);
    }


    /**
     * 根据条件code查询任务一个详情信息
     *
     * @param code 任务编码
     * @return ProjectJobVO
     */
    @Operation(summary = "创建ProjectJob", description = "创建ProjectJob")
    @GetMapping("/load/code/{code}")
    public ProjectJobVO loadByCode(@PathVariable String code) {
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
    @Operation(summary = "查询ProjectJob信息集合", description = "查询ProjectJob信息集合")
    @Parameters({
            @Parameter(name = "code", description = "项目编码"),
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true)
    })
    @GetMapping(value = "/list")
    public R list(@Parameter(hidden = true) ProjectJobPageVO projectJobVO, Integer curPage, Integer pageSize) {

        IPage<ProjectJob> page = new Page<>(curPage, pageSize);
        QueryWrapper<ProjectJob> queryWrapper = new QueryWrapper<>();
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotEmpty(projectJobVO.getProjectCode())) {
            queryWrapper.lambda().gt(ProjectJob::getProjectCode, projectJobVO.getProjectCode());
        }

        com.aicode.core.Page pageVO = new com.aicode.core.Page();
        long total = projectJobService.count(queryWrapper);
        if (total > 0) {
            queryWrapper.lambda().orderByDesc(ProjectJob::getId);

            IPage<ProjectJob> projectJobPage = projectJobService.page(page, queryWrapper);
            pageVO.setTotalRow(total);
            pageVO.setVoList(projectJobPage.getRecords());
            log.debug(JSON.toJSONString(page));
        }
        return R.success(pageVO);
    }


    /**
     * 修改 任务
     *
     * @return R
     */
    @Operation(summary = "修改ProjectJob", description = "修改ProjectJob")
    @Parameters({
            @Parameter(name = "code", description = "项目编码", required = true),
            @Parameter(name = "name", description = "任务名"),
            @Parameter(name = "state", description = "任务状态"),
            @Parameter(name = "description", description = "任务描述")
    })
    @PutMapping("/modify")
    public R modify(@Parameter(hidden = true) ProjectJob projectJob) {
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isEmpty(projectJob.getState())) {
            R.failed(BaseException.BaseExceptionEnum.Empty_Param);
        }
        projectJobService.update(projectJob, new LambdaQueryWrapper<ProjectJob>()
                .eq(ProjectJob::getCode, projectJob.getCode()));
        return R.success(projectJob);
    }


    /**
     * 删除 任务
     *
     * @return R
     */
    @Operation(summary = "删除ProjectJob", description = "删除ProjectJob")
    @Parameters({
            @Parameter(name = "code", description = "任务编码")
    })
    @DeleteMapping("/delete")
    public R delete(@Parameter(hidden = true) ProjectJobVO projectJobVO) {
        ProjectJob newProjectJob = new ProjectJob();
        BeanUtils.copyProperties(projectJobVO, newProjectJob);
        projectJobService.remove(new LambdaQueryWrapper<ProjectJob>()
                .eq(ProjectJob::getCode, projectJobVO.getCode()));
        return R.success("删除成功");
    }

    /**
     * 构建任务
     *
     * @param code 项目编码
     * @return BeanRet
     */
    @Operation(summary = "执行任务", description = "执行任务")
    @Parameters({
            @Parameter(name = "code", description = "任务编码")
    })
    @GetMapping(value = "/execute")

    public R execute(String code, HttpServletRequest request) {
        try {
            Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Session webSocketSession = WSClientManager.get();
            if (webSocketSession != null) {
                //生成代码
                ProjectJob projectJob = projectJobService.execute(code);
                return R.success(projectJob);
            }
            return R.failed(BaseException.BaseExceptionEnum.Result_Not_Exist);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return R.failed(BaseException.BaseExceptionEnum.Server_Error);
        }
    }

}
