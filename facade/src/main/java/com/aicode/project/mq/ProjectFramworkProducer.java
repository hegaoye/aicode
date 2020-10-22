package com.aicode.project.mq;

import com.aicode.project.entity.ProjectFramwork;

/**
 * 消息生产 接口
 */
public interface ProjectFramworkProducer {

    /**
     * 创建 ProjectFramwork
     *
     * @param projectFramwork 项目应用技术 的实体类
     */
    void build(ProjectFramwork projectFramwork);

}
