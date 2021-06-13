/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.core.enums.YNEnum;
import com.aicode.core.exceptions.BaseException;
import com.aicode.core.exceptions.ProjectException;
import com.aicode.core.tools.FileUtil;
import com.aicode.core.tools.HandleFuncs;
import com.aicode.core.tools.StringTools;
import com.aicode.core.tools.core.typemapping.DatabaseDataTypesUtils;
import com.aicode.database.dao.ColumnDAO;
import com.aicode.database.dao.DatabaseDAO;
import com.aicode.database.dao.TableDAO;
import com.aicode.database.entity.Column;
import com.aicode.database.entity.Table;
import com.aicode.map.dao.MapClassTableDAO;
import com.aicode.map.dao.MapFieldColumnDAO;
import com.aicode.map.dao.MapRelationshipDAO;
import com.aicode.map.entity.MapClassTable;
import com.aicode.map.entity.MapFieldColumn;
import com.aicode.map.entity.MapRelationship;
import com.aicode.project.dao.*;
import com.aicode.project.dao.mapper.ProjectMapper;
import com.aicode.project.entity.*;
import com.aicode.setting.dao.SettingDAO;
import com.aicode.setting.entity.Setting;
import com.aicode.setting.entity.SettingKey;
import com.alibaba.fastjson.JSON;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 项目信息
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

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
    private ProjectRepositoryAccountDAO projectRepositoryAccountDAO;

    @Resource
    private ProjectFramworkDAO projectFramworkDAO;
    @Resource
    private ProjectJobDAO projectJobDAO;
    @Resource
    private ProjectSqlDAO projectSqlDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(Project project) {
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
            log.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        Project projectLoad = projectDAO.selectOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getEnglishName, project.getEnglishName()));
        if (projectLoad != null) {
            log.error(BaseException.BaseExceptionEnum.Exists.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Exists);
        }

        //2.设定默认参数
        String basePackage = project.getBasePackage();
        basePackage = basePackage.endsWith(".") ? basePackage.substring(0, basePackage.lastIndexOf(".")) : basePackage;
        project.setBasePackage(basePackage);
        project.setCode(String.valueOf(uidGenerator.getUID()));
        project.setState(ProjectState.Enable.name());
        if (StringUtils.isBlank(project.getIsIncrement())) {
            project.setIsIncrement(YNEnum.N.name());
        }
        project.setDownloadUrl("DownloadUrl");
        project.setIsIncrement(YNEnum.N.name());
        project.setCreateTime(new Date());
        project.setUpdateTime(new Date());

        //3.保存
        log.info("创建项目 ===> " + JSON.toJSONString(project));
        return super.save(project);
    }

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<Project>
     */
    @Override
    public List<Project> list(QueryWrapper<Project> queryWrapper, int offset, int limit) {
        return projectDAO.list(queryWrapper, offset, limit);
    }

    /**
     * 删除项目
     * 1.判断项目是否存在
     * 2.删除数据
     * 3.删除项目
     *
     * @param code 项目编码
     */
    @Override
    public void delete(String code) {
        if (StringTools.isEmpty(code)) {
            log.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        //1.判断项目是否存在
        Project project = projectDAO.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getCode, code));

        //2.删除数据
        project.setState(ProjectState.Delete.name());
        projectDAO.delete(new LambdaQueryWrapper<Project>()
                .eq(Project::getCode, project.getCode()));

        //删除项目技术框架数据
        projectFramworkDAO.delete(new LambdaQueryWrapper<ProjectFramwork>()
                .eq(ProjectFramwork::getProjectCode, project.getCode()));

        //删除项目的sql脚本
        projectSqlDAO.delete(new LambdaQueryWrapper<ProjectSql>()
                .eq(ProjectSql::getProjectCode, project.getCode()));

        //删除项目的仓库账户
        projectRepositoryAccountDAO.delete(new LambdaQueryWrapper<ProjectRepositoryAccount>()
                .eq(ProjectRepositoryAccount::getProjectCode, project.getCode()));

        //删除项目模块
        projectModuleDAO.delete(new LambdaQueryWrapper<ProjectModule>()
                .eq(ProjectModule::getProjectCode, project.getCode()));

        //删除job
        projectJobDAO.delete(new LambdaQueryWrapper<ProjectJob>()
                .eq(ProjectJob::getProjectCode, project.getCode()));

        //删除构建日志
        projectJobLogsDAO.delete(new LambdaQueryWrapper<ProjectJobLogs>()
                .eq(ProjectJobLogs::getCode, project.getCode()));

        List<ProjectMap> projectMaps = projectMapDAO.selectList(new LambdaQueryWrapper<ProjectMap>()
                .eq(ProjectMap::getProjectCode, project.getCode()));

        for (ProjectMap projectMap : projectMaps) {
            mapFieldColumnDAO.delete(new LambdaQueryWrapper<MapFieldColumn>()
                    .eq(MapFieldColumn::getMapClassTableCode, projectMap.getMapClassTableCode()));

            mapRelationshipDAO.delete(new LambdaQueryWrapper<MapRelationship>()
                    .eq(MapRelationship::getMapClassTableCode, projectMap.getMapClassTableCode()));

            projectMapDAO.delete(new LambdaQueryWrapper<ProjectMap>()
                    .eq(ProjectMap::getMapClassTableCode, projectMap.getMapClassTableCode()));
        }


        //3.删除项目
        //项目源码删除
        Setting settingWorkspace = settingDAO.selectOne(new LambdaQueryWrapper<Setting>()
                .eq(Setting::getK, SettingKey.Workspace.name()));
        String projectPath = new HandleFuncs().getCurrentClassPath() + settingWorkspace.getV() + "/" + project.getEnglishName();
        projectPath = projectPath.replace("//", "/");
        File file = new File(projectPath);
        if (file.exists()) {
            FileUtil.delFolder(projectPath);
        }
        //项目zip删除
        Setting settingRepositoryPath = settingDAO.selectOne(new LambdaQueryWrapper<Setting>()
                .eq(Setting::getK, SettingKey.Repository_Path.name()));
        String repositoryPath = new HandleFuncs().getCurrentClassPath() + settingRepositoryPath.getV() + "/" + project.getEnglishName() + ".zip";
        repositoryPath = repositoryPath.replace("//", "/");
        File repositoryFile = new File(repositoryPath);
        if (repositoryFile.exists()) {
            repositoryFile.delete();
        }
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

        //2.解析数据库信息
        if (flag) {
            flag = this.parse(code);
            if (!flag) {
                log.error(BaseException.BaseExceptionEnum.Server_Error.toString());
                throw new ProjectException(BaseException.BaseExceptionEnum.Server_Error);
            }
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
            log.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        Project project = projectDAO.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getCode, code));
        if (project == null) {
            log.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }

        List<ProjectSql> projectSqls = projectSqlDAO.selectList(new LambdaQueryWrapper<ProjectSql>().eq(ProjectSql::getProjectCode, code));
        String database = project.getEnglishName();
        if (projectSqls.isEmpty() || StringUtils.isBlank(database)) {
            log.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        //2.创建数据库
        long i = databaseDAO.count(database);
        if (i <= 0) {
            Setting setting = settingDAO.selectOne(new LambdaQueryWrapper<Setting>()
                    .eq(Setting::getK, SettingKey.DefaultDatabase.name()));

            if (!projectSqls.isEmpty()) {
                projectSqls.forEach(projectSql -> {
                    if (projectSql.getState().equals(ProjectSqlState.Enable.name())) {
                        databaseDAO.createDatabase(database,projectSql.getTsql(), setting.getV());
                    }
                });
                return true;
            }
        }

        return false;
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
        Project project = projectDAO.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getCode, code));
        if (project == null) {
            log.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }

        List<ProjectMap> projectMapList = projectMapDAO.selectList(new LambdaQueryWrapper<ProjectMap>().eq(ProjectMap::getProjectCode, code));
        for (ProjectMap projectMap : projectMapList) {
            mapClassTableDAO.delete(new LambdaQueryWrapper<MapClassTable>().eq(MapClassTable::getCode, projectMap.getMapClassTableCode()));
            mapFieldColumnDAO.delete(new LambdaQueryWrapper<MapFieldColumn>().eq(MapFieldColumn::getCode, projectMap.getMapClassTableCode()));
            mapRelationshipDAO.delete(new LambdaQueryWrapper<MapRelationship>().eq(MapRelationship::getCode, projectMap.getMapClassTableCode()));
        }

        projectMapDAO.delete(new LambdaQueryWrapper<ProjectMap>().eq(ProjectMap::getProjectCode, code));

        //2.查询数据库信息
        List<Table> tableList = tableDAO.list(project.getEnglishName());
        if (tableList.isEmpty()) {
            log.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }

        //3.保存解析信息
        tableList.forEach(table -> {
            List<Column> columnList = columnDAO.list(project.getEnglishName(), table.getTableName());
            if (columnList.isEmpty()) {
                log.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
                throw new ProjectException(BaseException.BaseExceptionEnum.Result_Not_Exist);
            }

            MapClassTable mapClassTable = new MapClassTable(String.valueOf(uidGenerator.getUID()), table.getTableName(), table.getTableComment());
            mapClassTable.toJava();

            //保存项目与表的关系
            projectMapDAO.insert(ProjectMap.builder()
                    .projectCode(project.getCode())
                    .mapClassTableCode(mapClassTable.getCode())
                    .build());

            //保存项目表信息
            mapClassTableDAO.insert(mapClassTable);


            List<MapFieldColumn> mapFieldColumns = new ArrayList<>();
            columnList.forEach(column -> {
                MapFieldColumn mapFieldColumn = new MapFieldColumn();
                mapFieldColumn.setCode(String.valueOf(uidGenerator.getUID()));
                mapFieldColumn.setMapClassTableCode(mapClassTable.getCode());
                mapFieldColumn.setColumn(column.getColumnName());
                mapFieldColumn.setSqlType(column.getTypeName());
                mapFieldColumn.setNotes(column.getRemarks());
                mapFieldColumn.setDefaultValue(column.getColumnDefault());
                mapFieldColumn.setIsPrimaryKey(DatabaseDataTypesUtils.isPrimaryKey(column.getIsNullable()) ? YNEnum.Y.name() : YNEnum.N.name());
                mapFieldColumn.setIsDate(DatabaseDataTypesUtils.isDate(column.getTypeName()) ? YNEnum.Y.name() : YNEnum.N.name());

                mapFieldColumn.setIsState(YNEnum.N.name());
//                mapFieldColumn.setIsState(DatabaseDataTypesUtils.isStateOrType(column.getDataType()) ? YNEnum.Y.name() : YNEnum.N.name());
                if (YNEnum.N == YNEnum.getYN(mapFieldColumn.getIsState())) {
                    Map<String, Object> objectMap = StringTools.getStateOrType(column.getRemarks());
                    YNEnum ynEnum = null != objectMap && !objectMap.isEmpty() ? YNEnum.Y : YNEnum.N;
                    mapFieldColumn.setIsState(ynEnum.name());
                }
                mapFieldColumn.toJava();
                mapFieldColumns.add(mapFieldColumn);
            });
            //保存表的列信息
            mapFieldColumnDAO.batchInsert(mapFieldColumns);
        });

        project.setIsParseTable(YNEnum.Y.name());
        project.setIsParseClass(YNEnum.Y.name());
        project.setUpdateTime(new Date());
        projectDAO.update(project, new LambdaQueryWrapper<Project>()
                .eq(Project::getCode, project.getCode()));
        return true;
    }
}


