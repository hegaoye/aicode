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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Map;


@Component
@Service
public class ProjectJobSVImpl extends BaseMybatisSVImpl<ProjectJob, Long> implements ProjectJobSV {

    @Resource
    private ProjectDAO projectDAO;

    @Resource
    private ProjectJobDAO projectJobDAO;

    @Resource
    private ProjectJobLogsDAO projectJobLogsDAO;

    @Resource
    private ClassInfoDAO classInfoDAO;

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
        //2.获取类信息
        List<ClassInfo> classInfoList = classInfoDAO.query(map);
        //3.获取模板信息
        map.put("fileType", FileTypeEnum.Java.name());
        List<ProjectFiles> projectFilesList = projectFilesDAO.query(map);
        //4.获取技术信息
        List<ProjectFramwork> projectFramworkList = project.getProjectFramworkList();
        classInfoList.forEach(classInfo -> {
            this.generatorJava(classInfo, projectFilesList);
            this.generatorXml(classInfo, projectFramworkList);
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
        //1.检测项目工作工作空间是否存在
        String basePath = project.getBasePackage().replaceAll("\\\\", "/");
        basePath = basePath.endsWith("/") ? basePath : basePath + "/";
        String projectPath = new HandleFuncs().getCurrentClassPath() + basePath;
        File file = new File(projectPath);
        if (!file.exists()) {
            //2.创建项目工作空间
            file.mkdirs();
        }
    }

    /**
     * 生成java文件
     *
     * @param classInfo        类信息
     * @param projectFilesList 项目文件信息
     */
    private void generatorJava(ClassInfo classInfo, List<ProjectFiles> projectFilesList) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("className", classInfo.getClassName());
        map.put("basePackage", classInfo.getBasePackage());
        map.put("classComment", classInfo.getBasePackage());
        map.put("fields", classInfo.getClassAttributes());

        projectFilesList.forEach(projectFiles -> {
            String templateFileName = projectFiles.getTemplates().getName();
            String templatePath = projectFiles.getTemplates().getPath();
            String targetFilePath = classInfo.basePackage();
            FreemarkerHelper.generate(map, targetFilePath, templateFileName, templatePath);
        });
    }

    private void generatorXml(ClassInfo classInfo, List<ProjectFramwork> projectFramworkList) {

    }
}
