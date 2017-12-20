package com.rzhkj.project.ctrl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.rzhkj.core.base.BaseCtrl;
import com.rzhkj.core.entity.BeanRet;
import com.rzhkj.core.entity.Page;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.project.entity.FrameworksConfigureTemplate;
import com.rzhkj.project.entity.FrameworksConfigureTemplateStateEnum;
import com.rzhkj.project.service.FrameworksConfigureTemplateSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 框架配置文件模板控制器
 * 1.查询一个详情信息
 * 2.查询信息集合
 * 3.添加
 * 4.修改
 * 5.删除
 *
 * @author lixin
 */
@Controller
@RequestMapping("/framework/template")
@Api(value = "框架配置文件模板控制器", description = "框架配置文件模板控制器")
public class FrameworksConfigureTemplateCtrl extends BaseCtrl {

    @Resource
    private FrameworksConfigureTemplateSV frameworksConfigureTemplateSV;

    /**
     * 查询一个详情信息
     *
     * @param code 模板编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "模板编码", required = true, paramType = "query")
    })
    @GetMapping(value = "/load")
    @ResponseBody
    public BeanRet load(String code) {
        try {
            Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());

            Map<String, Object> map = new HashedMap();
            map.put("code", code);
            FrameworksConfigureTemplate frameworksConfigureTemplate = frameworksConfigureTemplateSV.load(map);

            logger.info(JSON.toJSONString(frameworksConfigureTemplate));
            return BeanRet.create(true, "查询一个详情信息", frameworksConfigureTemplate);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create("未查到需要的内容");
        }
    }


    /**
     * 查询信息集合 按照时间顺序倒叙排序
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询信息集合 按照时间顺序倒叙排序", notes = "查询信息集合 按照时间顺序倒叙排序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "frameworksCode", value = "框架编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(String frameworksCode, @ApiIgnore Page<FrameworksConfigureTemplate> page) {
        try {
            Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());

            Map<String, Object> map = Maps.newHashMap();
            map.put("frameworksCode", frameworksCode);
            page.setParams(map);
            page = frameworksConfigureTemplateSV.getList(page);
            int count = frameworksConfigureTemplateSV.count(map);
            page.setTotalRow(count);

            logger.info(JSON.toJSONString(page));
            return BeanRet.create(true, "", page);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create("未查到需要的内容");
        }
    }

    /**
     * 添加
     *
     * @return BeanRet
     */
    @ApiOperation(value = "添加", notes = "添加")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "frameworksCode", value = "框架编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "name", value = "模板名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "description", value = "模板说明", required = true, paramType = "query")
    })
    @PostMapping("/build")
    @ResponseBody
    public BeanRet build(@ApiIgnore FrameworksConfigureTemplate frameworksConfigureTemplate) {
        try {
            Assert.hasText(frameworksConfigureTemplate.getName(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(frameworksConfigureTemplate.getDescription(), BaseException.BaseExceptionEnum.Empty_Param.toString());

            frameworksConfigureTemplateSV.save(frameworksConfigureTemplate);
            return BeanRet.create(true, "添加成功", frameworksConfigureTemplate);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "添加失败");
        }
    }

    /**
     * 修改
     *
     * @return BeanRet
     */
    @ApiOperation(value = "修改", notes = "修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "模板编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "frameworksCode", value = "框架编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "name", value = "模板名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "description", value = "模板说明", required = true, paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态：停用[Disenable]，启用[Enable]", required = true, paramType = "query")
    })
    @PostMapping("/modify")
    @ResponseBody
    public BeanRet modify(@ApiIgnore FrameworksConfigureTemplate frameworksConfigureTemplate) {
        try {
            Assert.hasText(frameworksConfigureTemplate.getCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            frameworksConfigureTemplateSV.saveOrUpdate(frameworksConfigureTemplate);
            return BeanRet.create(true, "修改成功", frameworksConfigureTemplate);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "修改失败");
        }
    }


    /**
     * 删除
     *
     * @return BeanRet
     */
    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "模板编码", required = true, paramType = "query")
    })
    @DeleteMapping("/delete")
    @ResponseBody
    public BeanRet delete(String code) {
        try {
            Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Map<String, Object> map = Maps.newHashMap();
            map.put("code", code);
            FrameworksConfigureTemplate frameworksConfigureTemplate = frameworksConfigureTemplateSV.load(map);

            frameworksConfigureTemplate.setState(FrameworksConfigureTemplateStateEnum.Delete.name());
            frameworksConfigureTemplateSV.saveOrUpdate(frameworksConfigureTemplate);
            return BeanRet.create(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "删除失败");
        }
    }


}
