package com.rzhkj.project.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
import com.rzhkj.base.core.DataSourceProvider;
import com.rzhkj.base.core.StringHelper;
import com.rzhkj.base.core.typemapping.DatabaseDataTypesUtils;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.enums.YNEnum;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.ProjectException;
import com.rzhkj.core.tools.ConfigUtil;
import com.rzhkj.core.tools.JSON;
import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.dao.*;
import com.rzhkj.project.entity.*;
import com.rzhkj.project.service.ProjectSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
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
    private ProjectJobLogsDAO projectJobLogsDAO;

    @Resource
    private TableDAO tableDAO;
    @Resource
    private ColumnDAO columnDAO;
    @Resource
    private TableInfoDAO tableInfoDAO;
    @Resource
    private ColumnInfoDAO columnInfoDAO;
    @Resource
    private ProjectTablesDAO projectTablesDAO;
    @Resource
    private ProjectClassDAO projectClassDAO;
    @Resource
    private ClassInfoDAO classInfoDAO;
    @Resource
    private ClassAttributesDAO classAttributesDAO;

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

        //2.设定默认参数
        String basePackage = project.getBasePackage();
        basePackage = basePackage.endsWith(".") ? basePackage : basePackage + ".";
        project.setBasePackage(basePackage);
        project.setCode(String.valueOf(uidGenerator.getUID()));
        project.setState(ProjectStateEnum.Enable.name());
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
        flag = this.parseTable(code);
        if (!flag) {
            logger.error(BaseException.BaseExceptionEnum.Server_Error.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Server_Error);
        }

        //3.生成java类映射信息
        flag = this.parseTableToClass(code);
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

        String sql = project.getSql();
        String database = project.getEnglishName();
        if (StringUtils.isBlank(sql) || StringUtils.isBlank(database)) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        //2.创建数据库
        try {
            String driver = ConfigUtil.getValue("jdbc.driver", "jdbc.properties");
            String url = ConfigUtil.getValue("jdbc.url", "jdbc.properties");
            String username = ConfigUtil.getValue("jdbc.username", "jdbc.properties");
            String password = ConfigUtil.getValue("jdbc.password", "jdbc.properties");
            if (StringUtils.isBlank(url) || StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
                throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
            }
            new DataSourceProvider()
                    .getConnection(driver, url, username, password)
                    .createDatabase(database)
                    .executeSqlScript(sql)
                    .close();
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }


        //3.记录任务日志
        ProjectJobLogs projectJobLogs = new ProjectJobLogs();
        projectJobLogsDAO.insert(projectJobLogs);
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
    private boolean parseTable(String code) {
        //1.查询项目信息
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", code);
        Project project = projectDAO.load(map);
        if (project == null) {
            logger.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }

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

            TableInfo tableInfo = new TableInfo(String.valueOf(uidGenerator.getUID()), table.getTableName(), table.getTableComment(), columnList.size());
            projectTablesDAO.insert(new ProjectTables(project.getCode(), tableInfo.getCode()));//保存项目与表的关系
            tableInfoDAO.insert(tableInfo);//保存项目表信息


            List<ColumnInfo> columnInfoList = new ArrayList<>();
            columnList.forEach(column -> {
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.setCode(String.valueOf(uidGenerator.getUID()));
                columnInfo.setTableCode(tableInfo.getCode());
                columnInfo.setName(column.getColumnName());
                columnInfo.setType(column.getDataType());
                columnInfo.setNotes(column.getColumnComment());
                columnInfo.setDefaultValue(column.getColumnDefault());

                columnInfo.setIsPrimaryKey(DatabaseDataTypesUtils.isPrimaryKey(column.getDataType()) ? YNEnum.Y.name() : YNEnum.N.name());
                columnInfo.setIsDate(DatabaseDataTypesUtils.isDate(column.getDataType()) ? YNEnum.Y.name() : YNEnum.N.name());
                columnInfo.setIsState(DatabaseDataTypesUtils.isState(column.getDataType()) ? YNEnum.Y.name() : YNEnum.N.name());

                columnInfoList.add(columnInfo);
            });
            columnInfoDAO.batchInsert(columnInfoList);//保存表的列信息
        });

        project.setIsParseTable(YNEnum.Y.name());
        project.setUpdateTime(new Date());
        projectDAO.update(project);
        return true;
    }

    /**
     * 解析数据表到类信息
     * 1.查询表信息
     * 2.转化类信息
     * 3.查询列信息
     * 4.保存类信息
     *
     * @param code 项目编码
     * @return true/false
     */
    private boolean parseTableToClass(String code) {
        //1.查询表信息
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", code);
        Project project = projectDAO.load(map);
        List<ProjectTables> projectTablesList = project.getProjectTablesList();
        if (projectTablesList.isEmpty()) {
            logger.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }

        //2.转化类信息
        projectTablesList.forEach(projectTables -> {
            String tableName = projectTables.getTableInfo().getName();
            String packageName = tableName.substring(0, tableName.indexOf("_")) + ".po";

            ClassInfo classInfo = new ClassInfo();
            classInfo.setCode(String.valueOf(uidGenerator.getUID()));
            classInfo.setClassName(StringHelper.toJavaClassName(tableName));//转化类名
            classInfo.setTableCode(projectTables.getTableCode());
            classInfo.setBasePackage(project.getBasePackage() + packageName.toLowerCase());//设置包名
            classInfo.setNotes(projectTables.getTableInfo().getNotes());

            classInfoDAO.insert(classInfo);
            projectClassDAO.insert(new ProjectClass(project.getCode(), classInfo.getCode()));

            //3.查询列信息
            List<ColumnInfo> columnInfoList = projectTables.getTableInfo().getColumnInfos();
            columnInfoList.forEach(columnInfo -> {

                ClassAttributes classAttributes = new ClassAttributes();
                classAttributes.setCode(String.valueOf(uidGenerator.getUID()));
                classAttributes.setClassInfoCode(classInfo.getCode());
                classAttributes.setColumnCode(columnInfo.getCode());
                classAttributes.setName(StringHelper.toJavaVariableName(columnInfo.getName()));
                classAttributes.setType(DatabaseDataTypesUtils.getPreferredJavaType(columnInfo.getType()));
                classAttributes.setNotes(columnInfo.getNotes());
                classAttributes.setIsDate(columnInfo.getIsDate());
                classAttributes.setIsPrimaryKey(columnInfo.getIsPrimaryKey());
                classAttributes.setIsState(columnInfo.getIsState());

                //4.保存类信息
                classAttributesDAO.insert(classAttributes);
            });
        });

        return true;
    }


}