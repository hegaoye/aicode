/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.module.ctrl;

import com.aicode.core.entity.Page;
import com.aicode.core.entity.R;
import com.aicode.core.exceptions.BaseException;
import com.aicode.module.entity.Module;
import com.aicode.module.service.ModuleService;
import com.aicode.module.vo.ModulePageVO;
import com.aicode.module.vo.ModuleVO;
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
 * 第三方模块池
 * 1.查询一个详情信息
 * 2.查询信息集合
 * 3.添加
 * 4.修改
 * 5.删除
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/Module")
@Slf4j
@Api(value = "第三方模块池控制器", tags = "第三方模块池控制器")
public class ModuleController {
    @Autowired
    private ModuleService moduleService;


    /**
     * 创建 第三方模块池
     *
     * @return R
     */
    @ApiOperation(value = "创建Module", notes = "创建Module")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "模块名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "description", value = "模块说明", required = true, paramType = "query")
    })
    @PostMapping("/build")
    public R build(@ApiIgnore Module module) {
        moduleService.save(module);
        return R.success(module);
    }

    /**
     * 查询一个详情信息
     *
     * @param code 模板编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "模块编码", required = true, paramType = "query")
    })
    @GetMapping(value = "/load")

    public R load(String code) {
        Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());

        Module module = moduleService.getOne(new LambdaQueryWrapper<Module>()
                .eq(Module::getCode, code));

        log.info(JSON.toJSONString(module));
        return R.success(module);

    }

    /**
     * 根据条件code查询第三方模块池一个详情信息
     *
     * @param code 模块编码
     * @return ModuleVO
     */
    @ApiOperation(value = "创建Module", notes = "创建Module")
    @GetMapping("/load/code/{code}")
    public ModuleVO loadByCode(@PathVariable java.lang.String code) {
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

    /**
     * 查询第三方模块池信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询Module信息集合", notes = "查询Module信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public R list(@ApiIgnore ModulePageVO moduleVO, Integer curPage, Integer pageSize) {
        Page<Module> page = new Page<>(pageSize, curPage);
        QueryWrapper<Module> queryWrapper = new QueryWrapper<>();
        int total = moduleService.count(queryWrapper);
        if (total > 0) {
            List<Module> moduleList = moduleService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            page.setTotalRow(total);
            page.setRecords(moduleList);
            log.debug(JSON.toJSONString(page));
        }
        return R.success(page);
    }


    /**
     * 修改 第三方模块池
     *
     * @return R
     */
    @ApiOperation(value = "修改Module", notes = "修改Module")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "模板编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "name", value = "类型名", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "类型说明", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态：停用[Disenable]，启用[Enable]", paramType = "query")
    })
    @PutMapping("/modify")
    public R modify(@ApiIgnore Module module) {
        moduleService.update(module, new LambdaQueryWrapper<Module>()
                .eq(Module::getCode, module.getCode()));
        return R.success(module);
    }


    /**
     * 删除 第三方模块池
     *
     * @return R
     */
    @ApiOperation(value = "删除Module", notes = "删除Module")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "模块编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ModuleVO moduleVO) {
        Module newModule = new Module();
        BeanUtils.copyProperties(moduleVO, newModule);
        moduleService.remove(new LambdaQueryWrapper<Module>()
                .eq(Module::getId, moduleVO.getId()));
        return R.success("删除成功");
    }

}
