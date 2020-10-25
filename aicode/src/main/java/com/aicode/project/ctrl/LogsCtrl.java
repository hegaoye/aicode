package com.aicode.project.ctrl;

import com.aicode.core.entity.R;
import com.aicode.core.exceptions.BaseException;
import com.aicode.project.service.LogsSV;
import com.aicode.setting.service.SettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;


/**
 * 项目管理控制器
 *
 * @author lixin
 */
@Slf4j
@RestController
@RequestMapping("/logs")
@Api(value = "日志控制器", tags = "日志控制器")
public class LogsCtrl {

    @Resource
    private SettingService settingSV;

    @Resource
    private LogsSV logsSV;

    /**
     * 创建日志文件
     *
     * @return BeanRet
     */
    @ApiOperation(value = "创建日志文件", notes = "创建日志文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目code", required = true, paramType = "query"),
    })
    @PostMapping("/createLogFiles")
    public R createLogFiles(String projectCode) {
        String path = logsSV.createLogFiles(projectCode, new Date());
        return R.success(path);
    }

    /**
     * 保存日志数据
     *
     * @return BeanRet
     */
    @ApiOperation(value = "保存日志数据", notes = "保存日志数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logs", value = "日志信息", required = true, paramType = "query"),
            @ApiImplicitParam(name = "path", value = "日志路径", required = true, paramType = "query"),
    })
    @PostMapping("/saveLogs")
    public R saveLogs(String logs, String path) {
        Boolean result = logsSV.saveLogs(logs, path);
        if (result) {
            return R.success();
        } else {
            return R.failed("");
        }
    }

    /**
     * 查询请求路径
     *
     * @return BeanRet
     */
    @ApiOperation(value = "查询请求路径", notes = "查询请求路径")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectCode", value = "项目code", required = true, paramType = "query"),
    })
    @GetMapping("/loadFilePath")
    public R loadFilePath(String projectCode) {
        String path = logsSV.loadFilePath(projectCode);
        return R.success(path);
    }


    /**
     * 查看文件信息
     *
     * @return BeanRet
     */
    @ApiOperation(value = "查看文件信息", notes = "查看文件信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "datetime", value = "时间", required = true, paramType = "query"),
            @ApiImplicitParam(name = "projectCode", value = "项目编码", required = true, paramType = "query"),
    })
    @GetMapping("/load")
    public R load(String projectCode, String datetime) {
        Assert.notNull(projectCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.notNull(datetime, BaseException.BaseExceptionEnum.Empty_Param.toString());
        return logsSV.scanPath(projectCode, datetime);
    }


}
