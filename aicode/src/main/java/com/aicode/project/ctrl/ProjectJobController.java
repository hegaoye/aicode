/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.config.websocket.WSClientManager;
import com.aicode.core.exceptions.BaseException;
import com.aicode.project.entity.ProjectJob;
import com.aicode.project.service.ProjectJobService;
import com.aicode.project.vo.ProjectJobPageVO;
import com.aicode.project.vo.ProjectJobVO;
import com.aicode.core.entity.Page;
import com.aicode.core.entity.R;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;
import java.util.List;

/**
 * 任务
 * 1.查询任务详情信息
 * 2.查询项目任务信息集合
 * 3.创建任务
 * 4.修改任务
 * 5.删除任务
 * 6.执行任务
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/project/job")
@Slf4j
@Api(value = "任务控制器", tags = "任务控制器")
public class ProjectJobController {
    @Autowired
    private ProjectJobService projectJobService;
    @Autowired
    private WSClientManager wsClientManager;

    /**
     * 查询任务详情信息
     *
     * @param code 任务编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询任务详情信息", notes = "查询任务详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "任务编码", paramType = "query")
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
    @ApiOperation(value = "创建ProjectJob", notes = "创建ProjectJob")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "name", value = "任务名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "description", value = "任务描述", required = true, paramType = "query")
    })
    @PostMapping("/build")
    public R build(@ApiIgnore ProjectJob projectJob) {

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
            @ApiImplicitParam(name = "code", value = "项目编码", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @GetMapping(value = "/list")
    public R list(@ApiIgnore ProjectJobPageVO projectJobVO, Integer curPage, Integer pageSize) {
        Page<ProjectJob> page = new Page<>(pageSize, curPage);
        QueryWrapper<ProjectJob> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(projectJobVO.getProjectCode())) {
            queryWrapper.lambda().gt(ProjectJob::getProjectCode, projectJobVO.getProjectCode());
        }

        int total = projectJobService.count(queryWrapper);
        if (total > 0) {
            List<ProjectJob> projectJobList = projectJobService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            page.setTotalRow(total);
            page.setVoList(projectJobList);
            log.debug(JSON.toJSONString(page));
        }
        return R.success(page);
    }


    /**
     * 修改 任务
     *
     * @return R
     */
    @ApiOperation(value = "修改ProjectJob", notes = "修改ProjectJob")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "name", value = "任务名", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "任务状态", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "任务描述", paramType = "query")
    })
    @PutMapping("/modify")
    public R modify(@ApiIgnore ProjectJob projectJob) {
        if (StringUtils.isEmpty(projectJob.getState())) {
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
    @ApiOperation(value = "删除ProjectJob", notes = "删除ProjectJob")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "任务编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ProjectJobVO projectJobVO) {
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
    @ApiOperation(value = "执行任务", notes = "执行任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "任务编码", paramType = "query")
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
            return R.failed("");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return R.failed("执行任务失败");
        }
    }
}
