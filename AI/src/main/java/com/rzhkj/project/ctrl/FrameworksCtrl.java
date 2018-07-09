package com.rzhkj.project.ctrl;

import com.alibaba.fastjson.JSON;
import com.rzhkj.core.base.BaseCtrl;
import com.rzhkj.core.entity.BeanRet;
import com.rzhkj.core.entity.Page;
import com.rzhkj.core.enums.YNEnum;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.project.entity.Frameworks;
import com.rzhkj.project.service.FrameworksSV;
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
 * 框架技术控制器
 * 1.查询一个详情信息
 * 2.查询信息集合
 * 3.添加
 * 4.修改
 * 4.删除
 *
 * @author lixin
 */
@Controller
@RequestMapping("/framework")
@Api(value = "框架技术控制器", description = "框架技术控制器")
public class FrameworksCtrl extends BaseCtrl {

    @Resource
    private FrameworksSV frameworksSV;

    /**
     * 查询一个详情信息
     *
     * @param code 模板编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "技术编码", required = true, paramType = "query")
    })
    @GetMapping(value = "/load")
    @ResponseBody
    public BeanRet load(String code) {
        try {
            Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());

            Map<String, Object> map = new HashedMap();
            map.put("code", code);
            Frameworks Frameworks = frameworksSV.load(map);

            logger.info(JSON.toJSONString(Frameworks));
            return BeanRet.create(true, "查询一个详情信息", Frameworks);
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
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(@ApiIgnore Page<Frameworks> page) {
        try {
            Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());

            page = frameworksSV.getList(page);
            int count = frameworksSV.count(new HashedMap());
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
            @ApiImplicitParam(name = "name", value = "技术名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "account", value = "模板仓库账户", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "模板仓库密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "gitHome", value = "模板仓库地址", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isPublic", value = "是否是公开库 Y N", required = true, paramType = "query"),
            @ApiImplicitParam(name = "description", value = "技术描述", required = true, paramType = "query")
    })
    @PostMapping("/build")
    @ResponseBody
    public BeanRet build(@ApiIgnore Frameworks frameworks) {
        try {
            Assert.hasText(frameworks.getName(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(frameworks.getDescription(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            frameworksSV.save(frameworks);
            return BeanRet.create(true, "添加成功", frameworks);
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
            @ApiImplicitParam(name = "name", value = "类型名", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "类型说明", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态：停用[Disenable]，启用[Enable]", paramType = "query")
    })
    @PostMapping("/modify")
    @ResponseBody
    public BeanRet modify(@ApiIgnore Frameworks frameworks) {
        try {
            Assert.hasText(frameworks.getName(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            frameworksSV.saveOrUpdate(frameworks);
            return BeanRet.create(true, "修改成功", frameworks);
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
            @ApiImplicitParam(name = "code", value = "技术编码", required = true, paramType = "query")
    })
    @DeleteMapping("/delete")
    @ResponseBody
    public BeanRet delete(String code) {
        try {
            Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
            frameworksSV.delete(code);
            return BeanRet.create(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "删除失败");
        }
    }


}
