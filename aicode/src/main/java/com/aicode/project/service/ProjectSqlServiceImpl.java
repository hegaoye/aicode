/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.core.exceptions.BaseException;
import com.aicode.core.exceptions.ProjectException;
import com.aicode.project.dao.ProjectDAO;
import com.aicode.project.entity.Project;
import com.aicode.project.entity.ProjectSqlState;
import com.alibaba.druid.util.StringUtils;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aicode.project.dao.ProjectSqlDAO;
import com.aicode.project.dao.mapper.ProjectSqlMapper;
import com.aicode.project.entity.ProjectSql;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;
import java.util.Map;


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
                .eq(Project::getCode,entity.getProjectCode()));
        String sqlHeader = "";

        //是否存在 drop  database  if  exists
        tsql = tsql.replaceAll("\\s*(drop|DROP)\\s+(database|DATABASE)\\s+(if|IF){0,1}\\s+(exists|EXISTS){0,1}\\s+\\w+;{0,1}", "");
        tsql = tsql.replaceAll("\\s*(create|CREATE)\\s+(database|DATABASE)\\s+\\w+;{0,1}", "");
        tsql = tsql.replaceAll("^\\s*(use|USE)\\s+\\w+;", "");

        sqlHeader = "DROP DATABASE if EXISTS " + project.getEnglishName() + ";\n";
        sqlHeader += "CREATE DATABASE " + project.getEnglishName() + ";\n";
        sqlHeader += "USE " + project.getEnglishName() + ";\n";
        tsql = sqlHeader + tsql;
        entity.setTsql(tsql);
        return super.save(entity);
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


