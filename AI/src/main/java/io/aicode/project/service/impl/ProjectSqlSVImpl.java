/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package io.aicode.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
import io.aicode.core.base.BaseMybatisDAO;
import io.aicode.core.base.BaseMybatisSVImpl;
import io.aicode.core.exceptions.BaseException;
import io.aicode.core.exceptions.ProjectException;
import io.aicode.core.tools.StringTools;
import io.aicode.project.dao.ProjectDAO;
import io.aicode.project.dao.ProjectSqlDAO;
import io.aicode.project.entity.Project;
import io.aicode.project.entity.ProjectSql;
import io.aicode.project.entity.ProjectSqlStateEnum;
import io.aicode.project.service.ProjectSqlSV;
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
        map.put("code", entity.getProjectCode());
        Project project = projectDAO.load(map);
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
