/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.frameworks.ctrl;

import com.aicode.core.entity.Page;
import com.aicode.core.entity.R;
import com.aicode.core.exceptions.BaseException;
import com.aicode.frameworks.entity.Frameworks;
import com.aicode.frameworks.service.FrameworksService;
import com.aicode.frameworks.vo.FrameworksVO;
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
 * 框架技术池
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/framework")
@Slf4j
@Api(value = "框架技术池控制器", tags = "框架技术池控制器")
public class FrameworksController {
    @Autowired
    private FrameworksService frameworksService;


    /**
     * 创建 框架技术池
     *
     * @return R
     */
    @ApiOperation(value = "创建Frameworks", notes = "创建Frameworks")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "技术名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "account", value = "模板仓库账户", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "模板仓库密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "gitHome", value = "模板仓库地址", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isPublic", value = "是否是公开库 Y N", required = true, paramType = "query"),
            @ApiImplicitParam(name = "description", value = "技术描述", required = true, paramType = "query")
    })
    @PostMapping("/build")
    public R build(@ApiIgnore Frameworks frameworks) {
        frameworksService.save(frameworks);
        return R.success();
    }


    /**
     * 根据条件code查询框架技术池一个详情信息
     *
     * @param code 技术编码
     * @return FrameworksVO
     */
    @ApiOperation(value = "创建Frameworks", notes = "创建Frameworks")
    @GetMapping("/load")
    public R load(java.lang.String code) {
        if (code == null) {
            return null;
        }
        Frameworks frameworks = frameworksService.getOne(new LambdaQueryWrapper<Frameworks>()
                .eq(Frameworks::getCode, code));
        log.debug(JSON.toJSONString(frameworks));
        return R.success(frameworks);
    }


    /**
     * 根据条件code查询框架技术池一个详情信息
     *
     * @param code 技术编码
     * @return FrameworksVO
     */
    @ApiOperation(value = "创建Frameworks", notes = "创建Frameworks")
    @GetMapping("/load/code/{code}")
    public FrameworksVO loadByCode(@PathVariable java.lang.String code) {
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

    /**
     * 查询框架技术池信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询Frameworks信息集合", notes = "查询Frameworks信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public R list(@ApiIgnore Page<Frameworks> page) {
        Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());
        QueryWrapper<Frameworks> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ne(Frameworks::getId, 0);

        int total = frameworksService.count(queryWrapper);
        if (total > 0) {
            List<Frameworks> frameworksList = frameworksService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            page.setTotalRow(total);
            page.setRecords(frameworksList);
            log.debug(JSON.toJSONString(page));
        }
        return R.success(page);
    }


    /**
     * 修改 框架技术池
     *
     * @return R
     */
    @ApiOperation(value = "修改Frameworks", notes = "修改Frameworks")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "模板编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "name", value = "类型名", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "类型说明", paramType = "query"),
            @ApiImplicitParam(name = "isPublic", value = "状态：停用[Disenable]，启用[Enable]", paramType = "query")
    })
    @RequestMapping(value = "/modify", method = {RequestMethod.PUT, RequestMethod.POST})
    public R modify(@ApiIgnore Frameworks frameworks) {
        frameworksService.update(frameworks, new LambdaQueryWrapper<Frameworks>()
                .eq(Frameworks::getCode, frameworks.getCode()));
        return R.success(frameworks);
    }

    /**
     * 修改状态
     *
     * @return BeanRet
     */
    @ApiOperation(value = "修改状态", notes = "修改状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "模板编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isPublic", value = "状态：停用[N]，启用[Y]", paramType = "query")
    })
    @RequestMapping(value = "/updateIsPublic", method = {RequestMethod.PUT, RequestMethod.POST})
    public R updateIsPublic(@ApiIgnore Frameworks frameworks) {
        Assert.hasText(frameworks.getCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(frameworks.getIsPublic(), BaseException.BaseExceptionEnum.Empty_Param.toString());

        frameworksService.update(frameworks, new LambdaQueryWrapper<Frameworks>()
                .eq(Frameworks::getCode, frameworks.getCode()));
        return R.success(frameworks);
    }


    /**
     * 删除 框架技术池
     *
     * @return R
     */
    @ApiOperation(value = "删除Frameworks", notes = "删除Frameworks")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "技术编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore FrameworksVO frameworksVO) {
        Frameworks newFrameworks = new Frameworks();
        BeanUtils.copyProperties(frameworksVO, newFrameworks);
        frameworksService.remove(new LambdaQueryWrapper<Frameworks>()
                .eq(Frameworks::getCode, frameworksVO.getCode()));
        return R.success("删除成功");
    }

}
