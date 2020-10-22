package com.aicode.module.mq.producer;

import com.aicode.module.entity.ModuleFile;
import com.aicode.module.message.ModuleFileMessage;
import com.aicode.module.mq.ModuleFileProducer;
import com.aicode.module.mq.ModuleFileSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 模块文件信息 消息生产 实现
 */
@Slf4j
@Service
public class ModuleFileProducerImpl implements ModuleFileProducer {
    @Autowired
    private ModuleFileSink moduleFileSink;

    /**
     * 创建 ModuleFile
     *
     * @param moduleFile 账户 的实体类
     */
    @Override
    public void build(ModuleFile moduleFile) {
        String linkId = RandomStringUtils.random(32, true, true);
        ModuleFileMessage moduleFileMessage = new ModuleFileMessage();
        BeanUtils.copyProperties(moduleFile, moduleFileMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(moduleFileMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, moduleFileMessage.getCode());
        moduleFileSink.buildModuleFileOutput().send(messageBuilder.build());
    }
}
