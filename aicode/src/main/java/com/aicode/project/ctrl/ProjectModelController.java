/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.core.entity.Page;
import com.aicode.core.entity.R;
import com.aicode.project.entity.ProjectModel;
import com.aicode.project.service.ProjectModelService;
import com.aicode.project.vo.ProjectModelPageVO;
import com.aicode.project.vo.ProjectModelVO;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
     * 查询模块详情信息
     *
     * @param id
     * @return BeanRet
     */
    @ApiOperation(value = "查询模块详情信息", notes = "查询模块详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query")
    })
    @GetMapping(value = "/loadById/{id}")
    public R loadById(@PathVariable Long id) {
        if (id == null) {
            return R.failed("");
        }
        ProjectModel projectModel = projectModelService.getById(id);
        log.info(JSON.toJSONString(projectModel));
        return R.success(projectModel);
    }

    /**
     * 查询模块详情信息
     *
     * @param code 模块编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询模块详情信息", notes = "查询模块详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "模块编码", paramType = "query")
    })
    @GetMapping(value = "/loadByCode/{code}")
    public R loadByCode(@PathVariable String code) {
        if (StringUtils.isNotEmpty(code)) {
            return R.failed("");
        }

        ProjectModel projectModel = projectModelService.getOne(new LambdaQueryWrapper<ProjectModel>()
                .eq(ProjectModel::getCode, code));
        log.info(JSON.toJSONString(projectModel));
        return R.success(projectModel);
    }

    /**
     * 创建 模块
     *
     * @return R
     */
    @ApiOperation(value = "创建ProjectModel", notes = "创建ProjectModel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query", required = true),
            @ApiImplicitParam(name = "code", value = "模块编码", paramType = "query", required = true),
            @ApiImplicitParam(name = "preCode", value = "上级模块编码", paramType = "query", required = true),
            @ApiImplicitParam(name = "name", value = "模块显示名称", paramType = "query", required = true),
            @ApiImplicitParam(name = "route", value = "模块路由", paramType = "query", required = true),
            @ApiImplicitParam(name = "css", value = "模块css样式", paramType = "query", required = true),
            @ApiImplicitParam(name = "isMenu", value = "是否是菜单 Y,N", paramType = "query", required = true),
            @ApiImplicitParam(name = "ico", value = "模块图标", paramType = "query", required = true)
    })
    @PostMapping("/build")
    public R build(@ApiIgnore ProjectModel projectModel) {
        projectModelService.save(projectModel);
        return R.success(projectModel);
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
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "模块编码", paramType = "query"),
            @ApiImplicitParam(name = "preCode", value = "上级模块编码", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "模块显示名称", paramType = "query"),
            @ApiImplicitParam(name = "route", value = "模块路由", paramType = "query"),
            @ApiImplicitParam(name = "css", value = "模块css样式", paramType = "query"),
            @ApiImplicitParam(name = "isMenu", value = "是否是菜单 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "ico", value = "模块图标", paramType = "query")
    })
    @GetMapping(value = "/list")
    public R list(@ApiIgnore ProjectModelPageVO projectModelVO, Integer curPage, Integer pageSize) {
        Page<ProjectModel> page = new Page<>(pageSize, curPage);
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
        List<ProjectModel> projectModelList = projectModelService.list(queryWrapper, page.genRowStart(), page.getPageSize());
        page.setRecords(projectModelList);
        log.debug(JSON.toJSONString(page));
        return R.success(page);
    }


    /**
     * 修改 模块
     *
     * @return R
     */
    @ApiOperation(value = "修改ProjectModel", notes = "修改ProjectModel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "模块编码", paramType = "query"),
            @ApiImplicitParam(name = "preCode", value = "上级模块编码", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "模块显示名称", paramType = "query"),
            @ApiImplicitParam(name = "route", value = "模块路由", paramType = "query"),
            @ApiImplicitParam(name = "css", value = "模块css样式", paramType = "query"),
            @ApiImplicitParam(name = "isMenu", value = "是否是菜单 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "ico", value = "模块图标", paramType = "query")
    })
    @PutMapping("/modify")
    public R modify(@ApiParam(name = "修改ProjectModel", value = "传入json格式", required = true)
                    @RequestBody ProjectModel projectModel) {
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
                .eq(ProjectModel::getCode, projectModelVO.getCode()));
        return R.success("删除成功");
    }

}
