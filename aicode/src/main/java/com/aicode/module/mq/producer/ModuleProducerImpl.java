package com.aicode.module.mq.producer;

import com.aicode.module.entity.Module;
import com.aicode.module.message.ModuleMessage;
import com.aicode.module.mq.ModuleProducer;
import com.aicode.module.mq.ModuleSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 第三方模块池 消息生产 实现
 */
@Slf4j
@Service
public class ModuleProducerImpl implements ModuleProducer {
    @Autowired
    private ModuleSink moduleSink;

    /**
     * 创建 Module
     *
     * @param module 账户 的实体类
     */
    @Override
    public void build(Module module) {
        String linkId = RandomStringUtils.random(32, true, true);
        ModuleMessage moduleMessage = new ModuleMessage();
        BeanUtils.copyProperties(module, moduleMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(moduleMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, moduleMessage.getCode());
        moduleSink.buildModuleOutput().send(messageBuilder.build());
    }
}
