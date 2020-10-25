/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.frameworks.ctrl;

import com.aicode.frameworks.entity.FrameworksTemplate;
import com.aicode.frameworks.service.FrameworksTemplateService;
import com.aicode.frameworks.vo.FrameworksTemplatePageVO;
import com.aicode.frameworks.vo.FrameworksTemplateSaveVO;
import com.aicode.frameworks.vo.FrameworksTemplateVO;
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
 * 框架配置文件模板
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/frameworksTemplate")
@Slf4j
@Api(value = "框架配置文件模板控制器", tags = "框架配置文件模板控制器")
public class FrameworksTemplateController {
    @Autowired
    private FrameworksTemplateService frameworksTemplateService;


    /**
     * 创建 框架配置文件模板
     *
     * @return R
     */
    @ApiOperation(value = "创建FrameworksTemplate", notes = "创建FrameworksTemplate")
    @PostMapping("/build")
    public FrameworksTemplateSaveVO build(@ApiParam(name = "创建FrameworksTemplate", value = "传入json格式", required = true)
                                   @RequestBody FrameworksTemplateSaveVO frameworksTemplateSaveVO) {
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



    /**
     * 查询框架配置文件模板信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询FrameworksTemplate信息集合", notes = "查询FrameworksTemplate信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<FrameworksTemplateVO> list(@ApiIgnore FrameworksTemplatePageVO frameworksTemplateVO, Integer curPage, Integer pageSize) {
        Page<FrameworksTemplate> page = new Page<>(pageSize, curPage);
        QueryWrapper<FrameworksTemplate> queryWrapper = new QueryWrapper<>();
        int total = frameworksTemplateService.count(queryWrapper);
        PageVO<FrameworksTemplateVO> frameworksTemplateVOPageVO = new PageVO<>();
        if (total > 0) {
            List<FrameworksTemplate> frameworksTemplateList = frameworksTemplateService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            frameworksTemplateVOPageVO.setTotalRow(total);
            frameworksTemplateVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(frameworksTemplateList),FrameworksTemplateVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return frameworksTemplateVOPageVO;
    }


    /**
     * 修改 框架配置文件模板
     *
     * @return R
     */
    @ApiOperation(value = "修改FrameworksTemplate", notes = "修改FrameworksTemplate")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改FrameworksTemplate", value = "传入json格式", required = true)
                          @RequestBody FrameworksTemplateVO frameworksTemplateVO) {
        FrameworksTemplate newFrameworksTemplate = new FrameworksTemplate();
        BeanUtils.copyProperties(frameworksTemplateVO, newFrameworksTemplate);
        boolean isUpdated = frameworksTemplateService.update(newFrameworksTemplate, new LambdaQueryWrapper<FrameworksTemplate>()
                .eq(FrameworksTemplate::getId, frameworksTemplateVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 框架配置文件模板
     *
     * @return R
     */
    @ApiOperation(value = "删除FrameworksTemplate", notes = "删除FrameworksTemplate")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore FrameworksTemplateVO frameworksTemplateVO) {
        FrameworksTemplate newFrameworksTemplate = new FrameworksTemplate();
        BeanUtils.copyProperties(frameworksTemplateVO, newFrameworksTemplate);
        frameworksTemplateService.remove(new LambdaQueryWrapper<FrameworksTemplate>()
                .eq(FrameworksTemplate::getId, frameworksTemplateVO.getId()));
        return R.success("删除成功");
    }

}
