/*
 * http://www.aicode.io
本代码仅用于AI-Code.
 */
package io.aicode.project.ctrl;

import com.alibaba.fastjson.JSON;
import io.aicode.base.core.BeanRet;
import io.aicode.base.tools.Page;
import io.aicode.base.tools.StringTools;
import io.aicode.project.service.ProjectModelSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import io.aicode.base.BaseCtrl;
import io.aicode.project.entity.ProjectModel;


/**
 * 模块
 *
 * @author lixin
 */
@Controller
@RequestMapping("/projectModel")
@Api(value = "模块控制器", description = "模块控制器")
public class ProjectModelCtrl extends BaseCtrl {
    private final static Logger logger = LoggerFactory.getLogger(ProjectModelCtrl.class);
    @Resource
    private ProjectModelSV projectModelSV;


    /**
     * 查询模块详情信息
     *
     * @param id
     * @return BeanRet
     */
    @ApiOperation(value = "查询模块详情信息", notes = "查询模块详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query")
    })
    @GetMapping(value = "/loadById")
    @ResponseBody
    public BeanRet loadById(@PathVariable Long id) {
        if (id == null) {
            return BeanRet.create();
        }

        ProjectModel projectModel = projectModelSV.load(id);
        logger.info(JSON.toJSONString(projectModel));
        return BeanRet.create(true, "查询详情信息", projectModel);
    }


    /**
     * 查询模块详情信息
     *
     * @param code 模块编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询模块详情信息", notes = "查询模块详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "模块编码", paramType = "query")
    })
    @GetMapping(value = "/loadByCode")
    @ResponseBody
    public BeanRet loadByCode(@PathVariable String code) {
        if (StringTools.isEmpty(code)) {
            return BeanRet.create();
        }

        ProjectModel projectModel = projectModelSV.loadByCode(code);
        logger.info(JSON.toJSONString(projectModel));
        return BeanRet.create(true, "查询详情信息", projectModel);
    }


    /**
     * 查询模块信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询模块信息集合", notes = "查询模块信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "模块编码", paramType = "query"),
            @ApiImplicitParam(name = "preCode", value = "上级模块编码", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "模块显示名称", paramType = "query"),
            @ApiImplicitParam(name = "route", value = "模块路由", paramType = "query"),
            @ApiImplicitParam(name = "css", value = "模块css样式", paramType = "query"),
            @ApiImplicitParam(name = "isMenu", value = "是否是菜单 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "ico", value = "模块图标", paramType = "query")
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(@ApiIgnore ProjectModel projectModel, @ApiIgnore Page<ProjectModel> page) {
        if (page == null) {
            return BeanRet.create(false, "分页对象不能为空");
        }
        page.setParams(JSON.parseObject(JSON.toJSONString(projectModel)));
        page = projectModelSV.getList(page);
        return BeanRet.create(true, "", page);
    }

    /**
     * 创建模块
     *
     * @return BeanRet
     */
    @ApiOperation(value = "创建模块", notes = "创建模块")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query", required = true),
            @ApiImplicitParam(name = "code", value = "模块编码", paramType = "query", required = true),
            @ApiImplicitParam(name = "preCode", value = "上级模块编码", paramType = "query", required = true),
            @ApiImplicitParam(name = "name", value = "模块显示名称", paramType = "query", required = true),
            @ApiImplicitParam(name = "route", value = "模块路由", paramType = "query", required = true),
            @ApiImplicitParam(name = "css", value = "模块css样式", paramType = "query", required = true),
            @ApiImplicitParam(name = "isMenu", value = "是否是菜单 Y,N", paramType = "query", required = true),
            @ApiImplicitParam(name = "ico", value = "模块图标", paramType = "query", required = true)
    })
    @PostMapping("/save")
    @ResponseBody
    public BeanRet save(@ApiIgnore ProjectModel projectModel) {
        projectModelSV.save(projectModel);
        return BeanRet.create(true, "保存成功");
    }


    /**
     * 修改模块
     *
     * @return BeanRet
     */
    @ApiOperation(value = "修改模块", notes = "修改模块")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "模块编码", paramType = "query"),
            @ApiImplicitParam(name = "preCode", value = "上级模块编码", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "模块显示名称", paramType = "query"),
            @ApiImplicitParam(name = "route", value = "模块路由", paramType = "query"),
            @ApiImplicitParam(name = "css", value = "模块css样式", paramType = "query"),
            @ApiImplicitParam(name = "isMenu", value = "是否是菜单 Y,N", paramType = "query"),
            @ApiImplicitParam(name = "ico", value = "模块图标", paramType = "query")
    })
    @PutMapping("/modify")
    @ResponseBody
    public BeanRet modify(@ApiIgnore ProjectModel projectModel) {
        projectModelSV.update(projectModel);
        return BeanRet.create(true, "修改成功");
    }

    /**
     * 删除模块
     *
     * @return BeanRet
     */
    @ApiOperation(value = "删除模块", notes = "删除模块")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query", required = true),
            @ApiImplicitParam(name = "code", value = "模块编码", paramType = "query", required = true)
    })
    @DeleteMapping("/delete")
    @ResponseBody
    public BeanRet delete(Long id, String code) {
        if (id == null) {
            return BeanRet.create(false, "不能为空");
        }
        if (code == null) {
            return BeanRet.create(false, "模块编码不能为空");
        }
        projectModelSV.delete(id, code);
        return BeanRet.create(true, "删除ProjectModel成功");
    }

}
