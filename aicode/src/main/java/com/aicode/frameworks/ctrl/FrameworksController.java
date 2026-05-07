/*
 * aicode
 */
package com.aicode.frameworks.ctrl;

import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.frameworks.entity.Frameworks;
import com.aicode.frameworks.service.FrameworksService;
import com.aicode.frameworks.vo.FrameworksPageVO;
import com.aicode.frameworks.vo.FrameworksVO;
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

import java.util.List;

/**
 * 框架技术池
 *
 * @author aicode
 */
@RestController
@RequestMapping("/framework")
@Slf4j
@Tag(name = "框架技术池控制器", description = "框架技术池控制器")
public class FrameworksController {
    @Autowired
    private FrameworksService frameworksService;

    
    @Operation(summary = "创建Frameworks", description = "创建Frameworks")
    @PostMapping("/build")
    public R build(@Parameter(hidden = true) Frameworks frameworks) {
        frameworksService.save(frameworks);
        return R.success();
    }


    
    @Operation(summary = "创建Frameworks", description = "创建Frameworks")
    @GetMapping("/load")
    public R load(String code) {
        if (code == null) {
            return null;
        }
        Frameworks frameworks = frameworksService.getOne(new LambdaQueryWrapper<Frameworks>()
                .eq(Frameworks::getCode, code));
        log.debug(JSON.toJSONString(frameworks));
        return R.success(frameworks);
    }


    
    
    @Operation(summary = "创建Frameworks", description = "创建Frameworks")
    @GetMapping("/load/code/{code}")
    public FrameworksVO loadByCode(@PathVariable String code) {
        if (code == null) {
            return null;
        }
        Frameworks frameworks = frameworksService.getOne(new LambdaQueryWrapper<Frameworks>()
                .eq(Frameworks::getCode, code));
        FrameworksVO frameworksVO = new FrameworksVO();
        BeanUtils.copyProperties(frameworks, frameworksVO);
        log.debug(JSON.toJSONString(frameworksVO));
        return frameworksVO;
    }


    
    
    @Operation(summary = "查询Frameworks信息集合", description = "查询Frameworks信息集合")
    @Parameters({
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true),
    })
    @GetMapping(value = "/list")
    public R list(Integer curPage, Integer pageSize) {
        QueryWrapper<Frameworks> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ne(Frameworks::getId, 0);
        IPage<Frameworks> page = new Page<>(curPage, pageSize);

        com.aicode.core.Page<FrameworksPageVO> pageVO = new com.aicode.core.Page<>();
        long total = frameworksService.count(queryWrapper);
        if (total > 0) {
            queryWrapper.lambda().orderByDesc(Frameworks::getId);

            IPage<Frameworks> frameworksPage = frameworksService.page(page, queryWrapper);
            List<FrameworksPageVO> frameworksPageVOList = JSON.parseArray(JSON.toJSONString(frameworksPage.getRecords()), FrameworksPageVO.class);

            pageVO.setTotalRow(total);
            pageVO.setVoList(frameworksPageVOList);
            log.debug(JSON.toJSONString(page));
        }

        return R.success(pageVO);
    }

    
    @Operation(summary = "修改Frameworks", description = "修改Frameworks")
    @RequestMapping(value = "/modify", method = {RequestMethod.PUT, RequestMethod.POST})
    public R modify(@Parameter(hidden = true) Frameworks frameworks) {
        frameworksService.update(frameworks, new LambdaQueryWrapper<Frameworks>()
                .eq(Frameworks::getCode, frameworks.getCode()));
        return R.success(frameworks);
    }

    
    @Operation(summary = "修改状态", description = "修改状态")
    @Parameters({
            @Parameter(name = "code", description = "模板编码", required = true),
            @Parameter(name = "isPublic", description = "状态：停用[N]，启用[Y]")
    })
    @RequestMapping(value = "/updateIsPublic", method = {RequestMethod.PUT, RequestMethod.POST})
    public R updateIsPublic(@Parameter(hidden = true) Frameworks frameworks) {
        Assert.hasText(frameworks.getCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(frameworks.getIsPublic(), BaseException.BaseExceptionEnum.Empty_Param.toString());

        frameworksService.update(frameworks, new LambdaQueryWrapper<Frameworks>()
                .eq(Frameworks::getCode, frameworks.getCode()));
        return R.success(frameworks);
    }



    
    @Operation(summary = "删除Frameworks", description = "删除Frameworks")
    @Parameters({
            @Parameter(name = "code", description = "技术编码")
    })
    @PostMapping("/delete")
    public R delete(@Parameter(hidden = true) FrameworksVO frameworksVO) {
        Frameworks newFrameworks = new Frameworks();
        BeanUtils.copyProperties(frameworksVO, newFrameworks);
        frameworksService.remove(new LambdaQueryWrapper<Frameworks>()
                .eq(Frameworks::getCode, frameworksVO.getCode()));
        return R.success("删除成功");
    }

}
