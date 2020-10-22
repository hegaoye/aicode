package com.aicode.project.mq;

import com.aicode.project.entity.ProjectSql;

/**
 * 消息生产 接口
 */
public interface ProjectSqlProducer {

    /**
     * 创建 ProjectSql
     *
     * @param projectSql 项目sql脚本 的实体类
     */
    void build(ProjectSql projectSql);

}
