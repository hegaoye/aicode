package io.aicode.project.ctrl;

import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Maps;
import io.aicode.base.BaseCtrl;
import io.aicode.base.core.BeanRet;
import io.aicode.base.exceptions.BaseException;
import io.aicode.project.entity.ProjectSql;
import io.aicode.project.service.ProjectSV;
import io.aicode.project.service.ProjectSqlSV;
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
import java.util.List;
import java.util.Map;


/**
 * 项目sql管理控制器
 * 1.查询一个详情信息
 * 2.查询项目sql信息集合
 * 3.创建项目sql
 * 4.修改项目sql
 * 5.删除项目sql
 *
 * @author lixin
 */
@Controller
@RequestMapping("/project/sql")
@Api(value = "项目sql管理控制器", description = "项目sql管理控制器")
public class ProjectSqlCtrl extends BaseCtrl {

    @Resource
    private ProjectSqlSV projectSqlSV;

    @Resource
    private ProjectSV projectSV;

    /**
     * 查询一个详情信息
     *
     * @return BeanRet
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "sql编码", paramType = "query"),
            @ApiImplicitParam(name = "projectCode", value = "项目编码", paramType = "query")
    })
    @GetMapping(value = "/load")
    @ResponseBody
    public BeanRet load(String code, String projectCode) {
        try {
            Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Map<String, Object> map = new HashedMap();
            map.put("code", code);
            map.put("projectCode", projectCode);
            ProjectSql projectSql = projectSqlSV.load(map);
            logger.info(JSON.toJSONString(projectSql));
            return BeanRet.create(true, "查询一个详情信息", projectSql);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create("未查到需要的内容");
        }
    }


    /**
     * 查询项目sql信息集合 按照时间顺序倒叙排序
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询项目sql信息集合 按照时间顺序倒叙排序", notes = "查询项目sql信息集合 按照时间顺序倒叙排序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query")
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(String projectCode) {
        try {
            Assert.notNull(projectCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Map<String, Object> map = Maps.newHashMap();
            map.put("projectCode", projectCode);
            List<ProjectSql> projectSqlList = projectSqlSV.queryList(map);

            logger.info(JSON.toJSONString(projectSqlList));
            return BeanRet.create(true, "", projectSqlList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create("未查到需要的内容");
        }
    }

    /**
     * 创建项目sql
     *
     * @return BeanRet
     */
    @ApiOperation(value = "创建项目sql", notes = "创建项目sql")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "tsql", value = "sql脚本", required = true, paramType = "query")
    })
    @PostMapping("/build")
    @ResponseBody
    public BeanRet build(@ApiIgnore ProjectSql projectSql) {
        try {
            Assert.hasText(projectSql.getProjectCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(projectSql.getTsql(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            projectSqlSV.save(projectSql);
            return BeanRet.create(true, "创建项目成功", projectSql);
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
            @ApiImplicitParam(name = "code", value = "tsql编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "tsql", value = "sql脚本", required = true, paramType = "query")
    })
    @PutMapping("/modify")
    @ResponseBody
    public BeanRet modify(@ApiIgnore ProjectSql projectSql) {
        try {
            Assert.hasText(projectSql.getCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Map<String, Object> map = Maps.newHashMap();
            map.put("code", projectSql.getCode());
            ProjectSql projectSqlLoad = projectSqlSV.load(map);
            if (projectSqlLoad != null) {
                projectSqlSV.delete(projectSql.getCode());
                projectSqlLoad.setTsql(projectSql.getTsql());
                projectSqlSV.save(projectSqlLoad);
                return BeanRet.create(true, "修改项目成功", projectSql);
            } else {
                return BeanRet.create(false, "修改项目失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "修改项目失败");
        }
    }

    /**
     * 删除项目sql`
     *
     * @return BeanRet
     */
    @ApiOperation(value = "删除项目sql", notes = "删除项目sql")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "tsql编码", required = true, paramType = "query")
    })
    @DeleteMapping("/delete")
    @ResponseBody
    public BeanRet delete(String code) {
        try {
            Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());

            projectSqlSV.delete(code);
            return BeanRet.create(true, "删除项目sql成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "删除项目sql失败");
        }
    }


}
