package com.rzhkj.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
import com.rzhkj.base.core.FreemarkerHelper;
import com.rzhkj.base.core.ModelData;
import com.rzhkj.base.core.StringHelper;
import com.rzhkj.base.core.TemplateData;
import com.rzhkj.core.enums.YNEnum;
import com.rzhkj.core.tools.*;
import com.rzhkj.project.dao.*;
import com.rzhkj.project.entity.*;
import com.rzhkj.project.service.GenerateSV;
import com.rzhkj.setting.dao.SettingDAO;
import com.rzhkj.setting.entity.Setting;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 用于代码生成业务
 * Created by lixin on 2018/2/1.
 */
@Component
@Service
public class GeneratorSVImpl implements GenerateSV {
    protected final static Logger logger = LoggerFactory.getLogger(GeneratorSVImpl.class);

    @Resource
    private ProjectDAO projectDAO;
    @Resource
    private ProjectSqlDAO projectSqlDAO;

    @Resource
    private ProjectJobDAO projectJobDAO;
    @Resource
    private ProjectRepositoryAccountDAO projectRepositoryAccountDAO;

    @Resource
    private ProjectJobLogsDAO projectJobLogsDAO;

    @Resource
    private FrameworksTemplateDAO frameworksTemplateDAO;

    @Resource
    private SettingDAO settingDAO;

    @Resource
    private UidGenerator uidGenerator;


    /**
     * 根据项目码创建项目代码
     *
     * @param projectCode 项目编码
     * @param projectJob  项目job
     */
    @Override
    public void aiCode(String projectCode, ProjectJob projectJob) {
        try {
            //1.创建项目
            ProjectJobLogs projectJobLogs = new ProjectJobLogs();
            projectJobLogs.setCode(projectJob.getCode());
            projectJobLogs.setLog("Start By AI-Code @Copyright <a href='http://www.rzhkj.com' target='_blank'>仁中和</a>");
            projectJobLogsDAO.insert(projectJobLogs);
            Map<String, Object> map = Maps.newHashMap();
            map.put("code", projectCode);
            Project project = projectDAO.load(map);
            String projectPath = this.buildProject(project);
            projectDAO.update(projectCode, project.getBuildNumber() != null ? project.getBuildNumber() + 1 : 1);
            projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), " 已初始化项目 【 " + project.getName() + " ( " + project.getEnglishName() + " )】 工作空间"));
            logger.info("创建工作空间库完成");

            //2.获取类信息
            List<ProjectMap> projectMapList = project.getProjectMapList();
            List<MapClassTable> mapClassTableList = new ArrayList<>();
            projectMapList.forEach(projectMap -> {
                mapClassTableList.add(projectMap.getMapClassTable());
            });

            //3.获取模板信息
            List<ProjectFramwork> projectFramworkList = project.getProjectFramworkList();
            //从git中检出技术模板库
            this.readyframeworksTemplateList(projectFramworkList);

            //4.生成源码
            projectFramworkList.forEach(projectFramwork -> {
                projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), "已获取项目 【" + projectFramwork.getFrameworks().getName() + "】 的模板"));
                Map<String, Object> param = new HashMap<>();
                param.put("frameworkCode", projectFramwork.getFrameworks().getCode());
                Frameworks frameworks = projectFramwork.getFrameworks();
                List<FrameworksTemplate> frameworksTemplateList = frameworksTemplateDAO.query(param);
//                List<FrameworksTemplate> frameworksTemplateList = projectFramwork.getFrameworks().getFrameworksTemplateList();
                frameworksTemplateList.forEach(frameworksTemplate -> {
                    projectMapList.forEach(projectMap -> {
                        this.generator(projectPath, project, frameworks, projectMap.getMapClassTable(), frameworksTemplate, mapClassTableList);
                    });
                    projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), " 【已经生成】 模板 " + frameworksTemplate.getPath() + " 的源码"));
                });
            });

            //清理临时模板数据
            this.cleanTemplates(projectFramworkList);


            //生成sql脚本到项目下
            this.generateTsql(projectPath, project.getEnglishName(), projectCode);
            projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), " 【已经生成】 " + project.getEnglishName() + "Sql 脚本文件并追加系统配置"));

            //5.获取模块信息 TODO

            //6.获取版本控制管理信息
            map.clear();
            map.put("projectCode", project.getCode());
            ProjectRepositoryAccount projectRepositoryAccount = projectRepositoryAccountDAO.load(map);
            if (projectRepositoryAccount != null) {
                projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), "获取代码仓库信息: " + projectRepositoryAccount.getAccount()));
                GitTools.commitAndPush(new File(projectPath), projectRepositoryAccount.getAccount(), projectRepositoryAccount.getPassword(), "AI-Code 为您构建代码，享受智慧生活");
                projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), "代码已经提交到 ⇛⇛⇛ <a style='text-decoration:underline;' href='" + projectRepositoryAccount.getHome() + "' target='_blank'>" + projectRepositoryAccount.getHome() + " </a>仓库"));
            }

            //7.创建压缩文件
            this.zipProject(project);

            //记录日志
            projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), "代码已打包ZIP,您还可以点击下载 ⇛⇛⇛  <a style='text-decoration:underline;' href='" + project.getDownloadUrl() + "' target='_blank'>" + project.getEnglishName() + ".zip</a>"));
            projectJob.setState(ProjectJob.State.Completed.name());
            projectJobDAO.update(projectJob);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), "ERROR : " + e.getMessage()));
            projectJob.setState(ProjectJob.State.Error.name());
            projectJobDAO.update(projectJob);
        } finally {
            Map<String, Object> map = Maps.newHashMap();
            map.put("code", projectJob.getCode());
            ProjectJob projectJobLoad = projectJobDAO.load(map);
            if (projectJobLoad.getState().equals(ProjectJob.State.Completed.name())) {
                ProjectJobLogs projectJobLogs = new ProjectJobLogs();
                projectJobLogs.setCode(projectJob.getCode());
                projectJobLogs.setLog("Finished: SUCCESS");
                projectJobLogsDAO.insert(projectJobLogs);
            } else {
                ProjectJobLogs projectJobLogs = new ProjectJobLogs();
                projectJobLogs.setCode(projectJob.getCode());
                projectJobLogs.setLog("Finished: ERROR");
                projectJobLogsDAO.insert(projectJobLogs);
            }
            ProjectJobLogs projectJobLogs = new ProjectJobLogs();
            projectJobLogs.setCode(projectJob.getCode());
            projectJobLogs.setLog("End");
            projectJobLogsDAO.insert(projectJobLogs);
        }
    }

    //清理临时模板数据
    private void cleanTemplates(List<ProjectFramwork> projectFramworkList) {
        frameworksTemplateDAO.deleteAll();
        Setting setting = settingDAO.loadByKey(Setting.Key.Template_Path.name());
        String template_Path = new HandleFuncs().getCurrentClassPath() + setting.getV();//获得默认仓库地址

        for (ProjectFramwork projectFramwork : projectFramworkList) {
            Frameworks frameworks = projectFramwork.getFrameworks();
            if (frameworks.getGitHome() != null) {
                String project_template_Path = template_Path + frameworks.getGitHome().substring(frameworks.getGitHome().lastIndexOf("/") + 1).replace(".git", "");
                FileUtil.delFolder(project_template_Path);
            }
        }
    }

    //准备框架模板
    private void readyframeworksTemplateList(List<ProjectFramwork> projectFramworkList) {
        Setting setting = settingDAO.loadByKey(Setting.Key.Template_Path.name());
        String template_Path = new HandleFuncs().getCurrentClassPath() + setting.getV();//获得默认仓库地址
        for (ProjectFramwork projectFramwork : projectFramworkList) {
            Frameworks frameworks = projectFramwork.getFrameworks();
            logger.debug(JSON.toJSONString(frameworks));
            if (frameworks.getGitHome() != null) {
                String project_template_Path = template_Path + frameworks.getGitHome().substring(frameworks.getGitHome().lastIndexOf("/") + 1).replace(".git", "");
                if (YNEnum.Y == YNEnum.getYN(frameworks.getIsPublic())) {
                    GitTools.cloneGit(frameworks.getGitHome(), project_template_Path);
                } else {
                    GitTools.cloneGit(frameworks.getGitHome(), project_template_Path, frameworks.getAccount(), frameworks.getPassword());
                }

                //删除不相干的模板及目录文件
                File templateFile = new File(template_Path);
                File[] files = templateFile.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        for (File file2 : file.listFiles()) {
                            if (!frameworks.getName().equalsIgnoreCase(file2.getName())) {
                                if (file2.isDirectory()) {
                                    logger.debug("del:" + file2.getAbsolutePath());
                                    FileUtil.delFolder(file2.getAbsolutePath());
                                } else {
                                    file2.delete();
                                }
                            }
                        }
                    }
                }

                List<File> Files = FileUtil.getDirFiles(template_Path);
                for (File file : Files) {
                    if (file.getAbsoluteFile().toString().contains("\\.git\\") || file.getAbsoluteFile().toString().contains("README.md")) {
                        continue;
                    }
                    String path = ("/" + file.getAbsoluteFile().toString()).replace("\\", "/").replace(template_Path, "");
                    path = "/" + path;
                    path = path.replace("//", "/");
                    FrameworksTemplate frameworksTemplate = new FrameworksTemplate();
                    frameworksTemplate.setCode(String.valueOf(uidGenerator.getUID()));
                    frameworksTemplate.setPath(path);
                    frameworksTemplate.setFrameworkCode(frameworks.getCode());
                    frameworksTemplateDAO.insert(frameworksTemplate);
                }
            }
        }
    }


    private void generateTsql(String projectPath, String projectEnglishName, String projectCode) {
        String tsql = "-- AI-Code 为您构建代码，享受智慧生活!\n";
        String tsqlLast = "\nCREATE TABLE `worker_node` (\n" +
                "  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',\n" +
                "  `HOST_NAME` varchar(64) NOT NULL COMMENT 'host name',\n" +
                "  `PORT` varchar(64) NOT NULL COMMENT 'port',\n" +
                "  `TYPE` int(11) NOT NULL COMMENT 'node type: ACTUAL or CONTAINER',\n" +
                "  `LAUNCH_DATE` date NOT NULL COMMENT 'launch date',\n" +
                "  `MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT 'modified time',\n" +
                "  `CREATED` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'created time',\n" +
                "  PRIMARY KEY (`ID`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1263 DEFAULT CHARSET=utf8 COMMENT='DB WorkerID Assigner for UID Generator';\n";
        Map<String, Object> map = Maps.newHashMap();
        map.put("projectCode", projectCode);
        ProjectSql projectSql = projectSqlDAO.load(map);
        try {
            tsql += projectSql.getTsql() + tsqlLast;
            FileUtils.writeByteArrayToFile(new File(projectPath + "/" + projectEnglishName + ".sql"), tsql.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (!file.exists()) {
//            file.mkdirs();
            FileUtil.delFolder(projectPath);
        }

        //3.代码仓库检出
        Map<String, Object> map = Maps.newHashMap();
        map.put("projectCode", project.getCode());
        ProjectRepositoryAccount projectRepositoryAccount = projectRepositoryAccountDAO.load(map);
        if (projectRepositoryAccount != null) {
            if (ProjectRepositoryTypeEnum.GIT == ProjectRepositoryTypeEnum.getEnum(projectRepositoryAccount.getType())) {
                GitTools.cloneGit(projectRepositoryAccount.getHome(), projectPath, projectRepositoryAccount.getAccount(), projectRepositoryAccount.getPassword());
            } else if (ProjectRepositoryTypeEnum.SVN == ProjectRepositoryTypeEnum.getEnum(projectRepositoryAccount.getType())) {
                //TODO SVN 仓库工具类
            }
        }
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
    private void generator(String projectPath, Project project, Frameworks frameworks, MapClassTable mapClassTable, FrameworksTemplate frameworksTemplate, List<MapClassTable> mapClassTableList) {
        List<MapFieldColumn> mapFieldColumnPks = new ArrayList<>();
        List<MapFieldColumn> mapFieldColumnNotPks = new ArrayList<>();
        List<MapFieldColumn> mapFieldColumnList = new ArrayList<>();
        List<MapFieldColumn> mapFieldColumnTable = new ArrayList<>();
        mapClassTable.getMapFieldColumnList().forEach(mapFieldColumn -> {
            if (mapFieldColumn.getIsPrimaryKey().equals(YNEnum.Y.name())) {
                mapFieldColumnPks.add(mapFieldColumn);
            } else {
                mapFieldColumnNotPks.add(mapFieldColumn);
            }
            if (!mapFieldColumn.getIsPrimaryKey().equals(YNEnum.Y.name()) && !mapFieldColumn.getColumn().contains("updateTime") && !mapFieldColumn.getColumn().contains("summary")
                    && !mapFieldColumn.getColumn().contains("marker") && !mapFieldColumn.getColumn().contains("vn")) {
                mapFieldColumnTable.add(mapFieldColumn);
            }
            mapFieldColumnList.add(mapFieldColumn);
        });

        //各个模块下的所有类集合信息
        List<MapClassTable> modelClasses = new ArrayList<>();
        List<String> models = new ArrayList<>();
        List<ModelData> modelDatas = new ArrayList<>();
        Map<String, List<MapClassTable>> mapClassTableMap = new HashedMap();
        mapClassTableList.forEach(mapClassTableObj -> {
            models.add(mapClassTableObj.getClassModel());
        });
        mapClassTableList.forEach(mapClassTableObj -> {
            List<MapClassTable> mapClassTables = null;
            if (mapClassTableMap.containsKey(mapClassTableObj.getClassModel())) {
                mapClassTables = mapClassTableMap.get(mapClassTableObj.getClassModel());
            } else {
                mapClassTables = new ArrayList<>();
            }
            mapClassTables.add(mapClassTableObj);
            mapClassTableMap.put(mapClassTableObj.getClassModel(), mapClassTables);
        });

        models.forEach(model -> {
            if (mapClassTableMap.containsKey(model)) {
                List<MapClassTable> mapClassTables = mapClassTableMap.get(model);
                modelDatas.add(new ModelData(model, mapClassTables));
            }
        });
        HashSet hashSet = new HashSet(modelDatas);
        modelDatas.clear();
        modelDatas.addAll(hashSet);



        mapClassTableList.forEach(mapClassTableObj -> {
            if (mapClassTable.getClassModel().equals(mapClassTableObj.getClassModel())) {
                modelClasses.add(mapClassTableObj);
            }
        });

        //根据模块划分类集合信息

        TemplateData templateData = new TemplateData(project, mapClassTable, mapClassTableList, mapFieldColumnList, mapFieldColumnPks, mapFieldColumnNotPks, mapFieldColumnTable, modelClasses, modelDatas);
        Setting settingTemplatePath = settingDAO.loadByKey(Setting.Key.Template_Path.name());

        //生成路径处理
        String frameworksTemplatePath = frameworksTemplate.getPath();
        if (frameworksTemplatePath.contains("/${module}")) {
            frameworksTemplatePath = frameworksTemplatePath.substring(frameworksTemplatePath.indexOf("/$"));
        } else if (frameworksTemplatePath.contains(frameworks.getName())) {
            frameworksTemplatePath = frameworksTemplatePath.substring(frameworksTemplatePath.indexOf(frameworks.getName()) + frameworks.getName().length());
        } else {
            frameworksTemplatePath = frameworksTemplatePath.replaceFirst("/", "");
            if (frameworksTemplatePath.indexOf("/") > 0) {
                frameworksTemplatePath = frameworksTemplatePath.substring(frameworksTemplatePath.indexOf("/"));
            }
        }

        String targetFilePath = projectPath + "/" + frameworksTemplatePath.replace(".ftl", "")
                .replace("${basepackage}", project.getBasePackage().replace(".", "/"))
                .replace("${basePackage}", project.getBasePackage().replace(".", "/"))
                .replace("${className}", mapClassTable.getClassName())
                .replace("${classNameLower}", StringHelper.toJavaVariableName(mapClassTable.getClassName()))
                .replace("${dashedCaseName}", StringTools.humpToLine(mapClassTable.getClassName()))
                .replace("${module}", project.getEnglishName())
                .replace("${model}", templateData.getModel());

        String templatePath = new HandleFuncs().getCurrentClassPath()
                + "/" + settingTemplatePath.getV()
                + "/" + frameworksTemplate.getPath();
        logger.debug("模板路径：" + templatePath);
        if (targetFilePath.contains("angular")) {
            logger.debug("目标文件路径" + targetFilePath);
        }

        if (new File(templatePath).exists()) {
            if (!templatePath.contains(".jar")) {
                //增量判断，默认是增量只创建不存在的文件
                if (!new File(targetFilePath.replace("//", "/")).exists()) {
                    FreemarkerHelper.generate(templateData, targetFilePath, templatePath);
                }
            } else {
                try {
                    FileUtils.copyFileToDirectory(new File(templatePath), new File(targetFilePath.substring(0, targetFilePath.lastIndexOf("/"))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            logger.error("文件不存在 ===> " + templatePath);
        }

    }

    /**
     * 压缩文件
     *
     * @param project 项目信息
     */
    private void zipProject(Project project) {
        Setting settingWorkspace = settingDAO.loadByKey(Setting.Key.Workspace.name());
        String projectWorkspacePath = new HandleFuncs().getCurrentClassPath() + settingWorkspace.getV() + "/" + project.getEnglishName();
        projectWorkspacePath = projectWorkspacePath.replace("//", "/");

        Setting Repository_Path = settingDAO.loadByKey(Setting.Key.Repository_Path.name());
        String projectPath = new HandleFuncs().getCurrentClassPath() + Repository_Path.getV() + "/" + project.getEnglishName();
        String destination = projectPath;

        //压缩文件
        try {
            File zipFile = new File(destination);
            if (zipFile.exists()) {
                zipFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        ZipTools.zip(destination, projectWorkspacePath);
        project.setDownloadUrl((Repository_Path.getV() + "/" + project.getEnglishName() + ".zip").replace("//", "/"));
        projectDAO.update(project);
    }
}
