/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.dao;


import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.project.entity.ProjectFramwork;
import org.springframework.stereotype.Repository;


@Repository
public class ProjectFramworkDAO extends BaseMybatisDAOImpl<ProjectFramwork, Long> {

    /**
     * 删除
     *
     * @param projectFramwork 项目技术框架对象
     */
    public void delete(ProjectFramwork projectFramwork) {
        getSqlSession().delete(sqlmapNamespace + ".delete", projectFramwork);
    }
}
