package com.aicode.project.mq;

import com.aicode.project.entity.ProjectModule;

/**
 * 消息生产 接口
 */
public interface ProjectModuleProducer {

    /**
     * 创建 ProjectModule
     *
     * @param projectModule 项目选择模块 的实体类
     */
    void build(ProjectModule projectModule);

}
