/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;

import com.alibaba.fastjson.JSON;
import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
import com.rzhkj.base.core.FreemarkerHelper;
import com.rzhkj.base.core.TemplateData;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.enums.YNEnum;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.ProjectJobException;
import com.rzhkj.core.tools.*;
import com.rzhkj.core.tools.redis.RedisKey;
import com.rzhkj.project.dao.*;
import com.rzhkj.project.entity.*;
import com.rzhkj.project.service.ProjectJobSV;
import com.rzhkj.setting.dao.SettingDAO;
import com.rzhkj.setting.entity.Setting;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Component
@Service
public class ProjectJobSVImpl extends BaseMybatisSVImpl<ProjectJob, Long> implements ProjectJobSV {

    @Resource
    private ProjectDAO projectDAO;
    @Resource
    private SettingDAO settingDAO;
    @Resource
    private ProjectCodeCatalogDAO projectCodeCatalogDAO;
    @Resource
    private ProjectJobDAO projectJobDAO;
    @Resource
    private ProjectRepositoryAccountDAO projectRepositoryAccountDAO;

    @Resource
    private ProjectJobLogsDAO projectJobLogsDAO;

    @Resource
    private MapClassTableDAO mapClassTableDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectJobDAO;
    }

    /**
     * 创建项目任务
     * 1.验证参数
     * 2.设置默认属性
     * 3.保存
     *
     * @param projectJob
     */
    @Override
    public void build(ProjectJob projectJob) {
        //1.验证参数
        if (projectJob == null || StringTools.isEmpty(projectJob.getProjectCode())) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectJobException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        //2.设置默认属性
        projectJob.setCode(String.valueOf(uidGenerator.getUID()));
        projectJob.setNumber(0);
        projectJob.setState(ProjectJobStateEnum.Create.name());
        projectJob.setCreateTime(new Date());
        //3.保存
        this.save(projectJob);
    }

    /**
     * 删除任务
     * 1.判断参数
     * 2.判断任务是否存在
     * 3.删除任务
     * 4.删除任务执行日志
     *
     * @param code 任务编码
     */
    @Override
    public void delete(String code) {
        //1.判断参数
        if (StringTools.isEmpty(code)) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectJobException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        //2.判断任务是否存在
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", code);
        ProjectJob projectJob = projectJobDAO.load(map);
        if (projectJob == null) {
            logger.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ProjectJobException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }
        //3.删除任务
        projectJobDAO.delete(code);

        //4.删除任务执行日志
        projectJobLogsDAO.delete(code);

    }

    /**
     * 执行任务
     * 1.创建项目
     * 2.获取类信息
     * 3.获取模板信息
     * 4.生成源码
     * 5.获取模块信息
     * 6.获取版本控制管理信息
     *
     * @param projectCode 项目编码
     */
    @Override
    public ProjectJob execute(String projectCode) {
        String execute = RedisKey.execute(projectCode);
        if (redisUtils.hasKey(execute)) {
            String json = redisUtils.get(execute).toString();
            return JSON.parseObject(json, ProjectJob.class);
        }

        //创建任务追踪
        ProjectJob projectJob = new ProjectJob();
        projectJob.setCode(String.valueOf(uidGenerator.getUID()));
        projectJob.setProjectCode(projectCode);
        projectJob.setState(ProjectJob.State.Executing.name());
        projectJob.setNumber(1);
        projectJob.setCreateTime(new Date());
        projectJobDAO.insert(projectJob);
        //缓存锁控制
        redisUtils.set(execute, JSON.toJSONString(projectJob), 120/*120s控制*/);

        Executors.singleThreadExecutor(new Runnable() {
            @Override
            public void run() {
                try {
                    //1.创建项目
                    projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), "Start By AI-Code"));
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("projectCode", projectCode);
                    Project project = projectDAO.load(map);
                    String projectPath = buildProject(project);
                    projectDAO.update(projectCode, project.getBuildNumber() != null ? project.getBuildNumber() + 1 : 1);

                    projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), "创建工作空间库完成"));
                    logger.info("创建工作空间库完成");

                    //2.获取类信息
                    List<ProjectMap> projectMapList = project.getProjectMapList();
                    List<MapClassTable> mapClassTableList = new ArrayList<>();
                    projectMapList.forEach(projectMap -> {
                        mapClassTableList.add(projectMap.getMapClassTable());
                    });

                    projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), "已经获取类信息"));

                    //3.获取模板信息
                    List<ProjectFramwork> projectFramworkList = project.getProjectFramworkList();

                    projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), "已经获取模板信息"));

                    //4.生成源码
                    projectFramworkList.forEach(projectFramwork -> {
                        List<FrameworksTemplate> frameworksTemplateList = projectFramwork.getFrameworks().getFrameworksTemplateList();
                        frameworksTemplateList.forEach(frameworksTemplate -> {
                            projectMapList.forEach(projectMap -> {
                                generator(projectPath, project, projectMap.getMapClassTable(), frameworksTemplate, mapClassTableList);
                            });
                            projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), "[<span style='color:green;'>✔</span>" + DateTools.yyyyMMddHHmmss(new Date()) + "] [已经生成] 模板 " + frameworksTemplate.getPath() + " 的相关源码"));
                        });
                    });

                    //5.获取模块信息 TODO

                    //6.获取版本控制管理信息
                    projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), "获取代码仓库信息"));
                    map.clear();
                    map.put("projectCode", project.getCode());
                    ProjectRepositoryAccount projectRepositoryAccount = projectRepositoryAccountDAO.load(map);
                    projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), "代码向仓库 ⇛⇛⇛ <a style='text-decoration:underline;' href='" + projectRepositoryAccount.getHome() + "' target='_blank'>" + projectRepositoryAccount.getHome() + " 提交中......"));
                    GitTools.commitAndPush(new File(projectPath), projectRepositoryAccount.getAccount(), projectRepositoryAccount.getPassword(), "AI-Code 为您构建代码，享受智慧生活");
                    projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), "代码代码提交完成"));
                    projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), "End By <span style='color:green;'>☺</span> AI-Code 为您构建代码，享受智慧生活!"));
                    projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), "End"));
                    projectJob.setState(ProjectJob.State.Completed.name());
                    projectJobDAO.update(projectJob);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    projectJob.setState(ProjectJob.State.Error.name());
                    projectJobDAO.update(projectJob);
                }
            }
        });
        return projectJob;
    }

    /**
     * 1.检测项目工作工作空间是否存在
     * 2.创建项目工作空间
     * 3.代码仓库检出
     *
     * @param project
     */
    private String buildProject(Project project) {
        Setting settingWorkspace = settingDAO.loadByKey(Setting.Key.Workspace.name());
        String projectPath = new HandleFuncs().getCurrentClassPath() + settingWorkspace.getV() + "/" + project.getEnglishName();
        projectPath = projectPath.replace("//", "/");
        //1.检测项目工作工作空间是否存在
        File file = new File(projectPath);
        if (file.exists()) {
            FileUtil.delFolder(projectPath);
        }
        file.mkdirs();

        //3.代码仓库检出
        Map<String, Object> map = Maps.newHashMap();
        map.put("projectCode", project.getCode());
        ProjectRepositoryAccount projectRepositoryAccount = projectRepositoryAccountDAO.load(map);
        GitTools.cloneGit(projectRepositoryAccount.getHome(), projectPath, projectRepositoryAccount.getAccount(), projectRepositoryAccount.getPassword());
        return projectPath;
    }


    /**
     * 生成源码文件
     *
     * @param projectPath        项目路径
     * @param project            项目对象
     * @param mapClassTable      映射对象
     * @param frameworksTemplate 框架模板对象
     */
    private void generator(String projectPath, Project project, MapClassTable mapClassTable, FrameworksTemplate frameworksTemplate, List<MapClassTable> mapClassTableList) {
        List<MapFieldColumn> mapFieldColumnPks = new ArrayList<>();
        List<MapFieldColumn> mapFieldColumnNotPks = new ArrayList<>();
        List<MapFieldColumn> mapFieldColumnList = new ArrayList<>();
        mapClassTable.getMapFieldColumnList().forEach(mapFieldColumn -> {
            if (mapFieldColumn.getIsPrimaryKey().equals(YNEnum.Y.name())) {
                mapFieldColumnPks.add(mapFieldColumn);
            } else {
                mapFieldColumnNotPks.add(mapFieldColumn);
            }
            mapFieldColumnList.add(mapFieldColumn);
        });

        TemplateData templateData = new TemplateData(project, mapClassTable, mapClassTableList, mapFieldColumnList, mapFieldColumnPks, mapFieldColumnNotPks);
        Setting settingTemplatePath = settingDAO.loadByKey(Setting.Key.Template_Path.name());

        //生成路径处理
        String frameworksTemplatePath = frameworksTemplate.getPath();
        if (frameworksTemplatePath.contains("/$")) {
            frameworksTemplatePath = frameworksTemplatePath.substring(frameworksTemplatePath.indexOf("/$"));
        } else {
            frameworksTemplatePath = frameworksTemplatePath.replaceFirst("/", "");
            frameworksTemplatePath = frameworksTemplatePath.substring(frameworksTemplatePath.indexOf("/"));
        }

        String targetFilePath = projectPath + "/" + frameworksTemplatePath
                .replace("${basepackage}", project.getBasePackage().replace(".", "/"))
                .replace("${className}", mapClassTable.getClassName())
                .replace("${module}", project.getEnglishName())
                .replace("${model}", templateData.getModel());

        String templatePath = new HandleFuncs().getCurrentClassPath()
                + "/" + settingTemplatePath.getV()
                + "/" + frameworksTemplate.getPath();

        FreemarkerHelper.generate(templateData, targetFilePath, templatePath);

    }
}
