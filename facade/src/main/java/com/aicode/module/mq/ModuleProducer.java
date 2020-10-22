package com.aicode.module.mq;

import com.aicode.module.entity.Module;

/**
 * 消息生产 接口
 */
public interface ModuleProducer {

    /**
     * 创建 Module
     *
     * @param module 第三方模块池 的实体类
     */
    void build(Module module);

}
