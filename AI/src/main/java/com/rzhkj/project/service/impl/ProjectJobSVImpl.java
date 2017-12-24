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
import com.rzhkj.core.enums.YNEnum;
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

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
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
        //3.获取模板信息
        map.put("fileType", FileTypeEnum.Java.name());
        List<ProjectFiles> projectFilesList = projectFilesDAO.query(map);
        logger.info("已获取项目模板信息");
        //4.获取技术信息
        List<ProjectFramwork> projectFramworkList = project.getProjectFramworkList();
        logger.info("已获取框架信息");
        List<ProjectCodeCatalog> projectCodeCatalogList = projectCodeCatalogDAO.query(map);
        projectCodeCatalogList.forEach(projectCodeCatalog -> {
            this.generatorJava(project.getAuthor(), project.getCopyright(), projectCodeCatalog, projectFilesList);
        });
        List<String> poPackageList = new ArrayList<>();
        projectCodeCatalogList.forEach(projectCodeCatalog -> {
            if (projectCodeCatalog.getProjectCodeModel().getModel().equals(ProjectCodeModelEnum.po.name())) {
                poPackageList.add(projectCodeCatalog.getClassPackage());
            }
        });
        List<String> poPackages = new ArrayList<>(new HashSet<>(poPackageList));

        map.put("fileType", FileTypeEnum.Xml.name());
        projectCodeCatalogList = projectCodeCatalogDAO.query(map);

        projectCodeCatalogList.forEach(projectCodeCatalog -> {
            this.generatorConfigure(project.getAuthor(), project.getCopyright(), projectCodeCatalog, poPackages, projectFramworkList);
        });

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
    private void generatorJava(String author, String copyright, ProjectCodeCatalog projectCodeCatalog, List<ProjectFiles> projectFilesList) {
        ClassInfo classInfo = projectCodeCatalog.getClassInfo();
        List<ClassAttributes> classAttributesList = new ArrayList<>();
        classInfo.getClassAttributes().forEach(classAttributes -> {
            if (classAttributes.getIsPrimaryKey().equals(YNEnum.Y.name())) {
                classAttributesList.add(classAttributes);
            }
        });
        Map<String, Object> model = Maps.newHashMap();
        model.put("basePackage", classInfo.getBasePackage());//包名
        model.put("package", projectCodeCatalog.getClassPackage());//包名
        model.put("className", projectCodeCatalog.getFileName());//类名
        model.put("classNameLower", projectCodeCatalog.getFileName().toLowerCase());//类名小写
        model.put("classSimpleName", classInfo.getClassName());//类名
        model.put("classSimpleNameLower", classInfo.getClassName().toLowerCase());//类名小写
        model.put("fields", classInfo.getClassAttributes());//类属性集合
        model.put("primaryKeys", classAttributesList);
        model.put("comment", classInfo.getNotes());//类注释
        model.put("namespace", classInfo.getClassName().toLowerCase());//命名空间
        model.put("copyright", copyright);//项目版权
        model.put("author", author);//作者
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
                FreemarkerHelper.generate(model, targetFilePath, templateFileName, templatePath);
                logger.info("已生成java 类[" + projectCodeCatalog.getAbsolutePath() + "]相关文件");
            }
        });
    }

    /**
     * 生成配置文件
     *
     * @param projectCodeCatalog  类信息
     * @param projectFramworkList 技术框架信息
     */
    private void generatorConfigure(String author, String copyright, ProjectCodeCatalog projectCodeCatalog, List<String> poPackageList, List<ProjectFramwork> projectFramworkList) {
        ClassInfo classInfo = projectCodeCatalog.getClassInfo();
        List<ClassAttributes> classAttributesList = new ArrayList<>();
        if (classInfo != null) {
            classInfo.getClassAttributes().forEach(classAttributes -> {
                if (classAttributes.getIsPrimaryKey().equals(YNEnum.Y.name())) {
                    classAttributesList.add(classAttributes);
                }
            });
        }
        Map<String, Object> model = Maps.newHashMap();
        if (projectCodeCatalog.getClassPackage() != null) {
            model.put("package", projectCodeCatalog.getClassPackage());//包名
        }
        model.put("className", projectCodeCatalog.getFileName());//类名
        model.put("classNameLower", projectCodeCatalog.getFileName().toLowerCase());//类名小写
        if (classInfo != null) {
            model.put("basePackage", classInfo.getBasePackage());//包名
            model.put("classSimpleName", classInfo.getClassName());//类名
            model.put("classSimpleNameLower", classInfo.getClassName().toLowerCase());//类名小写
            model.put("fields", classInfo.getClassAttributes());//类属性集合
            model.put("comment", classInfo.getNotes());//类注释
            model.put("namespace", classInfo.getClassName().toLowerCase());//命名空间
        }
        model.put("primaryKeys", classAttributesList);
        model.put("typeAliases", poPackageList);
        model.put("copyright", copyright);//项目版权
        model.put("author", author);//作者

        Setting setting = settingDAO.loadByKey(Setting.Key.Workspace.name());
        Setting templatePathSetting = settingDAO.loadByKey(Setting.Key.Template_Path.name());
        String projectPath = new HandleFuncs().getCurrentClassPath();
        projectFramworkList.forEach(projectFramwork -> {
            projectFramwork.getFrameworks().getFrameworksConfigureTemplateList().forEach(frameworksConfigureTemplate -> {
                if (frameworksConfigureTemplate.getCode().equals(projectCodeCatalog.getFrameworksConfigureTemplateCode())) {

                    String templateFileName = frameworksConfigureTemplate.getName();
                    String templatePath = (projectPath + templatePathSetting.getV() + frameworksConfigureTemplate.getPath()).replace("//", "/");
                    String targetFilePath = projectCodeCatalog.basePackage(setting.getV());

                    if (model.containsKey("values")) {
                        model.remove("values");
                    }

                    Map<String, Object> valueMap = Maps.newHashMap();
                    valueMap.put("templateCode", frameworksConfigureTemplate.getCode());
                    valueMap.put("projectCode", projectFramwork.getProjectCode());
                    valueMap.put("frameworkCode", projectFramwork.getFrameworkCode());
                    List<ProjectFrameworkAttributeValue> projectFrameworkAttributeValues = projectFrameworkAttributeValueDAO.query(valueMap);
                    model.put("values", projectFrameworkAttributeValues);//配置值集合
                    FreemarkerHelper.generate(model, targetFilePath, templateFileName, templatePath);
                    logger.info("已生成框架相关配置文件");
                }
            });
        });
    }

}
