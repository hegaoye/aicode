package com.rzhkj.project.ctrl;

import com.alibaba.fastjson.JSON;
import com.rzhkj.core.base.BaseCtrl;
import com.rzhkj.core.entity.BeanRet;
import com.rzhkj.core.entity.Page;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.project.entity.ProjectRepositoryAccount;
import com.rzhkj.project.entity.ProjectTools;
import com.rzhkj.project.service.ProjectRepositoryAccountSV;
import com.rzhkj.project.service.ProjectToolsSV;
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
 * 代码仓库账户管理控制器
 * 1.查询一个详情信息
 * 2.查询信息集合
 * 3.添加工具
 * 4.删除
 *
 * @author lixin
 */
@Controller
@RequestMapping("/tools")
public class ProjectToolsCtrl extends BaseCtrl {

    @Resource
    private ProjectToolsSV projectToolsSV;

    /**
     * 查询一个详情信息
     *
     * @param projectCode 项目编码
     * @param toolsCode   工具编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "toolsCode", value = "工具编码", required = true, paramType = "query")
    })
    @GetMapping(value = "/load")
    @ResponseBody
    public BeanRet load(String projectCode, String toolsCode) {
        try {
            Assert.hasText(projectCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(toolsCode, BaseException.BaseExceptionEnum.Empty_Param.toString());

            Map<String, Object> map = new HashedMap();
            map.put("projectCode", projectCode);
            map.put("toolsCode", toolsCode);
            ProjectTools projectTools = projectToolsSV.load(map);

            logger.info(JSON.toJSONString(projectTools));
            return BeanRet.create(true, "查询一个详情信息", projectTools);
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
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(@ApiIgnore Page<ProjectTools> page) {
        try {
            Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());

            page = projectToolsSV.getList(page);
            int count = projectToolsSV.count(new HashedMap());
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
     * 添加工具
     *
     * @return BeanRet
     */
    @ApiOperation(value = "添加工具", notes = "添加工具")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "toolsCode", value = "工具编码", required = true, paramType = "query")
    })
    @PostMapping("/add")
    @ResponseBody
    public BeanRet add(@ApiIgnore ProjectTools projectTools) {
        try {
            Assert.hasText(projectTools.getProjectCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(projectTools.getToolsCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());

            projectToolsSV.save(projectTools);
            return BeanRet.create(true, "创建账户成功", projectTools);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "创建账户失败");
        }
    }


    /**
     * 删除
     *
     * @return BeanRet
     */
    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "toolsCode", value = "工具编码", required = true, paramType = "query")
    })
    @DeleteMapping("/delete")
    @ResponseBody
    public BeanRet delete(ProjectTools projectTools) {
        try {
            Assert.hasText(projectTools.getToolsCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(projectTools.getProjectCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());

            projectToolsSV.delete(projectTools);
            return BeanRet.create(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "删除失败");
        }
    }


}
