package com.aicode.project.mq;

import com.aicode.project.entity.ProjectJob;

/**
 * 消息生产 接口
 */
public interface ProjectJobProducer {

    /**
     * 创建 ProjectJob
     *
     * @param projectJob 任务 的实体类
     */
    void build(ProjectJob projectJob);

}
