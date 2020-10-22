package com.aicode.project.mq;

import com.aicode.project.entity.ProjectMap;

/**
 * 消息生产 接口
 */
public interface ProjectMapProducer {

    /**
     * 创建 ProjectMap
     *
     * @param projectMap 项目数据表 的实体类
     */
    void build(ProjectMap projectMap);

}
