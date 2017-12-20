package com.rzhkj.project.ctrl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.rzhkj.core.base.BaseCtrl;
import com.rzhkj.core.entity.BeanRet;
import com.rzhkj.core.entity.Page;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.project.entity.ProjectFrameworkAttributeValue;
import com.rzhkj.project.service.ProjectFrameworkAttributeValueSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 框架技术属性控制器
 * 1.查询一个详情信息
 * 2.查询信息集合
 * 3.添加
 * 4.修改
 *
 * @author lixin
 */
@Controller
@RequestMapping("/project/frameworks/attributes")
@Api(value = "框架技术属性控制器", description = "框架技术属性控制器")
public class ProjectFrameworksAttributesCtrl extends BaseCtrl {

    @Resource
    private ProjectFrameworkAttributeValueSV projectFrameworkAttributeValueSV;

    /**
     * 查询一个详情信息
     *
     * @param code 模板编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "属性值编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "attributeCode", value = "属性编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "frameworkCode", value = "技术编码", required = true, paramType = "query"),
    })
    @GetMapping(value = "/load")
    @ResponseBody
    public BeanRet load(String code, String attributeCode, String projectCode, String frameworkCode) {
        try {
            Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(attributeCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(projectCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(frameworkCode, BaseException.BaseExceptionEnum.Empty_Param.toString());

            Map<String, Object> map = new HashedMap();
            map.put("code", code);
            map.put("attributeCode", code);
            map.put("projectCode", code);
            map.put("frameworkCode", code);
            ProjectFrameworkAttributeValue projectFrameworkAttributeValue = projectFrameworkAttributeValueSV.load(map);

            logger.info(JSON.toJSONString(projectFrameworkAttributeValue));
            return BeanRet.create(true, "查询一个详情信息", projectFrameworkAttributeValue);
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
            @ApiImplicitParam(name = "frameworkCode", value = "技术编码", paramType = "query"),
            @ApiImplicitParam(name = "projectCode", value = "技术编码", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(String frameworkCode, String projectCode, @ApiIgnore Page<ProjectFrameworkAttributeValue> page) {
        try {
            Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(frameworkCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(projectCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Map<String, Object> map = Maps.newHashMap();
            map.put("frameworkCode", frameworkCode);
            map.put("projectCode", frameworkCode);
            page.setParams(map);
            page = projectFrameworkAttributeValueSV.getList(page);
            int count = projectFrameworkAttributeValueSV.count(map);
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
            @ApiImplicitParam(name = "frameworkCode", value = "技术编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "attributeCode", value = "属性编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "attributeValue", value = "属性编码", required = true, paramType = "query")
    })
    @PostMapping("/build")
    @ResponseBody
    public BeanRet build(@ApiIgnore ProjectFrameworkAttributeValue projectFrameworkAttributeValue) {
        try {
            Assert.hasText(projectFrameworkAttributeValue.getFrameworkCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(projectFrameworkAttributeValue.getAttributeCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(projectFrameworkAttributeValue.getProjectCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(projectFrameworkAttributeValue.getAttributeValue(), BaseException.BaseExceptionEnum.Empty_Param.toString());

            projectFrameworkAttributeValueSV.save(projectFrameworkAttributeValue);
            return BeanRet.create(true, "添加成功", projectFrameworkAttributeValue);
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
            @ApiImplicitParam(name = "code", value = "属性值编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "frameworkCode", value = "技术编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "attributeCode", value = "属性编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "attributeValue", value = "属性编码", required = true, paramType = "query")
    })
    @PostMapping("/modify")
    @ResponseBody
    public BeanRet modify(@ApiIgnore ProjectFrameworkAttributeValue projectFrameworkAttributeValue) {
        try {
            Assert.hasText(projectFrameworkAttributeValue.getCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            projectFrameworkAttributeValueSV.saveOrUpdate(projectFrameworkAttributeValue);
            return BeanRet.create(true, "修改成功", projectFrameworkAttributeValue);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "修改失败");
        }
    }

}
