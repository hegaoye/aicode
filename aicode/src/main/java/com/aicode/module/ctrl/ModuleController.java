/*
 * aicode
 */
package com.aicode.module.ctrl;

import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.module.entity.Module;
import com.aicode.module.service.ModuleService;
import com.aicode.module.vo.ModuleVO;
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
 * 第三方模块池
 *
 * @author aicode
 */
@Slf4j
@RestController
@RequestMapping("/module")
@Tag(name = "第三方模块池控制器", description = "第三方模块池控制器")
public class ModuleController {
    @Autowired
    private ModuleService moduleService;

    @Operation(summary = "创建Module", description = "创建Module")
    @Parameters({
            @Parameter(name = "name", description = "模块名", required = true),
            @Parameter(name = "description", description = "模块说明", required = true)
    })
    @PostMapping("/build")
    public R build(@Parameter(hidden = true) Module module) {
        moduleService.save(module);
        return R.success(module);
    }


    @Operation(summary = "查询一个详情信息", description = "查询一个详情信息")
    @Parameters({
            @Parameter(name = "code", description = "模块编码", required = true)
    })
    @GetMapping(value = "/load")

    public R load(String code) {
        Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());

        Module module = moduleService.getOne(new LambdaQueryWrapper<Module>()
                .eq(Module::getCode, code));

        log.info(JSON.toJSONString(module));
        return R.success(module);

    }


    @Operation(summary = "创建Module", description = "创建Module")
    @GetMapping("/load/code/{code}")
    public ModuleVO loadByCode(@PathVariable String code) {
        if (code == null) {
            return null;
        }
        Module module = moduleService.getOne(new LambdaQueryWrapper<Module>()
                .eq(Module::getCode, code));
        ModuleVO moduleVO = new ModuleVO();
        BeanUtils.copyProperties(module, moduleVO);
        log.debug(JSON.toJSONString(moduleVO));
        return moduleVO;
    }


    @Operation(summary = "查询Module信息集合", description = "查询Module信息集合")
    @Parameters({
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true),
    })
    @GetMapping(value = "/list")
    public R list(Integer curPage, Integer pageSize) {
        IPage<Module> page = new Page<>(curPage, pageSize);

        QueryWrapper<Module> queryWrapper = new QueryWrapper<>();
        long total = moduleService.count(queryWrapper);

        com.aicode.core.Page<Module> pageVO = new com.aicode.core.Page<>(pageSize, curPage);

        if (total > 0) {
            queryWrapper.lambda().orderByDesc(Module::getId);

            IPage<Module> modulePage = moduleService.page(page, queryWrapper);

            pageVO.setTotalRow(total);
            pageVO.setVoList(modulePage.getRecords());
            log.debug(JSON.toJSONString(page));
        }
        return R.success(pageVO);
    }


    @Operation(summary = "修改Module", description = "修改Module")
    @Parameters({
            @Parameter(name = "code", description = "模板编码", required = true),
            @Parameter(name = "name", description = "类型名"),
            @Parameter(name = "description", description = "类型说明"),
            @Parameter(name = "state", description = "状态：停用[Disenable]，启用[Enable]")
    })
    @PutMapping("/modify")
    public R modify(@Parameter(hidden = true) Module module) {
        moduleService.update(module, new LambdaQueryWrapper<Module>()
                .eq(Module::getCode, module.getCode()));
        return R.success(module);
    }

    @Operation(summary = "删除Module", description = "删除Module")
    @Parameters({
            @Parameter(name = "id", description = "id"),
            @Parameter(name = "code", description = "模块编码")
    })
    @DeleteMapping("/delete")
    public R delete(@Parameter(hidden = true) ModuleVO moduleVO) {
        Module newModule = new Module();
        BeanUtils.copyProperties(moduleVO, newModule);
        moduleService.remove(new LambdaQueryWrapper<Module>()
                .eq(Module::getId, moduleVO.getId()));
        return R.success("删除成功");
    }

}
