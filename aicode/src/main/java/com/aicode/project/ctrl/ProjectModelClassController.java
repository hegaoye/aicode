/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.core.entity.Page;
import com.aicode.core.entity.R;
import com.aicode.project.entity.ProjectModelClass;
import com.aicode.project.service.ProjectModelClassService;
import com.aicode.project.vo.ProjectModelClassPageVO;
import com.aicode.project.vo.ProjectModelClassVO;
import com.alibaba.druid.util.StringUtils;
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
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 模块下的类
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/projectModelClass")
@Slf4j
@Api(value = "模块下的类控制器", tags = "模块下的类控制器")
public class ProjectModelClassController {
    @Autowired
    private ProjectModelClassService projectModelClassService;


    /**
     * 查询模块下的类详情信息
     *
     * @param id
     * @return BeanRet
     */
    @ApiOperation(value = "查询模块下的类详情信息", notes = "查询模块下的类详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query")
    })
    @GetMapping(value = "/loadById/{id}")
    @ResponseBody
    public R loadById(@PathVariable Long id) {
        if (id == null) {
            return R.failed("");
        }

        ProjectModelClass projectModelClass = projectModelClassService.getById(id);
        log.info(JSON.toJSONString(projectModelClass));
        return R.success(projectModelClass);
    }

    /**
     * 创建 模块下的类
     *
     * @return R
     */
    @ApiOperation(value = "创建ProjectModelClass", notes = "创建ProjectModelClass")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query", required = true),
            @ApiImplicitParam(name = "mapClassTableCode", value = "类编码", paramType = "query", required = true),
            @ApiImplicitParam(name = "projectModelCode", value = "模块编码", paramType = "query", required = true)
    })
    @PostMapping({"/build", "save"})
    public ProjectModelClassVO build(@ApiIgnore ProjectModelClass projectModelClass) {
        projectModelClassService.save(projectModelClass);
        log.debug(JSON.toJSONString(projectModelClass));
        return JSON.parseObject(JSON.toJSONString(projectModelClass), ProjectModelClassVO.class);
    }


    /**
     * 查询模块下的类详情信息
     *
     * @param mapClassTableCode 类编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询模块下的类详情信息", notes = "查询模块下的类详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapClassTableCode", value = "类编码", paramType = "query")
    })
    @GetMapping(value = "/loadByMapClassTableCode")
    @ResponseBody
    public R loadByMapClassTableCode(@PathVariable String mapClassTableCode) {
        if (StringUtils.isEmpty(mapClassTableCode)) {
            return R.failed("");
        }
        ProjectModelClass projectModelClass = projectModelClassService.getOne(new LambdaQueryWrapper<ProjectModelClass>()
                .eq(ProjectModelClass::getMapClassTableCode, mapClassTableCode));
        log.info(JSON.toJSONString(projectModelClass));
        return R.success(projectModelClass);
    }


    /**
     * 查询模块下的类详情信息
     *
     * @param projectModelCode 模块编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询模块下的类详情信息", notes = "查询模块下的类详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectModelCode", value = "模块编码", paramType = "query")
    })
    @GetMapping(value = "/loadByProjectModelCode")
    @ResponseBody
    public R loadByProjectModelCode(@PathVariable String projectModelCode) {
        if (StringUtils.isEmpty(projectModelCode)) {
            return R.failed("");
        }

        ProjectModelClass projectModelClass = projectModelClassService.getOne(new LambdaQueryWrapper<ProjectModelClass>()
                .eq(ProjectModelClass::getProjectModelCode, projectModelCode));
        log.info(JSON.toJSONString(projectModelClass));
        return R.success(projectModelClass);
    }

    /**
     * 查询模块下的类信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询ProjectModelClass信息集合", notes = "查询ProjectModelClass信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "mapClassTableCode", value = "类编码", paramType = "query"),
            @ApiImplicitParam(name = "projectModelCode", value = "模块编码", paramType = "query")
    })
    @GetMapping(value = "/list")
    public R list(@ApiIgnore ProjectModelClassPageVO projectModelClassVO, Integer curPage, Integer pageSize) {
        Page<ProjectModelClass> page = new Page<>(pageSize, curPage);
        QueryWrapper<ProjectModelClass> queryWrapper = new QueryWrapper<>();
        if (projectModelClassVO.getId() > 0) {
            queryWrapper.lambda().eq(ProjectModelClass::getId, projectModelClassVO.getId());
        }

        if (!StringUtils.isEmpty(projectModelClassVO.getMapClassTableCode())) {
            queryWrapper.lambda().eq(ProjectModelClass::getMapClassTableCode, projectModelClassVO.getMapClassTableCode());
        }

        if (!StringUtils.isEmpty(projectModelClassVO.getProjectModelCode())) {
            queryWrapper.lambda().eq(ProjectModelClass::getProjectModelCode, projectModelClassVO.getProjectModelCode());
        }

        int total = projectModelClassService.count(queryWrapper);
        if (total > 0) {
            List<ProjectModelClass> projectModelClassList = projectModelClassService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            page.setTotalRow(total);
            page.setRecords(projectModelClassList);
            log.debug(JSON.toJSONString(page));
        }
        return R.success(page);
    }


    /**
     * 修改 模块下的类
     *
     * @return R
     */
    @ApiOperation(value = "修改ProjectModelClass", notes = "修改ProjectModelClass")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "mapClassTableCode", value = "类编码", paramType = "query"),
            @ApiImplicitParam(name = "projectModelCode", value = "模块编码", paramType = "query")
    })
    @PutMapping("/modify")
    public R modify(@ApiIgnore ProjectModelClass projectModelClass) {
        projectModelClassService.updateById(projectModelClass);
        return R.success();
    }


    /**
     * 删除 模块下的类
     *
     * @return R
     */
    @ApiOperation(value = "删除ProjectModelClass", notes = "删除ProjectModelClass")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "mapClassTableCode", value = "类编码", paramType = "query"),
            @ApiImplicitParam(name = "projectModelCode", value = "模块编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(Long id, String mapClassTableCode, String projectModelCode) {
        if (id == null) {
            return R.failed("");
        }
        if (mapClassTableCode == null) {
            return R.failed("");
        }
        if (projectModelCode == null) {
            return R.failed("");
        }
        projectModelClassService.remove(new LambdaQueryWrapper<ProjectModelClass>()
                .eq(ProjectModelClass::getId, id)
                .eq(ProjectModelClass::getProjectModelCode, projectModelCode)
                .eq(ProjectModelClass::getMapClassTableCode, mapClassTableCode));
        return R.success("删除成功");
    }

}
