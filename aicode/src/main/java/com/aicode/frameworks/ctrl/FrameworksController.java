/*
 * aicode
 */
package com.aicode.frameworks.ctrl;

import com.aicode.core.BaseException;
import com.aicode.core.PageVO;
import com.aicode.core.R;
import com.aicode.frameworks.entity.Frameworks;
import com.aicode.frameworks.service.FrameworksService;
import com.aicode.frameworks.vo.FrameworkSummaryVO;
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
    public R<Void> build(@Parameter(hidden = true) Frameworks frameworks) {
        frameworksService.save(frameworks);
        return R.success();
    }


    @Operation(summary = "按 code 加载框架技术", description = "按 code 加载框架技术（不含 Git 口令）")
    @Parameters({
            @Parameter(name = "code", description = "技术编码", required = true)
    })
    @GetMapping("/load")
    public R<FrameworkSummaryVO> load(String code) {
        // M5-A1：出参由 R<Frameworks>（实体直出，泄漏 password）改为 R<FrameworkSummaryVO>。
        // 详见 openspec/changes/archive/2026-10-18-framework-list-vo-contract/。
        if (code == null || code.isEmpty()) {
            // 用 R.success() 无参版本：避免 R.success(null) 走 R.success(BaseExceptionEnum)
            // 重载导致 NPE（common R.java:61 —— 见 M3-A-1 记录）。期望 data: null，code: "0000"。
            return R.success();
        }
        Frameworks frameworks = frameworksService.getOne(new LambdaQueryWrapper<Frameworks>()
                .eq(Frameworks::getCode, code));
        if (frameworks == null) {
            // 找不到：返 R.success()（data=null）而非走 NPE→9999 路径
            return R.success();
        }
        FrameworkSummaryVO summary = toSummary(frameworks);
        log.debug(JSON.toJSONString(summary));
        return R.success(summary);
    }


    /**
     * 把持久化实体 {@link Frameworks} 转换为出参视图对象 {@link FrameworkSummaryVO}。
     *
     * <p>逐字段显式赋值而非 {@link BeanUtils#copyProperties} —— 显式胜过隐式：
     * 实体新增字段时不会静默进入响应（这正是本变更要堵的漏洞类别）。</p>
     */
    private FrameworkSummaryVO toSummary(Frameworks frameworks) {
        FrameworkSummaryVO vo = new FrameworkSummaryVO();
        vo.setId(frameworks.getId());
        vo.setCode(frameworks.getCode());
        vo.setName(frameworks.getName());
        vo.setDescription(frameworks.getDescription());
        vo.setGitHome(frameworks.getGitHome());
        vo.setAccount(frameworks.getAccount());
        vo.setIsPublic(frameworks.getIsPublic());
        // 刻意不 setPassword —— 该字段 SHALL NOT 出现在任何响应中
        return vo;
    }


    @Operation(summary = "查询Frameworks信息集合", description = "查询Frameworks信息集合")
    @Parameters({
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true),
    })
    @GetMapping(value = "/list")
    public R<PageVO<FrameworkSummaryVO>> list(Integer curPage, Integer pageSize) {
        // M5-A1：容器由 com.aicode.core.Page<FrameworksPageVO>（字段名 voList，含
        // params / sortColumns / optObject 等内部字段）收敛为 PageVO<FrameworkSummaryVO>
        // （字段名 records + totalRow/totalPage/curPage/pageSize），与 /project/list 对齐。
        QueryWrapper<Frameworks> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ne(Frameworks::getId, 0);
        IPage<Frameworks> page = new Page<>(curPage, pageSize);

        // 分页默认值：与 com.aicode.core.Page.limit（=25）保持一致
        final int effectivePageSize = (pageSize == null || pageSize <= 0) ? 25 : pageSize;
        PageVO<FrameworkSummaryVO> pageVO = new PageVO<>();
        pageVO.setCurPage(curPage == null || curPage <= 0 ? 1 : curPage);
        pageVO.setPageSize(effectivePageSize);
        long total = frameworksService.count(queryWrapper);
        pageVO.setTotalRow(total);
        if (total > 0) {
            queryWrapper.lambda().orderByDesc(Frameworks::getId);

            IPage<Frameworks> frameworksPage = frameworksService.page(page, queryWrapper);
            List<FrameworkSummaryVO> records = new java.util.ArrayList<>(frameworksPage.getRecords().size());
            for (Frameworks fw : frameworksPage.getRecords()) {
                records.add(toSummary(fw));
            }
            pageVO.setRecords(records);
            pageVO.setTotalPage((int) Math.ceil((double) total / effectivePageSize));
        }
        log.debug(JSON.toJSONString(pageVO));

        return R.success(pageVO);
    }


    @Operation(summary = "修改Frameworks", description = "修改Frameworks")
    @RequestMapping(value = "/modify", method = {RequestMethod.PUT, RequestMethod.POST})
    public R<Frameworks> modify(@Parameter(hidden = true) Frameworks frameworks) {
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
    public R<Frameworks> updateIsPublic(@Parameter(hidden = true) Frameworks frameworks) {
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
    public R<String> delete(@Parameter(hidden = true) FrameworksVO frameworksVO) {
        Frameworks newFrameworks = new Frameworks();
        BeanUtils.copyProperties(frameworksVO, newFrameworks);
        frameworksService.remove(new LambdaQueryWrapper<Frameworks>()
                .eq(Frameworks::getCode, frameworksVO.getCode()));
        return R.success("删除成功");
    }

}
