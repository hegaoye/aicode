package com.aicode.frameworks.mq.producer;

import com.aicode.frameworks.entity.Frameworks;
import com.aicode.frameworks.message.FrameworksMessage;
import com.aicode.frameworks.mq.FrameworksProducer;
import com.aicode.frameworks.mq.FrameworksSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 框架技术池 消息生产 实现
 */
@Slf4j
@Service
public class FrameworksProducerImpl implements FrameworksProducer {
    @Autowired
    private FrameworksSink frameworksSink;

    /**
     * 创建 Frameworks
     *
     * @param frameworks 账户 的实体类
     */
    @Override
    public void build(Frameworks frameworks) {
        String linkId = RandomStringUtils.random(32, true, true);
        FrameworksMessage frameworksMessage = new FrameworksMessage();
        BeanUtils.copyProperties(frameworks, frameworksMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(frameworksMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, frameworksMessage.getCode());
        frameworksSink.buildFrameworksOutput().send(messageBuilder.build());
    }
}
