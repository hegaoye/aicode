package com.aicode.map.mq.producer;

import com.aicode.map.entity.MapRelationship;
import com.aicode.map.message.MapRelationshipMessage;
import com.aicode.map.mq.MapRelationshipProducer;
import com.aicode.map.mq.MapRelationshipSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 模型关系 消息生产 实现
 */
@Slf4j
@Service
public class MapRelationshipProducerImpl implements MapRelationshipProducer {
    @Autowired
    private MapRelationshipSink mapRelationshipSink;

    /**
     * 创建 MapRelationship
     *
     * @param mapRelationship 账户 的实体类
     */
    @Override
    public void build(MapRelationship mapRelationship) {
        String linkId = RandomStringUtils.random(32, true, true);
        MapRelationshipMessage mapRelationshipMessage = new MapRelationshipMessage();
        BeanUtils.copyProperties(mapRelationship, mapRelationshipMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(mapRelationshipMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, mapRelationshipMessage.getCode());
        mapRelationshipSink.buildMapRelationshipOutput().send(messageBuilder.build());
    }
}
