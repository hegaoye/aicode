package com.aicode.project.mq;

import com.aicode.project.entity.ProjectRepositoryAccount;

/**
 * 消息生产 接口
 */
public interface ProjectRepositoryAccountProducer {

    /**
     * 创建 ProjectRepositoryAccount
     *
     * @param projectRepositoryAccount 版本控制管理 的实体类
     */
    void build(ProjectRepositoryAccount projectRepositoryAccount);

}
