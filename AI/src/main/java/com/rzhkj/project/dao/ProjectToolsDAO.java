/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.dao;


import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.project.entity.ProjectTools;
import org.springframework.stereotype.Repository;


@Repository
public class ProjectToolsDAO extends BaseMybatisDAOImpl<ProjectTools, Long> {


    /**
     * 删除
     *
     * @param projectTools 项目工具对象
     */
    public void delete(ProjectTools projectTools) {
        getSqlSession().delete(sqlmapNamespace + ".delete", projectTools);
    }
}
