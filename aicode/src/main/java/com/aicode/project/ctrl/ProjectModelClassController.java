/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;


import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.project.entity.ProjectModelClass;
import com.aicode.project.service.ProjectModelClassService;
import com.aicode.project.vo.ProjectModelClassPageVO;
import com.aicode.project.vo.ProjectModelClassVO;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 模块下的类
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/projectModelClass")
@Slf4j
@Tag(name = "模块下的类控制器", description = "模块下的类控制器")
public class ProjectModelClassController {
    @Autowired
    private ProjectModelClassService projectModelClassService;


    /**
     * 查询模块下的类详情信息
     *
     * @param id
     * @return BeanRet
     */
    @Operation(summary = "查询模块下的类详情信息", description = "查询模块下的类详情信息")
    @Parameters({
            @Parameter(name = "id", description = "")
    })
    @GetMapping(value = "/loadById/{id}")

    public R loadById(@PathVariable Long id) {
        if (id == null) {
            return R.failed(BaseException.BaseExceptionEnum.Empty_Param);
        }

        ProjectModelClass projectModelClass = projectModelClassService.getById(id);
        log.info(JSON.toJSONString(projectModelClass));
        return R.success(projectModelClass);
    }

    /**
     * 创建 模块下的类
     *
     * @return R
     */
    @Operation(summary = "创建ProjectModelClass", description = "创建ProjectModelClass")
    @Parameters({
            @Parameter(name = "id", description = "", required = true),
            @Parameter(name = "mapClassTableCode", description = "类编码", required = true),
            @Parameter(name = "projectModelCode", description = "模块编码", required = true)
    })

    @PostMapping({"/build", "save"})
    public ProjectModelClassVO build(@Parameter(hidden = true) ProjectModelClass projectModelClass) {
        projectModelClassService.save(projectModelClass);
        log.debug(JSON.toJSONString(projectModelClass));
        return JSON.parseObject(JSON.toJSONString(projectModelClass), ProjectModelClassVO.class);
    }


    /**
     * 查询模块下的类详情信息
     *
     * @param mapClassTableCode 类编码
     * @return BeanRet
     */
    @Operation(summary = "查询模块下的类详情信息", description = "查询模块下的类详情信息")
    @Parameters({
            @Parameter(name = "mapClassTableCode", description = "类编码")
    })
    @GetMapping(value = "/loadByMapClassTableCode")

    public R loadByMapClassTableCode(@PathVariable String mapClassTableCode) {
        if (StringUtils.isEmpty(mapClassTableCode)) {
            return R.failed(BaseException.BaseExceptionEnum.Empty_Param);
        }
        ProjectModelClass projectModelClass = projectModelClassService.getOne(new LambdaQueryWrapper<ProjectModelClass>()
                .eq(ProjectModelClass::getMapClassTableCode, mapClassTableCode));
        log.info(JSON.toJSONString(projectModelClass));
        return R.success(projectModelClass);
    }


    /**
     * 查询模块下的类详情信息
     *
     * @param projectModelCode 模块编码
     * @return BeanRet
     */
    @Operation(summary = "查询模块下的类详情信息", description = "查询模块下的类详情信息")
    @Parameters({
            @Parameter(name = "projectModelCode", description = "模块编码")
    })
    @GetMapping(value = "/loadByProjectModelCode")

    public R loadByProjectModelCode(@PathVariable String projectModelCode) {
        if (StringUtils.isEmpty(projectModelCode)) {
            return R.failed(BaseException.BaseExceptionEnum.Empty_Param);
        }

        ProjectModelClass projectModelClass = projectModelClassService.getOne(new LambdaQueryWrapper<ProjectModelClass>()
                .eq(ProjectModelClass::getProjectModelCode, projectModelCode));
        log.info(JSON.toJSONString(projectModelClass));
        return R.success(projectModelClass);
    }

    /**
     * 查询模块下的类信息集合
     *
     * @return 分页对象
     */
    @Operation(summary = "查询ProjectModelClass信息集合", description = "查询ProjectModelClass信息集合")
    @Parameters({
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true),
            @Parameter(name = "id", description = ""),
            @Parameter(name = "mapClassTableCode", description = "类编码"),
            @Parameter(name = "projectModelCode", description = "模块编码")
    })
    @GetMapping(value = "/list")
    public R list(@Parameter(hidden = true) ProjectModelClassPageVO projectModelClassVO, Integer curPage, Integer pageSize) {
        IPage<ProjectModelClass> page = new Page<>(curPage, pageSize);
        QueryWrapper<ProjectModelClass> queryWrapper = new QueryWrapper<>();
        if (projectModelClassVO.getId() > 0) {
            queryWrapper.lambda().eq(ProjectModelClass::getId, projectModelClassVO.getId());
        }

        if (!StringUtils.isEmpty(projectModelClassVO.getMapClassTableCode())) {
            queryWrapper.lambda()
                    .eq(ProjectModelClass::getMapClassTableCode, projectModelClassVO.getMapClassTableCode());
        }

        if (!StringUtils.isEmpty(projectModelClassVO.getProjectModelCode())) {
            queryWrapper.lambda().eq(ProjectModelClass::getProjectModelCode, projectModelClassVO.getProjectModelCode());
        }

        com.aicode.core.Page pageVO = new com.aicode.core.Page();
        long total = projectModelClassService.count(queryWrapper);
        if (total > 0) {
            queryWrapper.lambda().orderByDesc(ProjectModelClass::getId);

            IPage<ProjectModelClass> projectMapPage = projectModelClassService.page(page, queryWrapper);

            pageVO.setTotalRow(total);
            pageVO.setVoList(projectMapPage.getRecords());
            log.debug(JSON.toJSONString(page));
        }
        return R.success(pageVO);
    }


    /**
     * 修改 模块下的类
     *
     * @return R
     */
    @Operation(summary = "修改ProjectModelClass", description = "修改ProjectModelClass")
    @Parameters({
            @Parameter(name = "id", description = ""),
            @Parameter(name = "mapClassTableCode", description = "类编码"),
            @Parameter(name = "projectModelCode", description = "模块编码")
    })
    @PutMapping("/modify")
    public R modify(@Parameter(hidden = true) ProjectModelClass projectModelClass) {
        projectModelClassService.updateById(projectModelClass);
        return R.success();
    }


    /**
     * 删除 模块下的类
     *
     * @return R
     */
    @Operation(summary = "删除ProjectModelClass", description = "删除ProjectModelClass")
    @Parameters({
            @Parameter(name = "id", description = ""),
            @Parameter(name = "mapClassTableCode", description = "类编码"),
            @Parameter(name = "projectModelCode", description = "模块编码")
    })
    @DeleteMapping("/delete")
    public R delete(Long id, String mapClassTableCode, String projectModelCode) {
        if (id == null) {
            return R.failed(BaseException.BaseExceptionEnum.Empty_Param);
        }
        if (mapClassTableCode == null) {
            return R.failed(BaseException.BaseExceptionEnum.Empty_Param);
        }
        if (projectModelCode == null) {
            return R.failed(BaseException.BaseExceptionEnum.Empty_Param);
        }
        projectModelClassService.remove(new LambdaQueryWrapper<ProjectModelClass>()
                .eq(ProjectModelClass::getId, id)
                .eq(ProjectModelClass::getProjectModelCode, projectModelCode)
                .eq(ProjectModelClass::getMapClassTableCode, mapClassTableCode));
        return R.success("删除成功");
    }

}
