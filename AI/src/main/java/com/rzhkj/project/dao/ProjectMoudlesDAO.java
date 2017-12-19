/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.dao;


import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.project.entity.ProjectMoudles;
import org.springframework.stereotype.Repository;


@Repository
public class ProjectMoudlesDAO extends BaseMybatisDAOImpl<ProjectMoudles, Long> {

    /**
     * 删除
     *
     * @param projectMoudles 项目选择的模块
     */
    public void delete(ProjectMoudles projectMoudles) {
        getSqlSession().delete(sqlmapNamespace + ".delete", projectMoudles);
    }
}
