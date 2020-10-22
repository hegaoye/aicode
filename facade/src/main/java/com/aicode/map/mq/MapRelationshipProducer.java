package com.aicode.map.mq;

import com.aicode.map.entity.MapRelationship;

/**
 * 消息生产 接口
 */
public interface MapRelationshipProducer {

    /**
     * 创建 MapRelationship
     *
     * @param mapRelationship 模型关系 的实体类
     */
    void build(MapRelationship mapRelationship);

}
