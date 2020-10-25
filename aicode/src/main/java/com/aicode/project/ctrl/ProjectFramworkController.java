/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.core.entity.Page;
import com.aicode.core.entity.R;
import com.aicode.core.exceptions.BaseException;
import com.aicode.project.entity.ProjectFramwork;
import com.aicode.project.service.ProjectFramworkService;
import com.aicode.project.vo.ProjectFramworkPageVO;
import com.aicode.project.vo.ProjectFramworkVO;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 项目应用技术
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/project/framwork")
@Slf4j
@Api(value = "项目应用技术控制器", tags = "项目应用技术控制器")
public class ProjectFramworkController {
    @Autowired
    private ProjectFramworkService projectFramworkService;


    /**
     * 查询一个详情信息
     *
     * @param projectCode   项目编码
     * @param frameworkCode 技术编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "frameworkCode", value = "技术编码", required = true, paramType = "query")
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

    /**
     * 创建 项目应用技术
     *
     * @return R
     */
    @ApiOperation(value = "创建ProjectFramwork", notes = "创建ProjectFramwork")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectStr", value = "项目技术json", required = true, paramType = "query")
    })
    @PostMapping({"/build", "/add"})
    public R build(@ApiIgnore String projectStr) {
        List<ProjectFramwork> projectFramwors = JSON.parseArray(projectStr, ProjectFramwork.class);
        if (CollectionUtils.isEmpty(projectFramwors)) {
            return R.failed("");
        }
        projectFramworkService.remove(new LambdaQueryWrapper<ProjectFramwork>()
                .eq(ProjectFramwork::getProjectCode, projectFramwors.get(0).getProjectCode()));

        projectFramworkService.saveBatch(projectFramwors);

        return R.success(projectFramwors);
    }


    /**
     * 查询项目应用技术信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询ProjectFramwork信息集合", notes = "查询ProjectFramwork信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @GetMapping(value = "/list")
    public R list(@ApiIgnore ProjectFramworkPageVO projectFramworkVO, Integer curPage, Integer pageSize) {
        Page<ProjectFramwork> page = new Page<>(pageSize, curPage);
        QueryWrapper<ProjectFramwork> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(projectFramworkVO.getProjectCode())) {
            queryWrapper.lambda().eq(ProjectFramwork::getProjectCode, projectFramworkVO.getProjectCode());
        }

        int total = projectFramworkService.count(queryWrapper);
        if (total > 0) {
            List<ProjectFramwork> projectFramworkList = projectFramworkService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            page.setTotalRow(total);
            page.setVoList(projectFramworkList);
            log.debug(JSON.toJSONString(page));
        }
        return R.success(page);
    }


    /**
     * 修改 项目应用技术
     *
     * @return R
     */
    @ApiOperation(value = "修改ProjectFramwork", notes = "修改ProjectFramwork")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改ProjectFramwork", value = "传入json格式", required = true)
                          @RequestBody ProjectFramworkVO projectFramworkVO) {
        ProjectFramwork newProjectFramwork = new ProjectFramwork();
        BeanUtils.copyProperties(projectFramworkVO, newProjectFramwork);
        boolean isUpdated = projectFramworkService.update(newProjectFramwork, new LambdaQueryWrapper<ProjectFramwork>()
                .eq(ProjectFramwork::getId, projectFramworkVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 项目应用技术
     *
     * @return R
     */
    @ApiOperation(value = "删除ProjectFramwork", notes = "删除ProjectFramwork")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "frameworkCode", value = "添加项目技术", required = true, paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ProjectFramworkVO projectFramworkVO) {
        Assert.hasText(projectFramworkVO.getFrameworkCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(projectFramworkVO.getProjectCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());

        ProjectFramwork newProjectFramwork = new ProjectFramwork();
        BeanUtils.copyProperties(projectFramworkVO, newProjectFramwork);
        projectFramworkService.remove(new LambdaQueryWrapper<ProjectFramwork>()
                .eq(ProjectFramwork::getProjectCode, projectFramworkVO.getProjectCode())
                .eq(ProjectFramwork::getFrameworkCode, projectFramworkVO.getFrameworkCode()));
        return R.success("删除成功");
    }

}
