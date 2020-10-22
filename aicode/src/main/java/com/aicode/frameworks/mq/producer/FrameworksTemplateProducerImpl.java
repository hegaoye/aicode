package com.aicode.frameworks.mq.producer;

import com.aicode.frameworks.entity.FrameworksTemplate;
import com.aicode.frameworks.message.FrameworksTemplateMessage;
import com.aicode.frameworks.mq.FrameworksTemplateProducer;
import com.aicode.frameworks.mq.FrameworksTemplateSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 框架配置文件模板 消息生产 实现
 */
@Slf4j
@Service
public class FrameworksTemplateProducerImpl implements FrameworksTemplateProducer {
    @Autowired
    private FrameworksTemplateSink frameworksTemplateSink;

    /**
     * 创建 FrameworksTemplate
     *
     * @param frameworksTemplate 账户 的实体类
     */
    @Override
    public void build(FrameworksTemplate frameworksTemplate) {
        String linkId = RandomStringUtils.random(32, true, true);
        FrameworksTemplateMessage frameworksTemplateMessage = new FrameworksTemplateMessage();
        BeanUtils.copyProperties(frameworksTemplate, frameworksTemplateMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(frameworksTemplateMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, frameworksTemplateMessage.getCode());
        frameworksTemplateSink.buildFrameworksTemplateOutput().send(messageBuilder.build());
    }
}
