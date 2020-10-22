package com.aicode.module.mq;

import com.aicode.module.entity.ModuleFile;

/**
 * 消息生产 接口
 */
public interface ModuleFileProducer {

    /**
     * 创建 ModuleFile
     *
     * @param moduleFile 模块文件信息 的实体类
     */
    void build(ModuleFile moduleFile);

}
