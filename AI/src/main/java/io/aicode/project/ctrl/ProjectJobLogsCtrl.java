package io.aicode.project.ctrl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import io.aicode.base.BaseCtrl;
import io.aicode.base.core.BeanRet;
import io.aicode.base.core.entity.Page;
import io.aicode.base.exceptions.BaseException;
import io.aicode.project.entity.ProjectJobLogs;
import io.aicode.project.service.ProjectJobLogsSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 项目任务日志管理控制器
 * 1.查询项目任务信息集合
 *
 * @author lixin
 */
@Controller
@RequestMapping("/project/job/logs")
@Api(value = "项目任务管理控制器", description = "项目任务管理控制器")
public class ProjectJobLogsCtrl extends BaseCtrl {

    @Resource
    private ProjectJobLogsSV projectJobLogsSV;


    /**
     * 查询项目任务信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询项目任务信息集合", notes = "查询项目任务信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "任务编码", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(String code, @ApiIgnore Page<ProjectJobLogs> page) {
        try {
            Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Map<String, Object> map = Maps.newHashMap();
            map.put("code", code);
            page.setParams(map);
//            page.setPageSize(500);
            page = projectJobLogsSV.getList(page);
            int count = projectJobLogsSV.count(map);
            page.setTotalRow(count);

            logger.info(JSON.toJSONString(page));
            return BeanRet.create(true, "", page);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create("未查到需要的内容");
        }
    }

}
