package com.rzhkj.project.ctrl;

import com.alibaba.fastjson.JSON;
import com.rzhkj.core.base.BaseCtrl;
import com.rzhkj.core.entity.BeanRet;
import com.rzhkj.core.entity.Page;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.project.entity.ProjectRepositoryAccount;
import com.rzhkj.project.service.ProjectRepositoryAccountSV;
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
 * 3.创建账户
 * 4.修改
 * 5.删除
 *
 * @author lixin
 */
@Controller
@RequestMapping("/project/repository")
@Api(value = "代码仓库账户管理控制器", description = "代码仓库账户管理控制器")
public class ProjectRepositoryAccountCtrl extends BaseCtrl {

    @Resource
    private ProjectRepositoryAccountSV projectRepositoryAccountSV;

    /**
     * 查询一个详情信息
     *
     * @param code 版本管理编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "版本管理编码", paramType = "query")
    })
    @GetMapping(value = "/load")
    @ResponseBody
    public BeanRet load(String code) {
        try {
            Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());

            Map<String, Object> map = new HashedMap();
            map.put("code", code);
            ProjectRepositoryAccount projectRepositoryAccount = projectRepositoryAccountSV.load(map);

            logger.info(JSON.toJSONString(projectRepositoryAccount));
            return BeanRet.create(true, "查询一个详情信息", projectRepositoryAccount);
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
            @ApiImplicitParam(name = "projectCode", value = "项目编码", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(@ApiIgnore Page<ProjectRepositoryAccount> page) {
        try {
            Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());

            page = projectRepositoryAccountSV.getList(page);
            int count = projectRepositoryAccountSV.count(new HashedMap());
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
     * 创建账户
     *
     * @return BeanRet
     */
    @ApiOperation(value = "创建账户", notes = "创建账户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "帐户名 最长32个汉字", required = true, paramType = "query"),
            @ApiImplicitParam(name = "projectCode", value = "项目编码", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码 最长32个英文字符", required = true, paramType = "query"),
            @ApiImplicitParam(name = "home", value = "仓库地址", required = true, paramType = "query"),
            @ApiImplicitParam(name = "description", value = "仓库说明 最长128个汉字", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "仓库类型:Git, Svn", required = true, paramType = "query")
    })
    @PostMapping("/build")
    @ResponseBody
    public BeanRet build(@ApiIgnore ProjectRepositoryAccount projectRepositoryAccount) {
        try {
            Assert.hasText(projectRepositoryAccount.getAccount(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(projectRepositoryAccount.getPassword(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(projectRepositoryAccount.getHome(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(projectRepositoryAccount.getDescription(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(projectRepositoryAccount.getType(), BaseException.BaseExceptionEnum.Empty_Param.toString());

            projectRepositoryAccountSV.save(projectRepositoryAccount);
            return BeanRet.create(true, "创建账户成功", projectRepositoryAccount);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "创建账户失败");
        }
    }


    /**
     * 修改
     *
     * @return BeanRet
     */
    @ApiOperation(value = "修改", notes = "修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "版本管理编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "projectCode", value = "项目编码", paramType = "query"),
            @ApiImplicitParam(name = "account", value = "帐户名 最长32个汉字", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码 最长32个英文字符", required = true, paramType = "query"),
            @ApiImplicitParam(name = "home", value = "仓库地址", required = true, paramType = "query"),
            @ApiImplicitParam(name = "description", value = "仓库说明 最长128个汉字", required = true, paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态：停用[Disenable]，启用[Enable]", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "仓库类型:Git, Svn", required = true, paramType = "query")
    })
    @PutMapping("/modify")
    @ResponseBody
    public BeanRet modify(@ApiIgnore ProjectRepositoryAccount projectRepositoryAccount) {
        try {
            Assert.hasText(projectRepositoryAccount.getCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());

            projectRepositoryAccountSV.saveOrUpdate(projectRepositoryAccount);
            return BeanRet.create(true, "修改项目成功", projectRepositoryAccount);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "修改项目失败");
        }
    }

    /**
     * 删除
     *
     * @return BeanRet
     */
    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "版本管理编码", required = true, paramType = "query")
    })
    @DeleteMapping("/delete")
    @ResponseBody
    public BeanRet delete(String code) {
        try {
            Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());

            projectRepositoryAccountSV.delete(code);
            return BeanRet.create(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "删除失败");
        }
    }


}
