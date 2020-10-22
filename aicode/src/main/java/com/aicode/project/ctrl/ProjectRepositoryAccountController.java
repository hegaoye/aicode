/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.project.entity.ProjectRepositoryAccount;
import com.aicode.project.service.ProjectRepositoryAccountService;
import com.aicode.project.vo.ProjectRepositoryAccountPageVO;
import com.aicode.project.vo.ProjectRepositoryAccountSaveVO;
import com.aicode.project.vo.ProjectRepositoryAccountVO;
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
 * 版本控制管理
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/projectRepositoryAccount")
@Slf4j
@Api(value = "版本控制管理控制器", tags = "版本控制管理控制器")
public class ProjectRepositoryAccountController {
    @Autowired
    private ProjectRepositoryAccountService projectRepositoryAccountService;


    /**
     * 创建 版本控制管理
     *
     * @return R
     */
    @ApiOperation(value = "创建ProjectRepositoryAccount", notes = "创建ProjectRepositoryAccount")
    @PostMapping("/build")
    public ProjectRepositoryAccountSaveVO build(@ApiParam(name = "创建ProjectRepositoryAccount", value = "传入json格式", required = true)
                                   @RequestBody ProjectRepositoryAccountSaveVO projectRepositoryAccountSaveVO) {
        if (null == projectRepositoryAccountSaveVO) {
            return null;
        }
        ProjectRepositoryAccount newProjectRepositoryAccount = new ProjectRepositoryAccount();
        BeanUtils.copyProperties(projectRepositoryAccountSaveVO, newProjectRepositoryAccount);

        projectRepositoryAccountService.save(newProjectRepositoryAccount);

        projectRepositoryAccountSaveVO = new ProjectRepositoryAccountSaveVO();
        BeanUtils.copyProperties(newProjectRepositoryAccount, projectRepositoryAccountSaveVO);
        log.debug(JSON.toJSONString(projectRepositoryAccountSaveVO));
        return projectRepositoryAccountSaveVO;
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
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<ProjectRepositoryAccountVO> list(@ApiIgnore ProjectRepositoryAccountPageVO projectRepositoryAccountVO, Integer curPage, Integer pageSize) {
        Page<ProjectRepositoryAccount> page = new Page<>(pageSize, curPage);
        QueryWrapper<ProjectRepositoryAccount> queryWrapper = new QueryWrapper<>();
        int total = projectRepositoryAccountService.count(queryWrapper);
        PageVO<ProjectRepositoryAccountVO> projectRepositoryAccountVOPageVO = new PageVO<>();
        if (total > 0) {
            List<ProjectRepositoryAccount> projectRepositoryAccountList = projectRepositoryAccountService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            projectRepositoryAccountVOPageVO.setTotalRow(total);
            projectRepositoryAccountVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(projectRepositoryAccountList),ProjectRepositoryAccountVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return projectRepositoryAccountVOPageVO;
    }


    /**
     * 修改 版本控制管理
     *
     * @return R
     */
    @ApiOperation(value = "修改ProjectRepositoryAccount", notes = "修改ProjectRepositoryAccount")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改ProjectRepositoryAccount", value = "传入json格式", required = true)
                          @RequestBody ProjectRepositoryAccountVO projectRepositoryAccountVO) {
        ProjectRepositoryAccount newProjectRepositoryAccount = new ProjectRepositoryAccount();
        BeanUtils.copyProperties(projectRepositoryAccountVO, newProjectRepositoryAccount);
        boolean isUpdated = projectRepositoryAccountService.update(newProjectRepositoryAccount, new LambdaQueryWrapper<ProjectRepositoryAccount>()
                .eq(ProjectRepositoryAccount::getId, projectRepositoryAccountVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 版本控制管理
     *
     * @return R
     */
    @ApiOperation(value = "删除ProjectRepositoryAccount", notes = "删除ProjectRepositoryAccount")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "版本管理编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ProjectRepositoryAccountVO projectRepositoryAccountVO) {
        ProjectRepositoryAccount newProjectRepositoryAccount = new ProjectRepositoryAccount();
        BeanUtils.copyProperties(projectRepositoryAccountVO, newProjectRepositoryAccount);
        projectRepositoryAccountService.remove(new LambdaQueryWrapper<ProjectRepositoryAccount>()
                .eq(ProjectRepositoryAccount::getId, projectRepositoryAccountVO.getId()));
        return R.success("删除成功");
    }

}
