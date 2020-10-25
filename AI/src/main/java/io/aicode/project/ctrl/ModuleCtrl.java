package io.aicode.project.ctrl;

import com.alibaba.fastjson.JSON;
import io.aicode.base.BaseCtrl;
import io.aicode.base.core.BeanRet;
import io.aicode.base.exceptions.BaseException;
import io.aicode.base.tools.Page;
import io.aicode.project.entity.Module;
import io.aicode.project.service.ModuleSV;
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
 * 第三方模块控制器
 * 1.查询一个详情信息
 * 2.查询信息集合
 * 3.添加
 * 4.修改
 * 5.删除
 *
 * @author lixin
 */
@Controller
@RequestMapping("/Module")
@Api(value = "第三方模块控制器", description = "第三方模块控制器")
public class ModuleCtrl extends BaseCtrl {

    @Resource
    private ModuleSV moduleSV;

    /**
     * 查询一个详情信息
     *
     * @param code 模板编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "模块编码", required = true, paramType = "query")
    })
    @GetMapping(value = "/load")
    @ResponseBody
    public BeanRet load(String code) {
        try {
            Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());

            Map<String, Object> map = new HashedMap();
            map.put("code", code);
            Module module = moduleSV.load(map);

            logger.info(JSON.toJSONString(module));
            return BeanRet.create(true, "查询一个详情信息", module);
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
    public BeanRet list(@ApiIgnore Page<Module> page) {
        try {
            Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());

            page = moduleSV.getList(page);
            int count = moduleSV.count(null);
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
            @ApiImplicitParam(name = "name", value = "模块名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "description", value = "模块说明", required = true, paramType = "query")
    })
    @PostMapping("/build")
    @ResponseBody
    public BeanRet build(@ApiIgnore Module module) {
        try {
            Assert.hasText(module.getName(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(module.getDescription(), BaseException.BaseExceptionEnum.Empty_Param.toString());

            moduleSV.save(module);
            return BeanRet.create(true, "添加成功", module);
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
    public BeanRet modify(@ApiIgnore Module module) {
        try {
            Assert.hasText(module.getName(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            moduleSV.saveOrUpdate(module);
            return BeanRet.create(true, "修改成功", module);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "修改失败");
        }
    }

}
