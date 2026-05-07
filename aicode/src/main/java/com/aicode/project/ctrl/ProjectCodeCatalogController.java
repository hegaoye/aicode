/*
 * aicode
 */
package com.aicode.project.ctrl;

import com.aicode.core.PageVO;
import com.aicode.core.R;
import com.aicode.project.entity.ProjectCodeCatalog;
import com.aicode.project.service.ProjectCodeCatalogService;
import com.aicode.project.vo.ProjectCodeCatalogSaveVO;
import com.aicode.project.vo.ProjectCodeCatalogVO;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 生成源码资料
 *
 * @author aicode
 */
@RestController
@RequestMapping("/projectCodeCatalog")
@Slf4j
@Tag(name = "生成源码资料控制器", description = "生成源码资料控制器")
public class ProjectCodeCatalogController {
    @Autowired
    private ProjectCodeCatalogService projectCodeCatalogService;


    @Operation(summary = "创建ProjectCodeCatalog", description = "创建ProjectCodeCatalog")
    @PostMapping("/build")
    public ProjectCodeCatalogSaveVO build(@RequestBody ProjectCodeCatalogSaveVO projectCodeCatalogSaveVO) {
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



    @Operation(summary = "查询ProjectCodeCatalog信息集合", description = "查询ProjectCodeCatalog信息集合")
    @Parameters({
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true),
    })
    @GetMapping(value = "/list")
    public PageVO<ProjectCodeCatalogVO> list(Integer curPage, Integer pageSize) {
        IPage<ProjectCodeCatalog> page = new Page<>(curPage, pageSize);
        QueryWrapper<ProjectCodeCatalog> queryWrapper = new QueryWrapper<>();
        long total = projectCodeCatalogService.count(queryWrapper);
        PageVO<ProjectCodeCatalogVO> projectCodeCatalogVOPageVO = new PageVO<>();
        if (total > 0) {
            queryWrapper.lambda().orderByDesc(ProjectCodeCatalog::getId);

            IPage<ProjectCodeCatalog> projectCodeCatalogPage = projectCodeCatalogService.page(page, queryWrapper);
            List<ProjectCodeCatalogVO> projectCodeCatalogPageVOList = JSON.parseArray(JSON.toJSONString(projectCodeCatalogPage.getRecords()), ProjectCodeCatalogVO.class);


            projectCodeCatalogVOPageVO.setTotalRow(total);
            projectCodeCatalogVOPageVO.setRecords(projectCodeCatalogPageVOList);
            log.debug(JSON.toJSONString(page));
        }
        return projectCodeCatalogVOPageVO;
    }


    @Operation(summary = "修改ProjectCodeCatalog", description = "修改ProjectCodeCatalog")
    @PutMapping("/modify")
    public boolean modify(@RequestBody ProjectCodeCatalogVO projectCodeCatalogVO) {
        ProjectCodeCatalog newProjectCodeCatalog = new ProjectCodeCatalog();
        BeanUtils.copyProperties(projectCodeCatalogVO, newProjectCodeCatalog);
        boolean isUpdated = projectCodeCatalogService.update(newProjectCodeCatalog, new LambdaQueryWrapper<ProjectCodeCatalog>()
                .eq(ProjectCodeCatalog::getId, projectCodeCatalogVO.getId()));
        return isUpdated;
    }


    @Operation(summary = "删除ProjectCodeCatalog", description = "删除ProjectCodeCatalog")
    @Parameters({
            @Parameter(name = "id", description = ""),
            @Parameter(name = "code", description = "编码"),
            @Parameter(name = "projectCode", description = "项目编码")
    })
    @DeleteMapping("/delete")
    public R delete(@Parameter(hidden = true) ProjectCodeCatalogVO projectCodeCatalogVO) {
        ProjectCodeCatalog newProjectCodeCatalog = new ProjectCodeCatalog();
        BeanUtils.copyProperties(projectCodeCatalogVO, newProjectCodeCatalog);
        projectCodeCatalogService.remove(new LambdaQueryWrapper<ProjectCodeCatalog>()
                .eq(ProjectCodeCatalog::getId, projectCodeCatalogVO.getId()));
        return R.success("删除成功");
    }


}
