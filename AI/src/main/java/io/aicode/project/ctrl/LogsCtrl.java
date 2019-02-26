package io.aicode.project.ctrl;

import io.aicode.core.base.BaseCtrl;
import io.aicode.core.entity.BeanRet;
import io.aicode.project.service.LogsSV;
import io.aicode.setting.service.SettingSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;


/**
 * 项目管理控制器

 *
 * @author lixin
 */
@Controller
@RequestMapping("/logs")
@Api(value = "日志控制器", description = "日志控制器")
public class LogsCtrl extends BaseCtrl {

    @Resource
    private SettingSV settingSV;

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
    @ResponseBody
    public BeanRet createLogFiles(String projectCode) {
        String path = logsSV.createLogFiles(projectCode);
        return BeanRet.create(true, "成功", path);
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
    @ResponseBody
    public BeanRet saveLogs(String logs, String path) {
        Boolean result = logsSV.saveLogs(logs, path);
        if(result) {
            return BeanRet.create(true, "成功");
        } else {
            return BeanRet.create(false, "失败");
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
    @PostMapping("/loadFilePath")
    @ResponseBody
    public BeanRet loadFilePath(String projectCode) {
        String path = logsSV.loadFilePath(projectCode);
        return BeanRet.create(true, "成功", path);
    }


    /**
     * 查看文件信息
     *
     * @return BeanRet
     */
    @ApiOperation(value = "查看文件信息", notes = "查看文件信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filePath", value = "项目code", required = true, paramType = "query"),
    })
    @PostMapping("/scanPath")
    @ResponseBody
    public BeanRet scanPath(String filePath) {
        return logsSV.scanPath(filePath);
    }


}
