/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;


import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.project.entity.ProjectRepositoryAccount;
import com.aicode.project.service.ProjectRepositoryAccountService;
import com.aicode.project.vo.ProjectRepositoryAccountPageVO;
import com.aicode.project.vo.ProjectRepositoryAccountVO;
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


    /**
     * 查询一个详情信息
     *
     * @param code 版本管理编码
     * @return BeanRet
     */
    
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

    /**
     * 创建 版本控制管理
     *
     * @return R
     */
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


    /**
     * 根据条件code查询版本控制管理一个详情信息
     *
     * @param code 版本管理编码
     * @return ProjectRepositoryAccountVO
     */
    
    @Operation(summary = "创建ProjectRepositoryAccount", description = "创建ProjectRepositoryAccount")
    @GetMapping("/load/code/{code}")
    public ProjectRepositoryAccountVO loadByCode(@PathVariable String code) {
        if (code == null) {
            return null;
        }
        ProjectRepositoryAccount projectRepositoryAccount = projectRepositoryAccountService.getOne(new LambdaQueryWrapper<ProjectRepositoryAccount>()
                .eq(ProjectRepositoryAccount::getCode, code));
        ProjectRepositoryAccountVO projectRepositoryAccountVO = new ProjectRepositoryAccountVO();
        BeanUtils.copyProperties(projectRepositoryAccount, projectRepositoryAccountVO);
        log.debug(JSON.toJSONString(projectRepositoryAccountVO));
        return projectRepositoryAccountVO;
    }

    /**
     * 查询版本控制管理信息集合
     *
     * @return 分页对象
     */
    
    @Operation(summary = "查询ProjectRepositoryAccount信息集合", description = "查询ProjectRepositoryAccount信息集合")
    @Parameters({
            @Parameter(name = "projectCode", description = "项目编码"),
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true)
    })
    @GetMapping(value = "/list")
    public R list(@Parameter(hidden = true) ProjectRepositoryAccountPageVO projectRepositoryAccountVO) {
        IPage<ProjectRepositoryAccount> page = new Page<>();
        QueryWrapper<ProjectRepositoryAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProjectRepositoryAccount::getProjectCode, projectRepositoryAccountVO.getProjectCode());
        com.aicode.core.Page pageVO = new com.aicode.core.Page();
        long total = projectRepositoryAccountService.count(queryWrapper);
        if (total > 0) {
            IPage<ProjectRepositoryAccount> projectRepositoryAccountIPage = projectRepositoryAccountService.page(page, queryWrapper);


            pageVO.setTotalRow(total);
            pageVO.setVoList(projectRepositoryAccountIPage.getRecords());
        }
        return R.success(pageVO);
    }


    /**
     * 修改 版本控制管理
     *
     * @return R
     */
    
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


    /**
     * 删除 版本控制管理
     *
     * @return R
     */
    
    @Deprecated
    @Operation(summary = "删除ProjectRepositoryAccount", description = "删除ProjectRepositoryAccount")
    @Parameters({
            @Parameter(name = "code", description = "版本管理编码")
    })
    @DeleteMapping("/delete")
    public R delete(@Parameter(hidden = true) ProjectRepositoryAccountVO projectRepositoryAccountVO) {
        ProjectRepositoryAccount newProjectRepositoryAccount = new ProjectRepositoryAccount();
        BeanUtils.copyProperties(projectRepositoryAccountVO, newProjectRepositoryAccount);
        projectRepositoryAccountService.remove(new LambdaQueryWrapper<ProjectRepositoryAccount>()
                .eq(ProjectRepositoryAccount::getCode, projectRepositoryAccountVO.getCode()));
        return R.success("删除成功");
    }

}
