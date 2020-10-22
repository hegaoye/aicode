package com.aicode.frameworks.mq;

import com.aicode.frameworks.entity.Frameworks;

/**
 * 消息生产 接口
 */
public interface FrameworksProducer {

    /**
     * 创建 Frameworks
     *
     * @param frameworks 框架技术池 的实体类
     */
    void build(Frameworks frameworks);

}
