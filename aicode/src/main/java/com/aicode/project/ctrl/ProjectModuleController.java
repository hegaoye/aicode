/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;


import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.project.entity.ProjectModule;
import com.aicode.project.service.ProjectModuleService;
import com.aicode.project.vo.ProjectModulePageVO;
import com.aicode.project.vo.ProjectModuleVO;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * 项目选择模块
 * 1.查询一个详情信息
 * 2.查询信息集合
 * 3.添加模块
 * 4.删除
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/project/mouldles")
@Slf4j
@Tag(name = "项目选择模块控制器", description = "项目选择模块控制器")
public class ProjectModuleController {
    @Autowired
    private ProjectModuleService projectModuleService;


    @Operation(summary = "查询一个详情信息", description = "查询一个详情信息")
    @Parameters({
            @Parameter(name = "projectCode", description = "项目编码", required = true),
            @Parameter(name = "moudleCode", description = "模块编码", required = true)
    })
    @GetMapping(value = "/load")
    public R load(String projectCode, String moudleCode) {
        Assert.hasText(projectCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(moudleCode, BaseException.BaseExceptionEnum.Empty_Param.toString());

        ProjectModule projectModule = projectModuleService.getOne(new LambdaQueryWrapper<ProjectModule>()
                .eq(ProjectModule::getProjectCode, projectCode)
                .eq(ProjectModule::getModuleCode, moudleCode));

        return R.success(projectModule);

    }


    @Operation(summary = "创建ProjectModule", description = "创建ProjectModule")
    @Parameters({
            @Parameter(name = "projectCode", description = "项目编码", required = true),
            @Parameter(name = "moudleCode", description = "模块编码", required = true)
    })
    @PostMapping("/add")
    public R build(@Parameter(hidden = true) ProjectModule projectModule) {

        projectModuleService.save(projectModule);

        return R.success(projectModule);
    }


    @Operation(summary = "查询ProjectModule信息集合", description = "查询ProjectModule信息集合")
    @Parameters({
            @Parameter(name = "projectCode", description = "项目编码", required = true),
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true)
    })
    @GetMapping(value = "/list")
    public R list(@Parameter(hidden = true) ProjectModulePageVO projectModuleVO, Integer curPage, Integer pageSize) {
        IPage<ProjectModule> page = new Page<>(pageSize, curPage);
        QueryWrapper<ProjectModule> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProjectModule::getProjectCode, projectModuleVO.getProjectCode());

        com.aicode.core.Page pageVO = new com.aicode.core.Page();
        long total = projectModuleService.count(queryWrapper);
        if (total > 0) {
            IPage<ProjectModule> projectModuleIPage = projectModuleService.page(page, queryWrapper);

            pageVO.setTotalRow(total);
            pageVO.setVoList(projectModuleIPage.getRecords());
            log.debug(JSON.toJSONString(page));
        }
        return R.success(pageVO);
    }

}
