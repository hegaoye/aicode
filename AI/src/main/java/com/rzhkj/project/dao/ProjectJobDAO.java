/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.dao;


import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.project.entity.ProjectJob;
import org.springframework.stereotype.Repository;


@Repository
public class ProjectJobDAO extends BaseMybatisDAOImpl<ProjectJob, Long> {

    /**
     * 删除任务
     *
     * @param code 任务编码
     */
    public void delete(String code) {
        getSqlSession().delete(sqlmapNamespace + ".delete", code);
    }

    /**
     * 删除任务
     *
     * @param projectJob
     */
    public void delete(ProjectJob projectJob) {
        getSqlSession().delete(sqlmapNamespace + ".delete", projectJob);
    }
}
