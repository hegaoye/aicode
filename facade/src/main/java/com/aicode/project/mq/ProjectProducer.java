package com.aicode.project.mq;

import com.aicode.project.entity.Project;

/**
 * 消息生产 接口
 */
public interface ProjectProducer {

    /**
     * 创建 Project
     *
     * @param project 项目信息 的实体类
     */
    void build(Project project);

}
