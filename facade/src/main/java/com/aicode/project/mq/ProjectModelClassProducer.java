package com.aicode.project.mq;

import com.aicode.project.entity.ProjectModelClass;

/**
 * 消息生产 接口
 */
public interface ProjectModelClassProducer {

    /**
     * 创建 ProjectModelClass
     *
     * @param projectModelClass 模块下的类 的实体类
     */
    void build(ProjectModelClass projectModelClass);

}
