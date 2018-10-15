package io.aicode.project.ctrl;

import com.alibaba.fastjson.JSON;
import io.aicode.core.base.BaseCtrl;
import io.aicode.core.entity.BeanRet;
import io.aicode.core.entity.Page;
import io.aicode.core.exceptions.BaseException;
import io.aicode.project.entity.ProjectModule;
import io.aicode.project.service.ProjectModuleSV;
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
 * 模块管理控制器
 * 1.查询一个详情信息
 * 2.查询信息集合
 * 3.添加模块
 * 4.删除
 *
 * @author lixin
 */
@Controller
@RequestMapping("/project/mouldles")
@Api(value = "模块管理控制器", description = "模块管理控制器")
public class ProjectModuleCtrl extends BaseCtrl {

    @Resource
    private ProjectModuleSV projectModuleSV;

    /**
     * 查询一个详情信息
     *
     * @param projectCode 项目编码
     * @param moudleCode  模块编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "moudleCode", value = "模块编码", required = true, paramType = "query")
    })
    @GetMapping(value = "/load")
    @ResponseBody
    public BeanRet load(String projectCode, String moudleCode) {
        try {
            Assert.hasText(projectCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(moudleCode, BaseException.BaseExceptionEnum.Empty_Param.toString());

            Map<String, Object> map = new HashedMap();
            map.put("projectCode", projectCode);
            map.put("moudleCode", moudleCode);
            ProjectModule projectModule = projectModuleSV.load(map);

            logger.info(JSON.toJSONString(projectModule));
            return BeanRet.create(true, "查询一个详情信息", projectModule);
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
    public BeanRet list(@ApiIgnore Page<ProjectModule> page) {
        try {
            Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());

            page = projectModuleSV.getList(page);
            int count = projectModuleSV.count(new HashedMap());
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
     * 添加模块
     *
     * @return BeanRet
     */
    @ApiOperation(value = "添加模块", notes = "添加模块")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "moudleCode", value = "模块编码", required = true, paramType = "query")
    })
    @PostMapping("/add")
    @ResponseBody
    public BeanRet add(@ApiIgnore ProjectModule projectModule) {
        try {
            Assert.hasText(projectModule.getProjectCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(projectModule.getModuleCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());

            projectModuleSV.save(projectModule);
            return BeanRet.create(true, "创建账户成功", projectModule);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "创建账户失败");
        }
    }
}
