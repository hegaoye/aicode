package com.aicode.account.mq.producer;

import com.aicode.account.entity.Account;
import com.aicode.account.message.AccountMessage;
import com.aicode.account.mq.AccountProducer;
import com.aicode.account.mq.AccountSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 账户 消息生产 实现
 */
@Slf4j
@Service
public class AccountProducerImpl implements AccountProducer {
    @Autowired
    private AccountSink _accountSink;

    /**
     * 创建 Account
     *
     * @param _account 账户 的实体类
     */
    @Override
    public void build(Account _account) {
        String linkId = RandomStringUtils.random(32, true, true);
        AccountMessage _accountMessage = new AccountMessage();
        BeanUtils.copyProperties(_account, _accountMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(_accountMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, _accountMessage.getCode());
        _accountSink.buildAccountOutput().send(messageBuilder.build());
    }
}
