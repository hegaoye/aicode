/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.dao;

import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.project.entity.ProjectSql;
import org.springframework.stereotype.Repository;


@Repository
public class ProjectSqlDAO extends BaseMybatisDAOImpl<ProjectSql, Long> {

    /**
     * 删除项目sql
     *
     * @param code tsql编码
     */
    public void delete(String code) {
        getSqlSession().delete(sqlmapNamespace + ".delete", code);
    }
}
