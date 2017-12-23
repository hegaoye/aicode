/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
import com.rzhkj.base.core.FreemarkerHelper;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.ProjectJobException;
import com.rzhkj.core.tools.HandleFuncs;
import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.dao.*;
import com.rzhkj.project.entity.*;
import com.rzhkj.project.service.ProjectJobSV;
import com.rzhkj.setting.dao.SettingDAO;
import com.rzhkj.setting.entity.Setting;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
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
    private ProjectJobLogsDAO projectJobLogsDAO;

    @Resource
    private ClassInfoDAO classInfoDAO;

    @Resource
    private ProjectFrameworkAttributeValueDAO projectFrameworkAttributeValueDAO;

    @Resource
    private TemplatesDAO templatesDAO;

    @Resource
    private ProjectFilesDAO projectFilesDAO;

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
        if (projectJob == null || StringTools.isEmpty(projectJob.getProjectCode(), projectJob.getDescription(), projectJob.getName())) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectJobException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        //2.设置默认属性
        projectJob.setCode(String.valueOf(uidGenerator.getUID()));
        projectJob.setNumber(0);
        projectJob.setState(ProjectJobStateEnum.Create.name());
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
     * 4.获取技术信息
     * 5.获取工具信息
     * 6.获取第三方模块信息
     * 7.获取版本控制管理信息
     *
     * @param code 任务编码
     */
    @Override
    public void execute(String code) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", code);
        ProjectJob projectJob = projectJobDAO.load(map);
        if (projectJob == null) {
            logger.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ProjectJobException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }
        //1.创建项目
        map.clear();
        map.put("projectCode", projectJob.getProjectCode());
        Project project = projectDAO.load(map);
        this.buildProject(project);
        logger.info("创建工作空间库完成");
        //2.获取类信息
        List<ClassInfo> classInfoList = classInfoDAO.query(map);
        logger.info("已解析所有表信息");
        //3.获取模板信息
        map.put("fileType", FileTypeEnum.Java.name());
        List<ProjectFiles> projectFilesList = projectFilesDAO.query(map);
        logger.info("已获取项目模板信息");
        //4.获取技术信息
        List<ProjectFramwork> projectFramworkList = project.getProjectFramworkList();
        logger.info("已获取框架信息");
        List<ProjectCodeCatalog> projectCodeCatalogList = projectCodeCatalogDAO.query(map);
        projectCodeCatalogList.forEach(projectCodeCatalog -> {
            this.generatorJava(projectCodeCatalog, projectFilesList);

//            this.generatorConfigure(classInfo, projectFramworkList);
            logger.info("已生成框架相关配置文件");
        });
//        classInfoList.forEach(classInfo -> {
//            this.generatorJava(classInfo, projectFilesList);
//            logger.info("已生成java 类[" + classInfo.getClassName() + "]相关文件");
//            this.generatorConfigure(classInfo, projectFramworkList);
//            logger.info("已生成框架相关配置文件");
//        });

        //5.获取工具信息

        //6.获取第三方模块信息

        //7.获取版本控制管理信息
    }

    /**
     * 1.检测项目工作工作空间是否存在
     * 2.创建项目工作空间
     *
     * @param project
     */
    private void buildProject(Project project) {
        Setting settingWorkspace = settingDAO.loadByKey(Setting.Key.Workspace.name());
        String workspace = new HandleFuncs().getCurrentClassPath() + settingWorkspace.getV() + "/" + project.getEnglishName();
        workspace = workspace.replace("//", "/");
        //1.检测项目工作工作空间是否存在
        File file = new File(workspace);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 生成java文件
     *
     * @param projectCodeCatalog 项目源码目录
     * @param projectFilesList   项目文件信息
     */
    private void generatorJava(ProjectCodeCatalog projectCodeCatalog, List<ProjectFiles> projectFilesList) {
        ClassInfo classInfo = projectCodeCatalog.getClassInfo();
        Map<String, Object> map = Maps.newHashMap();
        map.put("className", projectCodeCatalog.getFileName());
        map.put("basePackage", classInfo.getBasePackage());
        map.put("classComment", classInfo.getBasePackage());
        map.put("fields", classInfo.getClassAttributes());
        Setting setting = settingDAO.loadByKey(Setting.Key.Workspace.name());
        Setting templatePathSetting = settingDAO.loadByKey(Setting.Key.Template_Path.name());
        String projectPath = new HandleFuncs().getCurrentClassPath();
        projectFilesList.forEach(projectFiles -> {
            if (projectFiles.getTemplates().getModel().equals(projectCodeCatalog.getProjectCodeModel().getModel())) {
                String templateFileName = projectFiles.getTemplates().getName();
                String templatePath = projectFiles.getTemplates().getPath();
                String targetFilePath = projectCodeCatalog.basePackage(setting.getV());
                templatePath = projectPath + templatePathSetting.getV() + templatePath;
                templatePath = templatePath.replace("//", "/");
                FreemarkerHelper.generate(map, targetFilePath, templateFileName, templatePath);
                logger.info("已生成java 类[" + projectCodeCatalog.getAbsolutePath() + "]相关文件");
                projectFilesDAO.update(projectFiles);
            }
        });
    }

    /**
     * 生成配置文件
     *
     * @param classInfo           类信息
     * @param projectFramworkList 技术框架信息
     */
    private void generatorConfigure(ClassInfo classInfo, List<ProjectFramwork> projectFramworkList) {
        Map<String, Object> model = Maps.newHashMap();
        model.put("className", classInfo.getClassName());
        model.put("basePackage", classInfo.getBasePackage());
        model.put("classComment", classInfo.getBasePackage());
        model.put("fields", classInfo.getClassAttributes());
        List<Map<String, Object>> modelList = new ArrayList<>();
        final String[] targetFilePath = {null};
        final String[] templateFileName = {null};
        final String[] templatePath = {null};
        projectFramworkList.forEach(projectFramwork -> {
            projectFramwork.getFrameworks().getFrameworksConfigureTemplateList().forEach(frameworksConfigureTemplate -> {
                templateFileName[0] = frameworksConfigureTemplate.getName();
                templatePath[0] = frameworksConfigureTemplate.getPath();
                String filePath = new HandleFuncs().getCurrentClassPath()
                        + frameworksConfigureTemplate.getSaveFilePath()
                        + "/" + frameworksConfigureTemplate.getName()
                        + "." + StringUtils.uncapitalize(frameworksConfigureTemplate.getFileType());
                targetFilePath[0] = filePath.replace("//", "/");
                if (model.containsKey("values")) {
                    model.remove("values");
                }
                Map<String, Object> valueMap = Maps.newHashMap();
                valueMap.put("templateCode", frameworksConfigureTemplate.getCode());
                valueMap.put("projectCode", projectFramwork.getProjectCode());
                valueMap.put("frameworkCode", projectFramwork.getFrameworkCode());
                List<ProjectFrameworkAttributeValue> projectFrameworkAttributeValues = projectFrameworkAttributeValueDAO.query(valueMap);
                model.put("values", projectFrameworkAttributeValues);
            });
        });

        if (!modelList.isEmpty() && targetFilePath[0] != null && templateFileName[0] != null && templatePath[0] != null) {
            modelList.forEach(entityMap -> {
                FreemarkerHelper.generate(entityMap, targetFilePath[0], templateFileName[0], templatePath[0]);
            });
        }
    }

}
