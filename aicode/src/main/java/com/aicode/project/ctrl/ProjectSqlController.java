/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;


import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.project.entity.ProjectSql;
import com.aicode.project.service.ProjectSqlService;
import com.aicode.project.vo.ProjectSqlPageVO;
import com.aicode.project.vo.ProjectSqlVO;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * 项目sql管理控制器
 * 1.查询一个详情信息
 * 2.查询项目sql信息集合
 * 3.创建项目sql
 * 4.修改项目sql
 * 5.删除项目sql
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/project/sql")
@Slf4j
@Tag(name = "项目sql脚本控制器", description = "项目sql脚本控制器")
public class ProjectSqlController {
    @Autowired
    private ProjectSqlService projectSqlService;


    /**
     * 创建 项目sql脚本
     *
     * @return R
     */
    @Operation(summary = "创建ProjectSql", description = "创建ProjectSql")
    @Parameters({
            @Parameter(name = "projectCode", description = "项目编码", required = true),
            @Parameter(name = "tsql", description = "sql脚本", required = true)
    })
    @PostMapping("/build")
    public R build(@Parameter(hidden = true) ProjectSql newProjectSql) {
        projectSqlService.save(newProjectSql);
        return R.success(newProjectSql);
    }


    /**
     * 查询一个详情信息
     *
     * @return BeanRet
     */
    @Operation(summary = "查询一个详情信息", description = "查询一个详情信息")
    @Parameters({
            @Parameter(name = "code", description = "sql编码"),
            @Parameter(name = "projectCode", description = "项目编码")
    })
    @GetMapping(value = "/load")
    public R load(String code, String projectCode) {
        Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
        ProjectSql projectSql = projectSqlService.getOne(new LambdaQueryWrapper<ProjectSql>()
                .eq(ProjectSql::getCode, code)
                .eq(ProjectSql::getProjectCode, projectCode));
        log.info(JSON.toJSONString(projectSql));
        return R.success(projectSql);

    }


    /**
     * 根据条件projectCode查询项目sql脚本一个详情信息
     *
     * @param projectCode 项目编码
     * @return ProjectSqlVO
     */
    @Operation(summary = "创建ProjectSql", description = "创建ProjectSql")
    @GetMapping("/load/projectCode/{projectCode}")
    public ProjectSqlVO loadByProjectCode(@PathVariable String projectCode) {
        if (projectCode == null) {
            return null;
        }
        ProjectSql projectSql = projectSqlService.getOne(new LambdaQueryWrapper<ProjectSql>()
                .eq(ProjectSql::getProjectCode, projectCode));
        ProjectSqlVO projectSqlVO = new ProjectSqlVO();
        BeanUtils.copyProperties(projectSql, projectSqlVO);
        log.debug(JSON.toJSONString(projectSqlVO));
        return projectSqlVO;
    }

    /**
     * 查询项目sql脚本信息集合
     *
     * @return 分页对象
     */
    @Operation(summary = "查询ProjectSql信息集合", description = "查询ProjectSql信息集合")
    @Parameters({
            @Parameter(name = "projectCode", description = "项目编码", required = true)
    })
    @GetMapping(value = "/list")
    public R list(@Parameter(hidden = true) ProjectSqlPageVO projectSqlVO) {
        QueryWrapper<ProjectSql> queryWrapper = new QueryWrapper<>();
        if (projectSqlVO.getCode() != null) {
            queryWrapper.lambda().eq(ProjectSql::getProjectCode, projectSqlVO.getProjectCode());
        }

        List<ProjectSql> projectSqlList = projectSqlService.list(queryWrapper);
        return R.success(projectSqlList);
    }


    /**
     * 修改 项目sql脚本
     *
     * @return R
     */
    @Operation(summary = "修改ProjectSql", description = "修改ProjectSql")
    @Parameters({
            @Parameter(name = "code", description = "tsql编码", required = true),
            @Parameter(name = "tsql", description = "sql脚本", required = true)
    })
    @PostMapping("/modify")
    public R modify(ProjectSqlVO projectSqlVO) {
        Assert.hasText(projectSqlVO.getCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        ProjectSql projectSqlLoad = projectSqlService.getOne(new LambdaQueryWrapper<ProjectSql>()
                .eq(ProjectSql::getCode, projectSqlVO.getCode()));
        if (null != projectSqlLoad) {
            projectSqlService.remove(new LambdaQueryWrapper<ProjectSql>().eq(ProjectSql::getCode, projectSqlVO.getCode()));
            projectSqlLoad.setTsql(projectSqlVO.getTsql());
            projectSqlService.save(projectSqlLoad);
            return R.success(projectSqlVO);
        } else {
            return R.failed(BaseException.BaseExceptionEnum.Server_Error);
        }
    }


    /**
     * 删除 项目sql脚本
     *
     * @return R
     */
    @Operation(summary = "删除ProjectSql", description = "删除ProjectSql")
    @Parameters({
            @Parameter(name = "id", description = ""),
            @Parameter(name = "projectCode", description = "项目编码")
    })
    @DeleteMapping("/delete")
    public R delete(@Parameter(hidden = true) ProjectSqlVO projectSqlVO) {
        ProjectSql newProjectSql = new ProjectSql();
        BeanUtils.copyProperties(projectSqlVO, newProjectSql);
        projectSqlService.remove(new LambdaQueryWrapper<ProjectSql>()
                .eq(ProjectSql::getId, projectSqlVO.getId()));
        return R.success("删除成功");
    }

}
