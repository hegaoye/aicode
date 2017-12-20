package com.rzhkj.project.ctrl;

import com.alibaba.fastjson.JSON;
import com.rzhkj.core.base.BaseCtrl;
import com.rzhkj.core.entity.BeanRet;
import com.rzhkj.core.entity.Page;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.project.entity.ProjectFramwork;
import com.rzhkj.project.service.ProjectFramworkSV;
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
 * 代码仓库账户管理控制器
 * 1.查询一个详情信息
 * 2.查询信息集合
 * 3.添加项目技术
 * 4.删除
 *
 * @author lixin
 */
@Controller
@RequestMapping("/project/framworks")
@Api(value = "代码仓库账户管理控制器", description = "代码仓库账户管理控制器")
public class ProjectFramworkCtrl extends BaseCtrl {

    @Resource
    private ProjectFramworkSV projectFramworkSV;

    /**
     * 查询一个详情信息
     *
     * @param projectCode   项目编码
     * @param frameworkCode 技术编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "frameworkCode", value = "技术编码", required = true, paramType = "query")
    })
    @GetMapping(value = "/load")
    @ResponseBody
    public BeanRet load(String projectCode, String frameworkCode) {
        try {
            Assert.hasText(projectCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(frameworkCode, BaseException.BaseExceptionEnum.Empty_Param.toString());

            Map<String, Object> map = new HashedMap();
            map.put("projectCode", projectCode);
            map.put("frameworkCode", frameworkCode);
            ProjectFramwork projectFramwork = projectFramworkSV.load(map);

            logger.info(JSON.toJSONString(projectFramwork));
            return BeanRet.create(true, "查询一个详情信息", projectFramwork);
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
    public BeanRet list(@ApiIgnore Page<ProjectFramwork> page) {
        try {
            Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());

            page = projectFramworkSV.getList(page);
            int count = projectFramworkSV.count(new HashedMap());
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
     * 添加项目技术
     *
     * @return BeanRet
     */
    @ApiOperation(value = "添加项目技术", notes = "添加项目技术")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "frameworkCode", value = "技术编码", required = true, paramType = "query")
    })
    @PostMapping("/add")
    @ResponseBody
    public BeanRet add(@ApiIgnore ProjectFramwork projectFramwork) {
        try {
            Assert.hasText(projectFramwork.getProjectCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(projectFramwork.getFrameworkCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());

            projectFramworkSV.save(projectFramwork);
            return BeanRet.create(true, "添加项目技术成功", projectFramwork);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "添加项目技术失败");
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
            @ApiImplicitParam(name = "frameworkCode", value = "添加项目技术", required = true, paramType = "query")
    })
    @DeleteMapping("/delete")
    @ResponseBody
    public BeanRet delete(ProjectFramwork projectFramwork) {
        try {
            Assert.hasText(projectFramwork.getFrameworkCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(projectFramwork.getProjectCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());

            projectFramworkSV.delete(projectFramwork);
            return BeanRet.create(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "删除失败");
        }
    }


}
