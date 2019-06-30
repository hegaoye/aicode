/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package io.aicode.project.dao;

import com.google.common.collect.Maps;
import io.aicode.base.BaseMybatisDAOImpl;
import io.aicode.project.entity.ProjectSql;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Repository
public class ProjectSqlDAO extends BaseMybatisDAOImpl<ProjectSql, Long> {

    /**
     * 删除项目sql
     *
     * @param code tsql编码
     */
    public void delete(String code) {
        Map<String,Object> map= Maps.newHashMap();
        map.put("code",code);
        getSqlSession().delete(sqlmapNamespace + ".delete", map);
    }


    /**
     * 删除项目sql
     *
     */
    public void delete(ProjectSql projectSql) {
        getSqlSession().delete(sqlmapNamespace + ".delete", projectSql);
    }
}
