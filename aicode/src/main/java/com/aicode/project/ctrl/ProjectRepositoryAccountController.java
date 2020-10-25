/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.core.entity.Page;
import com.aicode.core.entity.R;
import com.aicode.core.exceptions.BaseException;
import com.aicode.project.entity.ProjectRepositoryAccount;
import com.aicode.project.service.ProjectRepositoryAccountService;
import com.aicode.project.vo.ProjectRepositoryAccountPageVO;
import com.aicode.project.vo.ProjectRepositoryAccountVO;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 版本控制管理
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/project/repository")
@Slf4j
@Api(value = "版本控制管理控制器", tags = "版本控制管理控制器")
public class ProjectRepositoryAccountController {
    @Autowired
    private ProjectRepositoryAccountService projectRepositoryAccountService;


    /**
     * 查询一个详情信息
     *
     * @param code 版本管理编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "版本管理编码", paramType = "query")
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
    @ApiOperation(value = "创建ProjectRepositoryAccount", notes = "创建ProjectRepositoryAccount")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "帐户名 最长32个汉字", required = true, paramType = "query"),
            @ApiImplicitParam(name = "projectCode", value = "项目编码", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码 最长32个英文字符", required = true, paramType = "query"),
            @ApiImplicitParam(name = "home", value = "仓库地址", required = true, paramType = "query"),
            @ApiImplicitParam(name = "description", value = "仓库说明 最长128个汉字", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "仓库类型:Git, Svn", required = true, paramType = "query")
    })
    @PostMapping("/build")
    public R build(@ApiIgnore ProjectRepositoryAccount projectRepositoryAccount) {
        projectRepositoryAccountService.save(projectRepositoryAccount);
        return R.success(projectRepositoryAccount);
    }


    /**
     * 根据条件code查询版本控制管理一个详情信息
     *
     * @param code 版本管理编码
     * @return ProjectRepositoryAccountVO
     */
    @ApiOperation(value = "创建ProjectRepositoryAccount", notes = "创建ProjectRepositoryAccount")
    @GetMapping("/load/code/{code}")
    public ProjectRepositoryAccountVO loadByCode(@PathVariable java.lang.String code) {
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
    @ApiOperation(value = "查询ProjectRepositoryAccount信息集合", notes = "查询ProjectRepositoryAccount信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @GetMapping(value = "/list")
    public R list(@ApiIgnore ProjectRepositoryAccountPageVO projectRepositoryAccountVO) {
        Page<ProjectRepositoryAccount> page = new Page<>();
        QueryWrapper<ProjectRepositoryAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProjectRepositoryAccount::getProjectCode, projectRepositoryAccountVO.getProjectCode());
        int total = projectRepositoryAccountService.count(queryWrapper);
        if (total > 0) {
            List<ProjectRepositoryAccount> projectRepositoryAccountList = projectRepositoryAccountService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            page.setTotalRow(total);
            page.setVoList(projectRepositoryAccountList);
        }
        return R.success(page);
    }


    /**
     * 修改 版本控制管理
     *
     * @return R
     */
    @ApiOperation(value = "修改ProjectRepositoryAccount", notes = "修改ProjectRepositoryAccount")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "版本管理编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "projectCode", value = "项目编码", paramType = "query"),
            @ApiImplicitParam(name = "account", value = "帐户名 最长32个汉字", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码 最长32个英文字符", required = true, paramType = "query"),
            @ApiImplicitParam(name = "home", value = "仓库地址", required = true, paramType = "query"),
            @ApiImplicitParam(name = "description", value = "仓库说明 最长128个汉字", required = true, paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态：停用[Disenable]，启用[Enable]", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "仓库类型:Git, Svn", required = true, paramType = "query")
    })
    @PutMapping("/modify")
    public R modify(@ApiIgnore ProjectRepositoryAccount projectRepositoryAccount) {
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
    @ApiOperation(value = "删除ProjectRepositoryAccount", notes = "删除ProjectRepositoryAccount")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "版本管理编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ProjectRepositoryAccountVO projectRepositoryAccountVO) {
        ProjectRepositoryAccount newProjectRepositoryAccount = new ProjectRepositoryAccount();
        BeanUtils.copyProperties(projectRepositoryAccountVO, newProjectRepositoryAccount);
        projectRepositoryAccountService.remove(new LambdaQueryWrapper<ProjectRepositoryAccount>()
                .eq(ProjectRepositoryAccount::getCode, projectRepositoryAccountVO.getCode()));
        return R.success("删除成功");
    }

}
