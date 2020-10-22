package com.aicode.project.mq.producer;

import com.aicode.project.entity.ProjectRepositoryAccount;
import com.aicode.project.message.ProjectRepositoryAccountMessage;
import com.aicode.project.mq.ProjectRepositoryAccountProducer;
import com.aicode.project.mq.ProjectRepositoryAccountSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 版本控制管理 消息生产 实现
 */
@Slf4j
@Service
public class ProjectRepositoryAccountProducerImpl implements ProjectRepositoryAccountProducer {
    @Autowired
    private ProjectRepositoryAccountSink projectRepositoryAccountSink;

    /**
     * 创建 ProjectRepositoryAccount
     *
     * @param projectRepositoryAccount 账户 的实体类
     */
    @Override
    public void build(ProjectRepositoryAccount projectRepositoryAccount) {
        String linkId = RandomStringUtils.random(32, true, true);
        ProjectRepositoryAccountMessage projectRepositoryAccountMessage = new ProjectRepositoryAccountMessage();
        BeanUtils.copyProperties(projectRepositoryAccount, projectRepositoryAccountMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(projectRepositoryAccountMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, projectRepositoryAccountMessage.getCode());
        projectRepositoryAccountSink.buildProjectRepositoryAccountOutput().send(messageBuilder.build());
    }
}
