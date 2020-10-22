/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.project.entity.ProjectModule;
import com.aicode.project.service.ProjectModuleService;
import com.aicode.project.vo.ProjectModulePageVO;
import com.aicode.project.vo.ProjectModuleSaveVO;
import com.aicode.project.vo.ProjectModuleVO;
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
 * 项目选择模块
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/projectModule")
@Slf4j
@Api(value = "项目选择模块控制器", tags = "项目选择模块控制器")
public class ProjectModuleController {
    @Autowired
    private ProjectModuleService projectModuleService;


    /**
     * 创建 项目选择模块
     *
     * @return R
     */
    @ApiOperation(value = "创建ProjectModule", notes = "创建ProjectModule")
    @PostMapping("/build")
    public ProjectModuleSaveVO build(@ApiParam(name = "创建ProjectModule", value = "传入json格式", required = true)
                                   @RequestBody ProjectModuleSaveVO projectModuleSaveVO) {
        if (null == projectModuleSaveVO) {
            return null;
        }
        ProjectModule newProjectModule = new ProjectModule();
        BeanUtils.copyProperties(projectModuleSaveVO, newProjectModule);

        projectModuleService.save(newProjectModule);

        projectModuleSaveVO = new ProjectModuleSaveVO();
        BeanUtils.copyProperties(newProjectModule, projectModuleSaveVO);
        log.debug(JSON.toJSONString(projectModuleSaveVO));
        return projectModuleSaveVO;
    }



    /**
     * 查询项目选择模块信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询ProjectModule信息集合", notes = "查询ProjectModule信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<ProjectModuleVO> list(@ApiIgnore ProjectModulePageVO projectModuleVO, Integer curPage, Integer pageSize) {
        Page<ProjectModule> page = new Page<>(pageSize, curPage);
        QueryWrapper<ProjectModule> queryWrapper = new QueryWrapper<>();
        int total = projectModuleService.count(queryWrapper);
        PageVO<ProjectModuleVO> projectModuleVOPageVO = new PageVO<>();
        if (total > 0) {
            List<ProjectModule> projectModuleList = projectModuleService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            projectModuleVOPageVO.setTotalRow(total);
            projectModuleVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(projectModuleList),ProjectModuleVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return projectModuleVOPageVO;
    }


    /**
     * 修改 项目选择模块
     *
     * @return R
     */
    @ApiOperation(value = "修改ProjectModule", notes = "修改ProjectModule")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改ProjectModule", value = "传入json格式", required = true)
                          @RequestBody ProjectModuleVO projectModuleVO) {
        ProjectModule newProjectModule = new ProjectModule();
        BeanUtils.copyProperties(projectModuleVO, newProjectModule);
        boolean isUpdated = projectModuleService.update(newProjectModule, new LambdaQueryWrapper<ProjectModule>()
                .eq(ProjectModule::getId, projectModuleVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 项目选择模块
     *
     * @return R
     */
    @ApiOperation(value = "删除ProjectModule", notes = "删除ProjectModule")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ProjectModuleVO projectModuleVO) {
        ProjectModule newProjectModule = new ProjectModule();
        BeanUtils.copyProperties(projectModuleVO, newProjectModule);
        projectModuleService.remove(new LambdaQueryWrapper<ProjectModule>()
                .eq(ProjectModule::getId, projectModuleVO.getId()));
        return R.success("删除成功");
    }

}
