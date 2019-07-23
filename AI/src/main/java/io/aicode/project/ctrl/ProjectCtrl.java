package io.aicode.project.ctrl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import io.aicode.base.BaseCtrl;
import io.aicode.base.JwtToken;
import io.aicode.base.core.BeanRet;
import io.aicode.base.enums.YNEnum;
import io.aicode.base.exceptions.BaseException;
import io.aicode.base.tools.Constants;
import io.aicode.base.tools.FileUtil;
import io.aicode.base.tools.HandleFuncs;
import io.aicode.base.tools.Page;
import io.aicode.display.facade.DisplayAttributeSV;
import io.aicode.project.entity.Project;
import io.aicode.project.service.MapRelationshipSV;
import io.aicode.project.service.ProjectSV;
import io.aicode.setting.entity.Setting;
import io.aicode.setting.service.SettingSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;


/**
 * 项目管理控制器
 * 1.查询一个详情信息
 * 2.查询项目信息集合
 * 3.创建项目
 * 4.修改项目
 * 5.删除项目
 * 6.执行脚本
 *
 * @author lixin
 */
@Controller
@RequestMapping("/project")
@Api(value = "项目管理控制器", description = "项目管理控制器")
public class ProjectCtrl extends BaseCtrl {

    @Resource
    private ProjectSV projectSV;
    @Resource
    private SettingSV settingSV;

    @Resource
    private DisplayAttributeSV displayAttributeSV;

    @Resource
    private MapRelationshipSV mapRelationshipSV;

    /**
     * 查询一个详情信息
     *
     * @param code 项目编码
     * @return BeanRet
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "项目编码", paramType = "query")
    })
    @GetMapping(value = "/load")
    @ResponseBody
    public BeanRet load(String code) {
        Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Map<String, Object> map = new HashedMap();
        map.put("code", code);
        Project project = projectSV.load(map);
        //查询表关系
        int count = mapRelationshipSV.countByProjectCode(code);
        //查询显示属性
        int displayCount = displayAttributeSV.countByProjectCode(code);
        if (count > 0 || displayCount > 0) {
            project.setRelationshipAndDisplay(YNEnum.Y.name());
        } else {
            project.setRelationshipAndDisplay(YNEnum.N.name());
        }
        logger.info(JSON.toJSONString(project));
        return BeanRet.create(true, "查询一个详情信息", project);
    }


    /**
     * 查询文件路径
     *
     * @param code     项目编码
     * @param filePath 文件路径
     * @return BeanRet
     */
    @ApiOperation(value = "查询文件路径", notes = "查询文件路径")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "项目编码", paramType = "query")
    })
    @GetMapping(value = "/scan/path")
    @ResponseBody
    public BeanRet scanPath(String code, String filePath) throws IOException {
        Assert.hasText(filePath, BaseException.BaseExceptionEnum.Empty_Param.toString());
        logger.info(filePath);

        Map<String, Object> map = new HashedMap();
        map.put("code", code);
        Project project = projectSV.load(map);
        String workspace = settingSV.load(Setting.Key.Workspace);

        filePath = new HandleFuncs().getCurrentClassPath() + workspace + filePath;
        logger.info(filePath);

        File file = new File(filePath);
        if (file.isDirectory()) {
            List<Map<String, String>> mapList = FileUtil.sanDirFiles(filePath, project.getEnglishName());
            return BeanRet.create(true, "查询一个详情信息", mapList);
        }

        if (file.isFile() && !filePath.contains(".jar")) {
            String fileStr = FileUtils.readFileToString(new File(filePath), "UTF-8");
            logger.info(fileStr);
            return BeanRet.create(true, "查询一个详情信息", fileStr);
        }
        return BeanRet.create(true, "", filePath.replaceAll("/\\w*\\.jar", ""));
    }


    /**
     * 查询项目信息集合 按照时间顺序倒叙排序
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询项目信息集合 按照时间顺序倒叙排序", notes = "查询项目信息集合 按照时间顺序倒叙排序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @GetMapping(value = "/list")
    @ResponseBody
    public BeanRet list(@ApiIgnore Page<Project> page, String token) {
        Assert.notNull(page, BaseException.BaseExceptionEnum.Empty_Param.toString());
        String accountCode = JwtToken.getTokenValue(token, Constants.AccountCode.val.toString());
        Map<String, Object> map = Maps.newHashMap();
        map.put("accountCode", accountCode);
        page.setParams(map);
        page = projectSV.getList(page);
        int count = projectSV.count(map);
        page.setTotalRow(count);
        logger.info(JSON.toJSONString(page));
        return BeanRet.create(true, "", page);
    }

    /**
     * 创建项目
     *
     * @return BeanRet
     */
    @ApiOperation(value = "创建项目", notes = "创建项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "项目名 最长128个汉字", required = true, paramType = "query"),
            @ApiImplicitParam(name = "englishName", value = "项目英文名 （项目数据库名） 最长256个英文字符", required = true, paramType = "query"),
            @ApiImplicitParam(name = "description", value = "项目描述 最长256个汉字", required = true, paramType = "query"),
            @ApiImplicitParam(name = "databaseType", value = "数据库类型:Mysql,Oracle...", required = true, paramType = "query"),
            @ApiImplicitParam(name = "language", value = "项目语言:Java,Python,Js...", required = true, paramType = "query"),
            @ApiImplicitParam(name = "copyright", value = "项目版权文字信息", required = true, paramType = "query"),
            @ApiImplicitParam(name = "author", value = "作者", required = true, paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "联系方式", required = true, paramType = "query"),
            @ApiImplicitParam(name = "basePackage", value = "项目基础包名", required = true, paramType = "query")
    })
    @PostMapping("/build")
    @ResponseBody
    public BeanRet build(@ApiIgnore Project project, String token) {
        Assert.hasText(project.getName(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(project.getEnglishName(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(project.getAuthor(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(project.getBasePackage(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(project.getCopyright(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(project.getDatabaseType(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(project.getPhone(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(project.getLanguage(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        String accountCode = JwtToken.getTokenValue(token, Constants.AccountCode.val.toString());
        project.setAccountCode(accountCode);
        projectSV.build(project);
        return BeanRet.create(true, "创建项目成功", project);
    }


    /**
     * 修改项目
     *
     * @return BeanRet
     */
    @ApiOperation(value = "修改项目", notes = "修改项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "项目编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "name", value = "项目名 最长128个汉字", paramType = "query"),
            @ApiImplicitParam(name = "englishName", value = "项目英文名 （项目数据库名） 最长256个英文字符", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "项目描述 最长256个汉字", paramType = "query"),
            @ApiImplicitParam(name = "databaseType", value = "数据库类型:Mysql,Oracle...", paramType = "query"),
            @ApiImplicitParam(name = "language", value = "项目语言:Java,Python,Js...", paramType = "query"),
            @ApiImplicitParam(name = "copyright", value = "项目版权文字信息", paramType = "query"),
            @ApiImplicitParam(name = "author", value = "作者", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "联系方式", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "项目状态", paramType = "query"),
            @ApiImplicitParam(name = "basePackage", value = "项目基础包名", paramType = "query")
    })
    @PutMapping("/modify")
    @ResponseBody
    public BeanRet modify(@ApiIgnore Project project) {
        Assert.hasText(project.getCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        if (project.getCode() != null) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("code", project.getCode());
            Project projectLoad = projectSV.load(map);
            if (projectLoad != null) {
                project.setId(projectLoad.getId());
            }
            map.clear();
            map.put("englishName", project.getEnglishName());
            projectLoad = projectSV.load(map);
            if (projectLoad != null && !projectLoad.getCode().equals(project.getCode())) {
                return BeanRet.create(false, "修改项目失败,已经存在相同名称");
            }
            projectSV.saveOrUpdate(project);
        }
        return BeanRet.create(true, "修改项目成功", project);
    }

    /**
     * 删除项目
     *
     * @return BeanRet
     */
    @ApiOperation(value = "删除项目", notes = "删除项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "项目编码", required = true, paramType = "query")
    })
    @DeleteMapping("/delete")
    @ResponseBody
    public BeanRet delete(String code) {
        Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
        projectSV.delete(code);
        return BeanRet.create(true, "修改项目成功");
    }


    /**
     * 初始化
     *
     * @return BeanRet
     */
    @ApiOperation(value = "执行脚本", notes = "执行脚本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "项目编码", required = true, paramType = "query")
    })
    @PutMapping("/init")
    @ResponseBody
    public BeanRet init(String code) {
        Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
        projectSV.execute(code);
        return BeanRet.create(true, "执行脚本成功");
    }

    @GetMapping("/download/{proejctName}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "proejctName", value = "项目名", required = true, paramType = "path")
    })
    public void downloadFile(@PathVariable("proejctName") String proejctName, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(proejctName)) {
            return;
        }

        String fileName = proejctName + ".zip";// 设置文件名，根据业务需要替换成要下载的文件名
        if (fileName != null) {
            ClassPathResource resource = new ClassPathResource("repository/" + fileName);
            response.setContentType("application/force-download");// 设置强制下载不打开
            fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
            byte[] buffer = new byte[1024];
            try (InputStream inputStream = resource.getInputStream();
                 BufferedInputStream bis = new BufferedInputStream(inputStream)) {
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            }
        }
    }


    /**
     * 进入首页
     *
     * @return index页面
     */
    @GetMapping(value = "/index")
    public String index() {
        return "/index";
    }

}
