package io.aicode.project.ctrl;

import io.aicode.core.base.BaseCtrl;
import io.aicode.core.entity.BeanRet;
import io.aicode.core.tools.FileUtil;
import io.aicode.core.tools.HandleFuncs;
import io.aicode.core.tools.StringTools;
import io.aicode.project.service.LogsSV;
import io.aicode.setting.entity.Setting;
import io.aicode.setting.service.SettingSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 模板控制器
 * 1.查询一个详情信息
 * 2.查询信息集合
 * 3.添加
 * 4.修改
 * 4.删除
 *
 * @author lixin
 */
@Controller
@RequestMapping("/template")
@Api(value = "模板控制器", description = "模板控制器")
public class TemplateCtrl extends BaseCtrl {

   @Resource
   SettingSV settingSV;

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
    public BeanRet createLogFiles( String projectCode) {
        String path = logsSV.createLogFiles(projectCode, new Date());
        return BeanRet.create(true, "成功", path);
    }

    /**
     * 创建模板
     *
     * @return BeanRet
     */
    @ApiOperation(value = "创建模板文件", notes = "创建模板文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filePath", value = "路径", required = true, paramType = "query"),
            @ApiImplicitParam(name = "fileName", value = "文件名", paramType = "query")
    })
    @PostMapping("/createFramework")
    @ResponseBody
    public BeanRet createFramework( String filePath, String fileName) {
        try {

          /*  String workspace = settingSV.load(Setting.Key.SandBox_Path);
            String path = new HandleFuncs().getCurrentClassPath() + workspace + filePath;
            FileUtil.createDir(path , fileName);
            //创建log文件
            String logName = filePath + ".log";
            FileUtil.createDir(path , logName);
            //保存日志
            LogTools.logMsg( Class.class.toString() + "已成功创建日志文件：" + logName, path  +"/"+ logName);
            LogTools.logMsg(Class.class.toString() +"已成功创建模板：" + filePath, path  +"/"+ logName);
            Log log = LogTools.realtimeShowLog(path  +"/"+ logName,0);
            System.out.print(log.getLogInfo());
            //保存数据*/
            return BeanRet.create(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "删除失败");
        }
    }

    /**
     * 保存数据
     *
     * @return BeanRet
     */
   /* @ApiOperation(value = "保存数据", notes = "保存数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filePath", value = "路径", required = true, paramType = "query"),
            @ApiImplicitParam(name = "content", value = "文件内容", paramType = "query"),
    })
    @PostMapping("/saveFileInfo")
    @ResponseBody
    public BeanRet saveFileInfo(String filePath, String content) {
        //生成目录
        String workspace = settingSV.load(Setting.Key.SandBox_Path);
        String path = new HandleFuncs().getCurrentClassPath() + workspace + filePath;
        System.out.print(path);
        FileOutputStream fop;
        File file;
        try {
            file = new File(path);
            fop = new FileOutputStream(file);
            OutputStreamWriter oStreamWriter = new OutputStreamWriter(fop, "utf-8");
            BufferedWriter writer = new BufferedWriter(oStreamWriter);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BeanRet.create(true, "保存数据");
    }*/

    /**
     * 查询文件路径
     *
     * @param filePath 文件路径
     * @return BeanRet
     */
    @ApiOperation(value = "查询文件路径", notes = "查询文件路径")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filePath", value = "文件地址", paramType = "query"),
            @ApiImplicitParam(name = "fileName", value = "文件名", paramType = "query")
    })
    @GetMapping(value = "/scan/path")
    @ResponseBody
    public BeanRet scanPath(String filePath, String fileName) throws IOException {
        logger.info(filePath);
        String workspace = settingSV.load(Setting.Key.SandBox_Path);
        String path = new HandleFuncs().getCurrentClassPath() + workspace;
        if (StringTools.isNotEmpty(filePath)) {
            path = path + filePath;
        }
        File file = new File(path);
        String subStr = fileName;
        if (StringTools.isEmpty(fileName)) {
            subStr = workspace;
        }
        if (file.isDirectory()) {
            List<Map<String, String>> mapList = FileUtil.sanDirFiles(path, subStr);
            return BeanRet.create(true, "查询一个详情信息", mapList);
        }

        if (file.isFile() && !filePath.contains(".jar")) {
            String fileStr = FileUtils.readFileToString(new File(path), "UTF-8");
            return BeanRet.create(true, "查询一个详情信息", fileStr);
        }
        return BeanRet.create(true, "", filePath.replaceAll("/\\w*\\.jar", ""));
    }

}
