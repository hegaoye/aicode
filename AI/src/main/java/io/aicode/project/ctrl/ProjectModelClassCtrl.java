/*
 * http://www.aicode.io
本代码仅用于AI-Code.
 */
package io.aicode.project.ctrl;

import com.alibaba.fastjson.JSON;
import io.aicode.base.BaseCtrl;
import io.aicode.base.core.BeanRet;
import io.aicode.base.tools.Page;
import io.aicode.base.tools.StringTools;
import io.aicode.project.entity.ProjectModelClass;
import io.aicode.project.service.ProjectModelClassSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;


/**
 * 模块下的类
 *
 * @author lixin
 */
@Controller
@RequestMapping("/projectModelClass")
@Api(value = "模块下的类控制器", description = "模块下的类控制器")
public class ProjectModelClassCtrl extends BaseCtrl {
    private final static Logger logger = LoggerFactory.getLogger(ProjectModelClassCtrl.class);


    @Resource
    private ProjectModelClassSV projectModelClassSV;


    /**
     * 查询模块下的类详情信息
     *
     * @param id
     * @return BeanRet
     */
    @ApiOperation(value = "查询模块下的类详情信息", notes = "查询模块下的类详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query")
    })
    @GetMapping(value = "/loadById")
    @ResponseBody
    public BeanRet loadById(@PathVariable Long id) {
        if (id == null) {
            return BeanRet.create();
        }

        ProjectModelClass projectModelClass = projectModelClassSV.load(id);
        logger.info(JSON.toJSONString(projectModelClass));
        return BeanRet.create(true, "查询详情信息", projectModelClass);
    }


    /**
     * 查询模块下的类详情信息
     *
     * @param mapClassTableCode 类编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询模块下的类详情信息", notes = "查询模块下的类详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapClassTableCode", value = "类编码", paramType = "query")
    })
    @GetMapping(value = "/loadByMapClassTableCode")
    @ResponseBody
    public BeanRet loadByMapClassTableCode(@PathVariable String mapClassTableCode) {
        if (StringTools.isEmpty(mapClassTableCode)) {
            return BeanRet.create();
        }

        ProjectModelClass projectModelClass = projectModelClassSV.loadByMapClassTableCode(mapClassTableCode);
        logger.info(JSON.toJSONString(projectModelClass));
        return BeanRet.create(true, "查询详情信息", projectModelClass);
    }


    /**
     * 查询模块下的类详情信息
     *
     * @param projectModelCode 模块编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询模块下的类详情信息", notes = "查询模块下的类详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectModelCode", value = "模块编码", paramType = "query")
    })
    @GetMapping(value = "/loadByProjectModelCode")
    @ResponseBody
    public BeanRet loadByProjectModelCode(@PathVariable String projectModelCode) {
        if (StringTools.isEmpty(projectModelCode)) {
            return BeanRet.create();
        }

        ProjectModelClass projectModelClass = projectModelClassSV.loadByProjectModelCode(projectModelCode);
        logger.info(JSON.toJSONString(projectModelClass));
        return BeanRet.create(true, "查询详情信息", projectModelClass);
    }


    /**
     * 查询模块下的类信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询模块下的类信息集合", notes = "查询模块下的类信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "mapClassTableCode", value = "类编码", paramType = "query"),
            @ApiImplicitParam(name = "projectModelCode", value = "模块编码", paramType = "query")
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(@ApiIgnore ProjectModelClass projectModelClass, @ApiIgnore Page<ProjectModelClass> page) {
        if (page == null) {
            return BeanRet.create(false, "分页对象不能为空");
        }
        page.setParams(JSON.parseObject(JSON.toJSONString(projectModelClass)));
        page = projectModelClassSV.getList(page);
        return BeanRet.create(true, "", page);
    }

    /**
     * 创建模块下的类
     *
     * @return BeanRet
     */
    @ApiOperation(value = "创建模块下的类", notes = "创建模块下的类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query", required = true),
            @ApiImplicitParam(name = "mapClassTableCode", value = "类编码", paramType = "query", required = true),
            @ApiImplicitParam(name = "projectModelCode", value = "模块编码", paramType = "query", required = true)
    })
    @PostMapping("/save")
    @ResponseBody
    public BeanRet save(@ApiIgnore ProjectModelClass projectModelClass) {
        projectModelClassSV.save(projectModelClass);
        return BeanRet.create(true, "保存成功");
    }


    /**
     * 修改模块下的类
     *
     * @return BeanRet
     */
    @ApiOperation(value = "修改模块下的类", notes = "修改模块下的类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "mapClassTableCode", value = "类编码", paramType = "query"),
            @ApiImplicitParam(name = "projectModelCode", value = "模块编码", paramType = "query")
    })
    @PutMapping("/modify")
    @ResponseBody
    public BeanRet modify(@ApiIgnore ProjectModelClass projectModelClass) {
        projectModelClassSV.update(projectModelClass);
        return BeanRet.create(true, "修改成功");
    }

    /**
     * 删除模块下的类
     *
     * @return BeanRet
     */
    @ApiOperation(value = "删除模块下的类", notes = "删除模块下的类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query", required = true),
            @ApiImplicitParam(name = "mapClassTableCode", value = "类编码", paramType = "query", required = true),
            @ApiImplicitParam(name = "projectModelCode", value = "模块编码", paramType = "query", required = true)
    })
    @DeleteMapping("/delete")
    @ResponseBody
    public BeanRet delete(Long id, String mapClassTableCode, String projectModelCode) {
        if (id == null) {
            return BeanRet.create(false, "不能为空");
        }
        if (mapClassTableCode == null) {
            return BeanRet.create(false, "类编码不能为空");
        }
        if (projectModelCode == null) {
            return BeanRet.create(false, "模块编码不能为空");
        }
        projectModelClassSV.delete(id, mapClassTableCode, projectModelCode);
        return BeanRet.create(true, "删除ProjectModelClass成功");
    }

}
