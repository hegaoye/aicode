/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.module.ctrl;

import com.aicode.module.entity.Module;
import com.aicode.module.service.ModuleService;
import com.aicode.module.vo.ModulePageVO;
import com.aicode.module.vo.ModuleSaveVO;
import com.aicode.module.vo.ModuleVO;
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
 * 第三方模块池
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/module")
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
    @PostMapping("/build")
    public ModuleSaveVO build(@ApiParam(name = "创建Module", value = "传入json格式", required = true)
                                   @RequestBody ModuleSaveVO moduleSaveVO) {
        if (null == moduleSaveVO) {
            return null;
        }
        Module newModule = new Module();
        BeanUtils.copyProperties(moduleSaveVO, newModule);

        moduleService.save(newModule);

        moduleSaveVO = new ModuleSaveVO();
        BeanUtils.copyProperties(newModule, moduleSaveVO);
        log.debug(JSON.toJSONString(moduleSaveVO));
        return moduleSaveVO;
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
    public PageVO<ModuleVO> list(@ApiIgnore ModulePageVO moduleVO, Integer curPage, Integer pageSize) {
        Page<Module> page = new Page<>(pageSize, curPage);
        QueryWrapper<Module> queryWrapper = new QueryWrapper<>();
        int total = moduleService.count(queryWrapper);
        PageVO<ModuleVO> moduleVOPageVO = new PageVO<>();
        if (total > 0) {
            List<Module> moduleList = moduleService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            moduleVOPageVO.setTotalRow(total);
            moduleVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(moduleList),ModuleVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return moduleVOPageVO;
    }


    /**
     * 修改 第三方模块池
     *
     * @return R
     */
    @ApiOperation(value = "修改Module", notes = "修改Module")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改Module", value = "传入json格式", required = true)
                          @RequestBody ModuleVO moduleVO) {
        Module newModule = new Module();
        BeanUtils.copyProperties(moduleVO, newModule);
        boolean isUpdated = moduleService.update(newModule, new LambdaQueryWrapper<Module>()
                .eq(Module::getId, moduleVO.getId()));
        return isUpdated;
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
