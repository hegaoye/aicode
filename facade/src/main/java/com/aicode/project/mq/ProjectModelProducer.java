package com.aicode.project.mq;

import com.aicode.project.entity.ProjectModel;

/**
 * 消息生产 接口
 */
public interface ProjectModelProducer {

    /**
     * 创建 ProjectModel
     *
     * @param projectModel 模块 的实体类
     */
    void build(ProjectModel projectModel);

}
