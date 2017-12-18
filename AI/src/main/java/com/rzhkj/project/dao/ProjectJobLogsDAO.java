/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.dao;


import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.project.entity.ProjectJobLogs;
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
}
