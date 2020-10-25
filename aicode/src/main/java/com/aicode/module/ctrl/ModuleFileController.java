/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.module.ctrl;

import com.aicode.module.entity.ModuleFile;
import com.aicode.module.service.ModuleFileService;
import com.aicode.module.vo.ModuleFilePageVO;
import com.aicode.module.vo.ModuleFileSaveVO;
import com.aicode.module.vo.ModuleFileVO;
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
 * 模块文件信息
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/moduleFile")
@Slf4j
@Api(value = "模块文件信息控制器", tags = "模块文件信息控制器")
public class ModuleFileController {
    @Autowired
    private ModuleFileService moduleFileService;


    /**
     * 创建 模块文件信息
     *
     * @return R
     */
    @ApiOperation(value = "创建ModuleFile", notes = "创建ModuleFile")
    @PostMapping("/build")
    public ModuleFileSaveVO build(@ApiParam(name = "创建ModuleFile", value = "传入json格式", required = true)
                                   @RequestBody ModuleFileSaveVO moduleFileSaveVO) {
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



    /**
     * 查询模块文件信息信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询ModuleFile信息集合", notes = "查询ModuleFile信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<ModuleFileVO> list(@ApiIgnore ModuleFilePageVO moduleFileVO, Integer curPage, Integer pageSize) {
        Page<ModuleFile> page = new Page<>(pageSize, curPage);
        QueryWrapper<ModuleFile> queryWrapper = new QueryWrapper<>();
        int total = moduleFileService.count(queryWrapper);
        PageVO<ModuleFileVO> moduleFileVOPageVO = new PageVO<>();
        if (total > 0) {
            List<ModuleFile> moduleFileList = moduleFileService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            moduleFileVOPageVO.setTotalRow(total);
            moduleFileVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(moduleFileList),ModuleFileVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return moduleFileVOPageVO;
    }


    /**
     * 修改 模块文件信息
     *
     * @return R
     */
    @ApiOperation(value = "修改ModuleFile", notes = "修改ModuleFile")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改ModuleFile", value = "传入json格式", required = true)
                          @RequestBody ModuleFileVO moduleFileVO) {
        ModuleFile newModuleFile = new ModuleFile();
        BeanUtils.copyProperties(moduleFileVO, newModuleFile);
        boolean isUpdated = moduleFileService.update(newModuleFile, new LambdaQueryWrapper<ModuleFile>()
                .eq(ModuleFile::getId, moduleFileVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 模块文件信息
     *
     * @return R
     */
    @ApiOperation(value = "删除ModuleFile", notes = "删除ModuleFile")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ModuleFileVO moduleFileVO) {
        ModuleFile newModuleFile = new ModuleFile();
        BeanUtils.copyProperties(moduleFileVO, newModuleFile);
        moduleFileService.remove(new LambdaQueryWrapper<ModuleFile>()
                .eq(ModuleFile::getId, moduleFileVO.getId()));
        return R.success("删除成功");
    }

}
