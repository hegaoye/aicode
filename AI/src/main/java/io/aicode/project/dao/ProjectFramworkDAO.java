/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.dao;


import io.aicode.core.base.BaseMybatisDAOImpl;
import io.aicode.project.entity.ProjectFramwork;
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

    /**
     * 清空项目关联框架
     *
     * @param projectCode 项目编码
     */
    public void deleteAll(String projectCode) {
        getSqlSession().delete(sqlmapNamespace + ".deleteAll", projectCode);
    }
}
