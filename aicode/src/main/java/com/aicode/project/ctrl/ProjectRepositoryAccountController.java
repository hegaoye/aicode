/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;


import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.project.entity.ProjectRepositoryAccount;
import com.aicode.project.service.ProjectRepositoryAccountService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * 版本控制管理
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/project/repository")
@Slf4j
@Tag(name = "版本控制管理控制器", description = "版本控制管理控制器")
public class ProjectRepositoryAccountController {
    @Autowired
    private ProjectRepositoryAccountService projectRepositoryAccountService;


    @Operation(summary = "查询一个详情信息", description = "查询一个详情信息")
    @Parameters({
            @Parameter(name = "code", description = "版本管理编码")
    })
    @GetMapping(value = "/load")
    public R load(String code) {
        Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
        ProjectRepositoryAccount projectRepositoryAccount = projectRepositoryAccountService.getOne(new LambdaQueryWrapper<ProjectRepositoryAccount>()
                .eq(ProjectRepositoryAccount::getCode, code));

        return R.success(projectRepositoryAccount);

    }

    @Operation(summary = "创建ProjectRepositoryAccount", description = "创建ProjectRepositoryAccount")
    @Parameters({
            @Parameter(name = "account", description = "帐户名 最长32个汉字", required = true),
            @Parameter(name = "projectCode", description = "项目编码"),
            @Parameter(name = "password", description = "密码 最长32个英文字符", required = true),
            @Parameter(name = "home", description = "仓库地址", required = true),
            @Parameter(name = "description", description = "仓库说明 最长128个汉字", required = true),
            @Parameter(name = "type", description = "仓库类型:Git, Svn", required = true)
    })
    @PostMapping("/build")
    public R build(@Parameter(hidden = true) ProjectRepositoryAccount projectRepositoryAccount) {
        projectRepositoryAccountService.save(projectRepositoryAccount);
        return R.success(projectRepositoryAccount);
    }


    @Operation(summary = "修改ProjectRepositoryAccount", description = "修改ProjectRepositoryAccount")
    @Parameters({
            @Parameter(name = "code", description = "版本管理编码", required = true),
            @Parameter(name = "projectCode", description = "项目编码"),
            @Parameter(name = "account", description = "帐户名 最长32个汉字", required = true),
            @Parameter(name = "password", description = "密码 最长32个英文字符", required = true),
            @Parameter(name = "home", description = "仓库地址", required = true),
            @Parameter(name = "description", description = "仓库说明 最长128个汉字", required = true),
            @Parameter(name = "state", description = "状态：停用[Disenable]，启用[Enable]", required = true),
            @Parameter(name = "type", description = "仓库类型:Git, Svn", required = true)
    })
    @PostMapping("/modify")
    public R modify(@Parameter(hidden = true) ProjectRepositoryAccount projectRepositoryAccount) {
        ProjectRepositoryAccount projectRepositoryAccountLoad = projectRepositoryAccountService.getOne(new LambdaQueryWrapper<ProjectRepositoryAccount>()
                .eq(ProjectRepositoryAccount::getCode, projectRepositoryAccount.getCode()));
        if (projectRepositoryAccountLoad != null) {
            projectRepositoryAccount.setId(projectRepositoryAccountLoad.getId());
            projectRepositoryAccountService.updateById(projectRepositoryAccount);

        }
        return R.success(projectRepositoryAccount);
    }

}
