/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.project.entity.ProjectModelClass;
import com.aicode.project.service.ProjectModelClassService;
import com.aicode.project.vo.ProjectModelClassPageVO;
import com.aicode.project.vo.ProjectModelClassSaveVO;
import com.aicode.project.vo.ProjectModelClassVO;
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
 * 模块下的类
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/projectModelClass")
@Slf4j
@Api(value = "模块下的类控制器", tags = "模块下的类控制器")
public class ProjectModelClassController {
    @Autowired
    private ProjectModelClassService projectModelClassService;


    /**
     * 创建 模块下的类
     *
     * @return R
     */
    @ApiOperation(value = "创建ProjectModelClass", notes = "创建ProjectModelClass")
    @PostMapping("/build")
    public ProjectModelClassSaveVO build(@ApiParam(name = "创建ProjectModelClass", value = "传入json格式", required = true)
                                   @RequestBody ProjectModelClassSaveVO projectModelClassSaveVO) {
        if (null == projectModelClassSaveVO) {
            return null;
        }
        ProjectModelClass newProjectModelClass = new ProjectModelClass();
        BeanUtils.copyProperties(projectModelClassSaveVO, newProjectModelClass);

        projectModelClassService.save(newProjectModelClass);

        projectModelClassSaveVO = new ProjectModelClassSaveVO();
        BeanUtils.copyProperties(newProjectModelClass, projectModelClassSaveVO);
        log.debug(JSON.toJSONString(projectModelClassSaveVO));
        return projectModelClassSaveVO;
    }


    /**
     * 根据条件mapClassTableCode查询模块下的类一个详情信息
     *
     * @param mapClassTableCode 类编码
     * @return ProjectModelClassVO
     */
    @ApiOperation(value = "创建ProjectModelClass", notes = "创建ProjectModelClass")
    @GetMapping("/load/mapClassTableCode/{mapClassTableCode}")
    public ProjectModelClassVO loadByMapClassTableCode(@PathVariable java.lang.String mapClassTableCode) {
        if (mapClassTableCode == null) {
            return null;
        }
        ProjectModelClass projectModelClass = projectModelClassService.getOne(new LambdaQueryWrapper<ProjectModelClass>()
                .eq(ProjectModelClass::getMapClassTableCode, mapClassTableCode));
        ProjectModelClassVO projectModelClassVO = new ProjectModelClassVO();
        BeanUtils.copyProperties(projectModelClass, projectModelClassVO);
        log.debug(JSON.toJSONString(projectModelClassVO));
        return projectModelClassVO;
    }
    /**
     * 根据条件projectModelCode查询模块下的类一个详情信息
     *
     * @param projectModelCode 模块编码
     * @return ProjectModelClassVO
     */
    @ApiOperation(value = "创建ProjectModelClass", notes = "创建ProjectModelClass")
    @GetMapping("/load/projectModelCode/{projectModelCode}")
    public ProjectModelClassVO loadByProjectModelCode(@PathVariable java.lang.String projectModelCode) {
        if (projectModelCode == null) {
            return null;
        }
        ProjectModelClass projectModelClass = projectModelClassService.getOne(new LambdaQueryWrapper<ProjectModelClass>()
                .eq(ProjectModelClass::getProjectModelCode, projectModelCode));
        ProjectModelClassVO projectModelClassVO = new ProjectModelClassVO();
        BeanUtils.copyProperties(projectModelClass, projectModelClassVO);
        log.debug(JSON.toJSONString(projectModelClassVO));
        return projectModelClassVO;
    }

    /**
     * 查询模块下的类信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询ProjectModelClass信息集合", notes = "查询ProjectModelClass信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<ProjectModelClassVO> list(@ApiIgnore ProjectModelClassPageVO projectModelClassVO, Integer curPage, Integer pageSize) {
        Page<ProjectModelClass> page = new Page<>(pageSize, curPage);
        QueryWrapper<ProjectModelClass> queryWrapper = new QueryWrapper<>();
        int total = projectModelClassService.count(queryWrapper);
        PageVO<ProjectModelClassVO> projectModelClassVOPageVO = new PageVO<>();
        if (total > 0) {
            List<ProjectModelClass> projectModelClassList = projectModelClassService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            projectModelClassVOPageVO.setTotalRow(total);
            projectModelClassVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(projectModelClassList),ProjectModelClassVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return projectModelClassVOPageVO;
    }


    /**
     * 修改 模块下的类
     *
     * @return R
     */
    @ApiOperation(value = "修改ProjectModelClass", notes = "修改ProjectModelClass")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改ProjectModelClass", value = "传入json格式", required = true)
                          @RequestBody ProjectModelClassVO projectModelClassVO) {
        ProjectModelClass newProjectModelClass = new ProjectModelClass();
        BeanUtils.copyProperties(projectModelClassVO, newProjectModelClass);
        boolean isUpdated = projectModelClassService.update(newProjectModelClass, new LambdaQueryWrapper<ProjectModelClass>()
                .eq(ProjectModelClass::getId, projectModelClassVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 模块下的类
     *
     * @return R
     */
    @ApiOperation(value = "删除ProjectModelClass", notes = "删除ProjectModelClass")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "mapClassTableCode", value = "类编码", paramType = "query"),
            @ApiImplicitParam(name = "projectModelCode", value = "模块编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ProjectModelClassVO projectModelClassVO) {
        ProjectModelClass newProjectModelClass = new ProjectModelClass();
        BeanUtils.copyProperties(projectModelClassVO, newProjectModelClass);
        projectModelClassService.remove(new LambdaQueryWrapper<ProjectModelClass>()
                .eq(ProjectModelClass::getId, projectModelClassVO.getId()));
        return R.success("删除成功");
    }

}
