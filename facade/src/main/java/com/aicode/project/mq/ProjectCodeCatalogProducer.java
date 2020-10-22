package com.aicode.project.mq;

import com.aicode.project.entity.ProjectCodeCatalog;

/**
 * 消息生产 接口
 */
public interface ProjectCodeCatalogProducer {

    /**
     * 创建 ProjectCodeCatalog
     *
     * @param projectCodeCatalog 生成源码资料 的实体类
     */
    void build(ProjectCodeCatalog projectCodeCatalog);

}
