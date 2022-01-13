/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.core.enums.SqlConvertEnum;
import com.aicode.core.exceptions.BaseException;
import com.aicode.core.exceptions.ProjectException;
import com.aicode.project.dao.ProjectDAO;
import com.aicode.project.dao.ProjectSqlDAO;
import com.aicode.project.dao.mapper.ProjectSqlMapper;
import com.aicode.project.entity.Project;
import com.aicode.project.entity.ProjectSql;
import com.aicode.project.entity.ProjectSqlState;
import com.alibaba.druid.util.StringUtils;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 项目sql脚本
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class ProjectSqlServiceImpl extends ServiceImpl<ProjectSqlMapper, ProjectSql> implements ProjectSqlService {

    @Autowired
    private ProjectSqlDAO projectSqlDAO;
    @Autowired
    private ProjectDAO projectDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(ProjectSql entity) {
        if (entity == null || StringUtils.isEmpty(entity.getProjectCode()) || StringUtils.isEmpty(entity.getTsql())) {
            log.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        entity.setCode(String.valueOf(uidGenerator.getUID()));
        entity.setState(ProjectSqlState.Enable.name());
        String tsql = entity.getTsql();
        Project project = projectDAO.selectOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getCode, entity.getProjectCode()));
        String sqlHeader = "";

        //sql 转换,符合 h2 兼容mysql 模式
        tsql = this.convertSql(tsql);

        sqlHeader += "CREATE SCHEMA IF NOT EXISTS " + project.getEnglishName() + ";\n";
        sqlHeader += "USE " + project.getEnglishName() + ";\n";
        tsql = sqlHeader + tsql;
        entity.setTsql(tsql);
        return super.save(entity);
    }

    /**
     * sql 自动过滤
     *
     * @param tsql sql语句
     * @return sql
     */
    private String convertSql(String tsql) {
        tsql = SqlConvertEnum.DROP.replaceAll(tsql);
        tsql = SqlConvertEnum.CREATE_DATABASE.replaceAll(tsql);
        tsql = SqlConvertEnum.SET_NAMES.replaceAll(tsql);
        tsql = SqlConvertEnum.SET_FOREIGN_KEY_CHECKS_0.replaceAll(tsql);
        tsql = SqlConvertEnum.SET_FOREIGN_KEY_CHECKS_1.replaceAll(tsql);
        tsql = SqlConvertEnum.DEFAULT_NULL.replaceAll(tsql);
        tsql = SqlConvertEnum.COLLATE.replaceAll(tsql);
        tsql = SqlConvertEnum.AUTO_INCREMENT.replaceAll(tsql);
        tsql = SqlConvertEnum.ENGINE.replaceAll(tsql);
        tsql = SqlConvertEnum.DEFAULT_CHARSET.replaceAll(tsql);
        tsql = SqlConvertEnum.COMMENT.replaceAll(tsql);
        tsql = SqlConvertEnum.SQL_COMMENT.replaceAll(tsql);

        return tsql;
    }

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<ProjectSql>
     */
    @Override
    public List<ProjectSql> list(QueryWrapper<ProjectSql> queryWrapper, int offset, int limit) {
        return projectSqlDAO.list(queryWrapper, offset, limit);
    }
}


