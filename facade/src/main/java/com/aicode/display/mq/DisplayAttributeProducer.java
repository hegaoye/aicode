package com.aicode.display.mq;

import com.aicode.display.entity.DisplayAttribute;

/**
 * 消息生产 接口
 */
public interface DisplayAttributeProducer {

    /**
     * 创建 DisplayAttribute
     *
     * @param displayAttribute 显示属性 的实体类
     */
    void build(DisplayAttribute displayAttribute);

}
