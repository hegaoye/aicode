package com.aicode.project.mq;

import com.aicode.project.entity.ProjectJobLogs;

/**
 * 消息生产 接口
 */
public interface ProjectJobLogsProducer {

    /**
     * 创建 ProjectJobLogs
     *
     * @param projectJobLogs 任务日志 的实体类
     */
    void build(ProjectJobLogs projectJobLogs);

}
