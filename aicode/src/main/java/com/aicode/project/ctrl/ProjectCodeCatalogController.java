/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.project.entity.ProjectCodeCatalog;
import com.aicode.project.service.ProjectCodeCatalogService;
import com.aicode.project.vo.ProjectCodeCatalogPageVO;
import com.aicode.project.vo.ProjectCodeCatalogSaveVO;
import com.aicode.project.vo.ProjectCodeCatalogVO;
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
 * 生成源码资料
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/projectCodeCatalog")
@Slf4j
@Api(value = "生成源码资料控制器", tags = "生成源码资料控制器")
public class ProjectCodeCatalogController {
    @Autowired
    private ProjectCodeCatalogService projectCodeCatalogService;


    /**
     * 创建 生成源码资料
     *
     * @return R
     */
    @ApiOperation(value = "创建ProjectCodeCatalog", notes = "创建ProjectCodeCatalog")
    @PostMapping("/build")
    public ProjectCodeCatalogSaveVO build(@ApiParam(name = "创建ProjectCodeCatalog", value = "传入json格式", required = true)
                                   @RequestBody ProjectCodeCatalogSaveVO projectCodeCatalogSaveVO) {
        if (null == projectCodeCatalogSaveVO) {
            return null;
        }
        ProjectCodeCatalog newProjectCodeCatalog = new ProjectCodeCatalog();
        BeanUtils.copyProperties(projectCodeCatalogSaveVO, newProjectCodeCatalog);

        projectCodeCatalogService.save(newProjectCodeCatalog);

        projectCodeCatalogSaveVO = new ProjectCodeCatalogSaveVO();
        BeanUtils.copyProperties(newProjectCodeCatalog, projectCodeCatalogSaveVO);
        log.debug(JSON.toJSONString(projectCodeCatalogSaveVO));
        return projectCodeCatalogSaveVO;
    }


    /**
     * 根据条件code查询生成源码资料一个详情信息
     *
     * @param code 编码
     * @return ProjectCodeCatalogVO
     */
    @ApiOperation(value = "创建ProjectCodeCatalog", notes = "创建ProjectCodeCatalog")
    @GetMapping("/load/code/{code}")
    public ProjectCodeCatalogVO loadByCode(@PathVariable java.lang.String code) {
        if (code == null) {
            return null;
        }
        ProjectCodeCatalog projectCodeCatalog = projectCodeCatalogService.getOne(new LambdaQueryWrapper<ProjectCodeCatalog>()
                .eq(ProjectCodeCatalog::getCode, code));
        ProjectCodeCatalogVO projectCodeCatalogVO = new ProjectCodeCatalogVO();
        BeanUtils.copyProperties(projectCodeCatalog, projectCodeCatalogVO);
        log.debug(JSON.toJSONString(projectCodeCatalogVO));
        return projectCodeCatalogVO;
    }
    /**
     * 根据条件projectCode查询生成源码资料一个详情信息
     *
     * @param projectCode 项目编码
     * @return ProjectCodeCatalogVO
     */
    @ApiOperation(value = "创建ProjectCodeCatalog", notes = "创建ProjectCodeCatalog")
    @GetMapping("/load/projectCode/{projectCode}")
    public ProjectCodeCatalogVO loadByProjectCode(@PathVariable java.lang.String projectCode) {
        if (projectCode == null) {
            return null;
        }
        ProjectCodeCatalog projectCodeCatalog = projectCodeCatalogService.getOne(new LambdaQueryWrapper<ProjectCodeCatalog>()
                .eq(ProjectCodeCatalog::getProjectCode, projectCode));
        ProjectCodeCatalogVO projectCodeCatalogVO = new ProjectCodeCatalogVO();
        BeanUtils.copyProperties(projectCodeCatalog, projectCodeCatalogVO);
        log.debug(JSON.toJSONString(projectCodeCatalogVO));
        return projectCodeCatalogVO;
    }

    /**
     * 查询生成源码资料信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询ProjectCodeCatalog信息集合", notes = "查询ProjectCodeCatalog信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<ProjectCodeCatalogVO> list(@ApiIgnore ProjectCodeCatalogPageVO projectCodeCatalogVO, Integer curPage, Integer pageSize) {
        Page<ProjectCodeCatalog> page = new Page<>(pageSize, curPage);
        QueryWrapper<ProjectCodeCatalog> queryWrapper = new QueryWrapper<>();
        int total = projectCodeCatalogService.count(queryWrapper);
        PageVO<ProjectCodeCatalogVO> projectCodeCatalogVOPageVO = new PageVO<>();
        if (total > 0) {
            List<ProjectCodeCatalog> projectCodeCatalogList = projectCodeCatalogService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            projectCodeCatalogVOPageVO.setTotalRow(total);
            projectCodeCatalogVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(projectCodeCatalogList),ProjectCodeCatalogVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return projectCodeCatalogVOPageVO;
    }


    /**
     * 修改 生成源码资料
     *
     * @return R
     */
    @ApiOperation(value = "修改ProjectCodeCatalog", notes = "修改ProjectCodeCatalog")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改ProjectCodeCatalog", value = "传入json格式", required = true)
                          @RequestBody ProjectCodeCatalogVO projectCodeCatalogVO) {
        ProjectCodeCatalog newProjectCodeCatalog = new ProjectCodeCatalog();
        BeanUtils.copyProperties(projectCodeCatalogVO, newProjectCodeCatalog);
        boolean isUpdated = projectCodeCatalogService.update(newProjectCodeCatalog, new LambdaQueryWrapper<ProjectCodeCatalog>()
                .eq(ProjectCodeCatalog::getId, projectCodeCatalogVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 生成源码资料
     *
     * @return R
     */
    @ApiOperation(value = "删除ProjectCodeCatalog", notes = "删除ProjectCodeCatalog")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "编码", paramType = "query"),
            @ApiImplicitParam(name = "projectCode", value = "项目编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ProjectCodeCatalogVO projectCodeCatalogVO) {
        ProjectCodeCatalog newProjectCodeCatalog = new ProjectCodeCatalog();
        BeanUtils.copyProperties(projectCodeCatalogVO, newProjectCodeCatalog);
        projectCodeCatalogService.remove(new LambdaQueryWrapper<ProjectCodeCatalog>()
                .eq(ProjectCodeCatalog::getId, projectCodeCatalogVO.getId()));
        return R.success("删除成功");
    }

}
