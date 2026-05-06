package com.aicode.project.ctrl;

import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.project.service.LogsSV;
import com.aicode.setting.service.SettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


/**
 * 项目管理控制器
 *
 * @author lixin
 */
@Slf4j
@RestController
@RequestMapping("/logs")
@Tag(name = "日志控制器", description = "日志控制器")
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
    @Operation(summary = "创建日志文件", description = "创建日志文件")
    @Parameters({
            @Parameter(name = "projectCode", description = "项目code", required = true),
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
    @Operation(summary = "保存日志数据", description = "保存日志数据")
    @Parameters({
            @Parameter(name = "logs", description = "日志信息", required = true),
            @Parameter(name = "path", description = "日志路径", required = true),
    })
    @PostMapping("/saveLogs")
    public R saveLogs(String logs, String path) {
        Boolean result = logsSV.saveLogs(logs, path);
        return R.success();
    }

    /**
     * 查询请求路径
     *
     * @return BeanRet
     */
    @Operation(summary = "查询请求路径", description = "查询请求路径")
    @Parameters({
            @Parameter(name = "projectCode", description = "项目code", required = true),
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
    @Operation(summary = "查看文件信息", description = "查看文件信息")
    @Parameters({
            @Parameter(name = "datetime", description = "时间", required = true),
            @Parameter(name = "projectCode", description = "项目编码", required = true),
    })
    @GetMapping("/load")
    public R load(String projectCode, String datetime) {
        log.info("projectCode:{} datetime:{}", projectCode, datetime);
        Assert.notNull(projectCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.notNull(datetime, BaseException.BaseExceptionEnum.Empty_Param.toString());
        return logsSV.scanPath(projectCode, datetime);
    }


}
