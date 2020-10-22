/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.project.entity.ProjectModel;
import com.aicode.project.service.ProjectModelService;
import com.aicode.project.vo.ProjectModelPageVO;
import com.aicode.project.vo.ProjectModelSaveVO;
import com.aicode.project.vo.ProjectModelVO;
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
 * 模块
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/projectModel")
@Slf4j
@Api(value = "模块控制器", tags = "模块控制器")
public class ProjectModelController {
    @Autowired
    private ProjectModelService projectModelService;


    /**
     * 创建 模块
     *
     * @return R
     */
    @ApiOperation(value = "创建ProjectModel", notes = "创建ProjectModel")
    @PostMapping("/build")
    public ProjectModelSaveVO build(@ApiParam(name = "创建ProjectModel", value = "传入json格式", required = true)
                                   @RequestBody ProjectModelSaveVO projectModelSaveVO) {
        if (null == projectModelSaveVO) {
            return null;
        }
        ProjectModel newProjectModel = new ProjectModel();
        BeanUtils.copyProperties(projectModelSaveVO, newProjectModel);

        projectModelService.save(newProjectModel);

        projectModelSaveVO = new ProjectModelSaveVO();
        BeanUtils.copyProperties(newProjectModel, projectModelSaveVO);
        log.debug(JSON.toJSONString(projectModelSaveVO));
        return projectModelSaveVO;
    }


    /**
     * 根据条件code查询模块一个详情信息
     *
     * @param code 模块编码
     * @return ProjectModelVO
     */
    @ApiOperation(value = "创建ProjectModel", notes = "创建ProjectModel")
    @GetMapping("/load/code/{code}")
    public ProjectModelVO loadByCode(@PathVariable java.lang.String code) {
        if (code == null) {
            return null;
        }
        ProjectModel projectModel = projectModelService.getOne(new LambdaQueryWrapper<ProjectModel>()
                .eq(ProjectModel::getCode, code));
        ProjectModelVO projectModelVO = new ProjectModelVO();
        BeanUtils.copyProperties(projectModel, projectModelVO);
        log.debug(JSON.toJSONString(projectModelVO));
        return projectModelVO;
    }

    /**
     * 查询模块信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询ProjectModel信息集合", notes = "查询ProjectModel信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<ProjectModelVO> list(@ApiIgnore ProjectModelPageVO projectModelVO, Integer curPage, Integer pageSize) {
        Page<ProjectModel> page = new Page<>(pageSize, curPage);
        QueryWrapper<ProjectModel> queryWrapper = new QueryWrapper<>();
        if (projectModelVO.getCss() != null) {
            queryWrapper.lambda().eq(ProjectModel::getCss, projectModelVO.getCss());
        }
        if (projectModelVO.getIsMenu() != null) {
            queryWrapper.lambda().eq(ProjectModel::getIsMenu, projectModelVO.getIsMenu());
        }
        int total = projectModelService.count(queryWrapper);
        PageVO<ProjectModelVO> projectModelVOPageVO = new PageVO<>();
        if (total > 0) {
            List<ProjectModel> projectModelList = projectModelService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            projectModelVOPageVO.setTotalRow(total);
            projectModelVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(projectModelList),ProjectModelVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return projectModelVOPageVO;
    }


    /**
     * 修改 模块
     *
     * @return R
     */
    @ApiOperation(value = "修改ProjectModel", notes = "修改ProjectModel")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改ProjectModel", value = "传入json格式", required = true)
                          @RequestBody ProjectModelVO projectModelVO) {
        ProjectModel newProjectModel = new ProjectModel();
        BeanUtils.copyProperties(projectModelVO, newProjectModel);
        boolean isUpdated = projectModelService.update(newProjectModel, new LambdaQueryWrapper<ProjectModel>()
                .eq(ProjectModel::getId, projectModelVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 模块
     *
     * @return R
     */
    @ApiOperation(value = "删除ProjectModel", notes = "删除ProjectModel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "模块编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ProjectModelVO projectModelVO) {
        ProjectModel newProjectModel = new ProjectModel();
        BeanUtils.copyProperties(projectModelVO, newProjectModel);
        projectModelService.remove(new LambdaQueryWrapper<ProjectModel>()
                .eq(ProjectModel::getId, projectModelVO.getId()));
        return R.success("删除成功");
    }

}
