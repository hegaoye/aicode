/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.core.BaseException;
import com.aicode.core.enums.SqlConvertEnum;
import com.aicode.exceptions.ProjectException;
import com.aicode.project.dao.ProjectSqlDAO;
import com.aicode.project.dao.mapper.ProjectMapper;
import com.aicode.project.dao.mapper.ProjectSqlMapper;
import com.aicode.project.entity.Project;
import com.aicode.project.entity.ProjectSql;
import com.aicode.project.entity.ProjectSqlState;
// 原为 com.alibaba.druid.util.StringUtils：与 commons-lang3 同名但语义不同，且随 Druid 消失
// 会导致编译失败。此处改用 commons-lang3，并按语义等价关系 isEmpty -> isBlank：
// druid 的 isEmpty 把「仅空白字符」也视作空，commons-lang3 的 isEmpty 不视作空。
// 改用 isBlank 才能保持原有校验强度（whitespace-only 的 projectCode 仍被判为空）。
import org.apache.commons.lang3.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.IdUtil;


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
    private ProjectMapper projectMapper;
    @Override
    public boolean save(ProjectSql entity) {
        if (entity == null || StringUtils.isBlank(entity.getProjectCode()) || StringUtils.isBlank(entity.getTsql())) {
            log.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        // code 是业务编码，不是主键派生值（主键由数据库自增赋值，见 MybatisPlusConfigTidb 的 IdType.AUTO）
        entity.setCode(IdUtil.getSnowflakeNextIdStr());
        entity.setState(ProjectSqlState.Enable.name());
        String tsql = entity.getTsql();
        Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>()
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
}


