package com.aicode.frameworks.mq;

import com.aicode.frameworks.entity.FrameworksTemplate;

/**
 * 消息生产 接口
 */
public interface FrameworksTemplateProducer {

    /**
     * 创建 FrameworksTemplate
     *
     * @param frameworksTemplate 框架配置文件模板 的实体类
     */
    void build(FrameworksTemplate frameworksTemplate);

}
