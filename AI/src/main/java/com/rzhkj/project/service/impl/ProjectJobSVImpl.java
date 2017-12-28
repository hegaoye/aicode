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
        String projectPath = this.buildProject(project);
        logger.info("创建工作空间库完成");

        //2.获取类信息
        List<ProjectMap> projectMapList = project.getProjectMapList();
        List<MapClassTable> mapClassTableList = new ArrayList<>();
        projectMapList.forEach(projectMap -> {
            mapClassTableList.add(projectMap.getMapClassTable());
        });
        //3.获取模板信息
        List<ProjectFramwork> projectFramworkList = project.getProjectFramworkList();
        //4.生成源码
        projectFramworkList.forEach(projectFramwork -> {
            List<FrameworksTemplate> frameworksTemplateList = projectFramwork.getFrameworks().getFrameworksTemplateList();
            frameworksTemplateList.forEach(frameworksTemplate -> {
                projectMapList.forEach(projectMap -> {
                    this.generator(projectPath, project, projectMap.getMapClassTable(), frameworksTemplate, mapClassTableList);
                });
            });
        });

        //5.获取模块信息

        //6.获取版本控制管理信息

    }

    /**
     * 1.检测项目工作工作空间是否存在
     * 2.创建项目工作空间
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
            file.mkdirs();
        }
        return projectPath;
    }


    /**
     * 生成源码文件
     *
     * @param projectPath
     * @param project
     * @param mapClassTable
     * @param frameworksTemplate
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

        Map<String, Object> model = Maps.newHashMap();
        model.put("basePackage", project.getBasePackage());//包名
        model.put("table", mapClassTable);//表对象
        model.put("tableName", mapClassTable.getTableName());//表名

        model.put("classes", mapClassTableList);//类对象
        model.put("class", mapClassTable);//类对象
        model.put("className", mapClassTable.getClassName());//类名
        model.put("classNameLower", mapClassTable.getClassName().toLowerCase());//类名小写

        model.put("columns", mapFieldColumnList);//列对象集合
        model.put("pkColumns", mapFieldColumnPks);//主键数据信息
        model.put("notPkColumns", mapFieldColumnNotPks);//非主键数据信息

//        model.put("relationships", mapClassTable.getMapRelationshipList());//关联关系

        model.put("fields", mapFieldColumnList);//类属性集合
        model.put("pkFields", mapFieldColumnPks);//主键数据信息
        model.put("notPkFields", mapFieldColumnNotPks);//非主键主键数据信息

        model.put("notes", mapClassTable.getNotes());//类注释
        model.put("copyright", project.getCopyright());//项目版权
        model.put("author", project.getAuthor());//作者

        Setting settingTemplatePath = settingDAO.loadByKey(Setting.Key.Template_Path.name());
        String targetFilePath = projectPath + "/" + frameworksTemplate.getPath().replace("${basepackage}", project.getBasePackage().replace(".", "/")).replace("${className}", mapClassTable.getClassName());
        String templatePath = new HandleFuncs().getCurrentClassPath() + "/" + settingTemplatePath.getV() + "/" + frameworksTemplate.getPath();

        FreemarkerHelper.generate(model, targetFilePath, templatePath);
    }
}
