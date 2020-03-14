package io.aicode.project.service.impl;

import com.alibaba.fastjson.JSON;
import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.aicode.base.core.ModelData;
import io.aicode.base.core.StringHelper;
import io.aicode.base.core.TemplateData;
import io.aicode.base.core.template.Configuration;
import io.aicode.base.core.template.TemplateHelper;
import io.aicode.base.enums.TemplateEngineEnum;
import io.aicode.base.enums.YNEnum;
import io.aicode.base.tools.FileUtil;
import io.aicode.base.tools.GitTools;
import io.aicode.base.tools.StringTools;
import io.aicode.base.tools.ZipTools;
import io.aicode.base.websocket.WSClientManager;
import io.aicode.display.entity.DisplayAttribute;
import io.aicode.project.dao.*;
import io.aicode.project.entity.*;
import io.aicode.project.service.GenerateSV;
import io.aicode.project.service.LogsSV;
import io.aicode.setting.dao.SettingDAO;
import io.aicode.setting.entity.Setting;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private MapFieldColumnDAO mapFieldColumnDAO;

    @Resource
    private LogsSV logsSV;


    @Resource
    private UidGenerator uidGenerator;

    @Autowired
    private TemplateHelper freemarkerHelper;
    @Autowired
    private TemplateHelper beetlHelper;


    /**
     * 根据项目码创建项目代码
     *
     * @param projectCode 项目编码
     * @param projectJob  项目job
     */
    @Override
    public void aiCode(String projectCode, ProjectJob projectJob) {
        String path = logsSV.createLogFiles(projectCode, projectJob.getCreateTime());

        try {
            //1.创建项目
            String log = "Start By AI-Code @Copyright <a href='http://www.aicode.io' target='_blank'>AI-Code</a>";
            WSClientManager.sendMessage(log);
            logsSV.saveLogs(log, path);
            Map<String, Object> map = Maps.newHashMap();
            map.put("code", projectCode);
            Project project = projectDAO.load(map);
            String projectPath = this.buildProject(project);
            projectDAO.update(projectCode, project.getBuildNumber() != null ? project.getBuildNumber() + 1 : 1);
            log = "已初始化项目 【 " + project.getName() + " ( " + project.getEnglishName() + " )】 工作空间";
            WSClientManager.sendMessage(log);
            logsSV.saveLogs(log, path);
            projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), log));
            logger.info("创建工作空间库完成");
            logsSV.saveLogs("创建工作空间库完成", path);


            //2.获取类信息
            log = "转化数据库结构与类模型...";
            WSClientManager.sendMessage(log);
            logsSV.saveLogs(log, path);
            List<ProjectMap> projectMapList = project.getProjectMapList();
            List<MapClassTable> mapClassTableList = new ArrayList<>();
            projectMapList.forEach(projectMap -> {
                mapClassTableList.add(projectMap.getMapClassTable());
            });
            WSClientManager.sendMessage("转化数据库结构与类模型成功！");
            logsSV.saveLogs("转化数据库结构与类模型成功！", path);

            //3.获取模板信息
            List<ProjectFramwork> projectFramworkList = project.getProjectFramworkList();
            //从git中检出技术模板库
            log = ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>";
            WSClientManager.sendMessage(log);
            WSClientManager.sendMessage("开始 下载技术模板");
            WSClientManager.sendMessage(log);
            logsSV.saveLogs(log, path);
            logsSV.saveLogs("开始 下载技术模板", path);
            logsSV.saveLogs(log, path);
            WSClientManager.sendMessage("git 努力下载中.....");

            /**
             * 准备框架模板
             */
            TemplateEngineEnum templateEngineEnum = this.prepareframeworksTemplateList(projectFramworkList, path);

            WSClientManager.sendMessage(log);
            WSClientManager.sendMessage("结束 下载技术模板成功");
            WSClientManager.sendMessage(log);
            logsSV.saveLogs(log, path);
            logsSV.saveLogs("结束 下载技术模板成功", path);
            logsSV.saveLogs(log, path);

            //4.生成源码
            WSClientManager.sendMessage("开始生成源码...");
            logsSV.saveLogs("开始生成源码...", path);
            projectFramworkList.forEach(projectFramwork -> {
                WSClientManager.sendMessage("已获取项目 【" + projectFramwork.getFrameworks().getName() + "】 的模板");
                logsSV.saveLogs("已获取项目 【" + projectFramwork.getFrameworks().getName() + "】 的模板", path);
                Map<String, Object> param = new HashMap<>();
                param.put("frameworkCode", projectFramwork.getFrameworks().getCode());
                Frameworks frameworks = projectFramwork.getFrameworks();
                List<FrameworksTemplate> frameworksTemplateList = frameworksTemplateDAO.query(param);
                frameworksTemplateList.forEach(frameworksTemplate -> {
                    projectMapList.forEach(projectMap -> {
                        this.generator(projectPath, project, frameworks, projectMap.getMapClassTable(), frameworksTemplate, mapClassTableList, templateEngineEnum);
                    });
                    WSClientManager.sendMessage("[生成] 模板 " + frameworksTemplate.getPath() + " 的源码");
                    logsSV.saveLogs("[生成] 模板 " + frameworksTemplate.getPath() + " 的源码", path);
                });
            });

            //清理临时模板数据
            this.cleanTemplates(projectFramworkList);


            //生成sql脚本到项目下
            String sql = this.generateTsql(projectPath, project.getEnglishName(), projectCode);
            WSClientManager.sendMessage("分布式唯一算法sql " + sql);
            logsSV.saveLogs("分布式唯一算法sql " + sql, path);
            String sqllog = "【已经生成】 " + project.getEnglishName() + "Sql 脚本文件并追加系统配置";
            WSClientManager.sendMessage(sqllog);
            logsSV.saveLogs(sqllog, path);
            projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), sqllog));

            //5.获取模块信息 TODO

            //6.获取版本控制管理信息
            map.clear();
            map.put("projectCode", project.getCode());
            ProjectRepositoryAccount projectRepositoryAccount = projectRepositoryAccountDAO.load(map);
            if (projectRepositoryAccount != null) {
                String gitLog = "获取代码仓库信息: " + projectRepositoryAccount.getAccount();
                WSClientManager.sendMessage(gitLog);
                logsSV.saveLogs(gitLog, path);
                projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), gitLog));
                GitTools.commitAndPush(new File(projectPath), projectRepositoryAccount.getAccount(), projectRepositoryAccount.getPassword(), "AI-Code 为您构建代码，享受智慧生活");
                gitLog = "代码已经提交到 ⇛⇛⇛ <a style='text-decoration:underline;' href='" + projectRepositoryAccount.getHome() + "' target='_blank'>[" + projectRepositoryAccount.getHome() + "] </a>仓库";
                WSClientManager.sendMessage(gitLog);
                logsSV.saveLogs(gitLog, path);
                projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), gitLog));
            }

            //7.创建压缩文件
            this.zipProject(project);
            String endLog = "代码已打包ZIP, ⇛⇛⇛  <a style='text-decoration:underline;' href='" + project.getDownloadUrl() + "' target='_blank'>[点击下载" + project.getEnglishName() + ".zip]</a>";
            WSClientManager.sendMessage(endLog);
            logsSV.saveLogs(endLog, path);
            //记录日志
            projectJobLogsDAO.insert(new ProjectJobLogs(projectJob.getCode(), endLog));
            projectJob.setState(ProjectJob.State.Completed.name());
            projectJobDAO.update(projectJob);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            WSClientManager.sendMessage(e.getMessage());
            logsSV.saveLogs(e.getMessage(), path);
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
                WSClientManager.sendMessage("Finished: SUCCESS");
                logsSV.saveLogs("Finished: SUCCESS", path);
                projectJobLogsDAO.insert(projectJobLogs);
            } else {
                ProjectJobLogs projectJobLogs = new ProjectJobLogs();
                projectJobLogs.setCode(projectJob.getCode());
                projectJobLogs.setLog("Finished: ERROR");
                WSClientManager.sendMessage("Finished: ERROR");
                logsSV.saveLogs("Finished: ERROR", path);
                projectJobLogsDAO.insert(projectJobLogs);
            }
            ProjectJobLogs projectJobLogs = new ProjectJobLogs();
            projectJobLogs.setCode(projectJob.getCode());
            projectJobLogs.setLog("End");
            WSClientManager.sendMessage("End");
            projectJobLogsDAO.insert(projectJobLogs);
        }
    }

    //清理临时模板数据
    private void cleanTemplates(List<ProjectFramwork> projectFramworkList) {
        frameworksTemplateDAO.deleteAll();
        Setting setting = settingDAO.loadByKey(Setting.Key.Template_Path.name());
        String template_Path = this.convertPath(setting.getV(), "", true);//获得默认仓库地址

        for (ProjectFramwork projectFramwork : projectFramworkList) {
            Frameworks frameworks = projectFramwork.getFrameworks();
            if (frameworks.getGitHome() != null) {
                String project_template_Path = template_Path + frameworks.getGitHome().substring(frameworks.getGitHome().lastIndexOf("/") + 1).replace(".git", "");
                FileUtil.delFolder(project_template_Path);
            }
        }
    }

    //准备框架模板
    private TemplateEngineEnum prepareframeworksTemplateList(List<ProjectFramwork> projectFramworkList, String logsPath) {
        Setting setting = settingDAO.loadByKey(Setting.Key.Template_Path.name());
        //获得默认仓库地址
        String template_Path = this.convertPath("/", setting.getV(), true);
        //拼接项目框架字符串，用于判断
        String projectFramworkKeyWords = "";
        for (ProjectFramwork projectFramwork : projectFramworkList) {
            projectFramworkKeyWords = projectFramworkKeyWords + projectFramwork.getFrameworks().getName() + "|";
        }

        TemplateEngineEnum templateEngineEnum = null;
        for (ProjectFramwork projectFramwork : projectFramworkList) {
            Frameworks frameworks = projectFramwork.getFrameworks();
            logger.debug(JSON.toJSONString(frameworks));
            if (frameworks.getGitHome() != null) {
                String project_template_Path = template_Path + frameworks.getGitHome().substring(frameworks.getGitHome().lastIndexOf("/") + 1).replace(".git", "");
                //TODO 已经存在的进行清理 需要开发
                WSClientManager.sendMessage("已连接到模板仓库，开始克隆已选技术[" + frameworks.getName() + "]的模板");
                logsSV.saveLogs("已连接到模板仓库，开始克隆已选技术[" + frameworks.getName() + "]的模板", logsPath);
                if (YNEnum.Y == YNEnum.getYN(frameworks.getIsPublic())) {
                    GitTools.cloneGit(frameworks.getGitHome(), project_template_Path);
                } else {
                    GitTools.cloneGit(frameworks.getGitHome(), project_template_Path, frameworks.getAccount(), frameworks.getPassword());
                }

                String template_root_path = this.findPath(project_template_Path, frameworks.getName());
//                String template_root_path = this.findPath(template_Path, frameworks.getName());

                //删除不相干的模板及目录文件
                File templateFile = new File(template_root_path.replace(frameworks.getName(), ""));
                File[] files = templateFile.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        if (!projectFramworkKeyWords.contains(file.getName())) {
                            if (file.isDirectory()) {
                                logger.debug("del:" + file.getAbsolutePath());
                                FileUtil.delFolder(file.getAbsolutePath());
                            } else {
                                file.delete();
                            }
                        }
                    }
                }

                List<File> Files = FileUtil.getDirFiles(template_root_path);
                for (File file : Files) {
                    if (file.getAbsoluteFile().toString().contains("\\.git\\") || file.getAbsoluteFile().toString().contains("README.md")) {
                        continue;
                    }
                    String path = this.convertPath("/", file.getAbsoluteFile().toString(), false).replace("//", "");
                    if (null == templateEngineEnum) {
                        templateEngineEnum = this.adapterTemplateEngine(path);
                    }
                    path = path.substring(path.indexOf(template_Path) + template_Path.length());
                    FrameworksTemplate frameworksTemplate = new FrameworksTemplate();
                    frameworksTemplate.setCode(String.valueOf(uidGenerator.getUID()));
                    frameworksTemplate.setPath(path);
                    frameworksTemplate.setFrameworkCode(frameworks.getCode());
                    frameworksTemplateDAO.insert(frameworksTemplate);
                    WSClientManager.sendMessage("[模板] " + frameworksTemplate.getPath().substring(frameworksTemplate.getPath().lastIndexOf("/") + 1));
                    logsSV.saveLogs("[模板] " + frameworksTemplate.getPath().substring(frameworksTemplate.getPath().lastIndexOf("/") + 1), logsPath);
                }
                WSClientManager.sendMessage("模板克隆成功！");
                logsSV.saveLogs("模板克隆成功！", logsPath);
            }
        }

        if (null == templateEngineEnum) {
            templateEngineEnum = TemplateEngineEnum.Freemarker;
        }

        return templateEngineEnum;
    }


    private String generateTsql(String projectPath, String projectEnglishName, String projectCode) {
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
        return tsqlLast;
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
        WSClientManager.sendMessage("创建项目[" + project.getEnglishName() + "]");
        String projectPath = this.convertPath(settingWorkspace.getV(), project.getEnglishName(), true);
        //1.检测项目工作工作空间是否存在
        File file = new File(projectPath);
        if (!file.exists()) {
            FileUtil.delFolder(projectPath);
            WSClientManager.sendMessage("删除已存在[" + project.getEnglishName() + "]项目");
        }

        //3.代码仓库检出
        Map<String, Object> map = Maps.newHashMap();
        map.put("projectCode", project.getCode());
        ProjectRepositoryAccount projectRepositoryAccount = projectRepositoryAccountDAO.load(map);
        if (projectRepositoryAccount != null) {
            if (ProjectRepositoryTypeEnum.GIT == ProjectRepositoryTypeEnum.getEnum(projectRepositoryAccount.getType())) {
                if (projectRepositoryAccount.getHome().startsWith("git://") && projectRepositoryAccount.getHome().endsWith(".git")) {
                    WSClientManager.sendMessage("初始化设定git项目");
                    GitTools.cloneGit(projectRepositoryAccount.getHome(), projectPath, projectRepositoryAccount.getAccount(), projectRepositoryAccount.getPassword());
                    WSClientManager.sendMessage("初始化设定git项目完成");
                } else {
                    WSClientManager.sendMessage("git 仓库地址不合法无法检出指定项目，请在生成后手动下载源码包！");
                }
            } else if (ProjectRepositoryTypeEnum.SVN == ProjectRepositoryTypeEnum.getEnum(projectRepositoryAccount.getType())) {
                //TODO SVN 仓库工具类9
            }
        }

        if (file.exists()) {
            List<ProjectFramwork> frameworksList = project.getProjectFramworkList();
            WSClientManager.sendMessage("判断是否是选择多技术项目....");
            if (frameworksList.size() > 1) {
                WSClientManager.sendMessage("多技术框架项目，将生成多个框架源码....");
                for (ProjectFramwork projectFramwork : frameworksList) {
                    File frameworkFile = new File(projectPath + "/" + projectFramwork.getFrameworks().getName());
                    if (!frameworkFile.exists()) {
                        frameworkFile.mkdir();
                        WSClientManager.sendMessage("创建" + projectFramwork.getFrameworks().getName() + "文件夹成功!");
                    }
                }
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
     * @param templateEngineEnum
     */
    private void generator(String projectPath, Project project, Frameworks frameworks, MapClassTable mapClassTable, FrameworksTemplate frameworksTemplate, List<MapClassTable> mapClassTableList, TemplateEngineEnum templateEngineEnum) {
        List<MapFieldColumn> mapFieldColumnPks = new ArrayList<>();
        List<MapFieldColumn> mapFieldColumnNotPks = new ArrayList<>();
        List<MapFieldColumn> mapFieldColumnList = new ArrayList<>();
        List<MapFieldColumn> mapFieldColumnTable = new ArrayList<>();
        List<DisplayAttribute> displayAttributes = new ArrayList<>();
        List<TemplateData> oneToOneList = new ArrayList<>();
        List<TemplateData> oneToManyList = new ArrayList<>();
        mapClassTable.getMapFieldColumnList().forEach(mapFieldColumn -> {
            if (mapFieldColumn.getIsPrimaryKey().equals(YNEnum.Y.name())) {
                mapFieldColumnPks.add(mapFieldColumn);
            } else {
                mapFieldColumnNotPks.add(mapFieldColumn);
            }
            if (!mapFieldColumn.getIsPrimaryKey().equals(YNEnum.Y.name())
                    && !mapFieldColumn.getColumn().contains("updateTime")
                    && !mapFieldColumn.getColumn().contains("summary")
                    && !mapFieldColumn.getColumn().contains("marker")
                    && !mapFieldColumn.getColumn().contains("vn")) {
                mapFieldColumnTable.add(mapFieldColumn);
            }
            mapFieldColumnList.add(mapFieldColumn);
            //封装类属性的显示显示属性
            displayAttributes.add(mapFieldColumn.getDisplayAttribute());
        });

        //获取1对1,1对多关系集合
        mapClassTable.getMapRelationshipList().forEach(mapRelationship -> {
            Map<String, Object> param = new HashMap<>();
            param.put("mapClassTableCode", mapRelationship.getAssociateClass().getCode());
            List<MapFieldColumn> associateClassColumns = mapFieldColumnDAO.query(param);
            if (YNEnum.getYN(mapRelationship.getIsOneToOne()) == YNEnum.Y) {
                oneToOneList.add(new TemplateData(project, mapRelationship.getAssociateClass(), mapRelationship.getMainField(),
                        mapRelationship.getJoinField(), associateClassColumns));
            }
            if (YNEnum.getYN(mapRelationship.getIsOneToMany()) == YNEnum.Y) {
                oneToManyList.add(new TemplateData(project, mapRelationship.getAssociateClass(), mapRelationship.getMainField(),
                        mapRelationship.getJoinField(), associateClassColumns));
            }
            logger.debug(JSON.toJSONString(mapRelationship.getAssociateClass().getMapFieldColumnList()));
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

        TemplateData templateData = new TemplateData(project, mapClassTable, mapClassTableList, mapFieldColumnList,
                mapFieldColumnPks, mapFieldColumnNotPks, mapFieldColumnTable, modelClasses, modelDatas, oneToOneList, oneToManyList);
        templateData.setDisplayAttributes(displayAttributes);

        Setting settingTemplatePath = settingDAO.loadByKey(Setting.Key.Template_Path.name());

        //生成路径处理
        String frameworksTemplatePath = frameworksTemplate.getPath();
        if (frameworksTemplatePath.contains("/${module}") || frameworksTemplatePath.contains("/$module$")) {
            frameworksTemplatePath = frameworksTemplatePath.substring(frameworksTemplatePath.indexOf("/$"));
        } else if (frameworksTemplatePath.contains(frameworks.getName())) {
            frameworksTemplatePath = frameworksTemplatePath.substring(frameworksTemplatePath.indexOf(frameworks.getName()) + frameworks.getName().length());
        } else {
            frameworksTemplatePath = frameworksTemplatePath.replaceFirst("/", "");
            if (frameworksTemplatePath.indexOf("/") > 0) {
                frameworksTemplatePath = frameworksTemplatePath.substring(frameworksTemplatePath.indexOf("/"));
            }
        }

        //确立文件生成路径
        String targetFilePath = "";
        if (project.getProjectFramworkList().size() > 1) {
            targetFilePath = projectPath + "/" + frameworks.getName() + "/";
        } else {
            targetFilePath = projectPath + "/";
        }
        targetFilePath = targetFilePath + frameworksTemplatePath.replace(".ftl", "")
                .replace("${basepackage}", project.getBasePackage().replace(".", "/"))
                .replace("${basePackage}", project.getBasePackage().replace(".", "/"))
                .replace("${package}", project.getBasePackage().replace(".", "/"))
                .replace("${Package}", project.getBasePackage().replace(".", "/"))
                .replace("${className}", mapClassTable.getClassName())
                .replace("${classNameLower}", StringHelper.toJavaVariableName(mapClassTable.getClassName()))
                .replace("${dashedCaseName}", StringTools.humpToLine(mapClassTable.getClassName()))
                .replace("${module}", project.getEnglishName())
                .replace("${model}", templateData.getModel())

                .replace(".btl", "")
                .replace("$basepackage$", project.getBasePackage().replace(".", "/"))
                .replace("$basePackage$", project.getBasePackage().replace(".", "/"))
                .replace("$package$", project.getBasePackage().replace(".", "/"))
                .replace("$Package$", project.getBasePackage().replace(".", "/"))
                .replace("$className$", mapClassTable.getClassName())
                .replace("$classNameLower$", StringHelper.toJavaVariableName(mapClassTable.getClassName()))
                .replace("$dashedCaseName$", StringTools.humpToLine(mapClassTable.getClassName()))
                .replace("$module$", project.getEnglishName())
                .replace("$model$", templateData.getModel());

        String templatePath = "/" + settingTemplatePath.getV()
                + "/" + frameworksTemplate.getPath();
        templatePath = templatePath.replace("//", "/").replace("///", "/");
        logger.debug("模板路径：" + templatePath);
        if (targetFilePath.contains("angular")) {
            logger.debug("目标文件路径" + targetFilePath);
        }

        //增量状态判断
        if (YNEnum.getYN(project.getIsIncrement()) == YNEnum.N) {
            if (new File(templatePath).exists()) {
                if (!templatePath.contains(".jar")) {
                    //适配模板引擎
                    String msg = "";
                    if (null == adapterTemplateEngine(templatePath)) {
                        if (TemplateEngineEnum.Freemarker == templateEngineEnum) {
                            msg = freemarkerHelper.generate(templateData, targetFilePath, templatePath);
                        } else if (TemplateEngineEnum.Beetl == templateEngineEnum) {
                            msg = beetlHelper.generate(templateData, targetFilePath, templatePath);
                        }
                        WSClientManager.sendMessage(msg);
                    }
                } else {
                    try {
                        FileUtils.copyFileToDirectory(new File(templatePath), new File(targetFilePath.substring(0, targetFilePath.lastIndexOf("/"))));
                    } catch (IOException e) {
                        e.printStackTrace();
                        WSClientManager.sendMessage(e.getMessage());
                    }
                }
            } else {
                logger.error("文件不存在 ===> " + templatePath);
            }
        }

    }

    /**
     * 适配 模板引擎
     *
     * @param path 项目路径
     * @return TemplateEngineEnum
     */
    private TemplateEngineEnum adapterTemplateEngine(String path) {
        try {
            //定义文件名默认 aicode.json  ，以及可能的错误名字进行兼容
            File aicodeFile = null;
            String[] fileName = {"aicode.json", "aicode", "ai-code.json", "ai-code"};
            List<String> fileNameList = Lists.newArrayList(fileName);
            for (String name : fileNameList) {
                if (path.endsWith(name)) {
                    aicodeFile = new File(path);
                    if (aicodeFile != null && aicodeFile.exists()) {
                        break;
                    }
                }
            }
            if (aicodeFile == null) {
                return null;
            }

            String json = FileUtils.readFileToString(aicodeFile);
            Configuration configuration = JSON.parseObject(json, Configuration.class);
            TemplateEngineEnum templateEngineEnum = TemplateEngineEnum.getTemplate(configuration.getEngine());
            return templateEngineEnum;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //默认 freemarker
        return TemplateEngineEnum.Freemarker;
    }

    /**
     * 压缩文件
     *
     * @param project 项目信息
     */
    private void zipProject(Project project) {
        Setting settingWorkspace = settingDAO.loadByKey(Setting.Key.Workspace.name());
        String projectWorkspacePath = this.convertPath(settingWorkspace.getV(), project.getEnglishName(), true);

        Setting repositoryPathSetting = settingDAO.loadByKey(Setting.Key.Repository_Path.name());
        String destination = repositoryPathSetting.getV() + "/" + project.getEnglishName();


        //压缩文件
        try {
            File repositoryFile = new File(repositoryPathSetting.getV());
            if (!repositoryFile.exists()) {
                repositoryFile.mkdirs();
            }

            File zipFile = new File(destination + ".zip");
            if (zipFile.exists()) {
                zipFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        ZipTools.zip(destination, projectWorkspacePath);
        project.setDownloadUrl("/project/download/" + project.getEnglishName());
        projectDAO.update(project);
    }

    /**
     * 替换路径拼装中导致的 多余slash
     *
     * @param path     路径
     * @param filename 文件名
     * @return 绝对路径
     */
    private String convertPath(String path, String filename, boolean isAbsolute) {
        String absolutePath = "";
        if (isAbsolute) {
            absolutePath = "/" + path + "/" + filename;
        } else {
            absolutePath = path + "/" + filename;
        }
        absolutePath = absolutePath.replace("////", "/").replace("///", "/").replace("//", "/").replace("\\\\\\", "/").replace("\\\\", "/").replace("\\", "/");
        return absolutePath;
    }

    /**
     * 查找指定的路径
     *
     * @param root       根路径
     * @param targetPath 目标路径
     * @return 目标路径绝对路径
     */
    private String findPath(String root, String targetPath) {
        File templateFile = new File(root);
        File[] files = templateFile.listFiles();
        String path = null;
        for (File file : files) {
            if (file.isDirectory()) {
                if (!file.getName().equals(targetPath)) {
                    path = findPath(file.getAbsolutePath(), targetPath);
                } else {
                    path = file.getAbsolutePath();
                    break;
                }
            }
        }
        return path;
    }

}
