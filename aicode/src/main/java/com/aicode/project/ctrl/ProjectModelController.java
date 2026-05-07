/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;


import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.project.entity.ProjectModel;
import com.aicode.project.service.ProjectModelService;
import com.aicode.project.vo.ProjectModelPageVO;
import com.aicode.project.vo.ProjectModelVO;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
 * 模块
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/projectModel")
@Slf4j
@Tag(name = "模块控制器", description = "模块控制器")
public class ProjectModelController {
    @Autowired
    private ProjectModelService projectModelService;


    @Operation(summary = "创建ProjectModel", description = "创建ProjectModel")
    @Parameters({
            @Parameter(name = "id", description = "", required = true),
            @Parameter(name = "code", description = "模块编码", required = true),
            @Parameter(name = "preCode", description = "上级模块编码", required = true),
            @Parameter(name = "name", description = "模块显示名称", required = true),
            @Parameter(name = "route", description = "模块路由", required = true),
            @Parameter(name = "css", description = "模块css样式", required = true),
            @Parameter(name = "isMenu", description = "是否是菜单 Y,N", required = true),
            @Parameter(name = "ico", description = "模块图标", required = true)
    })
    @PostMapping("/build")
    public R build(@Parameter(hidden = true) ProjectModel projectModel) {
        projectModelService.save(projectModel);
        return R.success(projectModel);
    }


    @Operation(summary = "查询ProjectModel信息集合", description = "查询ProjectModel信息集合")
    @Parameters({
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true),
            @Parameter(name = "id", description = ""),
            @Parameter(name = "code", description = "模块编码"),
            @Parameter(name = "preCode", description = "上级模块编码"),
            @Parameter(name = "name", description = "模块显示名称"),
            @Parameter(name = "route", description = "模块路由"),
            @Parameter(name = "css", description = "模块css样式"),
            @Parameter(name = "isMenu", description = "是否是菜单 Y,N"),
            @Parameter(name = "ico", description = "模块图标")
    })
    @GetMapping(value = "/list")
    public R list(@Parameter(hidden = true) ProjectModelPageVO projectModelVO, Integer curPage, Integer pageSize) {
        IPage<ProjectModel> page = new Page<>(pageSize, curPage);
        QueryWrapper<ProjectModel> queryWrapper = new QueryWrapper<>();
        if (projectModelVO.getId() != null) {
            queryWrapper.lambda().eq(ProjectModel::getId, projectModelVO.getId());
        }
        if (StringUtils.isNotEmpty(projectModelVO.getCode())) {
            queryWrapper.lambda().eq(ProjectModel::getCode, projectModelVO.getCode());
        }
        if (StringUtils.isNotEmpty(projectModelVO.getPreCode())) {
            queryWrapper.lambda().eq(ProjectModel::getPreCode, projectModelVO.getPreCode());
        }
        if (StringUtils.isNotEmpty(projectModelVO.getRoute())) {
            queryWrapper.lambda().eq(ProjectModel::getRoute, projectModelVO.getRoute());
        }
        if (StringUtils.isNotEmpty(projectModelVO.getName())) {
            queryWrapper.lambda().eq(ProjectModel::getName, projectModelVO.getName());
        }
        if (StringUtils.isNotEmpty(projectModelVO.getCss())) {
            queryWrapper.lambda().eq(ProjectModel::getCss, projectModelVO.getCss());
        }
        if (StringUtils.isNotEmpty(projectModelVO.getIsMenu())) {
            queryWrapper.lambda().eq(ProjectModel::getIsMenu, projectModelVO.getIsMenu());
        }
        if (StringUtils.isNotEmpty(projectModelVO.getIco())) {
            queryWrapper.lambda().eq(ProjectModel::getIco, projectModelVO.getIco());
        }
        IPage<ProjectModel> projectMapPage = projectModelService.page(page, queryWrapper);


        com.aicode.core.Page pageVO = new com.aicode.core.Page();
        pageVO.setVoList(projectMapPage.getRecords());
        log.debug(JSON.toJSONString(page));
        return R.success(pageVO);
    }


    @Operation(summary = "修改ProjectModel", description = "修改ProjectModel")
    @Parameters({
            @Parameter(name = "id", description = ""),
            @Parameter(name = "code", description = "模块编码"),
            @Parameter(name = "preCode", description = "上级模块编码"),
            @Parameter(name = "name", description = "模块显示名称"),
            @Parameter(name = "route", description = "模块路由"),
            @Parameter(name = "css", description = "模块css样式"),
            @Parameter(name = "isMenu", description = "是否是菜单 Y,N"),
            @Parameter(name = "ico", description = "模块图标")
    })
    @PutMapping("/modify")
    public R modify(@RequestBody ProjectModel projectModel) {
        LambdaQueryWrapper<ProjectModel> lambdaQueryWrapper = new LambdaQueryWrapper<ProjectModel>();
        if (projectModel.getId() != null) {
            lambdaQueryWrapper.eq(ProjectModel::getId, projectModel.getId());
        }
        if (StringUtils.isNotEmpty(projectModel.getCode())) {
            lambdaQueryWrapper.eq(ProjectModel::getCode, projectModel.getCode());
        }
        projectModelService.update(projectModel, lambdaQueryWrapper);
        return R.success();
    }


    @Operation(summary = "删除ProjectModel", description = "删除ProjectModel")
    @Parameters({
            @Parameter(name = "id", description = ""),
            @Parameter(name = "code", description = "模块编码")
    })
    @DeleteMapping("/delete")
    public R delete(@Parameter(hidden = true) ProjectModelVO projectModelVO) {
        ProjectModel newProjectModel = new ProjectModel();
        BeanUtils.copyProperties(projectModelVO, newProjectModel);
        projectModelService.remove(new LambdaQueryWrapper<ProjectModel>()
                .eq(ProjectModel::getCode, projectModelVO.getCode()));
        return R.success("删除成功");
    }

}
