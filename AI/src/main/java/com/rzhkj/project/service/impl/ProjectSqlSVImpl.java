/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.ProjectException;
import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.dao.ProjectDAO;
import com.rzhkj.project.dao.ProjectSqlDAO;
import com.rzhkj.project.entity.Project;
import com.rzhkj.project.entity.ProjectSql;
import com.rzhkj.project.entity.ProjectSqlStateEnum;
import com.rzhkj.project.service.ProjectSqlSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Component
@Service
public class ProjectSqlSVImpl extends BaseMybatisSVImpl<ProjectSql, Long> implements ProjectSqlSV {

    @Resource
    private ProjectSqlDAO projectSqlDAO;

    @Resource
    private ProjectDAO projectDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectSqlDAO;
    }

    @Override
    public void save(ProjectSql entity) throws BaseException {
        if (entity == null || StringTools.isEmpty(entity.getProjectCode()) || StringTools.isEmpty(entity.getTsql())) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        entity.setCode(String.valueOf(uidGenerator.getUID()));
        entity.setState(ProjectSqlStateEnum.Enable.name());
        String tsql = entity.getTsql();
        Map<String, Object> map = Maps.newHashMap();
        map.put("projectCode", entity.getProjectCode());
        Project project = projectDAO.load(map);
        String sqlHeader = "";
        //是否存在 drop  database  if  exists
        tsql = tsql.replaceAll("\\s*(drop|DROP)\\s+(database|DATABASE)\\s+(if|IF){0,1}\\s+(exists|EXISTS){0,1}\\s+\\w+;{0,1}", "");
        tsql = tsql.replaceAll("\\s*(create|CREATE)\\s+(database|DATABASE)\\s+\\w+;{0,1}", "");
        tsql = tsql.replaceAll("\\s*(use|USE)\\s+\\w+;{0,1}", "");
        sqlHeader = "DROP DATABASE if EXISTS " + project.getEnglishName() + ";\n";
        sqlHeader += "CREATE DATABASE " + project.getEnglishName() + ";\n";
        sqlHeader += "USE " + project.getEnglishName() + ";\n";
        tsql = sqlHeader + " " + tsql;
        entity.setTsql(tsql);
        super.save(entity);
    }

    public static void main(String[] args) {
        String s = "    /*\n" +
                "Navicat MariaDB Data Transfer\n" +
                "\n" +
                "Source Server         : 192.168.10.250\n" +
                "Source Server Version : 100208\n" +
                "Source Host           : 192.168.10.250:3306\n" +
                "Source Database       : cycle\n" +
                "\n" +
                "Target Server Type    : MariaDB\n" +
                "Target Server Version : 100208\n" +
                "File Encoding         : 65001\n" +
                "\n" +
                "Date: 2018-01-23 09:02:02\n" +
                "*/ sdfdf";
        System.out.println(s.replaceAll("\\s*/\\*\\s*(.*\\n)*\\*/", ""));
    }

    /**
     * 删除项目sql
     *
     * @param code tsql编码
     */
    @Override
    public void delete(String code) {
        if (StringTools.isEmpty(code)) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        projectSqlDAO.delete(code);
    }
}
