/*
 * aicode
 */
package com.aicode.frameworks.ctrl;

import com.aicode.core.PageVO;
import com.aicode.core.R;
import com.aicode.frameworks.entity.FrameworksTemplate;
import com.aicode.frameworks.service.FrameworksTemplateService;
import com.aicode.frameworks.vo.FrameworksTemplateSaveVO;
import com.aicode.frameworks.vo.FrameworksTemplateVO;
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
 * 框架配置文件模板
 *
 * @author aicode
 */
@RestController
@RequestMapping("/frameworksTemplate")
@Slf4j
@Tag(name = "框架配置文件模板控制器", description = "框架配置文件模板控制器")
public class FrameworksTemplateController {
    @Autowired
    private FrameworksTemplateService frameworksTemplateService;


    
    @Deprecated
    @Operation(summary = "创建FrameworksTemplate", description = "创建FrameworksTemplate")
    @PostMapping("/build")
    public FrameworksTemplateSaveVO build(@RequestBody FrameworksTemplateSaveVO frameworksTemplateSaveVO) {
        if (null == frameworksTemplateSaveVO) {
            return null;
        }
        FrameworksTemplate newFrameworksTemplate = new FrameworksTemplate();
        BeanUtils.copyProperties(frameworksTemplateSaveVO, newFrameworksTemplate);

        frameworksTemplateService.save(newFrameworksTemplate);

        frameworksTemplateSaveVO = new FrameworksTemplateSaveVO();
        BeanUtils.copyProperties(newFrameworksTemplate, frameworksTemplateSaveVO);
        log.debug(JSON.toJSONString(frameworksTemplateSaveVO));
        return frameworksTemplateSaveVO;
    }


    
    @Deprecated
    @Operation(summary = "查询FrameworksTemplate信息集合", description = "查询FrameworksTemplate信息集合")
    @Parameters({
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true),
    })
    @GetMapping(value = "/list")
    public PageVO<FrameworksTemplateVO> list(Integer curPage, Integer pageSize) {
        IPage<FrameworksTemplate> page = new Page<>(curPage, pageSize);

        QueryWrapper<FrameworksTemplate> queryWrapper = new QueryWrapper<>();
        long total = frameworksTemplateService.count(queryWrapper);

        PageVO<FrameworksTemplateVO> frameworksTemplateVOPageVO = new PageVO<>();
        if (total > 0) {
            queryWrapper.lambda().orderByDesc(FrameworksTemplate::getId);

            IPage<FrameworksTemplate> frameworksTemplatePage = frameworksTemplateService.page(page, queryWrapper);
            List<FrameworksTemplateVO> frameworksTemplatePageVOList = JSON.parseArray(JSON.toJSONString(frameworksTemplatePage.getRecords()), FrameworksTemplateVO.class);

            frameworksTemplateVOPageVO.setTotalRow(total);
            frameworksTemplateVOPageVO.setRecords(frameworksTemplatePageVOList);
            log.debug(JSON.toJSONString(page));
        }

        return frameworksTemplateVOPageVO;
    }


    
    @Deprecated
    @Operation(summary = "修改FrameworksTemplate", description = "修改FrameworksTemplate")
    @PutMapping("/modify")
    public boolean modify(@RequestBody FrameworksTemplateVO frameworksTemplateVO) {
        FrameworksTemplate newFrameworksTemplate = new FrameworksTemplate();
        BeanUtils.copyProperties(frameworksTemplateVO, newFrameworksTemplate);
        boolean isUpdated = frameworksTemplateService.update(newFrameworksTemplate, new LambdaQueryWrapper<FrameworksTemplate>()
                .eq(FrameworksTemplate::getId, frameworksTemplateVO.getId()));
        return isUpdated;
    }


    
    @Deprecated
    @Operation(summary = "删除FrameworksTemplate", description = "删除FrameworksTemplate")
    @Parameters({
            @Parameter(name = "id", description = "")
    })
    @DeleteMapping("/delete")
    public R delete(@Parameter(hidden = true) FrameworksTemplateVO frameworksTemplateVO) {
        FrameworksTemplate newFrameworksTemplate = new FrameworksTemplate();
        BeanUtils.copyProperties(frameworksTemplateVO, newFrameworksTemplate);
        frameworksTemplateService.remove(new LambdaQueryWrapper<FrameworksTemplate>()
                .eq(FrameworksTemplate::getId, frameworksTemplateVO.getId()));
        return R.success("删除成功");
    }

}
