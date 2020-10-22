package com.aicode.map.mq.producer;

import com.aicode.map.entity.MapClassTable;
import com.aicode.map.message.MapClassTableMessage;
import com.aicode.map.mq.MapClassTableProducer;
import com.aicode.map.mq.MapClassTableSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 类表映射信息 消息生产 实现
 */
@Slf4j
@Service
public class MapClassTableProducerImpl implements MapClassTableProducer {
    @Autowired
    private MapClassTableSink mapClassTableSink;

    /**
     * 创建 MapClassTable
     *
     * @param mapClassTable 账户 的实体类
     */
    @Override
    public void build(MapClassTable mapClassTable) {
        String linkId = RandomStringUtils.random(32, true, true);
        MapClassTableMessage mapClassTableMessage = new MapClassTableMessage();
        BeanUtils.copyProperties(mapClassTable, mapClassTableMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(mapClassTableMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, mapClassTableMessage.getCode());
        mapClassTableSink.buildMapClassTableOutput().send(messageBuilder.build());
    }
}
