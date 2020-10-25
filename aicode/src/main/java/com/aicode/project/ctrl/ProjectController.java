/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;

import com.aicode.core.entity.Page;
import com.aicode.core.entity.R;
import com.aicode.core.enums.Constants;
import com.aicode.core.enums.YNEnum;
import com.aicode.core.exceptions.BaseException;
import com.aicode.core.tools.FileUtil;
import com.aicode.core.tools.JwtToken;
import com.aicode.display.service.DisplayAttributeService;
import com.aicode.map.service.MapRelationshipService;
import com.aicode.project.entity.*;
import com.aicode.project.service.*;
import com.aicode.project.vo.ProjectVO;
import com.aicode.setting.entity.SettingKey;
import com.aicode.setting.service.SettingService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * 项目信息
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/project")
@Slf4j
@Api(value = "项目信息控制器", tags = "项目信息控制器")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SettingService settingService;

    @Autowired
    private ProjectFramworkService projectFramworkService;

    @Autowired
    private ProjectMapService projectMapService;

    @Autowired
    private ProjectModuleService projectModuleService;

    @Autowired
    private ProjectJobService projectJobService;

    @Autowired
    private ProjectSqlService projectSqlService;

    @Autowired
    private ProjectRepositoryAccountService projectRepositoryAccountService;

    @Autowired
    private DisplayAttributeService displayAttributeService;

    @Autowired
    private MapRelationshipService mapRelationshipService;

    /**
     * 初始化
     *
     * @return BeanRet
     */
    @ApiOperation(value = "执行脚本", notes = "执行脚本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "项目编码", required = true, paramType = "query")
    })
    @PostMapping("/init")
    public R init(String code) {
        Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
        projectService.execute(code);
        return R.success();
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
            String repositoryPath = settingService.load(SettingKey.Repository_Path, String.class);
            response.setContentType("application/force-download");// 设置强制下载不打开
            fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fileInputStream = new FileInputStream(new File(repositoryPath + fileName));
            try (InputStream inputStream = fileInputStream;
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
    public R load(String code) {
        Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Project project = projectService.getOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getCode, code));

        List<ProjectFramwork> projectFramworks = projectFramworkService.list(new LambdaQueryWrapper<ProjectFramwork>()
                .eq(ProjectFramwork::getProjectCode, project.getCode()));
        project.setProjectFramworkList(projectFramworks);

        List<ProjectJob> projectJobs = projectJobService.list(new LambdaQueryWrapper<ProjectJob>()
                .eq(ProjectJob::getProjectCode, project.getCode()));
        project.setProjectJobList(projectJobs);

        List<ProjectMap> projectMaps = projectMapService.list(new LambdaQueryWrapper<ProjectMap>()
                .eq(ProjectMap::getProjectCode, project.getCode()));
        project.setProjectMapList(projectMaps);

        List<ProjectModule> projectModules = projectModuleService.list(new LambdaQueryWrapper<ProjectModule>()
                .eq(ProjectModule::getProjectCode, project.getCode()));
        project.setProjectModuleList(projectModules);

        List<ProjectRepositoryAccount> projectRepositoryAccounts = projectRepositoryAccountService.list(new LambdaQueryWrapper<ProjectRepositoryAccount>()
                .eq(ProjectRepositoryAccount::getProjectCode, project.getCode()));
        project.setProjectRepositoryAccountList(projectRepositoryAccounts);

        List<ProjectSql> projectSqls = projectSqlService.list(new LambdaQueryWrapper<ProjectSql>()
                .eq(ProjectSql::getProjectCode, project.getCode()));
        project.setProjectSqlList(projectSqls);

        //查询表关系
        int count = mapRelationshipService.countByProjectCode(code);

        //查询显示属性
        int displayCount = displayAttributeService.countByProjectCode(code);
        if (count > 0 || displayCount > 0) {
            project.setRelationshipAndDisplay(YNEnum.Y.name());
        } else {
            project.setRelationshipAndDisplay(YNEnum.N.name());
        }
        log.info(JSON.toJSONString(project));
        return R.success(project);
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
    public R scanPath(String code, String filePath) throws IOException {
        Assert.hasText(filePath, BaseException.BaseExceptionEnum.Empty_Param.toString());
        log.info(filePath);
        //自动过滤ace冗余路路径的问题
        if (filePath.contains("ace/")) {
            filePath = filePath.replace("ace/", "/").replace("//", "/");
        }

        Project project = projectService.getOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getCode, code));
        String workspace = settingService.load(SettingKey.Workspace);

        filePath = workspace + filePath;
        log.info(filePath);

        File file = new File(filePath);
        if (file.isDirectory()) {
            List<Map<String, String>> mapList = FileUtil.sanDirFiles(filePath, project.getEnglishName());
            return R.success(mapList);
        }

        if (file.isFile() && !filePath.contains(".jar")) {
            String fileStr = FileUtils.readFileToString(new File(filePath), "UTF-8");
            log.info(fileStr);
            return R.success(fileStr);
        }
        return R.success(filePath.replaceAll("/\\w*\\.jar", ""));
    }


    /**
     * 创建 项目信息
     *
     * @return R
     */
    @ApiOperation(value = "创建Project", notes = "创建Project")
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
    public R build(@ApiIgnore Project project, String token) {
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

        projectService.save(project);


        return R.success(project);
    }


    /**
     * 根据条件code查询项目信息一个详情信息
     *
     * @param code 项目编码
     * @return ProjectVO
     */
    @ApiOperation(value = "创建Project", notes = "创建Project")
    @GetMapping("/load/code/{code}")
    public ProjectVO loadByCode(@PathVariable java.lang.String code) {
        if (code == null) {
            return null;
        }
        Project project = projectService.getOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getCode, code));
        ProjectVO projectVO = new ProjectVO();
        BeanUtils.copyProperties(project, projectVO);
        log.debug(JSON.toJSONString(projectVO));
        return projectVO;
    }

    /**
     * 查询项目信息信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询Project信息集合", notes = "查询Project信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query")
    })
    @GetMapping(value = "/list")
    public R list(String token, Integer curPage, Integer pageSize) {
        String accountCode = JwtToken.getTokenValue(token, Constants.AccountCode.val.toString());

        Page<Project> page = new Page<>(pageSize, curPage);
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        if (accountCode != null) {
            queryWrapper.lambda().gt(Project::getAccountCode, accountCode);
        }

        int total = projectService.count(queryWrapper);
        if (total > 0) {
            List<Project> projectList = projectService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            page.setTotalRow(total);
            page.setVoList(projectList);
            log.debug(JSON.toJSONString(page));
        }
        return R.success(page);
    }


    /**
     * 修改 项目信息
     *
     * @return R
     */
    @ApiOperation(value = "修改Project", notes = "修改Project")
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
    public R modify(@ApiIgnore Project project) {
        Assert.hasText(project.getCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        if (project.getCode() != null) {
            Project projectLoad = projectService.getOne(new LambdaQueryWrapper<Project>()
                    .eq(Project::getCode, project.getCode()));
            if (projectLoad != null) {
                project.setId(projectLoad.getId());
            }

            projectLoad = projectService.getOne(new LambdaQueryWrapper<Project>()
                    .eq(Project::getEnglishName, project.getEnglishName()));
            if (projectLoad != null && !projectLoad.getCode().equals(project.getCode())) {
                return R.failed(BaseException.BaseExceptionEnum.Build_Exist);
            }
            projectService.saveOrUpdate(project);
        }
        return R.success(project);
    }


    /**
     * 删除 项目信息
     *
     * @return R
     */
    @ApiOperation(value = "删除Project", notes = "删除Project")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "项目编码", required = true, paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore ProjectVO projectVO) {
        Project newProject = new Project();
        BeanUtils.copyProperties(projectVO, newProject);
        projectService.delete(projectVO.getCode());
        return R.success("删除成功");
    }

}
