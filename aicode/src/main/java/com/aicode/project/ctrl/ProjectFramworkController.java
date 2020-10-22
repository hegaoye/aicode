/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.project.entity.ProjectFramwork;
import com.aicode.project.service.ProjectFramworkService;
import com.aicode.project.vo.ProjectFramworkPageVO;
import com.aicode.project.vo.ProjectFramworkSaveVO;
import com.aicode.project.vo.ProjectFramworkVO;
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
 * 项目应用技术
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/projectFramwork")
@Slf4j
@Api(value = "项目应用技术控制器", tags = "项目应用技术控制器")
public class ProjectFramworkController {
    @Autowired
    private ProjectFramworkService projectFramworkService;


    /**
     * 创建 项目应用技术
     *
     * @return R
     */
    @ApiOperation(value = "创建ProjectFramwork", notes = "创建ProjectFramwork")
    @PostMapping("/build")
    public ProjectFramworkSaveVO build(@ApiParam(name = "创建ProjectFramwork", value = "传入json格式", required = true)
                                   @RequestBody ProjectFramworkSaveVO projectFramworkSaveVO) {
        if (null == projectFramworkSaveVO) {
            return null;
        }
        ProjectFramwork newProjectFramwork = new ProjectFramwork();
        BeanUtils.copyProperties(projectFramworkSaveVO, newProjectFramwork);

        projectFramworkService.save(newProjectFramwork);

        projectFramworkSaveVO = new ProjectFramworkSaveVO();
        BeanUtils.copyProperties(newProjectFramwork, projectFramworkSaveVO);
        log.debug(JSON.toJSONString(projectFramworkSaveVO));
        return projectFramworkSaveVO;
    }



    /**
     * 查询项目应用技术信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询ProjectFramwork信息集合", notes = "查询ProjectFramwork信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<ProjectFramworkVO> list(@ApiIgnore ProjectFramworkPageVO projectFramworkVO, Integer curPage, Integer pageSize) {
        Page<ProjectFramwork> page = new Page<>(pageSize, curPage);
        QueryWrapper<ProjectFramwork> queryWrapper = new QueryWrapper<>();
        int total = projectFramworkService.count(queryWrapper);
        PageVO<ProjectFramworkVO> projectFramworkVOPageVO = new PageVO<>();
        if (total > 0) {
            List<ProjectFramwork> projectFramworkList = projectFramworkService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            projectFramworkVOPageVO.setTotalRow(total);
            projectFramworkVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(projectFramworkList),ProjectFramworkVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return projectFramworkVOPageVO;
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
            @ApiImplicitParam(name = "id", value = "id", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ProjectFramworkVO projectFramworkVO) {
        ProjectFramwork newProjectFramwork = new ProjectFramwork();
        BeanUtils.copyProperties(projectFramworkVO, newProjectFramwork);
        projectFramworkService.remove(new LambdaQueryWrapper<ProjectFramwork>()
                .eq(ProjectFramwork::getId, projectFramworkVO.getId()));
        return R.success("删除成功");
    }

}
