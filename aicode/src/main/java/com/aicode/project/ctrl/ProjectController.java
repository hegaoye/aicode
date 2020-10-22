/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.project.entity.Project;
import com.aicode.project.service.ProjectService;
import com.aicode.project.vo.ProjectPageVO;
import com.aicode.project.vo.ProjectSaveVO;
import com.aicode.project.vo.ProjectVO;
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
 * 项目信息
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/project")
@Slf4j
@Api(value = "项目信息控制器", tags = "项目信息控制器")
public class ProjectController {
    @Autowired
    private ProjectService projectService;


    /**
     * 创建 项目信息
     *
     * @return R
     */
    @ApiOperation(value = "创建Project", notes = "创建Project")
    @PostMapping("/build")
    public ProjectSaveVO build(@ApiParam(name = "创建Project", value = "传入json格式", required = true)
                                   @RequestBody ProjectSaveVO projectSaveVO) {
        if (null == projectSaveVO) {
            return null;
        }
        Project newProject = new Project();
        BeanUtils.copyProperties(projectSaveVO, newProject);

        projectService.save(newProject);

        projectSaveVO = new ProjectSaveVO();
        BeanUtils.copyProperties(newProject, projectSaveVO);
        log.debug(JSON.toJSONString(projectSaveVO));
        return projectSaveVO;
    }


    /**
     * 根据条件code查询项目信息一个详情信息
     *
     * @param code 项目编码
     * @return ProjectVO
     */
    @ApiOperation(value = "创建Project", notes = "创建Project")
    @GetMapping("/load/code/{code}")
    public ProjectVO loadByCode(@PathVariable java.lang.String code) {
        if (code == null) {
            return null;
        }
        Project project = projectService.getOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getCode, code));
        ProjectVO projectVO = new ProjectVO();
        BeanUtils.copyProperties(project, projectVO);
        log.debug(JSON.toJSONString(projectVO));
        return projectVO;
    }

    /**
     * 查询项目信息信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询Project信息集合", notes = "查询Project信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
            @ApiImplicitParam(name = "createTimeBegin", value = "创建时间", paramType = "query"),
            @ApiImplicitParam(name = "createTimeEnd", value = "创建时间", paramType = "query"),
            @ApiImplicitParam(name = "updateTimeBegin", value = "更新时间", paramType = "query"),
            @ApiImplicitParam(name = "updateTimeEnd", value = "更新时间", paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<ProjectVO> list(@ApiIgnore ProjectPageVO projectVO, Integer curPage, Integer pageSize) {
        Page<Project> page = new Page<>(pageSize, curPage);
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        if (projectVO.getCreateTimeBegin() != null) {
            queryWrapper.lambda().gt(Project::getCreateTime, projectVO.getCreateTimeBegin());
        }
        if (projectVO.getCreateTimeEnd() != null) {
            queryWrapper.lambda().lt(Project::getCreateTime, projectVO.getCreateTimeEnd());
        }
        if (projectVO.getUpdateTimeBegin() != null) {
            queryWrapper.lambda().gt(Project::getUpdateTime, projectVO.getUpdateTimeBegin());
        }
        if (projectVO.getUpdateTimeEnd() != null) {
            queryWrapper.lambda().lt(Project::getUpdateTime, projectVO.getUpdateTimeEnd());
        }
        int total = projectService.count(queryWrapper);
        PageVO<ProjectVO> projectVOPageVO = new PageVO<>();
        if (total > 0) {
            List<Project> projectList = projectService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            projectVOPageVO.setTotalRow(total);
            projectVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(projectList),ProjectVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return projectVOPageVO;
    }


    /**
     * 修改 项目信息
     *
     * @return R
     */
    @ApiOperation(value = "修改Project", notes = "修改Project")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改Project", value = "传入json格式", required = true)
                          @RequestBody ProjectVO projectVO) {
        Project newProject = new Project();
        BeanUtils.copyProperties(projectVO, newProject);
        boolean isUpdated = projectService.update(newProject, new LambdaQueryWrapper<Project>()
                .eq(Project::getId, projectVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 项目信息
     *
     * @return R
     */
    @ApiOperation(value = "删除Project", notes = "删除Project")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "项目编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ProjectVO projectVO) {
        Project newProject = new Project();
        BeanUtils.copyProperties(projectVO, newProject);
        projectService.remove(new LambdaQueryWrapper<Project>()
                .eq(Project::getId, projectVO.getId()));
        return R.success("删除成功");
    }

}
