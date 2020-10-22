/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.project.entity.ProjectSql;
import com.aicode.project.service.ProjectSqlService;
import com.aicode.project.vo.ProjectSqlPageVO;
import com.aicode.project.vo.ProjectSqlSaveVO;
import com.aicode.project.vo.ProjectSqlVO;
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
 * 项目sql脚本
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/projectSql")
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
    @PostMapping("/build")
    public ProjectSqlSaveVO build(@ApiParam(name = "创建ProjectSql", value = "传入json格式", required = true)
                                   @RequestBody ProjectSqlSaveVO projectSqlSaveVO) {
        if (null == projectSqlSaveVO) {
            return null;
        }
        ProjectSql newProjectSql = new ProjectSql();
        BeanUtils.copyProperties(projectSqlSaveVO, newProjectSql);

        projectSqlService.save(newProjectSql);

        projectSqlSaveVO = new ProjectSqlSaveVO();
        BeanUtils.copyProperties(newProjectSql, projectSqlSaveVO);
        log.debug(JSON.toJSONString(projectSqlSaveVO));
        return projectSqlSaveVO;
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
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<ProjectSqlVO> list(@ApiIgnore ProjectSqlPageVO projectSqlVO, Integer curPage, Integer pageSize) {
        Page<ProjectSql> page = new Page<>(pageSize, curPage);
        QueryWrapper<ProjectSql> queryWrapper = new QueryWrapper<>();
        if (projectSqlVO.getCode() != null) {
            queryWrapper.lambda().eq(ProjectSql::getCode, projectSqlVO.getCode());
        }
        if (projectSqlVO.getTsql() != null) {
            queryWrapper.lambda().eq(ProjectSql::getTsql, projectSqlVO.getTsql());
        }
        int total = projectSqlService.count(queryWrapper);
        PageVO<ProjectSqlVO> projectSqlVOPageVO = new PageVO<>();
        if (total > 0) {
            List<ProjectSql> projectSqlList = projectSqlService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            projectSqlVOPageVO.setTotalRow(total);
            projectSqlVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(projectSqlList),ProjectSqlVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return projectSqlVOPageVO;
    }


    /**
     * 修改 项目sql脚本
     *
     * @return R
     */
    @ApiOperation(value = "修改ProjectSql", notes = "修改ProjectSql")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改ProjectSql", value = "传入json格式", required = true)
                          @RequestBody ProjectSqlVO projectSqlVO) {
        ProjectSql newProjectSql = new ProjectSql();
        BeanUtils.copyProperties(projectSqlVO, newProjectSql);
        boolean isUpdated = projectSqlService.update(newProjectSql, new LambdaQueryWrapper<ProjectSql>()
                .eq(ProjectSql::getId, projectSqlVO.getId()));
        return isUpdated;
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
