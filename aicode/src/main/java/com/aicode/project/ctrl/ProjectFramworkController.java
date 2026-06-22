/*
 * aicode
 */
package com.aicode.project.ctrl;

import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.project.entity.ProjectFramwork;
import com.aicode.project.service.ProjectFramworkService;
import com.aicode.project.vo.ProjectFramworkPageVO;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目应用技术
 *
 * @author aicode
 */
@Slf4j
@RestController
@RequestMapping("/project/framwork")
@Tag(name = "项目应用技术控制器", description = "项目应用技术控制器")
public class ProjectFramworkController {
    @Autowired
    private ProjectFramworkService projectFramworkService;

    @Operation(summary = "查询一个详情信息", description = "查询一个详情信息")
    @Parameters({
            @Parameter(name = "projectCode", description = "项目编码", required = true),
            @Parameter(name = "frameworkCode", description = "技术编码", required = true)
    })
    @GetMapping(value = "/load")
    public R load(String projectCode, String frameworkCode) {
        Assert.hasText(projectCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(frameworkCode, BaseException.BaseExceptionEnum.Empty_Param.toString());

        ProjectFramwork projectFramwork = projectFramworkService.getOne(new LambdaQueryWrapper<ProjectFramwork>()
                .eq(ProjectFramwork::getProjectCode, projectCode)
                .eq(ProjectFramwork::getFrameworkCode, frameworkCode));

        log.info(JSON.toJSONString(projectFramwork));
        return R.success(projectFramwork);

    }

    @Operation(summary = "创建ProjectFramwork", description = "创建ProjectFramwork")
    @Parameters({
            @Parameter(name = "projectStr", description = "项目技术json", required = true)
    })
    @PostMapping("/add")
    public R add(@Parameter(hidden = true) String projectStr) {
        return doBuild(projectStr);
    }

    private R doBuild(String projectStr) {
        List<ProjectFramwork> projectFramwors = JSON.parseArray(projectStr, ProjectFramwork.class);
        if (CollectionUtils.isEmpty(projectFramwors)) {
            return R.failed(BaseException.BaseExceptionEnum.Empty_Param);
        }
        projectFramworkService.remove(new LambdaQueryWrapper<ProjectFramwork>()
                .eq(ProjectFramwork::getProjectCode, projectFramwors.get(0).getProjectCode()));

        projectFramworkService.saveBatch(projectFramwors);

        return R.success(projectFramwors);
    }


    @Operation(summary = "查询ProjectFramwork信息集合", description = "查询ProjectFramwork信息集合")
    @Parameters({
            @Parameter(name = "projectCode", description = "项目编码", required = true),
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true)
    })
    @GetMapping(value = "/list")
    public R list(@Parameter(hidden = true) ProjectFramworkPageVO projectFramworkVO, Integer curPage, Integer pageSize) {
        IPage<ProjectFramwork> page = new Page<>(curPage, pageSize);

        QueryWrapper<ProjectFramwork> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(projectFramworkVO.getProjectCode())) {
            queryWrapper.lambda().eq(ProjectFramwork::getProjectCode, projectFramworkVO.getProjectCode());
        }

        com.aicode.core.Page pageVO = new com.aicode.core.Page();
        long total = projectFramworkService.count(queryWrapper);
        if (total > 0) {
            IPage<ProjectFramwork> projectFramworkPage = projectFramworkService.page(page, queryWrapper);

            pageVO.setTotalRow(total);
            pageVO.setVoList(projectFramworkPage.getRecords());
            log.debug(JSON.toJSONString(page));
        }
        return R.success(pageVO);
    }


}
