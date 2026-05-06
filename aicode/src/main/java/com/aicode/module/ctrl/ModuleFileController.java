/*
 * aicode
 */
package com.aicode.module.ctrl;

import com.aicode.core.PageVO;
import com.aicode.core.R;
import com.aicode.module.entity.ModuleFile;
import com.aicode.module.service.ModuleFileService;
import com.aicode.module.vo.ModuleFilePageVO;
import com.aicode.module.vo.ModuleFileSaveVO;
import com.aicode.module.vo.ModuleFileVO;
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
 * 模块文件信息
 *
 * @author aicode
 */
@Slf4j
@RestController
@RequestMapping("/moduleFile")
@Tag(name = "模块文件信息控制器", description = "模块文件信息控制器")
public class ModuleFileController {
    @Autowired
    private ModuleFileService moduleFileService;

    @Operation(summary = "创建ModuleFile", description = "创建ModuleFile")
    @PostMapping("/build")
    public ModuleFileSaveVO build(@RequestBody ModuleFileSaveVO moduleFileSaveVO) {
        if (null == moduleFileSaveVO) {
            return null;
        }
        ModuleFile newModuleFile = new ModuleFile();
        BeanUtils.copyProperties(moduleFileSaveVO, newModuleFile);

        moduleFileService.save(newModuleFile);

        moduleFileSaveVO = new ModuleFileSaveVO();
        BeanUtils.copyProperties(newModuleFile, moduleFileSaveVO);
        log.debug(JSON.toJSONString(moduleFileSaveVO));
        return moduleFileSaveVO;
    }


    @Operation(summary = "查询ModuleFile信息集合", description = "查询ModuleFile信息集合")
    @Parameters({
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true),
    })
    @GetMapping(value = "/list")
    public PageVO<ModuleFileVO> list(@Parameter(hidden = true) ModuleFilePageVO moduleFileVO, Integer curPage, Integer pageSize) {
        IPage<ModuleFile> page = new Page<>(curPage, pageSize);
        QueryWrapper<ModuleFile> queryWrapper = new QueryWrapper<>();
        long total = moduleFileService.count(queryWrapper);
        PageVO<ModuleFileVO> moduleFileVOPageVO = new PageVO<>();
        if (total > 0) {
            queryWrapper.lambda().orderByDesc(ModuleFile::getId);

            IPage<ModuleFile> moduleFilePage = moduleFileService.page(page, queryWrapper);
            List<ModuleFileVO> moduleFilePageVOList = JSON.parseArray(JSON.toJSONString(moduleFilePage.getRecords()), ModuleFileVO.class);


            moduleFileVOPageVO.setTotalRow(total);
            moduleFileVOPageVO.setRecords(moduleFilePageVOList);
            log.debug(JSON.toJSONString(page));
        }

        return moduleFileVOPageVO;
    }


    @Operation(summary = "修改ModuleFile", description = "修改ModuleFile")
    @PutMapping("/modify")
    public boolean modify(@RequestBody ModuleFileVO moduleFileVO) {
        ModuleFile newModuleFile = new ModuleFile();
        BeanUtils.copyProperties(moduleFileVO, newModuleFile);
        boolean isUpdated = moduleFileService.update(newModuleFile, new LambdaQueryWrapper<ModuleFile>()
                .eq(ModuleFile::getId, moduleFileVO.getId()));
        return isUpdated;
    }


    @Operation(summary = "删除ModuleFile", description = "删除ModuleFile")
    @Parameters({
            @Parameter(name = "id", description = "id")
    })
    @DeleteMapping("/delete")
    public R delete(@Parameter(hidden = true) ModuleFileVO moduleFileVO) {
        ModuleFile newModuleFile = new ModuleFile();
        BeanUtils.copyProperties(moduleFileVO, newModuleFile);
        moduleFileService.remove(new LambdaQueryWrapper<ModuleFile>()
                .eq(ModuleFile::getId, moduleFileVO.getId()));
        return R.success("删除成功");
    }

}
