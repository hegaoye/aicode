/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.dao;


import io.aicode.core.base.BaseMybatisDAOImpl;
import io.aicode.project.entity.ProjectJobLogs;
import org.springframework.stereotype.Repository;


@Repository
public class ProjectJobLogsDAO extends BaseMybatisDAOImpl<ProjectJobLogs, Long> {


    /**
     * 删除任务执行日志
     *
     * @param code 任务编码
     */
    public void delete(String code) {
        getSqlSession().delete(sqlmapNamespace + ".delete", code);
    }

    /**
     * 删除任务执行日志
     *
     * @param projectJobLogs
     */
    public void delete(ProjectJobLogs projectJobLogs) {
        getSqlSession().delete(sqlmapNamespace + ".delete", projectJobLogs);
    }
}
