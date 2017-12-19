package com.rzhkj.project.ctrl;

import com.alibaba.fastjson.JSON;
import com.rzhkj.core.base.BaseCtrl;
import com.rzhkj.core.entity.BeanRet;
import com.rzhkj.core.entity.Page;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.project.entity.Project;
import com.rzhkj.project.service.ProjectSV;
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
 * 项目管理控制器
 * 1.查询一个详情信息
 * 2.查询项目信息集合
 * 3.创建项目
 * 4.修改项目
 * 5.删除项目
 * 6.执行脚本
 *
 * @author lixin
 */
@Controller
@RequestMapping("/project")
public class ProjectCtrl extends BaseCtrl {

    @Resource
    private ProjectSV projectSV;

    /**
     * 查询一个详情信息
     *
     * @param code 项目编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "项目编码", paramType = "query")
    })
    @GetMapping(value = "/load")
    @ResponseBody
    public BeanRet load(String code) {
        try {
            Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Map<String, Object> map = new HashedMap();
            map.put("code", code);
            Project project = projectSV.load(map);
            logger.info(JSON.toJSONString(project));
            return BeanRet.create(true, "查询一个详情信息", project);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create("未查到需要的内容");
        }
    }


    /**
     * 查询项目信息集合 按照时间顺序倒叙排序
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询项目信息集合 按照时间顺序倒叙排序", notes = "查询项目信息集合 按照时间顺序倒叙排序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(@ApiIgnore Page<Project> page) {
        try {
            Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());
            page = projectSV.getList(page);
            int count = projectSV.count(new HashedMap());
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
     * 创建项目
     *
     * @return BeanRet
     */
    @ApiOperation(value = "创建项目", notes = "创建项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "项目名 最长128个汉字", required = true, paramType = "query"),
            @ApiImplicitParam(name = "englishName", value = "项目英文名 （项目数据库名） 最长256个英文字符", required = true, paramType = "query"),
            @ApiImplicitParam(name = "description", value = "项目描述 最长256个汉字", required = true, paramType = "query"),
            @ApiImplicitParam(name = "databaseType", value = "数据库类型:Mysql,Oracle...", required = true, paramType = "query"),
            @ApiImplicitParam(name = "language", value = "项目语言:Java,Python,Js...", required = true, paramType = "query"),
            @ApiImplicitParam(name = "copyright", value = "项目版权文字信息", required = true, paramType = "query"),
            @ApiImplicitParam(name = "author", value = "作者", required = true, paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "联系方式", required = true, paramType = "query"),
            @ApiImplicitParam(name = "basePackage", value = "项目基础包名", required = true, paramType = "query")
    })
    @PostMapping("/build")
    @ResponseBody
    public BeanRet build(@ApiIgnore Project project) {
        try {
            Assert.hasText(project.getName(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(project.getEnglishName(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(project.getAuthor(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(project.getBasePackage(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(project.getCopyright(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(project.getDatabaseType(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(project.getDescription(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(project.getPhone(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(project.getLanguage(), BaseException.BaseExceptionEnum.Empty_Param.toString());

            projectSV.build(project);
            return BeanRet.create(true, "创建项目成功", project);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "创建项目失败");
        }
    }


    /**
     * 修改项目
     *
     * @return BeanRet
     */
    @ApiOperation(value = "修改项目", notes = "修改项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "name", value = "项目名 最长128个汉字", paramType = "query"),
            @ApiImplicitParam(name = "englishName", value = "项目英文名 （项目数据库名） 最长256个英文字符", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "项目描述 最长256个汉字", paramType = "query"),
            @ApiImplicitParam(name = "databaseType", value = "数据库类型:Mysql,Oracle...", paramType = "query"),
            @ApiImplicitParam(name = "language", value = "项目语言:Java,Python,Js...", paramType = "query"),
            @ApiImplicitParam(name = "copyright", value = "项目版权文字信息", paramType = "query"),
            @ApiImplicitParam(name = "author", value = "作者", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "联系方式", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "项目状态", paramType = "query"),
            @ApiImplicitParam(name = "basePackage", value = "项目基础包名", paramType = "query")
    })
    @PutMapping("/modify")
    @ResponseBody
    public BeanRet modify(@ApiIgnore Project project) {
        try {
            Assert.hasText(project.getCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());

            projectSV.saveOrUpdate(project);
            return BeanRet.create(true, "修改项目成功", project);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "修改项目失败");
        }
    }

    /**
     * 删除项目
     *
     * @return BeanRet
     */
    @ApiOperation(value = "删除项目", notes = "删除项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "项目编码", required = true, paramType = "query")
    })
    @DeleteMapping("/delete")
    @ResponseBody
    public BeanRet delete(String code) {
        try {
            Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());

            projectSV.delete(code);
            return BeanRet.create(true, "修改项目成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "修改项目失败");
        }
    }


    /**
     * 执行脚本
     *
     * @return BeanRet
     */
    @ApiOperation(value = "执行脚本", notes = "执行脚本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "项目编码", required = true, paramType = "query")
    })
    @PutMapping("/execute")
    @ResponseBody
    public BeanRet execute(String code) {
        try {
            Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());

            projectSV.execute(code);
            return BeanRet.create(true, "执行脚本成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "执行脚本失败");
        }
    }


}
