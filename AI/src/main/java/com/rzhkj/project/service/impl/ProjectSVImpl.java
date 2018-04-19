package com.rzhkj.project.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
import com.rzhkj.base.core.typemapping.DatabaseDataTypesUtils;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.enums.YNEnum;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.ProjectException;
import com.rzhkj.core.tools.JSON;
import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.dao.*;
import com.rzhkj.project.entity.*;
import com.rzhkj.project.service.ProjectSV;
import com.rzhkj.setting.dao.SettingDAO;
import com.rzhkj.setting.entity.Setting;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Component
@Service
public class ProjectSVImpl extends BaseMybatisSVImpl<Project, Long> implements ProjectSV {

    @Resource
    private ProjectDAO projectDAO;
    @Resource
    private SettingDAO settingDAO;
    @Resource
    private DatabaseDAO databaseDAO;
    @Resource
    private ProjectModuleDAO projectModuleDAO;
    @Resource
    private ProjectCodeCatalogDAO projectCodeCatalogDAO;
    @Resource
    private ProjectJobLogsDAO projectJobLogsDAO;
    @Resource
    private TableDAO tableDAO;
    @Resource
    private ColumnDAO columnDAO;
    @Resource
    private MapClassTableDAO mapClassTableDAO;
    @Resource
    private MapFieldColumnDAO mapFieldColumnDAO;
    @Resource
    private MapRelationshipDAO mapRelationshipDAO;
    @Resource
    private ProjectMapDAO projectMapDAO;
    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectDAO;
    }


    /**
     * 创建项目
     * 1.判断必传参数
     * 2.设定默认参数
     * 3.保存
     *
     * @param project 实体
     * @throws BaseException
     */
    @Override
    public void build(Project project) {
        //1.判断必传参数
        if (project == null
                || StringUtils.isBlank(project.getName())
                || StringUtils.isBlank(project.getEnglishName())
                || StringUtils.isBlank(project.getPhone())
                || StringUtils.isBlank(project.getAuthor())
                || StringUtils.isBlank(project.getCopyright())
                || StringUtils.isBlank(project.getDatabaseType())
                || StringUtils.isBlank(project.getDescription())
                || StringUtils.isBlank(project.getLanguage())
                || StringUtils.isBlank(project.getBasePackage())
                ) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("englishName", project.getEnglishName());
        Project projectLoad = projectDAO.load(map);
        if (projectLoad != null) {
            logger.error(BaseException.BaseExceptionEnum.Exists.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Exists);
        }

        //2.设定默认参数
        String basePackage = project.getBasePackage();
        basePackage = basePackage.endsWith(".") ? basePackage.substring(0, basePackage.lastIndexOf(".")) : basePackage;
        project.setBasePackage(basePackage);
        project.setCode(String.valueOf(uidGenerator.getUID()));
        project.setState(ProjectStateEnum.Enable.name());
        project.setDownloadUrl("DownloadUrl");
        project.setCreateTime(new Date());
        project.setUpdateTime(new Date());

        //3.保存
        logger.info("创建项目 ===> " + JSON.toJSONString(project));
        super.save(project);
    }


    /**
     * 删除项目
     * 1.判断项目是否存在
     * 2.删除项目
     *
     * @param code 项目编码
     */
    @Override
    public void delete(String code) {
        if (StringTools.isEmpty(code)) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        //1.判断项目是否存在
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", code);
        Project project = projectDAO.load(map);

        //2.删除项目
        project.setState(ProjectStateEnum.Delete.name());
        projectDAO.update(project);
    }

    /**
     * 执行脚本
     * 1.创建数据库
     * 2.解析数据库信息
     * 3.生成java类映射信息
     *
     * @param code 项目编码
     */
    @Override
    public void execute(String code) {
        //1.创建数据库
        boolean flag = this.createDatabase(code);
        if (!flag) {
            logger.error(BaseException.BaseExceptionEnum.Server_Error.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Server_Error);
        }

        //2.解析数据库信息
        flag = this.parse(code);
        if (!flag) {
            logger.error(BaseException.BaseExceptionEnum.Server_Error.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Server_Error);
        }

    }


    /**
     * 创建数据库
     * 1.判断必要参数
     * 2.创建数据库
     * 3.记录任务日志
     *
     * @param code 项目编码
     * @return true/false
     */
    private boolean createDatabase(String code) {
        //1.判断必要参数
        if (code == null) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        Map<String, Object> map = Maps.newHashMap();
        map.put("code", code);
        Project project = projectDAO.load(map);
        if (project == null) {
            logger.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }

        String database = project.getEnglishName();
        if (project.getProjectSqlList().isEmpty() || StringUtils.isBlank(database)) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        //2.创建数据库
        map.clear();
        map.put("schemaName", database);
        int i = databaseDAO.count(map);
        if (i == 0) {
            Setting setting = settingDAO.loadByKey(Setting.Key.DefaultDatabase.name());
            if (!project.getProjectSqlList().isEmpty()) {
                project.getProjectSqlList().forEach(projectSql -> {
                    if (projectSql.getState().equals(ProjectSqlStateEnum.Enable.name())) {
                        databaseDAO.createDatabase(projectSql.getTsql(), setting.getV());
                    }
                });
            }
        }


        //3.记录任务日志
//        ProjectJobLogs projectJobLogs = new ProjectJobLogs();
//        projectJobLogsDAO.insert(projectJobLogs);
        return true;
    }

    /**
     * 解析数据库
     * 1.查询项目信息
     * 2.查询数据库信息
     * 3.保存解析信息
     *
     * @param code 项目编码
     * @return true/false
     */
    private boolean parse(String code) {
        //1.查询项目信息
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", code);
        Project project = projectDAO.load(map);
        if (project == null) {
            logger.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }
        List<ProjectMap> projectMapList = project.getProjectMapList();
        for (ProjectMap projectMap : projectMapList) {
            mapClassTableDAO.delete(projectMap.getMapClassTableCode());
            mapFieldColumnDAO.delete(projectMap.getMapClassTableCode());
            mapRelationshipDAO.delete(projectMap.getMapClassTableCode());
        }
        map.clear();
        map.put("projectCode", code);
        projectMapDAO.delete(map);

        //2.查询数据库信息
        map.clear();
        map.put("database", project.getEnglishName());
        List<Table> tableList = tableDAO.query(map);
        if (tableList.isEmpty()) {
            logger.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }

        //3.保存解析信息
        tableList.forEach(table -> {
            Map<String, Object> columnMap = Maps.newHashMap();
            columnMap.put("database", project.getEnglishName());
            columnMap.put("tableName", table.getTableName());
            List<Column> columnList = columnDAO.query(columnMap);
            if (columnList.isEmpty()) {
                logger.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
                throw new ProjectException(BaseException.BaseExceptionEnum.Result_Not_Exist);
            }

            MapClassTable mapClassTable = new MapClassTable(String.valueOf(uidGenerator.getUID()), table.getTableName(), table.getTableComment());
            mapClassTable.toJava();
            projectMapDAO.insert(new ProjectMap(project.getCode(), mapClassTable.getCode()));//保存项目与表的关系
            mapClassTableDAO.insert(mapClassTable);//保存项目表信息


            List<MapFieldColumn> mapFieldColumns = new ArrayList<>();
            columnList.forEach(column -> {
                MapFieldColumn mapFieldColumn = new MapFieldColumn();
                mapFieldColumn.setCode(String.valueOf(uidGenerator.getUID()));
                mapFieldColumn.setMapClassTableCode(mapClassTable.getCode());
                mapFieldColumn.setColumn(column.getColumnName());
                mapFieldColumn.setSqlType(column.getDataType());
                mapFieldColumn.setNotes(column.getColumnComment());
                mapFieldColumn.setDefaultValue(column.getColumnDefault());
                mapFieldColumn.setIsPrimaryKey(DatabaseDataTypesUtils.isPrimaryKey(column.getColumnKey()) ? YNEnum.Y.name() : YNEnum.N.name());
                mapFieldColumn.setIsDate(DatabaseDataTypesUtils.isDate(column.getDataType()) ? YNEnum.Y.name() : YNEnum.N.name());
                mapFieldColumn.setIsState(DatabaseDataTypesUtils.isState(column.getDataType()) ? YNEnum.Y.name() : YNEnum.N.name());
                mapFieldColumn.toJava();
                mapFieldColumns.add(mapFieldColumn);
            });
            mapFieldColumnDAO.batchInsert(mapFieldColumns);//保存表的列信息
        });

        project.setIsParseTable(YNEnum.Y.name());
        project.setIsParseClass(YNEnum.Y.name());
        project.setUpdateTime(new Date());
        projectDAO.update(project);
        return true;
    }

}
