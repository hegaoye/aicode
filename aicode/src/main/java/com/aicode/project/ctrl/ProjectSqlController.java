/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.core.entity.R;
import com.aicode.core.exceptions.BaseException;
import com.aicode.project.entity.ProjectSql;
import com.aicode.project.service.ProjectSqlService;
import com.aicode.project.vo.ProjectSqlPageVO;
import com.aicode.project.vo.ProjectSqlVO;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
@Api(value = "项目sql脚本控制器", tags = "项目sql脚本控制器")
public class ProjectSqlController {
    @Autowired
    private ProjectSqlService projectSqlService;


    /**
     * 创建 项目sql脚本
     *
     * @return R
     */
    @ApiOperation(value = "创建ProjectSql", notes = "创建ProjectSql")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "tsql", value = "sql脚本", required = true, paramType = "query")
    })
    @PostMapping("/build")
    public R build(@ApiIgnore ProjectSql newProjectSql) {
        projectSqlService.save(newProjectSql);
        return R.success(newProjectSql);
    }


    /**
     * 查询一个详情信息
     *
     * @return BeanRet
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "sql编码", paramType = "query"),
            @ApiImplicitParam(name = "projectCode", value = "项目编码", paramType = "query")
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
    @ApiOperation(value = "创建ProjectSql", notes = "创建ProjectSql")
    @GetMapping("/load/projectCode/{projectCode}")
    public ProjectSqlVO loadByProjectCode(@PathVariable java.lang.String projectCode) {
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
    @ApiOperation(value = "查询ProjectSql信息集合", notes = "查询ProjectSql信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query")
    })
    @GetMapping(value = "/list")
    public R list(@ApiIgnore ProjectSqlPageVO projectSqlVO) {
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
    @ApiOperation(value = "修改ProjectSql", notes = "修改ProjectSql")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "tsql编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "tsql", value = "sql脚本", required = true, paramType = "query")
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
            return R.failed("");
        }
    }


    /**
     * 删除 项目sql脚本
     *
     * @return R
     */
    @ApiOperation(value = "删除ProjectSql", notes = "删除ProjectSql")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "projectCode", value = "项目编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ProjectSqlVO projectSqlVO) {
        ProjectSql newProjectSql = new ProjectSql();
        BeanUtils.copyProperties(projectSqlVO, newProjectSql);
        projectSqlService.remove(new LambdaQueryWrapper<ProjectSql>()
                .eq(ProjectSql::getId, projectSqlVO.getId()));
        return R.success("删除成功");
    }

}
