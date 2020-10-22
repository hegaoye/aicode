package com.aicode.display.mq.producer;

import com.aicode.display.entity.DisplayAttribute;
import com.aicode.display.message.DisplayAttributeMessage;
import com.aicode.display.mq.DisplayAttributeProducer;
import com.aicode.display.mq.DisplayAttributeSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 显示属性 消息生产 实现
 */
@Slf4j
@Service
public class DisplayAttributeProducerImpl implements DisplayAttributeProducer {
    @Autowired
    private DisplayAttributeSink displayAttributeSink;

    /**
     * 创建 DisplayAttribute
     *
     * @param displayAttribute 账户 的实体类
     */
    @Override
    public void build(DisplayAttribute displayAttribute) {
        String linkId = RandomStringUtils.random(32, true, true);
        DisplayAttributeMessage displayAttributeMessage = new DisplayAttributeMessage();
        BeanUtils.copyProperties(displayAttribute, displayAttributeMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(displayAttributeMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, displayAttributeMessage.getCode());
        displayAttributeSink.buildDisplayAttributeOutput().send(messageBuilder.build());
    }
}
