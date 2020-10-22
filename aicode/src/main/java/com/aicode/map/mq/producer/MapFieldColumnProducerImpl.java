package com.aicode.map.mq.producer;

import com.aicode.map.entity.MapFieldColumn;
import com.aicode.map.message.MapFieldColumnMessage;
import com.aicode.map.mq.MapFieldColumnProducer;
import com.aicode.map.mq.MapFieldColumnSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 字段属性映射信息 消息生产 实现
 */
@Slf4j
@Service
public class MapFieldColumnProducerImpl implements MapFieldColumnProducer {
    @Autowired
    private MapFieldColumnSink mapFieldColumnSink;

    /**
     * 创建 MapFieldColumn
     *
     * @param mapFieldColumn 账户 的实体类
     */
    @Override
    public void build(MapFieldColumn mapFieldColumn) {
        String linkId = RandomStringUtils.random(32, true, true);
        MapFieldColumnMessage mapFieldColumnMessage = new MapFieldColumnMessage();
        BeanUtils.copyProperties(mapFieldColumn, mapFieldColumnMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(mapFieldColumnMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, mapFieldColumnMessage.getCode());
        mapFieldColumnSink.buildMapFieldColumnOutput().send(messageBuilder.build());
    }
}
