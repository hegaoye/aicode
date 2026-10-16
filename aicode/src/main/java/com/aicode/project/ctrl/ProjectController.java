/*
 * aicode
 */
package com.aicode.project.ctrl;

import com.aicode.core.BaseException;
import com.aicode.core.PageVO;
import com.aicode.core.R;
import com.aicode.core.enums.Constants;
import com.aicode.core.enums.YNEnum;
import com.aicode.core.tools.FileUtil;
import com.aicode.core.tools.JwtToken;
import com.aicode.display.service.DisplayAttributeService;
import com.aicode.frameworks.entity.Frameworks;
import com.aicode.frameworks.service.FrameworksService;
import com.aicode.map.service.MapRelationshipService;
import com.aicode.project.entity.*;
import com.aicode.project.service.*;
import com.aicode.project.vo.ProjectDetailVO;
import com.aicode.project.vo.ProjectSummaryVO;
import com.aicode.setting.entity.SettingKey;
import com.aicode.setting.service.SettingService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 项目信息
 *
 * @author aicode
 */
@Slf4j
@RestController
@RequestMapping("/project")
@Tag(name = "项目信息控制器", description = "项目信息控制器")
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
    private FrameworksService frameworksService;
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

    @Operation(summary = "执行脚本", description = "执行脚本")
    @Parameters({
            @Parameter(name = "code", description = "项目编码", required = true)
    })
    @PostMapping("/init")
    public R<Void> init(String code) {
        Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
        projectService.execute(code);
        return R.success();
    }


    /**
     * 下载项目源码（生产环境核心端点——用户构建后必须能下载生成的 zip 包）。
     * <p>@deprecated 前端未直接调用；保留作为兜底（dev 分支亦保留此端点）。
     * 如未来确认无引用，可通过 {@code openspec} change 流程删除。</p>
     */
    @GetMapping("/download/{projectName}")
    @Operation(summary = "下载项目源码", description = "下载项目源码")
    @Parameters({
            @Parameter(name = "projectName", description = "项目名", required = true)
    })
    @Deprecated
    public void downloadFile(@PathVariable("projectName") String projectName, HttpServletResponse response) throws Exception {
        if (org.apache.commons.lang3.StringUtils.isBlank(projectName)) {
            return;
        }

        String fileName = projectName + ".zip";
        log.info("下载文件名-{}", fileName);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(fileName)) {
            String repositoryPath = settingService.load(SettingKey.Repository_Path, String.class);
            log.info("zip包在服务器的位置-{}", repositoryPath);
            response.setContentType("application/force-download");
            fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
            byte[] buffer = new byte[1024];
            fileName = repositoryPath + fileName;
            log.info("下载完整路径-{}", fileName);
            FileInputStream fileInputStream = new FileInputStream(new File(fileName));
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
     * 查询一个详情信息
     *
     * @param code 项目编码
     * @return BeanRet
     */
    @Operation(summary = "查询一个详情信息", description = "查询一个详情信息")
    @Parameters({
            @Parameter(name = "code", description = "项目编码")
    })
    @GetMapping(value = "/load")
    public R<Project> load(String code) {
        Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
        Project project = projectService.getOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getCode, code));

        List<ProjectFramwork> projectFramworks = projectFramworkService.list(new LambdaQueryWrapper<ProjectFramwork>()
                .eq(ProjectFramwork::getProjectCode, project.getCode()));
        projectFramworks.forEach(projectFramwork -> {
            Frameworks frameworks = frameworksService.getOne(new LambdaQueryWrapper<Frameworks>().eq(Frameworks::getCode, projectFramwork.getFrameworkCode()));
            projectFramwork.setFrameworks(frameworks);
        });
        project.setProjectFramworkList(projectFramworks);

        List<ProjectJob> projectJobs = projectJobService.list(new LambdaQueryWrapper<ProjectJob>()
                .eq(ProjectJob::getProjectCode, project.getCode())
                .orderByDesc(ProjectJob::getCreateTime));
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


    @Operation(summary = "查询文件路径", description = "查询文件路径")
    @Parameters({
            @Parameter(name = "code", description = "项目编码"),
            @Parameter(name = "filePath", description = "文件路径")
    })
    @GetMapping(value = "/scan/path")
    public R<Object> scanPath(String code, String filePath) throws IOException {
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


    @Operation(summary = "创建Project", description = "创建Project")
    @Parameters({
            @Parameter(name = "name", description = "项目名 最长128个汉字", required = true),
            @Parameter(name = "englishName", description = "项目英文名 （项目数据库名） 最长256个英文字符", required = true),
            @Parameter(name = "description", description = "项目描述 最长256个汉字", required = true),
            @Parameter(name = "databaseType", description = "数据库类型:Mysql,Oracle...", required = true),
            @Parameter(name = "language", description = "项目语言:Java,Python,Js...", required = true),
            @Parameter(name = "copyright", description = "项目版权文字信息", required = true),
            @Parameter(name = "author", description = "作者", required = true),
            @Parameter(name = "phone", description = "联系方式", required = true),
            @Parameter(name = "basePackage", description = "项目基础包名", required = true)
    })
    @PostMapping("/build")
    public R<Project> build(@Parameter(hidden = true) Project project, String token) {
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


    @Operation(summary = "查询Project信息集合", description = "查询Project信息集合")
    @Parameters({
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true)
    })
    @GetMapping(value = "/list")
    public R<PageVO<ProjectSummaryVO>> list(String token, Integer curPage, Integer pageSize) {
        PageVO<ProjectSummaryVO> pageVO = new PageVO<>();
        if (StringUtils.isBlank(token) || "null".equals(token)) {
            return R.success(pageVO);
        }

        String accountCode = JwtToken.getTokenValue(token, Constants.AccountCode.val.toString());
        List<Project> projectList = projectService.lambdaQuery()
                .eq(Project::getAccountCode, accountCode)
                .orderByDesc(Project::getCreateTime)
                .list();

        List<ProjectSummaryVO> records = new java.util.ArrayList<>(projectList.size());
        for (Project project : projectList) {
            records.add(toSummary(project));
        }

        pageVO.setTotalRow(projectList.size());
        pageVO.setRecords(records);
        log.debug(JSON.toJSONString(pageVO));

        return R.success(pageVO);
    }

    /**
     * 把持久化实体 {@link Project} 转换为列表出参视图对象 {@link ProjectSummaryVO}。
     *
     * 7 字段（code / name / englishName / state / createTime / updateTime /
     * accountCode）—— 列表场景只关心"哪一行、什么状态、谁拥有、何时建"。
     *
     * 逐字段显式赋值而非 {@link BeanUtils#copyProperties} —— 显式胜过隐式：
     * 任何字段重命名会在编译期暴露给维护者，避免悄悄漂移到不期望的字段
     * （参考 `display-attribute-vo-contract` 归档变更的同等做法）。
     */
    private ProjectSummaryVO toSummary(Project project) {
        ProjectSummaryVO vo = new ProjectSummaryVO();
        vo.setCode(project.getCode());
        vo.setName(project.getName());
        vo.setEnglishName(project.getEnglishName());
        vo.setState(project.getState());
        vo.setCreateTime(project.getCreateTime());
        return vo;
    }

    /**
     * 把持久化实体 {@link Project} 转换为详情出参视图对象 {@link ProjectDetailVO}。
     *
     * 12 字段（code / name / englishName / description / copyright / author /
     * phone / basePackage / buildNumber / createTime / updateTime /
     * accountCode）—— 详情页关注"项目名/描述/作者/路径/构建次数"，配置
     * 类字段（databaseType / language / state / isRepository 等）刻意移除。
     *
     * 返回 {@code target} 的入参形式让测试可复用同一映射函数；与 toSummary
     * 保持手工 setXxx 风格一致。
     */
    private ProjectDetailVO copyToDetail(Project project, ProjectDetailVO target) {
        target.setCode(project.getCode());
        target.setName(project.getName());
        target.setEnglishName(project.getEnglishName());
        target.setDescription(project.getDescription());
        target.setCopyright(project.getCopyright());
        target.setAuthor(project.getAuthor());
        target.setPhone(project.getPhone());
        target.setBasePackage(project.getBasePackage());
        target.setBuildNumber(project.getBuildNumber());
        target.setCreateTime(project.getCreateTime());
        target.setUpdateTime(project.getUpdateTime());
        target.setAccountCode(project.getAccountCode());
        return target;
    }


    /**
     * 按 code 加载项目 —— 单条 Project 的 R<ProjectVO> 形态。
     *
     * 详见 `openspec/changes/2026-10-18-project-load-by-code-vo-contract/`：
     * - 早返 null code 路径：返 `R.success(null)`（data 为 null），不再直返 `null`，
     *   与 `api-contract` line 82 "data 为空时 SHALL 以 null 呈现" 规约一致；
     * - code 不存在的路径：service.getOne 返 null → 顺手返 `R.success(null)`，
     *   避免 `BeanUtils.copyProperties(null, ...)` 在 Spring 6+ 抛 NPE（让前端区分
     *   "找不到" 与 "服务异常" —— 既有逻辑曾以 NPE→9999 掩盖两种语义）；
     * - 正常路径：装配 ProjectDetailVO 后 `R.success(projectDetail)`，让前端 M3-A
     *   `ProjectDetailPage` 走 `unwrapSuccess` 样板消费；
     * - M3-C：装配类字段 `databaseType` / `language` / `state` / `isRepository` 等
     *   在 detail 中也移除（详见 `2026-10-17-project-vo-trim`）；
     * - 鉴权：依赖全局 `LoginInterceptor`（按 `auth-account` R5，新接口禁止
     *   `?token=` URL 参数），调用方由 axios 拦截器注入 Authorization 头。
     */
    @Operation(summary = "按 code 加载项目", description = "按 code 加载项目")
    @GetMapping("/load/code/{code}")
    public R<ProjectDetailVO> loadByCode(@PathVariable String code) {
        if (code == null) {
            // 用 R.success() 无参版本：避免 null 走 R.success(BaseExceptionEnum) 重载
            // （后者实现是 baseExceptionEnum.code，在 null 下 NPE —— 见 common R.java:61）
            // 期望：data: null，code: "0000"，success: true
            return R.success();
        }
        Project project = projectService.getOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getCode, code));
        if (project == null) {
            // 找不到对应 Project：返 R.success()（data=null）而非走 NPE→9999 路径
            return R.success();
        }
        ProjectDetailVO projectDetailVO = new ProjectDetailVO();
        copyToDetail(project, projectDetailVO);
        log.debug(JSON.toJSONString(projectDetailVO));
        return R.success(projectDetailVO);
    }


    @Operation(summary = "修改Project", description = "修改Project")
    @Parameters({
            @Parameter(name = "code", description = "项目编码", required = true),
            @Parameter(name = "name", description = "项目名 最长128个汉字"),
            @Parameter(name = "englishName", description = "项目英文名 （项目数据库名） 最长256个英文字符"),
            @Parameter(name = "description", description = "项目描述 最长256个汉字"),
            @Parameter(name = "databaseType", description = "数据库类型:Mysql,Oracle..."),
            @Parameter(name = "language", description = "项目语言:Java,Python,Js..."),
            @Parameter(name = "copyright", description = "项目版权文字信息"),
            @Parameter(name = "author", description = "作者"),
            @Parameter(name = "phone", description = "联系方式"),
            @Parameter(name = "state", description = "项目状态"),
            @Parameter(name = "basePackage", description = "项目基础包名")
    })
    @RequestMapping("/modify")
    public R<Project> modify(@Parameter(hidden = true) Project project) {
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

    @Operation(summary = "删除Project", description = "删除Project")
    @Parameters({
            @Parameter(name = "code", description = "项目编码", required = true)
    })
    @PostMapping("/delete")
    public R<String> delete(@Parameter(hidden = true) String code) {
        // M3-C：原方法以 ProjectVO 为入参（用 projectVO.getCode() + BeanUtils.copyProperties
        // 全拷贝到 Project 实体），与 ProjectVO 实体全集拷贝形态一同被裁切。改用
        // String code 入参即可——这里实际只用 code 字段。
        projectService.delete(code);
        return R.success("删除成功");
    }

}
