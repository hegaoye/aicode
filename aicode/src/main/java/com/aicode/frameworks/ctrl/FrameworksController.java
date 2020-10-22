/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.frameworks.ctrl;

import com.aicode.frameworks.entity.Frameworks;
import com.aicode.frameworks.service.FrameworksService;
import com.aicode.frameworks.vo.FrameworksPageVO;
import com.aicode.frameworks.vo.FrameworksSaveVO;
import com.aicode.frameworks.vo.FrameworksVO;
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
 * 框架技术池
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/frameworks")
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
    @PostMapping("/build")
    public FrameworksSaveVO build(@ApiParam(name = "创建Frameworks", value = "传入json格式", required = true)
                                   @RequestBody FrameworksSaveVO frameworksSaveVO) {
        if (null == frameworksSaveVO) {
            return null;
        }
        Frameworks newFrameworks = new Frameworks();
        BeanUtils.copyProperties(frameworksSaveVO, newFrameworks);

        frameworksService.save(newFrameworks);

        frameworksSaveVO = new FrameworksSaveVO();
        BeanUtils.copyProperties(newFrameworks, frameworksSaveVO);
        log.debug(JSON.toJSONString(frameworksSaveVO));
        return frameworksSaveVO;
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
    public PageVO<FrameworksVO> list(@ApiIgnore FrameworksPageVO frameworksVO, Integer curPage, Integer pageSize) {
        Page<Frameworks> page = new Page<>(pageSize, curPage);
        QueryWrapper<Frameworks> queryWrapper = new QueryWrapper<>();
        int total = frameworksService.count(queryWrapper);
        PageVO<FrameworksVO> frameworksVOPageVO = new PageVO<>();
        if (total > 0) {
            List<Frameworks> frameworksList = frameworksService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            frameworksVOPageVO.setTotalRow(total);
            frameworksVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(frameworksList),FrameworksVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return frameworksVOPageVO;
    }


    /**
     * 修改 框架技术池
     *
     * @return R
     */
    @ApiOperation(value = "修改Frameworks", notes = "修改Frameworks")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改Frameworks", value = "传入json格式", required = true)
                          @RequestBody FrameworksVO frameworksVO) {
        Frameworks newFrameworks = new Frameworks();
        BeanUtils.copyProperties(frameworksVO, newFrameworks);
        boolean isUpdated = frameworksService.update(newFrameworks, new LambdaQueryWrapper<Frameworks>()
                .eq(Frameworks::getId, frameworksVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 框架技术池
     *
     * @return R
     */
    @ApiOperation(value = "删除Frameworks", notes = "删除Frameworks")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "技术编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore FrameworksVO frameworksVO) {
        Frameworks newFrameworks = new Frameworks();
        BeanUtils.copyProperties(frameworksVO, newFrameworks);
        frameworksService.remove(new LambdaQueryWrapper<Frameworks>()
                .eq(Frameworks::getId, frameworksVO.getId()));
        return R.success("删除成功");
    }

}
