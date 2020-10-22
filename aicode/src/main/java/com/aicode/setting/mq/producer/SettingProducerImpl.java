package com.aicode.setting.mq.producer;

import com.aicode.setting.entity.Setting;
import com.aicode.setting.message.SettingMessage;
import com.aicode.setting.mq.SettingProducer;
import com.aicode.setting.mq.SettingSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 设置 消息生产 实现
 */
@Slf4j
@Service
public class SettingProducerImpl implements SettingProducer {
    @Autowired
    private SettingSink settingSink;

    /**
     * 创建 Setting
     *
     * @param setting 账户 的实体类
     */
    @Override
    public void build(Setting setting) {
        String linkId = RandomStringUtils.random(32, true, true);
        SettingMessage settingMessage = new SettingMessage();
        BeanUtils.copyProperties(setting, settingMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(settingMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, settingMessage.getCode());
        settingSink.buildSettingOutput().send(messageBuilder.build());
    }
}
